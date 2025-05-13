package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.model.Turno;
import org.dam.fcojavier.gestionpersonal.DAOs.TurnoDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TurnoDialogController {
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField horaInicioField;
    @FXML
    private TextField horaFinField;

    private TurnoDAO turnoDAO;
    private Turno turno;
    private boolean guardadoExitoso = false;

    @FXML
    public void initialize() {
        turnoDAO = new TurnoDAO();
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
        if (turno != null) {
            // Modo edición
            descripcionField.setText(turno.getDescripcion());
            horaInicioField.setText(turno.getHoraInicio().toString());
            horaFinField.setText(turno.getHoraFin().toString());
        }
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            try {
                if (turno == null) {
                    // Modo nuevo turno
                    turno = new Turno(
                        descripcionField.getText(),
                        LocalTime.parse(horaInicioField.getText()),
                        LocalTime.parse(horaFinField.getText())
                    );
                    Turno turnoInsertado = turnoDAO.insert(turno);
                    guardadoExitoso = (turnoInsertado != null);
                } else {
                    // Modo edición
                    turno.setDescripcion(descripcionField.getText());
                    turno.setHoraInicio(LocalTime.parse(horaInicioField.getText()));
                    turno.setHoraFin(LocalTime.parse(horaFinField.getText()));
                    Turno turnoActualizado = turnoDAO.update(turno);
                    guardadoExitoso = (turnoActualizado != null);
                }

                if (guardadoExitoso) {
                    cerrarVentana();
                } else {
                    mostrarError("Error", "No se pudo guardar el turno");
                }
            } catch (DAOException e) {
                mostrarError("Error", "Error al guardar el turno: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private boolean validarCampos() {
        if (descripcionField.getText().trim().isEmpty()) {
            mostrarError("Error", "La descripción no puede estar vacía");
            return false;
        }

        try {
            LocalTime horaInicio = LocalTime.parse(horaInicioField.getText());
            LocalTime horaFin = LocalTime.parse(horaFinField.getText());
            
            if (horaFin.isBefore(horaInicio)) {
                mostrarError("Error", "La hora de fin debe ser posterior a la hora de inicio");
                return false;
            }
        } catch (DateTimeParseException e) {
            mostrarError("Error", "El formato de hora debe ser HH:mm");
            return false;
        }

        return true;
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