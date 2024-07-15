package customComponents;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

// mostra una notifica popup
public class Toast {
    private Pane pane;
    public Toast(Pane pane){
        this.pane = pane;
    }


    public void show(String message){
        JFXSnackbar toast = new JFXSnackbar(pane);
        toast.fireEvent(new JFXSnackbar.SnackbarEvent(
                new JFXSnackbarLayout(message, "OK", null),
                Duration.millis(2500)
        ));
    }
}
