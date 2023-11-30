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

    public AeropuertoMonitor(Pane airportArea, Pane controlPasaportesArea, Pane equipajeArea, Pane zonaEspera) {
        this.airportArea = airportArea;
        this.controlPasaportesArea = controlPasaportesArea;
        this.equipajeArea = equipajeArea;
        this.zonaEspera = zonaEspera;
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

        int yAgentePosition = 120;
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
        teletransportarAgentePasaportes(agenteAsignado, xPosition, yAgentePosition);


        // Nueva lógica para regresar al agente a la zona de espera
        new Thread(() -> {
            try {
                Thread.sleep((new Random().nextInt(5) + 1) * 1000); // Espera de 1 a 5 segundos
                regresarAgenteAEspera(agenteAsignado);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private synchronized void regresarAgenteAEspera(AgentePasaporte agente) {
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });
        agentesPasaportesDisponibles.add(agente);
        notifyAll(); // Notificar que hay un agente disponible
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
        int yPosition = 80; // Ajusta según la disposición de tu UI

        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        Platform.runLater(() -> {
            controlPasaportesArea.getChildren().removeAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
            equipajeArea.getChildren().addAll(pasajero.getRepresentacion().getCircle(), pasajero.getEquipaje().getCircle());
        });

        // Regresar al agente de pasaportes a la zona de espera cuando el pasajero se mueve al área de equipaje
        AgentePasaporte agenteAsignado = pasajero.getAgenteAsignado();
        if (agenteAsignado != null) {
            regresarAgenteAEspera(agenteAsignado);
            pasajero.setAgenteAsignado(null); // Quitar la asignación del agente al pasajero
        }
    }

    public synchronized void teletransportarAgenteEquipaje(AgenteEquipaje agente) {
        // Calcula una posición aleatoria en la zona de equipaje
        Random random = new Random();
        int posicionEquipaje = random.nextInt(MAX_AGENTES_EQUIPAJE);
        int xPosition = 50 + posicionEquipaje * 30;
        int yPositionEquipaje = 80; // Ajusta la posición Y según sea necesario para la zona de equipaje

        // Actualiza la posición del agente
        agente.ModificarRepresentacion(xPosition, yPositionEquipaje, true);

        // Actualiza la UI en el hilo de JavaFX
        Platform.runLater(() -> {
            if (agente.getRepresentacion().getCircle().getParent() != null) {
                ((Pane) agente.getRepresentacion().getCircle().getParent()).getChildren().remove(agente.getRepresentacion().getCircle());
            }
            equipajeArea.getChildren().add(agente.getRepresentacion().getCircle());
        });
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
        int yPositionZonaEspera = 150; // Ajusta la posición Y según sea necesario para la zona de espera

        // Modifica la representación del agente para moverlo a la zona de espera
        agente.ModificarRepresentacion(xPosition, yPositionZonaEspera, true);

        // Añade el agente a la zona de espera
        Platform.runLater(() -> {
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        agentesEquipajeActuales++;

        // Crea un hilo para mover aleatoriamente al agente a la zona de equipaje
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep((new Random().nextInt(5) + 1) * 1000); // Espera aleatoria entre 1 y 5 segundos
                    teletransportarAgenteEquipaje(agente);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
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
