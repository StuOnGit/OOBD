package data;

public class Bagaglio {
    /**
     * @param codiceBagaglio,peso,prezzo,biglietto attributi della classe "Bagaglio"
     * */
    private String codiceBagaglio ;
    private double peso;
    private double prezzo;
    private Biglietto biglietto;
    /**
     * costruttore della suddetta classe
     * */
    public Bagaglio(String codiceBagaglio, double peso, double prezzo, Biglietto biglietto) {
        this.codiceBagaglio = codiceBagaglio;
        this.peso = peso;
        this.prezzo = prezzo;
        this.biglietto = biglietto;
    }
    /**
     * Override del metodo "toString" dalla superclasse "Object"
     * */
    @Override
    public String toString() {
        return "Bagaglio n. " + codiceBagaglio + ", peso = " +peso+ ", prezzo = "+ prezzo +".";
    }

    /**
     * getter e setter della suddetta classe
     * */
    public String getCodiceBagaglio() {
        return codiceBagaglio;
    }

    public void setCodiceBagaglio(String codiceBagaglio) {
        this.codiceBagaglio = codiceBagaglio;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public Biglietto getBiglietto() {
        return biglietto;
    }

    public void setBiglietto(Biglietto biglietto) {
        this.biglietto = biglietto;
    }
}
