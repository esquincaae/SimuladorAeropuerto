package com.example.simuladoraeropuerto.models;

import javafx.scene.shape.Circle;

public class Circulo {
    private boolean estar;
    private Circle circle;

    public Circulo(Circle circle){
        this.circle = circle;
        this.estar = false;
    }

    public void setCircle (int x, int y) {
        this.circle.setCenterX(x);
        this.circle.setCenterY(y);
    }

    public void setEstar(boolean estar){
        this.estar = estar;
    }

    public Circle getCircle() {
        return circle;
    }

    public boolean isEstar() {
        return estar;
    }
}
