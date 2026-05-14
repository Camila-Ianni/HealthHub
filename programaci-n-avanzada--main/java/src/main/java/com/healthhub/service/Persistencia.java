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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Persistencia {

    private final Path rutaBase;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Persistencia(Path rutaBase) {
        this.rutaBase = rutaBase;
    }


    public static class AgendaConsolidadaFila {
        private final LocalDateTime fechaHora;
        private final String nombrePaciente;
        private final String nombreMedico;
        private final EstadoTurno estado;
        private final boolean sobreturno;

        public AgendaConsolidadaFila(
            LocalDateTime fechaHora,
            String nombrePaciente,
            String nombreMedico,
            EstadoTurno estado,
            boolean sobreturno
        ) {
            this.fechaHora = fechaHora;
            this.nombrePaciente = nombrePaciente;
            this.nombreMedico = nombreMedico;
            this.estado = estado;
            this.sobreturno = sobreturno;
        }

        public LocalDateTime getFechaHora() {
            return fechaHora;
        }

        public String getNombrePaciente() {
            return nombrePaciente;
        }

        public String getNombreMedico() {
            return nombreMedico;
        }

        public EstadoTurno getEstado() {
            return estado;
        }

        public boolean isSobreturno() {
            return sobreturno;
        }
    }


    public String obtenerConsultaAgendaConsolidada() {
        return "SELECT t.FechaHora, p.Nombre || ' ' || p.Apellido AS Paciente, "
            + "m.Nombre || ' ' || m.Apellido AS Medico, t.Estado, t.Sobreturno "
            + "FROM Turno t "
            + "INNER JOIN Paciente p ON p.DNI = t.DniPaciente "
            + "INNER JOIN Medico m ON m.Matricula = t.MatriculaMedico "
            + "ORDER BY t.FechaHora";
    }


    public List<AgendaConsolidadaFila> consultarAgendaConsolidadaInnerJoin(
        List<Turno> turnos,
        List<Paciente> pacientes,
        List<Medico> medicos
    ) {
        Map<String, Paciente> pacientePorDni = new HashMap<>();
        for (Paciente paciente : pacientes) {
            pacientePorDni.put(paciente.getDni(), paciente);
        }

        Map<String, Medico> medicoPorMatricula = new HashMap<>();
        for (Medico medico : medicos) {
            medicoPorMatricula.put(medico.getMatricula(), medico);
        }

        List<AgendaConsolidadaFila> filas = new ArrayList<>();
        for (Turno turno : turnos) {
            Paciente paciente = pacientePorDni.get(turno.getDniPaciente());
            Medico medico = medicoPorMatricula.get(turno.getMatriculaMedico());
            if (paciente == null || medico == null) {
                continue;
            }

            filas.add(new AgendaConsolidadaFila(
                turno.getFechaHora(),
                paciente.getNombre() + " " + paciente.getApellido(),
                medico.getNombre() + " " + medico.getApellido(),
                turno.getEstado(),
                turno.isSobreturno()
            ));
        }

        filas.sort(Comparator.comparing(AgendaConsolidadaFila::getFechaHora));
        return filas;
    }



    public void guardarPacientes(List<Paciente> pacientes) {
        String archivo = rutaBase.resolve("pacientes_data.txt").toString();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("DNI|Nombre|Apellido|Telefono|ObraSocial");
            writer.newLine();

            for (Paciente paciente : pacientes) {
                String linea = String.join("|",
                    paciente.getDni(),
                    paciente.getNombre(),
                    paciente.getApellido(),
                    paciente.getTelefono(),
                    paciente.getObraSocial()
                );
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar pacientes: " + e.getMessage());
        }
    }


    public void guardarMedicos(List<Medico> medicos) {
        String archivo = rutaBase.resolve("medicos_data.txt").toString();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("Matricula|Nombre|Apellido|Especialidad");
            writer.newLine();

            for (Medico medico : medicos) {
                String linea = String.join("|",
                    medico.getMatricula(),
                    medico.getNombre(),
                    medico.getApellido(),
                    medico.getEspecialidad()
                );
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar medicos: " + e.getMessage());
        }
    }


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
                        String.valueOf(disp.getDia().getValue()),
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


    public void guardarHistoriales(List<HistorialClinico> historiales) {
        String archivo = rutaBase.resolve("historiales_data.txt").toString();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("DniPaciente|Fecha|Resumen|Diagnostico|Estudios");
            writer.newLine();

            for (HistorialClinico historial : historiales) {
                String dniPaciente = historial.getDniPaciente();

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



    public List<Paciente> cargarPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String archivo = rutaBase.resolve("pacientes_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 5) {
                    pacientes.add(new Paciente(
                        partes[0],
                        partes[1],
                        partes[2],
                        partes[3],
                        partes[4]
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar pacientes (quizás el archivo no existe): " + e.getMessage());
        }

        return pacientes;
    }


    public List<Medico> cargarMedicos() {
        List<Medico> medicos = new ArrayList<>();
        String archivo = rutaBase.resolve("medicos_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 4) {
                    medicos.add(new Medico(
                        partes[0],
                        partes[1],
                        partes[2],
                        partes[3]
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar medicos: " + e.getMessage());
        }

        return medicos;
    }


    public Map<String, List<Disponibilidad>> cargarDisponibilidades() {
        Map<String, List<Disponibilidad>> disponibilidades = new HashMap<>();
        String archivo = rutaBase.resolve("disponibilidades_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 4) {
                    try {
                        String matricula = partes[0];
                        int diaNum = Integer.parseInt(partes[1]);
                        LocalTime inicio = LocalTime.parse(partes[2], TIME_FORMAT);
                        LocalTime fin = LocalTime.parse(partes[3], TIME_FORMAT);

                        Disponibilidad disp = new Disponibilidad(DayOfWeek.of(diaNum), inicio, fin);
                        disponibilidades.computeIfAbsent(matricula, k -> new ArrayList<>()).add(disp);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.out.println("Línea inválida en disponibilidades, se omite: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar disponibilidades: " + e.getMessage());
        }

        return disponibilidades;
    }


    public List<Turno> cargarTurnos() {
        List<Turno> turnos = new ArrayList<>();
        String archivo = rutaBase.resolve("turnos_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 6) {
                    turnos.add(new Turno(
                        partes[0],
                        partes[1],
                        partes[2],
                        LocalDateTime.parse(partes[3], DATETIME_FORMAT),
                        Boolean.parseBoolean(partes[5]),
                        EstadoTurno.valueOf(partes[4])
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar turnos: " + e.getMessage());
        }

        return turnos;
    }


    public List<HistorialClinico> cargarHistoriales() {
        List<HistorialClinico> historiales = new ArrayList<>();
        Map<String, HistorialClinico> mapaHistoriales = new HashMap<>();
        String archivo = rutaBase.resolve("historiales_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 2) {
                    String dniPaciente = partes[0];

                    HistorialClinico historial = mapaHistoriales.computeIfAbsent(
                        dniPaciente, k -> new HistorialClinico(k)
                    );

                    if (partes.length >= 5 && !partes[1].isEmpty()) {
                        EntradaHistorial entrada = new EntradaHistorial(
                            LocalDateTime.parse(partes[1], DATETIME_FORMAT),
                            partes[2],
                            partes[3],
                            partes[4]
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


    public List<Empleado> cargarEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        String archivo = rutaBase.resolve("empleados_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 3) {
                    empleados.add(new Empleado(
                        partes[0],
                        partes[1],
                        RolUsuario.valueOf(partes[2])
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar empleados: " + e.getMessage());
        }

        return empleados;
    }


    public Map<String, List<String>> cargarNotificaciones() {
        Map<String, List<String>> notificaciones = new HashMap<>();
        String archivo = rutaBase.resolve("notificaciones_data.txt").toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|", 2);
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
