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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controlador para el diálogo de inicio de sesión.
 * Gestiona la autenticación de empresas en el sistema y la carga
 * de la vista principal tras un inicio de sesión exitoso.
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    /** Campo de texto para el email */
    @FXML private TextField emailField;
    
    /** Campo de contraseña */
    @FXML private PasswordField passwordField;
    
    /** Etiqueta para mostrar mensajes de error */
    @FXML private Label mensajeError;

    /** DAO para acceder a los datos de empresas */
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    /**
     * Maneja el evento de inicio de sesión.
     * Valida los campos y procesa el intento de inicio de sesión.
     */
    @FXML
    protected void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        logger.info("Intento de inicio de sesión para el email: {}", email);

        if (!validarCampos(email, password)) {
            logger.warn("Intento de inicio de sesión fallido - campos inválidos");
            return;
        }

        try {
            procesarLogin(email, password);
        } catch (DAOException e) {
            logger.error("Error durante el proceso de inicio de sesión", e);
            mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    /**
     * Valida que los campos requeridos no estén vacíos.
     *
     * @param email El email introducido
     * @param password La contraseña introducida
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCampos(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return false;
        }
        return true;
    }

    /**
     * Procesa el intento de inicio de sesión verificando las credenciales.
     *
     * @param email El email del usuario
     * @param password La contraseña del usuario
     * @throws DAOException Si hay un error al acceder a la base de datos
     */
    private void procesarLogin(String email, String password) throws DAOException {
        Empresa empresa = empresaDAO.findByEmail(email);
        
        if (empresa == null) {
            logger.warn("Intento de inicio de sesión fallido - email no encontrado: {}", email);
            mostrarError("No se encuentra ninguna cuenta con este email");
            return;
        }

        if (!verificarPassword(password, empresa.getPassword())) {
            logger.warn("Intento de inicio de sesión fallido - contraseña incorrecta para el email: {}", email);
            mostrarError("Contraseña incorrecta");
            return;
        }

        logger.info("Inicio de sesión exitoso para la empresa: {}", empresa.getNombre());
        iniciarSesionEmpresa(empresa);
    }

    /**
     * Inicia la sesión de la empresa y carga su vista principal.
     *
     * @param empresa La empresa que inicia sesión
     */
    private void iniciarSesionEmpresa(Empresa empresa) {
        try {
            UsuarioSesion.getInstance().loginEmpresa(empresa);
            logger.info("Sesión iniciada correctamente para la empresa: {}", empresa.getNombre());
            cargarVistaEmpresa(empresa);
        } catch (IOException ex) {
            logger.error("Error al cargar la vista de empresa para: {}", empresa.getNombre(), ex);
            manejarErrorCargaVista();
        }
    }

    /**
     * Carga la vista principal de la empresa.
     *
     * @param empresa La empresa cuya vista se va a cargar
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
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

    /**
     * Configura la ventana principal de la aplicación.
     *
     * @param stage El Stage principal
     * @param scene La Scene a mostrar
     * @param nombreEmpresa El nombre de la empresa para el título
     */
    private void configurarVentanaPrincipal(Stage stage, Scene scene, String nombreEmpresa) {
        stage.setScene(scene);
        stage.setTitle("Panel de Empresa - " + nombreEmpresa);
        stage.setWidth(1500);
        stage.setHeight(875);
        stage.centerOnScreen();
    }

    /**
     * Maneja los errores que pueden ocurrir al cargar la vista principal.
     * Cierra la sesión y muestra un mensaje de error.
     */
    private void manejarErrorCargaVista() {
        logger.error("Error fatal al cargar la vista principal - cerrando sesión");
        UsuarioSesion.getInstance().logout();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al cargar la vista de empresa");
        alert.setContentText("Ha ocurrido un error al intentar cargar la vista. Por favor, inténtelo de nuevo.");
        alert.showAndWait();
    }

    /**
     * Verifica si la contraseña proporcionada coincide con el hash almacenado.
     *
     * @param password La contraseña a verificar
     * @param hash El hash almacenado
     * @return true si la contraseña es correcta
     */
    private boolean verificarPassword(String password, String hash) {
        return PasswordUtilidades.checkPassword(password, hash);
    }

    /**
     * Muestra un mensaje de error en la interfaz.
     *
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}