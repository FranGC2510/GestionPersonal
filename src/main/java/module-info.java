module org.dam.fcojavier.gestionpersonal {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires java.xml.bind;
    requires java.sql;


    opens org.dam.fcojavier.gestionpersonal to javafx.fxml;
    exports org.dam.fcojavier.gestionpersonal;
}