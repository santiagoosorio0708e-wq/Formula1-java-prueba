package com.f1.simulacion;

import com.f1.modelo.CondicionClimatica;

import java.util.Random;

/**
 * Genera condiciones climáticas aleatorias y gestiona cambios durante la carrera.
 */
public class GeneradorClima {

    private CondicionClimatica climaActual;
    private final Random random;
    private static final double PROBABILIDAD_CAMBIO = 0.15; // 15% por vuelta de cambiar

    public GeneradorClima() {
        this.random = new Random();
        this.climaActual = generarClimaInicial();
    }

    /**
     * Genera un clima inicial aleatorio con probabilidades ponderadas.
     */
    private CondicionClimatica generarClimaInicial() {
        double prob = random.nextDouble();
        if (prob < 0.45) return CondicionClimatica.SECO;        // 45%
        if (prob < 0.70) return CondicionClimatica.NUBLADO;     // 25%
        if (prob < 0.90) return CondicionClimatica.LLUVIOSO;    // 20%
        return CondicionClimatica.EXTREMO;                       // 10%
    }

    /**
     * Intenta cambiar el clima. Solo permite transiciones realistas.
     * @return el nuevo clima si cambió, null si no cambió
     */
    public CondicionClimatica intentarCambioClima() {
        if (random.nextDouble() > PROBABILIDAD_CAMBIO) {
            return null; // Sin cambio
        }

        CondicionClimatica nuevoClima = obtenerTransicionRealista();
        if (nuevoClima != climaActual) {
            climaActual = nuevoClima;
            return nuevoClima;
        }
        return null;
    }

    /**
     * Genera una transición realista de clima.
     * SECO <-> NUBLADO <-> LLUVIOSO <-> EXTREMO
     */
    private CondicionClimatica obtenerTransicionRealista() {
        double prob = random.nextDouble();
        return switch (climaActual) {
            case SECO -> prob < 0.7 ? CondicionClimatica.NUBLADO : CondicionClimatica.SECO;
            case NUBLADO -> {
                if (prob < 0.4) yield CondicionClimatica.SECO;
                else if (prob < 0.8) yield CondicionClimatica.LLUVIOSO;
                else yield CondicionClimatica.NUBLADO;
            }
            case LLUVIOSO -> {
                if (prob < 0.3) yield CondicionClimatica.NUBLADO;
                else if (prob < 0.6) yield CondicionClimatica.EXTREMO;
                else yield CondicionClimatica.LLUVIOSO;
            }
            case EXTREMO -> prob < 0.6 ? CondicionClimatica.LLUVIOSO : CondicionClimatica.EXTREMO;
        };
    }

    public CondicionClimatica getClimaActual() {
        return climaActual;
    }

    public void setClimaActual(CondicionClimatica clima) {
        this.climaActual = clima;
    }
}
