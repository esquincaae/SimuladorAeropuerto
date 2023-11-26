package com.example.simuladoraeropuerto.views;

import com.example.simuladoraeropuerto.models.AgenteControl;
import com.example.simuladoraeropuerto.models.OperadorEquipaje;
import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.Queue;

public class VistaPrincipal {
    private Pane areaEntrada;
    private int pasajerosEnAreaEntrada = 0;
    private final int MAX_PASAJEROS_AREA_ENTRADA = 10;
    private Queue<Circle> listaEspera = new LinkedList<>();

    public Pane crearContenido() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F0F0;");

        areaEntrada = crearAreaEntrada();
        Pane areaControlPasaportes = crearAreaControlPasaportes();
        Pane areaManejoEquipaje = crearAreaManejoEquipaje();
        Pane zonaEspera = crearZonaEspera();

        root.getChildren().addAll(areaEntrada, areaControlPasaportes, areaManejoEquipaje, zonaEspera);

        return root;
    }

    public void agregarPasajeroAlAreaEntrada(Circle pasajero) {
        if (pasajerosEnAreaEntrada < MAX_PASAJEROS_AREA_ENTRADA) {
            Platform.runLater(() -> areaEntrada.getChildren().add(pasajero));
            pasajerosEnAreaEntrada++;
        } else {
            listaEspera.add(pasajero); // Agregamos el pasajero a la lista de espera
        }
    }

    public int[] obtenerPosicionLibre() {
        int x = 20 + (pasajerosEnAreaEntrada % 5) * 30; // Espacio horizontal entre pasajeros
        int y = 50 + (pasajerosEnAreaEntrada / 5) * 30; // Espacio vertical entre pasajeros
        return new int[]{x, y};
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

    private Pane crearAreaControlPasaportes() {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10));
        Rectangle area = new Rectangle(500, 150, Color.LIGHTBLUE);
        Text texto = new Text("Control de Pasaportes");
        texto.setFont(new Font("Arial", 20));
        texto.setX(20);
        texto.setY(35);

        pane.getChildren().addAll(area, texto);

        // Creación de los agentes
        for (int i = 0; i < 10; i++) {
            AgenteControl agente = new AgenteControl();
            Circle visualRepresentation = agente.getVisualRepresentation();
            visualRepresentation.setCenterX(50 + i * 45); // Posicionamiento horizontal
            visualRepresentation.setCenterY(75); // Posición vertical
            pane.getChildren().add(visualRepresentation);
        }

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

        // Creación de los operadores de equipaje
        for (int i = 0; i < 5; i++) { // Número de operadores
            OperadorEquipaje operador = new OperadorEquipaje();
            Circle visualRepresentation = operador.getVisualRepresentation();
            visualRepresentation.setCenterX(50 + i * 90); // Posicionamiento horizontal
            visualRepresentation.setCenterY(120); // Posición vertical
            pane.getChildren().add(visualRepresentation);
        }

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
