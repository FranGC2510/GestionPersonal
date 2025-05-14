package org.dam.fcojavier.gestionpersonal.utils;

import org.dam.fcojavier.gestionpersonal.enums.TipoSesion;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

public class UsuarioSesion {
    private static UsuarioSesion instance;
    private Empresa empresaLogueada;
    private Empleado empleadoLogueado;
    private TipoSesion tipoSesion;

    // Constructor privado para implementar Singleton
    private UsuarioSesion() {
        this.tipoSesion = TipoSesion.NINGUNA;

    }

    public static UsuarioSesion getInstance() {
        if (instance == null) {
            instance = new UsuarioSesion();
        }
        return instance;
    }

    public void loginEmpresa(Empresa empresa) {
        logout(); // Cerrar cualquier sesión activa
        this.empresaLogueada = empresa;
        this.tipoSesion = TipoSesion.EMPRESA;
    }

    public void loginEmpleado(Empleado empleado) {
        logout(); // Cerrar cualquier sesión activa
        this.empleadoLogueado = empleado;
        this.tipoSesion = TipoSesion.EMPLEADO;
    }

    public void logout() {
        this.empresaLogueada = null;
        this.empleadoLogueado = null;
        this.tipoSesion = TipoSesion.NINGUNA;
    }

    public Empresa getEmpresaLogueada() {
        return empresaLogueada;
    }

    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }

    public TipoSesion getTipoSesion() {
        return tipoSesion;
    }

    public boolean isLoggedIn() {
        return tipoSesion != TipoSesion.NINGUNA;
    }

    public boolean isEmpresaLoggedIn() {
        return tipoSesion == TipoSesion.EMPRESA;
    }

    public boolean isEmpleadoLoggedIn() {
        return tipoSesion == TipoSesion.EMPLEADO;
    }
}
