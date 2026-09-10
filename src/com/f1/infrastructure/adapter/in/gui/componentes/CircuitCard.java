package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.domain.model.Circuito;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Tarjeta interactiva para mostrar un circuito en la cuadrícula.
 */
public class CircuitCard extends JPanel {

    private Circuito circuito;
    private boolean isHovered = false;
    private java.util.function.Consumer<Circuito> onSelectCallback;
    private Path2D miniTrack;

    public CircuitCard(Circuito circuito, java.util.function.Consumer<Circuito> onSelect) {
        this.circuito = circuito;
        this.onSelectCallback = onSelect;
        
        setLayout(new BorderLayout());
        setBackground(F1Colors.BG_CARD);
        setPreferredSize(new Dimension(240, 220));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setBorder(BorderFactory.createLineBorder(new Color(40, 40, 50), 1));
        
        // Generar silueta miniatura rápida
        miniTrack = generateMiniTrack(circuito.getNombre());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblName = new JLabel("<html><div style='text-align: center;'>" + circuito.getNombre().toUpperCase() + "</div></html>");
        lblName.setFont(F1Fonts.BODY_BOLD);
        lblName.setForeground(F1Colors.TEXT_WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCountry = new JLabel(circuito.getPais());
        lblCountry.setFont(F1Fonts.BODY_SMALL);
        lblCountry.setForeground(F1Colors.F1_RED);
        lblCountry.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStats = new JLabel(String.format("%.2f km - %d Vueltas", circuito.getLongitudKm(), circuito.getVueltas()));
        lblStats.setFont(F1Fonts.BODY_SMALL);
        lblStats.setForeground(F1Colors.TEXT_MUTED);
        lblStats.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblCountry);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(lblStats);

        add(infoPanel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                updateVisuals();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                updateVisuals();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (onSelectCallback != null) onSelectCallback.accept(circuito);
            }
        });
    }

    private void updateVisuals() {
        if (isHovered) {
            setBorder(BorderFactory.createLineBorder(F1Colors.F1_RED, 2));
            setBackground(new Color(45, 45, 60));
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(40, 40, 50), 1));
            setBackground(F1Colors.BG_CARD);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Franja decorativa
        g2.setColor(isHovered ? F1Colors.F1_RED : new Color(50, 50, 60));
        g2.fillRect(0, 0, getWidth(), 4);

        // Dibujar miniatura de pista
        int w = getWidth();
        int h = getHeight() - 70; // Espacio arriba de los textos
        
        if (miniTrack != null) {
            double scale = Math.min(w, h) * 0.5;
            double offsetX = (w - scale) / 2.0;
            double offsetY = (h - scale) / 2.0 + 10;
            
            AffineTransform tx = new AffineTransform();
            tx.translate(offsetX, offsetY);
            tx.scale(scale, scale);
            Shape scaledTrack = tx.createTransformedShape(miniTrack);
            
            g2.setStroke(new BasicStroke(isHovered ? 4f : 3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(isHovered ? F1Colors.F1_RED_DARK : new Color(60, 60, 75));
            g2.draw(scaledTrack);
        }

        g2.dispose();
    }

    // Un mini-generador para la tarjeta
    private Path2D generateMiniTrack(String name) {
        // Semilla estática para que siempre sea igual
        Random r = new Random(name.toLowerCase().hashCode());
        Path2D p = new Path2D.Double();
        
        int numPoints = 6 + r.nextInt(5);
        double[] x = new double[numPoints];
        double[] y = new double[numPoints];
        
        // Círculo base con ruido
        for (int i = 0; i < numPoints; i++) {
            double angle = (Math.PI * 2 * i) / numPoints;
            double radius = 0.3 + (r.nextDouble() * 0.7);
            x[i] = 0.5 + Math.cos(angle) * radius;
            y[i] = 0.5 + Math.sin(angle) * radius;
        }
        
        p.moveTo(x[0], y[0]);
        for (int i = 1; i < numPoints; i++) {
            // Curvas suaves
            int prev = (i - 1 + numPoints) % numPoints;
            int next = (i + 1) % numPoints;
            double cx = x[prev] + (x[i] - x[prev]) * 0.5;
            double cy = y[prev] + (y[i] - y[prev]) * 0.5;
            p.curveTo(cx, cy, x[i], y[i], x[next], y[next]);
        }
        p.closePath();
        return p;
    }
}
