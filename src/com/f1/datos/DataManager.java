package com.f1.datos;

import com.f1.modelo.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;

/**
 * Singleton que gestiona todos los datos del sistema usando HashMap en memoria,
 * pero respaldado por SQLite para Pilotos y Resultados de Carrera.
 */
public class DataManager {
    
    private static DataManager instance;

    private final Map<Integer, Piloto> pilotos;
    private final Map<String, Equipo> equipos;
    private final Map<String, Vehiculo> vehiculos;
    private final Map<String, Circuito> circuitos;
    private final List<List<ResultadoClasificacion>> historialSesiones;
    private final Map<String, ConfiguracionVehiculo> configuracionesGuardadas;

    private int nextPilotoId = 1;
    private final Gson gson = new Gson();

    private DataManager() {
        this.pilotos = new HashMap<>();
        this.equipos = new HashMap<>();
        this.vehiculos = new HashMap<>();
        this.circuitos = new HashMap<>();
        this.historialSesiones = new ArrayList<>();
        this.configuracionesGuardadas = new HashMap<>();
        
        ConexionBD.inicializarBD();
        cargarPilotosDesdeBD();
        cargarHistorialDesdeBD();
        cargarEquiposDesdeBD();
        cargarVehiculosDesdeBD();
        cargarCircuitosDesdeBD();
    }

    private void cargarEntidadesEstaticas() {
        if (equipos.isEmpty()) {
            DataInitializer.cargarEquipos(this);
            DataInitializer.cargarVehiculos(this);
            DataInitializer.cargarCircuitos(this);
        }
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            
            if (instance.pilotos.isEmpty()) {
                DataInitializer.inicializar();
            } else if (instance.equipos.isEmpty()) {
                instance.cargarEntidadesEstaticas();
            }
        }
        return instance;
    }

    private void cargarPilotosDesdeBD() {
        String sql = "SELECT * FROM pilotos";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                Piloto p = new Piloto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("equipo"),
                    rs.getString("rol"),
                    rs.getInt("experiencia"),
                    rs.getDouble("habilidad")
                );
                pilotos.put(p.getId(), p);
                nextPilotoId = Math.max(nextPilotoId, p.getId() + 1);
            }
        } catch (SQLException e) {
            System.out.println("Error cargando pilotos: " + e.getMessage());
        }
    }

    private void cargarHistorialDesdeBD() {
        String sql = "SELECT * FROM resultados ORDER BY sesion_id ASC, posicion ASC";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            int currentSesionId = -1;
            List<ResultadoClasificacion> sesionActual = null;
            
            while (rs.next()) {
                int sesionId = rs.getInt("sesion_id");
                if (sesionId != currentSesionId) {
                    if (sesionActual != null) {
                        historialSesiones.add(sesionActual);
                    }
                    sesionActual = new ArrayList<>();
                    currentSesionId = sesionId;
                }
                
                Piloto p = pilotos.get(rs.getInt("piloto_id"));
                if (p != null) {
                    ResultadoClasificacion r = new ResultadoClasificacion(
                        rs.getInt("posicion"),
                        p,
                        rs.getDouble("tiempo_total"),
                        rs.getString("diferencia"),
                        rs.getInt("pit_stops"),
                        rs.getDouble("mejor_vuelta"),
                        rs.getString("estado_final")
                    );
                    sesionActual.add(r);
                }
            }
            if (sesionActual != null) {
                historialSesiones.add(sesionActual);
            }
        } catch (SQLException e) {
            System.out.println("Error cargando historial: " + e.getMessage());
        }
    }

    // ==================== PILOTOS ====================

    public void agregarPiloto(Piloto piloto) {
        if (piloto.getId() == 0) {
            piloto.setId(nextPilotoId++);
        } else {
            nextPilotoId = Math.max(nextPilotoId, piloto.getId() + 1);
        }
        
        // Persistir en SQL
        String sql = "INSERT INTO pilotos(id, nombre, equipo, rol, experiencia, habilidad) VALUES(?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT(id) DO UPDATE SET nombre=excluded.nombre, equipo=excluded.equipo, rol=excluded.rol, " +
                     "experiencia=excluded.experiencia, habilidad=excluded.habilidad";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, piloto.getId());
            pstmt.setString(2, piloto.getNombre());
            pstmt.setString(3, piloto.getEquipo());
            pstmt.setString(4, piloto.getRol());
            pstmt.setInt(5, piloto.getExperiencia());
            pstmt.setDouble(6, piloto.getHabilidad());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertando piloto: " + e.getMessage());
        }

        pilotos.put(piloto.getId(), piloto);
    }

    public void editarPiloto(int id, Piloto pilotoActualizado) {
        if (pilotos.containsKey(id)) {
            pilotoActualizado.setId(id);
            
            String sql = "UPDATE pilotos SET nombre=?, equipo=?, rol=?, experiencia=?, habilidad=? WHERE id=?";
            try (Connection conn = ConexionBD.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, pilotoActualizado.getNombre());
                pstmt.setString(2, pilotoActualizado.getEquipo());
                pstmt.setString(3, pilotoActualizado.getRol());
                pstmt.setInt(4, pilotoActualizado.getExperiencia());
                pstmt.setDouble(5, pilotoActualizado.getHabilidad());
                pstmt.setInt(6, id);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error actualizando piloto: " + e.getMessage());
            }

            pilotos.put(id, pilotoActualizado);
        }
    }

    public void eliminarPiloto(int id) {
        String sql = "DELETE FROM pilotos WHERE id=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminando piloto: " + e.getMessage());
        }
        
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

    // ==================== HISTORIAL ====================

    public void guardarSesion(List<ResultadoClasificacion> resultados) {
        String sqlSesion = "INSERT INTO sesiones(fecha) VALUES(CURRENT_TIMESTAMP)";
        String sqlResultado = "INSERT INTO resultados(sesion_id, posicion, piloto_id, tiempo_total, diferencia, pit_stops, mejor_vuelta, estado_final) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmtSesion = conn.prepareStatement(sqlSesion, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmtSesion.executeUpdate();
            ResultSet rsKeys = pstmtSesion.getGeneratedKeys();
            if (rsKeys.next()) {
                int sesionId = rsKeys.getInt(1);
                
                try (PreparedStatement pstmtRes = conn.prepareStatement(sqlResultado)) {
                    for (ResultadoClasificacion r : resultados) {
                        pstmtRes.setInt(1, sesionId);
                        pstmtRes.setInt(2, r.getPosicion());
                        pstmtRes.setInt(3, r.getPiloto().getId());
                        pstmtRes.setDouble(4, r.getTiempoTotal());
                        pstmtRes.setString(5, r.getDiferencia());
                        pstmtRes.setInt(6, r.getPitStops());
                        pstmtRes.setDouble(7, r.getMejorVuelta());
                        pstmtRes.setString(8, r.getEstadoFinal());
                        pstmtRes.addBatch();
                    }
                    pstmtRes.executeBatch();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error guardando sesión: " + e.getMessage());
        }
        
        historialSesiones.add(new ArrayList<>(resultados));
    }

    public List<List<ResultadoClasificacion>> getHistorialSesiones() {
        return new ArrayList<>(historialSesiones);
    }

    public int getCantidadSesiones() {
        return historialSesiones.size();
    }

    private void cargarEquiposDesdeBD() {
        String sql = "SELECT * FROM equipos";
        try (Connection conn = ConexionBD.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Equipo e = gson.fromJson(rs.getString("data_json"), Equipo.class);
                equipos.put(e.getNombre(), e);
            }
        } catch (SQLException e) { System.out.println("Error cargando equipos: " + e.getMessage()); }
    }

    private void cargarVehiculosDesdeBD() {
        String sql = "SELECT * FROM vehiculos";
        try (Connection conn = ConexionBD.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vehiculo v = gson.fromJson(rs.getString("data_json"), Vehiculo.class);
                vehiculos.put(vehiculoKey(v), v);
            }
        } catch (SQLException e) { System.out.println("Error cargando vehiculos: " + e.getMessage()); }
    }

    private void cargarCircuitosDesdeBD() {
        String sql = "SELECT * FROM circuitos";
        try (Connection conn = ConexionBD.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Circuito c = gson.fromJson(rs.getString("data_json"), Circuito.class);
                circuitos.put(c.getNombre(), c);
            }
        } catch (SQLException e) { System.out.println("Error cargando circuitos: " + e.getMessage()); }
    }

    private void guardarEntidadJSON(String tabla, String pkColumn, String pkValue, Object entidad) {
        String json = gson.toJson(entidad);
        String sql = "INSERT INTO " + tabla + "(" + pkColumn + ", data_json) VALUES(?, ?) " +
                     "ON CONFLICT(" + pkColumn + ") DO UPDATE SET data_json=excluded.data_json";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pkValue);
            pstmt.setString(2, json);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Error guardando " + tabla + ": " + e.getMessage()); }
    }

    private void eliminarEntidadJSON(String tabla, String pkColumn, String pkValue) {
        String sql = "DELETE FROM " + tabla + " WHERE " + pkColumn + "=?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pkValue);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Error eliminando " + tabla + ": " + e.getMessage()); }
    }

    // ==================== EQUIPOS ====================
    public void agregarEquipo(Equipo equipo) { 
        equipos.put(equipo.getNombre(), equipo); 
        guardarEntidadJSON("equipos", "nombre", equipo.getNombre(), equipo);
    }
    public void editarEquipo(String nombre, Equipo e) { 
        if(equipos.containsKey(nombre)) { 
            equipos.remove(nombre); 
            if (!nombre.equals(e.getNombre())) eliminarEntidadJSON("equipos", "nombre", nombre);
            equipos.put(e.getNombre(), e); 
            guardarEntidadJSON("equipos", "nombre", e.getNombre(), e);
        } 
    }
    public void eliminarEquipo(String nombre) { 
        equipos.remove(nombre); 
        eliminarEntidadJSON("equipos", "nombre", nombre);
    }
    public Equipo obtenerEquipo(String nombre) { return equipos.get(nombre); }
    public List<Equipo> listarEquipos() { return new ArrayList<>(equipos.values()).stream().sorted(Comparator.comparing(Equipo::getNombre)).collect(Collectors.toList()); }
    public List<Equipo> buscarEquipos(String termino) { String l = termino.toLowerCase(); return equipos.values().stream().filter(e -> e.getNombre().toLowerCase().contains(l) || e.getPais().toLowerCase().contains(l)).sorted(Comparator.comparing(Equipo::getNombre)).collect(Collectors.toList()); }
    public int getCantidadEquipos() { return equipos.size(); }
    public List<String> obtenerNombresEquipos() { return equipos.keySet().stream().sorted().collect(Collectors.toList()); }

    // ==================== VEHÍCULOS ====================
    private String vehiculoKey(Vehiculo v) { return v.getEquipo() + "_" + v.getModelo(); }
    public void agregarVehiculo(Vehiculo vehiculo) { 
        String key = vehiculoKey(vehiculo);
        vehiculos.put(key, vehiculo); 
        guardarEntidadJSON("vehiculos", "id", key, vehiculo);
    }
    public void editarVehiculo(String eOriginal, String mOriginal, Vehiculo vAct) { 
        String keyOld = eOriginal + "_" + mOriginal;
        vehiculos.remove(keyOld); 
        String keyNew = vehiculoKey(vAct);
        if (!keyOld.equals(keyNew)) eliminarEntidadJSON("vehiculos", "id", keyOld);
        vehiculos.put(keyNew, vAct); 
        guardarEntidadJSON("vehiculos", "id", keyNew, vAct);
    }
    public void eliminarVehiculo(String equipo, String modelo) { 
        String key = equipo + "_" + modelo;
        vehiculos.remove(key); 
        eliminarEntidadJSON("vehiculos", "id", key);
    }
    public Vehiculo obtenerVehiculo(String equipo, String modelo) { return vehiculos.get(equipo + "_" + modelo); }
    public Vehiculo obtenerVehiculoPorEquipo(String equipo) { return vehiculos.values().stream().filter(v -> v.getEquipo().equals(equipo)).findFirst().orElse(null); }
    public List<Vehiculo> listarVehiculos() { return new ArrayList<>(vehiculos.values()).stream().sorted(Comparator.comparing(Vehiculo::getEquipo)).collect(Collectors.toList()); }
    public List<Vehiculo> buscarVehiculos(String termino) { String l = termino.toLowerCase(); return vehiculos.values().stream().filter(v -> v.getEquipo().toLowerCase().contains(l) || v.getModelo().toLowerCase().contains(l)).sorted(Comparator.comparing(Vehiculo::getEquipo)).collect(Collectors.toList()); }
    public int getCantidadVehiculos() { return vehiculos.size(); }

    // ==================== CIRCUITOS ====================
    public void agregarCircuito(Circuito circuito) { 
        circuitos.put(circuito.getNombre(), circuito); 
        guardarEntidadJSON("circuitos", "nombre", circuito.getNombre(), circuito);
    }
    public void editarCircuito(String nombre, Circuito cAct) { 
        if(circuitos.containsKey(nombre)) { 
            circuitos.remove(nombre); 
            if (!nombre.equals(cAct.getNombre())) eliminarEntidadJSON("circuitos", "nombre", nombre);
            circuitos.put(cAct.getNombre(), cAct); 
            guardarEntidadJSON("circuitos", "nombre", cAct.getNombre(), cAct);
        } 
    }
    public void eliminarCircuito(String nombre) { 
        circuitos.remove(nombre); 
        eliminarEntidadJSON("circuitos", "nombre", nombre);
    }
    public Circuito obtenerCircuito(String nombre) { return circuitos.get(nombre); }
    public List<Circuito> listarCircuitos() { return new ArrayList<>(circuitos.values()).stream().sorted(Comparator.comparing(Circuito::getNombre)).collect(Collectors.toList()); }
    public List<Circuito> buscarCircuitos(String termino) { String l = termino.toLowerCase(); return circuitos.values().stream().filter(c -> c.getNombre().toLowerCase().contains(l) || c.getPais().toLowerCase().contains(l)).sorted(Comparator.comparing(Circuito::getNombre)).collect(Collectors.toList()); }
    public int getCantidadCircuitos() { return circuitos.size(); }
    public List<String> obtenerNombresCircuitos() { return circuitos.keySet().stream().sorted().collect(Collectors.toList()); }

    // ==================== CONFIGURACIONES ====================
    public void guardarConfiguracion(int pilotoId, String circuito, ConfiguracionVehiculo config) { configuracionesGuardadas.put(pilotoId + "_" + circuito, config); }
    public ConfiguracionVehiculo obtenerConfiguracion(int pilotoId, String circuito) { return configuracionesGuardadas.get(pilotoId + "_" + circuito); }
    public List<String> listarConfiguracionesGuardadas() { return new ArrayList<>(configuracionesGuardadas.keySet()); }

    // ==================== UTILIDADES ====================
    public List<Piloto> obtenerPilotosPorEquipo(String equipo) {
        return pilotos.values().stream().filter(p -> p.getEquipo().equals(equipo)).collect(Collectors.toList());
    }

    public void reiniciar() {
        pilotos.clear(); equipos.clear(); vehiculos.clear(); circuitos.clear();
        historialSesiones.clear(); configuracionesGuardadas.clear(); nextPilotoId = 1;
    }
}
