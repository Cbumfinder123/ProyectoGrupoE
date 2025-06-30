-- Eliminar BD si existe
DROP DATABASE IF EXISTS `reptilg`;

-- Crear BD
CREATE DATABASE `reptilg`;
USE `reptilg`;

-- Tabla CATEGORÍA
CREATE TABLE tbl_categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

INSERT INTO tbl_categoria (nombre) VALUES 
('Gitcards'), ('Juegos'), ('Consolas'), ('Accesorios');

-- Tabla PROVEEDOR
CREATE TABLE tbl_proveedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contacto VARCHAR(100)
);

INSERT INTO tbl_proveedor (nombre, contacto) VALUES 
('TechSupply Global', 'ventas@techsupplyglobal.com'),
('GamerDistribuciones', 'info@gamerdistribuciones.com'),
('Zona Gamer SAC', 'contacto@zonagamersac.pe'),
('NextLevel Imports', 'soporte@nextlevelimports.com'),
('PixelWare Solutions', 'clientes@pixelware.com'),
('NeoGaming Corp', 'ventas@neogamingcorp.com'),
('ByteMasters', 'atencion@bytemasters.io'),
('ControlPoint SAC', 'contacto@controlpoint.pe');

-- Tabla PLATAFORMA
CREATE TABLE tbl_plataforma (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

INSERT INTO tbl_plataforma (nombre) VALUES 
('PC'), ('Sony'), ('Microsoft'), ('Nintendo');

-- Tabla PRODUCTO
CREATE TABLE tbl_producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    id_categoria INT NOT NULL,
    id_proveedor INT NOT NULL,
    id_plataforma INT NOT NULL,
    stock INT DEFAULT 0,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (id_categoria) REFERENCES tbl_categoria(id),
    FOREIGN KEY (id_proveedor) REFERENCES tbl_proveedor(id),
    FOREIGN KEY (id_plataforma) REFERENCES tbl_plataforma(id)
);

INSERT INTO tbl_producto (codigo, nombre, id_categoria, id_proveedor, id_plataforma, stock, precio, descripcion) VALUES
('PROD-001', 'God of War: Ragnarok', 2, 1, 2, 50, 59.99, 'Juego de aventura para PlayStation'),
('PROD-002', 'Xbox Series X', 3, 3, 3, 30, 499.99, 'Consola de última generación'),
('PROD-003', 'Teclado RGB Gaming', 4, 4, 1, 100, 89.99, 'Teclado mecánico para PC'),
('PROD-004', 'Nintendo Switch OLED', 3, 5, 4, 25, 349.99, 'Consola portátil'),
('PROD-005', 'FIFA 23', 2, 2, 3, 40, 49.99, 'Juego de fútbol para Xbox');

-- Tabla CLIENTE
CREATE TABLE tbl_cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(8) UNIQUE NOT NULL
);

INSERT INTO tbl_cliente (nombre, dni) VALUES 
('Juan Pérez', '12345678'),
('María Gómez', '87654321'),
('Carlos López', '11223344'),
('Ana Rodríguez', '44332211'),
('Luis Torres', '55667788');

-- Tabla VENTA
CREATE TABLE tbl_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    cliente_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES tbl_cliente(id)
);

INSERT INTO tbl_venta (fecha, cliente_id, total) VALUES 
('2023-10-15 14:30:00', 1, 149.98),
('2023-10-16 10:15:00', 2, 499.99),
('2023-10-17 16:45:00', 3, 239.97),
('2023-10-18 11:20:00', 4, 349.99),
('2023-10-19 09:30:00', 5, 99.98);

-- Tabla DETALLE_VENTA (modificada para permitir ON DELETE SET NULL)
CREATE TABLE tbl_detalle_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    producto_id INT NULL, -- <- permitir NULL
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES tbl_venta(id),
    FOREIGN KEY (producto_id) REFERENCES tbl_producto(id)
        ON DELETE SET NULL -- <- importante
);

INSERT INTO tbl_detalle_venta (venta_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 1, 59.99),  
(1, 3, 1, 89.99),   
(2, 2, 1, 499.99),  
(3, 1, 2, 59.99),   
(3, 3, 1, 89.99),   
(4, 4, 1, 349.99),  
(5, 5, 2, 49.99);   

-- Tabla REPORTE
CREATE TABLE tbl_reporte (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    accion VARCHAR(20) NOT NULL,
    detalle VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL,
    entidad_id INT,
    codigo_entidad VARCHAR(20)
);
