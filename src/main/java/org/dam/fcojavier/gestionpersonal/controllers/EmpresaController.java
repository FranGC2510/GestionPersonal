package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpleadoDAO;
import org.dam.fcojavier.gestionpersonal.DAOs.EmpresaDAO;
import org.dam.fcojavier.gestionpersonal.GestionPersonalApp;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empresa;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.dam.fcojavier.gestionpersonal.utils.UsuarioSesion;

import java.io.IOException;
import java.util.Optional;

public class EmpresaController {
    @FXML
    private Text empresaNombreText;
    @FXML
    private Text empleadosCantidadText;
    @FXML
    private Text ausenciasCantidadText;

    private Empresa empresa;

    public void initialize() {
        updateDashboard();
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        updateDashboard();
    }

    private void updateDashboard() {
        if (empresa != null) {
            empresaNombreText.setText(empresa.getNombre());
            // TODO: Implementar los contadores cuando tengamos las tablas correspondientes
            empleadosCantidadText.setText("0");
            ausenciasCantidadText.setText("0");
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        // Cerrar la sesión en el UsuarioSesion
        UsuarioSesion.getInstance().logout();

        // Volver a la pantalla de bienvenida
        FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) empresaNombreText.getScene().getWindow();

        // Restaurar el título y tamaño de la ventana
        stage.setTitle("Gestión de Personal");
        stage.setWidth(1500);  // Ancho original
        stage.setHeight(875); // Alto original

        // Cambiar la escena
        stage.setScene(scene);

        // Centrar la ventana
        stage.centerOnScreen();
    }

    @FXML
    private void handleGestionEmpleados() {
        try {
            // Cargar la vista de empleados
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("gestion-empleados-view.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtener el controlador y establecer la empresa
            GestionEmpleadosController controller = loader.getController();
            controller.setEmpresa(empresa);

            // Configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Gestión de Empleados");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(empresaNombreText.getScene().getWindow());
            stage.setScene(scene);

            // Mostrar la ventana
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al abrir la gestión de empleados");
            alert.setContentText("Ha ocurrido un error al intentar abrir la ventana de gestión de empleados.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleGestionTurnos() {
        try {
            // Cargar la vista de turnos
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("gestion-turnos-view.fxml"));
            Scene scene = new Scene(loader.load());

            // Configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Gestión de Turnos");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(empresaNombreText.getScene().getWindow());
            stage.setScene(scene);

            // Mostrar la ventana
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al abrir la gestión de turnos");
            alert.setContentText("Ha ocurrido un error al intentar abrir la ventana de gestión de turnos.");
            alert.showAndWait();
        }


    }

    @FXML
    private void handleGestionAusencias() {
        try {
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("gestion-ausencias-view.fxml"));
            Scene scene = new Scene(loader.load());
        
            Stage stage = new Stage();
            stage.setTitle("Gestión de Ausencias");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(empresaNombreText.getScene().getWindow());
            stage.setScene(scene);
        
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al abrir la gestión de ausencias");
            alert.setContentText("Ha ocurrido un error al intentar abrir la ventana.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleEditarEmpresa() {
        try {
            // Cargar el diálogo de edición
            FXMLLoader loader = new FXMLLoader(GestionPersonalApp.class.getResource("editarEmpresa-dialog.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtener el controlador y establecer la empresa
            EditarEmpresaController controller = loader.getController();
            controller.setEmpresa(empresa);

            // Configurar el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar empresa");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(empresaNombreText.getScene().getWindow());
            dialogStage.setScene(scene);



            // Mostrar el diálogo y esperar resultado
            dialogStage.showAndWait();

            // Si los cambios fueron guardados, actualizar la vista
            if (controller.edicionCorrecta()) {
                empresa = controller.getEmpresa();
                updateDashboard();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrarEmpresa() {
        try {
            // Primero verificamos si tiene empleados
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            if (empleadoDAO.findByEmpresa(empresa.getIdEmpresa())) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error al eliminar");
                error.setHeaderText("No se puede eliminar la empresa");
                error.setContentText("La empresa tiene empleados asociados. Debe eliminar todos los empleados antes de poder eliminar la empresa.");
                error.showAndWait();
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro que desea eliminar su cuenta de empresa?");
            confirmacion.setContentText("Esta acción no se puede deshacer y eliminará todos los datos asociados a su empresa, incluyendo empleados y registros de ausencias.");

            Optional<ButtonType> result = confirmacion.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Eliminar la empresa de la base de datos
                EmpresaDAO empresaDAO = new EmpresaDAO();
                empresaDAO.delete(empresa);

                // Cerrar sesión y volver a la pantalla de bienvenida
                handleLogout();
            }
        } catch (DAOException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al eliminar la cuenta");
            alert.setContentText("No se pudo eliminar la cuenta. Por favor, inténtelo de nuevo.");
            alert.showAndWait();

        }
    }
}