package controllers;

import customComponents.TrattaHbox;
import data.Aeroporto;
import data.Tratta;
import database.dao.AeroportoDao;
import database.dao.TrattaDao;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import utility.Refreshable;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

// controlla per la scheda tabellone
public class ControllerTabellone implements Initializable , Refreshable<Tratta> {

    @FXML
    private VBox voloBox; // vbox per numero volo
    @FXML
    private VBox destinazioneBox; // vbox per destinazione/provenienza
    @FXML
    private VBox partenzaBox; // vbox per ora della partenza
    @FXML
    private VBox statoBox; // stato della tratta: In attesa, conclusa, imbarco in gate __

    @FXML
    private Button partenzeBtn, arriviBtn; // pulsanti per cambiare tabellone
    @FXML
    private Label destinazioneLbl;
    private List<Tratta> localTratte; // lista di tratte locali salvate dal database
    private Aeroporto aeroportoGestito;
    private boolean refreshing = false;
    private Runnable selectedFunction; // o partenze() o arrivo(), a seconda del bottone che viene premuto

    @FXML
    public void partenze(ActionEvent e){
        selectedFunction = this::partenze;
        refresh();
    }
    // setta le tratte della giornata in partenza
    public void partenze(){
        destinazioneLbl.setText("Destinazione");
        setLabels(localTratte.stream().filter(t -> t.getAereoportoPartenza().equals(aeroportoGestito)), true);
        partenzeBtn.setStyle(
                partenzeBtn.getStyle()
                        .replace("-fx-background-color: f0f0f0", "-fx-background-color: black")
                        .replace("-fx-text-fill: black", "-fx-text-fill: white")
        );
        ((FontAwesomeIcon) partenzeBtn.getGraphic()).setFill(Color.WHITE);
        arriviBtn.setStyle(
                arriviBtn.getStyle()
                        .replace("-fx-background-color: black", "-fx-background-color: f0f0f0")
                        .replace("-fx-text-fill: white", "-fx-text-fill: black")
        );
        ((FontAwesomeIcon) arriviBtn.getGraphic()).setFill(Color.BLACK);
    }

    @FXML
    public void arrivi(ActionEvent e){
        selectedFunction = this::arrivi;
        refresh();
    }
    // setta le tratte della giornata in arrivo
    public void arrivi(){
        destinazioneLbl.setText("Provenienza");
        setLabels(localTratte.stream().filter(t -> !t.getAereoportoPartenza().equals(aeroportoGestito)), false);

        arriviBtn.setStyle(
                arriviBtn.getStyle()
                        .replace("-fx-background-color: f0f0f0", "-fx-background-color: black")
                        .replace("-fx-text-fill: black", "-fx-text-fill: white")
        );
        ((FontAwesomeIcon) arriviBtn.getGraphic()).setFill(Color.WHITE);
        partenzeBtn.setStyle(
                partenzeBtn.getStyle()
                        .replace("-fx-background-color: black", "-fx-background-color: f0f0f0")
                        .replace("-fx-text-fill: white", "-fx-text-fill: black")
        );
        ((FontAwesomeIcon) partenzeBtn.getGraphic()).setFill(Color.BLACK);
    }

    private void setLabels(Stream<Tratta> tratte, boolean isPartenza){
        voloBox.getChildren().clear();
        destinazioneBox.getChildren().clear();
        partenzaBox.getChildren().clear();
        statoBox.getChildren().clear();

        tratte.forEach(t -> {
            Label numeroVolo = new Label(t.getNumeroVolo());
            numeroVolo.setPrefSize(210, 30);
            numeroVolo.setAlignment(Pos.CENTER);
            numeroVolo.setStyle("-fx-text-fill: white; -fx-font-size: 18");
            voloBox.getChildren().add(numeroVolo);

            Label aeroportoArrivo = new Label();
            if (isPartenza) aeroportoArrivo.setText(t.getAereoportoArrivo().getCitta() + "-" + t.getAereoportoArrivo().getNome());
            else aeroportoArrivo.setText(t.getAereoportoPartenza().getCitta() + "-" + t.getAereoportoPartenza().getNome());

            aeroportoArrivo.setPrefSize(210, 30);
            aeroportoArrivo.setAlignment(Pos.CENTER);
            aeroportoArrivo.setStyle("-fx-text-fill: yellow; -fx-font-size: 18");
            destinazioneBox.getChildren().add(aeroportoArrivo);

            Label partenza = new Label(t.getOraPartenzaFormatted() + ((t.getRitardo() != 0)? " +" + t.getRitardo()+"(min)": ""));
            partenza.setPrefSize(210, 30);
            partenza.setAlignment(Pos.CENTER);
            partenza.setStyle("-fx-text-fill: white; -fx-font-size: 18");
            partenzaBox.getChildren().add(partenza);

            Label stato = new Label();
            if (isPartenza) {
                if (t.getGate() != null && !t.isConclusa()) {
                    stato.setText("Imbarco in " + t.getGate());
                    stato.setStyle("-fx-text-fill: YELLOW;");
                } else if (t.getGate() != null && t.isConclusa()) {
                    stato.setText("Conclusa");
                    stato.setStyle("-fx-text-fill: green;");
                } else {
                    stato.setText("In Attesa");
                    stato.setStyle("-fx-text-fill: white;");
                }

            } else {
                if (t.isConclusa()){
                    stato.setText("Conclusa");
                    stato.setStyle("-fx-text-fill: green;");
                } else{
                    stato.setText("In Attesa");
                    stato.setStyle("-fx-text-fill: yellow;");
                }
            }
            stato.setPrefSize(210, 30);
            stato.setAlignment(Pos.CENTER);
            stato.setStyle(stato.getStyle() + "-fx-font-size: 18");
            statoBox.getChildren().add(stato);
        });
    }

    public Task<List<Tratta>> refresh(){
        if (!isRefreshing()){
            refreshing = true;
            Task<List<Tratta>> task = new Task<>() {
                @Override
                protected List<Tratta> call() {
                    try {
                        return new TrattaDao().getTratteWithDate(LocalDate.now(ZoneId.systemDefault()), LocalDate.now(ZoneId.systemDefault()));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                localTratte = task.getValue();
                selectedFunction.run();
                refreshing = false;
            });
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            return task;
        } else return null;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            aeroportoGestito = new AeroportoDao().getAeroportoGestito();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        selectedFunction = this::partenze;
    }

}
