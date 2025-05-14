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

public class PerteneceTurnoDAO {
    private final String insert_SQL = "INSERT INTO pertenece (id_empleado, id_turno, fecha) VALUES (?, ?, ?)";
    private final String delete_SQL = "DELETE FROM pertenece WHERE id_empleado = ? AND id_turno = ? AND fecha = ?";
    private final String findByEmpleado_SQL = "SELECT * FROM pertenece WHERE id_empleado = ?";
    private final String findByTurno_SQL = "SELECT * FROM pertenece WHERE id_turno = ?";
    private final String findByFecha_SQL = "SELECT * FROM pertenece WHERE fecha = ?";
    private final String findByAsignacionesFecha_SQL = "SELECT pt.*, t.* FROM pertenece pt " +
                                                        "JOIN turno t ON pt.id_turno = t.id_turno " +
                                                        "WHERE pt.id_empleado = ? AND pt.fecha = ?";
    private final String findAll_SQL = "SELECT * FROM pertenece";
    private final String exists_SQL = "SELECT COUNT(*) FROM pertenece WHERE id_empleado = ? AND id_turno = ? AND fecha = ?";
    private final String update_SQL = "UPDATE pertenece SET id_turno = ?, fecha = ? WHERE id_empleado = ? AND fecha = ?";

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final TurnoDAO turnoDAO = new TurnoDAO();

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

    public List<PerteneceTurno> findByEmpleado(Empleado empleado) throws DAOException {
        List<PerteneceTurno> asignaciones = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByEmpleado_SQL)) {
            pstm.setInt(1, empleado.getIdEmpleado());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                PerteneceTurno perteneceTurno = new PerteneceTurno();
                perteneceTurno.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                perteneceTurno.setTurno(turnoDAO.findById(rs.getInt("id_turno")));
                perteneceTurno.setFecha(rs.getDate("fecha").toLocalDate());
                asignaciones.add(perteneceTurno);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar asignaciones por empleado: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return asignaciones;
    }

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

    public List<PerteneceTurno> findByTurno(Turno turno) throws DAOException {
        List<PerteneceTurno> asignaciones = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByTurno_SQL)) {
            pstm.setInt(1, turno.getIdTurno());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                PerteneceTurno perteneceTurno = new PerteneceTurno();
                perteneceTurno.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                perteneceTurno.setTurno(turnoDAO.findById(rs.getInt("id_turno")));
                perteneceTurno.setFecha(rs.getDate("fecha").toLocalDate());
                asignaciones.add(perteneceTurno);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar asignaciones por turno: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return asignaciones;
    }

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

    public PerteneceTurno update(PerteneceTurno perteneceTurnoActual, PerteneceTurno perteneceTurnoNuevo) throws DAOException {
        PerteneceTurno turnoActualizado = null;

        if (perteneceTurnoActual != null && perteneceTurnoNuevo != null) {
            try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(update_SQL)) {
                pstm.setInt(1, perteneceTurnoNuevo.getTurno().getIdTurno());
                pstm.setDate(2, Date.valueOf(perteneceTurnoNuevo.getFecha()));
                pstm.setInt(3, perteneceTurnoActual.getEmpleado().getIdEmpleado());
                pstm.setDate(4, Date.valueOf(perteneceTurnoActual.getFecha()));

                if (pstm.executeUpdate() > 0) {
                    turnoActualizado = perteneceTurnoNuevo;
                }
            } catch (SQLException e) {
                throw new DAOException("Error al actualizar asignación de turno: " + e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
            }
        }

        return turnoActualizado;
    }
}
