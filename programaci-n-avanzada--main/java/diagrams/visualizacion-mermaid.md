# Visualización de Diagramas Mermaid

## 1) Diagrama Entidad-Relación

```mermaid
erDiagram
    %% Esquema SQL de HealthHub (compatible con MySQL; runtime actual en H2 modo MySQL)
    EMPLEADO {
        string legajo PK
        string nombre
        string rol
    }

    PACIENTE {
        string dni PK
        string nombre
        string apellido
        string telefono
        string obraSocial
    }

    MEDICO {
        string matricula PK
        string nombre
        string apellido
        string especialidad
    }

    DISPONIBILIDAD {
        bigint id PK
        string matricula FK
        int dia
        time hora_inicio
        time hora_fin
    }

    TURNO {
        string id PK
        string dniPaciente FK
        string matriculaMedico FK
        datetime fechaHora
        string estado
        boolean sobreturno
    }

    HISTORIAL_CLINICO {
        string dniPaciente PK, FK
    }

    ENTRADA_HISTORIAL {
        bigint id PK
        string dniPaciente FK
        datetime fecha
        string resumen
        string diagnostico
        string estudios
    }

    NOTIFICACION {
        bigint id PK
        string legajo FK
        string mensaje
    }

    PACIENTE ||--|| HISTORIAL_CLINICO : posee
    HISTORIAL_CLINICO ||--o{ ENTRADA_HISTORIAL : contiene
    MEDICO ||--o{ DISPONIBILIDAD : define
    PACIENTE ||--o{ TURNO : solicita
    MEDICO ||--o{ TURNO : atiende
    EMPLEADO ||--o{ NOTIFICACION : recibe
```

## 2) Diagrama de Casos de Uso

```mermaid
flowchart LR
    R[Recepcionista]
    M[Medico]
    A[Administrador]

    subgraph Sistema[Health Hub]
        L((Inicio de sesion))
        D((Panel principal))
        P((Gestion de pacientes))
        MD((Gestion de medicos))
        T((Gestion de turnos))
        H((Gestion de historiales))
        N((Notificaciones))
        G((Persistencia SQL))
    end

    R --> L
    M --> L
    A --> L

    L --> D
    D --> P
    D --> MD
    D --> T
    D --> H
    D --> N

    R --> P
    R --> T
    R --> H
    R --> N

    A --> P
    A --> MD
    A --> T

    M --> T
    M --> H
    M --> N

    P --> G
    MD --> G
    T --> G
    H --> G
    N --> G

    G --- DB[(H2 modo MySQL / esquema MySQL importable)]
```

## 3) Diagrama de Clases

```mermaid
classDiagram
    class App {
      +main(String[])
    }

    class VentanaLogin {
      -HealthHubContext contexto
      -JTextField campoLegajo
      +main(String[])
    }

    class VentanaPrincipal {
      -HealthHubContext contexto
      -Empleado empleado
    }

    class VentanaGestionGeneral {
      -HealthHubContext contexto
      -String pestanaInicial
    }

    class VentanaAgenda
    class VentanaEstadisticas
    class HealthHubIcons
    class HealthHubSwing
    class HealthHubContext

    class GestorPacientes {
      +registrarPaciente(Paciente)
      +modificarPaciente(String, String, String, String, String)
      +buscarPorDni(String)
      +buscarPorNombreCompleto(String)
      +listarTodos()
    }

    class GestorMedicos {
      +registrarMedico(Medico)
      +agregarDisponibilidad(String, Disponibilidad)
      +consultarDisponibilidad(String)
      +buscarMedico(String)
      +listarTodos()
    }

    class GestorTurnos {
      +crearTurno(String, String, LocalDateTime, boolean)
      +cancelarTurno(String)
      +reprogramarTurno(String, LocalDateTime)
      +marcarAtendido(String)
      +listarTodos()
    }

    class GestorHistoriales {
      +crearHistorialSiNoExiste(String)
      +registrarConsulta(RolUsuario, String, String, String, String)
      +actualizarDiagnostico(String, String)
      +actualizarEstudios(String, String)
      +verHistorial(String)
    }

    class GestorEmpleados {
      +registrarEmpleado(String, String, RolUsuario)
      +buscarPorLegajo(String)
      +listarTodos()
    }

    class GestorNotificaciones {
      +agregarNotificacion(String, String)
      +verNotificaciones(String)
      +limpiarNotificaciones(String)
      +listarTodas()
    }

    class Persistencia {
      +guardarPacientes(List~Paciente~)
      +guardarMedicos(List~Medico~)
      +guardarTurnos(List~Turno~)
      +guardarHistoriales(List~HistorialClinico~)
      +guardarEmpleados(List~Empleado~)
      +guardarNotificaciones(Map~String, List~String~~)
      +cargarPacientes()
      +cargarMedicos()
      +cargarTurnos()
      +cargarHistoriales()
      +cargarEmpleados()
      +cargarNotificaciones()
    }

    class Empleado {
      -String legajo
      -String nombre
      -RolUsuario rol
    }

    class Paciente {
      -String dni
      -String nombre
      -String apellido
      -String telefono
      -String obraSocial
    }

    class Medico {
      -String matricula
      -String nombre
      -String apellido
      -String especialidad
    }

    class Turno {
      -String id
      -String dniPaciente
      -String matriculaMedico
      -LocalDateTime fechaHora
      -EstadoTurno estado
      -boolean sobreturno
    }

    class HistorialClinico {
      -String dniPaciente
      -List~EntradaHistorial~ entradas
      +agregarEntrada(EntradaHistorial)
      +getEntradas()
    }

    class EntradaHistorial {
      -LocalDateTime fecha
      -String resumen
      -String diagnostico
      -String estudios
    }

    class Disponibilidad {
      -DayOfWeek dia
      -LocalTime horaInicio
      -LocalTime horaFin
    }

    App ..> VentanaLogin
    VentanaLogin ..> HealthHubContext
    VentanaPrincipal ..> HealthHubContext
    VentanaGestionGeneral ..> HealthHubContext
    VentanaPrincipal ..> VentanaAgenda
    VentanaPrincipal ..> VentanaEstadisticas
    VentanaPrincipal ..> VentanaGestionGeneral
    HealthHubContext ..> GestorPacientes
    HealthHubContext ..> GestorMedicos
    HealthHubContext ..> GestorTurnos
    HealthHubContext ..> GestorHistoriales
    HealthHubContext ..> GestorEmpleados
    HealthHubContext ..> GestorNotificaciones
    HealthHubContext ..> Persistencia

    GestorPacientes --> Paciente
    GestorMedicos --> Medico
    GestorTurnos --> Turno
    GestorHistoriales --> HistorialClinico
    HistorialClinico --> EntradaHistorial
    Medico --> Disponibilidad
    Empleado --> RolUsuario
    Turno --> EstadoTurno
```
