package com.example.simuladoraeropuerto.concurrent;

import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;

public class PasajeroGenerator implements Runnable {
    private final AeropuertoMonitor monitor;

    public PasajeroGenerator(AeropuertoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while (true) { // Bucle infinito para generar pasajeros
            Pasajero pasajero = new Pasajero();
            monitor.entrarPasajero(pasajero);

            // Aquí puedes agregar un delay para simular tiempo entre llegadas
            try {
                Thread.sleep(1000); // Ejemplo: 1 segundo entre cada pasajero
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
