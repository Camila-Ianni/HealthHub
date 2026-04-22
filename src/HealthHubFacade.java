import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Punto de entrada de aplicación para operar pacientes, doctores y turnos.
 */
public class HealthHubFacade {
    private final GestorPacientes gestorPacientes;
    private final GestorDoctores gestorDoctores;
    private final GestorTurnos gestorTurnos;

    public HealthHubFacade() {
        this.gestorPacientes = new GestorPacientes();
        this.gestorDoctores = new GestorDoctores();
        this.gestorTurnos = new GestorTurnos();
    }

    public void registrarPaciente(Patient p) {
        gestorPacientes.registrarPaciente(p);
    }

    public void registrarDoctor(Doctor d) {
        gestorDoctores.registrarDoctor(d);
    }

    public Appointment programarTurno(String idTurno, String patientId, String doctorId, String fecha, String hora, String motivo) {
        Patient p = gestorPacientes.buscarPorPacienteId(patientId);
        if (p == null) {
            throw new IllegalArgumentException("No existe el paciente con ID: " + patientId);
        }

        Doctor d = gestorDoctores.buscarPorDoctorId(doctorId);
        if (d == null) {
            throw new IllegalArgumentException("No existe el doctor con ID: " + doctorId);
        }

        Appointment t = new Appointment(idTurno, p, d, fecha, hora, motivo);
        gestorTurnos.registrarTurno(t);
        return t;
    }

    public List<Patient> listarPacientes() {
        return gestorPacientes.listarPacientes();
    }

    public List<Doctor> listarDoctores() {
        return gestorDoctores.listarDoctores();
    }

    public List<Appointment> listarTurnos() {
        return gestorTurnos.listarTurnos();
    }

    public void guardarBackup(String carpeta) {
        Path base = Paths.get(carpeta);
        try {
            if (!Files.exists(base)) {
                Files.createDirectories(base);
            }

            Path archivoPacientes = base.resolve("pacientes_data.txt");
            Path archivoDoctores = base.resolve("doctores_data.txt");
            Path archivoTurnos = base.resolve("turnos_data.txt");

            guardarPacientes(archivoPacientes);
            guardarDoctores(archivoDoctores);
            guardarTurnos(archivoTurnos);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el backup", e);
        }
    }

    public void cargarBackup(String carpeta) {
        Path base = Paths.get(carpeta);
        Path archivoPacientes = base.resolve("pacientes_data.txt");
        Path archivoDoctores = base.resolve("doctores_data.txt");
        Path archivoTurnos = base.resolve("turnos_data.txt");

        Map<String, Patient> pacientesPorId = new HashMap<String, Patient>();
        Map<String, Doctor> doctoresPorId = new HashMap<String, Doctor>();

        gestorTurnos.limpiar();
        gestorPacientes.limpiar();
        gestorDoctores.limpiar();

        try {
            cargarPacientes(archivoPacientes, pacientesPorId);
            cargarDoctores(archivoDoctores, doctoresPorId);
            cargarTurnos(archivoTurnos, pacientesPorId, doctoresPorId);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el backup", e);
        }
    }

    private void guardarPacientes(Path archivoPacientes) throws IOException {
        List<String> lineas = new ArrayList<String>();
        for (Patient p : gestorPacientes.listarPacientes()) {
            String linea = unirCampos(
                p.getId(), p.getUsername(), p.getPassword(), p.getEmail(),
                p.getPatientId(), p.getFirstName(), p.getLastName(),
                p.getDateOfBirth(), p.getGender(), p.getPhoneNumber(),
                p.getAddress(), p.getMedicalHistory()
            );
            lineas.add(linea);
        }
        escribirLineas(archivoPacientes, lineas);
    }

    private void guardarDoctores(Path archivoDoctores) throws IOException {
        List<String> lineas = new ArrayList<String>();
        for (Doctor d : gestorDoctores.listarDoctores()) {
            String linea = unirCampos(
                d.getId(), d.getUsername(), d.getPassword(), d.getEmail(),
                d.getDoctorId(), d.getFirstName(), d.getLastName(),
                d.getSpecialization(), d.getLicenseNumber(), d.getPhoneNumber(),
                String.valueOf(d.getYearsOfExperience()), d.getDepartment(),
                String.valueOf(d.isAvailable())
            );
            lineas.add(linea);
        }
        escribirLineas(archivoDoctores, lineas);
    }

    private void guardarTurnos(Path archivoTurnos) throws IOException {
        List<String> lineas = new ArrayList<String>();
        for (Appointment t : gestorTurnos.listarTurnos()) {
            String linea = unirCampos(
                t.getAppointmentId(),
                t.getPatient() != null ? t.getPatient().getPatientId() : "",
                t.getDoctor() != null ? t.getDoctor().getDoctorId() : "",
                t.getAppointmentDate(), t.getAppointmentTime(),
                t.getStatus(), t.getReason(), t.getDiagnosis(), t.getPrescription()
            );
            lineas.add(linea);
        }
        escribirLineas(archivoTurnos, lineas);
    }

    private void cargarPacientes(Path archivoPacientes, Map<String, Patient> pacientesPorId) throws IOException {
        if (!Files.exists(archivoPacientes)) {
            return;
        }

        // Nota para mí mismo: parser deliberadamente manual para no ocultar lógica en utilidades "mágicas".
        try (BufferedReader br = Files.newBufferedReader(archivoPacientes, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    List<String> c = separarCampos(linea);
                    if (c.size() >= 12) {
                        Patient p = new Patient(
                            c.get(0), c.get(1), c.get(2), c.get(3),
                            c.get(4), c.get(5), c.get(6), c.get(7),
                            c.get(8), c.get(9), c.get(10), c.get(11)
                        );
                        gestorPacientes.registrarPaciente(p);
                        pacientesPorId.put(p.getPatientId(), p);
                    }
                }
            }
        }
    }

    private void cargarDoctores(Path archivoDoctores, Map<String, Doctor> doctoresPorId) throws IOException {
        if (!Files.exists(archivoDoctores)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(archivoDoctores, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    List<String> c = separarCampos(linea);
                    if (c.size() >= 13) {
                        int experiencia = parseEnteroSeguro(c.get(10));
                        Doctor d = new Doctor(
                            c.get(0), c.get(1), c.get(2), c.get(3),
                            c.get(4), c.get(5), c.get(6), c.get(7),
                            c.get(8), c.get(9), experiencia, c.get(11)
                        );
                        d.setAvailable(Boolean.parseBoolean(c.get(12)));
                        gestorDoctores.registrarDoctor(d);
                        doctoresPorId.put(d.getDoctorId(), d);
                    }
                }
            }
        }
    }

    private void cargarTurnos(Path archivoTurnos, Map<String, Patient> pacientesPorId, Map<String, Doctor> doctoresPorId) throws IOException {
        if (!Files.exists(archivoTurnos)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(archivoTurnos, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                List<String> c = separarCampos(linea);
                if (c.size() < 9) {
                    continue;
                }

                Patient p = pacientesPorId.get(c.get(1));
                Doctor d = doctoresPorId.get(c.get(2));
                if (p == null || d == null) {
                    continue;
                }

                Appointment t = new Appointment(c.get(0), p, d, c.get(3), c.get(4), c.get(6));
                t.setStatus(c.get(5));
                t.setDiagnosis(c.get(7));
                t.setPrescription(c.get(8));
                gestorTurnos.registrarTurno(t);
            }
        }
    }

    private void escribirLineas(Path archivo, List<String> lineas) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(
            archivo,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }

    private String unirCampos(String... valores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(escapar(valores[i]));
        }
        return sb.toString();
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("|", "\\|");
    }

    private List<String> separarCampos(String linea) {
        List<String> campos = new ArrayList<String>();
        StringBuilder actual = new StringBuilder();
        boolean escapeActivo = false;

        for (int i = 0; i < linea.length(); i++) {
            char ch = linea.charAt(i);

            if (escapeActivo) {
                actual.append(ch);
                escapeActivo = false;
            } else {
                if (ch == '\\') {
                    escapeActivo = true;
                } else if (ch == '|') {
                    campos.add(actual.toString());
                    actual = new StringBuilder();
                } else {
                    actual.append(ch);
                }
            }
        }

        // Nota para mí mismo: este append final evita perder el último campo si no termina con delimitador.
        campos.add(actual.toString());
        return campos;
    }

    private int parseEnteroSeguro(String valor) {
        if (!textoConValor(valor)) {
            return 0;
        }
        return Integer.parseInt(valor.trim());
    }

    private boolean textoConValor(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
