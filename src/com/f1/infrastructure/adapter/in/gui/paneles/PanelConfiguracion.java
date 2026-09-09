package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.out.persistence.DataManager;
import com.f1.infrastructure.adapter.in.gui.MainFrame;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.domain.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de configuraciÃ³n del vehÃ­culo antes de iniciar la simulaciÃ³n.
 */
public class PanelConfiguracion extends JPanel {

    private final MainFrame mainFrame;
    private JComboBox<String> cmbCircuito;
    private JComboBox<String> cmbVehiculo;
    private JComboBox<String> cmbModoConduccion;
    private JComboBox<String> cmbCargaAero;
    private JComboBox<String> cmbPresionNeum;
    private JComboBox<String> cmbEstrategiaComb;
    private JLabel lblPreview;

    public PanelConfiguracion(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("âš™ï¸ ConfiguraciÃ³n de Carrera");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Columna izquierda: selecciÃ³n
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(F1Colors.BG_CARD);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        addSectionTitle(leftPanel, "SelecciÃ³n de Circuito y VehÃ­culo");

        List<String> circuitos = DataManager.getInstance().obtenerNombresCircuitos();
        cmbCircuito = addComboField(leftPanel, "Circuito:", circuitos.toArray(new String[0]));

        List<Vehiculo> vehiculos = DataManager.getInstance().listarVehiculos();
        String[] vehiculoNames = vehiculos.stream()
                .map(v -> v.getEquipo() + " " + v.getModelo())
                .toArray(String[]::new);
        cmbVehiculo = addComboField(leftPanel, "VehÃ­culo:", vehiculoNames);

        leftPanel.add(Box.createVerticalStrut(20));
        addSectionTitle(leftPanel, "ConfiguraciÃ³n del VehÃ­culo");

        cmbModoConduccion = addComboField(leftPanel, "Modo de ConducciÃ³n:",
                new String[]{"Normal", "Agresivo", "Ahorro"});
        cmbCargaAero = addComboField(leftPanel, "Carga AerodinÃ¡mica:",
                new String[]{"Baja", "Media", "Alta"});
        cmbPresionNeum = addComboField(leftPanel, "PresiÃ³n NeumÃ¡ticos:",
                new String[]{"Baja", "EstÃ¡ndar", "Alta"});
        cmbEstrategiaComb = addComboField(leftPanel, "Estrategia Combustible:",
                new String[]{"Agresiva", "Balanceada", "Ahorro"});

        contentPanel.add(leftPanel);

        // Columna derecha: preview y botÃ³n
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(F1Colors.BG_CARD);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel previewTitle = new JLabel("ðŸ“Š Preview de Rendimiento");
        previewTitle.setFont(F1Fonts.SUBTITLE);
        previewTitle.setForeground(F1Colors.TEXT_WHITE);
        rightPanel.add(previewTitle, BorderLayout.NORTH);

        lblPreview = new JLabel("<html><body style='color:#a0a0b4;font-size:12px;'>" +
                "<p>Selecciona un circuito y vehÃ­culo para ver el rendimiento estimado.</p></body></html>");
        lblPreview.setFont(F1Fonts.BODY);
        lblPreview.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        rightPanel.add(lblPreview, BorderLayout.CENTER);

        // Botones de acciÃ³n
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setOpaque(false);

        F1Button btnPreview = new F1Button("ðŸ”„ Actualizar Preview", F1Button.Style.SECONDARY);
        btnPreview.setPreferredSize(new Dimension(200, 40));
        btnPreview.addActionListener(e -> actualizarPreview());
        actionPanel.add(btnPreview);

        F1Button btnIniciar = new F1Button("â–¶ï¸ INICIAR SIMULACIÃ“N", F1Button.Style.PRIMARY);
        btnIniciar.setPreferredSize(new Dimension(250, 45));
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        actionPanel.add(btnIniciar);

        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        contentPanel.add(rightPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Listeners para auto-actualizar preview
        cmbCircuito.addActionListener(e -> actualizarPreview());
        cmbVehiculo.addActionListener(e -> actualizarPreview());
        cmbModoConduccion.addActionListener(e -> actualizarPreview());
        cmbCargaAero.addActionListener(e -> actualizarPreview());
        cmbPresionNeum.addActionListener(e -> actualizarPreview());
        cmbEstrategiaComb.addActionListener(e -> actualizarPreview());
    }

    private void addSectionTitle(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(F1Fonts.SECTION_TITLE);
        label.setForeground(F1Colors.F1_RED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(label);
    }

    private JComboBox<String> addComboField(JPanel panel, String labelText, String[] items) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(F1Fonts.BODY);
        label.setForeground(F1Colors.TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(170, 30));
        row.add(label, BorderLayout.WEST);

        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(F1Fonts.INPUT);
        combo.setBackground(F1Colors.BG_INPUT);
        combo.setForeground(F1Colors.TEXT_PRIMARY);
        row.add(combo, BorderLayout.CENTER);

        panel.add(row);
        return combo;
    }

    private ConfiguracionVehiculo crearConfiguracion() {
        ConfiguracionVehiculo.ModoCond modo = switch (cmbModoConduccion.getSelectedIndex()) {
            case 1 -> ConfiguracionVehiculo.ModoCond.AGRESIVO;
            case 2 -> ConfiguracionVehiculo.ModoCond.AHORRO;
            default -> ConfiguracionVehiculo.ModoCond.NORMAL;
        };
        ConfiguracionVehiculo.CargaAero aero = switch (cmbCargaAero.getSelectedIndex()) {
            case 0 -> ConfiguracionVehiculo.CargaAero.BAJA;
            case 2 -> ConfiguracionVehiculo.CargaAero.ALTA;
            default -> ConfiguracionVehiculo.CargaAero.MEDIA;
        };
        ConfiguracionVehiculo.PresionNeum presion = switch (cmbPresionNeum.getSelectedIndex()) {
            case 0 -> ConfiguracionVehiculo.PresionNeum.BAJA;
            case 2 -> ConfiguracionVehiculo.PresionNeum.ALTA;
            default -> ConfiguracionVehiculo.PresionNeum.ESTANDAR;
        };
        ConfiguracionVehiculo.EstrategiaComb estrategia = switch (cmbEstrategiaComb.getSelectedIndex()) {
            case 0 -> ConfiguracionVehiculo.EstrategiaComb.AGRESIVA;
            case 2 -> ConfiguracionVehiculo.EstrategiaComb.AHORRO;
            default -> ConfiguracionVehiculo.EstrategiaComb.BALANCEADA;
        };
        return new ConfiguracionVehiculo(modo, aero, presion, estrategia);
    }

    private void actualizarPreview() {
        ConfiguracionVehiculo config = crearConfiguracion();
        String html = String.format(
                "<html><body style='color:#e6e6f0;font-size:12px;'>" +
                "<p><b>Factor de Rendimiento:</b> %.3f</p>" +
                "<p><b>Factor de Desgaste:</b> %.3f</p>" +
                "<p><b>Factor de Consumo:</b> %.3f</p>" +
                "<br><p style='color:#a0a0b4;'>Factor < 1.0 = mÃ¡s rÃ¡pido/menos consumo</p>" +
                "<p style='color:#a0a0b4;'>Factor > 1.0 = mÃ¡s lento/mÃ¡s consumo</p>" +
                "</body></html>",
                config.calcularFactorRendimiento(),
                config.calcularFactorDesgaste(),
                config.calcularFactorConsumo());
        lblPreview.setText(html);
    }

    private void iniciarSimulacion() {
        if (cmbCircuito.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un circuito.");
            return;
        }

        String circuitoNombre = (String) cmbCircuito.getSelectedItem();
        Circuito circuito = DataManager.getInstance().obtenerCircuito(circuitoNombre);
        ConfiguracionVehiculo config = crearConfiguracion();

        if (circuito == null) {
            JOptionPane.showMessageDialog(this, "Circuito no encontrado.");
            return;
        }

        // Ir al panel de simulaciÃ³n y comenzar
        mainFrame.getPanelSimulacion().iniciarSimulacion(circuito, config);
        mainFrame.selectPanel("SIMULACION");
    }
}
