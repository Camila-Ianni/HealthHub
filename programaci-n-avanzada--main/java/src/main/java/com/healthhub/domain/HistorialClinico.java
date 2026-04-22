package com.healthhub.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HistorialClinico - guarda todas las consultas médicas de un paciente.
 * 
 * Cada paciente tiene su propio historial que se crea cuando lo registran.
 * Solo los médicos pueden agregar o modificar entradas.
 * 
 * TODO: Pensar si hay que hacer un historial inmutable y que cada modificación cree uno nuevo.
 * Por ahora lo hacemos simple con una lista que se va modificando.
 */
public class HistorialClinico {
    
    private final String dniPaciente;              // DNI del paciente dueño del historial
    private final List<EntradaHistorial> entradas; // Lista de consultas/entradas
    
    public HistorialClinico(String dniPaciente) {
        this.dniPaciente = dniPaciente;
        this.entradas = new ArrayList<>();
    }
    
    public String getDniPaciente() {
        return dniPaciente;
    }
    
    /**
     * Devuelve las entradas del historial.
     * Usamos Collections.unmodifiableList para que no lo modifiquen desde afuera.
     * Así nos aseguramos de que usen el método agregarEntrada().
     */
    public List<EntradaHistorial> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }
    
    /**
     * Agrega una nueva entrada al historial.
     * Solo lo debería llamar GestorHistoriales después de validar que es un médico.
     */
    public void agregarEntrada(EntradaHistorial entrada) {
        entradas.add(entrada);
    }
    
    /**
     * Devuelve la última entrada del historial.
     * Lo usamos cuando queremos actualizar el diagnóstico o estudios de la consulta más reciente.
     * 
     * @return la última entrada, o null si el historial está vacío
     */
    public EntradaHistorial ultimaEntrada() {
        if (entradas.isEmpty()) {
            return null;
        }
        return entradas.get(entradas.size() - 1);
    }
    
    @Override
    public String toString() {
        return "Historial{dni=" + dniPaciente + ", entradas=" + entradas.size() + "}";
    }
}
