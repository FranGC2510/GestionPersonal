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
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Empleado> empleadosTable;

    @FXML
    private TableColumn<Empleado, String> nombreColumn;

    @FXML
    private TableColumn<Empleado, String> apellidoColumn;

    @FXML
    private TableColumn<Empleado, String> departamentoColumn;

    @FXML
    private TableColumn<Empleado, String> puestoColumn;

    @FXML
    private TableColumn<Empleado, String> telefonoColumn;

    @FXML
    private TableColumn<Empleado, String> emailColumn;

    @FXML
    private TableColumn<Empleado, Boolean> activoColumn;

    @FXML
    private TableColumn<Empleado, TipoEmpleado> rolColumn;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ComboBox<String> filtroComboBox;

    private EmpleadoDAO empleadoDAO;
    private ObservableList<Empleado> empleados;
    private FilteredList<Empleado> empleadosFiltrados;
    private Empresa empresaActual;

    public void setEmpresa(Empresa empresa) {
        this.empresaActual = empresa;
        cargarEmpleados();
    }

    @FXML
    public void initialize() {
        empleadoDAO = new EmpleadoDAO();
        empleados = FXCollections.observableArrayList();
        empleadosFiltrados = new FilteredList<>(empleados, _ -> true);
        empleadosTable.setItems(empleadosFiltrados);

        // Cargar empleados existentes
        cargarEmpleados();

        // Configurar las columnas de la tabla
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        puestoColumn.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        activoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // Personalizar la columna de estado (activo/inactivo)
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

        // Habilitar/deshabilitar botones según la selección
        empleadosTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            editButton.setDisable(!haySeleccion);
            deleteButton.setDisable(!haySeleccion);
        });

        // Configurar búsqueda en tiempo real
        searchField.textProperty().addListener((_, _, newText) -> {
            String busqueda = newText.toLowerCase().trim();
            empleadosFiltrados.setPredicate(empleado -> {
                if (busqueda.isEmpty()) {
                    return true;
                }
                return empleado.getNombre().toLowerCase().contains(busqueda) ||
                        empleado.getApellido().toLowerCase().contains(busqueda) ||
                        empleado.getDepartamento().toLowerCase().contains(busqueda) ||
                        empleado.getPuesto().toLowerCase().contains(busqueda) ||
                        empleado.getEmail().toLowerCase().contains(busqueda);
            });
        });

        // Configurar opciones de filtrado
        filtroComboBox.getItems().addAll(
            "Nombre (A-Z)",
            "Nombre (Z-A)",
            "Departamento (A-Z)",
            "Departamento (Z-A)",
            "Rol",
            "Estado (Activos primero)",
            "Estado (Inactivos primero)"
        );
    
        filtroComboBox.setOnAction(event -> aplicarFiltro());

        cargarEmpleados();
    }

    private void aplicarFiltro() {
        String filtroSeleccionado = filtroComboBox.getValue();
        if (filtroSeleccionado != null) {
            switch (filtroSeleccionado) {
                case "Nombre (A-Z)" -> empleados.sort(Comparator.comparing(Empleado::getNombre));
                case "Nombre (Z-A)" -> empleados.sort(Comparator.comparing(Empleado::getNombre).reversed());
                case "Departamento (A-Z)" -> empleados.sort(Comparator.comparing(Empleado::getDepartamento));
                case "Departamento (Z-A)" -> empleados.sort(Comparator.comparing(Empleado::getDepartamento).reversed());
                case "Rol" -> empleados.sort(Comparator.comparing(Empleado::getRol));
                case "Estado (Activos primero)" -> empleados.sort(Comparator.comparing(Empleado::getActivo).reversed());
                case "Estado (Inactivos primero)" -> empleados.sort(Comparator.comparing(Empleado::getActivo));
            }
        }
    }

    private void cargarEmpleados() {
        try {
            empleados.setAll(empleadoDAO.findAll());
        } catch (DAOException e) {
            mostrarError("Error al cargar empleados", e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleNuevoEmpleado() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Empleado");
        dialog.setHeaderText("Crear un nuevo empleado");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/dam/fcojavier/gestionpersonal/editar-empleados-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            EditarEmpleadosController controller = loader.getController();
            controller.setDialogPane(dialogPane);
            controller.setEmpresa(empresaActual);

            dialog.setDialogPane(dialogPane);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Empleado nuevoEmpleado = controller.getEmpleado();
                try {
                    empleadoDAO.insert(nuevoEmpleado);
                    empleados.add(nuevoEmpleado);
                } catch (DAOException e) {
                    mostrarError("Error al crear empleado", e.getMessage());
                }
            }

        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar el diálogo de empleado");
        }
    }

    @FXML
    private void handleEditarEmpleado() {
        Empleado empleadoSeleccionado = empleadosTable.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editar Empleado");
            dialog.setHeaderText("Editar empleado existente");

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/org/dam/fcojavier/gestionpersonal/editar-empleados-dialog.fxml"));
                DialogPane dialogPane = loader.load();

                EditarEmpleadosController controller = loader.getController();
                controller.setDialogPane(dialogPane);
                controller.setEmpleado(empleadoSeleccionado);
                controller.setEmpresa(empresaActual);

                dialog.setDialogPane(dialogPane);

                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Empleado empleadoActualizado = controller.getEmpleado();
                    try {
                        empleadoDAO.update(empleadoActualizado);
                        // Actualizar la lista
                        int index = empleados.indexOf(empleadoSeleccionado);
                        if (index >= 0) {
                            empleados.set(index, empleadoActualizado);
                        }
                    } catch (DAOException e) {
                        mostrarError("Error al actualizar empleado", e.getMessage());
                    }
                }

            } catch (IOException e) {
                mostrarError("Error", "No se pudo cargar el diálogo de empleado");
            }
        }
    }

    @FXML
    private void handleBorrarEmpleado() {
        Empleado empleadoSeleccionado = empleadosTable.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro de que desea eliminar este empleado?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");

            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    empleadoDAO.delete(empleadoSeleccionado);
                    empleados.remove(empleadoSeleccionado);
                } catch (DAOException e) {
                    mostrarError("Error al eliminar empleado", e.getMessage());
                }
            }
        }
    }
}