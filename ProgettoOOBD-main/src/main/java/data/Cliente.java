package data;

public class Cliente {
    /**
     * @param codiceFiscale,nome,documento attributi utili alla classe "Cliente"
     * */
    private String codiceFiscale;
    private String nome;
    private String documento;

    /**
     * costruttore della suddetta classe
     * */
    public Cliente(String codiceFiscale, String nome, String documento) {
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.documento = documento;
    }

    /**
     * getter e setter della suddetta classe
     * */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
