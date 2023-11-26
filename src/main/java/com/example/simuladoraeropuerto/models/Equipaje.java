package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Equipaje {
    private Circle visualRepresentation;

    public Equipaje() {
        visualRepresentation = new Circle(5, Color.SADDLEBROWN);
    }

    public Circle getVisualRepresentation() {
        return visualRepresentation;
    }
}
