package org.dam.fcojavier.gestionpersonal.model;

import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import java.util.Objects;

/**
 * Clase que representa a un empleado en el sistema de gestión de personal.
 * Contiene toda la información relacionada con un empleado de una empresa,
 * incluyendo sus datos personales, información de contacto y rol en la empresa.
 * Esta clase se corresponde con la tabla 'empleado' en la base de datos.
 *
 */
public class Empleado {
    /** Identificador único del empleado en la base de datos */
    private int idEmpleado;
    
    /** Empresa a la que pertenece el empleado */
    private Empresa empresa;
    
    /** Nombre del empleado */
    private String nombre;
    
    /** Apellidos del empleado */
    private String apellido;
    
    /** Número de teléfono de contacto */
    private String telefono;
    
    /** Dirección de correo electrónico */
    private String email;
    
    /** Departamento al que está asignado el empleado */
    private String departamento;
    
    /** Indica si el empleado está activo en la empresa */
    private boolean activo;
    
    /** Puesto que ocupa el empleado en la empresa */
    private String puesto;
    
    /** Rol del empleado (SUPERVISOR o EMPLEADO) */
    private TipoEmpleado rol;

    /**
     * Constructor por defecto.
     * Crea una nueva instancia de Empleado sin inicializar sus campos.
     */
    public Empleado() {
    }

    /**
     * Constructor que inicializa un empleado con sus datos básicos.
     * Por defecto, establece el empleado como activo y con rol EMPLEADO.
     *
     * @param empresa La empresa a la que pertenece el empleado
     * @param nombre Nombre del empleado
     * @param apellidos Apellidos del empleado
     * @param telefono Número de teléfono
     * @param email Dirección de correo electrónico
     * @param passwordHash Hash de la contraseña (no se almacena)
     */
    public Empleado(Empresa empresa, String nombre, String apellidos,
                   String telefono, String email, String passwordHash) {
        this.empresa = empresa;
        this.nombre = nombre;
        this.apellido = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.activo = true;
        this.rol = TipoEmpleado.EMPLEADO;
    }

    // Getters y Setters

    /**
     * @return El identificador único del empleado
     */
    public int getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * @param idEmpleado El nuevo identificador del empleado
     */
    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * @return La empresa a la que pertenece el empleado
     */
    public Empresa getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa La nueva empresa del empleado
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * @return El nombre del empleado
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre El nuevo nombre del empleado
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return Los apellidos del empleado
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido Los nuevos apellidos del empleado
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * @return El número de teléfono del empleado
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono El nuevo número de teléfono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return El email del empleado
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email El nuevo email del empleado
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return El departamento al que pertenece el empleado
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * @param departamento El nuevo departamento del empleado
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * @return true si el empleado está activo, false en caso contrario
     */
    public boolean getActivo() {
        return activo;
    }

    /**
     * @param activo El nuevo estado de actividad del empleado
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return El puesto que ocupa el empleado
     */
    public String getPuesto() {
        return puesto;
    }

    /**
     * @param puesto El nuevo puesto del empleado
     */
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    /**
     * @return El rol del empleado (SUPERVISOR o EMPLEADO)
     */
    public TipoEmpleado getRol() {
        return rol;
    }

    /**
     * @param rol El nuevo rol del empleado
     */
    public void setRol(TipoEmpleado rol) {
        this.rol = rol;
    }

    /**
     * Genera una representación en texto del empleado incluyendo sus datos principales.
     * 
     * @return Una cadena con los datos del empleado
     */
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

    /**
     * Compara este empleado con otro objeto para determinar si son iguales.
     * La comparación se basa en el nombre, apellidos y email del empleado.
     *
     * @param o El objeto a comparar
     * @return true si son el mismo empleado, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Empleado empleado = (Empleado) o;
        return Objects.equals(nombre, empleado.nombre) && 
               Objects.equals(apellido, empleado.apellido) && 
               Objects.equals(email, empleado.email);
    }

    /**
     * Genera un código hash para este empleado basado en su nombre, apellidos y email.
     *
     * @return El código hash generado
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellido, email);
    }
}