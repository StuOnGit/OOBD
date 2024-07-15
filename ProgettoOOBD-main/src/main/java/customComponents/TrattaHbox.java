package customComponents;

import data.Tratta;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

// componente custom per rappresentare tratte in una lista. Vedi il modello base in TrattaHbox.fxml
public class TrattaHbox extends HBox {
    private Tratta tratta;
    private Label partenza;
    private Label arrivo;
    private Label numeroVolo;
    private Label dataPartenza;
    private Label oraPartenza;
    private Label ritardo;
    private Label gate;
    private Label compagnia;

    public Tratta getTratta() {
        return tratta;
    }

    public TrattaHbox(Tratta tratta) {
        this.tratta = tratta;
        try {
            getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/TratteHbox.fxml")));

            partenza = (Label) lookup("#partenza");
            arrivo = (Label) lookup("#arrivo");
            numeroVolo = (Label) lookup("#numeroVolo");
            dataPartenza = (Label) lookup("#dataPartenza");
            oraPartenza = (Label) lookup("#oraPartenza");
            ritardo = (Label) lookup("#ritardo");
            gate = (Label) lookup("#gate");
            compagnia = (Label) lookup("#compagnia");
            setLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLabels(){
        partenza.setText(tratta.getAereoportoPartenza().getCitta());
        arrivo.setText(tratta.getAereoportoArrivo().getCitta());
        numeroVolo.setText(tratta.getNumeroVolo());
        dataPartenza.setText(tratta.getDataPartenzaFormatted());
        oraPartenza.setText(tratta.getOraPartenzaFormatted());
        ritardo.setText(tratta.getRitardo() + "''");
        gate.setText(tratta.getGate());
        compagnia.setText(tratta.getCompagnia().getNome());

        if(tratta.isConclusa()){
            numeroVolo.setStyle("-fx-background-radius: 25px; -fx-background-color: lightgreen");
        }
        if (tratta.getRitardo() > 0){
            ritardo.setStyle("-fx-background-radius: 25px; -fx-background-color: #DC143C");
        }
    }
}
