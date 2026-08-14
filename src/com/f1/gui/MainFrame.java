package com.f1.gui;

import com.f1.gui.componentes.GradientPanel;
import com.f1.gui.util.F1Colors;
import com.f1.gui.util.F1Fonts;
import com.f1.gui.paneles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal de la aplicación F1 Simulation.
 * Contiene un sidebar de navegación y un área central con CardLayout.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private Map<String, JPanel> sidebarItems;
    private String currentPanel = "DASHBOARD";

    // Paneles
    private PanelDashboard panelDashboard;
    private PanelPilotos panelPilotos;
    private PanelEquipos panelEquipos;
    private PanelVehiculos panelVehiculos;
    private PanelCircuitos panelCircuitos;
    private PanelConfiguracion panelConfiguracion;
    private PanelSimulacion panelSimulacion;
    private PanelPodio panelPodio;
    private PanelHistorial panelHistorial;

    public MainFrame() {
        setTitle("🏎️ Formula 1 Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(F1Colors.BG_DARK);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(F1Colors.BG_DARK);

        // Crear paneles
        panelDashboard = new PanelDashboard(this);
        panelPilotos = new PanelPilotos();
        panelEquipos = new PanelEquipos();
        panelVehiculos = new PanelVehiculos();
        panelCircuitos = new PanelCircuitos();
        panelConfiguracion = new PanelConfiguracion(this);
        panelSimulacion = new PanelSimulacion(this);
        panelPodio = new PanelPodio();
        panelHistorial = new PanelHistorial();

        contentPanel.add(panelDashboard, "DASHBOARD");
        contentPanel.add(panelPilotos, "PILOTOS");
        contentPanel.add(panelEquipos, "EQUIPOS");
        contentPanel.add(panelVehiculos, "VEHICULOS");
        contentPanel.add(panelCircuitos, "CIRCUITOS");
        contentPanel.add(panelConfiguracion, "CONFIGURACION");
        contentPanel.add(panelSimulacion, "SIMULACION");
        contentPanel.add(panelPodio, "PODIO");
        contentPanel.add(panelHistorial, "HISTORIAL");

        add(contentPanel, BorderLayout.CENTER);

        // Seleccionar dashboard por defecto
        selectPanel("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(F1Colors.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, F1Colors.BORDER));

        // Logo / Título
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(F1Colors.BG_SIDEBAR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        logoPanel.setMaximumSize(new Dimension(230, 80));

        JLabel logoLabel = new JLabel("F1 SIMULATION");
        logoLabel.setFont(F1Fonts.TITLE);
        logoLabel.setForeground(F1Colors.F1_RED);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        sidebar.add(logoPanel);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(F1Colors.BORDER);
        sep.setMaximumSize(new Dimension(230, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        // Items del menú
        sidebarItems = new LinkedHashMap<>();
        addSidebarItem(sidebar, "🏠  Dashboard", "DASHBOARD");
        addSidebarItem(sidebar, "🏁  Pilotos", "PILOTOS");
        addSidebarItem(sidebar, "🏢  Equipos", "EQUIPOS");
        addSidebarItem(sidebar, "🏎️  Vehículos", "VEHICULOS");
        addSidebarItem(sidebar, "🛣️  Circuitos", "CIRCUITOS");

        sidebar.add(Box.createVerticalStrut(10));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(F1Colors.BORDER);
        sep2.setMaximumSize(new Dimension(230, 1));
        sidebar.add(sep2);
        sidebar.add(Box.createVerticalStrut(10));

        addSidebarItem(sidebar, "⚙️  Configuración", "CONFIGURACION");
        addSidebarItem(sidebar, "▶️  Simulación", "SIMULACION");
        addSidebarItem(sidebar, "🏆  Podio", "PODIO");
        addSidebarItem(sidebar, "📊  Historial", "HISTORIAL");

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(F1Colors.BG_SIDEBAR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        footerPanel.setMaximumSize(new Dimension(230, 50));

        JLabel versionLabel = new JLabel("v1.0.0 · Java Swing");
        versionLabel.setFont(F1Fonts.CAPTION);
        versionLabel.setForeground(F1Colors.TEXT_MUTED);
        footerPanel.add(versionLabel, BorderLayout.CENTER);
        sidebar.add(footerPanel);

        return sidebar;
    }

    private void addSidebarItem(JPanel sidebar, String text, String cardName) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(F1Colors.BG_SIDEBAR);
        item.setMaximumSize(new Dimension(230, 44));
        item.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(F1Fonts.SIDEBAR_ITEM);
        label.setForeground(F1Colors.TEXT_SECONDARY);
        item.add(label, BorderLayout.CENTER);

        // Indicador activo (borde izquierdo rojo)
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(3, 0));
        indicator.setBackground(F1Colors.BG_SIDEBAR);
        item.add(indicator, BorderLayout.WEST);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectPanel(cardName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(currentPanel)) {
                    item.setBackground(F1Colors.BG_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardName.equals(currentPanel)) {
                    item.setBackground(F1Colors.BG_SIDEBAR);
                }
            }
        });

        sidebarItems.put(cardName, item);
        sidebar.add(item);
    }

    /**
     * Selecciona y muestra un panel por su nombre.
     */
    public void selectPanel(String cardName) {
        currentPanel = cardName;
        cardLayout.show(contentPanel, cardName);

        // Actualizar estilos del sidebar
        for (Map.Entry<String, JPanel> entry : sidebarItems.entrySet()) {
            JPanel item = entry.getValue();
            JLabel label = (JLabel) ((BorderLayout) item.getLayout())
                    .getLayoutComponent(BorderLayout.CENTER);
            JPanel indicator = (JPanel) ((BorderLayout) item.getLayout())
                    .getLayoutComponent(BorderLayout.WEST);

            if (entry.getKey().equals(cardName)) {
                item.setBackground(new Color(225, 6, 0, 20));
                label.setForeground(F1Colors.F1_RED);
                label.setFont(F1Fonts.SIDEBAR_ITEM_BOLD);
                indicator.setBackground(F1Colors.F1_RED);
            } else {
                item.setBackground(F1Colors.BG_SIDEBAR);
                label.setForeground(F1Colors.TEXT_SECONDARY);
                label.setFont(F1Fonts.SIDEBAR_ITEM);
                indicator.setBackground(F1Colors.BG_SIDEBAR);
            }
        }

        // Refrescar datos del panel activo
        refreshCurrentPanel();
    }

    private void refreshCurrentPanel() {
        switch (currentPanel) {
            case "DASHBOARD" -> panelDashboard.refresh();
            case "PILOTOS" -> panelPilotos.refresh();
            case "EQUIPOS" -> panelEquipos.refresh();
            case "VEHICULOS" -> panelVehiculos.refresh();
            case "CIRCUITOS" -> panelCircuitos.refresh();
            case "HISTORIAL" -> panelHistorial.refresh();
        }
    }

    /**
     * Obtiene el panel de simulación.
     */
    public PanelSimulacion getPanelSimulacion() { return panelSimulacion; }

    /**
     * Obtiene el panel de podio.
     */
    public PanelPodio getPanelPodio() { return panelPodio; }
}
