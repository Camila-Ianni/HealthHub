package com.healthhub.service;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Medico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GestorMedicos {

    private Map<String, Medico> medicosPorMatricula = new HashMap<>();

    private Map<String, List<Disponibilidad>> disponibilidadesPorMedico = new HashMap<>();


    public boolean registrarMedico(Medico medico) {
        if (medicosPorMatricula.containsKey(medico.getMatricula())) {
            return false;
        }

        medicosPorMatricula.put(medico.getMatricula(), medico);
        disponibilidadesPorMedico.put(medico.getMatricula(), new ArrayList<>());

        return true;
    }


    public boolean agregarDisponibilidad(String matricula, Disponibilidad disponibilidad) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);

        if (disponibilidades == null) {
            return false;
        }

        if (haySolapamiento(matricula, disponibilidad)) {
            return false;
        }

        disponibilidades.add(disponibilidad);
        return true;
    }


    public boolean reemplazarDisponibilidades(String matricula, List<Disponibilidad> nuevasDisponibilidades) {
        if (!medicosPorMatricula.containsKey(matricula)) {
            return false;
        }

        disponibilidadesPorMedico.put(matricula, new ArrayList<>(nuevasDisponibilidades));
        return true;
    }


    public List<Disponibilidad> consultarDisponibilidad(String matricula) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);
        return disponibilidades != null ? new ArrayList<>(disponibilidades) : new ArrayList<>();
    }


    public Optional<Medico> buscarMedico(String matricula) {
        return Optional.ofNullable(medicosPorMatricula.get(matricula));
    }


    public boolean estaDisponible(String matricula, java.time.LocalDateTime fechaHora) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);

        if (disponibilidades == null || disponibilidades.isEmpty()) {
            return false;
        }

        int diaSemana = fechaHora.getDayOfWeek().getValue();
        java.time.LocalTime hora = fechaHora.toLocalTime();

        for (Disponibilidad disp : disponibilidades) {
            if (disp.getDia().getValue() == diaSemana) {
                if (!hora.isBefore(disp.getHoraInicio()) && !hora.isAfter(disp.getHoraFin())) {
                    return true;
                }
            }
        }

        return false;
    }


    private boolean haySolapamiento(String matricula, Disponibilidad nuevaDisponibilidad) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);

        if (disponibilidades == null) {
            return false;
        }

        for (Disponibilidad existente : disponibilidades) {
            if (existente.getDia() == nuevaDisponibilidad.getDia()) {
                if (nuevaDisponibilidad.getHoraInicio().isBefore(existente.getHoraFin()) &&
                    existente.getHoraInicio().isBefore(nuevaDisponibilidad.getHoraFin())) {
                    return true;
                }
            }
        }

        return false;
    }


    public void cargarEstado(List<Medico> listaMedicos, Map<String, List<Disponibilidad>> disponibilidades) {
        medicosPorMatricula.clear();
        disponibilidadesPorMedico.clear();

        for (Medico medico : listaMedicos) {
            medicosPorMatricula.put(medico.getMatricula(), medico);
        }

        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidades.entrySet()) {
            disponibilidadesPorMedico.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }


    public List<Medico> listarTodos() {
        return new ArrayList<>(medicosPorMatricula.values());
    }


    public Map<String, List<Disponibilidad>> obtenerDisponibilidadesPorMedico() {
        Map<String, List<Disponibilidad>> copia = new HashMap<>();
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidadesPorMedico.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }
}
