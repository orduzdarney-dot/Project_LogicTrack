-- data.sql: Inserta los datos iniciales de prueba

-- USUARIOS (Contraseña: 12345)
INSERT INTO usuario (nombre, correo, password, rol) VALUES
                                                        ('admin1', 'admin@logitrack.com', '$2a$10$NjjAedBMTBJNOKJjZIj3DeO1p/RWNi9cmb2heWUgG/Wt1pSbRXaLe', 'ADMIN'),
                                                        ('juan_perez', 'empleado@logitrack.com', '$2a$10$NjjAedBMTBJNOKJjZIj3DeO1p/RWNi9cmb2heWUgG/Wt1pSbRXaLe', 'EMPLEADO'),
                                                        ('maria_gomez', 'maria@logitrack.com', '$2a$10$NjjAedBMTBJNOKJjZIj3DeO1p/RWNi9cmb2heWUgG/Wt1pSbRXaLe', 'EMPLEADO')
    ON DUPLICATE KEY UPDATE nombre=nombre; -- Evita error si ya existen

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