package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.enums.TipoEmpleado;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.interfaces.CrudDAO;
import org.dam.fcojavier.gestionpersonal.model.Empleado;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmpleadoDAO implements CrudDAO<Empleado> {

    private final String insert_SQL = "INSERT INTO empleado (id_empresa, nombre, apellidos, departamento, telefono, email, password_hash, puesto, rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String update_SQL = "UPDATE empleado SET id_empresa = ?, nombre = ?, apellidos = ?, departamento = ?, telefono = ?, email = ?, password_hash = ?, puesto = ?, rol = ? WHERE id_empleado = ?";
    private final String delete_SQL = "DELETE FROM empleado WHERE id_empleado = ?";
    private final String findById_SQL = "SELECT * FROM empleado WHERE id_empleado = ?";
    private final String findAll_SQL = "SELECT * FROM empleado";
    private final String findByEmail_SQL = "SELECT * FROM empleado WHERE email = ?";

    @Override
    public Empleado insert(Empleado empleado) throws DAOException {
        if(empleado!=null && findByEmail(empleado.getEmail())==null) {
            try(PreparedStatement pstm= ConnectionDB.getConnection().prepareStatement(insert_SQL, PreparedStatement.RETURN_GENERATED_KEYS)){
                pstm.setInt(1, empleado.getEmpresa().getIdEmpresa());
                pstm.setString(2, empleado.getNombre());
                pstm.setString(3, empleado.getApellido());
                pstm.setString(4,empleado.getDepartamento());
                pstm.setString(5,empleado.getTelefono());
                pstm.setString(6,empleado.getEmail());
                pstm.setString(7, empleado.getPasswordHash());
                pstm.setString(8,empleado.getPuesto());
                pstm.setString(9, empleado.getRol().name());

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

    @Override
    public Empleado update(Empleado empleado) throws DAOException {
        Empleado empleadoActualizado=null;
        if(empleado != null) {
            Empleado empleadoExistente = findById(empleado.getIdEmpleado());
            if(empleadoExistente != null) {
                try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(update_SQL)){
                    pstm.setInt(1, empleado.getEmpresa().getIdEmpresa());
                    pstm.setString(2, empleado.getNombre());
                    pstm.setString(3, empleado.getApellido());
                    pstm.setString(4,empleado.getDepartamento());
                    pstm.setString(5,empleado.getTelefono());
                    pstm.setString(6,empleado.getEmail());
                    pstm.setString(7, empleado.getPasswordHash());
                    pstm.setString(8,empleado.getPuesto());
                    pstm.setString(9, empleado.getRol().name());
                    pstm.setInt(10,empleadoExistente.getIdEmpleado());

                    if(pstm.executeUpdate() > 0) {
                        empleadoActualizado = empleado;
                    }
                }catch (SQLException e){
                    throw new DAOException("Error al modificar el empleado: "+e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
                }
            }else{
                throw new DAOException("El empleado no existe", DAOErrorTipo.NOT_FOUND);
            }
        }
        return empleadoActualizado;
    }

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
                    empleado.setPasswordHash(rs.getString("password_hash"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                }
            }
        }catch (SQLException e){
            throw new DAOException("Error al buscar el empleado: "+e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleado;
    }

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
                empleado.setPasswordHash(rs.getString("password_hash"));
                empleado.setPuesto(rs.getString("puesto"));
                empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                empleados.add(empleado);
            }
        }catch (SQLException e){
            throw new DAOException("Error al listar los empleados: "+e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleados;
    }

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
                    empleado.setPasswordHash(rs.getString("password_hash"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setRol(TipoEmpleado.valueOf(rs.getString("rol")));
                }
            }
        } catch (SQLException e){
            throw new DAOException("Error al buscar el empleado por email: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empleado;
    }

}
