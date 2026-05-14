package com.healthhub.app;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
import com.healthhub.domain.EntradaHistorial;
import com.healthhub.domain.EstadoTurno;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;
import com.healthhub.service.GestorEmpleados;
import com.healthhub.service.GestorHistoriales;
import com.healthhub.service.GestorMedicos;
import com.healthhub.service.GestorNotificaciones;
import com.healthhub.service.GestorPacientes;
import com.healthhub.service.GestorTurnos;
import com.healthhub.service.Persistencia;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * App - Clase principal del sistema Health Hub.
 * Implementa login por legajo, menues por rol y funcionalidades de gestion.
 */
public class App {

    private static Scanner scanner;

    private static GestorPacientes gestorPacientes;
    private static GestorMedicos gestorMedicos;
    private static GestorTurnos gestorTurnos;
    private static GestorHistoriales gestorHistoriales;
    private static GestorEmpleados gestorEmpleados;
    private static GestorNotificaciones gestorNotifs;
    private static Persistencia persistencia;

    private static final Path RUTA_DATOS = Path.of("data");
    private static final String CARPETA_BACKUP = "data";
    private static final LocalTime HORA_INICIO_LABORAL = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIN_LABORAL = LocalTime.of(20, 0);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        inicializarSistema();

        boolean continuar = true;
        while (continuar) {
            Optional<Empleado> empleadoOpt = login();
            if (empleadoOpt.isEmpty()) {
                continuar = false;
                break;
            }

            Empleado empleado = empleadoOpt.get();
            System.out.println("\nBienvenido/a " + empleado.getNombre() + " (" + empleado.getRol() + ").");

            switch (empleado.getRol()) {
                case RECEPCIONISTA:
                    menuRecepcionista();
                    break;
                case MEDICO:
                    menuMedico();
                    break;
                case ADMINISTRADOR:
                    menuAdministrador();
                    break;
                default:
                    System.out.println("Rol no reconocido.");
                    break;
            }
        }

        guardarBackup(CARPETA_BACKUP);
        System.out.println("Saliendo del sistema...");
        scanner.close();
    }

    private static void inicializarSistema() {
        scanner = new Scanner(System.in);
        RUTA_DATOS.toFile().mkdirs();

        gestorHistoriales = new GestorHistoriales();
        gestorPacientes = new GestorPacientes(gestorHistoriales);
        gestorMedicos = new GestorMedicos();
        gestorNotifs = new GestorNotificaciones();
        gestorTurnos = new GestorTurnos(gestorMedicos, gestorNotifs);
        gestorEmpleados = new GestorEmpleados();

        persistencia = new Persistencia(RUTA_DATOS);
        cargarBackup(CARPETA_BACKUP);

        System.out.println("[INFO] Sistema inicializado correctamente.");
        informarHorarioLaboral();
    }

    private static Optional<Empleado> login() {
        while (true) {
            System.out.println("\n=== HEALTH HUB - LOGIN ===");
            System.out.print("Ingrese su legajo (o 0 para salir): ");
            String legajo = scanner.nextLine().trim();

            if ("0".equals(legajo)) {
                return Optional.empty();
            }

            if (legajo.isEmpty()) {
                System.out.println("Error: el legajo no puede estar vacio.");
                continue;
            }

            Optional<Empleado> empleadoOpt = gestorEmpleados.buscarPorLegajo(legajo);
            if (empleadoOpt.isPresent()) {
                return empleadoOpt;
            }

            System.out.println("Error: no existe un empleado con legajo '" + legajo + "'.");
            System.out.print("1. Reintentar | 0. Salir: ");
            String opcion = scanner.nextLine().trim();
            if ("0".equals(opcion)) {
                return Optional.empty();
            }
        }
    }

    private static void cargarDatosDeArchivos() {
        List<Medico> medicosCargados = persistencia.cargarMedicos();
        Map<String, List<Disponibilidad>> disponibilidadesCargadas = persistencia.cargarDisponibilidades();

        for (Medico medico : medicosCargados) {
            gestorMedicos.registrarMedico(medico);
        }
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidadesCargadas.entrySet()) {
            for (Disponibilidad disp : entry.getValue()) {
                gestorMedicos.agregarDisponibilidad(entry.getKey(), disp);
            }
        }

        List<Paciente> pacientesCargados = persistencia.cargarPacientes();
        for (Paciente paciente : pacientesCargados) {
            gestorPacientes.registrarPaciente(paciente);
        }

        List<HistorialClinico> historialesCargados = persistencia.cargarHistoriales();
        gestorHistoriales.cargarHistoriales(historialesCargados);

        List<Turno> turnosCargados = persistencia.cargarTurnos();
        gestorTurnos.cargarTurnos(turnosCargados);

        Map<String, List<String>> notificacionesCargadas = persistencia.cargarNotificaciones();
        gestorNotifs.cargarNotificaciones(notificacionesCargadas);

        List<Empleado> empleadosCargados = persistencia.cargarEmpleados();
        for (Empleado empleado : empleadosCargados) {
            gestorEmpleados.registrarEmpleado(empleado.getLegajo(), empleado.getNombre(), empleado.getRol());
        }

        System.out.println("[INFO] Datos cargados: " + pacientesCargados.size() + " pacientes, "
            + medicosCargados.size() + " medicos, " + turnosCargados.size() + " turnos.");
    }

    private static void guardarDatosEnArchivos() {
        persistencia.guardarPacientes(gestorPacientes.listarTodos());
        persistencia.guardarMedicos(gestorMedicos.listarTodos());
        persistencia.guardarEmpleados(gestorEmpleados.listarTodos());
        persistencia.guardarTurnos(gestorTurnos.listarTodos());
        persistencia.guardarHistoriales(gestorHistoriales.listarTodos());
        persistencia.guardarDisponibilidades(gestorMedicos.obtenerDisponibilidadesPorMedico());
        persistencia.guardarNotificaciones(gestorNotifs.listarTodas());
    }

    private static void cargarBackup(String carpeta) {
        System.out.println("[INFO] Cargando backup desde '" + carpeta + "'...");
        cargarDatosDeArchivos();
    }

    private static void guardarBackup(String carpeta) {
        System.out.println("[INFO] Guardando backup en '" + carpeta + "'...");
        guardarDatosEnArchivos();
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
            System.out.println("0. Cerrar sesion");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine().trim();
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
                    crearTurno(true);
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
            System.out.println("0. Cerrar sesion");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine().trim();
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
            System.out.println("5. Ver agenda consolidada");
            System.out.println("6. Ver estadisticas del dia");
            System.out.println("0. Cerrar sesion");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine().trim();
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
                case "5":
                    verAgendaConsolidada();
                    break;
                case "6":
                    verEstadisticasDelDia();
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
    // FUNCIONES DEL MENU RECEPCIONISTA
    // =========================================================================

    private static void registrarPaciente() {
        System.out.println("\n--- Registrar Paciente ---");
        String dni = solicitarNumeroNoVacio("DNI (sin puntos ni espacios): ", "DNI");

        if (gestorPacientes.buscarPorDni(dni).isPresent()) {
            System.out.println("Error: ya existe un paciente con ese DNI.");
            return;
        }

        String nombre = solicitarTextoObligatorio("Nombre: ");
        String apellido = solicitarTextoObligatorio("Apellido: ");
        String telefono = solicitarTextoObligatorio("Telefono: ");
        String obraSocial = solicitarTextoObligatorio("Obra social: ");

        Paciente paciente = new Paciente(dni, nombre, apellido, telefono, obraSocial);
        mostrarResumenPaciente(paciente);
        if (!confirmarDatos()) {
            System.out.println("Operacion cancelada. No se guardaron cambios.");
            return;
        }

        boolean ok = gestorPacientes.registrarPaciente(paciente);
        if (!ok) {
            System.out.println("Error: no se pudo registrar el paciente.");
            return;
        }

        persistencia.guardarPacientes(gestorPacientes.listarTodos());
        persistencia.guardarHistoriales(gestorHistoriales.listarTodos());
        System.out.println("Paciente registrado correctamente.");
    }

    private static void modificarPaciente() {
        System.out.println("\n--- Modificar Paciente ---");
        String dni = solicitarNumeroNoVacio("DNI del paciente: ", "DNI");

        Optional<Paciente> pacienteOpt = gestorPacientes.buscarPorDni(dni);
        if (pacienteOpt.isEmpty()) {
            System.out.println("Error: no existe un paciente con ese DNI.");
            return;
        }

        String nombre = solicitarTextoObligatorio("Nuevo nombre: ");
        String apellido = solicitarTextoObligatorio("Nuevo apellido: ");
        String telefono = solicitarTextoObligatorio("Nuevo telefono: ");
        String obraSocial = solicitarTextoObligatorio("Nueva obra social: ");

        boolean ok = gestorPacientes.modificarPaciente(dni, nombre, apellido, telefono, obraSocial);
        if (!ok) {
            System.out.println("Error: no se pudo modificar el paciente.");
            return;
        }

        Paciente actualizado = gestorPacientes.buscarPorDni(dni).orElse(pacienteOpt.get());
        System.out.println("Paciente modificado correctamente.");
        mostrarResumenPaciente(actualizado);
    }

    private static void buscarPacientePorDni() {
        System.out.println("\n--- Buscar Paciente por DNI ---");
        String dni = solicitarNumeroNoVacio("DNI a buscar: ", "DNI");
        Optional<Paciente> pacienteOpt = gestorPacientes.buscarPorDni(dni);

        if (pacienteOpt.isEmpty()) {
            System.out.println("No se encontro paciente con ese DNI.");
            return;
        }
        mostrarResumenPaciente(pacienteOpt.get());
    }

    private static void buscarPacientePorNombre() {
        System.out.println("\n--- Buscar Paciente por Nombre ---");
        String busqueda = solicitarTextoObligatorio("Nombre y apellido (o parte): ");
        List<Paciente> resultados = gestorPacientes.buscarPorNombreCompleto(busqueda);

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron pacientes para '" + busqueda + "'.");
            return;
        }

        System.out.println("Resultados:");
        for (Paciente p : resultados) {
            System.out.println("- " + p.getDni() + " | " + p.nombreCompleto() + " | Obra social: " + p.getObraSocial());
        }
    }

    private static void crearTurno(boolean esSobreturno) {
        String tipoTurno = esSobreturno ? "Sobreturno" : "Turno";
        System.out.println("\n--- Crear " + tipoTurno + " ---");

        String dni = solicitarNumeroNoVacio("DNI del paciente: ", "DNI");
        if (gestorPacientes.buscarPorDni(dni).isEmpty()) {
            System.out.println("Error: el paciente no existe.");
            return;
        }

        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        if (gestorMedicos.buscarMedico(matricula).isEmpty()) {
            System.out.println("Error: el medico no existe.");
            return;
        }

        LocalDateTime fechaHora = solicitarFechaHoraTurnoValida(esSobreturno ? null : matricula);
        if (fechaHora == null) {
            return;
        }
        if (fechaHora.isBefore(LocalDateTime.now())) {
            System.out.println("Error: no se puede crear un turno en una fecha/hora anterior a la actual.");
            return;
        }

        Optional<Turno> turnoOpt = gestorTurnos.crearTurno(dni, matricula, fechaHora, esSobreturno);
        if (turnoOpt.isEmpty()) {
            System.out.println("Error: no se pudo crear el " + tipoTurno.toLowerCase()
                + ". Verifique disponibilidad o fecha/hora.");
            return;
        }

        Turno turno = turnoOpt.get();
        System.out.println(tipoTurno + " creado correctamente.");
        System.out.println("Resumen guardado:");
        System.out.println("- ID: " + turno.getId());
        System.out.println("- Paciente DNI: " + turno.getDniPaciente());
        System.out.println("- Medico matricula: " + turno.getMatriculaMedico());
        System.out.println("- Fecha/hora: " + turno.getFechaHora().format(FORMATO_FECHA_HORA));
        System.out.println("- Estado: " + turno.getEstado());
    }

    private static void cancelarTurno() {
        System.out.println("\n--- Cancelar Turno ---");
        String turnoId = solicitarTextoObligatorio("ID del turno: ");
        boolean ok = gestorTurnos.cancelarTurno(turnoId);
        if (!ok) {
            System.out.println("Error: no se pudo cancelar. Verifique ID y estado del turno.");
            return;
        }
        System.out.println("Turno cancelado correctamente.");
    }

    private static void reprogramarTurno() {
        System.out.println("\n--- Reprogramar Turno ---");
        String turnoId = solicitarTextoObligatorio("ID del turno: ");
        Optional<Turno> turnoOpt = gestorTurnos.buscarTurno(turnoId);
        if (turnoOpt.isEmpty()) {
            System.out.println("Error: no existe un turno con ese ID.");
            return;
        }

        Turno turnoActual = turnoOpt.get();
        String matriculaValidacion = turnoActual.isSobreturno() ? null : turnoActual.getMatriculaMedico();
        LocalDateTime nuevaFechaHora = solicitarFechaHoraTurnoValida(matriculaValidacion);
        if (nuevaFechaHora == null) {
            return;
        }

        boolean ok = gestorTurnos.reprogramarTurno(turnoId, nuevaFechaHora);
        if (!ok) {
            System.out.println("Error: no se pudo reprogramar. Verifique disponibilidad.");
            return;
        }

        System.out.println("Turno reprogramado correctamente.");
        System.out.println("Nueva fecha/hora: " + nuevaFechaHora.format(FORMATO_FECHA_HORA));
    }

    private static void consultarDisponibilidad() {
        System.out.println("\n--- Consultar Disponibilidad ---");
        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        if (gestorMedicos.buscarMedico(matricula).isEmpty()) {
            System.out.println("Error: el medico no existe.");
            return;
        }

        LocalDate fecha = solicitarFecha("Fecha a consultar (YYYY-MM-DD): ");
        DayOfWeek dia = fecha.getDayOfWeek();
        List<Disponibilidad> disponibilidades = gestorMedicos.consultarDisponibilidad(matricula);

        List<Disponibilidad> delDia = new ArrayList<>();
        for (Disponibilidad d : disponibilidades) {
            if (d.getDia().equals(dia)) {
                delDia.add(d);
            }
        }

        if (delDia.isEmpty()) {
            System.out.println("No hay disponibilidad cargada para ese dia.");
            return;
        }

        System.out.println("Disponibilidad del medico " + matricula + " para " + fecha.format(FORMATO_FECHA) + ":");
        for (Disponibilidad d : delDia) {
            System.out.println("- " + d.getHoraInicio().format(FORMATO_HORA) + " a " + d.getHoraFin().format(FORMATO_HORA));
        }
    }

    // =========================================================================
    // FUNCIONES DEL MENU MEDICO
    // =========================================================================

    private static void consultarTurnosMedico() {
        System.out.println("\n--- Consultar Turnos del Medico ---");
        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        LocalDate fecha = solicitarFecha("Fecha (YYYY-MM-DD): ");

        List<Turno> turnos = gestorTurnos.listarTurnosPorMedicoYFecha(matricula, fecha);
        if (turnos.isEmpty()) {
            System.out.println("No hay turnos para esa fecha.");
            return;
        }

        System.out.println("Turnos asignados:");
        for (Turno turno : turnos) {
            System.out.println("- ID: " + turno.getId()
                + " | Paciente: " + turno.getDniPaciente()
                + " | Hora: " + turno.getFechaHora().toLocalTime().format(FORMATO_HORA)
                + " | Estado: " + turno.getEstado()
                + " | Sobreturno: " + (turno.isSobreturno() ? "SI" : "NO"));
        }
    }

    private static void verHistorial() {
        System.out.println("\n--- Ver Historial Clinico ---");
        String dni = solicitarNumeroNoVacio("DNI del paciente: ", "DNI");
        HistorialClinico historial = gestorHistoriales.verHistorial(dni);
        if (historial == null) {
            System.out.println("No existe historial para ese paciente.");
            return;
        }

        List<EntradaHistorial> entradas = historial.getEntradas();
        if (entradas.isEmpty()) {
            System.out.println("El historial no tiene entradas.");
            return;
        }

        System.out.println("Historial de paciente " + dni + ":");
        for (EntradaHistorial e : entradas) {
            System.out.println("- " + e.getFecha().format(FORMATO_FECHA_HORA) + " | " + e.getResumen()
                + " | Dx: " + e.getDiagnostico() + " | Estudios: " + e.getEstudios());
        }
    }

    private static void registrarConsulta() {
        System.out.println("\n--- Registrar Consulta Medica ---");
        String dni = solicitarNumeroNoVacio("DNI del paciente: ", "DNI");
        if (gestorPacientes.buscarPorDni(dni).isEmpty()) {
            System.out.println("Error: el paciente no existe.");
            return;
        }

        String resumen = solicitarTextoObligatorio("Resumen de la consulta: ");
        String diagnostico = solicitarTextoObligatorio("Diagnostico: ");
        System.out.print("Estudios (si corresponde): ");
        String estudios = scanner.nextLine().trim();

        boolean ok = gestorHistoriales.registrarConsulta(RolUsuario.MEDICO, dni, resumen, diagnostico, estudios);
        if (!ok) {
            System.out.println("Error: no se pudo registrar la consulta.");
            return;
        }

        System.out.println("Consulta registrada correctamente para paciente DNI " + dni + ".");
    }

    private static void actualizarDiagnostico() {
        System.out.println("\n--- Actualizar Diagnostico ---");
        String dni = solicitarNumeroNoVacio("DNI del paciente: ", "DNI");
        String diagnostico = solicitarTextoObligatorio("Nuevo diagnostico: ");
        boolean ok = gestorHistoriales.actualizarDiagnostico(dni, diagnostico);
        if (!ok) {
            System.out.println("Error: no hay historial o no hay entradas para actualizar.");
            return;
        }
        System.out.println("Diagnostico actualizado correctamente.");
    }

    private static void registrarEstudio() {
        System.out.println("\n--- Registrar Estudio ---");
        String dni = solicitarNumeroNoVacio("DNI del paciente: ", "DNI");
        String estudio = solicitarTextoObligatorio("Descripcion del estudio y resultado: ");
        boolean ok = gestorHistoriales.actualizarEstudios(dni, estudio);
        if (!ok) {
            System.out.println("Error: no hay historial o no hay entradas para actualizar.");
            return;
        }
        System.out.println("Estudio actualizado correctamente.");
    }

    private static void marcarTurnoAtendido() {
        System.out.println("\n--- Marcar Turno como Atendido ---");
        String turnoId = solicitarTextoObligatorio("ID del turno: ");
        boolean ok = gestorTurnos.marcarAtendido(turnoId);
        if (!ok) {
            System.out.println("Error: no se pudo marcar el turno. Verifique el ID.");
            return;
        }
        System.out.println("Turno marcado como atendido.");
    }

    private static void cancelarJornada() {
        System.out.println("\n--- Cancelar Jornada Medica ---");
        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        LocalDate fecha = solicitarFecha("Fecha de la jornada (YYYY-MM-DD): ");
        int cancelados = gestorTurnos.cancelarTurnosDeJornada(matricula, fecha);
        System.out.println("Jornada procesada. Turnos cancelados: " + cancelados);
    }

    private static void verNotificaciones() {
        System.out.println("\n--- Ver Notificaciones ---");
        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        List<String> notificaciones = gestorNotifs.verNotificaciones(matricula);
        if (notificaciones.isEmpty()) {
            System.out.println("No hay notificaciones.");
            return;
        }
        System.out.println("Notificaciones:");
        for (String mensaje : notificaciones) {
            System.out.println("- " + mensaje);
        }
        gestorNotifs.limpiarNotificaciones(matricula);
        System.out.println("Notificaciones marcadas como leidas.");
    }

    // =========================================================================
    // FUNCIONES DEL MENU ADMINISTRADOR
    // =========================================================================

    private static void registrarMedico() {
        System.out.println("\n--- Registrar Medico ---");
        String matricula = solicitarNumeroNoVacio("Matricula profesional: ", "Matricula");
        if (gestorMedicos.buscarMedico(matricula).isPresent()) {
            System.out.println("Error: ya existe un medico con esa matricula.");
            return;
        }

        String nombre = solicitarTextoObligatorio("Nombre: ");
        String apellido = solicitarTextoObligatorio("Apellido: ");
        String especialidad = solicitarTextoObligatorio("Especialidad: ");

        Medico medico = new Medico(matricula, nombre, apellido, especialidad);
        mostrarResumenMedico(medico);
        if (!confirmarDatos()) {
            System.out.println("Operacion cancelada. No se guardaron cambios.");
            return;
        }

        boolean ok = gestorMedicos.registrarMedico(medico);
        if (!ok) {
            System.out.println("Error: no se pudo registrar el medico.");
            return;
        }

        persistencia.guardarMedicos(gestorMedicos.listarTodos());
        System.out.println("Medico registrado correctamente.");
    }

    private static void agregarDisponibilidad() {
        System.out.println("\n--- Agregar Disponibilidad ---");
        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        int diaNum = solicitarDiaSemana("Dia de la semana (1=Lunes, 7=Domingo): ");
        LocalTime inicio = solicitarHora("Hora de inicio (HH:MM): ");
        LocalTime fin = solicitarHora("Hora de fin (HH:MM): ");

        if (!inicio.isBefore(fin)) {
            System.out.println("Error: la hora de inicio debe ser anterior a la de fin.");
            return;
        }

        Disponibilidad disponibilidad = new Disponibilidad(DayOfWeek.of(diaNum), inicio, fin);
        boolean ok = gestorMedicos.agregarDisponibilidad(matricula, disponibilidad);
        if (!ok) {
            System.out.println("Error: no se pudo agregar disponibilidad (medico inexistente o solapamiento).");
            return;
        }
        System.out.println("Disponibilidad agregada correctamente.");
    }

    private static void reemplazarDisponibilidadSimple() {
        System.out.println("\n--- Reemplazar Disponibilidad ---");
        String matricula = solicitarNumeroNoVacio("Matricula del medico: ", "Matricula");
        int diaNum = solicitarDiaSemana("Dia (1=Lunes, 7=Domingo): ");
        LocalTime inicio = solicitarHora("Hora inicio (HH:MM): ");
        LocalTime fin = solicitarHora("Hora fin (HH:MM): ");

        if (!inicio.isBefore(fin)) {
            System.out.println("Error: la hora de inicio debe ser anterior a la de fin.");
            return;
        }

        List<Disponibilidad> nuevas = new ArrayList<>();
        nuevas.add(new Disponibilidad(DayOfWeek.of(diaNum), inicio, fin));
        boolean ok = gestorMedicos.reemplazarDisponibilidades(matricula, nuevas);
        if (!ok) {
            System.out.println("Error: no se pudo reemplazar disponibilidad (medico inexistente).");
            return;
        }
        System.out.println("Disponibilidad reemplazada correctamente.");
    }

    private static void registrarEmpleado() {
        System.out.println("\n--- Registrar Empleado ---");
        String legajo = solicitarTextoObligatorio("Legajo: ");
        if (gestorEmpleados.buscarPorLegajo(legajo).isPresent()) {
            System.out.println("Error: ya existe un empleado con ese legajo.");
            return;
        }

        String nombre = solicitarTextoObligatorio("Nombre completo: ");
        RolUsuario rol = solicitarRol("Rol (RECEPCIONISTA, MEDICO, ADMINISTRADOR): ");
        boolean ok = gestorEmpleados.registrarEmpleado(legajo, nombre, rol);
        if (!ok) {
            System.out.println("Error: no se pudo registrar empleado.");
            return;
        }
        System.out.println("Empleado registrado correctamente.");
        System.out.println("Resumen guardado:");
        System.out.println("- Legajo: " + legajo);
        System.out.println("- Nombre: " + nombre);
        System.out.println("- Rol: " + rol);
    }

    private static void verAgendaConsolidada() {
        System.out.println("\n--- Agenda Consolidada ---");
        List<Persistencia.AgendaConsolidadaFila> filas = persistencia.consultarAgendaConsolidadaInnerJoin(
            gestorTurnos.listarTodos(),
            gestorPacientes.listarTodos(),
            gestorMedicos.listarTodos()
        );
        if (filas.isEmpty()) {
            System.out.println("No hay turnos registrados.");
            return;
        }

        System.out.println("Consulta SQL (simulada):");
        System.out.println(persistencia.obtenerConsultaAgendaConsolidada());
        System.out.println("Fecha/Hora | Paciente | Medico | Estado | Sobreturno");
        for (Persistencia.AgendaConsolidadaFila fila : filas) {
            System.out.println(fila.getFechaHora().format(FORMATO_FECHA_HORA)
                + " | " + fila.getNombrePaciente()
                + " | " + fila.getNombreMedico()
                + " | " + fila.getEstado()
                + " | " + (fila.isSobreturno() ? "SI" : "NO"));
        }
    }

    private static void verEstadisticasDelDia() {
        System.out.println("\n--- Estadisticas del Dia ---");
        LocalDate hoy = LocalDate.now();

        long total = gestorTurnos.contarTurnosPorFecha(hoy);
        long cancelados = gestorTurnos.contarTurnosPorEstadoEnFecha(hoy, EstadoTurno.CANCELADO);
        long atendidos = gestorTurnos.contarTurnosPorEstadoEnFecha(hoy, EstadoTurno.ATENDIDO);

        double porcentajeCancelados = total == 0 ? 0.0 : (cancelados * 100.0) / total;
        double porcentajeAtendidos = total == 0 ? 0.0 : (atendidos * 100.0) / total;

        System.out.println("Total de turnos de hoy: " + total);
        System.out.println(String.format("Porcentaje cancelados: %.2f%%", porcentajeCancelados));
        System.out.println(String.format("Porcentaje atendidos: %.2f%%", porcentajeAtendidos));

        Optional<String> matriculaOpt = gestorTurnos.obtenerMatriculaConMasSobreturnos();
        if (matriculaOpt.isEmpty()) {
            System.out.println("Medico con mas sobreturnos: sin registros.");
            return;
        }

        String matricula = matriculaOpt.get();
        long cantidadSobreturnos = gestorTurnos.contarSobreturnosPorMedico(matricula);
        String nombreMedico = gestorMedicos.buscarMedico(matricula)
            .map(m -> m.getNombre() + " " + m.getApellido())
            .orElse("Matricula " + matricula);
        System.out.println("Medico con mas sobreturnos: " + nombreMedico + " (" + cantidadSobreturnos + ")");
    }

    // =========================================================================
    // METODOS AUXILIARES
    // =========================================================================

    private static String solicitarTextoObligatorio(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("Error: este campo no puede estar vacio.");
        }
    }

    private static String solicitarNumeroNoVacio(String etiqueta, String nombreCampo) {
        while (true) {
            String valor = solicitarTextoObligatorio(etiqueta);
            if (valor.matches("\\d+")) {
                return valor;
            }
            System.out.println("Error: " + nombreCampo + " debe ser numerico.");
        }
    }

    private static LocalDate solicitarFecha(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String fechaStr = scanner.nextLine().trim();
            try {
                return LocalDate.parse(fechaStr, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("Error: formato invalido. Use YYYY-MM-DD.");
            }
        }
    }

    private static LocalTime solicitarHora(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String horaStr = scanner.nextLine().trim();
            try {
                return LocalTime.parse(horaStr, FORMATO_HORA);
            } catch (DateTimeParseException e) {
                System.out.println("Error: formato invalido. Use HH:MM.");
            }
        }
    }

    private static LocalDateTime solicitarFechaHoraTurnoValida(String matriculaParaValidarDisponibilidad) {
        while (true) {
            LocalDate fecha = solicitarFecha("Fecha (YYYY-MM-DD): ");
            LocalTime hora = solicitarHora("Hora (HH:MM): ");
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            if (fechaHora.isBefore(LocalDateTime.now())) {
                System.out.println("Error: la fecha/hora no puede ser anterior a la actual.");
                continue;
            }

            if (!horaEnHorarioLaboral(hora)) {
                System.out.println("Error: la hora debe estar dentro del horario laboral (08:00 a 20:00).");
                continue;
            }

            if (matriculaParaValidarDisponibilidad != null
                && !gestorMedicos.estaDisponible(matriculaParaValidarDisponibilidad, fechaHora)) {
                System.out.println("Error: el medico no tiene disponibilidad en ese horario.");
                continue;
            }

            return fechaHora;
        }
    }

    private static int solicitarDiaSemana(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String diaStr = scanner.nextLine().trim();
            try {
                int dia = Integer.parseInt(diaStr);
                if (dia >= 1 && dia <= 7) {
                    return dia;
                }
                System.out.println("Error: el dia debe estar entre 1 y 7.");
            } catch (NumberFormatException e) {
                System.out.println("Error: dia invalido. Debe ser numerico.");
            }
        }
    }

    private static RolUsuario solicitarRol(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String rolStr = scanner.nextLine().trim().toUpperCase();
            try {
                return RolUsuario.valueOf(rolStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: rol invalido. Opciones: RECEPCIONISTA, MEDICO, ADMINISTRADOR.");
            }
        }
    }

    private static boolean horaEnHorarioLaboral(LocalTime hora) {
        return !hora.isBefore(HORA_INICIO_LABORAL) && !hora.isAfter(HORA_FIN_LABORAL);
    }

    private static void mostrarResumenPaciente(Paciente paciente) {
        System.out.println("Resumen de confirmacion:");
        System.out.println("- DNI: " + paciente.getDni());
        System.out.println("- Nombre completo: " + paciente.nombreCompleto());
        System.out.println("- Telefono: " + paciente.getTelefono());
        System.out.println("- Obra social: " + paciente.getObraSocial());
    }

    private static void mostrarResumenMedico(Medico medico) {
        System.out.println("Resumen de confirmacion:");
        System.out.println("- Matricula: " + medico.getMatricula());
        System.out.println("- Nombre: " + medico.getNombre() + " " + medico.getApellido());
        System.out.println("- Especialidad: " + medico.getEspecialidad());
    }

    private static boolean confirmarDatos() {
        while (true) {
            System.out.print("Son estos datos correctos? (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();
            if ("S".equals(respuesta)) {
                return true;
            }
            if ("N".equals(respuesta)) {
                return false;
            }
            System.out.println("Respuesta invalida. Ingrese S o N.");
        }
    }

    private static void informarHorarioLaboral() {
        LocalTime now = LocalTime.now();
        boolean horarioLaboral = !now.isBefore(HORA_INICIO_LABORAL) && !now.isAfter(HORA_FIN_LABORAL);
        if (!horarioLaboral) {
            System.out.println("[INFO] Fuera de horario laboral (08:00-20:00).");
        }
    }
}
