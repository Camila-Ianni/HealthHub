import java.util.ArrayList;
import java.util.List;

/**
 * Gestión de doctores.
 */
public class GestorDoctores {
    private final List<Doctor> doctores;

    public GestorDoctores() {
        this.doctores = new ArrayList<Doctor>();
    }

    public void registrarDoctor(Doctor d) {
        if (!esDoctorValido(d)) {
            throw new IllegalArgumentException("Doctor inválido");
        }
        if (buscarPorDoctorId(d.getDoctorId()) != null) {
            throw new IllegalArgumentException("Ya existe un doctor con ese doctorId: " + d.getDoctorId());
        }
        doctores.add(d);
    }

    public Doctor buscarPorDoctorId(String doctorId) {
        for (Doctor d : doctores) {
            if (coincideId(d.getDoctorId(), doctorId)) {
                return d;
            }
        }
        return null;
    }

    public List<Doctor> listarDoctores() {
        return new ArrayList<Doctor>(doctores);
    }

    public void limpiar() {
        doctores.clear();
    }

    private boolean esDoctorValido(Doctor d) {
        return d != null && textoConValor(d.getDoctorId()) && textoConValor(d.getFirstName()) && textoConValor(d.getLastName());
    }

    private boolean textoConValor(String v) {
        return v != null && !v.trim().isEmpty();
    }

    private boolean coincideId(String actual, String buscado) {
        return textoConValor(actual) && textoConValor(buscado) && actual.trim().equals(buscado.trim());
    }
}
