package com.example.simuladoraeropuerto.models;

import javafx.scene.shape.Circle;

public class Pasajero {
    private Circle representacion;

    public Pasajero() {
        this.representacion = new Circle(10, javafx.scene.paint.Color.RED); // Círculo rojo como representación
    }

    public Circle getRepresentacion() {
        return representacion;
    }
}
