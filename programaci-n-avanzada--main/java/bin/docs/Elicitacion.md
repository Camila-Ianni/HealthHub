# Elicitación de Requisitos - Clínica Health Hub

## Contexto del proyecto
Este trabajo fue solicitado por la clínica Health Hub, que actualmente maneja todos sus turnos y historiales en planillas de cálculo y papel. Nos reunimos con el personal de la clínica para entender qué necesitaban.

## Entrevista con el personal

### Participantes
- Recepcionista (2 años en el puesto)
- Médico clínico (10 años de experiencia en la clínica)
- Administrador (dueño de la clínica)

### ¿Qué problema tienen actualmente?
Al principio no nos dieron mucha bola, pero después de insistir un poco, nos contaron que:
- Se les superponen turnos sin querer (cuando llaman dos personas para el mismo horario)
- Pierden mucho tiempo buscando historiales en papel
- A veces anotan mal los datos y después no encuentran al paciente

**Nota:** En un principio pensamos que era solo un problema de "organización", pero en realidad es un tema de que no tienen nada centralizado.

### ¿Quién hace qué?
Acá tuvimos una duda grande. Al principio pensábamos que el médico podía hacer de todo, pero nos aclararon que:
- El **recepcionista** es el que atiende el teléfono y da los turnos
- El **médico** solo atiende a los pacientes y escribe en el historial
- El **administrador** es el que da de alta a los médicos y define cuándo atienden

**Duda que surgió:** ¿El administrador también puede ser recepcionista? Nos dijeron que sí, pero para el sistema lo vamos a manejar como roles separados.

### ¿Qué datos necesitan?
Para los **pacientes**:
- DNI (único, no se puede repetir)
- Nombre y apellido
- Teléfono
- Obra social

Para los **médicos**:
- Matrícula (también única)
- Nombre y apellido
- Especialidad

**Otra duda:** ¿Un médico puede tener más de una especialidad? Nos dijeron que sí, pero para simplificar el TP vamos a poner solo una. Si da tiempo, lo vemos después.

### ¿Cómo funcionan los turnos?
Esto fue lo más difícil de entender:
1. El paciente llama y pide turno
2. El recepcionista busca cuándo está disponible el médico
3. Si hay lugar, se anota el turno
4. Si no hay lugar, puede esperar que se libere algo

**Importante:** Nos dijeron que a veces necesitan agregar "sobreturnos" cuando es algo urgente. Al principio no entendíamos qué era, pero básicamente es un turno extra que se agrega aunque el médico ya esté lleno.

### ¿Qué pasa con el historial clínico?
Cada paciente tiene un historial que se crea cuando lo registran por primera vez. Solo los médicos pueden escribir en el historial. El recepcionista no puede verlo ni modificarlo.

**Lo que tiene el historial:**
- Fecha de la consulta
- Resumen de lo que habló el médico con el paciente
- Diagnóstico
- Estudios (si mandó a hacer alguno)

### ¿Algo más?
Nos pidieron que si un médico cancela un día (por enfermedad o vacaciones), se avise a todos los pacientes que tenían turno. Esto nos complicó un poco el diseño, pero lo vamos a intentar hacer con notificaciones simples.

## Reglas de negocio que anotamos

Después de hablar con ellos, estas son las reglas que tenemos que respetar:

1. **No se puede registrar un paciente con DNI repetido** - esto es clave para no tener pacientes duplicados
2. **Un médico no puede tener dos turnos a la misma hora** - obvio, pero hay que validarlo
3. **Si un médico cancela una jornada, se cancelan todos los turnos de ese día** - esto nos dio dolor de cabeza
4. **El historial clínico se crea automáticamente cuando se registra un paciente** - así no se olvidan
5. **Cada vez que se modifica un turno, hay que avisar al médico** - por ahora con un mensaje en pantalla alcanza

## Limitaciones que nos pusieron

- Solo van a usar computadoras de la clínica (no hace falta que funcione en celulares)
- No tienen presupuesto para comprar licencias de bases de datos
- Quieren algo simple, que puedan aprender a usar en un día

**Nuestra decisión:** Vamos a usar archivos de texto (.txt) para guardar los datos. Así no hace falta instalar nada y es fácil de probar. Después, si quieren, se puede pasar a una base de datos real.

## Cosas que no vamos a hacer (por ahora)

- Portal para que los pacientes saquen turnos online
- App para el celular
- Que los médicos puedan ver su agenda desde su casa
- Estadísticas ni reportes complejos

Esto lo hablamos con el profesor y dijo que está bien para la primera entrega.

---

*Documento escrito por los alumnos después de la entrevista con la clínica. Fecha: Abril 2026*
