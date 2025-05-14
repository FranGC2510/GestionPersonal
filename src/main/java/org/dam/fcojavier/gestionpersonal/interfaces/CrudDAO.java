package org.dam.fcojavier.gestionpersonal.interfaces;

import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;

import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD básicas.
 * Todos los métodos implementan lazy loading por defecto, cargando solo la entidad principal
 * sin sus relaciones. Las implementaciones específicas pueden ofrecer métodos adicionales
 * para carga eager cuando sea necesario.
 *
 * @param <T> tipo de entidad que maneja el DAO
 */
public interface CrudDAO<T> {
    /**
     * Inserta una nueva entidad
     * @param objeto entidad a insertar
     * @return la entidad insertada con su ID generado
     * @throws DAOException si hay error en la operación
     */
    T insert(T objeto) throws DAOException;

    /**
     * Actualiza una entidad existente
     * @param objeto entidad a actualizar
     * @return la entidad actualizada
     * @throws DAOException si hay error en la operación o la entidad no existe
     */
    T update(T objeto) throws DAOException;

    /**
     * Elimina una entidad por su ID
     * @param objeto entidad a eliminar
     * @return true si la entidad fue eliminada, false si no existía
     * @throws DAOException si hay error en la operación
     */
    boolean delete(T objeto) throws DAOException;

    /**
     * Busca una entidad por su identificador usando lazy loading.
     * Solo carga la entidad principal sin sus relaciones.
     *
     * @param id identificador de la entidad
     * @return la entidad encontrada o null si no existe
     * @throws DAOException si hay error en la operación
     */
    T findById(int id) throws DAOException;

    /**
     * Obtiene todas las entidades usando lazy loading.
     * Solo carga las entidades principales sin sus relaciones.
     *
     * @return lista con todas las entidades
     * @throws DAOException si hay error en la operación
     */
    List<T> findAll() throws DAOException;
}
