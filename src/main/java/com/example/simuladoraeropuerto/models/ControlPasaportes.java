package com.example.simuladoraeropuerto.models;

import java.util.concurrent.Semaphore;

public class ControlPasaportes {
    private final Semaphore cabinasDisponibles;

    public ControlPasaportes(int numeroCabinas) {
        this.cabinasDisponibles = new Semaphore(numeroCabinas);
    }

    public void atenderPasajero(Pasajero pasajero) {
        try {
            if (!cabinasDisponibles.tryAcquire()) {
                pasajero.moverALaCola(); // Mover al pasajero a la cola de espera
                cabinasDisponibles.acquire(); // Esperar por una cabina disponible
            }
            // Procesar el pasaporte del pasajero
            Thread.sleep(1000); // Simular tiempo de atención
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cabinasDisponibles.release(); // Liberar la cabina
        }
    }
}