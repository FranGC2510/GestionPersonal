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

public class GestionTurnosController {
    // Elementos FXML
    @FXML private TableView<Turno> turnosTable;
    @FXML private TableColumn<Turno, String> descripcionColumn;
    @FXML private TableColumn<Turno, String> horaInicioColumn;
    @FXML private TableColumn<Turno, String> horaFinColumn;
    @FXML private TableColumn<Turno, Double> duracionColumn;
    @FXML private TableView<PerteneceTurno> asignacionesTable;
    @FXML private TableColumn<PerteneceTurno, String> empleadoColumn;
    @FXML private TableColumn<PerteneceTurno, String> turnoColumn;
    @FXML private TableColumn<PerteneceTurno, LocalDate> fechaColumn;
    @FXML private TableColumn<PerteneceTurno, String> horariosColumn;
    @FXML private DatePicker fechaFiltro;
    @FXML private Button editarTurnoBtn;
    @FXML private Button eliminarTurnoBtn;
    @FXML private Button eliminarAsignacionBtn;

    // Variables de clase
    private final TurnoDAO turnoDAO;
    private final PerteneceTurnoDAO perteneceTurnoDAO;
    private final ObservableList<Turno> turnos;
    private final ObservableList<PerteneceTurno> asignaciones;
    private final FilteredList<PerteneceTurno> asignacionesFiltradas;
    private Empresa empresaActual;
    private static final String RUTA_DIALOG_TURNO = "turno-dialog.fxml";
    private static final String RUTA_DIALOG_ASIGNAR = "asignar-turno-dialog.fxml";

    // Constructor
    public GestionTurnosController() {
        this.turnoDAO = new TurnoDAO();
        this.perteneceTurnoDAO = new PerteneceTurnoDAO();
        this.turnos = FXCollections.observableArrayList();
        this.asignaciones = FXCollections.observableArrayList();
        this.asignacionesFiltradas = new FilteredList<>(asignaciones, _ -> true);
    }

    @FXML
    public void initialize() {
        configurarTablas();
        configurarFiltros();
        configurarSeleccion();
        cargarDatos();
    }

    private void configurarTablas() {
        configurarTablaTurnos();
        configurarTablaAsignaciones();
    }

    private void configurarTablaTurnos() {
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        horaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        horaFinColumn.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        duracionColumn.setCellValueFactory(new PropertyValueFactory<>("duracionHoras"));
        turnosTable.setItems(turnos);
    }

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

    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    private String formatearHorario(Turno turno) {
        return turno.getHoraInicio() + " - " + turno.getHoraFin();
    }

    private void configurarSeleccion() {
        turnosTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, seleccion) -> actualizarBotonesTurno(seleccion != null));
        asignacionesTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, seleccion) -> actualizarBotonAsignacion(seleccion != null));
    }

    private void actualizarBotonesTurno(boolean haySeleccion) {
        editarTurnoBtn.setDisable(!haySeleccion);
        eliminarTurnoBtn.setDisable(!haySeleccion);
    }

    private void actualizarBotonAsignacion(boolean haySeleccion) {
        eliminarAsignacionBtn.setDisable(!haySeleccion);
    }

    private void configurarFiltros() {
        fechaFiltro.valueProperty().addListener((_, _, _) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        asignacionesFiltradas.setPredicate(this::cumpleFiltroFecha);
    }

    private boolean cumpleFiltroFecha(PerteneceTurno asignacion) {
        if (fechaFiltro.getValue() == null) return true;
        return asignacion.getFecha().equals(fechaFiltro.getValue());
    }

    private void cargarDatos() {
        cargarTurnos();
        cargarAsignaciones();
    }

    private void cargarTurnos() {
        try {
            turnos.setAll(turnoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los turnos: " + e.getMessage());
        }
    }

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

    @FXML
    private void handleNuevoTurno() {
        mostrarDialogoTurno("Nuevo Turno", null);
    }

    @FXML
    private void handleEditarTurno() {
        Turno turnoSeleccionado = turnosTable.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado != null) {
            mostrarDialogoTurno("Editar Turno", turnoSeleccionado);
        }
    }

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

    private boolean confirmarEliminacion(String elemento) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este " + elemento + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

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

    @FXML
    private void handleLimpiarFiltro() {
        fechaFiltro.setValue(null);
        cargarAsignaciones();
    }

    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarDatos();
    }

    public void abrirDialogAsignarTurno() {
        handleAsignarTurno();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}