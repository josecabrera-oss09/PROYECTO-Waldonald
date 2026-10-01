-- ============================================================
-- RECETAS INICIALES PARA PROBAR DESCUENTO DE INGREDIENTES
-- Ejecutar después de nofinal.sql y reparar_nofinal.sql.
-- ============================================================

USE waldonalds;

INSERT IGNORE INTO ingrediente
    (nombre, unidad_medida, stock_actual, stock_minimo, estado)
VALUES
    ('Helado de vainilla', 'PORCION', 100, 10, TRUE),
    ('Cono', 'UNIDAD', 100, 10, TRUE),
    ('Topping M&M', 'GRAMOS', 5000, 500, TRUE),
    ('Fresa', 'GRAMOS', 5000, 500, TRUE),
    ('Vaso de postre', 'UNIDAD', 100, 10, TRUE);

UPDATE ingrediente
SET stock_actual = 100
WHERE nombre IN ('Helado de vainilla', 'Cono', 'Topping M&M', 'Fresa', 'Vaso de postre')
  AND stock_actual = 0;

-- WlCono Vainilla: producto preparado, no stock directo.
UPDATE producto
SET tipo_control_stock = 'RECETA'
WHERE nombre = 'WlCono Vainilla';

INSERT IGNORE INTO receta_producto
    (id_producto, id_ingrediente, cantidad_requerida,
     permite_quitar, permite_extra, precio_extra)
SELECT p.id_producto, i.id_ingrediente, 1.00, FALSE, FALSE, 0.00
FROM producto p
JOIN ingrediente i ON i.nombre = 'Helado de vainilla'
WHERE p.nombre = 'WlCono Vainilla';

INSERT IGNORE INTO receta_producto
    (id_producto, id_ingrediente, cantidad_requerida,
     permite_quitar, permite_extra, precio_extra)
SELECT p.id_producto, i.id_ingrediente, 1.00, FALSE, FALSE, 0.00
FROM producto p
JOIN ingrediente i ON i.nombre = 'Cono'
WHERE p.nombre = 'WlCono Vainilla';

-- WlFlurry M&M's: ejemplo de producto compuesto.
UPDATE producto
SET tipo_control_stock = 'RECETA'
WHERE nombre IN ('WlFlurry M&M''s', 'WlFlurry M&M''s Fresa');

INSERT IGNORE INTO receta_producto
    (id_producto, id_ingrediente, cantidad_requerida)
SELECT p.id_producto, i.id_ingrediente, 1.00
FROM producto p
JOIN ingrediente i ON i.nombre = 'Helado de vainilla'
WHERE p.nombre IN ('WlFlurry M&M''s', 'WlFlurry M&M''s Fresa');

INSERT IGNORE INTO receta_producto
    (id_producto, id_ingrediente, cantidad_requerida)
SELECT p.id_producto, i.id_ingrediente, 20.00
FROM producto p
JOIN ingrediente i ON i.nombre = 'Topping M&M'
WHERE p.nombre IN ('WlFlurry M&M''s', 'WlFlurry M&M''s Fresa');

SELECT
    p.nombre,
    p.tipo_control_stock,
    i.nombre AS ingrediente,
    rp.cantidad_requerida
FROM producto p
JOIN receta_producto rp ON rp.id_producto = p.id_producto
JOIN ingrediente i ON i.id_ingrediente = rp.id_ingrediente
WHERE p.nombre IN ('WlCono Vainilla', 'WlFlurry M&M''s', 'WlFlurry M&M''s Fresa')
ORDER BY p.nombre, i.nombre;
