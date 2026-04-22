package com.healthhub.service;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
import com.healthhub.domain.EntradaHistorial;
import com.healthhub.domain.EstadoTurno;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistencia - se encarga de guardar y cargar los datos en archivos .txt.
 * 
 * IDEA: Guardamos cada entidad en su propio archivo para que sea más fácil de probar.
 * Esto es temporal para esta entrega - después se podría pasar a una base de datos.
 * 
 * Archivos que usamos:
 * - pacientes_data.txt
 * - medicos_data.txt
 * - turnos_data.txt
 * - historiales_data.txt
 * - disponibilidades_data.txt
 * - empleados_data.txt
 * - notificaciones_data.txt
 * 
 * TODO: Agregar backup automático cada cierto tiempo
 */
public class Persistencia {
    
    // Ruta base donde guardamos los archivos
    private final Path rutaBase;
    
    // Formatos para fechas y horas - los usamos para parsear y formatear
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public Persistencia(Path rutaBase) {
        this.rutaBase = rutaBase;
    }
    
    // =========================================================================
    // MÉTODOS DE GUARDADO
    // =========================================================================
    
    /**
     * Guarda todos los pacientes en el archivo pacientes_data.txt
     */
    public void guardarPacientes(List<Paciente> pacientes) {
        String archivo = rutaBase.resolve("pacientes_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            // Primera línea con los nombres de las columnas (como header)
            writer.write("DNI|Nombre|Apellido|Telefono|ObraSocial");
            writer.newLine();
            
            for (Paciente p : pacientes) {
                // Usamos | como separador porque es fácil de leer
                String linea = String.join("|",
                    p.getDni(),
                    p.getNombre(),
                    p.getApellido(),
                    p.getTelefono(),
                    p.getObraSocial()
                );
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar pacientes: " + e.getMessage());
            // TODO: Manejar mejor el error, quizás loguearlo en un archivo aparte
        }
    }
    
    /**
     * Guarda todos los médicos en medicos_data.txt
     */
    public void guardarMedicos(List<Medico> medicos) {
        String archivo = rutaBase.resolve("medicos_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("Matricula|Nombre|Apellido|Especialidad");
            writer.newLine();
            
            for (Medico m : medicos) {
                String linea = String.join("|",
                    m.getMatricula(),
                    m.getNombre(),
                    m.getApellido(),
                    m.getEspecialidad()
                );
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar medicos: " + e.getMessage());
        }
    }
    
    /**
     * Guarda las disponibilidades de los médicos
     */
    public void guardarDisponibilidades(Map<String, List<Disponibilidad>> disponibilidades) {
        String archivo = rutaBase.resolve("disponibilidades_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("Matricula|Dia|HoraInicio|HoraFin");
            writer.newLine();
            
            for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidades.entrySet()) {
                String matricula = entry.getKey();
                for (Disponibilidad disp : entry.getValue()) {
                    String linea = String.join("|",
                        matricula,
                        String.valueOf(disp.getDia().getValue()),  // 1=Lunes, 7=Domingo
                        disp.getHoraInicio().format(TIME_FORMAT),
                        disp.getHoraFin().format(TIME_FORMAT)
                    );
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar disponibilidades: " + e.getMessage());
        }
    }
    
    /**
     * Guarda todos los turnos en turnos_data.txt
     */
    public void guardarTurnos(List<Turno> turnos) {
        String archivo = rutaBase.resolve("turnos_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("ID|DniPaciente|MatriculaMedico|FechaHora|Estado|Sobreturno");
            writer.newLine();
            
            for (Turno t : turnos) {
                String linea = String.join("|",
                    t.getId(),
                    t.getDniPaciente(),
                    t.getMatriculaMedico(),
                    t.getFechaHora().format(DATETIME_FORMAT),
                    t.getEstado().name(),
                    String.valueOf(t.isSobreturno())
                );
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar turnos: " + e.getMessage());
        }
    }
    
    /**
     * Guarda los historiales clínicos
     */
    public void guardarHistoriales(List<HistorialClinico> historiales) {
        String archivo = rutaBase.resolve("historiales_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("DniPaciente|Fecha|Resumen|Diagnostico|Estudios");
            writer.newLine();
            
            for (HistorialClinico historial : historiales) {
                String dniPaciente = historial.getDniPaciente();
                
                // Si el historial está vacío, igual lo guardamos con una línea vacía
                if (historial.getEntradas().isEmpty()) {
                    writer.write(dniPaciente + "||||");
                    writer.newLine();
                    continue;
                }
                
                for (EntradaHistorial entrada : historial.getEntradas()) {
                    String linea = String.join("|",
                        dniPaciente,
                        entrada.getFecha().format(DATETIME_FORMAT),
                        entrada.getResumen(),
                        entrada.getDiagnostico(),
                        entrada.getEstudios() != null ? entrada.getEstudios() : ""
                    );
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar historiales: " + e.getMessage());
        }
    }
    
    /**
     * Guarda los empleados
     */
    public void guardarEmpleados(List<Empleado> empleados) {
        String archivo = rutaBase.resolve("empleados_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("Legajo|Nombre|Rol");
            writer.newLine();
            
            for (Empleado e : empleados) {
                String linea = String.join("|",
                    e.getLegajo(),
                    e.getNombre(),
                    e.getRol().name()
                );
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar empleados: " + e.getMessage());
        }
    }
    
    /**
     * Guarda las notificaciones de los médicos
     */
    public void guardarNotificaciones(Map<String, List<String>> notificaciones) {
        String archivo = rutaBase.resolve("notificaciones_data.txt").toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("Matricula|Mensaje");
            writer.newLine();
            
            for (Map.Entry<String, List<String>> entry : notificaciones.entrySet()) {
                String matricula = entry.getKey();
                for (String mensaje : entry.getValue()) {
                    writer.write(matricula + "|" + mensaje);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar notificaciones: " + e.getMessage());
        }
    }
    
    // =========================================================================
    // MÉTODOS DE CARGA
    // =========================================================================
    
    /**
     * Carga los pacientes desde el archivo
     */
    public List<Paciente> cargarPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String archivo = rutaBase.resolve("pacientes_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 5) {
                    pacientes.add(new Paciente(
                        partes[0],  // DNI
                        partes[1],  // Nombre
                        partes[2],  // Apellido
                        partes[3],  // Teléfono
                        partes[4]   // Obra social
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar pacientes (quizás el archivo no existe): " + e.getMessage());
        }
        
        return pacientes;
    }
    
    /**
     * Carga los médicos desde el archivo
     */
    public List<Medico> cargarMedicos() {
        List<Medico> medicos = new ArrayList<>();
        String archivo = rutaBase.resolve("medicos_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 4) {
                    medicos.add(new Medico(
                        partes[0],  // Matrícula
                        partes[1],  // Nombre
                        partes[2],  // Apellido
                        partes[3]   // Especialidad
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar medicos: " + e.getMessage());
        }
        
        return medicos;
    }
    
    /**
     * Carga las disponibilidades desde el archivo
     */
    public Map<String, List<Disponibilidad>> cargarDisponibilidades() {
        Map<String, List<Disponibilidad>> disponibilidades = new HashMap<>();
        String archivo = rutaBase.resolve("disponibilidades_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 4) {
                    String matricula = partes[0];
                    int diaNum = Integer.parseInt(partes[1]);
                    LocalTime inicio = LocalTime.parse(partes[2], TIME_FORMAT);
                    LocalTime fin = LocalTime.parse(partes[3], TIME_FORMAT);
                    
                    Disponibilidad disp = new Disponibilidad(DayOfWeek.of(diaNum), inicio, fin);
                    disponibilidades.computeIfAbsent(matricula, k -> new ArrayList<>()).add(disp);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar disponibilidades: " + e.getMessage());
        }
        
        return disponibilidades;
    }
    
    /**
     * Carga los turnos desde el archivo
     */
    public List<Turno> cargarTurnos() {
        List<Turno> turnos = new ArrayList<>();
        String archivo = rutaBase.resolve("turnos_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 6) {
                    turnos.add(new Turno(
                        partes[0],  // ID
                        partes[1],  // DNI paciente
                        partes[2],  // Matrícula médico
                        LocalDateTime.parse(partes[3], DATETIME_FORMAT),
                        Boolean.parseBoolean(partes[5]),  // Sobreturno
                        EstadoTurno.valueOf(partes[4])    // Estado
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar turnos: " + e.getMessage());
        }
        
        return turnos;
    }
    
    /**
     * Carga los historiales clínicos desde el archivo
     */
    public List<HistorialClinico> cargarHistoriales() {
        List<HistorialClinico> historiales = new ArrayList<>();
        Map<String, HistorialClinico> mapaHistoriales = new HashMap<>();
        String archivo = rutaBase.resolve("historiales_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 2) {
                    String dniPaciente = partes[0];
                    
                    // Creamos el historial si no existe
                    HistorialClinico historial = mapaHistoriales.computeIfAbsent(
                        dniPaciente, k -> new HistorialClinico(k)
                    );
                    
                    // Si hay datos de entrada, los agregamos
                    if (partes.length >= 5 && !partes[1].isEmpty()) {
                        EntradaHistorial entrada = new EntradaHistorial(
                            LocalDateTime.parse(partes[1], DATETIME_FORMAT),
                            partes[2],  // Resumen
                            partes[3],  // Diagnóstico
                            partes[4]   // Estudios
                        );
                        historial.agregarEntrada(entrada);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar historiales: " + e.getMessage());
        }
        
        historiales.addAll(mapaHistoriales.values());
        return historiales;
    }
    
    /**
     * Carga los empleados desde el archivo
     */
    public List<Empleado> cargarEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        String archivo = rutaBase.resolve("empleados_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 3) {
                    empleados.add(new Empleado(
                        partes[0],  // Legajo
                        partes[1],  // Nombre
                        RolUsuario.valueOf(partes[2])  // Rol
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar empleados: " + e.getMessage());
        }
        
        return empleados;
    }
    
    /**
     * Carga las notificaciones desde el archivo
     */
    public Map<String, List<String>> cargarNotificaciones() {
        Map<String, List<String>> notificaciones = new HashMap<>();
        String archivo = rutaBase.resolve("notificaciones_data.txt").toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();  // Saltamos el header
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|", 2);  // Split en 2 partes máximo
                if (partes.length >= 2) {
                    String matricula = partes[0];
                    String mensaje = partes[1];
                    notificaciones.computeIfAbsent(matricula, k -> new ArrayList<>()).add(mensaje);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar notificaciones: " + e.getMessage());
        }
        
        return notificaciones;
    }
}
