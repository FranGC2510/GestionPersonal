package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.*;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador para el diálogo de asignación de turnos a empleados.
 * Gestiona la interfaz de usuario para crear nuevas asignaciones de turnos,
 * validando conflictos de horarios y asignaciones existentes.
 *
 */
public class AsignarTurnoDialogController {
    /** ComboBox para selección de empleado */
    @FXML private ComboBox<Empleado> empleadoComboBox;
    
    /** ComboBox para selección de turno */
    @FXML private ComboBox<Turno> turnoComboBox;
    
    /** DatePicker para selección de fecha */
    @FXML private DatePicker fechaPicker;

    /** DAO para acceso a datos de empleados */
    private final EmpleadoDAO empleadoDAO;
    
    /** DAO para acceso a datos de turnos */
    private final TurnoDAO turnoDAO;
    
    /** DAO para acceso a datos de asignaciones de turnos */
    private final PerteneceTurnoDAO perteneceTurnoDAO;
    
    /** Indica si la asignación se realizó con éxito */
    private boolean asignacionExitosa;

    /** Mensajes de error constantes */
    private static final String ERROR_SELECCION_EMPLEADO = "Debe seleccionar un empleado";
    private static final String ERROR_SELECCION_TURNO = "Debe seleccionar un turno";
    private static final String ERROR_SELECCION_FECHA = "Debe seleccionar una fecha";
    private static final String ERROR_ASIGNACION_EXISTENTE = "Ya existe una asignación para este empleado en esta fecha";
    private static final String ERROR_CONFLICTO_HORARIO = "Existe un conflicto de horario con otro turno asignado";

    /**
     * Constructor que inicializa los DAOs necesarios.
     */
    public AsignarTurnoDialogController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.turnoDAO = new TurnoDAO();
        this.perteneceTurnoDAO = new PerteneceTurnoDAO();
        this.asignacionExitosa = false;
    }

    /**
     * Inicializa el controlador.
     * Configura los ComboBoxes y el DatePicker, y carga los datos iniciales.
     */
    @FXML
    public void initialize() {
        configurarComboBoxes();
        configurarDatePicker();
        cargarDatos();
    }

    /**
     * Configura los ComboBoxes de empleados y turnos.
     * Inicializa ambos ComboBoxes con sus respectivos cell factories.
     */
    private void configurarComboBoxes() {
        configurarComboBoxEmpleados();
        configurarComboBoxTurnos();
    }

    /**
     * Configura el ComboBox de empleados con su cell factory personalizado.
     * Establece el formato de visualización para los empleados.
     */
    private void configurarComboBoxEmpleados() {
        Callback<ListView<Empleado>, ListCell<Empleado>> cellFactory = crearCellFactoryEmpleado();
        empleadoComboBox.setButtonCell(cellFactory.call(null));
        empleadoComboBox.setCellFactory(cellFactory);
    }

    /**
     * Crea un cell factory personalizado para la visualización de empleados.
     *
     * @return Callback que genera celdas personalizadas para empleados
     */
    private Callback<ListView<Empleado>, ListCell<Empleado>> crearCellFactoryEmpleado() {
        return lv -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        };
    }

    /**
     * Formatea el nombre completo del empleado para su visualización.
     *
     * @param empleado El empleado a formatear
     * @return String con el nombre y apellido del empleado
     */
    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    /**
     * Configura el ComboBox de turnos con su cell factory personalizado.
     * Establece el formato de visualización para los turnos.
     */
    private void configurarComboBoxTurnos() {
        Callback<ListView<Turno>, ListCell<Turno>> cellFactory = crearCellFactoryTurno();
        turnoComboBox.setButtonCell(cellFactory.call(null));
        turnoComboBox.setCellFactory(cellFactory);
    }

    /**
     * Crea un cell factory personalizado para la visualización de turnos.
     *
     * @return Callback que genera celdas personalizadas para turnos
     */
    private Callback<ListView<Turno>, ListCell<Turno>> crearCellFactoryTurno() {
        return lv -> new ListCell<>() {
            @Override
            protected void updateItem(Turno turno, boolean empty) {
                super.updateItem(turno, empty);
                setText(empty || turno == null ? null : formatearTurno(turno));
            }
        };
    }

    /**
     * Formatea la información del turno para su visualización.
     *
     * @param turno El turno a formatear
     * @return String con la descripción y horario del turno
     */
    private String formatearTurno(Turno turno) {
        return String.format("%s (%s - %s)", 
            turno.getDescripcion(), turno.getHoraInicio(), turno.getHoraFin());
    }

    /**
     * Configura el DatePicker con restricciones de fechas.
     * Deshabilita las fechas anteriores al día actual.
     */
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

    /**
     * Carga los datos iniciales en los ComboBoxes.
     * Obtiene la lista de empleados y turnos de la base de datos.
     */
    private void cargarDatos() {
        try {
            empleadoComboBox.getItems().addAll(empleadoDAO.findAll());
            turnoComboBox.getItems().addAll(turnoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los datos: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de asignación de turno.
     * Valida los campos y crea la nueva asignación si es posible.
     */
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

    /**
     * Crea una nueva asignación de turno validando conflictos.
     *
     * @return La nueva asignación o null si hay conflictos
     * @throws DAOException Si ocurre un error en el acceso a datos
     */
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

    /**
     * Verifica si existe conflicto de horario con otras asignaciones.
     *
     * @param empleado El empleado a verificar
     * @param nuevoTurno El nuevo turno a asignar
     * @param fecha La fecha de la asignación
     * @return true si hay conflicto, false en caso contrario
     * @throws DAOException Si ocurre un error en el acceso a datos
     */
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

    /**
     * Verifica si hay superposición entre dos rangos de tiempo.
     *
     * @param inicio1 Hora de inicio del primer rango
     * @param fin1 Hora de fin del primer rango
     * @param inicio2 Hora de inicio del segundo rango
     * @param fin2 Hora de fin del segundo rango
     * @return true si hay superposición, false en caso contrario
     */
    private boolean haySuperposicion(LocalTime inicio1, LocalTime fin1, 
                                   LocalTime inicio2, LocalTime fin2) {
        if (fin1.isBefore(inicio1)) {
            return !(fin2.isBefore(inicio1) && inicio2.isAfter(fin1));
        } else if (fin2.isBefore(inicio2)) {
            return !(fin1.isBefore(inicio2) && inicio1.isAfter(fin2));
        } else {
            return !(fin1.isBefore(inicio2) || fin2.isBefore(inicio1));
        }
    }

    /**
     * Procesa la asignación de turno en la base de datos.
     *
     * @param nuevaAsignacion La asignación a procesar
     * @throws DAOException Si ocurre un error en el acceso a datos
     */
    private void procesarAsignacion(PerteneceTurno nuevaAsignacion) throws DAOException {
        PerteneceTurno asignacionCreada = perteneceTurnoDAO.insert(nuevaAsignacion);
        if (asignacionCreada != null) {
            asignacionExitosa = true;
            cerrarVentana();
        } else {
            mostrarError("Error", "No se pudo crear la asignación");
        }
    }

    /**
     * Verifica si ya existe una asignación para el empleado, turno y fecha dados.
     *
     * @param empleado El empleado a verificar
     * @param turno El turno a verificar
     * @param fecha La fecha a verificar
     * @return true si ya existe la asignación, false en caso contrario
     * @throws DAOException Si ocurre un error en el acceso a datos
     */
    private boolean existeAsignacion(Empleado empleado, Turno turno, LocalDate fecha) throws DAOException {
        return perteneceTurnoDAO.exists(empleado.getIdEmpleado(), turno.getIdTurno(), fecha);
    }

    /**
     * Valida que todos los campos requeridos estén seleccionados.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
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

    /**
     * Verifica si la asignación se realizó con éxito.
     *
     * @return true si la asignación fue exitosa, false en caso contrario
     */
    public boolean isAsignacionExitosa() {
        return asignacionExitosa;
    }

    /**
     * Maneja el evento de cancelación.
     * Cierra la ventana sin realizar cambios.
     */
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    /**
     * Muestra un diálogo de error con el mensaje especificado.
     *
     * @param titulo El título del diálogo de error
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana actual del diálogo.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) empleadoComboBox.getScene().getWindow();
        stage.close();
    }
}