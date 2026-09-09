package com.f1.infrastructure.adapter.in.gui.util;

import java.awt.Color;

/**
 * Paleta de colores oficial del proyecto Formula 1.
 * DiseÃ±o oscuro premium inspirado en la estÃ©tica F1.
 */
public final class F1Colors {

    private F1Colors() {} // Evitar instanciaciÃ³n

    // ==================== COLORES PRINCIPALES ====================
    public static final Color F1_RED = new Color(225, 6, 0);           // #E10600 - Rojo F1
    public static final Color F1_RED_DARK = new Color(180, 5, 0);      // Rojo oscuro
    public static final Color F1_RED_HOVER = new Color(255, 30, 20);   // Rojo hover

    // ==================== FONDOS ====================
    public static final Color BG_DARK = new Color(21, 21, 30);         // #15151E - Fondo principal
    public static final Color BG_CARD = new Color(30, 30, 46);         // #1E1E2E - Fondo cards
    public static final Color BG_SIDEBAR = new Color(17, 17, 27);      // #11111B - Sidebar
    public static final Color BG_INPUT = new Color(40, 40, 60);        // Input fields
    public static final Color BG_HOVER = new Color(45, 45, 65);        // Hover state
    public static final Color BG_TABLE_ALT = new Color(25, 25, 40);    // Fila alterna tabla
    public static final Color BG_HEADER = new Color(12, 12, 20);       // Header oscuro

    // ==================== TEXTO ====================
    public static final Color TEXT_PRIMARY = new Color(230, 230, 240);  // Texto principal
    public static final Color TEXT_SECONDARY = new Color(160, 160, 180);// Texto secundario
    public static final Color TEXT_MUTED = new Color(100, 100, 120);    // Texto desvanecido
    public static final Color TEXT_WHITE = new Color(255, 255, 255);    // Blanco puro

    // ==================== ACENTOS ====================
    public static final Color ACCENT_BLUE = new Color(56, 145, 255);   // Azul acento
    public static final Color ACCENT_GREEN = new Color(40, 200, 120);  // Verde Ã©xito
    public static final Color ACCENT_YELLOW = new Color(255, 193, 7);  // Amarillo alerta
    public static final Color ACCENT_ORANGE = new Color(255, 140, 0);  // Naranja
    public static final Color ACCENT_PURPLE = new Color(138, 43, 226); // PÃºrpura

    // ==================== PODIO ====================
    public static final Color PODIO_GOLD = new Color(255, 215, 0);     // #FFD700 - Oro P1
    public static final Color PODIO_SILVER = new Color(192, 192, 192); // #C0C0C0 - Plata P2
    public static final Color PODIO_BRONZE = new Color(205, 127, 50);  // #CD7F32 - Bronce P3

    // ==================== ESTADO ====================
    public static final Color STATUS_OK = new Color(40, 200, 120);     // OK / Buen estado
    public static final Color STATUS_WARNING = new Color(255, 193, 7); // Advertencia
    public static final Color STATUS_DANGER = new Color(225, 6, 0);    // Peligro / CrÃ­tico
    public static final Color STATUS_NEUTRAL = new Color(100, 100, 120);// Neutral

    // ==================== CLIMA ====================
    public static final Color CLIMA_SECO = new Color(255, 220, 100);   // Amarillo cÃ¡lido
    public static final Color CLIMA_NUBLADO = new Color(130, 130, 150);// Gris
    public static final Color CLIMA_LLUVIOSO = new Color(50, 100, 180);// Azul oscuro
    public static final Color CLIMA_EXTREMO = new Color(150, 30, 80);  // Rojo/PÃºrpura

    // ==================== EQUIPOS (colores distintivos) ====================
    public static final Color TEAM_RED_BULL = new Color(30, 65, 155);
    public static final Color TEAM_MERCEDES = new Color(0, 210, 190);
    public static final Color TEAM_FERRARI = new Color(220, 0, 0);
    public static final Color TEAM_MCLAREN = new Color(255, 135, 0);
    public static final Color TEAM_ASTON_MARTIN = new Color(0, 111, 98);
    public static final Color TEAM_ALPINE = new Color(0, 144, 255);
    public static final Color TEAM_ALFA_ROMEO = new Color(144, 0, 0);
    public static final Color TEAM_HAAS = new Color(180, 180, 180);
    public static final Color TEAM_ALPHATAURI = new Color(43, 69, 98);
    public static final Color TEAM_WILLIAMS = new Color(0, 90, 255);

    // ==================== BORDES ====================
    public static final Color BORDER = new Color(50, 50, 70);
    public static final Color BORDER_LIGHT = new Color(70, 70, 90);
    public static final Color BORDER_FOCUS = F1_RED;

    /**
     * Obtiene el color del equipo por nombre.
     */
    public static Color getTeamColor(String equipo) {
        return switch (equipo) {
            case "Red Bull Racing" -> TEAM_RED_BULL;
            case "Mercedes-AMG Petronas" -> TEAM_MERCEDES;
            case "Ferrari" -> TEAM_FERRARI;
            case "McLaren" -> TEAM_MCLAREN;
            case "Aston Martin" -> TEAM_ASTON_MARTIN;
            case "Alpine" -> TEAM_ALPINE;
            case "Alfa Romeo" -> TEAM_ALFA_ROMEO;
            case "Haas" -> TEAM_HAAS;
            case "AlphaTauri" -> TEAM_ALPHATAURI;
            case "Williams" -> TEAM_WILLIAMS;
            default -> TEXT_SECONDARY;
        };
    }

    /**
     * Obtiene el color del clima.
     */
    public static Color getClimaColor(String clima) {
        return switch (clima.toUpperCase()) {
            case "SECO" -> CLIMA_SECO;
            case "NUBLADO" -> CLIMA_NUBLADO;
            case "LLUVIOSO" -> CLIMA_LLUVIOSO;
            case "EXTREMO" -> CLIMA_EXTREMO;
            default -> TEXT_SECONDARY;
        };
    }

    /**
     * Obtiene un color con transparencia.
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
