package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.dam.fcojavier.gestionpersonal.model.Ausencia;
import org.dam.fcojavier.gestionpersonal.model.Empleado;

import java.time.LocalDate;

public class EditarAusenciaController {
    // Elementos FXML
    @FXML private ComboBox<Empleado> empleadoComboBox;
    @FXML private TextArea motivoTextArea;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;

    // Variables de clase
    private Ausencia ausencia;
    private DialogPane dialogPane;
    private static final String ERROR_FECHAS = "La fecha de fin no puede ser anterior a la fecha de inicio";

    @FXML
    public void initialize() {
        configurarDatePickers();
        configurarValidaciones();
    }

    private void configurarDatePickers() {
        configurarDatePicker(fechaInicioPicker);
        configurarDatePicker(fechaFinPicker);
    }

    private void configurarDatePicker(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(false);
            }
        });
    }

    private void configurarValidaciones() {
        // Validación de fechas en tiempo real
        fechaInicioPicker.valueProperty().addListener((_, _, _) -> validarFechas());
        fechaFinPicker.valueProperty().addListener((_, _, _) -> validarFechas());
        
        // Validación del empleado seleccionado
        empleadoComboBox.valueProperty().addListener((_, _, _) -> validarFormulario());
        
        // Validación del motivo
        motivoTextArea.textProperty().addListener((_, _, _) -> validarFormulario());
    }

    public void setDialogPane(DialogPane dialogPane) {
        this.dialogPane = dialogPane;
        validarFormulario();
    }

    public void setEmpleados(ObservableList<Empleado> empleados) {
        empleadoComboBox.setItems(empleados);
    }

    public void configurarComboBoxEmpleados() {
        Callback<ListView<Empleado>, ListCell<Empleado>> cellFactory = crearCellFactory();
        empleadoComboBox.setCellFactory(cellFactory);
        empleadoComboBox.setButtonCell(cellFactory.call(null));
    }

    private Callback<ListView<Empleado>, ListCell<Empleado>> crearCellFactory() {
        return param -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        };
    }

    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    public void setAusencia(Ausencia ausencia) {
        this.ausencia = ausencia;
        if (ausencia != null) {
            cargarDatosAusencia();
        }
    }

    private void cargarDatosAusencia() {
        empleadoComboBox.setValue(ausencia.getEmpleado());
        motivoTextArea.setText(ausencia.getMotivo());
        fechaInicioPicker.setValue(ausencia.getFechaInicio());
        fechaFinPicker.setValue(ausencia.getFechaFin());
    }

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

    private void actualizarDatosAusencia() {
        ausencia.setEmpleado(empleadoComboBox.getValue());
        ausencia.setMotivo(motivoTextArea.getText().trim());
        ausencia.setFechaInicio(fechaInicioPicker.getValue());
        ausencia.setFechaFin(fechaFinPicker.getValue());
    }

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

    private void validarFormulario() {
        if (dialogPane != null) {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setDisable(!validarFormularioCompleto());
        }
    }

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

    private boolean validarRangoFechas() {
        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();

        if (fechaInicio == null) return false;
        if (fechaFin == null) return true; // Fecha fin es opcional
        return !fechaFin.isBefore(fechaInicio);
    }
}