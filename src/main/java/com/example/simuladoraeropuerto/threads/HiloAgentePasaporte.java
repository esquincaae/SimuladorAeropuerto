package com.example.simuladoraeropuerto.threads;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.AgentePasaporte;
import javafx.application.Platform;

import java.util.Observable;
import java.util.Random;

public class HiloAgentePasaporte extends Observable implements Runnable {
    private final AeropuertoMonitor monitor;

    public HiloAgentePasaporte(AeropuertoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        for (int i = 0; i < AeropuertoMonitor.MAX_AGENTES; i++) {
            AgentePasaporte agente = new AgentePasaporte();
            monitor.entrarAgentePasaporte(agente);

            Platform.runLater(() -> {
                setChanged();
                notifyObservers(agente.getRepresentacion());
            });

            // No es necesario mover al agente aquí, ya que se mueve en teletransportarAPasaportes
        }
    }

}
