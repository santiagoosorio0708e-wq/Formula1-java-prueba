package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.in.gui.MainFrame;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.domain.model.*;
import com.f1.infrastructure.adapter.out.simulacion.MotorSimulacion;
import com.f1.infrastructure.adapter.out.simulacion.SimulacionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Panel de simulación en tiempo real con tabla de posiciones,
 * indicadores de combustible/neumáticos y alertas de pit stop.
 */
public class PanelSimulacion extends JPanel implements SimulacionListener {

    private final MainFrame mainFrame;
    private MotorSimulacion motor;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblCircuito, lblVuelta, lblClima, lblEstado;
    private JPanel alertPanel;
    private JTextArea alertArea;
    private F1Button btnIniciar, btnPausar, btnDetener;

    public PanelSimulacion(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // === HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        infoPanel.setOpaque(false);

        lblCircuito = createInfoLabel(infoPanel, "ðŸ›£ï¸ Circuito", "Sin seleccionar");
        lblVuelta = createInfoLabel(infoPanel, " Vuelta", "0 / 0");
        lblClima = createInfoLabel(infoPanel, "ðŸŒ¤ï¸ Clima", "---");
        lblEstado = createInfoLabel(infoPanel, " Estado", "Esperando...");

        headerPanel.add(infoPanel, BorderLayout.CENTER);

        // Botones de control
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlPanel.setOpaque(false);

        btnIniciar = new F1Button("â–¶ï¸ Iniciar", F1Button.Style.SUCCESS);
        btnIniciar.addActionListener(e -> {
            if (motor != null && !motor.isEnCurso()) {
                motor.iniciarCarrera();
                btnIniciar.setEnabled(false);
                lblEstado.setText("ðŸŸ¢ EN CURSO");
            }
        });
        controlPanel.add(btnIniciar);

        btnPausar = new F1Button("â¸ï¸ Pausar", F1Button.Style.SECONDARY);
        btnPausar.addActionListener(e -> {
            if (motor != null && motor.isEnCurso()) {
                motor.togglePausa();
                btnPausar.setText(motor.isPausado() ? "â–¶ï¸ Reanudar" : "â¸ï¸ Pausar");
                lblEstado.setText(motor.isPausado() ? "â¸ï¸ PAUSADO" : "ðŸŸ¢ EN CURSO");
            }
        });
        controlPanel.add(btnPausar);

        btnDetener = new F1Button("â¹ï¸ Detener", F1Button.Style.DANGER);
        btnDetener.addActionListener(e -> {
            if (motor != null) {
                motor.detenerCarrera();
                lblEstado.setText("ðŸ”´ DETENIDO");
                btnIniciar.setEnabled(true);
            }
        });
        controlPanel.add(btnDetener);

        headerPanel.add(controlPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // === TABLA DE POSICIONES ===
        String[] columns = {"Pos", "Piloto", "Equipo", "Último Tiempo", "Tiempo Total",
                "Gap", "Combustible", "Neumáticos", "Pits", "Estado"};
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
        table.setRowHeight(32);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        // Header
        table.getTableHeader().setFont(F1Fonts.TABLE_HEADER);
        table.getTableHeader().setForeground(F1Colors.TEXT_WHITE);
        table.getTableHeader().setBackground(F1Colors.BG_HEADER);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, F1Colors.F1_RED));

        // Custom renderer para celdas con colores
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? F1Colors.BG_CARD : F1Colors.BG_TABLE_ALT);
                    c.setForeground(F1Colors.TEXT_PRIMARY);

                    String val = value != null ? value.toString() : "";
                    // Colorear combustible y neumáticos
                    if (column == 6 || column == 7) { // Combustible / Neumáticos
                        try {
                            String numStr = val.replace("%", "").trim();
                            double num = Double.parseDouble(numStr);
                            if (column == 6) { // Combustible
                                if (num <= 20) c.setForeground(F1Colors.STATUS_DANGER);
                                else if (num <= 50) c.setForeground(F1Colors.STATUS_WARNING);
                                else c.setForeground(F1Colors.STATUS_OK);
                            } else { // Neumáticos (invertido)
                                if (num >= 80) c.setForeground(F1Colors.STATUS_DANGER);
                                else if (num >= 50) c.setForeground(F1Colors.STATUS_WARNING);
                                else c.setForeground(F1Colors.STATUS_OK);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                    // Colorear posición
                    if (column == 0) {
                        if (row == 0) c.setForeground(F1Colors.PODIO_GOLD);
                        else if (row == 1) c.setForeground(F1Colors.PODIO_SILVER);
                        else if (row == 2) c.setForeground(F1Colors.PODIO_BRONZE);
                    }
                    // Colorear estado
                    if (column == 9 && val.contains("PIT")) {
                        c.setForeground(F1Colors.STATUS_WARNING);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Anchos de columna
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(40);
        table.getColumnModel().getColumn(9).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(F1Colors.BORDER, 1));
        scrollPane.getViewport().setBackground(F1Colors.BG_CARD);
        add(scrollPane, BorderLayout.CENTER);

        // === PANEL DE ALERTAS ===
        alertPanel = new JPanel(new BorderLayout());
        alertPanel.setBackground(F1Colors.BG_CARD);
        alertPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        alertPanel.setPreferredSize(new Dimension(0, 120));

        JLabel alertTitle = new JLabel("ðŸ“¢ Alertas de Carrera");
        alertTitle.setFont(F1Fonts.SECTION_TITLE);
        alertTitle.setForeground(F1Colors.ACCENT_YELLOW);
        alertPanel.add(alertTitle, BorderLayout.NORTH);

        alertArea = new JTextArea();
        alertArea.setFont(F1Fonts.BODY_SMALL);
        alertArea.setBackground(F1Colors.BG_CARD);
        alertArea.setForeground(F1Colors.TEXT_SECONDARY);
        alertArea.setEditable(false);
        alertArea.setLineWrap(true);
        JScrollPane alertScroll = new JScrollPane(alertArea);
        alertScroll.setBorder(null);
        alertScroll.getViewport().setBackground(F1Colors.BG_CARD);
        alertPanel.add(alertScroll, BorderLayout.CENTER);

        add(alertPanel, BorderLayout.SOUTH);
    }

    private JLabel createInfoLabel(JPanel parent, String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(F1Colors.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(F1Fonts.BODY_SMALL);
        titleLbl.setForeground(F1Colors.TEXT_SECONDARY);
        card.add(titleLbl, BorderLayout.NORTH);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(F1Fonts.SUBTITLE);
        valueLbl.setForeground(F1Colors.TEXT_WHITE);
        card.add(valueLbl, BorderLayout.CENTER);

        parent.add(card);
        return valueLbl;
    }

    /**
     * Inicia una nueva simulación con el circuito y configuración dados.
     */
    public void iniciarSimulacion(Circuito circuito, ConfiguracionVehiculo config) {
        // Detener simulación anterior si existe
        if (motor != null && motor.isEnCurso()) {
            motor.detenerCarrera();
        }

        // Crear nuevo motor
        motor = new MotorSimulacion(circuito, config, this);

        // Actualizar UI
        lblCircuito.setText(circuito.getNombre());
        lblVuelta.setText("0 / " + motor.getTotalVueltas());
        lblEstado.setText("â³ LISTO");
        alertArea.setText("");
        tableModel.setRowCount(0);
        btnIniciar.setEnabled(true);
        btnPausar.setText("â¸ï¸ Pausar");

        // Cargar tabla inicial
        for (EstadoCarrera estado : motor.getEstados()) {
            tableModel.addRow(new Object[]{
                    "-", estado.getPiloto().getNombre(), estado.getPiloto().getEquipo(),
                    "--:--.---", "--:--.---", "---",
                    "100%", "0%", "0", "Esperando"
            });
        }

        agregarAlerta("ðŸ Carrera configurada en " + circuito.getNombre() +
                " (" + motor.getTotalVueltas() + " vueltas). Presiona 'Iniciar' para comenzar.");
    }

    private void agregarAlerta(String mensaje) {
        alertArea.append(mensaje + "\n");
        alertArea.setCaretPosition(alertArea.getDocument().getLength());
    }

    // ==================== SimulacionListener ====================

    @Override
    public void onVueltaCompletada(EstadoCarrera estado, int vuelta) {
        lblVuelta.setText(vuelta + " / " + motor.getTotalVueltas());
    }

    @Override
    public void onPitStop(EstadoCarrera estado) {
        agregarAlerta("ðŸ”§ PIT STOP: " + estado.getPiloto().getNombre() +
                " (" + estado.getPiloto().getEquipo() + ") - Vuelta " + estado.getVueltaActual());
    }

    @Override
    public void onCarreraFinalizada(List<ResultadoClasificacion> resultados) {
        lblEstado.setText("ðŸ FINALIZADA");
        btnIniciar.setEnabled(true);
        agregarAlerta("ðŸ Â¡CARRERA FINALIZADA! Ganador: " +
                (resultados.isEmpty() ? "N/A" : resultados.get(0).getPiloto().getNombre()));

        // Actualizar podio
        mainFrame.getPanelPodio().mostrarResultados(resultados);
    }

    @Override
    public void onCambioClima(CondicionClimatica nuevoClima) {
        lblClima.setText(nuevoClima.getIcono() + " " + nuevoClima.getNombre());
        lblClima.setForeground(F1Colors.getClimaColor(nuevoClima.name()));
        agregarAlerta("ðŸŒ¤ï¸ Cambio de clima: " + nuevoClima.getIcono() + " " + nuevoClima.getNombre() +
                " (Factor velocidad: x" + String.format("%.2f", nuevoClima.getFactorVelocidad()) + ")");
    }

    @Override
    public void onRetiro(EstadoCarrera estado, String motivo) {
        agregarAlerta("âŒ RETIRO: " + estado.getPiloto().getNombre() + " - " + motivo);
    }

    @Override
    public void onActualizacionPosiciones(List<EstadoCarrera> estados) {
        // Ordenar por tiempo total
        estados.sort((a, b) -> Double.compare(a.getTiempoTotal(), b.getTiempoTotal()));
        double tiempoLider = estados.isEmpty() ? 0 : estados.get(0).getTiempoTotal();

        tableModel.setRowCount(0);
        for (int i = 0; i < estados.size(); i++) {
            EstadoCarrera e = estados.get(i);
            String gap = i == 0 ? "LÃDER"
                    : "+" + String.format("%.3f", e.getTiempoTotal() - tiempoLider);

            tableModel.addRow(new Object[]{
                    "P" + (i + 1),
                    e.getPiloto().getNombre(),
                    e.getPiloto().getEquipo(),
                    EstadoCarrera.formatearTiempo(e.getUltimoTiempo()),
                    EstadoCarrera.formatearTiempo(e.getTiempoTotal()),
                    gap,
                    String.format("%.0f", e.getCombustibleActual()),
                    String.format("%.0f", e.getDesgasteNeumaticos()),
                    String.valueOf(e.getNumeroPitStops()),
                    e.getEstadoTexto()
            });
        }
    }
}
