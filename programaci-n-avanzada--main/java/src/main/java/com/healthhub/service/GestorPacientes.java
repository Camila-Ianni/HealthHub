package com.healthhub.service;

import com.healthhub.domain.Paciente;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


public class GestorPacientes {

    private Map<String, Paciente> pacientesPorDni = new HashMap<>();

    private Map<String, List<String>> indicePorNombre = new HashMap<>();

    private GestorHistoriales gestorHistoriales;

    public GestorPacientes(GestorHistoriales gestorHistoriales) {
        this.gestorHistoriales = gestorHistoriales;
    }


    public boolean registrarPaciente(Paciente paciente) {
        if (pacientesPorDni.containsKey(paciente.getDni())) {
            return false;
        }

        pacientesPorDni.put(paciente.getDni(), paciente);

        indexarPaciente(paciente);

        if (gestorHistoriales != null) {
            gestorHistoriales.crearHistorialSiNoExiste(paciente.getDni());
        }

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
        String clave = normalizar(nombreCompleto);
        List<String> dnis = indicePorNombre.getOrDefault(clave, new ArrayList<>());

        List<Paciente> resultado = new ArrayList<>();
        for (String dni : dnis) {
            Paciente paciente = pacientesPorDni.get(dni);
            if (paciente != null) {
                resultado.add(paciente);
            }
        }

        return resultado;
    }


    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientesPorDni.values());
    }


    public void cargarPacientes(List<Paciente> pacientes) {
        pacientesPorDni.clear();
        indicePorNombre.clear();

        for (Paciente paciente : pacientes) {
            pacientesPorDni.put(paciente.getDni(), paciente);
            indexarPaciente(paciente);
        }
    }



    private void indexarPaciente(Paciente paciente) {
        String clave = normalizar(paciente.nombreCompleto());
        indicePorNombre.computeIfAbsent(clave, k -> new ArrayList<>()).add(paciente.getDni());
    }


    private void desindexarPaciente(Paciente paciente) {
        String clave = normalizar(paciente.nombreCompleto());
        List<String> dnis = indicePorNombre.get(clave);

        if (dnis == null) {
            return;
        }

        dnis.remove(paciente.getDni());

        if (dnis.isEmpty()) {
            indicePorNombre.remove(clave);
        }
    }


    private String normalizar(String valor) {
        String limpio = valor.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        String sinAcentos = Normalizer.normalize(limpio, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return sinAcentos.replaceAll("[^\\p{Alnum}\\s]", "");
    }
}
