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
    // Elementos FXML
    @FXML private ComboBox<Empleado> empleadoComboBox;
    @FXML private ComboBox<Turno> turnoComboBox;
    @FXML private DatePicker fechaPicker;

    // Variables de clase
    private final EmpleadoDAO empleadoDAO;
    private final TurnoDAO turnoDAO;
    private final PerteneceTurnoDAO perteneceTurnoDAO;
    private boolean asignacionExitosa;
    private static final String ERROR_SELECCION_EMPLEADO = "Debe seleccionar un empleado";
    private static final String ERROR_SELECCION_TURNO = "Debe seleccionar un turno";
    private static final String ERROR_SELECCION_FECHA = "Debe seleccionar una fecha";
    private static final String ERROR_ASIGNACION_EXISTENTE = "Ya existe una asignación para este empleado en esta fecha";
    private static final String ERROR_CONFLICTO_HORARIO = "Existe un conflicto de horario con otro turno asignado";

    // Constructor
    public AsignarTurnoDialogController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.turnoDAO = new TurnoDAO();
        this.perteneceTurnoDAO = new PerteneceTurnoDAO();
        this.asignacionExitosa = false;
    }

    @FXML
    public void initialize() {
        configurarComboBoxes();
        configurarDatePicker();
        cargarDatos();
    }

    private void configurarComboBoxes() {
        configurarComboBoxEmpleados();
        configurarComboBoxTurnos();
    }

    private void configurarComboBoxEmpleados() {
        Callback<ListView<Empleado>, ListCell<Empleado>> cellFactory = crearCellFactoryEmpleado();
        empleadoComboBox.setButtonCell(cellFactory.call(null));
        empleadoComboBox.setCellFactory(cellFactory);
    }

    private Callback<ListView<Empleado>, ListCell<Empleado>> crearCellFactoryEmpleado() {
        return lv -> new ListCell<>() {
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

    private void configurarComboBoxTurnos() {
        Callback<ListView<Turno>, ListCell<Turno>> cellFactory = crearCellFactoryTurno();
        turnoComboBox.setButtonCell(cellFactory.call(null));
        turnoComboBox.setCellFactory(cellFactory);
    }

    private Callback<ListView<Turno>, ListCell<Turno>> crearCellFactoryTurno() {
        return lv -> new ListCell<>() {
            @Override
            protected void updateItem(Turno turno, boolean empty) {
                super.updateItem(turno, empty);
                setText(empty || turno == null ? null : formatearTurno(turno));
            }
        };
    }

    private String formatearTurno(Turno turno) {
        return String.format("%s (%s - %s)", 
            turno.getDescripcion(), turno.getHoraInicio(), turno.getHoraFin());
    }

    private void configurarDatePicker() {
        fechaPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean deshabilitar = empty || date.isBefore(LocalDate.now());
                setDisable(deshabilitar);
                setStyle(deshabilitar ? "-fx-background-color: #ffc0cb;" : null);
            }
        });
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
        if (!validarCampos()) return;

        try {
            PerteneceTurno nuevaAsignacion = crearAsignacion();
            if (nuevaAsignacion == null) return;

            procesarAsignacion(nuevaAsignacion);
        } catch (DAOException e) {
            mostrarError("Error", "Error al asignar el turno: " + e.getMessage());
        }
    }

    private PerteneceTurno crearAsignacion() throws DAOException {
        Empleado empleado = empleadoComboBox.getValue();
        Turno turno = turnoComboBox.getValue();
        LocalDate fecha = fechaPicker.getValue();

        if (existeAsignacion(empleado, turno, fecha)) {
            mostrarError("Error", ERROR_ASIGNACION_EXISTENTE);
            return null;
        }

        if (hayConflictoHorario(empleado, turno, fecha)) {
            mostrarError("Error", ERROR_CONFLICTO_HORARIO);
            return null;
        }

        return new PerteneceTurno(empleado, turno, fecha);
    }

    private boolean existeAsignacion(Empleado empleado, Turno turno, LocalDate fecha) throws DAOException {
        return perteneceTurnoDAO.exists(empleado.getIdEmpleado(), turno.getIdTurno(), fecha);
    }

    private void procesarAsignacion(PerteneceTurno nuevaAsignacion) throws DAOException {
        PerteneceTurno asignacionCreada = perteneceTurnoDAO.insert(nuevaAsignacion);
        if (asignacionCreada != null) {
            asignacionExitosa = true;
            cerrarVentana();
        } else {
            mostrarError("Error", "No se pudo crear la asignación");
        }
    }

    private boolean hayConflictoHorario(Empleado empleado, Turno nuevoTurno, LocalDate fecha) 
            throws DAOException {
        List<PerteneceTurno> asignacionesExistentes = 
            perteneceTurnoDAO.findByAsignacionesFecha(empleado, fecha);

        return asignacionesExistentes.stream()
            .map(PerteneceTurno::getTurno)
            .anyMatch(turnoExistente -> haySuperposicion(
                nuevoTurno.getHoraInicio(), nuevoTurno.getHoraFin(),
                turnoExistente.getHoraInicio(), turnoExistente.getHoraFin()));
    }

    private boolean haySuperposicion(LocalTime inicio1, LocalTime fin1, LocalTime inicio2, LocalTime fin2) {
        if (fin1.isBefore(inicio1)) {
            return !(fin2.isBefore(inicio1) && inicio2.isAfter(fin1));
        } else if (fin2.isBefore(inicio2)) {
            return !(fin1.isBefore(inicio2) && inicio1.isAfter(fin2));
        } else {
            return !(fin1.isBefore(inicio2) || fin2.isBefore(inicio1));
        }
    }

    private boolean validarCampos() {
        if (empleadoComboBox.getValue() == null) {
            mostrarError("Error", ERROR_SELECCION_EMPLEADO);
            return false;
        }
        if (turnoComboBox.getValue() == null) {
            mostrarError("Error", ERROR_SELECCION_TURNO);
            return false;
        }
        if (fechaPicker.getValue() == null) {
            mostrarError("Error", ERROR_SELECCION_FECHA);
            return false;
        }
        return true;
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
        Stage stage = (Stage) empleadoComboBox.getScene().getWindow();
        stage.close();
    }

    public boolean isAsignacionExitosa() {
        return asignacionExitosa;
    }
}