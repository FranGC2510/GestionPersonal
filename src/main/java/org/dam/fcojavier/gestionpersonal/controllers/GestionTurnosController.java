package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
import java.util.Optional;

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
    private TableColumn<Turno, Void> accionesColumn;
    @FXML
    private ComboBox<Empleado> empleadoComboBox;
    @FXML
    private DatePicker fechaAsignacion;

    private TurnoDAO turnoDAO;
    private PerteneceTurnoDAO perteneceTurnoDAO;
    private ObservableList<Turno> turnos;


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

        configurarColumnaBotones();
        cargarTurnos();
    }

    private void configurarColumnaBotones() {
        accionesColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editarBtn = new Button("Editar");
            private final Button eliminarBtn = new Button("Eliminar");
            private final HBox botones = new HBox(5, editarBtn, eliminarBtn);

            {
                editarBtn.setOnAction(event -> handleEditarTurno(getTableView().getItems().get(getIndex())));
                eliminarBtn.setOnAction(event -> handleEliminarTurno(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botones);
            }
        });
    }

    private void cargarTurnos() {
        try {
            turnos.clear();
            turnos.addAll(turnoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los turnos: " + e.getMessage());
        }

    }


    @FXML
    private void handleNuevoTurno() {
        abrirDialogoTurno(null);
    }

    private void handleEditarTurno(Turno turno) {
        abrirDialogoTurno(turno);
    }

    private void handleEliminarTurno(Turno turno) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este turno?");
        confirmacion.setContentText("Esta acción eliminará el turno '" + turno.getDescripcion() +
                "' y todas sus asignaciones a empleados.");

        Optional<ButtonType> result = confirmacion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (turnoDAO.delete(turno)) {
                    turnos.remove(turno);
                    mostrarInformacion("Éxito", "El turno ha sido eliminado correctamente.");
                } else {
                    mostrarError("Error", "No se pudo eliminar el turno.");
                }
            } catch (DAOException e) {
                mostrarError("Error", "Error al eliminar el turno: " + e.getMessage());
            }
        }

    }

    @FXML
    private void handleAsignarTurno() {
        // Implementar asignación de turno
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