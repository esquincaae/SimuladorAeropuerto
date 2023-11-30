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
        posicionesEntrada[posicionEntrada] = false;
        pasajerosActuales--;

        int posicionPasaportes = asignarPosicionLibre(posicionesPasaportes);
        int xPosition = 50 + posicionPasaportes * 30;
        int yPosition = 80;

        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        Platform.runLater(() -> {
            airportArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
            controlPasaportesArea.getChildren().addAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
        });

        asignarAgentePasaportes(pasajero, xPosition, 120);

        // Elimina la lógica de la espera y el retorno del agente aquí
        // El agente ahora será regresado a la zona de espera en teletransportarAEquipaje
    }


    private void asignarAgentePasaportes(Pasajero pasajero, int xPosition, int yAgentePosition) {
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

            // Teletransportar al agente a la posición correcta
            teletransportarAgentePasaportes(agenteAsignado, xPosition, yAgentePosition);
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



    public void teletransportarAgentePasaportes(AgentePasaporte agente, int x, int y) {
        agente.ModificarRepresentacion(x, y, true);
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            controlPasaportesArea.getChildren().add(agente.getRepresentacion().getCircle());
        });
    }


    public synchronized int entrarPasajero(Pasajero pasajero) {
        while (pasajerosActuales >= maxPasajeros) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int posicion = asignarPosicionLibre(posicionesEntrada);
        int xPosition = 50 + posicion * 30;
        int yPosition = 50;
        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

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
        int xPosition = 50 + posicionEquipaje * 30;
        int yPosition = 150;

        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

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

        // Gestión de agentes de equipaje
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
            pasajero.setAgenteAsignado(null); // Resetea el agente asignado del pasajero
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
