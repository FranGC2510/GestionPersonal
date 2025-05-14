package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;
    @FXML
    protected void handleLoginClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("login-dialog.fxml"));
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Iniciar Sesión");
            dialog.initStyle(StageStyle.UNIFIED);
            dialog.initOwner(loginButton.getScene().getWindow());
            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.setResizable(true);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Configurar botón OK para que no cierre el diálogo automáticamente
            Button dialogLoginButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            dialogLoginButton.setText("Iniciar Sesión");
            dialogLoginButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                LoginController controller = fxmlLoader.getController();
                controller.handleLogin();
                // Prevenir que el diálogo se cierre
                event.consume();
            });

            // Mostrar el diálogo
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleRegistroClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("registro-dialog.fxml"));
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Registro");
            dialog.initStyle(StageStyle.UNIFIED);
            dialog.initOwner(registerButton.getScene().getWindow());
            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Configurar botón OK para que no cierre el diálogo automáticamente
            Button dialogRegisterButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            dialogRegisterButton.setText("Registrarse");
            dialogRegisterButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                RegistroController controller = fxmlLoader.getController();
                controller.handleRegister();
                // Prevenir que el diálogo se cierre
                event.consume();
            });

            // Mostrar el diálogo
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
