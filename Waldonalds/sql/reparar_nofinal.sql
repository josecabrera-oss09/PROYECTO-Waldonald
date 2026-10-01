-- ============================================================
-- REPARACION DE nofinal.sql
-- Ejecutar sobre una base waldonalds que ya fue creada.
-- No recrea tablas ni borra productos.
-- ============================================================

USE waldonalds;

-- nofinal.sql no contiene INSERT de ingredientes; por eso el panel aparece vacío.
INSERT IGNORE INTO ingrediente
    (nombre, unidad_medida, stock_actual, stock_minimo, estado)
VALUES
    ('Carne de hamburguesa', 'UNIDAD', 100, 10, TRUE),
    ('Pan de hamburguesa', 'UNIDAD', 100, 10, TRUE),
    ('Queso', 'UNIDAD', 100, 10, TRUE),
    ('Lechuga', 'GRAMOS', 10000, 1000, TRUE),
    ('Tomate', 'GRAMOS', 10000, 1000, TRUE),
    ('Salsa especial', 'ML', 10000, 1000, TRUE),
    ('Papas prefritas', 'GRAMOS', 20000, 2000, TRUE),
    ('Jarabe Coca-Cola', 'ML', 10000, 1000, TRUE),
    ('Jarabe Sprite', 'ML', 10000, 1000, TRUE),
    ('Jarabe Fanta', 'ML', 10000, 1000, TRUE),
    ('Hielo', 'GRAMOS', 20000, 2000, TRUE),
    ('Vaso pequeno', 'UNIDAD', 100, 20, TRUE),
    ('Vaso mediano', 'UNIDAD', 100, 20, TRUE),
    ('Vaso grande', 'UNIDAD', 100, 20, TRUE),
    ('Tapa pequena', 'UNIDAD', 100, 20, TRUE),
    ('Tapa mediana', 'UNIDAD', 100, 20, TRUE),
    ('Tapa grande', 'UNIDAD', 100, 20, TRUE),
    ('Pajilla', 'UNIDAD', 100, 20, TRUE);

-- nofinal.sql crea los productos con stock_actual = 0.
-- Inicializa los productos no combo para las pruebas del menú.
SET SQL_SAFE_UPDATES = 0;
UPDATE producto
SET stock_actual = 100
WHERE es_combo = FALSE
  AND stock_actual = 0;
SET SQL_SAFE_UPDATES = 1;

-- nofinal.sql marca los combos, pero no trae sus opciones. Estas opciones
-- iniciales permiten abrir el configurador; si un combo ya tiene opciones
-- activas, no se modifica.
INSERT IGNORE INTO combo_opcion
    (id_combo, id_producto_opcion, grupo, cantidad_incluida,
     es_predeterminado, precio_adicional, estado)
SELECT c.id_producto, o.id_producto, 'PRINCIPAL', 1, TRUE, 0.00, TRUE
FROM producto c JOIN producto o ON o.nombre = CASE
    WHEN LOWER(c.nombre) LIKE '%nugget%' THEN 'WlNuggets'
    WHEN LOWER(c.nombre) LIKE '%pollo%' OR LOWER(c.nombre) LIKE '%crispy%' THEN 'WlCrispy Chicken Deluxe'
    WHEN LOWER(c.nombre) LIKE '%hamburguesa%' OR LOWER(c.nombre) LIKE '%big mac%' THEN 'Big Mac'
    ELSE 'WlNuggets' END
WHERE c.es_combo = TRUE
  AND NOT EXISTS (SELECT 1 FROM combo_opcion x WHERE x.id_combo = c.id_producto AND x.grupo = 'PRINCIPAL' AND x.estado = TRUE);

INSERT IGNORE INTO combo_opcion
    (id_combo, id_producto_opcion, grupo, cantidad_incluida,
     es_predeterminado, precio_adicional, estado)
SELECT c.id_producto, o.id_producto, 'ACOMPANAMIENTO', 1, TRUE, 0.00, TRUE
FROM producto c JOIN producto o ON o.nombre = 'Papas'
WHERE c.es_combo = TRUE
  AND NOT EXISTS (SELECT 1 FROM combo_opcion x WHERE x.id_combo = c.id_producto AND x.grupo = 'ACOMPANAMIENTO' AND x.estado = TRUE);

INSERT IGNORE INTO combo_opcion
    (id_combo, id_producto_opcion, grupo, cantidad_incluida,
     es_predeterminado, precio_adicional, estado)
SELECT c.id_producto, o.id_producto, 'BEBIDA', 1, TRUE, 0.00, TRUE
FROM producto c JOIN producto o ON o.nombre = 'Coca-Cola'
WHERE c.es_combo = TRUE
  AND NOT EXISTS (SELECT 1 FROM combo_opcion x WHERE x.id_combo = c.id_producto AND x.grupo = 'BEBIDA' AND x.estado = TRUE);

SELECT DATABASE() AS base_actual;
SELECT COUNT(*) AS total_ingredientes FROM ingrediente;
SELECT COUNT(*) AS total_productos FROM producto;
