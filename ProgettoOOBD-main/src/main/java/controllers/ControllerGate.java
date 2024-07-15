package controllers;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import customComponents.GateCard;
import customComponents.GateCardPopup;
import customComponents.TrattaHbox;
import data.*;
import database.dao.AeroportoDao;
import database.dao.CodaImbarcoDao;
import database.dao.GateDao;
import database.dao.TrattaDao;
import enumeration.DipendentiEnum;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.util.Pair;
import utility.Refreshable;
import utility.UserRestricted;

import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// controller per il pannello dei gate
public class ControllerGate implements Initializable, Refreshable<Gate>, UserRestricted {

    @FXML
    private ScrollPane scroll; // scroll pane che contiene il flowPane
    private FlowPane flowPane; // flowPane che contiene le gateCard
    @FXML
    private JFXComboBox<String> searchMode; // vincoli di ricerca
    @FXML
    private JFXCheckBox occupatiCheck, liberiCheck, chiusiCheck; // vincoli di ricerca in base allo stato
    @FXML
    private TextField searchBar; // barra di ricerca
    @FXML
    private JFXButton cancelBtn; // elimina i vincoli di ricerca
    @FXML
    private Label nessunGate; // label che viene visualizzata quando una ricerca è nulla
    @FXML
    private JFXSpinner spinner; // spinner che viene visualizzato durante il refresh
    private List<GateCard> localGate; // lista locale di gate salvati dal database
    private Dipendente loggedUser; // utente loggato


    // Filtri sullo stato: occupato ecc...
    public void statusFilter(ActionEvent e){
        refresh();
    }
   // Ricerca in base alla searchBar
    public void search(KeyEvent k){
        String text = searchBar.getText();

        flowPane.getChildren().clear();
        flowPane.getChildren().addAll(localGate);

        switch (this.searchMode.getValue()){
            case "Codice" -> {
                flowPane.getChildren().removeIf(node -> !((GateCard) node).getGate().getGateCode().toUpperCase().contains(text.toUpperCase())); // rimuovi se la card non contine il testo in searchBar
            }
            case "Coda" -> {
                flowPane.getChildren().removeIf(node ->{
                    for(CodaImbarco c : ((GateCard) node).getGate().getCodeImbarco()){
                        if (c.getClasse().toString().contains(text.toUpperCase())) return false;
                    }
                    return true;
                });
            }
        }
        nessunGate.setVisible(flowPane.getChildren().isEmpty());
    }

    // mostra la lista di tratte disponibili per l'imbarco
    private void showImpostaTratta(GateCard gCard, GateCardPopup popup){
        JFXListView<TrattaHbox> l = new JFXListView<>();
        l.setPrefSize(820, 300);
        try {
            Aeroporto aeroportoGestito = new AeroportoDao().getAeroportoGestito();
            new TrattaDao().getTratteAperte(loggedUser.getCompagnia()).stream()
                    .filter(t -> t.dateDistance() >= 0 && t.getAereoportoPartenza().equals(aeroportoGestito))
                    .forEach(t -> {
                l.getItems().add(new TrattaHbox(t));
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        JFXAlert alert = new JFXAlert(flowPane.getScene().getWindow());
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(l);
        alert.initModality(Modality.NONE);
        l.setOnMouseClicked( e1 -> {
            TrattaHbox t = l.getSelectionModel().getSelectedItem();
            if (t != null){
                impostaCode(gCard, alert, popup, t.getTratta());
            }
        });
        alert.showAndWait();
    }

    // imposta le code per l'imbarco dopo aver scelto la tratta
    private void impostaCode(GateCard gCard, JFXAlert<Void> alert, GateCardPopup popup, Tratta tratta){
        try {
            CodaImbarcoDao codaImbarcoDao = new CodaImbarcoDao();
            gCard.getGate().setTratta(tratta, codaImbarcoDao.getByTratta(tratta));
            new TrattaDao().update(tratta);
            for(CodaImbarco c : gCard.getGate().getCodeImbarco()){
                codaImbarcoDao.apriCoda(c);
            }
            new GateDao().update(gCard.getGate());
            gCard.updateLabels();
            popup.setOccupato();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        alert.hide();
    }

    // conclude l'imbarco e aggiorna il database
    public void terminaImbarco(GateCard gCard, GateCardPopup popup){
        Gate g = gCard.getGate();
        try {
            Pair<Tratta, List<CodaImbarco>> p = g.end();
            new TrattaDao().update(p.getKey());
            new GateDao().update(g);
            CodaImbarcoDao cDao = new CodaImbarcoDao();
            for (CodaImbarco c: p.getValue()) {
                cDao.chiudiCoda(c);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        gCard.updateLabels();
        popup.setAperto();
    }

    // chiude il gate
    public void chiudiGate(GateCard gCard, GateCardPopup popup){
        gCard.getGate().close();
        try {
            new GateDao().update(gCard.getGate());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        popup.setChiuso();
        gCard.updateLabels();
    }

    // Rende il gate libero
    private void apriGate(GateCard gCard, GateCardPopup popup) {
        gCard.getGate().open();
        try {
            new GateDao().update(gCard.getGate());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        popup.setAperto();
        gCard.updateLabels();
    }

    public boolean isRefreshing(){
        return spinner.isVisible();
    }

    public Task<List<Gate>> refresh() {
        if (!isRefreshing()){
            flowPane.getChildren().clear();
            spinner.setVisible(true);
            nessunGate.setVisible(false);
            Task<List<Gate>> task = new Task<>() {
                @Override
                protected List<Gate> call() {
                    try {
                       return new GateDao().getGateWithStatus(occupatiCheck.isSelected(), liberiCheck.isSelected(), chiusiCheck.isSelected());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return null;
                }
            };
            task.setOnSucceeded(t -> {
                localGate.clear();
                localGate.addAll(task.getValue().stream().map(g -> {
                    GateCard gCard = new GateCard(g);
                    GateCardPopup popup = new GateCardPopup(gCard);
                    if (loggedUser.getRuolo() != DipendentiEnum.Amministratore
                            && loggedUser.getRuolo() != DipendentiEnum.AddettoImbarco
                            && loggedUser.getRuolo() != DipendentiEnum.ResponsabileVoli) {

                        popup.getVBox().setDisable(true);
                    } else {
                        if (gCard.getGate().getTratta() != null){
                            if (loggedUser.getCompagnia() == null || gCard.getGate().getTratta().getCompagnia().equals(loggedUser.getCompagnia())) {
                                popup.getVBox().setDisable(false);
                            }
                        }
                    }

                    popup.setImpostaTratta(e -> showImpostaTratta(gCard, popup));
                    popup.setTerminaImbarco(e -> terminaImbarco(gCard, popup));
                    popup.setChiudiGate(e -> chiudiGate(gCard, popup));
                    popup.setApriGate(e -> apriGate(gCard, popup));

                    return gCard;
                }).collect(Collectors.toList()));
                search(null);
                spinner.setVisible(false);
            });

            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            return task;
        } else return null;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchMode.getItems().addAll("Codice", "Coda");
        searchMode.getSelectionModel().selectFirst();
        occupatiCheck.setSelected(true);
        liberiCheck.setSelected(true);
        chiusiCheck.setSelected(true);
        localGate = new LinkedList<>();

        cancelBtn.setOnAction(e ->{ // Cancella il testo e annulla la ricerca
            searchBar.setText("");
            refresh();
        });

        // crea un pannello e lo inserisce in scroll
        flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(5, 10, 5, 20));
        flowPane.setStyle("-fx-background-color: transparent");
        scroll.setContent(flowPane);

        chiusiCheck.disableProperty().bind(spinner.visibleProperty());
        occupatiCheck.disableProperty().bind(spinner.visibleProperty());
        liberiCheck.disableProperty().bind(spinner.visibleProperty());
        searchBar.disableProperty().bind(spinner.visibleProperty());
        cancelBtn.disableProperty().bind(spinner.visibleProperty());
        searchMode.disableProperty().bind(spinner.visibleProperty());
    }

    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUser = loggedUser;
    }
}
