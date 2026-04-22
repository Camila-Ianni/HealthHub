package com.healthhub.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class AutoSaveService {
    private final Path backupPath;

    public AutoSaveService(Path backupPath) {
        this.backupPath = backupPath;
    }

    public synchronized void guardarEstado(String contenido) {
        try {
            Path parent = backupPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(backupPath, contenido, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("[WARN] No se pudo guardar backup automatico: " + e.getMessage());
        }
    }

    public synchronized Optional<String> cargarEstado() {
        if (!Files.exists(backupPath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readString(backupPath, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("[WARN] No se pudo leer backup: " + e.getMessage());
            return Optional.empty();
        }
    }
}
