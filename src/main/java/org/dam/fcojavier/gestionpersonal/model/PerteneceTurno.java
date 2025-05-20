package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa la asignación de un turno a un empleado en una fecha específica.
 * Esta clase implementa la relación muchos-a-muchos entre empleados y turnos, 
 * correspondiente a la tabla 'pertenece' en la base de datos.
 * La combinación de empleado, turno y fecha forma una clave primaria compuesta.
 *
 */
public class PerteneceTurno {
    /** Empleado al que se le asigna el turno */
    private Empleado empleado;
    
    /** Turno asignado */
    private Turno turno;
    
    /** Fecha para la cual se realiza la asignación */
    private LocalDate fecha;

    /**
     * Constructor por defecto.
     * Crea una nueva instancia de PerteneceTurno sin inicializar sus campos.
     */
    public PerteneceTurno() {
    }

    /**
     * Constructor que inicializa todos los campos de la asignación.
     *
     * @param empleado Empleado al que se asigna el turno
     * @param turno Turno que se asigna
     * @param fecha Fecha en la que se realiza la asignación
     */
    public PerteneceTurno(Empleado empleado, Turno turno, LocalDate fecha) {
        this.empleado = empleado;
        this.turno = turno;
        this.fecha = fecha;
    }

    /**
     * Obtiene el empleado asociado a esta asignación.
     *
     * @return El empleado al que se le asignó el turno
     */
    public Empleado getEmpleado() {
        return empleado;
    }

    /**
     * Establece el empleado para esta asignación.
     *
     * @param empleado El empleado al que se asignará el turno
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    /**
     * Obtiene el turno asignado.
     *
     * @return El turno asignado
     */
    public Turno getTurno() {
        return turno;
    }

    /**
     * Establece el turno para esta asignación.
     *
     * @param turno El turno que se asignará
     */
    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    /**
     * Obtiene la fecha de la asignación.
     *
     * @return La fecha en la que se realizó la asignación
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha para esta asignación.
     *
     * @param fecha La fecha en la que se realizará la asignación
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Compara esta asignación con otro objeto para determinar si son iguales.
     * La comparación se basa en los IDs del empleado y turno, junto con la fecha,
     * ya que forman una clave primaria compuesta en la base de datos.
     *
     * @param o El objeto a comparar
     * @return true si son la misma asignación, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerteneceTurno that = (PerteneceTurno) o;
        return Objects.equals(empleado.getIdEmpleado(), that.empleado.getIdEmpleado()) &&
                Objects.equals(turno.getIdTurno(), that.turno.getIdTurno()) &&
                Objects.equals(fecha, that.fecha);
    }

    /**
     * Genera un código hash para esta asignación basado en los IDs del empleado y turno,
     * y la fecha. Este método es consistente con equals().
     *
     * @return El código hash generado
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                empleado != null ? empleado.getIdEmpleado() : null,
                turno != null ? turno.getIdTurno() : null,
                fecha
        );
    }

    /**
     * Genera una representación en texto de la asignación.
     * Incluye los IDs del empleado y turno, y la fecha de la asignación.
     *
     * @return Una cadena que representa la asignación del turno
     */
    @Override
    public String toString() {
        return "AsignacionTurno{" +
                "empleado=" + (empleado != null ? empleado.getIdEmpleado() : "null") +
                ", turno=" + (turno != null ? turno.getIdTurno() : "null") +
                ", fecha=" + fecha +
                '}';
    }
}