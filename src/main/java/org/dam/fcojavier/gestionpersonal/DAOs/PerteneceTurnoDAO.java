package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.PerteneceTurno;
import org.dam.fcojavier.gestionpersonal.model.Turno;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la asignación de turnos a empleados en la base de datos.
 * Maneja la relación muchos a muchos entre empleados y turnos, incluyendo la fecha de asignación.
 *
 */
public class PerteneceTurnoDAO {
    /** Consulta SQL para insertar una nueva asignación de turno */
    private final String insert_SQL = "INSERT INTO pertenece (id_empleado, id_turno, fecha) VALUES (?, ?, ?)";
    
    /** Consulta SQL para eliminar una asignación de turno */
    private final String delete_SQL = "DELETE FROM pertenece WHERE id_empleado = ? AND id_turno = ? AND fecha = ?";
    
    /** Consulta SQL para buscar asignaciones por fecha */
    private final String findByFecha_SQL = "SELECT * FROM pertenece WHERE fecha = ?";
    
    /** Consulta SQL para buscar asignaciones por empleado y fecha */
    private final String findByAsignacionesFecha_SQL = "SELECT pt.*, t.* FROM pertenece pt " +
            "JOIN turno t ON pt.id_turno = t.id_turno " +
            "WHERE pt.id_empleado = ? AND pt.fecha = ?";
    
    /** Consulta SQL para obtener todas las asignaciones */
    private final String findAll_SQL = "SELECT * FROM pertenece";
    
    /** Consulta SQL para verificar si existe una asignación */
    private final String exists_SQL = "SELECT COUNT(*) FROM pertenece WHERE id_empleado = ? AND id_turno = ? AND fecha = ?";

    /** DAO para acceder a los datos de empleados */
    private final EmpleadoDAO empleadoDAO;
    
    /** DAO para acceder a los datos de turnos */
    private final TurnoDAO turnoDAO;

    /**
     * Constructor que inicializa los DAOs necesarios para las operaciones.
     */
    public PerteneceTurnoDAO() {
        this.empleadoDAO = new EmpleadoDAO();
        this.turnoDAO = new TurnoDAO();
    }

    /**
     * Inserta una nueva asignación de turno en la base de datos.
     *
     * @param perteneceTurno La asignación de turno a insertar
     * @return La asignación insertada, o null si hubo un error
     * @throws DAOException Si ocurre un error durante la inserción
     */
    public PerteneceTurno insert(PerteneceTurno perteneceTurno) throws DAOException {
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(insert_SQL)) {
            pstm.setInt(1, perteneceTurno.getEmpleado().getIdEmpleado());
            pstm.setInt(2, perteneceTurno.getTurno().getIdTurno());
            pstm.setDate(3, Date.valueOf(perteneceTurno.getFecha()));

            if (pstm.executeUpdate() == 0) {
                perteneceTurno = null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al asignar turno: " + e.getMessage(), DAOErrorTipo.INSERT_ERROR);
        }
        return perteneceTurno;
    }

    /**
     * Elimina una asignación de turno de la base de datos.
     *
     * @param perteneceTurno La asignación de turno a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws DAOException Si ocurre un error durante la eliminación
     */
    public boolean delete(PerteneceTurno perteneceTurno) throws DAOException {
        boolean deleted = false;
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(delete_SQL)) {
            pstm.setInt(1, perteneceTurno.getEmpleado().getIdEmpleado());
            pstm.setInt(2, perteneceTurno.getTurno().getIdTurno());
            pstm.setDate(3, Date.valueOf(perteneceTurno.getFecha()));
            deleted = pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar asignación de turno: " + e.getMessage(), DAOErrorTipo.DELETE_ERROR);
        }
        return deleted;
    }

    /**
     * Busca todas las asignaciones de turnos para una fecha específica.
     *
     * @param fecha La fecha para la cual buscar asignaciones
     * @return Lista de asignaciones para la fecha especificada
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    public List<PerteneceTurno> findByFecha(LocalDate fecha) throws DAOException {
        List<PerteneceTurno> asignaciones = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByFecha_SQL)) {
            pstm.setDate(1, Date.valueOf(fecha));
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                PerteneceTurno perteneceTurno = new PerteneceTurno();
                perteneceTurno.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                perteneceTurno.setTurno(turnoDAO.findById(rs.getInt("id_turno")));
                perteneceTurno.setFecha(rs.getDate("fecha").toLocalDate());
                asignaciones.add(perteneceTurno);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar asignaciones por fecha: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return asignaciones;
    }

    /**
     * Busca todas las asignaciones de turnos para un empleado en una fecha específica.
     *
     * @param empleado El empleado para el cual buscar asignaciones
     * @param fecha La fecha para la cual buscar asignaciones
     * @return Lista de asignaciones para el empleado y fecha especificados
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    public List<PerteneceTurno> findByAsignacionesFecha(Empleado empleado, LocalDate fecha) throws DAOException {
        List<PerteneceTurno> asignaciones = new ArrayList<>();

        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByAsignacionesFecha_SQL)) {

            pstm.setInt(1, empleado.getIdEmpleado());
            pstm.setDate(2, Date.valueOf(fecha));

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Turno turno = new Turno();
                    turno.setIdTurno(rs.getInt("id_turno"));
                    turno.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    turno.setHoraFin(rs.getTime("hora_fin").toLocalTime());

                    PerteneceTurno perteneceTurno = new PerteneceTurno();
                    perteneceTurno.setEmpleado(empleado);
                    perteneceTurno.setTurno(turno);
                    perteneceTurno.setFecha(rs.getDate("fecha").toLocalDate());

                    asignaciones.add(perteneceTurno);
                }
            }

            return asignaciones;
        } catch (SQLException e) {
            throw new DAOException("Error al buscar asignaciones de turnos", DAOErrorTipo.NOT_FOUND);
        }

    }

    /**
     * Obtiene todas las asignaciones de turnos registradas en la base de datos.
     *
     * @return Lista de todas las asignaciones de turnos
     * @throws DAOException Si ocurre un error al obtener los datos
     */
    public List<PerteneceTurno> findAll() throws DAOException {
        List<PerteneceTurno> asignaciones = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findAll_SQL)) {
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                PerteneceTurno perteneceTurno = new PerteneceTurno();
                perteneceTurno.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                perteneceTurno.setTurno(turnoDAO.findById(rs.getInt("id_turno")));
                perteneceTurno.setFecha(rs.getDate("fecha").toLocalDate());
                asignaciones.add(perteneceTurno);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al cargar todas las asignaciones: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return asignaciones;
    }

    /**
     * Verifica si existe una asignación específica en la base de datos.
     *
     * @param idEmpleado ID del empleado
     * @param idTurno ID del turno
     * @param fecha Fecha de la asignación
     * @return true si existe la asignación, false en caso contrario
     * @throws DAOException Si ocurre un error durante la verificación
     */
    public boolean exists(int idEmpleado, int idTurno, LocalDate fecha) throws DAOException {
        boolean exists = false;
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(exists_SQL)) {
            pstm.setInt(1, idEmpleado);
            pstm.setInt(2, idTurno);
            pstm.setDate(3, Date.valueOf(fecha));
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar existencia de asignación: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return exists;
    }
}