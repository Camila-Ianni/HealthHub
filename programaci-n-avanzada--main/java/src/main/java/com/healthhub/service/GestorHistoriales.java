package com.healthhub.service;

import com.healthhub.domain.EntradaHistorial;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.RolUsuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GestorHistoriales - maneja los historiales clínicos de los pacientes.
 * 
 * IMPORTANTE: Solo los médicos pueden modificar los historiales.
 * Esto lo tendríamos que validar bien, pero para esta entrega lo hacemos simple.
 */
public class GestorHistoriales {
    
    // Acá guardamos los historiales, la clave es el DNI del paciente
    private Map<String, HistorialClinico> historialesPorDni = new HashMap<>();
    
    /**
     * Crea un historial clínico vacío para un paciente.
     * Lo llamamos cuando se registra un paciente nuevo.
     * 
     * @param dniPaciente el DNI del paciente
     * @return el historial creado (o el que ya existía)
     */
    public HistorialClinico crearHistorialSiNoExiste(String dniPaciente) {
        if (!historialesPorDni.containsKey(dniPaciente)) {
            historialesPorDni.put(dniPaciente, new HistorialClinico(dniPaciente));
        }
        return historialesPorDni.get(dniPaciente);
    }
    
    /**
     * Registra una consulta médica en el historial de un paciente.
     * 
     * @param rol el rol del que hace la operación (debería ser MEDICO)
     * @param dniPaciente el DNI del paciente
     * @param resumen resumen de la consulta
     * @param diagnostico el diagnóstico
     * @param estudios los estudios médicos (puede ser vacío)
     * @return true si se registró, false si no existe el historial
     */
    public boolean registrarConsulta(RolUsuario rol, String dniPaciente, String resumen, String diagnostico, String estudios) {
        // TODO: Validar que el rol sea MEDICO antes de permitir registrar
        // Por ahora lo dejamos pasar para la maqueta
        
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }
        
        EntradaHistorial entrada = new EntradaHistorial(
            LocalDateTime.now(),
            resumen,
            diagnostico,
            estudios
        );
        
        historial.agregarEntrada(entrada);
        return true;
    }
    
    /**
     * Actualiza el diagnóstico de la última entrada del historial.
     * 
     * @param dniPaciente el DNI del paciente
     * @param nuevoDiagnostico el nuevo diagnóstico
     * @return true si se actualizó, false si no hay entradas en el historial
     */
    public boolean actualizarDiagnostico(String dniPaciente, String nuevoDiagnostico) {
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }
        
        List<EntradaHistorial> entradas = historial.getEntradas();
        if (entradas.isEmpty()) {
            return false;
        }
        
        // Obtenemos la última entrada y le actualizamos el diagnóstico
        EntradaHistorial ultima = entradas.get(entradas.size() - 1);
        ultima.setDiagnostico(nuevoDiagnostico);
        
        return true;
    }
    
    /**
     * Actualiza los estudios de la última entrada del historial.
     * 
     * @param dniPaciente el DNI del paciente
     * @param nuevosEstudios los nuevos estudios
     * @return true si se actualizó, false si no hay entradas en el historial
     */
    public boolean actualizarEstudios(String dniPaciente, String nuevosEstudios) {
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial == null) {
            return false;
        }
        
        List<EntradaHistorial> entradas = historial.getEntradas();
        if (entradas.isEmpty()) {
            return false;
        }
        
        EntradaHistorial ultima = entradas.get(entradas.size() - 1);
        ultima.setEstudios(nuevosEstudios);
        
        return true;
    }
    
    /**
     * Devuelve el historial clínico de un paciente.
     * 
     * @param dniPaciente el DNI del paciente
     * @return el historial clínico, o null si no existe
     */
    public HistorialClinico verHistorial(String dniPaciente) {
        return historialesPorDni.get(dniPaciente);
    }
    
    /**
     * Agrega una entrada directamente al historial.
     * Lo usamos cuando cargamos desde el backup.
     * 
     * @param dniPaciente el DNI del paciente
     * @param entrada la entrada a agregar
     */
    public void agregarEntrada(String dniPaciente, EntradaHistorial entrada) {
        HistorialClinico historial = historialesPorDni.get(dniPaciente);
        if (historial != null) {
            historial.agregarEntrada(entrada);
        }
    }
    
    /**
     * Carga los historiales desde el backup.
     * 
     * @param listaHistoriales lista de historiales a cargar
     */
    public void cargarHistoriales(List<HistorialClinico> listaHistoriales) {
        historialesPorDni.clear();
        for (HistorialClinico historial : listaHistoriales) {
            historialesPorDni.put(historial.getDniPaciente(), historial);
        }
    }
    
    /**
     * Devuelve todos los historiales registrados.
     * Lo usamos para guardar en archivos.
     */
    public List<HistorialClinico> listarTodos() {
        return new ArrayList<>(historialesPorDni.values());
    }
}
