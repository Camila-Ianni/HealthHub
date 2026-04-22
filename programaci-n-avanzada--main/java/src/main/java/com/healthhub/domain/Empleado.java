package com.healthhub.domain;

public class Empleado {
    private final String legajo;
    private final String nombre;
    private final RolUsuario rol;

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
}
