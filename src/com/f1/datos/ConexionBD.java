package com.f1.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestiona la conexión a la base de datos SQLite y la creación de tablas.
 */
public class ConexionBD {
    private static final String URL = "jdbc:sqlite:f1_data.db";

    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error de conexión SQL: " + e.getMessage());
        }
        return conn;
    }

    public static void inicializarBD() {
        String sqlPilotos = "CREATE TABLE IF NOT EXISTS pilotos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "equipo TEXT, " +
                "rol TEXT, " +
                "experiencia INTEGER, " +
                "habilidad REAL)";

        String sqlSesiones = "CREATE TABLE IF NOT EXISTS sesiones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String sqlResultados = "CREATE TABLE IF NOT EXISTS resultados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sesion_id INTEGER, " +
                "posicion INTEGER, " +
                "piloto_id INTEGER, " +
                "tiempo_total REAL, " +
                "diferencia TEXT, " +
                "pit_stops INTEGER, " +
                "mejor_vuelta REAL, " +
                "estado_final TEXT, " +
                "FOREIGN KEY(sesion_id) REFERENCES sesiones(id))";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sqlPilotos);
                stmt.execute(sqlSesiones);
                stmt.execute(sqlResultados);
            }
        } catch (SQLException e) {
            System.out.println("Error creando tablas: " + e.getMessage());
        }
    }
}
