package com.healthhub.domain;

/**
 * Empleado - representa a un empleado que puede acceder al sistema.
 * 
 * Cada empleado tiene:
 * - Legajo (único, es el identificador interno)
 * - Nombre
 * - Rol (RECEPCIONISTA, MEDICO, ADMINISTRADOR)
 * 
 * NOTA: En una versión más completa, esto tendría login con contraseña.
 * Pero para esta entrega solo registramos los empleados sin autenticación.
 */
public class Empleado {
    
    private final String legajo;       // Legajo del empleado (único)
    private final String nombre;       // Nombre completo
    private final RolUsuario rol;      // Rol que tiene en el sistema
    
    public Empleado(String legajo, String nombre, RolUsuario rol) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.rol = rol;
    }
    
    public String getLegajo() {
        return legajo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public RolUsuario getRol() {
        return rol;
    }
    
    @Override
    public String toString() {
        return "Empleado{legajo='" + legajo + "', nombre='" + nombre + "', rol=" + rol + "}";
    }
}
