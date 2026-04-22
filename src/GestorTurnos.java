import java.util.ArrayList;
import java.util.List;

/**
 * Gestión de turnos (appointments).
 */
public class GestorTurnos {
    private final List<Appointment> turnos;

    public GestorTurnos() {
        this.turnos = new ArrayList<Appointment>();
    }

    public void registrarTurno(Appointment t) {
        if (!esTurnoValido(t)) {
            throw new IllegalArgumentException("Turno inválido");
        }
        if (buscarPorId(t.getAppointmentId()) != null) {
            throw new IllegalArgumentException("Ya existe un turno con ese ID: " + t.getAppointmentId());
        }
        turnos.add(t);
    }

    public Appointment buscarPorId(String appointmentId) {
        for (Appointment t : turnos) {
            if (coincideId(t.getAppointmentId(), appointmentId)) {
                return t;
            }
        }
        return null;
    }

    public List<Appointment> listarTurnos() {
        return new ArrayList<Appointment>(turnos);
    }

    public void limpiar() {
        turnos.clear();
    }

    private boolean esTurnoValido(Appointment t) {
        return t != null
            && textoConValor(t.getAppointmentId())
            && t.getPatient() != null
            && t.getDoctor() != null
            && textoConValor(t.getAppointmentDate())
            && textoConValor(t.getAppointmentTime());
    }

    private boolean textoConValor(String v) {
        return v != null && !v.trim().isEmpty();
    }

    private boolean coincideId(String actual, String buscado) {
        return textoConValor(actual) && textoConValor(buscado) && actual.trim().equals(buscado.trim());
    }
}
