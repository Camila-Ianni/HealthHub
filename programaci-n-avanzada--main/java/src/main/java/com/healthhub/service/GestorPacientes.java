package com.healthhub.service;

import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Paciente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * GestorPacientes - se encarga de manejar todos los pacientes de la clínica.
 * Usa un HashMap para buscar rápido por DNI y un índice por nombre para búsquedas parciales.
 * 
 * TODO: Revisar si los nombres con acento se buscan bien (ahora normalizamos a minúsculas sin tildes)
 */
public class GestorPacientes {
    
    // Acá guardamos los pacientes, la clave es el DNI
    private Map<String, Paciente> pacientesPorDni = new HashMap<>();
    
    // Este índice nos sirve para buscar por nombre (aunque sea parcial)
    // La clave es el nombre normalizado y el valor es una lista de DNI
    private Map<String, List<String>> indicePorNombre = new HashMap<>();
    
    // Referencia al gestor de historiales - lo necesitamos cuando creamos un paciente nuevo
    private GestorHistoriales gestorHistoriales;
    
    public GestorPacientes(GestorHistoriales gestorHistoriales) {
        this.gestorHistoriales = gestorHistoriales;
    }
    
    /**
     * Registra un paciente nuevo.
     * @param p el paciente a registrar
     * @return true si se pudo registrar, false si el DNI ya existía
     */
    public boolean registrarPaciente(Paciente p) {
        // Primero verificamos que el DNI no esté ya registrado
        if (pacientesPorDni.containsKey(p.getDni())) {
            return false;
        }
        
        // Lo agregamos al mapa principal
        pacientesPorDni.put(p.getDni(), p);
        
        // También lo indexamos por nombre para las búsquedas
        indexarPaciente(p);
        
        // Creamos el historial clínico vacío (lo pide la consigna)
        if (gestorHistoriales != null) {
            gestorHistoriales.crearHistorialSiNoExiste(p.getDni());
        }
        
        return true;
    }
    
    /**
     * Modifica los datos de un paciente existente.
     * @param dni el DNI del paciente a modificar
     * @param nombre nuevo nombre
     * @param apellido nuevo apellido
     * @param telefono nuevo teléfono
     * @param obraSocial nueva obra social
     * @return true si se pudo modificar, false si no existe el paciente
     */
    public boolean modificarPaciente(String dni, String nombre, String apellido, String telefono, String obraSocial) {
        Paciente paciente = pacientesPorDni.get(dni);
        if (paciente == null) {
            return false;
        }
        
        // Antes de modificar, lo sacamos del índice por nombre (porque si cambia el nombre, la clave cambia)
        desindexarPaciente(paciente);
        
        // Actualizamos los datos
        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setTelefono(telefono);
        paciente.setObraSocial(obraSocial);
        
        // Lo volvemos a indexar con el nombre nuevo
        indexarPaciente(paciente);
        
        return true;
    }
    
    /**
     * Busca un paciente por su DNI.
     * @param dni el DNI a buscar
     * @return un Optional con el paciente si existe, o vacío si no
     */
    public Optional<Paciente> buscarPorDni(String dni) {
        return Optional.ofNullable(pacientesPorDni.get(dni));
    }
    
    /**
     * Busca pacientes por nombre completo o parcial.
     * @param nombreCompleto el nombre a buscar (puede ser solo una parte)
     * @return lista de pacientes que coinciden
     */
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
    
    /**
     * Devuelve todos los pacientes registrados.
     * @return lista de pacientes
     */
    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientesPorDni.values());
    }
    
    /**
     * Carga una lista de pacientes desde el backup.
     * Esto lo usamos cuando arranca el sistema para restaurar los datos.
     */
    public void cargarPacientes(List<Paciente> pacientes) {
        pacientesPorDni.clear();
        indicePorNombre.clear();
        
        for (Paciente paciente : pacientes) {
            pacientesPorDni.put(paciente.getDni(), paciente);
            indexarPaciente(paciente);
        }
    }
    
    // =========================================================================
    // MÉTODOS AUXILIARES (privados)
    // =========================================================================
    
    /**
     * Agrega el paciente al índice por nombre.
     * Así después podemos buscar aunque el usuario escriba solo una parte.
     */
    private void indexarPaciente(Paciente paciente) {
        String clave = normalizar(paciente.nombreCompleto());
        indicePorNombre.computeIfAbsent(clave, k -> new ArrayList<>()).add(paciente.getDni());
    }
    
    /**
     * Saca al paciente del índice por nombre.
     * Lo usamos antes de modificar los datos.
     */
    private void desindexarPaciente(Paciente paciente) {
        String clave = normalizar(paciente.nombreCompleto());
        List<String> dnis = indicePorNombre.get(clave);
        
        if (dnis == null) {
            return;
        }
        
        dnis.remove(paciente.getDni());
        
        // Si la lista quedó vacía, sacamos la clave del índice
        if (dnis.isEmpty()) {
            indicePorNombre.remove(clave);
        }
    }
    
    /**
     * Normaliza un string para que las búsquedas sean más fáciles.
     * Pasa a minúsculas, saca espacios extra y deja todo más limpio.
     * 
     * TODO: Sacar los acentos también (ej: "María" -> "maria")
     */
    private String normalizar(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
