package com.example.simuladoraeropuerto.controllers;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.Circulo;
import com.example.simuladoraeropuerto.threads.HiloAgentePasaporte;
import com.example.simuladoraeropuerto.threads.HiloAgenteEquipaje;
import com.example.simuladoraeropuerto.threads.HiloPasajero;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
public class MainController implements Observer {
    @FXML
    private Pane airportArea;

    @FXML
    private Pane zonaEspera;

    @FXML
    private Pane controlPasaportesArea;
    @FXML
    private Pane equipajeArea;
    @FXML
    private Pane areaSalida;


    private AeropuertoMonitor monitor;
    private ExecutorService executorService;

    @FXML
    public void initialize() {
        monitor = new AeropuertoMonitor(airportArea, controlPasaportesArea, equipajeArea, zonaEspera, areaSalida);


        HiloPasajero hiloPasajero = new HiloPasajero(monitor);
        hiloPasajero.addObserver(this);
        Thread hPasajero = new Thread(hiloPasajero);
        hPasajero.start();

        HiloAgentePasaporte hiloAgentePasaporte = new HiloAgentePasaporte(monitor);
        hiloAgentePasaporte.addObserver(this);
        Thread hAgentePasaporte = new Thread(hiloAgentePasaporte);
        hAgentePasaporte.start();

        HiloAgenteEquipaje hiloAgenteEquipaje = new HiloAgenteEquipaje(monitor);
        hiloAgenteEquipaje.addObserver(this);
        Thread hAgenteEquipaje = new Thread(hiloAgenteEquipaje);
        hAgenteEquipaje.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Circulo) {
            Circulo c = (Circulo) arg;
            if (c.isEstar()) {
                Platform.runLater(() -> {
                    if (c.getCircle().getParent() != null) {
                        ((Pane) c.getCircle().getParent()).getChildren().remove(c.getCircle());
                    }
                    if (o instanceof HiloPasajero) {
                        airportArea.getChildren().add(c.getCircle());
                    } else {
                        zonaEspera.getChildren().add(c.getCircle());
                    }
                });
            }
        }
    }

}
