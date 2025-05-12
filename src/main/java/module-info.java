module org.dam.fcojavier.gestionpersonal {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires java.xml.bind;
    requires java.sql;


    opens org.dam.fcojavier.gestionpersonal to javafx.fxml;
    opens org.dam.fcojavier.gestionpersonal.controllers to javafx.fxml;
    opens org.dam.fcojavier.gestionpersonal.bbdd to java.xml.bind;

    exports org.dam.fcojavier.gestionpersonal;
    exports org.dam.fcojavier.gestionpersonal.controllers;

}