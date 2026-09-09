package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.out.persistence.DataManager;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Table;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.domain.model.Circuito;
import com.f1.domain.model.GanadorHistorico;
import com.f1.domain.model.Piloto;
import com.f1.domain.model.RecordVuelta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel CRUD para circuitos con historial de ganadores.
 */
public class PanelCircuitos extends JPanel {

    private F1Table table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PanelCircuitos() {
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("ðŸ›£ï¸ GestiÃ³n de Circuitos");
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

        F1Button btnEditar = new F1Button("âœï¸ Editar", F1Button.Style.SECONDARY);
        btnEditar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un circuito."); return; }
            String nombre = (String) tableModel.getValueAt(row, 0);
            mostrarDialogo(DataManager.getInstance().obtenerCircuito(nombre));
        });
        toolbar.add(btnEditar);

        F1Button btnEliminar = new F1Button("ðŸ—‘ï¸ Eliminar", F1Button.Style.DANGER);
        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un circuito."); return; }
            String nombre = (String) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Â¿Eliminar " + nombre + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                DataManager.getInstance().eliminarCircuito(nombre);
                refresh();
            }
        });
        toolbar.add(btnEliminar);

        F1Button btnDetalle = new F1Button("ðŸ“‹ Detalle", F1Button.Style.SECONDARY);
        btnDetalle.addActionListener(e -> verDetalle());
        toolbar.add(btnDetalle);

        headerPanel.add(toolbar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Nombre", "PaÃ­s", "Longitud (km)", "Vueltas", "Distancia Total", "RÃ©cord"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new F1Table(tableModel);
        add(table.wrapInScrollPane(), BorderLayout.CENTER);
    }

    public void refresh() {
        cargarDatos(DataManager.getInstance().listarCircuitos());
    }

    private void cargarDatos(List<Circuito> circuitos) {
        tableModel.setRowCount(0);
        for (Circuito c : circuitos) {
            tableModel.addRow(new Object[]{
                    c.getNombre(), c.getPais(),
                    String.format("%.2f", c.getLongitudKm()),
                    c.getVueltas(),
                    String.format("%.1f km", c.getDistanciaTotal()),
                    c.getRecord() != null ? c.getRecord().toString() : "N/A"
            });
        }
    }

    private void filtrar() {
        String t = searchField.getText().trim();
        cargarDatos(t.isEmpty() ? DataManager.getInstance().listarCircuitos()
                : DataManager.getInstance().buscarCircuitos(t));
    }

    private void verDetalle() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un circuito."); return; }
        String nombre = (String) tableModel.getValueAt(row, 0);
        Circuito c = DataManager.getInstance().obtenerCircuito(nombre);
        if (c == null) return;

        DataManager dm = DataManager.getInstance();
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif;padding:10px;'>");
        html.append("<h2>").append(c.getNombre()).append("</h2>");
        html.append("<p><b>PaÃ­s:</b> ").append(c.getPais()).append("</p>");
        html.append("<p><b>Longitud:</b> ").append(String.format("%.2f km", c.getLongitudKm())).append("</p>");
        html.append("<p><b>Vueltas:</b> ").append(c.getVueltas()).append("</p>");
        html.append("<p><b>Distancia total:</b> ").append(String.format("%.1f km", c.getDistanciaTotal())).append("</p>");
        html.append("<p><b>DescripciÃ³n:</b> ").append(c.getDescripcion()).append("</p>");

        if (c.getRecord() != null) {
            html.append("<h3>RÃ©cord de Vuelta</h3>");
            html.append("<p>").append(c.getRecord().getTiempo())
                    .append(" por ").append(c.getRecord().getPiloto())
                    .append(" (").append(c.getRecord().getAnio()).append(")</p>");
        }

        if (!c.getGanadores().isEmpty()) {
            html.append("<h3>Historial de Ganadores</h3><table border='1' cellpadding='4'>");
            html.append("<tr><th>Temporada</th><th>Ganador</th></tr>");
            for (GanadorHistorico g : c.getGanadores()) {
                Piloto p = dm.obtenerPiloto(g.getPilotoId());
                html.append("<tr><td>").append(g.getTemporada()).append("</td><td>")
                        .append(p != null ? p.getNombre() : "ID:" + g.getPilotoId())
                        .append("</td></tr>");
            }
            html.append("</table>");
        }

        html.append("</body></html>");
        JOptionPane.showMessageDialog(this, new JLabel(html.toString()),
                "Detalle: " + c.getNombre(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDialogo(Circuito existente) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existente == null ? "Agregar Circuito" : "Editar Circuito", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(F1Colors.BG_CARD);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(F1Colors.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtNombre = addField(form, gbc, "Nombre:", 0);
        JTextField txtPais = addField(form, gbc, "PaÃ­s:", 1);
        JTextField txtLongitud = addField(form, gbc, "Longitud (km):", 2);
        JTextField txtVueltas = addField(form, gbc, "Vueltas:", 3);
        JTextField txtDescripcion = addField(form, gbc, "DescripciÃ³n:", 4);

        if (existente != null) {
            txtNombre.setText(existente.getNombre());
            txtPais.setText(existente.getPais());
            txtLongitud.setText(String.valueOf(existente.getLongitudKm()));
            txtVueltas.setText(String.valueOf(existente.getVueltas()));
            txtDescripcion.setText(existente.getDescripcion());
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
                String nom = txtNombre.getText().trim();
                if (nom.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Nombre requerido."); return; }
                Circuito c = new Circuito(nom, txtPais.getText().trim(),
                        Double.parseDouble(txtLongitud.getText().trim()),
                        Integer.parseInt(txtVueltas.getText().trim()),
                        txtDescripcion.getText().trim(),
                        existente != null ? existente.getRecord() : null,
                        existente != null ? existente.getGanadores() : new ArrayList<>(), "");
                if (existente != null) {
                    DataManager.getInstance().editarCircuito(existente.getNombre(), c);
                } else {
                    DataManager.getInstance().agregarCircuito(c);
                }
                refresh();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valores numÃ©ricos invÃ¡lidos.");
            }
        });
        btnPanel.add(btnSave);
        form.add(btnPanel, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(F1Fonts.BODY);
        lbl.setForeground(F1Colors.TEXT_PRIMARY);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
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
