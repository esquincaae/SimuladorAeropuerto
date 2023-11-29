package com.example.simuladoraeropuerto.concurrent;

import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class AeropuertoMonitor {
    private final Pane airportArea;
    private final int maxPasajeros = 10;
    private int pasajerosActuales = 0;

    public AeropuertoMonitor(Pane airportArea) {
        this.airportArea = airportArea;
    }

    public synchronized void entrarPasajero(Pasajero pasajero) {
        while (pasajerosActuales >= maxPasajeros) {
            try {
                wait(); // Esperar si la zona está llena
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Añadir pasajero a la zona de entrada y actualizar contador
        // Calcula la posición del nuevo pasajero y su equipaje
        int xPosition = 50 + pasajerosActuales * 30; // Ajustar estos valores según la disposición deseada
        int yPosition = 50; // Ajustar según la disposición deseada

        pasajero.ModificarRepresentacion(xPosition, yPosition, true);
        pasajero.ModificarEquipaje(xPosition+15, yPosition, true);

        pasajerosActuales++;
    }

    public synchronized void salirPasajero(Pasajero pasajero) {
        // Remover pasajero y equipaje de la zona de entrada y actualizar contador
        Platform.runLater(() -> {
            //airportArea.getChildren().removeAll(pasajero.getRepresentacion(), pasajero.getEquipaje());
        });
        pasajerosActuales--;
        notifyAll(); // Notificar a otros hilos que hay espacio
    }

}
