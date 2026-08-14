package com.f1.gui.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Tipografías del proyecto Formula 1.
 * Usa fuentes del sistema con fallback a SansSerif.
 */
public final class F1Fonts {

    private F1Fonts() {}

    private static final String FONT_FAMILY;

    static {
        // Intentar usar fuentes modernas disponibles
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        String selected = "SansSerif";
        String[] preferred = {"Segoe UI", "Inter", "Roboto", "Arial"};
        for (String pref : preferred) {
            for (String available : fonts) {
                if (available.equals(pref)) {
                    selected = pref;
                    break;
                }
            }
            if (!selected.equals("SansSerif")) break;
        }
        FONT_FAMILY = selected;
    }

    // ==================== TÍTULOS ====================
    public static final Font TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 32);
    public static final Font TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font SECTION_TITLE = new Font(FONT_FAMILY, Font.BOLD, 16);

    // ==================== CUERPO ====================
    public static final Font BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font BODY_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font BODY_SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 12);

    // ==================== ESPECIALES ====================
    public static final Font BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font TABLE_HEADER = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font TABLE_CELL = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font INPUT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font CAPTION = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font MONOSPACE = new Font("Consolas", Font.PLAIN, 14);
    public static final Font TIMER = new Font("Consolas", Font.BOLD, 28);
    public static final Font PODIO_NAME = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font PODIO_POSITION = new Font(FONT_FAMILY, Font.BOLD, 48);
    public static final Font SIDEBAR_ITEM = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font SIDEBAR_ITEM_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);

    public static String getFontFamily() {
        return FONT_FAMILY;
    }
}
