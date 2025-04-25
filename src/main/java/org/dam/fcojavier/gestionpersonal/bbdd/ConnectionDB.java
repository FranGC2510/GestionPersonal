package org.dam.fcojavier.gestionpersonal.bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private final static String FILE="connection.xml";
    private static Connection con;
    private static ConnectionDB _instance;

    private ConnectionDB() {
        ConnectionProperties properties = XMLManager.readXML(new ConnectionProperties(),FILE);
        try {
            con = DriverManager.getConnection(properties.getURL(),properties.getUser(),properties.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            con=null;
        }
    }

    public static Connection getConnection() {
        if(_instance==null) {
            _instance = new ConnectionDB();
        }
        return con;
    }

    public static void closeConnection() {
        if(con!=null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
