module org.dam.fcojavier.gestionpersonal {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.dam.fcojavier.gestionpersonal to javafx.fxml;
    exports org.dam.fcojavier.gestionpersonal;
}