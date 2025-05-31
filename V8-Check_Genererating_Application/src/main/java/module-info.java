module com.example.v8check_genererating_application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.v8check_genererating_application to javafx.fxml;
    exports com.example.v8check_genererating_application;
    exports com.example.v8check_genererating_application.interfaces;
    opens com.example.v8check_genererating_application.interfaces to javafx.fxml;
}