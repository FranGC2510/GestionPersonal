package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.dam.fcojavier.gestionpersonal.model.Ausencia;
import org.dam.fcojavier.gestionpersonal.model.Empleado;

import java.time.LocalDate;

/**
 * Controlador para el diálogo de edición de ausencias.
 * Gestiona la interfaz de usuario para crear y editar ausencias de empleados,
 * incluyendo la validación de fechas y campos requeridos.
 */
public class EditarAusenciaController {
    /** ComboBox para selección de empleado */
    @FXML private ComboBox<Empleado> empleadoComboBox;
    
    /** Campo de texto para el motivo de la ausencia */
    @FXML private TextArea motivoTextArea;
    
    /** Selector de fecha de inicio */
    @FXML private DatePicker fechaInicioPicker;
    
    /** Selector de fecha de fin */
    @FXML private DatePicker fechaFinPicker;

    /** Ausencia que se está editando */
    private Ausencia ausencia;
    
    /** Panel de diálogo principal */
    private DialogPane dialogPane;
    
    /** Mensaje de error para fechas inválidas */
    private static final String ERROR_FECHAS = "La fecha de fin no puede ser anterior a la fecha de inicio";

    /**
     * Inicializa el controlador.
     * Configura los selectores de fecha y las validaciones en tiempo real.
     */
    @FXML
    public void initialize() {
        configurarDatePickers();
        configurarValidaciones();
    }

    /**
     * Configura los selectores de fecha con sus respectivas validaciones.
     */
    private void configurarDatePickers() {
        configurarDatePicker(fechaInicioPicker);
        configurarDatePicker(fechaFinPicker);
    }

    /**
     * Configura un selector de fecha individual.
     *
     * @param datePicker El selector de fecha a configurar
     */
    private void configurarDatePicker(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(false);
            }
        });
    }

    /**
     * Configura las validaciones en tiempo real para todos los campos.
     */
    private void configurarValidaciones() {
        fechaInicioPicker.valueProperty().addListener((_, _, _) -> validarFechas());
        fechaFinPicker.valueProperty().addListener((_, _, _) -> validarFechas());
        empleadoComboBox.valueProperty().addListener((_, _, _) -> validarFormulario());
        motivoTextArea.textProperty().addListener((_, _, _) -> validarFormulario());
    }

    /**
     * Establece el panel de diálogo y realiza la validación inicial.
     *
     * @param dialogPane El panel de diálogo a establecer
     */
    public void setDialogPane(DialogPane dialogPane) {
        this.dialogPane = dialogPane;
        validarFormulario();
    }

    /**
     * Establece la lista de empleados disponibles en el ComboBox.
     *
     * @param empleados Lista observable de empleados
     */
    public void setEmpleados(ObservableList<Empleado> empleados) {
        empleadoComboBox.setItems(empleados);
    }

    /**
     * Configura el ComboBox de empleados con su visualización personalizada.
     */
    public void configurarComboBoxEmpleados() {
        Callback<ListView<Empleado>, ListCell<Empleado>> cellFactory = crearCellFactory();
        empleadoComboBox.setCellFactory(cellFactory);
        empleadoComboBox.setButtonCell(cellFactory.call(null));
    }

    /**
     * Crea un cell factory personalizado para la visualización de empleados.
     *
     * @return Callback que genera celdas personalizadas para empleados
     */
    private Callback<ListView<Empleado>, ListCell<Empleado>> crearCellFactory() {
        return param -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        };
    }

    /**
     * Formatea el nombre completo del empleado.
     *
     * @param empleado El empleado a formatear
     * @return String con el nombre y apellido del empleado
     */
    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    /**
     * Establece la ausencia a editar y carga sus datos en el formulario.
     *
     * @param ausencia La ausencia a editar
     */
    public void setAusencia(Ausencia ausencia) {
        this.ausencia = ausencia;
        if (ausencia != null) {
            cargarDatosAusencia();
        }
    }

    /**
     * Carga los datos de la ausencia en los campos del formulario.
     */
    private void cargarDatosAusencia() {
        empleadoComboBox.setValue(ausencia.getEmpleado());
        motivoTextArea.setText(ausencia.getMotivo());
        fechaInicioPicker.setValue(ausencia.getFechaInicio());
        fechaFinPicker.setValue(ausencia.getFechaFin());
    }

    /**
     * Obtiene la ausencia con los datos actualizados del formulario.
     *
     * @return La ausencia actualizada, o null si la validación falla
     */
    public Ausencia getAusencia() {
        if (!validarFormularioCompleto()) {
            return null;
        }
        if (ausencia == null) {
            ausencia = new Ausencia();
        }
        actualizarDatosAusencia();
        return ausencia;
    }

    /**
     * Actualiza los datos de la ausencia con los valores del formulario.
     */
    private void actualizarDatosAusencia() {
        ausencia.setEmpleado(empleadoComboBox.getValue());
        ausencia.setMotivo(motivoTextArea.getText().trim());
        ausencia.setFechaInicio(fechaInicioPicker.getValue());
        ausencia.setFechaFin(fechaFinPicker.getValue());
    }

    /**
     * Valida las fechas seleccionadas y actualiza la interfaz según el resultado.
     */
    private void validarFechas() {
        boolean fechasValidas = true;
        String mensajeError = null;

        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();

        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            fechasValidas = false;
            mensajeError = ERROR_FECHAS;
        }

        aplicarEstiloFechas(fechasValidas, mensajeError);
        validarFormulario();
    }

    /**
     * Aplica estilos visuales a los campos de fecha según su validez.
     *
     * @param fechasValidas Indica si las fechas son válidas
     * @param mensajeError Mensaje de error a mostrar si las fechas son inválidas
     */
    private void aplicarEstiloFechas(boolean fechasValidas, String mensajeError) {
        String estilo = fechasValidas ? "" : "-fx-border-color: red;";
        fechaInicioPicker.setStyle(estilo);
        fechaFinPicker.setStyle(estilo);

        if (!fechasValidas) {
            Tooltip tooltip = new Tooltip(mensajeError);
            fechaInicioPicker.setTooltip(tooltip);
            fechaFinPicker.setTooltip(tooltip);
        } else {
            fechaInicioPicker.setTooltip(null);
            fechaFinPicker.setTooltip(null);
        }
    }

    /**
     * Valida el formulario completo y actualiza el estado del botón OK.
     */
    private void validarFormulario() {
        if (dialogPane != null) {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setDisable(!validarFormularioCompleto());
        }
    }

    /**
     * Valida todos los campos requeridos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarFormularioCompleto() {
        boolean empleadoSeleccionado = empleadoComboBox.getValue() != null;
        boolean motivoValido = !motivoTextArea.getText().trim().isEmpty();
        boolean fechaInicioValida = fechaInicioPicker.getValue() != null;
        boolean fechasValidas = validarRangoFechas();

        return empleadoSeleccionado && 
               motivoValido && 
               fechaInicioValida && 
               fechasValidas;
    }

    /**
     * Valida el rango de fechas seleccionado.
     *
     * @return true si el rango es válido, false en caso contrario
     */
    private boolean validarRangoFechas() {
        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();

        if (fechaInicio == null) return false;
        if (fechaFin == null) return true; // Fecha fin es opcional
        return !fechaFin.isBefore(fechaInicio);
    }
}