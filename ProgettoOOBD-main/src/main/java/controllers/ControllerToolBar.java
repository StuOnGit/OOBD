package controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utility.WindowDragger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// toolbar custom
public class ControllerToolBar extends WindowDragger implements Initializable {

    @FXML
    AnchorPane panel, container;
    private ControllerMainPane mainPaneController;

    @Override
    public void setOffset(MouseEvent e) {
        super.setOffset(e);
    }

    @Override
    public void moveWindow(MouseEvent e) {
        super.moveWindow(e);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane mainPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPane.fxml"));
            mainPane = loader.load();
            mainPaneController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.getChildren().add(mainPane);
    }

    // pulsante per ridurre ad icona
    public void min(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
    // pulsante per chiudere la finestra
    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    // restituisce il controller della finestra
    public ControllerMainPane getMainPaneController() {
        return mainPaneController;
    }
}
