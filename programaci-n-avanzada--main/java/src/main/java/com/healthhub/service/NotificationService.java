package com.healthhub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {
    private final Map<String, List<String>> notificacionesPorMedico = new HashMap<>();

    public void notificarMedico(String matricula, String mensaje) {
        String registro = LocalDateTime.now() + " - " + mensaje;
        notificacionesPorMedico.computeIfAbsent(matricula, key -> new ArrayList<>()).add(registro);
    }

    public List<String> verNotificaciones(String matricula) {
        return new ArrayList<>(notificacionesPorMedico.getOrDefault(matricula, List.of()));
    }

    public Map<String, List<String>> snapshotNotificaciones() {
        Map<String, List<String>> copia = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : notificacionesPorMedico.entrySet()) {
            copia.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copia;
    }

    public void cargarNotificaciones(Map<String, List<String>> notificaciones) {
        notificacionesPorMedico.clear();
        for (Map.Entry<String, List<String>> entry : notificaciones.entrySet()) {
            notificacionesPorMedico.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }
}
