package com.f1.infrastructure.adapter.in.gui;

import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.infrastructure.adapter.in.gui.paneles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private Map<String, SidebarItem> sidebarItems;
    private String currentPanel = "DASHBOARD";

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
        setTitle("F1 Simulation - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(15, 18, 25));

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

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

        selectPanel("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 25, 35), getWidth(), 0, new Color(15, 18, 25));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(250, 0));

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                String text = "F1 SIMULATION";
                g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = 50;

                for (int i = 8; i > 0; i -= 2) {
                    g2d.setColor(new Color(255, 0, 0, 30));
                    g2d.setStroke(new BasicStroke(i, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawChars(text.toCharArray(), 0, text.length(), x, y);
                }
                
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(255, 150, 150));
                g2d.drawChars(text.toCharArray(), 0, text.length(), x, y);
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(250, 90));
        logoPanel.setPreferredSize(new Dimension(250, 90));
        sidebar.add(logoPanel);

        sidebar.add(Box.createVerticalStrut(10));

        sidebarItems = new LinkedHashMap<>();
        // IDs: 0=Dashboard, 1=Piloto, 2=Equipo, 3=Coche, 4=Circuito, 5=Config, 6=Simulacion, 7=Podio, 8=Historial
        addSidebarItem(sidebar, 0, "Dashboard", "DASHBOARD");
        addSidebarItem(sidebar, 1, "Pilotos", "PILOTOS");
        addSidebarItem(sidebar, 2, "Equipos", "EQUIPOS");
        addSidebarItem(sidebar, 3, "Vehículos", "VEHICULOS");
        addSidebarItem(sidebar, 4, "Circuitos", "CIRCUITOS");

        sidebar.add(Box.createVerticalStrut(15));
        
        JPanel sepPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 15));
                g.drawLine(30, getHeight()/2, getWidth()-30, getHeight()/2);
            }
        };
        sepPanel.setOpaque(false);
        sepPanel.setMaximumSize(new Dimension(250, 20));
        sidebar.add(sepPanel);
        
        sidebar.add(Box.createVerticalStrut(15));

        addSidebarItem(sidebar, 5, "Configuración", "CONFIGURACION");
        addSidebarItem(sidebar, 6, "Simulaciones", "SIMULACION");
        addSidebarItem(sidebar, 7, "Podio", "PODIO");
        addSidebarItem(sidebar, 8, "Historial", "HISTORIAL");

        sidebar.add(Box.createVerticalGlue());

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 20));
        footerPanel.setMaximumSize(new Dimension(250, 50));
        JLabel versionLabel = new JLabel("v1.0.0 · Java Swing");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(255, 255, 255, 80));
        footerPanel.add(versionLabel, BorderLayout.CENTER);
        sidebar.add(footerPanel);

        return sidebar;
    }

    private void addSidebarItem(JPanel sidebar, int iconType, String text, String cardName) {
        SidebarItem item = new SidebarItem(iconType, text, cardName);
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { selectPanel(cardName); }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(currentPanel)) {
                    item.setHover(true);
                    item.repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardName.equals(currentPanel)) {
                    item.setHover(false);
                    item.repaint();
                }
            }
        });
        sidebarItems.put(cardName, item);
        sidebar.add(item);
        sidebar.add(Box.createVerticalStrut(5));
    }

    public void selectPanel(String cardName) {
        currentPanel = cardName;
        cardLayout.show(contentPanel, cardName);

        for (Map.Entry<String, SidebarItem> entry : sidebarItems.entrySet()) {
            SidebarItem item = entry.getValue();
            item.setSelected(entry.getKey().equals(cardName));
            item.setHover(false);
            item.repaint();
        }

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

    public PanelSimulacion getPanelSimulacion() { return panelSimulacion; }
    public PanelPodio getPanelPodio() { return panelPodio; }

    private class SidebarItem extends JPanel {
        private final int iconType;
        private final String text;
        private boolean selected = false;
        private boolean hover = false;

        public SidebarItem(int iconType, String text, String cardName) {
            this.iconType = iconType;
            this.text = text;
            setOpaque(false);
            setMaximumSize(new Dimension(220, 48));
            setPreferredSize(new Dimension(220, 48));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setSelected(boolean selected) { this.selected = selected; }
        public void setHover(boolean hover) { this.hover = hover; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();

            if (selected) {
                g2d.setColor(new Color(225, 6, 0, 30));
                g2d.fillRoundRect(10, 2, w - 20, h - 4, 15, 15);
                g2d.setColor(new Color(225, 6, 0, 150));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(10, 2, w - 20, h - 4, 15, 15);
            } else if (hover) {
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRoundRect(10, 2, w - 20, h - 4, 15, 15);
            }

            // Dibujar icono vectorial
            drawVectorIcon(g2d, iconType, 25, h / 2, selected ? Color.WHITE : new Color(255, 255, 255, 150));

            // Texto
            g2d.setFont(new Font("SansSerif", selected ? Font.BOLD : Font.PLAIN, 15));
            g2d.setColor(selected ? new Color(225, 6, 0) : new Color(255, 255, 255, 180));
            g2d.drawString(text, 65, h / 2 + 5);
        }

        private void drawVectorIcon(Graphics2D g2d, int type, int cx, int cy, Color c) {
            g2d.setColor(c);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            switch(type) {
                case 0: // Dashboard (Engranaje + lineas)
                    g2d.drawOval(cx-6, cy-6, 12, 12);
                    g2d.drawLine(cx, cy-8, cx, cy-10); g2d.drawLine(cx, cy+8, cx, cy+10);
                    g2d.drawLine(cx-8, cy, cx-10, cy); g2d.drawLine(cx+8, cy, cx+10, cy);
                    break;
                case 1: // Piloto (Casco)
                    g2d.fillArc(cx-8, cy-8, 16, 16, 0, 180);
                    g2d.drawRect(cx-8, cy, 16, 6);
                    break;
                case 2: // Equipos (Edificio)
                    g2d.drawRect(cx-7, cy-10, 14, 20);
                    g2d.fillRect(cx-3, cy-6, 2, 2); g2d.fillRect(cx+1, cy-6, 2, 2);
                    g2d.fillRect(cx-3, cy-2, 2, 2); g2d.fillRect(cx+1, cy-2, 2, 2);
                    break;
                case 3: // Coche
                    g2d.drawRoundRect(cx-10, cy-3, 20, 6, 3, 3);
                    g2d.fillOval(cx-8, cy+1, 6, 6); g2d.fillOval(cx+2, cy+1, 6, 6);
                    g2d.drawLine(cx-10, cy-1, cx-14, cy-1);
                    break;
                case 4: // Circuito
                    int[] px = {cx-8, cx-2, cx+6, cx+8, cx+2, cx-6};
                    int[] py = {cy+2, cy-6, cy-4, cy+2, cy+8, cy+6};
                    g2d.drawPolygon(px, py, 6);
                    break;
                case 5: // Config
                    g2d.drawLine(cx-7, cy-7, cx+7, cy+7);
                    g2d.drawOval(cx-9, cy-9, 4, 4); g2d.drawOval(cx+5, cy+5, 4, 4);
                    break;
                case 6: // Simulacion
                    g2d.drawPolygon(new int[]{cx-5, cx-5, cx+6}, new int[]{cy-6, cy+6, cy}, 3);
                    break;
                case 7: // Podio
                    g2d.drawRect(cx-3, cy-2, 6, 12);
                    g2d.drawRect(cx-9, cy+4, 6, 6);
                    g2d.drawRect(cx+3, cy+6, 6, 4);
                    break;
                case 8: // Historial
                    g2d.drawOval(cx-7, cy-7, 14, 14);
                    g2d.drawLine(cx, cy, cx, cy-4);
                    g2d.drawLine(cx, cy, cx+3, cy+3);
                    break;
            }
        }
    }
}
