package com.example.v8check_genererating_application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
       try{
           FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("base.fxml"));
           Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
           // /home/kendricks/IdeaProjects/CheckGeneratingApplicationV6/src/main/resources/com/camillebusinesses/checks/checkgeneratingapplicationv6/base.fxml
           stage.setTitle("Hello!");
           stage.setScene(scene);
           stage.show();
       }
       catch (IOException e){
           Throwable cause = e.getCause();
           while (cause != null) {
               System.out.println(cause.getMessage());
               cause = cause.getCause();
           }
       }
    }

    public static void main(String[] args) {
        launch();
    }
}