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
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmaPasswordField;

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
            Empresa empresaRegistrada = empresaDAO.findByEmail(emailField.getText());
            // Iniciar sesión en el SessionManager
            UsuarioSesion.getInstance().loginEmpresa(empresaRegistrada);

            try {
                // Cargar la vista de empresa
                FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("empresa-view.fxml"));
                Scene scene = new Scene(loader.load());

                // Obtener el controlador y establecer la empresa
                EmpresaController controller = loader.getController();
                controller.setEmpresa(empresaRegistrada);

                // Obtener la ventana principal (welcome-view) y cambiar la escena
                Stage dialogStage = (Stage) emailField.getScene().getWindow();
                Stage mainStage = (Stage) dialogStage.getOwner();
                mainStage.setScene(scene);
                mainStage.setTitle("Panel de Empresa - " + empresaRegistrada.getNombre());

                // Cerrar el diálogo de registro
                dialogStage.close();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al cargar la vista de empresa");
                alert.setContentText("Ha ocurrido un error al intentar cargar la vista. Por favor, inicie sesión manualmente.");
                alert.showAndWait();
                // Cerrar la sesión si hubo error al cargar la vista
                UsuarioSesion.getInstance().logout();
                // Cerrar solo el diálogo
                Stage dialogStage = (Stage) emailField.getScene().getWindow();
                dialogStage.close();
            }

        } catch (DAOException e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    private boolean validarCamposComunes() {
        String nombre = nombreEmpresaField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmaPassword = confirmaPasswordField.getText();
        String direccion = direccionEmpresaField.getText();
        String telefono = telefonoEmpresaField.getText();
        boolean flag= true;

        if (nombre.isEmpty() || email.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            mostrarError("Por favor, complete todos los campos requeridos");
            flag= false;
        }

        // Validar email
        if (email.isEmpty()) {
            mostrarError("Por favor, ingrese un email");
            flag= false;
        }else if (!Validacion.isValidoEmail(email)) {
            mostrarError(Validacion.validateEmail(email));
            flag= false;
        }

        // Validar contraseña
        if (password.isEmpty()) {
            mostrarError("Por favor, ingrese una contraseña");
            flag= false;
        }else if(!Validacion.isValidaPassword(password)){
            mostrarError(Validacion.validaPassword(password));
            flag= false;
        }

        // Validar confirmación de contraseña
        if (!password.equals(confirmaPassword)) {
            mostrarError("Las contraseñas no coinciden");
            flag= false;
        }

        if (direccion.isEmpty()) {
            mostrarError("Por favor, ingrese la dirección de la empresa");
            flag= false;
        }
        if (telefono.isEmpty()) {
            mostrarError("Por favor, ingrese el teléfono de la empresa");
            flag= false;
        }else if(!Validacion.isValidoTelefono(telefono)){
            mostrarError(Validacion.validateTelefono(telefono));
            flag= false;
        }

        return flag;
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
        String hashedPassword = PasswordUtilidades.hashPassword(passwordField.getText());
        empresa.setPassword(hashedPassword);
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
