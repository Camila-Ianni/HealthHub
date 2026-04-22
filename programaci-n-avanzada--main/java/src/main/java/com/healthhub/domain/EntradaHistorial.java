package com.healthhub.domain;

import java.time.LocalDateTime;

public class EntradaHistorial {
    private final LocalDateTime fecha;
    private final String resumen;
    private String diagnostico;
    private String estudios;

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

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void setEstudios(String estudios) {
        this.estudios = estudios;
    }

    @Override
    public String toString() {
        return "Entrada{fecha=" + fecha + ", resumen='" + resumen + "', diagnostico='" + diagnostico
                + "', estudios='" + estudios + "'}";
    }
}
