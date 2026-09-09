package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.infrastructure.adapter.out.persistence.DataManager;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Table;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.domain.model.Piloto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD completo para la gestión de pilotos.
 */
public class PanelPilotos extends JPanel {

    private F1Table table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PanelPilotos() {
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

        JLabel titleLabel = new JLabel("> GESTIÓN DE PILOTOS _");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Toolbar
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
        searchField.putClientProperty("JTextField.placeholderText", "Buscar piloto...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarPilotos();
            }
        });
        toolbar.add(searchField);

        F1Button btnAgregar = new F1Button("[+ AGREGAR]", F1Button.Style.PRIMARY);
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());
        toolbar.add(btnAgregar);

        F1Button btnEditar = new F1Button("[ EDITAR ]", F1Button.Style.SECONDARY);
        btnEditar.addActionListener(e -> mostrarDialogoEditar());
        toolbar.add(btnEditar);

        F1Button btnEliminar = new F1Button("[ ELIMINAR ]", F1Button.Style.DANGER);
        btnEliminar.addActionListener(e -> eliminarPiloto());
        toolbar.add(btnEliminar);

        headerPanel.add(toolbar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabla
        String[] columns = {"ID", "Nombre", "Equipo", "Rol", "Experiencia", "Habilidad"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new F1Table(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        add(table.wrapInScrollPane(), BorderLayout.CENTER);
    }

    public void refresh() {
        cargarDatos(DataManager.getInstance().listarPilotos());
    }

    private void cargarDatos(List<Piloto> pilotos) {
        tableModel.setRowCount(0);
        for (Piloto p : pilotos) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getEquipo(), p.getRol(),
                    p.getExperiencia() + " años", String.format("%.0f", p.getHabilidad())
            });
        }
    }

    private void filtrarPilotos() {
        String termino = searchField.getText().trim();
        if (termino.isEmpty()) {
            refresh();
        } else {
            cargarDatos(DataManager.getInstance().buscarPilotos(termino));
        }
    }

    private void mostrarDialogoAgregar() {
        JDialog dialog = crearDialogoPiloto("Agregar Piloto", null);
        dialog.setVisible(true);
    }

    private void mostrarDialogoEditar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un piloto para editar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Piloto piloto = DataManager.getInstance().obtenerPiloto(id);
        if (piloto != null) {
            JDialog dialog = crearDialogoPiloto("Editar Piloto", piloto);
            dialog.setVisible(true);
        }
    }

    private void eliminarPiloto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un piloto para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nombre = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Â¿Eliminar al piloto " + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.getInstance().eliminarPiloto(id);
            refresh();
        }
    }

    private JDialog crearDialogoPiloto(String titulo, Piloto pilotoExistente) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(F1Colors.BG_CARD);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(F1Colors.BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos
        JTextField txtNombre = createField(formPanel, gbc, "Nombre:", 0);
        JComboBox<String> cmbEquipo = createComboField(formPanel, gbc, "Equipo:", 1,
                DataManager.getInstance().obtenerNombresEquipos().toArray(new String[0]));
        JComboBox<String> cmbRol = createComboField(formPanel, gbc, "Rol:", 2,
                new String[]{"Líder", "Escudero"});
        JTextField txtExperiencia = createField(formPanel, gbc, "Experiencia (años):", 3);
        JTextField txtHabilidad = createField(formPanel, gbc, "Habilidad (0-100):", 4);

        // Pre-rellenar si es edición
        if (pilotoExistente != null) {
            txtNombre.setText(pilotoExistente.getNombre());
            cmbEquipo.setSelectedItem(pilotoExistente.getEquipo());
            cmbRol.setSelectedItem(pilotoExistente.getRol());
            txtExperiencia.setText(String.valueOf(pilotoExistente.getExperiencia()));
            txtHabilidad.setText(String.valueOf((int) pilotoExistente.getHabilidad()));
        }

        // Botones
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        F1Button btnCancelar = new F1Button("Cancelar", F1Button.Style.SECONDARY);
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancelar);

        F1Button btnGuardar = new F1Button("Guardar", F1Button.Style.SUCCESS);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String equipo = (String) cmbEquipo.getSelectedItem();
                String rol = (String) cmbRol.getSelectedItem();
                int experiencia = Integer.parseInt(txtExperiencia.getText().trim());
                double habilidad = Double.parseDouble(txtHabilidad.getText().trim());

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "El nombre es requerido.");
                    return;
                }

                if (pilotoExistente != null) {
                    DataManager.getInstance().editarPiloto(pilotoExistente.getId(),
                            new Piloto(pilotoExistente.getId(), nombre, equipo, rol, experiencia, habilidad));
                } else {
                    DataManager.getInstance().agregarPiloto(
                            new Piloto(0, nombre, equipo, rol, experiencia, habilidad));
                }
                refresh();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valores numéricos inválidos.");
            }
        });
        btnPanel.add(btnGuardar);

        formPanel.add(btnPanel, gbc);
        dialog.add(formPanel);
        return dialog;
    }

    private JTextField createField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(F1Fonts.BODY);
        lbl.setForeground(F1Colors.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
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

    private JComboBox<String> createComboField(JPanel panel, GridBagConstraints gbc,
                                                String label, int row, String[] items) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(F1Fonts.BODY);
        lbl.setForeground(F1Colors.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(F1Fonts.INPUT);
        combo.setBackground(F1Colors.BG_INPUT);
        combo.setForeground(F1Colors.TEXT_PRIMARY);
        panel.add(combo, gbc);
        return combo;
    }
}
