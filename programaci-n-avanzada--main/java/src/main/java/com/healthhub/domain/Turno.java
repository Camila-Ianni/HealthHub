package com.healthhub.domain;

import java.time.LocalDateTime;

public class Turno {
    private final String id;
    private final String dniPaciente;
    private final String matriculaMedico;
    private LocalDateTime fechaHora;
    private EstadoTurno estado;
    private final boolean sobreturno;

    public Turno(String id, String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno) {
        this(id, dniPaciente, matriculaMedico, fechaHora, sobreturno, EstadoTurno.PROGRAMADO);
    }

    public Turno(String id, String dniPaciente, String matriculaMedico, LocalDateTime fechaHora, boolean sobreturno, EstadoTurno estado) {
        this.id = id;
        this.dniPaciente = dniPaciente;
        this.matriculaMedico = matriculaMedico;
        this.fechaHora = fechaHora;
        this.sobreturno = sobreturno;
        this.estado = estado;
    }

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

    public void cancelar() {
        estado = EstadoTurno.CANCELADO;
    }

    public void reprogramar(LocalDateTime nuevaFechaHora) {
        fechaHora = nuevaFechaHora;
    }

    public void marcarAtendido() {
        estado = EstadoTurno.ATENDIDO;
    }

    @Override
    public String toString() {
        return "Turno{id='" + id + "', dniPaciente='" + dniPaciente + "', medico='" + matriculaMedico
                + "', fechaHora=" + fechaHora + ", estado=" + estado + ", sobreturno=" + sobreturno + "}";
    }
}
