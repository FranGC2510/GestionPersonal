package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empresa;
import org.dam.fcojavier.gestionpersonal.utils.PasswordUtilidades;
import org.dam.fcojavier.gestionpersonal.utils.UsuarioSesion;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label mensajeError;

    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    @FXML
    protected void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }

        try {
            // Intentar primero como empresa
            Empresa empresa = empresaDAO.findByEmail(email);
            if (empresa != null) {
                if (verificarPassword(password, empresa.getPassword())) {
                    // Iniciar sesión en el UsuarioSesion
                    UsuarioSesion.getInstance().loginEmpresa(empresa);

                    // Cargar la vista de empresa
                    try {
                        FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("empresa-view.fxml"));
                        Scene scene = new Scene(loader.load());

                        // Obtener el controlador y establecer la empresa
                        EmpresaController controller = loader.getController();
                        controller.setEmpresa(empresa);

                        // Obtener el diálogo actual y la ventana principal
                        Stage dialogStage = (Stage) emailField.getScene().getWindow();
                        Stage mainStage = (Stage) dialogStage.getOwner();

                        // Establecer la nueva escena en la ventana principal
                        mainStage.setScene(scene);
                        mainStage.setTitle("Panel de Empresa - " + empresa.getNombre());

                        // Cerrar el diálogo de login
                        dialogStage.close();
                    } catch (IOException ex) {
                        // Si hay error al cargar la vista, cerrar la sesión
                        UsuarioSesion.getInstance().logout();

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error al cargar la vista de empresa");
                        alert.setContentText("Ha ocurrido un error al intentar cargar la vista. Por favor, inténtelo de nuevo.");
                        alert.showAndWait();
                    }
                    return;
                }
                mostrarError("Contraseña incorrecta");
                return;
            }

            // Si no se encuentra el email en ninguna tabla
            mostrarError("No se encuentra ninguna cuenta con este email");
        } catch (DAOException e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }


    private boolean verificarPassword(String password, String hash) {
        return PasswordUtilidades.checkPassword(password, hash);
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}
