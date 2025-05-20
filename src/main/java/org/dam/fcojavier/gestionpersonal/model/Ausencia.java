package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa una ausencia laboral de un empleado.
 * Contiene información sobre el período de ausencia, su motivo y el empleado asociado.
 * Esta clase se corresponde con la tabla 'ausencia' en la base de datos.
 *
 */
public class Ausencia {
    /** Identificador único de la ausencia en la base de datos */
    private int idAusencia;
    
    /** Motivo o razón de la ausencia */
    private String motivo;
    
    /** Fecha en la que comienza la ausencia */
    private LocalDate fechaInicio;
    
    /** Fecha en la que finaliza la ausencia (puede ser null para ausencias de un día) */
    private LocalDate fechaFin;
    
    /** Empleado al que corresponde la ausencia */
    private Empleado empleado;

    /**
     * Constructor por defecto.
     * Crea una nueva instancia de Ausencia sin inicializar sus campos.
     */
    public Ausencia() {}

    /**
     * Constructor que inicializa una ausencia con su motivo.
     * Los demás campos deberán establecerse mediante los métodos setter.
     *
     * @param motivo Motivo o razón de la ausencia
     */
    public Ausencia(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Obtiene el identificador único de la ausencia.
     *
     * @return El ID de la ausencia
     */
    public int getIdAusencia() {
        return idAusencia;
    }

    /**
     * Establece el identificador único de la ausencia.
     *
     * @param idAusencia El nuevo ID de la ausencia
     */
    public void setIdAusencia(int idAusencia) {
        this.idAusencia = idAusencia;
    }

    /**
     * Obtiene el motivo de la ausencia.
     *
     * @return El motivo de la ausencia
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Establece el motivo de la ausencia.
     *
     * @param motivo El nuevo motivo de la ausencia
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Obtiene la fecha de inicio de la ausencia.
     *
     * @return La fecha de inicio
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio de la ausencia.
     *
     * @param fechaInicio La nueva fecha de inicio
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha de fin de la ausencia.
     *
     * @return La fecha de fin, puede ser null si la ausencia es de un solo día
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha de fin de la ausencia.
     *
     * @param fechaFin La nueva fecha de fin
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene el empleado asociado a la ausencia.
     *
     * @return El empleado que registró la ausencia
     */
    public Empleado getEmpleado() {
        return empleado;
    }

    /**
     * Establece el empleado asociado a la ausencia.
     *
     * @param empleado El empleado que registra la ausencia
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    /**
     * Compara esta ausencia con otro objeto para determinar si son iguales.
     * La comparación se basa únicamente en el ID de la ausencia.
     *
     * @param o El objeto a comparar
     * @return true si son la misma ausencia, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ausencia ausencia = (Ausencia) o;
        return idAusencia == ausencia.idAusencia;
    }

    /**
     * Genera un código hash para esta ausencia basado en su ID.
     *
     * @return El código hash generado
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(idAusencia);
    }

    /**
     * Genera una representación en texto de la ausencia.
     * Incluye el ID y el motivo de la ausencia.
     *
     * @return Una cadena que representa la ausencia
     */
    @Override
    public String toString() {
        return "Ausencia{" +
                "idAusencia=" + idAusencia +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}