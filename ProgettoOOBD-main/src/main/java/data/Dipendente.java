package data;

import enumeration.DipendentiEnum;

// entità del database
public class Dipendente {
    private String codiceImpiegato;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private DipendentiEnum ruolo;
    private Compagnia compagnia;

    //costruttore con codiceImpiegato
    public Dipendente(String codiceImpiegato, String nome, String cognome, String email, String password, DipendentiEnum ruolo, Compagnia compagnia) {
        this.codiceImpiegato = codiceImpiegato;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.compagnia = compagnia;
    }
    //costruttore senza codiceImpiegato
    public Dipendente( String nome, String cognome, String email, String password, DipendentiEnum ruolo, Compagnia compagnia) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.compagnia = compagnia;
    }


    public String getCodiceImpiegato() {
        return codiceImpiegato;
    }

    public void setCodiceImpiegato(String codiceImpiegato) {
        this.codiceImpiegato = codiceImpiegato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DipendentiEnum getRuolo() {
        return ruolo;
    }


    public void setRuolo(DipendentiEnum ruolo) {
        this.ruolo = ruolo;
    }

    public Compagnia getCompagnia() {
        return compagnia;
    }

    public void setCompagnia(Compagnia compagnia) {
        this.compagnia = compagnia;
    }
}
