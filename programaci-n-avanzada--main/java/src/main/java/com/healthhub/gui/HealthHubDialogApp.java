package com.healthhub.gui;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class HealthHubDialogApp {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private HealthHubContext contexto;

    public void iniciar() {
        contexto = HealthHubContext.crearDesdePersistencia();

        while (true) {
            String legajo = pedirTexto("Ingresá tu legajo para acceder:", "Acceso HealthHub");
            if (legajo == null) {
                contexto.guardarTodo();
                return;
            }

            Optional<Empleado> empleadoOpt = contexto.gestorEmpleados.buscarPorLegajo(legajo.trim());
            if (empleadoOpt.isEmpty()) {
                mostrarMensaje("No se encontró un empleado con ese legajo.", "Acceso", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            ejecutarSesion(empleadoOpt.get());
        }
    }

    private void ejecutarSesion(Empleado empleado) {
        while (true) {
            String opcion = seleccionar("Sesión de " + empleado.getNombre() + " (" + empleado.getRol() + ")", opcionesMenu());
            if (opcion == null || "Cerrar sesión".equals(opcion)) {
                contexto.guardarTodo();
                return;
            }

            switch (opcion) {
                case "Registrar paciente" -> registrarPaciente();
                case "Buscar paciente" -> buscarPaciente();
                case "Modificar paciente" -> modificarPaciente();
                case "Registrar médico" -> registrarMedico();
                case "Agregar disponibilidad" -> agregarDisponibilidad();
                case "Crear turno" -> crearTurno();
                case "Cancelar turno" -> cancelarTurno();
                case "Reprogramar turno" -> reprogramarTurno();
                case "Marcar turno atendido" -> marcarTurnoAtendido();
                case "Registrar consulta" -> registrarConsulta();
                case "Ver historial" -> verHistorial();
                case "Ver notificaciones" -> verNotificaciones();
                case "Ver resumen" -> verResumen();
                default -> {
                }
            }
        }
    }

    private String[] opcionesMenu() {
        return new String[] {
            "Registrar paciente",
            "Buscar paciente",
            "Modificar paciente",
            "Registrar médico",
            "Agregar disponibilidad",
            "Crear turno",
            "Cancelar turno",
            "Reprogramar turno",
            "Marcar turno atendido",
            "Registrar consulta",
            "Ver historial",
            "Ver notificaciones",
            "Ver resumen",
            "Cerrar sesión"
        };
    }

    private void registrarPaciente() {
        String dni = pedirTexto("DNI:", "Nuevo paciente");
        String nombre = pedirTexto("Nombre:", "Nuevo paciente");
        String apellido = pedirTexto("Apellido:", "Nuevo paciente");
        String telefono = pedirTexto("Teléfono:", "Nuevo paciente");
        String obraSocial = pedirTexto("Obra social:", "Nuevo paciente");

        if (dni == null || nombre == null || apellido == null || telefono == null || obraSocial == null) {
            return;
        }

        boolean creado = contexto.gestorPacientes.registrarPaciente(new Paciente(
            dni.trim(), nombre.trim(), apellido.trim(), telefono.trim(), obraSocial.trim()
        ));

        if (creado) {
            contexto.guardarTodo();
            mostrarMensaje("Paciente registrado correctamente.", "Pacientes", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("Ya existe un paciente con ese DNI.", "Pacientes", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void buscarPaciente() {
        String criterio = pedirTexto("Buscá por DNI o nombre completo:", "Buscar paciente");
        if (criterio == null) {
            return;
        }

        String texto;
        Optional<Paciente> porDni = contexto.gestorPacientes.buscarPorDni(criterio.trim());
        if (porDni.isPresent()) {
            texto = formatearPaciente(porDni.get());
        } else {
            List<Paciente> encontrados = contexto.gestorPacientes.buscarPorNombreCompleto(criterio.trim());
            if (encontrados.isEmpty()) {
                mostrarMensaje("No se encontraron pacientes.", "Pacientes", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Paciente paciente : encontrados) {
                sb.append(formatearPaciente(paciente)).append("\n\n");
            }
            texto = sb.toString();
        }

        mostrarTexto("Resultado de búsqueda", texto);
    }

    private void modificarPaciente() {
        String dni = pedirTexto("DNI del paciente a modificar:", "Modificar paciente");
        if (dni == null) {
            return;
        }

        Optional<Paciente> pacienteOpt = contexto.gestorPacientes.buscarPorDni(dni.trim());
        if (pacienteOpt.isEmpty()) {
            mostrarMensaje("No existe un paciente con ese DNI.", "Pacientes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = pedirTexto("Nombre:", pacienteOpt.get().getNombre());
        String apellido = pedirTexto("Apellido:", pacienteOpt.get().getApellido());
        String telefono = pedirTexto("Teléfono:", pacienteOpt.get().getTelefono());
        String obraSocial = pedirTexto("Obra social:", pacienteOpt.get().getObraSocial());

        if (nombre == null || apellido == null || telefono == null || obraSocial == null) {
            return;
        }

        boolean modificado = contexto.gestorPacientes.modificarPaciente(
            dni.trim(), nombre.trim(), apellido.trim(), telefono.trim(), obraSocial.trim()
        );

        if (modificado) {
            contexto.guardarTodo();
            mostrarMensaje("Paciente actualizado.", "Pacientes", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void registrarMedico() {
        String matricula = pedirTexto("Matrícula:", "Nuevo médico");
        String nombre = pedirTexto("Nombre:", "Nuevo médico");
        String apellido = pedirTexto("Apellido:", "Nuevo médico");
        String especialidad = pedirTexto("Especialidad:", "Nuevo médico");

        if (matricula == null || nombre == null || apellido == null || especialidad == null) {
            return;
        }

        boolean creado = contexto.gestorMedicos.registrarMedico(new Medico(
            matricula.trim(), nombre.trim(), apellido.trim(), especialidad.trim()
        ));

        if (creado) {
            contexto.guardarTodo();
            mostrarMensaje("Médico registrado correctamente.", "Médicos", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("Ya existe un médico con esa matrícula.", "Médicos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agregarDisponibilidad() {
        String matricula = pedirTexto("Matrícula del médico:", "Disponibilidad");
        if (matricula == null) {
            return;
        }

        Optional<Medico> medicoOpt = contexto.gestorMedicos.buscarMedico(matricula.trim());
        if (medicoOpt.isEmpty()) {
            mostrarMensaje("No existe un médico con esa matrícula.", "Disponibilidad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DayOfWeek dia = seleccionarDia();
        if (dia == null) {
            return;
        }

        LocalTime inicio = pedirHora("Hora de inicio (HH:mm):");
        if (inicio == null) {
            return;
        }

        LocalTime fin = pedirHora("Hora de fin (HH:mm):");
        if (fin == null) {
            return;
        }

        boolean agregado = contexto.gestorMedicos.agregarDisponibilidad(matricula.trim(), new Disponibilidad(dia, inicio, fin));
        if (agregado) {
            contexto.guardarTodo();
            mostrarMensaje("Disponibilidad agregada.", "Disponibilidad", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("No se pudo agregar la disponibilidad. Revisá solapamientos o matrícula.", "Disponibilidad", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void crearTurno() {
        String dni = pedirTexto("DNI del paciente:", "Nuevo turno");
        String matricula = pedirTexto("Matrícula del médico:", "Nuevo turno");
        LocalDateTime fechaHora = pedirFechaHora("Fecha y hora (yyyy-MM-dd HH:mm):");
        boolean sobreturno = confirmar("¿Es sobreturno?");

        if (dni == null || matricula == null || fechaHora == null) {
            return;
        }

        Optional<Turno> turno = contexto.gestorTurnos.crearTurno(dni.trim(), matricula.trim(), fechaHora, sobreturno);
        if (turno.isPresent()) {
            contexto.guardarTodo();
            mostrarMensaje("Turno creado: " + turno.get().getId(), "Turnos", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("No se pudo crear el turno. Verificá disponibilidad, horarios y datos.", "Turnos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelarTurno() {
        String turnoId = pedirTexto("ID del turno:", "Cancelar turno");
        if (turnoId == null) {
            return;
        }

        boolean cancelado = contexto.gestorTurnos.cancelarTurno(turnoId.trim());
        if (cancelado) {
            contexto.guardarTodo();
            mostrarMensaje("Turno cancelado.", "Turnos", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("No se pudo cancelar el turno.", "Turnos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void reprogramarTurno() {
        String turnoId = pedirTexto("ID del turno:", "Reprogramar turno");
        LocalDateTime nuevaFechaHora = pedirFechaHora("Nueva fecha y hora (yyyy-MM-dd HH:mm):");
        if (turnoId == null || nuevaFechaHora == null) {
            return;
        }

        boolean reprogramado = contexto.gestorTurnos.reprogramarTurno(turnoId.trim(), nuevaFechaHora);
        if (reprogramado) {
            contexto.guardarTodo();
            mostrarMensaje("Turno reprogramado.", "Turnos", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("No se pudo reprogramar el turno.", "Turnos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void marcarTurnoAtendido() {
        String turnoId = pedirTexto("ID del turno:", "Marcar atendido");
        if (turnoId == null) {
            return;
        }

        boolean marcado = contexto.gestorTurnos.marcarAtendido(turnoId.trim());
        if (marcado) {
            contexto.guardarTodo();
            mostrarMensaje("Turno marcado como atendido.", "Turnos", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("No se pudo marcar el turno.", "Turnos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void registrarConsulta() {
        String dni = pedirTexto("DNI del paciente:", "Registrar consulta");
        if (dni == null) {
            return;
        }

        String resumen = pedirTexto("Resumen de la consulta:", "Registrar consulta");
        String diagnostico = pedirTexto("Diagnóstico:", "Registrar consulta");
        String estudios = pedirTexto("Estudios:", "Registrar consulta");

        if (resumen == null || diagnostico == null || estudios == null) {
            return;
        }

        boolean ok = contexto.gestorHistoriales.registrarConsulta(
            RolUsuario.MEDICO,
            dni.trim(),
            resumen.trim(),
            diagnostico.trim(),
            estudios.trim()
        );

        if (ok) {
            contexto.guardarTodo();
            mostrarMensaje("Consulta registrada.", "Historiales", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("No se pudo registrar la consulta.", "Historiales", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verHistorial() {
        String dni = pedirTexto("DNI del paciente:", "Ver historial");
        if (dni == null) {
            return;
        }

        HistorialClinico historial = contexto.gestorHistoriales.verHistorial(dni.trim());
        if (historial == null) {
            mostrarMensaje("No existe historial para ese paciente.", "Historiales", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Historial de DNI ").append(historial.getDniPaciente()).append("\n\n");
        if (historial.getEntradas().isEmpty()) {
            sb.append("Sin entradas registradas.");
        } else {
            for (var entrada : historial.getEntradas()) {
                sb.append(entrada).append("\n\n");
            }
        }
        mostrarTexto("Historial clínico", sb.toString());
    }

    private void verNotificaciones() {
        String matricula = pedirTexto("Matrícula del médico:", "Notificaciones");
        if (matricula == null) {
            return;
        }

        List<String> notificaciones = contexto.gestorNotificaciones.verNotificaciones(matricula.trim());
        if (notificaciones.isEmpty()) {
            mostrarMensaje("No hay notificaciones.", "Notificaciones", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String notificacion : notificaciones) {
            sb.append("- ").append(notificacion).append("\n");
        }
        mostrarTexto("Notificaciones", sb.toString());
    }

    private void verResumen() {
        String texto = "Pacientes: " + contexto.gestorPacientes.listarTodos().size()
            + "\nMédicos: " + contexto.gestorMedicos.listarTodos().size()
            + "\nTurnos: " + contexto.gestorTurnos.listarTodos().size()
            + "\nHistoriales: " + contexto.gestorHistoriales.listarTodos().size()
            + "\nEmpleados: " + contexto.gestorEmpleados.listarTodos().size();
        mostrarTexto("Resumen", texto);
    }

    private String pedirTexto(String mensaje, String titulo) {
        return (String) JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE, null, null, null);
    }

    private boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(null, mensaje, "Confirmación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private String seleccionar(String titulo, String[] opciones) {
        return (String) JOptionPane.showInputDialog(null, titulo, titulo, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
    }

    private DayOfWeek seleccionarDia() {
        DayOfWeek[] dias = DayOfWeek.values();
        return (DayOfWeek) JOptionPane.showInputDialog(null, "Día de la semana:", "Disponibilidad", JOptionPane.QUESTION_MESSAGE, null, dias, dias[0]);
    }

    private LocalDateTime pedirFechaHora(String mensaje) {
        String valor = pedirTexto(mensaje, "Fecha y hora");
        if (valor == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(valor.trim(), FORMATO_FECHA_HORA);
        } catch (DateTimeParseException e) {
            mostrarMensaje("Formato inválido. Usá yyyy-MM-dd HH:mm.", "Fecha y hora", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private LocalTime pedirHora(String mensaje) {
        String valor = pedirTexto(mensaje, "Hora");
        if (valor == null) {
            return null;
        }

        try {
            return LocalTime.parse(valor.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            mostrarMensaje("Formato inválido. Usá HH:mm.", "Hora", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipo);
    }

    private void mostrarTexto(String titulo, String contenido) {
        JTextArea area = new JTextArea(contenido, 18, 60);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(null, new JScrollPane(area), titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatearPaciente(Paciente paciente) {
        return "DNI: " + paciente.getDni()
            + "\nNombre: " + paciente.getNombre() + " " + paciente.getApellido()
            + "\nTeléfono: " + paciente.getTelefono()
            + "\nObra social: " + paciente.getObraSocial();
    }
}