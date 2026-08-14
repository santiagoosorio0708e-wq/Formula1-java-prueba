package com.f1.simulacion;

import com.f1.modelo.CondicionClimatica;
import com.f1.modelo.EstadoCarrera;
import com.f1.modelo.ResultadoClasificacion;

import java.util.List;

/**
 * Interface de eventos para la simulación.
 * Permite que la GUI se actualice en respuesta a eventos de la carrera.
 */
public interface SimulacionListener {
    
    /**
     * Notifica que un piloto completó una vuelta.
     */
    void onVueltaCompletada(EstadoCarrera estado, int vuelta);
    
    /**
     * Notifica que un piloto necesita o está realizando pit stop.
     */
    void onPitStop(EstadoCarrera estado);
    
    /**
     * Notifica que la carrera finalizó con los resultados.
     */
    void onCarreraFinalizada(List<ResultadoClasificacion> resultados);
    
    /**
     * Notifica un cambio de clima durante la carrera.
     */
    void onCambioClima(CondicionClimatica nuevoClima);
    
    /**
     * Notifica que un piloto se retiró.
     */
    void onRetiro(EstadoCarrera estado, String motivo);
    
    /**
     * Notifica actualización general de posiciones.
     */
    void onActualizacionPosiciones(List<EstadoCarrera> estados);
}
