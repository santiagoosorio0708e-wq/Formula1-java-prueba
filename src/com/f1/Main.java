package com.f1;

import com.f1.datos.DataInitializer;
import com.f1.gui.MainFrame;
import com.f1.gui.util.F1Colors;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada de la aplicación Formula 1 Simulation.
 * Inicializa los datos y lanza la interfaz gráfica.
 */
public class Main {

    public static void main(String[] args) {
        // Configurar Look and Feel oscuro
        configurarUI();

        // Cargar datos iniciales
        DataInitializer.inicializar();

        // Lanzar GUI en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Configura el UIManager para el tema oscuro.
     */
    private static void configurarUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Usar L&F por defecto
        }

        // Configurar colores globales
        UIManager.put("Panel.background", F1Colors.BG_DARK);
        UIManager.put("OptionPane.background", F1Colors.BG_CARD);
        UIManager.put("OptionPane.messageForeground", F1Colors.TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.background", F1Colors.BG_INPUT);
        UIManager.put("Button.foreground", F1Colors.TEXT_PRIMARY);
        UIManager.put("TextField.background", F1Colors.BG_INPUT);
        UIManager.put("TextField.foreground", F1Colors.TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", F1Colors.TEXT_PRIMARY);
        UIManager.put("ComboBox.background", F1Colors.BG_INPUT);
        UIManager.put("ComboBox.foreground", F1Colors.TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", F1Colors.F1_RED);
        UIManager.put("ComboBox.selectionForeground", F1Colors.TEXT_WHITE);
        UIManager.put("ScrollPane.background", F1Colors.BG_DARK);
        UIManager.put("Viewport.background", F1Colors.BG_CARD);
        UIManager.put("List.background", F1Colors.BG_CARD);
        UIManager.put("List.foreground", F1Colors.TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", F1Colors.F1_RED);
        UIManager.put("TextArea.background", F1Colors.BG_CARD);
        UIManager.put("TextArea.foreground", F1Colors.TEXT_PRIMARY);
    }
}
