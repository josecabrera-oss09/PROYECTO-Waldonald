-- Ejecutar una vez en una base existente. No modifica ventas ni almacena datos de tarjeta.
CREATE TABLE IF NOT EXISTS nit_cliente (
    nit VARCHAR(20) NOT NULL PRIMARY KEY,
    ultima_utilizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
