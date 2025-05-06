package org.dam.fcojavier.gestionpersonal.model;

import java.util.ArrayList;
import java.util.List;

public class Encargado extends Empleado{
    private List<Empleado> empleadosSupervisa;

    public Encargado(Empresa empresa, String nombre, String apellidos,
                     String telefono, String email, String passwordHash) {
        super(empresa, nombre, apellidos, telefono, email, passwordHash);
        this.empleadosSupervisa = new ArrayList<>();
    }

    public List<Empleado> getEmpleadosSupervisa() {
        return empleadosSupervisa;
    }

    public void setEmpleadosSupervisa(ArrayList<Empleado> empleadosSupervisa) {
        this.empleadosSupervisa = empleadosSupervisa;
    }

    @Override
    public String toString() {
        return "Encargado{" + super.toString() +
                ", numeroEmpleados=" + empleadosSupervisa.size() +
                '}';
    }
}
