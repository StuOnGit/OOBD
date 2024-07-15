package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import customComponents.Toast;
import data.Compagnia;
import database.dao.CompagniaDao;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import utility.WindowDragger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerCompagnieAdd extends WindowDragger implements Initializable {
    /**
     * @param nome,sigla,nazione,pesoMassimo,prezzoBagagli rappresentano dove inserire i dati della Compagnia
     * @param conferma bottone di conferma
     * @param img componente di un cerchio
     * @param imageFile file per l'inserimento di un immagine per la Compagnia
     * @param trascinaLbl label
     * @param mainPane pannello principale*/
    @FXML
    private JFXTextField nome, sigla, nazione, pesoMassimo, prezzoBagagli;
    @FXML
    private JFXButton conferma;
    @FXML
    private Circle img;
    private File imageFile;
    @FXML
    private Label trascinaLbl;

    private Pane mainPane;

    /**
     * setta il pannello principale
     * */
    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;
    }
    /**
     * conferma l'aggiunta della nuova Compagnia, in caso di errore mostra un messaggio di avviso.
     * setta l'immagine se trascinata e aggiunge la nuova compagnia all'interno del DB.
     * */
    @FXML
    public void conferma(ActionEvent e){

        Toast toast = new Toast(mainPane);
        try {
            Compagnia compagnia = new Compagnia(
                    nome.getText(),
                    sigla.getText(),
                    nazione.getText(),
                    Float.parseFloat(prezzoBagagli.getText()),
                    Float.parseFloat(pesoMassimo.getText())
            );
            new CompagniaDao().insert(compagnia);
            toast.show("Compagnia aggiunta con successo");
        } catch (SQLException | NumberFormatException throwables) {
            throwables.printStackTrace();
            toast.show("Errore compagnia non aggiunta");
        }

        if (imageFile != null){
            try {
                URI dest = new File("icons/" + nome.getText() + ".png").toURI();
                Files.copy(
                        Paths.get(imageFile.getAbsolutePath()),
                        Paths.get(dest),
                        StandardCopyOption.COPY_ATTRIBUTES
                );
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        ((Node) e.getSource()).getScene().getWindow().hide();
    }
    /**
     * chiude la finestra.
     * */
    @FXML
    public void annulla(ActionEvent e){
        ((Node) e.getSource()).getScene().getWindow().hide();
    }

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
     * accetta i file che vengono trascinati
     * */
    @FXML
    public void onDragOver(DragEvent e){
        if (e.getDragboard().hasFiles()){
            e.acceptTransferModes(TransferMode.ANY);
        }
    }
    /**
     * i file rilasciati vengono inseriti in una lista di file e
     * imageFile e' inizializzata con il primo elemento della lista e mostra l'immagine trascinata
     * settando la trascinaLabel invisibile.
     * */
    @FXML
    public void fileDropped(DragEvent e){
        List<File> f = e.getDragboard().getFiles();
        if (!f.isEmpty()){
            try {
                imageFile = f.get(0);
                Image image = new Image(new FileInputStream(imageFile));
                img.setFill(new ImagePattern(image));
                img.setVisible(true);
                trascinaLbl.setVisible(false);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }
/**
 * funzione necessaria dall'implementazione dell'interfaccia "Initializable":
 * disabilita la possibilita' di poter confermare l'aggiunta della Compagnia se i campi sono vuoti
 * e imposta una proprieta' di testo per "sigla" che non puo' essere maggiore di 3, e la imposta
 * sempre in maiuscolo.
 * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conferma.disableProperty().bind(new BooleanBinding() {
            {
                bind(nome.textProperty(),
                        sigla.textProperty(),
                        nazione.textProperty(),
                        pesoMassimo.textProperty(),
                        prezzoBagagli.textProperty());
            }
            @Override
            protected boolean computeValue() {
                return nome.getText().isEmpty() ||
                        sigla.getText().isEmpty() ||
                        nazione.getText().isEmpty() ||
                        pesoMassimo.getText().isEmpty() ||
                        prezzoBagagli.getText().isEmpty();
            }
        });
        sigla.textProperty().addListener((observable, oldValue, newValue) -> {
            if (sigla.getText().length() <= 3)
                sigla.setText(newValue.toUpperCase());
            else
                sigla.setText(sigla.getText().substring(0, 3));
        });
    }
}
