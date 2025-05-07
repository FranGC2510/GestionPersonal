package org.dam.fcojavier.gestionpersonal.model;

import org.dam.fcojavier.gestionpersonal.enums.EstadoAusencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class SolicitaAusencia {
    private Empleado empleado;
    private Ausencia ausencia;
    private EstadoAusencia estado;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public SolicitaAusencia(Empleado empleado, Ausencia ausencia, EstadoAusencia estado, LocalDateTime fechaSolicitud, LocalDate fechaInicio, LocalDate fechaFin) {
        this.empleado = empleado;
        this.ausencia = ausencia;
        this.estado = EstadoAusencia.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Ausencia getAusencia() {
        return ausencia;
    }

    public void setAusencia(Ausencia ausencia) {
        this.ausencia = ausencia;
    }

    public EstadoAusencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAusencia estado) {
        this.estado = estado;
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SolicitaAusencia that = (SolicitaAusencia) o;
        return Objects.equals(empleado, that.empleado) && Objects.equals(ausencia, that.ausencia) && Objects.equals(fechaSolicitud, that.fechaSolicitud);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empleado, ausencia, fechaSolicitud);
    }

    @Override
    public String toString() {
        return "SolicitaAusencia{" +
                "empleado=" + empleado +
                ", ausencia=" + ausencia +
                ", estado=" + estado +
                ", fechaSolicitud=" + fechaSolicitud +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                '}';
    }
}
