package data;

import enumeration.GateStatus;
import javafx.util.Pair;
import java.util.LinkedList;
import java.util.List;

// un gate è come se fosse uno stack di lunghezza 1
// setTratta è l'equivalente di un push
// end è l'equivalente di un pop
public class Gate {
    private String gateCode;
    private GateStatus status = GateStatus.LIBERO; // stato del gate.
    private Tratta tratta = null; // Tratta che sta gestendo al momento.
    private List<CodaImbarco> codeImbarco = new LinkedList(); // code della tratta che sta gestendo al momento

    public Gate(String gateCode, Tratta tratta, List<CodaImbarco> codeImbarco, Boolean isClosed) {
        this.gateCode = gateCode;
        if (isClosed) status = GateStatus.CHIUSO;
        setTratta(tratta, codeImbarco);
    }

    public Gate(String gateCode, Boolean isClosed){
        this.gateCode = gateCode;
        if (isClosed) status = GateStatus.CHIUSO;
    }

    @Override
    public String toString() {
        return gateCode;
    }

    public Gate(String gateCode){
        this.gateCode = gateCode;
    }

    public GateStatus getStatus() {
        return status;
    }

    public Tratta getTratta() {
        return tratta;
    }

    public String getGateCode() {
        return gateCode;
    }

    public void setGateCode(String gateCode) {
        this.gateCode = gateCode;
    }

    public void close() {
        if (status != GateStatus.OCCUPATO) status = GateStatus.CHIUSO;
    }
    public void open(){
        if (status != GateStatus.OCCUPATO) status = GateStatus.LIBERO;
    }
    // termina l'imbarco estraendo la tratta e le code. Equivalente di un pop
    public Pair<Tratta, List<CodaImbarco>> end(){ // conclude l'imbarco
        Tratta t = null;
        List<CodaImbarco> code = new LinkedList<>();
        if (status == GateStatus.OCCUPATO) {
            status = GateStatus.LIBERO;
            tratta.concludi();
            codeImbarco.forEach(c -> c.setTempoEffettivo()); // setta il tempo effettivo.
            code.addAll(codeImbarco);
            t = new Tratta(tratta);
            tratta = null;
            codeImbarco.clear();
        }
        return new Pair(t, code);
    }

    // fa una somma dei tempi stimati delle varie code
    public int getTempoStimatoTotale(){
        return codeImbarco.stream().map(CodaImbarco::getTempoStimato).reduce(0, Integer::sum);
    }

    // imposta una tratta da imbarcare solo se è libero. Equivalente di un push
    public void setTratta(Tratta tratta, List<CodaImbarco> codeImbarco) {
        if (status == GateStatus.LIBERO && tratta != null) {
            tratta.setGate(gateCode);
            this.tratta = tratta;
            this.codeImbarco = codeImbarco;
            status = GateStatus.OCCUPATO;
            codeImbarco.forEach(c -> c.setCodiceGate(gateCode));
            //codeImbarco.forEach(c -> c.setOraApertura(Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime()));
            setCodeImbarco(codeImbarco);
        }
    }

    public void addCoda(CodaImbarco codaImbarco) {
        codeImbarco.add(codaImbarco);
    }

    public void setCodeImbarco(List<CodaImbarco> codeImbarco) {
        this.codeImbarco = codeImbarco;
    }

    public List<CodaImbarco> getCodeImbarco() {
        return codeImbarco;
    }

}
