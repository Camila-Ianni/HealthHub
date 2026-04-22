package com.healthhub.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GestorNotificaciones - maneja las notificaciones que ve cada médico.
 * 
 * Esto es bien simple: cada médico tiene una lista de mensajes que puede consultar.
 * En una versión real esto sería más complejo, pero para la entrega alcanza.
 */
public class GestorNotificaciones {
    
    // Mapa de notificaciones por matrícula de médico
    // La clave es la matrícula y el valor es una lista de mensajes
    private Map<String, List<String>> notificacionesPorMedico = new HashMap<>();
    
    /**
     * Agrega una notificación para un médico.
     * 
     * @param matricula la matrícula del médico
     * @param mensaje el mensaje a notificar
     */
    public void agregarNotificacion(String matricula, String mensaje) {
        notificacionesPorMedico.computeIfAbsent(matricula, k -> new ArrayList<>()).add(mensaje);
    }
    
    /**
     * Devuelve todas las notificaciones de un médico.
     * 
     * @param matricula la matrícula del médico
     * @return lista de mensajes (vacía si no hay notificaciones)
     */
    public List<String> verNotificaciones(String matricula) {
        List<String> notificaciones = notificacionesPorMedico.get(matricula);
        return notificaciones != null ? new ArrayList<>(notificaciones) : new ArrayList<>();
    }
    
    /**
     * Limpia las notificaciones de un médico después de que las vio.
     * 
     * @param matricula la matrícula del médico
     */
    public void limpiarNotificaciones(String matricula) {
        notificacionesPorMedico.put(matricula, new ArrayList<>());
    }
    
    /**
     * Carga notificaciones desde el backup.
     */
    public void cargarNotificaciones(Map<String, List<String>> notificaciones) {
        notificacionesPorMedico.clear();
        for (Map.Entry<String, List<String>> entry : notificaciones.entrySet()) {
            notificacionesPorMedico.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * Devuelve una copia de todas las notificaciones por médico.
     */
    public Map<String, List<String>> listarTodas() {
        Map<String, List<String>> copia = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : notificacionesPorMedico.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }
}
