# Gestor de Personal y Turnos Empresariales

Aplicación de escritorio en Java que permite gestionar empresas, empleados, turnos y ausencias.  
Incluye autenticación jerárquica, asignación de roles y persistencia en base de datos MySQL.

---

## Descripción

El proyecto consiste en el desarrollo de una aplicación para la gestión de personal dentro de una o varias empresas. Está centrado en la administración eficiente de empleados, horarios laborales y solicitudes de ausencia, permitiendo una visión estructurada según el rol del usuario.

### Tipos de Usuarios

- **Empresas**:

   - Gestionan sus empleados y departamentos
   - Crean y asignan turnos
   - Administran ausencias del personal
- **Empleados** (futura implementación):

   - Pueden ser Supervisores o Empleados regulares
   - Visualizan sus turnos asignados
   - Solicitan y gestionan ausencias

### Características Principales

- **Sistema de Autenticación**:

   - Protección mediante email y contraseña
   - Encriptación segura de contraseñas con BCrypt
   - Validación de datos de usuario
- **Gestión de Personal**:

   - Control de empleados activos/inactivos
   - Asignación de roles y departamentos
   - Seguimiento de ausencias
- **Persistencia de Datos**:

   - Almacenamiento en base de datos MySQL
   - Gestión de conexiones segura
   - Logging de operaciones críticas

---

## Tecnologías Detalladas

### Java Core

- Versión: Java 23
- Características utilizadas:
   - Collections Framework
   - Programación orientada a objetos
   - Manejo de excepciones personalizadas

### JavaFX

- Versión: 17.0.12
- Componentes:
   - FXML para diseño de interfaces
   - CSS para estilos
   - Controladores para lógica de UI

### MySQL

- Versión: 8.0.33
- Características:
   - Conexiones pooling
   - Transacciones ACID
   - Claves foráneas y CASCADE

### Otras Tecnologías

- SLF4J y Logback para logging
- JBCrypt para encriptación
- JAXB para configuración XML
- JUnit 5 para testing

---

## Arquitectura

- **Tipo**: Aplicación de escritorio
- **Patrones**: MVC (Modelo-Vista-Controlador), DAO (Data Access Object)
- **Persistencia**: MySQL
- **UI**: JavaFX

---
## Diagramas
### Diagrama de Clases
Incluye las entidades Empresa, Empleado, Turno, Ausencia, y sus relaciones DAO/Vista/Controlador.
![UML.png](documentacion/UML.png)

### Diagrama de Casos de Uso
Muestra los distintos casos de uso disponibles para cada tipo de usuario (empresa, supervisor, empleado).

### Diagrama Entidad-Relación (Base de Datos)
Representa las tablas empresa, empleado, ausencia, turno, pertenece y sus relaciones mediante claves foráneas.
![E-Rv4.jpg](documentacion/E-Rv4.jpg)

---
## Estructura de la Base de Datos

La base de datos `gestion_plantillas` utiliza el conjunto de caracteres UTF-8 y el motor InnoDB. Se compone de las siguientes tablas:

### Tabla `empresa`

- Almacena información de empresas registradas
- Campos principales:
   - `id_empresa`: Identificador único
   - `nombre`: Nombre de la empresa (único)
   - `email`: Correo electrónico (único)
   - `password_hash`: Contraseña encriptada

### Tabla `empleado`

- Registro de empleados de cada empresa
- Campos principales:
   - `id_usuario`: Identificador único
   - `nombre` y `apellidos`: Datos personales
   - `rol`: SUPERVISOR o EMPLEADO
   - `id_empresa`: Empresa a la que pertenece

### Tabla `ausencia`

- Control de ausencias del personal
- Campos principales:
   - `motivo`: Razón de la ausencia
   - `fecha_inicio` y `fecha_fin`: Período
   - `id_empleado`: Empleado ausente

### Tabla `turno`

- Definición de turnos de trabajo
- Campos principales:
   - `descripcion`: Nombre del turno
   - `hora_inicio` y `hora_fin`: Horario

### Tabla `pertenece`

- Asignación de turnos a empleados
- Relación many-to-many entre empleados y turnos
- Incluye fecha específica de asignación

---
## Estructura del Proyecto

### `src/main/java/org/dam/fcojavier/gestionpersonal/`

- **controllers/**: Controladores de JavaFX
- **DAOs/**: Acceso a datos
- **model/**: Entidades del dominio
- **utils/**: Utilidades generales
- **exceptions/**: Excepciones personalizadas
- **interfaces/**: Interfaces del sistema
- **bbdd/**: Gestión de conexiones
- **enums/**: Enumeraciones

---

## Funcionalidades principales

- Registro e inicio de sesión de empresas
- Gestión completa de empleados
- Control de turnos y horarios
- Registro y seguimiento de ausencias
- Sistema de roles y permisos
- Logging de operaciones críticas
- Interfaz gráfica intuitiva

---
## Requisitos y Ejecución

### Requisitos
- Java 23
- MySQL 8.0.33
- Maven 3.9+

### Instalación
1. Clonar el repositorio
2. Ejecutar el script SQL para crear la base de datos
3. Configurar credenciales en connection.xml
4. Ejecutar:
   ```bash
   mvn clean install
   ```
5. Lanzar la aplicación:
   ```bash
   mvn javafx:run
   ```

---
## Consideraciones

- Las contraseñas se almacenan encriptadas con BCrypt
- Se utiliza el patrón DAO para acceso a datos
- La conexión a base de datos se configura mediante XML
- Los logs se almacenan en archivos diarios
- Soporte para múltiples empresas en la misma base de datos

---
## Estado del proyecto

**Estado**: En desarrollo activo

**Pendiente**:
- Informes en PDF
- Notificaciones por email
- Sistema de fichaje
- Calendario visual de turnos

---
## Autor

- Hecho por:
   - Fco Javier García Cañero
- Proyecto académico para IES Francisco de los Rios
