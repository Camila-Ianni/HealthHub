package com.healthhub.app;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.RolUsuario;
import com.healthhub.domain.Turno;
import com.healthhub.service.HealthHubFacade;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final HealthHubFacade SYSTEM = new HealthHubFacade();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            informarHorarioLaboral();
            System.out.println("\n=== HEALTH HUB - MENU PRINCIPAL ===");
            System.out.println("1. Menu Recepcionista");
            System.out.println("2. Menu Medico");
            System.out.println("3. Menu Administrador");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = SCANNER.nextLine();
            switch (opcion) {
                case "1" -> menuRecepcionista();
                case "2" -> menuMedico();
                case "3" -> menuAdministrador();
                case "0" -> continuar = false;
                default -> System.out.println("Opcion invalida.");
            }
        }

        System.out.println("Saliendo del sistema Health Hub...");
    }

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

            switch (SCANNER.nextLine()) {
                case "1" -> registrarPaciente();
                case "2" -> modificarPaciente();
                case "3" -> buscarPacientePorDni();
                case "4" -> buscarPacientePorNombre();
                case "5" -> crearTurno(false);
                case "6" -> cancelarTurno();
                case "7" -> reprogramarTurno();
                case "8" -> consultarDisponibilidad();
                case "9" -> crearTurno(true);
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

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

            switch (SCANNER.nextLine()) {
                case "1" -> consultarTurnosMedico();
                case "2" -> verHistorial();
                case "3" -> registrarConsulta();
                case "4" -> actualizarDiagnostico();
                case "5" -> registrarEstudio();
                case "6" -> marcarTurnoAtendido();
                case "7" -> cancelarJornada();
                case "8" -> verNotificaciones();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

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

            switch (SCANNER.nextLine()) {
                case "1" -> registrarMedico();
                case "2" -> agregarDisponibilidad();
                case "3" -> reemplazarDisponibilidadSimple();
                case "4" -> registrarEmpleado();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void registrarPaciente() {
        System.out.print("DNI: ");
        String dni = SCANNER.nextLine();
        System.out.print("Nombre: ");
        String nombre = SCANNER.nextLine();
        System.out.print("Apellido: ");
        String apellido = SCANNER.nextLine();
        System.out.print("Telefono: ");
        String telefono = SCANNER.nextLine();
        System.out.print("Obra social: ");
        String obraSocial = SCANNER.nextLine();

        boolean ok = SYSTEM.registrarPaciente(dni, nombre, apellido, telefono, obraSocial);
        System.out.println(ok ? "Paciente registrado y historial creado." : "No se pudo registrar (DNI ya existente).");
    }

    private static void modificarPaciente() {
        System.out.print("DNI paciente: ");
        String dni = SCANNER.nextLine();
        System.out.print("Nuevo nombre: ");
        String nombre = SCANNER.nextLine();
        System.out.print("Nuevo apellido: ");
        String apellido = SCANNER.nextLine();
        System.out.print("Nuevo telefono: ");
        String telefono = SCANNER.nextLine();
        System.out.print("Nueva obra social: ");
        String obraSocial = SCANNER.nextLine();

        boolean ok = SYSTEM.modificarPaciente(dni, nombre, apellido, telefono, obraSocial);
        System.out.println(ok ? "Paciente modificado." : "No existe paciente con ese DNI.");
    }

    private static void buscarPacientePorDni() {
        System.out.print("DNI: ");
        String dni = SCANNER.nextLine();
        System.out.println(SYSTEM.buscarPacientePorDni(dni).map(Paciente::toString).orElse("No encontrado."));
    }

    private static void buscarPacientePorNombre() {
        System.out.print("Nombre y apellido: ");
        String filtro = SCANNER.nextLine();
        List<Paciente> pacientes = SYSTEM.buscarPacientePorNombre(filtro);
        if (pacientes.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        pacientes.forEach(System.out::println);
    }

    private static void crearTurno(boolean sobreturno) {
        try {
            System.out.print("DNI paciente: ");
            String dni = SCANNER.nextLine();
            System.out.print("Matricula medico: ");
            String matricula = SCANNER.nextLine();
            System.out.print("Fecha y hora (YYYY-MM-DDTHH:MM): ");
            LocalDateTime fechaHora = LocalDateTime.parse(SCANNER.nextLine());

            var turno = SYSTEM.crearTurno(dni, matricula, fechaHora, sobreturno);
            if (turno.isPresent()) {
                System.out.println("Turno creado: " + turno.get().getId());
            } else {
                System.out.println("No se pudo crear (datos invalidos, fuera de agenda, duplicado o sobreturno no permitido).");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha/hora invalido.");
        }
    }

    private static void cancelarTurno() {
        System.out.print("ID turno: ");
        String turnoId = SCANNER.nextLine();
        boolean ok = SYSTEM.cancelarTurno(turnoId);
        System.out.println(ok ? "Turno cancelado." : "Turno inexistente.");
    }

    private static void reprogramarTurno() {
        try {
            System.out.print("ID turno: ");
            String turnoId = SCANNER.nextLine();
            System.out.print("Nueva fecha y hora (YYYY-MM-DDTHH:MM): ");
            LocalDateTime nuevaFechaHora = LocalDateTime.parse(SCANNER.nextLine());
            boolean ok = SYSTEM.reprogramarTurno(turnoId, nuevaFechaHora);
            System.out.println(ok ? "Turno reprogramado." : "No se pudo reprogramar (ID invalido o duplicado).");
        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha/hora invalido.");
        }
    }

    private static void consultarDisponibilidad() {
        System.out.print("Matricula medico: ");
        String matricula = SCANNER.nextLine();
        List<Disponibilidad> lista = SYSTEM.consultarDisponibilidad(matricula);
        if (lista.isEmpty()) {
            System.out.println("Sin disponibilidad registrada.");
            return;
        }
        lista.forEach(System.out::println);
    }

    private static void consultarTurnosMedico() {
        System.out.print("Matricula medico: ");
        String matricula = SCANNER.nextLine();
        List<Turno> turnos = SYSTEM.verTurnosMedico(matricula);
        if (turnos.isEmpty()) {
            System.out.println("Sin turnos asignados.");
            return;
        }
        turnos.forEach(System.out::println);
    }

    private static void verHistorial() {
        System.out.print("DNI paciente: ");
        String dni = SCANNER.nextLine();
        var historialOpt = SYSTEM.verHistorial(dni);
        if (historialOpt.isEmpty()) {
            System.out.println("Sin historial.");
            return;
        }
        HistorialClinico historial = historialOpt.get();
        System.out.println("Historial de " + historial.getDniPaciente());
        historial.getEntradas().forEach(System.out::println);
    }

    private static void registrarConsulta() {
        System.out.print("DNI paciente: ");
        String dni = SCANNER.nextLine();
        System.out.print("Resumen: ");
        String resumen = SCANNER.nextLine();
        System.out.print("Diagnostico: ");
        String diagnostico = SCANNER.nextLine();
        System.out.print("Estudios: ");
        String estudios = SCANNER.nextLine();

        boolean ok = SYSTEM.registrarConsultaMedica(dni, resumen, diagnostico, estudios);
        System.out.println(ok ? "Consulta registrada en historial." : "No se pudo registrar (verifique historial).\n");
    }

    private static void actualizarDiagnostico() {
        System.out.print("DNI paciente: ");
        String dni = SCANNER.nextLine();
        System.out.print("Nuevo diagnostico: ");
        String diagnostico = SCANNER.nextLine();
        boolean ok = SYSTEM.actualizarDiagnostico(dni, diagnostico);
        System.out.println(ok ? "Diagnostico actualizado." : "No se pudo actualizar.");
    }

    private static void registrarEstudio() {
        System.out.print("DNI paciente: ");
        String dni = SCANNER.nextLine();
        System.out.print("Estudio y resultado: ");
        String estudio = SCANNER.nextLine();
        boolean ok = SYSTEM.registrarEstudio(dni, estudio);
        System.out.println(ok ? "Estudio actualizado." : "No se pudo actualizar.");
    }

    private static void marcarTurnoAtendido() {
        System.out.print("ID turno: ");
        String turnoId = SCANNER.nextLine();
        boolean ok = SYSTEM.marcarTurnoAtendido(turnoId);
        System.out.println(ok ? "Turno marcado como atendido." : "Turno inexistente.");
    }

    private static void cancelarJornada() {
        try {
            System.out.print("Matricula medico: ");
            String matricula = SCANNER.nextLine();
            System.out.print("Fecha de jornada (YYYY-MM-DD): ");
            LocalDate fecha = LocalDate.parse(SCANNER.nextLine());
            System.out.println(SYSTEM.cancelarJornadaMedica(matricula, fecha));
        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha invalido.");
        }
    }

    private static void verNotificaciones() {
        System.out.print("Matricula medico: ");
        String matricula = SCANNER.nextLine();
        List<String> notificaciones = SYSTEM.verNotificacionesMedico(matricula);
        if (notificaciones.isEmpty()) {
            System.out.println("Sin notificaciones.");
            return;
        }
        notificaciones.forEach(System.out::println);
    }

    private static void registrarMedico() {
        System.out.print("Matricula: ");
        String matricula = SCANNER.nextLine();
        System.out.print("Nombre: ");
        String nombre = SCANNER.nextLine();
        System.out.print("Apellido: ");
        String apellido = SCANNER.nextLine();
        System.out.print("Especialidad: ");
        String especialidad = SCANNER.nextLine();
        boolean ok = SYSTEM.registrarMedico(matricula, nombre, apellido, especialidad);
        System.out.println(ok ? "Medico registrado." : "No se pudo registrar (matricula duplicada).");
    }

    private static void agregarDisponibilidad() {
        try {
            System.out.print("Matricula medico: ");
            String matricula = SCANNER.nextLine();
            System.out.print("Dia (1=Lunes ... 7=Domingo): ");
            DayOfWeek dia = DayOfWeek.of(Integer.parseInt(SCANNER.nextLine()));
            System.out.print("Hora inicio (HH:MM): ");
            LocalTime inicio = LocalTime.parse(SCANNER.nextLine());
            System.out.print("Hora fin (HH:MM): ");
            LocalTime fin = LocalTime.parse(SCANNER.nextLine());
            boolean ok = SYSTEM.agregarDisponibilidad(matricula, dia, inicio, fin);
            System.out.println(ok ? "Disponibilidad agregada." : "No se pudo agregar (solapamiento o medico inexistente).");
        } catch (RuntimeException e) {
            System.out.println("Datos invalidos.");
        }
    }

    private static void reemplazarDisponibilidadSimple() {
        try {
            System.out.print("Matricula medico: ");
            String matricula = SCANNER.nextLine();
            System.out.println("Se reemplazara por una unica franja.");
            System.out.print("Dia (1=Lunes ... 7=Domingo): ");
            DayOfWeek dia = DayOfWeek.of(Integer.parseInt(SCANNER.nextLine()));
            System.out.print("Hora inicio (HH:MM): ");
            LocalTime inicio = LocalTime.parse(SCANNER.nextLine());
            System.out.print("Hora fin (HH:MM): ");
            LocalTime fin = LocalTime.parse(SCANNER.nextLine());
            boolean ok = SYSTEM.reemplazarDisponibilidades(matricula, List.of(new Disponibilidad(dia, inicio, fin)));
            System.out.println(ok ? "Disponibilidad reemplazada." : "No se pudo reemplazar.");
        } catch (RuntimeException e) {
            System.out.println("Datos invalidos.");
        }
    }

    private static void registrarEmpleado() {
        try {
            System.out.print("Legajo: ");
            String legajo = SCANNER.nextLine();
            System.out.print("Nombre: ");
            String nombre = SCANNER.nextLine();
            System.out.print("Rol (RECEPCIONISTA, MEDICO, ADMINISTRADOR): ");
            RolUsuario rol = RolUsuario.valueOf(SCANNER.nextLine().trim().toUpperCase());
            boolean ok = SYSTEM.registrarEmpleado(legajo, nombre, rol);
            System.out.println(ok ? "Empleado registrado." : "No se pudo registrar (legajo duplicado).");
        } catch (IllegalArgumentException e) {
            System.out.println("Rol invalido.");
        }
    }

    private static void informarHorarioLaboral() {
        LocalTime now = LocalTime.now();
        boolean horarioLaboral = !now.isBefore(LocalTime.of(8, 0)) && !now.isAfter(LocalTime.of(20, 0));
        if (!horarioLaboral) {
            System.out.println("[INFO] Fuera de horario laboral (08:00-20:00). Sistema en modo base de prueba.");
        }
    }
}
