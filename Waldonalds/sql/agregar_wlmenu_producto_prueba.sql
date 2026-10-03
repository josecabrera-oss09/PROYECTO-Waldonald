USE waldonalds;

START TRANSACTION;

-- Producto al que se le agregará la presentación WlMenú.
SET @id_hamburguesa = (
    SELECT id_producto
    FROM producto
    WHERE nombre = 'Hamburguesa de Prueba POS'
      AND estado = TRUE
    ORDER BY id_producto DESC
    LIMIT 1
);

-- Se utiliza la misma categoría activa del producto principal.
SET @id_categoria = (
    SELECT id_categoria
    FROM producto
    WHERE id_producto = @id_hamburguesa
    LIMIT 1
);

-- =====================================================
-- COMPONENTE: PAPAS MEDIANAS
-- =====================================================

SET @id_papas = (
    SELECT id_producto
    FROM producto
    WHERE nombre = 'Papas Medianas WlMenú'
    LIMIT 1
);

INSERT INTO producto (
    id_categoria,
    nombre,
    descripcion,
    precio_base,
    imagen,
    subcategoria,
    disponibilidad_menu,
    tipo_stock,
    personalizable,
    stock_actual,
    stock_minimo,
    estado
)
SELECT
    @id_categoria,
    'Papas Medianas WlMenú',
    'Papas medianas utilizadas como complemento del WlMenú.',
    15.00,
    NULL,
    'Papas',
    'TODO_DIA',
    'DIRECTO',
    FALSE,
    100,
    10,
    TRUE
WHERE @id_papas IS NULL;

SET @id_papas = COALESCE(@id_papas, LAST_INSERT_ID());


-- =====================================================
-- COMPONENTES: BEBIDAS
-- =====================================================

SET @id_coca = (
    SELECT id_producto
    FROM producto
    WHERE nombre = 'Coca Cola Mediana WlMenú'
    LIMIT 1
);

INSERT INTO producto (
    id_categoria,
    nombre,
    descripcion,
    precio_base,
    imagen,
    subcategoria,
    disponibilidad_menu,
    tipo_stock,
    personalizable,
    stock_actual,
    stock_minimo,
    estado
)
SELECT
    @id_categoria,
    'Coca Cola Mediana WlMenú',
    'Bebida mediana para el WlMenú.',
    12.00,
    NULL,
    'Bebidas',
    'TODO_DIA',
    'DIRECTO',
    FALSE,
    100,
    10,
    TRUE
WHERE @id_coca IS NULL;

SET @id_coca = COALESCE(@id_coca, LAST_INSERT_ID());


SET @id_sprite = (
    SELECT id_producto
    FROM producto
    WHERE nombre = 'Sprite Mediana WlMenú'
    LIMIT 1
);

INSERT INTO producto (
    id_categoria,
    nombre,
    descripcion,
    precio_base,
    imagen,
    subcategoria,
    disponibilidad_menu,
    tipo_stock,
    personalizable,
    stock_actual,
    stock_minimo,
    estado
)
SELECT
    @id_categoria,
    'Sprite Mediana WlMenú',
    'Bebida mediana para el WlMenú.',
    12.00,
    NULL,
    'Bebidas',
    'TODO_DIA',
    'DIRECTO',
    FALSE,
    100,
    10,
    TRUE
WHERE @id_sprite IS NULL;

SET @id_sprite = COALESCE(@id_sprite, LAST_INSERT_ID());


-- =====================================================
-- PRESENTACIÓN WLMENÚ
-- =====================================================

SET @id_wlmenu = (
    SELECT id_presentacion
    FROM presentacion_menu
    WHERE id_producto_principal = @id_hamburguesa
      AND tipo = 'MENU'
      AND estado = TRUE
    ORDER BY id_presentacion
    LIMIT 1
);

INSERT INTO presentacion_menu (
    id_producto_principal,
    nombre,
    tipo,
    precio,
    predeterminada,
    estado
)
SELECT
    @id_hamburguesa,
    'WlMenú',
    'MENU',
    55.00,
    FALSE,
    TRUE
WHERE @id_wlmenu IS NULL;

SET @id_wlmenu = COALESCE(@id_wlmenu, LAST_INSERT_ID());


-- =====================================================
-- GRUPO INTERNO: HAMBURGUESA PRINCIPAL
-- =====================================================

SET @id_grupo_principal = (
    SELECT id_grupo
    FROM grupo_presentacion
    WHERE id_presentacion = @id_wlmenu
      AND nombre = 'Producto principal'
    LIMIT 1
);

INSERT INTO grupo_presentacion (
    id_presentacion,
    nombre,
    minimo,
    maximo,
    permite_repetir,
    visible,
    permite_personalizar,
    estado
)
SELECT
    @id_wlmenu,
    'Producto principal',
    1,
    1,
    FALSE,
    FALSE,
    TRUE,
    TRUE
WHERE @id_grupo_principal IS NULL;

SET @id_grupo_principal = COALESCE(
    @id_grupo_principal,
    LAST_INSERT_ID()
);

SET @id_opcion_principal = (
    SELECT id_opcion
    FROM opcion_grupo
    WHERE id_grupo = @id_grupo_principal
      AND nombre = 'Hamburguesa de Prueba POS'
    LIMIT 1
);

INSERT INTO opcion_grupo (
    id_grupo,
    nombre,
    incremento_precio,
    predeterminada,
    estado
)
SELECT
    @id_grupo_principal,
    'Hamburguesa de Prueba POS',
    0.00,
    TRUE,
    TRUE
WHERE @id_opcion_principal IS NULL;

SET @id_opcion_principal = COALESCE(
    @id_opcion_principal,
    LAST_INSERT_ID()
);

INSERT INTO opcion_componente (
    id_opcion,
    id_producto,
    cantidad
)
SELECT
    @id_opcion_principal,
    @id_hamburguesa,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM opcion_componente
    WHERE id_opcion = @id_opcion_principal
      AND id_producto = @id_hamburguesa
);


-- =====================================================
-- GRUPO VISIBLE: COMPLEMENTO
-- =====================================================

SET @id_grupo_complemento = (
    SELECT id_grupo
    FROM grupo_presentacion
    WHERE id_presentacion = @id_wlmenu
      AND nombre = 'Elige el complemento'
    LIMIT 1
);

INSERT INTO grupo_presentacion (
    id_presentacion,
    nombre,
    minimo,
    maximo,
    permite_repetir,
    visible,
    permite_personalizar,
    estado
)
SELECT
    @id_wlmenu,
    'Elige el complemento',
    1,
    1,
    FALSE,
    TRUE,
    FALSE,
    TRUE
WHERE @id_grupo_complemento IS NULL;

SET @id_grupo_complemento = COALESCE(
    @id_grupo_complemento,
    LAST_INSERT_ID()
);

SET @id_opcion_papas = (
    SELECT id_opcion
    FROM opcion_grupo
    WHERE id_grupo = @id_grupo_complemento
      AND nombre = 'Papas medianas'
    LIMIT 1
);

INSERT INTO opcion_grupo (
    id_grupo,
    nombre,
    incremento_precio,
    predeterminada,
    estado
)
SELECT
    @id_grupo_complemento,
    'Papas medianas',
    0.00,
    TRUE,
    TRUE
WHERE @id_opcion_papas IS NULL;

SET @id_opcion_papas = COALESCE(@id_opcion_papas, LAST_INSERT_ID());

INSERT INTO opcion_componente (
    id_opcion,
    id_producto,
    cantidad
)
SELECT
    @id_opcion_papas,
    @id_papas,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM opcion_componente
    WHERE id_opcion = @id_opcion_papas
      AND id_producto = @id_papas
);


-- =====================================================
-- GRUPO VISIBLE: BEBIDA
-- =====================================================

SET @id_grupo_bebida = (
    SELECT id_grupo
    FROM grupo_presentacion
    WHERE id_presentacion = @id_wlmenu
      AND nombre = 'Elige la bebida'
    LIMIT 1
);

INSERT INTO grupo_presentacion (
    id_presentacion,
    nombre,
    minimo,
    maximo,
    permite_repetir,
    visible,
    permite_personalizar,
    estado
)
SELECT
    @id_wlmenu,
    'Elige la bebida',
    1,
    1,
    FALSE,
    TRUE,
    FALSE,
    TRUE
WHERE @id_grupo_bebida IS NULL;

SET @id_grupo_bebida = COALESCE(
    @id_grupo_bebida,
    LAST_INSERT_ID()
);

SET @id_opcion_coca = (
    SELECT id_opcion
    FROM opcion_grupo
    WHERE id_grupo = @id_grupo_bebida
      AND nombre = 'Coca Cola mediana'
    LIMIT 1
);

INSERT INTO opcion_grupo (
    id_grupo,
    nombre,
    incremento_precio,
    predeterminada,
    estado
)
SELECT
    @id_grupo_bebida,
    'Coca Cola mediana',
    0.00,
    TRUE,
    TRUE
WHERE @id_opcion_coca IS NULL;

SET @id_opcion_coca = COALESCE(@id_opcion_coca, LAST_INSERT_ID());

INSERT INTO opcion_componente (
    id_opcion,
    id_producto,
    cantidad
)
SELECT
    @id_opcion_coca,
    @id_coca,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM opcion_componente
    WHERE id_opcion = @id_opcion_coca
      AND id_producto = @id_coca
);


SET @id_opcion_sprite = (
    SELECT id_opcion
    FROM opcion_grupo
    WHERE id_grupo = @id_grupo_bebida
      AND nombre = 'Sprite mediana'
    LIMIT 1
);

INSERT INTO opcion_grupo (
    id_grupo,
    nombre,
    incremento_precio,
    predeterminada,
    estado
)
SELECT
    @id_grupo_bebida,
    'Sprite mediana',
    0.00,
    FALSE,
    TRUE
WHERE @id_opcion_sprite IS NULL;

SET @id_opcion_sprite = COALESCE(@id_opcion_sprite, LAST_INSERT_ID());

INSERT INTO opcion_componente (
    id_opcion,
    id_producto,
    cantidad
)
SELECT
    @id_opcion_sprite,
    @id_sprite,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM opcion_componente
    WHERE id_opcion = @id_opcion_sprite
      AND id_producto = @id_sprite
);

COMMIT;

-- Comprobación final. Deben aparecer dos filas: Individual y WlMenú.
SELECT
    pm.id_presentacion,
    pm.nombre,
    pm.tipo,
    pm.precio,
    pm.predeterminada,
    pm.estado
FROM presentacion_menu pm
WHERE pm.id_producto_principal = @id_hamburguesa
ORDER BY pm.tipo DESC;
