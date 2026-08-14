package com.f1.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa un equipo (escudería) de Fórmula 1.
 * Cada equipo tiene dos pilotos, un motor y un país de origen.
 */
public class Equipo {
    private String nombre;
    private String pais;
    private String motor;
    private List<Integer> pilotoIds;
    private String imagen;

    public Equipo() {
        this.pilotoIds = new ArrayList<>();
    }

    public Equipo(String nombre, String pais, String motor, List<Integer> pilotoIds, String imagen) {
        this.nombre = nombre;
        this.pais = pais;
        this.motor = motor;
        this.pilotoIds = pilotoIds != null ? new ArrayList<>(pilotoIds) : new ArrayList<>();
        this.imagen = imagen;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getPais() { return pais; }
    public String getMotor() { return motor; }
    public List<Integer> getPilotoIds() { return new ArrayList<>(pilotoIds); }
    public String getImagen() { return imagen; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPais(String pais) { this.pais = pais; }
    public void setMotor(String motor) { this.motor = motor; }
    public void setPilotoIds(List<Integer> pilotoIds) { this.pilotoIds = new ArrayList<>(pilotoIds); }
    public void setImagen(String imagen) { this.imagen = imagen; }

    /**
     * Agrega un piloto al equipo.
     */
    public void agregarPiloto(int pilotoId) {
        if (!pilotoIds.contains(pilotoId)) {
            pilotoIds.add(pilotoId);
        }
    }

    /**
     * Remueve un piloto del equipo.
     */
    public void removerPiloto(int pilotoId) {
        pilotoIds.remove(Integer.valueOf(pilotoId));
    }

    @Override
    public String toString() {
        return String.format("%s (%s - Motor: %s)", nombre, pais, motor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        return Objects.equals(nombre, equipo.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
