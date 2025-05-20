package org.dam.fcojavier.gestionpersonal.bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que implementa el patrón Singleton para gestionar la conexión a la base de datos.
 * Proporciona métodos para obtener y cerrar una conexión a la base de datos utilizando
 * propiedades de conexión almacenadas en un archivo XML.
 *
 */
public class ConnectionDB {
    /** Nombre del archivo XML que contiene las propiedades de conexión */
    private final static String FILE = "connection.xml";
    
    /** Instancia de la conexión a la base de datos */
    private static Connection con;
    
    /** Instancia única de la clase (patrón Singleton) */
    private static ConnectionDB _instance;

    /**
     * Constructor privado que inicializa la conexión a la base de datos.
     * Lee las propiedades de conexión desde un archivo XML y establece la conexión.
     */
    private ConnectionDB() {
        ConnectionProperties properties = XMLManager.readXML(new ConnectionProperties(),FILE);
        try {
            con = DriverManager.getConnection(properties.getURL(),properties.getUser(),properties.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
        }
    }

    /**
     * Obtiene una conexión a la base de datos.
     * Si no existe una instancia previa, crea una nueva.
     *
     * @return Connection objeto que representa la conexión a la base de datos
     */
    public static Connection getConnection() {
        if(_instance == null) {
            _instance = new ConnectionDB();
        }
        return con;
    }

    /**
     * Cierra la conexión a la base de datos si está abierta.
     * Este método debe llamarse cuando ya no se necesite la conexión
     * para liberar los recursos del sistema.
     */
    public static void closeConnection() {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}