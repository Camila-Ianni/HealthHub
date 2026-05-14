package com.healthhub.service;

import com.healthhub.domain.Empleado;
import com.healthhub.domain.RolUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GestorEmpleados {

    private Map<String, Empleado> empleadosPorLegajo = new HashMap<>();


    public boolean registrarEmpleado(String legajo, String nombre, RolUsuario rol) {
        if (empleadosPorLegajo.containsKey(legajo)) {
            return false;
        }

        empleadosPorLegajo.put(legajo, new Empleado(legajo, nombre, rol));
        return true;
    }


    public Optional<Empleado> buscarPorLegajo(String legajo) {
        return Optional.ofNullable(empleadosPorLegajo.get(legajo));
    }


    public List<Empleado> listarTodos() {
        return new ArrayList<>(empleadosPorLegajo.values());
    }


    public void cargarEmpleados(List<Empleado> listaEmpleados) {
        empleadosPorLegajo.clear();
        for (Empleado empleado : listaEmpleados) {
            empleadosPorLegajo.put(empleado.getLegajo(), empleado);
        }
    }
}
