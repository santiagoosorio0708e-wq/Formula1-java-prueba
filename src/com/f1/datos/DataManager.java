package com.f1.datos;

import com.f1.modelo.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton que gestiona todos los datos del sistema usando HashMap.
 * Proporciona operaciones CRUD para pilotos, equipos, vehículos y circuitos.
 */
public class DataManager {
    
    private static DataManager instance;

    private final Map<Integer, Piloto> pilotos;
    private final Map<String, Equipo> equipos;
    private final Map<String, Vehiculo> vehiculos;    // key = "equipo_modelo"
    private final Map<String, Circuito> circuitos;
    private final List<List<ResultadoClasificacion>> historialSesiones;
    private final Map<String, ConfiguracionVehiculo> configuracionesGuardadas; // key = "pilotoId_circuito"

    private int nextPilotoId;

    private DataManager() {
        this.pilotos = new HashMap<>();
        this.equipos = new HashMap<>();
        this.vehiculos = new HashMap<>();
        this.circuitos = new HashMap<>();
        this.historialSesiones = new ArrayList<>();
        this.configuracionesGuardadas = new HashMap<>();
        this.nextPilotoId = 1;
    }

    /**
     * Obtiene la instancia única del DataManager.
     */
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // ==================== PILOTOS ====================

    public void agregarPiloto(Piloto piloto) {
        if (piloto.getId() == 0) {
            piloto.setId(nextPilotoId++);
        } else {
            nextPilotoId = Math.max(nextPilotoId, piloto.getId() + 1);
        }
        pilotos.put(piloto.getId(), piloto);
    }

    public void editarPiloto(int id, Piloto pilotoActualizado) {
        if (pilotos.containsKey(id)) {
            pilotoActualizado.setId(id);
            pilotos.put(id, pilotoActualizado);
        }
    }

    public void eliminarPiloto(int id) {
        pilotos.remove(id);
    }

    public Piloto obtenerPiloto(int id) {
        return pilotos.get(id);
    }

    public List<Piloto> listarPilotos() {
        return new ArrayList<>(pilotos.values()).stream()
                .sorted(Comparator.comparingInt(Piloto::getId))
                .collect(Collectors.toList());
    }

    public List<Piloto> buscarPilotos(String termino) {
        String lower = termino.toLowerCase();
        return pilotos.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lower)
                        || p.getEquipo().toLowerCase().contains(lower)
                        || p.getRol().toLowerCase().contains(lower))
                .sorted(Comparator.comparingInt(Piloto::getId))
                .collect(Collectors.toList());
    }

    public int getCantidadPilotos() {
        return pilotos.size();
    }

    // ==================== EQUIPOS ====================

    public void agregarEquipo(Equipo equipo) {
        equipos.put(equipo.getNombre(), equipo);
    }

    public void editarEquipo(String nombre, Equipo equipoActualizado) {
        if (equipos.containsKey(nombre)) {
            equipos.remove(nombre);
            equipos.put(equipoActualizado.getNombre(), equipoActualizado);
        }
    }

    public void eliminarEquipo(String nombre) {
        equipos.remove(nombre);
    }

    public Equipo obtenerEquipo(String nombre) {
        return equipos.get(nombre);
    }

    public List<Equipo> listarEquipos() {
        return new ArrayList<>(equipos.values()).stream()
                .sorted(Comparator.comparing(Equipo::getNombre))
                .collect(Collectors.toList());
    }

    public List<Equipo> buscarEquipos(String termino) {
        String lower = termino.toLowerCase();
        return equipos.values().stream()
                .filter(e -> e.getNombre().toLowerCase().contains(lower)
                        || e.getPais().toLowerCase().contains(lower)
                        || e.getMotor().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(Equipo::getNombre))
                .collect(Collectors.toList());
    }

    public int getCantidadEquipos() {
        return equipos.size();
    }

    // ==================== VEHÍCULOS ====================

    private String vehiculoKey(Vehiculo v) {
        return v.getEquipo() + "_" + v.getModelo();
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.put(vehiculoKey(vehiculo), vehiculo);
    }

    public void editarVehiculo(String equipoOriginal, String modeloOriginal, Vehiculo vehiculoActualizado) {
        String keyOriginal = equipoOriginal + "_" + modeloOriginal;
        vehiculos.remove(keyOriginal);
        vehiculos.put(vehiculoKey(vehiculoActualizado), vehiculoActualizado);
    }

    public void eliminarVehiculo(String equipo, String modelo) {
        vehiculos.remove(equipo + "_" + modelo);
    }

    public Vehiculo obtenerVehiculo(String equipo, String modelo) {
        return vehiculos.get(equipo + "_" + modelo);
    }

    public Vehiculo obtenerVehiculoPorEquipo(String equipo) {
        return vehiculos.values().stream()
                .filter(v -> v.getEquipo().equals(equipo))
                .findFirst()
                .orElse(null);
    }

    public List<Vehiculo> listarVehiculos() {
        return new ArrayList<>(vehiculos.values()).stream()
                .sorted(Comparator.comparing(Vehiculo::getEquipo))
                .collect(Collectors.toList());
    }

    public List<Vehiculo> buscarVehiculos(String termino) {
        String lower = termino.toLowerCase();
        return vehiculos.values().stream()
                .filter(v -> v.getEquipo().toLowerCase().contains(lower)
                        || v.getModelo().toLowerCase().contains(lower)
                        || v.getMotor().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(Vehiculo::getEquipo))
                .collect(Collectors.toList());
    }

    public int getCantidadVehiculos() {
        return vehiculos.size();
    }

    // ==================== CIRCUITOS ====================

    public void agregarCircuito(Circuito circuito) {
        circuitos.put(circuito.getNombre(), circuito);
    }

    public void editarCircuito(String nombre, Circuito circuitoActualizado) {
        if (circuitos.containsKey(nombre)) {
            circuitos.remove(nombre);
            circuitos.put(circuitoActualizado.getNombre(), circuitoActualizado);
        }
    }

    public void eliminarCircuito(String nombre) {
        circuitos.remove(nombre);
    }

    public Circuito obtenerCircuito(String nombre) {
        return circuitos.get(nombre);
    }

    public List<Circuito> listarCircuitos() {
        return new ArrayList<>(circuitos.values()).stream()
                .sorted(Comparator.comparing(Circuito::getNombre))
                .collect(Collectors.toList());
    }

    public List<Circuito> buscarCircuitos(String termino) {
        String lower = termino.toLowerCase();
        return circuitos.values().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(lower)
                        || c.getPais().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(Circuito::getNombre))
                .collect(Collectors.toList());
    }

    public int getCantidadCircuitos() {
        return circuitos.size();
    }

    // ==================== HISTORIAL ====================

    public void guardarSesion(List<ResultadoClasificacion> resultados) {
        historialSesiones.add(new ArrayList<>(resultados));
    }

    public List<List<ResultadoClasificacion>> getHistorialSesiones() {
        return new ArrayList<>(historialSesiones);
    }

    public int getCantidadSesiones() {
        return historialSesiones.size();
    }

    // ==================== CONFIGURACIONES ====================

    public void guardarConfiguracion(int pilotoId, String circuito, ConfiguracionVehiculo config) {
        configuracionesGuardadas.put(pilotoId + "_" + circuito, config);
    }

    public ConfiguracionVehiculo obtenerConfiguracion(int pilotoId, String circuito) {
        return configuracionesGuardadas.get(pilotoId + "_" + circuito);
    }

    public List<String> listarConfiguracionesGuardadas() {
        return new ArrayList<>(configuracionesGuardadas.keySet());
    }

    // ==================== UTILIDADES ====================

    /**
     * Obtiene los pilotos de un equipo específico.
     */
    public List<Piloto> obtenerPilotosPorEquipo(String equipo) {
        return pilotos.values().stream()
                .filter(p -> p.getEquipo().equals(equipo))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los nombres de todos los equipos.
     */
    public List<String> obtenerNombresEquipos() {
        return equipos.keySet().stream().sorted().collect(Collectors.toList());
    }

    /**
     * Obtiene los nombres de todos los circuitos.
     */
    public List<String> obtenerNombresCircuitos() {
        return circuitos.keySet().stream().sorted().collect(Collectors.toList());
    }

    /**
     * Reinicia todos los datos (útil para testing).
     */
    public void reiniciar() {
        pilotos.clear();
        equipos.clear();
        vehiculos.clear();
        circuitos.clear();
        historialSesiones.clear();
        configuracionesGuardadas.clear();
        nextPilotoId = 1;
    }
}
