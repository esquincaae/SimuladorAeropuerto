package com.example.simuladoraeropuerto.views;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class VistaPrincipal {

    public Pane crearContenido() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F0F0;");

        Pane areaEntrada = crearAreaEntrada();
        Pane areaControlPasaportes = crearAreaControlPasaportes();
        Pane areaManejoEquipaje = crearAreaManejoEquipaje();
        Pane zonaEspera = crearZonaEspera();

        root.getChildren().addAll(areaEntrada, areaControlPasaportes, areaManejoEquipaje, zonaEspera);

        return root;
    }

    private Pane crearAreaControlPasaportes() {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10));
        Rectangle area = new Rectangle(500, 150, Color.LIGHTBLUE);
        Text texto = new Text("Control de Pasaportes");
        texto.setFont(new Font("Arial", 20));
        texto.setX(20);
        texto.setY(35);
        pane.getChildren().addAll(area, texto);

        // Los círculos que representaban a los agentes han sido removidos

        return pane;
    }

    private Pane crearAreaEntrada() {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10));
        Rectangle area = new Rectangle(500, 100, Color.SANDYBROWN);
        Text texto = new Text("Entrada del Aeropuerto");
        texto.setFont(new Font("Arial", 20));
        texto.setX(20);
        texto.setY(35);

        pane.getChildren().addAll(area, texto);

        return pane;
    }

    private Pane crearAreaManejoEquipaje() {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10));
        Rectangle area = new Rectangle(500, 150, Color.BEIGE);
        Text texto = new Text("Manejo de Equipaje");
        texto.setFont(new Font("Arial", 20));
        texto.setX(20);
        texto.setY(35);
        Rectangle cinta = new Rectangle(480, 30, Color.DARKGRAY);
        cinta.setX(10);
        cinta.setY(60);

        pane.getChildren().addAll(area, texto, cinta);

        return pane;
    }

    private Pane crearZonaEspera() {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10));
        Rectangle area = new Rectangle(500, 150, Color.LIGHTGREEN);
        Text texto = new Text("Zona de Espera");
        texto.setFont(new Font("Arial", 20));
        texto.setX(20);
        texto.setY(35);

        pane.getChildren().addAll(area, texto);

        return pane;
    }
}