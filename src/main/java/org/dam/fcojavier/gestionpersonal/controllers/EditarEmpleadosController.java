package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.model.*;
import org.dam.fcojavier.gestionpersonal.utils.Validacion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Controlador para el diálogo de edición de empleados.
 * Gestiona la interfaz de usuario para crear y editar empleados,
 * incluyendo validaciones en tiempo real de los campos.
 */
public class EditarEmpleadosController {
    /** Campo para el nombre del empleado */
    @FXML private TextField nombreField;
    
    /** Campo para los apellidos del empleado */
    @FXML private TextField apellidoField;
    
    /** Campo para el departamento del empleado */
    @FXML private TextField departamentoField;
    
    /** Campo para el puesto del empleado */
    @FXML private TextField puestoField;
    
    /** Campo para el teléfono del empleado */
    @FXML private TextField telefonoField;
    
    /** Campo para el email del empleado */
    @FXML private TextField emailField;
    
    /** Selector del rol del empleado */
    @FXML private ComboBox<TipoEmpleado> rolComboBox;
    
    /** Casilla para indicar si el empleado está activo */
    @FXML private CheckBox activoCheckBox;

    /** Empleado que se está editando */
    private Empleado empleado;
    
    /** Panel de diálogo principal */
    private DialogPane dialogPane;
    
    /** Empresa a la que pertenece el empleado */
    private Empresa empresa;
    
    /** Mapa de validaciones para los campos */
    private Map<TextField, ValidacionCampo> validaciones;

    /**
     * Registro para almacenar la información de validación de un campo.
     *
     * @param validador Predicado que verifica si el valor es válido
     * @param mensajeError Mensaje a mostrar si la validación falla
     */
    private record ValidacionCampo(Predicate<String> validador, String mensajeError) {}

    /**
     * Inicializa el controlador.
     * Configura las validaciones, listeners y valores por defecto.
     */
    @FXML
    public void initialize() {
        configurarValidaciones();
        configurarListeners();
        inicializarComboBoxRoles();
        inicializarCheckBoxActivo();
    }

    /**
     * Configura las validaciones para cada campo del formulario.
     */
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

    /**
     * Configura los listeners para validación en tiempo real.
     */
    private void configurarListeners() {
        validaciones.forEach((campo, validacion) -> 
            campo.textProperty().addListener((_, _, newValue) -> 
                validarCampo(campo, validacion.validador().test(newValue), validacion.mensajeError())
            )
        );
    }

    /**
     * Inicializa el ComboBox de roles con los valores disponibles.
     */
    private void inicializarComboBoxRoles() {
        rolComboBox.getItems().setAll(TipoEmpleado.values());
        rolComboBox.valueProperty().addListener((_, _, _) -> validarCampos());
        rolComboBox.setValue(TipoEmpleado.EMPLEADO);
    }

    /**
     * Establece el valor por defecto del CheckBox de estado.
     */
    private void inicializarCheckBoxActivo() {
        activoCheckBox.setSelected(true);
    }

    /**
     * Establece el panel de diálogo y realiza la validación inicial.
     *
     * @param dialogPane El panel de diálogo a establecer
     */
    public void setDialogPane(DialogPane dialogPane) {
        this.dialogPane = dialogPane;
        validarCampos();
    }

    /**
     * Establece la empresa para el empleado.
     *
     * @param empresa La empresa a establecer
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * Establece el empleado a editar y carga sus datos.
     *
     * @param empleado El empleado a editar
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
        if (empleado != null) {
            cargarDatosEmpleado();
        }
    }

    /**
     * Carga los datos del empleado en el formulario.
     */
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

    /**
     * Obtiene el empleado con los datos actualizados del formulario.
     *
     * @return El empleado actualizado o null si la validación falla
     */
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

    /**
     * Actualiza los datos del empleado con los valores del formulario.
     */
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

    /**
     * Obtiene el texto de un campo eliminando espacios en blanco.
     *
     * @param campo El campo de texto
     * @return El texto limpio
     */
    private String obtenerTextoLimpio(TextField campo) {
        return campo.getText().trim();
    }

    /**
     * Valida todos los campos del formulario.
     *
     * @return true si todos los campos son válidos
     */
    private boolean validarTodosLosCampos() {
        return validaciones.entrySet().stream()
                .allMatch(entry -> entry.getValue().validador().test(entry.getKey().getText())) &&
               rolComboBox.getValue() != null;
    }

    /**
     * Aplica la validación y actualiza el estado del botón OK.
     */
    private void validarCampos() {
        if (dialogPane != null) {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setDisable(!validarTodosLosCampos());
        }
    }

    /**
     * Valida un campo específico y actualiza su estilo visual.
     *
     * @param campo El campo a validar
     * @param condicion El resultado de la validación
     * @param mensajeError El mensaje de error a mostrar
     */
    private void validarCampo(TextField campo, boolean condicion, String mensajeError) {
        aplicarEstiloCampo(campo, condicion, mensajeError);
        validarCampos();
    }

    /**
     * Aplica el estilo visual según el estado de validación.
     *
     * @param campo El campo a estilizar
     * @param esValido Indica si el campo es válido
     * @param mensajeError El mensaje de error a mostrar
     */
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