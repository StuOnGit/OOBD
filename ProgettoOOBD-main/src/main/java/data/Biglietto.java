package data;

import enumeration.CodeEnum;

public class Biglietto {
    /**
     * @param cliente,codiceBiglietto,prezzo,posto,classe,checkIn,imbarcato,numeroVolo attributi utili alla classe Biglietto
     * */
    private Cliente cliente;
    private Integer codiceBiglietto;
    private double prezzo;
    private int posto;
    private CodeEnum classe;
    private boolean checkIn, imbarcato;
    private String numeroVolo;
    /**
     * primo costruttore della suddetta classe
     * */
    public Biglietto(Integer codiceBiglietto, double prezzo, int posto, CodeEnum classe, boolean checkIn, boolean imbarcato, String numeroVolo, Cliente cliente) {
        this.codiceBiglietto = codiceBiglietto;
        this.prezzo = prezzo;
        this.posto = posto;
        this.classe = classe;
        this.checkIn = checkIn;
        this.imbarcato = imbarcato;
        this.numeroVolo = numeroVolo;
        this.cliente = cliente;
    }
    /**
     * secondo costruttore della suddetta classe
     * */
    public Biglietto(double prezzo, CodeEnum classe, boolean checkIn, boolean imbarcato, String numeroVolo, Cliente cliente) {
        this.prezzo = prezzo;
        this.posto = posto;
        this.classe = classe;
        this.checkIn = checkIn;
        this.imbarcato = imbarcato;
        this.numeroVolo = numeroVolo;
        this.cliente = cliente;
    }

    /**
     * getter e setter della suddetta classe
     * */
    public Integer getCodiceBiglietto() {
        return codiceBiglietto;
    }

    public void setCodiceBiglietto(Integer codiceBiglietto) {
        this.codiceBiglietto = codiceBiglietto;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getPosto() {
        return posto;
    }

    public void setPosto(int posto) {
        this.posto = posto;
    }

    public CodeEnum getClasse() {
        return classe;
    }

    public void setClasse(CodeEnum classe) {
        this.classe = classe;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }

    public boolean isImbarcato() {
        return imbarcato;
    }

    public void setImbarcato(boolean imbarcato) {
        this.imbarcato = imbarcato;
    }

    public String getNumeroVolo() {
        return numeroVolo;
    }

    public void setNumeroVolo(String numeroVolo) {
        this.numeroVolo = numeroVolo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
