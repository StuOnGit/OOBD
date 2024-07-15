package controllers;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import data.Biglietto;
import data.Cliente;
import data.Tratta;
import database.dao.BigliettoDao;
import database.dao.CodaImbarcoDao;
import enumeration.CodeEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Window;
import utility.Validators;
import utility.WindowDragger;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerAcquisto extends WindowDragger implements Initializable {
    /**
     * @param tratta rappresenta la tratta del biglietto.
     * @param nome,cognome,riconoscimento,cf sono i dati inseriti dall'utente che identificano il cliente.
     * @param classe la tipologia del biglietto, economy, priority etc..
     * @param documento rappresenta il tipo di documento identificativo.
     * @param pagaBtn e' il componente grafico che rappresenta il bottone di conferma
     * @param mainWindow rappresenta la finestra principale del programma
     * @param prezzo rappresenta il prezzo del biglietto
     */
    private Tratta tratta;
    @FXML
    JFXTextField nome, cognome, riconoscimento, cf;
    @FXML
    JFXComboBox<CodeEnum> classe;
    @FXML
    JFXComboBox<String> documento;
    @FXML
    JFXButton pagaBtn;
    Window mainWindow;
    double prezzo = 19.99;
    /**
     * imposta la tratta e per ogni coda d'imbarco che quella tratta permette, la aggiunge alla comboBox.
     * */
    public void setTratta(Tratta tratta) {
        this.tratta = tratta;
        try {
            new CodaImbarcoDao().getByTratta(tratta).forEach(c -> classe.getItems().add(c.getClasse()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if(!classe.getItems().isEmpty())
            classe.getSelectionModel().selectFirst();
        computePrezzo(null);
    }
    /**
     * restituisce la tratta
     * */
    public Tratta getTratta(){
        return this.tratta;
    }
    /**
     * chiude la finestra
     * */
    @FXML
    public void close(ActionEvent e){
        ((JFXButton) e.getSource()).getScene().getWindow().hide();
    }

    /**
     * se i dati inseriti dall'utente sono tutti legittimi
     * allora crea un Cliente per passarlo come parametro a Biglietto, e dopodichè prova a
     * inserire quest'ultimo nel Database, e in base al caso farà comparire
     * un messaggio di conferma o di errore, e
     * chiude la finestra.
     * */
    @FXML
    public void buy(ActionEvent e) {
        cf.validate();
        riconoscimento.validate();
        nome.validate();
        cognome.validate();
        if (cf.validate() && riconoscimento.validate()  && nome.validate() && cognome.validate() && classe.getValue() != null) {
            Cliente cliente = new Cliente(getCf(), getNome() + "-" + getCognome(), getRiconoscimento());

            Biglietto biglietto = new Biglietto(prezzo, classe.getValue(), false, false, tratta.getNumeroVolo(), cliente);
            BigliettoDao bDao = new BigliettoDao();

            FXMLLoader fxmlLoader;
            try {
                bDao.insert(biglietto);
                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AcquistoBigliettoConfirm.fxml"));

            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AcquistoBigliettoError.fxml"));
            }
            try {
                Parent root = fxmlLoader.load();
                JFXAlert alert = new JFXAlert(mainWindow);
                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading();
                alert.setOverlayClose(true);
                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert.setContent(root);
                alert.initModality(Modality.NONE);
                alert.showAndWait();
            } catch (IOException ioException){
                ioException.printStackTrace();
            }
                close(e);
            }
        }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia 'Initializable':
     * inizializza i valori della comboBox 'documento', aggiunge i Validators alle varie
     * componenti grafiche del fxml associato e computa il prezzo.
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cf.getValidators().add(new Validators().createRequiredValidator("Non puo' essere vuota"));
        nome.getValidators().add(new Validators().createRequiredValidator("Inserire nome"));
        cognome.getValidators().add(new Validators().createRequiredValidator("Inserire cognome"));
        cf.getValidators().add(new Validators().createCfValidator("Non e' valido"));

        documento.getItems().add("Patente");
        documento.getItems().add("Carta d'Identita'");
        documento.getItems().add("Passaporto");
        documento.getSelectionModel().selectFirst();

        computePrezzo(null);
        setDocumentValidator(null);
    }
    /**
     * setta i Validator per la comboBox e il suo relativo messaggio di errore.
     */
    public void setDocumentValidator(ActionEvent event) {
        riconoscimento.getValidators().clear();

        switch (documento.getValue()){
            case "Patente" ->  {
                riconoscimento.getValidators().add(new Validators().createPatentValidator("Patente errata"));
            }
            case "Carta d'Identita'" ->{
                riconoscimento.getValidators().add(new Validators().createIdValidator("Id errato"));
            }
            case "Passaporto" ->{
                riconoscimento.getValidators().add(new Validators().createPassportValidator("Passaporto errato"));
            }
        }

    }
    /**
     * in base al valore della comboBox "classe"
     * prezzo assume valori differenti con operazioni
     * scelte a piacere dei programmatori*/
    public void computePrezzo(ActionEvent event) {
        prezzo = 19.99;
        if (!classe.getItems().isEmpty()) {
            CodeEnum value = classe.getValue();
            if (value == CodeEnum.ECONOMY) {
                prezzo -= 5;
            } else if (value == CodeEnum.BUSINESS) {
                prezzo *= 1.5;
            } else if (value == CodeEnum.FAMIGLIE) {
                prezzo -= 1;
            } else if (value == CodeEnum.PRIORITY) {
                prezzo = prezzo * 2 + 1.99;
            } else if (value == CodeEnum.DIVERSAMENTE_ABILI) {
                prezzo /= 2;
            }
        }
        pagaBtn.setText(String.format("%.2f$", prezzo));
    }
    /**
     * Riportati getter e setter dei vari attributi della suddetta classe
     * */
    public String getNome() {
        return nome.getText();
    }

    public void setNome(String nome) {
        this.nome.setText(nome);
    }

    public String getCognome() {
        return cognome.getText();
    }

    public void setCognome(String cognome) {
        this.cognome.setText(cognome);
    }

    public String getRiconoscimento() {
        return riconoscimento.getText();
    }

    public void setRiconoscimento(String riconoscimento) {
        this.riconoscimento.setText(riconoscimento);
    }

    public String getCf() {
        return cf.getText();
    }

    public void setCf(String cf) {
        this.cf.setText(cf);
    }

    public JFXComboBox getClasse() {
        return classe;
    }

    public void setClasse(JFXComboBox classe) {
        this.classe = classe;
    }

    public JFXComboBox getDocumento() {
        return documento;
    }

    public void setDocumento(JFXComboBox documento) {
        this.documento = documento;
    }

    public void setMainWindow(Window mainWindow) {
        this.mainWindow = mainWindow;
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
