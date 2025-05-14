package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.TurnoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.PerteneceTurnoDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AsignarTurnoDialogController {
    @FXML
    private ComboBox<Empleado> empleadoComboBox;
    @FXML
    private ComboBox<Turno> turnoComboBox;
    @FXML
    private DatePicker fechaPicker;

    private EmpleadoDAO empleadoDAO;
    private TurnoDAO turnoDAO;
    private PerteneceTurnoDAO perteneceTurnoDAO;
    private boolean asignacionExitosa = false;

    @FXML
    public void initialize() {
        empleadoDAO = new EmpleadoDAO();
        turnoDAO = new TurnoDAO();
        perteneceTurnoDAO = new PerteneceTurnoDAO();

        // Configurar formato de visualización de empleados
        Callback<ListView<Empleado>, ListCell<Empleado>> empleadoCellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre() + " " + empleado.getApellido());
                }
            }
        };

        empleadoComboBox.setButtonCell(empleadoCellFactory.call(null));
        empleadoComboBox.setCellFactory(empleadoCellFactory);

        // Configurar formato de visualización de turnos
        Callback<ListView<Turno>, ListCell<Turno>> turnoCellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Turno turno, boolean empty) {
                super.updateItem(turno, empty);
                if (empty || turno == null) {
                    setText(null);
                } else {
                    setText(turno.getDescripcion() + " (" + turno.getHoraInicio() + " - " + turno.getHoraFin() + ")");
                }
            }
        };

        turnoComboBox.setButtonCell(turnoCellFactory.call(null));
        turnoComboBox.setCellFactory(turnoCellFactory);

        // Configurar DatePicker para no permitir fechas anteriores
        fechaPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
                if (empty || date.isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Color rosa claro para fechas deshabilitadas
                }
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            empleadoComboBox.getItems().addAll(empleadoDAO.findAll());
            turnoComboBox.getItems().addAll(turnoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void handleAsignar() {
        if (validarCampos()) {
            try {
                Empleado empleado = empleadoComboBox.getValue();
                Turno turno = turnoComboBox.getValue();
                LocalDate fecha = fechaPicker.getValue();

                // Verificar si ya existe una asignación
                if (perteneceTurnoDAO.exists(empleado.getIdEmpleado(), turno.getIdTurno(), fecha)) {
                    mostrarError("Error", "Ya existe una asignación para este empleado en esta fecha");
                    return;
                }

                // Verificar conflictos de horario
                if (hayConflictoHorario(empleado, turno, fecha)) {
                    mostrarError("Error", "Existe un conflicto de horario con otro turno asignado");
                    return;
                }

                // Crear nueva asignación
                PerteneceTurno nuevaAsignacion = new PerteneceTurno(empleado, turno, fecha);
                PerteneceTurno asignacionCreada = perteneceTurnoDAO.insert(nuevaAsignacion);

                if (asignacionCreada != null) {
                    asignacionExitosa = true;
                    cerrarVentana();
                } else {
                    mostrarError("Error", "No se pudo crear la asignación");
                }

            } catch (DAOException e) {
                mostrarError("Error", "Error al asignar el turno: " + e.getMessage());
            }
        }

    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private boolean validarCampos() {
        if (empleadoComboBox.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un empleado");
            return false;
        }

        if (turnoComboBox.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un turno");
            return false;
        }

        if (fechaPicker.getValue() == null) {
            mostrarError("Error", "Debe seleccionar una fecha");
            return false;
        }

        return true;
    }

    private boolean hayConflictoHorario(Empleado empleado, Turno nuevoTurno, LocalDate fecha) throws DAOException {
        // Obtener todas las asignaciones del empleado para esa fecha
        List<PerteneceTurno> asignacionesExistentes = perteneceTurnoDAO.findByAsignacionesFecha(empleado, fecha);

        // Convertir las horas del nuevo turno a LocalTime para comparación
        LocalTime nuevoInicio = nuevoTurno.getHoraInicio();
        LocalTime nuevoFin = nuevoTurno.getHoraFin();

        // Verificar superposición con turnos existentes
        for (PerteneceTurno asignacion : asignacionesExistentes) {
            Turno turnoExistente = asignacion.getTurno();
            LocalTime existenteInicio = turnoExistente.getHoraInicio();
            LocalTime existenteFin = turnoExistente.getHoraFin();

            // Verificar si hay superposición de horarios
            if (haySuperposicion(nuevoInicio, nuevoFin, existenteInicio, existenteFin)) {
                return true; // Hay conflicto
            }
        }

        return false; // No hay conflicto
    }

    private boolean haySuperposicion(LocalTime inicio1, LocalTime fin1, LocalTime inicio2, LocalTime fin2) {
        // Manejar el caso especial cuando un turno cruza la medianoche
        if (fin1.isBefore(inicio1)) { // El primer turno cruza la medianoche
            return !(fin2.isBefore(inicio1) && inicio2.isAfter(fin1));
        } else if (fin2.isBefore(inicio2)) { // El segundo turno cruza la medianoche
            return !(fin1.isBefore(inicio2) && inicio1.isAfter(fin2));
        } else {
            // Caso normal - ningún turno cruza la medianoche
            return !(fin1.isBefore(inicio2) || fin2.isBefore(inicio1));
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) empleadoComboBox.getScene().getWindow();
        stage.close();
    }

    public boolean isAsignacionExitosa() {
        return asignacionExitosa;
    }
}