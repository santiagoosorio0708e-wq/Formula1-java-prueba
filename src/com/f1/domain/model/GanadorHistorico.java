package com.f1.domain.model;

/**
 * Representa un ganador histÃ³rico en un circuito para una temporada especÃ­fica.
 */
public class GanadorHistorico {
    private int temporada;
    private int pilotoId;

    public GanadorHistorico() {}

    public GanadorHistorico(int temporada, int pilotoId) {
        this.temporada = temporada;
        this.pilotoId = pilotoId;
    }

    // Getters
    public int getTemporada() { return temporada; }
    public int getPilotoId() { return pilotoId; }

    // Setters
    public void setTemporada(int temporada) { this.temporada = temporada; }
    public void setPilotoId(int pilotoId) { this.pilotoId = pilotoId; }

    @Override
    public String toString() {
        return String.format("Temporada %d - Piloto ID: %d", temporada, pilotoId);
    }
}
