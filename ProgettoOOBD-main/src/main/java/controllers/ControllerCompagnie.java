package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import customComponents.CompagniaCard;
import data.Aeroporto;
import data.Compagnia;
import data.Dipendente;
import database.dao.AeroportoDao;
import database.dao.CompagniaDao;
import enumeration.DipendentiEnum;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utility.Refreshable;
import utility.UserRestricted;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerCompagnie implements Initializable, Refreshable<Compagnia>, UserRestricted {
    /**
     * @param scroll rappresenta un pannello di scroll.
     * @param searchMode permette la tipologia di ricerca.
     * @param aeroportoGestito mostra che aeroporto è gestito.
     * @param flowPane pannello interno contenente componenti grafiche.
     * @param spinner spinner di caricamento.
     * @param localCompagnie lista di Compagnie gestite dall'aeroporto.
     * @param mainPane pannello principale.
     * @param searchBar barra di ricerca.
     * @param loggedUser utente che utilizza il programma.
     * @param addBtn bottone di aggiunta compagnia.
     * */
    @FXML
    private ScrollPane scroll;
    @FXML
    private JFXComboBox<String> searchMode;
    @FXML
    private Label aeroportoGestito;

    private FlowPane flowPane;
    @FXML
    private JFXSpinner spinner;
    private List<CompagniaCard> localCompagnie;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField searchBar;
    private Dipendente loggedUser;
    @FXML
    private JFXButton addBtn;

    /**
     * apre una nuova finestra "CompagniaAdd", alla sua chiusura refresha la GUI principale.
     * */
    @FXML
    private void add(ActionEvent e){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CompagniaAdd.fxml"));
            Parent parent = fxmlLoader.load();
            ControllerCompagnieAdd controller = fxmlLoader.getController();
            controller.setMainPane(mainPane);
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
    }
    /**
     * setta alle impostazioni iniziali la ricerca.
     * */
    public void canc(ActionEvent e){
        searchBar.setText("");
        searchMode.getSelectionModel().selectFirst();
        refresh();
    }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia "Refreshable"
     * informa se la GUI principale sta refreshando.
     * */
    public boolean isRefreshing(){
        return spinner.isVisible();
    }

    /**
     * funzione necessaria dall'implementazione della classe "Refreshable":
     * ricarica la pagina caricando le ultime informazioni e le setta.
     */
    public Task<List<Compagnia>> refresh() {
        if (!isRefreshing()){
            flowPane.getChildren().clear();
            spinner.setVisible(true);
            Task<List<Compagnia>> task = new Task<>() {
                @Override
                protected List<Compagnia> call() {
                    try {
                        return new CompagniaDao().getCompagnie();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                localCompagnie.clear();
                localCompagnie.addAll(task.getValue().stream().map(CompagniaCard::new).collect(Collectors.toList()));
                search(null);
                spinner.setVisible(false);
            });
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            return task;
        } else return null;
    }
    /**
     * ricerca locale, si attiva ogni volta che l'utente inserisce un carattere
     * all'interno della searchBar e in base alla tipologia di ricerca restituisce il risultato.
     */
    public void search(KeyEvent k) {
        String searchMode = this.searchMode.getValue();
        String text = searchBar.getText();
        flowPane.getChildren().clear();
        flowPane.getChildren().addAll(localCompagnie);
        switch (searchMode){
            case "Nome" -> flowPane.getChildren().removeIf(node -> !((CompagniaCard) node).getCompagnia().getNome().toUpperCase().contains(text.toUpperCase())); // rimuovi se la card non contine il testo in searchBar
            case "ICAO" -> flowPane.getChildren().removeIf(node -> !((CompagniaCard) node).getCompagnia().getSigla().toUpperCase().contains(text.toUpperCase()));
            case "Nazione" -> flowPane.getChildren().removeIf(node -> !((CompagniaCard) node).getCompagnia().getNazione().toUpperCase().contains(text.toUpperCase()));
        }
    }
/**
 * funzione necessaria dall'implementazione dell'interfaccia 'Initializable':
 * setta la comboBox "searchMode" , il flowPane e effettua una ricerca al Database
 * per settare l'aeroporto gestito.
 * Setta infine lo scrollPane, inizializza quindi la lista contentente le "CompagnieCard".
 */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchMode.getItems().addAll("Nome", "ICAO", "Nazione");
        searchMode.getSelectionModel().selectFirst();

        flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(5, 10, 5, 20));
        flowPane.setStyle("-fx-background-color: transparent");

        try {
            Aeroporto a = new AeroportoDao().getAeroportoGestito();
            aeroportoGestito.setText(a.getCitta() + "-" + a.getNome() + " (" + a.getCodiceICAO() + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        scroll.setContent(flowPane);
        localCompagnie = new LinkedList<>();
    }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia UserRestricted:
     * non permette l'aggiunta di compagnie agli utenti non autorizzati
     */
    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUser = loggedUser;
        if (loggedUser.getRuolo() == DipendentiEnum.Amministratore && loggedUser.getCompagnia() == null){
            addBtn.setVisible(true);
        } else {
            addBtn.setVisible(false);
        }
    }
}
