package com.f1.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa un circuito de Fórmula 1 con sus características,
 * récord de vuelta y historial de ganadores.
 */
public class Circuito {
    private String nombre;
    private String pais;
    private double longitudKm;
    private int vueltas;
    private String descripcion;
    private RecordVuelta record;
    private List<GanadorHistorico> ganadores;
    private String imagen;

    public Circuito() {
        this.ganadores = new ArrayList<>();
    }

    public Circuito(String nombre, String pais, double longitudKm, int vueltas,
                    String descripcion, RecordVuelta record,
                    List<GanadorHistorico> ganadores, String imagen) {
        this.nombre = nombre;
        this.pais = pais;
        this.longitudKm = longitudKm;
        this.vueltas = vueltas;
        this.descripcion = descripcion;
        this.record = record;
        this.ganadores = ganadores != null ? new ArrayList<>(ganadores) : new ArrayList<>();
        this.imagen = imagen;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getPais() { return pais; }
    public double getLongitudKm() { return longitudKm; }
    public int getVueltas() { return vueltas; }
    public String getDescripcion() { return descripcion; }
    public RecordVuelta getRecord() { return record; }
    public List<GanadorHistorico> getGanadores() { return new ArrayList<>(ganadores); }
    public String getImagen() { return imagen; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPais(String pais) { this.pais = pais; }
    public void setLongitudKm(double longitudKm) { this.longitudKm = longitudKm; }
    public void setVueltas(int vueltas) { this.vueltas = vueltas; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setRecord(RecordVuelta record) { this.record = record; }
    public void setGanadores(List<GanadorHistorico> ganadores) { this.ganadores = new ArrayList<>(ganadores); }
    public void setImagen(String imagen) { this.imagen = imagen; }

    /**
     * Agrega un ganador histórico al circuito.
     */
    public void agregarGanador(GanadorHistorico ganador) {
        this.ganadores.add(ganador);
    }

    /**
     * Calcula la distancia total de la carrera en km.
     */
    public double getDistanciaTotal() {
        return longitudKm * vueltas;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.2f km x %d vueltas", nombre, pais, longitudKm, vueltas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Circuito circuito = (Circuito) o;
        return Objects.equals(nombre, circuito.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
