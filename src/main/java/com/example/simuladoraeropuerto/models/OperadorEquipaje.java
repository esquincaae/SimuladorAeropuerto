package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OperadorEquipaje {
    private Circle visualRepresentation;

    public OperadorEquipaje() {
        // Crear la representación visual del operador de equipaje
        visualRepresentation = new Circle(15, Color.PURPLE); // Color púrpura para operadores
    }

    public Circle getVisualRepresentation() {
        return visualRepresentation;
    }

    // Métodos adicionales para lógica futura pueden ser añadidos aquí
}
