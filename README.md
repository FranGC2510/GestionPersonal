```

  # Gestión de Personal y Turnos Empresariales

Aplicación JavaFX que permite gestionar empleados, turnos y ausencias para empresas.
Incluye persistencia de datos mediante MySQL y gestión de sesiones con patrón Singleton.

---

## Descripción

El proyecto consiste en el desarrollo de una aplicación para la gestión de personal y turnos empresariales. El programa permite que las empresas se registren en el sistema y, según el rol del usuario, podrán realizar diferentes acciones:

### Tipos de Usuarios

- **Empresas**:
  - Gestionan sus empleados y departamentos
  - Crean y asignan turnos
  - Administran ausencias del personal

- **Empleados**:
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

## Configuración del Proyecto

1. Clonar el repositorio
2. Crear la base de datos usando el script proporcionado
3. Configurar `connection.xml` con los datos de conexión
4. Ejecutar con Maven: bash mvn clean install mvn javafx:run
        
---

## Consideraciones

- Las contraseñas se almacenan encriptadas con BCrypt
- Se utiliza el patrón DAO para acceso a datos
- La conexión a base de datos se configura mediante XML
- Los logs se almacenan en archivos diarios
- Soporte para múltiples empresas en la misma base de datos

---

## Autor

- Hecho por:
  - Fco Javier García Cañero
- Proyecto académico para IES Francisco de los Rios

```
