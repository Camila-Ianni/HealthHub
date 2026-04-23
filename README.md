SRS – Sistema Health Hub

Historial de versiones del documento
Versión
Fecha
Descripción
Autor
1.0
07/04/2026
Creación del documento SRS
Camila Ianni 


Tabla de contenidos
Objetivos
Beneficios
Alcances
Limitaciones
Requisitos no funcionales globales
5.1 Módulo Gestión de Pacientes
5.2 Módulo Gestión de Médicos
5.3 Módulo Gestión de Turnos
5.4 Módulo Historial Clínico
Prototipos de interfaz
Glosario

1. Objetivo
El objetivo del sistema es desarrollar una solución que permita gestionar de forma eficiente los turnos médicos de pacientes y sus historiales clínicos dentro de la clínica Health Hub, reemplazando el sistema manual actual basado en planillas.

2. Beneficios
Automatización del proceso de asignación de turnos
Reducción de errores manuales
Mejora en la organización de horarios médicos
Acceso rápido al historial clínico del paciente
Notificaciones automáticas ante cualquier cambio
Optimización del trabajo del recepcionista

3. Alcance
El sistema contará con tres tipos de usuarios, los cuales desarrollan las siguientes funciones:
Recepcionista
Registrar pacientes
Buscar pacientes
Crear turnos
Cancelar y reprogramar turnos
Consultar disponibilidad de médicos
Médico
Consultar turnos asignados
Visualizar historial clínico
Actualizar historial clínico
Marcar turnos como atendidos
Informar cancelaciones o demoras
Administrador
Registrar médicos
Gestionar disponibilidad
Registrar empleados
El paciente no tendrá acceso directo al sistema en esta versión, solo el administrador

4. Limitaciones
El sistema funcionará únicamente en computadoras
No habrá acceso para pacientes
No se contemplan dispositivos móviles
La carga de información será realizada por empleados

5. Requisitos no funcionales globales
RNFG1: El sistema funcionará en computadoras de la clínica
RNFG2: El tiempo de respuesta no debe superar los 5 segundos
RNFG3: La información debe almacenarse de forma segura
RNFG4: El sistema debe estar disponible en horario laboral
RNFG5: La interfaz debe ser simple e intuitiva

5.1 Módulo Gestión de Pacientes
5.1.1 Requisitos funcionales

RFC1: El recepcionista podrá registrar pacientes, indicando DNI no existente previamente en el sistema, nombre, apellido, teléfono y obra social.
RFC2: El recepcionista podrá modificar datos de pacientes mediante el DNI del paciente.
RFC3: El recepcionista podrá buscar pacientes a través de un filtro de búsqueda por DNI o Nombre y Apellido.
RFC4: El sistema permitirá asociar un historial clínico a cada paciente, generando un historial clínico al crear usuario tipo paciente.
5.1.2 Requisitos no funcionales
RNFC1: La búsqueda de pacientes debe realizarse en menos de 3 segundos, optimizando la forma de leer los datos a través de una buena elección de estructura de datos y/o algoritmo de búsqueda.
RNFC2: Los datos deben guardarse automáticamente, asegurando la información sin depender de un botón de guardar externo.

5.2 Módulo Gestión de Médicos
5.2.1 Requisitos funcionales
RFC5: El administrador podrá registrar médicos a través de formularios con datos personales, matrícula profesional y especialidad.
RFC6: El administrador podrá definir la disponibilidad horaria marcando que días y en qué horario específico el médico está disponible para atender.
RFC7: El administrador podrá modificar la disponibilidad ajustando los horarios en el calendario en caso de un cambio de esquema laboral.
RFC8: El médico podrá cancelar su jornada, cancelando todo turno en la jornada laboral del día seleccionado.

5.2.2 Requisitos no funcionales
RNFC3: La disponibilidad debe actualizarse en tiempo real, haciendo que cualquier cambio en el horario del médico indicado se vea reflejado en el sistema inmediatamente.
RNFC4: El sistema no debe permitir solapamientos de horarios, guardando los horarios de forma ordenada, sin permitir que se realicen dos turnos en la misma franja horaria.

5.3 Módulo Gestión de Turnos
5.3.1 Requisitos funcionales
RFC9: El recepcionista podrá crear turnos eligiendo un espacio vacío en la agenda del medio seleccionado.
RFC10: El recepcionista podrá cancelar turnos anulando la cita para ese horario, permitiendo que ese horario quede libre para otros turnos posibles.
RFC11: El recepcionista podrá reprogramar turnos cambiando la fecha u hora de la cita, sin tener que eliminarla y crearla de nuevo.
RFC12: El sistema permitirá registrar sobreturnos, habilitando la carga de una cita extra más, aunque el horario oficial no lo permita.
RFC13: El médico podrá marcar un turno como atendido, se dará la opción al médico de marcar si X turnos se han completado.
RFC14: El sistema notificará automáticamente cambios en los turnos, se mostrará un mensaje en el sistema del médico a la cual la cita pertenezca.
5.3.2 Requisitos no funcionales
RNFC5: Las notificaciones deben generarse automáticamente, haciendo al realizar el cambio de turno o eliminación del turno se envía aviso sin la interacción manual de ningún empleado.
RNFC6: El sistema debe evitar la asignación de turnos duplicados bloqueando el intento de dar dos turnos al mismo horario para el mismo médico.

5.4 Módulo Historial Clínico
5.4.1 Requisitos funcionales
RFC15: El médico podrá visualizar el historial clínico, como una lista de pacientes y su historial médico, se podrá revisar atenciones, diagnósticos y estudios previos.
RFC16: El médico podrá registrar consultas médicas escribiendo un resumen detallado de lo que sucedió durante la atención.
RFC17: El médico podrá actualizar diagnósticos anotando la conclusión sobre la salud del paciente para que quede en su historial médico.
RFC18: El médico podrá registrar estudios realizados dejando constancia de los análisis o exámenes en el historial, y que resultados arrojaron dichos análisis.
5.4.2 Requisitos no funcionales
RNFC7: Solo los médicos podrán modificar el historial clínico, bloqueando el acceso al historial a cualquier otro tipo de usuario.
RNFC8: La información debe mantenerse confidencial, permitiendo que solo el personal autorizado pueda acceder a información sensible de los pacientes.

6. Prototipos de interfaz
No aplica en esta etapa del proyecto.


