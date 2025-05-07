package org.dam.fcojavier.gestionpersonal.model;

import java.util.Objects;

public class Ausencia {
    private int idAusencia;
    private String motivo;

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
