module com.example.simuladoraeropuerto {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.almasb.fxgl.all;

    // Asegúrate de abrir el paquete específico que contiene tus clases de controladores de FXML
    opens com.example.simuladoraeropuerto.app to javafx.fxml;

    // Exporta el paquete que contiene tu clase de aplicación
    exports com.example.simuladoraeropuerto.app;
}
