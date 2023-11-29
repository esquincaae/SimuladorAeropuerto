module com.example.simuladoraeropuerto {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.simuladoraeropuerto to javafx.fxml;
    exports com.example.simuladoraeropuerto;
    exports com.example.simuladoraeropuerto.controllers;
    opens com.example.simuladoraeropuerto.controllers to javafx.fxml;
}