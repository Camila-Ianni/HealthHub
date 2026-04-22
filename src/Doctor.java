/**
 * Doctor class
 * Extends User - represents a doctor in the healthcare system
 */
public class Doctor extends User {
    // Doctor-specific attributes
    private String doctorId;             // Unique doctor ID
    private String firstName;            // Doctor first name
    private String lastName;             // Doctor last name
    private String specialization;       // Medical specialization
    private String licenseNumber;        // Medical license number
    private String phoneNumber;          // Contact phone
    private int yearsOfExperience;       // Years in practice
    private String department;           // Hospital department
    private boolean isAvailable;         // Availability status

    // Default constructor
    public Doctor() {
        super();
    }

    // Parameterized constructor
    public Doctor(String id, String username, String password, String email,
                  String doctorId, String firstName, String lastName,
                  String specialization, String licenseNumber, String phoneNumber,
                  int yearsOfExperience, String department) {
        super(id, username, password, email, "DOCTOR");  // Call parent constructor
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.department = department;
        this.isAvailable = true;  // Default: available
    }

    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Helper method - returns full name with "Dr." prefix
    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }

    // Override toString for readable output
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId='" + doctorId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", department='" + department + '\'' +
                ", experience=" + yearsOfExperience + " years" +
                ", available=" + isAvailable +
                '}';
    }
}
