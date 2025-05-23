package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.interfaces.CrudDAO;
import org.dam.fcojavier.gestionpersonal.model.Turno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el acceso a datos para la entidad Turno.
 * Proporciona operaciones CRUD para gestionar los turnos de trabajo en la base de datos.
 * Los turnos representan períodos de tiempo con hora de inicio y fin.
 *
 */
public class TurnoDAO implements CrudDAO<Turno> {

    /** Consulta SQL para insertar un nuevo turno */
    private final String insert_SQL = "INSERT INTO turno (descripcion, hora_inicio, hora_fin) VALUES (?, ?, ?)";
    
    /** Consulta SQL para actualizar un turno existente */
    private final String update_SQL = "UPDATE turno SET descripcion = ?, hora_inicio = ?, hora_fin = ? WHERE id_turno = ?";
    
    /** Consulta SQL para eliminar un turno */
    private final String delete_SQL = "DELETE FROM turno WHERE id_turno = ?";
    
    /** Consulta SQL para buscar un turno por su ID */
    private final String findById_SQL = "SELECT * FROM turno WHERE id_turno = ?";
    
    /** Consulta SQL para obtener todos los turnos */
    private final String findAll_SQL = "SELECT * FROM turno";

    /**
     * Inserta un nuevo turno en la base de datos.
     * Verifica que el turno no exista previamente por su ID.
     *
     * @param turno El turno a insertar
     * @return El turno insertado con su ID generado, o null si ya existe
     * @throws DAOException Si ocurre un error durante la inserción
     */
    @Override
    public Turno insert(Turno turno) throws DAOException {
        Turno turnoInsertado = null;
        if(findById(turno.getIdTurno())==null){
            try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(insert_SQL, Statement.RETURN_GENERATED_KEYS)) {

                pstm.setString(1, turno.getDescripcion());
                pstm.setTime(2, Time.valueOf(turno.getHoraInicio()));
                pstm.setTime(3, Time.valueOf(turno.getHoraFin()));

                if (pstm.executeUpdate() > 0) {
                    ResultSet rs = pstm.getGeneratedKeys();
                    if (rs.next()) {
                        turno.setIdTurno(rs.getInt(1));
                        turnoInsertado = turno;
                    }
                }

            } catch (SQLException e) {
                throw new DAOException("Error al insertar turno: " + e.getMessage(), DAOErrorTipo.INSERT_ERROR);
            }
        }
        return turnoInsertado;

    }

    /**
     * Actualiza un turno existente en la base de datos.
     *
     * @param turno El turno con los nuevos datos
     * @return El turno actualizado, o null si no existe
     * @throws DAOException Si ocurre un error durante la actualización o si el turno no existe
     */
    @Override
    public Turno update(Turno turno) throws DAOException {
        Turno turnoActualizado = null;

        if (turno != null) {
            Turno turnoExistente = findById(turno.getIdTurno());
            if (turnoExistente != null) {
                try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(update_SQL)) {
                    pstm.setString(1, turno.getDescripcion());
                    pstm.setTime(2, Time.valueOf(turno.getHoraInicio()));
                    pstm.setTime(3, Time.valueOf(turno.getHoraFin()));
                    pstm.setInt(4, turno.getIdTurno());

                    if (pstm.executeUpdate() > 0) {
                        turnoActualizado = turno;
                    }
                } catch (SQLException e) {
                    throw new DAOException("Error al actualizar turno: " + e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
                }
            }else{
                throw new DAOException("El turno no existe", DAOErrorTipo.NOT_FOUND);
            }
        }

        return turnoActualizado;

    }

    /**
     * Elimina un turno de la base de datos.
     * La eliminación también eliminará todas las asignaciones asociadas a este turno.
     *
     * @param turno El turno a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws DAOException Si ocurre un error durante la eliminación
     */
    @Override
    public boolean delete(Turno turno) throws DAOException {
        boolean deleted = false;

        if (turno != null) {
            Turno turnoExistente = findById(turno.getIdTurno());
            if (turnoExistente != null) {
                try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(delete_SQL)) {
                    pstm.setInt(1, turnoExistente.getIdTurno());
                    if (pstm.executeUpdate() > 0) {
                        deleted = true;
                    }
                } catch (SQLException e) {
                    throw new DAOException("Error al eliminar turno: " + e.getMessage(), DAOErrorTipo.DELETE_ERROR);
                }
            }
        }

        return deleted;

    }

    /**
     * Busca un turno por su ID.
     *
     * @param id ID del turno a buscar
     * @return El turno encontrado, o null si no existe
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    @Override
    public Turno findById(int id) throws DAOException {
        Turno turno = null;

        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findById_SQL)) {
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                turno = new Turno();
                turno.setIdTurno(rs.getInt("id_turno"));
                turno.setDescripcion(rs.getString("descripcion"));
                turno.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                turno.setHoraFin(rs.getTime("hora_fin").toLocalTime());
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar turno: " + e.getMessage(),
                    DAOErrorTipo.NOT_FOUND);
        }

        return turno;

    }

    /**
     * Obtiene todos los turnos registrados en la base de datos.
     *
     * @return Lista de todos los turnos
     * @throws DAOException Si ocurre un error al obtener los datos
     */
    @Override
    public List<Turno> findAll() throws DAOException {
        List<Turno> turnos = new ArrayList<>();

        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findAll_SQL);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                Turno turno = new Turno();
                turno.setIdTurno(rs.getInt("id_turno"));
                turno.setDescripcion(rs.getString("descripcion"));
                turno.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                turno.setHoraFin(rs.getTime("hora_fin").toLocalTime());
                turnos.add(turno);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los turnos: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }

        return turnos;

    }
}