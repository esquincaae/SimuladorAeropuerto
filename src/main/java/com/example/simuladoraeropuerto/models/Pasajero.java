package com.example.simuladoraeropuerto.models;

import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pasajero extends Thread {
    private final VistaPrincipal vista;
    private final int x, y; // Posiciones para el pasajero

    public Pasajero(VistaPrincipal vista, int x, int y) {
        this.vista = vista;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        Circle pasajeroVisual = new Circle(10, Color.RED); // Tamaño y color del círculo
        pasajeroVisual.setCenterX(x);
        pasajeroVisual.setCenterY(y);
        vista.agregarPasajeroAlAreaEntrada(pasajeroVisual);
    }
}
