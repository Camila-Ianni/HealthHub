package com.healthhub.gui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class HealthHubIcons {

    private HealthHubIcons() {
    }

    public static Icon icono(String nombre, int size, Color color) {
        return new IconVector(nombre, size, color != null ? color : HealthHubSwing.ACENTO_OSCURO);
    }

    public static Icon icono(String nombre, int size) {
        return icono(nombre, size, HealthHubSwing.ACENTO_OSCURO);
    }

    private static final class IconVector implements Icon {

        private final String nombre;
        private final int size;
        private final Color color;

        private IconVector(String nombre, int size, Color color) {
            this.nombre = nombre;
            this.size = size;
            this.color = color;
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
            g2.setStroke(new BasicStroke(Math.max(1.6f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (nombre) {
                case "agenda" -> dibujarAgenda(g2, x, y);
                case "pacientes" -> dibujarPaciente(g2, x, y);
                case "medicos" -> dibujarMedico(g2, x, y);
                case "turnos" -> dibujarTurno(g2, x, y);
                case "historiales" -> dibujarHistorial(g2, x, y);
                case "estadisticas" -> dibujarEstadisticas(g2, x, y);
                case "notificaciones" -> dibujarNotificacion(g2, x, y);
                case "salir" -> dibujarSalir(g2, x, y);
                case "login" -> dibujarLogin(g2, x, y);
                default -> dibujarGenerico(g2, x, y);
            }

            g2.dispose();
        }

        private void dibujarAgenda(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 3, y + 4, size - 6, size - 7, 5, 5);
            g2.drawLine(x + 7, y + 2, x + 7, y + 7);
            g2.drawLine(x + size - 7, y + 2, x + size - 7, y + 7);
            g2.drawLine(x + 5, y + 10, x + size - 5, y + 10);
            g2.drawLine(x + 8, y + 15, x + 12, y + 15);
            g2.drawLine(x + 14, y + 15, x + 18, y + 15);
        }

        private void dibujarPaciente(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 7, y + 3, size - 14, size - 14);
            g2.drawArc(x + 4, y + 12, size - 8, size - 7, 20, 140);
        }

        private void dibujarMedico(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 4, y + 4, size - 8, size - 8);
            g2.drawLine(x + size / 2, y + 8, x + size / 2, y + size - 8);
            g2.drawLine(x + 8, y + size / 2, x + size - 8, y + size / 2);
            g2.drawLine(x + size / 2, y + 8, x + size / 2, y + size - 8);
        }

        private void dibujarTurno(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 4, y + 4, size - 8, size - 8);
            g2.drawLine(x + size / 2, y + size / 2, x + size / 2 + 4, y + size / 2 - 5);
            g2.drawLine(x + size / 2, y + size / 2, x + size / 2, y + 10);
        }

        private void dibujarHistorial(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 5, y + 4, size - 10, size - 7, 5, 5);
            g2.drawLine(x + 13, y + 4, x + 13, y + size - 3);
            g2.drawLine(x + 16, y + 10, x + size - 7, y + 10);
            g2.drawLine(x + 16, y + 15, x + size - 10, y + 15);
        }

        private void dibujarEstadisticas(Graphics2D g2, int x, int y) {
            g2.drawLine(x + 5, y + size - 5, x + 5, y + 11);
            g2.drawLine(x + 11, y + size - 5, x + 11, y + 6);
            g2.drawLine(x + 17, y + size - 5, x + 17, y + 14);
        }

        private void dibujarNotificacion(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 5, y + 4, size - 10, size - 10);
            g2.drawLine(x + size / 2, y + 14, x + size / 2, y + size - 7);
            g2.drawLine(x + size / 2, y + size - 5, x + size / 2, y + size - 5);
            g2.fillOval(x + size / 2 - 1, y + size - 6, 3, 3);
        }

        private void dibujarSalir(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 4, y + 5, size - 12, size - 10, 4, 4);
            g2.drawLine(x + 9, y + size / 2, x + size - 6, y + size / 2);
            g2.drawLine(x + size - 10, y + size / 2 - 4, x + size - 6, y + size / 2);
            g2.drawLine(x + size - 10, y + size / 2 + 4, x + size - 6, y + size / 2);
        }

        private void dibujarLogin(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 4, y + 5, size - 12, size - 10, 4, 4);
            g2.drawLine(x + 8, y + size / 2, x + size - 8, y + size / 2);
            g2.drawLine(x + size - 12, y + size / 2 - 4, x + size - 8, y + size / 2);
            g2.drawLine(x + size - 12, y + size / 2 + 4, x + size - 8, y + size / 2);
        }

        private void dibujarGenerico(Graphics2D g2, int x, int y) {
            g2.drawRoundRect(x + 2, y + 2, size - 5, size - 5, 8, 8);
        }
    }
}
