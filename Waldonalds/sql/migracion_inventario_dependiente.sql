-- ============================================================
-- WALDONALDS - MIGRACION DE INVENTARIO DEPENDIENTE
-- Ejecutar sobre la base waldonalds ya existente.
-- Compatible con instalaciones antiguas de MySQL.
-- ============================================================

USE waldonalds;

DROP PROCEDURE IF EXISTS migrar_inventario_dependiente;

DELIMITER $$

CREATE PROCEDURE migrar_inventario_dependiente()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'producto'
          AND COLUMN_NAME = 'tipo_control_stock'
    ) THEN
        ALTER TABLE producto
            ADD COLUMN tipo_control_stock ENUM('DIRECTO','RECETA','COMBO')
                NOT NULL DEFAULT 'DIRECTO'
                AFTER precio_base;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'movimiento_inventario'
          AND COLUMN_NAME = 'id_detalle'
    ) THEN
        ALTER TABLE movimiento_inventario
            ADD COLUMN id_detalle INT NULL AFTER id_pedido;
    END IF;
END$$

DELIMITER ;

CALL migrar_inventario_dependiente();
DROP PROCEDURE migrar_inventario_dependiente;

SET SQL_SAFE_UPDATES = 0;

UPDATE producto
SET tipo_control_stock = CASE
    WHEN es_combo = TRUE THEN 'COMBO'
    ELSE 'DIRECTO'
END;

UPDATE producto AS p
SET tipo_control_stock = 'RECETA'
WHERE p.es_combo = FALSE
  AND EXISTS (
      SELECT 1 FROM receta_producto rp
      WHERE rp.id_producto = p.id_producto
  );

SET SQL_SAFE_UPDATES = 1;

DROP VIEW IF EXISTS vista_stock_disponible;
DROP VIEW IF EXISTS vista_stock_combo;
DROP VIEW IF EXISTS vista_stock_producto_base;

CREATE VIEW vista_stock_producto_base AS
SELECT
    p.id_producto,
    p.nombre,
    p.tipo_control_stock,
    p.stock_minimo,
    CASE
        WHEN p.tipo_control_stock = 'DIRECTO' THEN p.stock_actual
        WHEN p.tipo_control_stock = 'RECETA' THEN COALESCE((
            SELECT FLOOR(MIN(i.stock_actual / rp.cantidad_requerida))
            FROM receta_producto rp
            INNER JOIN ingrediente i
                ON i.id_ingrediente = rp.id_ingrediente
            WHERE rp.id_producto = p.id_producto
              AND i.estado = TRUE
        ), 0)
        ELSE NULL
    END AS stock_disponible
FROM producto p
WHERE p.tipo_control_stock IN ('DIRECTO', 'RECETA');

CREATE VIEW vista_stock_combo AS
SELECT
    c.id_producto,
    c.nombre,
    c.tipo_control_stock,
    c.stock_minimo,
    COALESCE(MIN(FLOOR(v.stock_disponible / co.cantidad_incluida)), 0)
        AS stock_disponible
FROM producto c
INNER JOIN combo_opcion co
    ON co.id_combo = c.id_producto
   AND co.estado = TRUE
INNER JOIN vista_stock_producto_base v
    ON v.id_producto = co.id_producto_opcion
WHERE c.tipo_control_stock = 'COMBO'
GROUP BY c.id_producto, c.nombre, c.tipo_control_stock, c.stock_minimo;

CREATE VIEW vista_stock_disponible AS
SELECT id_producto, nombre, tipo_control_stock, stock_minimo, stock_disponible
FROM vista_stock_producto_base
UNION ALL
SELECT id_producto, nombre, tipo_control_stock, stock_minimo, stock_disponible
FROM vista_stock_combo;

SELECT * FROM vista_stock_disponible ORDER BY nombre;
