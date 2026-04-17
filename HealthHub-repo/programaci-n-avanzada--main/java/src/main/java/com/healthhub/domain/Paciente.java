package com.healthhub.domain;

public class Paciente {
    private final String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String obraSocial;

    public Paciente(String dni, String nombre, String apellido, String telefono, String obraSocial) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.obraSocial = obraSocial;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public String nombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return "Paciente{dni='" + dni + "', nombre='" + nombre + "', apellido='" + apellido + "', obraSocial='" + obraSocial + "'}";
    }
}
