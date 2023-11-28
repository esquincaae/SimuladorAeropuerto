package com.example.simuladoraeropuerto;

import com.example.simuladoraeropuerto.concurrent.AeropuertoMonitor;
import com.example.simuladoraeropuerto.concurrent.PasajeroGenerator;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloController {
    @FXML
    private Pane airportArea; // Área del aeropuerto donde se mostrarán los pasajeros

    private AeropuertoMonitor monitor;
    private ExecutorService executorService;

    @FXML
    public void initialize() {
        monitor = new AeropuertoMonitor(airportArea);
        executorService = Executors.newCachedThreadPool();

        // Iniciar la generación de pasajeros automáticamente al cargar el controlador
        executorService.execute(new PasajeroGenerator(monitor));
    }

    public void stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // Asegurarse de cerrar el ExecutorService
        }
    }
}
