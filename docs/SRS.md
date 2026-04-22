# SRS - HealthHub

**Versión:** 1.1  
**Fecha:** Abril 2026  
**Tipo de documento:** Informe académico de requisitos

---

## 1. Contexto y propósito

HealthHub es una aplicación de consola en Java orientada a practicar POO con un caso realista del dominio salud.  
La idea no es competir con un sistema hospitalario completo, sino demostrar diseño limpio, encapsulamiento y flujo de datos consistente entre pacientes, doctores y turnos.

Este SRS resume qué hace el sistema hoy, qué límites tiene y por dónde conviene extenderlo.

---

## 2. Alcance del sistema

### 2.1 Alcance incluido

1. Alta y consulta de pacientes.
2. Alta y consulta de doctores.
3. Programación y seguimiento de turnos.
4. Cambio de estado de turnos (agendado, completado, cancelado).
5. Persistencia básica en archivos de texto por entidad:
   - `pacientes_data.txt`
   - `doctores_data.txt`
   - `turnos_data.txt`

### 2.2 Fuera de alcance (por ahora)

1. Base de datos relacional.
2. Interfaz web o móvil.
3. Control de concurrencia multiusuario.
4. Seguridad fuerte (hash de contraseñas, permisos por sesión).
5. Integraciones externas (laboratorios, obras sociales, email).

---

## 3. Actores y uso esperado

### 3.1 Actor estudiante/desarrollador
Corre el sistema, registra datos de prueba y verifica el comportamiento de los objetos.

### 3.2 Actor docente/evaluador
Revisa calidad de diseño orientado a objetos, legibilidad y consistencia del flujo funcional.

### 3.3 Actor usuario final (simulado)
Representado en los casos de uso de consola: agenda turnos y consulta datos básicos.

---

## 4. Requisitos funcionales

### RF-01 Gestión de pacientes
- El sistema debe permitir registrar pacientes con datos personales y médicos básicos.
- Debe permitir buscar por `patientId`.
- Debe listar los pacientes disponibles en memoria.

### RF-02 Gestión de doctores
- El sistema debe permitir registrar doctores con especialidad, matrícula y disponibilidad.
- Debe permitir buscar por `doctorId`.
- Debe listar doctores disponibles en memoria.

### RF-03 Gestión de turnos
- El sistema debe crear un turno vinculando paciente y doctor existentes.
- Debe guardar fecha, hora y motivo.
- Debe permitir marcar un turno como completado con diagnóstico y prescripción.
- Debe permitir cancelar un turno.

### RF-04 Persistencia de respaldo
- El sistema debe guardar la información en archivos de texto separados por entidad.
- El sistema debe poder reconstruir los objetos desde esos archivos.
- La carga debe ser tolerante a líneas vacías y entradas incompletas.

---

## 5. Requisitos no funcionales

### RNF-01 Mantenibilidad
- Código organizado por responsabilidades (`GestorPacientes`, `GestorDoctores`, `GestorTurnos`, `HealthHubFacade`).
- Métodos auxiliares privados para validaciones puntuales.
- Nombres claros y consistentes con el dominio.

### RNF-02 Robustez básica
- Validación de IDs vacíos o nulos antes de operar.
- Rechazo explícito de duplicados en altas.
- Manejo explícito de errores de I/O en la fachada.

### RNF-03 Portabilidad
- Java estándar sin frameworks externos.
- Ejecución esperada en Windows, Linux y macOS con JDK 8+.

### RNF-04 Legibilidad académica
- Comentarios puntuales que expliquen decisiones de diseño.
- Estructura que priorice claridad por encima de optimizaciones tempranas.

---

## 6. Modelo de datos (resumen)

### User
Base con `id`, `username`, `password`, `email`, `role`.

### Patient (extiende User)
`patientId`, nombre, fecha de nacimiento, género, teléfono, dirección, historial médico.

### Doctor (extiende User)
`doctorId`, nombre, especialidad, matrícula, teléfono, años de experiencia, departamento, disponibilidad.

### Appointment
`appointmentId`, paciente, doctor, fecha, hora, estado, motivo, diagnóstico, prescripción.

---

## 7. Casos de uso resumidos

### CU-01 Registrar paciente
**Precondición:** datos mínimos válidos.  
**Flujo:** se crea y se agrega al gestor de pacientes.  
**Resultado:** paciente disponible para turnos.

### CU-02 Registrar doctor
**Precondición:** datos mínimos válidos.  
**Flujo:** se crea y se agrega al gestor de doctores.  
**Resultado:** doctor disponible para turnos.

### CU-03 Programar turno
**Precondición:** paciente y doctor existentes.  
**Flujo:** la fachada resuelve entidades y registra el turno.  
**Resultado:** turno en estado `SCHEDULED`.

### CU-04 Completar turno
**Precondición:** turno existente.  
**Flujo:** se cargan diagnóstico y prescripción.  
**Resultado:** turno en estado `COMPLETED`.

### CU-05 Cancelar turno
**Precondición:** turno existente.  
**Flujo:** se marca cancelación.  
**Resultado:** turno en estado `CANCELLED`.

### CU-06 Guardar/Cargar respaldo
**Precondición:** ruta accesible.  
**Flujo:** serialización manual por entidades y parseo iterativo al cargar.  
**Resultado:** estado recuperable entre ejecuciones.

---

## 8. Limitaciones conocidas

1. El formato de archivos es simple y pensado para aprendizaje; no es ideal para alto volumen.
2. No hay cifrado ni hash de contraseñas.
3. No hay validación de reglas médicas reales (por ejemplo, superposición de turnos).
4. No hay separación por capas de infraestructura avanzada.

---

## 9. Mejoras futuras propuestas

1. Migrar persistencia a base de datos con repositorios.
2. Incorporar validación de agenda (choques de horarios).
3. Añadir autenticación real y gestión de sesiones.
4. Exponer la lógica mediante API REST.
5. Agregar pruebas unitarias automatizadas por gestor y por fachada.

---

## 10. Cierre

El sistema cumple bien como proyecto académico: demuestra modelado OO, coordinación entre clases y persistencia elemental.  
Todavía tiene límites claros, pero justamente esos límites dejan trazada una hoja de ruta concreta para evolucionarlo sin rehacer todo desde cero.
