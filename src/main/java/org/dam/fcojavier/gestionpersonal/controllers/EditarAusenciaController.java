package org.dam.fcojavier.gestionpersonal.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.dam.fcojavier.gestionpersonal.model.Ausencia;
import org.dam.fcojavier.gestionpersonal.model.Empleado;

import java.time.LocalDate;

public class EditarAusenciaController {
    @FXML
    private ComboBox<Empleado> empleadoComboBox;
    @FXML
    private TextArea motivoTextArea;
    @FXML
    private DatePicker fechaInicioPicker;
    @FXML
    private DatePicker fechaFinPicker;
    
    private Ausencia ausencia;
    private DialogPane dialogPane;
    
    @FXML
    public void initialize() {
        fechaInicioPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(false); // Permite seleccionar cualquier fecha
            }
        });

        fechaFinPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(false); // Permite seleccionar cualquier fecha
            }
        });
    }
    
    public void setDialogPane(DialogPane dialogPane) {
        this.dialogPane = dialogPane;
    }
    
    public void setAusencia(Ausencia ausencia) {
        this.ausencia = ausencia;
        if (ausencia != null) {
            empleadoComboBox.setValue(ausencia.getEmpleado());
            motivoTextArea.setText(ausencia.getMotivo());
            fechaInicioPicker.setValue(ausencia.getFechaInicio());
            fechaFinPicker.setValue(ausencia.getFechaFin());
        }
    }
    
    public Ausencia getAusencia() {
        if (ausencia == null) {
            ausencia = new Ausencia();
        }
        
        ausencia.setEmpleado(empleadoComboBox.getValue());
        ausencia.setMotivo(motivoTextArea.getText());
        ausencia.setFechaInicio(fechaInicioPicker.getValue());
        ausencia.setFechaFin(fechaFinPicker.getValue());
        
        return ausencia;
    }
    
    private void validarFechas() {
        if (dialogPane != null) {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            boolean fechasValidas = true;
            
            LocalDate fechaInicio = fechaInicioPicker.getValue();
            LocalDate fechaFin = fechaFinPicker.getValue();
            
            if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
                fechasValidas = false;
            }
            
            okButton.setDisable(!fechasValidas);
        }
    }

    public void setEmpleados(ObservableList<Empleado> empleados) {
        empleadoComboBox.setItems(empleados);
    }

    public void configurarComboBoxEmpleados() {
        empleadoComboBox.setCellFactory(param -> new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre() + " " + empleado.getApellido());
                }
            }
        });
        
        empleadoComboBox.setButtonCell(new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean empty) {
                super.updateItem(empleado, empty);
                if (empty || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre() + " " + empleado.getApellido());
                }
            }
        });
    }
}