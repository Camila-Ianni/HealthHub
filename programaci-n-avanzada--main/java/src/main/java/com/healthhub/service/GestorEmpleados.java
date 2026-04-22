package com.healthhub.service;

import com.healthhub.domain.Empleado;
import com.healthhub.domain.RolUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GestorEmpleados - maneja los empleados del sistema (recepcionistas, médicos, administradores).
 * 
 * NOTA: En realidad los médicos ya tienen su propio gestor, pero acá guardamos
 * los empleados que pueden acceder al sistema con su rol correspondiente.
 */
public class GestorEmpleados {
    
    // Mapa de empleados por legajo
    private Map<String, Empleado> empleadosPorLegajo = new HashMap<>();
    
    /**
     * Registra un empleado nuevo.
     * 
     * @param legajo el legajo del empleado
     * @param nombre el nombre completo
     * @param rol el rol que va a tener (RECEPCIONISTA, MEDICO, ADMINISTRADOR)
     * @return true si se registró, false si el legajo ya existía
     */
    public boolean registrarEmpleado(String legajo, String nombre, RolUsuario rol) {
        if (empleadosPorLegajo.containsKey(legajo)) {
            return false;
        }
        
        empleadosPorLegajo.put(legajo, new Empleado(legajo, nombre, rol));
        return true;
    }
    
    /**
     * Busca un empleado por su legajo.
     * 
     * @param legajo el legajo a buscar
     * @return un Optional con el empleado si existe, o vacío si no
     */
    public Optional<Empleado> buscarPorLegajo(String legajo) {
        return Optional.ofNullable(empleadosPorLegajo.get(legajo));
    }
    
    /**
     * Devuelve todos los empleados registrados.
     */
    public List<Empleado> listarTodos() {
        return new ArrayList<>(empleadosPorLegajo.values());
    }
    
    /**
     * Carga empleados desde el backup.
     */
    public void cargarEmpleados(List<Empleado> listaEmpleados) {
        empleadosPorLegajo.clear();
        for (Empleado empleado : listaEmpleados) {
            empleadosPorLegajo.put(empleado.getLegajo(), empleado);
        }
    }
}
