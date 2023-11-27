package com.example.simuladoraeropuerto.models;

import com.example.simuladoraeropuerto.views.VistaPrincipal;
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
    private VistaPrincipal vistaPrincipal;
    private boolean enZonaTrabajo = false;

    public AgenteControl(VistaPrincipal vista) {
        this.vistaPrincipal = vista;
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

    private void moverASuZona() {
        if (vistaPrincipal.getNumeroEnCola() > 0) {
            if (!enZonaTrabajo) {
                vistaPrincipal.moverAgenteAEspacioDeTrabajo(this, 100, 100); // Estos valores deben ajustarse
                enZonaTrabajo = true;
            }
        } else {
            if (enZonaTrabajo) {
                vistaPrincipal.moverAgenteAZonaEspera(this, 50, 75); // Estos valores deben ajustarse
                enZonaTrabajo = false;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            moverASuZona();
            lock.lock();
            try {
                while (pasajeroActual == null) {
                    disponible.await();
                }
                Thread.sleep(1000); // Simular atención al pasajero
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
