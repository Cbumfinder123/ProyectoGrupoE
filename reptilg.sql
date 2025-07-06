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

-- Tabla USUARIOS
CREATE TABLE tb_usuarios (
    cod_usua INT AUTO_INCREMENT PRIMARY KEY,
    nom_usua VARCHAR(100) NOT NULL,
    ape_usua VARCHAR(100) NOT NULL,
    user_usua VARCHAR(50) NOT NULL UNIQUE,
    pswd_usua VARCHAR(255) NOT NULL,
    fnac_usua DATE NOT NULL,
    idtipo INT DEFAULT 2,
    est_usua BIT NOT NULL DEFAULT 1
);

INSERT INTO tb_usuarios (nom_usua, ape_usua, user_usua, pswd_usua, fnac_usua, idtipo, est_usua) VALUES 
('Juan', 'Admin', 'admin1', 'admin123', '1990-01-01', 1, 1),
('Lucía', 'Tester', 'ltorres', 'test456', '1998-12-12', 2, 1);
select* from tb_usuarios;
-- Tabla VENTA
CREATE TABLE tbl_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    cliente_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES tbl_cliente(id)
);

-- Tabla DETALLE_VENTA
CREATE TABLE tbl_detalle_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    producto_id INT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES tbl_venta(id),
    FOREIGN KEY (producto_id) REFERENCES tbl_producto(id)
        ON DELETE SET NULL
);

CREATE TABLE tbl_reporte (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    accion VARCHAR(20) NOT NULL,
    detalle VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL,
    entidad_id INT,
    codigo_entidad VARCHAR(20),
    usuario_id INT NULL,
    FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(cod_usua)
);

-- Insertar VENTAS
INSERT INTO tbl_venta (cliente_id, total, fecha) VALUES
(1, 99.98, '2023-04-10 10:00:00'),      -- Juan Pérez
(2, 349.99, '2023-12-01 15:30:00'),     -- María Gómez
(3, 139.98, '2024-06-20 12:15:00'),     -- Carlos López
(4, 499.99, '2024-11-07 09:45:00'),     -- Ana Rodríguez
(5, 89.99,  '2025-02-18 18:30:00');     -- Luis Torres

-- Insertar DETALLE_VENTA
INSERT INTO tbl_detalle_venta (venta_id, producto_id, cantidad, precio_unitario) VALUES
(1, 5, 2, 49.99),      -- FIFA 23 x2
(2, 4, 1, 349.99),     -- Nintendo Switch OLED
(3, 1, 1, 59.99),      -- God of War: Ragnarok
(3, 3, 1, 89.99),      -- Teclado RGB
(4, 2, 1, 499.99),     -- Xbox Series X
(5, 3, 1, 89.99);      -- Teclado RGB

-- Insertar REPORTES de tipo VENTA
INSERT INTO tbl_reporte (tipo, accion, detalle, fecha, entidad_id, codigo_entidad, usuario_id) VALUES
('VENTA', 'REGISTRO', 'Juan Pérez compró 2 unidades de FIFA 23', '2023-04-10 10:05:00', 1, 'VENT-001', 1),
('VENTA', 'REGISTRO', 'María Gómez compró una Nintendo Switch OLED', '2023-12-01 15:31:00', 2, 'VENT-002', 2),
('VENTA', 'REGISTRO', 'Carlos adquirió God of War y un Teclado RGB', '2024-06-20 12:16:00', 3, 'VENT-003', 2),
('VENTA', 'REGISTRO', 'Ana Rodríguez se llevó una Xbox Series X', '2024-11-07 09:46:00', 4, 'VENT-004', 1),
('VENTA', 'REGISTRO', 'Luis compró un Teclado RGB Gaming', '2025-02-18 18:31:00', 5, 'VENT-005', 1);

