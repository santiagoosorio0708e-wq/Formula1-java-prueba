package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.out.persistence.DataManager;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.domain.model.EstadoCarrera;
import com.f1.domain.model.ResultadoClasificacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Panel de historial de sesiones de clasificaciÃ³n.
 */
public class PanelHistorial extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblTotalSesiones;

    public PanelHistorial() {
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("ðŸ“Š Historial de Sesiones");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        lblTotalSesiones = new JLabel("0 sesiones");
        lblTotalSesiones.setFont(F1Fonts.BODY);
        lblTotalSesiones.setForeground(F1Colors.TEXT_SECONDARY);
        headerPanel.add(lblTotalSesiones, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"SesiÃ³n #", "Ganador", "Equipo", "Tiempo", "2do Lugar", "3er Lugar", "Total Pilotos"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(F1Fonts.TABLE_CELL);
        table.setForeground(F1Colors.TEXT_PRIMARY);
        table.setBackground(F1Colors.BG_CARD);
        table.setSelectionBackground(F1Colors.F1_RED_DARK);
        table.setSelectionForeground(F1Colors.TEXT_WHITE);
        table.setGridColor(F1Colors.BORDER);
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(F1Fonts.TABLE_HEADER);
        table.getTableHeader().setForeground(F1Colors.TEXT_WHITE);
        table.getTableHeader().setBackground(F1Colors.BG_HEADER);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, F1Colors.F1_RED));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? F1Colors.BG_CARD : F1Colors.BG_TABLE_ALT);
                    c.setForeground(F1Colors.TEXT_PRIMARY);
                    if (column == 1) c.setForeground(F1Colors.PODIO_GOLD);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(F1Colors.BORDER, 1));
        scrollPane.getViewport().setBackground(F1Colors.BG_CARD);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        DataManager dm = DataManager.getInstance();
        List<List<ResultadoClasificacion>> historial = dm.getHistorialSesiones();

        lblTotalSesiones.setText(historial.size() + " sesiones");
        tableModel.setRowCount(0);

        for (int i = 0; i < historial.size(); i++) {
            List<ResultadoClasificacion> sesion = historial.get(i);
            if (sesion.isEmpty()) continue;

            ResultadoClasificacion p1 = sesion.get(0);
            String segundo = sesion.size() > 1 ? sesion.get(1).getPiloto().getNombre() : "N/A";
            String tercero = sesion.size() > 2 ? sesion.get(2).getPiloto().getNombre() : "N/A";

            tableModel.addRow(new Object[]{
                    "SesiÃ³n " + (i + 1),
                    p1.getPiloto().getNombre(),
                    p1.getEquipo(),
                    p1.getTiempoFormateado(),
                    segundo,
                    tercero,
                    sesion.size()
            });
        }
    }
}
