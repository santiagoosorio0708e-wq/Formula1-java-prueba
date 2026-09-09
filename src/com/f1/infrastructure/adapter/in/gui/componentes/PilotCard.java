package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.domain.model.Piloto;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tarjeta interactiva para mostrar a un piloto en la cuadrícula.
 */
public class PilotCard extends JPanel {

    private Piloto piloto;
    private PilotAvatar avatar;
    private Color teamColor;
    private boolean isHovered = false;
    private boolean isSelected = false;

    // Callback para cuando se hace hover o click
    private java.util.function.Consumer<Piloto> onSelectCallback;

    public PilotCard(Piloto piloto, java.util.function.Consumer<Piloto> onSelect) {
        this.piloto = piloto;
        this.onSelectCallback = onSelect;
        this.teamColor = F1Colors.getTeamColor(piloto.getEquipo());
        
        setLayout(new BorderLayout());
        setBackground(F1Colors.BG_CARD);
        setPreferredSize(new Dimension(130, 170));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Borde inicial
        setBorder(BorderFactory.createLineBorder(new Color(40, 40, 50), 1));

        // Componentes internos
        avatar = new PilotAvatar(teamColor);
        add(avatar, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblName = new JLabel(piloto.getNombre().toUpperCase());
        lblName.setFont(F1Fonts.BODY_BOLD);
        lblName.setForeground(F1Colors.TEXT_WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTeam = new JLabel(piloto.getEquipo());
        lblTeam.setFont(F1Fonts.BODY_SMALL);
        lblTeam.setForeground(F1Colors.TEXT_MUTED);
        lblTeam.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblTeam);

        add(infoPanel, BorderLayout.SOUTH);

        // Eventos de ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                avatar.setHovered(true);
                updateVisuals();
                if (onSelectCallback != null) {
                    onSelectCallback.accept(piloto);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                avatar.setHovered(false);
                updateVisuals();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Click visual
            }
        });
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateVisuals();
    }

    private void updateVisuals() {
        if (isSelected || isHovered) {
            setBorder(BorderFactory.createLineBorder(teamColor, 2));
            setBackground(new Color(45, 45, 60)); // Ligeramente más claro
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(40, 40, 50), 1));
            setBackground(F1Colors.BG_CARD);
        }
        repaint();
    }

    public Piloto getPiloto() {
        return piloto;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Franja de color del equipo superior
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(teamColor);
        g2.fillRect(0, 0, getWidth(), 4);
        g2.dispose();
    }
}
