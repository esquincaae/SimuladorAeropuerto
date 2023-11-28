package com.example.simuladoraeropuerto.concurrent;

import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

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
        // Calcula la posición del nuevo pasajero
        double xPosition = 50 + pasajerosActuales * 30; // Ajustar estos valores según la disposición deseada
        double yPosition = 50; // Ajustar según la disposición deseada

        // Añadir pasajero a la zona de entrada y actualizar contador
        Platform.runLater(() -> {
            pasajero.getRepresentacion().setCenterX(xPosition);
            pasajero.getRepresentacion().setCenterY(yPosition);
            airportArea.getChildren().add(pasajero.getRepresentacion());
        });
        pasajerosActuales++;
    }

    public synchronized void salirPasajero(Pasajero pasajero) {
        // Remover pasajero de la zona de entrada y actualizar contador
        Platform.runLater(() -> airportArea.getChildren().remove(pasajero.getRepresentacion()));
        pasajerosActuales--;
        notifyAll(); // Notificar a otros hilos que hay espacio
    }
}
