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

    public Pasajero(VistaPrincipal vista, ControlPasaportes control, int x, int y) {
        this.vista = vista;
        this.controlPasaportes = control;
        this.x = x;
        this.y = y;
        this.pasajeroVisual = new Circle(10, Color.RED);
        this.pasajeroVisual.setCenterX(x);
        this.pasajeroVisual.setCenterY(y);
    }

    @Override
    public void run() {
        vista.agregarPasajeroAlAreaEntrada(pasajeroVisual);
        controlPasaportes.atenderPasajero(this);
    }

    public void moverALaCola() {
        Platform.runLater(() -> {
            // Supongamos que la cola empieza en (x=550, y=50) y cada nuevo pasajero se pone detrás del anterior
            pasajeroVisual.setCenterX(550);
            pasajeroVisual.setCenterY(50 + vista.getNumeroEnCola() * 20);
        });
        vista.incrementarNumeroEnCola(); // Aumentar el contador de pasajeros en la cola
    }

}
