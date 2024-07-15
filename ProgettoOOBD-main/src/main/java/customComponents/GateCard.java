package customComponents;

import data.Gate;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import enumeration.CodeEnum;
import enumeration.GateStatus;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

// Componente custom per mostrare le informazioni del gate
public class GateCard extends Pane{
    private Gate gate; // il gate da mostrare

    private Label gateCode, partenza, arrivo, stato, tempo; // le label principali
    private Pane trattaPane; // pannello con partenza arrivo e tempo
    public VBox code; // pannello con code di imbarco
    private Label stimaDiversamenteAbili, stimaFamiglie,
            stimaBusiness, stimaPriority, stimaEconomy;
    private Label diversamenteAbili, famiglie,
            business, priority, economy;

    // tempo stimato di imbarco in minuti
    public void setTempo(Integer tempo) {
        if (tempo == null)
            this.tempo.setText("Tempo stimato: --''");
        else
            this.tempo.setText("Tempo stimato: " + tempo + "''");
    }

    // setta lo stato del gate vedi enumeration.GateStatus
    public void setStato(GateStatus stato) {
        switch (stato){
            case CHIUSO -> { // se chiuso allora sfondo grigio e disabilita
                this.stato.setText("Chiuso");
                setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 25px; -fx-opacity: 0.4");
                ((FontAwesomeIcon) this.stato.getGraphic()).setIcon(FontAwesomeIconName.MINUS_CIRCLE);
                trattaPane.setVisible(false);
                code.setVisible(false);
            }
            case LIBERO -> { // se libero sfondo grigio e abilita
                this.stato.setText("Libero");
                setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 25px; -fx-opacity: 1");
                ((FontAwesomeIcon) this.stato.getGraphic()).setIcon(FontAwesomeIconName.CHECK);
                trattaPane.setVisible(false);
                code.setVisible(false);
            }
            case OCCUPATO -> { // se occupato sfondo giallo (si può cambiare volendo magari anche mettendo un gradient...) e visualizza le label
                this.stato.setText("Occupato");
                setStyle("-fx-background-color: yellow; -fx-background-radius: 25px; -fx-opacity: 1");
                ((FontAwesomeIcon) this.stato.getGraphic()).setIcon(FontAwesomeIconName.CLOCK_ALT);
                trattaPane.setVisible(true);
                code.setVisible(true);
            }
        }
    }

    public void disableAllCode(){
        diversamenteAbili.setDisable(true);
        famiglie.setDisable(true);
        business.setDisable(true);
        priority.setDisable(true);
        economy.setDisable(true);

        stimaBusiness.setText("");
        stimaDiversamenteAbili.setText("");
        stimaEconomy.setText("");
        stimaFamiglie.setText("");
        stimaPriority.setText("");
    }

    public GateCard(Gate gate){
        try {
            this.gate = gate;
            loadComponents();
            updateLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLabels(){
        gateCode.setText(gate.getGateCode());
        if (gate.getStatus() == GateStatus.OCCUPATO) {
            partenza.setText(gate.getTratta().getAereoportoPartenza().getCitta());
            arrivo.setText(gate.getTratta().getAereoportoArrivo().getCitta());
        }
        setStato(gate.getStatus());
        disableAllCode();
        gate.getCodeImbarco().forEach(c -> {
            switch (c.getClasse()){
                case ECONOMY -> {
                    stimaEconomy.setText(c.getTempoStimato() + "''");
                    economy.setDisable(false);
                }
                case BUSINESS -> {
                    stimaBusiness.setText(c.getTempoStimato() + "''");
                    business.setDisable(false);
                }
                case DIVERSAMENTE_ABILI -> {
                    stimaDiversamenteAbili.setText(c.getTempoStimato() + "''");
                    diversamenteAbili.setDisable(false);
                }
                case FAMIGLIE -> {
                    stimaFamiglie.setText(c.getTempoStimato() + "''");
                    famiglie.setDisable(false);
                }
                case PRIORITY -> {
                    stimaPriority.setText(c.getTempoStimato() + "''");
                    priority.setDisable(false);
                }
            }
        });
        setTempo(gate.getTempoStimatoTotale()); // tempo stimato per l'imbarco.
    }

    // carica i componenti dal file fxml
    private void loadComponents() throws IOException{
        getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/GateCard.fxml")));
        gateCode = ((Label) lookup("#gateCode")); // "lockup" serve per ricercare i componenti tramite l'id
        partenza = ((Label) lookup("#partenza"));
        arrivo = ((Label) lookup("#arrivo"));
        tempo = ((Label) lookup("#tempo"));
        stato = (Label) lookup("#stato");
        trattaPane = (Pane) lookup("#trattaPane");
        code = (VBox) lookup("#code");
        stimaBusiness = (Label) lookup("#stimaBusiness");
        stimaDiversamenteAbili = (Label) lookup("#stimaDiversamenteAbili");
        stimaEconomy = (Label) lookup("#stimaEconomy");
        stimaFamiglie = (Label) lookup("#stimaFamiglie");
        stimaPriority = (Label) lookup("#stimaPriority");

        diversamenteAbili = (Label) lookup("#DIVERSAMENTE_ABILI");
        famiglie = (Label) lookup("#FAMIGLIE");
        business = (Label) lookup("#BUSINESS");
        priority = (Label) lookup("#PRIORITY");
        economy = (Label) lookup("#ECONOMY");
    }

    public Gate getGate() {
        return gate;
    }
}
