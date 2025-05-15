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
import org.dam.fcojavier.gestionpersonal.utils.Validacion;

import java.io.IOException;

public class RegistroController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmaPasswordField;
    @FXML private TextField nombreEmpresaField;
    @FXML private TextField direccionEmpresaField;
    @FXML private TextField telefonoEmpresaField;
    @FXML private Label mensajeError;

    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private static final int VENTANA_ANCHO = 1500;
    private static final int VENTANA_ALTO = 875;
    private boolean registroCompletado = false;

    @FXML
    protected void handleRegister() {
        ocultarError();
        
        if (!validarCampos()) {
            return;
        }

        try {
            if (procesarRegistro()) {
                registroCompletado = true;
                Empresa empresaRegistrada = empresaDAO.findByEmail(emailField.getText());
                iniciarSesionYMostrarPanel(empresaRegistrada);
            }
        } catch (DAOException e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmaPassword = confirmaPasswordField.getText();
        String nombre = nombreEmpresaField.getText().trim();
        String direccion = direccionEmpresaField.getText().trim();
        String telefono = telefonoEmpresaField.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || 
            direccion.isEmpty() || telefono.isEmpty()) {
            mostrarError("Por favor, complete todos los campos requeridos");
            return false;
        }

        if (!Validacion.isValidoEmail(email)) {
            mostrarError(Validacion.validateEmail(email));
            return false;
        }

        if (!Validacion.isValidaPassword(password)) {
            mostrarError(Validacion.validaPassword(password));
            return false;
        }

        if (!password.equals(confirmaPassword)) {
            mostrarError("Las contraseñas no coinciden");
            return false;
        }

        if (!Validacion.isValidoTelefono(telefono)) {
            mostrarError(Validacion.validateTelefono(telefono));
            return false;
        }

        return true;
    }

    private boolean procesarRegistro() throws DAOException {
        if (emailExiste(emailField.getText())) {
            mostrarError("Ya existe una cuenta con este email");
            return false;
        }

        Empresa empresa = crearEmpresa();
        return empresaDAO.insert(empresa) != null;
    }

    private void iniciarSesionYMostrarPanel(Empresa empresa) {
        try {
            UsuarioSesion.getInstance().loginEmpresa(empresa);
            cargarVistaPrincipal(empresa);
        } catch (IOException e) {
            manejarErrorCargaVista();
        }
    }

    private Empresa crearEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setEmail(emailField.getText().trim());
        empresa.setPassword(PasswordUtilidades.hashPassword(passwordField.getText()));
        empresa.setNombre(nombreEmpresaField.getText().trim());
        empresa.setDireccion(direccionEmpresaField.getText().trim());
        empresa.setTelefono(telefonoEmpresaField.getText().trim());
        return empresa;
    }

    private boolean emailExiste(String email) throws DAOException {
        return empresaDAO.findByEmail(email) != null;
    }

    private void cargarVistaPrincipal(Empresa empresa) throws IOException {
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
        stage.setWidth(VENTANA_ANCHO);
        stage.setHeight(VENTANA_ALTO);
        stage.centerOnScreen();
    }

    public boolean isRegistroCompletado() {
        return registroCompletado;
    }

    private void manejarErrorCargaVista() {
        UsuarioSesion.getInstance().logout();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al cargar la vista de empresa");
        alert.setContentText("Ha ocurrido un error al intentar cargar la vista. Por favor, inicie sesión manualmente.");
        alert.showAndWait();
        
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    private void ocultarError() {
        mensajeError.setVisible(false);
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}