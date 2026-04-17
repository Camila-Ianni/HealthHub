package com.healthhub.service;

import com.healthhub.domain.EntradaHistorial;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.RolUsuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HistorialService {
    private final Map<String, HistorialClinico> historialesPorDni = new HashMap<>();

    public HistorialClinico crearHistorialSiNoExiste(String dniPaciente) {
        return historialesPorDni.computeIfAbsent(dniPaciente, HistorialClinico::new);
    }

    public Optional<HistorialClinico> verHistorial(String dniPaciente) {
        return Optional.ofNullable(historialesPorDni.get(dniPaciente));
    }

    public boolean registrarConsulta(RolUsuario rol, String dniPaciente, String resumen, String diagnostico, String estudios) {
        if (rol != RolUsuario.MEDICO) {
            return false;
        }
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }
        historial.agregarEntrada(new EntradaHistorial(LocalDateTime.now(), resumen, diagnostico, estudios));
        return true;
    }

    public boolean actualizarDiagnosticoUltimaEntrada(RolUsuario rol, String dniPaciente, String nuevoDiagnostico) {
        if (rol != RolUsuario.MEDICO) {
            return false;
        }
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null || historial.ultimaEntrada() == null) {
            return false;
        }
        historial.ultimaEntrada().setDiagnostico(nuevoDiagnostico);
        return true;
    }

    public boolean registrarEstudioUltimaEntrada(RolUsuario rol, String dniPaciente, String estudio) {
        if (rol != RolUsuario.MEDICO) {
            return false;
        }
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null || historial.ultimaEntrada() == null) {
            return false;
        }
        historial.ultimaEntrada().setEstudios(estudio);
        return true;
    }

    public List<HistorialClinico> listarHistoriales() {
        return new ArrayList<>(historialesPorDni.values());
    }

    public void cargarHistoriales(List<HistorialClinico> historiales) {
        historialesPorDni.clear();
        for (HistorialClinico historial : historiales) {
            historialesPorDni.put(historial.getDniPaciente(), historial);
        }
    }

    public void agregarEntrada(String dniPaciente, EntradaHistorial entrada) {
        crearHistorialSiNoExiste(dniPaciente).agregarEntrada(entrada);
    }
}
