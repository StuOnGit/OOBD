package customComponents;

import com.jfoenix.controls.JFXButton;
import controllers.ControllerDipendentiCard;
import data.Dipendente;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import enumeration.DipendentiEnum;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

public class DipendentiCard extends AnchorPane {
    /**
     * @param utente icona
     * @param bottoneUtente bottone
     * @param gerarchia label rappresentante il ruolo del Dipendente
     * @param pannello pane principale della DipendentiCard
     * @param m_dipendente dipendente della DipendentiCard
     * */
    private FontAwesomeIcon utente;
    private JFXButton bottoneUtente;
    private Label gerarchia;
    public AnchorPane pannello;
    private Dipendente m_dipendente;

    /**
     * costruttore della suddetta classe
     * */
    public DipendentiCard(Dipendente dipendente) {
        this.m_dipendente = dipendente;
        try{
            caricaComponenti();
            setGerarchia(dipendente.getRuolo());
            bottoneUtente.setText(dipendente.getNome()+ " " + dipendente.getCognome());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * restituisce la stringa contentente il nome del dipendente
     * */
    public String getBottoneUtente() {
        //per sapere il nome del dipendente
        return bottoneUtente.getText().toUpperCase();
    }
    /**
     * restituisce il ruolo del Dipendente
     * */
    public DipendentiEnum getGerarchia(){
        if(gerarchia.getText().contains("Imbarco")) return DipendentiEnum.AddettoImbarco;
        else if(gerarchia.getText().contains("Amministratore")) return DipendentiEnum.Amministratore;
        else if(gerarchia.getText().contains("Check")) return DipendentiEnum.AddettoCheckIn;
        else if(gerarchia.getText().contains("Ticket")) return DipendentiEnum.TicketAgent;
        else return DipendentiEnum.ResponsabileVoli;
    }
    /**
     * setta la gerarchia del Dipendente
     * */
    public void setGerarchia(DipendentiEnum gerarchia) throws IOException {
        switch (gerarchia) {
            case Amministratore:
                this.gerarchia.setText("Amministratore");
                setStyle("-fx-background-color: #1d3791; -fx-background-radius: 25px;");
                break;
            case AddettoCheckIn:
                this.gerarchia.setText("Addetto al Check In");
                setStyle("-fx-background-color: #0475c3; -fx-background-radius: 25px;");
                break;
            case AddettoImbarco:
                this.gerarchia.setText("Addetto all'Imbarco");
                setStyle("-fx-background-color: #be1c27; -fx-background-radius: 25px;");
                break;
            case TicketAgent:
                this.gerarchia.setText("Ticket Agents");
                setStyle("-fx-background-color: #19a91b; -fx-background-radius: 25px;");
                break;
            case ResponsabileVoli:
                this.gerarchia.setText("Responsabile Voli");
                setStyle("-fx-background-color: #d26619; -fx-background-radius: 25px;");
                break;
        }

    }

    /**
     * carica i componenti all'interno del .fxml
     * */
    private void caricaComponenti() throws IOException {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DipendentiCard.fxml"));
          getChildren().add(loader.load());
          ControllerDipendentiCard cDipenCard = loader.getController();
          cDipenCard.setDipendente(this.m_dipendente);

          this.pannello = ((AnchorPane) lookup("#pannello"));
          this.gerarchia = ((Label) lookup("#gerarchia"));
          this.bottoneUtente = ((JFXButton) lookup("#bottoneUtente"));
          this.utente = ((FontAwesomeIcon) lookup("#utente"));
      }
}
