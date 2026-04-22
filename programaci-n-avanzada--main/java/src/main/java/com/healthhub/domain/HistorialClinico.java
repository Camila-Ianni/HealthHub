package com.healthhub.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialClinico {
    private final String dniPaciente;
    private final List<EntradaHistorial> entradas;

    public HistorialClinico(String dniPaciente) {
        this.dniPaciente = dniPaciente;
        this.entradas = new ArrayList<>();
    }

    public String getDniPaciente() {
        return dniPaciente;
    }

    public List<EntradaHistorial> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public void agregarEntrada(EntradaHistorial entrada) {
        entradas.add(entrada);
    }

    public EntradaHistorial ultimaEntrada() {
        if (entradas.isEmpty()) {
            return null;
        }
        return entradas.get(entradas.size() - 1);
    }
}
