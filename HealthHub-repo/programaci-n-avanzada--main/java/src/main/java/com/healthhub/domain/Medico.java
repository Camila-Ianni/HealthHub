package com.healthhub.domain;

public class Medico {
    private final String matricula;
    private String nombre;
    private String apellido;
    private String especialidad;

    public Medico(String matricula, String nombre, String apellido, String especialidad) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "Medico{matricula='" + matricula + "', nombre='" + nombre + " " + apellido + "', especialidad='" + especialidad + "'}";
    }
}
