package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AgenteControl extends Thread {
    private Circle visualRepresentation;
    private Pasajero pasajeroActual;
    private final Lock lock = new ReentrantLock();
    private final Condition disponible = lock.newCondition();
    private boolean libre = true;

    public AgenteControl() {
        visualRepresentation = new Circle(15, Color.BLUE);
        this.start();
    }

    public boolean estaLibre() {
        return libre;
    }
    public Circle getVisualRepresentation() {
        return visualRepresentation;
    }

    public void atenderPasajero(Pasajero pasajero) {
        lock.lock();
        try {
            while (!libre) {
                disponible.await();
            }
            this.pasajeroActual = pasajero;
            libre = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void run() {
        while (true) {
            lock.lock();
            try {
                while (pasajeroActual == null) {
                    disponible.await();
                }
                // Simular atención al pasajero
                Thread.sleep(1000);
                pasajeroActual.atencionCompletada();
                pasajeroActual = null;
                libre = true;
                disponible.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
