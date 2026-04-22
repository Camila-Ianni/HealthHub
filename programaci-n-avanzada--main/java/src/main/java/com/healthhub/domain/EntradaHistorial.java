package com.healthhub.domain;

import java.time.LocalDateTime;

/**
 * EntradaHistorial - representa una consulta médica en el historial clínico.
 * 
 * Cada entrada tiene:
 * - Fecha y hora de la consulta
 * - Resumen de lo que habló el médico con el paciente
 * - Diagnóstico (se puede modificar después)
 * - Estudios (también modificable)
 * 
 * NOTA: La fecha no se puede cambiar porque es el registro histórico de cuándo fue la consulta.
 */
public class EntradaHistorial {
    
    private final LocalDateTime fecha;     // Cuándo fue la consulta
    private final String resumen;          // Resumen de la consulta
    private String diagnostico;            // Diagnóstico del médico (modificable)
    private String estudios;               // Estudios médicos (modificable)
    
    public EntradaHistorial(LocalDateTime fecha, String resumen, String diagnostico, String estudios) {
        this.fecha = fecha;
        this.resumen = resumen;
        this.diagnostico = diagnostico;
        this.estudios = estudios;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public String getResumen() {
        return resumen;
    }
    
    public String getDiagnostico() {
        return diagnostico;
    }
    
    public String getEstudios() {
        return estudios;
    }
    
    /**
     * Actualiza el diagnóstico.
     * Lo usamos cuando el médico se equivocó o quiere agregar más detalles.
     */
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }
    
    /**
     * Actualiza los estudios.
     * Por ejemplo, cuando el paciente vuelve con los resultados.
     */
    public void setEstudios(String estudios) {
        this.estudios = estudios;
    }
    
    @Override
    public String toString() {
        return "[" + fecha + "] " + resumen + " | Dx: " + diagnostico + " | Estudios: " + estudios;
    }
}
