package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

public class EditarEmpleadosController {
    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidoField;

    @FXML
    private TextField departamentoField;

    @FXML
    private TextField puestoField;

    @FXML
    private TextField telefonoField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<TipoEmpleado> rolComboBox;

    @FXML
    private CheckBox activoCheckBox;

    private Empleado empleado;
    private DialogPane dialogPane;
    private Empresa empresa;

    @FXML
    public void initialize() {
        // Configurar ComboBox de roles
        rolComboBox.getItems().setAll(TipoEmpleado.values());
        rolComboBox.valueProperty().addListener((_, _, _) -> validarCampos());

        // Validación en tiempo real
        nombreField.textProperty().addListener((_, _, newText) -> {
            validarCampo(nombreField, !newText.trim().isEmpty(), "El nombre es obligatorio");
        });

        apellidoField.textProperty().addListener((_, _, newText) -> {
            validarCampo(apellidoField, !newText.trim().isEmpty(), "El apellido es obligatorio");
        });

        emailField.textProperty().addListener((_, _, newText) -> {
            validarCampo(emailField, newText.matches("^[A-Za-z0-9+_.-]+@(.+)$"), "Email inválido");
        });

        telefonoField.textProperty().addListener((_, _, newText) -> {
            validarCampo(telefonoField, newText.matches("^\\d{9}$"), "Teléfono debe tener 9 dígitos");
        });

        // Por defecto, el empleado está activo
        activoCheckBox.setSelected(true);
    }

    public void setDialogPane(DialogPane dialogPane) {
        this.dialogPane = dialogPane;
        validarCampos();
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;

        if (empleado != null) {
            // Modo edición
            nombreField.setText(empleado.getNombre());
            apellidoField.setText(empleado.getApellido());
            departamentoField.setText(empleado.getDepartamento());
            puestoField.setText(empleado.getPuesto());
            telefonoField.setText(empleado.getTelefono());
            emailField.setText(empleado.getEmail());
            rolComboBox.setValue(empleado.getRol());
            activoCheckBox.setSelected(empleado.getActivo());
        }
    }

    public Empleado getEmpleado() {
        if (empleado == null) {
            empleado = new Empleado();
            empleado.setEmpresa(empresa);
        }

        empleado.setNombre(nombreField.getText());
        empleado.setApellido(apellidoField.getText());
        empleado.setDepartamento(departamentoField.getText());
        empleado.setPuesto(puestoField.getText());
        empleado.setTelefono(telefonoField.getText());
        empleado.setEmail(emailField.getText());
        empleado.setRol(rolComboBox.getValue());
        empleado.setActivo(activoCheckBox.isSelected());

        return empleado;
    }

    private void validarCampos() {
        if (dialogPane != null) {
            boolean nombreValido = !nombreField.getText().trim().isEmpty();
            boolean apellidoValido = !apellidoField.getText().trim().isEmpty();
            boolean emailValido = emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$");
            boolean telefonoValido = telefonoField.getText().matches("^\\d{9}$");
            boolean rolValido = rolComboBox.getValue() != null;

            boolean datosValidos = nombreValido &&
                    apellidoValido &&
                    emailValido &&
                    telefonoValido &&
                    rolValido;

            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setDisable(!datosValidos);
        }

    }

    private void validarCampo(TextField campo, boolean condicion, String mensajeError) {
        if (!condicion) {
            campo.setStyle("-fx-border-color: red;");
            campo.setTooltip(new Tooltip(mensajeError));
        } else {
            campo.setStyle("");
            campo.setTooltip(null);
        }
        validarCampos();
    }
}
