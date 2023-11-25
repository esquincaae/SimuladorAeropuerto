package com.example.simuladoraeropuerto.views;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class VistaPrincipal {

    public Pane crearContenido() {
        Pane root = new Pane();

        // Área de control de pasaportes
        Rectangle areaControlPasaportes = new Rectangle(300, 100, Color.LIGHTBLUE);
        areaControlPasaportes.setX(10);
        areaControlPasaportes.setY(10);
        Text textoControlPasaportes = new Text(20, 50, "Control de Pasaportes");

        // Cabinas de control de pasaportes
        for (int i = 0; i < 10; i++) {
            Rectangle cabina = new Rectangle(20, 20, Color.GRAY);
            cabina.setX(15 + i * 30);
            cabina.setY(15);
            root.getChildren().add(cabina);
        }

        // Área de manejo de equipaje
        Rectangle areaManejoEquipaje = new Rectangle(300, 100, Color.BEIGE);
        areaManejoEquipaje.setX(10);
        areaManejoEquipaje.setY(120);
        Text textoManejoEquipaje = new Text(20, 160, "Manejo de Equipaje");

        // Cinta transportadora
        Rectangle cintaEquipaje = new Rectangle(280, 20, Color.DARKGRAY);
        cintaEquipaje.setX(20);
        cintaEquipaje.setY(190);

        // Zona de espera
        Rectangle zonaEspera = new Rectangle(300, 100, Color.LIGHTGREEN);
        zonaEspera.setX(10);
        zonaEspera.setY(230);
        Text textoZonaEspera = new Text(20, 270, "Zona de Espera");

        // Representación de Pasajeros (Círculos Azules)
        for (int i = 0; i < 5; i++) {
            Circle pasajero = new Circle(10, Color.BLUE);
            pasajero.setCenterX(50 + i * 20);
            pasajero.setCenterY(300);
            root.getChildren().add(pasajero);
        }

        // Representación de Agentes de Control (Círculos Rojos)
        for (int i = 0; i < 10; i++) {
            Circle agente = new Circle(10, Color.RED);
            agente.setCenterX(15 + i * 30);
            agente.setCenterY(40);
            root.getChildren().add(agente);
        }

        // Representación de Operadores de Equipaje (Círculos Verdes)
        Circle operador = new Circle(10, Color.GREEN);
        operador.setCenterX(20);
        operador.setCenterY(210);
        root.getChildren().add(operador);

        // Agregar todos los elementos al root
        root.getChildren().addAll(areaControlPasaportes, areaManejoEquipaje, zonaEspera,
                textoControlPasaportes, textoManejoEquipaje, textoZonaEspera,
                cintaEquipaje);

        return root;
    }
}
