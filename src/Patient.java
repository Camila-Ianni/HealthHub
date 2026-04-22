/**
 * Patient class
 * Extends User - represents a patient in the healthcare system
 */
public class Patient extends User {
    // Patient-specific attributes
    private String patientId;        // Unique patient ID
    private String firstName;        // Patient first name
    private String lastName;         // Patient last name
    private String dateOfBirth;      // Birth date (format: YYYY-MM-DD)
    private String gender;           // Gender (Male/Female/Other)
    private String phoneNumber;      // Contact phone
    private String address;          // Home address
    private String medicalHistory;   // Medical history summary

    // Default constructor
    public Patient() {
        super();
    }

    // Parameterized constructor
    public Patient(String id, String username, String password, String email, 
                   String patientId, String firstName, String lastName, 
                   String dateOfBirth, String gender, String phoneNumber, 
                   String address, String medicalHistory) {
        super(id, username, password, email, "PATIENT");  // Call parent constructor
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    // Helper method - returns full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Override toString for readable output
    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", dob='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phoneNumber + '\'' +
                '}';
    }
}
