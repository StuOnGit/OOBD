package controllers;

import com.jfoenix.controls.JFXButton;
import data.Dipendente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;


public class ControllerDipendentiCard  {
    /**
     * @param bottoneUtente bottone
     * @param mDipendente dipendente della Card
     * */
    @FXML
    JFXButton bottoneUtente;
    private Dipendente mDipendente;

    /**
     * imposta il dipendente
     * */
    public void setDipendente(Dipendente dipendente){
        this.mDipendente = dipendente;
    }
    /**
     * mostra una finestra "DipendentiDati" settandola con i valori del dipendente della Card.
     * */
    public void creaScheda(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DipendentiDati.fxml"));
        Parent root = loader.load();
        ControllerDipendentiDati cDipendentiDati = loader.getController();
        cDipendentiDati.setMyDipendente(this.mDipendente);

        cDipendentiDati.setMail(mDipendente.getEmail());
        cDipendentiDati.setCodiceImpiegato(mDipendente.getCodiceImpiegato());
        cDipendentiDati.setNomeCognome(mDipendente.getCognome()+ " " + mDipendente.getNome());
        cDipendentiDati.setPassword(mDipendente.getPassword());
        cDipendentiDati.setRuolo(mDipendente.getRuolo().toString());
        cDipendentiDati.setCompagnia(mDipendente.getCompagnia());


        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }
}
