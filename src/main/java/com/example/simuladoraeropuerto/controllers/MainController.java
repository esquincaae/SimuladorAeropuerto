package com.example.simuladoraeropuerto.controllers;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.AgentePasaporte;
import com.example.simuladoraeropuerto.models.Circulo;
import com.example.simuladoraeropuerto.models.Pasajero;
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
        monitor = new AeropuertoMonitor(airportArea);

        // Inicializa el agente de pasaporte
        inicializarAgentePasaporte();

        // Configura y arranca el hilo de pasajeros
        HiloPasajero hiloPasajero = new HiloPasajero(monitor);
        hiloPasajero.addObserver(this);
        Thread h = new Thread(hiloPasajero);
        h.start();
    }

    private void inicializarAgentePasaporte() {
        Platform.runLater(() -> {
            for (int i = 0; i < NUMERO_AGENTES; i++) {
                AgentePasaporte agente = new AgentePasaporte();

                // Calcula la posición de cada agente
                double espacioEntreAgentes = controlPasaportesArea.getWidth() / NUMERO_AGENTES;
                double posicionXAgente = espacioEntreAgentes * i + espacioEntreAgentes / 2 - agente.getRepresentacion().getCircle().getRadius();
                double posicionYAgente = controlPasaportesArea.getHeight() / 2 - agente.getRepresentacion().getCircle().getRadius();

                // Ajusta la posición del agente
                agente.getRepresentacion().setCircle((int) posicionXAgente, (int) posicionYAgente);

                // Añade el agente al Pane
                controlPasaportesArea.getChildren().add(agente.getRepresentacion().getCircle());
            }
        });
    }



    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Circulo) {
            Circulo c = (Circulo) arg;
            if(c.isEstar()){
                Platform.runLater(() -> {
                    if(c.getCircle().getParent() != null){
                        ((Pane)c.getCircle().getParent()).getChildren().remove(c.getCircle());
                    }
                    airportArea.getChildren().add(c.getCircle());
                });
            }
        }
    }
}
