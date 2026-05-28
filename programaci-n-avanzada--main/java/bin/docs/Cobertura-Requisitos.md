# Cobertura base de requisitos en código

## Módulo Pacientes
- **RFC1:** `PacienteService.registrarPaciente` (valida DNI único).
- **RFC2:** `PacienteService.modificarPaciente` (actualiza por DNI).
- **RFC3:** `PacienteService.buscarPorDni` y `buscarPorNombreCompleto`.
- **RFC4:** `PacienteService.registrarPaciente` crea historial vía `HistorialService.crearHistorialSiNoExiste`.
- **RNFC1:** búsqueda con `HashMap` por DNI + índice por nombre (`indiceNombre`).
- **RNFC2:** guardado automático con `AutoSaveService.guardar` en cada alta/modificación.

## Módulo Médicos
- **RFC5:** `MedicoService.registrarMedico`.
- **RFC6:** `MedicoService.agregarDisponibilidad`.
- **RFC7:** `MedicoService.reemplazarDisponibilidades`.
- **RFC8:** `MedicoService.cancelarJornada` + `TurnoService.cancelarTurnosDeJornada`.
- **RNFC3:** actualización inmediata en memoria (consulta directa post-cambio).
- **RNFC4:** validación de solapamientos en `Disponibilidad.solapaCon` y servicio.

## Módulo Turnos
- **RFC9:** `TurnoService.crearTurno` (turno normal).
- **RFC10:** `TurnoService.cancelarTurno`.
- **RFC11:** `TurnoService.reprogramarTurno`.
- **RFC12:** `TurnoService.crearTurno(..., sobreturno=true)`.
- **RFC13:** `TurnoService.marcarAtendido`.
- **RFC14:** `NotificationService.notificarMedico` disparado al crear/cancelar/reprogramar.
- **RNFC5:** notificaciones automáticas sin acción manual.
- **RNFC6:** bloqueo de duplicados en `TurnoService.existeTurnoDuplicado`.

## Módulo Historial Clínico
- **RFC15:** `HistorialService.verHistorial`.
- **RFC16:** `HistorialService.registrarConsulta`.
- **RFC17:** `HistorialService.actualizarDiagnosticoUltimaEntrada`.
- **RFC18:** `HistorialService.registrarEstudioUltimaEntrada`.
- **RNFC7:** validación de rol médico en métodos de edición del historial.
- **RNFC8:** acceso restringido por rol en edición + backup local en `backup.txt`.

## Requisitos no funcionales globales (base)
- **RNFG1:** app de consola Java para PCs (`App.java`).
- **RNFG2:** operaciones en memoria con estructuras eficientes (`HashMap`, `List`).
- **RNFG3:** persistencia automática básica con `AutoSaveService`.
- **RNFG4:** aviso de horario laboral en `App.informarHorarioLaboral`.
- **RNFG5:** menús por rol simples e intuitivos en `App`.
