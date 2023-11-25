package com.example.simuladoraeropuerto.app;

import com.example.simuladoraeropuerto.views.VistaPrincipal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Aeropuerto extends Application {

    @Override
    public void start(Stage primaryStage) {
        VistaPrincipal vistaPrincipal = new VistaPrincipal();
        Scene scene = new Scene(vistaPrincipal.crearContenido(), 600, 600);

        primaryStage.setTitle("Simulación de Aeropuerto");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
