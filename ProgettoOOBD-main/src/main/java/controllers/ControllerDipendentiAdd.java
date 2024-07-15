package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import customComponents.Toast;
import data.Compagnia;
import data.Dipendente;
import database.dao.CompagniaDao;
import database.dao.DipendentiDao;
import enumeration.DipendentiEnum;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utility.UserRestricted;
import utility.Validators;
import utility.WindowDragger;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerDipendentiAdd extends WindowDragger implements Initializable, UserRestricted {
    /**
     * @param nome,cognome,email,password TextField per l'inserimento dati
     * @param addDipendente,annullaBtn bottoni
     * @param ruolo comboBox contenente il ruolo del Dipendente
     * @param compagnia comboBox contentente la compagnia del Dipendente
     * @param loggedUsr dipendente loggato
     * @param mainPane pannello principale*/
    @FXML
    JFXTextField nome, cognome, email, password;
    @FXML
    JFXButton addDipendente, annullaBtn;
    @FXML
    JFXComboBox<DipendentiEnum> ruolo;
    @FXML
    JFXComboBox<Compagnia> compagnia;
    private Dipendente loggedUsr;

    private Pane mainPane;
    /**
     * imposta il mainPane
     * */
    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;
    }
    /**
     * chiude la finestra
     * */
    public void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    /**
     * Override della superClasse estesa: "WindowDragger".
     * permette di muovere la finestra
     * */
    @Override
    public void moveWindow(MouseEvent mouseEvent) {
        super.moveWindow(mouseEvent);
    }
    /**
     * Override della superClasse estesa: "WindowDragger".
     * setta l'offset
     * */
    @Override
    public void setOffset(MouseEvent mouseEvent) {
        super.setOffset(mouseEvent);
    }
    /**
     * se l'email inserita dall'utente e' valida aggiunge un nuovo dipendente al Database
     * e mostra un avviso di conferma, in caso di errore, mostra un avviso di errore.
     * */
    public void add(ActionEvent event) {
        if(email.validate()){
            //qui si può allora aggiungere il Dipendente
            Toast toast = new Toast(mainPane);
            try {
                new DipendentiDao().insert(new Dipendente(null, nome.getText(), cognome.getText(), email.getText(), password.getText(), ruolo.getValue(), compagnia.getValue()));
                toast.show("Aggiunto dipendente con successo");
                close(event);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                toast.show("Qualcosa e' andato storto, riprova");
            }
        }
    }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia 'Initializable':
     * setta le componenti della comboBox "ruolo",
     * disabilita il bottone "addDipendente" se non sono stati compilati tutti i campi delle TextField,
     * aggiunge un Validator alla TextField "email".
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ruolo.getItems().add(DipendentiEnum.TicketAgent);
        ruolo.getItems().add(DipendentiEnum.Amministratore);
        ruolo.getItems().add(DipendentiEnum.AddettoImbarco);
        ruolo.getItems().add(DipendentiEnum.ResponsabileVoli);
        ruolo.getItems().add(DipendentiEnum.AddettoCheckIn);
        ruolo.getSelectionModel().selectFirst();

        addDipendente.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(nome.textProperty(),
                    cognome.textProperty(),
                    email.textProperty(),
                    password.textProperty()
                );
            }
            @Override
            protected boolean computeValue() {
                return (nome.getText().isEmpty() ||
                        cognome.getText().isEmpty() ||
                        email.getText().isEmpty() ||
                        password.getText().isEmpty()
                );
            }
        });

        email.setValidators(new Validators().createEmailValidator("Email Invalida"));
    }
    /**
     * funzione necessaria dall'implementazione dell'interfaccia UserRestricted:
     * non permette agli amministratori l'aggiunta di dipendenti non appartenenti alla propria compagnia
     * a meno che non abbiamo una compagnia di appartenenza.
     */
    @Override
    public void setLoggedUser(Dipendente loggedUser) {
        this.loggedUsr = loggedUser;
        if (loggedUser.getCompagnia() != null) {
            compagnia.getItems().add(loggedUser.getCompagnia());
        }else {
            try {
                compagnia.getItems().addAll(new CompagniaDao().getCompagnie());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        compagnia.getSelectionModel().selectFirst();
    }
}
