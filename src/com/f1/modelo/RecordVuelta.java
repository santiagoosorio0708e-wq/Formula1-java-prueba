package com.f1.modelo;

/**
 * Representa un récord de vuelta en un circuito.
 */
public class RecordVuelta {
    private String tiempo;
    private String piloto;
    private int anio;

    public RecordVuelta() {}

    public RecordVuelta(String tiempo, String piloto, int anio) {
        this.tiempo = tiempo;
        this.piloto = piloto;
        this.anio = anio;
    }

    // Getters
    public String getTiempo() { return tiempo; }
    public String getPiloto() { return piloto; }
    public int getAnio() { return anio; }

    // Setters
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }
    public void setPiloto(String piloto) { this.piloto = piloto; }
    public void setAnio(int anio) { this.anio = anio; }

    @Override
    public String toString() {
        return String.format("%s por %s (%d)", tiempo, piloto, anio);
    }
}
