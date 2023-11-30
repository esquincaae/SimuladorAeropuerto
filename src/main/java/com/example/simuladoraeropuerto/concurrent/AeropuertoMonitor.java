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

        int posicionPasaportes = asignarPosicionLibre(posicionesPasaportes);
        if (posicionPasaportes == -1) {

            return;
        }
        pasajero.setPosicionPasaportes(posicionPasaportes);


        int xPosition = 50 + posicionPasaportes * 30;
        int yPosition = 80;

        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        Platform.runLater(() -> {
            airportArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
            controlPasaportesArea.getChildren().addAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
        });

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
            teletransportarAgentePasaportes(agenteAsignado, xPosition, yPosition - 40);


            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        controlPasaportesArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());

                    });
                    regresarAgenteAEspera(agenteAsignado);
                    synchronized (this) {
                        posicionesPasaportes[pasajero.getPosicionPasaportes()] = false;
                        notifyAll();
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
        while (pasajerosActuales >= maxPasajeros) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            }
        }

        int posicion = asignarPosicionLibre(posicionesEntrada);
        if (posicion == -1) {

            return -1;
        }

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
        if (posicionEquipaje == -1) {
            return;
        }
        pasajero.setPosicionEquipaje(posicionEquipaje);

        int xPosition = 50 + posicionEquipaje * 30;
        int yPosition = 150;

        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        Platform.runLater(() -> {
            controlPasaportesArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
            equipajeArea.getChildren().addAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
        });

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    equipajeArea.getChildren().remove(pasajero.getEquipaje().getCircle());
                    pasajero.ModificarEquipaje(0, 0, false);
                });
                teletransportarASalida(pasajero);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

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

                if (pasajero.getAgenteAsignado() != null) {
                    regresarAgenteAEspera(pasajero.getAgenteAsignado());
                    pasajero.setAgenteAsignado(null);
                    pasajerosActuales--;
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


        new Thread(() -> {
            try {
                Thread.sleep(2000);
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

        new Thread(() -> {
            try {
                int espera = new Random().nextInt(4) + 2;
                Thread.sleep(espera * 1000);

                Platform.runLater(() -> {
                    if (pasajero.getRepresentacion().getCircle().getParent() != null) {
                        ((Pane) pasajero.getRepresentacion().getCircle().getParent()).getChildren().remove(pasajero.getRepresentacion().getCircle());
                    }
                    areaSalida.getChildren().add(pasajero.getRepresentacion().getCircle());
                });

                new Thread(() -> {
                    try {
                        int esperaSalida = new Random().nextInt(5) + 1;
                        Thread.sleep(esperaSalida * 1000);

                        Platform.runLater(() -> {
                            areaSalida.getChildren().remove(pasajero.getRepresentacion().getCircle());
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    synchronized (AeropuertoMonitor.this) {
                        posicionesEquipaje[pasajero.getPosicionEquipaje()] = false;
                        pasajerosActuales--;
                        notifyAll();
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

        new Thread(() -> {
            try {
                // Espera un tiempo aleatorio entre 2 y 5 segundos
                int espera = new Random().nextInt(4) + 2;
                Thread.sleep(espera * 1000);

                Platform.runLater(() -> {
                    if (agente.getRepresentacion().getCircle().getParent() != null) {
                        ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
                    }
                    zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
                });

                synchronized (AeropuertoMonitor.this) {
                    agentesEquipajeDisponibles.add(agente);
                    notifyAll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public synchronized void entrarAgenteEquipaje(AgenteEquipaje agente) {
        while (agentesEquipajeActuales >= MAX_AGENTES_EQUIPAJE) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        int xPosition = 50 + agentesEquipajeActuales * 30;
        int yPositionZonaEspera = 150;

        agente.ModificarRepresentacion(xPosition, yPositionZonaEspera, true);

        Platform.runLater(() -> {
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        agentesEquipajeActuales++;
        agentesEquipajeDisponibles.add(agente);
        notifyAll();
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
