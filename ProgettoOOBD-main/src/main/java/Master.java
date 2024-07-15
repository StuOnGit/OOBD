import database.PGConnection;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Master {
    public static void main(String[] args) {
        try {
            File config = new File("config.ini");
            if (config.exists()) {
                Scanner scanner = new Scanner(new FileInputStream(config));
                while (scanner.hasNext()) {
                    String[] line = scanner.nextLine().split(":");
                    switch (line[0]) {
                        case "HOST" -> PGConnection.host = line[1];
                        case "PORT" -> PGConnection.port = Integer.parseInt(line[1]);
                        case "DBNAME" -> PGConnection.dbName = line[1];
                        case "USER" -> PGConnection.user = line[1];
                        case "PASSWORD" -> PGConnection.password = line[1];
                    }
                }
            } else{
                JOptionPane.showMessageDialog(null, "File di configurazione non trovato. Prova ad aggiungere il file config.ini nella directory del progetto", "Errore", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String errorStr = null;
        if (PGConnection.host == null){
            errorStr = "HOST non puo' essere null." +
                    " Il file di configurazione dovrebbe contenere una linea simile a:\n" +
                    "HOST:localHost";
        } else if (PGConnection.port == null){
            errorStr = "PORT non può essere null." +
                    " Il file di configurazione dovrebbe contenere una linea simile a:\n" +
                    "PORT:5432";
        }else if (PGConnection.dbName == null){
            errorStr = "DBNAME non puo' essere null." +
                    " Il file di configurazione dovrebbe contenere una linea simile a:\n" +
                    "HOST:GoAirlines";
        }else if (PGConnection.user == null){
            errorStr = "USER non puo' essere null." +
                    " Il file di configurazione dovrebbe contenere una linea simile a:\n" +
                    "USER:postgres";
        }else if (PGConnection.password == null){
            errorStr = "PASSWORD non puo' essere null." +
                    " Il file di configurazione dovrebbe contenere una linea simile a:\n" +
                    "PASSWORD:password";
        }

        if (errorStr != null){
            JOptionPane.showMessageDialog(null, errorStr, "Errore", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }

        PGConnection.getConnection(); // Testa la connessione;

        // Per un problema di compatibilità tra javafx e java 15 è necessario avviare l'applicazione in questo modo. (Soluzione presa dal progetto ufficiale di javafx https://github.com/openjdk/jfx)
        GoAirlines.main(args);
    }
}
