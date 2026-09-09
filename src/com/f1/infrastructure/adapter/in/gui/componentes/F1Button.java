package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * BotÃ³n personalizado con estilo F1, hover animado y bordes redondeados.
 */
public class F1Button extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER, SUCCESS }

    private Color bgColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int borderRadius = 8;

    public F1Button(String text) {
        this(text, Style.PRIMARY);
    }

    public F1Button(String text, Style style) {
        super(text);
        applyStyle(style);
        setupButton();
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
                bgColor = F1Colors.BG_INPUT;
                hoverColor = F1Colors.BG_HOVER;
                pressedColor = new Color(55, 55, 75);
                textColor = F1Colors.TEXT_PRIMARY;
            }
            case DANGER -> {
                bgColor = new Color(180, 30, 30);
                hoverColor = new Color(200, 40, 40);
                pressedColor = new Color(150, 20, 20);
                textColor = F1Colors.TEXT_WHITE;
            }
            case SUCCESS -> {
                bgColor = new Color(30, 150, 90);
                hoverColor = new Color(40, 180, 110);
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
        setPreferredSize(new Dimension(130, 36));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg;
        if (isPressed) bg = pressedColor;
        else if (isHovered) bg = hoverColor;
        else bg = bgColor;

        g2d.setColor(bg);
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));

        g2d.dispose();
        super.paintComponent(g);
    }

    public void setStyle(Style style) {
        applyStyle(style);
        repaint();
    }

    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }
}
