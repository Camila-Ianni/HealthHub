package com.healthhub.gui;

import com.healthhub.service.Persistencia;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaAgenda extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final HealthHubContext contexto;
    private final DefaultTableModel modelo;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField filtroDni;
    private final JTextField filtroMedico;

    public VentanaAgenda(HealthHubContext contexto) {
        this.contexto = contexto;
        this.modelo = new DefaultTableModel(new String[] {
            "Fecha/Hora", "DNI", "Paciente", "Matrícula", "Médico", "Estado", "Sobreturno"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.sorter = new TableRowSorter<>(modelo);
        this.filtroDni = new JTextField(14);
        this.filtroMedico = new JTextField(14);

        setTitle("Health Hub - Agenda");
        setMinimumSize(new Dimension(900, 560));
        setSize(980, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(HealthHubSwing.FONDO);

        add(HealthHubSwing.panelConTitulo("Agenda consolidada", "Turnos integrados con pacientes y médicos"), BorderLayout.NORTH);
        add(crearPanelBusqueda(), BorderLayout.CENTER);

        cargarDatos();
    }

    private JPanel crearPanelBusqueda() {
        JPanel base = new JPanel(new BorderLayout(0, 16));
        base.setBackground(HealthHubSwing.FONDO);
        base.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.setBackground(Color.WHITE);
        filtros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        filtros.add(etiqueta("Buscar por DNI"));
        filtros.add(filtroDni);
        filtros.add(etiqueta("Médico"));
        filtros.add(filtroMedico);

        JButton buscar = HealthHubSwing.botonPrincipal("Buscar");
        buscar.addActionListener(e -> aplicarFiltros());
        filtros.add(buscar);

        JButton limpiar = HealthHubSwing.botonTexto("Limpiar");
        limpiar.addActionListener(e -> limpiarFiltros());
        filtros.add(limpiar);

        JTable tabla = crearTabla();
        base.add(filtros, BorderLayout.NORTH);
        base.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return base;
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        label.setForeground(HealthHubSwing.TEXTO);
        return label;
    }

    private JTable crearTabla() {
        JTable tabla = new JTable(modelo);
        tabla.setRowSorter(sorter);
        tabla.setRowHeight(34);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(232, 241, 241));
        tabla.setShowVerticalLines(false);
        tabla.setFont(HealthHubSwing.fuente(13, Font.PLAIN));
        tabla.setSelectionBackground(new Color(209, 242, 238));
        tabla.setSelectionForeground(HealthHubSwing.TEXTO);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(HealthHubSwing.ACENTO);
        header.setForeground(Color.WHITE);
        header.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
            ) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(244, 250, 250));
                }
                c.setForeground(HealthHubSwing.TEXTO);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        return tabla;
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        List<Persistencia.AgendaConsolidadaFila> filas = contexto.persistencia.consultarAgendaConsolidadaInnerJoin(
            contexto.gestorTurnos.listarTodos(),
            contexto.gestorPacientes.listarTodos(),
            contexto.gestorMedicos.listarTodos()
        );

        for (Persistencia.AgendaConsolidadaFila fila : filas) {
            modelo.addRow(new Object[] {
                fila.getFechaHora().format(FORMATO_FECHA_HORA),
                fila.getDniPaciente(),
                fila.getNombrePaciente(),
                fila.getMatriculaMedico(),
                fila.getNombreMedico(),
                fila.getEstado(),
                fila.isSobreturno() ? "Sí" : "No"
            });
        }
    }

    private void aplicarFiltros() {
        String dni = filtroDni.getText().trim();
        String medico = filtroMedico.getText().trim();

        if (dni.isEmpty() && medico.isEmpty()) {
            sorter.setRowFilter(null);
            JOptionPane.showMessageDialog(this, "Se muestran todos los turnos.", "Agenda", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder patron = new StringBuilder("(?i)");
        if (!dni.isEmpty()) {
            patron.append(dni);
        }
        if (!medico.isEmpty()) {
            if (patron.length() > 4) {
                patron.append("|");
            }
            patron.append(medico);
        }

        sorter.setRowFilter(RowFilter.regexFilter(patron.toString(), 1, 3, 4));
    }

    private void limpiarFiltros() {
        filtroDni.setText("");
        filtroMedico.setText("");
        sorter.setRowFilter(null);
    }
}
