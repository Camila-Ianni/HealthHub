# HealthHub - Diagramas del Sistema

## Diagrama Entidad-Relación (DER)

```mermaid
erDiagram
    USER ||--o{ PATIENT : "is-a"
    USER ||--o{ DOCTOR : "is-a"
    PATIENT ||--o{ APPOINTMENT : "has"
    DOCTOR ||--o{ APPOINTMENT : "attends"
    
    USER {
        string id PK
        string username
        string password
        string email
        string role
    }
    
    PATIENT {
        string patientId PK
        string userId FK
        string firstName
        string lastName
        string dateOfBirth
        string gender
        string phoneNumber
        string address
        string medicalHistory
    }
    
    DOCTOR {
        string doctorId PK
        string userId FK
        string firstName
        string lastName
        string specialization
        string licenseNumber
        string phoneNumber
        int yearsOfExperience
        string department
        boolean isAvailable
    }
    
    APPOINTMENT {
        string appointmentId PK
        string patientId FK
        string doctorId FK
        string appointmentDate
        string appointmentTime
        string status
        string reason
        string diagnosis
        string prescription
    }
```

---

## Diagrama de Clases (UML)

```mermaid
classDiagram
    class User {
        -String id
        -String username
        -String password
        -String email
        -String role
        +User()
        +User(id, username, password, email, role)
        +getId() String
        +setId(id) void
        +getUsername() String
        +setUsername(username) void
        +getPassword() String
        +setPassword(password) void
        +getEmail() String
        +setEmail(email) void
        +getRole() String
        +setRole(role) void
        +toString() String
    }
    
    class Patient {
        -String patientId
        -String firstName
        -String lastName
        -String dateOfBirth
        -String gender
        -String phoneNumber
        -String address
        -String medicalHistory
        +Patient()
        +Patient(id, username, password, email, patientId, firstName, lastName, dob, gender, phone, address, history)
        +getPatientId() String
        +setPatientId(id) void
        +getFirstName() String
        +setFirstName(name) void
        +getLastName() String
        +setLastName(name) void
        +getDateOfBirth() String
        +setDateOfBirth(dob) void
        +getGender() String
        +setGender(gender) void
        +getPhoneNumber() String
        +setPhoneNumber(phone) void
        +getAddress() String
        +setAddress(address) void
        +getMedicalHistory() String
        +setMedicalHistory(history) void
        +getFullName() String
        +toString() String
    }
    
    class Doctor {
        -String doctorId
        -String firstName
        -String lastName
        -String specialization
        -String licenseNumber
        -String phoneNumber
        -int yearsOfExperience
        -String department
        -boolean isAvailable
        +Doctor()
        +Doctor(id, username, password, email, doctorId, firstName, lastName, specialization, license, phone, years, department)
        +getDoctorId() String
        +setDoctorId(id) void
        +getFirstName() String
        +setFirstName(name) void
        +getLastName() String
        +setLastName(name) void
        +getSpecialization() String
        +setSpecialization(spec) void
        +getLicenseNumber() String
        +setLicenseNumber(license) void
        +getPhoneNumber() String
        +setPhoneNumber(phone) void
        +getYearsOfExperience() int
        +setYearsOfExperience(years) void
        +getDepartment() String
        +setDepartment(dept) void
        +isAvailable() boolean
        +setAvailable(available) void
        +getFullName() String
        +toString() String
    }
    
    class Appointment {
        -String appointmentId
        -Patient patient
        -Doctor doctor
        -String appointmentDate
        -String appointmentTime
        -String status
        -String reason
        -String diagnosis
        -String prescription
        +Appointment()
        +Appointment(id, patient, doctor, date, time, reason)
        +getAppointmentId() String
        +setAppointmentId(id) void
        +getPatient() Patient
        +setPatient(patient) void
        +getDoctor() Doctor
        +setDoctor(doctor) void
        +getAppointmentDate() String
        +setAppointmentDate(date) void
        +getAppointmentTime() String
        +setAppointmentTime(time) void
        +getStatus() String
        +setStatus(status) void
        +getReason() String
        +setReason(reason) void
        +getDiagnosis() String
        +setDiagnosis(diagnosis) void
        +getPrescription() String
        +setPrescription(prescription) void
        +completeAppointment(diagnosis, prescription) void
        +cancelAppointment() void
        +toString() String
    }
    
    User <|-- Patient : extends
    User <|-- Doctor : extends
    Appointment "0..*" --> "1" Patient : has
    Appointment "0..*" --> "1" Doctor : attends
```

---

## Diagrama de Casos de Uso

```mermaid
graph TB
    subgraph "HealthHub System"
        UC1[Schedule Appointment]
        UC2[Complete Appointment]
        UC3[Cancel Appointment]
        UC4[Register Patient]
        UC5[Update Patient Info]
        UC6[Register Doctor]
        UC7[Update Doctor Availability]
        UC8[View Appointments]
    end
    
    Patient((Patient))
    Doctor((Doctor))
    Admin((Admin))
    
    Patient --> UC1
    Patient --> UC3
    Patient --> UC5
    Patient --> UC8
    
    Doctor --> UC2
    Doctor --> UC3
    Doctor --> UC7
    Doctor --> UC8
    
    Admin --> UC4
    Admin --> UC6
```

---

## Diagrama de Secuencia - Agendar Cita

```mermaid
sequenceDiagram
    participant P as Patient
    participant A as Appointment
    participant D as Doctor
    
    P->>A: new Appointment(id, patient, doctor, date, time, reason)
    A->>A: set appointmentId
    A->>A: set patient reference
    A->>A: set doctor reference
    A->>A: set appointmentDate
    A->>A: set appointmentTime
    A->>A: set reason
    A->>A: set status = "SCHEDULED"
    A-->>P: Appointment created
    P->>A: getAppointmentId()
    A-->>P: return appointmentId
```

---

## Diagrama de Secuencia - Completar Cita

```mermaid
sequenceDiagram
    participant D as Doctor
    participant A as Appointment
    participant P as Patient
    
    D->>A: completeAppointment(diagnosis, prescription)
    A->>A: setDiagnosis(diagnosis)
    A->>A: setPrescription(prescription)
    A->>A: setStatus("COMPLETED")
    A-->>D: Appointment completed
    P->>A: getStatus()
    A-->>P: return "COMPLETED"
    P->>A: getDiagnosis()
    A-->>P: return diagnosis
    P->>A: getPrescription()
    A-->>P: return prescription
```

---

## Diagrama de Estados - Appointment

```mermaid
stateDiagram-v2
    [*] --> SCHEDULED : Create Appointment
    SCHEDULED --> COMPLETED : completeAppointment()
    SCHEDULED --> CANCELLED : cancelAppointment()
    COMPLETED --> [*]
    CANCELLED --> [*]
    
    note right of SCHEDULED
        Initial state when
        appointment is created
    end note
    
    note right of COMPLETED
        Doctor has finished
        consultation
    end note
    
    note right of CANCELLED
        Patient or Doctor
        cancelled
    end note
```

---

## Diagrama de Objetos - Ejemplo en Runtime

```mermaid
graph LR
    subgraph "User Objects"
        U1[User: U001<br/>role: PATIENT]
        U2[User: U003<br/>role: DOCTOR]
    end
    
    subgraph "Patient Object"
        P1[Patient: P001<br/>John Doe<br/>dob: 1990-05-15]
    end
    
    subgraph "Doctor Object"
        D1[Doctor: D001<br/>Dr. Robert Johnson<br/>Cardiology]
    end
    
    subgraph "Appointment Object"
        A1[Appointment: A001<br/>date: 2026-04-01<br/>status: SCHEDULED]
    end
    
    U1 -.inherits.- P1
    U2 -.inherits.- D1
    P1 --> A1
    D1 --> A1
```

---

## Diagrama de Componentes

```mermaid
graph TB
    subgraph "HealthHub Application"
        Main[Main.java<br/>Entry Point]
        
        subgraph "User Management"
            User[User.java<br/>Base Class]
            Patient[Patient.java<br/>Patient Model]
            Doctor[Doctor.java<br/>Doctor Model]
        end
        
        subgraph "Appointment Management"
            Appt[Appointment.java<br/>Appointment Model]
        end
    end
    
    Main --> User
    Main --> Patient
    Main --> Doctor
    Main --> Appt
    
    Patient -.extends.-> User
    Doctor -.extends.-> User
    Appt --> Patient
    Appt --> Doctor
```

---

## Cardinalidades del Sistema

| Relación | Cardinalidad | Descripción |
|----------|--------------|-------------|
| User → Patient | 1:0..1 | Un usuario puede ser 0 o 1 paciente |
| User → Doctor | 1:0..1 | Un usuario puede ser 0 o 1 doctor |
| Patient → Appointment | 1:N | Un paciente puede tener muchas citas |
| Doctor → Appointment | 1:N | Un doctor puede atender muchas citas |
| Appointment → Patient | N:1 | Muchas citas pertenecen a un paciente |
| Appointment → Doctor | N:1 | Muchas citas son atendidas por un doctor |

---

## Restricciones de Integridad

1. **User.id** debe ser único
2. **Patient.patientId** debe ser único
3. **Doctor.doctorId** debe ser único
4. **Appointment.appointmentId** debe ser único
5. **Appointment.patient** no puede ser null
6. **Appointment.doctor** no puede ser null
7. **Doctor.licenseNumber** debe ser único
8. **Appointment.status** solo puede ser: SCHEDULED, COMPLETED, CANCELLED

---

**Fin de Diagramas**
