

-- 1. Eliminar base de datos si existe (cuidado en producción)
DROP DATABASE IF EXISTS logitrack_db;

-- 2. Crear base de datos
CREATE DATABASE logitrack_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE logitrack_db;

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: productos
-- ============================================================
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    precio DECIMAL(10, 2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: bodegas
-- ============================================================
CREATE TABLE bodega (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    ubicacion VARCHAR(200) NOT NULL,
    capacidad INT NOT NULL DEFAULT 0,
    encargado VARCHAR(150),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- TABLA: movimientos
-- ============================================================
CREATE TABLE movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 1),
    
    -- Relaciones
    usuario_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    bodega_origen_id BIGINT,
    bodega_destino_id BIGINT,
    
    -- Foreign Keys
    CONSTRAINT fk_movimiento_usuario 
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT fk_movimiento_producto 
        FOREIGN KEY (producto_id) REFERENCES producto(id) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT fk_movimiento_bodega_origen 
        FOREIGN KEY (bodega_origen_id) REFERENCES bodega(id) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    
    CONSTRAINT fk_movimiento_bodega_destino 
        FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;


CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_nombre VARCHAR(100),
    accion VARCHAR(20) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    valores_anteriores TEXT,
    valores_nuevos TEXT,
    ip_origen VARCHAR(50)
) ENGINE=InnoDB;


CREATE INDEX idx_movimiento_fecha ON movimiento(fecha);
CREATE INDEX idx_movimiento_tipo ON movimiento(tipo);
CREATE INDEX idx_movimiento_producto ON movimiento(producto_id);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha);
CREATE INDEX idx_auditoria_entidad ON auditoria(entidad);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- USUARIOS
-- Contraseña: 12345 (encriptada con BCrypt, 10 rondas)
INSERT INTO usuario (nombre, correo, password, rol) VALUES
('admin1', 'admin@logitrack.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
('juan_perez', 'empleado@logitrack.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLEADO'),
('maria_gomez', 'maria@logitrack.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLEADO');

-- BODEGAS
INSERT INTO bodega (nombre, ubicacion, capacidad, encargado) VALUES
('Bodega Central Quito', 'Av. Amazonas N36-152, Quito', 5000, 'Carlos Ramírez'),
('Bodega Central Medellín', 'Av. China N36-152, Medellín', 3000, 'Ana Torres'),
('Bodega Sucursal Loja', 'Calle Principal 123, Loja', 1500, 'Luis Mendoza');

-- PRODUCTOS
INSERT INTO producto (nombre, categoria, stock, precio) VALUES
('Teclado Mecánico', 'Tecnología', 15, 85.50),
('Monitor 24 pulgadas', 'Tecnología', 10, 220.00),
('Silla Ergonómica', 'Mobiliario', 25, 180.75),
('Escritorio de Oficina', 'Mobiliario', 8, 350.00),
('Cable HDMI 2m', 'Accesorios', 100, 12.99),
('Mouse Inalámbrico', 'Tecnología', 5, 35.00),
('Lámpara LED', 'Iluminación', 3, 45.50),
('Organizador de Cables', 'Accesorios', 50, 8.99);


INSERT INTO movimiento (fecha, tipo, cantidad, usuario_id, producto_id, bodega_origen_id, bodega_destino_id) VALUES
(NOW() - INTERVAL 5 DAY, 'ENTRADA', 20, 1, 1, NULL, 1),
(NOW() - INTERVAL 4 DAY, 'ENTRADA', 15, 1, 2, NULL, 1),
(NOW() - INTERVAL 3 DAY, 'SALIDA', 5, 2, 1, 1, NULL),
(NOW() - INTERVAL 2 DAY, 'TRANSFERENCIA', 10, 1, 3, 1, 2),
(NOW() - INTERVAL 1 DAY, 'ENTRADA', 50, 3, 5, NULL, 2),
(NOW(), 'SALIDA', 3, 2, 4, 2, NULL);


INSERT INTO auditoria (fecha, usuario_nombre, accion, entidad, valores_anteriores, valores_nuevos) VALUES
(NOW() - INTERVAL 5 DAY, 'admin1', 'INSERT', 'Producto', NULL, '{"nombre":"Teclado Mecánico","stock":20}'),
(NOW() - INTERVAL 4 DAY, 'admin1', 'INSERT', 'Producto', NULL, '{"nombre":"Monitor 24 pulgadas","stock":15}'),
(NOW() - INTERVAL 3 DAY, 'juan_perez', 'UPDATE', 'Producto', '{"stock":20}', '{"stock":15}'),
(NOW() - INTERVAL 2 DAY, 'admin1', 'INSERT', 'Movimiento', NULL, '{"tipo":"TRANSFERENCIA","cantidad":10}'),
(NOW() - INTERVAL 1 DAY, 'maria_gomez', 'INSERT', 'Producto', NULL, '{"nombre":"Cable HDMI 2m","stock":50}'),
(NOW(), 'juan_perez', 'DELETE', 'Producto', '{"nombre":"Producto Eliminado"}', NULL);




CREATE OR REPLACE VIEW v_stock_por_bodega AS
SELECT 
    b.nombre AS bodega,
    p.nombre AS producto,
    p.categoria,
    p.stock AS stock_total,
    CASE 
        WHEN p.stock < 10 THEN 'CRÍTICO'
        WHEN p.stock < 25 THEN 'BAJO'
        ELSE 'NORMAL'
    END AS estado_stock
FROM producto p
CROSS JOIN bodega b
ORDER BY p.stock ASC;


CREATE OR REPLACE VIEW v_resumen_movimientos AS
SELECT 
    m.tipo,
    COUNT(*) AS total_movimientos,
    SUM(m.cantidad) AS total_unidades,
    u.nombre AS usuario_responsable
FROM movimiento m
JOIN usuario u ON m.usuario_id = u.id
GROUP BY m.tipo, u.nombre;

