package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import customComponents.DipendentiCard;
import data.Dipendente;
import database.dao.DipendentiDao;
import enumeration.DipendentiEnum;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerDipendenti implements Initializable, Refreshable<Dipendente>, UserRestricted {
    /**
     * @param mainPane pannello principale
     * @param searchBar barra di ricerca
     * @param scrollPane sottoPanello di scroll
     * @param cancelBtn,addBtn botton
     * @param dipendentiType comboBox contenente la gerarchia del dipendente
     * @param spinner spinner di caricamento
     * @param dipendentiList lista di dipendenti
     * @param flowPane pannello utile per l'aggiunta delle DipendentiCard
     * @param loggedUser dipendente loggato*/

    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField searchBar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private JFXButton cancelBtn,addBtn;
    @FXML
    private JFXComboBox<String> dipendentiType;
    @FXML
    JFXSpinner spinner;
    private List<DipendentiCard> dipendentiList;
    private FlowPane flowPane;
    private Dipendente loggedUser;
    /**
     * funzione necessaria dall'implementazione dell'interfaccia "Initializable":
     * setta i componenti della comboBox "dipendentiType", l'Action del bottone "cancelBtn"
     * setta il  flowPane e le sue caratteristiche e lo aggiunge allo scrollPane
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dipendentiType.getItems().addAll("Dipendenti","Amministratori","Ticket Agent","Addetti all'Imbarco","Responsabili Voli", "Addetti al CheckIn");
        dipendentiType.getSelectionModel().selectFirst();

        // Cancella il testo e annulla la ricerca
        cancelBtn.setOnAction(e ->{
            searchBar.setText("");
            search(null);
        });

        //creazione del pannello da inserire in scrollPane
        flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(5, 10, 5, 20));
        flowPane.setStyle("-fx-background-color: transparent");

        dipendentiList = new ArrayList<>();

        scrollPane.setContent(flowPane);
    }
    /**
     * ricerca che e' attivata ogni qualvolta l'utente inserisce un carattere
     * all'interno della searchBar.
     * */
    public void search(KeyEvent keyEvent) {

        String testo = searchBar.getText().toUpperCase();//perchè così in qualunque modo inserisci il nome lo trova *guarda commento di sotto

        flowPane.getChildren().removeIf(node -> !((DipendentiCard) node).getBottoneUtente().contains(testo)); //getBottoneUtente() restituisce una stringa in UpperCase
        dipendentiList.forEach(node -> {
            if(node.getBottoneUtente().contains(testo) && !flowPane.getChildren().contains(node)){
                    flowPane.getChildren().add(node);
            }
        });
        filtroLavoro(dipendentiType.getValue());
    }
    /**
     * gestisce la ricerca in base al valore della comboBox "dipendentiType".
     * */
    public void filtroLavoro(String scelta){
        switch (scelta){
            case("Amministratori"):

                flowPane.getChildren().removeIf(node -> !(((DipendentiCard) node).getGerarchia() == DipendentiEnum.Amministratore));
                break;
            case("Addetti al CheckIn"):

                flowPane.getChildren().removeIf(node -> !(((DipendentiCard) node).getGerarchia() == DipendentiEnum.AddettoCheckIn));
                break;
            case("Ticket Agent"):
                flowPane.getChildren().removeIf(node -> !(((DipendentiCard) node).getGerarchia() == DipendentiEnum.TicketAgent));
                break;
            case("Addetti all'Imbarco"):


                flowPane.getChildren().removeIf(node -> !(((DipendentiCard) node).getGerarchia() == DipendentiEnum.AddettoImbarco));
                break;
            case("Responsabili Voli"):

                flowPane.getChildren().removeIf(node -> !(((DipendentiCard) node).getGerarchia() == DipendentiEnum.ResponsabileVoli));
                break;

        }
    }
    /**
     * funzione ausiliaria che collega la ricerca con la scelta della gerarchia del Dipendente
     * */
    public void sceltaDipendenti(ActionEvent actionEvent) {
        String scelta = this.dipendentiType.getValue();
        filtroLavoro(scelta);
        search(null);
    }
    /**
     * crea una nuova finestra e alla chiusura della stessa
     * attiva un refresh della GUI principale.
     * */
    public void addDipendente(ActionEvent event) throws IOException {
        //crea scheda per inserire utente
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DipendentiAdd.fxml"));
        Parent root = loader.load();
        ControllerDipendentiAdd c = loader.getController();
        c.setMainPane(mainPane);
        c.setLoggedUser(loggedUser);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.showAndWait();
        refresh();
    }

    /**
     * funzione necessaria dall'implementazione della classe "Refreshable":
     * ricarica la pagina caricando le ultime informazioni e le setta.
     */
    public Task<List<Dipendente>> refresh() {
        if (!isRefreshing()){
            flowPane.getChildren().clear();
            spinner.setVisible(true);
            Task<List<Dipendente>> task = new Task<>() {
                @Override
                protected List<Dipendente> call() {
                    try {
                        return new DipendentiDao().getDipendentyByCompagnia(loggedUser.getCompagnia());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                dipendentiList.clear();
                task.getValue().stream().distinct().forEach(d -> dipendentiList.add(new DipendentiCard(d)));
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
     * funzione necessaria dall'implementazione della classe "Refreshable":
     * */
    @Override
    public boolean isRefreshing() {
        return spinner.isVisible();
    }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia UserRestricted:
     * non permette l'aggiunta di dipendenti agli utenti non autorizzati
     */
    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUser = loggedUser;
        addBtn.setVisible(loggedUser.getRuolo() == DipendentiEnum.Amministratore);
    }
}
