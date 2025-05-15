package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.interfaces.CrudDAO;
import org.dam.fcojavier.gestionpersonal.model.Empleado;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase que implementa el acceso a datos para la entidad Empleado.
 * Proporciona operaciones CRUD y consultas específicas para gestionar los empleados
 * en la base de datos. Implementa la interfaz CrudDAO para operaciones básicas.
 *
 */
public class EmpleadoDAO implements CrudDAO<Empleado> {

    /** Consulta SQL para insertar un nuevo empleado */
    private final String insert_SQL = "INSERT INTO empleado (id_empresa, nombre, apellidos, departamento, telefono, email, puesto, rol, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    /** Consulta SQL para actualizar un empleado existente */
    private final String update_SQL = "UPDATE empleado SET id_empresa = ?, nombre = ?, apellidos = ?, telefono = ?, email = ?, activo = ?, departamento = ?, rol = ? WHERE id_empleado = ?";
    
    /** Consulta SQL para eliminar un empleado */
    private final String delete_SQL = "DELETE FROM empleado WHERE id_empleado = ?";
    
    /** Consulta SQL para buscar un empleado por su ID */
    private final String findById_SQL = "SELECT * FROM empleado WHERE id_empleado = ?";
    
    /** Consulta SQL para obtener todos los empleados */
    private final String findAll_SQL = "SELECT * FROM empleado";
    
    /** Consulta SQL para buscar un empleado por su email */
    private final String findByEmail_SQL = "SELECT * FROM empleado WHERE email = ?";
    
    /** Consulta SQL para obtener empleados por empresa */
    private final String findByEmpresa_SQL = "SELECT * FROM empleado WHERE id_empresa = ?";

    /**
     * Inserta un nuevo empleado en la base de datos.
     * Verifica que el empleado no exista previamente por su email.
     *
     * @param empleado El empleado a insertar
     * @return El empleado insertado con su ID generado, o null si ya existe
     * @throws DAOException Si ocurre un error durante la inserción
     */
    @Override
    public Empleado insert(Empleado empleado) throws DAOException {
        if(empleado!=null && findByEmail(empleado.getEmail())==null) {
            try(PreparedStatement pstm= ConnectionDB.getConnection().prepareStatement(insert_SQL, PreparedStatement.RETURN_GENERATED_KEYS)){
                pstm.setInt(1, empleado.getEmpresa().getIdEmpresa());
                pstm.setString(2, empleado.getNombre());
                pstm.setString(3, empleado.getApellido());
                pstm.setString(4, empleado.getDepartamento());
                pstm.setString(5, empleado.getTelefono());
                pstm.setString(6, empleado.getEmail());
                pstm.setString(7, empleado.getPuesto());
                pstm.setString(8, empleado.getRol().name());
                pstm.setBoolean(9, empleado.getActivo());

                if (pstm.executeUpdate() == 0) {
                    throw new DAOException("Error al crear el empleado", DAOErrorTipo.INSERT_ERROR);
                }

                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    empleado.setIdEmpleado(rs.getInt(1));
                }

            }catch (SQLException e){
                throw new DAOException(e.getMessage(), DAOErrorTipo.CONNECTION_ERROR);
            }
        }else{
            empleado=null;
        }
        return empleado;
    }

    /**
     * Actualiza un empleado existente en la base de datos.
     *
     * @param empleado El empleado con los nuevos datos
     * @return El empleado actualizado, o null si no existe
     * @throws DAOException Si ocurre un error durante la actualización
     */
    @Override
    public Empleado update(Empleado empleado) throws DAOException {
        Empleado empleadoActualizado = null;
        if(empleado != null) {
            Empleado empleadoExistente = findById(empleado.getIdEmpleado());
            if(empleadoExistente != null) {
                try(PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(update_SQL)) {
                    pstm.setInt(1, empleado.getEmpresa().getIdEmpresa());
                    pstm.setString(2, empleado.getNombre());
                    pstm.setString(3, empleado.getApellido());
                    pstm.setString(4, empleado.getTelefono());
                    pstm.setString(5, empleado.getEmail());
                    pstm.setBoolean(6, empleado.getActivo());
                    pstm.setString(7, empleado.getDepartamento());
                    pstm.setString(8, empleado.getRol().name());
                    pstm.setInt(9, empleadoExistente.getIdEmpleado());

                    if(pstm.executeUpdate() > 0) {
                        empleadoActualizado = empleado;
                    }
                } catch (SQLException e) {
                    throw new DAOException("Error al modificar el empleado: " + e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
                }
            } else {
                throw new DAOException("El empleado no existe", DAOErrorTipo.NOT_FOUND);
            }
        }
        return empleadoActualizado;
    }

    /**
     * Elimina un empleado de la base de datos.
     *
     * @param empleado El empleado a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws DAOException Si ocurre un error durante la eliminación
     */
    @Override
    public boolean delete(Empleado empleado) throws DAOException {
        boolean deleted=false;
        if(empleado!=null){
            Empleado empleadoEncontrado= findById(empleado.getIdEmpleado());
            if(empleadoEncontrado!=null){
                try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(delete_SQL)){
                    pstm.setInt(1, empleadoEncontrado.getIdEmpleado());
                    pstm.executeUpdate();
                    deleted=true;
                }catch (SQLException e){
                    throw new DAOException("Error al borrar el empleado: "+e.getMessage(), DAOErrorTipo.DELETE_ERROR);
                }
            }
        }
        return deleted;
    }

    /**
     * Busca un empleado por su ID.
     *
     * @param id ID del empleado a buscar
     * @return El empleado encontrado, o null si no existe
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    @Override
    public Empleado findById(int id) throws DAOException {
        Empleado empleado = null;
        EmpresaDAO EmpresaDAO = new EmpresaDAO();

        try (PreparedStatement stmt=ConnectionDB.getConnection().prepareStatement(findById_SQL)){
            stmt.setInt(1, id);
            try(ResultSet rs=stmt.executeQuery()){
                if (rs.next()) {
                    empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    empleado.setEmpresa(EmpresaDAO.findById(rs.getInt("id_empresa")));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setApellido(rs.getString("apellidos"));
                    empleado.setDepartamento(rs.getString("departamento"));
                    empleado.setTelefono(rs.getString("telefono"));
                    empleado.setEmail(rs.getString("email"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setActivo(rs.getBoolean("activo"));
                    empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                }
            }
        }catch (SQLException e){
            throw new DAOException("Error al buscar el empleado: "+e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleado;
    }

    /**
     * Obtiene todos los empleados registrados en la base de datos.
     *
     * @return Lista de todos los empleados
     * @throws DAOException Si ocurre un error al obtener los datos
     */
    @Override
    public List<Empleado> findAll() throws DAOException {
        List<Empleado> empleados = new java.util.ArrayList<>();
        EmpresaDAO EmpresaDAO = new EmpresaDAO();

        try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(findAll_SQL)){
            ResultSet rs=pstm.executeQuery();
            while(rs.next()){
                Empleado empleado=new Empleado();
                empleado.setIdEmpleado(rs.getInt("id_empleado"));
                empleado.setEmpresa(EmpresaDAO.findById(rs.getInt("id_empresa")));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellido(rs.getString("apellidos"));
                empleado.setDepartamento(rs.getString("departamento"));
                empleado.setTelefono(rs.getString("telefono"));
                empleado.setEmail(rs.getString("email"));
                empleado.setPuesto(rs.getString("puesto"));
                empleado.setActivo(rs.getBoolean("activo"));
                empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                empleados.add(empleado);
            }
        }catch (SQLException e){
            throw new DAOException("Error al listar los empleados: "+e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleados;
    }

    /**
     * Busca un empleado por su dirección de email.
     *
     * @param email Email del empleado a buscar
     * @return El empleado encontrado, o null si no existe
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    public Empleado findByEmail(String email) throws DAOException {
        Empleado empleado = null;
        EmpresaDAO empresaDAO = new EmpresaDAO();

        try (PreparedStatement stmt = ConnectionDB.getConnection().prepareStatement(findByEmail_SQL)){
            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    empleado.setEmpresa(empresaDAO.findById(rs.getInt("id_empresa")));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setApellido(rs.getString("apellidos"));
                    empleado.setDepartamento(rs.getString("departamento"));
                    empleado.setTelefono(rs.getString("telefono"));
                    empleado.setEmail(rs.getString("email"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setActivo(rs.getBoolean("activo"));
                    empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                }
            }
        } catch (SQLException e){
            throw new DAOException("Error al buscar el empleado por email: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleado;
    }

    /**
     * Verifica si una empresa tiene empleados.
     *
     * @param idEmpresa ID de la empresa a verificar
     * @return true si la empresa tiene empleados, false en caso contrario
     * @throws DAOException Si ocurre un error durante la verificación
     */
    public boolean hayEmpleadosByEmpresa(int idEmpresa) throws DAOException {
        int cantidadEmpleados = 0;
        try (PreparedStatement stmt = ConnectionDB.getConnection().prepareStatement(findByEmpresa_SQL)) {
            stmt.setInt(1, idEmpresa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                cantidadEmpleados= rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar empleados de la empresa", DAOErrorTipo.NOT_FOUND);
        }
        return cantidadEmpleados>0;
    }

    /**
     * Obtiene todos los empleados de una empresa específica.
     *
     * @param empresa La empresa cuyos empleados se desean obtener
     * @return Lista de empleados de la empresa
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    public List<Empleado> findByEmpresa(Empresa empresa) throws DAOException {
        List<Empleado> empleados = new java.util.ArrayList<>();

        try (PreparedStatement stmt = ConnectionDB.getConnection().prepareStatement(findByEmpresa_SQL)) {
            stmt.setInt(1, empresa.getIdEmpresa());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Empleado empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    empleado.setEmpresa(empresa);
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setApellido(rs.getString("apellidos"));
                    empleado.setDepartamento(rs.getString("departamento"));
                    empleado.setTelefono(rs.getString("telefono"));
                    empleado.setEmail(rs.getString("email"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setActivo(rs.getBoolean("activo"));
                    empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                    empleados.add(empleado);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar empleados de la empresa: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleados;
    }
}