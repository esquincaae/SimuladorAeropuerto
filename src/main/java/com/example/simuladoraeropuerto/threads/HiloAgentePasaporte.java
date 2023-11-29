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
        Random random = new Random();
        for (int i = 0; i < AeropuertoMonitor.MAX_AGENTES; i++) {
            AgentePasaporte agente = new AgentePasaporte();
            monitor.entrarAgentePasaporte(agente);

            Platform.runLater(() -> {
                setChanged();
                notifyObservers(agente.getRepresentacion());
            });

            // Espera aleatoria antes de mover al agente al área de control de pasaportes
            try {
                Thread.sleep((random.nextInt(5) + 1) * 1000); // Espera de 1 a 5 segundos
                monitor.teletransportarAgentePasaportes(agente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
