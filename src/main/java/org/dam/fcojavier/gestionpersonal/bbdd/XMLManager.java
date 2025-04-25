package org.dam.fcojavier.gestionpersonal.bbdd;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

/**
 * Clase que proporciona métodos genéricos para la serialización y deserialización de objetos en formato XML
 * utilizando JAXB.
 */
public class XMLManager {
    /**
     * Guarda un objeto en un archivo XML.
     * @param objeto   El objeto que se desea guardar en formato XML.
     * @param fileName El nombre del archivo XML donde se guardará el objeto.
     * @param <T>      Tipo genérico que representa el objeto a serializar.
     * @return true si el proceso de guardado fue exitoso, false en caso contrario.
     */
    public static <T> boolean writeXML(T objeto, String fileName) {
        boolean result = false;
        try {
            //Paso 1: Crear el contexto de JaxB para la clase que queremos serializar
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());

            //Paso 2: proceso Marshalling: convertir objeto en XML
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(objeto,new File(fileName));
            result = true;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Lee un archivo XML y lo convierte en un objeto Java.
     * @param objeto   Una instancia del tipo de objeto esperado (se usa para obtener la clase).
     * @param fileName El nombre del archivo XML desde donde se cargará el objeto.
     * @param <T>      Tipo genérico que representa el objeto a deserializar.
     * @return El objeto deserializado desde el XML, o null si hubo un error.
     */
    public static <T> T readXML(T objeto, String fileName) {
        T result = null;
        File file = new File(fileName);
        if(!existeXML(fileName) || file.length() == 0) {
            try{
                file.createNewFile();
                writeXML(objeto, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            //Paso 1: Crear el contexto de JaxB para la clase que queremos serializar
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());

            //Paso 2: Unmarshaling: leer XML y convertirlo a un objeto
            Unmarshaller unmarshaller = context.createUnmarshaller();
            result = (T) unmarshaller.unmarshal(new File(fileName));


        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Verifica si existe un archivo XML con el nombre especificado.
     *
     * @param fileName Nombre del archivo XML a verificar
     * @return true si el archivo existe, false en caso contrario
     */
    private static boolean existeXML(String fileName) {
        boolean result = false;
        File file = new File(fileName);
        if(file.exists()) {
            result = true;
        }
        return result;
    }
}
