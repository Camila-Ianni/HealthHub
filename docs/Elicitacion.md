# Elicitación de Requisitos - HealthHub

**Proyecto:** HealthHub  
**Fecha:** Abril 2026  
**Formato:** Informe breve de levantamiento

---

## 1. Cómo se levantaron los requisitos

Para este proyecto se tomó un enfoque simple y académico:

1. Se definió un problema concreto: gestión básica de pacientes, doctores y turnos.
2. Se listaron operaciones mínimas que una clínica pequeña necesita para operar.
3. Se tradujeron esas operaciones a clases y responsabilidades en Java.
4. Se recortó alcance para mantener foco en POO y no en infraestructura.

No hubo entrevistas formales con usuarios reales; se trabajó con escenarios simulados típicos de consultorio.

---

## 2. Necesidades detectadas

### 2.1 Necesidades funcionales
- Registrar personas (pacientes y doctores) con datos relevantes.
- Poder vincular ambas entidades en un turno.
- Guardar observaciones clínicas básicas al cerrar un turno.
- Evitar pérdida total de datos entre ejecuciones.

### 2.2 Necesidades técnicas
- Mantener el código entendible para evaluación docente.
- Usar Java puro, sin frameworks.
- Evitar sobreingeniería en una primera versión.

---

## 3. Decisiones de alcance (con justificación)

1. **Persistencia por archivos de texto separados.**  
   Se eligió por simpleza y trazabilidad manual del contenido.

2. **Sin interfaz gráfica.**  
   Se priorizó diseño de clases y flujo de negocio.

3. **Sin base de datos en esta fase.**  
   Se dejó como evolución posterior para no mezclar demasiados frentes.

4. **Validaciones básicas, no reglas hospitalarias avanzadas.**  
   El objetivo fue robustez mínima sin convertir el proyecto en un HIS completo.

---

## 4. Riesgos identificados

1. Si los archivos se editan a mano con formato incorrecto, parte de la carga puede omitirse.
2. Al no haber control de concurrencia, dos ejecuciones paralelas podrían pisar archivos.
3. Sin hash de contraseñas, la seguridad es limitada y apta solo para entorno académico.

---

## 5. Criterios de aceptación que guiaron el desarrollo

1. El sistema debe ejecutar de punta a punta sin dependencias externas.
2. Debe poder crear y manipular objetos de dominio sin romper encapsulamiento.
3. Debe guardar y recuperar datos desde disco con formato predecible.
4. El código debe ser legible por un estudiante intermedio/avanzado sin explicación extra.

---

## 6. Comentario final de ingeniería

El levantamiento de requisitos fue intencionalmente práctico: se buscó un balance entre realismo y alcance manejable para un proyecto universitario.  
La base actual permite crecer hacia una arquitectura más formal (repositorios, API, seguridad real) sin descartar el trabajo ya hecho.
