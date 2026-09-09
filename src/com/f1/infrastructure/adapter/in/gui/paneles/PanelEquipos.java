package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.out.persistence.DataManager;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Table;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.domain.model.Equipo;
import com.f1.domain.model.Piloto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel CRUD completo para la gestiÃ³n de equipos.
 */
public class PanelEquipos extends JPanel {

    private F1Table table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PanelEquipos() {
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("ðŸ¢ GestiÃ³n de Equipos");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        searchField = new JTextField(20);
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
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un equipo."); return; }
            String nombre = (String) tableModel.getValueAt(row, 0);
            mostrarDialogo(DataManager.getInstance().obtenerEquipo(nombre));
        });
        toolbar.add(btnEditar);

        F1Button btnEliminar = new F1Button("ðŸ—‘ï¸ Eliminar", F1Button.Style.DANGER);
        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un equipo."); return; }
            String nombre = (String) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Â¿Eliminar " + nombre + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                DataManager.getInstance().eliminarEquipo(nombre);
                refresh();
            }
        });
        toolbar.add(btnEliminar);

        headerPanel.add(toolbar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Nombre", "PaÃ­s", "Motor", "Pilotos"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new F1Table(tableModel);
        add(table.wrapInScrollPane(), BorderLayout.CENTER);
    }

    public void refresh() {
        cargarDatos(DataManager.getInstance().listarEquipos());
    }

    private void cargarDatos(List<Equipo> equipos) {
        tableModel.setRowCount(0);
        DataManager dm = DataManager.getInstance();
        for (Equipo e : equipos) {
            String pilotos = e.getPilotoIds().stream()
                    .map(id -> {
                        Piloto p = dm.obtenerPiloto(id);
                        return p != null ? p.getNombre() : "ID:" + id;
                    })
                    .collect(Collectors.joining(", "));
            tableModel.addRow(new Object[]{e.getNombre(), e.getPais(), e.getMotor(), pilotos});
        }
    }

    private void filtrar() {
        String t = searchField.getText().trim();
        cargarDatos(t.isEmpty() ? DataManager.getInstance().listarEquipos()
                : DataManager.getInstance().buscarEquipos(t));
    }

    private void mostrarDialogo(Equipo existente) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existente == null ? "Agregar Equipo" : "Editar Equipo", true);
        dialog.setSize(400, 300);
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
        JTextField txtMotor = addField(form, gbc, "Motor:", 2);

        if (existente != null) {
            txtNombre.setText(existente.getNombre());
            txtPais.setText(existente.getPais());
            txtMotor.setText(existente.getMotor());
        }

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        F1Button btnCancel = new F1Button("Cancelar", F1Button.Style.SECONDARY);
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        F1Button btnSave = new F1Button("Guardar", F1Button.Style.SUCCESS);
        btnSave.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Nombre requerido."); return; }
            Equipo eq = new Equipo(nombre, txtPais.getText().trim(), txtMotor.getText().trim(),
                    existente != null ? existente.getPilotoIds() : Arrays.asList(), "");
            if (existente != null) {
                DataManager.getInstance().editarEquipo(existente.getNombre(), eq);
            } else {
                DataManager.getInstance().agregarEquipo(eq);
            }
            refresh();
            dialog.dispose();
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
