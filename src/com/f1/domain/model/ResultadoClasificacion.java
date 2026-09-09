package com.f1.domain.model;

/**
 * Representa el resultado final de un piloto en la clasificaciÃ³n.
 */
public class ResultadoClasificacion {
    private int posicion;
    private Piloto piloto;
    private String equipo;
    private double tiempoTotal;
    private String tiempoFormateado;
    private String diferencia;
    private int pitStops;
    private double mejorVuelta;
    private String estadoFinal;

    public ResultadoClasificacion() {}

    public ResultadoClasificacion(int posicion, Piloto piloto, double tiempoTotal,
                                   String diferencia, int pitStops, double mejorVuelta,
                                   String estadoFinal) {
        this.posicion = posicion;
        this.piloto = piloto;
        this.equipo = piloto.getEquipo();
        this.tiempoTotal = tiempoTotal;
        this.tiempoFormateado = EstadoCarrera.formatearTiempo(tiempoTotal);
        this.diferencia = diferencia;
        this.pitStops = pitStops;
        this.mejorVuelta = mejorVuelta;
        this.estadoFinal = estadoFinal;
    }

    // Getters
    public int getPosicion() { return posicion; }
    public Piloto getPiloto() { return piloto; }
    public String getEquipo() { return equipo; }
    public double getTiempoTotal() { return tiempoTotal; }
    public String getTiempoFormateado() { return tiempoFormateado; }
    public String getDiferencia() { return diferencia; }
    public int getPitStops() { return pitStops; }
    public double getMejorVuelta() { return mejorVuelta; }
    public String getEstadoFinal() { return estadoFinal; }

    // Setters
    public void setPosicion(int posicion) { this.posicion = posicion; }
    public void setPiloto(Piloto piloto) { this.piloto = piloto; }
    public void setEquipo(String equipo) { this.equipo = equipo; }
    public void setTiempoTotal(double tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
        this.tiempoFormateado = EstadoCarrera.formatearTiempo(tiempoTotal);
    }
    public void setDiferencia(String diferencia) { this.diferencia = diferencia; }
    public void setPitStops(int pitStops) { this.pitStops = pitStops; }
    public void setMejorVuelta(double mejorVuelta) { this.mejorVuelta = mejorVuelta; }
    public void setEstadoFinal(String estadoFinal) { this.estadoFinal = estadoFinal; }

    @Override
    public String toString() {
        return String.format("P%d - %s (%s) | %s | Gap: %s | Pits: %d",
                posicion, piloto.getNombre(), equipo, tiempoFormateado, diferencia, pitStops);
    }
}
