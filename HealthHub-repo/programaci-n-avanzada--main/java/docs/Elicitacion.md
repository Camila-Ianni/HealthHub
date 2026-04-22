# Elicitación - Clínica Health Hub

## Entrevista resumida

### Objetivo de la entrevista
Levantar necesidades operativas de recepcionista, médico y administrador para reemplazar planillas manuales por un sistema centralizado.

### Preguntas clave y respuestas
1. **¿Cuál es el principal problema actual?**
   - Se generan errores de carga, superposición de turnos y demora para ubicar historiales.

2. **¿Quién crea y modifica turnos?**
   - Principalmente el recepcionista; el médico puede informar cancelaciones/demoras.

3. **¿Qué dato identifica al paciente?**
   - DNI único, obligatorio al registrar.

4. **¿Qué necesita ver el médico al atender?**
   - Agenda del día, historial clínico, diagnósticos previos y estudios.

5. **¿Qué acciones debe poder hacer el administrador?**
   - Registrar médicos y definir/modificar disponibilidad laboral.

6. **¿Cómo se manejan excepciones de agenda?**
   - Se permiten sobreturnos para casos urgentes.

7. **¿Qué nivel de notificación se espera?**
   - Notificación automática al médico al crear, cancelar o reprogramar turnos.

8. **¿Quién puede editar historial clínico?**
   - Solo el médico.

9. **¿Qué restricciones técnicas tiene la clínica?**
   - Uso en PCs internas, sin acceso móvil ni portal de pacientes.

10. **¿Qué desempeño mínimo esperan?**
    - Operaciones comunes en menos de 5 segundos y búsqueda de pacientes en menos de 3 segundos.

## Reglas de negocio detectadas
- No se registra paciente con DNI repetido.
- No se permiten dos turnos del mismo médico en la misma franja.
- Cancelar jornada médica implica cancelar turnos de esa jornada.
- Historial clínico se crea junto con el alta del paciente.
- Toda modificación de turno dispara notificación.
