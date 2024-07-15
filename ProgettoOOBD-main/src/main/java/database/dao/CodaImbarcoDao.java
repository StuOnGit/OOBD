package database.dao;

import data.*;
import database.PGConnection;
import enumeration.CodeEnum;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CodaImbarcoDao {
    public List<CodaImbarco> getAll() throws SQLException {
        List<CodaImbarco> list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM codaimbarco");
            resultSet = statement.executeQuery();
            CompagniaDao cDao = new CompagniaDao();
            while (resultSet.next()) {
                Compagnia compagnia = cDao.getByNome(resultSet.getString("compagnia"));
                list.add(new CodaImbarco(
                        resultSet.getInt("codiceCoda"),
                        resultSet.getTimestamp("oraApertura").toLocalDateTime(),
                        resultSet.getString("classe"),
                        resultSet.getInt("tempoStimato"),
                        resultSet.getInt("tempoEffettivo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }

        return list;
    }

    public List<CodaImbarco> getByGateAndTratta(String gate, String tratta) throws SQLException {
        List<CodaImbarco> list = new LinkedList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM codaimbarco WHERE codicegate = ? AND numerovolo = ?");
            statement.setString(1, gate);
            statement.setString(2, tratta);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("oraApertura");
                list.add(new CodaImbarco(
                        resultSet.getInt("codiceCoda"),
                        (timestamp != null) ? timestamp.toLocalDateTime() : null,
                        resultSet.getString("classe"),
                        resultSet.getInt("tempoStimato"),
                        resultSet.getInt("tempoEffettivo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return list;
    }

    public List<CodaImbarco> getByTratta(Tratta t) throws SQLException {
        List<CodaImbarco> list = new LinkedList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM codaimbarco WHERE numerovolo = ?");
            statement.setString(1, t.getNumeroVolo());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("oraApertura");
                list.add(new CodaImbarco(
                        resultSet.getInt("codiceCoda"),
                        (timestamp != null) ? timestamp.toLocalDateTime() : null,
                        resultSet.getString("classe"),
                        resultSet.getInt("tempoStimato"),
                        resultSet.getInt("tempoEffettivo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return list;
    }

    public CodaImbarco getByCodice(int codice) throws SQLException {
        CodaImbarco coda = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM codaimbarco WHERE codicecoda = ?");
            statement.setInt(1, codice);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("oraApertura");
                coda = new CodaImbarco(
                        resultSet.getInt("codiceCoda"),
                        (timestamp != null) ? timestamp.toLocalDateTime() : null,
                        resultSet.getString("classe"),
                        resultSet.getInt("tempoStimato"),
                        resultSet.getInt("tempoEffettivo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return coda;
    }

    public void addNew(CodeEnum coda, Tratta tratta) throws SQLException {
        PreparedStatement statement = null;

        try {
            statement = PGConnection.getConnection().prepareStatement(String.format("insert into codaimbarco(classe, NumeroVolo) values('%s', ?)", coda.toString()));
            statement.setString(1, tratta.getNumeroVolo());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    public void apriCoda(CodaImbarco coda) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("UPDATE codaimbarco SET codicegate = ? WHERE codicecoda = ?");
            statement.setString(1, coda.getCodiceGate());
            statement.setInt(2, coda.getCodiceCoda());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    public void chiudiCoda(CodaImbarco coda) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("UPDATE codaimbarco SET tempoeffettivo = ?, codicegate = ? WHERE codicecoda = ? AND (tempoeffettivo is NULL OR tempoeffettivo = 0)");
            statement.setInt(1, coda.getTempoEffettivo());
            statement.setString(2, coda.getCodiceGate());
            statement.setInt(3, coda.getCodiceCoda());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    public CodaImbarco getByBiglietto(Biglietto biglietto) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        CodaImbarco coda = null;

        try {
            statement = PGConnection.getConnection().prepareStatement("SELECT * FROM codaimbarco WHERE numerovolo = ? AND classe = '" + biglietto.getClasse() + "'");
            statement.setString(1, biglietto.getNumeroVolo());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("oraApertura");
                coda = new CodaImbarco(
                        resultSet.getInt("codiceCoda"),
                        (timestamp != null) ? timestamp.toLocalDateTime() : null,
                        resultSet.getString("classe"),
                        resultSet.getInt("tempoStimato"),
                        resultSet.getInt("tempoEffettivo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return coda;
    }


    public void deleteByTratta(Tratta tratta) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = PGConnection.getConnection().prepareStatement("DELETE FROM codaimbarco WHERE numerovolo = ?");
            statement.setString(1, tratta.getNumeroVolo());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
        }
    }

    public int minutiStimatiGiorno(LocalDate date, Gate gate) throws SQLException {
        Integer minuti = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT SUM(tempostimato) as minuti FROM codaimbarco WHERE DATE(oraapertura)= ? AND codicegate = ?";
        try {
            statement = PGConnection.getConnection().prepareStatement(query);
            statement.setDate(1, Date.valueOf(date));
            statement.setString(2, gate.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                minuti = resultSet.getInt("minuti");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
        }
        if (minuti != null) {
            return minuti;
        } else return 0;
    }

    public int minutiEffettiviGiorno(LocalDate date, Gate gate) throws SQLException {
        Integer minuti = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT SUM(tempoeffettivo) as minuti FROM codaimbarco WHERE DATE(oraapertura)= ? AND codicegate = ?";
        try {
            statement = PGConnection.getConnection().prepareStatement(query);
            statement.setDate(1, Date.valueOf(date));
            statement.setString(2, gate.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                minuti = resultSet.getInt("minuti");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
            if (minuti != null) {
                return minuti;
            } else return 0;
        }
    }

    public int minutiEffettiviSettimana(LocalDate date, Gate gate) throws SQLException {
        Integer minuti = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT SUM(tempoeffettivo) as minuti FROM codaimbarco WHERE (DATE(oraapertura) BETWEEN ? AND ?) AND codicegate = ?";
        try{
             statement = PGConnection.getConnection().prepareStatement(query);
             statement.setDate(1, Date.valueOf(date));
            statement.setDate(2, Date.valueOf(date.plusDays(7)));
            statement.setString(3, gate.toString());
             resultSet = statement.executeQuery();
             if (resultSet.next()) {
                minuti = resultSet.getInt("minuti");
            }
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
        }
        if (minuti != null) {
            return minuti;
        } else return 0;
    }

    public int minutiStimatiSettimana(LocalDate date, Gate gate) throws SQLException {
        Integer minuti = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT SUM(tempostimato) as minuti FROM codaimbarco WHERE (DATE(oraapertura) BETWEEN ? AND ?) AND codicegate = ?";
        try{
            statement = PGConnection.getConnection().prepareStatement(query);
            statement.setDate(1, Date.valueOf(date));
            statement.setDate(2, Date.valueOf(date.plusDays(7)));
            statement.setString(3, gate.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                minuti = resultSet.getInt("minuti");
            }
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
        }
        if (minuti != null) {
            return minuti;
        } else return 0;
    }

    public int minutiStimatiMese(int anno,int mese, Gate gate) throws SQLException {
        Integer minuti = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT SUM(tempostimato) as minuti FROM codaimbarco WHERE EXTRACT(year from oraapertura) = ? AND EXTRACT(month from oraapertura) = ? AND codicegate = ?";
        try{
            statement = PGConnection.getConnection().prepareStatement(query);
            statement.setInt(1, anno);
            statement.setInt(2, mese);
            statement.setString(3, gate.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                minuti = resultSet.getInt("minuti");
            }
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
        }
        if (minuti != null) {
            return minuti;
        } else return 0;
    }

    public int minutiEffettiviMese(int anno,int mese, Gate gate) throws SQLException {
        Integer minuti = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT SUM(tempoeffettivo) as minuti FROM codaimbarco WHERE EXTRACT(year from oraapertura) = ? AND EXTRACT(month from oraapertura) = ? AND codicegate = ?";
        try{
            statement = PGConnection.getConnection().prepareStatement(query);
            statement.setInt(1, anno);
            statement.setInt(2, mese);
            statement.setString(3, gate.toString());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                minuti = resultSet.getInt("minuti");
            }
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
            if (PGConnection.getConnection() != null) PGConnection.getConnection().close();
            if (statement != null) statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
        }
        if (minuti != null) {
            return minuti;
        } else return 0;
    }
}