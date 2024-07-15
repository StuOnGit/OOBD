package controllers;

import com.jfoenix.controls.*;
import customComponents.TrattaHbox;
import data.Dipendente;
import data.Tratta;
import database.dao.TrattaDao;
import enumeration.DipendentiEnum;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utility.Refreshable;
import utility.UserRestricted;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerTratte implements Initializable, Refreshable<Tratta>, UserRestricted {

    @FXML
    private JFXComboBox<String> searchMode; // imposta la modalità di ricerca
    @FXML
    private JFXDatePicker dpk1, dpk2; // Selezionano la data di inizio e di fine per la ricerca
    @FXML
    private TextField searchBar; // barra di ricerca
    @FXML
    private JFXSpinner spinner; // spinner del caricamento. Se è visibile allora sta effettuando una richiesta al database
    @FXML
    private JFXButton cancelBtn; // elimina i vincoli di ricerca
    @FXML
    private AnchorPane mainPane;
    @FXML
    private JFXButton addBtn; // bottone per aggiungere tratte

    @FXML
    private JFXListView<TrattaHbox> listView; // mostra le varie tratte
    private List<TrattaHbox> localTratte; // lista locale delle tratte scaricate dal database
    private Dipendente loggedUser; // riferimento a utente loggato

    public boolean isRefreshing(){
        return spinner.isVisible();
    }

    public void search(KeyEvent k){
        String searchMode = this.searchMode.getValue();
        String text = searchBar.getText();
        listView.getItems().clear();
        listView.getItems().addAll(localTratte);
        switch (searchMode){
            case "Partenza" -> listView.getItems().removeIf(node -> !node.getTratta().getAereoportoPartenza().getCitta().toUpperCase().contains(text.toUpperCase())); // rimuovi se la card non contine il testo in searchBar
            case "Arrivo" -> listView.getItems().removeIf(node -> !node.getTratta().getAereoportoArrivo().getCitta().toUpperCase().contains(text.toUpperCase()));
            case "Compagnia" -> listView.getItems().removeIf(node -> !node.getTratta().getCompagnia().getNome().toUpperCase().contains(text.toUpperCase()));
            case "NumeroVolo" -> listView.getItems().removeIf(node -> !node.getTratta().getNumeroVolo().toUpperCase().contains(text.toUpperCase()));
        }
    } // applica i vincoli di ricerca

    public void datePick(ActionEvent e){
        refresh();
    } //seleziona la data

    public void canc(ActionEvent e){
        searchBar.setText("");
        dpk1.setValue(null);
        dpk2.setValue(null);
        searchMode.getSelectionModel().selectFirst();
        refresh();
    } // elimina i vincoli di ricerca

    @FXML
    public void mouseClick(MouseEvent e){
        if (listView.getSelectionModel().getSelectedItem() == null) return;
        Tratta tratta = listView.getSelectionModel().getSelectedItem().getTratta();
        if (e.getButton() == MouseButton.PRIMARY) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TratteInfo.fxml"));
                Parent parent = fxmlLoader.load();
                ControllerTratteInfo controller = fxmlLoader.getController();
                controller.initialize(tratta, loggedUser);
                controller.setMainWindow(cancelBtn.getScene().getWindow());

                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if(e.getButton() == MouseButton.SECONDARY){
            if(loggedUser.getRuolo() == DipendentiEnum.Amministratore || loggedUser.getRuolo() == DipendentiEnum.ResponsabileVoli){
                if (loggedUser.getCompagnia() == null || loggedUser.getCompagnia().equals(tratta.getCompagnia())) {
                    JFXButton elimina = new JFXButton("Elimina");
                    elimina.setStyle("-fx-background-radius: 0; -fx-font-size: 18; -fx-background-color: red; -fx-text-fill: white");
                    elimina.setOnAction(a -> {
                        try {
                            new TrattaDao().deleteTratta(tratta);
                            refresh();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                    JFXPopup popup = new JFXPopup(elimina);
                    popup.show(listView.getScene().getWindow(), e.getSceneX(), e.getSceneY(), JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 0, 0);
                }
            }
        }
    } // seleziona una tratta e mostra le informazioni

    @FXML
    private void add(ActionEvent e){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TratteAdd.fxml"));
            Parent parent = fxmlLoader.load();
            ControllerTratteAdd controller = fxmlLoader.getController();
            controller.setMainPane(mainPane);
            controller.setLoggedUser(loggedUser);
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.showAndWait();
            refresh();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    } // apre la scheda per creare una tratta

    public Task<List<Tratta>> refresh() {
        if (!isRefreshing()){
            listView.getItems().clear();
            spinner.setVisible(true);
            Task<List<Tratta>> task = new Task<>() {
                @Override
                protected List<Tratta> call() {
                    try {
                        return new TrattaDao().getTratteWithDate(dpk1.getValue(), dpk2.getValue());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                localTratte.clear();
                localTratte.addAll(task.getValue().stream().map(TrattaHbox::new).collect(Collectors.toList()));
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
        searchMode.getItems().addAll("Partenza", "Arrivo", "Compagnia", "NumeroVolo");
        searchMode.getSelectionModel().selectFirst();
        localTratte = new LinkedList<>();

        cancelBtn.disableProperty().bind(spinner.visibleProperty());
        searchMode.disableProperty().bind(spinner.visibleProperty());
        searchBar.disableProperty().bind(spinner.visibleProperty());
        dpk1.disableProperty().bind(spinner.visibleProperty());
        dpk2.disableProperty().bind(spinner.visibleProperty());
    }

    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUser = loggedUser;
        if (loggedUser.getRuolo() == DipendentiEnum.Amministratore || loggedUser.getRuolo() == DipendentiEnum.ResponsabileVoli){
            addBtn.setVisible(true);
        } else {
            addBtn.setVisible(false);
        }
    }
}
