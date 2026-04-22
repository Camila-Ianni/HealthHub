package com.healthhub.service;

import com.healthhub.domain.EstadoTurno;
import com.healthhub.domain.Turno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TurnoService {
    private final Map<String, Turno> turnosPorId = new HashMap<>();
    private final NotificationService notificationService;

    public TurnoService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public Optional<Turno> crearTurno(String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno) {
        int activos = contarActivos(matriculaMedico, fechaHora, null);
        boolean sobreturnoExistente = existeSobreturnoActivo(matriculaMedico, fechaHora, null);

        if (!sobreturno) {
            if (activos > 0) {
                return Optional.empty();
            }
        } else if (activos == 0 || sobreturnoExistente || activos >= 2) {
            return Optional.empty();
        }

        Turno turno = new Turno(UUID.randomUUID().toString(), dniPaciente, matriculaMedico, fechaHora, sobreturno);
        turnosPorId.put(turno.getId(), turno);
        notificationService.notificarMedico(matriculaMedico, "Nuevo turno asignado: " + turno.getFechaHora());
        return Optional.of(turno);
    }

    public boolean cancelarTurno(String turnoId) {
        Turno turno = turnosPorId.get(turnoId);
        if (turno == null) {
            return false;
        }
        turno.cancelar();
        notificationService.notificarMedico(turno.getMatriculaMedico(), "Turno cancelado: " + turno.getFechaHora());
        return true;
    }

    public boolean reprogramarTurno(String turnoId, LocalDateTime nuevaFechaHora) {
        Turno turno = turnosPorId.get(turnoId);
        if (turno == null) {
            return false;
        }
        int activos = contarActivos(turno.getMatriculaMedico(), nuevaFechaHora, turnoId);
        boolean sobreturnoExistente = existeSobreturnoActivo(turno.getMatriculaMedico(), nuevaFechaHora, turnoId);

        if (!turno.isSobreturno()) {
            if (activos > 0) {
                return false;
            }
        } else if (activos == 0 || sobreturnoExistente || activos >= 2) {
            return false;
        }

        turno.reprogramar(nuevaFechaHora);
        notificationService.notificarMedico(turno.getMatriculaMedico(), "Turno reprogramado: " + nuevaFechaHora);
        return true;
    }

    public boolean marcarAtendido(String turnoId) {
        Turno turno = turnosPorId.get(turnoId);
        if (turno == null) {
            return false;
        }
        turno.marcarAtendido();
        return true;
    }

    public List<Turno> listarTurnosPorMedico(String matricula) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno turno : turnosPorId.values()) {
            if (turno.getMatriculaMedico().equals(matricula)) {
                resultado.add(turno);
            }
        }
        return resultado;
    }

    public Optional<Turno> buscarTurno(String turnoId) {
        return Optional.ofNullable(turnosPorId.get(turnoId));
    }

    public List<Turno> listarTodosTurnos() {
        return new ArrayList<>(turnosPorId.values());
    }

    public void cargarTurnos(List<Turno> turnos) {
        turnosPorId.clear();
        for (Turno turno : turnos) {
            turnosPorId.put(turno.getId(), turno);
        }
    }

    public int cancelarTurnosDeJornada(String matricula, LocalDate fecha) {
        int cancelados = 0;
        for (Turno turno : turnosPorId.values()) {
            if (turno.getMatriculaMedico().equals(matricula)
                    && turno.getFechaHora().toLocalDate().equals(fecha)
                    && turno.getEstado() == EstadoTurno.PROGRAMADO) {
                turno.cancelar();
                cancelados++;
            }
        }
        if (cancelados > 0) {
            notificationService.notificarMedico(matricula, "Se cancelaron " + cancelados + " turnos de la jornada " + fecha);
        }
        return cancelados;
    }

    private int contarActivos(String matriculaMedico, LocalDateTime fechaHora, String turnoExcluidoId) {
        int count = 0;
        for (Turno turno : turnosPorId.values()) {
            if (turnoExcluidoId != null && turnoExcluidoId.equals(turno.getId())) {
                continue;
            }
            boolean mismoMedico = turno.getMatriculaMedico().equals(matriculaMedico);
            boolean mismaFechaHora = turno.getFechaHora().equals(fechaHora);
            boolean activo = turno.getEstado() != EstadoTurno.CANCELADO;
            if (mismoMedico && mismaFechaHora && activo) {
                count++;
            }
        }
        return count;
    }

    private boolean existeSobreturnoActivo(String matriculaMedico, LocalDateTime fechaHora, String turnoExcluidoId) {
        for (Turno turno : turnosPorId.values()) {
            if (turnoExcluidoId != null && turnoExcluidoId.equals(turno.getId())) {
                continue;
            }
            boolean mismoMedico = turno.getMatriculaMedico().equals(matriculaMedico);
            boolean mismaFechaHora = turno.getFechaHora().equals(fechaHora);
            boolean sobreturno = turno.isSobreturno();
            boolean activo = turno.getEstado() != EstadoTurno.CANCELADO;
            if (mismoMedico && mismaFechaHora && sobreturno && activo) {
                return true;
            }
        }
        return false;
    }
}
