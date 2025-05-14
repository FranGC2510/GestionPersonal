package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalDate;
import java.util.Objects;

public class Ausencia {
    private int idAusencia;
    private String motivo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Empleado empleado;


    public Ausencia() {}

    /**
     * Constructor con todos los campos necesarios.
     * @param motivo Motivo de la ausencia
     */
    public Ausencia(String motivo) {
        this.motivo = motivo;
    }

    public int getIdAusencia() {
        return idAusencia;
    }

    public void setIdAusencia(int idAusencia) {
        this.idAusencia = idAusencia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ausencia ausencia = (Ausencia) o;
        return idAusencia == ausencia.idAusencia;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idAusencia);
    }

    @Override
    public String toString() {
        return "Ausencia{" +
                "idAusencia=" + idAusencia +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
