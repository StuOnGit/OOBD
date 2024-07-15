package database.dao;

import data.CodaImbarco;
import data.Compagnia;
import data.Gate;
import data.Tratta;
import database.PGConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GateDao {
    // da tutti i gate
    public List<Gate> getGateCodes() throws SQLException {
        List<Gate> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM gate ORDER BY codicegate");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Tratta tratta = new TrattaDao().getByNumeroVolo(resultSet.getString("tratta"));
                List<CodaImbarco> code = new ArrayList();
                if (tratta != null)
                    code = new CodaImbarcoDao().getByGateAndTratta(resultSet.getString("CodiceGate"), tratta.getNumeroVolo());
                Boolean chiuso = false;
                if (resultSet.getString("stato").toUpperCase().contains("CHIUSO")) chiuso = true;
                list.add(new Gate(resultSet.getString("CodiceGate"), tratta, code, chiuso));
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

    // aggiorna lo stato di un gate e la tratta che sta imbarcando
    public void update(Gate gate) throws SQLException{
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement(String.format("UPDATE gate SET stato = '%s', tratta = ? WHERE codicegate = ?", gate.getStatus().toString()));
            if (gate.getTratta() != null)
                statement.setString(1, gate.getTratta().getNumeroVolo());
            else
                statement.setNull(1, Types.VARCHAR);
            statement.setString(2, gate.getGateCode());
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    // da i gate tramite lo stato
    public List<Gate> getGateWithStatus(boolean isOccupato, boolean isLibero, boolean isChiuso) throws SQLException{
        List<Gate> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM gate WHERE FALSE";
        if (isOccupato)
            query += " or stato = 'OCCUPATO'";
        if (isLibero)
            query += " or stato = 'LIBERO'";
        if (isChiuso)
            query += " or stato = 'CHIUSO'";
        query += " ORDER BY codicegate";
        try {
            statement = PGConnection.getConnection().prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Tratta tratta = new TrattaDao().getByNumeroVolo(resultSet.getString("tratta"));
                List<CodaImbarco> code = new ArrayList();
                if (tratta != null)
                    code = new CodaImbarcoDao().getByGateAndTratta(resultSet.getString("CodiceGate"), tratta.getNumeroVolo());
                Boolean chiuso = false;
                if (resultSet.getString("stato").toUpperCase().contains("CHIUSO")) chiuso = true;
                list.add(new Gate(resultSet.getString("CodiceGate"), tratta, code, chiuso));
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
}
