/**
 * Appointment class
 * Manages medical appointments between patients and doctors
 */
public class Appointment {
    // Appointment attributes
    private String appointmentId;    // Unique appointment ID
    private Patient patient;         // Patient object reference
    private Doctor doctor;           // Doctor object reference
    private String appointmentDate;  // Appointment date
    private String appointmentTime;  // Appointment time
    private String status;           // Status: SCHEDULED, COMPLETED, CANCELLED
    private String reason;           // Reason for visit
    private String diagnosis;        // Doctor's diagnosis
    private String prescription;     // Prescribed treatment

    // Default constructor
    public Appointment() {
    }

    // Parameterized constructor - creates scheduled appointment
    public Appointment(String appointmentId, Patient patient, Doctor doctor,
                      String appointmentDate, String appointmentTime,
                      String reason) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = "SCHEDULED";  // Initial status
    }

    // Getters and Setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    /**
     * Completes an appointment with diagnosis and prescription
     * Changes status to COMPLETED
     */
    public void completeAppointment(String diagnosis, String prescription) {
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.status = "COMPLETED";
    }

    /**
     * Cancels an appointment
     * Changes status to CANCELLED
     */
    public void cancelAppointment() {
        this.status = "CANCELLED";
    }

    // Override toString for readable output
    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + appointmentId + '\'' +
                ", patient=" + (patient != null ? patient.getFullName() : "N/A") +
                ", doctor=" + (doctor != null ? doctor.getFullName() : "N/A") +
                ", date='" + appointmentDate + '\'' +
                ", time='" + appointmentTime + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
