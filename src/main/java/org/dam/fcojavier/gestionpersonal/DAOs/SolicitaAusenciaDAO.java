package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.enums.EstadoAusencia;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.SolicitaAusencia;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SolicitaAusenciaDAO {
    private final String insert_SQL = "INSERT INTO solicita (id_empleado, id_ausencia, fecha_solicitud, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?, ?)";
    private final String updateEstado_SQL = " UPDATE solicita SET estado = ? WHERE id_empleado = ? AND id_ausencia = ? AND fecha_solicitud = ?";
    private final String findById_SQL = "SELECT * FROM solicita WHERE id_empleado = ? AND id_ausencia = ? AND fecha_solicitud = ?";
    private final String findByEmpleado_SQL = "SELECT * FROM solicita WHERE id_empleado = ?";
    private final String findByAusencia_SQL = "SELECT * FROM solicita WHERE id_ausencia = ?";
    private final String findByEstado_SQL = "SELECT * FROM solicita WHERE estado = ?";
    private final String ausenciasActivas_SQL = "SELECT * FROM solicita WHERE estado = 'aprobada' AND fecha_fin >= CURRENT_DATE";

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final AusenciaDAO ausenciaDAO = new AusenciaDAO();

    public SolicitaAusencia insert(SolicitaAusencia solicitud) throws DAOException {
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(insert_SQL)) {
            pstm.setInt(1, solicitud.getEmpleado().getIdEmpleado());
            pstm.setInt(2, solicitud.getAusencia().getIdAusencia());
            pstm.setDate(3, Date.valueOf(solicitud.getFechaSolicitud()));
            pstm.setDate(4, Date.valueOf(solicitud.getFechaInicio()));
            pstm.setDate(5, Date.valueOf(solicitud.getFechaFin()));
            pstm.setString(6, solicitud.getEstado().toString());

            if (pstm.executeUpdate() == 0) {
                solicitud = null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al crear la solicitud de ausencia: " + e.getMessage(), DAOErrorTipo.INSERT_ERROR);
        }
        return solicitud;
    }

    public SolicitaAusencia updateEstado(SolicitaAusencia solicitud) throws DAOException {
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(updateEstado_SQL)) {
            pstm.setString(1, solicitud.getEstado().toString());
            pstm.setInt(2, solicitud.getEmpleado().getIdEmpleado());
            pstm.setInt(3, solicitud.getAusencia().getIdAusencia());
            pstm.setDate(4, Date.valueOf(solicitud.getFechaSolicitud()));

            if (pstm.executeUpdate() == 0) {
                solicitud = null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el estado de la solicitud: " + e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
        }
        return solicitud;
    }

    public SolicitaAusencia findById(int idEmpleado, int idAusencia, LocalDate fechaSolicitud) throws DAOException {
        SolicitaAusencia solicitud = null;
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findById_SQL)) {
            pstm.setInt(1, idEmpleado);
            pstm.setInt(2, idAusencia);
            pstm.setDate(3, Date.valueOf(fechaSolicitud));

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    solicitud = new SolicitaAusencia();
                    solicitud.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                    solicitud.setAusencia(ausenciaDAO.findById(rs.getInt("id_ausencia")));
                    solicitud.setFechaSolicitud(rs.getDate("fecha_solicitud").toLocalDate());
                    solicitud.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    solicitud.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    solicitud.setEstado(EstadoAusencia.valueOf(rs.getString("estado")));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la solicitud: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return solicitud;
    }

    public List<SolicitaAusencia> findByEmpleado(Empleado empleado) throws DAOException {
        List<SolicitaAusencia> solicitudes = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByEmpleado_SQL)) {
            pstm.setInt(1, empleado.getIdEmpleado());
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    SolicitaAusencia solicitud = new SolicitaAusencia();
                    solicitud.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                    solicitud.setAusencia(ausenciaDAO.findById(rs.getInt("id_ausencia")));
                    solicitud.setFechaSolicitud(rs.getDate("fecha_solicitud").toLocalDate());
                    solicitud.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    solicitud.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    solicitud.setEstado(EstadoAusencia.valueOf(rs.getString("estado")));
                    solicitudes.add(solicitud);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar solicitudes del empleado: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return solicitudes;
    }

    public List<SolicitaAusencia> findByEstado(EstadoAusencia estado) throws DAOException {
        List<SolicitaAusencia> solicitudes = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByEstado_SQL)) {
            pstm.setString(1, estado.toString());
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    SolicitaAusencia solicitud = new SolicitaAusencia();
                    solicitud.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                    solicitud.setAusencia(ausenciaDAO.findById(rs.getInt("id_ausencia")));
                    solicitud.setFechaSolicitud(rs.getDate("fecha_solicitud").toLocalDate());
                    solicitud.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    solicitud.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    solicitud.setEstado(EstadoAusencia.valueOf(rs.getString("estado")));
                    solicitudes.add(solicitud);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar solicitudes por estado: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return solicitudes;
    }

    public List<SolicitaAusencia> getAusenciasActivas() throws DAOException {
        List<SolicitaAusencia> solicitudes = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(ausenciasActivas_SQL);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                SolicitaAusencia solicitud = new SolicitaAusencia();
                solicitud.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                solicitud.setAusencia(ausenciaDAO.findById(rs.getInt("id_ausencia")));
                solicitud.setFechaSolicitud(rs.getDate("fecha_solicitud").toLocalDate());
                solicitud.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                solicitud.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                solicitud.setEstado(EstadoAusencia.valueOf(rs.getString("estado")));
                solicitudes.add(solicitud);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener ausencias activas: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return solicitudes;
    }
}


