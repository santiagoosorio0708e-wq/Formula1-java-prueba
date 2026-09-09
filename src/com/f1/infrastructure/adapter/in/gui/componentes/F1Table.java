package com.f1.infrastructure.adapter.in.gui.componentes;

import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * JTable estilizada con tema oscuro F1, filas alternadas y header personalizado.
 */
public class F1Table extends JTable {

    public F1Table(TableModel model) {
        super(model);
        setupTable();
    }

    private void setupTable() {
        // ConfiguraciÃ³n general
        setFont(F1Fonts.TABLE_CELL);
        setForeground(F1Colors.TEXT_PRIMARY);
        setBackground(F1Colors.BG_CARD);
        setSelectionBackground(F1Colors.F1_RED_DARK);
        setSelectionForeground(F1Colors.TEXT_WHITE);
        setGridColor(F1Colors.BORDER);
        setRowHeight(38);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 1));
        setFillsViewportHeight(true);

        // Header
        JTableHeader header = getTableHeader();
        header.setFont(F1Fonts.TABLE_HEADER);
        header.setForeground(F1Colors.TEXT_WHITE);
        header.setBackground(F1Colors.BG_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, F1Colors.F1_RED));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));

        // Renderer personalizado
        setDefaultRenderer(Object.class, new F1CellRenderer());

        // Scroll
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    /**
     * Renderer personalizado para las celdas con filas alternadas.
     */
    private static class F1CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(F1Colors.F1_RED_DARK);
                c.setForeground(F1Colors.TEXT_WHITE);
            } else {
                c.setBackground(row % 2 == 0 ? F1Colors.BG_CARD : F1Colors.BG_TABLE_ALT);
                c.setForeground(F1Colors.TEXT_PRIMARY);
            }

            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return c;
        }
    }

    /**
     * Envuelve esta tabla en un JScrollPane estilizado.
     */
    public JScrollPane wrapInScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBorder(BorderFactory.createLineBorder(F1Colors.BORDER, 1));
        scrollPane.getViewport().setBackground(F1Colors.BG_CARD);
        scrollPane.setBackground(F1Colors.BG_CARD);

        // Estilizar scrollbar
        scrollPane.getVerticalScrollBar().setBackground(F1Colors.BG_DARK);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = F1Colors.BORDER_LIGHT;
                this.trackColor = F1Colors.BG_DARK;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                return btn;
            }
        });

        return scrollPane;
    }
}
