package com.example.simuladoraeropuerto.app;

import com.example.simuladoraeropuerto.models.AgenteControl;
import com.example.simuladoraeropuerto.models.Pasajero;
import com.example.simuladoraeropuerto.models.ControlPasaportes;
import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AeropuertoApp extends Application {
    private VistaPrincipal vistaPrincipal;
    private ControlPasaportes controlPasaportes;
    private ScheduledExecutorService executorService;

    @Override
    public void start(Stage primaryStage) {
        vistaPrincipal = new VistaPrincipal();
        AgenteControl[] agentes = vistaPrincipal.getAgentes();
        controlPasaportes = new ControlPasaportes(10, agentes);

        Scene scene = new Scene(vistaPrincipal.crearContenido(), 600, 600);

        primaryStage.setTitle("Simulación de Aeropuerto");
        primaryStage.setScene(scene);
        primaryStage.show();

        iniciarSimulacion();
    }

    private void iniciarSimulacion() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                int[] posicion = vistaPrincipal.obtenerPosicionLibre();
                Pasajero pasajero = new Pasajero(vistaPrincipal, controlPasaportes, posicion[0], posicion[1]);
                if (pasajero.getState() == Thread.State.NEW) {
                    pasajero.start();
                }
            });
        }, 0, 1, TimeUnit.SECONDS); // Inicia inmediatamente, repite cada 1 segundo
    }

    @Override
    public void stop() throws Exception {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}