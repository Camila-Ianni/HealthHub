package com.healthhub.app;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;
import com.healthhub.service.GestorPacientes;
import com.healthhub.service.GestorMedicos;
import com.healthhub.service.GestorTurnos;
import com.healthhub.service.GestorHistoriales;
import com.healthhub.service.GestorEmpleados;
import com.healthhub.service.GestorNotificaciones;
import com.healthhub.service.Persistencia;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * App - Clase principal del sistema Health Hub.
 * 
 * Esta es la maqueta del sistema para la primera entrega.
 * Los menús están implementados pero las funciones están vacías (solo muestran mensajes).
 * 
 * NOTA: Usamos Scanner para leer de la consola. Es lo que vimos en clase.
 * 
 * @author Alumnos de Programación Avanzada
 * @version 1.0 - Primera Entrega
 */
public class App {
    
    // Scanner para leer de la consola - lo usamos en todo el programa
    private static Scanner scanner;
    
    // Servicios del sistema - los inicializamos en el main
    private static GestorPacientes gestorPacientes;
    private static GestorMedicos gestorMedicos;
    private static GestorTurnos gestorTurnos;
    private static GestorHistoriales gestorHistoriales;
    private static GestorEmpleados gestorEmpleados;
    private static GestorNotificaciones gestorNotifs;
    private static Persistencia persistencia;
    
    // Ruta donde guardamos los archivos de datos
    private static final Path RUTA_DATOS = Path.of("data");
    private static final String CARPETA_BACKUP = "data";

    public static void main(String[] args) {
        // Inicializamos todo el sistema
        inicializarSistema();
        
        boolean continuar = true;
        
        // Bucle principal del menú
        while (continuar) {
            mostrarMenuPrincipal();
            System.out.print("Seleccione una opcion: ");
            String opcion = scanner.nextLine();
            
            // Usamos switch clásico como vimos en clase
            switch (opcion) {
                case "1":
                    menuRecepcionista();
                    break;
                case "2":
                    menuMedico();
                    break;
                case "3":
                    menuAdministrador();
                    break;
                case "0":
                    continuar = false;
                    guardarBackup(CARPETA_BACKUP);
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente de nuevo.");
                    break;
            }
        }
        
        scanner.close();
    }
    
    /**
     * Inicializa todos los servicios y carga los datos guardados.
     */
    private static void inicializarSistema() {
        scanner = new Scanner(System.in);
        
        // Creamos la carpeta de datos si no existe
        RUTA_DATOS.toFile().mkdirs();
        
        // Inicializamos los servicios
        gestorHistoriales = new GestorHistoriales();
        gestorPacientes = new GestorPacientes(gestorHistoriales);
        gestorMedicos = new GestorMedicos();
        gestorNotifs = new GestorNotificaciones();
        gestorTurnos = new GestorTurnos(gestorMedicos, gestorNotifs);
        gestorEmpleados = new GestorEmpleados();

        // Inicializamos la persistencia y restauramos estado
        persistencia = new Persistencia(RUTA_DATOS);
        cargarBackup(CARPETA_BACKUP);
        
        System.out.println("[INFO] Sistema inicializado correctamente.");
        informarHorarioLaboral();
    }
    
    /**
     * Carga todos los datos desde los archivos de persistencia.
     */
    private static void cargarDatosDeArchivos() {
        // Cargamos médicos primero (los turnos los necesitan)
        List<com.healthhub.domain.Medico> medicosCargados = persistencia.cargarMedicos();
        Map<String, List<Disponibilidad>> disponibilidadesCargadas = persistencia.cargarDisponibilidades();
        
        for (com.healthhub.domain.Medico medico : medicosCargados) {
            gestorMedicos.registrarMedico(medico);
        }
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidadesCargadas.entrySet()) {
            for (Disponibilidad disp : entry.getValue()) {
                gestorMedicos.agregarDisponibilidad(entry.getKey(), disp);
            }
        }
        
        // Cargamos pacientes
        List<Paciente> pacientesCargados = persistencia.cargarPacientes();
        for (Paciente paciente : pacientesCargados) {
            gestorPacientes.registrarPaciente(paciente);
        }

        // Cargamos historiales
        List<HistorialClinico> historialesCargados = persistencia.cargarHistoriales();
        gestorHistoriales.cargarHistoriales(historialesCargados);
        
        // Cargamos turnos
        List<Turno> turnosCargados = persistencia.cargarTurnos();
        gestorTurnos.cargarTurnos(turnosCargados);
        
        // Cargamos notificaciones
        Map<String, List<String>> notificacionesCargadas = persistencia.cargarNotificaciones();
        gestorNotifs.cargarNotificaciones(notificacionesCargadas);
        
        // Cargamos empleados
        List<com.healthhub.domain.Empleado> empleadosCargados = persistencia.cargarEmpleados();
        for (com.healthhub.domain.Empleado empleado : empleadosCargados) {
            gestorEmpleados.registrarEmpleado(empleado.getLegajo(), empleado.getNombre(), empleado.getRol());
        }
        
        System.out.println("[INFO] Datos cargados: " + pacientesCargados.size() + " pacientes, "
            + medicosCargados.size() + " médicos, " + turnosCargados.size() + " turnos.");
    }
    
    /**
     * Guarda todos los datos en los archivos correspondientes.
     * Lo llamamos antes de cerrar el sistema.
     */
    private static void guardarDatosEnArchivos() {
        System.out.println("[INFO] Guardando datos en archivos...");
        
        // Guardamos cada entidad en su archivo
        persistencia.guardarPacientes(gestorPacientes.listarTodos());
        persistencia.guardarMedicos(gestorMedicos.listarTodos());
        persistencia.guardarEmpleados(gestorEmpleados.listarTodos());
        persistencia.guardarTurnos(gestorTurnos.listarTodos());
        persistencia.guardarHistoriales(gestorHistoriales.listarTodos());
        persistencia.guardarDisponibilidades(gestorMedicos.obtenerDisponibilidadesPorMedico());
        persistencia.guardarNotificaciones(gestorNotifs.listarTodas());
        
        System.out.println("[INFO] Datos guardados correctamente.");
    }
    
    private static void cargarBackup(String carpeta) {
        System.out.println("[INFO] Cargando backup desde '" + carpeta + "'...");
        cargarDatosDeArchivos();
    }
    
    private static void guardarBackup(String carpeta) {
        System.out.println("[INFO] Guardando backup en '" + carpeta + "'...");
        guardarDatosEnArchivos();
    }
    
    /**
     * Muestra el menú principal con las opciones por rol.
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== HEALTH HUB - Sistema de Gestion Clinica ===");
        System.out.println("1. Acceso Recepcionista");
        System.out.println("2. Acceso Medico");
        System.out.println("3. Acceso Administrador");
        System.out.println("0. Salir");
    }

    // =========================================================================
    // MENU RECEPCIONISTA
    // =========================================================================
    
    private static void menuRecepcionista() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENU RECEPCIONISTA ---");
            System.out.println("1. Registrar paciente");
            System.out.println("2. Modificar paciente por DNI");
            System.out.println("3. Buscar paciente por DNI");
            System.out.println("4. Buscar paciente por Nombre y Apellido");
            System.out.println("5. Crear turno");
            System.out.println("6. Cancelar turno");
            System.out.println("7. Reprogramar turno");
            System.out.println("8. Consultar disponibilidad de medico");
            System.out.println("9. Registrar sobreturno");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine();
            
            switch (opcion) {
                case "1":
                    registrarPaciente();
                    break;
                case "2":
                    modificarPaciente();
                    break;
                case "3":
                    buscarPacientePorDni();
                    break;
                case "4":
                    buscarPacientePorNombre();
                    break;
                case "5":
                    crearTurno(false);
                    break;
                case "6":
                    cancelarTurno();
                    break;
                case "7":
                    reprogramarTurno();
                    break;
                case "8":
                    consultarDisponibilidad();
                    break;
                case "9":
                    crearTurno(true);  // sobreturno
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
                    break;
            }
        }
    }

    // =========================================================================
    // MENU MEDICO
    // =========================================================================
    
    private static void menuMedico() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENU MEDICO ---");
            System.out.println("1. Consultar turnos asignados");
            System.out.println("2. Visualizar historial clinico");
            System.out.println("3. Registrar consulta medica");
            System.out.println("4. Actualizar diagnostico (ultima entrada)");
            System.out.println("5. Registrar estudio (ultima entrada)");
            System.out.println("6. Marcar turno como atendido");
            System.out.println("7. Cancelar jornada");
            System.out.println("8. Ver notificaciones");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine();
            
            switch (opcion) {
                case "1":
                    consultarTurnosMedico();
                    break;
                case "2":
                    verHistorial();
                    break;
                case "3":
                    registrarConsulta();
                    break;
                case "4":
                    actualizarDiagnostico();
                    break;
                case "5":
                    registrarEstudio();
                    break;
                case "6":
                    marcarTurnoAtendido();
                    break;
                case "7":
                    cancelarJornada();
                    break;
                case "8":
                    verNotificaciones();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
                    break;
            }
        }
    }

    // =========================================================================
    // MENU ADMINISTRADOR
    // =========================================================================
    
    private static void menuAdministrador() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. Registrar medico");
            System.out.println("2. Agregar disponibilidad de medico");
            System.out.println("3. Reemplazar disponibilidades de medico");
            System.out.println("4. Registrar empleado");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine();
            
            switch (opcion) {
                case "1":
                    registrarMedico();
                    break;
                case "2":
                    agregarDisponibilidad();
                    break;
                case "3":
                    reemplazarDisponibilidadSimple();
                    break;
                case "4":
                    registrarEmpleado();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
                    break;
            }
        }
    }
    
    // =========================================================================
    // FUNCIONES DEL MENU RECEPCIONISTA (MAQUETAS)
    // =========================================================================

    private static void registrarPaciente() {
        System.out.println("\n--- Registrar Paciente ---");
        System.out.print("DNI (sin puntos ni espacios): ");
        String dni = scanner.nextLine().trim();
        
        // Validamos que el DNI no esté vacío antes de seguir
        if (dni.isEmpty()) {
            System.out.println("Error: El DNI no puede estar vacío.");
            return;
        }
        
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Telefono: ");
        String telefono = scanner.nextLine().trim();
        System.out.print("Obra social: ");
        String obraSocial = scanner.nextLine().trim();
        
        // TODO: Acá iría la lógica real de registro
        // Por ahora solo mostramos un mensaje como pide la consigna
        System.out.println("[MAQUETA] Paciente registrado correctamente (funcionalidad en desarrollo)");
    }

    private static void modificarPaciente() {
        System.out.println("\n--- Modificar Paciente ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim();
        
        // TODO: Revisar si hay que validar que exista antes de pedir los nuevos datos
        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Nuevo apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Nuevo telefono: ");
        String telefono = scanner.nextLine().trim();
        System.out.print("Nueva obra social: ");
        String obraSocial = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Datos modificados correctamente (funcionalidad en desarrollo)");
    }

    private static void buscarPacientePorDni() {
        System.out.println("\n--- Buscar Paciente por DNI ---");
        System.out.print("DNI a buscar: ");
        String dni = scanner.nextLine().trim();
        
        // Usamos una variable aux para el resultado
        System.out.println("[MAQUETA] Paciente encontrado: " + dni + " (funcionalidad en desarrollo)");
        // TODO: Acá habría que mostrar los datos reales del paciente
    }

    private static void buscarPacientePorNombre() {
        System.out.println("\n--- Buscar Paciente por Nombre ---");
        System.out.print("Nombre y apellido (o parte): ");
        String busqueda = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Búsqueda realizada para: " + busqueda + " (funcionalidad en desarrollo)");
        // TODO: Implementar búsqueda por nombre (parcial también)
    }

    private static void crearTurno(boolean esSobreturno) {
        System.out.println("\n--- Crear Turno ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim();
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Fecha (YYYY-MM-DD): ");
        String fechaStr = scanner.nextLine().trim();
        System.out.print("Hora (HH:MM): ");
        String horaStr = scanner.nextLine().trim();
        
        // TODO: Validar que la fecha y hora sean correctas
        String tipoTurno = esSobreturno ? "Sobreturno" : "Turno";
        System.out.println("[MAQUETA] " + tipoTurno + " creado para " + dni + " con médico " + matricula + " (funcionalidad en desarrollo)");
    }

    private static void cancelarTurno() {
        System.out.println("\n--- Cancelar Turno ---");
        System.out.print("ID del turno: ");
        String turnoId = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Turno " + turnoId + " cancelado (funcionalidad en desarrollo)");
    }

    private static void reprogramarTurno() {
        System.out.println("\n--- Reprogramar Turno ---");
        System.out.print("ID del turno: ");
        String turnoId = scanner.nextLine().trim();
        System.out.print("Nueva fecha (YYYY-MM-DD): ");
        String nuevaFecha = scanner.nextLine().trim();
        System.out.print("Nueva hora (HH:MM): ");
        String nuevaHora = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Turno " + turnoId + " reprogramado (funcionalidad en desarrollo)");
    }

    private static void consultarDisponibilidad() {
        System.out.println("\n--- Consultar Disponibilidad ---");
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Fecha a consultar (YYYY-MM-DD): ");
        String fecha = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Disponibilidad consultada para médico " + matricula + " (funcionalidad en desarrollo)");
    }
    
    // =========================================================================
    // FUNCIONES DEL MENU MEDICO (MAQUETAS)
    // =========================================================================

    private static void consultarTurnosMedico() {
        System.out.println("\n--- Consultar Turnos del Médico ---");
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Fecha (YYYY-MM-DD): ");
        String fecha = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Turnos consultados para médico " + matricula + " (funcionalidad en desarrollo)");
    }

    private static void verHistorial() {
        System.out.println("\n--- Ver Historial Clínico ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Historial mostrado para paciente " + dni + " (funcionalidad en desarrollo)");
    }

    private static void registrarConsulta() {
        System.out.println("\n--- Registrar Consulta Médica ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim();
        System.out.print("Resumen de la consulta: ");
        String resumen = scanner.nextLine().trim();
        System.out.print("Diagnóstico: ");
        String diagnostico = scanner.nextLine().trim();
        System.out.print("Estudios (si corresponde): ");
        String estudios = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Consulta registrada en historial (funcionalidad en desarrollo)");
    }

    private static void actualizarDiagnostico() {
        System.out.println("\n--- Actualizar Diagnóstico ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim();
        System.out.print("Nuevo diagnóstico: ");
        String diagnostico = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Diagnóstico actualizado (funcionalidad en desarrollo)");
    }

    private static void registrarEstudio() {
        System.out.println("\n--- Registrar Estudio ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim();
        System.out.print("Descripción del estudio y resultado: ");
        String estudio = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Estudio registrado (funcionalidad en desarrollo)");
    }

    private static void marcarTurnoAtendido() {
        System.out.println("\n--- Marcar Turno como Atendido ---");
        System.out.print("ID del turno: ");
        String turnoId = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Turno " + turnoId + " marcado como atendido (funcionalidad en desarrollo)");
    }

    private static void cancelarJornada() {
        System.out.println("\n--- Cancelar Jornada Médica ---");
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Fecha de la jornada (YYYY-MM-DD): ");
        String fechaStr = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Jornada cancelada para médico " + matricula + " (funcionalidad en desarrollo)");
    }

    private static void verNotificaciones() {
        System.out.println("\n--- Ver Notificaciones ---");
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Notificaciones mostradas (funcionalidad en desarrollo)");
    }
    
    // =========================================================================
    // FUNCIONES DEL MENU ADMINISTRADOR (MAQUETAS)
    // =========================================================================

    private static void registrarMedico() {
        System.out.println("\n--- Registrar Médico ---");
        System.out.print("Matrícula profesional: ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Especialidad: ");
        String especialidad = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Médico registrado correctamente (funcionalidad en desarrollo)");
    }

    private static void agregarDisponibilidad() {
        System.out.println("\n--- Agregar Disponibilidad ---");
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Día de la semana (1=Lunes, 7=Domingo): ");
        String diaStr = scanner.nextLine().trim();
        System.out.print("Hora de inicio (HH:MM): ");
        String inicioStr = scanner.nextLine().trim();
        System.out.print("Hora de fin (HH:MM): ");
        String finStr = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Disponibilidad agregada (funcionalidad en desarrollo)");
    }

    private static void reemplazarDisponibilidadSimple() {
        System.out.println("\n--- Reemplazar Disponibilidad ---");
        System.out.print("Matrícula del médico: ");
        String matricula = scanner.nextLine().trim();
        System.out.println("Se reemplazará toda la disponibilidad por una única franja.");
        System.out.print("Día (1=Lunes, 7=Domingo): ");
        String diaStr = scanner.nextLine().trim();
        System.out.print("Hora inicio (HH:MM): ");
        String inicioStr = scanner.nextLine().trim();
        System.out.print("Hora fin (HH:MM): ");
        String finStr = scanner.nextLine().trim();
        
        System.out.println("[MAQUETA] Disponibilidad reemplazada (funcionalidad en desarrollo)");
    }

    private static void registrarEmpleado() {
        System.out.println("\n--- Registrar Empleado ---");
        System.out.print("Legajo: ");
        String legajo = scanner.nextLine().trim();
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Rol (RECEPCIONISTA, MEDICO, ADMINISTRADOR): ");
        String rolStr = scanner.nextLine().trim().toUpperCase();
        
        System.out.println("[MAQUETA] Empleado registrado con rol " + rolStr + " (funcionalidad en desarrollo)");
    }
    
    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================
    
    /**
     * Muestra un mensaje si el sistema está fuera de horario laboral.
     * Lo pusimos porque en la clínica dijeron que solo atienden de 8 a 20hs.
     */
    private static void informarHorarioLaboral() {
        LocalTime now = LocalTime.now();
        boolean horarioLaboral = !now.isBefore(LocalTime.of(8, 0)) && !now.isAfter(LocalTime.of(20, 0));
        if (!horarioLaboral) {
            System.out.println("[INFO] Fuera de horario laboral (08:00-20:00). Sistema en modo base de prueba.");
        }
    }
}
