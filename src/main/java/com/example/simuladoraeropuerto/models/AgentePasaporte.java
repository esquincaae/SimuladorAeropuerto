package com.example.simuladoraeropuerto.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AgentePasaporte {
    private Circulo representacion;

    public AgentePasaporte() {
        Circle circle = new Circle(10, Color.BLUE);
        this.representacion = new Circulo(circle);
    }

    public Circulo getRepresentacion() {
        return representacion;
    }

    public void ModificarRepresentacion(int x, int y, boolean estar) {
        this.representacion.setCircle(x, y);
        this.representacion.setEstar(estar);
    }
}
