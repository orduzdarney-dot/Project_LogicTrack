-- schema.sql: Define la estructura de la base de datos

DROP TABLE IF EXISTS auditoria;
DROP TABLE IF EXISTS movimiento;
DROP TABLE IF EXISTS bodega;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL UNIQUE,
                         correo VARCHAR(150) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         rol ENUM('ADMIN', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
                         activo BOOLEAN NOT NULL DEFAULT TRUE,
                         fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE producto (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(150) NOT NULL,
                          categoria VARCHAR(100) NOT NULL,
                          stock INT NOT NULL DEFAULT 0,
                          precio DECIMAL(10, 2) NOT NULL,
                          fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE bodega (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(150) NOT NULL,
                        ubicacion VARCHAR(200) NOT NULL,
                        capacidad INT NOT NULL DEFAULT 0,
                        encargado VARCHAR(150),
                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE movimiento (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            fecha DATETIME NOT NULL,
                            tipo ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
                            cantidad INT NOT NULL CHECK (cantidad >= 1),
                            usuario_id BIGINT NOT NULL,
                            producto_id BIGINT NOT NULL,
                            bodega_origen_id BIGINT,
                            bodega_destino_id BIGINT,
                            CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                            CONSTRAINT fk_mov_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT ON UPDATE CASCADE,
                            CONSTRAINT fk_mov_bodega_origen FOREIGN KEY (bodega_origen_id) REFERENCES bodega(id) ON DELETE SET NULL ON UPDATE CASCADE,
                            CONSTRAINT fk_mov_bodega_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE auditoria (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           tipo_operacion VARCHAR(50) NOT NULL,
                           fecha DATETIME NOT NULL,
                           entidad_afectada VARCHAR(50) NOT NULL,
                           id_registro_afectado BIGINT NOT NULL,
                           valores_anteriores TEXT,
                           valores_nuevos TEXT,
                           nombre_usuario VARCHAR(50)
) ENGINE=InnoDB;