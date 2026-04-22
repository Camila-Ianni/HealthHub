# HealthHub - Java OOP Healthcare System

## Overview
HealthHub is a simple healthcare management system built using Java OOP principles. It manages patients, doctors, and appointments without using databases or frameworks.

## Project Structure
```
HealthHub/
└── src/
    ├── User.java         - Base user class
    ├── Patient.java      - Patient class (extends User)
    ├── Doctor.java       - Doctor class (extends User)
    ├── Appointment.java  - Appointment management
    └── Main.java         - Test application
```

## Classes

### 1. User (Base Class)
**Private Attributes:**
- id
- username
- password
- email
- role

**Features:**
- Default and parameterized constructors
- Getters and setters for all attributes
- toString() override

### 2. Patient (extends User)
**Private Attributes:**
- patientId
- firstName, lastName
- dateOfBirth
- gender
- phoneNumber
- address
- medicalHistory

**Features:**
- Inherits from User
- Full CRUD getters/setters
- getFullName() helper method
- Role automatically set to "PATIENT"

### 3. Doctor (extends User)
**Private Attributes:**
- doctorId
- firstName, lastName
- specialization
- licenseNumber
- phoneNumber
- yearsOfExperience
- department
- isAvailable

**Features:**
- Inherits from User
- Full CRUD getters/setters
- getFullName() returns "Dr. FirstName LastName"
- Role automatically set to "DOCTOR"

### 4. Appointment
**Private Attributes:**
- appointmentId
- patient (Patient object)
- doctor (Doctor object)
- appointmentDate
- appointmentTime
- status
- reason
- diagnosis
- prescription

**Features:**
- Manages appointment lifecycle
- completeAppointment() method
- cancelAppointment() method
- Status tracking (SCHEDULED, COMPLETED, CANCELLED)

## How to Compile and Run

### Compile:
```bash
cd HealthHub/src
javac *.java
```

### Run:
```bash
java Main
```

## Sample Output
```
=== HealthHub System ===

--- Registered Patients ---
Patient{patientId='P001', name='John Doe', dob='1990-05-15', gender='Male', phone='+1-555-1234'}

--- Available Doctors ---
Doctor{doctorId='D001', name='Dr. Robert Johnson', specialization='Cardiology', department='Cardiology', experience=15 years, available=true}

--- Scheduled Appointments ---
Appointment{id='A001', patient=John Doe, doctor=Dr. Robert Johnson, date='2026-04-01', time='10:00 AM', status='SCHEDULED', reason='Annual checkup'}

=== HealthHub System Test Complete ===
```

## OOP Principles Applied
✅ **Encapsulation** - Private attributes with public getters/setters
✅ **Inheritance** - Patient and Doctor extend User
✅ **Polymorphism** - toString() overriding
✅ **Abstraction** - Clean interfaces through public methods

## Requirements Met
✅ All classes have private attributes
✅ Constructors (default and parameterized) included
✅ Getters and setters for all attributes
✅ Main class tests basic functionality
✅ No database or frameworks used

## Author
Created for HealthHub healthcare system demonstration.
