package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.model.Turno;
import org.dam.fcojavier.gestionpersonal.DAOs.TurnoDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TurnoDialogController {
    // Elementos FXML
    @FXML private TextField descripcionField;
    @FXML private TextField horaInicioField;
    @FXML private TextField horaFinField;

    // Variables de clase
    private final TurnoDAO turnoDAO;
    private Turno turno;
    private boolean guardadoExitoso;

    // Constantes de error
    private static final String ERROR_DESCRIPCION_VACIA = "La descripción no puede estar vacía";
    private static final String ERROR_FORMATO_HORA = "El formato de hora debe ser HH:mm";
    private static final String ERROR_HORA_FIN = "La hora de fin debe ser posterior a la hora de inicio";
    private static final String ERROR_GUARDAR = "No se pudo guardar el turno";

    // Constructor
    public TurnoDialogController() {
        this.turnoDAO = new TurnoDAO();
        this.guardadoExitoso = false;
    }

    @FXML
    public void initialize() {
        configurarValidadores();
    }

    private void configurarValidadores() {
        // Validación en tiempo real para la descripción
        descripcionField.textProperty().addListener((_, _, newValue) ->
                validarDescripcion(newValue));

        // Validación en tiempo real para las horas
        horaInicioField.textProperty().addListener((_, _, newValue) ->
                validarFormatoHora(horaInicioField, newValue));
        horaFinField.textProperty().addListener((_, _, newValue) ->
                validarFormatoHora(horaFinField, newValue));
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion.trim().isEmpty()) {
            marcarCampoError(descripcionField, ERROR_DESCRIPCION_VACIA);
        } else {
            limpiarCampoError(descripcionField);
        }
    }

    private void validarFormatoHora(TextField campo, String hora) {
        try {
            if (!hora.isEmpty()) {
                LocalTime.parse(hora);
                limpiarCampoError(campo);
            }
        } catch (DateTimeParseException e) {
            marcarCampoError(campo, ERROR_FORMATO_HORA);
        }
    }

    private void marcarCampoError(TextField campo, String mensaje) {
        campo.setStyle("-fx-border-color: red;");
        campo.setTooltip(new Tooltip(mensaje));
    }

    private void limpiarCampoError(TextField campo) {
        campo.setStyle("");
        campo.setTooltip(null);
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
        if (turno != null) {
            cargarDatosTurno();
        }
    }

    private void cargarDatosTurno() {
        descripcionField.setText(turno.getDescripcion());
        horaInicioField.setText(turno.getHoraInicio().toString());
        horaFinField.setText(turno.getHoraFin().toString());
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormularioCompleto()) return;

        try {
            Turno turnoResultado = turno == null ?
                    crearNuevoTurno() :
                    actualizarTurnoExistente();

            procesarResultado(turnoResultado);
        } catch (DAOException e) {
            mostrarError("Error", "Error al guardar el turno: " + e.getMessage());
        }
    }

    private boolean validarFormularioCompleto() {
        if (descripcionField.getText().trim().isEmpty()) {
            mostrarError("Error", ERROR_DESCRIPCION_VACIA);
            return false;
        }

        try {
            LocalTime horaInicio = LocalTime.parse(horaInicioField.getText());
            LocalTime horaFin = LocalTime.parse(horaFinField.getText());

            if (horaFin.isBefore(horaInicio)) {
                mostrarError("Error", ERROR_HORA_FIN);
                return false;
            }
        } catch (DateTimeParseException e) {
            mostrarError("Error", ERROR_FORMATO_HORA);
            return false;
        }

        return true;
    }

    private Turno crearNuevoTurno() throws DAOException {
        Turno nuevoTurno = new Turno(
                descripcionField.getText(),
                LocalTime.parse(horaInicioField.getText()),
                LocalTime.parse(horaFinField.getText())
        );
        return turnoDAO.insert(nuevoTurno);
    }

    private Turno actualizarTurnoExistente() throws DAOException {
        turno.setDescripcion(descripcionField.getText());
        turno.setHoraInicio(LocalTime.parse(horaInicioField.getText()));
        turno.setHoraFin(LocalTime.parse(horaFinField.getText()));
        return turnoDAO.update(turno);
    }

    private void procesarResultado(Turno turnoResultado) {
        if (turnoResultado != null) {
            guardadoExitoso = true;
            cerrarVentana();
        } else {
            mostrarError("Error", ERROR_GUARDAR);
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) descripcionField.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}
