package com.f1.gui.paneles;

import com.f1.datos.DataManager;
import com.f1.gui.componentes.F1Button;
import com.f1.gui.componentes.F1Table;
import com.f1.gui.util.F1Colors;
import com.f1.gui.util.F1Fonts;
import com.f1.modelo.ModoConduccion;
import com.f1.modelo.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel CRUD para vehículos con función de comparación.
 */
public class PanelVehiculos extends JPanel {

    private F1Table table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PanelVehiculos() {
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("🏎️ Gestión de Vehículos");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        searchField = new JTextField(18);
        searchField.setFont(F1Fonts.INPUT);
        searchField.setBackground(F1Colors.BG_INPUT);
        searchField.setForeground(F1Colors.TEXT_PRIMARY);
        searchField.setCaretColor(F1Colors.TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { filtrar(); }
        });
        toolbar.add(searchField);

        F1Button btnAgregar = new F1Button("+ Agregar", F1Button.Style.PRIMARY);
        btnAgregar.addActionListener(e -> mostrarDialogo(null));
        toolbar.add(btnAgregar);

        F1Button btnEditar = new F1Button("✏️ Editar", F1Button.Style.SECONDARY);
        btnEditar.addActionListener(e -> editarSeleccionado());
        toolbar.add(btnEditar);

        F1Button btnEliminar = new F1Button("🗑️ Eliminar", F1Button.Style.DANGER);
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        toolbar.add(btnEliminar);

        F1Button btnComparar = new F1Button("📊 Comparar", F1Button.Style.SECONDARY);
        btnComparar.addActionListener(e -> compararVehiculos());
        toolbar.add(btnComparar);

        headerPanel.add(toolbar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Equipo", "Modelo", "Motor", "Vel. Máx (km/h)", "0-100 (s)", "Vel. Normal", "Vel. Agresiva"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new F1Table(tableModel);
        add(table.wrapInScrollPane(), BorderLayout.CENTER);
    }

    public void refresh() {
        cargarDatos(DataManager.getInstance().listarVehiculos());
    }

    private void cargarDatos(List<Vehiculo> vehiculos) {
        tableModel.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            ModoConduccion normal = v.getModoConduccion("conduccion_normal");
            ModoConduccion agresivo = v.getModoConduccion("conduccion_agresiva");
            tableModel.addRow(new Object[]{
                    v.getEquipo(), v.getModelo(), v.getMotor(),
                    String.format("%.0f", v.getVelocidadMaximaKmh()),
                    String.format("%.1f", v.getAceleracion0a100()),
                    normal != null ? String.format("%.0f km/h", normal.getVelocidadPromedioKmh()) : "N/A",
                    agresivo != null ? String.format("%.0f km/h", agresivo.getVelocidadPromedioKmh()) : "N/A"
            });
        }
    }

    private void filtrar() {
        String t = searchField.getText().trim();
        cargarDatos(t.isEmpty() ? DataManager.getInstance().listarVehiculos()
                : DataManager.getInstance().buscarVehiculos(t));
    }

    private void editarSeleccionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un vehículo."); return; }
        String equipo = (String) tableModel.getValueAt(row, 0);
        String modelo = (String) tableModel.getValueAt(row, 1);
        mostrarDialogo(DataManager.getInstance().obtenerVehiculo(equipo, modelo));
    }

    private void eliminarSeleccionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un vehículo."); return; }
        String equipo = (String) tableModel.getValueAt(row, 0);
        String modelo = (String) tableModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar " + equipo + " " + modelo + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DataManager.getInstance().eliminarVehiculo(equipo, modelo);
            refresh();
        }
    }

    private void mostrarDialogo(Vehiculo existente) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existente == null ? "Agregar Vehículo" : "Editar Vehículo", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(F1Colors.BG_CARD);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(F1Colors.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtEquipo = addField(form, gbc, "Equipo:", 0);
        JTextField txtModelo = addField(form, gbc, "Modelo:", 1);
        JTextField txtMotor = addField(form, gbc, "Motor:", 2);
        JTextField txtVelMax = addField(form, gbc, "Vel. Máx (km/h):", 3);
        JTextField txtAcel = addField(form, gbc, "0-100 (s):", 4);

        if (existente != null) {
            txtEquipo.setText(existente.getEquipo());
            txtModelo.setText(existente.getModelo());
            txtMotor.setText(existente.getMotor());
            txtVelMax.setText(String.valueOf(existente.getVelocidadMaximaKmh()));
            txtAcel.setText(String.valueOf(existente.getAceleracion0a100()));
        }

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        F1Button btnCancel = new F1Button("Cancelar", F1Button.Style.SECONDARY);
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        F1Button btnSave = new F1Button("Guardar", F1Button.Style.SUCCESS);
        btnSave.addActionListener(e -> {
            try {
                String equipo = txtEquipo.getText().trim();
                String modelo = txtModelo.getText().trim();
                if (equipo.isEmpty() || modelo.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Equipo y modelo requeridos.");
                    return;
                }
                Vehiculo v = new Vehiculo(equipo, modelo, txtMotor.getText().trim(),
                        Double.parseDouble(txtVelMax.getText().trim()),
                        Double.parseDouble(txtAcel.getText().trim()),
                        existente != null ? existente.getPilotoIds() : List.of(),
                        existente != null ? existente.getRendimiento() : Map.of(), "");
                if (existente != null) {
                    DataManager.getInstance().editarVehiculo(existente.getEquipo(), existente.getModelo(), v);
                } else {
                    DataManager.getInstance().agregarVehiculo(v);
                }
                refresh();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valores numéricos inválidos.");
            }
        });
        btnPanel.add(btnSave);
        form.add(btnPanel, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private void compararVehiculos() {
        int[] rows = table.getSelectedRows();
        if (rows.length < 2) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona al menos 2 vehículos para comparar.\n(Usa Ctrl+Click para selección múltiple)");
            return;
        }

        DataManager dm = DataManager.getInstance();
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif;padding:10px;'>");
        html.append("<h2>Comparación de Vehículos</h2><table border='1' cellpadding='6'>");
        html.append("<tr><th>Atributo</th>");

        Vehiculo[] vehiculos = new Vehiculo[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String equipo = (String) tableModel.getValueAt(rows[i], 0);
            String modelo = (String) tableModel.getValueAt(rows[i], 1);
            vehiculos[i] = dm.obtenerVehiculo(equipo, modelo);
            html.append("<th>").append(equipo).append("<br>").append(modelo).append("</th>");
        }
        html.append("</tr>");

        // Vel máxima
        html.append("<tr><td>Vel. Máxima</td>");
        for (Vehiculo v : vehiculos) html.append("<td>").append(String.format("%.0f km/h", v.getVelocidadMaximaKmh())).append("</td>");
        html.append("</tr>");

        // 0-100
        html.append("<tr><td>0-100 km/h</td>");
        for (Vehiculo v : vehiculos) html.append("<td>").append(String.format("%.1f s", v.getAceleracion0a100())).append("</td>");
        html.append("</tr>");

        // Consumo normal seco
        html.append("<tr><td>Consumo (Normal/Seco)</td>");
        for (Vehiculo v : vehiculos) {
            ModoConduccion m = v.getModoConduccion("conduccion_normal");
            html.append("<td>").append(m != null ? String.format("%.1f", m.getConsumo("seco")) : "N/A").append("</td>");
        }
        html.append("</tr>");

        // Desgaste normal seco
        html.append("<tr><td>Desgaste (Normal/Seco)</td>");
        for (Vehiculo v : vehiculos) {
            ModoConduccion m = v.getModoConduccion("conduccion_normal");
            html.append("<td>").append(m != null ? String.format("%.1f", m.getDesgaste("seco")) : "N/A").append("</td>");
        }
        html.append("</tr>");

        html.append("</table></body></html>");

        JOptionPane.showMessageDialog(this, new JLabel(html.toString()),
                "Comparación", JOptionPane.INFORMATION_MESSAGE);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(F1Fonts.BODY);
        lbl.setForeground(F1Colors.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        JTextField field = new JTextField();
        field.setFont(F1Fonts.INPUT);
        field.setBackground(F1Colors.BG_INPUT);
        field.setForeground(F1Colors.TEXT_PRIMARY);
        field.setCaretColor(F1Colors.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(field, gbc);
        return field;
    }
}
