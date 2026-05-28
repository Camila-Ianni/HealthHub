package com.healthhub.gui;

import com.healthhub.domain.EstadoTurno;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Optional;

public class VentanaEstadisticas extends JFrame {

    private final HealthHubContext contexto;

    public VentanaEstadisticas(HealthHubContext contexto) {
        this.contexto = contexto;

        setTitle("Health Hub - Estadísticas");
        setMinimumSize(new Dimension(760, 480));
        setSize(860, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(HealthHubSwing.FONDO);

        add(HealthHubSwing.panelConTitulo("Estadísticas del día", "Indicadores operativos de turnos y sobreturnos"), BorderLayout.NORTH);
        add(crearPanelIndicadores(), BorderLayout.CENTER);
    }

    private JPanel crearPanelIndicadores() {
        LocalDate hoy = LocalDate.now();
        long total = contexto.gestorTurnos.contarTurnosPorFecha(hoy);
        long cancelados = contexto.gestorTurnos.contarTurnosPorEstadoEnFecha(hoy, EstadoTurno.CANCELADO);
        long atendidos = contexto.gestorTurnos.contarTurnosPorEstadoEnFecha(hoy, EstadoTurno.ATENDIDO);
        double porcentajeCancelados = total == 0 ? 0.0 : (cancelados * 100.0) / total;
        double porcentajeAtendidos = total == 0 ? 0.0 : (atendidos * 100.0) / total;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(HealthHubSwing.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 26, 26, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 18, 18);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(HealthHubSwing.tarjeta("Turnos de hoy", String.valueOf(total)), gbc);

        gbc.gridx = 1;
        panel.add(HealthHubSwing.tarjeta("Cancelados", String.format("%.1f%%", porcentajeCancelados)), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(HealthHubSwing.tarjeta("Atendidos", String.format("%.1f%%", porcentajeAtendidos)), gbc);

        gbc.gridx = 1;
        panel.add(crearTarjetaMedicoSobreturnos(), gbc);

        return panel;
    }

    private JPanel crearTarjetaMedicoSobreturnos() {
        Optional<String> matriculaOpt = contexto.gestorTurnos.obtenerMatriculaConMasSobreturnos();
        String valor = "Sin registros";
        String detalle = "Médico con más sobreturnos";

        if (matriculaOpt.isPresent()) {
            String matricula = matriculaOpt.get();
            long cantidad = contexto.gestorTurnos.contarSobreturnosPorMedico(matricula);
            valor = contexto.gestorMedicos.buscarMedico(matricula)
                .map(medico -> medico.getNombre() + " " + medico.getApellido())
                .orElse("Matrícula " + matricula);
            detalle = cantidad + " sobreturnos";
        }

        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 235, 235)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(HealthHubSwing.fuente(20, Font.BOLD));
        valorLabel.setForeground(HealthHubSwing.ACENTO_OSCURO);

        JLabel detalleLabel = new JLabel(detalle);
        detalleLabel.setFont(HealthHubSwing.fuente(13, Font.BOLD));
        detalleLabel.setForeground(HealthHubSwing.TEXTO_SUAVE);

        tarjeta.add(valorLabel, BorderLayout.NORTH);
        tarjeta.add(detalleLabel, BorderLayout.CENTER);
        return tarjeta;
    }
}
