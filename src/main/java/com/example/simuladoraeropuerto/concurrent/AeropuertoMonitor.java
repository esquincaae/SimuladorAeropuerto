package com.example.simuladoraeropuerto.concurrent;

import com.example.simuladoraeropuerto.models.AgenteEquipaje;
import com.example.simuladoraeropuerto.models.AgentePasaporte;
import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class AeropuertoMonitor {
    private final Pane airportArea;
    private final Pane zonaEspera;

    private final Pane equipajeArea;
    private final Pane controlPasaportesArea;
    private final int maxPasajeros = 10;
    private final int maxAgentes = 10;
    private int pasajerosActuales = 0;
    private int agentesActuales = 0;

    public static final int MAX_AGENTES_EQUIPAJE = 10;
    private int agentesEquipajeActuales = 0;

    public static final int MAX_AGENTES = 10;

    public AeropuertoMonitor(Pane airportArea, Pane controlPasaportesArea, Pane equipajeArea, Pane zonaEspera) {
        this.airportArea = airportArea;
        this.controlPasaportesArea = controlPasaportesArea;
        this.equipajeArea = equipajeArea;
        this.zonaEspera = zonaEspera;
    }



    public synchronized void entrarPasajero(Pasajero pasajero) {
        while (pasajerosActuales >= maxPasajeros) {
            try {
                wait(); // Esperar si la zona está llena
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Cálculo y asignación de posición para el pasajero y su equipaje
        int xPosition = 50 + pasajerosActuales * 30;
        int yPosition = 50;
        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition + 15, yPosition, true);

        pasajerosActuales++;
    }

    public synchronized void salirPasajero(Pasajero pasajero) {
        Platform.runLater(() -> {
            // Remoción del pasajero y su equipaje
            // airportArea.getChildren().removeAll(pasajero.getRepresentacion(), pasajero.getEquipaje());
        });
        pasajerosActuales--;
        notifyAll(); // Notificar a otros hilos que hay espacio
    }

    public synchronized void entrarAgenteEquipaje(AgenteEquipaje agente) {
        while (agentesEquipajeActuales >= MAX_AGENTES_EQUIPAJE) {
            try {
                wait(); // Esperar si el área está llena
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Calcula la posición X para el agente de equipaje
        int xPosition = 50 + agentesEquipajeActuales * 30;

        // Modificar la representación del agente para moverlo a la zona de espera
        agente.ModificarRepresentacion(xPosition, 150, true); // Ajusta la posición Y según sea necesario

        // Añadir el agente a la zona de espera
        Platform.runLater(() -> {
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        agentesEquipajeActuales++;
    }


    public synchronized void entrarAgentePasaporte(AgentePasaporte agente) {
        while (agentesActuales >= maxAgentes) {
            try {
                wait(); // Esperar si el área está llena
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Calcula la posición X para el agente de pasaporte
        int xPosition = 50 + agentesActuales * 30;

        // Modificar la representación del agente para moverlo a la zona de espera
        agente.ModificarRepresentacion(xPosition, 100, true); // Ajusta la posición Y según sea necesario

        // Añadir el agente a la zona de espera
        Platform.runLater(() -> {
            zonaEspera.getChildren().add(agente.getRepresentacion().getCircle());
        });

        agentesActuales++;
    }

}
