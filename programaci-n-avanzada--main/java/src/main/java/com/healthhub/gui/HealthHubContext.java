package com.healthhub.gui;

import com.healthhub.domain.Disponibilidad;
import com.healthhub.domain.Empleado;
import com.healthhub.domain.HistorialClinico;
import com.healthhub.domain.Medico;
import com.healthhub.domain.Paciente;
import com.healthhub.domain.Turno;
import com.healthhub.service.GestorEmpleados;
import com.healthhub.service.GestorHistoriales;
import com.healthhub.service.GestorMedicos;
import com.healthhub.service.GestorNotificaciones;
import com.healthhub.service.GestorPacientes;
import com.healthhub.service.GestorTurnos;
import com.healthhub.service.Persistencia;

import java.nio.file.Path;
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
}