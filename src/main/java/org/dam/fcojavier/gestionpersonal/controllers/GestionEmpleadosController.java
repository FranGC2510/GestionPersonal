package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

import java.io.IOException;
import java.util.Optional;
import java.util.Comparator;

/**
 * Controlador para la gestión de empleados.
 * Proporciona funcionalidades para listar, crear, editar y eliminar empleados
 * de una empresa, así como funciones de búsqueda y filtrado.
 */
public class GestionEmpleadosController {
    /** Campo de búsqueda de empleados */
    @FXML private TextField searchField;
    
    /** Tabla principal de empleados */
    @FXML private TableView<Empleado> empleadosTable;
    
    /** Columna para el nombre del empleado */
    @FXML private TableColumn<Empleado, String> nombreColumn;
    
    /** Columna para el apellido del empleado */
    @FXML private TableColumn<Empleado, String> apellidoColumn;
    
    /** Columna para el departamento del empleado */
    @FXML private TableColumn<Empleado, String> departamentoColumn;
    
    /** Columna para el puesto del empleado */
    @FXML private TableColumn<Empleado, String> puestoColumn;
    
    /** Columna para el teléfono del empleado */
    @FXML private TableColumn<Empleado, String> telefonoColumn;
    
    /** Columna para el email del empleado */
    @FXML private TableColumn<Empleado, String> emailColumn;
    
    /** Columna para el estado activo/inactivo del empleado */
    @FXML private TableColumn<Empleado, Boolean> activoColumn;
    
    /** Columna para el rol del empleado */
    @FXML private TableColumn<Empleado, TipoEmpleado> rolColumn;
    
    /** Botón para editar empleado */
    @FXML private Button editButton;
    
    /** Botón para eliminar empleado */
    @FXML private Button deleteButton;
    
    /** Selector de filtros para la tabla */
    @FXML private ComboBox<String> filtroComboBox;

    /** DAO para acceder a los datos de empleados */
    private final EmpleadoDAO empleadoDAO;
    
    /** Lista observable de empleados */
    private final ObservableList<Empleado> empleados;
    
    /** Lista filtrada de empleados */
    private final FilteredList<Empleado> empleadosFiltrados;
    
    /** Empresa actual cuyos empleados se están gestionando */
    private Empresa empresaActual;
    
    /** Ruta al archivo FXML del diálogo de edición */
    private static final String RUTA_DIALOG = "/org/dam/fcojavier/gestionpersonal/editar-empleados-dialog.fxml";

    /**
     * Constructor del controlador.
     * Inicializa las estructuras de datos necesarias para la gestión de empleados.
     */
    public GestionEmpleadosController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.empleados = FXCollections.observableArrayList();
        this.empleadosFiltrados = new FilteredList<>(empleados, _ -> true);
    }

    /**
     * Inicializa el controlador.
     * Configura la tabla, los filtros y carga los empleados iniciales.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        configurarFiltros();
        configurarBusqueda();
        configurarSeleccion();
        cargarEmpleados();
    }

    /**
     * Configura la tabla principal de empleados.
     * Establece la fuente de datos y configura las columnas.
     */
    private void configurarTabla() {
        empleadosTable.setItems(empleadosFiltrados);
        configurarColumnas();
        configurarColumnaActivo();
    }

    /**
     * Configura las columnas de la tabla con sus respectivas propiedades.
     */
    private void configurarColumnas() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        puestoColumn.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        activoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    /**
     * Configura la columna de estado activo con formato personalizado.
     * Muestra "Activo" en verde o "Inactivo" en rojo según el estado.
     */
    private void configurarColumnaActivo() {
        activoColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                } else {
                    setText(activo ? "Activo" : "Inactivo");
                    setStyle(activo ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });
    }

    /**
     * Configura el combo box de filtros con las opciones de ordenamiento.
     */
    private void configurarFiltros() {
        filtroComboBox.getItems().addAll(
            "Nombre (A-Z)", "Nombre (Z-A)",
            "Departamento (A-Z)", "Departamento (Z-A)",
            "Rol",
            "Estado (Activos primero)", "Estado (Inactivos primero)"
        );
        filtroComboBox.setOnAction(event -> aplicarFiltro());
    }

    /**
     * Configura el campo de búsqueda para filtrar empleados.
     */
    private void configurarBusqueda() {
        searchField.textProperty().addListener((_, _, newText) -> 
            aplicarFiltroBusqueda(newText.toLowerCase().trim()));
    }

    /**
     * Configura el listener de selección de la tabla.
     * Habilita o deshabilita los botones según haya selección.
     */
    private void configurarSeleccion() {
        empleadosTable.getSelectionModel().selectedItemProperty().addListener(
            (_, _, newSelection) -> actualizarBotones(newSelection != null));
    }

    /**
     * Actualiza el estado de habilitación de los botones.
     *
     * @param habilitados true si los botones deben estar habilitados
     */
    private void actualizarBotones(boolean habilitados) {
        editButton.setDisable(!habilitados);
        deleteButton.setDisable(!habilitados);
    }

    /**
     * Maneja el evento de crear un nuevo empleado.
     * Muestra el diálogo de creación de empleado.
     */
    @FXML
    private void handleNuevoEmpleado() {
        try {
            mostrarDialogoEmpleado("Nuevo Empleado", "Crear un nuevo empleado", null);
        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar el diálogo de empleado");
        }
    }

    /**
     * Maneja el evento de editar un empleado existente.
     * Muestra el diálogo de edición con los datos del empleado seleccionado.
     */
    @FXML
    private void handleEditarEmpleado() {
        Empleado empleadoSeleccionado = empleadosTable.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            try {
                mostrarDialogoEmpleado("Editar Empleado", "Editar empleado existente", empleadoSeleccionado);
            } catch (IOException e) {
                mostrarError("Error", "No se pudo cargar el diálogo de empleado");
            }
        }
    }

    /**
     * Maneja el evento de eliminar un empleado.
     * Solicita confirmación antes de eliminar el empleado seleccionado.
     */
    @FXML
    private void handleBorrarEmpleado() {
        Empleado empleadoSeleccionado = empleadosTable.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null && confirmarBorrado()) {
            try {
                empleadoDAO.delete(empleadoSeleccionado);
                empleados.remove(empleadoSeleccionado);
            } catch (DAOException e) {
                mostrarError("Error al eliminar empleado", e.getMessage());
            }
        }
    }

    /**
     * Muestra el diálogo de edición/creación de empleado.
     *
     * @param titulo Título del diálogo
     * @param headerText Texto descriptivo del diálogo
     * @param empleado Empleado a editar, null si es nuevo
     * @throws IOException Si hay un error al cargar el FXML
     */
    private void mostrarDialogoEmpleado(String titulo, String headerText, Empleado empleado) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_DIALOG));
        DialogPane dialogPane = loader.load();

        Dialog<ButtonType> dialog = crearDialogo(titulo, headerText, dialogPane);
        EditarEmpleadosController controller = configurarControlador(loader, dialogPane, empleado);

        dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> procesarResultadoDialog(controller, empleado));
    }

    /**
     * Crea un diálogo configurado.
     *
     * @param titulo Título del diálogo
     * @param headerText Texto descriptivo
     * @param dialogPane Panel del diálogo
     * @return Dialog configurado
     */
    private Dialog<ButtonType> crearDialogo(String titulo, String headerText, DialogPane dialogPane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(headerText);
        dialog.setDialogPane(dialogPane);
        return dialog;
    }

    /**
     * Configura el controlador del diálogo de edición.
     *
     * @param loader Loader del FXML
     * @param dialogPane Panel del diálogo
     * @param empleado Empleado a editar
     * @return Controlador configurado
     */
    private EditarEmpleadosController configurarControlador(FXMLLoader loader, 
                                                          DialogPane dialogPane, 
                                                          Empleado empleado) {
        EditarEmpleadosController controller = loader.getController();
        controller.setDialogPane(dialogPane);
        controller.setEmpresa(empresaActual);
        if (empleado != null) {
            controller.setEmpleado(empleado);
        }
        return controller;
    }

    /**
     * Procesa el resultado del diálogo de edición/creación.
     *
     * @param controller Controlador del diálogo
     * @param empleadoOriginal Empleado original o null si es nuevo
     */
    private void procesarResultadoDialog(EditarEmpleadosController controller, Empleado empleadoOriginal) {
        try {
            Empleado empleadoResultante = controller.getEmpleado();
            if (empleadoOriginal == null) {
                procesarNuevoEmpleado(empleadoResultante);
            } else {
                procesarEmpleadoEditado(empleadoResultante, empleadoOriginal);
            }
        } catch (DAOException e) {
            mostrarError("Error al procesar empleado", e.getMessage());
        }
    }

    /**
     * Procesa la creación de un nuevo empleado.
     *
     * @param empleado Empleado a crear
     * @throws DAOException Si hay un error en la base de datos
     */
    private void procesarNuevoEmpleado(Empleado empleado) throws DAOException {
        empleadoDAO.insert(empleado);
        empleados.add(empleado);
    }

    /**
     * Procesa la actualización de un empleado existente.
     *
     * @param empleadoActualizado Empleado con datos actualizados
     * @param empleadoOriginal Empleado original
     * @throws DAOException Si hay un error en la base de datos
     */
    private void procesarEmpleadoEditado(Empleado empleadoActualizado, 
                                       Empleado empleadoOriginal) throws DAOException {
        empleadoDAO.update(empleadoActualizado);
        int index = empleados.indexOf(empleadoOriginal);
        if (index >= 0) {
            empleados.set(index, empleadoActualizado);
        }
    }

    /**
     * Muestra un diálogo de confirmación para eliminar un empleado.
     *
     * @return true si el usuario confirma la eliminación
     */
    private boolean confirmarBorrado() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este empleado?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Aplica el filtro de búsqueda a la lista de empleados.
     *
     * @param busqueda Texto de búsqueda
     */
    private void aplicarFiltroBusqueda(String busqueda) {
        empleadosFiltrados.setPredicate(empleado -> 
            busqueda.isEmpty() || coincideConBusqueda(empleado, busqueda));
    }

    /**
     * Verifica si un empleado coincide con el texto de búsqueda.
     *
     * @param empleado Empleado a verificar
     * @param busqueda Texto de búsqueda
     * @return true si el empleado coincide con la búsqueda
     */
    private boolean coincideConBusqueda(Empleado empleado, String busqueda) {
        return empleado.getNombre().toLowerCase().contains(busqueda) ||
               empleado.getApellido().toLowerCase().contains(busqueda) ||
               empleado.getDepartamento().toLowerCase().contains(busqueda) ||
               empleado.getPuesto().toLowerCase().contains(busqueda) ||
               empleado.getEmail().toLowerCase().contains(busqueda);
    }

    /**
     * Aplica el filtro seleccionado en el combo box.
     */
    private void aplicarFiltro() {
        String filtroSeleccionado = filtroComboBox.getValue();
        if (filtroSeleccionado != null) {
            aplicarFiltroSeleccionado(filtroSeleccionado);
        }
    }

    /**
     * Aplica el criterio de ordenamiento seleccionado.
     *
     * @param filtro Criterio de ordenamiento
     */
    private void aplicarFiltroSeleccionado(String filtro) {
        switch (filtro) {
            case "Nombre (A-Z)" -> empleados.sort(Comparator.comparing(Empleado::getNombre));
            case "Nombre (Z-A)" -> empleados.sort(Comparator.comparing(Empleado::getNombre).reversed());
            case "Departamento (A-Z)" -> empleados.sort(Comparator.comparing(Empleado::getDepartamento));
            case "Departamento (Z-A)" -> empleados.sort(Comparator.comparing(Empleado::getDepartamento).reversed());
            case "Rol" -> empleados.sort(Comparator.comparing(Empleado::getRol));
            case "Estado (Activos primero)" -> empleados.sort(Comparator.comparing(Empleado::getActivo).reversed());
            case "Estado (Inactivos primero)" -> empleados.sort(Comparator.comparing(Empleado::getActivo));
        }
    }

    /**
     * Carga los empleados de la empresa actual.
     */
    private void cargarEmpleados() {
        try {
            if (empresaActual != null) {
                empleados.setAll(empleadoDAO.findByEmpresa(empresaActual));
            } else {
                empleados.clear();
            }
        } catch (DAOException e) {
            mostrarError("Error al cargar empleados", e.getMessage());
        }
    }

    /**
     * Establece la empresa cuyos empleados se van a gestionar.
     *
     * @param empresa La empresa actual
     */
    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarEmpleados();
    }

    /**
     * Abre el diálogo para crear un nuevo empleado.
     * Este método es llamado externamente cuando se necesita crear un empleado
     * desde otra vista.
     */
    public void abrirDialogNuevoEmpleado() {
        handleNuevoEmpleado();
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