package com.f1.infrastructure.adapter.in.gui.paneles;

import com.f1.domain.model.Piloto;
import com.f1.infrastructure.adapter.out.persistence.DataManager;
import com.f1.infrastructure.adapter.in.gui.componentes.F1Button;
import com.f1.infrastructure.adapter.in.gui.componentes.PilotCard;
import com.f1.infrastructure.adapter.in.gui.componentes.PilotAvatar;
import com.f1.infrastructure.adapter.in.gui.util.F1Colors;
import com.f1.infrastructure.adapter.in.gui.util.F1Fonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD completo para la gestión de pilotos.
 * Rediseñado con interfaz moderna estilo videojuego (Grid + Hover lateral).
 */
public class PanelPilotos extends JPanel {

    private JPanel gridPanel;
    private JPanel detailsPanel;
    private JTextField searchField;
    private Piloto pilotoSeleccionado;

    // Componentes del detalle lateral
    private JLabel lblDetailName;
    private JLabel lblDetailTeam;
    private PilotAvatar detailAvatar;
    private JLabel lblRol;
    private StatBar barExp;
    private StatBar barHab;
    
    public PanelPilotos() {
        setBackground(F1Colors.BG_DARK);
        setLayout(new BorderLayout(20, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // --- HEADER SUPERIOR ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("GESTIÓN DE PILOTOS");
        titleLabel.setFont(F1Fonts.TITLE);
        titleLabel.setForeground(F1Colors.TEXT_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Toolbar de búsqueda y agregar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        toolbar.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(F1Fonts.INPUT);
        searchField.setBackground(F1Colors.BG_INPUT);
        searchField.setForeground(F1Colors.TEXT_PRIMARY);
        searchField.setCaretColor(F1Colors.TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchField.putClientProperty("JTextField.placeholderText", "Buscar piloto...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarPilotos();
            }
        });
        toolbar.add(searchField);

        F1Button btnAgregar = new F1Button("AGREGAR PILOTO", F1Button.Style.PRIMARY);
        btnAgregar.setPreferredSize(new Dimension(180, 40));
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());
        toolbar.add(btnAgregar);

        headerPanel.add(toolbar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- ZONA CENTRAL (GRID) ---
        // Usamos GridLayout(3, 7) como solicitó el usuario para que no queden cortados
        gridPanel = new JPanel(new GridLayout(3, 7, 10, 10));
        gridPanel.setBackground(F1Colors.BG_DARK);
        
        // Contenedor extra para centrar verticalmente si es necesario
        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.setBackground(F1Colors.BG_DARK);
        GridBagConstraints gbcGrid = new GridBagConstraints();
        gbcGrid.anchor = GridBagConstraints.WEST;
        gbcGrid.weightx = 1.0;
        gbcGrid.weighty = 1.0;
        gridWrapper.add(gridPanel, gbcGrid);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(null);
        scrollPane.setBackground(F1Colors.BG_DARK);
        scrollPane.getViewport().setBackground(F1Colors.BG_DARK);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        // Mostrar barras si son necesarias, pero el 3x7 debería caber
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Personalizar la barra de desplazamiento horizontal estilo juego
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(70, 70, 90);
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
                JButton jbutton = new JButton();
                jbutton.setPreferredSize(new Dimension(0, 0));
                jbutton.setMinimumSize(new Dimension(0, 0));
                jbutton.setMaximumSize(new Dimension(0, 0));
                return jbutton;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if(thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isDragging ? F1Colors.F1_RED : thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y + 4, thumbBounds.width, thumbBounds.height - 8, 8, 8);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // --- PANEL LATERAL DERECHO (DETALLES) ---
        crearPanelDetalles();
        add(detailsPanel, BorderLayout.EAST);
    }

    private void crearPanelDetalles() {
        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setPreferredSize(new Dimension(300, 0));
        detailsPanel.setBackground(F1Colors.BG_CARD);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Info superior (Avatar + Nombre)
        JPanel topInfo = new JPanel();
        topInfo.setLayout(new BoxLayout(topInfo, BoxLayout.Y_AXIS));
        topInfo.setOpaque(false);

        detailAvatar = new PilotAvatar(F1Colors.TEXT_MUTED);
        detailAvatar.setPreferredSize(new Dimension(160, 160));
        detailAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblDetailName = new JLabel("SELECCIONA");
        lblDetailName.setFont(F1Fonts.TITLE);
        lblDetailName.setForeground(F1Colors.TEXT_WHITE);
        lblDetailName.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblDetailTeam = new JLabel("UN PILOTO");
        lblDetailTeam.setFont(F1Fonts.BODY);
        lblDetailTeam.setForeground(F1Colors.TEXT_SECONDARY);
        lblDetailTeam.setAlignmentX(Component.CENTER_ALIGNMENT);

        topInfo.add(Box.createVerticalStrut(10));
        topInfo.add(detailAvatar);
        topInfo.add(Box.createVerticalStrut(20));
        topInfo.add(lblDetailName);
        topInfo.add(Box.createVerticalStrut(5));
        topInfo.add(lblDetailTeam);
        topInfo.add(Box.createVerticalStrut(30));

        // Estadísticas
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);

        lblRol = new JLabel("<html><font color='#A0A0B4'>Rol:</font><br><font size='+1' color='#FFFFFF'>--</font></html>");
        lblRol.setFont(F1Fonts.BODY);

        barExp = new StatBar("Experiencia (Años):", 20); // max 20 years
        barHab = new StatBar("Habilidad (0-100):", 100); // max 100

        statsPanel.add(lblRol);
        statsPanel.add(Box.createVerticalStrut(15));
        statsPanel.add(barExp);
        statsPanel.add(Box.createVerticalStrut(15));
        statsPanel.add(barHab);
        statsPanel.add(Box.createVerticalStrut(40));

        // Botones de acción
        JPanel actionsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        actionsPanel.setOpaque(false);
        
        F1Button btnEditar = new F1Button("EDITAR", F1Button.Style.SECONDARY);
        btnEditar.addActionListener(e -> mostrarDialogoEditar());
        
        F1Button btnEliminar = new F1Button("ELIMINAR", F1Button.Style.DANGER);
        btnEliminar.addActionListener(e -> eliminarPiloto());
        
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(statsPanel, BorderLayout.NORTH);
        centerContainer.add(actionsPanel, BorderLayout.SOUTH);

        detailsPanel.add(topInfo, BorderLayout.NORTH);
        detailsPanel.add(centerContainer, BorderLayout.CENTER);
        
        // Inicialmente ocultar botones si no hay piloto
        actionsPanel.setVisible(false);
    }

        
        // Inner class for the animated progress bar
        class StatBar extends JPanel {
            private String title;
            private int maxVal;
            private int currentVal = 0;

            public StatBar(String title, int maxVal) {
                this.title = title;
                this.maxVal = maxVal;
                setOpaque(false);
                setPreferredSize(new Dimension(260, 45));
                setMaximumSize(new Dimension(260, 45));
            }

            public void setValue(int val) {
                this.currentVal = val;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                
                // Texto
                g2.setFont(F1Fonts.BODY_SMALL);
                g2.setColor(F1Colors.TEXT_MUTED);
                g2.drawString(title, 0, 12);
                
                g2.setFont(F1Fonts.BODY_BOLD);
                g2.setColor(F1Colors.TEXT_WHITE);
                g2.drawString(String.valueOf(currentVal), w - 30, 12);

                // Barra de fondo
                int barY = 20;
                int barH = 8;
                g2.setColor(new Color(40, 40, 50));
                g2.fillRoundRect(0, barY, w, barH, barH, barH);

                // Barra de relleno
                if (currentVal > 0) {
                    float ratio = Math.min(1f, (float)currentVal / maxVal);
                    int fillW = (int) (w * ratio);
                    
                    // Color depende de ratio
                    Color c = F1Colors.STATUS_DANGER; // Rojo
                    if (ratio > 0.4f) c = F1Colors.STATUS_WARNING; // Amarillo
                    if (ratio > 0.7f) c = F1Colors.STATUS_OK; // Verde

                    g2.setColor(c);
                    g2.fillRoundRect(0, barY, fillW, barH, barH, barH);
                    
                    // Glow
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
                    g2.fillRoundRect(0, barY-2, fillW, barH+4, barH, barH);
                }

                g2.dispose();
            }
        }

    private void updateStatLabel(JLabel label, String title, String val) {
        label.setText("<html><font color='#A0A0B4'>" + title + "</font><br><font size='+1' color='#FFFFFF'>" + val + "</font></html>");
    }

    public void refresh() {
        cargarDatos(DataManager.getInstance().listarPilotos());
    }

    private void cargarDatos(List<Piloto> pilotos) {
        gridPanel.removeAll();
        
        for (Piloto p : pilotos) {
            PilotCard card = new PilotCard(p, this::seleccionarPiloto);
            gridPanel.add(card);
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void seleccionarPiloto(Piloto p) {
        this.pilotoSeleccionado = p;
        Color teamColor = F1Colors.getTeamColor(p.getEquipo());
        
        lblDetailName.setText(p.getNombre().toUpperCase());
        lblDetailTeam.setText(p.getEquipo());
        detailAvatar.setTeamColor(teamColor);
        detailAvatar.setHovered(true); // Glow effect on selection
        
        updateStatLabel(lblRol, "Rol:", p.getRol());
        barExp.setValue(p.getExperiencia());
        barHab.setValue((int)p.getHabilidad());
        
        // Mostrar panel de botones si estaba oculto
        ((JPanel)((BorderLayout)((JPanel)detailsPanel.getComponent(1)).getLayout()).getLayoutComponent(BorderLayout.SOUTH)).setVisible(true);
        
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(teamColor, 2),
            new EmptyBorder(19, 19, 19, 19)
        ));
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
        if (pilotoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un piloto para editar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JDialog dialog = crearDialogoPiloto("Editar Piloto", pilotoSeleccionado);
        dialog.setVisible(true);
    }

    private void eliminarPiloto() {
        if (pilotoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un piloto para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al piloto " + pilotoSeleccionado.getNombre() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.getInstance().eliminarPiloto(pilotoSeleccionado.getId());
            pilotoSeleccionado = null;
            lblDetailName.setText("SELECCIONA");
            lblDetailTeam.setText("UN PILOTO");
            detailAvatar.setTeamColor(F1Colors.TEXT_MUTED);
            detailAvatar.setHovered(false);
            barExp.setValue(0);
            barHab.setValue(0);
            ((JPanel)((BorderLayout)((JPanel)detailsPanel.getComponent(1)).getLayout()).getLayoutComponent(BorderLayout.SOUTH)).setVisible(false);
            detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(F1Colors.BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
            ));
            refresh();
        }
    }

    private JDialog crearDialogoPiloto(String titulo, Piloto pilotoExistente) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(F1Colors.BG_CARD);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(F1Colors.BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

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
        btnCancelar.setPreferredSize(new Dimension(100, 36));
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancelar);

        F1Button btnGuardar = new F1Button("Guardar", F1Button.Style.SUCCESS);
        btnGuardar.setPreferredSize(new Dimension(100, 36));
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
                    // Update current selection
                    seleccionarPiloto(DataManager.getInstance().obtenerPiloto(pilotoExistente.getId()));
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
        lbl.setFont(F1Fonts.BODY_BOLD);
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
        lbl.setFont(F1Fonts.BODY_BOLD);
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
