package com.f1.gui.componentes;

import com.f1.gui.util.F1Colors;
import com.f1.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente visual del podio para los 3 primeros clasificados.
 * Muestra escalones de diferente altura con colores oro, plata y bronce.
 */
public class PodiumPanel extends JPanel {

    private String[] nombres = {"", "", ""};
    private String[] equipos = {"", "", ""};
    private String[] tiempos = {"", "", ""};

    public PodiumPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(700, 350));
    }

    public void setResultados(String nombre1, String equipo1, String tiempo1,
                               String nombre2, String equipo2, String tiempo2,
                               String nombre3, String equipo3, String tiempo3) {
        nombres = new String[]{nombre1, nombre2, nombre3};
        equipos = new String[]{equipo1, equipo2, equipo3};
        tiempos = new String[]{tiempo1, tiempo2, tiempo3};
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        int podiumWidth = Math.min(w - 40, 660);
        int startX = (w - podiumWidth) / 2;
        int blockW = podiumWidth / 3;
        int baseY = h - 20;

        // Alturas de los escalones
        int[] heights = {200, 160, 130}; // P1, P2, P3
        Color[] colors = {F1Colors.PODIO_GOLD, F1Colors.PODIO_SILVER, F1Colors.PODIO_BRONZE};
        int[] xPositions = {startX + blockW, startX, startX + blockW * 2}; // P1 centro, P2 izq, P3 der
        String[] posLabels = {"1", "2", "3"};
        int[] order = {1, 0, 2}; // Dibujar P2, P1, P3 (P2 izq, P1 centro, P3 der)

        for (int idx = 0; idx < 3; idx++) {
            int i = order[idx]; // Posición real (0=P1, 1=P2, 2=P3)
            int x = xPositions[i];
            int blockH = heights[i];
            int y = baseY - blockH;

            // Escalón con gradiente
            GradientPaint gp = new GradientPaint(x, y, colors[i], x, baseY,
                    new Color(colors[i].getRed() / 2, colors[i].getGreen() / 2, colors[i].getBlue() / 2));
            g2d.setPaint(gp);
            g2d.fill(new RoundRectangle2D.Float(x + 4, y, blockW - 8, blockH, 12, 12));

            // Borde
            g2d.setColor(new Color(255, 255, 255, 40));
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new RoundRectangle2D.Float(x + 4, y, blockW - 8, blockH, 12, 12));

            // Número de posición
            g2d.setFont(F1Fonts.PODIO_POSITION);
            g2d.setColor(new Color(0, 0, 0, 80));
            FontMetrics fm = g2d.getFontMetrics();
            String posText = posLabels[i];
            int textX = x + (blockW - fm.stringWidth(posText)) / 2;
            g2d.drawString(posText, textX, y + blockH - 15);

            // Nombre del piloto (encima del escalón)
            g2d.setFont(F1Fonts.PODIO_NAME);
            g2d.setColor(F1Colors.TEXT_WHITE);
            fm = g2d.getFontMetrics();
            String nombre = nombres[i];
            textX = x + (blockW - fm.stringWidth(nombre)) / 2;
            g2d.drawString(nombre, textX, y - 35);

            // Equipo
            g2d.setFont(F1Fonts.BODY_SMALL);
            g2d.setColor(F1Colors.TEXT_SECONDARY);
            fm = g2d.getFontMetrics();
            String equipo = equipos[i];
            textX = x + (blockW - fm.stringWidth(equipo)) / 2;
            g2d.drawString(equipo, textX, y - 18);

            // Tiempo
            g2d.setFont(F1Fonts.MONOSPACE);
            g2d.setColor(colors[i]);
            fm = g2d.getFontMetrics();
            String tiempo = tiempos[i];
            textX = x + (blockW - fm.stringWidth(tiempo)) / 2;
            g2d.drawString(tiempo, textX, y - 2);
        }

        g2d.dispose();
    }
}
