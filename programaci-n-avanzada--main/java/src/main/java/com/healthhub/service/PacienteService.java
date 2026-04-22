package com.healthhub.service;

import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Paciente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PacienteService {
    private final Map<String, Paciente> pacientesPorDni = new HashMap<>();
    private final Map<String, List<String>> indiceNombre = new HashMap<>();
    private final HistorialService historialService;

    public PacienteService(HistorialService historialService) {
        this.historialService = historialService;
    }

    public boolean registrarPaciente(Paciente paciente) {
        if (pacientesPorDni.containsKey(paciente.getDni())) {
            return false;
        }
        pacientesPorDni.put(paciente.getDni(), paciente);
        indexarPaciente(paciente);
        HistorialClinico historial = historialService.crearHistorialSiNoExiste(paciente.getDni());
        return true;
    }

    public boolean modificarPaciente(String dni, String nombre, String apellido, String telefono, String obraSocial) {
        Paciente paciente = pacientesPorDni.get(dni);
        if (paciente == null) {
            return false;
        }
        desindexarPaciente(paciente);
        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setTelefono(telefono);
        paciente.setObraSocial(obraSocial);
        indexarPaciente(paciente);
        return true;
    }

    public Optional<Paciente> buscarPorDni(String dni) {
        return Optional.ofNullable(pacientesPorDni.get(dni));
    }

    public List<Paciente> buscarPorNombreCompleto(String nombreCompleto) {
        String key = normalizar(nombreCompleto);
        List<String> dnis = indiceNombre.getOrDefault(key, List.of());
        List<Paciente> resultado = new ArrayList<>();
        for (String dni : dnis) {
            Paciente paciente = pacientesPorDni.get(dni);
            if (paciente != null) {
                resultado.add(paciente);
            }
        }
        return resultado;
    }

    public List<Paciente> listarPacientes() {
        return new ArrayList<>(pacientesPorDni.values());
    }

    public void cargarPacientes(List<Paciente> pacientes) {
        pacientesPorDni.clear();
        indiceNombre.clear();
        for (Paciente paciente : pacientes) {
            pacientesPorDni.put(paciente.getDni(), paciente);
            indexarPaciente(paciente);
        }
    }

    private void indexarPaciente(Paciente paciente) {
        String key = normalizar(paciente.nombreCompleto());
        indiceNombre.computeIfAbsent(key, k -> new ArrayList<>()).add(paciente.getDni());
    }

    private void desindexarPaciente(Paciente paciente) {
        String key = normalizar(paciente.nombreCompleto());
        List<String> dnis = indiceNombre.get(key);
        if (dnis == null) {
            return;
        }
        dnis.remove(paciente.getDni());
        if (dnis.isEmpty()) {
            indiceNombre.remove(key);
        }
    }

    private String normalizar(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
