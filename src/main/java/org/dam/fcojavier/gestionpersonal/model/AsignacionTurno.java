package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalDate;
import java.util.Objects;

public class AsignacionTurno {
    private Empleado empleado;
    private Turno turno;
    private LocalDate fecha;

    public AsignacionTurno() {
    }

    public AsignacionTurno(Empleado empleado, Turno turno, LocalDate fecha) {
        this.empleado = empleado;
        this.turno = turno;
        this.fecha = fecha;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsignacionTurno that = (AsignacionTurno) o;
        return Objects.equals(empleado.getIdUsuario(), that.empleado.getIdUsuario()) &&
                Objects.equals(turno.getIdTurno(), that.turno.getIdTurno()) &&
                Objects.equals(fecha, that.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                empleado != null ? empleado.getIdUsuario() : null,
                turno != null ? turno.getIdTurno() : null,
                fecha
        );
    }

    @Override
    public String toString() {
        return "AsignacionTurno{" +
                "empleado=" + (empleado != null ? empleado.getIdUsuario() : "null") +
                ", turno=" + (turno != null ? turno.getIdTurno() : "null") +
                ", fecha=" + fecha +
                '}';
    }
}
