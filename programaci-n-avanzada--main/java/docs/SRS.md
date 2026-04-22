# SRS - Sistema de Gestión de Clínica Health Hub

## Tabla de contenidos
1. [Objetivo](#1-objetivo)
2. [Beneficios](#2-beneficios)
3. [Alcance](#3-alcance)
4. [Limitaciones](#4-limitaciones)
5. [Requisitos no funcionales globales](#5-requisitos-no-funcionales-globales)
   - [5.1 Módulo Gestión de Pacientes](#51-módulo-gestión-de-pacientes)
   - [5.2 Módulo Gestión de Médicos](#52-módulo-gestión-de-médicos)
   - [5.3 Módulo Gestión de Turnos](#53-módulo-gestión-de-turnos)
   - [5.4 Módulo Historial Clínico](#54-módulo-historial-clínico)
6. [Prototipos de interfaz](#6-prototipos-de-interfaz)
7. [Glosario](#7-glosario)

## 1. Objetivo
Desarrollar un sistema para gestionar turnos médicos e historiales clínicos de la clínica **Health Hub**, reemplazando el esquema manual en planillas, mejorando trazabilidad, tiempos de respuesta y control operativo.

## 2. Beneficios
- Automatización de asignación, cancelación y reprogramación de turnos.
- Reducción de errores por carga manual.
- Mejor organización de agenda médica y disponibilidad.
- Acceso rápido al historial clínico del paciente.
- Notificaciones automáticas ante cambios de turnos.
- Optimización de tareas del recepcionista y administrador.

## 3. Alcance
### Roles del sistema
- **Recepcionista**
  - Registrar, buscar y modificar pacientes.
  - Crear, cancelar y reprogramar turnos.
  - Consultar disponibilidad médica.
- **Médico**
  - Consultar turnos asignados.
  - Visualizar y actualizar historial clínico.
  - Marcar turnos como atendidos.
  - Informar cancelaciones o demoras.
- **Administrador**
  - Registrar médicos y empleados.
  - Gestionar disponibilidad horaria.

### Exclusiones
- El paciente no accede directamente al sistema en esta versión.

## 4. Limitaciones
- Uso exclusivo en computadoras de la clínica.
- Sin aplicación móvil.
- Carga de información realizada por empleados autorizados.
- Sin portal de autogestión para pacientes.

## 5. Requisitos no funcionales globales
- **RNFG1:** Funcionamiento en computadoras de la clínica.
- **RNFG2:** Tiempo de respuesta máximo de 5 segundos.
- **RNFG3:** Almacenamiento seguro de información.
- **RNFG4:** Disponibilidad del sistema durante horario laboral.
- **RNFG5:** Interfaz simple e intuitiva.

## 5.1 Módulo Gestión de Pacientes
### 5.1.1 Requisitos funcionales
- **RFC1:** Registrar pacientes con DNI único, nombre, apellido, teléfono y obra social.
- **RFC2:** Modificar datos del paciente por DNI.
- **RFC3:** Buscar pacientes por DNI o por nombre y apellido.
- **RFC4:** Asociar historial clínico al crear paciente.

### 5.1.2 Requisitos no funcionales
- **RNFC1:** Búsqueda en menos de 3 segundos con estructura/algoritmo eficiente.
- **RNFC2:** Guardado automático de datos sin botón extra.

## 5.2 Módulo Gestión de Médicos
### 5.2.1 Requisitos funcionales
- **RFC5:** Registrar médicos con datos personales, matrícula y especialidad.
- **RFC6:** Definir disponibilidad por día y franja horaria.
- **RFC7:** Modificar disponibilidad ante cambios laborales.
- **RFC8:** Permitir cancelación de jornada completa de un médico.

### 5.2.2 Requisitos no funcionales
- **RNFC3:** Actualización inmediata de disponibilidad.
- **RNFC4:** Prohibición de solapamientos de horarios y turnos.

## 5.3 Módulo Gestión de Turnos
### 5.3.1 Requisitos funcionales
- **RFC9:** Crear turnos en espacios libres de agenda médica.
- **RFC10:** Cancelar turnos liberando la franja horaria.
- **RFC11:** Reprogramar turnos sin borrar y recrear.
- **RFC12:** Registrar sobreturnos (cita extra excepcional).
- **RFC13:** Marcar turnos como atendidos.
- **RFC14:** Notificar automáticamente cambios al médico correspondiente.

### 5.3.2 Requisitos no funcionales
- **RNFC5:** Notificaciones automáticas ante cambios/cancelaciones.
- **RNFC6:** Bloqueo de turnos duplicados para mismo médico y horario.

## 5.4 Módulo Historial Clínico
### 5.4.1 Requisitos funcionales
- **RFC15:** Visualizar historial clínico con atenciones, diagnósticos y estudios.
- **RFC16:** Registrar consulta médica con resumen.
- **RFC17:** Actualizar diagnóstico del paciente.
- **RFC18:** Registrar estudios y resultados.

### 5.4.2 Requisitos no funcionales
- **RNFC7:** Solo médicos pueden modificar historial clínico.
- **RNFC8:** Confidencialidad de datos sensibles y acceso por permisos.

## 6. Prototipos de interfaz
No aplica en esta etapa del proyecto.

## 7. Glosario
- **DNI:** Documento Nacional de Identidad (identificador único de paciente).
- **Obra social:** Cobertura médica del paciente.
- **Sobreturno:** Turno extra agregado fuera de la agenda estándar.
- **Matrícula profesional:** Identificador habilitante del médico.
- **Disponibilidad:** Días y franjas horarias habilitadas para atención.
- **Historial clínico:** Registro cronológico de atenciones, diagnósticos y estudios.
- **Turno atendido:** Turno finalizado con consulta realizada.
- **RNF:** Requisito no funcional.
- **RFC:** Requisito funcional del cliente.
