package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalDate;

public class SupervisaEmpleado {
    private Empleado supervisor;
    private Empleado empleado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public SupervisaEmpleado(){}

    public SupervisaEmpleado(Empleado supervisor, Empleado empleado, LocalDate fechaInicio) {
        this.supervisor = supervisor;
        this.empleado = empleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = null;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Empleado supervisor) {
        this.supervisor = supervisor;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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

    @Override
    public String toString() {
        return "SupervisaEmpleado{" +
                "supervisor=" + supervisor +
                ", empleado=" + empleado +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                '}';
    }
}
