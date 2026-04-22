import java.util.ArrayList;
import java.util.List;

/**
 * Gestión de pacientes.
 */
public class GestorPacientes {
    private final List<Patient> pacientes;

    public GestorPacientes() {
        this.pacientes = new ArrayList<Patient>();
    }

    public void registrarPaciente(Patient p) {
        if (!esPacienteValido(p)) {
            throw new IllegalArgumentException("Paciente inválido");
        }
        if (buscarPorPacienteId(p.getPatientId()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente con ese patientId: " + p.getPatientId());
        }
        pacientes.add(p);
    }

    public Patient buscarPorPacienteId(String patientId) {
        for (Patient p : pacientes) {
            if (coincideId(p.getPatientId(), patientId)) {
                return p;
            }
        }
        return null;
    }

    public List<Patient> listarPacientes() {
        return new ArrayList<Patient>(pacientes);
    }

    public void limpiar() {
        pacientes.clear();
    }

    private boolean esPacienteValido(Patient p) {
        return p != null && textoConValor(p.getPatientId()) && textoConValor(p.getFirstName()) && textoConValor(p.getLastName());
    }

    private boolean textoConValor(String v) {
        return v != null && !v.trim().isEmpty();
    }

    private boolean coincideId(String actual, String buscado) {
        return textoConValor(actual) && textoConValor(buscado) && actual.trim().equals(buscado.trim());
    }
}
