-- Ejecutar una vez en la base waldonalds. No modifica las ventas existentes.
CREATE TABLE IF NOT EXISTS pago_operacion (
    clave CHAR(36) NOT NULL PRIMARY KEY,
    solicitud TEXT NOT NULL,
    id_pedido INT NULL UNIQUE,
    comprobante TEXT NULL,
    referencia VARCHAR(100) NOT NULL DEFAULT '',
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
