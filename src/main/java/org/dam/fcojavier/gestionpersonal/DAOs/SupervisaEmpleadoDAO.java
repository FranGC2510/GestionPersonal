package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.SupervisaEmpleado;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupervisaEmpleadoDAO {
    private final String insert_SQL = "INSERT INTO supervisa (id_supervisor, id_empleado, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";
    private final String update_SQL = "UPDATE supervisa SET fecha_fin = ? WHERE id_supervisor = ? AND id_empleado = ? AND fecha_inicio = ?";
    private final String findActualSupervisor_SQL = "SELECT * FROM supervisa WHERE id_empleado = ? AND fecha_fin IS NULL";
    private final String findHistoricoSupervisores_SQL = "SELECT * FROM supervisa WHERE id_empleado = ?";
    private final String findEmpleadosSupervisados_SQL = "SELECT * FROM supervisa WHERE id_supervisor = ? AND fecha_fin IS NULL";


    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public SupervisaEmpleado asignarSupervisor(SupervisaEmpleado supervision) throws DAOException {
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(insert_SQL)) {
            pstm.setInt(1, supervision.getSupervisor().getIdEmpleado());
            pstm.setInt(2, supervision.getEmpleado().getIdEmpleado());
            pstm.setDate(3, Date.valueOf(supervision.getFechaInicio()));
            pstm.setDate(4, supervision.getFechaFin() != null ? Date.valueOf(supervision.getFechaFin()) : null);

            if (pstm.executeUpdate() == 0) {
                supervision=null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al asignar supervisor: " + e.getMessage(), DAOErrorTipo.INSERT_ERROR);
        }
        return supervision;
    }

    public SupervisaEmpleado finalizarSupervision(SupervisaEmpleado supervision) throws DAOException {
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(update_SQL)) {
            pstm.setDate(1, Date.valueOf(supervision.getFechaFin()));
            pstm.setInt(2, supervision.getSupervisor().getIdEmpleado());
            pstm.setInt(3, supervision.getEmpleado().getIdEmpleado());
            pstm.setDate(4, Date.valueOf(supervision.getFechaInicio()));

            if (pstm.executeUpdate() == 0) {
                supervision=null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al finalizar supervisión: " + e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
        }
        return supervision;
    }

    public SupervisaEmpleado obtenerSupervisorActual(Empleado empleado) throws DAOException {
        SupervisaEmpleado supervision = new SupervisaEmpleado();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findActualSupervisor_SQL)) {
            pstm.setInt(1, empleado.getIdEmpleado());
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                supervision.setSupervisor(empleadoDAO.findById(rs.getInt("id_supervisor")));
                supervision.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                supervision.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    supervision.setFechaFin(fechaFin.toLocalDate());
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener supervisor actual: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return supervision;
    }

    public List<SupervisaEmpleado> obtenerHistoricoSupervisoresByEmpleado(Empleado empleado) throws DAOException {
        List<SupervisaEmpleado> historicoSupervisores = new ArrayList<>();
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findHistoricoSupervisores_SQL)) {
            pstm.setInt(1, empleado.getIdEmpleado());
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                SupervisaEmpleado supervision = new SupervisaEmpleado();
                supervision.setSupervisor(empleadoDAO.findById(rs.getInt("id_supervisor")));
                supervision.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                supervision.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    supervision.setFechaFin(fechaFin.toLocalDate());
                }
                historicoSupervisores.add(supervision);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener histórico de supervisores: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return historicoSupervisores;
    }

    /**
     * Obtiene la lista de empleados que actualmente supervisa un supervisor
     * @param supervisor El empleado supervisor
     * @return Lista de empleados supervisados activas
     * @throws DAOException si hay error en la base de datos
     */
    public List<SupervisaEmpleado> obtenerEmpleadosSupervisados(Empleado supervisor) throws DAOException {
        List<SupervisaEmpleado> empleadosSupervisados = new ArrayList<>();

        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findEmpleadosSupervisados_SQL)) {

            pstm.setInt(1, supervisor.getIdEmpleado());
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                SupervisaEmpleado supervision = new SupervisaEmpleado();
                supervision.setSupervisor(empleadoDAO.findById(rs.getInt("id_supervisor")));
                supervision.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                supervision.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    supervision.setFechaFin(fechaFin.toLocalDate());
                }
                empleadosSupervisados.add(supervision);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener empleados supervisados: " +
                    e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }

        return empleadosSupervisados;
    }

}

