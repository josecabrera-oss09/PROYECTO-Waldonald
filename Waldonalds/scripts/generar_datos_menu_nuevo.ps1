param(
    [Parameter(Mandatory = $true)]
    [string]$SourcePath,

    [string]$OutputPath = "sql/datos_menu_nueva_base.sql"
)

$ErrorActionPreference = "Stop"

function Escape-Sql([string]$Value) {
    if ($null -eq $Value) { return "" }
    return $Value.Replace("'", "''")
}

function Get-StockType($Product) {
    if ($Product.Categoria -eq "Cajita Feliz" -or
        $Product.Nombre -match "^(Caja Grande|Bucket |Caja de 24)") {
        return "NINGUNO"
    }
    if ($Product.Categoria -eq "Antojos" -and
        $Product.Nombre -match "Derretido|Tostado") {
        return "RECETA"
    }
    if ($Product.Categoria -eq "Bebidas" -and
        $Product.Nombre -match "Café|Chocolate|Té caliente|WlFizz|WlFrizz") {
        return "RECETA"
    }
    if ($Product.Categoria -eq "Desayunos") {
        return "RECETA"
    }
    if ($Product.Categoria -eq "Almuerzos" -and
        $Product.Nombre -notmatch "WlNuggets|Nuggets|Pollo WlCrispy (1 Pieza|Dos Piezas|10 Piezas)$|^Pollo WlCrispy$") {
        return "RECETA"
    }
    if ($Product.Categoria -eq "Postres" -and
        $Product.Nombre -match "WlCono|WlFlurry|Sundae") {
        return "RECETA"
    }
    if ($Product.Categoria -eq "WlCafé" -and
        $Product.Subcategoria -notmatch "Postres|Café en bolsa") {
        return "RECETA"
    }
    return "DIRECTO"
}

$source = Get-Content -LiteralPath $SourcePath -Raw
$pattern = "\(\(SELECT id_categoria FROM categoria WHERE nombre = '([^']+)'\),\s*'((?:''|[^'])*)',\s*'((?:''|[^'])*)',\s*'((?:''|[^'])*)',\s*([0-9.]+),\s*'((?:''|[^'])*)',\s*'([A-Z_]+)',\s*NULL,\s*(TRUE|FALSE),\s*([0-9]+),\s*([0-9]+),\s*TRUE\)"

$products = [regex]::Matches(
    $source,
    $pattern,
    [System.Text.RegularExpressions.RegexOptions]::Singleline
) | ForEach-Object {
    [pscustomobject]@{
        Categoria = $_.Groups[1].Value
        Subcategoria = $_.Groups[2].Value.Replace("''", "'")
        Nombre = $_.Groups[3].Value.Replace("''", "'")
        Descripcion = $_.Groups[4].Value.Replace("''", "'")
        Precio = $_.Groups[5].Value
        Imagen = $_.Groups[6].Value
        Horario = $_.Groups[7].Value
    }
} | Group-Object Categoria, Nombre | ForEach-Object {
    # Cuando el archivo viejo repite un producto, se conserva su última versión.
    $_.Group[-1]
} | Sort-Object Categoria, Nombre

$imageCorrections = @{
    "Bacon Cheddar WlMelt" = "/Imagenes/HAMBURGUESAS/bacon_cheddar_mcmelt.png"
    "Bucket Pollo WlCrispy Para Tres" = "/Imagenes/PARA COMPARTIR/Bucket Pollo McCrispy ® Para Tres.png"
    "Bucket Pollo WlCrispy Snack" = "/Imagenes/PARA COMPARTIR/Bucket Pollo McCrispy ® Snack.png"
    "Café guatemalteco" = "/Imagenes/wlcafe/cafe_guatemalteco.png"
    "Caja Grande con Postre" = "/Imagenes/para compartir/caja grande con postre (1).png"
    "Clásica Gourmet" = "/Imagenes/HAMBURGUESAS/clasica gourmet res.png"
    "Clásica Gourmet Doble Res Doble" = "/Imagenes/HAMBURGUESAS/clasica_gourmet_doble_res_doble.png"
    "Coca-Cola Zero" = "/Imagenes/BEBIDAS/coca_cola_zero.png"
    "Cuarto de Libra Bacon Doble con Queso" = "/Imagenes/HAMBURGUESAS/cuarto_de_libra_bacon_doble_queso.png"
    "Frappé Caramelo" = "/Imagenes/wlcafe/frappé caramelo.png"
    "Frappé Vainilla" = "/Imagenes/wlcafe/Frappé Vainilla.png"
    "Pollo WlCrispy Dos Piezas" = "/Imagenes/HAMBURGUESAS/pollowlcrispy _dospiezas.png"
    "Res" = "/Imagenes/HAMBURGUESAS/hamburguesa.png"
    "Té Guatemalteco Bora Bora" = "/Imagenes/wlcafe/te_guatemalteco_borabora.png"
    "Té Guatemalteco Melocotón Mix" = "/Imagenes/wlcafe/te_guatealteco_melocoton_mix.png"
    "Té Guatemalteco Menta Fusión" = "/Imagenes/wlcafe/te_guatemalteco_menta_fusion.png"
    "Té Guatemalteco Vainilla Relax" = "/Imagenes/wlcafe/teguatemalteco_vainilla_relax.png"
    "WlCrispy Chicken Deluxe" = "/Imagenes/HAMBURGUESAS/WlCrispy_Chicken_Deluxe.png"
    "WlFizz A.M." = "/Imagenes/wlcafe/wlfizz_A.M..png"
    "WlGriddle Tocino Huevo" = "/Imagenes/DESAYUNOS/wlgriddle_tocinohuevo.png"
    "WlMuffin de Doble Huevo" = "/Imagenes/DESAYUNOS/eggwlmuffin_doble_huevo.png"
    "WlMuffin Salchicha Doble y Huevo" = "/Imagenes/DESAYUNOS/wlMuffi_Salchich_Doble_y_Huevo.png"
    "WlMuffin Salchicha y doble huevo" = "/Imagenes/DESAYUNOS/wlMuffin_Salchicha_y_doble huevo.png"
}

$productRows = New-Object System.Collections.Generic.List[string]
foreach ($product in $products) {
    $stockType = Get-StockType $product
    $customizable = if ($stockType -eq "DIRECTO") { "FALSE" } else { "TRUE" }
    $stock = if ($stockType -eq "DIRECTO") { 100 } else { 0 }
    $minimumStock = if ($stockType -eq "DIRECTO") { 10 } else { 0 }
    $image = if ($imageCorrections.ContainsKey($product.Nombre)) {
        $imageCorrections[$product.Nombre]
    } else {
        $product.Imagen -replace "^/imagenes/", "/Imagenes/"
    }
    $productRows.Add((
        "('{0}','{1}','{2}','{3}',{4},'{5}','{6}','{7}',{8},{9},{10})" -f
        (Escape-Sql $product.Categoria),
        (Escape-Sql $product.Subcategoria),
        (Escape-Sql $product.Nombre),
        (Escape-Sql $product.Descripcion),
        $product.Precio,
        (Escape-Sql $image),
        $product.Horario,
        $stockType,
        $customizable,
        $stock,
        $minimumStock
    ))
}

# Productos auxiliares que no estaban en el catálogo viejo pero son necesarios
# para construir una Cajita o una caja familiar real.
$productRows.Add("('Cajita Feliz','Complementos','Papas Kids','Porción infantil de papas.',10.00,'/Imagenes/ANTOJOS/papas.png','TODO_DIA','DIRECTO',FALSE,200,20)")
$productRows.Add("('Cajita Feliz','Complementos','Puré de manzana','Puré de manzana para menú infantil.',9.00,'/Imagenes/BEBIDAS/jugo_manzana.png','TODO_DIA','DIRECTO',FALSE,150,15)")
$productRows.Add("('Cajita Feliz','Complementos','Yogur de fresa','Yogur de fresa para menú infantil.',9.00,NULL,'TODO_DIA','DIRECTO',FALSE,150,15)")
$productRows.Add("('Cajita Feliz','Bebidas','Jugo de manzana Kids','Jugo de manzana en tamaño infantil.',8.00,'/Imagenes/BEBIDAS/jugo_manzana.png','TODO_DIA','DIRECTO',FALSE,150,15)")
$productRows.Add("('Cajita Feliz','Juguetes','Juguete sorpresa','Juguete disponible para la promoción infantil.',12.00,NULL,'TODO_DIA','DIRECTO',FALSE,200,20)")
$productRows.Add("('Bebidas','Sodas','Coca-Cola 1.5 L','Bebida familiar para cajas y combos.',25.00,'/Imagenes/BEBIDAS/coca_cola.png','TODO_DIA','DIRECTO',FALSE,100,10)")

$header = @'
-- ============================================================
-- DATOS COMPLETOS PARA LA NUEVA BASE WALDONALDS
-- Generado desde los INSERT antiguos y adaptado al modelo de
-- recetas, presentaciones, WlMenús, Cajitas y combos.
-- ============================================================

USE waldonalds;

START TRANSACTION;

-- ============================================================
-- 1. CATEGORÍAS
-- ============================================================

INSERT INTO categoria (nombre, estado)
SELECT datos.nombre, TRUE
FROM (
    SELECT 'Antojos' AS nombre
    UNION ALL SELECT 'Bebidas'
    UNION ALL SELECT 'Cajita Feliz'
    UNION ALL SELECT 'Desayunos'
    UNION ALL SELECT 'Almuerzos'
    UNION ALL SELECT 'Postres'
    UNION ALL SELECT 'WlCafé'
) datos
WHERE NOT EXISTS (
    SELECT 1 FROM categoria c WHERE c.nombre = datos.nombre
);

UPDATE categoria
SET estado = TRUE
WHERE nombre IN ('Antojos','Bebidas','Cajita Feliz','Desayunos',
                 'Almuerzos','Postres','WlCafé');

-- ============================================================
-- 2. PRODUCTOS
-- Se usa una tabla temporal para evitar duplicados si el archivo
-- se ejecuta más de una vez.
-- ============================================================

DROP TEMPORARY TABLE IF EXISTS carga_producto;

CREATE TEMPORARY TABLE carga_producto (
    categoria VARCHAR(100),
    subcategoria VARCHAR(100),
    nombre VARCHAR(150),
    descripcion VARCHAR(500),
    precio DECIMAL(12,2),
    imagen VARCHAR(500),
    disponibilidad VARCHAR(20),
    tipo_stock VARCHAR(20),
    personalizable BOOLEAN,
    stock_actual INT,
    stock_minimo INT
);

INSERT INTO carga_producto (
    categoria, subcategoria, nombre, descripcion, precio, imagen,
    disponibilidad, tipo_stock, personalizable, stock_actual, stock_minimo
)
VALUES
'@

$afterProducts = @'

INSERT INTO producto (
    id_categoria, nombre, descripcion, precio_base, imagen, subcategoria,
    disponibilidad_menu, tipo_stock, personalizable,
    stock_actual, stock_minimo, estado
)
SELECT
    c.id_categoria,
    cp.nombre,
    cp.descripcion,
    cp.precio,
    cp.imagen,
    cp.subcategoria,
    cp.disponibilidad,
    cp.tipo_stock,
    cp.personalizable,
    cp.stock_actual,
    cp.stock_minimo,
    TRUE
FROM carga_producto cp
INNER JOIN categoria c ON c.nombre = cp.categoria
WHERE NOT EXISTS (
    SELECT 1
    FROM producto p
    WHERE p.id_categoria = c.id_categoria
      AND p.nombre = cp.nombre
);

-- Si el producto ya existía, se actualizan sus campos comerciales.
UPDATE producto p
INNER JOIN categoria c ON c.id_categoria = p.id_categoria
INNER JOIN carga_producto cp
        ON cp.categoria = c.nombre AND cp.nombre = p.nombre
SET p.descripcion = cp.descripcion,
    p.precio_base = cp.precio,
    p.imagen = cp.imagen,
    p.subcategoria = cp.subcategoria,
    p.disponibilidad_menu = cp.disponibilidad,
    p.tipo_stock = cp.tipo_stock,
    p.personalizable = cp.personalizable,
    p.stock_actual = CASE
        WHEN cp.tipo_stock = 'DIRECTO' AND p.stock_actual = 0
            THEN cp.stock_actual
        ELSE p.stock_actual
    END,
    p.stock_minimo = cp.stock_minimo,
    p.estado = TRUE;

DROP TEMPORARY TABLE carga_producto;

-- ============================================================
-- 3. INGREDIENTES
-- Las cantidades están expresadas según unidad_medida.
-- ============================================================

DROP TEMPORARY TABLE IF EXISTS carga_ingrediente;

CREATE TEMPORARY TABLE carga_ingrediente (
    nombre VARCHAR(100),
    unidad VARCHAR(30),
    stock DECIMAL(12,2),
    minimo DECIMAL(12,2)
);

INSERT INTO carga_ingrediente (nombre, unidad, stock, minimo) VALUES
('Pan hamburguesa','unidad',1000,100),
('Pan muffin','unidad',800,80),
('Pan WlGriddle','unidad',800,80),
('Pan tostado','rebanada',1500,150),
('Tortilla de harina','unidad',1000,100),
('Tortilla de maíz','unidad',1500,150),
('Carne de res','unidad',1500,150),
('Filete de pollo','unidad',1000,100),
('Huevo','unidad',2000,200),
('Salchicha desayuno','unidad',1000,100),
('Jamón','rebanada',1000,100),
('Tocino','tira',1500,150),
('Queso americano','rebanada',2500,250),
('Queso cheddar','rebanada',1000,100),
('Lechuga','gramo',20000,2000),
('Tomate','rodaja',3000,300),
('Cebolla','gramo',12000,1200),
('Pepinillo','rodaja',5000,500),
('Ketchup','mililitro',20000,2000),
('Mostaza','mililitro',15000,1500),
('Mayonesa','mililitro',15000,1500),
('Salsa Big Mac','mililitro',10000,1000),
('Salsa Big Tasty','mililitro',10000,1000),
('Salsa ranch','mililitro',8000,800),
('Salsa ahumada','mililitro',8000,800),
('Guacamole','gramo',8000,800),
('Pico de gallo','gramo',8000,800),
('Frijol','gramo',15000,1500),
('Loroco','gramo',5000,500),
('Vegetales mixtos','gramo',12000,1200),
('Plátano','gramo',10000,1000),
('Crema','mililitro',10000,1000),
('Masa de hot cake','porcion',1500,150),
('Mantequilla','gramo',8000,800),
('Miel','mililitro',10000,1000),
('Café molido','gramo',20000,2000),
('Agua preparada','mililitro',100000,10000),
('Leche','mililitro',80000,8000),
('Chocolate en polvo','gramo',15000,1500),
('Bolsa de té','unidad',2000,200),
('Mezcla chai','gramo',10000,1000),
('Hielo','gramo',100000,10000),
('Jarabe caramelo','mililitro',12000,1200),
('Jarabe chocolate','mililitro',12000,1200),
('Jarabe fresa','mililitro',12000,1200),
('Jarabe vainilla','mililitro',12000,1200),
('Jarabe frutal','mililitro',12000,1200),
('Base horchata','mililitro',15000,1500),
('Pulpa de mango','gramo',12000,1200),
('Pulpa de berries','gramo',12000,1200),
('Helado de vainilla','gramo',50000,5000),
('Cono','unidad',1500,150),
('Galleta Oreo','gramo',12000,1200),
('Chocolate M&M','gramo',12000,1200);

INSERT INTO ingrediente (
    nombre, unidad_medida, stock_actual, stock_minimo, estado
)
SELECT ci.nombre, ci.unidad, ci.stock, ci.minimo, TRUE
FROM carga_ingrediente ci
WHERE NOT EXISTS (
    SELECT 1 FROM ingrediente i WHERE i.nombre = ci.nombre
);

UPDATE ingrediente i
INNER JOIN carga_ingrediente ci ON ci.nombre = i.nombre
SET i.unidad_medida = ci.unidad,
    i.stock_actual = CASE WHEN i.stock_actual = 0 THEN ci.stock ELSE i.stock_actual END,
    i.stock_minimo = ci.minimo,
    i.estado = TRUE;

DROP TEMPORARY TABLE carga_ingrediente;

-- ============================================================
-- 4. RECETAS
-- Se cargan primero en una tabla temporal para fusionar reglas
-- repetidas antes de insertarlas en producto_ingrediente.
-- ============================================================

DROP TEMPORARY TABLE IF EXISTS carga_receta;

CREATE TEMPORARY TABLE carga_receta (
    id_producto INT,
    id_ingrediente INT,
    cantidad DECIMAL(12,2),
    permite_quitar BOOLEAN,
    permite_extra BOOLEAN,
    cantidad_extra DECIMAL(12,2),
    precio_extra DECIMAL(12,2),
    max_extras INT
);

-- Tostados y derretidos.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan tostado'
WHERE p.tipo_stock='RECETA' AND c.nombre IN ('Antojos','WlCafé')
  AND (p.nombre LIKE '%Tostado%' OR p.nombre LIKE '%Derretido%');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,3.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso americano'
WHERE p.tipo_stock='RECETA' AND c.nombre IN ('Antojos','WlCafé')
  AND (p.nombre LIKE '%Tostado%' OR p.nombre LIKE '%Derretido%');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,40,TRUE,TRUE,20,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Frijol'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Frijol%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Loroco'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%loroco%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jamón'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%jamón%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,1.50,3
FROM producto p JOIN ingrediente i ON i.nombre='Tomate'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tomate%';

-- Hamburguesas y sándwiches de almuerzo.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan hamburguesa'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE
         WHEN p.nombre='Doble Big Mac' THEN 4
         WHEN p.nombre='Big Mac' THEN 2
         WHEN p.nombre LIKE '%Triple%' THEN 3
         WHEN p.nombre LIKE '%Doble%' OR p.nombre='WlDouble' THEN 2
         ELSE 1
       END,
       FALSE,TRUE,1,12.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Carne de res'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT LIKE '%Pollo%' AND p.nombre NOT LIKE '%Crispy%'
  AND p.nombre NOT LIKE '%Sándwich%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' THEN 2 ELSE 1 END,
       FALSE,TRUE,1,10.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Filete de pollo'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Pollo%' OR p.nombre LIKE '%Crispy%'
       OR p.nombre LIKE '%Sándwich%');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' OR p.nombre LIKE '%Triple%' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,3.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso americano'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT LIKE '%Cheddar%'
  AND (p.descripcion LIKE '%queso%'
       OR p.nombre REGEXP 'Queso|WlMelt|Big Mac|Big Tasty|WlNífica|WlDouble|Triple Bacon|Pico Guacamol|Smoke Tocino|Gourmet');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,3.50,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso cheddar'
WHERE c.nombre IN ('Almuerzos','Desayunos') AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Cheddar%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' OR p.nombre LIKE '%Triple%' THEN 3 ELSE 2 END,
       TRUE,TRUE,1,4.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Tocino'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Bacon%' OR p.nombre LIKE '%Tocino%');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Lechuga'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Big|Tasty|Deluxe|Gourmet|WlNífica|Pollo|Crispy|Res|Sándwich';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,1.50,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Tomate'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Tasty|Deluxe|Gourmet|WlNífica|Pollo|Crispy|Res|Sándwich'
  AND p.nombre NOT LIKE '%Big Mac%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,10,TRUE,TRUE,5,0.75,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Cebolla'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT REGEXP 'Pollo|Crispy|Sándwich';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,1.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pepinillo'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT REGEXP 'Pollo|Crispy|Sándwich|Gourmet|Tasty';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,12,TRUE,TRUE,6,0.75,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Ketchup'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Hamburguesa|Cuarto|Triple|WlDouble|Quesoburguesa';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,8,TRUE,TRUE,4,0.75,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Mostaza'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Hamburguesa|Cuarto|Triple|WlDouble|Quesoburguesa';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,15,TRUE,TRUE,8,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Mayonesa'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Pollo|Crispy|Sándwich|Deluxe';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa Big Mac'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Big Mac%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa Big Tasty'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Big Tasty%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,35,TRUE,TRUE,20,4.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Guacamole'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Guacamol%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,TRUE,TRUE,15,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Pico de gallo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Pico%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa ahumada'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Smoke%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa ranch'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Ranch%';

-- Desayunos: WlMuffin y WlGriddle.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan muffin'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%WlMuffin%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan WlGriddle'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%WlGriddle%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE
         WHEN p.nombre='Burritos' THEN 2
         WHEN p.nombre LIKE '%Doble Huevo%'
           OR p.nombre LIKE '%Doble de Huevo%'
           OR p.nombre LIKE '%doble huevo%' THEN 2
         ELSE 1
       END,
       TRUE,TRUE,1,4.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Huevo'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Huevo%' OR p.descripcion LIKE '%huevo%'
       OR p.nombre LIKE '%Chapín%'
       OR p.nombre IN ('Burrito','Burritos','Desayuno clásico',
                       'Desayuno Deluxe','Desayuno tradicional'));

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' OR p.nombre='Burritos' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,5.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Salchicha desayuno'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Salchicha%' OR p.descripcion LIKE '%salchicha%'
       OR p.nombre LIKE '%Burrito%'
       OR p.nombre IN ('Desayuno clásico','Desayuno Deluxe'));

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,TRUE,TRUE,1,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Jamón'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Jamón%' OR p.descripcion LIKE '%jamón%');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,4.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Tocino'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Tocino%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,TRUE,TRUE,1,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso americano'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.descripcion LIKE '%queso%' OR p.nombre LIKE '%Queso%'
       OR p.nombre LIKE '%Cheddar%')
  AND p.nombre NOT LIKE '%Cheddar%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,40,TRUE,TRUE,20,2.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Frijol'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Chapín%';

-- Burritos.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre='Burritos' THEN 2 ELSE 1 END,FALSE,FALSE,1,0,1
FROM producto p JOIN ingrediente i ON i.nombre='Tortilla de harina'
WHERE p.tipo_stock='RECETA' AND p.nombre IN ('Burrito','Burritos');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre='Burritos' THEN 60 ELSE 30 END,
       TRUE,TRUE,20,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Vegetales mixtos'
WHERE p.tipo_stock='RECETA' AND p.nombre IN ('Burrito','Burritos');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre='Burritos' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Queso americano'
WHERE p.tipo_stock='RECETA' AND p.nombre IN ('Burrito','Burritos');

-- Hot Cakes y platos de desayuno.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,3,FALSE,TRUE,1,5.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Masa de hot cake'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Hot Cakes','Desayuno Deluxe');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,10,TRUE,TRUE,5,1.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Mantequilla'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Hot Cakes','Desayuno Deluxe');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,TRUE,TRUE,15,1.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Miel'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Hot Cakes','Desayuno Deluxe');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,2,FALSE,FALSE,1,0,1
FROM producto p JOIN ingrediente i ON i.nombre='Pan tostado'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Desayuno clásico','Desayuno Deluxe');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,80,TRUE,TRUE,40,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Frijol'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,80,TRUE,TRUE,40,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Plátano'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,TRUE,TRUE,15,1.50,2
FROM producto p JOIN ingrediente i ON i.nombre='Crema'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,3,FALSE,TRUE,1,1.00,3
FROM producto p JOIN ingrediente i ON i.nombre='Tortilla de maíz'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,TRUE,TRUE,1,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Queso americano'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%';

-- Bebidas calientes del catálogo general.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,18,FALSE,TRUE,9,2.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Café molido'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Café%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,250,FALSE,FALSE,50,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Agua preparada'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Café|Chocolate|Té caliente';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,180,FALSE,FALSE,50,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Agua preparada'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'WlFizz|WlFrizz';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,160,FALSE,TRUE,60,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Hielo'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'WlFizz|WlFrizz';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,150,FALSE,TRUE,50,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Leche'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'con leche|Chocolate';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Chocolate en polvo'
WHERE p.tipo_stock='RECETA' AND p.nombre='Chocolate';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,FALSE,TRUE,1,1.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Bolsa de té'
WHERE p.tipo_stock='RECETA' AND p.nombre='Té caliente';

-- WlCafé: café, frappés, bebidas frías, té y smoothies.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,18,FALSE,TRUE,9,2.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Café molido'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Café|Coffee|Capuccino|Latte|Frappé';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,180,FALSE,FALSE,50,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Agua preparada'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.subcategoria IN ('Bebidas Calientes','Bebidas Frías');

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,180,FALSE,TRUE,50,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Leche'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Capuccino|Chocolate|Frappé|Coffee|Latte|Chai';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,160,FALSE,TRUE,60,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Hielo'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.subcategoria='Bebidas Frías';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Chocolate en polvo'
WHERE p.tipo_stock='RECETA' AND p.nombre='Chocolate caliente';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe caramelo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Caramelo%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe chocolate'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Chocolate%'
  AND p.nombre<>'Chocolate caliente';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,20,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe vainilla'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Vainilla%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Galleta Oreo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Oreo%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,200,FALSE,TRUE,50,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Base horchata'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Horchata%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,120,FALSE,TRUE,40,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Pulpa de mango'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Mango%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,120,FALSE,TRUE,40,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Pulpa de berries'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Berries%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Mezcla chai'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Chai%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,FALSE,TRUE,1,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Bolsa de té'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Té Guatemalteco%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe frutal'
WHERE p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'WlFizz|WlFrizz|Bora Bora|Melocotón|Menta';

-- Helados y postres preparados.
INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%WlFlurry%' THEN 180 ELSE 120 END,
       FALSE,TRUE,60,5.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Helado de vainilla'
WHERE c.nombre='Postres' AND p.tipo_stock='RECETA';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN ingrediente i ON i.nombre='Cono'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Cono%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Galleta Oreo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%WlFlurry Oreo%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Chocolate M&M'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%M&M%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe caramelo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Caramelo%';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe chocolate'
WHERE p.tipo_stock='RECETA' AND p.nombre REGEXP 'Sundae Chocolate|WlFlurry.*Chocolate';

INSERT INTO carga_receta
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe fresa'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Fresa%';

-- Inserción final de recetas, evitando pares repetidos.
INSERT INTO producto_ingrediente (
    id_producto, id_ingrediente, cantidad_default,
    permite_quitar, permite_extra, cantidad_extra,
    precio_extra, max_extras, estado
)
SELECT
    receta.id_producto,
    receta.id_ingrediente,
    receta.cantidad,
    receta.permite_quitar,
    receta.permite_extra,
    receta.cantidad_extra,
    receta.precio_extra,
    receta.max_extras,
    TRUE
FROM (
    SELECT
        id_producto,
        id_ingrediente,
        MAX(cantidad) AS cantidad,
        MAX(permite_quitar) AS permite_quitar,
        MAX(permite_extra) AS permite_extra,
        MAX(cantidad_extra) AS cantidad_extra,
        MAX(precio_extra) AS precio_extra,
        MAX(max_extras) AS max_extras
    FROM carga_receta
    GROUP BY id_producto, id_ingrediente
) receta
WHERE NOT EXISTS (
    SELECT 1
    FROM producto_ingrediente pi
    WHERE pi.id_producto = receta.id_producto
      AND pi.id_ingrediente = receta.id_ingrediente
);

DROP TEMPORARY TABLE carga_receta;

-- ============================================================
-- 5. PRESENTACIÓN INDIVIDUAL
-- ============================================================

INSERT INTO presentacion_menu (
    id_producto_principal, nombre, tipo, precio, predeterminada, estado
)
SELECT p.id_producto,'Individual','INDIVIDUAL',p.precio_base,TRUE,TRUE
FROM producto p
WHERE p.estado=TRUE AND p.tipo_stock<>'NINGUNO'
  AND p.nombre NOT IN ('Papas Kids','Puré de manzana','Yogur de fresa',
                       'Jugo de manzana Kids','Juguete sorpresa','Coca-Cola 1.5 L')
  AND NOT EXISTS (
      SELECT 1 FROM presentacion_menu pm
      WHERE pm.id_producto_principal=p.id_producto
        AND pm.tipo='INDIVIDUAL'
  );

INSERT INTO grupo_presentacion (
    id_presentacion,nombre,minimo,maximo,permite_repetir,
    visible,permite_personalizar,estado
)
SELECT pm.id_presentacion,'Producto principal',1,1,FALSE,FALSE,TRUE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='INDIVIDUAL' AND pm.estado=TRUE
  AND NOT EXISTS (
      SELECT 1 FROM grupo_presentacion gp
      WHERE gp.id_presentacion=pm.id_presentacion
        AND gp.nombre='Producto principal'
  );

INSERT INTO opcion_grupo (
    id_grupo,nombre,incremento_precio,predeterminada,estado
)
SELECT gp.id_grupo,p.nombre,0,TRUE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='INDIVIDUAL' AND gp.nombre='Producto principal'
  AND NOT EXISTS (
      SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo
  );

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,pm.id_producto_principal,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
WHERE pm.tipo='INDIVIDUAL' AND gp.nombre='Producto principal'
  AND NOT EXISTS (
      SELECT 1 FROM opcion_componente oc
      WHERE oc.id_opcion=og.id_opcion
        AND oc.id_producto=pm.id_producto_principal
  );

-- ============================================================
-- 6. WLMENÚS DE ALMUERZO Y DESAYUNO
-- ============================================================

-- Almuerzos: precio individual + Q20.
INSERT INTO presentacion_menu (
    id_producto_principal,nombre,tipo,precio,predeterminada,estado
)
SELECT p.id_producto,'WlMenú','MENU',p.precio_base+20,FALSE,TRUE
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
WHERE c.nombre='Almuerzos' AND p.estado=TRUE
  AND p.subcategoria<>'Para compartir'
  AND p.nombre NOT LIKE '%10 Piezas%'
  AND p.nombre NOT LIKE '%Dos Piezas%'
  AND p.nombre NOT LIKE '%1 Pieza%'
  AND p.nombre<>'Pollo WlCrispy'
  AND NOT EXISTS (
      SELECT 1 FROM presentacion_menu pm
      WHERE pm.id_producto_principal=p.id_producto AND pm.tipo='MENU'
  );

-- Desayunos portátiles: precio individual + Q15.
INSERT INTO presentacion_menu (
    id_producto_principal,nombre,tipo,precio,predeterminada,estado
)
SELECT p.id_producto,'WlMenú Desayuno','MENU',p.precio_base+15,FALSE,TRUE
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
WHERE c.nombre='Desayunos' AND p.estado=TRUE
  AND (p.nombre LIKE '%WlMuffin%' OR p.nombre LIKE '%WlGriddle%'
       OR p.nombre IN ('Burrito','Burritos'))
  AND NOT EXISTS (
      SELECT 1 FROM presentacion_menu pm
      WHERE pm.id_producto_principal=p.id_producto AND pm.tipo='MENU'
  );

-- Grupos de toda presentación MENU.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Producto principal',1,1,FALSE,FALSE,TRUE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='MENU'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Producto principal');

INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige el complemento',1,1,FALSE,TRUE,FALSE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='MENU'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige el complemento');

INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige la bebida',1,1,FALSE,TRUE,FALSE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='MENU'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige la bebida');

-- Producto principal de cada WlMenú.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,p.nombre,0,TRUE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='MENU' AND gp.nombre='Producto principal'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,pm.id_producto_principal,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
WHERE pm.tipo='MENU' AND gp.nombre='Producto principal'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=pm.id_producto_principal);

-- Complementos. Los menús de desayuno usan Hash Brown; los demás, Papas.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,
       CASE WHEN principal.disponibilidad_menu='DESAYUNO'
            THEN 'Hash Brown' ELSE 'Papas' END,
       0,TRUE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto principal ON principal.id_producto=pm.id_producto_principal
WHERE pm.tipo='MENU' AND gp.nombre='Elige el complemento'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,componente.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre=og.nombre
WHERE pm.tipo='MENU' AND gp.nombre='Elige el complemento'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=componente.id_producto);

-- Bebidas permitidas según horario del producto principal.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,bebida.nombre,
       CASE WHEN bebida.nombre LIKE 'Jugo%' THEN 2 ELSE 0 END,
       CASE
         WHEN principal.disponibilidad_menu='DESAYUNO' AND bebida.nombre='Café' THEN TRUE
         WHEN principal.disponibilidad_menu<>'DESAYUNO' AND bebida.nombre='Coca-Cola' THEN TRUE
         ELSE FALSE
       END,
       TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto principal ON principal.id_producto=pm.id_producto_principal
JOIN producto bebida ON bebida.nombre IN (
    CASE WHEN principal.disponibilidad_menu='DESAYUNO' THEN 'Café' ELSE 'Coca-Cola' END,
    CASE WHEN principal.disponibilidad_menu='DESAYUNO' THEN 'Jugo naranja' ELSE 'Coca-Cola Zero' END,
    CASE WHEN principal.disponibilidad_menu='DESAYUNO' THEN 'Jugo manzana' ELSE 'Sprite' END,
    CASE WHEN principal.disponibilidad_menu='DESAYUNO' THEN 'Agua' ELSE 'Fanta' END
)
WHERE pm.tipo='MENU' AND gp.nombre='Elige la bebida'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og
                  WHERE og.id_grupo=gp.id_grupo AND og.nombre=bebida.nombre);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,bebida.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto bebida ON bebida.nombre=og.nombre
WHERE pm.tipo='MENU' AND gp.nombre='Elige la bebida'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=bebida.id_producto);

-- ============================================================
-- 7. CAJITAS FELICES
-- ============================================================

INSERT INTO presentacion_menu
(id_producto_principal,nombre,tipo,precio,predeterminada,estado)
SELECT p.id_producto,'Cajita Feliz','INFANTIL',p.precio_base,TRUE,TRUE
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
WHERE c.nombre='Cajita Feliz' AND p.nombre LIKE 'Cajita Feliz%'
  AND NOT EXISTS (SELECT 1 FROM presentacion_menu pm
                  WHERE pm.id_producto_principal=p.id_producto);

INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Producto principal',1,1,FALSE,FALSE,TRUE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='INFANTIL'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Producto principal');

INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige el complemento',1,1,FALSE,TRUE,FALSE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='INFANTIL'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige el complemento');

INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige la bebida',1,1,FALSE,TRUE,FALSE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='INFANTIL'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige la bebida');

INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige el juguete',1,1,FALSE,TRUE,FALSE,TRUE
FROM presentacion_menu pm
WHERE pm.tipo='INFANTIL'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige el juguete');

-- Entrada fija de cada Cajita.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,
       CASE
         WHEN raiz.nombre LIKE '%derretido%' THEN 'Derretido clásico'
         WHEN raiz.nombre LIKE '%Hot Cakes%' THEN 'Hot Cakes'
         WHEN raiz.nombre LIKE '%Pollo WlCrispy%' THEN 'Pollo WlCrispy 1 Pieza'
         WHEN raiz.nombre LIKE '%quesoburguesa%' THEN 'Quesoburguesa'
         WHEN raiz.nombre LIKE '%WlNuggets%' THEN 'WlNuggets 4 pz'
         WHEN raiz.nombre LIKE '%frijol%' THEN 'WlMuffin Huevo y Frijol'
         WHEN raiz.nombre LIKE '%Huevo y queso%' THEN 'WlMuffin Huevo y Queso'
         WHEN raiz.nombre LIKE '%salchicha%' THEN 'WlMuffin Salchicha'
         ELSE 'Hamburguesa'
       END,
       0,TRUE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto raiz ON raiz.id_producto=pm.id_producto_principal
WHERE pm.tipo='INFANTIL' AND gp.nombre='Producto principal'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,componente.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre=og.nombre
WHERE pm.tipo='INFANTIL' AND gp.nombre='Producto principal'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=componente.id_producto);

-- Complementos infantiles.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,componente.nombre,0,
       componente.nombre='Papas Kids',TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre IN
    ('Papas Kids','Puré de manzana','Yogur de fresa')
WHERE pm.tipo='INFANTIL' AND gp.nombre='Elige el complemento'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og
                  WHERE og.id_grupo=gp.id_grupo AND og.nombre=componente.nombre);

-- Bebidas infantiles.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,componente.nombre,0,
       componente.nombre='Jugo de manzana Kids',TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre IN ('Jugo de manzana Kids','Agua')
WHERE pm.tipo='INFANTIL' AND gp.nombre='Elige la bebida'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og
                  WHERE og.id_grupo=gp.id_grupo AND og.nombre=componente.nombre);

-- Juguete.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,'Juguete sorpresa',0,TRUE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
WHERE pm.tipo='INFANTIL' AND gp.nombre='Elige el juguete'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

-- Componentes de complemento, bebida y juguete.
INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,componente.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre=og.nombre
WHERE pm.tipo='INFANTIL' AND gp.nombre<>'Producto principal'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=componente.id_producto);

-- ============================================================
-- 8. COMBOS Y CAJAS PREDEFINIDAS
-- ============================================================

INSERT INTO presentacion_menu
(id_producto_principal,nombre,tipo,precio,predeterminada,estado)
SELECT p.id_producto,'Combo','COMBO',p.precio_base,TRUE,TRUE
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
WHERE p.tipo_stock='NINGUNO' AND c.nombre<>'Cajita Feliz'
  AND NOT EXISTS (SELECT 1 FROM presentacion_menu pm
                  WHERE pm.id_producto_principal=p.id_producto);

-- Cajas Grandes configurables: cuatro principales independientes.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige tus productos',4,4,TRUE,TRUE,TRUE,TRUE
FROM presentacion_menu pm JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO' AND p.nombre IN ('Caja Grande','Caja Grande con Postre')
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige tus productos');

INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,componente.nombre,0,componente.nombre='Big Mac',TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre IN
    ('Big Mac','Cuarto de Libra con Queso','WlNífica Doble','10 WlNuggets de Pollo')
WHERE pm.tipo='COMBO' AND gp.nombre='Elige tus productos'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og
                  WHERE og.id_grupo=gp.id_grupo AND og.nombre=componente.nombre);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,componente.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto componente ON componente.nombre=og.nombre
WHERE pm.tipo='COMBO' AND gp.nombre='Elige tus productos'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=componente.id_producto);

-- Contenido fijo de cajas grandes: cuatro papas y bebida familiar.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Incluye',1,1,FALSE,FALSE,FALSE,TRUE
FROM presentacion_menu pm JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO' AND p.nombre IN ('Caja Grande','Caja Grande con Postre')
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion AND gp.nombre='Incluye');

INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,'Papas y bebida familiar',0,TRUE,TRUE
FROM grupo_presentacion gp
WHERE gp.nombre='Incluye'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,p.id_producto,
       CASE WHEN p.nombre='Papas' THEN 4 ELSE 1 END
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN producto p ON p.nombre IN ('Papas','Coca-Cola 1.5 L')
WHERE gp.nombre='Incluye'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion AND oc.id_producto=p.id_producto);

-- Postres seleccionables para Caja Grande con Postre.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige tus postres',2,2,TRUE,TRUE,FALSE,TRUE
FROM presentacion_menu pm JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO' AND p.nombre='Caja Grande con Postre'
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Elige tus postres');

INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,postre.nombre,0,postre.nombre='Pastel de manzana',TRUE
FROM grupo_presentacion gp
JOIN producto postre ON postre.nombre IN
    ('Pastel de manzana','Pastel de queso','Sundae Chocolate','Sundae Fresa')
WHERE gp.nombre='Elige tus postres'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og
                  WHERE og.id_grupo=gp.id_grupo AND og.nombre=postre.nombre);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,postre.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN producto postre ON postre.nombre=og.nombre
WHERE gp.nombre='Elige tus postres'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion AND oc.id_producto=postre.id_producto);

-- Resto de combos predefinidos: una opción fija con varios componentes.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Contenido del combo',1,1,FALSE,FALSE,TRUE,TRUE
FROM presentacion_menu pm JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO' AND p.nombre NOT IN ('Caja Grande','Caja Grande con Postre')
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion
                    AND gp.nombre='Contenido del combo');

INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,p.nombre,0,TRUE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO' AND gp.nombre='Contenido del combo'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

-- Componentes fijos de cada caja o bucket.
INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,componente.id_producto,
       CASE
         WHEN raiz.nombre='Caja de 24 WlNuggets'
              AND componente.nombre='10 WlNuggets de Pollo' THEN 2
         WHEN raiz.nombre='Caja Grande Snack' AND componente.nombre='Quesoburguesa' THEN 2
         WHEN raiz.nombre='Caja Grande Snack' AND componente.nombre='Papas' THEN 2
         WHEN raiz.nombre LIKE 'Bucket%' AND componente.nombre='Papas' THEN 3
         WHEN raiz.nombre LIKE 'Caja Grande D%' AND componente.nombre='Hash Brown' THEN 4
         WHEN raiz.nombre LIKE 'Caja Grande D%' AND componente.nombre='Café' THEN 2
         WHEN raiz.nombre='Caja Grande Deluxe' AND componente.nombre='Hot Cakes' THEN 2
         WHEN raiz.nombre='Caja Grande Deluxe' AND componente.nombre='WlMuffin de Huevo' THEN 2
         WHEN raiz.nombre='Caja Grande Desayuno' AND componente.nombre='WlMuffin Salchicha' THEN 2
         ELSE 1
       END
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto raiz ON raiz.id_producto=pm.id_producto_principal
JOIN producto componente ON
    (raiz.nombre='Bucket para Todos' AND componente.nombre IN
        ('Pollo WlCrispy 10 Piezas','Papas','Coca-Cola 1.5 L'))
 OR (raiz.nombre='Bucket Pollo WlCrispy' AND componente.nombre IN
        ('Pollo WlCrispy 10 Piezas','Papas'))
 OR (raiz.nombre='Bucket Pollo WlCrispy Para Tres' AND componente.nombre IN
        ('Pollo WlCrispy 10 Piezas','Papas','Coca-Cola 1.5 L'))
 OR (raiz.nombre='Bucket Pollo WlCrispy Snack' AND componente.nombre IN
        ('Pollo WlCrispy Dos Piezas','WlNuggets 4 pz','Papas'))
 OR (raiz.nombre='Caja de 24 WlNuggets' AND componente.nombre IN
        ('10 WlNuggets de Pollo','WlNuggets 4 pz'))
 OR (raiz.nombre='Caja Grande Snack' AND componente.nombre IN
        ('Quesoburguesa','WlNuggets 4 pz','Papas','Coca-Cola 1.5 L'))
 OR (raiz.nombre='Caja Grande Deluxe' AND componente.nombre IN
        ('Hot Cakes','WlMuffin de Huevo','Hash Brown','Café'))
 OR (raiz.nombre='Caja Grande Desayuno' AND componente.nombre IN
        ('WlMuffin Salchicha','Burritos','Hash Brown','Café'))
WHERE pm.tipo='COMBO' AND gp.nombre='Contenido del combo'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=componente.id_producto);

COMMIT;

-- ============================================================
-- 9. COMPROBACIONES
-- Todas estas consultas deben devolver cero filas, salvo el resumen.
-- ============================================================

-- Productos RECETA sin ingredientes.
SELECT p.id_producto,p.nombre AS receta_sin_ingredientes
FROM producto p
LEFT JOIN producto_ingrediente pi ON pi.id_producto=p.id_producto AND pi.estado=TRUE
WHERE p.tipo_stock='RECETA' AND p.estado=TRUE
GROUP BY p.id_producto,p.nombre
HAVING COUNT(pi.id_producto_ingrediente)=0;

-- Productos vendibles sin presentación.
SELECT p.id_producto,p.nombre AS producto_sin_presentacion
FROM producto p
LEFT JOIN presentacion_menu pm ON pm.id_producto_principal=p.id_producto AND pm.estado=TRUE
WHERE p.estado=TRUE
GROUP BY p.id_producto,p.nombre
HAVING COUNT(pm.id_presentacion)=0
   AND NOT EXISTS (
       SELECT 1 FROM opcion_componente oc WHERE oc.id_producto=p.id_producto
   );

-- Grupos sin opciones.
SELECT gp.id_grupo,gp.nombre AS grupo_sin_opciones
FROM grupo_presentacion gp
LEFT JOIN opcion_grupo og ON og.id_grupo=gp.id_grupo AND og.estado=TRUE
WHERE gp.estado=TRUE
GROUP BY gp.id_grupo,gp.nombre
HAVING COUNT(og.id_opcion)=0;

-- Opciones sin productos componentes.
SELECT og.id_opcion,og.nombre AS opcion_sin_componentes
FROM opcion_grupo og
LEFT JOIN opcion_componente oc ON oc.id_opcion=og.id_opcion
WHERE og.estado=TRUE
GROUP BY og.id_opcion,og.nombre
HAVING COUNT(oc.id_opcion_componente)=0;

-- Resumen final por tipo de stock.
SELECT tipo_stock,COUNT(*) AS productos
FROM producto
WHERE estado=TRUE
GROUP BY tipo_stock
ORDER BY tipo_stock;
'@

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add($header.TrimEnd())
for ($index = 0; $index -lt $productRows.Count; $index++) {
    $suffix = if ($index -eq $productRows.Count - 1) { ";" } else { "," }
    $lines.Add($productRows[$index] + $suffix)
}
$lines.Add($afterProducts.TrimStart())

$resolvedOutput = if ([System.IO.Path]::IsPathRooted($OutputPath)) {
    $OutputPath
} else {
    Join-Path (Get-Location) $OutputPath
}

$outputDirectory = Split-Path -Parent $resolvedOutput
if (-not (Test-Path -LiteralPath $outputDirectory)) {
    New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
}

[System.IO.File]::WriteAllText(
    $resolvedOutput,
    ($lines -join [Environment]::NewLine),
    [System.Text.UTF8Encoding]::new($false)
)

Write-Output "Archivo generado: $resolvedOutput"
Write-Output "Productos fuente únicos: $($products.Count)"
Write-Output "Productos auxiliares agregados: 6"
