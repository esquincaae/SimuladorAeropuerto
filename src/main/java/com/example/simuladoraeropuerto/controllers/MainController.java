package com.example.simuladoraeropuerto.controllers;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.Circulo;
import com.example.simuladoraeropuerto.threads.HiloPasajero;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Observer {
    @FXML
    private Pane airportArea; // Área del aeropuerto donde se mostrarán los pasajeros

    private AeropuertoMonitor monitor;
    private ExecutorService executorService;

    @FXML
    public void initialize() {
        monitor = new AeropuertoMonitor(airportArea);
        HiloPasajero hiloPasajero = new HiloPasajero(monitor);
        hiloPasajero.addObserver(this);
        Thread h = new Thread(hiloPasajero);

        h.start();
    }

    public void stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // Asegurarse de cerrar el ExecutorService
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Circulo c = (Circulo) arg;
        if(c.isEstar()){
            if(c.getCircle().getParent() != null){
                ((Pane)c.getCircle().getParent()).getChildren().remove(c.getCircle());
            }
            airportArea.getChildren().add(c.getCircle());
        }
    }
}
