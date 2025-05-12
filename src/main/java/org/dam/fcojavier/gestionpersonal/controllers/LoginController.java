package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label mensajeError;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
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
                    // TODO: Navegar al dashboard de empresa
                    System.out.println("Login empresa exitoso");
                    // Cerrar el diálogo
                    emailField.getScene().getWindow().hide();
                    return;
                }
                mostrarError("Contraseña incorrecta");
                return;
            }

            // Si no es empresa, intentar como empleado
            Empleado empleado = empleadoDAO.findByEmail(email);
            if (empleado != null) {
                if (verificarPassword(password, empleado.getPasswordHash())) {
                    // TODO: Navegar al dashboard de empleado
                    System.out.println("Login empleado exitoso");
                    // Cerrar el diálogo
                    emailField.getScene().getWindow().hide();
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
        // TODO: Implementar verificación de hash
        return password.equals(hash); // Temporal, NO USAR EN PRODUCCIÓN
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setVisible(true);
    }
}
