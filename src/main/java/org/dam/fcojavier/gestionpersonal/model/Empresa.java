package org.dam.fcojavier.gestionpersonal.model;

import java.util.Objects;

/**
 * Clase que representa una empresa en el sistema de gestión de personal.
 * Contiene la información básica de una empresa y se corresponde con la tabla 'empresa'
 * en la base de datos. Cada empresa puede tener múltiples empleados asociados.
 *
 */
public class Empresa {
    /** Identificador único de la empresa en la base de datos */
    private int idEmpresa;
    
    /** Nombre comercial de la empresa (debe ser único) */
    private String nombre;
    
    /** Dirección física de la empresa */
    private String direccion;
    
    /** Número de teléfono de contacto (debe ser único) */
    private String telefono;
    
    /** Dirección de correo electrónico (debe ser único) */
    private String email;
    
    /** Contraseña hasheada para autenticación */
    private String password;

    /**
     * Constructor por defecto.
     * Crea una nueva instancia de Empresa sin inicializar sus campos.
     */
    public Empresa() {
    }

    /**
     * Constructor que inicializa todos los campos de la empresa excepto el ID.
     * El ID se asignará automáticamente por la base de datos.
     *
     * @param nombre Nombre comercial de la empresa
     * @param direccion Dirección física de la empresa
     * @param telefono Número de teléfono de contacto
     * @param email Dirección de correo electrónico
     * @param password Contraseña hasheada para autenticación
     */
    public Empresa(String nombre, String direccion, String telefono, String email, String password) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.password = password;
    }

    /**
     * @return El identificador único de la empresa
     */
    public int getIdEmpresa() {
        return idEmpresa;
    }

    /**
     * @param idEmpresa El nuevo identificador de la empresa
     */
    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    /**
     * @return El nombre comercial de la empresa
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre El nuevo nombre de la empresa
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return La dirección física de la empresa
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion La nueva dirección de la empresa
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return El número de teléfono de la empresa
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
     * @return La dirección de correo electrónico de la empresa
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email La nueva dirección de correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return La contraseña hasheada de la empresa
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password La nueva contraseña hasheada
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Compara esta empresa con otro objeto para determinar si son iguales.
     * La comparación se basa únicamente en el nombre de la empresa, ya que debe ser único.
     *
     * @param o El objeto a comparar
     * @return true si son la misma empresa, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Empresa empresa = (Empresa) o;
        return Objects.equals(nombre, empresa.nombre);
    }

    /**
     * Genera un código hash para esta empresa basado en su nombre.
     *
     * @return El código hash generado
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(nombre);
    }

    /**
     * Genera una representación en texto de la empresa incluyendo sus datos principales.
     * Por seguridad, no incluye la contraseña en la representación.
     *
     * @return Una cadena con los datos de la empresa
     */
    @Override
    public String toString() {
        return "Empresa{" +
                "idEmpresa=" + idEmpresa +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}