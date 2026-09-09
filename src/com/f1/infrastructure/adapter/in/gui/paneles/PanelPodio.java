package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.in.gui.componentes.F1Table;
import com.f1.infrastructure.adapter.in.gui.componentes.PodiumPanel;
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
 * Panel del podio con visualización de los 3 primeros y tabla completa.
 */
public class PanelPodio extends JPanel {

    private PodiumPanel podiumPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblMejorVuelta, lblTotalPits, lblTiempoGanador;

    public PanelPodio() {
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("> CLASIFICACIÓN FINAL Y PODIO _");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Panel central con podio + estadísticas
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(0, 370));

        // Podio visual
        podiumPanel = new PodiumPanel();
        topPanel.add(podiumPanel, BorderLayout.CENTER);

        // Estadísticas laterales
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(F1Colors.BG_CARD);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        statsPanel.setPreferredSize(new Dimension(220, 0));

        JLabel statsTitle = new JLabel(" Estadísticas");
        statsTitle.setFont(F1Fonts.SECTION_TITLE);
        statsTitle.setForeground(F1Colors.F1_RED);
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(statsTitle);
        statsPanel.add(Box.createVerticalStrut(15));

        lblTiempoGanador = createStatItem(statsPanel, "> Tiempo Ganador:");
        lblMejorVuelta = createStatItem(statsPanel, "> Mejor Vuelta:");
        lblTotalPits = createStatItem(statsPanel, "> Total Pit Stops:");

        topPanel.add(statsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.CENTER);

        // Tabla de clasificación completa
        String[] columns = {"Pos", "Piloto", "Equipo", "Tiempo Total", "Diferencia",
                "Pit Stops", "Mejor Vuelta", "Estado"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(F1Fonts.TABLE_CELL);
        table.setForeground(F1Colors.TEXT_PRIMARY);
        table.setBackground(F1Colors.BG_CARD);
        table.setSelectionBackground(F1Colors.F1_RED_DARK);
        table.setGridColor(F1Colors.BORDER);
        table.setRowHeight(34);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(F1Fonts.TABLE_HEADER);
        table.getTableHeader().setForeground(F1Colors.TEXT_WHITE);
        table.getTableHeader().setBackground(F1Colors.BG_HEADER);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, F1Colors.F1_RED));

        // Renderer para posiciones
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? F1Colors.BG_CARD : F1Colors.BG_TABLE_ALT);
                    c.setForeground(F1Colors.TEXT_PRIMARY);
                    if (column == 0) {
                        if (row == 0) c.setForeground(F1Colors.PODIO_GOLD);
                        else if (row == 1) c.setForeground(F1Colors.PODIO_SILVER);
                        else if (row == 2) c.setForeground(F1Colors.PODIO_BRONZE);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(F1Colors.BORDER, 1));
        scrollPane.getViewport().setBackground(F1Colors.BG_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JLabel createStatItem(JPanel parent, String title) {
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(F1Fonts.BODY_SMALL);
        titleLbl.setForeground(F1Colors.TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(titleLbl);

        JLabel valueLbl = new JLabel("---");
        valueLbl.setFont(F1Fonts.BODY_BOLD);
        valueLbl.setForeground(F1Colors.TEXT_WHITE);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 12, 0));
        parent.add(valueLbl);

        return valueLbl;
    }

    /**
     * Muestra los resultados finales de la carrera.
     */
    public void mostrarResultados(List<ResultadoClasificacion> resultados) {
        if (resultados == null || resultados.isEmpty()) return;

        // Podio top 3
        if (resultados.size() >= 3) {
            podiumPanel.setResultados(
                    resultados.get(0).getPiloto().getNombre(),
                    resultados.get(0).getEquipo(),
                    resultados.get(0).getTiempoFormateado(),
                    resultados.get(1).getPiloto().getNombre(),
                    resultados.get(1).getEquipo(),
                    resultados.get(1).getTiempoFormateado(),
                    resultados.get(2).getPiloto().getNombre(),
                    resultados.get(2).getEquipo(),
                    resultados.get(2).getTiempoFormateado());
        }

        // Estadísticas
        lblTiempoGanador.setText(resultados.get(0).getTiempoFormateado());

        double mejorVuelta = resultados.stream()
                .mapToDouble(ResultadoClasificacion::getMejorVuelta)
                .filter(v -> v > 0)
                .min().orElse(0);
        lblMejorVuelta.setText(EstadoCarrera.formatearTiempo(mejorVuelta));

        int totalPits = resultados.stream().mapToInt(ResultadoClasificacion::getPitStops).sum();
        lblTotalPits.setText(String.valueOf(totalPits));

        // Tabla completa
        tableModel.setRowCount(0);
        for (ResultadoClasificacion r : resultados) {
            tableModel.addRow(new Object[]{
                    "P" + r.getPosicion(),
                    r.getPiloto().getNombre(),
                    r.getEquipo(),
                    r.getTiempoFormateado(),
                    r.getDiferencia(),
                    r.getPitStops(),
                    EstadoCarrera.formatearTiempo(r.getMejorVuelta()),
                    r.getEstadoFinal()
            });
        }
    }
}
