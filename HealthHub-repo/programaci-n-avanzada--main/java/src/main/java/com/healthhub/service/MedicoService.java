package com.healthhub.service;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Medico;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MedicoService {
    private final Map<String, Medico> medicosPorMatricula = new HashMap<>();
    private final Map<String, List<Disponibilidad>> disponibilidadPorMedico = new HashMap<>();

    public boolean registrarMedico(Medico medico) {
        if (medicosPorMatricula.containsKey(medico.getMatricula())) {
            return false;
        }
        medicosPorMatricula.put(medico.getMatricula(), medico);
        disponibilidadPorMedico.put(medico.getMatricula(), new ArrayList<>());
        return true;
    }

    public boolean agregarDisponibilidad(String matricula, Disponibilidad nuevaDisponibilidad) {
        List<Disponibilidad> lista = disponibilidadPorMedico.get(matricula);
        if (lista == null) {
            return false;
        }
        boolean haySolapamiento = lista.stream().anyMatch(item -> item.solapaCon(nuevaDisponibilidad));
        if (haySolapamiento) {
            return false;
        }
        lista.add(nuevaDisponibilidad);
        return true;
    }

    public boolean reemplazarDisponibilidades(String matricula, List<Disponibilidad> nuevas) {
        if (!disponibilidadPorMedico.containsKey(matricula)) {
            return false;
        }
        for (int i = 0; i < nuevas.size(); i++) {
            for (int j = i + 1; j < nuevas.size(); j++) {
                if (nuevas.get(i).solapaCon(nuevas.get(j))) {
                    return false;
                }
            }
        }
        disponibilidadPorMedico.put(matricula, new ArrayList<>(nuevas));
        return true;
    }

    public List<Disponibilidad> consultarDisponibilidad(String matricula) {
        return new ArrayList<>(disponibilidadPorMedico.getOrDefault(matricula, List.of()));
    }

    public List<Medico> listarMedicos() {
        return new ArrayList<>(medicosPorMatricula.values());
    }

    public Optional<Medico> buscarMedico(String matricula) {
        return Optional.ofNullable(medicosPorMatricula.get(matricula));
    }

    public boolean estaDisponible(String matricula, LocalDateTime fechaHora) {
        List<Disponibilidad> disponibilidades = disponibilidadPorMedico.get(matricula);
        if (disponibilidades == null) {
            return false;
        }
        LocalTime hora = fechaHora.toLocalTime();
        return disponibilidades.stream().anyMatch(item ->
                item.getDia().equals(fechaHora.getDayOfWeek())
                        && !hora.isBefore(item.getHoraInicio())
                        && hora.isBefore(item.getHoraFin()));
    }

    public Map<String, List<Disponibilidad>> snapshotDisponibilidades() {
        Map<String, List<Disponibilidad>> copia = new HashMap<>();
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidadPorMedico.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }

    public void cargarEstado(List<Medico> medicos, Map<String, List<Disponibilidad>> disponibilidades) {
        medicosPorMatricula.clear();
        disponibilidadPorMedico.clear();
        for (Medico medico : medicos) {
            medicosPorMatricula.put(medico.getMatricula(), medico);
            disponibilidadPorMedico.put(medico.getMatricula(),
                    new ArrayList<>(disponibilidades.getOrDefault(medico.getMatricula(), List.of())));
        }
    }
}
