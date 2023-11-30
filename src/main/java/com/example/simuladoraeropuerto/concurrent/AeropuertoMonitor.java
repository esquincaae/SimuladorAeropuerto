package com.example.simuladoraeropuerto.concurrent;

import com.example.simuladoraeropuerto.models.AgenteEquipaje;
import com.example.simuladoraeropuerto.models.AgentePasaporte;
import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class AeropuertoMonitor {
    private final Pane airportArea;
    private final Pane zonaEspera;

    private final Pane areaSalida;
    private final Pane equipajeArea;
    private final Pane controlPasaportesArea;
    private final int maxPasajeros = 10;
    private final int maxAgentes = 10;
    private int pasajerosActuales = 0;
    private int agentesActuales = 0;

    private boolean[] posicionesEntrada = new boolean[maxPasajeros];
    private boolean[] posicionesPasaportes = new boolean[maxPasajeros];


    public static final int MAX_AGENTES_EQUIPAJE = 10;
    private int agentesEquipajeActuales = 0;

    public static final int MAX_AGENTES = 10;

    private boolean[] posicionesEquipaje;


    private Queue<AgentePasaporte> agentesPasaportesDisponibles = new LinkedList<>();
    private Queue<AgenteEquipaje> agentesEquipajeDisponibles = new LinkedList<>();


    public AeropuertoMonitor(Pane airportArea, Pane controlPasaportesArea, Pane equipajeArea, Pane zonaEspera, Pane areaSalida) {
        this.airportArea = airportArea;
        this.controlPasaportesArea = controlPasaportesArea;
        this.equipajeArea = equipajeArea;
        this.zonaEspera = zonaEspera;
        this.areaSalida = areaSalida;
        posicionesEquipaje = new boolean[maxPasajeros];
    }

    public synchronized void teletransportarAPasaportes(Pasajero pasajero, int posicionEntrada) {
        // Liberar la posición de entrada que ocupaba el pasajero
        posicionesEntrada[posicionEntrada] = false;

        // Asignar una posición libre en el área de pasaportes
        int posicionPasaportes = asignarPosicionLibre(posicionesPasaportes);
        if (posicionPasaportes == -1) {
            // Manejo de error: no hay posiciones libres en pasaportes
            // Considera cómo manejar esto, quizás reintegrar al pasajero en la cola
            return;
        }
        pasajero.setPosicionPasaportes(posicionPasaportes); // Guardar la posición asignada en pasaportes

        // Calcular la posición X e Y para el pasajero y su equipaje en el área de pasaportes
        int xPosition = 50 + posicionPasaportes * 30;
        int yPosition = 80;

        // Actualizar la representación del pasajero y su equipaje
        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        // Mover al pasajero y su equipaje a la zona de control de pasaportes
        Platform.runLater(() -> {
            airportArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
            controlPasaportesArea.getChildren().addAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
        });

        // Asignar un agente de pasaportes al pasajero
        asignarAgentePasaportes(pasajero, xPosition, yPosition);
    }


    private void asignarAgentePasaportes(Pasajero pasajero, int xPosition, int yPosition) {
        synchronized (this) {
            while (agentesPasaportesDisponibles.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            AgentePasaporte agenteAsignado = agentesPasaportesDisponibles.remove();
            pasajero.setAgenteAsignado(agenteAsignado);
            teletransportarAgentePasaportes(agenteAsignado, xPosition, yPosition - 40); // Ajuste en la posición Y para el agente

            // Procesar al pasajero y luego regresar al agente a la espera
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Simula el tiempo de procesamiento
                    Platform.runLater(() -> {
                        controlPasaportesArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
                        // Aquí puedes añadir el código para mover al pasajero a la siguiente zona
                    });
                    regresarAgenteAEspera(agenteAsignado); // Regresa el agente a la espera
                    synchronized (this) {
                        posicionesPasaportes[pasajero.getPosicionPasaportes()] = false; // Libera la posición en pasaportes
                        notifyAll(); // Notifica a los hilos en espera
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private void regresarAgenteAEspera(AgentePasaporte agente) {
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        synchronized (this) {
            agentesPasaportesDisponibles.add(agente);
            notifyAll();
        }
    }


    private void teletransportarAgentePasaportes(AgentePasaporte agente, int x, int y) {
        agente.ModificarRepresentacion(x, y, true);
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            controlPasaportesArea.getChildren().add(agente.getRepresentacion().getCircle());
        });
    }

    public synchronized int entrarPasajero(Pasajero pasajero) {
        // Espera hasta que haya espacio para un nuevo pasajero
        while (pasajerosActuales >= maxPasajeros) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1; // Manejo de interrupción
            }
        }

        // Asignar una posición libre en la zona de entrada
        int posicion = asignarPosicionLibre(posicionesEntrada);
        if (posicion == -1) {
            // Manejo de error: no hay posiciones libres en la entrada
            // Considera cómo manejar esto, quizás reintegrar al pasajero en la cola
            return -1;
        }

        // Calcular la posición X e Y para el pasajero y su equipaje en la zona de entrada
        int xPosition = 50 + posicion * 30;
        int yPosition = 50;

        // Actualizar la representación del pasajero y su equipaje
        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        // Incrementar el contador de pasajeros actuales
        pasajerosActuales++;
        return posicion;
    }


    private int asignarPosicionLibre(boolean[] posiciones) {
        for (int i = 0; i < posiciones.length; i++) {
            if (!posiciones[i]) {
                posiciones[i] = true;
                return i;
            }
        }
        return -1;
    }

    public synchronized void teletransportarAEquipaje(Pasajero pasajero) {
        int posicionEquipaje = asignarPosicionLibre(posicionesEquipaje);
        if (posicionEquipaje == -1) {
            // Manejo de error: no hay posiciones libres en equipaje
            // Considera cómo manejar esto, quizás reintegrar al pasajero en la cola
            return;
        }
        pasajero.setPosicionEquipaje(posicionEquipaje); // Guardar la posición asignada en equipaje

        // Calcular la posición X e Y para el pasajero y su equipaje en el área de equipaje
        int xPosition = 50 + posicionEquipaje * 30;
        int yPosition = 150;

        // Actualizar la representación del pasajero y su equipaje
        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        // Mover al pasajero y su equipaje a la zona de equipaje
        Platform.runLater(() -> {
            controlPasaportesArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
            equipajeArea.getChildren().addAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
        });

        // Lógica para el proceso del equipaje y posterior traslado a la zona de salida
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Espera de 1 segundo
                Platform.runLater(() -> {
                    equipajeArea.getChildren().remove(pasajero.getEquipaje().getCircle());
                    pasajero.ModificarEquipaje(0, 0, false); // Opcional: actualizar estado del equipaje
                });
                teletransportarASalida(pasajero); // Llamada al nuevo método
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Gestión de agentes de equipaje en un hilo separado
        new Thread(() -> {
            synchronized (AeropuertoMonitor.this) {
                while (agentesEquipajeDisponibles.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                AgenteEquipaje agenteAsignado = agentesEquipajeDisponibles.remove();
                teletransportarAgenteEquipaje(agenteAsignado, posicionEquipaje);

                // Regresar al agente de pasaportes a la zona de espera
                if (pasajero.getAgenteAsignado() != null) {
                    regresarAgenteAEspera(pasajero.getAgenteAsignado());
                    pasajero.setAgenteAsignado(null);
                    pasajerosActuales--; // Decrementar aquí después de completar el proceso en pasaportes
                }
            }
        }).start();
    }


    private void asignarYTeletransportarAgenteEquipaje(Pasajero pasajero, int xPosition, int yPosition) {
        synchronized (this) {
            while (agentesEquipajeDisponibles.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            AgenteEquipaje agenteAsignado = agentesEquipajeDisponibles.remove();
            teletransportarAgenteEquipaje(agenteAsignado, xPosition, yPosition); // Método para mover al agente
        }
    }

    public synchronized void teletransportarAgenteEquipaje(AgenteEquipaje agente, int x, int y) {
        agente.ModificarRepresentacion(x, y, true);
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            equipajeArea.getChildren().add(agente.getRepresentacion().getCircle());
        });

        // Nueva lógica para que el agente regrese a la zona de espera
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Espera de 2 segundos antes de regresar
                regresarAgenteAEsperaEquipaje(agente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void regresarAgenteAEsperaEquipaje(AgenteEquipaje agente) {
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        synchronized (this) {
            agentesEquipajeDisponibles.add(agente);
            notifyAll();
        }
    }


    public synchronized void teletransportarASalida(Pasajero pasajero) {
        // Espera un tiempo aleatorio entre 2 y 5 segundos para teletransportar al pasajero a la salida
        new Thread(() -> {
            try {
                int espera = new Random().nextInt(4) + 2; // genera un número entre 2 y 5
                Thread.sleep(espera * 1000);

                Platform.runLater(() -> {
                    if (pasajero.getRepresentacion().getCircle().getParent() != null) {
                        ((Pane) pasajero.getRepresentacion().getCircle().getParent()).getChildren().remove(pasajero.getRepresentacion().getCircle());
                    }
                    areaSalida.getChildren().add(pasajero.getRepresentacion().getCircle());
                });

                // Lógica para eliminar al pasajero del área de salida tras 1-5 segundos
                new Thread(() -> {
                    try {
                        int esperaSalida = new Random().nextInt(5) + 1; // genera un número entre 1 y 5
                        Thread.sleep(esperaSalida * 1000);

                        Platform.runLater(() -> {
                            areaSalida.getChildren().remove(pasajero.getRepresentacion().getCircle());
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    synchronized (AeropuertoMonitor.this) {
                        posicionesEquipaje[pasajero.getPosicionEquipaje()] = false; // Libera la posición de equipaje
                        pasajerosActuales--; // Decrementa el contador de pasajeros
                        notifyAll(); // Notifica a los hilos en espera de que hay un espacio libre
                    }
                }).start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }



    public synchronized void teletransportarAgenteEquipaje(AgenteEquipaje agente, int posicionEquipaje) {
        int xPosition = 50 + posicionEquipaje * 30;
        int yPositionEquipaje = 80;

        agente.ModificarRepresentacion(xPosition, yPositionEquipaje, true);
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            equipajeArea.getChildren().add(agente.getRepresentacion().getCircle());
        });

        // Nueva lógica para que el agente regrese a la zona de espera
        new Thread(() -> {
            try {
                // Espera un tiempo aleatorio entre 2 y 5 segundos
                int espera = new Random().nextInt(4) + 2; // genera un número entre 2 y 5
                Thread.sleep(espera * 1000);

                Platform.runLater(() -> {
                    if (agente.getRepresentacion().getCircle().getParent() != null) {
                        ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
                    }
                    zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
                });

                // Actualizar la cola de agentes disponibles
                synchronized (AeropuertoMonitor.this) {
                    agentesEquipajeDisponibles.add(agente);
                    notifyAll(); // Notificar que hay un agente disponible
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public synchronized void entrarAgenteEquipaje(AgenteEquipaje agente) {
        // Espera si el área de la zona de espera está llena
        while (agentesEquipajeActuales >= MAX_AGENTES_EQUIPAJE) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Calcula la posición X para el agente en la zona de espera
        int xPosition = 50 + agentesEquipajeActuales * 30;
        int yPositionZonaEspera = 150;

        // Modifica la representación del agente para moverlo a la zona de espera
        agente.ModificarRepresentacion(xPosition, yPositionZonaEspera, true);

        // Añade el agente a la zona de espera
        Platform.runLater(() -> {
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        agentesEquipajeActuales++;
        agentesEquipajeDisponibles.add(agente); // Añadir agente a la cola de disponibles
        notifyAll(); // Notificar que hay un agente disponible
    }






    public synchronized void entrarAgentePasaporte(AgentePasaporte agente) {
        while (agentesActuales >= maxAgentes) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int xPosition = 50 + agentesActuales * 30;
        agente.ModificarRepresentacion(xPosition, 100, true);
        Platform.runLater(() -> {
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        agentesActuales++;
        agentesPasaportesDisponibles.add(agente);
        notifyAll();
    }



}
