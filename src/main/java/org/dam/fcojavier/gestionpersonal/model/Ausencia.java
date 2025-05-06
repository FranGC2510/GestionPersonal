package org.dam.fcojavier.gestionpersonal.model;

import org.dam.fcojavier.gestionpersonal.enums.EstadoAusencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ausencia {
    private int idAusencia;
    private Empleado empleado;
    private Encargado encargado;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoAusencia estado;
    private String motivo;

    /**
     * Constructor por defecto. Inicializa estado como PENDIENTE.
     */
    public Ausencia() {
        this.estado = EstadoAusencia.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    /**
     * Constructor con todos los campos necesarios.
     * @param empleado Empleado que solicita la ausencia
     * @param fechaInicio Fecha de inicio de la ausencia
     * @param fechaFin Fecha de fin de la ausencia
     * @param motivo Motivo de la ausencia
     */
    public Ausencia(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin, String motivo) {
        this();
        this.empleado = empleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
    }

    public int getIdAusencia() {
        return idAusencia;
    }

    public void setIdAusencia(int idAusencia) {
        this.idAusencia = idAusencia;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Encargado getEncargado() {
        return encargado;
    }

    public void setEncargado(Encargado encargado) {
        this.encargado = encargado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoAusencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAusencia estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ausencia ausencia = (Ausencia) o;
        return Objects.equals(empleado, ausencia.empleado) && Objects.equals(fechaInicio, ausencia.fechaInicio) && Objects.equals(fechaFin, ausencia.fechaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empleado, fechaInicio, fechaFin);
    }

    /**
     * Calcula la duración de la ausencia en días
     * @return número de días entre fechaInicio y fechaFin (inclusive), o 0 si faltan las fechas
     */
    public long getDuracionDias() {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        // Se suma 1 porque queremos incluir ambos días
        return fechaFin.toEpochDay() - fechaInicio.toEpochDay() + 1;
    }

    @Override
    public String toString() {
        return "Ausencia{" +
                "idAusencia=" + idAusencia +
                ", empleado=" + (empleado != null ? empleado.getIdUsuario() : "null") +
                ", encargado=" + (encargado != null ? encargado.getIdUsuario() : "null") +
                ", fechaSolicitud=" + fechaSolicitud +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado=" + estado +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
