package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.dam.fcojavier.gestionpersonal.model.Turno;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.PerteneceTurno;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

// Añadir estos campos al inicio de la clase
import javafx.beans.property.SimpleStringProperty;

public class GestionTurnosController {
    @FXML
    private TableView<Turno> turnosTable;
    @FXML
    private TableColumn<Turno, String> descripcionColumn;
    @FXML
    private TableColumn<Turno, String> horaInicioColumn;
    @FXML
    private TableColumn<Turno, String> horaFinColumn;
    @FXML
    private TableColumn<Turno, Double> duracionColumn;
    @FXML
    private TableView<PerteneceTurno> asignacionesTable;
    @FXML
    private TableColumn<PerteneceTurno, String> empleadoColumn;
    @FXML
    private TableColumn<PerteneceTurno, String> turnoColumn;
    @FXML
    private TableColumn<PerteneceTurno, LocalDate> fechaColumn;
    @FXML
    private TableColumn<PerteneceTurno, String> horariosColumn;
    @FXML
    private DatePicker fechaFiltro;
    @FXML
    private Button editarTurnoBtn;
    @FXML
    private Button eliminarTurnoBtn;
    @FXML
    private Button eliminarAsignacionBtn;

    private TurnoDAO turnoDAO;
    private PerteneceTurnoDAO perteneceTurnoDAO;
    private ObservableList<Turno> turnos;
    private ObservableList<PerteneceTurno> asignaciones;

    @FXML
    public void initialize() {
        turnoDAO = new TurnoDAO();
        perteneceTurnoDAO = new PerteneceTurnoDAO();
        turnos = FXCollections.observableArrayList();

        // Configurar columnas
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        horaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        horaFinColumn.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        duracionColumn.setCellValueFactory(new PropertyValueFactory<>("duracionHoras"));

        // Vincular la lista observable con la tabla
        turnosTable.setItems(turnos);

        cargarTurnos();

        // Inicializar la lista de asignaciones y el DAO
        perteneceTurnoDAO = new PerteneceTurnoDAO();
        asignaciones = FXCollections.observableArrayList();

        // Configurar columnas de la tabla de asignaciones
        empleadoColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmpleado().getNombre() +
                        " " + cellData.getValue().getEmpleado().getApellido()));

        turnoColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTurno().getDescripcion()));

        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        horariosColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTurno().getHoraInicio() +
                        " - " + cellData.getValue().getTurno().getHoraFin()));


        // Vincular la lista observable con la tabla
        asignacionesTable.setItems(asignaciones);

        // Cargar datos iniciales
        cargarAsignaciones();

        // Configurar el estado de los botones basado en la selección de las tablas
        turnosTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            editarTurnoBtn.setDisable(!haySeleccion);
            eliminarTurnoBtn.setDisable(!haySeleccion);
        });

        asignacionesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            eliminarAsignacionBtn.setDisable(newSelection == null);
        });

        // Deshabilitar botones inicialmente
        editarTurnoBtn.setDisable(true);
        eliminarTurnoBtn.setDisable(true);
        eliminarAsignacionBtn.setDisable(true);
    }

    @FXML
    private void handleEditarTurno() {
        Turno turnoSeleccionado = turnosTable.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado != null) {
            abrirDialogoTurno(turnoSeleccionado);
        }
    }

    @FXML
    private void handleEliminarTurno() {
        Turno turnoSeleccionado = turnosTable.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro que desea eliminar este turno?");
            confirmacion.setContentText("Esta acción eliminará el turno '" + turnoSeleccionado.getDescripcion() +
                    "' y todas sus asignaciones a empleados.");

            Optional<ButtonType> result = confirmacion.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    if (turnoDAO.delete(turnoSeleccionado)) {
                        turnos.remove(turnoSeleccionado);
                        mostrarInformacion("Éxito", "El turno ha sido eliminado correctamente.");
                    } else {
                        mostrarError("Error", "No se pudo eliminar el turno.");
                    }
                } catch (DAOException e) {
                    mostrarError("Error", "Error al eliminar el turno: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleEliminarAsignacion() {
        PerteneceTurno asignacionSeleccionada = asignacionesTable.getSelectionModel().getSelectedItem();
        if (asignacionSeleccionada != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro que desea eliminar esta asignación?");
            confirmacion.setContentText("Esta acción eliminará la asignación del turno para el empleado " +
                    asignacionSeleccionada.getEmpleado().getNombre() + " " +
                    asignacionSeleccionada.getEmpleado().getApellido() +
                    " en la fecha " + asignacionSeleccionada.getFecha());

            Optional<ButtonType> result = confirmacion.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    if (perteneceTurnoDAO.delete(asignacionSeleccionada)) {
                        asignaciones.remove(asignacionSeleccionada);
                        mostrarInformacion("Éxito", "La asignación ha sido eliminada correctamente.");
                    } else {
                        mostrarError("Error", "No se pudo eliminar la asignación.");
                    }
                } catch (DAOException e) {
                    mostrarError("Error", "Error al eliminar la asignación: " + e.getMessage());
                }
            }
        }
    }
    
    private void cargarTurnos() {
        try {
            turnos.clear();
            turnos.addAll(turnoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los turnos: " + e.getMessage());
        }

    }

    private void cargarAsignaciones() {
        try {
            asignaciones.clear();
            if (fechaFiltro.getValue() != null) {
                asignaciones.addAll(perteneceTurnoDAO.findByFecha(fechaFiltro.getValue()));
            } else {
                // Cargar todas las asignaciones o las más recientes
                LocalDate hoy = LocalDate.now();
                asignaciones.addAll(perteneceTurnoDAO.findAll());
            }
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar las asignaciones: " + e.getMessage());
        }
    }


    @FXML
    private void handleNuevoTurno() {
        abrirDialogoTurno(null);
    }




    @FXML
    private void handleFiltrarAsignaciones() {
        cargarAsignaciones();
    }

    @FXML
    private void handleLimpiarFiltro() {
        fechaFiltro.setValue(null);
        cargarAsignaciones();
    }

    @FXML
    private void handleAsignarTurno() {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("asignar-turno-dialog.fxml"));
            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Asignar Turno");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(turnosTable.getScene().getWindow());
            dialogStage.setScene(scene);
            // Establecer tamaño inicial
            dialogStage.setWidth(400);
            dialogStage.setHeight(300);


            AsignarTurnoDialogController controller = loader.getController();
            dialogStage.showAndWait();

            if (controller.isAsignacionExitosa()) {
                mostrarInformacion("Éxito", "Turno asignado correctamente");
                cargarAsignaciones(); // Recargar las asignaciones
            }
        } catch (IOException e) {
            mostrarError("Error", "Error al abrir el diálogo de asignación de turno");
        }


    }

    private void abrirDialogoTurno(Turno turno) {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("turno-dialog.fxml"));
            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle(turno == null ? "Nuevo Turno" : "Editar Turno");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(turnosTable.getScene().getWindow());
            dialogStage.setScene(scene);

            TurnoDialogController controller = loader.getController();
            controller.setTurno(turno);
            dialogStage.showAndWait();

            if (controller.isGuardadoExitoso()) {
                cargarTurnos(); // Recargar la tabla
            }
        } catch (IOException e) {
            mostrarError("Error", "Error al abrir el diálogo de turno");
        }
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