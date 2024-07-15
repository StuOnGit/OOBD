package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import data.Aeroporto;
import data.CodaImbarco;
import data.Dipendente;
import data.Tratta;
import database.dao.AeroportoDao;
import database.dao.CodaImbarcoDao;
import database.dao.TrattaDao;
import enumeration.DipendentiEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import utility.UserRestricted;
import utility.WindowDragger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

// mostra le informazioni di una tratta, e se possibile porta alla pagina di acquisto biglietti
public class ControllerTratteInfo extends WindowDragger implements UserRestricted {

    private Tratta tratta; // la tratta da mostrare

    @FXML
    private Label partenza, arrivo, compagnia, dataPartenza, durata,
    ritardo, gate, numeroVolo, passeggeri, posti; // label per mostrare gli attributi della tratta
    @FXML
    private JFXCheckBox completata; // se è completa allora è spuntata altrimenti no
    @FXML
    private JFXButton closeBtn; // chiude la finestra
    private Window mainWindow; // riferimento alla finestra principale per l'animazione di conferma
    @FXML
    private JFXButton acquistaBtn; // porta alla pagina di acquisto
    @FXML
    private AnchorPane codePane; // mostra le code di imbarco
    @FXML
    private Label nonGestitaLbl; // compare se la tratta non è gestita
    private Aeroporto aeroportoGestito;
    private int passeggeriCount = 0;
    private Dipendente loggedUser;

    // chiude la finestra
    @FXML
    private void close(ActionEvent e){
        closeBtn.getScene().getWindow().hide();
    }

    // imposta la tratta da mostrare
    public void setTratta(Tratta tratta) {
        this.tratta = tratta;
    }


    // mostra la finestra di acquisto
    @FXML
    public void buyTicket(ActionEvent e){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BigliettiAcquisto.fxml"));
            Parent parent = fxmlLoader.load();
            ControllerAcquisto controller = fxmlLoader.getController();
            controller.setTratta(tratta);
            controller.setMainWindow(mainWindow);
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            ((JFXButton) e.getSource()).getScene().getWindow().hide();
            stage.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }

    // aggiorna le labels
    private void setLabels(){
        partenza.setText(tratta.getAereoportoPartenza().getCitta());
        arrivo.setText(tratta.getAereoportoArrivo().getCitta());
        compagnia.setText(tratta.getCompagnia().getNome());
        dataPartenza.setText(tratta.getDataPartenzaFormatted() + " " +  tratta.getOraPartenzaFormatted());
        durata.setText(tratta.getDurataVolo() + "''");
        ritardo.setText(tratta.getRitardo() + "''");
        if (tratta.getGate() != null)
            gate.setText(tratta.getGate());
        numeroVolo.setText(tratta.getNumeroVolo());
        completata.setSelected(tratta.isConclusa());

        posti.setText(tratta.getPosti() + "");

        if (tratta.getAereoportoPartenza().equals(aeroportoGestito)) {
            try {
                passeggeriCount = new TrattaDao().getPasseggeri(tratta);
                passeggeri.setText(passeggeriCount + "");
                List<CodaImbarco> l = new CodaImbarcoDao().getByTratta(tratta);
                l.forEach(c -> {
                    Label lbl1 = (Label) codePane.lookup("#" + c.getClasse() + "1");
                    Label lbl11 = (Label) codePane.lookup("#" + c.getClasse() + "11");
                    Label lbl111 = (Label) codePane.lookup("#" + c.getClasse() + "111");
                    if (c.getOraApertura() != null)
                        lbl1.setText(c.getOraApertura().format(DateTimeFormatter.ofPattern("HH:mm")));
                    lbl11.setText(c.getTempoStimato() + "");
                    lbl111.setText(((c.getTempoEffettivo() != null) ? c.getTempoEffettivo() : "-") + "");
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            nonGestitaLbl.setVisible(true);
            passeggeri.setText("-");
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

    public void setMainWindow(Window window) {
        this.mainWindow = window;
    }

    // inizializza la scheda
    public void initialize(Tratta t, Dipendente loggedUser) {
        setTratta(t);
        setLoggedUser(loggedUser);
        try {
            aeroportoGestito = new AeroportoDao().getAeroportoGestito();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        setLabels();
        acquistaBtn.setDisable(
                !tratta.getAereoportoPartenza().equals(aeroportoGestito) ||
                completata.isSelected() ||
                passeggeriCount >= tratta.getPosti()

        );
        if (loggedUser.getRuolo() != DipendentiEnum.TicketAgent && loggedUser.getRuolo() != DipendentiEnum.Amministratore){
                acquistaBtn.setDisable(true);
        }
        if(loggedUser.getCompagnia() != null){
            if(!loggedUser.getCompagnia().getNome().equals(tratta.getCompagnia().getNome())){
                acquistaBtn.setDisable(true);
            }
        }
        codePane.visibleProperty().bind(nonGestitaLbl.visibleProperty().not());
    }

    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUser = loggedUser;
    }
}
