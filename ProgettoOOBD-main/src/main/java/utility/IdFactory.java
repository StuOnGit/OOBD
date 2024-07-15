package utility;

import data.Compagnia;
import database.dao.TrattaDao;

import java.sql.SQLException;
import java.util.Random;

public class IdFactory {
    public String randomString(int len){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(len)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString.toUpperCase();
    }
    public String newNumeroVolo(Compagnia c){
        String numeroVolo = null;
        try {
            TrattaDao tDao = new TrattaDao();
            do {
                numeroVolo = randomString(5);
            }while (tDao.getByNumeroVolo(c.getSigla() + numeroVolo) != null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return c.getSigla() + numeroVolo;
    }
}
