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

public class GestionEmpleadosController {
    // Elementos FXML
    @FXML private TextField searchField;
    @FXML private TableView<Empleado> empleadosTable;
    @FXML private TableColumn<Empleado, String> nombreColumn;
    @FXML private TableColumn<Empleado, String> apellidoColumn;
    @FXML private TableColumn<Empleado, String> departamentoColumn;
    @FXML private TableColumn<Empleado, String> puestoColumn;
    @FXML private TableColumn<Empleado, String> telefonoColumn;
    @FXML private TableColumn<Empleado, String> emailColumn;
    @FXML private TableColumn<Empleado, Boolean> activoColumn;
    @FXML private TableColumn<Empleado, TipoEmpleado> rolColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private ComboBox<String> filtroComboBox;

    // Variables de clase
    private final EmpleadoDAO empleadoDAO;
    private final ObservableList<Empleado> empleados;
    private final FilteredList<Empleado> empleadosFiltrados;
    private Empresa empresaActual;
    private static final String RUTA_DIALOG = "/org/dam/fcojavier/gestionpersonal/editar-empleados-dialog.fxml";

    // Constructor
    public GestionEmpleadosController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.empleados = FXCollections.observableArrayList();
        this.empleadosFiltrados = new FilteredList<>(empleados, _ -> true);
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFiltros();
        configurarBusqueda();
        configurarSeleccion();
        cargarEmpleados();
    }

    private void configurarTabla() {
        empleadosTable.setItems(empleadosFiltrados);
        configurarColumnas();
        configurarColumnaActivo();
    }

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

    private void configurarFiltros() {
        filtroComboBox.getItems().addAll(
            "Nombre (A-Z)", "Nombre (Z-A)",
            "Departamento (A-Z)", "Departamento (Z-A)",
            "Rol",
            "Estado (Activos primero)", "Estado (Inactivos primero)"
        );
        filtroComboBox.setOnAction(event -> aplicarFiltro());
    }

    private void configurarBusqueda() {
        searchField.textProperty().addListener((_, _, newText) -> 
            aplicarFiltroBusqueda(newText.toLowerCase().trim()));
    }

    private void configurarSeleccion() {
        empleadosTable.getSelectionModel().selectedItemProperty().addListener(
            (_, _, newSelection) -> actualizarBotones(newSelection != null));
    }

    private void actualizarBotones(boolean habilitados) {
        editButton.setDisable(!habilitados);
        deleteButton.setDisable(!habilitados);
    }

    @FXML
    private void handleNuevoEmpleado() {
        try {
            mostrarDialogoEmpleado("Nuevo Empleado", "Crear un nuevo empleado", null);
        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar el diálogo de empleado");
        }
    }

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

    private void mostrarDialogoEmpleado(String titulo, String headerText, Empleado empleado) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_DIALOG));
        DialogPane dialogPane = loader.load();

        Dialog<ButtonType> dialog = crearDialogo(titulo, headerText, dialogPane);
        EditarEmpleadosController controller = configurarControlador(loader, dialogPane, empleado);

        dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> procesarResultadoDialog(controller, empleado));
    }

    private Dialog<ButtonType> crearDialogo(String titulo, String headerText, DialogPane dialogPane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(headerText);
        dialog.setDialogPane(dialogPane);
        return dialog;
    }

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

    private void procesarNuevoEmpleado(Empleado empleado) throws DAOException {
        empleadoDAO.insert(empleado);
        empleados.add(empleado);
    }

    private void procesarEmpleadoEditado(Empleado empleadoActualizado, 
                                       Empleado empleadoOriginal) throws DAOException {
        empleadoDAO.update(empleadoActualizado);
        int index = empleados.indexOf(empleadoOriginal);
        if (index >= 0) {
            empleados.set(index, empleadoActualizado);
        }
    }

    private boolean confirmarBorrado() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este empleado?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void aplicarFiltroBusqueda(String busqueda) {
        empleadosFiltrados.setPredicate(empleado -> 
            busqueda.isEmpty() || coincideConBusqueda(empleado, busqueda));
    }

    private boolean coincideConBusqueda(Empleado empleado, String busqueda) {
        return empleado.getNombre().toLowerCase().contains(busqueda) ||
               empleado.getApellido().toLowerCase().contains(busqueda) ||
               empleado.getDepartamento().toLowerCase().contains(busqueda) ||
               empleado.getPuesto().toLowerCase().contains(busqueda) ||
               empleado.getEmail().toLowerCase().contains(busqueda);
    }

    private void aplicarFiltro() {
        String filtroSeleccionado = filtroComboBox.getValue();
        if (filtroSeleccionado != null) {
            aplicarFiltroSeleccionado(filtroSeleccionado);
        }
    }

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

    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarEmpleados();
    }

    public void abrirDialogNuevoEmpleado() {
        handleNuevoEmpleado();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}