/**
 * Main class - Entry point for HealthHub system
 * Tests all functionality: patients, doctors, appointments
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== HealthHub System ===\n");

        HealthHubFacade facade = new HealthHubFacade();

        // Create test patients
        Patient patient1 = new Patient(
            "U001", "jdoe", "pass123", "john.doe@email.com",
            "P001", "John", "Doe", "1990-05-15", "Male",
            "+1-555-1234", "123 Main St, City", "No major illnesses"
        );

        Patient patient2 = new Patient(
            "U002", "msmith", "pass456", "mary.smith@email.com",
            "P002", "Mary", "Smith", "1985-08-22", "Female",
            "+1-555-5678", "456 Oak Ave, City", "Allergic to penicillin"
        );

        // Create test doctors
        Doctor doctor1 = new Doctor(
            "U003", "drjohnson", "doc123", "johnson@healthhub.com",
            "D001", "Robert", "Johnson", "Cardiology", "LIC-12345",
            "+1-555-9999", 15, "Cardiology"
        );

        Doctor doctor2 = new Doctor(
            "U004", "drwilliams", "doc456", "williams@healthhub.com",
            "D002", "Sarah", "Williams", "Pediatrics", "LIC-67890",
            "+1-555-8888", 10, "Pediatrics"
        );

        facade.registrarPaciente(patient1);
        facade.registrarPaciente(patient2);
        facade.registrarDoctor(doctor1);
        facade.registrarDoctor(doctor2);

        // Display registered patients
        System.out.println("--- Registered Patients ---");
        for (Patient p : facade.listarPacientes()) {
            System.out.println(p);
        }
        System.out.println();

        // Display available doctors
        System.out.println("--- Available Doctors ---");
        for (Doctor d : facade.listarDoctores()) {
            System.out.println(d);
        }
        System.out.println();

        // Create test appointments
        Appointment appointment1 = facade.programarTurno(
            "A001", "P001", "D001", "2026-04-01", "10:00 AM", "Annual checkup"
        );

        Appointment appointment2 = facade.programarTurno(
            "A002", "P002", "D002", "2026-04-02", "02:30 PM", "Child vaccination"
        );

        // Display scheduled appointments
        System.out.println("--- Scheduled Appointments ---");
        for (Appointment a : facade.listarTurnos()) {
            System.out.println(a);
        }
        System.out.println();

        // Test: Complete an appointment
        System.out.println("--- Completing Appointment A001 ---");
        appointment1.completeAppointment(
            "Patient is healthy. Blood pressure normal.",
            "Continue regular exercise. Follow-up in 6 months."
        );
        System.out.println("Status: " + appointment1.getStatus());
        System.out.println("Diagnosis: " + appointment1.getDiagnosis());
        System.out.println("Prescription: " + appointment1.getPrescription());
        System.out.println();

        // Test: Cancel an appointment
        System.out.println("--- Cancelling Appointment A002 ---");
        appointment2.cancelAppointment();
        System.out.println("Status: " + appointment2.getStatus());
        System.out.println();

        // Test: Getters and Setters
        System.out.println("--- Testing Getters/Setters ---");
        patient1.setPhoneNumber("+1-555-0000");
        System.out.println("Updated Patient Phone: " + patient1.getPhoneNumber());
        
        doctor1.setAvailable(false);
        System.out.println("Doctor Available: " + doctor1.isAvailable());
        
        appointment1.setStatus("COMPLETED");
        System.out.println("Appointment Status: " + appointment1.getStatus());
        System.out.println();

        facade.guardarBackup("data");

        System.out.println("=== HealthHub System Test Complete ===");
    }
}
