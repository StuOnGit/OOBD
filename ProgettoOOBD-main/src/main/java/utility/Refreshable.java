package utility;

import javafx.concurrent.Task;
import java.util.List;

// intrerfaccia per classi che caricano informazioni in background dal database
public interface Refreshable<T> {
    Task<List<T>> refresh(); // carica le informazioni dal database in background
    boolean isRefreshing();
}
