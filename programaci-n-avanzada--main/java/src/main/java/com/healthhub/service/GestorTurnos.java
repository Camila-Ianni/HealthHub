package com.healthhub.service;

import com.healthhub.domain.EstadoTurno;
import com.healthhub.domain.Turno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * GestorTurnos - se encarga de crear y gestionar los turnos médicos.
 * 
 * NOTA: Los turnos tienen un ID único que generamos automáticamente con UUID.
 * Esto nos permite identificar cada turno aunque sea del mismo paciente y médico.
 */
public class GestorTurnos {
    
    // Acá guardamos todos los turnos, la clave es el ID del turno
    private List<Turno> turnos = new ArrayList<>();
    
    // Referencia al gestor de médicos para verificar disponibilidades
    private GestorMedicos gestorMedicos;
    
    // Referencia al gestor de notificaciones para avisar cambios
    private GestorNotificaciones gestorNotifs;
    
    public GestorTurnos(GestorMedicos gestorMedicos, GestorNotificaciones gestorNotifs) {
        this.gestorMedicos = gestorMedicos;
        this.gestorNotifs = gestorNotifs;
    }
    
    /**
     * Crea un turno nuevo para un paciente con un médico en una fecha y hora dada.
     * @param dniPaciente el DNI del paciente
     * @param matriculaMedico la matrícula del médico
     * @param fechaHora la fecha y hora del turno
     * @param sobreturno true si es un sobreturno (extraordinario), false si es normal
     * @return un Optional con el turno creado, o vacío si no se pudo crear
     */
    public Optional<Turno> crearTurno(String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno) {
        // Generamos un ID único para el turno
        String idTurno = UUID.randomUUID().toString();
        
        Turno turno = new Turno(idTurno, dniPaciente, matriculaMedico, fechaHora, sobreturno);
        turnos.add(turno);
        
        // Si no es sobreturno, notificamos al médico
        if (!sobreturno && gestorNotifs != null) {
            String mensaje = "Nuevo turno creado: " + fechaHora;
            gestorNotifs.agregarNotificacion(matriculaMedico, mensaje);
        }
        
        return Optional.of(turno);
    }
    
    /**
     * Cancela un turno existente.
     * @param turnoId el ID del turno a cancelar
     * @return true si se canceló, false si no existe o ya estaba cancelado
     */
    public boolean cancelarTurno(String turnoId) {
        Optional<Turno> turnoOpt = buscarTurno(turnoId);
        
        if (turnoOpt.isEmpty()) {
            return false;
        }
        
        Turno turno = turnoOpt.get();
        
        // No podemos cancelar un turno que ya está cancelado o atendido
        if (turno.getEstado() != EstadoTurno.PROGRAMADO) {
            return false;
        }
        
        turno.cancelar();
        
        // Notificamos al médico sobre la cancelación
        if (gestorNotifs != null) {
            String mensaje = "Turno cancelado: " + turno.getFechaHora();
            gestorNotifs.agregarNotificacion(turno.getMatriculaMedico(), mensaje);
        }
        
        return true;
    }
    
    /**
     * Reprograma un turno a una nueva fecha y hora.
     * @param turnoId el ID del turno a reprogramar
     * @param nuevaFechaHora la nueva fecha y hora
     * @return true si se pudo reprogramar, false si no existe o hay conflicto
     */
    public boolean reprogramarTurno(String turnoId, LocalDateTime nuevaFechaHora) {
        Optional<Turno> turnoOpt = buscarTurno(turnoId);
        
        if (turnoOpt.isEmpty()) {
            return false;
        }
        
        Turno turno = turnoOpt.get();
        
        // Si no es sobreturno, verificamos que el médico esté disponible en el nuevo horario
        if (!turno.isSobreturno() && gestorMedicos != null) {
            if (!gestorMedicos.estaDisponible(turno.getMatriculaMedico(), nuevaFechaHora)) {
                return false;
            }
        }
        
        turno.reprogramar(nuevaFechaHora);
        
        // Notificamos al médico sobre el cambio
        if (gestorNotifs != null) {
            String mensaje = "Turno reprogramado a: " + nuevaFechaHora;
            gestorNotifs.agregarNotificacion(turno.getMatriculaMedico(), mensaje);
        }
        
        return true;
    }
    
    /**
     * Marca un turno como atendido.
     * @param turnoId el ID del turno
     * @return true si se marcó, false si no existe
     */
    public boolean marcarAtendido(String turnoId) {
        Optional<Turno> turnoOpt = buscarTurno(turnoId);
        
        if (turnoOpt.isEmpty()) {
            return false;
        }
        
        turnoOpt.get().marcarAtendido();
        return true;
    }
    
    /**
     * Busca un turno por su ID.
     * @param turnoId el ID del turno
     * @return un Optional con el turno si existe, o vacío si no
     */
    public Optional<Turno> buscarTurno(String turnoId) {
        for (Turno turno : turnos) {
            if (turno.getId().equals(turnoId)) {
                return Optional.of(turno);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Devuelve todos los turnos de un médico en una fecha específica.
     * @param matricula la matrícula del médico
     * @param fecha la fecha a consultar
     * @return lista de turnos ordenados por hora
     */
    public List<Turno> listarTurnosPorMedicoYFecha(String matricula, LocalDate fecha) {
        List<Turno> resultado = new ArrayList<>();
        
        for (Turno turno : turnos) {
            if (turno.getMatriculaMedico().equals(matricula) &&
                turno.getFechaHora().toLocalDate().equals(fecha)) {
                resultado.add(turno);
            }
        }
        
        // Ordenamos por hora (el más temprano primero)
        resultado.sort((t1, t2) -> t1.getFechaHora().compareTo(t2.getFechaHora()));
        
        return resultado;
    }
    
    /**
     * Devuelve todos los turnos de un médico (sin filtrar por fecha).
     * @param matricula la matrícula del médico
     * @return lista de turnos
     */
    public List<Turno> listarTurnosPorMedico(String matricula) {
        List<Turno> resultado = new ArrayList<>();
        
        for (Turno turno : turnos) {
            if (turno.getMatriculaMedico().equals(matricula)) {
                resultado.add(turno);
            }
        }
        
        return resultado;
    }
    
    /**
     * Cancela todos los turnos de un médico en una fecha específica.
     * Esto lo usamos cuando el médico cancela una jornada completa.
     * 
     * @param matricula la matrícula del médico
     * @param fecha la fecha de la jornada a cancelar
     * @return cantidad de turnos cancelados
     */
    public int cancelarTurnosDeJornada(String matricula, LocalDate fecha) {
        int cancelados = 0;
        
        for (Turno turno : turnos) {
            if (turno.getMatriculaMedico().equals(matricula) &&
                turno.getFechaHora().toLocalDate().equals(fecha) &&
                turno.getEstado() == EstadoTurno.PROGRAMADO) {
                turno.cancelar();
                cancelados++;
                
                // Notificamos al médico sobre cada cancelación
                if (gestorNotifs != null) {
                    String mensaje = "Jornada cancelada - turno del " + turno.getFechaHora();
                    gestorNotifs.agregarNotificacion(matricula, mensaje);
                }
            }
        }
        
        return cancelados;
    }
    
    /**
     * Carga una lista de turnos desde el backup.
     * @param listaTurnos lista de turnos a cargar
     */
    public void cargarTurnos(List<Turno> listaTurnos) {
        turnos.clear();
        turnos.addAll(listaTurnos);
    }
    
    /**
     * Devuelve todos los turnos registrados.
     */
    public List<Turno> listarTodos() {
        return new ArrayList<>(turnos);
    }
}
