package com.example.simuladoraeropuerto.app;

import com.example.simuladoraeropuerto.models.AgenteControl;
import com.example.simuladoraeropuerto.models.Pasajero;
import com.example.simuladoraeropuerto.models.ControlPasaportes;
import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AeropuertoApp extends Application {
    private VistaPrincipal vistaPrincipal;
    private ControlPasaportes controlPasaportes;

    public void start(Stage primaryStage) {
        vistaPrincipal = new VistaPrincipal();
        AgenteControl[] agentes = vistaPrincipal.getAgentes();
        controlPasaportes = new ControlPasaportes(10, agentes); // Ahora pasando los agentes

        Scene scene = new Scene(vistaPrincipal.crearContenido(), 600, 600);

        primaryStage.setTitle("Simulación de Aeropuerto");
        primaryStage.setScene(scene);
        primaryStage.show();

        iniciarSimulacion();
    }

    private void iniciarSimulacion() {
        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    int[] posicion = vistaPrincipal.obtenerPosicionLibre();
                    Pasajero pasajero = new Pasajero(vistaPrincipal, controlPasaportes, posicion[0], posicion[1]);
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

    public static void main(String[] args) {
        launch(args);
    }
}
