package database.dao;

import data.Aeroporto;
import database.PGConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AeroportoDao {
    /**
     * esegue una query al database, e restituisce la lista di tutti gli Aeroporti
     * */
    public List<Aeroporto> getAeroporti() throws SQLException {
        List<Aeroporto> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM aeroporto");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(new Aeroporto(
                        resultSet.getString("codice"),
                        resultSet.getString("nome"),
                        resultSet.getString("citta")
                ));
            }
        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return list;
    }
    /**
     * esegue una query al database,restituisce un Aeroporto prendendo come parametro una stringa del codice univoco ICAO
     * */
    public Aeroporto getByCodice(String icao) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Aeroporto aeroporto = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM aeroporto WHERE codice = ?");
            statement.setString(1, icao);
            resultSet = statement.executeQuery();
            if (resultSet.next())
                aeroporto = new Aeroporto(resultSet.getString("codice"), resultSet.getString("nome"), resultSet.getString("citta"));
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return aeroporto;
    }
    /**
     * esegue una query al database,restituisce l'aeroporto gestito dal programma
     * */
    public Aeroporto getAeroportoGestito() throws SQLException{
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Aeroporto aeroporto = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM aeroportogestito NATURAL JOIN aeroporto");
            resultSet = statement.executeQuery();
            if (resultSet.next())
                aeroporto = new Aeroporto(resultSet.getString("codice"), resultSet.getString("nome"), resultSet.getString("citta"));
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return aeroporto;
    }
}
