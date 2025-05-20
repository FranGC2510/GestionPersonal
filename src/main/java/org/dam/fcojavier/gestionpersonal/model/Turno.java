package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalTime;

/**
 * Clase que representa un turno de trabajo en el sistema de gestión de personal.
 * Define un período de tiempo con una hora de inicio y fin, junto con una descripción.
 * Esta clase se corresponde con la tabla 'turno' en la base de datos.
 *
 */
public class Turno {
    /** Identificador único del turno en la base de datos */
    private int idTurno;
    
    /** Descripción o nombre del turno */
    private String descripcion;
    
    /** Hora de inicio del turno */
    private LocalTime horaInicio;
    
    /** Hora de finalización del turno */
    private LocalTime horaFin;

    /**
     * Constructor por defecto.
     * Crea una nueva instancia de Turno sin inicializar sus campos.
     */
    public Turno() {
    }

    /**
     * Constructor que inicializa un turno con sus datos básicos.
     * El ID se asignará automáticamente por la base de datos.
     *
     * @param descripcion Descripción o nombre del turno
     * @param horaInicio Hora de inicio del turno
     * @param horaFin Hora de finalización del turno
     */
    public Turno(String descripcion, LocalTime horaInicio, LocalTime horaFin) {
        this.descripcion = descripcion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    /**
     * Obtiene el identificador único del turno.
     *
     * @return El ID del turno
     */
    public int getIdTurno() {
        return idTurno;
    }

    /**
     * Establece el identificador único del turno.
     *
     * @param idTurno El nuevo ID del turno
     */
    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    /**
     * Obtiene la descripción del turno.
     *
     * @return La descripción del turno
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del turno.
     *
     * @param descripcion La nueva descripción del turno
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la hora de inicio del turno.
     *
     * @return La hora de inicio
     */
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    /**
     * Establece la hora de inicio del turno.
     *
     * @param horaInicio La nueva hora de inicio
     */
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    /**
     * Obtiene la hora de finalización del turno.
     *
     * @return La hora de finalización
     */
    public LocalTime getHoraFin() {
        return horaFin;
    }

    /**
     * Establece la hora de finalización del turno.
     *
     * @param horaFin La nueva hora de finalización
     */
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Calcula la duración del turno en horas.
     * Si falta la hora de inicio o fin, devuelve 0.
     *
     * @return La duración en horas como número decimal
     */
    public double getDuracionHoras() {
        double duracionHoras;
        if (horaInicio == null || horaFin == null) {
            duracionHoras = 0.0;
        } else {
            int minutos = (horaFin.getHour() * 60 + horaFin.getMinute()) -
                    (horaInicio.getHour() * 60 + horaInicio.getMinute());
            duracionHoras = minutos / 60.0;
        }
        return duracionHoras;
    }

    /**
     * Genera una representación en texto del turno incluyendo todos sus campos.
     *
     * @return Una cadena con los datos del turno
     */
    @Override
    public String toString() {
        return "Turno{" +
                "idTurno=" + idTurno +
                ", descripcion='" + descripcion + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}