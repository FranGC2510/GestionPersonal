package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;
import org.dam.fcojavier.gestionpersonal.utils.Validacion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class EditarEmpleadosController {
    // Campos FXML
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField departamentoField;
    @FXML private TextField puestoField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private ComboBox<TipoEmpleado> rolComboBox;
    @FXML private CheckBox activoCheckBox;

    // Variables de clase
    private Empleado empleado;
    private DialogPane dialogPane;
    private Empresa empresa;
    private Map<TextField, ValidacionCampo> validaciones;

    // Clase interna para manejar validaciones
    private record ValidacionCampo(
            Predicate<String> validador,
            String mensajeError
    ) {}

    @FXML
    public void initialize() {
        configurarValidaciones();
        configurarListeners();
        inicializarComboBoxRoles();
        inicializarCheckBoxActivo();
    }

    private void configurarValidaciones() {
        validaciones = new HashMap<>();
        validaciones.put(nombreField, new ValidacionCampo(
                texto -> !texto.trim().isEmpty(),
                "El nombre es obligatorio"
        ));
        validaciones.put(apellidoField, new ValidacionCampo(
                texto -> !texto.trim().isEmpty(),
                "El apellido es obligatorio"
        ));
        validaciones.put(emailField, new ValidacionCampo(
                Validacion::isValidoEmail,
                Validacion.ERROR_EMAIL
        ));
        validaciones.put(telefonoField, new ValidacionCampo(
                Validacion::isValidoTelefono,
                Validacion.ERROR_TELEFONO
        ));
    }

    private void configurarListeners() {
        validaciones.forEach((campo, validacion) -> 
            campo.textProperty().addListener((_, _, newValue) -> 
                validarCampo(campo, validacion.validador().test(newValue), validacion.mensajeError())
            )
        );
    }

    private void inicializarComboBoxRoles() {
        rolComboBox.getItems().setAll(TipoEmpleado.values());
        rolComboBox.valueProperty().addListener((_, _, _) -> validarCampos());
        rolComboBox.setValue(TipoEmpleado.EMPLEADO); // Valor por defecto
    }

    private void inicializarCheckBoxActivo() {
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
            cargarDatosEmpleado();
        }
    }

    private void cargarDatosEmpleado() {
        nombreField.setText(empleado.getNombre());
        apellidoField.setText(empleado.getApellido());
        departamentoField.setText(empleado.getDepartamento());
        puestoField.setText(empleado.getPuesto());
        telefonoField.setText(empleado.getTelefono());
        emailField.setText(empleado.getEmail());
        rolComboBox.setValue(empleado.getRol());
        activoCheckBox.setSelected(empleado.getActivo());
    }

    public Empleado getEmpleado() {
        if (!validarTodosLosCampos()) {
            return null;
        }

        if (empleado == null) {
            empleado = new Empleado();
            empleado.setEmpresa(empresa);
        }

        actualizarDatosEmpleado();
        return empleado;
    }

    private void actualizarDatosEmpleado() {
        empleado.setNombre(obtenerTextoLimpio(nombreField));
        empleado.setApellido(obtenerTextoLimpio(apellidoField));
        empleado.setDepartamento(obtenerTextoLimpio(departamentoField));
        empleado.setPuesto(obtenerTextoLimpio(puestoField));
        empleado.setTelefono(obtenerTextoLimpio(telefonoField));
        empleado.setEmail(obtenerTextoLimpio(emailField));
        empleado.setRol(rolComboBox.getValue());
        empleado.setActivo(activoCheckBox.isSelected());
    }

    private String obtenerTextoLimpio(TextField campo) {
        return campo.getText().trim();
    }

    private boolean validarTodosLosCampos() {
        return validaciones.entrySet().stream()
                .allMatch(entry -> entry.getValue().validador().test(entry.getKey().getText())) &&
               rolComboBox.getValue() != null;
    }

    private void validarCampos() {
        if (dialogPane != null) {
            boolean datosValidos = validarTodosLosCampos();
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setDisable(!datosValidos);
        }
    }

    private void validarCampo(TextField campo, boolean condicion, String mensajeError) {
        aplicarEstiloCampo(campo, condicion, mensajeError);
        validarCampos();
    }

    private void aplicarEstiloCampo(TextField campo, boolean esValido, String mensajeError) {
        if (!esValido) {
            campo.setStyle("-fx-border-color: red;");
            campo.setTooltip(new Tooltip(mensajeError));
        } else {
            campo.setStyle("");
            campo.setTooltip(null);
        }
    }
}