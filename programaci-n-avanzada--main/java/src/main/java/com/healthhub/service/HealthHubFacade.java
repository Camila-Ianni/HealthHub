package com.healthhub.service;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
import com.healthhub.domain.EntradaHistorial;
import com.healthhub.domain.EstadoTurno;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HealthHubFacade {
    private final AutoSaveService autoSaveService;
    private final NotificationService notificationService;
    private final HistorialService historialService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final TurnoService turnoService;
    private final EmpleadoService empleadoService;

    public HealthHubFacade() {
        this.autoSaveService = new AutoSaveService(Path.of("backup.txt"));
        this.notificationService = new NotificationService();
        this.historialService = new HistorialService();
        this.pacienteService = new PacienteService(historialService);
        this.medicoService = new MedicoService();
        this.turnoService = new TurnoService(notificationService);
        this.empleadoService = new EmpleadoService();
        cargarEstadoDesdeBackup();
    }

    public boolean registrarPaciente(String dni, String nombre, String apellido, String telefono, String obraSocial) {
        boolean ok = pacienteService.registrarPaciente(new Paciente(dni, nombre, apellido, telefono, obraSocial));
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean modificarPaciente(String dni, String nombre, String apellido, String telefono, String obraSocial) {
        boolean ok = pacienteService.modificarPaciente(dni, nombre, apellido, telefono, obraSocial);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public Optional<Paciente> buscarPacientePorDni(String dni) {
        return pacienteService.buscarPorDni(dni);
    }

    public List<Paciente> buscarPacientePorNombre(String nombreCompleto) {
        return pacienteService.buscarPorNombreCompleto(nombreCompleto);
    }

    public boolean registrarMedico(String matricula, String nombre, String apellido, String especialidad) {
        boolean ok = medicoService.registrarMedico(new Medico(matricula, nombre, apellido, especialidad));
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean agregarDisponibilidad(String matricula, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        boolean ok = medicoService.agregarDisponibilidad(matricula, new Disponibilidad(dia, horaInicio, horaFin));
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean reemplazarDisponibilidades(String matricula, List<Disponibilidad> disponibilidades) {
        boolean ok = medicoService.reemplazarDisponibilidades(matricula, disponibilidades);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public List<Disponibilidad> consultarDisponibilidad(String matricula) {
        return medicoService.consultarDisponibilidad(matricula);
    }

    public Optional<Turno> crearTurno(String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno) {
        if (pacienteService.buscarPorDni(dniPaciente).isEmpty() || medicoService.buscarMedico(matriculaMedico).isEmpty()) {
            return Optional.empty();
        }
        if (!sobreturno && !medicoService.estaDisponible(matriculaMedico, fechaHora)) {
            return Optional.empty();
        }
        Optional<Turno> turno = turnoService.crearTurno(dniPaciente, matriculaMedico, fechaHora, sobreturno);
        turno.ifPresent(item -> guardarEstado());
        return turno;
    }

    public boolean cancelarTurno(String turnoId) {
        boolean ok = turnoService.cancelarTurno(turnoId);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean reprogramarTurno(String turnoId, LocalDateTime nuevaFechaHora) {
        Optional<Turno> turno = turnoService.buscarTurno(turnoId);
        if (turno.isEmpty()) {
            return false;
        }
        if (!turno.get().isSobreturno() && !medicoService.estaDisponible(turno.get().getMatriculaMedico(), nuevaFechaHora)) {
            return false;
        }
        boolean ok = turnoService.reprogramarTurno(turnoId, nuevaFechaHora);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean marcarTurnoAtendido(String turnoId) {
        boolean ok = turnoService.marcarAtendido(turnoId);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public List<Turno> verTurnosMedico(String matricula) {
        return turnoService.listarTurnosPorMedico(matricula);
    }

    public String cancelarJornadaMedica(String matricula, LocalDate fecha) {
        if (medicoService.buscarMedico(matricula).isEmpty()) {
            return "No existe medico con matricula " + matricula;
        }
        int cancelados = turnoService.cancelarTurnosDeJornada(matricula, fecha);
        guardarEstado();
        return "Jornada cancelada para medico " + matricula + ". Turnos cancelados: " + cancelados;
    }

    public boolean registrarConsultaMedica(String dniPaciente, String resumen, String diagnostico, String estudios) {
        boolean ok = historialService.registrarConsulta(RolUsuario.MEDICO, dniPaciente, resumen, diagnostico, estudios);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean actualizarDiagnostico(String dniPaciente, String diagnostico) {
        boolean ok = historialService.actualizarDiagnosticoUltimaEntrada(RolUsuario.MEDICO, dniPaciente, diagnostico);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public boolean registrarEstudio(String dniPaciente, String estudio) {
        boolean ok = historialService.registrarEstudioUltimaEntrada(RolUsuario.MEDICO, dniPaciente, estudio);
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    public Optional<HistorialClinico> verHistorial(String dniPaciente) {
        return historialService.verHistorial(dniPaciente);
    }

    public List<String> verNotificacionesMedico(String matricula) {
        return notificationService.verNotificaciones(matricula);
    }

    public boolean registrarEmpleado(String legajo, String nombre, RolUsuario rol) {
        boolean ok = empleadoService.registrar(new Empleado(legajo, nombre, rol));
        if (ok) {
            guardarEstado();
        }
        return ok;
    }

    private void guardarEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append("[PACIENTES]\n");
        for (Paciente paciente : pacienteService.listarPacientes()) {
            sb.append(escapar(paciente.getDni())).append('|')
                    .append(escapar(paciente.getNombre())).append('|')
                    .append(escapar(paciente.getApellido())).append('|')
                    .append(escapar(paciente.getTelefono())).append('|')
                    .append(escapar(paciente.getObraSocial())).append('\n');
        }

        sb.append("[MEDICOS]\n");
        for (Medico medico : medicoService.listarMedicos()) {
            sb.append(escapar(medico.getMatricula())).append('|')
                    .append(escapar(medico.getNombre())).append('|')
                    .append(escapar(medico.getApellido())).append('|')
                    .append(escapar(medico.getEspecialidad())).append('\n');
        }

        sb.append("[DISPONIBILIDADES]\n");
        for (Map.Entry<String, List<Disponibilidad>> entry : medicoService.snapshotDisponibilidades().entrySet()) {
            for (Disponibilidad disponibilidad : entry.getValue()) {
                sb.append(escapar(entry.getKey())).append('|')
                        .append(disponibilidad.getDia()).append('|')
                        .append(disponibilidad.getHoraInicio()).append('|')
                        .append(disponibilidad.getHoraFin()).append('\n');
            }
        }

        sb.append("[TURNOS]\n");
        for (Turno turno : turnoService.listarTodosTurnos()) {
            sb.append(escapar(turno.getId())).append('|')
                    .append(escapar(turno.getDniPaciente())).append('|')
                    .append(escapar(turno.getMatriculaMedico())).append('|')
                    .append(turno.getFechaHora()).append('|')
                    .append(turno.getEstado()).append('|')
                    .append(turno.isSobreturno()).append('\n');
        }

        sb.append("[HISTORIAL_ENTRADAS]\n");
        for (HistorialClinico historial : historialService.listarHistoriales()) {
            for (EntradaHistorial entrada : historial.getEntradas()) {
                sb.append(escapar(historial.getDniPaciente())).append('|')
                        .append(entrada.getFecha()).append('|')
                        .append(escapar(entrada.getResumen())).append('|')
                        .append(escapar(entrada.getDiagnostico())).append('|')
                        .append(escapar(entrada.getEstudios())).append('\n');
            }
        }

        sb.append("[EMPLEADOS]\n");
        for (Empleado empleado : empleadoService.listar()) {
            sb.append(escapar(empleado.getLegajo())).append('|')
                    .append(escapar(empleado.getNombre())).append('|')
                    .append(empleado.getRol()).append('\n');
        }

        sb.append("[NOTIFICACIONES]\n");
        for (Map.Entry<String, List<String>> entry : notificationService.snapshotNotificaciones().entrySet()) {
            for (String mensaje : entry.getValue()) {
                sb.append(escapar(entry.getKey())).append('|').append(escapar(mensaje)).append('\n');
            }
        }

        autoSaveService.guardarEstado(sb.toString());
    }

    private void cargarEstadoDesdeBackup() {
        Optional<String> contenidoOpt = autoSaveService.cargarEstado();
        if (contenidoOpt.isEmpty()) {
            return;
        }

        List<Paciente> pacientes = new ArrayList<>();
        List<Medico> medicos = new ArrayList<>();
        Map<String, List<Disponibilidad>> disponibilidades = new HashMap<>();
        List<Turno> turnos = new ArrayList<>();
        List<Empleado> empleados = new ArrayList<>();
        Map<String, List<String>> notificaciones = new HashMap<>();
        List<EntradaBackupHistorial> entradasHistorial = new ArrayList<>();

        String seccion = "";
        for (String lineaCruda : contenidoOpt.get().split("\n")) {
            String linea = lineaCruda.endsWith("\r")
                    ? lineaCruda.substring(0, lineaCruda.length() - 1)
                    : lineaCruda;
            if (linea.isBlank()) {
                continue;
            }
            if (linea.startsWith("[") && linea.endsWith("]")) {
                seccion = linea;
                continue;
            }
            List<String> campos = splitEscapado(linea, '|');

            try {
                switch (seccion) {
                    case "[PACIENTES]" -> {
                        if (campos.size() >= 5) {
                            pacientes.add(new Paciente(campos.get(0), campos.get(1), campos.get(2), campos.get(3), campos.get(4)));
                        }
                    }
                    case "[MEDICOS]" -> {
                        if (campos.size() >= 4) {
                            medicos.add(new Medico(campos.get(0), campos.get(1), campos.get(2), campos.get(3)));
                        }
                    }
                    case "[DISPONIBILIDADES]" -> {
                        if (campos.size() >= 4) {
                            disponibilidades.computeIfAbsent(campos.get(0), key -> new ArrayList<>())
                                    .add(new Disponibilidad(
                                            DayOfWeek.valueOf(campos.get(1)),
                                            LocalTime.parse(campos.get(2)),
                                            LocalTime.parse(campos.get(3))));
                        }
                    }
                    case "[TURNOS]" -> {
                        if (campos.size() >= 6) {
                            turnos.add(new Turno(
                                    campos.get(0),
                                    campos.get(1),
                                    campos.get(2),
                                    LocalDateTime.parse(campos.get(3)),
                                    Boolean.parseBoolean(campos.get(5)),
                                    EstadoTurno.valueOf(campos.get(4))));
                        }
                    }
                    case "[HISTORIAL_ENTRADAS]" -> {
                        if (campos.size() >= 5) {
                            entradasHistorial.add(new EntradaBackupHistorial(
                                    campos.get(0),
                                    new EntradaHistorial(
                                            LocalDateTime.parse(campos.get(1)),
                                            campos.get(2),
                                            campos.get(3),
                                            campos.get(4))));
                        }
                    }
                    case "[EMPLEADOS]" -> {
                        if (campos.size() >= 3) {
                            empleados.add(new Empleado(campos.get(0), campos.get(1), RolUsuario.valueOf(campos.get(2))));
                        }
                    }
                    case "[NOTIFICACIONES]" -> {
                        if (campos.size() >= 2) {
                            notificaciones.computeIfAbsent(campos.get(0), key -> new ArrayList<>()).add(campos.get(1));
                        }
                    }
                    default -> {
                    }
                }
            } catch (RuntimeException ignored) {
            }
        }

        pacienteService.cargarPacientes(pacientes);
        medicoService.cargarEstado(medicos, disponibilidades);
        turnoService.cargarTurnos(turnos);
        empleadoService.cargarEmpleados(empleados);
        notificationService.cargarNotificaciones(notificaciones);

        historialService.cargarHistoriales(new ArrayList<>());
        for (Paciente paciente : pacientes) {
            historialService.crearHistorialSiNoExiste(paciente.getDni());
        }
        for (EntradaBackupHistorial item : entradasHistorial) {
            historialService.agregarEntrada(item.dniPaciente(), item.entrada());
        }
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : valor.toCharArray()) {
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '|') {
                sb.append("\\|");
            } else if (c == '\n') {
                sb.append("\\n");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static List<String> splitEscapado(String linea, char separador) {
        List<String> partes = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean escapando = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (escapando) {
                if (c == 'n') {
                    actual.append('\n');
                } else {
                    actual.append(c);
                }
                escapando = false;
            } else if (c == '\\') {
                escapando = true;
            } else if (c == separador) {
                partes.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (escapando) {
            actual.append('\\');
        }
        partes.add(actual.toString());
        return partes;
    }

    private record EntradaBackupHistorial(String dniPaciente, EntradaHistorial entrada) {
    }
}
