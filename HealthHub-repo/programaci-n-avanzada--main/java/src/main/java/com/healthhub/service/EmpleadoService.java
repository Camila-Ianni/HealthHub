package com.healthhub.service;

import com.healthhub.domain.Empleado;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class EmpleadoService {
    private final Map<String, Empleado> empleadosPorLegajo = new LinkedHashMap<>();

    public boolean registrar(Empleado empleado) {
        if (empleadosPorLegajo.containsKey(empleado.getLegajo())) {
            return false;
        }
        empleadosPorLegajo.put(empleado.getLegajo(), empleado);
        return true;
    }

    public List<Empleado> listar() {
        return new ArrayList<>(empleadosPorLegajo.values());
    }

    public void cargarEmpleados(List<Empleado> empleados) {
        empleadosPorLegajo.clear();
        for (Empleado empleado : empleados) {
            empleadosPorLegajo.put(empleado.getLegajo(), empleado);
        }
    }
}
