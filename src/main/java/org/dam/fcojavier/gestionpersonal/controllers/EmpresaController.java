package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.*;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.*;
import org.dam.fcojavier.gestionpersonal.utils.UsuarioSesion;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmpresaController {
    @FXML
    private Text empresaNombreText;
    @FXML
    private Text empleadosCantidadText;
    @FXML
    private Text ausenciasCantidadText;
    @FXML
    private Text empleadosTrabajandoText;

    private Empresa empresa;

    public void initialize() {
        actualizarDashboard();
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        actualizarDashboard();
    }

    private void actualizarDashboard() {
        if (empresa != null) {
            empresaNombreText.setText(empresa.getNombre());
            actualizarContadores();
        }
    }

    private void actualizarContadores() {
        try {
            actualizarContadorEmpleados();
            actualizarEmpleadosTrabajando();
            actualizarContadorAusencias();
        } catch (DAOException e) {
            mostrarError("Error al actualizar el dashboard", 
                        "No se pudieron cargar los datos actualizados.");
            establecerValoresPorDefecto();
        }
    }

    private void actualizarContadorEmpleados() throws DAOException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        List<Empleado> todosEmpleados = empleadoDAO.findAll();
        long cantidadEmpleados = todosEmpleados.stream()
                .filter(empleado -> empleado.getEmpresa().getIdEmpresa() == empresa.getIdEmpresa())
                .count();
        empleadosCantidadText.setText(String.valueOf(cantidadEmpleados));
    }

    private void actualizarEmpleadosTrabajando() throws DAOException {
        PerteneceTurnoDAO perteneceTurnoDAO = new PerteneceTurnoDAO();
        List<PerteneceTurno> turnosHoy = perteneceTurnoDAO.findByFecha(LocalDate.now());
        long empleadosTrabajando = turnosHoy.stream()
                .filter(pt -> pt.getEmpleado().getEmpresa().getIdEmpresa() == empresa.getIdEmpresa())
                .count();
        empleadosTrabajandoText.setText(String.valueOf(empleadosTrabajando));
    }

    private void actualizarContadorAusencias() throws DAOException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        AusenciaDAO ausenciaDAO = new AusenciaDAO(empleadoDAO);
        List<Ausencia> ausencias = ausenciaDAO.findAll();
        long ausenciasActivas = ausencias.stream()
                .filter(this::esAusenciaActiva)
                .count();
        ausenciasCantidadText.setText(String.valueOf(ausenciasActivas));
    }

    private boolean esAusenciaActiva(Ausencia ausencia) {
        try {
            return ausencia.getEmpleado().getEmpresa().getIdEmpresa() == empresa.getIdEmpresa() &&
                    ausencia.getFechaFin().isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        UsuarioSesion.getInstance().logout();
        volverAPantallaBienvenida();
    }

    private void volverAPantallaBienvenida() throws IOException {
        FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) empresaNombreText.getScene().getWindow();
        
        configurarVentanaPrincipal(stage);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void configurarVentanaPrincipal(Stage stage) {
        stage.setTitle("Gestión de Personal");
        stage.setWidth(1500);
        stage.setHeight(875);
    }

    @FXML
    private void handleGestionEmpleados() {
        try {
            abrirVentanaGestion("gestion-empleados-view.fxml", "Gestión de Empleados", 
                              controller -> ((GestionEmpleadosController)controller).setEmpresa(empresa));
        } catch (IOException e) {
            mostrarError("Error al abrir la gestión de empleados",
                        "Ha ocurrido un error al intentar abrir la ventana de gestión de empleados.");
        }
    }

    @FXML
    private void handleGestionTurnos() {
        try {
            abrirVentanaGestion("gestion-turnos-view.fxml", "Gestión de Turnos", 
                              controller -> ((GestionTurnosController)controller).setEmpresa(empresa));
        } catch (IOException e) {
            mostrarError("Error al abrir la gestión de turnos",
                        "Ha ocurrido un error al intentar abrir la ventana de gestión de turnos.");
        }
    }

    @FXML
    private void handleGestionAusencias() {
        try {
            abrirVentanaGestion(
                    "gestion-ausencias-view.fxml",
                    "Gestión de Ausencias",
                    controller -> ((GestionAusenciasController) controller).setEmpresa(empresa)
            );
        } catch (IOException e) {
            mostrarError("Error al abrir la gestión de ausencias",
                        "Ha ocurrido un error al intentar abrir la ventana.");
        }
    }

    @FXML
    private void handleEditarEmpresa() {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("editarEmpresa-dialog.fxml"));
            Scene scene = new Scene(loader.load());

            EditarEmpresaController controller = loader.getController();
            controller.setEmpresa(empresa);

            Stage dialogStage = crearDialogoModal("Editar empresa");
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.edicionCorrecta()) {
                empresa = controller.getEmpresa();
                actualizarDashboard();
            }
        } catch (IOException e) {
            mostrarError("Error", "Error al abrir el diálogo de edición");
        }
    }

    @FXML
    private void handleBorrarEmpresa() {
        try {
            if (tieneEmpleadosAsociados()) {
                mostrarErrorEmpresaConEmpleados();
                return;
            }

            if (confirmarBorradoEmpresa()) {
                borrarEmpresa();
                handleLogout();
            }
        } catch (DAOException | IOException e) {
            mostrarError("Error al eliminar la cuenta",
                        "No se pudo eliminar la cuenta. Por favor, inténtelo de nuevo.");
        }
    }

    private boolean tieneEmpleadosAsociados() throws DAOException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        return empleadoDAO.hayEmpleadosByEmpresa(empresa.getIdEmpresa());
    }

    private void mostrarErrorEmpresaConEmpleados() {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error al eliminar");
        error.setHeaderText("No se puede eliminar la empresa");
        error.setContentText("La empresa tiene empleados asociados. Debe eliminar todos los empleados antes de poder eliminar la empresa.");
        error.showAndWait();
    }

    private boolean confirmarBorradoEmpresa() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar su cuenta de empresa?");
        confirmacion.setContentText("Esta acción no se puede deshacer y eliminará todos los datos asociados a su empresa.");
        
        Optional<ButtonType> result = confirmacion.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void borrarEmpresa() throws DAOException {
        EmpresaDAO empresaDAO = new EmpresaDAO();
        empresaDAO.delete(empresa);
    }

    @FXML
    private void handleNuevoEmpleadoRapido() {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("gestion-empleados-view.fxml"));
            Scene scene = new Scene(loader.load());

            GestionEmpleadosController controller = loader.getController();
            controller.setEmpresa(empresa);

            Stage stage = crearDialogoModal("Gestión de Empleados");
            stage.setScene(scene);
            stage.show();
            
            controller.abrirDialogNuevoEmpleado();
        } catch (IOException e) {
            mostrarError("Error al abrir la gestión de empleados",
                        "Ha ocurrido un error al intentar abrir la ventana de gestión de empleados.");
        }
    }

    @FXML
    private void handleAsignarTurnoRapido() {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("gestion-turnos-view.fxml"));
            Scene scene = new Scene(loader.load());

            GestionTurnosController controller = loader.getController();
            controller.setEmpresa(empresa);

            Stage stage = crearDialogoModal("Gestión de Turnos");
            stage.setScene(scene);
            stage.show();
            
            controller.abrirDialogAsignarTurno();
        } catch (IOException e) {
            mostrarError("Error al abrir la gestión de turnos",
                        "Ha ocurrido un error al intentar abrir la ventana de gestión de turnos.");
        }
    }

    private Stage crearDialogoModal(String titulo) {
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(empresaNombreText.getScene().getWindow());
        return stage;
    }

    private void mostrarError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void establecerValoresPorDefecto() {
        empleadosCantidadText.setText("0");
        empleadosTrabajandoText.setText("0");
        ausenciasCantidadText.setText("0");
    }

    private void abrirVentanaGestion(String fxmlFile, String title, ControllerInitializer initializer) throws IOException {
        FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load());
        
        if (initializer != null) {
            initializer.initialize(loader.getController());
        }

        Stage stage = crearDialogoModal(title);
        stage.setScene(scene);
        stage.show();
    }

    @FunctionalInterface
    private interface ControllerInitializer {
        void initialize(Object controller);
    }
}