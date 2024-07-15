package database.dao;

import data.Compagnia;
import database.PGConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompagniaDao {
    // da tutte le compagnie
    public List<Compagnia> getCompagnie() throws SQLException {
        List<Compagnia> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM compagnia");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(new Compagnia(
                        resultSet.getString("nome"),
                        resultSet.getString("sigla"),
                        resultSet.getString("nazione"),
                        resultSet.getFloat("prezzobagagli"),
                        resultSet.getFloat("pesomassimo")
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

    public Compagnia getByNome(String nome) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Compagnia comp = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM compagnia WHERE nome = ?");
            statement.setString(1, nome);
            resultSet = statement.executeQuery();
            if (resultSet.next())
                comp = new Compagnia(
                        resultSet.getString("nome"),
                        resultSet.getString("sigla"),
                        resultSet.getString("nazione"),
                        resultSet.getFloat("prezzobagagli"),
                        resultSet.getFloat("pesomassimo")
                );
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return comp;
    }

    // inserisce una nuova compagnia
    public void insert(Compagnia compagnia) throws SQLException{
        PreparedStatement statement = PGConnection.getConnection().prepareStatement("insert into compagnia(nome, sigla, nazione, prezzobagagli, pesomassimo) values(?, ?, ?, ?, ?)");
        statement.setString(1, compagnia.getNome());
        statement.setString(2, compagnia.getSigla());
        statement.setString(3, compagnia.getNazione());
        statement.setFloat(4, compagnia.getPrezzoBagagli());
        statement.setFloat(5, compagnia.getPesoMassimo());

        statement.executeUpdate();

        if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
        if (statement != null) statement.close();
    }
}
