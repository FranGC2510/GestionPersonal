package org.dam.fcojavier.gestionpersonal.model;

import java.time.LocalTime;

public class Turno {
    private int idTurno;
    private String descripcion;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Turno() {
    }

    public Turno(String descripcion, LocalTime horaInicio, LocalTime horaFin) {
        this.descripcion = descripcion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Calcula la duración del turno en horas
     * @return duración en horas (con decimales), o 0 si falta horaInicio o horaFin
     */
    public double getDuracionHoras() {
        double duracionHoras;
        if (horaInicio == null || horaFin == null) {
            duracionHoras = 0.0;
        }else{
            int minutos = (horaFin.getHour() * 60 + horaFin.getMinute()) -
                    (horaInicio.getHour() * 60 + horaInicio.getMinute());
            duracionHoras = minutos / 60.0;
        }

        return duracionHoras;
    }

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
