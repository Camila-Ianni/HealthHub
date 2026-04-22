# HealthHub - Guía de Compilación y Ejecución

## Requisitos Previos

### Software Necesario
- **Java JDK 8 o superior**
- **Editor de texto** (cualquiera)
- **Terminal/Command Prompt**

### Verificar Instalación de Java
```bash
java -version
javac -version
```

---

## Estructura del Proyecto

```
HealthHub/
├── src/
│   ├── User.java
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Appointment.java
│   └── Main.java
├── README.md
├── SRS.md
├── DIAGRAMS.md
├── SKILLS.md
└── COMPILE_AND_RUN.md (este archivo)
```

---

## Compilar el Proyecto

### Opción 1: Compilar todos los archivos
```bash
cd HealthHub/src
javac *.java
```

### Opción 2: Compilar archivos individuales
```bash
cd HealthHub/src
javac User.java
javac Patient.java
javac Doctor.java
javac Appointment.java
javac Main.java
```

### Verificar Compilación
Después de compilar, deberías ver archivos `.class`:
```bash
ls -la
```

Salida esperada:
```
User.class
Patient.class
Doctor.class
Appointment.class
Main.class
```

---

## Ejecutar el Programa

### Desde el directorio src/
```bash
cd HealthHub/src
java Main
```

### Salida Esperada
```
=== HealthHub System ===

--- Registered Patients ---
Patient{patientId='P001', name='John Doe', dob='1990-05-15', gender='Male', phone='+1-555-1234'}
Patient{patientId='P002', name='Mary Smith', dob='1985-08-22', gender='Female', phone='+1-555-5678'}

--- Available Doctors ---
Doctor{doctorId='D001', name='Dr. Robert Johnson', specialization='Cardiology', department='Cardiology', experience=15 years, available=true}
Doctor{doctorId='D002', name='Dr. Sarah Williams', specialization='Pediatrics', department='Pediatrics', experience=10 years, available=true}

--- Scheduled Appointments ---
Appointment{id='A001', patient=John Doe, doctor=Dr. Robert Johnson, date='2026-04-01', time='10:00 AM', status='SCHEDULED', reason='Annual checkup'}
Appointment{id='A002', patient=Mary Smith, doctor=Dr. Sarah Williams, date='2026-04-02', time='02:30 PM', status='SCHEDULED', reason='Child vaccination'}

--- Completing Appointment A001 ---
Status: COMPLETED
Diagnosis: Patient is healthy. Blood pressure normal.
Prescription: Continue regular exercise. Follow-up in 6 months.

--- Cancelling Appointment A002 ---
Status: CANCELLED

--- Testing Getters/Setters ---
Updated Patient Phone: +1-555-0000
Doctor Available: false
Appointment Status: COMPLETED

=== HealthHub System Test Complete ===
```

---

## Limpiar Archivos Compilados

### Eliminar archivos .class
```bash
cd HealthHub/src
rm *.class
```

### En Windows
```cmd
cd HealthHub\src
del *.class
```

---

## Errores Comunes y Soluciones

### Error: "javac: command not found"
**Causa:** Java JDK no está instalado o no está en el PATH

**Solución:**
1. Instalar Java JDK desde [Oracle](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://openjdk.org/)
2. Agregar Java al PATH del sistema

### Error: "cannot find symbol"
**Causa:** Dependencias no compiladas

**Solución:**
```bash
# Compilar en orden correcto
javac User.java
javac Patient.java Doctor.java
javac Appointment.java
javac Main.java
```

### Error: "class file contains wrong class"
**Causa:** Archivos .class desactualizados

**Solución:**
```bash
rm *.class
javac *.java
```

---

## Modificar el Código

### 1. Editar archivos .java
Usa cualquier editor de texto:
- VSCode
- IntelliJ IDEA
- Eclipse
- Notepad++
- nano/vim

### 2. Recompilar
Después de modificar, siempre recompila:
```bash
javac *.java
```

### 3. Ejecutar de nuevo
```bash
java Main
```

---

## Crear un JAR Ejecutable (Opcional)

### 1. Crear archivo Manifest
```bash
echo "Main-Class: Main" > manifest.txt
```

### 2. Compilar
```bash
javac *.java
```

### 3. Crear JAR
```bash
jar cfm HealthHub.jar manifest.txt *.class
```

### 4. Ejecutar JAR
```bash
java -jar HealthHub.jar
```

---

## Desarrollo Avanzado

### Usar un IDE

#### IntelliJ IDEA
1. File → New → Project from Existing Sources
2. Seleccionar carpeta HealthHub
3. Importar como proyecto Java
4. Run → Run 'Main'

#### Eclipse
1. File → New → Java Project
2. Deseleccionar "Use default location"
3. Seleccionar carpeta HealthHub
4. Finish
5. Right-click Main.java → Run As → Java Application

#### VSCode
1. Instalar extensión "Extension Pack for Java"
2. Abrir carpeta HealthHub
3. F5 para ejecutar

---

## Testing

### Manual Testing
El archivo `Main.java` ya incluye tests básicos:
- Creación de pacientes
- Creación de doctores
- Agendado de citas
- Completar citas
- Cancelar citas
- Getters y setters

### Agregar más tests
Editar `Main.java` y agregar:
```java
// Test adicional
Patient patient3 = new Patient(...);
System.out.println("Test: " + patient3.getFullName());
```

---

## Próximos Pasos

1. **Agregar más funcionalidad**
   - Validación de datos
   - Manejo de excepciones
   - Persistencia con archivos

2. **Mejorar la interfaz**
   - Menú interactivo
   - Scanner para entrada de usuario

3. **Agregar Collections**
   - ArrayList para listas de pacientes/doctores
   - HashMap para búsquedas rápidas

4. **Unit Testing**
   - JUnit framework
   - Test coverage

---

## Soporte

Si encuentras problemas:
1. Revisa la [documentación oficial de Java](https://docs.oracle.com/javase/8/docs/)
2. Consulta SRS.md para especificaciones
3. Revisa DIAGRAMS.md para arquitectura
4. Lee SKILLS.md para conceptos OOP

---

**Última actualización:** 28 de Marzo, 2026
