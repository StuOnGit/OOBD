package customComponents;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import database.dao.TrattaDao;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.sql.SQLException;

// menu popup per le GateCard
public class GateCardPopup extends JFXPopup {

    private JFXButton chiudi; // bottone per settare il gate chiuso
    private JFXButton apri; // bottone per settare il gate libero
    private JFXButton impostaTratta; // bottone per iniziare un imbarco
    private JFXButton terminaImbarco; // bottone per terminare un imbarco
    private VBox v; // vbox contenente i bottoni

    public GateCardPopup(GateCard gCard){
        v = new VBox();

        chiudi = new JFXButton("Chiudi");
        chiudi.setStyle("-fx-background-radius: 0");
        apri = new JFXButton("Apri");
        apri.setStyle("-fx-background-radius: 0");
        impostaTratta = new JFXButton("Imposta Tratta");
        impostaTratta.setStyle("-fx-background-radius: 0");

        chiudi.setPrefWidth(100);
        apri.setPrefWidth(100);
        impostaTratta.setPrefWidth(100);

        terminaImbarco = new JFXButton("Termina imbarco");
        terminaImbarco.setStyle("-fx-background-radius: 0");
        //terminaImbarco.setPrefWidth(100);

        switch (gCard.getGate().getStatus()){
            case CHIUSO -> setChiuso();
            case LIBERO -> setAperto();
            case OCCUPATO -> setOccupato();
        }

        setPopupContent(v);
        gCard.setOnMouseClicked(m ->{
            if (m.getButton() == MouseButton.SECONDARY){
                show(gCard.getScene().getWindow(), m.getSceneX(), m.getSceneY(), JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 0, 0);
            }
        });
    }

    public void setOccupato(){
        v.getChildren().clear();
        v.getChildren().add(terminaImbarco);
    }
    public void setAperto(){
        v.getChildren().clear();
        v.getChildren().addAll(
                chiudi,
                apri,
                impostaTratta
        );
        impostaTratta.setDisable(false);
    }

    public VBox getVBox() { return v; }

    // imposta le azioni dei vari bottoni. Vedi ControllerGate
    public void setImpostaTratta(EventHandler<ActionEvent> e) {
        impostaTratta.setOnAction(e);
    }
    public void setTerminaImbarco(EventHandler<ActionEvent> e) {
        terminaImbarco.setOnAction(e);
    }
    public void setChiudiGate(EventHandler<ActionEvent> e) {
        chiudi.setOnAction(e);
    }
    public void setChiuso() {
        v.getChildren().clear();
        v.getChildren().addAll(
                chiudi,
                apri,
                impostaTratta
        );
        impostaTratta.setDisable(true);
    }
    public void setApriGate(EventHandler<ActionEvent> e) {
        apri.setOnAction(e);
    }

    public JFXButton getChiudi() {
        return chiudi;
    }

    public JFXButton getApri() {
        return apri;
    }

    public JFXButton getImpostaTratta() {
        return impostaTratta;
    }

    public JFXButton getTerminaImbarco() {
        return terminaImbarco;
    }
}
