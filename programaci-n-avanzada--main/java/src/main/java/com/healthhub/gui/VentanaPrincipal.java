package com.healthhub.gui;

import com.healthhub.domain.Empleado;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class VentanaPrincipal extends JFrame {

    private final HealthHubContext contexto;
    private final Empleado empleado;
    private final JPanel contenido;
    private final CardLayout tarjetas;

    public VentanaPrincipal(HealthHubContext contexto, Empleado empleado) {
        this.contexto = contexto;
        this.empleado = empleado;
        this.tarjetas = new CardLayout();
        this.contenido = new JPanel(tarjetas);

        setTitle("Health Hub - Dashboard");
        setMinimumSize(new Dimension(1040, 680));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(crearSidebar(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearSidebar() {
        GradientPanel sidebar = new GradientPanel();
        sidebar.setPreferredSize(new Dimension(260, 680));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createEmptyBorder(26, 18, 24, 18));

        JPanel marca = new JPanel(new BorderLayout(0, 12));
        marca.setOpaque(false);
        marca.add(HealthHubSwing.crearLogo(74, 74), BorderLayout.NORTH);

        JLabel usuario = new JLabel("<html><div style='text-align:center;'>"
            + empleado.getNombre() + "<br><span style='font-size:10px;'>" + empleado.getRol() + "</span></div></html>");
        usuario.setHorizontalAlignment(SwingConstants.CENTER);
        usuario.setForeground(Color.WHITE);
        usuario.setFont(HealthHubSwing.fuente(15, Font.BOLD));
        marca.add(usuario, BorderLayout.CENTER);

        JPanel navegacion = new JPanel(new GridLayout(0, 1, 0, 12));
        navegacion.setOpaque(false);
        navegacion.setBorder(BorderFactory.createEmptyBorder(34, 0, 0, 0));
        navegacion.add(botonNav("Inicio", "estadisticas", () -> mostrarTarjeta("dashboard")));
        navegacion.add(botonNav("Pacientes", "pacientes", () -> abrirGestion("pacientes")));
        navegacion.add(botonNav("Médicos", "medicos", () -> abrirGestion("medicos")));
        navegacion.add(botonNav("Turnos", "turnos", () -> abrirGestion("turnos")));
        navegacion.add(botonNav("Historiales", "historiales", () -> abrirGestion("historiales")));
        navegacion.add(botonNav("Agenda", "agenda", () -> abrirAgenda()));
        navegacion.add(botonNav("Estadísticas", "estadisticas", () -> abrirEstadisticas()));

        JButton cerrar = botonNav("Cerrar sesión", "salir", () -> cerrarSesion());

        sidebar.add(marca, BorderLayout.NORTH);
        sidebar.add(navegacion, BorderLayout.CENTER);
        sidebar.add(cerrar, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton botonNav(String texto, String icono, Runnable accion) {
        JButton boton = new JButton(texto, HealthHubIcons.icono(icono, 20, Color.WHITE));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(12);
        boton.setFont(HealthHubSwing.fuente(14, Font.BOLD));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(255, 255, 255, 34));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private JPanel crearContenido() {
        JPanel base = new JPanel(new BorderLayout());
        base.setBackground(HealthHubSwing.FONDO);

        JPanel encabezado = HealthHubSwing.panelConTitulo(
            "Bienvenida, " + empleado.getNombre(),
            "Centro operativo para " + empleado.getRol()
        );
        base.add(encabezado, BorderLayout.NORTH);

        contenido.setBackground(HealthHubSwing.FONDO);
        contenido.add(crearDashboard(), "dashboard");
        contenido.add(crearPanelSimple("Pacientes", "Alta, edición y búsqueda rápida desde un solo panel."), "pacientes");
        contenido.add(crearPanelSimple("Médicos", "Registro, disponibilidades y seguimiento de agenda."), "medicos");
        contenido.add(crearPanelSimple("Turnos", "Creación, reprogramación, cancelación y atención."), "turnos");
        contenido.add(crearPanelSimple("Historiales", "Consultas, diagnósticos y estudios clínicos."), "historiales");

        base.add(contenido, BorderLayout.CENTER);
        return base;
    }

    private JPanel crearDashboard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 18, 18);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(HealthHubSwing.tarjeta("Pacientes registrados", String.valueOf(contexto.gestorPacientes.listarTodos().size())), gbc);

        gbc.gridx = 1;
        panel.add(HealthHubSwing.tarjeta("Médicos activos", String.valueOf(contexto.gestorMedicos.listarTodos().size())), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(HealthHubSwing.tarjeta("Turnos cargados", String.valueOf(contexto.gestorTurnos.listarTodos().size())), gbc);

        gbc.gridx = 1;
        panel.add(HealthHubSwing.tarjeta("Área", empleado.getRol().name()), gbc);

        return panel;
    }

    private JPanel crearPanelSimple(String titulo, String descripcion) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(HealthHubSwing.FONDO);

        JPanel tarjeta = new JPanel(new BorderLayout(0, 10));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(28, 34, 28, 34)
        ));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(HealthHubSwing.fuente(24, Font.BOLD));
        tituloLabel.setForeground(HealthHubSwing.ACENTO_OSCURO);

        JLabel descripcionLabel = new JLabel(descripcion);
        descripcionLabel.setFont(HealthHubSwing.fuente(14, Font.PLAIN));
        descripcionLabel.setForeground(HealthHubSwing.TEXTO_SUAVE);

        tarjeta.add(tituloLabel, BorderLayout.NORTH);
        tarjeta.add(descripcionLabel, BorderLayout.CENTER);
        panel.add(tarjeta);
        return panel;
    }

    private void mostrarTarjeta(String nombre) {
        tarjetas.show(contenido, nombre);
    }

    private void abrirAgenda() {
        new VentanaAgenda(contexto).setVisible(true);
    }

    private void abrirEstadisticas() {
        new VentanaEstadisticas(contexto).setVisible(true);
    }

    private void abrirGestion(String pestaña) {
        new VentanaGestionGeneral(contexto, pestaña).setVisible(true);
    }

    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Desea cerrar la sesión actual?",
            "Cerrar sesión",
            JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            contexto.guardarTodo();
            new VentanaLogin(contexto).setVisible(true);
            dispose();
        }
    }
}
