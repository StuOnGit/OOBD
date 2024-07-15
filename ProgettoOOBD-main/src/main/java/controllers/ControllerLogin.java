package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import data.Dipendente;
import database.dao.DipendentiDao;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utility.Validators;
import utility.WindowDragger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

// controller per la schermata di login
public class ControllerLogin extends WindowDragger implements Initializable { // per dubbi su windowDragger vai a utility.windowDragger
    @FXML
    private Pane pannello;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXPasswordField password;

    public void Exit(MouseEvent e) {
        Platform.exit();
    }

    // prova ad effettuare il login
    @FXML
    public void loginAction(ActionEvent e) {
        try {
            Dipendente dipendente = new DipendentiDao().login(email.getText(), password.getText());

            if (dipendente != null){
                try {
                    Stage mainStage = new Stage();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ToolBar.fxml"));
                    Parent root = loader.load();
                    ControllerMainPane mainController = ((ControllerToolBar)loader.getController()).getMainPaneController();
                    mainController.initialize(dipendente);
                    Scene scene = new Scene(root);
                    mainStage.setScene(scene);
                    mainStage.setResizable(false);
                    mainStage.initStyle(StageStyle.TRANSPARENT);
                    scene.setFill(Color.TRANSPARENT);

                    Image icone = new Image(getClass().getResourceAsStream("/img/logoWhite.png"));
                    mainStage.getIcons().add(icone);

                    pannello.getScene().getWindow().hide();
                    mainStage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                password.validate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ValidatorBase v = new ValidatorBase() {
            {
                setMessage("Email o password Errata");
            }
            @Override
            protected void eval() {
                hasErrors.set(true);
            }
        };
        password.setValidators(v);
    }

    // controlla se il tasto premuto da tastiera è enter
    @FXML
    public void enterPressed(KeyEvent keyEvent){
        if(keyEvent.getCode() == KeyCode.ENTER){
            loginAction(null);
        }
    }
    @Override
    public void setOffset(MouseEvent e) {
        super.setOffset(e);
    } // override dei metodi di windowDragger

    @Override
    public void moveWindow(MouseEvent e) {
        super.moveWindow(e);
    } // override dei metodi di windowDragger
}
