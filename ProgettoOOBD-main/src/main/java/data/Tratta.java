package data;

import java.sql.Date;
import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Tratta {
    private String numeroVolo;
    private LocalDate dataPartenza;
    private LocalTime oraPartenza;
    private int durataVolo;
    private int ritardo = 0;
    private boolean conclusa = false;
    private Compagnia compagnia;
    private String gate = null;
    private Aeroporto aereoportoPartenza;
    private Aeroporto aereoportoArrivo;
    private int posti;

    public DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu"); // formato per visualizzare la data
    public DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm"); // formato per visualizzare l'ora


    public Tratta(String numeroVolo, LocalDate dataPartenza, LocalTime oraPartenza, int durataVolo, int ritardo, boolean conclusa, String gate, Compagnia compagnia, Aeroporto aereoportoPartenza, Aeroporto aereoportoArrivo, int posti) {
        this.numeroVolo = numeroVolo;
        this.dataPartenza = dataPartenza;
        this.oraPartenza = oraPartenza;
        this.durataVolo = durataVolo;
        this.ritardo = ritardo;
        this.conclusa = conclusa;
        this.gate = gate;
        this.compagnia = compagnia;
        this.aereoportoPartenza = aereoportoPartenza;
        this.aereoportoArrivo = aereoportoArrivo;
        this.posti = posti;
    }

    public Tratta(Tratta tratta) {
        this.numeroVolo = tratta.getNumeroVolo();
        this.dataPartenza = tratta.getDataPartenza();
        this.oraPartenza = tratta.getOraPartenza();
        this.durataVolo = tratta.getDurataVolo();
        this.ritardo = tratta.getRitardo();
        this.conclusa = tratta.isConclusa();
        this.gate = tratta.getGate();
        this.compagnia = tratta.getCompagnia();
        this.aereoportoPartenza = tratta.getAereoportoPartenza();
        this.aereoportoArrivo = tratta.getAereoportoArrivo();
    }

    // genera i valori da inserire nel database
    @Override
    public String toString() {
        return "(" +
                "'" + numeroVolo.toUpperCase() + '\'' +
                ", '" + Date.valueOf(dataPartenza) + '\'' +
                ", '" + Time.valueOf(oraPartenza) + '\'' +
                ", " + durataVolo +
                ", " + ritardo +
                ", " + conclusa +
                ", " + ((gate == null)? "NULL": "'" + gate + "'")   +
                ", '" + compagnia.getNome() + '\'' +
                ", '" + aereoportoPartenza.getCodiceICAO() + '\'' +
                ", '" + aereoportoArrivo.getCodiceICAO() + '\'' +
                ", " + posti +
                ')';
    }

    // danno la data e l'ora formattate
    public String getDataPartenzaFormatted(){
        return dateFormat.format(dataPartenza);
    }
    public String getOraPartenzaFormatted(){
        return timeFormat.format(oraPartenza);
    }

    public boolean isConclusa() {
        return conclusa;
    }

    public int getRitardo() {
        return ritardo;
    }

    public void setRitardo(int ritardo) {
        this.ritardo = ritardo;
    }

    public void setNumeroVolo(String numeroVolo) {
        this.numeroVolo = numeroVolo;
    }

    public void setDataPartenza(LocalDate dataPartenza) {
        this.dataPartenza = dataPartenza;
    }

    public void setOraPartenza(LocalTime oraPartenza) {
        this.oraPartenza = oraPartenza;
    }

    public void setCompagnia(Compagnia compagnia) {
        this.compagnia = compagnia;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public void setAereoportoPartenza(Aeroporto aereoportoPartenza) {
        this.aereoportoPartenza = aereoportoPartenza;
    }

    public void setAereoportoArrivo(Aeroporto aereoportoArrivo) {
        this.aereoportoArrivo = aereoportoArrivo;
    }

    public String getNumeroVolo() {
        return numeroVolo;
    }

    public LocalDate getDataPartenza() {
        return dataPartenza;
    }

    public LocalTime getOraPartenza() {
        return oraPartenza;
    }

    public Compagnia getCompagnia() {
        return compagnia;
    }

    public String getGate() {
        return gate;
    }

    public Aeroporto getAereoportoPartenza() {
        return aereoportoPartenza;
    }

    public Aeroporto getAereoportoArrivo() {
        return aereoportoArrivo;
    }

    public int getDurataVolo() {
        return durataVolo;
    }

    public void setDurataVolo(int durataVolo) {
        this.durataVolo = durataVolo;
    }

    // da la distanza di tempo in minuti tra data e ora di partenza e data e ora attuali
    public int dateDistance(){
        return (int) ChronoUnit.MINUTES.between(LocalDateTime.of(dataPartenza, oraPartenza), Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public int getPosti() {
        return posti;
    }

    public void setPosti(int posti) {
        this.posti = posti;
    }

    // quando viene terminato l'imbarco viene impostato il ritardo
    public void concludi() {
        ritardo = dateDistance();
        conclusa = true;
    }
}
