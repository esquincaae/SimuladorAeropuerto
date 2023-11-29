package com.example.simuladoraeropuerto.controllers;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.AgentePasaporte;
import com.example.simuladoraeropuerto.models.Circulo;
import com.example.simuladoraeropuerto.models.Pasajero;
import com.example.simuladoraeropuerto.threads.HiloAgentePasaporte;
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

    private static final int NUMERO_AGENTES = 10; // Número de agentes de pasaporte
    private AeropuertoMonitor monitor;
    private ExecutorService executorService;
    private AgentePasaporte agentePasaporte;

    @FXML
    public void initialize() {
        monitor = new AeropuertoMonitor(airportArea, controlPasaportesArea);

        // Inicializar y arrancar el hilo de pasajeros
        HiloPasajero hiloPasajero = new HiloPasajero(monitor);
        hiloPasajero.addObserver(this);
        Thread hPasajero = new Thread(hiloPasajero);
        hPasajero.start();

        // Inicializar y arrancar el hilo de agentes de pasaporte
        HiloAgentePasaporte hiloAgente = new HiloAgentePasaporte(monitor);
        hiloAgente.addObserver(this);
        Thread hAgente = new Thread(hiloAgente);
        hAgente.start();
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
                    }
                });
            }
        }
    }
}

