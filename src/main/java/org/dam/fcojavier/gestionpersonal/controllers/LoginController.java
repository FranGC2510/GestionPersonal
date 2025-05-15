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
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (!validarCampos(email, password)) {
            return;
        }

        try {
            procesarLogin(email, password);
        } catch (DAOException e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    private boolean validarCampos(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return false;
        }
        return true;
    }

    private void procesarLogin(String email, String password) throws DAOException {
        Empresa empresa = empresaDAO.findByEmail(email);
        
        if (empresa == null) {
            mostrarError("No se encuentra ninguna cuenta con este email");
            return;
        }

        if (!verificarPassword(password, empresa.getPassword())) {
            mostrarError("Contraseña incorrecta");
            return;
        }

        iniciarSesionEmpresa(empresa);
    }

    private void iniciarSesionEmpresa(Empresa empresa) {
        try {
            UsuarioSesion.getInstance().loginEmpresa(empresa);
            cargarVistaEmpresa(empresa);
        } catch (IOException ex) {
            manejarErrorCargaVista();
        }
    }

    private void cargarVistaEmpresa(Empresa empresa) throws IOException {
        FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("empresa-view.fxml"));
        Scene scene = new Scene(loader.load());

        EmpresaController controller = loader.getController();
        controller.setEmpresa(empresa);

        Stage dialogStage = (Stage) emailField.getScene().getWindow();
        Stage mainStage = (Stage) dialogStage.getOwner();

        configurarVentanaPrincipal(mainStage, scene, empresa.getNombre());
        dialogStage.close();
    }

    private void configurarVentanaPrincipal(Stage stage, Scene scene, String nombreEmpresa) {
        stage.setScene(scene);
        stage.setTitle("Panel de Empresa - " + nombreEmpresa);
        stage.setWidth(1500);
        stage.setHeight(875);
        stage.centerOnScreen();
    }

    private void manejarErrorCargaVista() {
        UsuarioSesion.getInstance().logout();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al cargar la vista de empresa");
        alert.setContentText("Ha ocurrido un error al intentar cargar la vista. Por favor, inténtelo de nuevo.");
        alert.showAndWait();
    }

    private boolean verificarPassword(String password, String hash) {
        return PasswordUtilidades.checkPassword(password, hash);
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}