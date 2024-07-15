package data;

import enumeration.CodeEnum;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CodaImbarco {
    private int codiceCoda;
    private LocalDateTime oraApertura;
    private String codiceGate;
    private CodeEnum classe;
    private Integer tempoStimato;
    private Integer tempoEffettivo;
    private int passeggeri;

    public CodaImbarco(int codiceCoda, LocalDateTime oraApertura, String classe, int tempoStimato, int tempoEffettivo) {
        this.codiceCoda = codiceCoda;
        setClasse(classe);
        this.tempoStimato = tempoStimato;
        this.tempoEffettivo = tempoEffettivo;
        this.oraApertura = oraApertura;
    }

    public CodaImbarco(CodeEnum classe) {
        this.classe = classe;
        passeggeri = 0;
        tempoStimato = 0;
        tempoEffettivo = null;
    }

    public int getCodiceCoda() {
        return codiceCoda;
    }

    public CodeEnum getClasse() {
        return classe;
    }

    public int getTempoStimato() {
        return tempoStimato;
    }

    public Integer getTempoEffettivo() {
        return tempoEffettivo;
    }

    public int getPasseggeri() {
        return passeggeri;
    }

    public void setClasse(CodeEnum classe) {
        this.classe = classe;
    }

    public void setClasse(String s){
        if (CodeEnum.DIVERSAMENTE_ABILI.toString().equals(s)) classe = CodeEnum.DIVERSAMENTE_ABILI;
        else if (CodeEnum.FAMIGLIE.toString().equals(s)) classe = CodeEnum.FAMIGLIE;
        else if (CodeEnum.BUSINESS.toString().equals(s)) classe = CodeEnum.BUSINESS;
        else if (CodeEnum.PRIORITY.toString().equals(s)) classe = CodeEnum.PRIORITY;
        else if (CodeEnum.ECONOMY.toString().equals(s)) classe = CodeEnum.ECONOMY;
        else classe = CodeEnum.ECONOMY;
    }

    public void stimaTempo() {
        tempoStimato = passeggeri*2;
    } // 2 minuti a passeggero. (forse un po' esagerato)

    public void setTempoEffettivo(int tempoEffettivo) {
        this.tempoEffettivo = tempoEffettivo;
    }
    // imposta il tempo effettivo come la distanza in minuti dall'ora di apertura all'ora corrente
    public void setTempoEffettivo() {
        if(oraApertura != null)
            this.tempoEffettivo = (int) ChronoUnit.MINUTES.between(oraApertura, Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public void setPasseggeri(int passeggeri) {
        this.passeggeri = passeggeri;
    }

    public String getCodiceGate() {
        return codiceGate;
    }

    public void setCodiceGate(String codiceGate) {
        this.codiceGate = codiceGate;
    }

    public LocalDateTime getOraApertura() {
        return oraApertura;
    }

    public void setOraApertura(LocalDateTime oraApertura) {
        this.oraApertura = oraApertura;
    }
}
