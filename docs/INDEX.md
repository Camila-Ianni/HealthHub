# HealthHub Documentation Index

## 📚 Documentation Files

### 1. SRS.md - Software Requirements Specification
**Purpose:** Complete system requirements  
**Sections:**
- Introduction and Scope
- Product Functions
- System Features (User, Patient, Doctor, Appointment Management)
- External Interface Requirements
- Nonfunctional Requirements
- Use Cases

**Read this:** To understand what the system does and why

---

### 2. Elicitacion.md - Levantamiento de Requisitos
**Purpose:** Explica cómo se definió el alcance del sistema  
**Incluye:**
- Fuente de requisitos y supuestos
- Decisiones de alcance
- Riesgos conocidos
- Criterios de aceptación académicos

**Read this:** Para entender por qué el sistema está recortado de esa forma

---

### 3. DIAGRAMS.md - System Diagrams
**Purpose:** Visual architecture and design  
**Includes:**
- Entity-Relationship Diagram (DER)
- UML Class Diagram
- Use Case Diagram
- Sequence Diagrams
- State Diagram
- Component Diagram

**Read this:** To visualize system structure

---

### 4. SKILLS.md - Java & OOP Skills Reference
**Purpose:** Educational guide to Java OOP concepts  
**Topics:**
- Java Fundamentals
- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Best Practices
- Code Quality Metrics

**Read this:** To learn OOP principles used in this project

---

### 5. COMPILE_AND_RUN.md - User Guide
**Purpose:** How to compile and run the project  
**Contents:**
- System Requirements
- Compilation Instructions
- Execution Steps
- Troubleshooting
- Creating JAR files
- IDE Setup

**Read this:** To get the project running

---

## 🗂️ Documentation Organization

```
HealthHub/
├── README.md              # Project overview
├── .gitignore             # Git ignore rules
├── docs/                  # All documentation (this folder)
│   ├── INDEX.md           # This file
│   ├── SRS.md             # Requirements
│   ├── Elicitacion.md     # Requirement elicitation notes
│   ├── DIAGRAMS.md        # Visual diagrams
│   ├── SKILLS.md          # OOP concepts
│   └── COMPILE_AND_RUN.md # User guide
└── src/                   # Source code
    ├── User.java
    ├── Patient.java
    ├── Doctor.java
    ├── Appointment.java
    ├── GestorPacientes.java
    ├── GestorDoctores.java
    ├── GestorTurnos.java
    ├── HealthHubFacade.java
    └── Main.java
```

---

## 📖 Reading Order for Different Audiences

### For Developers
1. README.md (project overview)
2. COMPILE_AND_RUN.md (get it running)
3. DIAGRAMS.md (understand architecture)
4. SRS.md (understand requirements)
5. SKILLS.md (learn OOP concepts)

### For Students Learning OOP
1. README.md (what is this?)
2. SKILLS.md (learn concepts)
3. DIAGRAMS.md (see how it's structured)
4. Source code in src/ (read the code)
5. COMPILE_AND_RUN.md (try it yourself)

### For Project Managers
1. README.md (what does it do?)
2. SRS.md (what are the requirements?)
3. DIAGRAMS.md (how is it built?)

### For QA/Testers
1. SRS.md (what should it do?)
2. COMPILE_AND_RUN.md (how to run it)
3. DIAGRAMS.md (expected behavior flows)

---

## 🔗 Quick Links

| Document | Size | Purpose |
|----------|------|---------|
| [SRS.md](SRS.md) | ~8KB | Requirements |
| [Elicitacion.md](Elicitacion.md) | ~4KB | Scope and requirement decisions |
| [DIAGRAMS.md](DIAGRAMS.md) | ~10KB | Architecture |
| [SKILLS.md](SKILLS.md) | ~8KB | Learning |
| [COMPILE_AND_RUN.md](COMPILE_AND_RUN.md) | ~5.5KB | Usage |

---

## 📝 Notes

- All diagrams use **Mermaid** syntax for GitHub rendering
- Code examples in documentation are extracted from actual source
- Documentation follows ISO/IEC 25010 quality standards
- Last updated: March 28, 2026

---

**For questions or issues, refer to the appropriate document above.**
