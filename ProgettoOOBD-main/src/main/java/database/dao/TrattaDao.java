package database.dao;

import data.Aeroporto;
import data.Compagnia;
import data.Gate;
import data.Tratta;
import database.PGConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrattaDao {
    // restituisce tutte le tratte
    public List<Tratta> getAllTratte() throws SQLException {
        List<Tratta> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM tratta ORDER BY datapartenza, orapartenza, compagnia");
            resultSet = statement.executeQuery();
            AeroportoDao aDao = new AeroportoDao();
            CompagniaDao cDao = new CompagniaDao();

            while (resultSet.next()) {
                Aeroporto partenza = aDao.getByCodice(resultSet.getString("AeroportoPartenza"));
                Aeroporto arrivo = aDao.getByCodice(resultSet.getString("AeroportoArrivo"));
                Compagnia comp = cDao.getByNome(resultSet.getString("Compagnia"));
                Tratta tratta = new Tratta(
                    resultSet.getString("NumeroVolo"),
                    resultSet.getDate("DataPartenza").toLocalDate(),
                    resultSet.getTime("OraPartenza").toLocalTime(),
                    resultSet.getInt("DurataVolo"),
                    resultSet.getInt("ritardo"),
                    resultSet.getBoolean("conclusa"),
                    resultSet.getString("CodiceGate"),
                    comp,
                    partenza,
                    arrivo,
                    resultSet.getInt("posti")
                );
                list.add(tratta);
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

    // tratte con data da from a to
    public List<Tratta> getTratteWithDate(LocalDate from, LocalDate to) throws SQLException {
        List<Tratta> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM tratta WHERE TRUE";

        if (from != null){
            query += " AND datapartenza >= '" + Date.valueOf(from) + "'";
        }
        if (to != null){
            query += " AND datapartenza <= '" + Date.valueOf(to) + "'";
        }

        try {
            statement = PGConnection.getConnection().prepareStatement(query + " ORDER BY datapartenza, orapartenza, compagnia");
            resultSet = statement.executeQuery();
            AeroportoDao aDao = new AeroportoDao();
            CompagniaDao cDao = new CompagniaDao();

            while (resultSet.next()) {
                Aeroporto partenza = aDao.getByCodice(resultSet.getString("AeroportoPartenza"));
                Aeroporto arrivo = aDao.getByCodice(resultSet.getString("AeroportoArrivo"));
                Compagnia comp = cDao.getByNome(resultSet.getString("Compagnia"));
                Tratta tratta = new Tratta(
                        resultSet.getString("NumeroVolo"),
                        resultSet.getDate("DataPartenza").toLocalDate(),
                        resultSet.getTime("OraPartenza").toLocalTime(),
                        resultSet.getInt("DurataVolo"),
                        resultSet.getInt("ritardo"),
                        resultSet.getBoolean("conclusa"),
                        resultSet.getString("CodiceGate"),
                        comp,
                        partenza,
                        arrivo,
                        resultSet.getInt("posti")
                );
                list.add(tratta);
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

    // inserisce una nuova tratta
    public void insert(Tratta tratta) throws SQLException {
        PreparedStatement statement = null;

        statement = PGConnection.getConnection().prepareStatement("insert into tratta values " + tratta.toString());
        statement.executeUpdate();

        if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
        if (statement != null) statement.close();
    }

    // restituisce una tratta con numeroVolo uguale all'input
    public Tratta getByNumeroVolo(String numeroVolo) throws SQLException {
        Tratta t = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM tratta WHERE numerovolo = ? ORDER BY datapartenza, orapartenza, compagnia");
            statement.setString(1, numeroVolo);
            resultSet = statement.executeQuery();
            AeroportoDao aDao = new AeroportoDao();
            CompagniaDao cDao = new CompagniaDao();

            if (resultSet.next()) {
                Aeroporto partenza = aDao.getByCodice(resultSet.getString("AeroportoPartenza"));
                Aeroporto arrivo = aDao.getByCodice(resultSet.getString("AeroportoArrivo"));
                Compagnia comp = cDao.getByNome(resultSet.getString("Compagnia"));

                t = new Tratta(
                        resultSet.getString("NumeroVolo"),
                        resultSet.getDate("DataPartenza").toLocalDate(),
                        resultSet.getTime("OraPartenza").toLocalTime(),
                        resultSet.getInt("DurataVolo"),
                        resultSet.getInt("ritardo"),
                        resultSet.getBoolean("conclusa"),
                        resultSet.getString("CodiceGate"),
                        comp,
                        partenza,
                        arrivo,
                        resultSet.getInt("posti")
                );
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return t;
    }

    // aggiorna la tratta impostando il gate e il ritardo
    public void update(Tratta tratta) throws SQLException {
        PreparedStatement statement = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("UPDATE tratta SET conclusa = ?, codicegate = ?, ritardo = ? WHERE numerovolo = ?");
            statement.setBoolean(1, tratta.isConclusa());
            statement.setString(2, tratta.getGate());
            statement.setInt(3, tratta.getRitardo());
            statement.setString(4, tratta.getNumeroVolo());
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    // restituisce le tratte ancora non concluse di una specifica compagnia
    public List<Tratta> getTratteAperte(Compagnia compagnia) throws SQLException {
        List<Tratta> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            if (compagnia == null)
                statement = PGConnection.getConnection().prepareStatement("SELECT * FROM tratta WHERE NOT conclusa AND codicegate IS NULL ORDER BY datapartenza, orapartenza, compagnia");
            else {
                statement = PGConnection.getConnection().prepareStatement("SELECT * FROM tratta WHERE NOT conclusa AND codicegate IS NULL AND compagnia = ? ORDER BY datapartenza, orapartenza, compagnia");
                statement.setString(1, compagnia.getNome());
            }
            resultSet = statement.executeQuery();
            AeroportoDao aDao = new AeroportoDao();
            CompagniaDao cDao = new CompagniaDao();

            while (resultSet.next()) {
                Aeroporto partenza = aDao.getByCodice(resultSet.getString("AeroportoPartenza"));
                Aeroporto arrivo = aDao.getByCodice(resultSet.getString("AeroportoArrivo"));
                Compagnia comp = cDao.getByNome(resultSet.getString("Compagnia"));

                Tratta tratta = new Tratta(
                        resultSet.getString("NumeroVolo"),
                        resultSet.getDate("DataPartenza").toLocalDate(),
                        resultSet.getTime("OraPartenza").toLocalTime(),
                        resultSet.getInt("DurataVolo"),
                        resultSet.getInt("ritardo"),
                        resultSet.getBoolean("conclusa"),
                        resultSet.getString("CodiceGate"),
                        comp,
                        partenza,
                        arrivo,
                        resultSet.getInt("posti")
                );
                list.add(tratta);
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

    // i passeggeri prenotati per la tratta
    public int getPasseggeri(Tratta tratta)throws SQLException{
        int passeggeri = 0;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM passeggeritotali WHERE numerovolo = ?");
            statement.setString(1, tratta.getNumeroVolo());
            resultSet = statement.executeQuery();
            AeroportoDao aDao = new AeroportoDao();
            CompagniaDao cDao = new CompagniaDao();

            if (resultSet.next()){
                passeggeri = resultSet.getInt("Passeggeri");
            }
        } catch (SQLException | NullPointerException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }

        return passeggeri;
    }

    // cancella la tratta con le code e i biglietti
    public void deleteTratta(Tratta tratta) throws SQLException{
        PreparedStatement statement = null;
        BigliettoDao bDao = new BigliettoDao();
        CodaImbarcoDao cDao = new CodaImbarcoDao();

        bDao.deleteByTratta(tratta);
        cDao.deleteByTratta(tratta);
        try {
            statement = PGConnection.getConnection().prepareStatement("DELETE FROM tratta WHERE numerovolo = ?");
            statement.setString(1, tratta.getNumeroVolo());
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }
}
