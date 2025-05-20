package org.dam.fcojavier.gestionpersonal.utils;

import org.dam.fcojavier.gestionpersonal.enums.TipoSesion;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

/**
 * Clase que implementa el patrón Singleton para gestionar la sesión del usuario en el sistema.
 * Permite manejar dos tipos de sesiones: Empresa y Empleado, asegurando que solo puede
 * haber una sesión activa a la vez.
 *
 * @author [Tu nombre]
 * @version 1.0
 */
public class UsuarioSesion {
    /** Instancia única de la clase (patrón Singleton) */
    private static UsuarioSesion instance;
    
    /** Almacena la empresa que ha iniciado sesión, si la hay */
    private Empresa empresaLogueada;
    
    /** Almacena el empleado que ha iniciado sesión, si lo hay */
    private Empleado empleadoLogueado;
    
    /** Indica el tipo de sesión actual (EMPRESA, EMPLEADO o NINGUNA) */
    private TipoSesion tipoSesion;

    /**
     * Constructor privado que inicializa el tipo de sesión como NINGUNA.
     * Es privado para implementar el patrón Singleton.
     */
    private UsuarioSesion() {
        this.tipoSesion = TipoSesion.NINGUNA;
    }

    /**
     * Obtiene la instancia única de UsuarioSesion.
     * Si no existe, la crea.
     *
     * @return La instancia única de UsuarioSesion
     */
    public static UsuarioSesion getInstance() {
        if (instance == null) {
            instance = new UsuarioSesion();
        }
        return instance;
    }

    /**
     * Inicia sesión como empresa.
     * Cierra cualquier sesión anterior que pudiera estar activa.
     *
     * @param empresa La empresa que va a iniciar sesión
     */
    public void loginEmpresa(Empresa empresa) {
        logout();
        this.empresaLogueada = empresa;
        this.tipoSesion = TipoSesion.EMPRESA;
    }

    /**
     * Inicia sesión como empleado.
     * Cierra cualquier sesión anterior que pudiera estar activa.
     *
     * @param empleado El empleado que va a iniciar sesión
     */
    public void loginEmpleado(Empleado empleado) {
        logout();
        this.empleadoLogueado = empleado;
        this.tipoSesion = TipoSesion.EMPLEADO;
    }

    /**
     * Cierra la sesión actual, eliminando cualquier referencia a empresa o empleado
     * y estableciendo el tipo de sesión como NINGUNA.
     */
    public void logout() {
        this.empresaLogueada = null;
        this.empleadoLogueado = null;
        this.tipoSesion = TipoSesion.NINGUNA;
    }

    /**
     * Obtiene la empresa que tiene la sesión iniciada.
     *
     * @return La empresa con sesión activa o null si no hay sesión de empresa
     */
    public Empresa getEmpresaLogueada() {
        return empresaLogueada;
    }

    /**
     * Obtiene el empleado que tiene la sesión iniciada.
     *
     * @return El empleado con sesión activa o null si no hay sesión de empleado
     */
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }

    /**
     * Obtiene el tipo de sesión actual.
     *
     * @return El tipo de sesión actual (EMPRESA, EMPLEADO o NINGUNA)
     */
    public TipoSesion getTipoSesion() {
        return tipoSesion;
    }

    /**
     * Verifica si hay alguna sesión activa.
     *
     * @return true si hay una sesión activa, false en caso contrario
     */
    public boolean isLoggedIn() {
        return tipoSesion != TipoSesion.NINGUNA;
    }

    /**
     * Verifica si hay una sesión activa de empresa.
     *
     * @return true si hay una sesión de empresa activa, false en caso contrario
     */
    public boolean isEmpresaLoggedIn() {
        return tipoSesion == TipoSesion.EMPRESA;
    }

    /**
     * Verifica si hay una sesión activa de empleado.
     *
     * @return true si hay una sesión de empleado activa, false en caso contrario
     */
    public boolean isEmpleadoLoggedIn() {
        return tipoSesion == TipoSesion.EMPLEADO;
    }
}