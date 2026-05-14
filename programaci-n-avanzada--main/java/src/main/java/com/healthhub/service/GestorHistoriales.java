package com.healthhub.service;

import com.healthhub.domain.EntradaHistorial;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.RolUsuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GestorHistoriales {

    private Map<String, HistorialClinico> historialesPorDni = new HashMap<>();


    public HistorialClinico crearHistorialSiNoExiste(String dniPaciente) {
        if (!historialesPorDni.containsKey(dniPaciente)) {
            historialesPorDni.put(dniPaciente, new HistorialClinico(dniPaciente));
        }
        return historialesPorDni.get(dniPaciente);
    }


    public boolean registrarConsulta(RolUsuario rol, String dniPaciente, String resumen, String diagnostico, String estudios) {

        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }

        EntradaHistorial entrada = new EntradaHistorial(
            LocalDateTime.now(),
            resumen,
            diagnostico,
            estudios
        );

        historial.agregarEntrada(entrada);
        return true;
    }


    public boolean actualizarDiagnostico(String dniPaciente, String nuevoDiagnostico) {
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }

        List<EntradaHistorial> entradas = historial.getEntradas();
        if (entradas.isEmpty()) {
            return false;
        }

        EntradaHistorial ultima = entradas.get(entradas.size() - 1);
        ultima.setDiagnostico(nuevoDiagnostico);

        return true;
    }


    public boolean actualizarEstudios(String dniPaciente, String nuevosEstudios) {
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }

        List<EntradaHistorial> entradas = historial.getEntradas();
        if (entradas.isEmpty()) {
            return false;
        }

        EntradaHistorial ultima = entradas.get(entradas.size() - 1);
        ultima.setEstudios(nuevosEstudios);

        return true;
    }


    public HistorialClinico verHistorial(String dniPaciente) {
        return historialesPorDni.get(dniPaciente);
    }


    public void agregarEntrada(String dniPaciente, EntradaHistorial entrada) {
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial != null) {
            historial.agregarEntrada(entrada);
        }
    }


    public void cargarHistoriales(List<HistorialClinico> listaHistoriales) {
        historialesPorDni.clear();
        for (HistorialClinico historial : listaHistoriales) {
            historialesPorDni.put(historial.getDniPaciente(), historial);
        }
    }


    public List<HistorialClinico> listarTodos() {
        return new ArrayList<>(historialesPorDni.values());
    }
}
