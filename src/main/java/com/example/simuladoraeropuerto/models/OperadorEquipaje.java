package com.example.simuladoraeropuerto.models;

import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OperadorEquipaje extends Thread {
    private Circle visualRepresentation;
    private Equipaje equipajeActual;
    private VistaPrincipal vista; // Referencia a VistaPrincipal

    public OperadorEquipaje(VistaPrincipal vista) {
        this.vista = vista; // Inicializa la referencia
        visualRepresentation = new Circle(15, Color.PURPLE);
        this.start();
    }
    public synchronized void procesarEquipaje(Equipaje equipaje) {
        this.equipajeActual = equipaje;
        vista.iniciarProcesamientoEquipaje(equipaje); // Notificar inicio de procesamiento
        try {
            Thread.sleep(1000); // Simular tiempo de procesamiento
            vista.terminarProcesamientoEquipaje(equipaje); // Notificar fin de procesamiento
            equipajeActual = null;
            notifyAll();
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