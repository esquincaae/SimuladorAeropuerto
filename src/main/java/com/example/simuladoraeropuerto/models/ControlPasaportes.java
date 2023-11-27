package com.example.simuladoraeropuerto.models;

import java.util.concurrent.Semaphore;

public class ControlPasaportes {
    private final Semaphore cabinasDisponibles;
    private AgenteControl[] agentes; // Array de agentes de control de pasaportes

    public ControlPasaportes(int numeroCabinas, AgenteControl[] agentes) {
        this.cabinasDisponibles = new Semaphore(numeroCabinas);
        this.agentes = agentes;
    }

    public void atenderPasajero(Pasajero pasajero) {
        try {
            if (!cabinasDisponibles.tryAcquire()) {
                pasajero.moverALaCola();
                cabinasDisponibles.acquire();
            }

            // Asignar el pasajero a un agente libre
            for (AgenteControl agente : agentes) {
                if (agente.estaLibre()) {
                    agente.atenderPasajero(pasajero);
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
