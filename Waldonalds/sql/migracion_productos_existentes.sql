USE waldonalds;

-- Crea una presentación individual para los productos anteriores a este cambio.
-- No duplica productos que ya tengan alguna presentación configurada.
INSERT INTO presentacion_menu
    (id_producto_principal, nombre, tipo, precio, predeterminada, estado)
SELECT p.id_producto, 'Individual', 'INDIVIDUAL', p.precio_base, TRUE, TRUE
FROM producto p
WHERE NOT EXISTS (
    SELECT 1 FROM presentacion_menu pm
    WHERE pm.id_producto_principal = p.id_producto
);

-- Grupo interno que permite tratar al producto principal igual que a cualquier
-- producto incluido en un menú o combo.
INSERT INTO grupo_presentacion
    (id_presentacion, nombre, minimo, maximo, permite_repetir,
     visible, permite_personalizar, estado)
SELECT pm.id_presentacion, '__Producto principal', 1, 1, FALSE, FALSE, TRUE, TRUE
FROM presentacion_menu pm
WHERE pm.tipo = 'INDIVIDUAL'
AND NOT EXISTS (
    SELECT 1 FROM grupo_presentacion gp
    WHERE gp.id_presentacion = pm.id_presentacion
      AND gp.nombre = '__Producto principal'
);

INSERT INTO opcion_grupo
    (id_grupo, nombre, incremento_precio, predeterminada, estado)
SELECT gp.id_grupo, p.nombre, 0, TRUE, TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion = gp.id_presentacion
JOIN producto p ON p.id_producto = pm.id_producto_principal
WHERE gp.nombre = '__Producto principal'
AND NOT EXISTS (
    SELECT 1 FROM opcion_grupo og WHERE og.id_grupo = gp.id_grupo
);

INSERT INTO opcion_componente (id_opcion, id_producto, cantidad)
SELECT og.id_opcion, pm.id_producto_principal, 1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo = og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion = gp.id_presentacion
WHERE gp.nombre = '__Producto principal'
AND NOT EXISTS (
    SELECT 1 FROM opcion_componente oc WHERE oc.id_opcion = og.id_opcion
);
