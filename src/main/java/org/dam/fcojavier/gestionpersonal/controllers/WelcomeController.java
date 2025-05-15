package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @FXML
    private void handleLoginClick() {
        try {
            mostrarDialogoLogin();
        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar la ventana de inicio de sesión.");
        }
    }

    @FXML
    private void handleRegistroClick() {
        try {
            mostrarDialogoRegistro();
        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar la ventana de registro.");
        }
    }

    private void mostrarDialogoLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("login-dialog.fxml"));
        Dialog<ButtonType> dialog = crearDialogoBase("Iniciar Sesión", loginButton);
        dialog.getDialogPane().setContent(fxmlLoader.load());
        
        configurarBotonLogin(dialog, fxmlLoader.getController());
        
        dialog.showAndWait();
    }

    private void mostrarDialogoRegistro() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("registro-dialog.fxml"));
        Dialog<ButtonType> dialog = crearDialogoBase("Registro", registerButton);
        dialog.getDialogPane().setContent(fxmlLoader.load());
        
        configurarBotonRegistro(dialog, fxmlLoader.getController());
        
        dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                RegistroController controller = fxmlLoader.getController();
                if (!controller.isRegistroCompletado()) {
                    dialog.close();
                }
            });
    }

    private Dialog<ButtonType> crearDialogoBase(String titulo, Button owner) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.initStyle(StageStyle.UNIFIED);
        dialog.initOwner(owner.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(true);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        return dialog;
    }

    private void configurarBotonLogin(Dialog<ButtonType> dialog, LoginController controller) {
        Button loginButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setText("Iniciar Sesión");
        
        loginButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            controller.handleLogin();
            event.consume();
        });
    }

    private void configurarBotonRegistro(Dialog<ButtonType> dialog, RegistroController controller) {
        Button registroButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        registroButton.setText("Registrarse");
        
        registroButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            controller.handleRegister();
            event.consume();
        });
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}