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

$productMatches = [regex]::Matches(
    $source,
    $pattern,
    [System.Text.RegularExpressions.RegexOptions]::Singleline
)
$sourceRowCount = $productMatches.Count

$products = $productMatches | ForEach-Object {
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
    "Clásica Gourmet" = "/Imagenes/HAMBURGUESAS/clasica_gourmet.png"
    "Clásica Gourmet Doble Res Doble" = "/Imagenes/HAMBURGUESAS/clasica_gourmet_doble_res_doble.png"
    "Coca-Cola Zero" = "/Imagenes/BEBIDAS/coca_cola_zero.png"
    "Cuarto de Libra Bacon Doble con Queso" = "/Imagenes/HAMBURGUESAS/cuarto_libra_bacon_doble_queso.png"
    "Frappé Caramelo" = "/Imagenes/wlcafe/frappe_caramelo.png"
    "Frappé Vainilla" = "/Imagenes/wlcafe/frappe_vainilla.png"
    "Pico Guacamol Gourmet Doble" = "/Imagenes/HAMBURGUESAS/pico_guacamol_gourmet_res.png"
    "Pollo WlCrispy Dos Piezas" = "/Imagenes/HAMBURGUESAS/pollowlcrispy _dospiezas.png"
    "Té Guatemalteco Bora Bora" = "/Imagenes/wlcafe/te_guatemalteco_borabora.png"
    "Té Guatemalteco Melocotón Mix" = "/Imagenes/wlcafe/te_guatealteco_melocoton_mix.png"
    "Té Guatemalteco Menta Fusión" = "/Imagenes/wlcafe/te_guatemalteco_menta_fusion.png"
    "Té Guatemalteco Vainilla Relax" = "/Imagenes/wlcafe/teguatemalteco_vainilla_relax.png"
    "WlCrispy Chicken Deluxe" = "/Imagenes/HAMBURGUESAS/WlCrispy_Chicken_Deluxe.png"
    "WlFizz A.M." = "/Imagenes/wlcafe/wlfizz_A.M..png"
    "WlFrizz Blue" = "/Imagenes/bebidas/wlfrizz blue.png"
    "WlGriddle Tocino Huevo" = "/Imagenes/DESAYUNOS/wlgriddle_tocinohuevo.png"
    "WlMuffin de Doble Huevo" = "/Imagenes/DESAYUNOS/eggwlmuffin_doble_huevo.png"
    "WlMuffin Salchicha Doble y Huevo" = "/Imagenes/DESAYUNOS/wlMuffi_Salchich_Doble_y_Huevo.png"
    "WlMuffin Salchicha y doble huevo" = "/Imagenes/DESAYUNOS/wlMuffin_Salchicha_y_doble huevo.png"
    "WlMuffin Cheddar WlMelt" = "/Imagenes/Desayunos/wlmuffin_cheddar_wlmelt.png"
}

# Mapa de rutas reales. Ademas de comprobar que el archivo exista, conserva
# exactamente las mayusculas y espacios del recurso empaquetado por Java.
$imageRoot = [System.IO.Path]::GetFullPath(
    (Join-Path $PSScriptRoot "..\src\Imagenes")
)
$realImageRoutes = @{}
if (Test-Path -LiteralPath $imageRoot) {
    Get-ChildItem -LiteralPath $imageRoot -Recurse -File | ForEach-Object {
        $relative = $_.FullName.Substring($imageRoot.Length).TrimStart('\')
        $route = "/Imagenes/" + $relative.Replace('\', '/')
        $realImageRoutes[$route.ToLowerInvariant()] = $route
    }
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
    $imageKey = $image.ToLowerInvariant()
    if ($realImageRoutes.ContainsKey($imageKey)) {
        $image = $realImageRoutes[$imageKey]
    }
    $imageSql = "'" + (Escape-Sql $image) + "'"
    $productRows.Add((
        "((SELECT id_categoria FROM categoria WHERE nombre='{0}'),'{1}','{2}',{3},{4},'{5}','{6}','{7}',{8},{9},{10},TRUE)" -f
        (Escape-Sql $product.Categoria),
        (Escape-Sql $product.Nombre),
        (Escape-Sql $product.Descripcion),
        $product.Precio,
        $imageSql,
        (Escape-Sql $product.Subcategoria),
        $product.Horario,
        $stockType,
        $customizable,
        $stock,
        $minimumStock
    ))
}

# Productos auxiliares que no estaban en el catálogo viejo pero son necesarios
# para construir una Cajita o una caja familiar real.
$productRows.Add("((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Papas Kids','Porción infantil de papas.',10.00,'/Imagenes/antojos/papas.png','Complementos','TODO_DIA','DIRECTO',FALSE,200,20,TRUE)")
$productRows.Add("((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Puré de manzana','Puré de manzana para menú infantil.',9.00,'/Imagenes/bebidas/jugo_manzana.png','Complementos','TODO_DIA','DIRECTO',FALSE,150,15,TRUE)")
$productRows.Add("((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Yogur de fresa','Yogur de fresa para menú infantil.',9.00,NULL,'Complementos','TODO_DIA','DIRECTO',FALSE,150,15,TRUE)")
$productRows.Add("((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Jugo de manzana Kids','Jugo de manzana en tamaño infantil.',8.00,'/Imagenes/bebidas/jugo_manzana.png','Bebidas','TODO_DIA','DIRECTO',FALSE,150,15,TRUE)")
$productRows.Add("((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Juguete sorpresa','Juguete sorpresa disponible para niños.',12.00,NULL,'Juguetes','TODO_DIA','DIRECTO',FALSE,200,20,TRUE)")
$productRows.Add("((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Coca-Cola 1.5 L','Bebida familiar para cajas y combos.',25.00,'/Imagenes/bebidas/coca_cola.png','Sodas','TODO_DIA','DIRECTO',FALSE,100,10,TRUE)")

$header = @'
-- ============================================================
-- DATOS INICIALES PARA LA NUEVA BASE WALDONALDS
-- Ejecutar una sola vez sobre las tablas vacías.
-- Contiene únicamente sentencias INSERT.
-- ============================================================

-- ============================================================
-- 1. CATEGORÍAS
-- ============================================================

INSERT INTO categoria (nombre, estado) VALUES
('Antojos',TRUE),
('Bebidas',TRUE),
('Cajita Feliz',TRUE),
('Desayunos',TRUE),
('Almuerzos',TRUE),
('Postres',TRUE),
('WlCafé',TRUE);

-- ============================================================
-- 2. PRODUCTOS
-- ============================================================

INSERT INTO producto (
    id_categoria,nombre,descripcion,precio_base,imagen,subcategoria,
    disponibilidad_menu,tipo_stock,personalizable,
    stock_actual,stock_minimo,estado
)
VALUES
'@

$afterProducts = @'

-- ============================================================
-- 3. INGREDIENTES
-- Las cantidades están expresadas según unidad_medida.
-- ============================================================

INSERT INTO ingrediente
(nombre,unidad_medida,stock_actual,stock_minimo) VALUES
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

-- ============================================================
-- 4. RECETAS
-- Las reglas se consolidan dentro del mismo INSERT.
-- ============================================================

-- Tostados y derretidos.
INSERT INTO producto_ingrediente (
    id_producto,id_ingrediente,cantidad_default,
    permite_quitar,permite_extra,cantidad_extra,
    precio_extra,max_extras,estado
)
SELECT receta.id_producto,receta.id_ingrediente,
       MAX(receta.cantidad),MAX(receta.permite_quitar),
       MAX(receta.permite_extra),MAX(receta.cantidad_extra),
       MAX(receta.precio_extra),MAX(receta.max_extras),TRUE
FROM (
SELECT p.id_producto AS id_producto,
       i.id_ingrediente AS id_ingrediente,
       2 AS cantidad,
       FALSE AS permite_quitar,
       FALSE AS permite_extra,
       1 AS cantidad_extra,
       0 AS precio_extra,
       1 AS max_extras
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
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Fresa%'
) receta
GROUP BY receta.id_producto,receta.id_ingrediente;

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

-- En los menús que incluyen Papas también se permite cambiarlas por
-- WlPatatas. El precio adicional corresponde a una porción.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,'WlPatatas',5.00,FALSE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
WHERE pm.tipo='MENU' AND gp.nombre='Elige el complemento'
  AND EXISTS (SELECT 1 FROM opcion_grupo papas
              WHERE papas.id_grupo=gp.id_grupo
                AND papas.nombre='Papas')
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo existente
                  WHERE existente.id_grupo=gp.id_grupo
                    AND existente.nombre='WlPatatas');

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

-- Cambio opcional de Papas Kids por una porción de WlPatatas.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,'WlPatatas',5.00,FALSE,TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
WHERE pm.tipo='INFANTIL' AND gp.nombre='Elige el complemento'
  AND EXISTS (SELECT 1 FROM opcion_grupo papas
              WHERE papas.id_grupo=gp.id_grupo
                AND papas.nombre='Papas Kids')
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo existente
                  WHERE existente.id_grupo=gp.id_grupo
                    AND existente.nombre='WlPatatas');

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

-- Contenido fijo de cajas grandes. Las papas se crean más adelante como
-- una elección visible, por lo que aquí solo queda la bebida familiar.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Incluye',1,1,FALSE,FALSE,FALSE,TRUE
FROM presentacion_menu pm JOIN producto p ON p.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO' AND p.nombre IN ('Caja Grande','Caja Grande con Postre')
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion gp
                  WHERE gp.id_presentacion=pm.id_presentacion AND gp.nombre='Incluye');

INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,'Bebida familiar',0,TRUE,TRUE
FROM grupo_presentacion gp
WHERE gp.nombre='Incluye'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo og WHERE og.id_grupo=gp.id_grupo);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,p.id_producto,1
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN producto p ON p.nombre='Coca-Cola 1.5 L'
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
        ('Pollo WlCrispy 10 Piezas','Coca-Cola 1.5 L'))
 OR (raiz.nombre='Bucket Pollo WlCrispy' AND componente.nombre IN
        ('Pollo WlCrispy 10 Piezas'))
 OR (raiz.nombre='Bucket Pollo WlCrispy Para Tres' AND componente.nombre IN
        ('Pollo WlCrispy 10 Piezas','Coca-Cola 1.5 L'))
 OR (raiz.nombre='Bucket Pollo WlCrispy Snack' AND componente.nombre IN
        ('Pollo WlCrispy Dos Piezas','WlNuggets 4 pz'))
 OR (raiz.nombre='Caja de 24 WlNuggets' AND componente.nombre IN
        ('10 WlNuggets de Pollo','WlNuggets 4 pz'))
 OR (raiz.nombre='Caja Grande Snack' AND componente.nombre IN
        ('Quesoburguesa','WlNuggets 4 pz','Coca-Cola 1.5 L'))
 OR (raiz.nombre='Caja Grande Deluxe' AND componente.nombre IN
        ('Hot Cakes','WlMuffin de Huevo','Hash Brown','Café'))
 OR (raiz.nombre='Caja Grande Desayuno' AND componente.nombre IN
        ('WlMuffin Salchicha','Burritos','Hash Brown','Café'))
WHERE pm.tipo='COMBO' AND gp.nombre='Contenido del combo'
  AND NOT EXISTS (SELECT 1 FROM opcion_componente oc
                  WHERE oc.id_opcion=og.id_opcion
                    AND oc.id_producto=componente.id_producto);

-- Elección de papas de los combos que originalmente incluyen Papas.
-- Caja Grande lleva 4 porciones, los buckets 3 y Caja Grande Snack 2.
INSERT INTO grupo_presentacion
(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado)
SELECT pm.id_presentacion,'Elige tus papas',1,1,FALSE,TRUE,FALSE,TRUE
FROM presentacion_menu pm
JOIN producto principal ON principal.id_producto=pm.id_producto_principal
WHERE pm.tipo='COMBO'
  AND principal.nombre IN (
      'Bucket para Todos',
      'Bucket Pollo WlCrispy',
      'Bucket Pollo WlCrispy Para Tres',
      'Bucket Pollo WlCrispy Snack',
      'Caja Grande',
      'Caja Grande con Postre',
      'Caja Grande Snack'
  )
  AND NOT EXISTS (SELECT 1 FROM grupo_presentacion existente
                  WHERE existente.id_presentacion=pm.id_presentacion
                    AND existente.nombre='Elige tus papas');

INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,acompanamiento.nombre,
       CASE
         WHEN acompanamiento.nombre='Papas' THEN 0
         WHEN principal.nombre IN ('Caja Grande','Caja Grande con Postre')
              THEN 20.00
         WHEN principal.nombre='Caja Grande Snack'
              THEN 10.00
         ELSE 15.00
       END,
       acompanamiento.nombre='Papas',TRUE
FROM grupo_presentacion gp
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto principal ON principal.id_producto=pm.id_producto_principal
JOIN producto acompanamiento ON acompanamiento.nombre IN ('Papas','WlPatatas')
WHERE pm.tipo='COMBO' AND gp.nombre='Elige tus papas'
  AND NOT EXISTS (SELECT 1 FROM opcion_grupo existente
                  WHERE existente.id_grupo=gp.id_grupo
                    AND existente.nombre=acompanamiento.nombre);

INSERT INTO opcion_componente (id_opcion,id_producto,cantidad)
SELECT og.id_opcion,acompanamiento.id_producto,
       CASE
         WHEN principal.nombre IN ('Caja Grande','Caja Grande con Postre') THEN 4
         WHEN principal.nombre='Caja Grande Snack' THEN 2
         ELSE 3
       END
FROM opcion_grupo og
JOIN grupo_presentacion gp ON gp.id_grupo=og.id_grupo
JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion
JOIN producto principal ON principal.id_producto=pm.id_producto_principal
JOIN producto acompanamiento ON acompanamiento.nombre=og.nombre
WHERE pm.tipo='COMBO' AND gp.nombre='Elige tus papas'
  AND og.nombre IN ('Papas','WlPatatas')
  AND NOT EXISTS (SELECT 1 FROM opcion_componente existente
                  WHERE existente.id_opcion=og.id_opcion
                    AND existente.id_producto=acompanamiento.id_producto);

'@

# Las recetas se escriben como un solo INSERT con UNION ALL, sin tablas
# temporales. Los comentarios intermedios se conservan para facilitar estudio.
$afterProducts = [regex]::Replace(
    $afterProducts,
    "INSERT INTO carga_receta\r?\n",
    "UNION ALL" + [Environment]::NewLine
)
$afterProducts = [regex]::Replace(
    $afterProducts,
    ";\r?\n(?=(?:\s*--[^\r\n]*\r?\n)*\s*UNION ALL)",
    [Environment]::NewLine
)

$lines = New-Object System.Collections.Generic.List[string]
$auditHeader = @"
-- Fuente adaptada: $(Split-Path -Leaf $SourcePath)
-- Filas de producto encontradas: $sourceRowCount
-- Productos únicos conservados: $($products.Count)
-- Filas duplicadas descartadas: $($sourceRowCount - $products.Count)
-- Productos internos agregados para Cajitas y combos: 6
"@
$lines.Add($auditHeader.TrimEnd())
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
