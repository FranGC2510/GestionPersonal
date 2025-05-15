package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.utils.PasswordUtilidades;
import org.dam.fcojavier.gestionpersonal.utils.Validacion;

/**
 * Controlador para el diálogo de edición de empresas.
 * Gestiona la interfaz de usuario para editar los datos de una empresa,
 * incluyendo validaciones de campos y actualización de contraseña.
 */
public class EditarEmpresaController {
    /** Campo para el nombre de la empresa */
    @FXML private TextField nombreField;

    /** Campo para el email de la empresa */
    @FXML private TextField emailField;

    /** Campo para la dirección de la empresa */
    @FXML private TextField direccionField;

    /** Campo para el teléfono de la empresa */
    @FXML private TextField telefonoField;

    /** Campo para la nueva contraseña */
    @FXML private PasswordField passwordField;

    /** Campo para confirmar la nueva contraseña */
    @FXML private PasswordField confirmarPasswordField;

    /** Texto para mostrar mensajes de error */
    @FXML private Text mensajeError;

    /** Empresa que se está editando */
    private Empresa empresa;

    /** Indica si la edición se realizó correctamente */
    private boolean edicion = false;

    /**
     * Establece la empresa a editar y carga sus datos en el formulario.
     *
     * @param empresa La empresa a editar
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        cargarDatosEmpresa();
    }

    /**
     * Carga los datos de la empresa en los campos del formulario.
     */
    private void cargarDatosEmpresa() {
        nombreField.setText(empresa.getNombre());
        emailField.setText(empresa.getEmail());
        direccionField.setText(empresa.getDireccion());
        telefonoField.setText(empresa.getTelefono());
        passwordField.clear();
        confirmarPasswordField.clear();
    }

    /**
     * Maneja el evento de cancelar la edición.
     * Cierra la ventana sin guardar cambios.
     */
    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }

    /**
     * Verifica si la edición se realizó correctamente.
     *
     * @return true si la edición fue exitosa, false en caso contrario
     */
    public boolean edicionCorrecta() {
        return edicion;
    }

    /**
     * Obtiene la empresa con los datos actualizados.
     *
     * @return La empresa con los datos actualizados
     */
    public Empresa getEmpresa() {
        return empresa;
    }

    /**
     * Maneja el evento de guardar los cambios.
     * Valida los campos, actualiza los datos y guarda en la base de datos.
     */
    @FXML
    private void handleGuardar() {
        mensajeError.setVisible(false);

        if (!validarCampos()) {
            return;
        }

        try {
            actualizarDatosEmpresa();
            guardarEnBaseDeDatos();
            cerrarVentana();
        } catch (DAOException e) {
            mostrarError("Error al guardar los cambios: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de la empresa con los valores del formulario.
     */
    private void actualizarDatosEmpresa() {
        empresa.setNombre(nombreField.getText());
        empresa.setDireccion(direccionField.getText());
        empresa.setTelefono(telefonoField.getText());

        if (!passwordField.getText().isEmpty()) {
            String hashedPassword = PasswordUtilidades.hashPassword(passwordField.getText());
            empresa.setPassword(hashedPassword);
        }
    }

    /**
     * Guarda los cambios de la empresa en la base de datos.
     *
     * @throws DAOException Si ocurre un error al guardar los datos
     */
    private void guardarEnBaseDeDatos() throws DAOException {
        EmpresaDAO empresaDAO = new EmpresaDAO();
        empresaDAO.update(empresa);
        edicion = true;
    }

    /**
     * Cierra la ventana del diálogo.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida todos los campos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        if (!validarCamposObligatorios()) {
            return false;
        }

        if (!validarTelefono()) {
            return false;
        }

        return validarPassword();
    }

    /**
     * Valida que los campos obligatorios no estén vacíos.
     *
     * @return true si todos los campos obligatorios están completos
     */
    private boolean validarCamposObligatorios() {
        if (nombreField.getText().isEmpty() || 
            direccionField.getText().isEmpty() || 
            telefonoField.getText().isEmpty()) {
            mostrarError("Por favor, complete todos los campos obligatorios");
            return false;
        }
        return true;
    }

    /**
     * Valida el formato del teléfono.
     *
     * @return true si el teléfono tiene un formato válido
     */
    private boolean validarTelefono() {
        String errorTelefono = Validacion.validateTelefono(telefonoField.getText());
        if (errorTelefono != null) {
            mostrarError(errorTelefono);
            return false;
        }
        return true;
    }

    /**
     * Valida la contraseña si se ha ingresado una nueva.
     *
     * @return true si la contraseña es válida o no se ha ingresado ninguna
     */
    private boolean validarPassword() {
        if (!passwordField.getText().isEmpty()) {
            String errorPassword = Validacion.validaPassword(passwordField.getText());
            if (errorPassword != null) {
                mostrarError(errorPassword);
                return false;
            }

            if (!passwordField.getText().equals(confirmarPasswordField.getText())) {
                mostrarError("Las contraseñas no coinciden");
                return false;
            }
        }
        return true;
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