CREATE DATABASE IF NOT EXISTS waldonalds;
USE waldonalds;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    usuario VARCHAR(60) NOT NULL UNIQUE,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(150) NOT NULL,
    rol ENUM('ADMINISTRADOR','CAJERO') NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    turno ENUM('MANANA','TARDE'),
    hora_inicio TIME,
    hora_fin TIME
);

CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE ingrediente (
    id_ingrediente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    unidad_medida VARCHAR(30) NOT NULL,
    stock_actual DECIMAL(12,2) NOT NULL DEFAULT 0,
    stock_minimo DECIMAL(12,2) NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    precio_base DECIMAL(12,2) NOT NULL DEFAULT 0,
    imagen VARCHAR(500),
    subcategoria VARCHAR(100),
    disponibilidad_menu ENUM('TODO_DIA','DESAYUNO','ALMUERZO') NOT NULL DEFAULT 'TODO_DIA',
    tipo_stock ENUM('RECETA','DIRECTO','NINGUNO') NOT NULL DEFAULT 'DIRECTO',
    personalizable BOOLEAN NOT NULL DEFAULT TRUE,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE producto_ingrediente (
    id_producto_ingrediente INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_ingrediente INT NOT NULL,
    cantidad_default DECIMAL(12,2) NOT NULL,
    permite_quitar BOOLEAN NOT NULL DEFAULT FALSE,
    permite_extra BOOLEAN NOT NULL DEFAULT FALSE,
    cantidad_extra DECIMAL(12,2) NOT NULL DEFAULT 1,
    precio_extra DECIMAL(12,2) NOT NULL DEFAULT 0,
    max_extras INT NOT NULL DEFAULT 1,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_ingrediente) REFERENCES ingrediente(id_ingrediente)
);

CREATE TABLE presentacion_menu (
    id_presentacion INT AUTO_INCREMENT PRIMARY KEY,
    id_producto_principal INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('INDIVIDUAL','MENU','INFANTIL','COMBO') NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    predeterminada BOOLEAN NOT NULL DEFAULT FALSE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_producto_principal) REFERENCES producto(id_producto)
);

CREATE TABLE grupo_presentacion (
    id_grupo INT AUTO_INCREMENT PRIMARY KEY,
    id_presentacion INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    minimo INT NOT NULL DEFAULT 1,
    maximo INT NOT NULL DEFAULT 1,
    permite_repetir BOOLEAN NOT NULL DEFAULT FALSE,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    permite_personalizar BOOLEAN NOT NULL DEFAULT TRUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_presentacion) REFERENCES presentacion_menu(id_presentacion)
);

CREATE TABLE opcion_grupo (
    id_opcion INT AUTO_INCREMENT PRIMARY KEY,
    id_grupo INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    incremento_precio DECIMAL(12,2) NOT NULL DEFAULT 0,
    predeterminada BOOLEAN NOT NULL DEFAULT FALSE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_grupo) REFERENCES grupo_presentacion(id_grupo)
);

CREATE TABLE opcion_componente (
    id_opcion_componente INT AUTO_INCREMENT PRIMARY KEY,
    id_opcion INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_opcion) REFERENCES opcion_grupo(id_opcion),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    numero_orden INT NOT NULL DEFAULT 0,
    id_usuario INT NOT NULL,
    tipo_servicio ENUM('COMER_AQUI','PARA_LLEVAR') NOT NULL,
    estado ENUM('PAGADO','CANCELADO') NOT NULL,
    metodo_pago ENUM('EFECTIVO','TARJETA'),
    monto_recibido DECIMAL(12,2) NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE pedido_detalle (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_presentacion INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    presentacion VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    FOREIGN KEY (id_presentacion) REFERENCES presentacion_menu(id_presentacion)
);

CREATE TABLE pedido_opcion (
    id_pedido_opcion INT AUTO_INCREMENT PRIMARY KEY,
    id_detalle INT NOT NULL,
    id_grupo INT NOT NULL,
    id_opcion INT NOT NULL,
    numero INT NOT NULL DEFAULT 1,
    grupo VARCHAR(150) NOT NULL,
    opcion VARCHAR(150) NOT NULL,
    precio_extra DECIMAL(12,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_detalle) REFERENCES pedido_detalle(id_detalle),
    FOREIGN KEY (id_grupo) REFERENCES grupo_presentacion(id_grupo),
    FOREIGN KEY (id_opcion) REFERENCES opcion_grupo(id_opcion)
);

CREATE TABLE pedido_producto (
    id_pedido_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido_opcion INT NOT NULL,
    id_producto INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_pedido_opcion) REFERENCES pedido_opcion(id_pedido_opcion),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE pedido_modificacion (
    id_modificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido_producto INT NOT NULL,
    id_producto_ingrediente INT NOT NULL,
    tipo ENUM('SIN','EXTRA') NOT NULL,
    ingrediente VARCHAR(100) NOT NULL,
    cantidad DECIMAL(12,2) NOT NULL,
    precio_extra DECIMAL(12,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_pedido_producto) REFERENCES pedido_producto(id_pedido_producto),
    FOREIGN KEY (id_producto_ingrediente) REFERENCES producto_ingrediente(id_producto_ingrediente)
);

CREATE TABLE movimiento_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT,
    id_ingrediente INT,
    id_pedido INT,
    tipo_movimiento ENUM('ENTRADA','SALIDA','AJUSTE') NOT NULL,
    cantidad DECIMAL(12,2) NOT NULL,
    motivo VARCHAR(150),
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_ingrediente) REFERENCES ingrediente(id_ingrediente),
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
);

CREATE TABLE pago_operacion (
    clave VARCHAR(36) PRIMARY KEY,
    solicitud LONGTEXT NOT NULL,
    id_pedido INT UNIQUE,
    comprobante LONGTEXT,
    referencia VARCHAR(100) NOT NULL DEFAULT '',
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
);

-- Cada producto vendible necesita al menos una presentación.
-- Ejemplo de presentación individual para un producto existente:
-- INSERT INTO presentacion_menu(id_producto_principal,nombre,tipo,precio,predeterminada)
-- VALUES (1,'Individual','INDIVIDUAL',41.00,TRUE);
