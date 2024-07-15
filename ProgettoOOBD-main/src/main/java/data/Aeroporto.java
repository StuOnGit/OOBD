package data;

import java.util.Objects;

public class Aeroporto {
    /**
     * @param codiceICAO,nome,citta attributi dell'aeroporto
     * */
    private String codiceICAO;
    private String nome;
    private String citta;
    /**
     * costruttore della suddetta classe
     * */
    public Aeroporto(String codiceICAO, String nome, String citta) {
        this.codiceICAO = codiceICAO;
        this.nome = nome;
        this.citta = citta;
    }
    /**
     * Override del metodo "toString" dalla superclasse "Object"
     * */
    @Override
    public String toString() {
        return citta + "-" + nome;
    }
    /**
     * Override del metodo "equals" dalla superclasse "Object",
     * utile per vedere se due aeroporti sono uguali.
     * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aeroporto aeroporto = (Aeroporto) o;
        return Objects.equals(codiceICAO, aeroporto.codiceICAO);
    }

    /**
     * getter e setter della suddetta classe
     * */
    public String getCodiceICAO() {
        return codiceICAO;
    }

    public String getNome() {
        return nome;
    }

    public String getCitta() {
        return citta;
    }

    public void setCodiceICAO(String codiceICAO) {
        this.codiceICAO = codiceICAO;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setCitta(String citta) {
        this.citta = citta;
    }
}
