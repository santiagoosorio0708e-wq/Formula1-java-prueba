package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.domain.model.Circuito;
import com.f1.domain.model.GanadorHistorico;
import com.f1.domain.model.Piloto;
import com.f1.infrastructure.adapter.in.gui.componentes.CircuitCard;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.componentes.TrackPreviewPanel;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;
import com.f1.infrastructure.adapter.out.persistence.DataManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelCircuitos extends JPanel {

    private JPanel gridPanel;
    private JTextField searchField;
    private CardLayout cardLayout;
    
    // Panel de detalles (Overlay)
    private JPanel detailContainer;
    private TrackPreviewPanel trackPreview;
    private JPanel detailStatsPanel;
    private Circuito circuitoSeleccionado;

    public PanelCircuitos() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(F1Colors.BG_DARK);

        // Vista 1: Grid de Circuitos
        JPanel viewGrid = crearVistaGrid();
        
        // Vista 2: Detalle de Circuito
        JPanel viewDetail = crearVistaDetalle();

        add(viewGrid, "GRID");
        add(viewDetail, "DETAIL");

        cardLayout.show(this, "GRID");
    }

    private JPanel crearVistaGrid() {
        JPanel viewGrid = new JPanel(new BorderLayout());
        viewGrid.setBackground(F1Colors.BG_DARK);

        // --- CABECERA ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(F1Colors.BG_DARK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("> GESTIÓN DE CIRCUITOS _");
        titleLabel.setFont(F1Fonts.TITLE_LARGE);
        titleLabel.setForeground(F1Colors.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        searchField = new JTextField(15);
        searchField.setFont(F1Fonts.INPUT);
        searchField.setBackground(F1Colors.BG_INPUT);
        searchField.setForeground(F1Colors.TEXT_PRIMARY);
        searchField.setCaretColor(F1Colors.TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        searchField.addActionListener(e -> filtrar());
        toolbar.add(searchField);

        F1Button btnBuscar = new F1Button("BUSCAR", F1Button.Style.SECONDARY);
        btnBuscar.addActionListener(e -> filtrar());
        toolbar.add(btnBuscar);

        F1Button btnAgregar = new F1Button("[+ AGREGAR]", F1Button.Style.PRIMARY);
        btnAgregar.addActionListener(e -> mostrarDialogo(null));
        toolbar.add(btnAgregar);

        headerPanel.add(toolbar, BorderLayout.EAST);
        viewGrid.add(headerPanel, BorderLayout.NORTH);

        // --- GRID ---
        gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        gridPanel.setBackground(F1Colors.BG_DARK);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(F1Colors.BG_DARK);
        scrollPane.getViewport().setBackground(F1Colors.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        viewGrid.add(scrollPane, BorderLayout.CENTER);

        return viewGrid;
    }

    private JPanel crearVistaDetalle() {
        detailContainer = new JPanel(new BorderLayout());
        detailContainer.setBackground(F1Colors.BG_DARK);

        // Cabecera Detalle
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 20, 30));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        F1Button btnVolver = new F1Button("[ < VOLVER ]", F1Button.Style.SECONDARY);
        btnVolver.addActionListener(e -> cardLayout.show(this, "GRID"));
        header.add(btnVolver, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        
        F1Button btnEditar = new F1Button("[ EDITAR ]", F1Button.Style.SECONDARY);
        btnEditar.addActionListener(e -> {
            if (circuitoSeleccionado != null) mostrarDialogo(circuitoSeleccionado);
        });
        
        F1Button btnEliminar = new F1Button("[ ELIMINAR ]", F1Button.Style.DANGER);
        btnEliminar.addActionListener(e -> {
            if (circuitoSeleccionado != null) {
                if (JOptionPane.showConfirmDialog(this, "¿Eliminar " + circuitoSeleccionado.getNombre() + "?",
                        "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DataManager.getInstance().eliminarCircuito(circuitoSeleccionado.getNombre());
                    refresh();
                    cardLayout.show(this, "GRID");
                }
            }
        });

        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        header.add(actionsPanel, BorderLayout.EAST);

        detailContainer.add(header, BorderLayout.NORTH);

        // Centro (Preview y Stats)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        trackPreview = new TrackPreviewPanel();
        contentPanel.add(trackPreview, BorderLayout.CENTER);

        detailStatsPanel = new JPanel();
        detailStatsPanel.setLayout(new BoxLayout(detailStatsPanel, BoxLayout.Y_AXIS));
        detailStatsPanel.setPreferredSize(new Dimension(350, 0));
        detailStatsPanel.setBackground(F1Colors.BG_CARD);
        detailStatsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, F1Colors.F1_RED),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        contentPanel.add(detailStatsPanel, BorderLayout.EAST);
        detailContainer.add(contentPanel, BorderLayout.CENTER);

        return detailContainer;
    }

    private void abrirDetalle(Circuito c) {
        circuitoSeleccionado = c;
        trackPreview.setPreviewData(c, null); // Sin config por ahora
        actualizarStatsDetalle(c);
        cardLayout.show(this, "DETAIL");
    }

    private void actualizarStatsDetalle(Circuito c) {
        detailStatsPanel.removeAll();

        JLabel lblName = new JLabel("<html><div style='width:300px;'>" + c.getNombre().toUpperCase() + "</div></html>");
        lblName.setFont(F1Fonts.TITLE);
        lblName.setForeground(F1Colors.TEXT_WHITE);
        detailStatsPanel.add(lblName);

        detailStatsPanel.add(Box.createVerticalStrut(10));

        JLabel lblPais = new JLabel("País: " + c.getPais());
        lblPais.setFont(F1Fonts.BODY_BOLD);
        lblPais.setForeground(F1Colors.F1_RED);
        detailStatsPanel.add(lblPais);

        detailStatsPanel.add(Box.createVerticalStrut(20));

        agregarStatUI("Longitud:", String.format("%.2f km", c.getLongitudKm()));
        agregarStatUI("Vueltas:", String.valueOf(c.getVueltas()));
        agregarStatUI("Distancia Total:", String.format("%.1f km", c.getDistanciaTotal()));

        detailStatsPanel.add(Box.createVerticalStrut(20));

        JTextArea txtDesc = new JTextArea(c.getDescripcion());
        txtDesc.setFont(F1Fonts.BODY_SMALL);
        txtDesc.setForeground(F1Colors.TEXT_SECONDARY);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        detailStatsPanel.add(txtDesc);

        if (c.getRecord() != null) {
            detailStatsPanel.add(Box.createVerticalStrut(20));
            JLabel lblRec = new JLabel("RÉCORD DE VUELTA");
            lblRec.setFont(F1Fonts.BODY_BOLD);
            lblRec.setForeground(F1Colors.TEXT_WHITE);
            detailStatsPanel.add(lblRec);
            
            JLabel recData = new JLabel(c.getRecord().getTiempo() + " - " + c.getRecord().getPiloto() + " (" + c.getRecord().getAnio() + ")");
            recData.setFont(F1Fonts.BODY);
            recData.setForeground(F1Colors.TEXT_MUTED);
            detailStatsPanel.add(recData);
        }

        detailStatsPanel.revalidate();
        detailStatsPanel.repaint();
    }

    private void agregarStatUI(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(400, 25));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(F1Fonts.BODY);
        lbl.setForeground(F1Colors.TEXT_SECONDARY);
        
        JLabel val = new JLabel(value);
        val.setFont(F1Fonts.BODY_BOLD);
        val.setForeground(F1Colors.TEXT_WHITE);
        
        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.EAST);
        
        detailStatsPanel.add(p);
        detailStatsPanel.add(Box.createVerticalStrut(5));
    }

    public void refresh() {
        cargarDatos(DataManager.getInstance().listarCircuitos());
    }

    private void cargarDatos(List<Circuito> circuitos) {
        gridPanel.removeAll();
        for (Circuito c : circuitos) {
            CircuitCard card = new CircuitCard(c, this::abrirDetalle);
            gridPanel.add(card);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
        
        if (circuitoSeleccionado != null) {
            Circuito act = DataManager.getInstance().obtenerCircuito(circuitoSeleccionado.getNombre());
            if (act != null) {
                abrirDetalle(act);
            } else {
                cardLayout.show(this, "GRID");
            }
        }
    }

    private void filtrar() {
        String t = searchField.getText().trim();
        cargarDatos(t.isEmpty() ? DataManager.getInstance().listarCircuitos()
                : DataManager.getInstance().buscarCircuitos(t));
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
        JTextField txtPais = addField(form, gbc, "País:", 1);
        JTextField txtLongitud = addField(form, gbc, "Longitud (km):", 2);
        JTextField txtVueltas = addField(form, gbc, "Vueltas:", 3);
        JTextField txtDescripcion = addField(form, gbc, "Descripción:", 4);

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
                        Double.parseDouble(txtLongitud.getText().trim().replace(",", ".")),
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
                JOptionPane.showMessageDialog(dialog, "Valores numéricos inválidos.");
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
