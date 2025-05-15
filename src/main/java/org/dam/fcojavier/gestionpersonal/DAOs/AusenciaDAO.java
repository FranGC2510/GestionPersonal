package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.interfaces.CrudDAO;
import org.dam.fcojavier.gestionpersonal.model.Ausencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase que implementa el acceso a datos para la entidad Ausencia.
 * Proporciona operaciones CRUD y consultas específicas para gestionar las ausencias
 * de los empleados en la base de datos.
 *
 */
public class AusenciaDAO implements CrudDAO<Ausencia> {
    /** Consulta SQL para insertar una nueva ausencia */
    private final String insert_SQL = "INSERT INTO ausencia (motivo, fecha_inicio, fecha_fin, id_empleado) VALUES (?, ?, ?, ?)";
    
    /** Consulta SQL para actualizar una ausencia existente */
    private final String update_SQL = "UPDATE ausencia SET motivo = ?, fecha_inicio = ?, fecha_fin = ?, id_empleado = ? WHERE id_ausencia = ?";
    
    /** Consulta SQL para eliminar una ausencia */
    private final String delete_SQL = "DELETE FROM ausencia WHERE id_ausencia = ?";
    
    /** Consulta SQL para buscar una ausencia por su ID */
    private final String findById_SQL = "SELECT * FROM ausencia WHERE id_ausencia = ?";
    
    /** Consulta SQL para obtener todas las ausencias */
    private final String findAll_SQL = "SELECT * FROM ausencia";
    
    /** Consulta SQL para obtener ausencias por empresa */
    private final String findByEmpresa_SQL = "SELECT a.* FROM ausencia a " +
            "INNER JOIN empleado e ON a.id_empleado = e.id_empleado " +
            "WHERE e.id_empresa = ?";

    /** DAO para acceder a los datos de empleados */
    private final EmpleadoDAO empleadoDAO;

    /**
     * Constructor que inicializa el DAO con una referencia al DAO de empleados.
     *
     * @param empleadoDAO DAO para acceder a los datos de empleados
     */
    public AusenciaDAO(EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }

    /**
     * Inserta una nueva ausencia en la base de datos.
     *
     * @param ausencia La ausencia a insertar
     * @return La ausencia insertada con su ID generado, o null si ya existe
     * @throws DAOException Si ocurre un error durante la inserción
     */
    @Override
    public Ausencia insert(Ausencia ausencia) throws DAOException {
        if (ausencia != null && findById(ausencia.getIdAusencia()) == null) {
            try(PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(insert_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstm.setString(1, ausencia.getMotivo());
                pstm.setDate(2, java.sql.Date.valueOf(ausencia.getFechaInicio()));
                pstm.setDate(3, ausencia.getFechaFin() != null ? java.sql.Date.valueOf(ausencia.getFechaFin()) : null);
                pstm.setInt(4, ausencia.getEmpleado().getIdEmpleado());
                
                if (pstm.executeUpdate() == 0) {
                    throw new DAOException("Error al crear la ausencia", DAOErrorTipo.INSERT_ERROR);
                }

                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    ausencia.setIdAusencia(rs.getInt(1));
                }
            } catch (SQLException e) {
                throw new DAOException("Error al insertar la ausencia: " + e.getMessage(), DAOErrorTipo.CONNECTION_ERROR);
            }
        } else {
            ausencia = null;
        }
        return ausencia;
    }

    /**
     * Actualiza una ausencia existente en la base de datos.
     *
     * @param ausencia La ausencia con los nuevos datos
     * @return La ausencia actualizada, o null si no existe
     * @throws DAOException Si ocurre un error durante la actualización
     */
    @Override
    public Ausencia update(Ausencia ausencia) throws DAOException {
        Ausencia ausenciaActualizada = null;
        if(ausencia != null) {
            Ausencia ausenciaExistente = findById(ausencia.getIdAusencia());
            if(ausenciaExistente != null) {
                try(PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(update_SQL)) {
                    pstm.setString(1, ausencia.getMotivo());
                    pstm.setDate(2, java.sql.Date.valueOf(ausencia.getFechaInicio()));
                    pstm.setDate(3, ausencia.getFechaFin() != null ? java.sql.Date.valueOf(ausencia.getFechaFin()) : null);
                    pstm.setInt(4, ausencia.getEmpleado().getIdEmpleado());
                    pstm.setInt(5, ausenciaExistente.getIdAusencia());
                    
                    if(pstm.executeUpdate() > 0) {
                        ausenciaActualizada = ausencia;
                    }
                } catch (SQLException e) {
                    throw new DAOException("Error al modificar la ausencia: " + e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
                }
            } else {
                throw new DAOException("La ausencia no existe", DAOErrorTipo.NOT_FOUND);
            }
        }
        return ausenciaActualizada;
    }

    /**
     * Elimina una ausencia de la base de datos.
     *
     * @param ausencia La ausencia a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws DAOException Si ocurre un error durante la eliminación
     */
    @Override
    public boolean delete(Ausencia ausencia) throws DAOException {
        boolean deleted=false;
        if(ausencia!=null){
            Ausencia ausenciaEncontrada= findById(ausencia.getIdAusencia());
            if(ausenciaEncontrada!=null){
                try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(delete_SQL)){
                    pstm.setInt(1, ausenciaEncontrada.getIdAusencia());
                    pstm.executeUpdate();
                    deleted=true;
                }catch (SQLException e){
                    throw new DAOException("Error al borrar la ausencia: "+e.getMessage(), DAOErrorTipo.DELETE_ERROR);
                }
            }
        }
        return deleted;
    }

    /**
     * Busca una ausencia por su ID.
     *
     * @param id ID de la ausencia a buscar
     * @return La ausencia encontrada, o null si no existe
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    @Override
    public Ausencia findById(int id) throws DAOException {
        Ausencia ausencia = null;

        try (PreparedStatement stmt = ConnectionDB.getConnection().prepareStatement(findById_SQL)) {
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ausencia = new Ausencia();
                    ausencia.setIdAusencia(rs.getInt("id_ausencia"));
                    ausencia.setMotivo(rs.getString("motivo"));
                    ausencia.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    java.sql.Date fechaFin = rs.getDate("fecha_fin");
                    if (fechaFin != null) {
                        ausencia.setFechaFin(fechaFin.toLocalDate());
                    }
                    // Obtener el empleado completo usando el EmpleadoDAO
                    ausencia.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la ausencia: " + e.getMessage(), DAOErrorTipo.CONNECTION_ERROR);
        }
        return ausencia;
    }

    /**
     * Obtiene todas las ausencias registradas en la base de datos.
     *
     * @return Lista de todas las ausencias
     * @throws DAOException Si ocurre un error al obtener los datos
     */
    @Override
    public List<Ausencia> findAll() throws DAOException {
        List<Ausencia> ausencias = new java.util.ArrayList<>();

        try(
                PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findAll_SQL);
                ResultSet rs = pstm.executeQuery()
        ) {
            while(rs.next()) {
                Ausencia ausencia = new Ausencia();
                ausencia.setIdAusencia(rs.getInt("id_ausencia"));
                ausencia.setMotivo(rs.getString("motivo"));
                ausencia.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                java.sql.Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    ausencia.setFechaFin(fechaFin.toLocalDate());
                }
                // Obtener el empleado completo usando el EmpleadoDAO
                ausencia.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                
                ausencias.add(ausencia);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar las ausencias: " + e.getMessage(), DAOErrorTipo.CONNECTION_ERROR);
        }
        return ausencias;
    }

    /**
     * Busca todas las ausencias asociadas a una empresa específica.
     *
     * @param idEmpresa ID de la empresa
     * @return Lista de ausencias de la empresa
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    public List<Ausencia> findByEmpresa(int idEmpresa) throws DAOException {
        List<Ausencia> ausencias = new java.util.ArrayList<>();

        try(PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByEmpresa_SQL)) {
            pstm.setInt(1, idEmpresa);
            try(ResultSet rs = pstm.executeQuery()) {
                while(rs.next()) {
                    Ausencia ausencia = new Ausencia();
                    ausencia.setIdAusencia(rs.getInt("id_ausencia"));
                    ausencia.setMotivo(rs.getString("motivo"));
                    ausencia.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    java.sql.Date fechaFin = rs.getDate("fecha_fin");
                    if (fechaFin != null) {
                        ausencia.setFechaFin(fechaFin.toLocalDate());
                    }
                    ausencia.setEmpleado(empleadoDAO.findById(rs.getInt("id_empleado")));
                    ausencias.add(ausencia);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar las ausencias por empresa: " + e.getMessage(), DAOErrorTipo.CONNECTION_ERROR);
        }
        return ausencias;
    }
}