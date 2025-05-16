package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.dam.fcojavier.gestionpersonal.model.Turno;
import org.dam.fcojavier.gestionpersonal.DAOs.TurnoDAO;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Controlador para el diálogo de creación y edición de turnos.
 * Gestiona la interfaz de usuario para crear y modificar turnos,
 * validando el formato de los campos y la consistencia de los datos.
 */
public class TurnoDialogController {

    /** Campo para la descripción del turno */
    @FXML private TextField descripcionField;
    
    /** Campo para la hora de inicio del turno */
    @FXML private TextField horaInicioField;
    
    /** Campo para la hora de fin del turno */
    @FXML private TextField horaFinField;

    // Variables de clase
    private final TurnoDAO turnoDAO;
    private Turno turno;
    private boolean guardadoExitoso;

    // Constantes de error
    private static final String ERROR_DESCRIPCION_VACIA = "La descripción no puede estar vacía";
    private static final String ERROR_FORMATO_HORA = "El formato de hora debe ser HH:mm";
    private static final String ERROR_HORA_FIN = "La hora de fin debe ser posterior a la hora de inicio";
    private static final String ERROR_GUARDAR = "No se pudo guardar el turno";

    // Constructor
    public TurnoDialogController() {
        this.turnoDAO = new TurnoDAO();
        this.guardadoExitoso = false;
    }

    /**
     * Constructor que inicializa el DAO de turnos y las variables de estado.
     */

    /**
     * Inicializa el controlador configurando los validadores de campos.
     */
    @FXML
    public void initialize() {
        configurarValidadores();
    }

    /**
     * Configura los validadores en tiempo real para los campos del formulario.
     * Establece listeners para validar la descripción y el formato de las horas
     * mientras el usuario escribe.
     */
    private void configurarValidadores() {
        // Validación en tiempo real para la descripción
        descripcionField.textProperty().addListener((_, _, newValue) ->
                validarDescripcion(newValue));

        // Validación en tiempo real para las horas
        horaInicioField.textProperty().addListener((_, _, newValue) ->
                validarFormatoHora(horaInicioField, newValue));
        horaFinField.textProperty().addListener((_, _, newValue) ->
                validarFormatoHora(horaFinField, newValue));
    }

    /**
     * Valida que la descripción del turno no esté vacía.
     * Marca el campo con error si está vacío.
     *
     * @param descripcion La descripción a validar
     */
    private void validarDescripcion(String descripcion) {
        if (descripcion.trim().isEmpty()) {
            marcarCampoError(descripcionField, ERROR_DESCRIPCION_VACIA);
        } else {
            limpiarCampoError(descripcionField);
        }
    }

    /**
     * Valida que el formato de hora sea correcto (HH:mm).
     * Marca el campo con error si el formato es inválido.
     *
     * @param campo El campo de texto que contiene la hora
     * @param hora La hora a validar
     */
    private void validarFormatoHora(TextField campo, String hora) {
        try {
            if (!hora.isEmpty()) {
                LocalTime.parse(hora);
                limpiarCampoError(campo);
            }
        } catch (DateTimeParseException e) {
            marcarCampoError(campo, ERROR_FORMATO_HORA);
        }
    }

    /**
     * Marca visualmente un campo como error y muestra un mensaje.
     *
     * @param campo El campo de texto a marcar
     * @param mensaje El mensaje de error a mostrar
     */
    private void marcarCampoError(TextField campo, String mensaje) {
        campo.setStyle("-fx-border-color: red;");
        campo.setTooltip(new Tooltip(mensaje));
    }

    /**
     * Limpia los indicadores de error de un campo.
     *
     * @param campo El campo de texto a limpiar
     */
    private void limpiarCampoError(TextField campo) {
        campo.setStyle("");
        campo.setTooltip(null);
    }

    /**
     * Establece el turno a editar.
     * Si es null, se tratará como un nuevo turno.
     *
     * @param turno El turno a editar, o null para crear uno nuevo
     */
    public void setTurno(Turno turno) {
        this.turno = turno;
        if (turno != null) {
            cargarDatosTurno();
        }
    }

    /**
     * Carga los datos del turno existente en los campos del formulario.
     * Se llama cuando se está editando un turno existente.
     */
    private void cargarDatosTurno() {
        descripcionField.setText(turno.getDescripcion());
        horaInicioField.setText(turno.getHoraInicio().toString());
        horaFinField.setText(turno.getHoraFin().toString());
    }

    /**
     * Maneja el evento del botón guardar.
     * Valida los campos y guarda el turno si son válidos.
     */
    @FXML
    private void handleGuardar() {
        if (!validarFormularioCompleto()) return;

        try {
            Turno turnoResultado = turno == null ?
                    crearNuevoTurno() :
                    actualizarTurnoExistente();

            procesarResultado(turnoResultado);
        } catch (DAOException e) {
            mostrarError("Error", "Error al guardar el turno: " + e.getMessage());
        }
    }

    /**
     * Valida todos los campos del formulario antes de guardar.
     * Verifica que la descripción no esté vacía y que las horas sean válidas.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarFormularioCompleto() {
        if (descripcionField.getText().trim().isEmpty()) {
            mostrarError("Error", ERROR_DESCRIPCION_VACIA);
            return false;
        }

        try {
            LocalTime horaInicio = LocalTime.parse(horaInicioField.getText());
            LocalTime horaFin = LocalTime.parse(horaFinField.getText());

            if (horaFin.isBefore(horaInicio)) {
                mostrarError("Error", ERROR_HORA_FIN);
                return false;
            }
        } catch (DateTimeParseException e) {
            mostrarError("Error", ERROR_FORMATO_HORA);
            return false;
        }

        return true;
    }

    /**
     * Crea un nuevo turno con los datos del formulario.
     *
     * @return El nuevo turno creado y guardado en la base de datos
     * @throws DAOException Si ocurre un error al guardar en la base de datos
     */
    private Turno crearNuevoTurno() throws DAOException {
        Turno nuevoTurno = new Turno(
                descripcionField.getText(),
                LocalTime.parse(horaInicioField.getText()),
                LocalTime.parse(horaFinField.getText())
        );
        return turnoDAO.insert(nuevoTurno);
    }

    /**
     * Actualiza un turno existente con los datos del formulario.
     *
     * @return El turno actualizado y guardado en la base de datos
     * @throws DAOException Si ocurre un error al actualizar en la base de datos
     */
    private Turno actualizarTurnoExistente() throws DAOException {
        turno.setDescripcion(descripcionField.getText());
        turno.setHoraInicio(LocalTime.parse(horaInicioField.getText()));
        turno.setHoraFin(LocalTime.parse(horaFinField.getText()));
        return turnoDAO.update(turno);
    }

    /**
     * Procesa el resultado de la operación de guardado.
     * Si el turno se guardó correctamente, cierra la ventana.
     *
     * @param turnoResultado El turno resultante de la operación de guardado
     */
    private void procesarResultado(Turno turnoResultado) {
        if (turnoResultado != null) {
            guardadoExitoso = true;
            cerrarVentana();
        } else {
            mostrarError("Error", ERROR_GUARDAR);
        }
    }

    /**
     * Maneja el evento del botón cancelar.
     * Cierra la ventana sin realizar cambios.
     */
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param titulo El título del diálogo
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana actual del diálogo.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) descripcionField.getScene().getWindow();
        stage.close();
    }

    /**
     * Indica si el turno se guardó exitosamente.
     *
     * @return true si el turno se guardó correctamente
     */
    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
    }