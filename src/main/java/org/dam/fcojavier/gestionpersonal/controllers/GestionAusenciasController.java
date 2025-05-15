package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dam.fcojavier.gestionpersonal.DAOs.AusenciaDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Ausencia;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class GestionAusenciasController {
    // Elementos FXML
    @FXML private ComboBox<Empleado> empleadoFilterComboBox;
    @FXML private DatePicker fechaFilterDatePicker;
    @FXML private TableView<Ausencia> ausenciasTable;
    @FXML private TableColumn<Ausencia, String> empleadoColumn;
    @FXML private TableColumn<Ausencia, String> motivoColumn;
    @FXML private TableColumn<Ausencia, LocalDate> fechaInicioColumn;
    @FXML private TableColumn<Ausencia, LocalDate> fechaFinColumn;
    @FXML private Button editarButton;
    @FXML private Button eliminarButton;

    // Variables de clase
    private final AusenciaDAO ausenciaDAO;
    private final EmpleadoDAO empleadoDAO;
    private final ObservableList<Ausencia> ausencias;
    private Empresa empresaActual;

    // Constructor
    public GestionAusenciasController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.ausenciaDAO = new AusenciaDAO(empleadoDAO);
        this.ausencias = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBoxEmpleados();
        configurarFiltros();
    }

    @FXML
    private void handleLimpiarFiltros() {
        empleadoFilterComboBox.setValue(null);
        fechaFilterDatePicker.setValue(null);
        aplicarFiltros();
    }

    private void configurarTabla() {
        configurarColumnasTabla();
        ausenciasTable.setItems(ausencias);
        configurarSeleccionTabla();
    }

    private void configurarColumnasTabla() {
        empleadoColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatearNombreEmpleado(cellData.getValue().getEmpleado())));
        motivoColumn.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        fechaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fechaFinColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
    }

    private void configurarSeleccionTabla() {
        ausenciasTable.getSelectionModel().selectedItemProperty().addListener(
            (_, _, seleccion) -> actualizarBotonesSegunSeleccion(seleccion != null));
    }

    private void actualizarBotonesSegunSeleccion(boolean haySeleccion) {
        editarButton.setDisable(!haySeleccion);
        eliminarButton.setDisable(!haySeleccion);
    }

    private void configurarComboBoxEmpleados() {
        configurarVisualizacionComboBox();
        configurarCeldaSeleccionadaComboBox();
    }

    private void configurarVisualizacionComboBox() {
        empleadoFilterComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        });
    }

    private void configurarCeldaSeleccionadaComboBox() {
        empleadoFilterComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        });
    }

    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    private void configurarFiltros() {
        empleadoFilterComboBox.valueProperty().addListener((_, _, _) -> aplicarFiltros());
        fechaFilterDatePicker.valueProperty().addListener((_, _, _) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        ausenciasTable.setItems(ausencias.filtered(this::cumpleFiltros));
    }

    private boolean cumpleFiltros(Ausencia ausencia) {
        return cumpleFiltroEmpleado(ausencia) && cumpleFiltroFecha(ausencia);
    }

    private boolean cumpleFiltroEmpleado(Ausencia ausencia) {
        return empleadoFilterComboBox.getValue() == null || 
               ausencia.getEmpleado().equals(empleadoFilterComboBox.getValue());
    }

    private boolean cumpleFiltroFecha(Ausencia ausencia) {
        LocalDate fechaFiltro = fechaFilterDatePicker.getValue();
        if (fechaFiltro == null) return true;

        return ausencia.getFechaInicio().equals(fechaFiltro) ||
               (ausencia.getFechaFin() != null &&
                !ausencia.getFechaInicio().isAfter(fechaFiltro) &&
                !ausencia.getFechaFin().isBefore(fechaFiltro));
    }

    private void cargarDatos() {
        cargarAusencias();
        cargarEmpleados();
    }

    private void cargarAusencias() {
        try {
            ausencias.setAll(ausenciaDAO.findByEmpresa(empresaActual.getIdEmpresa()));
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar las ausencias: " + e.getMessage());
        }
    }

    private void cargarEmpleados() {
        try {
            empleadoFilterComboBox.setItems(FXCollections.observableArrayList(
                    empleadoDAO.findByEmpresa(empresaActual)
            ));
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los empleados: " + e.getMessage());
        }
    }

    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarDatos();
    }

    @FXML
    private void handleNuevaAusencia() {
        mostrarDialogoAusencia("Nueva Ausencia", null);
    }

    @FXML
    private void handleEditarAusencia() {
        Ausencia ausenciaSeleccionada = ausenciasTable.getSelectionModel().getSelectedItem();
        if (ausenciaSeleccionada != null) {
            mostrarDialogoAusencia("Editar Ausencia", ausenciaSeleccionada);
        }
    }

    private void mostrarDialogoAusencia(String titulo, Ausencia ausencia) {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("editar-ausencias-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            EditarAusenciaController controller = configurarControladorDialog(loader, dialogPane, ausencia);
            Dialog<ButtonType> dialog = crearDialogo(titulo, dialogPane);

            dialog.showAndWait()
                  .filter(response -> response == ButtonType.OK)
                  .ifPresent(response -> procesarResultadoDialog(controller, ausencia));

        } catch (IOException e) {
            mostrarError("Error", "Error al cargar el diálogo: " + e.getMessage());
        }
    }

    private EditarAusenciaController configurarControladorDialog(FXMLLoader loader, DialogPane dialogPane, Ausencia ausencia) throws DAOException {
        EditarAusenciaController controller = loader.getController();
        controller.setDialogPane(dialogPane);
        controller.setEmpleados(FXCollections.observableArrayList(
                empleadoDAO.findByEmpresa(empresaActual)
        ));
        controller.configurarComboBoxEmpleados();
        if (ausencia != null) {
            controller.setAusencia(ausencia);
        }
        return controller;
    }

    private Dialog<ButtonType> crearDialogo(String titulo, DialogPane dialogPane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setDialogPane(dialogPane);
        return dialog;
    }

    private void procesarResultadoDialog(EditarAusenciaController controller, Ausencia ausenciaOriginal) {
        try {
            Ausencia ausenciaModificada = controller.getAusencia();
            if (ausenciaOriginal == null) {
                procesarNuevaAusencia(ausenciaModificada);
            } else {
                procesarAusenciaEditada(ausenciaModificada, ausenciaOriginal);
            }
        } catch (DAOException e) {
            mostrarError("Error", "Error al procesar la ausencia: " + e.getMessage());
        }
    }

    private void procesarNuevaAusencia(Ausencia ausencia) throws DAOException {
        Ausencia ausenciaInsertada = ausenciaDAO.insert(ausencia);
        if (ausenciaInsertada != null) {
            ausencias.add(ausenciaInsertada);
        }
    }

    private void procesarAusenciaEditada(Ausencia ausenciaModificada, Ausencia ausenciaOriginal) 
            throws DAOException {
        Ausencia ausenciaActualizada = ausenciaDAO.update(ausenciaModificada);
        if (ausenciaActualizada != null) {
            int index = ausencias.indexOf(ausenciaOriginal);
            if (index >= 0) {
                ausencias.set(index, ausenciaActualizada);
            }
        }
    }

    @FXML
    private void handleEliminarAusencia() {
        Ausencia ausenciaSeleccionada = ausenciasTable.getSelectionModel().getSelectedItem();
        if (ausenciaSeleccionada != null && confirmarEliminacion()) {
            eliminarAusencia(ausenciaSeleccionada);
        }
    }

    private boolean confirmarEliminacion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta ausencia?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void eliminarAusencia(Ausencia ausencia) {
        try {
            ausenciaDAO.delete(ausencia);
            ausencias.remove(ausencia);
        } catch (DAOException e) {
            mostrarError("Error al eliminar", e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}