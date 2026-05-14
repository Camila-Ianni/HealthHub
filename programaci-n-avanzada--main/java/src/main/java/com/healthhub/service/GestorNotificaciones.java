package com.healthhub.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GestorNotificaciones {

    private Map<String, List<String>> notificacionesPorMedico = new HashMap<>();


    public void agregarNotificacion(String matricula, String mensaje) {
        notificacionesPorMedico.computeIfAbsent(matricula, k -> new ArrayList<>()).add(mensaje);
    }


    public List<String> verNotificaciones(String matricula) {
        List<String> notificaciones = notificacionesPorMedico.get(matricula);
        return notificaciones != null ? new ArrayList<>(notificaciones) : new ArrayList<>();
    }


    public void limpiarNotificaciones(String matricula) {
        notificacionesPorMedico.put(matricula, new ArrayList<>());
    }


    public void cargarNotificaciones(Map<String, List<String>> notificaciones) {
        notificacionesPorMedico.clear();
        for (Map.Entry<String, List<String>> entry : notificaciones.entrySet()) {
            notificacionesPorMedico.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }


    public Map<String, List<String>> listarTodas() {
        Map<String, List<String>> copia = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : notificacionesPorMedico.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }
}
