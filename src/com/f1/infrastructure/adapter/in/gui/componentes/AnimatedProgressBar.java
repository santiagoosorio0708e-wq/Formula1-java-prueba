package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Barra de progreso animada para combustible y desgaste de neumÃ¡ticos.
 * Cambia de color dinÃ¡micamente segÃºn el nivel (verde â†’ amarillo â†’ rojo).
 */
public class AnimatedProgressBar extends JPanel {

    private double value = 100.0;   // 0-100
    private double maxValue = 100.0;
    private String label = "";
    private boolean invertColors = false; // true = rojo en valor alto (ej: desgaste)
    private int barHeight = 16;
    private int borderRadius = 8;

    public AnimatedProgressBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(150, 24));
    }

    public AnimatedProgressBar(String label, boolean invertColors) {
        this();
        this.label = label;
        this.invertColors = invertColors;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int barY = (h - barHeight) / 2;

        // Fondo de la barra
        g2d.setColor(F1Colors.BG_INPUT);
        g2d.fill(new RoundRectangle2D.Float(0, barY, w, barHeight, borderRadius, borderRadius));

        // Barra de progreso
        double percentage = Math.max(0, Math.min(100, value)) / maxValue;
        int barWidth = (int) (w * percentage);

        if (barWidth > 0) {
            Color barColor = getBarColor(percentage);
            g2d.setColor(barColor);
            g2d.fill(new RoundRectangle2D.Float(0, barY, barWidth, barHeight, borderRadius, borderRadius));

            // Efecto brillo
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.fill(new RoundRectangle2D.Float(0, barY, barWidth, barHeight / 2, borderRadius, borderRadius));
        }

        // Texto
        g2d.setFont(F1Fonts.CAPTION);
        String text = String.format("%s %.0f%%", label, value);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (w - fm.stringWidth(text)) / 2;
        int textY = barY + (barHeight + fm.getAscent() - fm.getDescent()) / 2;
        g2d.setColor(F1Colors.TEXT_WHITE);
        g2d.drawString(text, textX, textY);

        g2d.dispose();
    }

    private Color getBarColor(double percentage) {
        if (invertColors) {
            percentage = 1.0 - percentage; // Invertir para desgaste
        }

        if (percentage > 0.5) {
            return F1Colors.STATUS_OK;
        } else if (percentage > 0.2) {
            return F1Colors.STATUS_WARNING;
        } else {
            return F1Colors.STATUS_DANGER;
        }
    }

    public void setValue(double value) {
        this.value = Math.max(0, Math.min(maxValue, value));
        repaint();
    }

    public double getValue() { return value; }

    public void setLabel(String label) {
        this.label = label;
        repaint();
    }

    public void setInvertColors(boolean invertColors) {
        this.invertColors = invertColors;
        repaint();
    }

    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
        repaint();
    }
}
