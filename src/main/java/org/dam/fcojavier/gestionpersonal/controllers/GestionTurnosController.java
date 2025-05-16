package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.TurnoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.PerteneceTurnoDAO;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empresa;
import org.dam.fcojavier.gestionpersonal.model.Turno;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.PerteneceTurno;

import java.io.IOException;
import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;

/**
 * Controlador para la gestión de turnos y sus asignaciones a empleados.
 * Permite crear, editar y eliminar turnos, así como gestionar las asignaciones
 * de turnos a empleados en fechas específicas.
 */
public class GestionTurnosController {
    /** Tabla de turnos */
    @FXML private TableView<Turno> turnosTable;
    
    /** Columna para la descripción del turno */
    @FXML private TableColumn<Turno, String> descripcionColumn;
    
    /** Columna para la hora de inicio del turno */
    @FXML private TableColumn<Turno, String> horaInicioColumn;
    
    /** Columna para la hora de fin del turno */
    @FXML private TableColumn<Turno, String> horaFinColumn;
    
    /** Columna para la duración del turno */
    @FXML private TableColumn<Turno, Double> duracionColumn;
    
    /** Tabla de asignaciones de turnos */
    @FXML private TableView<PerteneceTurno> asignacionesTable;
    
    /** Columna para el nombre del empleado */
    @FXML private TableColumn<PerteneceTurno, String> empleadoColumn;
    
    /** Columna para la descripción del turno asignado */
    @FXML private TableColumn<PerteneceTurno, String> turnoColumn;
    
    /** Columna para la fecha de asignación */
    @FXML private TableColumn<PerteneceTurno, LocalDate> fechaColumn;
    
    /** Columna para los horarios del turno */
    @FXML private TableColumn<PerteneceTurno, String> horariosColumn;
    
    /** Selector de fecha para filtrar */
    @FXML private DatePicker fechaFiltro;
    
    /** Botón para editar turno */
    @FXML private Button editarTurnoBtn;
    
    /** Botón para eliminar turno */
    @FXML private Button eliminarTurnoBtn;
    
    /** Botón para eliminar asignación */
    @FXML private Button eliminarAsignacionBtn;

    /** DAO para acceder a los datos de turnos */
    private final TurnoDAO turnoDAO;
    
    /** DAO para acceder a los datos de asignaciones */
    private final PerteneceTurnoDAO perteneceTurnoDAO;
    
    /** Lista observable de turnos */
    private final ObservableList<Turno> turnos;
    
    /** Lista observable de asignaciones */
    private final ObservableList<PerteneceTurno> asignaciones;
    
    /** Lista filtrada de asignaciones */
    private final FilteredList<PerteneceTurno> asignacionesFiltradas;
    
    /** Empresa actual */
    private Empresa empresaActual;
    
    /** Ruta al archivo FXML del diálogo de turno */
    private static final String RUTA_DIALOG_TURNO = "turno-dialog.fxml";
    
    /** Ruta al archivo FXML del diálogo de asignación */
    private static final String RUTA_DIALOG_ASIGNAR = "asignar-turno-dialog.fxml";

    /**
     * Constructor del controlador.
     * Inicializa los DAOs y las colecciones observables.
     */
    public GestionTurnosController() {
        this.turnoDAO = new TurnoDAO();
        this.perteneceTurnoDAO = new PerteneceTurnoDAO();
        this.turnos = FXCollections.observableArrayList();
        this.asignaciones = FXCollections.observableArrayList();
        this.asignacionesFiltradas = new FilteredList<>(asignaciones, _ -> true);
    }

    /**
     * Inicializa el controlador.
     * Configura las tablas, filtros y carga los datos iniciales.
     */
    @FXML
    public void initialize() {
        configurarTablas();
        configurarFiltros();
        configurarSeleccion();
        cargarDatos();
    }

    /**
     * Configura las tablas de turnos y asignaciones.
     */
    private void configurarTablas() {
        configurarTablaTurnos();
        configurarTablaAsignaciones();
    }

    /**
     * Configura la tabla de turnos.
     */
    private void configurarTablaTurnos() {
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        horaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        horaFinColumn.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        duracionColumn.setCellValueFactory(new PropertyValueFactory<>("duracionHoras"));
        turnosTable.setItems(turnos);
    }

    /**
     * Configura la tabla de asignaciones.
     */
    private void configurarTablaAsignaciones() {
        empleadoColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearNombreEmpleado(cellData.getValue().getEmpleado())));
        turnoColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTurno().getDescripcion()));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horariosColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearHorario(cellData.getValue().getTurno())));
        asignacionesTable.setItems(asignacionesFiltradas);
    }

    /**
     * Formatea el nombre completo del empleado.
     *
     * @param empleado Empleado cuyo nombre se formateará
     * @return Nombre completo formateado
     */
    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    /**
     * Formatea el horario del turno.
     *
     * @param turno Turno cuyo horario se formateará
     * @return Horario formateado
     */
    private String formatearHorario(Turno turno) {
        return turno.getHoraInicio() + " - " + turno.getHoraFin();
    }

    /**
     * Configura los listeners de selección para las tablas.
     */
    private void configurarSeleccion() {
        turnosTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, seleccion) -> actualizarBotonesTurno(seleccion != null));
        asignacionesTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, seleccion) -> actualizarBotonAsignacion(seleccion != null));
    }

    /**
     * Actualiza el estado de los botones de turno.
     *
     * @param haySeleccion true si hay un turno seleccionado
     */
    private void actualizarBotonesTurno(boolean haySeleccion) {
        editarTurnoBtn.setDisable(!haySeleccion);
        eliminarTurnoBtn.setDisable(!haySeleccion);
    }

    /**
     * Actualiza el estado del botón de asignación.
     *
     * @param haySeleccion true si hay una asignación seleccionada
     */
    private void actualizarBotonAsignacion(boolean haySeleccion) {
        eliminarAsignacionBtn.setDisable(!haySeleccion);
    }

    /**
     * Configura el filtro de fecha.
     */
    private void configurarFiltros() {
        fechaFiltro.valueProperty().addListener((_, _, _) -> aplicarFiltros());
    }

    /**
     * Aplica los filtros actuales a las asignaciones.
     */
    private void aplicarFiltros() {
        asignacionesFiltradas.setPredicate(this::cumpleFiltroFecha);
    }

    /**
     * Verifica si una asignación cumple con el filtro de fecha.
     *
     * @param asignacion Asignación a verificar
     * @return true si cumple con el filtro
     */
    private boolean cumpleFiltroFecha(PerteneceTurno asignacion) {
        if (fechaFiltro.getValue() == null) return true;
        return asignacion.getFecha().equals(fechaFiltro.getValue());
    }

    /**
     * Carga todos los datos necesarios.
     */
    private void cargarDatos() {
        cargarTurnos();
        cargarAsignaciones();
    }

    /**
     * Carga los turnos desde la base de datos.
     */
    private void cargarTurnos() {
        try {
            turnos.setAll(turnoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los turnos: " + e.getMessage());
        }
    }

    /**
     * Carga las asignaciones desde la base de datos.
     */
    private void cargarAsignaciones() {
        try {
            if (fechaFiltro.getValue() != null) {
                asignaciones.setAll(perteneceTurnoDAO.findByFecha(fechaFiltro.getValue()));
            } else {
                asignaciones.setAll(perteneceTurnoDAO.findAll());
            }
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar las asignaciones: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de crear un nuevo turno.
     */
    @FXML
    private void handleNuevoTurno() {
        mostrarDialogoTurno("Nuevo Turno", null);
    }

    /**
     * Maneja el evento de editar un turno existente.
     */
    @FXML
    private void handleEditarTurno() {
        Turno turnoSeleccionado = turnosTable.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado != null) {
            mostrarDialogoTurno("Editar Turno", turnoSeleccionado);
        }
    }

    /**
     * Muestra el diálogo de turno para creación o edición.
     *
     * @param titulo Título del diálogo
     * @param turno Turno a editar, null si es nuevo
     */
    private void mostrarDialogoTurno(String titulo, Turno turno) {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource(RUTA_DIALOG_TURNO));
            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle(titulo);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(turnosTable.getScene().getWindow());
            dialogStage.setScene(scene);

            TurnoDialogController controller = loader.getController();
            controller.setTurno(turno);

            dialogStage.showAndWait();

            if (controller.isGuardadoExitoso()) {
                cargarTurnos();
            }
        } catch (IOException e) {
            mostrarError("Error", "Error al abrir el diálogo de turno");
        }
    }

    /**
     * Maneja el evento de eliminar un turno.
     */
    @FXML
    private void handleEliminarTurno() {
        Turno turnoSeleccionado = turnosTable.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado != null && confirmarEliminacion("turno")) {
            try {
                turnoDAO.delete(turnoSeleccionado);
                turnos.remove(turnoSeleccionado);
                cargarAsignaciones(); // Recargar asignaciones por si se eliminaron en cascada
            } catch (DAOException e) {
                mostrarError("Error", "Error al eliminar el turno: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de eliminar una asignación.
     */
    @FXML
    private void handleEliminarAsignacion() {
        PerteneceTurno asignacionSeleccionada = asignacionesTable.getSelectionModel().getSelectedItem();
        if (asignacionSeleccionada != null && confirmarEliminacion("asignación")) {
            try {
                perteneceTurnoDAO.delete(asignacionSeleccionada);
                asignaciones.remove(asignacionSeleccionada);
            } catch (DAOException e) {
                mostrarError("Error", "Error al eliminar la asignación: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra un diálogo de confirmación para eliminar.
     *
     * @param elemento Tipo de elemento a eliminar
     * @return true si se confirma la eliminación
     */
    private boolean confirmarEliminacion(String elemento) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este " + elemento + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Maneja el evento de asignar un turno a un empleado.
     */
    @FXML
    private void handleAsignarTurno() {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource(RUTA_DIALOG_ASIGNAR));
            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Asignar Turno");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(turnosTable.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.setWidth(400);
            dialogStage.setHeight(300);

            AsignarTurnoDialogController controller = loader.getController();
            dialogStage.showAndWait();

            if (controller.isAsignacionExitosa()) {
                mostrarInformacion("Éxito", "Turno asignado correctamente");
                cargarAsignaciones();
            }
        } catch (IOException e) {
            mostrarError("Error", "Error al abrir el diálogo de asignación de turno");
        }
    }

    /**
     * Maneja el evento de limpiar el filtro de fecha.
     */
    @FXML
    private void handleLimpiarFiltro() {
        fechaFiltro.setValue(null);
        cargarAsignaciones();
    }

    /**
     * Establece la empresa actual y carga sus datos.
     *
     * @param empresa La empresa cuyos turnos se gestionarán
     */
    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarDatos();
    }

    /**
     * Abre el diálogo para asignar un turno.
     * Este método puede ser llamado desde otros controladores.
     */
    public void abrirDialogAsignarTurno() {
        handleAsignarTurno();
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param titulo Título del error
     * @param mensaje Mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de información.
     *
     * @param titulo Título del diálogo
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}