package com.f1.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representa un vehículo de Fórmula 1 con su rendimiento detallado
 * bajo diferentes modos de conducción y condiciones climáticas.
 */
public class Vehiculo {
    private String equipo;
    private String modelo;
    private String motor;
    private double velocidadMaximaKmh;
    private double aceleracion0a100;
    private List<Integer> pilotoIds;
    private Map<String, ModoConduccion> rendimiento; // "conduccion_normal", "conduccion_agresiva", "ahorro_combustible"
    private String imagen;

    public Vehiculo() {
        this.pilotoIds = new ArrayList<>();
        this.rendimiento = new HashMap<>();
    }

    public Vehiculo(String equipo, String modelo, String motor,
                    double velocidadMaximaKmh, double aceleracion0a100,
                    List<Integer> pilotoIds, Map<String, ModoConduccion> rendimiento,
                    String imagen) {
        this.equipo = equipo;
        this.modelo = modelo;
        this.motor = motor;
        this.velocidadMaximaKmh = velocidadMaximaKmh;
        this.aceleracion0a100 = aceleracion0a100;
        this.pilotoIds = pilotoIds != null ? new ArrayList<>(pilotoIds) : new ArrayList<>();
        this.rendimiento = rendimiento != null ? new HashMap<>(rendimiento) : new HashMap<>();
        this.imagen = imagen;
    }

    // Getters
    public String getEquipo() { return equipo; }
    public String getModelo() { return modelo; }
    public String getMotor() { return motor; }
    public double getVelocidadMaximaKmh() { return velocidadMaximaKmh; }
    public double getAceleracion0a100() { return aceleracion0a100; }
    public List<Integer> getPilotoIds() { return new ArrayList<>(pilotoIds); }
    public Map<String, ModoConduccion> getRendimiento() { return rendimiento; }
    public String getImagen() { return imagen; }

    // Setters
    public void setEquipo(String equipo) { this.equipo = equipo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setMotor(String motor) { this.motor = motor; }
    public void setVelocidadMaximaKmh(double velocidadMaximaKmh) { this.velocidadMaximaKmh = velocidadMaximaKmh; }
    public void setAceleracion0a100(double aceleracion0a100) { this.aceleracion0a100 = aceleracion0a100; }
    public void setPilotoIds(List<Integer> pilotoIds) { this.pilotoIds = new ArrayList<>(pilotoIds); }
    public void setRendimiento(Map<String, ModoConduccion> rendimiento) { this.rendimiento = new HashMap<>(rendimiento); }
    public void setImagen(String imagen) { this.imagen = imagen; }

    /**
     * Obtiene el modo de conducción especificado.
     * @param modo "conduccion_normal", "conduccion_agresiva", "ahorro_combustible"
     * @return ModoConduccion con los parámetros del modo, o null si no existe
     */
    public ModoConduccion getModoConduccion(String modo) {
        return rendimiento.get(modo);
    }

    /**
     * Agrega o actualiza un modo de conducción.
     */
    public void setModoConduccion(String nombre, ModoConduccion modo) {
        rendimiento.put(nombre, modo);
    }

    @Override
    public String toString() {
        return String.format("%s %s (Motor: %s | Vel. Máx: %.0f km/h | 0-100: %.1fs)",
                equipo, modelo, motor, velocidadMaximaKmh, aceleracion0a100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return Objects.equals(equipo, vehiculo.equipo) && Objects.equals(modelo, vehiculo.modelo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipo, modelo);
    }
}
