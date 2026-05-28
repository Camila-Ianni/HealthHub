package com.healthhub.gui;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.EstadoTurno;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class VentanaGestionGeneral extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final HealthHubContext contexto;
    private final String pestañaInicial;

    private final DefaultTableModel modeloPacientes = new DefaultTableModel(new String[] {
        "DNI", "Nombre", "Apellido", "Teléfono", "Obra social", "Historial"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloMedicos = new DefaultTableModel(new String[] {
        "Matrícula", "Nombre", "Apellido", "Especialidad", "Disponibilidades"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloTurnos = new DefaultTableModel(new String[] {
        "ID", "Fecha/Hora", "Paciente", "Médico", "Estado", "Sobreturno"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloHistoriales = new DefaultTableModel(new String[] {
        "DNI", "Paciente", "Entradas", "Última actualización"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final TableRowSorter<DefaultTableModel> sorterPacientes = new TableRowSorter<>(modeloPacientes);
    private final TableRowSorter<DefaultTableModel> sorterMedicos = new TableRowSorter<>(modeloMedicos);
    private final TableRowSorter<DefaultTableModel> sorterTurnos = new TableRowSorter<>(modeloTurnos);
    private final TableRowSorter<DefaultTableModel> sorterHistoriales = new TableRowSorter<>(modeloHistoriales);

    private final JTextField filtroPacientes = new JTextField(16);
    private final JTextField filtroMedicos = new JTextField(16);
    private final JTextField filtroTurnos = new JTextField(16);
    private final JTextField filtroHistoriales = new JTextField(16);

    private final JTextArea detallesMedico = crearAreaDetalle();
    private final JTextArea detallesTurno = crearAreaDetalle();
    private final JTextArea detallesHistorial = crearAreaDetalle();
    private final DefaultListModel<String> modeloNotificaciones = new DefaultListModel<>();
    private final JList<String> listaNotificaciones = new JList<>(modeloNotificaciones);

    private JTable tablaPacientes;
    private JTable tablaMedicos;
    private JTable tablaTurnos;
    private JTable tablaHistoriales;
    private JComboBox<DayOfWeek> comboDia;
    private JComboBox<String> comboPacientesTurno;
    private JComboBox<String> comboMedicosTurno;

    public VentanaGestionGeneral(HealthHubContext contexto, String pestañaInicial) {
        this.contexto = contexto;
        this.pestañaInicial = pestañaInicial == null ? "inicio" : pestañaInicial;

        setTitle("Health Hub - Gestión Integral");
        setMinimumSize(new Dimension(1220, 780));
        setSize(1360, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(HealthHubSwing.FONDO);

        add(HealthHubSwing.panelConTitulo("Gestión integral", "Todo el flujo clínico ordenado en una sola interfaz"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);

        refrescarTodo();
        SwingUtilities.invokeLater(() -> seleccionarPestañaInicial());
    }

    private JPanel crearContenido() {
        JPanel base = new JPanel(new BorderLayout());
        base.setBackground(HealthHubSwing.FONDO);
        base.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        javax.swing.JTabbedPane pestañas = new javax.swing.JTabbedPane();
        pestañas.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        pestañas.addTab("Inicio", HealthHubIcons.icono("estadisticas", 18, HealthHubSwing.ACENTO_OSCURO), crearPanelResumen());
        pestañas.addTab("Pacientes", HealthHubIcons.icono("pacientes", 18, HealthHubSwing.ACENTO_OSCURO), crearPanelPacientes());
        pestañas.addTab("Médicos", HealthHubIcons.icono("medicos", 18, HealthHubSwing.ACENTO_OSCURO), crearPanelMedicos());
        pestañas.addTab("Turnos", HealthHubIcons.icono("turnos", 18, HealthHubSwing.ACENTO_OSCURO), crearPanelTurnos());
        pestañas.addTab("Historiales", HealthHubIcons.icono("historiales", 18, HealthHubSwing.ACENTO_OSCURO), crearPanelHistoriales());
        pestañas.addTab("Notificaciones", HealthHubIcons.icono("notificaciones", 18, HealthHubSwing.ACENTO_OSCURO), crearPanelNotificaciones());

        base.add(pestañas, BorderLayout.CENTER);
        return base;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 18, 18);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(HealthHubSwing.tarjeta("Pacientes", String.valueOf(contexto.gestorPacientes.listarTodos().size())), gbc);

        gbc.gridx = 1;
        panel.add(HealthHubSwing.tarjeta("Médicos", String.valueOf(contexto.gestorMedicos.listarTodos().size())), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(HealthHubSwing.tarjeta("Turnos", String.valueOf(contexto.gestorTurnos.listarTodos().size())), gbc);

        gbc.gridx = 1;
        panel.add(HealthHubSwing.tarjeta("Historiales", String.valueOf(contexto.gestorHistoriales.listarTodos().size())), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(crearTarjetaAccionesRapidas(), gbc);

        return panel;
    }

    private JPanel crearTarjetaAccionesRapidas() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel titulo = new JLabel("Atajos frecuentes");
        titulo.setFont(HealthHubSwing.fuente(18, Font.BOLD));
        titulo.setForeground(HealthHubSwing.TEXTO);

        JLabel subtitulo = new JLabel("Las operaciones más usadas quedan a un clic.");
        subtitulo.setFont(HealthHubSwing.fuente(12, Font.PLAIN));
        subtitulo.setForeground(HealthHubSwing.TEXTO_SUAVE);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(botonIcono("Nuevo paciente", "pacientes", () -> nuevoPaciente()));
        botones.add(botonIcono("Nuevo médico", "medicos", () -> nuevoMedico()));
        botones.add(botonIcono("Nuevo turno", "turnos", () -> nuevoTurno()));
        botones.add(botonIcono("Nueva consulta", "historiales", () -> nuevaConsulta()));

        JPanel encabezado = new JPanel(new BorderLayout(0, 4));
        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(subtitulo, BorderLayout.CENTER);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPacientes() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel barra = crearBarraBusqueda("Buscar paciente", filtroPacientes, this::aplicarFiltroPacientes);
        barra.add(botonIcono("Agregar", "pacientes", this::nuevoPaciente));
        barra.add(botonIcono("Editar", "login", this::editarPaciente));
        barra.add(botonIcono("Ver historial", "historiales", this::verHistorialPaciente));

        tablaPacientes = crearTabla(modeloPacientes, sorterPacientes);
        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelMedicos() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel barra = crearBarraBusqueda("Buscar médico", filtroMedicos, this::aplicarFiltroMedicos);
        barra.add(botonIcono("Agregar", "medicos", this::nuevoMedico));
        barra.add(botonIcono("Agregar disponibilidad", "agenda", this::agregarDisponibilidad));
        barra.add(botonIcono("Actualizar notificaciones", "notificaciones", this::actualizarNotificacionesSeleccionadas));

        tablaMedicos = crearTabla(modeloMedicos, sorterMedicos);
        tablaMedicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetallesMedicoSeleccionado();
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tablaMedicos), crearPanelDetallesMedico());
        split.setResizeWeight(0.72);
        split.setDividerLocation(760);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTurnos() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel barra = crearBarraBusqueda("Buscar turno", filtroTurnos, this::aplicarFiltroTurnos);
        barra.add(botonIcono("Agregar", "turnos", this::nuevoTurno));
        barra.add(botonIcono("Reprogramar", "agenda", this::reprogramarTurno));
        barra.add(botonIcono("Cancelar", "salir", this::cancelarTurno));
        barra.add(botonIcono("Atendido", "estadisticas", this::marcarAtendido));

        tablaTurnos = crearTabla(modeloTurnos, sorterTurnos);
        tablaTurnos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetallesTurnoSeleccionado();
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tablaTurnos), crearPanelDetallesTurno());
        split.setResizeWeight(0.75);
        split.setDividerLocation(820);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelHistoriales() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel barra = crearBarraBusqueda("Buscar historial", filtroHistoriales, this::aplicarFiltroHistoriales);
        barra.add(botonIcono("Registrar", "historiales", this::nuevaConsulta));
        barra.add(botonIcono("Actualizar diagnóstico", "estadisticas", this::actualizarDiagnostico));
        barra.add(botonIcono("Actualizar estudios", "agenda", this::actualizarEstudios));

        tablaHistoriales = crearTabla(modeloHistoriales, sorterHistoriales);
        tablaHistoriales.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetallesHistorialSeleccionado();
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tablaHistoriales), crearPanelDetallesHistorial());
        split.setResizeWeight(0.7);
        split.setDividerLocation(780);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelNotificaciones() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        barra.add(etiqueta("Médico activo"));
        JButton limpiar = HealthHubSwing.botonTexto("Limpiar notificaciones");
        limpiar.addActionListener(e -> limpiarNotificacionesSeleccionadas());
        barra.add(limpiar);

        listaNotificaciones.setFont(HealthHubSwing.fuente(13, Font.PLAIN));
        listaNotificaciones.setSelectionBackground(new Color(209, 242, 238));
        listaNotificaciones.setSelectionForeground(HealthHubSwing.TEXTO);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(listaNotificaciones), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDetallesMedico() {
        JPanel panel = crearPanelDetalleBase("Detalles del médico", detallesMedico);
        return panel;
    }

    private JPanel crearPanelDetallesTurno() {
        JPanel panel = crearPanelDetalleBase("Detalles del turno", detallesTurno);
        return panel;
    }

    private JPanel crearPanelDetallesHistorial() {
        JPanel panel = crearPanelDetalleBase("Detalle del historial", detallesHistorial);
        return panel;
    }

    private JPanel crearPanelDetalleBase(String titulo, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel label = new JLabel(titulo);
        label.setFont(HealthHubSwing.fuente(16, Font.BOLD));
        label.setForeground(HealthHubSwing.TEXTO);
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraBusqueda(String placeholder, JTextField campo, Runnable accionBuscar) {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel label = new JLabel(placeholder);
        label.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        label.setForeground(HealthHubSwing.TEXTO);
        barra.add(label);

        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 224, 224)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        campo.addActionListener(e -> accionBuscar.run());
        barra.add(campo);

        JButton buscar = HealthHubSwing.botonPrincipal("Buscar");
        buscar.addActionListener(e -> accionBuscar.run());
        barra.add(buscar);

        JButton limpiar = HealthHubSwing.botonTexto("Limpiar");
        limpiar.addActionListener(e -> {
            campo.setText("");
            accionBuscar.run();
        });
        barra.add(limpiar);

        return barra;
    }

    private JTable crearTabla(DefaultTableModel modelo, TableRowSorter<DefaultTableModel> sorter) {
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
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        label.setForeground(HealthHubSwing.TEXTO);
        return label;
    }

    private JButton botonIcono(String texto, String icono, Runnable accion) {
        JButton boton = new JButton(texto, HealthHubIcons.icono(icono, 18, HealthHubSwing.ACENTO_OSCURO));
        boton.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        boton.setForeground(HealthHubSwing.TEXTO);
        boton.setBackground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        boton.setFocusPainted(false);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private JTextArea crearAreaDetalle() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(HealthHubSwing.fuente(13, Font.PLAIN));
        area.setBackground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return area;
    }

    private void seleccionarPestañaInicial() {
        int index = switch (pestañaInicial.toLowerCase(Locale.ROOT)) {
            case "pacientes" -> 1;
            case "medicos" -> 2;
            case "turnos" -> 3;
            case "historiales" -> 4;
            case "notificaciones" -> 5;
            default -> 0;
        };
        if (getContentPane().getComponentCount() > 0 && getContentPane().getComponent(0) instanceof JPanel panel) {
            if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof javax.swing.JTabbedPane pestañas) {
                pestañas.setSelectedIndex(index);
            }
        }
    }

    private void refrescarTodo() {
        refrescarPacientes();
        refrescarMedicos();
        refrescarTurnos();
        refrescarHistoriales();
        refrescarNotificaciones();
        mostrarDetallesMedicoSeleccionado();
        mostrarDetallesTurnoSeleccionado();
        mostrarDetallesHistorialSeleccionado();
    }

    private void refrescarPacientes() {
        modeloPacientes.setRowCount(0);
        List<Paciente> pacientes = new ArrayList<>(contexto.gestorPacientes.listarTodos());
        pacientes.sort(Comparator.comparing(Paciente::getApellido).thenComparing(Paciente::getNombre));
        for (Paciente paciente : pacientes) {
            boolean tieneHistorial = contexto.gestorHistoriales.verHistorial(paciente.getDni()) != null;
            modeloPacientes.addRow(new Object[] {
                paciente.getDni(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getTelefono(),
                paciente.getObraSocial(),
                tieneHistorial ? "Sí" : "No"
            });
        }
    }

    private void refrescarMedicos() {
        modeloMedicos.setRowCount(0);
        List<Medico> medicos = new ArrayList<>(contexto.gestorMedicos.listarTodos());
        medicos.sort(Comparator.comparing(Medico::getApellido).thenComparing(Medico::getNombre));
        for (Medico medico : medicos) {
            modeloMedicos.addRow(new Object[] {
                medico.getMatricula(),
                medico.getNombre(),
                medico.getApellido(),
                medico.getEspecialidad(),
                contexto.gestorMedicos.consultarDisponibilidad(medico.getMatricula()).size()
            });
        }
    }

    private void refrescarTurnos() {
        modeloTurnos.setRowCount(0);
        List<Turno> turnos = new ArrayList<>(contexto.gestorTurnos.listarTodos());
        turnos.sort(Comparator.comparing(Turno::getFechaHora));
        for (Turno turno : turnos) {
            String paciente = contexto.gestorPacientes.buscarPorDni(turno.getDniPaciente())
                .map(Paciente::nombreCompleto)
                .orElse(turno.getDniPaciente());
            String medico = contexto.gestorMedicos.buscarMedico(turno.getMatriculaMedico())
                .map(m -> m.getNombre() + " " + m.getApellido())
                .orElse(turno.getMatriculaMedico());
            modeloTurnos.addRow(new Object[] {
                turno.getId(),
                turno.getFechaHora().format(FORMATO_FECHA_HORA),
                paciente,
                medico,
                turno.getEstado().name(),
                turno.isSobreturno() ? "Sí" : "No"
            });
        }
    }

    private void refrescarHistoriales() {
        modeloHistoriales.setRowCount(0);
        List<HistorialClinico> historiales = new ArrayList<>(contexto.gestorHistoriales.listarTodos());
        historiales.sort(Comparator.comparing(HistorialClinico::getDniPaciente));
        for (HistorialClinico historial : historiales) {
            Optional<Paciente> paciente = contexto.gestorPacientes.buscarPorDni(historial.getDniPaciente());
            String ultima = historial.ultimaEntrada() != null ? historial.ultimaEntrada().getFecha().format(FORMATO_FECHA_HORA) : "Sin entradas";
            modeloHistoriales.addRow(new Object[] {
                historial.getDniPaciente(),
                paciente.map(Paciente::nombreCompleto).orElse(historial.getDniPaciente()),
                historial.getEntradas().size(),
                ultima
            });
        }
    }

    private void refrescarNotificaciones() {
        modeloNotificaciones.clear();
        String matricula = obtenerMatriculaSeleccionada();
        if (matricula == null) {
            modeloNotificaciones.addElement("Seleccioná un médico para ver sus notificaciones.");
            return;
        }

        List<String> notificaciones = contexto.gestorNotificaciones.verNotificaciones(matricula);
        if (notificaciones.isEmpty()) {
            modeloNotificaciones.addElement("Sin notificaciones para " + matricula + ".");
            return;
        }

        for (String notificacion : notificaciones) {
            modeloNotificaciones.addElement(notificacion);
        }
    }

    private void aplicarFiltroPacientes() {
        filtrarTabla(sorterPacientes, filtroPacientes.getText(), 0, 1, 2, 3, 4);
    }

    private void aplicarFiltroMedicos() {
        filtrarTabla(sorterMedicos, filtroMedicos.getText(), 0, 1, 2, 3);
    }

    private void aplicarFiltroTurnos() {
        filtrarTabla(sorterTurnos, filtroTurnos.getText(), 0, 1, 2, 3, 4);
    }

    private void aplicarFiltroHistoriales() {
        filtrarTabla(sorterHistoriales, filtroHistoriales.getText(), 0, 1, 2, 3);
    }

    private void filtrarTabla(TableRowSorter<DefaultTableModel> sorter, String texto, int... columnas) {
        String valor = texto.trim();
        if (valor.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(valor), columnas));
    }

    private void nuevoPaciente() {
        JTextField dni = new JTextField();
        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField telefono = new JTextField();
        JTextField obraSocial = new JTextField();

        if (!mostrarFormulario("Nuevo paciente", new String[] {"DNI", "Nombre", "Apellido", "Teléfono", "Obra social"},
            new JComponent[] {dni, nombre, apellido, telefono, obraSocial})) {
            return;
        }

        if (dni.getText().trim().isEmpty() || nombre.getText().trim().isEmpty() || apellido.getText().trim().isEmpty()) {
            mostrarError("Completá DNI, nombre y apellido.");
            return;
        }

        boolean ok = contexto.gestorPacientes.registrarPaciente(new Paciente(
            dni.getText().trim(), nombre.getText().trim(), apellido.getText().trim(), telefono.getText().trim(), obraSocial.getText().trim()
        ));
        if (!ok) {
            mostrarError("Ya existe un paciente con ese DNI.");
            return;
        }

        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Paciente registrado correctamente.");
    }

    private void editarPaciente() {
        Paciente paciente = obtenerPacienteSeleccionado();
        if (paciente == null) {
            mostrarError("Seleccioná un paciente.");
            return;
        }

        JTextField nombre = new JTextField(paciente.getNombre());
        JTextField apellido = new JTextField(paciente.getApellido());
        JTextField telefono = new JTextField(paciente.getTelefono());
        JTextField obraSocial = new JTextField(paciente.getObraSocial());

        if (!mostrarFormulario("Editar paciente", new String[] {"Nombre", "Apellido", "Teléfono", "Obra social"},
            new JComponent[] {nombre, apellido, telefono, obraSocial})) {
            return;
        }

        contexto.gestorPacientes.modificarPaciente(paciente.getDni(), nombre.getText().trim(), apellido.getText().trim(), telefono.getText().trim(), obraSocial.getText().trim());
        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Paciente actualizado.");
    }

    private void verHistorialPaciente() {
        Paciente paciente = obtenerPacienteSeleccionado();
        if (paciente == null) {
            mostrarError("Seleccioná un paciente.");
            return;
        }
        HistorialClinico historial = contexto.gestorHistoriales.verHistorial(paciente.getDni());
        if (historial == null) {
            mostrarError("Ese paciente todavía no tiene historial.");
            return;
        }
        detallesHistorial.setText(formatearHistorial(historial));
        mostrarPestaña(4);
    }

    private void nuevoMedico() {
        JTextField matricula = new JTextField();
        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField especialidad = new JTextField();

        if (!mostrarFormulario("Nuevo médico", new String[] {"Matrícula", "Nombre", "Apellido", "Especialidad"},
            new JComponent[] {matricula, nombre, apellido, especialidad})) {
            return;
        }

        boolean ok = contexto.gestorMedicos.registrarMedico(new Medico(
            matricula.getText().trim(), nombre.getText().trim(), apellido.getText().trim(), especialidad.getText().trim()
        ));
        if (!ok) {
            mostrarError("Ya existe un médico con esa matrícula.");
            return;
        }

        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Médico registrado correctamente.");
    }

    private void agregarDisponibilidad() {
        Medico medico = obtenerMedicoSeleccionado();
        if (medico == null) {
            mostrarError("Seleccioná un médico.");
            return;
        }

        JComboBox<DayOfWeek> dia = new JComboBox<>(DayOfWeek.values());
        JTextField inicio = new JTextField("08:00");
        JTextField fin = new JTextField("12:00");

        if (!mostrarFormulario("Nueva disponibilidad", new String[] {"Día", "Inicio (HH:mm)", "Fin (HH:mm)"},
            new JComponent[] {dia, inicio, fin})) {
            return;
        }

        try {
            boolean ok = contexto.gestorMedicos.agregarDisponibilidad(medico.getMatricula(), new Disponibilidad(
                (DayOfWeek) dia.getSelectedItem(),
                LocalTime.parse(inicio.getText().trim()),
                LocalTime.parse(fin.getText().trim())
            ));
            if (!ok) {
                mostrarError("La disponibilidad se superpone con otra existente.");
                return;
            }
            contexto.guardarTodo();
            refrescarTodo();
            mostrarInfo("Disponibilidad agregada.");
        } catch (Exception ex) {
            mostrarError("Formato de horario inválido.");
        }
    }

    private void actualizarNotificacionesSeleccionadas() {
        Medico medico = obtenerMedicoSeleccionado();
        if (medico == null) {
            mostrarError("Seleccioná un médico.");
            return;
        }
        refrescarNotificaciones();
        mostrarPestaña(5);
    }

    private void limpiarNotificacionesSeleccionadas() {
        Medico medico = obtenerMedicoSeleccionado();
        if (medico == null) {
            mostrarError("Seleccioná un médico.");
            return;
        }
        contexto.gestorNotificaciones.limpiarNotificaciones(medico.getMatricula());
        contexto.guardarTodo();
        refrescarNotificaciones();
        mostrarInfo("Notificaciones limpiadas.");
    }

    private void nuevoTurno() {
        asegurarCombosTurnos();

        JTextField fecha = new JTextField(LocalDate.now().plusDays(1).format(FORMATO_FECHA));
        JTextField hora = new JTextField("09:00");
        JComboBox<String> pacientes = new JComboBox<>(comboPacientesTurno.getModel());
        JComboBox<String> medicos = new JComboBox<>(comboMedicosTurno.getModel());
        JComboBox<Boolean> sobreturno = new JComboBox<>(new Boolean[] {Boolean.FALSE, Boolean.TRUE});

        if (!mostrarFormulario("Nuevo turno", new String[] {"Paciente", "Médico", "Fecha (yyyy-MM-dd)", "Hora (HH:mm)", "Sobreturno"},
            new JComponent[] {pacientes, medicos, fecha, hora, sobreturno})) {
            return;
        }

        try {
            String dni = extraerValorSeleccionado((String) pacientes.getSelectedItem());
            String matricula = extraerValorSeleccionado((String) medicos.getSelectedItem());
            LocalDateTime fechaHora = LocalDateTime.of(LocalDate.parse(fecha.getText().trim()), LocalTime.parse(hora.getText().trim()));
            boolean esSobreturno = Boolean.TRUE.equals(sobreturno.getSelectedItem());
            Optional<Turno> creado = contexto.gestorTurnos.crearTurno(dni, matricula, fechaHora, esSobreturno);
            if (creado.isEmpty()) {
                mostrarError("No se pudo crear el turno. Revisá disponibilidad, duplicados o fecha pasada.");
                return;
            }
            contexto.guardarTodo();
            refrescarTodo();
            mostrarInfo("Turno creado correctamente.");
        } catch (Exception ex) {
            mostrarError("Formato de fecha u hora inválido.");
        }
    }

    private void reprogramarTurno() {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno == null) {
            mostrarError("Seleccioná un turno.");
            return;
        }

        JTextField fecha = new JTextField(turno.getFechaHora().toLocalDate().format(FORMATO_FECHA));
        JTextField hora = new JTextField(turno.getFechaHora().toLocalTime().format(FORMATO_HORA));

        if (!mostrarFormulario("Reprogramar turno", new String[] {"Fecha (yyyy-MM-dd)", "Hora (HH:mm)"},
            new JComponent[] {fecha, hora})) {
            return;
        }

        try {
            LocalDateTime nueva = LocalDateTime.of(LocalDate.parse(fecha.getText().trim()), LocalTime.parse(hora.getText().trim()));
            if (!contexto.gestorTurnos.reprogramarTurno(turno.getId(), nueva)) {
                mostrarError("No se pudo reprogramar. Revisá disponibilidad y duplicados.");
                return;
            }
            contexto.guardarTodo();
            refrescarTodo();
            mostrarInfo("Turno reprogramado.");
        } catch (Exception ex) {
            mostrarError("Formato de fecha u hora inválido.");
        }
    }

    private void cancelarTurno() {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno == null) {
            mostrarError("Seleccioná un turno.");
            return;
        }
        if (!contexto.gestorTurnos.cancelarTurno(turno.getId())) {
            mostrarError("No se pudo cancelar el turno.");
            return;
        }
        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Turno cancelado.");
    }

    private void marcarAtendido() {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno == null) {
            mostrarError("Seleccioná un turno.");
            return;
        }
        if (!contexto.gestorTurnos.marcarAtendido(turno.getId())) {
            mostrarError("No se pudo marcar como atendido.");
            return;
        }
        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Turno marcado como atendido.");
    }

    private void nuevaConsulta() {
        asegurarCombosTurnos();
        JTextField fecha = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        JTextField resumen = new JTextField();
        JTextField diagnostico = new JTextField();
        JTextField estudios = new JTextField();
        JComboBox<String> pacientes = new JComboBox<>(comboPacientesTurno.getModel());

        if (!mostrarFormulario("Nueva consulta", new String[] {"Paciente", "Fecha", "Resumen", "Diagnóstico", "Estudios"},
            new JComponent[] {pacientes, fecha, resumen, diagnostico, estudios})) {
            return;
        }

        try {
            String dni = extraerValorSeleccionado((String) pacientes.getSelectedItem());
            boolean ok = contexto.gestorHistoriales.registrarConsulta(RolUsuario.MEDICO, dni, resumen.getText().trim(), diagnostico.getText().trim(), estudios.getText().trim());
            if (!ok) {
                mostrarError("No se pudo registrar la consulta.");
                return;
            }
            contexto.guardarTodo();
            refrescarTodo();
            mostrarInfo("Consulta registrada.");
        } catch (Exception ex) {
            mostrarError("No se pudo registrar la consulta.");
        }
    }

    private void actualizarDiagnostico() {
        HistorialClinico historial = obtenerHistorialSeleccionado();
        if (historial == null) {
            mostrarError("Seleccioná un historial.");
            return;
        }

        JTextField diagnostico = new JTextField();
        if (!mostrarFormulario("Actualizar diagnóstico", new String[] {"Nuevo diagnóstico"}, new JComponent[] {diagnostico})) {
            return;
        }

        if (!contexto.gestorHistoriales.actualizarDiagnostico(historial.getDniPaciente(), diagnostico.getText().trim())) {
            mostrarError("No se pudo actualizar el diagnóstico.");
            return;
        }
        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Diagnóstico actualizado.");
    }

    private void actualizarEstudios() {
        HistorialClinico historial = obtenerHistorialSeleccionado();
        if (historial == null) {
            mostrarError("Seleccioná un historial.");
            return;
        }

        JTextField estudios = new JTextField();
        if (!mostrarFormulario("Actualizar estudios", new String[] {"Nuevos estudios"}, new JComponent[] {estudios})) {
            return;
        }

        if (!contexto.gestorHistoriales.actualizarEstudios(historial.getDniPaciente(), estudios.getText().trim())) {
            mostrarError("No se pudieron actualizar los estudios.");
            return;
        }
        contexto.guardarTodo();
        refrescarTodo();
        mostrarInfo("Estudios actualizados.");
    }

    private boolean mostrarFormulario(String titulo, String[] etiquetas, JComponent[] campos) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 6, 6, 6));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        for (int i = 0; i < etiquetas.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel etiqueta = new JLabel(etiquetas[i]);
            etiqueta.setFont(HealthHubSwing.fuente(13, Font.BOLD));
            panel.add(etiqueta, gbc);

            gbc.gridx = 1;
            if (campos[i] instanceof JTextField textField) {
                textField.setColumns(20);
            }
            panel.add(campos[i], gbc);
        }

        int respuesta = JOptionPane.showConfirmDialog(this, panel, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return respuesta == JOptionPane.OK_OPTION;
    }

    private void mostrarDetallesMedicoSeleccionado() {
        Medico medico = obtenerMedicoSeleccionado();
        if (medico == null) {
            detallesMedico.setText("Seleccioná un médico para ver sus disponibilidades y notificaciones.");
            refrescarNotificaciones();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Matrícula: ").append(medico.getMatricula()).append('\n');
        sb.append("Nombre: ").append(medico.getNombre()).append(' ').append(medico.getApellido()).append('\n');
        sb.append("Especialidad: ").append(medico.getEspecialidad()).append("\n\n");
        sb.append("Disponibilidades:\n");
        for (Disponibilidad disponibilidad : contexto.gestorMedicos.consultarDisponibilidad(medico.getMatricula())) {
            sb.append("- ").append(disponibilidad).append('\n');
        }
        if (contexto.gestorMedicos.consultarDisponibilidad(medico.getMatricula()).isEmpty()) {
            sb.append("Sin disponibilidades registradas.\n");
        }
        detallesMedico.setText(sb.toString());
        refrescarNotificaciones();
    }

    private void mostrarDetallesTurnoSeleccionado() {
        Turno turno = obtenerTurnoSeleccionado();
        if (turno == null) {
            detallesTurno.setText("Seleccioná un turno para ver sus detalles.");
            return;
        }

        String paciente = contexto.gestorPacientes.buscarPorDni(turno.getDniPaciente())
            .map(Paciente::nombreCompleto)
            .orElse(turno.getDniPaciente());
        String medico = contexto.gestorMedicos.buscarMedico(turno.getMatriculaMedico())
            .map(m -> m.getNombre() + " " + m.getApellido())
            .orElse(turno.getMatriculaMedico());

        detallesTurno.setText(
            "ID: " + turno.getId() + "\n" +
            "Paciente: " + paciente + "\n" +
            "Médico: " + medico + "\n" +
            "Fecha/Hora: " + turno.getFechaHora().format(FORMATO_FECHA_HORA) + "\n" +
            "Estado: " + turno.getEstado() + "\n" +
            "Sobreturno: " + (turno.isSobreturno() ? "Sí" : "No")
        );
    }

    private void mostrarDetallesHistorialSeleccionado() {
        HistorialClinico historial = obtenerHistorialSeleccionado();
        if (historial == null) {
            detallesHistorial.setText("Seleccioná un historial para ver sus entradas.");
            return;
        }
        detallesHistorial.setText(formatearHistorial(historial));
    }

    private String formatearHistorial(HistorialClinico historial) {
        StringBuilder sb = new StringBuilder();
        sb.append("DNI: ").append(historial.getDniPaciente()).append('\n');
        sb.append("Entradas: ").append(historial.getEntradas().size()).append("\n\n");
        historial.getEntradas().forEach(entrada -> {
            sb.append("Fecha: ").append(entrada.getFecha().format(FORMATO_FECHA_HORA)).append('\n');
            sb.append("Resumen: ").append(entrada.getResumen()).append('\n');
            sb.append("Diagnóstico: ").append(entrada.getDiagnostico()).append('\n');
            sb.append("Estudios: ").append(entrada.getEstudios()).append("\n\n");
        });
        if (historial.getEntradas().isEmpty()) {
            sb.append("Sin entradas registradas.");
        }
        return sb.toString();
    }

    private void asegurarCombosTurnos() {
        if (comboPacientesTurno == null) {
            comboPacientesTurno = new JComboBox<>();
        }
        if (comboMedicosTurno == null) {
            comboMedicosTurno = new JComboBox<>();
        }

        DefaultComboBoxModel<String> modeloPacientes = new DefaultComboBoxModel<>();
        contexto.gestorPacientes.listarTodos().stream()
            .sorted(Comparator.comparing(Paciente::getApellido).thenComparing(Paciente::getNombre))
            .forEach(paciente -> modeloPacientes.addElement(paciente.getDni() + " - " + paciente.nombreCompleto()));
        comboPacientesTurno.setModel(modeloPacientes);

        DefaultComboBoxModel<String> modeloMedicos = new DefaultComboBoxModel<>();
        contexto.gestorMedicos.listarTodos().stream()
            .sorted(Comparator.comparing(Medico::getApellido).thenComparing(Medico::getNombre))
            .forEach(medico -> modeloMedicos.addElement(medico.getMatricula() + " - " + medico.getNombre() + " " + medico.getApellido()));
        comboMedicosTurno.setModel(modeloMedicos);
    }

    private String extraerValorSeleccionado(String valor) {
        if (valor == null) {
            return null;
        }
        int separador = valor.indexOf(" - ");
        return separador >= 0 ? valor.substring(0, separador) : valor;
    }

    private Paciente obtenerPacienteSeleccionado() {
        if (tablaPacientes == null) {
            return null;
        }
        int fila = tablaPacientes.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modelo = tablaPacientes.convertRowIndexToModel(fila);
        String dni = String.valueOf(modeloPacientes.getValueAt(modelo, 0));
        return contexto.gestorPacientes.buscarPorDni(dni).orElse(null);
    }

    private Medico obtenerMedicoSeleccionado() {
        if (tablaMedicos == null) {
            return null;
        }
        int fila = tablaMedicos.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modelo = tablaMedicos.convertRowIndexToModel(fila);
        String matricula = String.valueOf(modeloMedicos.getValueAt(modelo, 0));
        return contexto.gestorMedicos.buscarMedico(matricula).orElse(null);
    }

    private Turno obtenerTurnoSeleccionado() {
        if (tablaTurnos == null) {
            return null;
        }
        int fila = tablaTurnos.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modelo = tablaTurnos.convertRowIndexToModel(fila);
        String id = String.valueOf(modeloTurnos.getValueAt(modelo, 0));
        return contexto.gestorTurnos.buscarTurno(id).orElse(null);
    }

    private HistorialClinico obtenerHistorialSeleccionado() {
        if (tablaHistoriales == null) {
            return null;
        }
        int fila = tablaHistoriales.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modelo = tablaHistoriales.convertRowIndexToModel(fila);
        String dni = String.valueOf(modeloHistoriales.getValueAt(modelo, 0));
        return contexto.gestorHistoriales.verHistorial(dni);
    }

    private String obtenerMatriculaSeleccionada() {
        Medico medico = obtenerMedicoSeleccionado();
        return medico != null ? medico.getMatricula() : null;
    }

    private void mostrarPestaña(int index) {
        javax.swing.JTabbedPane tabs = encontrarTabs();
        if (tabs != null) {
            tabs.setSelectedIndex(index);
        }
    }

    private javax.swing.JTabbedPane encontrarTabs() {
        Component contenedor = getContentPane().getComponentCount() > 0 ? getContentPane().getComponent(0) : null;
        if (contenedor instanceof JPanel panel && panel.getComponentCount() > 0 && panel.getComponent(0) instanceof javax.swing.JTabbedPane tabs) {
            return tabs;
        }
        return null;
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Health Hub", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Health Hub", JOptionPane.ERROR_MESSAGE);
    }
}
