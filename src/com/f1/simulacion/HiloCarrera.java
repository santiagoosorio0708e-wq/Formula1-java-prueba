package com.f1.simulacion;

import com.f1.modelo.*;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Hilo individual para cada piloto en la carrera.
 * Cada instancia simula las vueltas de un piloto con cálculos de tiempo,
 * consumo de combustible y desgaste de neumáticos.
 */
public class HiloCarrera extends Thread {

    private final EstadoCarrera estado;
    private final Circuito circuito;
    private final int totalVueltas;
    private final CyclicBarrier barreraVuelta;
    private final MotorSimulacion motor;
    private final Random random;
    private volatile boolean pausado = false;
    private volatile boolean detenido = false;

    public HiloCarrera(EstadoCarrera estado, Circuito circuito, int totalVueltas,
                        CyclicBarrier barreraVuelta, MotorSimulacion motor) {
        this.estado = estado;
        this.circuito = circuito;
        this.totalVueltas = totalVueltas;
        this.barreraVuelta = barreraVuelta;
        this.motor = motor;
        this.random = new Random();
        setName("Hilo-" + estado.getPiloto().getNombre());
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            for (int vuelta = 1; vuelta <= totalVueltas && !detenido && !estado.isRetirado(); vuelta++) {
                // Esperar si está pausado
                while (pausado && !detenido) {
                    Thread.sleep(100);
                }
                if (detenido) break;

                // Obtener clima actual del motor (thread-safe)
                CondicionClimatica clima = motor.getClimaActual();

                // Verificar si necesita pit stop antes de la vuelta
                if (estado.necesitaPitStop()) {
                    estado.realizarPitStop();
                    motor.notificarPitStop(estado);
                    Thread.sleep(200); // Simular tiempo visual del pit stop
                }

                // Calcular tiempo de vuelta
                double tiempoVuelta = calcularTiempoVuelta(clima);

                // Registrar vuelta
                estado.registrarVuelta(tiempoVuelta);

                // Actualizar combustible
                double consumo = calcularConsumoCombustible(clima);
                estado.consumirCombustible(consumo);

                // Actualizar desgaste
                double desgaste = calcularDesgasteNeumaticos(clima);
                estado.incrementarDesgaste(desgaste);

                // Actualizar estado visual
                if (estado.alertaPitStop()) {
                    estado.setEstadoTexto("⚠️ Alerta PIT");
                } else {
                    estado.setEstadoTexto("En pista");
                }

                // Notificar vuelta completada
                motor.notificarVueltaCompletada(estado, vuelta);

                // Simular tiempo (escalado para visualización)
                Thread.sleep(150 + random.nextInt(100));

                // Sincronizar con otros hilos en la barrera
                try {
                    barreraVuelta.await();
                } catch (BrokenBarrierException e) {
                    if (!detenido) break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            motor.notificarHiloTerminado();
        }
    }

    /**
     * Calcula el tiempo de vuelta basado en múltiples factores.
     */
    private double calcularTiempoVuelta(CondicionClimatica clima) {
        Vehiculo vehiculo = estado.getVehiculo();
        Piloto piloto = estado.getPiloto();
        ConfiguracionVehiculo config = estado.getConfig();

        // Obtener modo de conducción
        String modoKey = config.getModoConduccion().getClave();
        ModoConduccion modo = vehiculo.getModoConduccion(modoKey);
        if (modo == null) modo = vehiculo.getModoConduccion("conduccion_normal");

        double velocidadPromedio = modo != null ? modo.getVelocidadPromedioKmh() : 300;

        // Tiempo base: distancia / velocidad * 3600 (convertir a segundos)
        double tiempoBase = (circuito.getLongitudKm() / velocidadPromedio) * 3600;

        // Factor del clima (más tiempo = más lento)
        double factorClima = clima.getFactorVelocidad();

        // Factor habilidad del piloto (mayor habilidad = más rápido)
        double factorHabilidad = 1.0 - (piloto.getHabilidad() * 0.002);

        // Factor desgaste de neumáticos (más desgaste = más lento)
        double factorDesgaste = 1.0 + (estado.getDesgasteNeumaticos() * 0.002);

        // Factor combustible (más combustible = más peso = más lento)
        double factorCombustible = 1.0 + (estado.getCombustibleActual() * 0.0005);

        // Factor de configuración
        double factorConfig = config.calcularFactorRendimiento();

        // Variación aleatoria (±0.5 segundos)
        double variacion = (random.nextDouble() - 0.5) * 1.0;

        double tiempoVuelta = tiempoBase * factorClima * factorHabilidad
                * factorDesgaste * factorCombustible * factorConfig + variacion;

        return Math.max(tiempoVuelta, tiempoBase * 0.85); // Mínimo realista
    }

    /**
     * Calcula el consumo de combustible por vuelta.
     */
    private double calcularConsumoCombustible(CondicionClimatica clima) {
        ConfiguracionVehiculo config = estado.getConfig();
        String modoKey = config.getModoConduccion().getClave();
        ModoConduccion modo = estado.getVehiculo().getModoConduccion(modoKey);

        double consumoBase = modo != null ? modo.getConsumo(clima.getClave()) : 2.0;
        double factorConsumo = config.calcularFactorConsumo() * clima.getFactorConsumo();

        return consumoBase * factorConsumo;
    }

    /**
     * Calcula el desgaste de neumáticos por vuelta.
     */
    private double calcularDesgasteNeumaticos(CondicionClimatica clima) {
        ConfiguracionVehiculo config = estado.getConfig();
        String modoKey = config.getModoConduccion().getClave();
        ModoConduccion modo = estado.getVehiculo().getModoConduccion(modoKey);

        double desgasteBase = modo != null ? modo.getDesgaste(clima.getClave()) : 1.5;
        double factorDesgaste = config.calcularFactorDesgaste() * clima.getFactorDesgaste();

        return desgasteBase * factorDesgaste;
    }

    // Control del hilo
    public void pausar() { this.pausado = true; }
    public void reanudar() { this.pausado = false; }
    public void detener() { this.detenido = true; this.pausado = false; }
    public boolean isPausado() { return pausado; }
    public EstadoCarrera getEstado() { return estado; }
}
