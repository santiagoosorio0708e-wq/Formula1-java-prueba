package com.f1.modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un modo de conducción con sus parámetros de rendimiento.
 * Cada modo tiene diferentes velocidades, consumo de combustible y desgaste
 * de neumáticos dependiendo de las condiciones climáticas.
 */
public class ModoConduccion {
    private double velocidadPromedioKmh;
    private Map<String, Double> consumoCombustible; // "seco", "lluvioso", "extremo"
    private Map<String, Double> desgasteNeumaticos; // "seco", "lluvioso", "extremo"

    public ModoConduccion() {
        this.consumoCombustible = new HashMap<>();
        this.desgasteNeumaticos = new HashMap<>();
    }

    public ModoConduccion(double velocidadPromedioKmh,
                          Map<String, Double> consumoCombustible,
                          Map<String, Double> desgasteNeumaticos) {
        this.velocidadPromedioKmh = velocidadPromedioKmh;
        this.consumoCombustible = consumoCombustible != null ? new HashMap<>(consumoCombustible) : new HashMap<>();
        this.desgasteNeumaticos = desgasteNeumaticos != null ? new HashMap<>(desgasteNeumaticos) : new HashMap<>();
    }

    // Getters
    public double getVelocidadPromedioKmh() { return velocidadPromedioKmh; }
    public Map<String, Double> getConsumoCombustible() { return consumoCombustible; }
    public Map<String, Double> getDesgasteNeumaticos() { return desgasteNeumaticos; }

    // Setters
    public void setVelocidadPromedioKmh(double velocidadPromedioKmh) {
        this.velocidadPromedioKmh = velocidadPromedioKmh;
    }
    public void setConsumoCombustible(Map<String, Double> consumoCombustible) {
        this.consumoCombustible = new HashMap<>(consumoCombustible);
    }
    public void setDesgasteNeumaticos(Map<String, Double> desgasteNeumaticos) {
        this.desgasteNeumaticos = new HashMap<>(desgasteNeumaticos);
    }

    /**
     * Obtiene el consumo de combustible para una condición climática.
     * @param clima "seco", "lluvioso", "extremo", "nublado"
     * @return consumo de combustible por vuelta
     */
    public double getConsumo(String clima) {
        String key = clima.toLowerCase();
        if (key.equals("nublado")) key = "seco"; // Nublado usa datos de seco con leve aumento
        return consumoCombustible.getOrDefault(key, 2.0);
    }

    /**
     * Obtiene el desgaste de neumáticos para una condición climática.
     * @param clima "seco", "lluvioso", "extremo", "nublado"
     * @return desgaste de neumáticos por vuelta
     */
    public double getDesgaste(String clima) {
        String key = clima.toLowerCase();
        if (key.equals("nublado")) key = "seco";
        return desgasteNeumaticos.getOrDefault(key, 1.5);
    }

    @Override
    public String toString() {
        return String.format("Vel: %.0f km/h | Consumo: %s | Desgaste: %s",
                velocidadPromedioKmh, consumoCombustible, desgasteNeumaticos);
    }
}
