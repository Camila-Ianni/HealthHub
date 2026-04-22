package com.healthhub.domain;

/**
 * EstadoTurno - representa los posibles estados de un turno.
 * 
 * Los estados son:
 * - PROGRAMADO: el turno está confirmado y pendiente de atender
 * - CANCELADO: el turno fue cancelado (por el recepcionista o por el médico)
 * - ATENDIDO: el turno ya fue atendido por el médico
 * 
 * NOTA: Usamos enum porque son estados fijos que no van a cambiar.
 */
public enum EstadoTurno {
    PROGRAMADO,
    CANCELADO,
    ATENDIDO
}
