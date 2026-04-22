package com.healthhub.domain;

/**
 * RolUsuario - representa los roles que puede tener un empleado en el sistema.
 * 
 * Los roles son:
 * - RECEPCIONISTA: puede gestionar pacientes y turnos
 * - MEDICO: puede ver historiales y registrar consultas
 * - ADMINISTRADOR: puede registrar médicos y gestionar disponibilidades
 * 
 * NOTA: Cada rol tiene permisos diferentes. Esto lo podríamos usar para validar
 * qué puede hacer cada usuario, pero para esta entrega no implementamos login.
 */
public enum RolUsuario {
    RECEPCIONISTA,
    MEDICO,
    ADMINISTRADOR
}
