package com.healthhub.domain;

/**
 * Medico - representa a un médico de la clínica.
 * 
 * Datos que guardamos:
 * - Matrícula (única, es el identificador del médico)
 * - Nombre
 * - Apellido
 * - Especialidad
 * 
 * NOTA: La especialidad la pusimos como String simple. En un sistema real
 * quizás tendríamos una lista de especialidades o una clase aparte, pero
 * para esta entrega con un String alcanza.
 * 
 * TODO: Revisar si los médicos pueden tener más de una especialidad.
 * Por ahora asumimos que no, como nos dijo el personal de la clínica.
 */
public class Medico {
    
    private final String matricula;    // Matrícula profesional (única)
    private String nombre;             // Nombre del médico
    private String apellido;           // Apellido del médico
    private String especialidad;       // Especialidad principal
    
    /**
     * Constructor de la clase Medico.
     * 
     * @param matricula la matrícula profesional
     * @param nombre el nombre
     * @param apellido el apellido
     * @param especialidad la especialidad
     */
    public Medico(String matricula, String nombre, String apellido, String especialidad) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
    }
    
    // Getters - todos los campos tienen getter
    
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
    
    // Setters - solo la especialidad se puede modificar
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    @Override
    public String toString() {
        return "Medico{matricula='" + matricula + "', nombre='" + nombre + " " + apellido + "', especialidad='" + especialidad + "'}";
    }
}
