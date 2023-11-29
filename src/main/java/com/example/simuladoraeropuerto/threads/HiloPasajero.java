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
    }

    @Override
    public void run() {
        while (true) {
            executorService = Executors.newCachedThreadPool();
            executorService.execute(this::rutina);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void rutina() {
        Pasajero pasajero = new Pasajero();
        entrarYTeletransportar(pasajero);
    }


    private void entrarYTeletransportar(Pasajero pasajero) {
        int posicionEntrada = monitor.entrarPasajero(pasajero); // Ahora esto captura un int
        Platform.runLater(() -> {
            setChanged();
            notifyObservers(pasajero.getRepresentacion());
        });

        Platform.runLater(() -> {
            setChanged();
            notifyObservers(pasajero.getEquipaje());
        });

        esperarYTeletransportar(pasajero, posicionEntrada);
    }

    private void esperarYTeletransportar(Pasajero pasajero, int posicionEntrada) {
        try {
            Thread.sleep((random.nextInt(5) + 1) * 1000); // Espera de 1 a 5 segundos
            monitor.teletransportarAPasaportes(pasajero, posicionEntrada);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

