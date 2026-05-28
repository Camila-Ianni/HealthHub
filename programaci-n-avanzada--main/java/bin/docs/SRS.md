# SRS - Sistema de Gestión Clínica Health Hub

## 1. Introducción

### 1.1 Propósito
Este documento describe los requisitos del sistema que vamos a desarrollar para la clínica Health Hub. La idea es reemplazar las planillas en papel y Excel que usan actualmente por un sistema más organizado.

### 1.2 Alcance
El sistema permite:
- Registrar pacientes y médicos
- Gestionar turnos (crear, cancelar, reprogramar)
- Llevar el historial clínico de cada paciente
- Notificar cambios en los turnos

**Lo que NO hace:**
- No tiene portal para pacientes
- No funciona en celulares
- No saca estadísticas ni reportes

### 1.3 Definiciones y acrónimos
| Término | Significado |
|---------|-------------|
| DNI | Documento Nacional de Identidad |
| RFC | Requisito Funcional del Cliente |
| RNF | Requisito No Funcional |
| OS | Obra Social |

## 2. Descripción general

### 2.1 Roles del sistema
El sistema tiene tres tipos de usuarios:

**Recepcionista:**
- Registra pacientes nuevos
- Modifica datos de pacientes
- Busca pacientes por DNI o nombre
- Crea, cancela y reprograma turnos
- Consulta disponibilidad de médicos
- Registra sobreturnos (para urgencias)

**Médico:**
- Ve los turnos que tiene asignados
- Consulta el historial clínico de sus pacientes
- Registra consultas en el historial
- Actualiza diagnósticos
- Registra estudios médicos
- Marca turnos como "atendidos"
- Ve notificaciones de cambios

**Administrador:**
- Registra médicos nuevos
- Define los horarios de atención de cada médico
- Registra empleados del sistema

### 2.2 Supuestos
- El sistema se usa solo en las computadoras de la clínica
- Solo personal autorizado puede acceder
- Los datos se guardan en archivos de texto (.txt) para esta entrega
- No hace falta login con contraseña (esto lo vimos con el profesor)

## 3. Requisitos funcionales

### 3.1 Módulo de Pacientes

**RFC1 - Registrar paciente**
El recepcionista puede registrar un paciente nuevo con: DNI, nombre, apellido, teléfono y obra social.
- *Precondición:* El DNI no debe estar ya registrado
- *Postcondición:* Se crea el paciente y su historial clínico vacío

**RFC2 - Modificar paciente**
El recepcionista puede modificar los datos de un paciente existente buscándolo por DNI.
- *Precondición:* El paciente debe existir
- *Postcondición:* Los datos del paciente se actualizan

**RFC3 - Buscar paciente**
El recepcionista puede buscar pacientes por DNI o por nombre y apellido (búsqueda parcial).
- *Precondición:* Ninguna
- *Postcondición:* Se muestran los pacientes que coinciden

### 3.2 Módulo de Médicos

**RFC4 - Registrar médico**
El administrador puede registrar un médico con: matrícula, nombre, apellido y especialidad.
- *Precondición:* La matrícula no debe estar ya registrada
- *Postcondición:* El médico queda disponible para asignar turnos

**RFC5 - Gestionar disponibilidad**
El administrador puede definir y modificar los horarios de atención de cada médico.
- *Precondición:* El médico debe existir
- *Postcondición:* Se actualiza la disponibilidad del médico

**RFC6 - Cancelar jornada**
El administrador puede cancelar toda una jornada de un médico (por enfermedad o vacaciones).
- *Precondición:* El médico debe existir
- *Postcondición:* Se cancelan todos los turnos de esa fecha y se notifica a los pacientes

### 3.3 Módulo de Turnos

**RFC7 - Crear turno**
El recepcionista puede crear un turno para un paciente con un médico en una fecha y hora específica.
- *Precondición:* El médico debe tener disponibilidad en ese horario y no tener otro turno asignado
- *Postcondición:* Se crea el turno y se notifica al médico

**RFC8 - Cancelar turno**
El recepcionista puede cancelar un turno existente.
- *Precondición:* El turno debe existir y no estar ya cancelado
- *Postcondición:* El turno se marca como cancelado y se libera el horario

**RFC9 - Reprogramar turno**
El recepcionista puede cambiar la fecha y hora de un turno existente.
- *Precondición:* El turno debe existir y el nuevo horario debe estar disponible
- *Postcondición:* El turno se actualiza con la nueva fecha/hora

**RFC10 - Registrar sobreturno**
El recepcionista puede registrar un turno extra aunque el médico ya esté lleno.
- *Precondición:* Solo para casos urgentes (esto no lo validamos, es confianza)
- *Postcondición:* Se crea el turno marcado como "sobreturno"

### 3.4 Módulo de Historial Clínico

**RFC11 - Ver historial clínico**
El médico puede ver el historial completo de un paciente.
- *Precondición:* Solo los médicos pueden acceder
- *Postcondición:* Se muestran todas las entradas del historial

**RFC12 - Registrar consulta**
El médico puede agregar una nueva entrada al historial con: resumen, diagnóstico y estudios.
- *Precondición:* El paciente debe tener historial clínico
- *Postcondición:* Se agrega una nueva entrada al historial

**RFC13 - Actualizar diagnóstico**
El médico puede modificar el diagnóstico de la última consulta registrada.
- *Precondición:* Debe existir al menos una entrada en el historial
- *Postcondición:* Se actualiza el diagnóstico de la última entrada

**RFC14 - Registrar estudio**
El médico puede agregar estudios médicos a la última entrada del historial.
- *Precondición:* Debe existir al menos una entrada en el historial
- *Postcondición:* Se actualizan los estudios de la última entrada

## 4. Requisitos no funcionales

### 4.1 Técnicos

**RNF1 - Plataforma**
El sistema debe funcionar en las computadoras de la clínica (Windows, Linux o Mac).
- *Justificación:* No tienen equipos especializados

**RNF2 - Persistencia**
Los datos se guardan en archivos de texto (.txt) para esta entrega.
- *Limitación:* Es temporal, después se puede pasar a base de datos
- *Ventaja:* No hace falta instalar nada extra

**RNF3 - Tiempo de respuesta**
Las operaciones comunes (buscar paciente, crear turno) deben tardar menos de 3 segundos.
- *Motivo:* Para que el recepcionista no haga esperar al paciente por teléfono

### 4.2 De usabilidad

**RNF4 - Interfaz simple**
El sistema debe ser fácil de usar, con menús claros.
- *Meta:* Que el personal aprenda a usarlo en un día

**RNF5 - Idioma**
Toda la interfaz debe estar en español.
- *Motivo:* El personal no maneja bien el inglés técnico

### 4.3 Limitaciones del proyecto

**L1 - Sin base de datos real**
Por ahora usamos archivos .txt porque es más fácil para probar. Esto tiene sus problemas:
- No hay transacciones (si se corta la luz a mitad del guardado, se puede perder data)
- Es más lento si hay muchos registros
- No hay backup automático

**L2 - Solo una terminal**
El sistema corre en una sola computadora. No se puede usar desde múltiples PCs a la vez.
- *Problema:* Si el recepcionista está usando el sistema, el médico no puede entrar
- *Solución futura:* Poner el sistema en red con una base de datos

**L3 - Sin validaciones complejas**
No validamos todos los datos que ingresa el usuario (por ejemplo, que el teléfono tenga el formato correcto).
- *Motivo:* Es la primera entrega, priorizamos lo funcional

**L4 - Sin autenticación**
No hay login con usuario y contraseña. Cualquiera que abre la terminal puede usar el sistema.
- *Riesgo:* No hay control de quién hizo qué
- *Nota:* Esto lo vamos a mejorar en próximas entregas si el profesor lo pide

## 5. Prototipos de interfaz

### 5.1 Menú principal
```
=== HEALTH HUB - Sistema de Gestion Clinica ===
1. Acceso Recepcionista
2. Acceso Medico
3. Acceso Administrador
0. Salir
```

### 5.2 Menú Recepcionista
```
--- MENU RECEPCIONISTA ---
1. Registrar paciente
2. Modificar paciente por DNI
3. Buscar paciente por DNI
4. Buscar paciente por Nombre y Apellido
5. Crear turno
6. Cancelar turno
7. Reprogramar turno
8. Consultar disponibilidad de medico
9. Registrar sobreturno
0. Volver
```

### 5.3 Menú Médico
```
--- MENU MEDICO ---
1. Consultar turnos asignados
2. Visualizar historial clinico
3. Registrar consulta medica
4. Actualizar diagnostico (ultima entrada)
5. Registrar estudio (ultima entrada)
6. Marcar turno como atendido
7. Cancelar jornada
8. Ver notificaciones
0. Volver
```

### 5.4 Menú Administrador
```
--- MENU ADMINISTRADOR ---
1. Registrar medico
2. Agregar disponibilidad de medico
3. Reemplazar disponibilidades de medico
4. Registrar empleado
0. Volver
```

## 6. Consideraciones finales

Este documento es la base para el desarrollo del sistema. Sabemos que hay cosas que se pueden mejorar, pero para la primera entrega nos enfocamos en cumplir con estos requisitos.

**Cosas que nos gustaría agregar si da el tiempo:**
- Backup automático de los archivos
- Validación de formatos (DNI, teléfono, matrícula)
- Que se pueda buscar pacientes solo con una parte del nombre

---

*Documento elaborado por los alumnos para la Primera Entrega de Programación Avanzada.
Clínica Health Hub - Abril 2026*
