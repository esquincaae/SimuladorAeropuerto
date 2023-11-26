package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AgenteControl {
    private Circle visualRepresentation;

    public AgenteControl() {
        // Crear la representación visual del agente
        visualRepresentation = new Circle(15, Color.BLUE); // Color azul para los agentes
    }

    public Circle getVisualRepresentation() {
        return visualRepresentation;
    }

    // Métodos adicionales pueden ser añadidos aquí en el futuro
}
