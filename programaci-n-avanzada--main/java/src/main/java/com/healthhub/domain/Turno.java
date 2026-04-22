package com.healthhub.domain;

import java.time.LocalDateTime;

/**
 * Turno - representa un turno médico en la clínica.
 * 
 * Cada turno tiene:
 * - ID único (lo generamos nosotros con UUID)
 * - DNI del paciente
 * - Matrícula del médico
 * - Fecha y hora
 * - Estado (PROGRAMADO, CANCELADO, ATENDIDO)
 * - Si es sobreturno o no
 * 
 * NOTA: El ID lo hacemos con UUID para que no haya problemas de turnos duplicados.
 * Al principio pensamos usar un contador, pero si borramos turnos se podía repetir.
 */
public class Turno {
    
    private final String id;                 // ID único del turno
    private final String dniPaciente;        // DNI del paciente
    private final String matriculaMedico;    // Matrícula del médico
    private LocalDateTime fechaHora;         // Fecha y hora del turno (modificable por reprogramación)
    private EstadoTurno estado;              // Estado actual del turno
    private final boolean sobreturno;        // True si es un sobreturno (extraordinario)
    
    /**
     * Constructor principal.
     * Por defecto el turno se crea como PROGRAMADO.
     */
    public Turno(String id, String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno) {
        this(id, dniPaciente, matriculaMedico, fechaHora, sobreturno, EstadoTurno.PROGRAMADO);
    }
    
    /**
     * Constructor completo con estado.
     * Lo usamos cuando cargamos desde el backup.
     */
    public Turno(String id, String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno, EstadoTurno estado) {
        this.id = id;
        this.dniPaciente = dniPaciente;
        this.matriculaMedico = matriculaMedico;
        this.fechaHora = fechaHora;
        this.sobreturno = sobreturno;
        this.estado = estado;
    }
    
    // Getters - todos los campos tienen getter
    
    public String getId() {
        return id;
    }
    
    public String getDniPaciente() {
        return dniPaciente;
    }
    
    public String getMatriculaMedico() {
        return matriculaMedico;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public EstadoTurno getEstado() {
        return estado;
    }
    
    public boolean isSobreturno() {
        return sobreturno;
    }
    
    /**
     * Cancela el turno.
     * Solo cambia el estado, no borramos el turno porque queda el historial.
     */
    public void cancelar() {
        estado = EstadoTurno.CANCELADO;
    }
    
    /**
     * Reprograma el turno a una nueva fecha y hora.
     * Lo usa el recepcionista cuando el paciente pide cambiar el horario.
     */
    public void reprogramar(LocalDateTime nuevaFechaHora) {
        fechaHora = nuevaFechaHora;
    }
    
    /**
     * Marca el turno como atendido.
     * Lo hace el médico después de atender al paciente.
     */
    public void marcarAtendido() {
        estado = EstadoTurno.ATENDIDO;
    }
    
    @Override
    public String toString() {
        return "Turno{id='" + id + "', dniPaciente='" + dniPaciente + "', medico='" + matriculaMedico
                + "', fechaHora=" + fechaHora + ", estado=" + estado + ", sobreturno=" + sobreturno + "}";
    }
}
