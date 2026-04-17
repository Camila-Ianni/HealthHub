package com.healthhub.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Disponibilidad {
    private final DayOfWeek dia;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

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

    public boolean solapaCon(Disponibilidad otra) {
        if (!dia.equals(otra.dia)) {
            return false;
        }
        return horaInicio.isBefore(otra.horaFin) && otra.horaInicio.isBefore(horaFin);
    }

    @Override
    public String toString() {
        return dia + " " + horaInicio + "-" + horaFin;
    }
}
