package com.healthhub.gui;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
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
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

public class HealthHubContext {

    final GestorPacientes gestorPacientes;
    final GestorMedicos gestorMedicos;
    final GestorTurnos gestorTurnos;
    final GestorHistoriales gestorHistoriales;
    final GestorEmpleados gestorEmpleados;
    final GestorNotificaciones gestorNotificaciones;
    final Persistencia persistencia;

    private HealthHubContext() {
        gestorHistoriales = new GestorHistoriales();
        gestorPacientes = new GestorPacientes(gestorHistoriales);
        gestorMedicos = new GestorMedicos();
        gestorNotificaciones = new GestorNotificaciones();
        gestorTurnos = new GestorTurnos(gestorMedicos, gestorNotificaciones);
        gestorEmpleados = new GestorEmpleados();
        persistencia = new Persistencia(Path.of("data"));
    }

    public static HealthHubContext crearDesdePersistencia() {
        HealthHubContext contexto = new HealthHubContext();

        List<Medico> medicos = contexto.persistencia.cargarMedicos();
        for (Medico medico : medicos) {
            contexto.gestorMedicos.registrarMedico(medico);
        }

        Map<String, List<Disponibilidad>> disponibilidades = contexto.persistencia.cargarDisponibilidades();
        for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidades.entrySet()) {
            for (Disponibilidad disponibilidad : entry.getValue()) {
                contexto.gestorMedicos.agregarDisponibilidad(entry.getKey(), disponibilidad);
            }
        }

        List<Paciente> pacientes = contexto.persistencia.cargarPacientes();
        for (Paciente paciente : pacientes) {
            contexto.gestorPacientes.registrarPaciente(paciente);
        }

        List<HistorialClinico> historiales = contexto.persistencia.cargarHistoriales();
        contexto.gestorHistoriales.cargarHistoriales(historiales);

        List<Turno> turnos = contexto.persistencia.cargarTurnos();
        contexto.gestorTurnos.cargarTurnos(turnos);

        contexto.gestorNotificaciones.cargarNotificaciones(contexto.persistencia.cargarNotificaciones());

        List<Empleado> empleados = contexto.persistencia.cargarEmpleados();
        contexto.gestorEmpleados.cargarEmpleados(empleados);

        contexto.asegurarDatosDemo();
        contexto.guardarTodo();

        return contexto;
    }

    void guardarTodo() {
        persistencia.guardarPacientes(gestorPacientes.listarTodos());
        persistencia.guardarMedicos(gestorMedicos.listarTodos());
        persistencia.guardarEmpleados(gestorEmpleados.listarTodos());
        persistencia.guardarTurnos(gestorTurnos.listarTodos());
        persistencia.guardarHistoriales(gestorHistoriales.listarTodos());
        persistencia.guardarDisponibilidades(gestorMedicos.obtenerDisponibilidadesPorMedico());
        persistencia.guardarNotificaciones(gestorNotificaciones.listarTodas());
    }

    private void asegurarDatosDemo() {
        boolean cambios = false;

        if (gestorEmpleados.listarTodos().isEmpty()) {
            gestorEmpleados.registrarEmpleado("1000", "Lucia Romero", RolUsuario.ADMINISTRADOR);
            gestorEmpleados.registrarEmpleado("2000", "Valeria Gomez", RolUsuario.RECEPCIONISTA);
            gestorEmpleados.registrarEmpleado("3000", "Martin Paredes", RolUsuario.MEDICO);
            cambios = true;
        }

        if (gestorMedicos.listarTodos().isEmpty()) {
            gestorMedicos.registrarMedico(new Medico("MP-101", "Martin", "Paredes", "Clinica general"));
            gestorMedicos.registrarMedico(new Medico("MP-202", "Sofia", "Suarez", "Pediatria"));

            gestorMedicos.agregarDisponibilidad("MP-101", new Disponibilidad(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(14, 0)));
            gestorMedicos.agregarDisponibilidad("MP-101", new Disponibilidad(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(15, 0)));
            gestorMedicos.agregarDisponibilidad("MP-202", new Disponibilidad(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(16, 0)));
            gestorMedicos.agregarDisponibilidad("MP-202", new Disponibilidad(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
            cambios = true;
        }

        if (gestorPacientes.listarTodos().isEmpty()) {
            gestorPacientes.registrarPaciente(new Paciente("40111222", "Ana", "Lopez", "1160001000", "OSDE"));
            gestorPacientes.registrarPaciente(new Paciente("40999888", "Bruno", "Diaz", "1160002000", "Swiss Medical"));
            gestorPacientes.registrarPaciente(new Paciente("38777666", "Carla", "Fernandez", "1160003000", "Galeno"));
            cambios = true;
        }

        if (gestorHistoriales.listarTodos().isEmpty()) {
            gestorHistoriales.registrarConsulta(RolUsuario.MEDICO, "40111222", "Consulta inicial", "Paciente estable", "Sin estudios pendientes");
            gestorHistoriales.registrarConsulta(RolUsuario.MEDICO, "40999888", "Control pediatrico", "Sin hallazgos relevantes", "Vacunacion al dia");
            cambios = true;
        }

        if (gestorTurnos.listarTodos().isEmpty()) {
            LocalDate proximoLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            gestorTurnos.crearTurno("40111222", "MP-101", LocalDateTime.of(proximoLunes, LocalTime.of(9, 0)), false);
            gestorTurnos.crearTurno("40999888", "MP-202", LocalDateTime.of(proximoLunes.plusDays(2), LocalTime.of(10, 0)), false);
            cambios = true;
        }

        if (gestorNotificaciones.listarTodas().isEmpty()) {
            gestorNotificaciones.agregarNotificacion("MP-101", "Bienvenida: ya podés gestionar la agenda desde el panel.");
            gestorNotificaciones.agregarNotificacion("MP-202", "Bienvenida: revisá tus turnos y notificaciones.");
            cambios = true;
        }

        if (cambios) {
            guardarTodo();
        }
    }
}