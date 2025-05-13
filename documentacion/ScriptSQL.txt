-- 0) Borrar base de datos anterior y crear una nueva
DROP DATABASE IF EXISTS gestion_plantillas;
CREATE DATABASE gestion_plantillas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_plantillas;

-- 1) Tabla empresa (completa)
CREATE TABLE empresa (
  id_empresa     INT AUTO_INCREMENT PRIMARY KEY,
  nombre         VARCHAR(100) NOT NULL UNIQUE,
  direccion      VARCHAR(255) NOT NULL,
  email          VARCHAR(100) NOT NULL UNIQUE,
  telefono       VARCHAR(20)  NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- 2) Tabla empleado (sin password_hash)
CREATE TABLE empleado (
  id_usuario    INT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(100) NOT NULL,
  apellidos     VARCHAR(100) NOT NULL,
  telefono      VARCHAR(20)  NOT NULL UNIQUE,
  email         VARCHAR(100) NOT NULL UNIQUE,
  activo        BOOLEAN NOT NULL DEFAULT TRUE,
  departamento  VARCHAR(100),
  rol           ENUM('SUPERVISOR', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
  id_empresa    INT NOT NULL,
  FOREIGN KEY (id_empresa)
    REFERENCES empresa(id_empresa)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3) Tabla ausencia (con clave foránea a empleado)
CREATE TABLE ausencia (
  id_ausencia   INT AUTO_INCREMENT PRIMARY KEY,
  motivo        VARCHAR(255) NOT NULL,
  fecha_inicio  DATE NOT NULL,
  fecha_fin     DATE,
  id_empleado   INT NOT NULL,
  FOREIGN KEY (id_empleado)
    REFERENCES empleado(id_usuario)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4) Tabla turno (sin id_creador)
CREATE TABLE turno (
  id_turno     INT AUTO_INCREMENT PRIMARY KEY,
  descripcion  VARCHAR(100) NOT NULL,
  hora_inicio  TIME         NOT NULL,
  hora_fin     TIME         NOT NULL
) ENGINE=InnoDB;

-- 5) Tabla pertenece (asignación de turnos a empleados)
CREATE TABLE pertenece (
  id_empleado INT NOT NULL,
  id_turno    INT NOT NULL,
  fecha       DATE NOT NULL,
  PRIMARY KEY (id_empleado, id_turno, fecha),
  FOREIGN KEY (id_empleado)
    REFERENCES empleado(id_usuario)
    ON DELETE CASCADE,
  FOREIGN KEY (id_turno)
    REFERENCES turno(id_turno)
    ON DELETE CASCADE
) ENGINE=InnoDB;