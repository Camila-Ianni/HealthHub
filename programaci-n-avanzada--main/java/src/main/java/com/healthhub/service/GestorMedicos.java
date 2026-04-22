package com.healthhub.service;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Medico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GestorMedicos - maneja los médicos de la clínica y sus disponibilidades.
 * 
 * NOTA: Acá guardamos las disponibilidades en una estructura separada de los médicos.
 * Esto nos permite tener múltiples horarios para cada médico sin complicar la clase Medico.
 */
public class GestorMedicos {
    
    // Mapa principal de médicos por matrícula
    private Map<String, Medico> medicosPorMatricula = new HashMap<>();
    
    // Acá guardamos las disponibilidades de cada médico
    // La clave es la matrícula y el valor es una lista de disponibilidades
    private Map<String, List<Disponibilidad>> disponibilidadesPorMedico = new HashMap<>();
    
    /**
     * Registra un médico nuevo.
     * @param medico el médico a registrar
     * @return true si se pudo registrar, false si la matrícula ya existía
     */
    public boolean registrarMedico(Medico medico) {
        if (medicosPorMatricula.containsKey(medico.getMatricula())) {
            return false;
        }
        
        medicosPorMatricula.put(medico.getMatricula(), medico);
        disponibilidadesPorMedico.put(medico.getMatricula(), new ArrayList<>());
        
        return true;
    }
    
    /**
     * Agrega una disponibilidad para un médico existente.
     * Validamos que no se solape con otras disponibilidades que ya tenga.
     * 
     * @param matricula la matrícula del médico
     * @param disponibilidad el horario a agregar
     * @return true si se agregó, false si el médico no existe o hay solapamiento
     */
    public boolean agregarDisponibilidad(String matricula, Disponibilidad disponibilidad) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);
        
        if (disponibilidades == null) {
            return false;  // El médico no existe
        }
        
        // Verificamos que no se solape con ninguna disponibilidad existente
        if (haySolapamiento(matricula, disponibilidad)) {
            return false;
        }
        
        disponibilidades.add(disponibilidad);
        return true;
    }
    
    /**
     * Reemplaza TODAS las disponibilidades de un médico por una lista nueva.
     * Esto lo usa el administrador cuando quiere cambiar completamente el horario.
     * 
     * @param matricula la matrícula del médico
     * @param nuevasDisponibilidades la lista de disponibilidades que reemplaza a las anteriores
     * @return true si se pudo hacer, false si el médico no existe
     */
    public boolean reemplazarDisponibilidades(String matricula, List<Disponibilidad> nuevasDisponibilidades) {
        if (!medicosPorMatricula.containsKey(matricula)) {
            return false;
        }
        
        disponibilidadesPorMedico.put(matricula, new ArrayList<>(nuevasDisponibilidades));
        return true;
    }
    
    /**
     * Devuelve las disponibilidades de un médico.
     * @param matricula la matrícula del médico
     * @return lista de disponibilidades (vacía si el médico no existe)
     */
    public List<Disponibilidad> consultarDisponibilidad(String matricula) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);
        return disponibilidades != null ? new ArrayList<>(disponibilidades) : new ArrayList<>();
    }
    
    /**
     * Busca un médico por su matrícula.
     * @param matricula la matrícula a buscar
     * @return un Optional con el médico si existe, o vacío si no
     */
    public Optional<Medico> buscarMedico(String matricula) {
        return Optional.ofNullable(medicosPorMatricula.get(matricula));
    }
    
    /**
     * Verifica si un médico está disponible en una fecha y hora específica.
     * Esto lo usamos cuando el recepcionista quiere crear un turno.
     * 
     * @param matricula la matrícula del médico
     * @param fechaHora la fecha y hora a verificar
     * @return true si está disponible, false si no
     */
    public boolean estaDisponible(String matricula, java.time.LocalDateTime fechaHora) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);
        
        if (disponibilidades == null || disponibilidades.isEmpty()) {
            return false;
        }
        
        // Buscamos si hay alguna disponibilidad que coincida con el día y la hora
        int diaSemana = fechaHora.getDayOfWeek().getValue();  // 1=Lunes, 7=Domingo
        java.time.LocalTime hora = fechaHora.toLocalTime();
        
        for (Disponibilidad disp : disponibilidades) {
            if (disp.getDia().getValue() == diaSemana) {
                // Verificamos que la hora esté dentro del rango
                if (!hora.isBefore(disp.getHoraInicio()) && !hora.isAfter(disp.getHoraFin())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si hay solapamiento entre una nueva disponibilidad y las existentes.
     * Lo hacemos para el mismo día y que los horarios se crucen.
     * 
     * @param matricula la matrícula del médico
     * @param nuevaDisponibilidad la disponibilidad a verificar
     * @return true si hay solapamiento, false si no
     */
    private boolean haySolapamiento(String matricula, Disponibilidad nuevaDisponibilidad) {
        List<Disponibilidad> disponibilidades = disponibilidadesPorMedico.get(matricula);
        
        if (disponibilidades == null) {
            return false;
        }
        
        for (Disponibilidad existente : disponibilidades) {
            // Solo verificamos si es el mismo día
            if (existente.getDia() == nuevaDisponibilidad.getDia()) {
                // Hay solapamiento si los horarios se cruzan
                // Esto pasa cuando: inicio1 < fin2 Y inicio2 < fin1
                if (nuevaDisponibilidad.getHoraInicio().isBefore(existente.getHoraFin()) &&
                    existente.getHoraInicio().isBefore(nuevaDisponibilidad.getHoraFin())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Carga el estado de los médicos desde el backup.
     * @param listaMedicos lista de médicos a cargar
     * @param disponibilidades mapa de disponibilidades por médico
     */
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
    
    /**
     * Devuelve todos los médicos registrados.
     */
    public List<Medico> listarTodos() {
        return new ArrayList<>(medicosPorMatricula.values());
    }

    /**
     * Devuelve una copia de todas las disponibilidades por médico.
     */
    public Map<String, List<Disponibilidad>> obtenerDisponibilidadesPorMedico() {
        Map<String, List<Disponibilidad>> copia = new HashMap<>();
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidadesPorMedico.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }
}
