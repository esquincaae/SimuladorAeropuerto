package com.example.simuladoraeropuerto.models;

import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pasajero extends Thread {
    private final VistaPrincipal vista;
    private final ControlPasaportes controlPasaportes;
    private final int x, y;
    private Circle pasajeroVisual;

    private Circle equipajeVisual; // Representación visual del equipaje

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
    }

    @Override
    public void run() {
        vista.agregarPasajeroAlAreaEntrada(pasajeroVisual, equipajeVisual);
        controlPasaportes.atenderPasajero(this);
    }
    // Método para remover el equipaje de la vista
    public void removerEquipaje() {
        Platform.runLater(() -> {
            // Remover el equipaje visual de la vista principal
            vista.removerEquipaje(equipajeVisual);
        });
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