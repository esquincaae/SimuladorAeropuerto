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
import java.util.concurrent.Executors;

public class MainController implements Observer {
    @FXML
    private Pane airportArea; // Área del aeropuerto donde se mostrarán los pasajeros
    @FXML
    private Pane controlPasaportesArea; // Área de control de pasaportes
    @FXML
    private Pane equipajeArea; // Área de manejo de equipaje

    private AeropuertoMonitor monitor;
    private ExecutorService executorService;

    @FXML
    public void initialize() {
        monitor = new AeropuertoMonitor(airportArea, controlPasaportesArea, equipajeArea);

        // Inicializar y arrancar el hilo de pasajeros
        HiloPasajero hiloPasajero = new HiloPasajero(monitor);
        hiloPasajero.addObserver(this);
        Thread hPasajero = new Thread(hiloPasajero);
        hPasajero.start();

        // Inicializar y arrancar el hilo de agentes de pasaporte
        HiloAgentePasaporte hiloAgentePasaporte = new HiloAgentePasaporte(monitor);
        hiloAgentePasaporte.addObserver(this);
        Thread hAgentePasaporte = new Thread(hiloAgentePasaporte);
        hAgentePasaporte.start();

        // Inicializar y arrancar el hilo de agentes de equipaje
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
                    // Determinar dónde añadir el círculo en función del tipo de objeto observado
                    if (o instanceof HiloPasajero) {
                        airportArea.getChildren().add(c.getCircle());
                    } else if (o instanceof HiloAgentePasaporte) {
                        controlPasaportesArea.getChildren().add(c.getCircle());
                    } else if (o instanceof HiloAgenteEquipaje) {
                        equipajeArea.getChildren().add(c.getCircle());
                    }
                });
            }
        }
    }
}
