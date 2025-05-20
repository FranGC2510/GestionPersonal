module org.dam.fcojavier.gestionpersonal {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires java.base;
    requires java.xml.bind;
    requires java.sql;
    requires org.slf4j;


    opens org.dam.fcojavier.gestionpersonal to javafx.fxml;
    opens org.dam.fcojavier.gestionpersonal.bbdd to java.xml.bind;
    opens org.dam.fcojavier.gestionpersonal.controllers to javafx.fxml;
    opens org.dam.fcojavier.gestionpersonal.model to javafx.base;

    exports org.dam.fcojavier.gestionpersonal;
    exports org.dam.fcojavier.gestionpersonal.controllers;
    exports org.dam.fcojavier.gestionpersonal.model;
    exports org.dam.fcojavier.gestionpersonal.enums;

}