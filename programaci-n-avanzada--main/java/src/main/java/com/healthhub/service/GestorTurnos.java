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


public class GestorTurnos {

    private List<Turno> turnos = new ArrayList<>();

    private GestorMedicos gestorMedicos;

    private GestorNotificaciones gestorNotifs;

    public GestorTurnos(GestorMedicos gestorMedicos, GestorNotificaciones gestorNotifs) {
        this.gestorMedicos = gestorMedicos;
        this.gestorNotifs = gestorNotifs;
    }


    public Optional<Turno> crearTurno(String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        String idTurno = UUID.randomUUID().toString();

        Turno turno = new Turno(idTurno, dniPaciente, matriculaMedico, fechaHora, sobreturno);
        turnos.add(turno);

        if (!sobreturno && gestorNotifs != null) {
            String mensaje = "Nuevo turno creado: " + fechaHora;
            gestorNotifs.agregarNotificacion(matriculaMedico, mensaje);
        }

        return Optional.of(turno);
    }


    public boolean cancelarTurno(String turnoId) {
        Optional<Turno> turnoOpt = buscarTurno(turnoId);

        if (turnoOpt.isEmpty()) {
            return false;
        }

        Turno turno = turnoOpt.get();

        if (turno.getEstado() != EstadoTurno.PROGRAMADO) {
            return false;
        }

        turno.cancelar();

        if (gestorNotifs != null) {
            String mensaje = "Turno cancelado: " + turno.getFechaHora();
            gestorNotifs.agregarNotificacion(turno.getMatriculaMedico(), mensaje);
        }

        return true;
    }


    public boolean reprogramarTurno(String turnoId, LocalDateTime nuevaFechaHora) {
        Optional<Turno> turnoOpt = buscarTurno(turnoId);

        if (turnoOpt.isEmpty()) {
            return false;
        }

        Turno turno = turnoOpt.get();

        if (!turno.isSobreturno() && gestorMedicos != null) {
            if (!gestorMedicos.estaDisponible(turno.getMatriculaMedico(), nuevaFechaHora)) {
                return false;
            }
        }

        turno.reprogramar(nuevaFechaHora);

        if (gestorNotifs != null) {
            String mensaje = "Turno reprogramado a: " + nuevaFechaHora;
            gestorNotifs.agregarNotificacion(turno.getMatriculaMedico(), mensaje);
        }

        return true;
    }


    public boolean marcarAtendido(String turnoId) {
        Optional<Turno> turnoOpt = buscarTurno(turnoId);

        if (turnoOpt.isEmpty()) {
            return false;
        }

        turnoOpt.get().marcarAtendido();
        return true;
    }


    public Optional<Turno> buscarTurno(String turnoId) {
        for (Turno turno : turnos) {
            if (turno.getId().equals(turnoId)) {
                return Optional.of(turno);
            }
        }
        return Optional.empty();
    }


    public List<Turno> listarTurnosPorMedicoYFecha(String matricula, LocalDate fecha) {
        List<Turno> resultado = new ArrayList<>();

        for (Turno turno : turnos) {
            if (turno.getMatriculaMedico().equals(matricula) &&
                turno.getFechaHora().toLocalDate().equals(fecha)) {
                resultado.add(turno);
            }
        }

        resultado.sort((t1, t2) -> t1.getFechaHora().compareTo(t2.getFechaHora()));

        return resultado;
    }


    public List<Turno> listarTurnosPorMedico(String matricula) {
        List<Turno> resultado = new ArrayList<>();

        for (Turno turno : turnos) {
            if (turno.getMatriculaMedico().equals(matricula)) {
                resultado.add(turno);
            }
        }

        return resultado;
    }


    public int cancelarTurnosDeJornada(String matricula, LocalDate fecha) {
        int cancelados = 0;

        for (Turno turno : turnos) {
            if (turno.getMatriculaMedico().equals(matricula) &&
                turno.getFechaHora().toLocalDate().equals(fecha) &&
                turno.getEstado() == EstadoTurno.PROGRAMADO) {
                turno.cancelar();
                cancelados++;

                if (gestorNotifs != null) {
                    String mensaje = "Jornada cancelada - turno del " + turno.getFechaHora();
                    gestorNotifs.agregarNotificacion(matricula, mensaje);
                }
            }
        }

        return cancelados;
    }


    public void cargarTurnos(List<Turno> listaTurnos) {
        turnos.clear();
        turnos.addAll(listaTurnos);
    }


    public List<Turno> listarTodos() {
        return new ArrayList<>(turnos);
    }


    public long contarTurnosPorFecha(LocalDate fecha) {
        long total = 0;
        for (Turno turno : turnos) {
            if (turno.getFechaHora().toLocalDate().equals(fecha)) {
                total++;
            }
        }
        return total;
    }


    public long contarTurnosPorEstadoEnFecha(LocalDate fecha, EstadoTurno estado) {
        long total = 0;
        for (Turno turno : turnos) {
            if (turno.getFechaHora().toLocalDate().equals(fecha) && turno.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }


    public Optional<String> obtenerMatriculaConMasSobreturnos() {
        Map<String, Long> sobreturnosPorMedico = new HashMap<>();
        for (Turno turno : turnos) {
            if (turno.isSobreturno()) {
                String matricula = turno.getMatriculaMedico();
                sobreturnosPorMedico.put(matricula, sobreturnosPorMedico.getOrDefault(matricula, 0L) + 1L);
            }
        }

        String mejorMatricula = null;
        long mayor = -1;
        for (Map.Entry<String, Long> entry : sobreturnosPorMedico.entrySet()) {
            if (entry.getValue() > mayor) {
                mayor = entry.getValue();
                mejorMatricula = entry.getKey();
            }
        }

        return Optional.ofNullable(mejorMatricula);
    }


    public long contarSobreturnosPorMedico(String matricula) {
        long total = 0;
        for (Turno turno : turnos) {
            if (turno.isSobreturno() && turno.getMatriculaMedico().equals(matricula)) {
                total++;
            }
        }
        return total;
    }
}
