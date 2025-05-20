package org.dam.fcojavier.gestionpersonal.bbdd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Clase que representa las propiedades de conexión a una base de datos MySQL.
 * Esta clase está diseñada para ser serializada/deserializada mediante JAXB para 
 * la lectura/escritura de configuraciones en formato XML.
 *
 */
@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionProperties implements Serializable {
    
    /** Identificador de versión para la serialización */
    private static final long serialVersionUID = 1L;
    
    /** Dirección del servidor de la base de datos */
    private String server;
    
    /** Puerto de conexión al servidor de base de datos */
    private String port;
    
    /** Nombre de la base de datos */
    private String dataBase;
    
    /** Nombre de usuario para la conexión */
    private String user;
    
    /** Contraseña para la conexión */
    private String password;

    /**
     * Constructor por defecto.
     * Requerido para la serialización JAXB.
     */
    public ConnectionProperties() {
    }

    /**
     * Constructor con todos los parámetros de conexión.
     *
     * @param server Dirección del servidor de base de datos
     * @param port Puerto de conexión
     * @param dataBase Nombre de la base de datos
     * @param user Usuario de la base de datos
     * @param password Contraseña del usuario
     */
    public ConnectionProperties(String server, String port, String dataBase, String user, String password) {
        this.server = server;
        this.port = port;
        this.dataBase = dataBase;
        this.user = user;
        this.password = password;
    }

    /**
     * Obtiene la dirección del servidor.
     * @return Dirección del servidor
     */
    public String getServer() {
        return server;
    }

    /**
     * Establece la dirección del servidor.
     * @param server Nueva dirección del servidor
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Obtiene el puerto de conexión.
     * @return Puerto de conexión
     */
    public String getPort() {
        return port;
    }

    /**
     * Establece el puerto de conexión.
     * @param port Nuevo puerto de conexión
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Obtiene el nombre de la base de datos.
     * @return Nombre de la base de datos
     */
    public String getDataBase() {
        return dataBase;
    }

    /**
     * Establece el nombre de la base de datos.
     * @param dataBase Nuevo nombre de la base de datos
     */
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    /**
     * Obtiene el nombre de usuario.
     * @return Nombre de usuario
     */
    public String getUser() {
        return user;
    }

    /**
     * Establece el nombre de usuario.
     * @param user Nuevo nombre de usuario
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Obtiene la contraseña.
     * @return Contraseña del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña.
     * @param password Nueva contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Genera una representación en cadena de texto de las propiedades de conexión.
     * @return Cadena con los valores de todas las propiedades
     */
    @Override
    public String toString() {
        return "ConnectionProperties{" +
                "server='" + server + '\'' +
                ", port='" + port + '\'' +
                ", dataBase='" + dataBase + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * Construye y devuelve la URL de conexión JDBC completa para MySQL.
     * @return URL de conexión en formato jdbc:mysql://servidor:puerto/basedatos
     */
    public String getURL() {
        return "jdbc:mysql://" + server + ":" + port + "/" + dataBase;
    }
}