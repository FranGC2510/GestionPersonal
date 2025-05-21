package org.dam.fcojavier.gestionpersonal.exceptions;

import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
/**
 * Excepción personalizada para operaciones DAO
 */
public class DAOException extends Exception {
    private final DAOErrorTipo error;
    public DAOException(String message, DAOErrorTipo error) {
        super(message);
        this.error = error;
    }

    public DAOErrorTipo getError() {
        return error;
    }
}
