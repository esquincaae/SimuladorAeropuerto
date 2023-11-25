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

        for (int i = 0; i < 10; i++) {
            Rectangle cabina = new Rectangle(30, 30, Color.GRAY);
            cabina.setX(40 + i * 45);
            cabina.setY(60);
            pane.getChildren().add(cabina);

            Circle agente = new Circle(15, Color.RED);
            agente.setCenterX(55 + i * 45);
            agente.setCenterY(110);
            pane.getChildren().add(agente);
        }

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

        // Representación visual de los pasajeros llegando
        for (int i = 0; i < 5; i++) {
            Circle pasajero = new Circle(15, Color.BLUE);
            pasajero.setCenterX(100 + i * 40);
            pasajero.setCenterY(70);
            pane.getChildren().add(pasajero);
        }

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

        Circle operador = new Circle(15, Color.GREEN);
        operador.setCenterX(30);
        operador.setCenterY(120);

        pane.getChildren().addAll(area, texto, cinta, operador);

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

        for (int i = 0; i < 5; i++) {
            Circle pasajero = new Circle(15, Color.BLUE);
            pasajero.setCenterX(100 + i * 40);
            pasajero.setCenterY(100);
            pane.getChildren().add(pasajero);
        }

        pane.getChildren().addAll(area, texto);

        return pane;
    }
}
