package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.in.gui.MainFrame;
import com.f1.infrastructure.adapter.out.persistence.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

public class PanelDashboard extends JPanel {

    private final MainFrame mainFrame;
    private long gameTime = 0;
    private Timer gameLoop;

    private GameMetricCard cardPilotos, cardEquipos, cardVehiculos, cardCircuitos, cardSesiones;
    private GameActionCard actionSimulacion, actionPilotos, actionCircuitos, actionPodio;

    private static final Color NEON_RED = new Color(255, 30, 30);
    private static final Color NEON_BLUE = new Color(30, 144, 255);
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color NEON_GREEN = new Color(50, 205, 50);
    private static final Color NEON_PURPLE = new Color(138, 43, 226);

    public PanelDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false); // Permite ver el fondo principal si lo hay, aunque dibujaremos el nuestro
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        JLabel title = new JLabel("HUD SYSTEM_ // DASHBOARD");
        title.setFont(new Font("Monospaced", Font.BOLD, 28));
        title.setForeground(new Color(200, 200, 200));
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        JPanel layoutPanel = new JPanel(new GridBagLayout());
        layoutPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 20, 10, 20);

        JPanel metricsPanel = new JPanel(new GridLayout(1, 5, 20, 0));
        metricsPanel.setOpaque(false);
        cardPilotos = new GameMetricCard("PILOTS", "0", NEON_RED);
        cardEquipos = new GameMetricCard("TEAMS", "0", NEON_BLUE);
        cardVehiculos = new GameMetricCard("CARS", "0", NEON_ORANGE);
        cardCircuitos = new GameMetricCard("TRACKS", "0", NEON_GREEN);
        cardSesiones = new GameMetricCard("SESSIONS", "0", NEON_PURPLE);
        metricsPanel.add(cardPilotos);
        metricsPanel.add(cardEquipos);
        metricsPanel.add(cardVehiculos);
        metricsPanel.add(cardCircuitos);
        metricsPanel.add(cardSesiones);
        
        gbc.gridy = 0;
        gbc.weighty = 0.35;
        layoutPanel.add(metricsPanel, gbc);

        JPanel actionsPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        actionsPanel.setOpaque(false);
        actionSimulacion = new GameActionCard("INIT_SIM", NEON_RED, () -> mainFrame.selectPanel("CONFIGURACION"), 0);
        actionPilotos = new GameActionCard("MGR_PILOTS", NEON_BLUE, () -> mainFrame.selectPanel("PILOTOS"), 1);
        actionCircuitos = new GameActionCard("MGR_TRACKS", NEON_GREEN, () -> mainFrame.selectPanel("CIRCUITOS"), 2);
        actionPodio = new GameActionCard("VIEW_PODIUM", NEON_ORANGE, () -> mainFrame.selectPanel("PODIO"), 3);
        actionsPanel.add(actionSimulacion);
        actionsPanel.add(actionPilotos);
        actionsPanel.add(actionCircuitos);
        actionsPanel.add(actionPodio);

        gbc.gridy = 1;
        gbc.weighty = 0.65;
        layoutPanel.add(actionsPanel, gbc);

        add(layoutPanel, BorderLayout.CENTER);

        // Game Loop a 60 FPS
        gameLoop = new Timer(16, e -> {
            gameTime += 16;
            repaint(); // Repinta todo el dashboard incluyendo tarjetas
        });
        gameLoop.start();
    }

    public void refresh() {
        DataManager dm = DataManager.getInstance();
        cardPilotos.setValue(String.valueOf(dm.getCantidadPilotos()));
        cardEquipos.setValue(String.valueOf(dm.getCantidadEquipos()));
        cardVehiculos.setValue(String.valueOf(dm.getCantidadVehiculos()));
        cardCircuitos.setValue(String.valueOf(dm.getCantidadCircuitos()));
        cardSesiones.setValue(String.valueOf(dm.getCantidadSesiones()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Fondo HUD Animado
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();

        // 1. Color base
        g2d.setColor(new Color(8, 12, 18));
        g2d.fillRect(0, 0, w, h);

        // 2. Cuadrícula animada estilo Matrix/HUD
        g2d.setColor(new Color(30, 40, 55, 60));
        g2d.setStroke(new BasicStroke(1f));
        int gridSize = 40;
        int offset = (int) ((gameTime * 0.05) % gridSize);
        for (int x = -gridSize; x < w; x += gridSize) {
            g2d.drawLine(x + offset, 0, x + offset, h);
        }
        for (int y = -gridSize; y < h; y += gridSize) {
            g2d.drawLine(0, y + offset, w, y + offset);
        }

        // 3. Scanline láser bajando
        int scanY = (int) ((gameTime * 0.2) % h);
        g2d.setColor(new Color(0, 255, 100, 30));
        g2d.fillRect(0, scanY, w, 4);
        g2d.setColor(new Color(0, 255, 100, 10));
        g2d.fillRect(0, scanY - 20, w, 20); // Trail
    }

    // --- Componentes Avanzados Sci-Fi ---

    private Path2D createAngledShape(int w, int h, int cutSize) {
        Path2D p = new Path2D.Double();
        p.moveTo(cutSize, 0);
        p.lineTo(w, 0);
        p.lineTo(w, h - cutSize);
        p.lineTo(w - cutSize, h);
        p.lineTo(0, h);
        p.lineTo(0, cutSize);
        p.closePath();
        return p;
    }

    private class GameMetricCard extends JPanel {
        private final String title;
        private String value;
        private final Color color;

        public GameMetricCard(String title, String value, Color color) {
            this.title = title;
            this.value = value;
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 140));
        }

        public void setValue(String value) { this.value = value; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth() - 4;
            int h = getHeight() - 4;
            int cut = 20;

            Path2D shape = createAngledShape(w, h, cut);
            g2d.translate(2, 2);

            // Fondo oscuro metálico
            g2d.setColor(new Color(20, 25, 30, 200));
            g2d.fill(shape);

            // Borde brillante pulsante
            double pulse = Math.sin(gameTime * 0.005) * 0.3 + 0.7; // 0.4 a 1.0
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(150 * pulse)));
            g2d.setStroke(new BasicStroke(2f));
            g2d.draw(shape);

            // Adornos técnicos
            g2d.setColor(color);
            g2d.fillRect(cut + 10, 0, 40, 4); // Barra superior
            g2d.fillRect(w - 50, h - 4, 40, 4); // Barra inferior

            // Título
            g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2d.setColor(Color.WHITE);
            g2d.drawString(title, 20, 25);

            // --- Anillo de Telemetría Giratorio ---
            int ringSize = 60;
            int cx = w / 2;
            int cy = h / 2 + 10;
            
            AffineTransform old = g2d.getTransform();
            g2d.translate(cx, cy);
            
            // Anillo base
            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.setStroke(new BasicStroke(4f));
            g2d.drawOval(-ringSize/2, -ringSize/2, ringSize, ringSize);

            // Arcos giratorios
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            
            double rot1 = gameTime * 0.1;
            g2d.draw(new Arc2D.Double(-ringSize/2, -ringSize/2, ringSize, ringSize, rot1, 100, Arc2D.OPEN));
            
            double rot2 = -gameTime * 0.15;
            int rs2 = ringSize + 12;
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
            g2d.setStroke(new BasicStroke(2f));
            g2d.draw(new Arc2D.Double(-rs2/2, -rs2/2, rs2, rs2, rot2, 60, Arc2D.OPEN));
            g2d.draw(new Arc2D.Double(-rs2/2, -rs2/2, rs2, rs2, rot2 + 180, 60, Arc2D.OPEN));

            g2d.setTransform(old);

            // Número en el centro del anillo
            g2d.setFont(new Font("Monospaced", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            int nx = cx - fm.stringWidth(value) / 2;
            int ny = cy + fm.getAscent() / 3;

            // Efecto Glitch / Scanline en el número
            g2d.setColor(Color.WHITE);
            g2d.drawString(value, nx, ny);
            g2d.setColor(new Color(0, 0, 0, 100));
            for (int i = ny - 30; i < ny + 10; i += 3) {
                g2d.drawLine(nx - 10, i, nx + 30, i);
            }
        }
    }

    private class GameActionCard extends JPanel {
        private final String title;
        private final Color color;
        private final Runnable action;
        private final int iconType;
        private boolean hover = false;

        public GameActionCard(String title, Color color, Runnable action, int iconType) {
            this.title = title;
            this.color = color;
            this.action = action;
            this.iconType = iconType;
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { action.run(); }
                public void mouseEntered(MouseEvent e) { hover = true; }
                public void mouseExited(MouseEvent e) { hover = false; }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth() - 4;
            int h = getHeight() - 4;
            int cut = 30;

            Path2D shape = createAngledShape(w, h, cut);
            g2d.translate(2, 2);

            // Fondo oscuro metálico
            g2d.setColor(new Color(15, 18, 24, 220));
            g2d.fill(shape);

            // Borde
            g2d.setColor(hover ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
            g2d.setStroke(new BasicStroke(hover ? 3f : 1.5f));
            g2d.draw(shape);

            // Animación Hover: Barrido de luz por el borde
            if (hover) {
                g2d.setClip(shape);
                int sweepY = (int) ((gameTime * 0.4) % (h + 100)) - 50;
                GradientPaint scan = new GradientPaint(0, sweepY, new Color(255, 255, 255, 0), 0, sweepY + 30, color);
                g2d.setPaint(scan);
                g2d.fillRect(0, 0, w, sweepY + 30);
                g2d.setClip(null);
            }

            // Título
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2d.setColor(Color.WHITE);
            g2d.drawString("> " + title + " _", 20, 30);

            // --- RADAR BACKGROUND ---
            g2d.setClip(shape); // Cortar el radar para que no salga del panel angulado
            int cx = w / 2;
            int cy = h / 2 - 20;
            int radarRadius = 100;
            
            // Círculos del radar
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            g2d.drawOval(cx - radarRadius, cy - radarRadius, radarRadius*2, radarRadius*2);
            g2d.drawOval(cx - radarRadius/2, cy - radarRadius/2, radarRadius, radarRadius);
            g2d.drawLine(cx, cy - radarRadius, cx, cy + radarRadius);
            g2d.drawLine(cx - radarRadius, cy, cx + radarRadius, cy);

            // Barrido del radar
            double angle = Math.toRadians((gameTime * 0.1) % 360);
            int sweepX = cx + (int)(Math.cos(angle) * radarRadius);
            int sweepY = cy + (int)(Math.sin(angle) * radarRadius);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            g2d.drawLine(cx, cy, sweepX, sweepY);
            
            // Sector del radar (Glow)
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            g2d.fillArc(cx - radarRadius, cy - radarRadius, radarRadius*2, radarRadius*2, -(int)Math.toDegrees(angle), 40);

            // Icono Vectorial Encima
            drawVectorIcon(g2d, cx, cy, w, h);
            g2d.setClip(null);

            // --- BOTON ANIMADO ---
            int btnW = 140;
            int btnH = 40;
            int btnX = (w - btnW) / 2;
            int btnY = h - btnH - 20;

            Path2D btnShape = createAngledShape(btnW, btnH, 10);
            AffineTransform old = g2d.getTransform();
            g2d.translate(btnX, btnY);

            if (hover) {
                // Rayas diagonales animadas
                g2d.setClip(btnShape);
                g2d.setColor(color);
                g2d.fill(btnShape);
                
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.setStroke(new BasicStroke(8f));
                int offset = (int)((gameTime * 0.05) % 20);
                for (int x = -btnH - 20; x < btnW + 20; x += 20) {
                    g2d.drawLine(x + offset, 0, x + offset + btnH, btnH);
                }
                g2d.setClip(null);
            } else {
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fill(btnShape);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.draw(btnShape);
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
            String bText = hover ? "EXECUTE()" : "ACCESS ->";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(bText, (btnW - fm.stringWidth(bText))/2, 25);
            
            g2d.setTransform(old);
        }

        private void drawVectorIcon(Graphics2D g2d, int cx, int cy, int w, int h) {
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            switch(iconType) {
                case 0: // Setup/Params
                    g2d.drawOval(cx-15, cy-15, 30, 30);
                    g2d.drawLine(cx, cy-25, cx, cy-15); g2d.drawLine(cx, cy+15, cx, cy+25);
                    g2d.drawLine(cx-25, cy, cx-15, cy); g2d.drawLine(cx+15, cy, cx+25, cy);
                    g2d.drawOval(cx-5, cy-5, 10, 10);
                    break;
                case 1: // Pilots (Group of people abstract)
                    g2d.drawOval(cx-10, cy-15, 20, 20);
                    g2d.drawArc(cx-25, cy+5, 50, 40, 0, 180);
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.drawOval(cx-25, cy-5, 15, 15); g2d.drawArc(cx-35, cy+10, 30, 20, 0, 180);
                    g2d.drawOval(cx+10, cy-5, 15, 15); g2d.drawArc(cx+5, cy+10, 30, 20, 0, 180);
                    break;
                case 2: // Circuit map
                    int[] px = {cx-40, cx-20, cx+10, cx+50, cx+30, cx-30};
                    int[] py = {cy+10, cy-30, cy-20, cy+10, cy+40, cy+30};
                    g2d.drawPolygon(px, py, 6);
                    g2d.setColor(color);
                    g2d.fillOval(px[0]-4, py[0]-4, 8, 8); // Start point
                    break;
                case 3: // Podium blocks
                    g2d.drawRect(cx-10, cy-10, 20, 40); // 1
                    g2d.drawRect(cx-30, cy+10, 20, 20); // 2
                    g2d.drawRect(cx+10, cy+15, 20, 15); // 3
                    break;
            }
        }
    }
}
