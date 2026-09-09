package com.f1.domain.model;

import java.util.Objects;

/**
 * Representa un piloto de FÃ³rmula 1.
 * Cada piloto pertenece a un equipo y tiene un rol (LÃ­der o Escudero).
 */
public class Piloto {
    private int id;
    private String nombre;
    private String equipo;
    private String rol;
    private int experiencia;    // AÃ±os de experiencia en F1
    private double habilidad;   // Nivel de habilidad (1-100)

    public Piloto() {}

    public Piloto(int id, String nombre, String equipo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.equipo = equipo;
        this.rol = rol;
        this.experiencia = 5;
        this.habilidad = 80.0;
    }

    public Piloto(int id, String nombre, String equipo, String rol, int experiencia, double habilidad) {
        this.id = id;
        this.nombre = nombre;
        this.equipo = equipo;
        this.rol = rol;
        this.experiencia = experiencia;
        this.habilidad = habilidad;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEquipo() { return equipo; }
    public String getRol() { return rol; }
    public int getExperiencia() { return experiencia; }
    public double getHabilidad() { return habilidad; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEquipo(String equipo) { this.equipo = equipo; }
    public void setRol(String rol) { this.rol = rol; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }
    public void setHabilidad(double habilidad) { this.habilidad = Math.max(0, Math.min(100, habilidad)); }

    @Override
    public String toString() {
        return String.format("#%d %s (%s - %s)", id, nombre, equipo, rol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piloto piloto = (Piloto) o;
        return id == piloto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
