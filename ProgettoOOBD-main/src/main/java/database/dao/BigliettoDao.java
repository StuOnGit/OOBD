package database.dao;

import data.*;
import database.PGConnection;
import enumeration.CodeEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BigliettoDao {
    /**
     * esegue una query al database, restituisce la lista di tutti i biglietti presenti nel Database.
     * */
    public List<Biglietto> getBiglietto() throws SQLException {
        List<Biglietto> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM biglietto");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CodeEnum c = new CodaImbarcoDao().getByCodice(resultSet.getInt("codicecoda")).getClasse();

                list.add(new Biglietto(
                            resultSet.getInt("codicebiglietto"),
                            resultSet.getInt("prezzo"),
                            resultSet.getInt("posto"),
                            c,
                            resultSet.getBoolean("checkin"),
                            resultSet.getBoolean("imbarcato"),
                            resultSet.getString("numerovolo"),
                            new Cliente(
                                    resultSet.getString("cf"),
                                    resultSet.getString("NomeCliente"),
                                    resultSet.getString("Documento")
                            )
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
     * esegue una query al database, restituisce un biglietto in base al codice univoco del biglietto
     * */
    public Biglietto getBigliettoByCodice(int codice) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Biglietto biglietto = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM biglietto WHERE codicebiglietto = ?");
            statement.setInt(1, codice);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                CodeEnum c = new CodaImbarcoDao().getByCodice(resultSet.getInt("codicecoda")).getClasse();

                biglietto = new Biglietto(
                        resultSet.getInt("codicebiglietto"),
                        resultSet.getInt("prezzo"),
                        resultSet.getInt("posto"),
                        c,
                        resultSet.getBoolean("checkin"),
                        resultSet.getBoolean("imbarcato"),
                        resultSet.getString("numerovolo"),
                        new Cliente(
                                resultSet.getString("cf"),
                                resultSet.getString("NomeCliente"),
                                resultSet.getString("Documento")
                        )
                );
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return biglietto;
    }

    /**
     * fa un update del biglietto, dove setta il checkin e l'imbarco
     * */
    public void update(Biglietto biglietto) throws SQLException{
        PreparedStatement statement = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("UPDATE biglietto SET checkin = ?, imbarcato = ? WHERE codicebiglietto = ?");
            statement.setBoolean(1, biglietto.isCheckIn());
            statement.setBoolean(2, biglietto.isImbarcato());
            statement.setInt(3, biglietto.getCodiceBiglietto());
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }
    /**
     * inserisce all'interno del database il biglietto passato come parametro
     * */
    public void insert(Biglietto biglietto) throws SQLException{
        PreparedStatement statement = null;
        int codiceCoda = new CodaImbarcoDao().getByBiglietto(biglietto).getCodiceCoda();
        statement = PGConnection.getConnection().prepareStatement("INSERT INTO biglietto(prezzo, posto, checkin, imbarcato, numerovolo, nomecliente, documento, codicecoda, cf) values (?,?,?,?,?,?,?,?,?)");
        statement.setDouble(1, biglietto.getPrezzo());
        statement.setInt(2, biglietto.getPosto());
        statement.setBoolean(3, biglietto.isCheckIn());
        statement.setBoolean(4, biglietto.isImbarcato());
        statement.setString(5, biglietto.getNumeroVolo());
        statement.setString(6, biglietto.getCliente().getNome());
        statement.setString(7, biglietto.getCliente().getDocumento());
        statement.setInt(8, codiceCoda);
        statement.setString(9, biglietto.getCliente().getCodiceFiscale());
        statement.executeUpdate();
        if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
        if (statement != null) statement.close();
    }
    /**
     * elimina i biglietti in base alla tratta, utile per quando si eliminano delle tratte
     * */
    public void deleteByTratta(Tratta tratta) throws SQLException{
        PreparedStatement statement = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("DELETE FROM BIGLIETTO WHERE numerovolo = ?");
            statement.setString(1, tratta.getNumeroVolo());
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    /**
     * restituisce la compagnia in base al biglietto passato come parametro
     * */
    public Compagnia getCompagnia(Biglietto biglietto) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Compagnia compagnia = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT  nome, sigla, nazione, pesomassimo, prezzobagagli FROM tratta  JOIN compagnia c on tratta.compagnia = c.nome WHERE tratta.numerovolo = ?");
            statement.setString(1,biglietto.getNumeroVolo());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                compagnia = new Compagnia(resultSet.getString("nome"), resultSet.getString("sigla"),resultSet.getString("nazione"), resultSet.getFloat("pesomassimo"), resultSet.getFloat("prezzobagagli"));
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return compagnia;
    }
}


