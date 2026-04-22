package com.healthhub.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Disponibilidad - representa un horario en el que un médico puede atender.
 * 
 * NOTA: Usamos DayOfWeek en vez de un int porque así es más claro qué día es.
 * El tema es que DayOfWeek.of(1) es Lunes, no Domingo como en Argentina.
 * Esto nos confundió un poco al principio.
 */
public class Disponibilidad {
    
    private final DayOfWeek dia;           // Día de la semana (1=Lunes, 7=Domingo)
    private final LocalTime horaInicio;    // Hora de inicio de la atención
    private final LocalTime horaFin;       // Hora de fin de la atención
    
    public Disponibilidad(DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
    
    public DayOfWeek getDia() {
        return dia;
    }
    
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    
    public LocalTime getHoraFin() {
        return horaFin;
    }
    
    /**
     * Verifica si esta disponibilidad se solapa con otra.
     * Lo usamos para no permitir que un médico tenga dos horarios superpuestos.
     * 
     * @param otra otra disponibilidad para comparar
     * @return true si hay solapamiento, false si no
     */
    public boolean solapaCon(Disponibilidad otra) {
        // Si son días distintos, no hay problema
        if (!dia.equals(otra.dia)) {
            return false;
        }
        
        // Hay solapamiento si los horarios se cruzan
        // Esto pasa cuando: inicio1 < fin2 Y inicio2 < fin1
        return horaInicio.isBefore(otra.horaFin) && otra.horaInicio.isBefore(horaFin);
    }
    
    @Override
    public String toString() {
        return dia + " " + horaInicio + "-" + horaFin;
    }
}
