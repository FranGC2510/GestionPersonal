package org.dam.fcojavier.gestionpersonal.DAOs;

import org.dam.fcojavier.gestionpersonal.bbdd.ConnectionDB;
import org.dam.fcojavier.gestionpersonal.enums.DAOErrorTipo;
import org.dam.fcojavier.gestionpersonal.exceptions.DAOException;
import org.dam.fcojavier.gestionpersonal.interfaces.CrudDAO;
import org.dam.fcojavier.gestionpersonal.model.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO implements CrudDAO<Empresa> {

    private final String insert_SQL = "INSERT INTO empresa (nombre, direccion, telefono, email, password_hash) VALUES (?, ?, ?, ?, ?)";
    private final String update_SQL = "UPDATE empresa SET nombre = ?, direccion = ?, telefono = ?, email = ?, password_hash = ? WHERE id_Empresa = ?";
    private final String delete_SQL = "DELETE FROM empresa WHERE id_Empresa = ?";
    private final String findByName_SQL = "SELECT * FROM empresa WHERE nombre = ?";
    private final String findAll_SQL = "SELECT * FROM empresa";
    @Override
    public Empresa insert(Empresa empresa) throws DAOException {
        if(empresa!=null && findByName(empresa.getNombre())==null) {
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
                throw new DAOException(e.getMessage(), DAOErrorTipo.INSERT_ERROR);
            }
        }else{
            empresa=null;
        }
        return empresa;
    }

    @Override
    public Empresa update(Empresa empresa) throws DAOException {
        Empresa empresaActualizada=null;
        if(empresa != null) {
            Empresa empresaExistente = findByName(empresa.getNombre());
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
                    throw new DAOException(e.getMessage(), DAOErrorTipo.UPDATE_ERROR);
                }
            }
        }
        return empresaActualizada;
    }

    @Override
    public boolean delete(Empresa empresa) throws DAOException {
        boolean deleted=false;
        if(empresa!=null){
            Empresa empresaEncontrada=findByName(empresa.getNombre());
            if(empresaEncontrada!=null){
                try(PreparedStatement pstm=ConnectionDB.getConnection().prepareStatement(delete_SQL)){
                    pstm.setInt(1, empresaEncontrada.getIdEmpresa());
                    pstm.executeUpdate();
                    deleted=true;
                }catch (SQLException e){
                    throw new DAOException(e.getMessage(), DAOErrorTipo.DELETE_ERROR);
                }
            }
        }
        return deleted;
    }

    @Override
    public Empresa findByName(String nombre) throws DAOException {
        Empresa empresa = null;
        Connection con = ConnectionDB.getConnection();
        try{
            PreparedStatement stmt=con.prepareStatement(findByName_SQL);
            stmt.setString(1, nombre);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                empresa=new Empresa();
                empresa.setIdEmpresa(rs.getInt("id_Empresa"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDireccion(rs.getString("direccion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setEmail(rs.getString("email"));
                empresa.setPassword(rs.getString("password_hash"));
            }
        }catch (SQLException e){
            throw new DAOException(e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empresa;
    }

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
            throw new DAOException(e.getMessage(), DAOErrorTipo.NOT_FOUND);
        }
        return empresas;
    }
}