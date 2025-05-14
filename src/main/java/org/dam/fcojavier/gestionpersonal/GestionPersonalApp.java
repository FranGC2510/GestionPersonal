package org.dam.fcojavier.gestionpersonal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionPersonalApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setTitle("Gestión de Personal");
        stage.setScene(scene);
        stage.setWidth(1500);
        stage.setHeight(875);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}