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

    @Override
    public void run() {
        vista.agregarPasajeroAlAreaEntrada(pasajeroVisual, equipajeVisual);

        try {
            // Espera aleatoria antes de dirigirse al control de pasaportes
            Thread.sleep(new Random().nextInt(4000) + 1000); // Espera de 1 a 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mover visualmente al pasajero hacia el área de control de pasaportes
        Platform.runLater(() -> {
            pasajeroVisual.setCenterX(250); // Por ejemplo, en la mitad de la ventana
            pasajeroVisual.setCenterY(75);  // Posición Y cerca de los agentes
        });

        controlPasaportes.atenderPasajero(this);
        esperarAtencion(); // Espera hasta que la atención esté completa

        // Procesar el equipaje después del control de pasaportes
        vista.entregarEquipaje(this, this.equipaje);
    }

    public Circle getVisualRepresentation() {
        return pasajeroVisual;
    }
    public Pasajero(VistaPrincipal vista, ControlPasaportes control, int x, int y) {
        this.vista = vista;
        this.controlPasaportes = control;
        this.x = x;
        this.y = y;
        this.pasajeroVisual = new Circle(10, Color.RED);
        this.pasajeroVisual.setCenterX(x);
        this.pasajeroVisual.setCenterY(y);
        this.equipajeVisual = new Circle(5, Color.SADDLEBROWN); // Equipaje más pequeño y de color café
        this.equipajeVisual.setCenterX(x + 15); // Posicionamiento inicial del equipaje al lado del pasajero
        this.equipajeVisual.setCenterY(y);
        this.equipaje = new Equipaje();
    }


    // Método para remover el equipaje de la vista
    public void removerEquipaje() {
        Platform.runLater(() -> {
            // Remover el equipaje visual de la vista principal
            vista.removerEquipaje(equipajeVisual);
        });
    }



    public void salirDeLaCola() {
        Platform.runLater(() -> {
            // Actualizar la posición visual del pasajero al salir de la cola
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
            equipajeVisual.setCenterX(550 + 15); // Mover el equipaje junto con el pasajero
            equipajeVisual.setCenterY(50 + vista.getNumeroEnCola() * 20);
        });
        vista.incrementarNumeroEnCola();
    }


}