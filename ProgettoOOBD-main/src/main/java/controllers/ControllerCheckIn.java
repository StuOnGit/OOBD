package controllers;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.*;
import database.dao.BagaglioDao;
import database.dao.BigliettoDao;
import database.dao.TrattaDao;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.w3c.dom.Text;
import utility.IdFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class ControllerCheckIn implements Initializable{
    /**
     * @param spinnerBagagli componente grafico che rappresenta uno spinner
     * @param verificaButton,inviaButton,numeroBagagliButton,cancelBtn rappresentano dei bottoni per svolgere
     * @param bagagliHbox,cartaImbarcoHbox rappresentano graficamente dati utili all'utente
     * @param erroreLabel,nome,cognome,bagagliLabel,codiceBiglietto,tratta,classe,posto,gate,cf,documentoNumero,numeroVolo
     *        sono tutte Label contententi dati utili.
     * @param bigliettoTextField componente dove si inserisce il numero del biglietto
     * @param nBagagli,nPeso,datiCheckIn vbox di raggruppamento in base al tipo di dato
     * @param spinnerClass,initialValue,minValue,maxValue,svf variabili utili allo "spinnerBagagli"
     * @param biglietto rappresenta il biglietto
     * @param tfList lista che contiene le textField dei bagagli e del loro peso*/
    @FXML
    Spinner<Integer> spinnerBagagli;
    @FXML
    JFXButton verificaButton, inviaButton, numeroBagagliButton, cancelBtn;
    @FXML
    HBox bagagliHbox, cartaImbarcoHbox;
    @FXML
    Label erroreLabel,nome,cognome,bagagliLabel,codiceBiglietto,
            tratta,classe,posto,gate,cf,documentoNumero,numeroVolo;
    @FXML
    JFXTextField bigliettoTextField;
    @FXML
    VBox nBagagli, nPeso, datiCheckIn;

    String spinnerClass =  Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL;
    int initialValue = 0;
    int minValue = 0;
    int maxValue = 5;
    SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue,maxValue,initialValue);
    Biglietto biglietto = null;

    List<TextField> tfList = new LinkedList();
    /**
     * funzione associata al bottone di verifica, ricerca il biglietto attraverso il suo codice
     * univoco, dopodichè se esiste setta tutte le label con i dati ricavati dalla ricerca nel Database
     * e li mostra attraverso i vari componenti grafici, altrimenti se non trova il biglietto, o
     * la tratta e' già conclusa o il checkIn e' stato già effettuato
     * mostra un messaggio di errore.
     * */
    @FXML
    public void verify(ActionEvent event) throws SQLException {

            BigliettoDao bDao = new BigliettoDao();

                biglietto = bDao.getBigliettoByCodice(Integer.parseInt(bigliettoTextField.getText()));
                if(biglietto != null) {

                        TrattaDao trattaDao = new TrattaDao();

                        Tratta trattastring = trattaDao.getByNumeroVolo(biglietto.getNumeroVolo());

                        Aeroporto partenza = trattastring.getAereoportoPartenza();
                        Aeroporto arrivo = trattastring.getAereoportoArrivo();

                        codiceBiglietto.setText(String.valueOf(biglietto.getCodiceBiglietto()));
                        tratta.setText(partenza.getCitta() + " -> " + arrivo.getCitta());
                        classe.setText(String.valueOf(biglietto.getClasse()));
                        posto.setText(biglietto.getPosto() + "");        // dai il risultato
                        gate.setText(trattastring.getGate());
                        cf.setText(biglietto.getCliente().getCodiceFiscale());
                        documentoNumero.setText(biglietto.getCliente().getDocumento());
                        nome.setText(biglietto.getCliente().getNome().split("-")[0]);
                        cognome.setText(biglietto.getCliente().getNome().split("-")[1]);
                        numeroVolo.setText(biglietto.getNumeroVolo());

                        if(trattastring.isConclusa()){
                            erroreLabel.setText("Errore -> tratta conclusa.");
                            erroreLabel.setVisible(true);

                            cartaImbarcoHbox.setVisible(true);
                        }else {
                            if (!biglietto.isCheckIn()) {
                                cartaImbarcoHbox.setVisible(true);
                                spinnerBagagli.setVisible(true);
                                bagagliLabel.setVisible(true);                                 // rendi visibile
                                numeroBagagliButton.setVisible(true);
                                erroreLabel.setVisible(false);
                            } else {
                                erroreLabel.setText("Errore -> gia' fatto checkIn");
                                erroreLabel.setVisible(true);

                                cartaImbarcoHbox.setVisible(true);
                            }
                        }
                } else {
                    erroreLabel.setText("Errore -> biglietto non trovato");
                    erroreLabel.setVisible(true);           // dai errore
                    inviaButton.setVisible(false);
                    bagagliHbox.setVisible(false);
                    cartaImbarcoHbox.setVisible(false);
                    spinnerBagagli.setVisible(false);
                    bagagliLabel.setVisible(false);
                    numeroBagagliButton.setVisible(false);
                }
    }
    /**
     * Crea un messaggio di conferma per la conferma del checkIn e setta l'attributo
     * del checkIn = true, dopodiche' fa l'update dello stesso all'interno del
     * database e inserisce poi anche i vari bagagli all'interno del database.
     * Reimposta in fine la GUI allo stato iniziale*/
    public void invia(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CheckIn_ImbarcoConfirm.fxml"));
        Parent root = fxmlLoader.load();
        String bagagli = spinnerBagagli.getValue().toString();


        ControllerConfirmCheckImbarco controller = fxmlLoader.getController();
        controller.setImbarcoCheckLabel("CheckIn");
        controller.setIconCheckImbarco("BUG");
        controller.setBagagli(bagagli);
        controller.setPasseggero(nome.getText() +" "+ cognome.getText());

        try {
            biglietto.setCheckIn(true);
            new BigliettoDao().update(biglietto);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        JFXAlert<Void> alert = new JFXAlert(nome.getScene().getWindow());
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(root);
        alert.initModality(Modality.NONE);
        alert.showAndWait();

        for(int i = 0; i < spinnerBagagli.getValue(); i++){
            BagaglioDao bgDao = new BagaglioDao();
            try {
                bgDao.insert(new Bagaglio(new IdFactory().randomString(8), Float.parseFloat(tfList.get(i).getText()), new BigliettoDao().getCompagnia(biglietto).getPrezzoBagagli()*Float.parseFloat(tfList.get(i).getText()),biglietto));
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        bigliettoTextField.setText("");
        erroreLabel.setVisible(false);
        inviaButton.setVisible(false);
        bagagliHbox.setVisible(false);
        cartaImbarcoHbox.setVisible(false);
        spinnerBagagli.setVisible(false);
        bagagliLabel.setVisible(false);
        numeroBagagliButton.setVisible(false);
    }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia 'Initializable':
     * setta lo spinner, e le varie componenti grafice, disabilitando il bottone di verifica
     * se non si è inserito nulla all'interno della TextField
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinnerBagagli.setValueFactory(svf);
        spinnerBagagli.getStyleClass().add(spinnerClass);

        erroreLabel.setVisible(false);
        inviaButton.setVisible(false);
        bagagliHbox.setVisible(false);
        cartaImbarcoHbox.setVisible(false);
        spinnerBagagli.setVisible(false);
        bagagliLabel.setVisible(false);
        numeroBagagliButton.setVisible(false);

        verificaButton.disableProperty().bind(new BooleanBinding() {
                {
                    super.bind(
                            bigliettoTextField.textProperty()
                    );
                }
                @Override
                protected boolean computeValue() {
                    return bigliettoTextField.getText().isEmpty();
                }
        });
    }
   /**
    * crea delle TextField in base a quanti bagagli si debbono imbarcare
    * e inserendoli quindi nella "tfList", disabilitando il bottone di invio
    * fino all'avvenuta compilazione di tutti i campi.
    * */
    public void imbarcaBagagli(ActionEvent event) {
        Integer bagagli = spinnerBagagli.getValue();
        if(!(nBagagli.getChildren().isEmpty() && nPeso.getChildren().isEmpty())){
            nBagagli.getChildren().remove(0, nBagagli.getChildren().size());
            nPeso.getChildren().remove(0, nPeso.getChildren().size());
        }
        tfList.clear();
        for(int i = 1; i <= bagagli; i++){
            nBagagli.getChildren().add(new Label(i + " bagaglio"));
            TextField tf = new TextField();
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tf.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            tfList.add(tf);
            nPeso.getChildren().add(tf);
        }
        inviaButton.disableProperty().bind(new BooleanBinding() {
            {
                tfList.forEach(i -> super.bind(i.textProperty()));
            }
            @Override
            protected boolean computeValue() {
                Boolean temp = false;
                for (TextField tf: tfList) {
                    if (tf.getText().isEmpty()){
                        temp = true;
                    }
                }
                return temp;
            }
        });
        bagagliHbox.setVisible(true);
        inviaButton.setVisible(true);
    }

    /**
     * funzione collegata al fxml che setta la GUI allo stato iniziale
     * */
    @FXML
    public void restart(ActionEvent event) {
        bigliettoTextField.setText("");
        erroreLabel.setVisible(false);
        inviaButton.setVisible(false);
        bagagliHbox.setVisible(false);
        cartaImbarcoHbox.setVisible(false);
        spinnerBagagli.setVisible(false);
        bagagliLabel.setVisible(false);
        numeroBagagliButton.setVisible(false);

    }
}
