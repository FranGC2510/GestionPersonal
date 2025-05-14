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

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class GestionAusenciasController {
    @FXML
    private ComboBox<Empleado> empleadoFilterComboBox;
    @FXML
    private DatePicker fechaFilterDatePicker;
    @FXML
    private TableView<Ausencia> ausenciasTable;
    @FXML
    private TableColumn<Ausencia, String> empleadoColumn;
    @FXML
    private TableColumn<Ausencia, String> motivoColumn;
    @FXML
    private TableColumn<Ausencia, LocalDate> fechaInicioColumn;
    @FXML
    private TableColumn<Ausencia, LocalDate> fechaFinColumn;
    @FXML
    private Button editarButton;
    @FXML
    private Button eliminarButton;


    private AusenciaDAO ausenciaDAO;
    private EmpleadoDAO empleadoDAO;
    private ObservableList<Ausencia> ausencias;
    
    @FXML
    public void initialize() {
        empleadoDAO = new EmpleadoDAO();
        ausenciaDAO = new AusenciaDAO(empleadoDAO);
        ausencias = FXCollections.observableArrayList();
        
        // Configurar el formato de visualización del ComboBox
        empleadoFilterComboBox.setCellFactory(param -> new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre() + " " + empleado.getApellido());
                }
            }
        });
        
        // Configurar cómo se muestra el item seleccionado
        empleadoFilterComboBox.setButtonCell(new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre() + " " + empleado.getApellido());
                }
            }
        });
        
        // Configurar columnas
        empleadoColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmpleado().getNombre() + 
                                   " " + cellData.getValue().getEmpleado().getApellido()));
        motivoColumn.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        fechaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fechaFinColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        
        // Cargar datos
        cargarAusencias();
        cargarEmpleados();
        
        // Configurar filtros
        configurarFiltros();
    }
    
    private void cargarAusencias() {
        try {
            ausencias.setAll(ausenciaDAO.findAll());
            ausenciasTable.setItems(ausencias);
        } catch (DAOException e) {
            mostrarError("Error al cargar ausencias", e.getMessage());
        }
    }

    private void cargarEmpleados() {
        try {
            ObservableList<Empleado> empleados = FXCollections.observableArrayList(empleadoDAO.findAll());
            empleadoFilterComboBox.setItems(empleados);
        } catch (DAOException e) {
            mostrarError("Error al cargar empleados", e.getMessage());
        }
    }

    private void configurarFiltros() {
        // Configurar filtro por empleado
        empleadoFilterComboBox.valueProperty().addListener((_, _, empleadoSeleccionado) -> aplicarFiltros());

        // Configurar filtro por fecha
        fechaFilterDatePicker.valueProperty().addListener((_, _, fechaSeleccionada) -> aplicarFiltros());

        // Habilitar/deshabilitar botones según selección
        ausenciasTable.getSelectionModel().selectedItemProperty().addListener((_, _, seleccion) -> {
            boolean haySeleccion = seleccion != null;
            editarButton.setDisable(!haySeleccion);
            eliminarButton.setDisable(!haySeleccion);
        });
    }

    private void aplicarFiltros() {
        ausenciasTable.setItems(ausencias.filtered(ausencia -> {
            boolean cumpleFiltroEmpleado = empleadoFilterComboBox.getValue() == null ||
                    ausencia.getEmpleado().equals(empleadoFilterComboBox.getValue());

            boolean cumpleFiltroFecha = fechaFilterDatePicker.getValue() == null ||
                    ausencia.getFechaInicio().equals(fechaFilterDatePicker.getValue()) ||
                    (ausencia.getFechaFin() != null &&
                            !ausencia.getFechaInicio().isAfter(fechaFilterDatePicker.getValue()) &&
                            !ausencia.getFechaFin().isBefore(fechaFilterDatePicker.getValue()));

            return cumpleFiltroEmpleado && cumpleFiltroFecha;
        }));
    }


    @FXML
    private void handleNuevaAusencia() {
        mostrarDialogoAusencia(null);
    }
    
    @FXML
    private void handleEditarAusencia() {
        Ausencia ausenciaSeleccionada = ausenciasTable.getSelectionModel().getSelectedItem();
        if (ausenciaSeleccionada != null) {
            mostrarDialogoAusencia(ausenciaSeleccionada);
        }
    }

    private void mostrarDialogoAusencia(Ausencia ausencia) {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("editar-ausencias-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            EditarAusenciaController controller = loader.getController();
            controller.setDialogPane(dialogPane);
            
            // Cargar la lista de empleados
            try {
                ObservableList<Empleado> empleados = FXCollections.observableArrayList(empleadoDAO.findAll());
                controller.setEmpleados(empleados);
            } catch (DAOException e) {
                mostrarError("Error", "Error al cargar la lista de empleados: " + e.getMessage());
                return;
            }
        
            // Configurar el formato de visualización del ComboBox en el diálogo
            controller.configurarComboBoxEmpleados();
        
            // Establecer la ausencia (si existe)
            controller.setAusencia(ausencia);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(ausencia == null ? "Nueva Ausencia" : "Editar Ausencia");

        Optional<ButtonType> clickedButton = dialog.showAndWait();

        if (clickedButton.isPresent() && clickedButton.get() == ButtonType.OK) {
            Ausencia ausenciaModificada = controller.getAusencia();
            if (ausencia == null) {
                // Nueva ausencia
                Ausencia ausenciaInsertada = ausenciaDAO.insert(ausenciaModificada);
                if (ausenciaInsertada != null) {
                    ausencias.add(ausenciaInsertada);
                }
            } else {
                // Actualizar ausencia existente
                Ausencia ausenciaActualizada = ausenciaDAO.update(ausenciaModificada);
                if (ausenciaActualizada != null) {
                    int index = ausencias.indexOf(ausencia);
                    if (index >= 0) {
                        ausencias.set(index, ausenciaActualizada);
                    }
                }
            }
        }
    } catch (IOException | DAOException e) {
        mostrarError("Error", "Error al procesar la ausencia: " + e.getMessage());
    }
}


    @FXML
    private void handleEliminarAusencia() {
        Ausencia ausenciaSeleccionada = ausenciasTable.getSelectionModel().getSelectedItem();
        if (ausenciaSeleccionada != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar esta ausencia?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");
            
            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    ausenciaDAO.delete(ausenciaSeleccionada);
                    ausencias.remove(ausenciaSeleccionada);
                } catch (DAOException e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
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