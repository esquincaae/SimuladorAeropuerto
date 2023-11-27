package com.example.simuladoraeropuerto.views;

import com.example.simuladoraeropuerto.models.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VistaPrincipal {

    private ControlPasaportes controlPasaportes;
    private Pane areaEntrada;
    private int pasajerosEnAreaEntrada = 0;

    private AgenteControl[] agentes; // Declaración de la variable 'agentes
    private final int MAX_PASAJEROS_AREA_ENTRADA = 10;
    private Queue<Circle> listaEspera = new LinkedList<>();
    private List<OperadorEquipaje> operadoresEquipaje;
    private int numeroEnCola = 0; // Contador para los pasajeros en la cola
    private Pane areaCola; // Área para mostrar la cola de pasajeros
    private List<Circle> colaPasajeros; // Lista para mantener los círculos de los pasajeros en la cola

    public synchronized void decrementarNumeroEnCola() {
        if (numeroEnCola > 0) {
            numeroEnCola--;
        }
    }

    public synchronized int getNumeroEnCola() {
        return numeroEnCola;
    }
    private static final int NUMERO_OPERADORES = 5; // Ajusta el número según sea necesario
    public synchronized void incrementarNumeroEnCola() {
        numeroEnCola++;
    }
    public VistaPrincipal() {
        colaPasajeros = new ArrayList<>();
        areaCola = crearAreaCola();
        areaEntrada = crearAreaEntrada();

        agentes = new AgenteControl[10];
        for (int i = 0; i < agentes.length; i++) {
            agentes[i] = new AgenteControl();
        }
        this.controlPasaportes = new ControlPasaportes(10, agentes);

        operadoresEquipaje = new ArrayList<>();
        for (int i = 0; i < NUMERO_OPERADORES; i++) {
            operadoresEquipaje.add(new OperadorEquipaje(this));
        }
    }

    private Pane crearAreaControlPasaportes() {
        Pane pane = new Pane();
        pane.setPadding(new Insets(10));
        Rectangle area = new Rectangle(500, 150, Color.LIGHTBLUE);
        Text texto = new Text("Control de Pasaportes");
        texto.setFont(new Font("Arial", 20));
        texto.setX(20);
        texto.setY(35);

        // Añadir primero el rectángulo y el texto
        pane.getChildren().addAll(area, texto);

        // Luego añade los agentes (asegurándote de que se rendericen por encima del rectángulo)
        for (int i = 0; i < agentes.length; i++) {
            Circle visual = agentes[i].getVisualRepresentation();
            visual.setCenterX(50 + i * 45);
            visual.setCenterY(75);
            pane.getChildren().add(visual);
        }

        return pane;
    }

    public void iniciarProcesamientoEquipaje(Equipaje equipaje) {
        Platform.runLater(() -> {
            Circle visualEquipaje = equipaje.getVisualRepresentation();
            // Asumiendo que tienes valores definidos para las posiciones
            visualEquipaje.setCenterX(300); // Ejemplo: posición X de procesamiento
            visualEquipaje.setCenterY(200); // Ejemplo: posición Y de procesamiento
        });
    }
    // Método para finalizar el procesamiento visual del equipaje
    public void terminarProcesamientoEquipaje(Equipaje equipaje) {
        Platform.runLater(() -> {
            Circle visualEquipaje = equipaje.getVisualRepresentation();
            // Mover el equipaje a la cinta transportadora
            visualEquipaje.setCenterX(200); // Ajusta según la ubicación de tu cinta transportadora
            visualEquipaje.setCenterY(80);  // Ajusta según la ubicación de tu cinta transportadora
        });
    }

    private Pane crearAreaCola() {
        Pane colaPane = new Pane();
        colaPane.setPrefSize(500, 100);
        colaPane.setStyle("-fx-background-color: lightgray;");
        return colaPane;
    }
    public AgenteControl[] getAgentes() {
        return agentes;
    }
    public Pane crearContenido() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F0F0;");

        areaEntrada = crearAreaEntrada();
        Pane areaControlPasaportes = crearAreaControlPasaportes();
        Pane areaManejoEquipaje = crearAreaManejoEquipaje();
        Pane zonaEspera = crearZonaEspera();

        root.getChildren().addAll(areaEntrada, areaControlPasaportes, areaManejoEquipaje, areaCola, zonaEspera);
        return root;
    }

    public synchronized void agregarPasajeroACola(Pasajero pasajero) {
        Platform.runLater(() -> {
            Circle visualPasajero = pasajero.getVisualRepresentation();
            // Calcular posición en la cola basada en numeroEnCola u otro criterio
            int posicionX = 20 + colaPasajeros.size() * 20; // Ejemplo de cálculo
            int posicionY = 50; // Posición vertical fija

            visualPasajero.setCenterX(posicionX);
            visualPasajero.setCenterY(posicionY);

            colaPasajeros.add(visualPasajero);
            areaCola.getChildren().add(visualPasajero);
        });
    }

    public synchronized void removerPasajeroDeCola(Pasajero pasajero) {
        Platform.runLater(() -> {
            Circle visualPasajero = pasajero.getVisualRepresentation();
            colaPasajeros.remove(visualPasajero);
            areaCola.getChildren().remove(visualPasajero);
            // Reajustar la cola si es necesario
            for (int i = 0; i < colaPasajeros.size(); i++) {
                Circle pasajeroEnCola = colaPasajeros.get(i);
                pasajeroEnCola.setCenterX(20 + i * 20); // Reajustar posiciones
            }
        });
    }

    public void agregarPasajeroAlAreaEntrada(Circle pasajero, Circle equipaje) {
        if (pasajerosEnAreaEntrada < MAX_PASAJEROS_AREA_ENTRADA) {
            Platform.runLater(() -> {
                areaEntrada.getChildren().addAll(pasajero, equipaje);
            });
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

    public void removerEquipaje(Circle equipaje) {
        Platform.runLater(() -> {
            areaEntrada.getChildren().remove(equipaje);
        });
    }
    public synchronized void entregarEquipaje(Pasajero pasajero, Equipaje equipaje) {
        for (OperadorEquipaje operador : operadoresEquipaje) {
            if (operador.estaLibre()) {
                operador.procesarEquipaje(equipaje);
                break;
            }
        }
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
        for (int i = 0; i < NUMERO_OPERADORES; i++) {
            OperadorEquipaje operador = operadoresEquipaje.get(i);
            Circle visualRepresentation = operador.getVisualRepresentation();
            visualRepresentation.setCenterX(50 + i * 90); // Posicionamiento horizontal
            visualRepresentation.setCenterY(120); // Posición vertical
            pane.getChildren().add(visualRepresentation);
        }

        return pane;
    }


    private void iniciarSimulacion() {
        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    int[] posicion = obtenerPosicionLibre();
                    Pasajero pasajero = new Pasajero(this, controlPasaportes, posicion[0], posicion[1]);
                    pasajero.start();
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

