package com.healthhub.domain;

/**
 * Paciente - representa a un paciente de la clínica.
 * 
 * Los datos que guardamos son los que nos pidieron en la consigna:
 * - DNI (único, no se puede repetir)
 * - Nombre
 * - Apellido
 * - Teléfono
 * - Obra social
 * 
 * NOTA: El DNI es final porque no tiene sentido cambiarlo una vez registrado.
 * El resto de los datos sí se pueden modificar si el paciente nos avisa.
 */
public class Paciente {
    
    private final String dni;        // Documento Nacional de Identidad (único)
    private String nombre;           // Nombre del paciente
    private String apellido;         // Apellido del paciente
    private String telefono;         // Teléfono de contacto
    private String obraSocial;       // Nombre de la obra social
    
    /**
     * Constructor de la clase Paciente.
     * 
     * @param dni el DNI del paciente (sin puntos ni espacios)
     * @param nombre el nombre
     * @param apellido el apellido
     * @param telefono el teléfono
     * @param obraSocial la obra social
     */
    public Paciente(String dni, String nombre, String apellido, String telefono, String obraSocial) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.obraSocial = obraSocial;
    }
    
    // Getters - todos los campos tienen getter para poder leerlos
    
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
    
    // Setters - solo los campos modificables tienen setter (el DNI no)
    
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
    
    /**
     * Devuelve el nombre completo del paciente.
     * Lo usamos para mostrar en las búsquedas y para indexar.
     */
    public String nombreCompleto() {
        return nombre + " " + apellido;
    }
    
    @Override
    public String toString() {
        return "Paciente{dni='" + dni + "', nombre='" + nombre + "', apellido='" + apellido + "', obraSocial='" + obraSocial + "'}";
    }
}
