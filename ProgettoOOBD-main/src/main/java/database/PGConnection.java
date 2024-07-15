package database;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PGConnection {
    private static Connection connection = null;
    public static String host = null;
    public static Integer port = null;
    public static String dbName = null;
    public static String user = null;
    public static String password = null;

    private PGConnection(){}

    public static Connection getConnection(){
        try {
            if (connection == null || connection.isClosed()){
                String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, password);
                }
                catch (ClassNotFoundException | SQLException e) {
                    JOptionPane.showMessageDialog(null, "Errore di connessione al database. Controlla il file config.ini", "Errore", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }
}
