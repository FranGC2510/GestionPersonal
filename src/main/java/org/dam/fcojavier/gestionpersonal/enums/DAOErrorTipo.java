package org.dam.fcojavier.gestionpersonal.enums;
/**
 * Tipos de errores que pueden ocurrir en operaciones DAO
 */
public enum DAOErrorTipo {
    NOT_FOUND("Entidad no encontrada"),
    INSERT_ERROR("Error en la inserción"),
    DELETE_ERROR("Error en la eliminacion"),
    UPDATE_ERROR("Error en la actualización"),
    DUPLICATE_KEY("Clave duplicada"),
    FOREIGN_KEY_VIOLATION("Violación de clave foránea"),
    CONNECTION_ERROR("Error de conexión"),
    UNKNOWN_ERROR("Error desconocido");

    private final String descripcion;
    DAOErrorTipo(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return descripcion;
    }
}
