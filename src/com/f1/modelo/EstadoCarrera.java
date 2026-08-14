package com.f1.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el estado en tiempo real de un piloto durante la carrera.
 * Gestiona combustible, desgaste de neumáticos, tiempos de vuelta y pit stops.
 */
public class EstadoCarrera {
    
    public static final double COMBUSTIBLE_INICIAL = 100.0;
    public static final double COMBUSTIBLE_ALERTA = 20.0;
    public static final double COMBUSTIBLE_CRITICO = 8.0;
    public static final double DESGASTE_ALERTA = 80.0;
    public static final double DESGASTE_CRITICO = 92.0;
    public static final double TIEMPO_PIT_STOP = 22.0; // segundos

    private Piloto piloto;
    private Vehiculo vehiculo;
    private ConfiguracionVehiculo config;
    private double combustibleActual;
    private double desgasteNeumaticos; // 0% = nuevo, 100% = destruidos
    private int vueltaActual;
    private double tiempoTotal;
    private boolean enPits;
    private boolean retirado;
    private int numeroPitStops;
    private List<Double> tiemposVuelta;
    private String estadoTexto;
    private int posicionActual;

    public EstadoCarrera(Piloto piloto, Vehiculo vehiculo, ConfiguracionVehiculo config) {
        this.piloto = piloto;
        this.vehiculo = vehiculo;
        this.config = config;
        this.combustibleActual = COMBUSTIBLE_INICIAL;
        this.desgasteNeumaticos = 0.0;
        this.vueltaActual = 0;
        this.tiempoTotal = 0.0;
        this.enPits = false;
        this.retirado = false;
        this.numeroPitStops = 0;
        this.tiemposVuelta = new ArrayList<>();
        this.estadoTexto = "En pista";
        this.posicionActual = 0;
    }

    /**
     * Verifica si el piloto necesita un pit stop.
     * @return true si el combustible está crítico o los neumáticos muy desgastados
     */
    public boolean necesitaPitStop() {
        return combustibleActual <= COMBUSTIBLE_CRITICO || desgasteNeumaticos >= DESGASTE_CRITICO;
    }

    /**
     * Verifica si hay alerta de pit stop (no crítico pero cercano).
     */
    public boolean alertaPitStop() {
        return combustibleActual <= COMBUSTIBLE_ALERTA || desgasteNeumaticos >= DESGASTE_ALERTA;
    }

    /**
     * Realiza un pit stop: recarga combustible y cambia neumáticos.
     */
    public void realizarPitStop() {
        this.enPits = true;
        this.estadoTexto = "🔧 PIT STOP";
        this.combustibleActual = COMBUSTIBLE_INICIAL;
        this.desgasteNeumaticos = 0.0;
        this.tiempoTotal += TIEMPO_PIT_STOP;
        this.numeroPitStops++;
        this.enPits = false;
        this.estadoTexto = "En pista";
    }

    /**
     * Registra una vuelta con el tiempo dado.
     * @param tiempoVuelta tiempo en segundos
     */
    public void registrarVuelta(double tiempoVuelta) {
        this.vueltaActual++;
        this.tiempoTotal += tiempoVuelta;
        this.tiemposVuelta.add(tiempoVuelta);
    }

    /**
     * Consume combustible según los parámetros dados.
     * @param cantidad cantidad a consumir
     */
    public void consumirCombustible(double cantidad) {
        this.combustibleActual = Math.max(0, this.combustibleActual - cantidad);
    }

    /**
     * Incrementa el desgaste de neumáticos.
     * @param cantidad cantidad de desgaste a agregar
     */
    public void incrementarDesgaste(double cantidad) {
        this.desgasteNeumaticos = Math.min(100, this.desgasteNeumaticos + cantidad);
    }

    /**
     * Retira al piloto de la carrera.
     */
    public void retirar(String motivo) {
        this.retirado = true;
        this.estadoTexto = "❌ Retirado: " + motivo;
    }

    /**
     * Obtiene el mejor tiempo de vuelta.
     */
    public double getMejorTiempo() {
        return tiemposVuelta.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    /**
     * Obtiene el último tiempo de vuelta.
     */
    public double getUltimoTiempo() {
        if (tiemposVuelta.isEmpty()) return 0.0;
        return tiemposVuelta.get(tiemposVuelta.size() - 1);
    }

    /**
     * Formatea un tiempo en segundos a formato MM:SS.mmm
     */
    public static String formatearTiempo(double segundos) {
        if (segundos <= 0) return "--:--.---";
        int mins = (int) (segundos / 60);
        double secs = segundos % 60;
        return String.format("%d:%06.3f", mins, secs);
    }

    // Getters
    public Piloto getPiloto() { return piloto; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public ConfiguracionVehiculo getConfig() { return config; }
    public double getCombustibleActual() { return combustibleActual; }
    public double getDesgasteNeumaticos() { return desgasteNeumaticos; }
    public int getVueltaActual() { return vueltaActual; }
    public double getTiempoTotal() { return tiempoTotal; }
    public boolean isEnPits() { return enPits; }
    public boolean isRetirado() { return retirado; }
    public int getNumeroPitStops() { return numeroPitStops; }
    public List<Double> getTiemposVuelta() { return new ArrayList<>(tiemposVuelta); }
    public String getEstadoTexto() { return estadoTexto; }
    public int getPosicionActual() { return posicionActual; }

    // Setters
    public void setEnPits(boolean enPits) { this.enPits = enPits; }
    public void setEstadoTexto(String estadoTexto) { this.estadoTexto = estadoTexto; }
    public void setPosicionActual(int posicionActual) { this.posicionActual = posicionActual; }
    public void setConfig(ConfiguracionVehiculo config) { this.config = config; }

    @Override
    public String toString() {
        return String.format("%s | Vuelta: %d | Tiempo: %s | Combustible: %.1f%% | Desgaste: %.1f%%",
                piloto.getNombre(), vueltaActual, formatearTiempo(tiempoTotal),
                combustibleActual, desgasteNeumaticos);
    }
}
