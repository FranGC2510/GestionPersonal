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

/**
 * Controlador para la gestión de ausencias de empleados.
 * Permite listar, crear, editar y eliminar ausencias, así como
 * filtrar por empleado y fecha.
 */
public class GestionAusenciasController {
    /** Selector de empleado para filtrar */
    @FXML private ComboBox<Empleado> empleadoFilterComboBox;
    
    /** Selector de fecha para filtrar */
    @FXML private DatePicker fechaFilterDatePicker;
    
    /** Tabla principal de ausencias */
    @FXML private TableView<Ausencia> ausenciasTable;
    
    /** Columna para el nombre del empleado */
    @FXML private TableColumn<Ausencia, String> empleadoColumn;
    
    /** Columna para el motivo de la ausencia */
    @FXML private TableColumn<Ausencia, String> motivoColumn;
    
    /** Columna para la fecha de inicio */
    @FXML private TableColumn<Ausencia, LocalDate> fechaInicioColumn;
    
    /** Columna para la fecha de fin */
    @FXML private TableColumn<Ausencia, LocalDate> fechaFinColumn;
    
    /** Botón para editar ausencia */
    @FXML private Button editarButton;
    
    /** Botón para eliminar ausencia */
    @FXML private Button eliminarButton;

    /** DAO para acceder a los datos de ausencias */
    private final AusenciaDAO ausenciaDAO;
    
    /** DAO para acceder a los datos de empleados */
    private final EmpleadoDAO empleadoDAO;
    
    /** Lista observable de ausencias */
    private final ObservableList<Ausencia> ausencias;
    
    /** Empresa actual */
    private Empresa empresaActual;

    /**
     * Constructor del controlador.
     * Inicializa los DAOs y la lista observable de ausencias.
     */
    public GestionAusenciasController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.ausenciaDAO = new AusenciaDAO(empleadoDAO);
        this.ausencias = FXCollections.observableArrayList();
    }

    /**
     * Inicializa el controlador.
     * Configura la tabla, el combo box de empleados y los filtros.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBoxEmpleados();
        configurarFiltros();
    }

    /**
     * Maneja el evento de limpiar filtros.
     * Resetea todos los filtros aplicados.
     */
    @FXML
    private void handleLimpiarFiltros() {
        empleadoFilterComboBox.setValue(null);
        fechaFilterDatePicker.setValue(null);
        aplicarFiltros();
    }

    /**
     * Maneja el evento de crear nueva ausencia.
     */
    @FXML
    private void handleNuevaAusencia() {
        mostrarDialogoAusencia("Nueva Ausencia", null);
    }

    /**
     * Maneja el evento de editar ausencia.
     */
    @FXML
    private void handleEditarAusencia() {
        Ausencia ausenciaSeleccionada = ausenciasTable.getSelectionModel().getSelectedItem();
        if (ausenciaSeleccionada != null) {
            mostrarDialogoAusencia("Editar Ausencia", ausenciaSeleccionada);
        }
    }

    /**
     * Maneja el evento de eliminar ausencia.
     */
    @FXML
    private void handleEliminarAusencia() {
        Ausencia ausenciaSeleccionada = ausenciasTable.getSelectionModel().getSelectedItem();
        if (ausenciaSeleccionada != null && confirmarEliminacion()) {
            eliminarAusencia(ausenciaSeleccionada);
        }
    }

    /**
     * Establece la empresa actual y carga sus datos.
     *
     * @param empresa La empresa cuyos datos se mostrarán
     */
    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarDatos();
    }

    /**
     * Configura la tabla principal.
     */
    private void configurarTabla() {
        configurarColumnasTabla();
        ausenciasTable.setItems(ausencias);
        configurarSeleccionTabla();
    }

    /**
     * Configura las columnas de la tabla.
     */
    private void configurarColumnasTabla() {
        empleadoColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatearNombreEmpleado(cellData.getValue().getEmpleado())));
        motivoColumn.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        fechaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fechaFinColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
    }

    /**
     * Configura el listener de selección de la tabla.
     */
    private void configurarSeleccionTabla() {
        ausenciasTable.getSelectionModel().selectedItemProperty().addListener(
            (_, _, seleccion) -> actualizarBotonesSegunSeleccion(seleccion != null));
    }

    /**
     * Actualiza el estado de los botones según la selección.
     *
     * @param haySeleccion true si hay una fila seleccionada
     */
    private void actualizarBotonesSegunSeleccion(boolean haySeleccion) {
        editarButton.setDisable(!haySeleccion);
        eliminarButton.setDisable(!haySeleccion);
    }

    /**
     * Configura el ComboBox de empleados.
     */
    private void configurarComboBoxEmpleados() {
        configurarVisualizacionComboBox();
        configurarCeldaSeleccionadaComboBox();
    }

    /**
     * Configura la visualización de las celdas del ComboBox.
     */
    private void configurarVisualizacionComboBox() {
        empleadoFilterComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        });
    }

    /**
     * Configura la celda seleccionada del ComboBox.
     */
    private void configurarCeldaSeleccionadaComboBox() {
        empleadoFilterComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                setText(empty || empleado == null ? null : formatearNombreEmpleado(empleado));
            }
        });
    }

    /**
     * Formatea el nombre completo del empleado.
     *
     * @param empleado Empleado a formatear
     * @return Nombre completo formateado
     */
    private String formatearNombreEmpleado(Empleado empleado) {
        return empleado.getNombre() + " " + empleado.getApellido();
    }

    /**
     * Configura los listeners de los filtros.
     */
    private void configurarFiltros() {
        empleadoFilterComboBox.valueProperty().addListener((_, _, _) -> aplicarFiltros());
        fechaFilterDatePicker.valueProperty().addListener((_, _, _) -> aplicarFiltros());
    }

    /**
     * Aplica los filtros seleccionados a la tabla.
     */
    private void aplicarFiltros() {
        ausenciasTable.setItems(ausencias.filtered(this::cumpleFiltros));
    }

    /**
     * Verifica si una ausencia cumple con todos los filtros.
     *
     * @param ausencia Ausencia a verificar
     * @return true si cumple con los filtros
     */
    private boolean cumpleFiltros(Ausencia ausencia) {
        return cumpleFiltroEmpleado(ausencia) && cumpleFiltroFecha(ausencia);
    }

    /**
     * Verifica si una ausencia cumple con el filtro de empleado.
     *
     * @param ausencia Ausencia a verificar
     * @return true si cumple con el filtro
     */
    private boolean cumpleFiltroEmpleado(Ausencia ausencia) {
        return empleadoFilterComboBox.getValue() == null || 
               ausencia.getEmpleado().equals(empleadoFilterComboBox.getValue());
    }

    /**
     * Verifica si una ausencia cumple con el filtro de fecha.
     *
     * @param ausencia Ausencia a verificar
     * @return true si cumple con el filtro
     */
    private boolean cumpleFiltroFecha(Ausencia ausencia) {
        LocalDate fechaFiltro = fechaFilterDatePicker.getValue();
        if (fechaFiltro == null) return true;

        return ausencia.getFechaInicio().equals(fechaFiltro) ||
               (ausencia.getFechaFin() != null &&
                !ausencia.getFechaInicio().isAfter(fechaFiltro) &&
                !ausencia.getFechaFin().isBefore(fechaFiltro));
    }

    /**
     * Carga los datos iniciales.
     */
    private void cargarDatos() {
        cargarAusencias();
        cargarEmpleados();
    }

    /**
     * Carga las ausencias de la empresa.
     */
    private void cargarAusencias() {
        try {
            ausencias.setAll(ausenciaDAO.findByEmpresa(empresaActual.getIdEmpresa()));
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar las ausencias: " + e.getMessage());
        }
    }

    /**
     * Carga los empleados de la empresa.
     */
    private void cargarEmpleados() {
        try {
            empleadoFilterComboBox.setItems(FXCollections.observableArrayList(
                    empleadoDAO.findByEmpresa(empresaActual)
            ));
        } catch (DAOException e) {
            mostrarError("Error", "Error al cargar los empleados: " + e.getMessage());
        }
    }

    /**
     * Muestra el diálogo de edición/creación de ausencia.
     *
     * @param titulo Título del diálogo
     * @param ausencia Ausencia a editar, null si es nueva
     */
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

    /**
     * Configura el controlador del diálogo.
     *
     * @param loader Loader del FXML
     * @param dialogPane Panel del diálogo
     * @param ausencia Ausencia a editar
     * @return Controlador configurado
     * @throws DAOException Si hay error al cargar los empleados
     */
    private EditarAusenciaController configurarControladorDialog(FXMLLoader loader, 
                                                               DialogPane dialogPane, 
                                                               Ausencia ausencia) throws DAOException {
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

    /**
     * Crea un diálogo configurado.
     *
     * @param titulo Título del diálogo
     * @param dialogPane Panel del diálogo
     * @return Dialog configurado
     */
    private Dialog<ButtonType> crearDialogo(String titulo, DialogPane dialogPane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setDialogPane(dialogPane);
        return dialog;
    }

    /**
     * Procesa el resultado del diálogo.
     *
     * @param controller Controlador del diálogo
     * @param ausenciaOriginal Ausencia original o null si es nueva
     */
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

    /**
     * Procesa la creación de una nueva ausencia.
     *
     * @param ausencia Ausencia a crear
     * @throws DAOException Si hay error en la base de datos
     */
    private void procesarNuevaAusencia(Ausencia ausencia) throws DAOException {
        Ausencia ausenciaInsertada = ausenciaDAO.insert(ausencia);
        if (ausenciaInsertada != null) {
            ausencias.add(ausenciaInsertada);
        }
    }

    /**
     * Procesa la edición de una ausencia existente.
     *
     * @param ausenciaModificada Ausencia con datos actualizados
     * @param ausenciaOriginal Ausencia original
     * @throws DAOException Si hay error en la base de datos
     */
    private void procesarAusenciaEditada(Ausencia ausenciaModificada, 
                                       Ausencia ausenciaOriginal) throws DAOException {
        Ausencia ausenciaActualizada = ausenciaDAO.update(ausenciaModificada);
        if (ausenciaActualizada != null) {
            int index = ausencias.indexOf(ausenciaOriginal);
            if (index >= 0) {
                ausencias.set(index, ausenciaActualizada);
            }
        }
    }

    /**
     * Muestra diálogo de confirmación para eliminar.
     *
     * @return true si se confirma la eliminación
     */
    private boolean confirmarEliminacion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta ausencia?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Elimina una ausencia.
     *
     * @param ausencia Ausencia a eliminar
     */
    private void eliminarAusencia(Ausencia ausencia) {
        try {
            ausenciaDAO.delete(ausencia);
            ausencias.remove(ausencia);
        } catch (DAOException e) {
            mostrarError("Error al eliminar", e.getMessage());
        }
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
}