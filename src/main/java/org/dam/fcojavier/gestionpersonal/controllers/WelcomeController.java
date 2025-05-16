package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controlador para la vista de bienvenida.
 * Gestiona la pantalla inicial de la aplicación, proporcionando acceso
 * a las funcionalidades de inicio de sesión y registro de usuarios.
 */
public class WelcomeController {
    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);
    /** Botón para iniciar sesión */
    @FXML private Button loginButton;
    
    /** Botón para registrarse */
    @FXML private Button registerButton;

    /**
     * Maneja el evento de clic en el botón de inicio de sesión.
     * Muestra el diálogo de inicio de sesión.
     */
    @FXML
    private void handleLoginClick() {
        logger.info("Iniciando proceso de inicio de sesión");
        try {
            mostrarDialogoLogin();
        } catch (IOException e) {
            logger.error("Error al cargar la ventana de inicio de sesión", e);
            mostrarError("Error", "No se pudo cargar la ventana de inicio de sesión.");
        }
    }

    /**
     * Maneja el evento de clic en el botón de registro.
     * Muestra el diálogo de registro.
     */
    @FXML
    private void handleRegistroClick() {
        logger.info("Iniciando proceso de registro");
        try {
            mostrarDialogoRegistro();
        } catch (IOException e) {
            logger.error("Error al cargar la ventana de registro", e);
            mostrarError("Error", "No se pudo cargar la ventana de registro.");
        }
    }

    /**
     * Muestra el diálogo de inicio de sesión.
     *
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
    private void mostrarDialogoLogin() throws IOException {
        logger.debug("Cargando diálogo de inicio de sesión");
        FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("login-dialog.fxml"));
        Dialog<ButtonType> dialog = crearDialogoBase("Iniciar Sesión", loginButton);
        dialog.getDialogPane().setContent(fxmlLoader.load());
        
        configurarBotonLogin(dialog, fxmlLoader.getController());

        logger.debug("Mostrando diálogo de inicio de sesión");
        dialog.showAndWait();
    }

    /**
     * Muestra el diálogo de registro.
     * Si el registro no se completa correctamente, cierra el diálogo.
     *
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
    private void mostrarDialogoRegistro() throws IOException {
        logger.debug("Cargando diálogo de registro");
        FXMLLoader fxmlLoader = new FXMLLoader(GestionPersonalApp.class.getResource("registro-dialog.fxml"));
        Dialog<ButtonType> dialog = crearDialogoBase("Registro", registerButton);
        dialog.getDialogPane().setContent(fxmlLoader.load());
        
        configurarBotonRegistro(dialog, fxmlLoader.getController());

        logger.debug("Mostrando diálogo de registro");
        dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                RegistroController controller = fxmlLoader.getController();
                if (!controller.isRegistroCompletado()) {
                    logger.info("Registro no completado, cerrando diálogo");
                    dialog.close();
                }
            });
    }

    /**
     * Crea un diálogo base con configuración común para login y registro.
     *
     * @param titulo El título del diálogo
     * @param owner El botón que sirve como propietario del diálogo
     * @return El diálogo configurado
     */
    private Dialog<ButtonType> crearDialogoBase(String titulo, Button owner) {
        logger.debug("Creando diálogo base con título: {}", titulo);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.initStyle(StageStyle.UNIFIED);
        dialog.initOwner(owner.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(true);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        return dialog;
    }

    /**
     * Configura el botón de inicio de sesión en el diálogo.
     * Establece el texto y el manejador de eventos.
     *
     * @param dialog El diálogo que contiene el botón
     * @param controller El controlador de login asociado
     */
    private void configurarBotonLogin(Dialog<ButtonType> dialog, LoginController controller) {
        logger.debug("Configurando botón de inicio de sesión");
        Button loginButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setText("Iniciar Sesión");
        
        loginButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            controller.handleLogin();
            event.consume();
        });
    }

    /**
     * Configura el botón de registro en el diálogo.
     * Establece el texto y el manejador de eventos.
     *
     * @param dialog El diálogo que contiene el botón
     * @param controller El controlador de registro asociado
     */
    private void configurarBotonRegistro(Dialog<ButtonType> dialog, RegistroController controller) {
        logger.debug("Configurando botón de registro");
        Button registroButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        registroButton.setText("Registrarse");
        
        registroButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            controller.handleRegister();
            event.consume();
        });
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param titulo El título del error
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String titulo, String mensaje) {
        logger.error("Error en la interfaz: {} - {}", titulo, mensaje);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}