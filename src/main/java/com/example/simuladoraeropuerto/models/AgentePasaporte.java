package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AgentePasaporte {
    private Circulo representacion;

    public AgentePasaporte() {
        Circle circle = new Circle(10, Color.BLUE); // Círculo azul para el agente
        this.representacion = new Circulo(circle);
    }

    public Circulo getRepresentacion() {
        return representacion;
    }
}
