package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.infrastructure.adapter.in.gui.util.F1Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel con fondo degradado personalizable.
 */
public class GradientPanel extends JPanel {
    
    private Color colorInicio;
    private Color colorFin;
    private boolean horizontal;
    private int borderRadius;

    public GradientPanel() {
        this(F1Colors.BG_DARK, F1Colors.BG_CARD, true, 0);
    }

    public GradientPanel(Color colorInicio, Color colorFin) {
        this(colorInicio, colorFin, true, 0);
    }

    public GradientPanel(Color colorInicio, Color colorFin, boolean horizontal, int borderRadius) {
        this.colorInicio = colorInicio;
        this.colorFin = colorFin;
        this.horizontal = horizontal;
        this.borderRadius = borderRadius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        GradientPaint gradient;
        if (horizontal) {
            gradient = new GradientPaint(0, 0, colorInicio, w, 0, colorFin);
        } else {
            gradient = new GradientPaint(0, 0, colorInicio, 0, h, colorFin);
        }

        g2d.setPaint(gradient);

        if (borderRadius > 0) {
            g2d.fill(new RoundRectangle2D.Float(0, 0, w, h, borderRadius, borderRadius));
        } else {
            g2d.fillRect(0, 0, w, h);
        }

        g2d.dispose();
        super.paintComponent(g);
    }

    public void setColors(Color inicio, Color fin) {
        this.colorInicio = inicio;
        this.colorFin = fin;
        repaint();
    }

    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }
}
