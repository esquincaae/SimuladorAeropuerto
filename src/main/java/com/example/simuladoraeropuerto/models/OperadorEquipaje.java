package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OperadorEquipaje extends Thread {
    private Circle visualRepresentation;
    private Equipaje equipajeActual;

    public OperadorEquipaje() {
        visualRepresentation = new Circle(15, Color.PURPLE);
        this.start();
    }

    public synchronized void procesarEquipaje(Equipaje equipaje) {
        this.equipajeActual = equipaje;
        // Procesar el equipaje
        try {
            Thread.sleep(1000); // Simular tiempo de procesamiento
            // Colocar equipaje en la cinta transportadora
            equipajeActual = null;
            notifyAll(); // Notificar que el operador está libre
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public synchronized boolean estaLibre() {
        return equipajeActual == null;
    }
    public Circle getVisualRepresentation() {
        return visualRepresentation;
    }

    @Override
    public void run() {
        while (true) {
            // Esperar por un nuevo equipaje para procesar
            synchronized (this) {
                while (equipajeActual == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            procesarEquipaje(equipajeActual);
        }
    }
}
