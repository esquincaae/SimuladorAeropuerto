package com.example.simuladoraeropuerto.threads;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;

import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HiloPasajero extends Observable implements Runnable {
    private final AeropuertoMonitor monitor;
    private ExecutorService executorService;
    private Random random = new Random();

    public HiloPasajero(AeropuertoMonitor monitor) {
        this.monitor = monitor;
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            executorService.execute(this::manejarPasajero);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void manejarPasajero() {
        Pasajero pasajero = new Pasajero();
        entrarYTeletransportar(pasajero);
    }

    private void entrarYTeletransportar(Pasajero pasajero) {
        int posicionEntrada = monitor.entrarPasajero(pasajero);
        Platform.runLater(() -> {
            setChanged();
            notifyObservers(pasajero.getRepresentacion());
        });

        Platform.runLater(() -> {
            setChanged();
            notifyObservers(pasajero.getEquipaje());
        });

        try {
            Thread.sleep((random.nextInt(5) + 1) * 1000);
            monitor.teletransportarAPasaportes(pasajero, posicionEntrada);

            Thread.sleep((random.nextInt(5) + 1) * 1000);
            monitor.teletransportarAEquipaje(pasajero);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
