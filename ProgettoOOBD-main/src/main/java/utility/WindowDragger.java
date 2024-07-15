package utility;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

// classe utile a spostare le finestre trascinandole per evitare riscrittura di codice.
public class WindowDragger {

    private double xOffset = 0.0;
    private double yOffset = 0.0;

    // imposta l'offset del mouse
    public void setOffset(MouseEvent e){
        Window win = ((Node) e.getSource()).getScene().getWindow();
        win.requestFocus();
        xOffset = win.getX() - e.getScreenX();
        yOffset = win.getY() - e.getScreenY();
    }
    public void moveWindow(MouseEvent e) {
        Window win = ((Node) e.getSource()).getScene().getWindow();
        win.setX(e.getScreenX() + xOffset);
        win.setY(e.getScreenY() + yOffset);
    }
}
