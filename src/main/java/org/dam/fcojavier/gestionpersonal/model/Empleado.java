package org.dam.fcojavier.gestionpersonal.model;

import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;

import java.util.List;

public class Empleado {
    private int idEmpleado;
    private Empresa empresa;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String passwordHash;
    private String departamento;
    private boolean activo;
    private String puesto;
    private TipoEmpleado rol;

    public Empleado() {
    }

    public Empleado(Empresa empresa, String nombre, String apellidos,
                    String telefono, String email, String passwordHash) {
        this.empresa = empresa;
        this.nombre = nombre;
        this.apellido = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.passwordHash = passwordHash;
        this.activo = true; // Por defecto los empleados están activos
        this.rol = TipoEmpleado.EMPLEADO;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public TipoEmpleado getRol() {
        return rol;
    }

    public void setRol(TipoEmpleado rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "idUsuario=" + idEmpleado +
                ", empresa=" + (empresa != null ? empresa.getIdEmpresa() : "null") +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellido + '\'' +
                ", departamento='" + departamento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", puesto='" + puesto + '\'' +
                '}';
    }
}
