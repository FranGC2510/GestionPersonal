package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.AusenciaDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.PerteneceTurnoDAO;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.dam.fcojavier.gestionpersonal.model.*;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.utils.UsuarioSesion;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador principal para la vista de empresa.
 * Gestiona el dashboard principal y proporciona acceso a todas las funcionalidades
 * de gestión de la empresa, incluyendo empleados, turnos y ausencias.
 */
public class EmpresaController {
    /** Texto que muestra el nombre de la empresa */
    @FXML private Text empresaNombreText;
    
    /** Texto que muestra la cantidad total de empleados */
    @FXML private Text empleadosCantidadText;
    
    /** Texto que muestra la cantidad de ausencias activas */
    @FXML private Text ausenciasCantidadText;
    
    /** Texto que muestra la cantidad de empleados trabajando actualmente */
    @FXML private Text empleadosTrabajandoText;

    /** Empresa actual */
    private Empresa empresa;

    /**
     * Inicializa el controlador.
     * Actualiza el dashboard con los datos iniciales.
     */
    public void initialize() {
        actualizarDashboard();
    }

    /**
     * Establece la empresa actual y actualiza la interfaz.
     *
     * @param empresa La empresa a establecer
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        actualizarDashboard();
    }

    /**
     * Actualiza todos los elementos del dashboard.
     */
    private void actualizarDashboard() {
        if (empresa != null) {
            empresaNombreText.setText(empresa.getNombre());
            actualizarContadores();
        }
    }

    /**
     * Actualiza todos los contadores del dashboard.
     * En caso de error, establece valores por defecto.
     */
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

    /**
     * Actualiza el contador de empleados totales.
     *
     * @throws DAOException Si hay un error al acceder a los datos
     */
    private void actualizarContadorEmpleados() throws DAOException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        List<Empleado> todosEmpleados = empleadoDAO.findAll();
        long cantidadEmpleados = todosEmpleados.stream()
                .filter(empleado -> empleado.getEmpresa().getIdEmpresa() == empresa.getIdEmpresa())
                .count();
        empleadosCantidadText.setText(String.valueOf(cantidadEmpleados));
    }

    /**
     * Actualiza el contador de empleados trabajando actualmente.
     *
     * @throws DAOException Si hay un error al acceder a los datos
     */
    private void actualizarEmpleadosTrabajando() throws DAOException {
        PerteneceTurnoDAO perteneceTurnoDAO = new PerteneceTurnoDAO();
        List<PerteneceTurno> turnosHoy = perteneceTurnoDAO.findByFecha(LocalDate.now());
        long empleadosTrabajando = turnosHoy.stream()
                .filter(pt -> pt.getEmpleado().getEmpresa().getIdEmpresa() == empresa.getIdEmpresa())
                .count();
        empleadosTrabajandoText.setText(String.valueOf(empleadosTrabajando));
    }

    /**
     * Actualiza el contador de ausencias activas.
     *
     * @throws DAOException Si hay un error al acceder a los datos
     */
    private void actualizarContadorAusencias() throws DAOException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        AusenciaDAO ausenciaDAO = new AusenciaDAO(empleadoDAO);
        List<Ausencia> ausencias = ausenciaDAO.findAll();
        long ausenciasActivas = ausencias.stream()
                .filter(this::esAusenciaActiva)
                .count();
        ausenciasCantidadText.setText(String.valueOf(ausenciasActivas));
    }

    /**
     * Verifica si una ausencia está activa para la empresa actual.
     *
     * @param ausencia La ausencia a verificar
     * @return true si la ausencia está activa
     */
    private boolean esAusenciaActiva(Ausencia ausencia) {
        try {
            return ausencia.getEmpleado().getEmpresa().getIdEmpresa() == empresa.getIdEmpresa() &&
                    ausencia.getFechaFin().isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Maneja el evento de cierre de sesión.
     *
     * @throws IOException Si hay un error al cargar la vista de bienvenida
     */
    @FXML
    private void handleLogout() throws IOException {
        UsuarioSesion.getInstance().logout();
        volverAPantallaBienvenida();
    }

    /**
     * Navega a la pantalla de bienvenida.
     *
     * @throws IOException Si hay un error al cargar la vista
     */
    private void volverAPantallaBienvenida() throws IOException {
        FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) empresaNombreText.getScene().getWindow();
        
        configurarVentanaPrincipal(stage);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    /**
     * Configura la ventana principal con sus dimensiones por defecto.
     *
     * @param stage El Stage a configurar
     */
    private void configurarVentanaPrincipal(Stage stage) {
        stage.setTitle("Gestión de Personal");
        stage.setWidth(1500);
        stage.setHeight(875);
    }

    /**
     * Maneja el evento de apertura de la gestión de empleados.
     */
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

    /**
     * Maneja el evento de apertura de la gestión de turnos.
     */
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

    /**
     * Maneja el evento de apertura de la gestión de ausencias.
     */
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

    /**
     * Maneja el evento de edición de la empresa.
     */
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

    /**
     * Maneja el evento de eliminación de la empresa.
     */
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

    /**
     * Verifica si la empresa tiene empleados asociados.
     *
     * @return true si la empresa tiene empleados
     * @throws DAOException Si hay un error al acceder a los datos
     */
    private boolean tieneEmpleadosAsociados() throws DAOException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        return empleadoDAO.hayEmpleadosByEmpresa(empresa.getIdEmpresa());
    }

    /**
     * Muestra un diálogo de error cuando la empresa tiene empleados asociados.
     */
    private void mostrarErrorEmpresaConEmpleados() {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error al eliminar");
        error.setHeaderText("No se puede eliminar la empresa");
        error.setContentText("La empresa tiene empleados asociados. Debe eliminar todos los empleados antes de poder eliminar la empresa.");
        error.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación para el borrado de la empresa.
     *
     * @return true si el usuario confirma el borrado
     */
    private boolean confirmarBorradoEmpresa() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar su cuenta de empresa?");
        confirmacion.setContentText("Esta acción no se puede deshacer y eliminará todos los datos asociados a su empresa.");
        
        Optional<ButtonType> result = confirmacion.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Elimina la empresa de la base de datos.
     *
     * @throws DAOException Si hay un error al eliminar la empresa
     */
    private void borrarEmpresa() throws DAOException {
        EmpresaDAO empresaDAO = new EmpresaDAO();
        empresaDAO.delete(empresa);
    }

    /**
     * Maneja el evento de creación rápida de empleado.
     */
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

    /**
     * Maneja el evento de asignación rápida de turno.
     */
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

    /**
     * Crea un nuevo diálogo modal.
     *
     * @param titulo El título del diálogo
     * @return El Stage configurado como diálogo modal
     */
    private Stage crearDialogoModal(String titulo) {
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(empresaNombreText.getScene().getWindow());
        return stage;
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param header El encabezado del error
     * @param content El contenido del mensaje de error
     */
    private void mostrarError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Establece valores por defecto para los contadores.
     */
    private void establecerValoresPorDefecto() {
        empleadosCantidadText.setText("0");
        empleadosTrabajandoText.setText("0");
        ausenciasCantidadText.setText("0");
    }

    /**
     * Abre una ventana de gestión genérica.
     *
     * @param fxmlFile El archivo FXML a cargar
     * @param title El título de la ventana
     * @param initializer El inicializador del controlador
     * @throws IOException Si hay un error al cargar la vista
     */
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

    /**
     * Interfaz funcional para la inicialización de controladores.
     */
    @FunctionalInterface
    private interface ControllerInitializer {
        /**
         * Inicializa un controlador.
         *
         * @param controller El controlador a inicializar
         */
        void initialize(Object controller);
    }
}