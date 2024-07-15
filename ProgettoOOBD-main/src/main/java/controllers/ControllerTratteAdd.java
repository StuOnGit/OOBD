package controllers;

import com.jfoenix.controls.*;
import customComponents.Toast;
import data.*;
import database.dao.*;
import enumeration.CodeEnum;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import utility.IdFactory;
import utility.UserRestricted;
import utility.WindowDragger;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

// scheda per aggiungere tratte
public class ControllerTratteAdd extends WindowDragger implements Initializable, UserRestricted {
    @FXML
    private JFXCheckBox
            diversamenteAbili,
            famiglie,
            business,
            priorty,
            economy; // se selezionate aggiunge la coda di imbarco

    @FXML
    private JFXComboBox<Compagnia> compagnia; //sceglie la compagnia che organizza la tratta
    @FXML
    private JFXComboBox<Aeroporto> partenza, arrivo; // sceglie aeroporto di arrivo e partenza
    @FXML
    private JFXDatePicker data; // data di partenza
    @FXML
    private JFXTimePicker ora; // ora di partenza
    @FXML
    private JFXTextField durata, posti;
    @FXML
    private JFXCheckBox conclusa;

    @FXML
    private JFXButton conferma; // aggiunge la tratta


    private Aeroporto aeroportoGestito;
    private List<Aeroporto> aeroporti;

    private Pane mainPane;

    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;
    }


    @FXML
    public void disableCode(ActionEvent e){
        if (conclusa.isSelected()) {
            diversamenteAbili.setSelected(false);
            famiglie.setSelected(false);
            business.setSelected(false);
            priorty.setSelected(false);
            economy.setSelected(false);
        } else {
            diversamenteAbili.setSelected(true);
            famiglie.setSelected(true);
            business.setSelected(true);
            priorty.setSelected(true);
            economy.setSelected(true);
        }
    }

    @FXML
    public void conferma(ActionEvent e){
        Tratta tratta = new Tratta(
                new IdFactory().newNumeroVolo(compagnia.getValue()),
                data.getValue(),
                ora.getValue(),
                Integer.parseInt(durata.getText()),
                0,
                false,
                null,
                compagnia.getValue(),
                partenza.getValue(),
                arrivo.getValue(),
                Integer.parseInt(posti.getText())
        );
        Toast toast = new Toast(mainPane);
        try {
            new TrattaDao().insert(tratta);
            CodaImbarcoDao cDao = new CodaImbarcoDao();
            if (diversamenteAbili.isSelected()) cDao.addNew(CodeEnum.DIVERSAMENTE_ABILI, tratta);
            if (famiglie.isSelected()) cDao.addNew(CodeEnum.FAMIGLIE, tratta);
            if (business.isSelected()) cDao.addNew(CodeEnum.BUSINESS, tratta);
            if (priorty.isSelected()) cDao.addNew(CodeEnum.PRIORITY, tratta);
            if (economy.isSelected()) cDao.addNew(CodeEnum.ECONOMY, tratta);

            toast.show("Tratta aggiunta con successo");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            toast.show("Errore tratta non aggiunta");
        }
        ((Node) e.getSource()).getScene().getWindow().hide();
    }

    public void annulla(ActionEvent e){
        ((Node) e.getSource()).getScene().getWindow().hide();
    }

    // almeno uno tra aeroporto di partenza e di arrivo deve essere aeroportogestito
    public void controlloAeroporto(ActionEvent e){
        if (partenza.getValue() != null && arrivo.getValue() != null){
            if (!partenza.getValue().equals(aeroportoGestito) && !arrivo.getValue().equals(aeroportoGestito)){
                if (((Node) e.getSource()).getId().equals(partenza.getId())) arrivo.getSelectionModel().select(aeroportoGestito);
                else partenza.getSelectionModel().select(aeroportoGestito);
            }else if (partenza.getValue().equals(arrivo.getValue())){
                if (((Node) e.getSource()).getId().equals(partenza.getId())) {
                    arrivo.getSelectionModel().clearSelection();
                }
                else partenza.getSelectionModel().clearSelection();
            }
        }
    }


    @Override
    public void setOffset(MouseEvent e) {
        super.setOffset(e);
    }

    @Override
    public void moveWindow(MouseEvent e) {
        super.moveWindow(e);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conferma.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(
                        compagnia.valueProperty(),
                        partenza.valueProperty(),
                        arrivo.valueProperty(),
                        data.valueProperty(),
                        arrivo.valueProperty(),
                        durata.textProperty(),
                        posti.textProperty(),
                        diversamenteAbili.selectedProperty(),
                        famiglie.selectedProperty(),
                        economy.selectedProperty(),
                        business.selectedProperty(),
                        conclusa.selectedProperty(),
                        priorty.selectedProperty());
            }
            @Override
            protected boolean computeValue() {
                return compagnia.getValue() == null ||
                        partenza.getValue() == null ||
                        arrivo.getValue() == null ||
                        data.getValue() == null ||
                        ora.getValue() == null ||
                        arrivo.getValue() == null ||
                        durata.getText().isEmpty() ||
                        posti.getText().isEmpty() ||
                        (!conclusa.isSelected() &&
                        !(
                                (diversamenteAbili.isSelected() ||
                                famiglie.isSelected() ||
                                priorty.isSelected() ||
                                business.isSelected() ||
                                economy.isSelected())
                        ));
            }
        });

        diversamenteAbili.disableProperty().bind(conclusa.selectedProperty());
        famiglie.disableProperty().bind(conclusa.selectedProperty());
        business.disableProperty().bind(conclusa.selectedProperty());
        priorty.disableProperty().bind(conclusa.selectedProperty());
        economy.disableProperty().bind(conclusa.selectedProperty());


        durata.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                durata.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        posti.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                posti.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        try {
            aeroportoGestito = new AeroportoDao().getAeroportoGestito();
            aeroporti = new AeroportoDao().getAeroporti();
            partenza.getItems().addAll(aeroporti);
            arrivo.getItems().addAll(aeroporti);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        if (loggedUser.getCompagnia() == null) {
            try {
                compagnia.getItems().addAll(new CompagniaDao().getCompagnie());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else
            compagnia.getItems().addAll(loggedUser.getCompagnia());
    }
}
