package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.model.Empresa;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.utils.PasswordUtilidades;
import org.dam.fcojavier.gestionpersonal.utils.Validacion;

public class EditarEmpresaController {
    @FXML
    private TextField nombreField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField direccionField;

    @FXML
    private TextField telefonoField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmarPasswordField;

    @FXML
    private Text mensajeError;

    private Empresa empresa;
    private boolean edicion = false;

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;

        // Rellenar los campos con los datos actuales
        nombreField.setText(empresa.getNombre());
        emailField.setText(empresa.getEmail());
        direccionField.setText(empresa.getDireccion());
        telefonoField.setText(empresa.getTelefono());
        passwordField.clear();
        confirmarPasswordField.clear();
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }

    public boolean edicionCorrecta() {
        return edicion;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    @FXML
    private void handleGuardar() {
        // Ocultar mensaje de error anterior
        mensajeError.setVisible(false);

        // Validar campos
        if (!validarCampos()) {
            return;
        }

        try {
            // Actualizar datos de la empresa
            empresa.setNombre(nombreField.getText());
            empresa.setDireccion(direccionField.getText());
            empresa.setTelefono(telefonoField.getText());

            // Solo actualizar contraseña si se ha ingresado una nueva
            if (!passwordField.getText().isEmpty()) {
                String hashedPassword = PasswordUtilidades.hashPassword(passwordField.getText());
                empresa.setPassword(hashedPassword);
            }

            // Guardar cambios en la base de datos
            EmpresaDAO empresaDAO = new EmpresaDAO();
            empresaDAO.update(empresa);

            // Marcar que los cambios se guardaron exitosamente
            edicion = true;

            // Cerrar el diálogo
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.close();

        } catch (DAOException e) {
            mostrarError("Error al guardar los cambios: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        boolean valido = true;
        // Validar campos obligatorios
        if (nombreField.getText().isEmpty() || direccionField.getText().isEmpty() || telefonoField.getText().isEmpty()) {
            mostrarError("Por favor, complete todos los campos obligatorios");
            valido= false;
        }

        // Validar formato del teléfono
        String errorTelefono = Validacion.validateTelefono(telefonoField.getText());
        if (errorTelefono != null) {
            mostrarError(errorTelefono);
            valido= false;
        }

        // Si se ingresó una contraseña, validar su formato y que coincidan
        if (!passwordField.getText().isEmpty()) {
            String errorPassword = Validacion.validaPassword(passwordField.getText());
            if (errorPassword != null) {
                mostrarError(errorPassword);
                valido= false;
            }

            if (!passwordField.getText().equals(confirmarPasswordField.getText())) {
                mostrarError("Las contraseñas no coinciden");
                valido= false;
            }
        }

        return valido;
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}
