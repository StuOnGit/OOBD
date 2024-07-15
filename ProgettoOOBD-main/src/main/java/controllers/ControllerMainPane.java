package controllers;

import com.jfoenix.controls.JFXButton;
import data.Dipendente;
import enumeration.DipendentiEnum;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utility.Refreshable;
import utility.UserRestricted;
import utility.WindowDragger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// controller principale
public class ControllerMainPane extends WindowDragger implements UserRestricted {
    @FXML
    private VBox lpBox; // vbox per i pulsanti laterali
    @FXML
    private Label dipendenteLbl; // label per informazioni sul dipendente loggato

    @FXML
    private JFXButton tratteBtn, gateBtn, checkInBtn,
            imbarcoBtn, compagnieBtn, aereiBtn,
            dipendentiBtn, statisticheBtn, tabelloneBtn;  // pulsanti laterali
    @FXML
    private Pane trattePane, gatePane, checkInPane,
            imbarcoPane, compagniePane, bagagliPane,
            dipendentiPane, statistichePane, tabellonePane;

    private Dipendente loggedUser; // utente loggato

    // riferimento ai varri controller secondari
    private ControllerTratte controllerTratte;
    private ControllerGate controllerGate;
    private ControllerDipendenti controllerDipendenti;
    private ControllerCompagnie controllerCompagnie;
    private ControllerStatistiche controllerStatistiche;
    private ControllerTabellone controllerTabellone;

    // lista di controller secondari che implementano refreshable
    private List<Refreshable> refreshableList = new ArrayList<>();

    public Dipendente getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUser = loggedUser;
        dipendenteLbl.setText(loggedUser.getNome() + "-"
                + loggedUser.getCognome() + " \n"
                + ((loggedUser.getCompagnia() != null)?loggedUser.getCompagnia().getNome(): "Aeroporto") + ":"
                + loggedUser.getRuolo() + "#"
                + loggedUser.getCodiceImpiegato());
    }


    // per evitare problemi di concorrenza con la connessione al database, solo uno alla volta dei controller secondari può effettuare il refresh
    public boolean canRefresh(){
        return !refreshableList.stream()
                .map(Refreshable::isRefreshing)
                .reduce(false, (a, b) -> a || b); // la clausa è vera se almeno uno dei controller sta effettuando il refresh
    }

    // riporta alla pagina di login
    public void logout(ActionEvent e){
        try {
            Stage login = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);

            Image icone = new Image(getClass().getResourceAsStream("/img/logoWhite.png"));
            login.getIcons().add(icone);

            login.setScene(scene);
            login.setResizable(false);
            login.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            ((Node) e.getSource()).getScene().getWindow().hide();
            login.show();
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }
    public void closeButton(ActionEvent e){ Platform.exit(); }

    // imposta il pannello da mostrare solo se nessuno sta effettuando il refresh
    public void setFrame(MouseEvent e){
        if (canRefresh()) {
            for(Node i : lpBox.getChildren()){
                i.setStyle(i.getStyle().replace("-fx-background-color: #18283f;",
                        ""));
            }

            JFXButton b = (JFXButton) e.getSource();
            b.setStyle(b.getStyle() + "-fx-background-color: #18283f;");

            if (tratteBtn.equals(b)) {
                trattePane.toFront();
                controllerTratte.refresh();
            } else if (gateBtn.equals(b)) {
                gatePane.toFront();
                controllerGate.refresh();
            } else if (checkInBtn.equals(b)) {
                checkInPane.toFront();
            } else if (imbarcoBtn.equals(b)) {
                imbarcoPane.toFront();
            } else if (compagnieBtn.equals(b)) {
                compagniePane.toFront();
                controllerCompagnie.refresh();
            } else if (aereiBtn.equals(b)) {
                bagagliPane.toFront();
            } else if (dipendentiBtn.equals(b)) {
                dipendentiPane.toFront();
                controllerDipendenti.refresh();
            } else if (statisticheBtn.equals(b)) {
                statistichePane.toFront();
            } else if (tabelloneBtn.equals(b)) {
                tabellonePane.toFront();
                controllerTabellone.refresh();
            }
        }

    }

    public void initialize(Dipendente loggedUser) {
        setLoggedUser(loggedUser);
        try {
            FXMLLoader tratteLoader = new FXMLLoader(getClass().getResource("/fxml/Tratte.fxml"));
            trattePane.getChildren().add(
                    tratteLoader.load()
            );
            controllerTratte = tratteLoader.getController();
            controllerTratte.setLoggedUser(loggedUser);
            refreshableList.add(controllerTratte);

            FXMLLoader gateLoader = new FXMLLoader(getClass().getResource("/fxml/Gate.fxml"));
            gatePane.getChildren().add(
                    gateLoader.load()
            );
            controllerGate = gateLoader.getController();
            controllerGate.setLoggedUser(loggedUser);
            refreshableList.add(controllerGate);

            if (loggedUser.getRuolo() == DipendentiEnum.AddettoCheckIn || loggedUser.getRuolo() == DipendentiEnum.Amministratore) {
                checkInPane.getChildren().add(
                        FXMLLoader.load(getClass().getResource("/fxml/CheckIn.fxml"))
                );
            } else{
                checkInBtn.setDisable(true);
            }
            if (loggedUser.getRuolo() == DipendentiEnum.AddettoImbarco || loggedUser.getRuolo() == DipendentiEnum.Amministratore) {
                imbarcoPane.getChildren().add(
                        FXMLLoader.load(getClass().getResource("/fxml/Imbarco.fxml"))
                );
            }else{
                imbarcoBtn.setDisable(true);
            }

            FXMLLoader compagnieLoader = new FXMLLoader(getClass().getResource("/fxml/Compagnie.fxml"));
            compagniePane.getChildren().add(
                    compagnieLoader.load()
            );
            controllerCompagnie = compagnieLoader.getController();
            controllerCompagnie.setLoggedUser(loggedUser);
            refreshableList.add(controllerCompagnie);


            if (loggedUser.getRuolo() == DipendentiEnum.Amministratore) {
                FXMLLoader dipendentiLoader = new FXMLLoader(getClass().getResource("/fxml/Dipendenti.fxml"));
                dipendentiPane.getChildren().add(
                        dipendentiLoader.load()
                );
                controllerDipendenti = dipendentiLoader.getController();
                controllerDipendenti.setLoggedUser(loggedUser);
                refreshableList.add(controllerDipendenti);
            } else{
                dipendentiBtn.setDisable(true);
            }

            FXMLLoader bagagliLoader = new FXMLLoader(getClass().getResource("/fxml/Bagagli.fxml"));
            bagagliPane.getChildren().add(
                    bagagliLoader.load()
            );

            FXMLLoader statisticheLoader = new FXMLLoader(getClass().getResource("/fxml/Statistiche.fxml"));
            statistichePane.getChildren().add(
                    statisticheLoader.load()
            );
            controllerStatistiche = statisticheLoader.getController();

            FXMLLoader tabelloneLoader = new FXMLLoader(getClass().getResource("/fxml/Tabellone.fxml"));
            tabellonePane.getChildren().add(
                    tabelloneLoader.load()
            );
            controllerTabellone = tabelloneLoader.getController();
            refreshableList.add(controllerTabellone);
            controllerTabellone.refresh();
        } catch (Exception e) {
            e.printStackTrace();
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

}
