package com.example.simuladoraeropuerto.threads;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.models.Pasajero;
import javafx.application.Platform;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HiloPasajero extends Observable implements Runnable {
    private final AeropuertoMonitor monitor;
    private ExecutorService executorService;

    public HiloPasajero(AeropuertoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while (true) { // Bucle infinito para generar pasajeros

            executorService = Executors.newCachedThreadPool();

            // Iniciar la generación de pasajeros automáticamente al cargar el controlador
            executorService.execute(() -> {
                rutina();
            });

            // Aquí puedes agregar un delay para simular tiempo entre llegadas
            try {
                Thread.sleep(1000); // Ejemplo: 1 segundo entre cada pasajero
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void rutina(){
        Pasajero pasajero = new Pasajero();
        entrar(pasajero);
    }

    public void entrar(Pasajero pasajero){
        monitor.entrarPasajero(pasajero);
        Platform.runLater(() -> {
            setChanged();
            notifyObservers(pasajero.getRepresentacion());
        });

        Platform.runLater(() -> {
            setChanged();
            notifyObservers(pasajero.getEquipaje());
        });

    }
}
