package com.example.simuladoraeropuerto.threads;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.AgenteEquipaje;
import javafx.application.Platform;

import java.util.Observable;

public class HiloAgenteEquipaje extends Observable implements Runnable {
    private final AeropuertoMonitor monitor;

    public HiloAgenteEquipaje(AeropuertoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        for (int i = 0; i < AeropuertoMonitor.MAX_AGENTES_EQUIPAJE; i++) {
            AgenteEquipaje agente = new AgenteEquipaje();
            monitor.entrarAgenteEquipaje(agente);

            Platform.runLater(() -> {
                setChanged();
                notifyObservers(agente.getRepresentacion());
            });
        }
    }
}
