package com.healthhub.gui;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;
import com.healthhub.service.GestorEmpleados;
import com.healthhub.service.GestorHistoriales;
import com.healthhub.service.GestorMedicos;
import com.healthhub.service.GestorNotificaciones;
import com.healthhub.service.GestorPacientes;
import com.healthhub.service.GestorTurnos;
import com.healthhub.service.Persistencia;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VentanaLogin extends JFrame {

    private final HealthHubContext contexto;
    private final JTextField campoLegajo;

    public VentanaLogin() {
        this(HealthHubContext.crearDesdeArchivos());
    }

    public VentanaLogin(HealthHubContext contexto) {
        this.contexto = contexto;
        this.campoLegajo = new JTextField();

        setTitle("Health Hub - Acceso");
        setUndecorated(true);
        setSize(400, 500);
        setMinimumSize(new Dimension(360, 460));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(HealthHubSwing.FONDO);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        GradientPanel panel = new GradientPanel();
        panel.setPreferredSize(new Dimension(400, 170));
        panel.setLayout(new GridBagLayout());

        JLabel logo = HealthHubSwing.crearLogo(92, 92);
        JLabel titulo = new JLabel("HEALTH HUB", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(HealthHubSwing.fuente(24, Font.BOLD));

        JLabel subtitulo = new JLabel("Gestión clínica y agenda", SwingConstants.CENTER);
        subtitulo.setForeground(new Color(255, 255, 255, 210));
        subtitulo.setFont(HealthHubSwing.fuente(12, Font.PLAIN));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(logo, gbc);

        gbc.gridy = 1;
        panel.add(titulo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(2, 0, 0, 0);
        panel.add(subtitulo, gbc);

        return panel;
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(34, 42, 28, 42));

        JLabel etiqueta = new JLabel("Legajo");
        etiqueta.setFont(HealthHubSwing.fuente(14, Font.BOLD));
        etiqueta.setForeground(HealthHubSwing.TEXTO);

        campoLegajo.setFont(HealthHubSwing.fuente(16, Font.PLAIN));
        campoLegajo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 224, 224)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        campoLegajo.setToolTipText("Ingresá tu legajo para acceder");
        campoLegajo.addActionListener(e -> ingresar());

        JLabel ayuda = new JLabel("Legajos de prueba: 1000 administrador, 2000 recepción, 3000 médico");
        ayuda.setFont(HealthHubSwing.fuente(12, Font.PLAIN));
        ayuda.setForeground(HealthHubSwing.TEXTO_SUAVE);

        JButton botonIngresar = HealthHubSwing.botonPrincipal("INGRESAR");
        botonIngresar.setIcon(HealthHubIcons.icono("login", 16, Color.WHITE));
        botonIngresar.addActionListener(e -> ingresar());

        JButton botonSalir = HealthHubSwing.botonTexto("Salir");
        botonSalir.setIcon(HealthHubIcons.icono("salir", 16, HealthHubSwing.ACENTO_OSCURO));
        botonSalir.addActionListener(e -> dispose());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(etiqueta, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 22, 0);
        panel.add(campoLegajo, gbc);

        gbc.gridy = 2;
        gbc.ipady = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        panel.add(ayuda, gbc);

        gbc.gridy = 3;
        gbc.ipady = 12;
        gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(botonIngresar, gbc);

        gbc.gridy = 4;
        gbc.ipady = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(botonSalir, gbc);

        return panel;
    }

    private void ingresar() {
        String legajo = campoLegajo.getText().trim();
        if (legajo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresá tu legajo para continuar.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Empleado> empleado = contexto.gestorEmpleados.buscarPorLegajo(legajo);
        if (empleado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró un empleado con ese legajo.", "Acceso", JOptionPane.ERROR_MESSAGE);
            return;
        }

        VentanaPrincipal principal = new VentanaPrincipal(contexto, empleado.get());
        principal.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HealthHubSwing.configurarLookAndFeel();
            new VentanaLogin().setVisible(true);
        });
    }
}

class HealthHubContext {

    final GestorPacientes gestorPacientes;
    final GestorMedicos gestorMedicos;
    final GestorTurnos gestorTurnos;
    final GestorHistoriales gestorHistoriales;
    final GestorEmpleados gestorEmpleados;
    final GestorNotificaciones gestorNotificaciones;
    final Persistencia persistencia;

    private HealthHubContext() {
        gestorHistoriales = new GestorHistoriales();
        gestorPacientes = new GestorPacientes(gestorHistoriales);
        gestorMedicos = new GestorMedicos();
        gestorNotificaciones = new GestorNotificaciones();
        gestorTurnos = new GestorTurnos(gestorMedicos, gestorNotificaciones);
        gestorEmpleados = new GestorEmpleados();
        persistencia = new Persistencia(Path.of("data"));
    }

    static HealthHubContext crearDesdeArchivos() {
        HealthHubContext contexto = new HealthHubContext();

        List<Medico> medicos = contexto.persistencia.cargarMedicos();
        for (Medico medico : medicos) {
            contexto.gestorMedicos.registrarMedico(medico);
        }

        Map<String, List<Disponibilidad>> disponibilidades = contexto.persistencia.cargarDisponibilidades();
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidades.entrySet()) {
            for (Disponibilidad disponibilidad : entry.getValue()) {
                contexto.gestorMedicos.agregarDisponibilidad(entry.getKey(), disponibilidad);
            }
        }

        List<Paciente> pacientes = contexto.persistencia.cargarPacientes();
        for (Paciente paciente : pacientes) {
            contexto.gestorPacientes.registrarPaciente(paciente);
        }

        List<HistorialClinico> historiales = contexto.persistencia.cargarHistoriales();
        contexto.gestorHistoriales.cargarHistoriales(historiales);

        List<Turno> turnos = contexto.persistencia.cargarTurnos();
        contexto.gestorTurnos.cargarTurnos(turnos);

        contexto.gestorNotificaciones.cargarNotificaciones(contexto.persistencia.cargarNotificaciones());

        List<Empleado> empleados = contexto.persistencia.cargarEmpleados();
        contexto.gestorEmpleados.cargarEmpleados(empleados);

        contexto.asegurarDatosDemo();
        contexto.guardarTodo();

        return contexto;
    }

    void guardarTodo() {
        persistencia.guardarPacientes(gestorPacientes.listarTodos());
        persistencia.guardarMedicos(gestorMedicos.listarTodos());
        persistencia.guardarEmpleados(gestorEmpleados.listarTodos());
        persistencia.guardarTurnos(gestorTurnos.listarTodos());
        persistencia.guardarHistoriales(gestorHistoriales.listarTodos());
        persistencia.guardarDisponibilidades(gestorMedicos.obtenerDisponibilidadesPorMedico());
        persistencia.guardarNotificaciones(gestorNotificaciones.listarTodas());
    }

    private void asegurarDatosDemo() {
        boolean cambios = false;

        if (gestorEmpleados.listarTodos().isEmpty()) {
            gestorEmpleados.registrarEmpleado("1000", "Lucia Romero", RolUsuario.ADMINISTRADOR);
            gestorEmpleados.registrarEmpleado("2000", "Valeria Gomez", RolUsuario.RECEPCIONISTA);
            gestorEmpleados.registrarEmpleado("3000", "Martin Paredes", RolUsuario.MEDICO);
            cambios = true;
        }

        if (gestorMedicos.listarTodos().isEmpty()) {
            gestorMedicos.registrarMedico(new Medico("MP-101", "Martin", "Paredes", "Clinica general"));
            gestorMedicos.registrarMedico(new Medico("MP-202", "Sofia", "Suarez", "Pediatria"));

            gestorMedicos.agregarDisponibilidad("MP-101", new Disponibilidad(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(14, 0)));
            gestorMedicos.agregarDisponibilidad("MP-101", new Disponibilidad(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(15, 0)));
            gestorMedicos.agregarDisponibilidad("MP-202", new Disponibilidad(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(16, 0)));
            gestorMedicos.agregarDisponibilidad("MP-202", new Disponibilidad(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
            cambios = true;
        }

        if (gestorPacientes.listarTodos().isEmpty()) {
            gestorPacientes.registrarPaciente(new Paciente("40111222", "Ana", "Lopez", "1160001000", "OSDE"));
            gestorPacientes.registrarPaciente(new Paciente("40999888", "Bruno", "Diaz", "1160002000", "Swiss Medical"));
            gestorPacientes.registrarPaciente(new Paciente("38777666", "Carla", "Fernandez", "1160003000", "Galeno"));
            cambios = true;
        }

        if (gestorHistoriales.listarTodos().isEmpty()) {
            gestorHistoriales.registrarConsulta(RolUsuario.MEDICO, "40111222", "Consulta inicial", "Paciente estable", "Sin estudios pendientes");
            gestorHistoriales.registrarConsulta(RolUsuario.MEDICO, "40999888", "Control pediatrico", "Sin hallazgos relevantes", "Vacunacion al dia");
            cambios = true;
        }

        if (gestorTurnos.listarTodos().isEmpty()) {
            LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            gestorTurnos.crearTurno("40111222", "MP-101", LocalDateTime.of(proximoLunes, LocalTime.of(9, 0)), false);
            gestorTurnos.crearTurno("40999888", "MP-202", LocalDateTime.of(proximoLunes.plusDays(2), LocalTime.of(10, 0)), false);
            cambios = true;
        }

        if (gestorNotificaciones.listarTodas().isEmpty()) {
            gestorNotificaciones.agregarNotificacion("MP-101", "Bienvenida: ya podés gestionar la agenda desde el panel.");
            gestorNotificaciones.agregarNotificacion("MP-202", "Bienvenida: revisá tus turnos y notificaciones.");
            cambios = true;
        }

        if (cambios) {
            guardarTodo();
        }
    }
}

class HealthHubSwing {

    static final Color ACENTO = new Color(0x009688);
    static final Color ACENTO_OSCURO = new Color(0x008B8B);
    static final Color VERDE_AGUA = new Color(0x42D6C6);
    static final Color FONDO = new Color(0xF7FAFA);
    static final Color TEXTO = new Color(0x263238);
    static final Color TEXTO_SUAVE = new Color(0x607D8B);
    static final String LOGO_PATH = "healthhub.png";

    private HealthHubSwing() {
    }

    static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", fuente(14, Font.PLAIN));
            UIManager.put("OptionPane.buttonFont", fuente(13, Font.BOLD));
            UIManager.put("Button.arc", 14);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("TextComponent.arc", 12);
        } catch (Exception ignored) {
        }
    }

    static Font fuente(int size, int style) {
        return new Font("Segoe UI", style, size);
    }

    static JLabel crearLogo(int ancho, int alto) {
        Path[] candidatos = new Path[] {
            Path.of(LOGO_PATH),
            Path.of("..", LOGO_PATH),
            Path.of("..", "..", LOGO_PATH)
        };

        for (Path candidato : candidatos) {
            if (Files.exists(candidato)) {
                ImageIcon icono = new ImageIcon(candidato.toAbsolutePath().toString());
                if (icono.getIconWidth() > 0) {
                    Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                    return new JLabel(new ImageIcon(imagen), SwingConstants.CENTER);
                }
            }
        }

        JLabel texto = new JLabel("HH", SwingConstants.CENTER);
        texto.setPreferredSize(new Dimension(ancho, alto));
        texto.setOpaque(false);
        texto.setForeground(Color.WHITE);
        texto.setFont(fuente(Math.max(22, ancho / 3), Font.BOLD));
        return texto;
    }

    static JButton botonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(14, Font.BOLD));
        boton.setForeground(Color.WHITE);
        boton.setBackground(ACENTO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    static JButton botonTexto(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(13, Font.BOLD));
        boton.setForeground(ACENTO_OSCURO);
        boton.setBackground(FONDO);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    static JPanel panelConTitulo(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(fuente(26, Font.BOLD));
        tituloLabel.setForeground(TEXTO);

        JLabel subtituloLabel = new JLabel(subtitulo);
        subtituloLabel.setFont(fuente(14, Font.PLAIN));
        subtituloLabel.setForeground(TEXTO_SUAVE);

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(subtituloLabel, BorderLayout.CENTER);
        return panel;
    }

    static JPanel tarjeta(String titulo, String valor) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(fuente(30, Font.BOLD));
        valorLabel.setForeground(ACENTO_OSCURO);

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(fuente(13, Font.BOLD));
        tituloLabel.setForeground(TEXTO_SUAVE);

        tarjeta.add(valorLabel, BorderLayout.NORTH);
        tarjeta.add(tituloLabel, BorderLayout.CENTER);
        return tarjeta;
    }
}

class GradientPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, HealthHubSwing.ACENTO_OSCURO, getWidth(), getHeight(), HealthHubSwing.VERDE_AGUA));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}

class VectorIcon implements javax.swing.Icon {

    private final String tipo;
    private final Color color;
    private final int size;

    VectorIcon(String tipo, Color color, int size) {
        this.tipo = tipo;
        this.color = color;
        this.size = size;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if ("agenda".equals(tipo)) {
            g2.drawRoundRect(x + 3, y + 4, size - 6, size - 7, 4, 4);
            g2.drawLine(x + 7, y + 2, x + 7, y + 7);
            g2.drawLine(x + size - 7, y + 2, x + size - 7, y + 7);
            g2.drawLine(x + 5, y + 10, x + size - 5, y + 10);
        } else if ("persona".equals(tipo)) {
            g2.drawOval(x + 7, y + 3, size - 14, size - 14);
            g2.drawArc(x + 4, y + 12, size - 8, size - 7, 20, 140);
        } else if ("medico".equals(tipo)) {
            g2.drawOval(x + 4, y + 4, size - 8, size - 8);
            g2.drawLine(x + size / 2, y + 8, x + size / 2, y + size - 8);
            g2.drawLine(x + 8, y + size / 2, x + size - 8, y + size / 2);
        } else {
            g2.drawLine(x + 5, y + size - 5, x + 5, y + 11);
            g2.drawLine(x + 11, y + size - 5, x + 11, y + 6);
            g2.drawLine(x + 17, y + size - 5, x + 17, y + 14);
        }

        g2.dispose();
    }
}
