package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.interfaces.CrudDAO;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase que implementa el acceso a datos para la entidad Empresa.
 * Proporciona operaciones CRUD y consultas específicas para gestionar las empresas
 * en la base de datos. Implementa la interfaz CrudDAO para operaciones básicas.
 *
 */
public class EmpresaDAO implements CrudDAO<Empresa> {

    /** Consulta SQL para insertar una nueva empresa */
    private final String insert_SQL = "INSERT INTO empresa (nombre, direccion, telefono, email, password_hash) VALUES (?, ?, ?, ?, ?)";
    
    /** Consulta SQL para actualizar una empresa existente */
    private final String update_SQL = "UPDATE empresa SET nombre = ?, direccion = ?, telefono = ?, email = ?, password_hash = ? WHERE id_empresa = ?";
    
    /** Consulta SQL para eliminar una empresa */
    private final String delete_SQL = "DELETE FROM empresa WHERE id_empresa = ?";
    
    /** Consulta SQL para buscar una empresa por su ID */
    private final String findById_SQL = "SELECT * FROM empresa WHERE id_empresa = ?";
    
    /** Consulta SQL para obtener todas las empresas */
    private final String findAll_SQL = "SELECT * FROM empresa";
    
    /** Consulta SQL para buscar una empresa por su email */
    private final String findByEmail_SQL = "SELECT * FROM empresa WHERE email = ?";

    /**
     * Inserta una nueva empresa en la base de datos.
     * Verifica que la empresa no exista previamente por su email.
     *
     * @param empresa La empresa a insertar
     * @return La empresa insertada con su ID generado, o null si ya existe
     * @throws DAOException Si ocurre un error durante la inserción o si ya existe una empresa con el mismo email
     */
    @Override
    public Empresa insert(Empresa empresa) throws DAOException {
        if(empresa!=null && findByEmail(empresa.getEmail())==null) {
            try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(insert_SQL, PreparedStatement.RETURN_GENERATED_KEYS)){
                pstm.setString(1, empresa.getNombre());
                pstm.setString(2, empresa.getDireccion());
                pstm.setString(3, empresa.getTelefono());
                pstm.setString(4, empresa.getEmail());
                pstm.setString(5, empresa.getPassword());
                pstm.executeUpdate();
                ResultSet rs=pstm.getGeneratedKeys();
                if(rs.next()){
                    empresa.setIdEmpresa(rs.getInt(1));
                }
            }catch (SQLException e){
                throw new DAOException("Error al insertar la empresa: "+e.getMessage(), DAOErrorTipo.INSERT_ERROR);
            }
        }else{
            empresa=null;
        }
        return empresa;
    }

    /**
     * Actualiza una empresa existente en la base de datos.
     *
     * @param empresa La empresa con los nuevos datos
     * @return La empresa actualizada, o null si no existe
     * @throws DAOException Si ocurre un error durante la actualización o si la empresa no existe
     */
    @Override
    public Empresa update(Empresa empresa) throws DAOException {
        Empresa empresaActualizada=null;
        if(empresa != null) {
            Empresa empresaExistente = findById(empresa.getIdEmpresa());
            if(empresaExistente != null) {
                try(PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(update_SQL)) {
                    pstm.setString(1, empresa.getNombre());
                    pstm.setString(2, empresa.getDireccion());
                    pstm.setString(3, empresa.getTelefono());
                    pstm.setString(4, empresa.getEmail());
                    pstm.setString(5, empresa.getPassword());
                    pstm.setInt(6, empresaExistente.getIdEmpresa());
                    
                    if(pstm.executeUpdate() > 0) {
                        empresaActualizada = empresa;
                    }
                } catch (SQLException e) {
                    throw new DAOException("Error al modificar la empresa: "+e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
                }
            }else{
                throw new DAOException("La empresa no existe", DAOErrorTipo.NOT_FOUND);
            }
        }
        return empresaActualizada;
    }

    /**
     * Elimina una empresa de la base de datos.
     * La eliminación es en cascada y afectará a todos los registros relacionados.
     *
     * @param empresa La empresa a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws DAOException Si ocurre un error durante la eliminación
     */
    @Override
    public boolean delete(Empresa empresa) throws DAOException {
        boolean deleted=false;
        if(empresa!=null){
            Empresa empresaEncontrada= findById(empresa.getIdEmpresa());
            if(empresaEncontrada!=null){
                try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(delete_SQL)){
                    pstm.setInt(1, empresaEncontrada.getIdEmpresa());
                    pstm.executeUpdate();
                    deleted=true;
                }catch (SQLException e){
                    throw new DAOException("Error al borrar la empresa: "+e.getMessage(), DAOErrorTipo.DELETE_ERROR);
                }
            }
        }
        return deleted;
    }

    /**
     * Busca una empresa por su ID.
     *
     * @param id ID de la empresa a buscar
     * @return La empresa encontrada, o null si no existe
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    @Override
    public Empresa findById(int id) throws DAOException {
        Empresa empresa = null;
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findById_SQL)){
            pstm.setInt(1, id);
            ResultSet rs=pstm.executeQuery();
            if(rs.next()){
                empresa=new Empresa();
                empresa.setIdEmpresa(rs.getInt("id_empresa"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDireccion(rs.getString("direccion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setEmail(rs.getString("email"));
                empresa.setPassword(rs.getString("password_hash"));
            }
        }catch (SQLException e){
            throw new DAOException("Error al buscar la empresa: "+e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empresa;
    }

    /**
     * Obtiene todas las empresas registradas en la base de datos.
     *
     * @return Lista de todas las empresas
     * @throws DAOException Si ocurre un error al obtener los datos
     */
    @Override
    public List<Empresa> findAll() throws DAOException {
        List<Empresa> empresas = new ArrayList<>();
        try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(findAll_SQL)){
            ResultSet rs=pstm.executeQuery();
            while(rs.next()){
                Empresa empresa=new Empresa();
                empresa.setIdEmpresa(rs.getInt("id_Empresa"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDireccion(rs.getString("direccion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setEmail(rs.getString("email"));
                empresa.setPassword(rs.getString("password_hash"));
                empresas.add(empresa);
            }
        }catch (SQLException e){
            throw new DAOException("Error al listar las empresas: "+e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empresas;
    }

    /**
     * Busca una empresa por su dirección de email.
     * Este método es útil para verificar la existencia de una empresa
     * antes de su inserción o para el proceso de inicio de sesión.
     *
     * @param email Email de la empresa a buscar
     * @return La empresa encontrada, o null si no existe
     * @throws DAOException Si ocurre un error durante la búsqueda
     */
    public Empresa findByEmail(String email) throws DAOException {
        Empresa empresa = null;
        try (PreparedStatement pstm = ConnectionDB.getConnection().prepareStatement(findByEmail_SQL)){
            pstm.setString(1, email);
            ResultSet rs = pstm.executeQuery();
            if(rs.next()){
                empresa = new Empresa();
                empresa.setIdEmpresa(rs.getInt("id_empresa"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDireccion(rs.getString("direccion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setEmail(rs.getString("email"));
                empresa.setPassword(rs.getString("password_hash"));
            }
        } catch (SQLException e){
            throw new DAOException("Error al buscar la empresa por email: " + e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empresa;
    }
}