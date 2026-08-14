package com.f1.simulacion;

import com.f1.datos.DataManager;
import com.f1.modelo.*;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Motor principal de simulación que gestiona todos los hilos de carrera.
 * Usa CyclicBarrier para sincronizar vueltas y CountDownLatch para finalización.
 */
public class MotorSimulacion {

    private final List<HiloCarrera> hilos;
    private final List<EstadoCarrera> estados;
    private final Circuito circuito;
    private final int totalVueltas;
    private final GeneradorClima generadorClima;
    private final SimulacionListener listener;
    private CyclicBarrier barreraVuelta;
    private CountDownLatch latchFin;
    private final AtomicInteger vueltaGlobal;
    private volatile boolean enCurso;
    private volatile boolean pausado;

    public MotorSimulacion(Circuito circuito, ConfiguracionVehiculo configUsuario,
                            SimulacionListener listener) {
        this.circuito = circuito;
        this.totalVueltas = Math.min(circuito.getVueltas(), 15); // Limitar para demo
        this.listener = listener;
        this.generadorClima = new GeneradorClima();
        this.vueltaGlobal = new AtomicInteger(0);
        this.hilos = new ArrayList<>();
        this.estados = new ArrayList<>();
        this.enCurso = false;
        this.pausado = false;

        inicializarPilotos(configUsuario);
    }

    /**
     * Inicializa los estados de carrera para los 20 pilotos.
     */
    private void inicializarPilotos(ConfiguracionVehiculo configUsuario) {
        DataManager dm = DataManager.getInstance();
        List<Piloto> pilotos = dm.listarPilotos();
        Random random = new Random();

        for (Piloto piloto : pilotos) {
            Vehiculo vehiculo = dm.obtenerVehiculoPorEquipo(piloto.getEquipo());
            if (vehiculo == null) continue;

            // El usuario configura su vehículo; los demás tienen config aleatoria
            ConfiguracionVehiculo config;
            if (piloto.getId() == 1) { // Primer piloto usa config del usuario
                config = configUsuario;
            } else {
                config = generarConfigAleatoria(random);
            }

            EstadoCarrera estado = new EstadoCarrera(piloto, vehiculo, config);
            estados.add(estado);
        }
    }

    /**
     * Genera una configuración aleatoria para pilotos de la IA.
     */
    private ConfiguracionVehiculo generarConfigAleatoria(Random random) {
        ConfiguracionVehiculo.ModoCond[] modos = ConfiguracionVehiculo.ModoCond.values();
        ConfiguracionVehiculo.CargaAero[] aeros = ConfiguracionVehiculo.CargaAero.values();
        ConfiguracionVehiculo.PresionNeum[] presiones = ConfiguracionVehiculo.PresionNeum.values();
        ConfiguracionVehiculo.EstrategiaComb[] estrategias = ConfiguracionVehiculo.EstrategiaComb.values();

        return new ConfiguracionVehiculo(
                modos[random.nextInt(modos.length)],
                aeros[random.nextInt(aeros.length)],
                presiones[random.nextInt(presiones.length)],
                estrategias[random.nextInt(estrategias.length)]
        );
    }

    /**
     * Inicia la carrera creando y arrancando todos los hilos.
     */
    public void iniciarCarrera() {
        if (enCurso) return;
        enCurso = true;
        pausado = false;

        int numPilotos = estados.size();
        latchFin = new CountDownLatch(numPilotos);

        // CyclicBarrier: todos los hilos esperan al final de cada vuelta
        barreraVuelta = new CyclicBarrier(numPilotos, () -> {
            // Acción ejecutada cuando todos llegan a la barrera
            int vuelta = vueltaGlobal.incrementAndGet();

            // Actualizar posiciones
            actualizarPosiciones();

            // Intentar cambio de clima cada 3 vueltas
            if (vuelta % 3 == 0) {
                CondicionClimatica nuevoClima = generadorClima.intentarCambioClima();
                if (nuevoClima != null && listener != null) {
                    SwingUtilities.invokeLater(() -> listener.onCambioClima(nuevoClima));
                }
            }

            // Notificar actualización de posiciones
            if (listener != null) {
                List<EstadoCarrera> copia = new ArrayList<>(estados);
                SwingUtilities.invokeLater(() -> listener.onActualizacionPosiciones(copia));
            }

            // Verificar si la carrera terminó
            if (vuelta >= totalVueltas) {
                finalizarCarrera();
            }
        });

        // Crear y arrancar hilos
        hilos.clear();
        for (EstadoCarrera estado : estados) {
            HiloCarrera hilo = new HiloCarrera(estado, circuito, totalVueltas, barreraVuelta, this);
            hilos.add(hilo);
        }

        // Notificar clima inicial
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onCambioClima(generadorClima.getClimaActual()));
        }

        // Arrancar todos los hilos
        for (HiloCarrera hilo : hilos) {
            hilo.start();
        }
    }

    /**
     * Pausa/reanuda la carrera.
     */
    public void togglePausa() {
        pausado = !pausado;
        for (HiloCarrera hilo : hilos) {
            if (pausado) hilo.pausar();
            else hilo.reanudar();
        }
    }

    /**
     * Detiene la carrera por completo.
     */
    public void detenerCarrera() {
        enCurso = false;
        for (HiloCarrera hilo : hilos) {
            hilo.detener();
        }
        // Romper la barrera
        barreraVuelta.reset();
    }

    /**
     * Actualiza las posiciones basado en tiempo total.
     */
    private void actualizarPosiciones() {
        List<EstadoCarrera> ordenados = estados.stream()
                .filter(e -> !e.isRetirado())
                .sorted(Comparator.comparingDouble(EstadoCarrera::getTiempoTotal))
                .collect(Collectors.toList());

        for (int i = 0; i < ordenados.size(); i++) {
            ordenados.get(i).setPosicionActual(i + 1);
        }
        // Retirados al final
        estados.stream().filter(EstadoCarrera::isRetirado)
                .forEach(e -> e.setPosicionActual(estados.size()));
    }

    /**
     * Finaliza la carrera y genera resultados.
     */
    private void finalizarCarrera() {
        enCurso = false;
        actualizarPosiciones();

        List<EstadoCarrera> ordenados = estados.stream()
                .sorted(Comparator.comparingDouble(EstadoCarrera::getTiempoTotal))
                .collect(Collectors.toList());

        List<ResultadoClasificacion> resultados = new ArrayList<>();
        double tiempoLider = ordenados.isEmpty() ? 0 : ordenados.get(0).getTiempoTotal();

        for (int i = 0; i < ordenados.size(); i++) {
            EstadoCarrera est = ordenados.get(i);
            String diferencia = i == 0 ? "LÍDER"
                    : "+" + EstadoCarrera.formatearTiempo(est.getTiempoTotal() - tiempoLider);

            resultados.add(new ResultadoClasificacion(
                    i + 1, est.getPiloto(), est.getTiempoTotal(),
                    diferencia, est.getNumeroPitStops(), est.getMejorTiempo(),
                    est.isRetirado() ? est.getEstadoTexto() : "Finalizado"));
        }

        // Guardar en historial
        DataManager.getInstance().guardarSesion(resultados);

        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onCarreraFinalizada(resultados));
        }
    }

    // Métodos para notificaciones desde los hilos
    void notificarVueltaCompletada(EstadoCarrera estado, int vuelta) {
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onVueltaCompletada(estado, vuelta));
        }
    }

    void notificarPitStop(EstadoCarrera estado) {
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onPitStop(estado));
        }
    }

    void notificarHiloTerminado() {
        latchFin.countDown();
    }

    // Getters thread-safe
    public synchronized CondicionClimatica getClimaActual() {
        return generadorClima.getClimaActual();
    }

    public boolean isEnCurso() { return enCurso; }
    public boolean isPausado() { return pausado; }
    public int getTotalVueltas() { return totalVueltas; }
    public int getVueltaActual() { return vueltaGlobal.get(); }
    public List<EstadoCarrera> getEstados() { return new ArrayList<>(estados); }
    public Circuito getCircuito() { return circuito; }
}
