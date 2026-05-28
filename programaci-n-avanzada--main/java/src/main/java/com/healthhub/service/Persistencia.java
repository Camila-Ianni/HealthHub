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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persistencia {

    private final Path rutaBase;
    private final String urlConexion;

    public Persistencia(Path rutaBase) {
        this.rutaBase = rutaBase;
        this.urlConexion = "jdbc:h2:file:" + rutaBase.resolve("healthhub-db").toString().replace('\\', '/') + ";MODE=MySQL";

        try {
            Files.createDirectories(rutaBase);
        } catch (IOException e) {
            System.out.println("No se pudo crear la carpeta de datos: " + e.getMessage());
        }

        inicializarEsquema();
    }

    public static class AgendaConsolidadaFila {
        private final LocalDateTime fechaHora;
        private final String dniPaciente;
        private final String nombrePaciente;
        private final String matriculaMedico;
        private final String nombreMedico;
        private final EstadoTurno estado;
        private final boolean sobreturno;

        public AgendaConsolidadaFila(
            LocalDateTime fechaHora,
            String dniPaciente,
            String nombrePaciente,
            String matriculaMedico,
            String nombreMedico,
            EstadoTurno estado,
            boolean sobreturno
        ) {
            this.fechaHora = fechaHora;
            this.dniPaciente = dniPaciente;
            this.nombrePaciente = nombrePaciente;
            this.matriculaMedico = matriculaMedico;
            this.nombreMedico = nombreMedico;
            this.estado = estado;
            this.sobreturno = sobreturno;
        }

        public LocalDateTime getFechaHora() {
            return fechaHora;
        }

        public String getDniPaciente() {
            return dniPaciente;
        }

        public String getNombrePaciente() {
            return nombrePaciente;
        }

        public String getMatriculaMedico() {
            return matriculaMedico;
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
        return "SELECT t.FECHA_HORA, p.NOMBRE || ' ' || p.APELLIDO AS PACIENTE, "
            + "m.NOMBRE || ' ' || m.APELLIDO AS MEDICO, t.ESTADO, t.SOBRETURNO "
            + "FROM TURNO t "
            + "INNER JOIN PACIENTE p ON p.DNI = t.DNI_PACIENTE "
            + "INNER JOIN MEDICO m ON m.MATRICULA = t.MATRICULA_MEDICO "
            + "ORDER BY t.FECHA_HORA";
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
                paciente.getDni(),
                paciente.getNombre() + " " + paciente.getApellido(),
                medico.getMatricula(),
                medico.getNombre() + " " + medico.getApellido(),
                turno.getEstado(),
                turno.isSobreturno()
            ));
        }

        filas.sort(Comparator.comparing(AgendaConsolidadaFila::getFechaHora));
        return filas;
    }

    public void guardarPacientes(List<Paciente> pacientes) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM PACIENTE");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO PACIENTE (DNI, NOMBRE, APELLIDO, TELEFONO, OBRA_SOCIAL) VALUES (?, ?, ?, ?, ?)"
                )) {
                    for (Paciente paciente : pacientes) {
                        ps.setString(1, paciente.getDni());
                        ps.setString(2, paciente.getNombre());
                        ps.setString(3, paciente.getApellido());
                        ps.setString(4, paciente.getTelefono());
                        ps.setString(5, paciente.getObraSocial());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }, "Error al guardar pacientes: ");
    }

    public void guardarMedicos(List<Medico> medicos) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM MEDICO");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO MEDICO (MATRICULA, NOMBRE, APELLIDO, ESPECIALIDAD) VALUES (?, ?, ?, ?)"
                )) {
                    for (Medico medico : medicos) {
                        ps.setString(1, medico.getMatricula());
                        ps.setString(2, medico.getNombre());
                        ps.setString(3, medico.getApellido());
                        ps.setString(4, medico.getEspecialidad());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }, "Error al guardar medicos: ");
    }

    public void guardarDisponibilidades(Map<String, List<Disponibilidad>> disponibilidades) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM DISPONIBILIDAD");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO DISPONIBILIDAD (MATRICULA, DIA, HORA_INICIO, HORA_FIN) VALUES (?, ?, ?, ?)"
                )) {
                    for (Map.Entry<String, List<Disponibilidad>> entry : disponibilidades.entrySet()) {
                        for (Disponibilidad disponibilidad : entry.getValue()) {
                            ps.setString(1, entry.getKey());
                            ps.setInt(2, disponibilidad.getDia().getValue());
                            ps.setTime(3, Time.valueOf(disponibilidad.getHoraInicio()));
                            ps.setTime(4, Time.valueOf(disponibilidad.getHoraFin()));
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                }
            }
        }, "Error al guardar disponibilidades: ");
    }

    public void guardarTurnos(List<Turno> turnos) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM TURNO");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO TURNO (ID, DNI_PACIENTE, MATRICULA_MEDICO, FECHA_HORA, ESTADO, SOBRETURNO) VALUES (?, ?, ?, ?, ?, ?)"
                )) {
                    for (Turno turno : turnos) {
                        ps.setString(1, turno.getId());
                        ps.setString(2, turno.getDniPaciente());
                        ps.setString(3, turno.getMatriculaMedico());
                        ps.setTimestamp(4, Timestamp.valueOf(turno.getFechaHora()));
                        ps.setString(5, turno.getEstado().name());
                        ps.setBoolean(6, turno.isSobreturno());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }, "Error al guardar turnos: ");
    }

    public void guardarHistoriales(List<HistorialClinico> historiales) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM HISTORIAL_CLINICO");
                try (PreparedStatement historialPs = conn.prepareStatement(
                    "INSERT INTO HISTORIAL_CLINICO (DNI_PACIENTE) VALUES (?)"
                ); PreparedStatement entradaPs = conn.prepareStatement(
                    "INSERT INTO ENTRADA_HISTORIAL (DNI_PACIENTE, FECHA, RESUMEN, DIAGNOSTICO, ESTUDIOS) VALUES (?, ?, ?, ?, ?)"
                )) {
                    for (HistorialClinico historial : historiales) {
                        historialPs.setString(1, historial.getDniPaciente());
                        historialPs.addBatch();

                        for (EntradaHistorial entrada : historial.getEntradas()) {
                            entradaPs.setString(1, historial.getDniPaciente());
                            entradaPs.setTimestamp(2, Timestamp.valueOf(entrada.getFecha()));
                            entradaPs.setString(3, entrada.getResumen());
                            entradaPs.setString(4, entrada.getDiagnostico());
                            entradaPs.setString(5, entrada.getEstudios());
                            entradaPs.addBatch();
                        }
                    }

                    historialPs.executeBatch();
                    entradaPs.executeBatch();
                }
            }
        }, "Error al guardar historiales: ");
    }

    public void guardarEmpleados(List<Empleado> empleados) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM EMPLEADO");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO EMPLEADO (LEGAJO, NOMBRE, ROL) VALUES (?, ?, ?)"
                )) {
                    for (Empleado empleado : empleados) {
                        ps.setString(1, empleado.getLegajo());
                        ps.setString(2, empleado.getNombre());
                        ps.setString(3, empleado.getRol().name());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }, "Error al guardar empleados: ");
    }

    public void guardarNotificaciones(Map<String, List<String>> notificaciones) {
        ejecutarTransaccion(conn -> {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM NOTIFICACION");
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO NOTIFICACION (LEGAJO, MENSAJE) VALUES (?, ?)"
                )) {
                    for (Map.Entry<String, List<String>> entry : notificaciones.entrySet()) {
                        for (String mensaje : entry.getValue()) {
                            ps.setString(1, entry.getKey());
                            ps.setString(2, mensaje);
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                }
            }
        }, "Error al guardar notificaciones: ");
    }

    public List<Paciente> cargarPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT DNI, NOMBRE, APELLIDO, TELEFONO, OBRA_SOCIAL FROM PACIENTE ORDER BY APELLIDO, NOMBRE"
        )) {
            while (rs.next()) {
                pacientes.add(new Paciente(
                    rs.getString("DNI"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDO"),
                    rs.getString("TELEFONO"),
                    rs.getString("OBRA_SOCIAL")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar pacientes: " + e.getMessage());
        }
        return pacientes;
    }

    public List<Medico> cargarMedicos() {
        List<Medico> medicos = new ArrayList<>();
        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT MATRICULA, NOMBRE, APELLIDO, ESPECIALIDAD FROM MEDICO ORDER BY APELLIDO, NOMBRE"
        )) {
            while (rs.next()) {
                medicos.add(new Medico(
                    rs.getString("MATRICULA"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDO"),
                    rs.getString("ESPECIALIDAD")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar medicos: " + e.getMessage());
        }
        return medicos;
    }

    public Map<String, List<Disponibilidad>> cargarDisponibilidades() {
        Map<String, List<Disponibilidad>> disponibilidades = new HashMap<>();
        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT MATRICULA, DIA, HORA_INICIO, HORA_FIN FROM DISPONIBILIDAD ORDER BY MATRICULA, DIA, HORA_INICIO"
        )) {
            while (rs.next()) {
                String matricula = rs.getString("MATRICULA");
                int dia = rs.getInt("DIA");
                LocalTime inicio = rs.getTime("HORA_INICIO").toLocalTime();
                LocalTime fin = rs.getTime("HORA_FIN").toLocalTime();
                disponibilidades.computeIfAbsent(matricula, k -> new ArrayList<>())
                    .add(new Disponibilidad(DayOfWeek.of(dia), inicio, fin));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar disponibilidades: " + e.getMessage());
        }
        return disponibilidades;
    }

    public List<Turno> cargarTurnos() {
        List<Turno> turnos = new ArrayList<>();
        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT ID, DNI_PACIENTE, MATRICULA_MEDICO, FECHA_HORA, SOBRETURNO, ESTADO FROM TURNO ORDER BY FECHA_HORA"
        )) {
            while (rs.next()) {
                turnos.add(new Turno(
                    rs.getString("ID"),
                    rs.getString("DNI_PACIENTE"),
                    rs.getString("MATRICULA_MEDICO"),
                    rs.getTimestamp("FECHA_HORA").toLocalDateTime(),
                    rs.getBoolean("SOBRETURNO"),
                    EstadoTurno.valueOf(rs.getString("ESTADO"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar turnos: " + e.getMessage());
        }
        return turnos;
    }

    public List<HistorialClinico> cargarHistoriales() {
        List<HistorialClinico> historiales = new ArrayList<>();
        Map<String, HistorialClinico> historialesPorDni = new HashMap<>();

        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT DNI_PACIENTE FROM HISTORIAL_CLINICO ORDER BY DNI_PACIENTE"
        )) {
            while (rs.next()) {
                HistorialClinico historial = new HistorialClinico(rs.getString("DNI_PACIENTE"));
                historiales.add(historial);
                historialesPorDni.put(historial.getDniPaciente(), historial);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar historiales: " + e.getMessage());
            return historiales;
        }

        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT DNI_PACIENTE, FECHA, RESUMEN, DIAGNOSTICO, ESTUDIOS FROM ENTRADA_HISTORIAL ORDER BY DNI_PACIENTE, FECHA, ID"
        )) {
            while (rs.next()) {
                String dniPaciente = rs.getString("DNI_PACIENTE");
                HistorialClinico historial = historialesPorDni.get(dniPaciente);
                if (historial == null) {
                    historial = new HistorialClinico(dniPaciente);
                    historiales.add(historial);
                    historialesPorDni.put(dniPaciente, historial);
                }
                historial.agregarEntrada(new EntradaHistorial(
                    rs.getTimestamp("FECHA").toLocalDateTime(),
                    rs.getString("RESUMEN"),
                    rs.getString("DIAGNOSTICO"),
                    rs.getString("ESTUDIOS")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar entradas de historiales: " + e.getMessage());
        }
        return historiales;
    }

    public List<Empleado> cargarEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT LEGAJO, NOMBRE, ROL FROM EMPLEADO ORDER BY LEGAJO"
        )) {
            while (rs.next()) {
                empleados.add(new Empleado(
                    rs.getString("LEGAJO"),
                    rs.getString("NOMBRE"),
                    RolUsuario.valueOf(rs.getString("ROL"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar empleados: " + e.getMessage());
        }
        return empleados;
    }

    public Map<String, List<String>> cargarNotificaciones() {
        Map<String, List<String>> notificaciones = new HashMap<>();
        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
            "SELECT LEGAJO, MENSAJE FROM NOTIFICACION ORDER BY ID"
        )) {
            while (rs.next()) {
                String legajo = rs.getString("LEGAJO");
                String mensaje = rs.getString("MENSAJE");
                notificaciones.computeIfAbsent(legajo, k -> new ArrayList<>()).add(mensaje);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar notificaciones: " + e.getMessage());
        }
        return notificaciones;
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(urlConexion, "sa", "");
    }

    private void inicializarEsquema() {
        try (Connection conn = conectar(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS PACIENTE ("
                + "DNI VARCHAR(20) PRIMARY KEY, "
                + "NOMBRE VARCHAR(100) NOT NULL, "
                + "APELLIDO VARCHAR(100) NOT NULL, "
                + "TELEFONO VARCHAR(30) NOT NULL, "
                + "OBRA_SOCIAL VARCHAR(100) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS MEDICO ("
                + "MATRICULA VARCHAR(20) PRIMARY KEY, "
                + "NOMBRE VARCHAR(100) NOT NULL, "
                + "APELLIDO VARCHAR(100) NOT NULL, "
                + "ESPECIALIDAD VARCHAR(100) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS EMPLEADO ("
                + "LEGAJO VARCHAR(20) PRIMARY KEY, "
                + "NOMBRE VARCHAR(100) NOT NULL, "
                + "ROL VARCHAR(30) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS DISPONIBILIDAD ("
                + "ID IDENTITY PRIMARY KEY, "
                + "MATRICULA VARCHAR(20) NOT NULL, "
                + "DIA INT NOT NULL, "
                + "HORA_INICIO TIME NOT NULL, "
                + "HORA_FIN TIME NOT NULL, "
                + "CONSTRAINT FK_DISPONIBILIDAD_MEDICO FOREIGN KEY (MATRICULA) REFERENCES MEDICO(MATRICULA) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS TURNO ("
                + "ID VARCHAR(40) PRIMARY KEY, "
                + "DNI_PACIENTE VARCHAR(20) NOT NULL, "
                + "MATRICULA_MEDICO VARCHAR(20) NOT NULL, "
                + "FECHA_HORA TIMESTAMP NOT NULL, "
                + "ESTADO VARCHAR(30) NOT NULL, "
                + "SOBRETURNO BOOLEAN NOT NULL, "
                + "CONSTRAINT FK_TURNO_PACIENTE FOREIGN KEY (DNI_PACIENTE) REFERENCES PACIENTE(DNI) ON DELETE CASCADE, "
                + "CONSTRAINT FK_TURNO_MEDICO FOREIGN KEY (MATRICULA_MEDICO) REFERENCES MEDICO(MATRICULA) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS HISTORIAL_CLINICO ("
                + "DNI_PACIENTE VARCHAR(20) PRIMARY KEY, "
                + "CONSTRAINT FK_HISTORIAL_PACIENTE FOREIGN KEY (DNI_PACIENTE) REFERENCES PACIENTE(DNI) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS ENTRADA_HISTORIAL ("
                + "ID IDENTITY PRIMARY KEY, "
                + "DNI_PACIENTE VARCHAR(20) NOT NULL, "
                + "FECHA TIMESTAMP NOT NULL, "
                + "RESUMEN VARCHAR(4000) NOT NULL, "
                + "DIAGNOSTICO VARCHAR(4000) NOT NULL, "
                + "ESTUDIOS VARCHAR(4000) NOT NULL, "
                + "CONSTRAINT FK_ENTRADA_HISTORIAL FOREIGN KEY (DNI_PACIENTE) REFERENCES HISTORIAL_CLINICO(DNI_PACIENTE) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS NOTIFICACION ("
                + "ID IDENTITY PRIMARY KEY, "
                + "LEGAJO VARCHAR(20) NOT NULL, "
                + "MENSAJE VARCHAR(1000) NOT NULL, "
                + "CONSTRAINT FK_NOTIFICACION_EMPLEADO FOREIGN KEY (LEGAJO) REFERENCES EMPLEADO(LEGAJO) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.out.println("No se pudo inicializar la base de datos: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface SqlConsumer {
        void accept(Connection connection) throws SQLException;
    }

    private void ejecutarTransaccion(SqlConsumer accion, String mensajeError) {
        Connection conn = null;
        try {
            conn = conectar();
            conn.setAutoCommit(false);
            accion.accept(conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                    // Se ignora el rollback secundario para no tapar el error original.
                }
            }
            System.out.println(mensajeError + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    // Sin accion.
                }
            }
        }
    }
}
