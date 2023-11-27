package com.example.simuladoraeropuerto.models;

import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pasajero extends Thread {
    private final VistaPrincipal vista;
    private final ControlPasaportes controlPasaportes;
    private final int x, y;
    private Circle pasajeroVisual;
    private Circle equipajeVisual; // Representación visual del equipaje
    private final Lock lock = new ReentrantLock();
    private final Condition atendido = lock.newCondition();
    private boolean haSidoAtendido = false;
    private Equipaje equipaje;

    public Pasajero(VistaPrincipal vista, ControlPasaportes control, int x, int y) {
        this.vista = vista;
        this.controlPasaportes = control;
        this.x = x;
        this.y = y;
        this.pasajeroVisual = new Circle(10, Color.RED);
        this.pasajeroVisual.setCenterX(x);
        this.pasajeroVisual.setCenterY(y);
        this.equipajeVisual = new Circle(5, Color.SADDLEBROWN);
        this.equipajeVisual.setCenterX(x + 15);
        this.equipajeVisual.setCenterY(y);
        this.equipaje = new Equipaje();
        this.start();
    }

    @Override
    public void run() {
        vista.agregarPasajeroAlAreaEntrada(pasajeroVisual, equipajeVisual);

        try {
            Thread.sleep(new Random().nextInt(4000) + 1000); // Tiempo antes de que el pasajero se mueva al control de pasaportes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AgenteControl agenteLibre = encontrarAgenteLibre();
        if (agenteLibre != null) {
            // Mueve el pasajero y su equipaje a la posición del agente
            moverPasajeroYEquipaje(agenteLibre);

            // Mover al agente a la posición del pasajero en la zona de control de pasaportes
            Platform.runLater(() -> {
                int posX = (int) pasajeroVisual.getCenterX();
                int posY = (int) pasajeroVisual.getCenterY();
                vista.moverAgenteAEspacioDeTrabajo(agenteLibre, posX, posY);
            });

            agenteLibre.atenderPasajero(this);
        }

        controlPasaportes.atenderPasajero(this);
        esperarAtencion();

        vista.entregarEquipaje(this, this.equipaje);
    }


    private AgenteControl encontrarAgenteLibre() {
        for (AgenteControl agente : controlPasaportes.getAgentes()) {
            if (agente.estaLibre()) {
                return agente;
            }
        }
        return null;
    }

    private void moverPasajeroYEquipaje(AgenteControl agente) {
        double posXAgente = agente.getVisualRepresentation().getCenterX();
        double posYAgente = agente.getVisualRepresentation().getCenterY();

        Platform.runLater(() -> {
            pasajeroVisual.setCenterX(posXAgente);
            pasajeroVisual.setCenterY(posYAgente + 30);
            equipajeVisual.setCenterX(posXAgente + 15);
            equipajeVisual.setCenterY(posYAgente + 30);
        });
    }

    public Circle getVisualRepresentation() {
        return pasajeroVisual;
    }

    public void removerEquipaje() {
        Platform.runLater(() -> {
            vista.removerEquipaje(equipajeVisual);
        });
    }

    public void salirDeLaCola() {
        Platform.runLater(() -> {
            vista.removerPasajeroDeCola(this);
        });
        vista.decrementarNumeroEnCola();
    }

    public void esperarAtencion() {
        lock.lock();
        try {
            while (!haSidoAtendido) {
                atendido.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void atencionCompletada() {
        lock.lock();
        try {
            haSidoAtendido = true;
            atendido.signal();
        } finally {
            lock.unlock();
        }
    }

    public void moverALaCola() {
        Platform.runLater(() -> {
            pasajeroVisual.setCenterX(550);
            pasajeroVisual.setCenterY(50 + vista.getNumeroEnCola() * 20);
            equipajeVisual.setCenterX(550 + 15);
            equipajeVisual.setCenterY(50 + vista.getNumeroEnCola() * 20);
        });
        vista.incrementarNumeroEnCola();
    }
}
