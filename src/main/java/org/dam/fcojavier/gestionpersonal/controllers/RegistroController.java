package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

public class RegistroController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField nombreEmpresaField;

    @FXML
    private TextField direccionEmpresaField;

    @FXML
    private TextField telefonoEmpresaField;

    @FXML
    private Label mensajeError;

    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    @FXML
    protected void handleRegister() {
        // Limpiar mensaje de error anterior
        mensajeError.setVisible(false);

        // Validar campos comunes
        if (!validarCamposComunes()) {
            return;
        }

        try {
            // Verificar que el email no existe
            if (emailExiste(emailField.getText())) {
                mostrarError("Ya existe una cuenta con este email");
                return;
            }

            // Registrar empresa
            registrarEmpresa();

            // Si llegamos aquí, el registro fue exitoso
            // Cerrar el diálogo
            emailField.getScene().getWindow().hide();

        } catch (DAOException e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    private boolean validarCamposComunes() {
        // Validar email
        if (emailField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese un email");
            return false;
        }

        // Validar contraseña
        if (passwordField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese una contraseña");
            return false;
        }

        // Validar confirmación de contraseña
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            mostrarError("Las contraseñas no coinciden");
            return false;
        }

        // Validar campos de empresa
        if (nombreEmpresaField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese el nombre de la empresa");
            return false;
        }
        if (direccionEmpresaField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese la dirección de la empresa");
            return false;
        }
        if (telefonoEmpresaField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese el teléfono de la empresa");
            return false;
        }

        return true;
    }

    private boolean emailExiste(String email) throws DAOException {
        return empresaDAO.findByEmail(email) != null;
    }

    private void registrarEmpresa() throws DAOException {
        // Validar campos de empresa
        if (nombreEmpresaField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese el nombre de la empresa");
            return;
        }
        if (direccionEmpresaField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese la dirección de la empresa");
            return;
        }
        if (telefonoEmpresaField.getText().isEmpty()) {
            mostrarError("Por favor, ingrese el teléfono de la empresa");
            return;
        }

        // Crear y guardar empresa
        Empresa empresa = new Empresa();
        empresa.setEmail(emailField.getText());
        empresa.setPassword(passwordField.getText()); // TODO: Implementar hash
        empresa.setNombre(nombreEmpresaField.getText());
        empresa.setDireccion(direccionEmpresaField.getText());
        empresa.setTelefono(telefonoEmpresaField.getText());

        if (empresaDAO.insert(empresa) == null) {
            throw new DAOException("No se pudo registrar la empresa", null);
        }
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}
