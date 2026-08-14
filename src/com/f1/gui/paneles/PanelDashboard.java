package com.f1.gui.paneles;

import com.f1.datos.DataManager;
import com.f1.gui.MainFrame;
import com.f1.gui.componentes.F1Button;
import com.f1.gui.componentes.GradientPanel;
import com.f1.gui.util.F1Colors;
import com.f1.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;

/**
 * Panel principal del Dashboard con resumen general del sistema.
 */
public class PanelDashboard extends JPanel {

    private final MainFrame mainFrame;
    private JLabel lblPilotos, lblEquipos, lblVehiculos, lblCircuitos, lblSesiones;

    public PanelDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(F1Fonts.TITLE_LARGE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("Resumen general del sistema F1");
        subtitleLabel.setFont(F1Fonts.BODY);
        subtitleLabel.setForeground(F1Colors.TEXT_SECONDARY);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Cards de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        lblPilotos = createStatCard(statsPanel, "🏁", "Pilotos", "0", F1Colors.F1_RED);
        lblEquipos = createStatCard(statsPanel, "🏢", "Equipos", "0", F1Colors.ACCENT_BLUE);
        lblVehiculos = createStatCard(statsPanel, "🏎️", "Vehículos", "0", F1Colors.ACCENT_ORANGE);
        lblCircuitos = createStatCard(statsPanel, "🛣️", "Circuitos", "0", F1Colors.ACCENT_GREEN);
        lblSesiones = createStatCard(statsPanel, "📊", "Sesiones", "0", F1Colors.ACCENT_PURPLE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Panel de acciones rápidas
        JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        actionsPanel.add(createActionCard("▶️ Iniciar Simulación",
                "Configura y ejecuta una nueva sesión de clasificación con 20 pilotos.",
                F1Colors.F1_RED, e -> mainFrame.selectPanel("CONFIGURACION")));

        actionsPanel.add(createActionCard("🏁 Gestionar Pilotos",
                "Agrega, edita o elimina pilotos del sistema.",
                F1Colors.ACCENT_BLUE, e -> mainFrame.selectPanel("PILOTOS")));

        actionsPanel.add(createActionCard("🛣️ Gestionar Circuitos",
                "Administra los circuitos disponibles para la simulación.",
                F1Colors.ACCENT_GREEN, e -> mainFrame.selectPanel("CIRCUITOS")));

        actionsPanel.add(createActionCard("🏆 Ver Podio",
                "Revisa los resultados y el podio de la última sesión.",
                F1Colors.PODIO_GOLD, e -> mainFrame.selectPanel("PODIO")));

        centerPanel.add(actionsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createStatCard(JPanel parent, String icon, String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(F1Colors.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel iconLabel = new JLabel(icon + "  " + title);
        iconLabel.setFont(F1Fonts.BODY_SMALL);
        iconLabel.setForeground(F1Colors.TEXT_SECONDARY);
        card.add(iconLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(F1Fonts.TITLE_LARGE);
        valueLabel.setForeground(accentColor);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        parent.add(card);
        return valueLabel;
    }

    private JPanel createActionCard(String title, String description, Color accentColor,
                                     java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(F1Colors.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Borde superior de color
        JPanel topBorder = new JPanel();
        topBorder.setPreferredSize(new Dimension(0, 3));
        topBorder.setBackground(accentColor);
        card.add(topBorder, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(F1Fonts.SUBTITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel descLabel = new JLabel("<html><p>" + description + "</p></html>");
        descLabel.setFont(F1Fonts.BODY_SMALL);
        descLabel.setForeground(F1Colors.TEXT_SECONDARY);
        descLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        textPanel.add(descLabel, BorderLayout.CENTER);

        F1Button btn = new F1Button("Ir →", F1Button.Style.SECONDARY);
        btn.addActionListener(action);
        textPanel.add(btn, BorderLayout.SOUTH);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        DataManager dm = DataManager.getInstance();
        lblPilotos.setText(String.valueOf(dm.getCantidadPilotos()));
        lblEquipos.setText(String.valueOf(dm.getCantidadEquipos()));
        lblVehiculos.setText(String.valueOf(dm.getCantidadVehiculos()));
        lblCircuitos.setText(String.valueOf(dm.getCantidadCircuitos()));
        lblSesiones.setText(String.valueOf(dm.getCantidadSesiones()));
    }
}
