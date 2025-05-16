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

/**
 * Controlador para el diálogo de registro de empresas.
 * Gestiona el proceso de registro de nuevas empresas, incluyendo la validación
 * de campos, creación de la cuenta y el inicio de sesión automático tras el registro.
 */
public class RegistroController {
    /** Campo de texto para el email */
    @FXML private TextField emailField;
    
    /** Campo para la contraseña */
    @FXML private PasswordField passwordField;
    
    /** Campo para confirmar la contraseña */
    @FXML private PasswordField confirmaPasswordField;
    
    /** Campo para el nombre de la empresa */
    @FXML private TextField nombreEmpresaField;
    
    /** Campo para la dirección de la empresa */
    @FXML private TextField direccionEmpresaField;
    
    /** Campo para el teléfono de la empresa */
    @FXML private TextField telefonoEmpresaField;
    
    /** Etiqueta para mostrar mensajes de error */
    @FXML private Label mensajeError;

    /** DAO para acceder a los datos de empresas */
    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    
    /** Ancho predeterminado de la ventana principal */
    private static final int VENTANA_ANCHO = 1500;
    
    /** Alto predeterminado de la ventana principal */
    private static final int VENTANA_ALTO = 875;
    
    /** Indica si el registro se completó exitosamente */
    private boolean registroCompletado = false;

    /**
     * Maneja el evento de registro.
     * Valida los campos, procesa el registro y muestra la vista principal si es exitoso.
     */
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

    /**
     * Valida todos los campos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmaPassword = confirmaPasswordField.getText();
        String nombre = nombreEmpresaField.getText().trim();
        String direccion = direccionEmpresaField.getText().trim();
        String telefono = telefonoEmpresaField.getText().trim();

        if (!validarCamposRequeridos(nombre, email, password, direccion, telefono) ||
            !validarFormatoEmail(email) ||
            !validarPassword(password, confirmaPassword) ||
            !validarTelefono(telefono)) {
            return false;
        }

        return true;
    }

    private boolean validarCamposRequeridos(String nombre, String email, String password, String direccion, String telefono) {
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() ||
                direccion.isEmpty() || telefono.isEmpty()) {
            mostrarError("Por favor, complete todos los campos requeridos");
            return false;
        }
        return true;
    }

    private boolean validarFormatoEmail(String email) {
        if (!Validacion.isValidoEmail(email)) {
            mostrarError(Validacion.validateEmail(email));
            return false;
        }
        return true;
    }

    private boolean validarPassword(String password, String confirmaPassword) {
        if (!Validacion.isValidaPassword(password)) {
            mostrarError(Validacion.validaPassword(password));
            return false;
        }

        if (!password.equals(confirmaPassword)) {
            mostrarError("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    private boolean validarTelefono(String telefono) {
        if (!Validacion.isValidoTelefono(telefono)) {
            mostrarError(Validacion.validateTelefono(telefono));
            return false;
        }
        return true;
    }

    /**
     * Procesa el registro de la nueva empresa.
     *
     * @return true si el registro fue exitoso
     * @throws DAOException Si hay un error al acceder a la base de datos
     */
    private boolean procesarRegistro() throws DAOException {
        if (emailExiste(emailField.getText())) {
            mostrarError("Ya existe una cuenta con este email");
            return false;
        }

        Empresa empresa = crearEmpresa();
        return empresaDAO.insert(empresa) != null;
    }

    /**
     * Inicia sesión con la empresa registrada y muestra su panel principal.
     *
     * @param empresa La empresa registrada
     */
    private void iniciarSesionYMostrarPanel(Empresa empresa) {
        try {
            UsuarioSesion.getInstance().loginEmpresa(empresa);
            cargarVistaPrincipal(empresa);
        } catch (IOException e) {
            manejarErrorCargaVista();
        }
    }

    /**
     * Crea una nueva instancia de Empresa con los datos del formulario.
     *
     * @return La nueva instancia de Empresa
     */
    private Empresa crearEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setEmail(emailField.getText().trim());
        empresa.setPassword(PasswordUtilidades.hashPassword(passwordField.getText()));
        empresa.setNombre(nombreEmpresaField.getText().trim());
        empresa.setDireccion(direccionEmpresaField.getText().trim());
        empresa.setTelefono(telefonoEmpresaField.getText().trim());
        return empresa;
    }

    /**
     * Verifica si ya existe una empresa con el email proporcionado.
     *
     * @param email El email a verificar
     * @return true si el email ya existe
     * @throws DAOException Si hay un error al acceder a la base de datos
     */
    private boolean emailExiste(String email) throws DAOException {
        return empresaDAO.findByEmail(email) != null;
    }

    /**
     * Carga la vista principal de la empresa.
     *
     * @param empresa La empresa registrada
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
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
        stage.setWidth(VENTANA_ANCHO);
        stage.setHeight(VENTANA_ALTO);
        stage.centerOnScreen();
    }

    /**
     * Verifica si el registro se completó exitosamente.
     *
     * @return true si el registro se completó correctamente
     */
    public boolean isRegistroCompletado() {
        return registroCompletado;
    }

    /**
     * Maneja los errores que pueden ocurrir al cargar la vista principal.
     * Cierra la sesión y muestra un mensaje de error.
     */
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

    /**
     * Oculta el mensaje de error en la interfaz.
     */
    private void ocultarError() {
        mensajeError.setVisible(false);
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