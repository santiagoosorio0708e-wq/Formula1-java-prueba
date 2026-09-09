package com.f1.infrastructure.adapter.in.gui.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Avatar vectorial estilo Holograma/Videojuego para los pilotos.
 * Genera una silueta abstracta de casco de F1, tintada con el color del equipo.
 */
public class PilotAvatar extends JComponent {

    private Color teamColor;
    private boolean isHovered;

    public PilotAvatar(Color teamColor) {
        this.teamColor = teamColor;
        this.isHovered = false;
        setPreferredSize(new Dimension(140, 140));
    }

    public void setTeamColor(Color color) {
        this.teamColor = color;
        repaint();
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Dibujar fondo holográfico (Hexágono o Círculo brillante)
        int padding = 10;
        int size = Math.min(w, h) - (padding * 2);
        int cx = w / 2;
        int cy = h / 2;

        if (isHovered) {
            // Glow effect
            g2.setColor(new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue(), 60));
            g2.fill(new Ellipse2D.Double(cx - size/2 - 5, cy - size/2 - 5, size + 10, size + 10));
        }

        // Borde circular exterior
        g2.setColor(new Color(40, 40, 50));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new Ellipse2D.Double(cx - size/2, cy - size/2, size, size));

        // Fondo interior sutil
        g2.setColor(new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue(), 20));
        g2.fill(new Ellipse2D.Double(cx - size/2, cy - size/2, size, size));

        // 2. Dibujar Casco F1 Vectorial (Aproximación)
        // Cuerpo inferior (Hombros/Traje)
        Path2D suit = new Path2D.Double();
        suit.moveTo(cx - size*0.4, cy + size*0.5); // Base Izquierda
        suit.curveTo(cx - size*0.4, cy + size*0.2, cx - size*0.2, cy + size*0.1, cx, cy + size*0.1); // Hombro Izq a centro
        suit.curveTo(cx + size*0.2, cy + size*0.1, cx + size*0.4, cy + size*0.2, cx + size*0.4, cy + size*0.5); // Hombro Der a base
        suit.closePath();

        // Máscara para que no salga del círculo
        Shape clipCircle = new Ellipse2D.Double(cx - size/2, cy - size/2, size, size);
        g2.setClip(clipCircle);

        // Pintar traje
        GradientPaint suitGrad = new GradientPaint(cx, cy, new Color(30, 30, 40), cx, cy + size/2, new Color(15, 15, 20));
        g2.setPaint(suitGrad);
        g2.fill(suit);
        g2.setColor(teamColor);
        g2.setStroke(new BasicStroke(3f));
        g2.draw(suit); // Detalles del traje en color del equipo

        // Casco
        Path2D helmet = new Path2D.Double();
        helmet.moveTo(cx - size*0.25, cy + size*0.15); // Base Izquierda
        helmet.curveTo(cx - size*0.3, cy - size*0.2, cx - size*0.2, cy - size*0.4, cx, cy - size*0.4); // Curva superior izq
        helmet.curveTo(cx + size*0.2, cy - size*0.4, cx + size*0.3, cy - size*0.2, cx + size*0.25, cy + size*0.15); // Curva superior der
        helmet.closePath();

        // Sombra y forma del casco principal
        g2.setColor(teamColor); // Color base del equipo
        g2.fill(helmet);
        
        // Brillo del casco superior (Lighting)
        Path2D shine = new Path2D.Double();
        shine.moveTo(cx, cy - size*0.4);
        shine.curveTo(cx - size*0.15, cy - size*0.4, cx - size*0.2, cy - size*0.2, cx - size*0.15, cy - size*0.1);
        shine.curveTo(cx - size*0.05, cy - size*0.2, cx + size*0.1, cy - size*0.3, cx, cy - size*0.4);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.fill(shine);

        // Visor (Oscuro, tipo Daft Punk / F1)
        Path2D visor = new Path2D.Double();
        visor.moveTo(cx - size*0.22, cy - size*0.1);
        visor.lineTo(cx + size*0.22, cy - size*0.1);
        visor.curveTo(cx + size*0.2, cy + size*0.05, cx + size*0.1, cy + size*0.1, cx, cy + size*0.1);
        visor.curveTo(cx - size*0.1, cy + size*0.1, cx - size*0.2, cy + size*0.05, cx - size*0.22, cy - size*0.1);
        visor.closePath();

        g2.setColor(new Color(10, 10, 15)); // Cristal oscuro
        g2.fill(visor);
        
        // Reflejo holográfico en el visor
        g2.setColor(new Color(100, 200, 255, 40));
        g2.fill(new RoundRectangle2D.Double(cx - size*0.15, cy - size*0.08, size*0.1, size*0.05, 5, 5));

        g2.dispose();
    }
}
