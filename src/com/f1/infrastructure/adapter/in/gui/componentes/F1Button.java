package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

/**
 * Botón personalizado con estilo F1:
 * - Esquinas anguladas (Slanted/Chamfered).
 * - Animación suave de hover y click usando Timer.
 * - Resplandor dinámico de videojuegos AAA.
 */
public class F1Button extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER, SUCCESS }

    private Style style;
    private Color bgColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    
    // Animación
    private Timer animTimer;
    private float hoverFactor = 0f; // 0.0 a 1.0
    private float pressFactor = 0f; // 0.0 a 1.0
    private boolean isHovered = false;
    private boolean isPressed = false;

    public F1Button(String text) {
        this(text, Style.PRIMARY);
    }

    public F1Button(String text, Style style) {
        super(text.toUpperCase()); // En F1 el texto suele ir en mayúsculas
        this.style = style;
        applyStyle(style);
        setupButton();
        setupAnimation();
    }

    private void applyStyle(Style style) {
        switch (style) {
            case PRIMARY -> {
                bgColor = F1Colors.F1_RED;
                hoverColor = F1Colors.F1_RED_HOVER;
                pressedColor = F1Colors.F1_RED_DARK;
                textColor = F1Colors.TEXT_WHITE;
            }
            case SECONDARY -> {
                bgColor = new Color(40, 40, 50); // Oscuro tech
                hoverColor = new Color(60, 60, 75);
                pressedColor = new Color(25, 25, 35);
                textColor = F1Colors.TEXT_PRIMARY;
            }
            case DANGER -> {
                bgColor = F1Colors.STATUS_DANGER;
                hoverColor = new Color(255, 60, 60);
                pressedColor = new Color(150, 20, 20);
                textColor = F1Colors.TEXT_WHITE;
            }
            case SUCCESS -> {
                bgColor = F1Colors.STATUS_OK;
                hoverColor = new Color(60, 220, 140);
                pressedColor = new Color(20, 120, 70);
                textColor = F1Colors.TEXT_WHITE;
            }
        }
    }

    private void setupButton() {
        setFont(F1Fonts.BUTTON);
        setForeground(textColor);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                animTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                animTimer.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                animTimer.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                animTimer.start();
            }
        });
    }

    private void setupAnimation() {
        animTimer = new Timer(16, e -> {
            boolean changed = false;
            
            // Hover animation
            if (isHovered && hoverFactor < 1f) {
                hoverFactor += 0.15f;
                if (hoverFactor > 1f) hoverFactor = 1f;
                changed = true;
            } else if (!isHovered && hoverFactor > 0f) {
                hoverFactor -= 0.1f;
                if (hoverFactor < 0f) hoverFactor = 0f;
                changed = true;
            }
            
            // Press animation
            if (isPressed && pressFactor < 1f) {
                pressFactor += 0.3f;
                if (pressFactor > 1f) pressFactor = 1f;
                changed = true;
            } else if (!isPressed && pressFactor > 0f) {
                pressFactor -= 0.2f;
                if (pressFactor < 0f) pressFactor = 0f;
                changed = true;
            }

            if (changed) {
                repaint();
            } else {
                animTimer.stop();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Calcular color interpolado
        Color currentBg = interpolateColor(bgColor, hoverColor, hoverFactor);
        currentBg = interpolateColor(currentBg, pressedColor, pressFactor);

        // 2. Crear geometría F1 (Angulada)
        int skew = 8; // Pixeles de inclinación
        Path2D shape = new Path2D.Float();
        shape.moveTo(skew, 0);
        shape.lineTo(w, 0);
        shape.lineTo(w - skew, h);
        shape.lineTo(0, h);
        shape.closePath();

        // Si está siendo presionado, escalar ligeramente hacia abajo (efecto click)
        if (pressFactor > 0) {
            double scale = 1.0 - (0.05 * pressFactor);
            g2.translate(w / 2.0 * (1 - scale), h / 2.0 * (1 - scale));
            g2.scale(scale, scale);
        }

        // 3. Dibujar sombra / glow en hover
        if (hoverFactor > 0) {
            g2.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), (int)(80 * hoverFactor)));
            g2.setStroke(new BasicStroke(4f + (3f * hoverFactor)));
            g2.draw(shape);
        }

        // 4. Dibujar fondo
        g2.setColor(currentBg);
        g2.fill(shape);

        // 5. Borde F1 Tech
        if (style == Style.SECONDARY) {
            g2.setColor(new Color(100, 100, 120));
        } else {
            g2.setColor(new Color(255, 255, 255, 100)); // Borde sutil blanco
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(shape);

        // 6. Efecto de "Scanline" interior
        g2.setClip(shape);
        g2.setColor(new Color(0, 0, 0, 20));
        for (int i = 0; i < w; i += 4) {
            g2.drawLine(i, 0, i - h, h);
        }

        // 7. Dibujar Texto
        g2.setClip(null); // Quitar clip
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (w - fm.stringWidth(getText())) / 2;
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
        
        // Sombra de texto
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(getText(), textX + 1, textY + 1);
        
        // Texto principal brillante
        g2.setColor(textColor);
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    private Color interpolateColor(Color c1, Color c2, float fraction) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
        int a = (int) (c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * fraction);
        return new Color(r, g, b, a);
    }

    public void setStyle(Style style) {
        this.style = style;
        applyStyle(style);
        repaint();
    }
}
