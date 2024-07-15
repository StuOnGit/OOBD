package controllers;

import com.jfoenix.controls.JFXButton;
import customComponents.Toast;
import data.Compagnia;
import data.Dipendente;
import database.dao.DipendentiDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utility.WindowDragger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerDipendentiDati extends WindowDragger {
    /**
     * @param mail,nomeCognome,codiceImpiegato,password,ruolo,compagnia label contenenti i dati relativi al Dipendente.
     * @param licenziaBtn bottone di licenziamento.
     * @param myDipendente dipendente.
     * */
    @FXML
    Label mail, nomeCognome, codiceImpiegato, password,ruolo, compagnia;
    @FXML
    JFXButton licenziaBtn;
    private Dipendente myDipendente;


    /**
     * Override della superClasse estesa: "WindowDragger".
     * setta l'offset
     * */
    @Override
    public void setOffset(MouseEvent e) {
        super.setOffset(e);
    }
    /**
     * Override della superClasse estesa: "WindowDragger".
     * permette di muovere la finestra
     * */
    @Override
    public void moveWindow(MouseEvent e) {
        super.moveWindow(e);
    }

    /**
     * chiude la finestra.
     * */
    public void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    /**
     * cancella dal database il dipendente selezionato.
     * */
    public void licenzia(ActionEvent event) throws IOException {
        //elimina dipendente
        try {
            new DipendentiDao().licenzia(this.myDipendente);
            close(event);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    /**
     * getter e setter della suddetta classe.
     * */
    public String getCompagnia() {
        return compagnia.toString();
    }

    public void setCompagnia(Compagnia compagnia) {
        this.compagnia.setText(compagnia.getNome());
    }

    public void setMyDipendente(Dipendente myDipendente) {
        this.myDipendente = myDipendente;
    }
    public Label getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail.setText(mail);
    }

    public Label getNomeCognome() {
        return nomeCognome;
    }

    public void setNomeCognome(String nomeCognome) {
        this.nomeCognome.setText(nomeCognome); ;
    }

    public Label getCodiceImpiegato() {
        return codiceImpiegato;
    }

    public void setCodiceImpiegato(String codiceImpiegato) {
        this.codiceImpiegato.setText(codiceImpiegato);
    }

    public Label getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setText(password);
    }

    public Label getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo.setText(ruolo);
    }
}
