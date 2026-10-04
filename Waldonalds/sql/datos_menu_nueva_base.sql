-- ============================================================
-- DATOS COMPLETOS PARA LA NUEVA BASE WALDONALDS
-- Generado desde los INSERT antiguos y adaptado al modelo de
-- recetas, presentaciones, WlMenús, Cajitas y combos.
-- ============================================================

USE waldonalds;

START TRANSACTION;

-- Recargo por cambiar una porción de Papas por WlPatatas.
-- Modifica únicamente este valor si cambia el precio del reemplazo.
SET @precio_extra_wlpatatas = 5.00;

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
('Almuerzos','Otras opciones','10 WlNuggets de Pollo','Diez piezas de pollo empanizado, tiernas por dentro y crujientes por fuera.',39.00,'/Imagenes/HAMBURGUESAS/10 mcnuggets de pollo.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Almuerzos','Hamburguesas','Bacon Cheddar WlMelt','Hamburguesa con carne, tocino y queso cheddar derretido.',49.00,'/Imagenes/HAMBURGUESAS/bacon_cheddar_mcmelt.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Big Mac','Hamburguesa con dos tortas de res, queso, lechuga, pepinillos y salsa especial.',41.00,'/Imagenes/HAMBURGUESAS/bigmac.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Big Tasty','Hamburguesa de res con queso, vegetales y salsa estilo Sabrosa.',48.00,'/Imagenes/HAMBURGUESAS/big tasty.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Big Tasty Bacon','Hamburguesa de res con tocino, queso, vegetales y salsa estilo Sabrosa.',54.00,'/Imagenes/HAMBURGUESAS/bigtasty_bacon.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Big Tasty Bacon Doble','Hamburguesa doble de res con tocino, queso, vegetales y salsa estilo Sabrosa.',65.00,'/Imagenes/HAMBURGUESAS/bigtasty_bacon_doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Pollo','Big Tasty de Pollo','Hamburguesa de pollo con queso, vegetales y salsa estilo Sabrosa.',47.00,'/Imagenes/HAMBURGUESAS/big_tasty_pollo.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Para compartir','Bucket para Todos','Cubeta de pollo crujiente pensado para compartir entre varias personas.',179.00,'/Imagenes/PARA COMPARTIR/bucket para todos.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Bucket Pollo WlCrispy','Cubeta con selección de piezas de pollo crujiente para compartir.',179.00,'/Imagenes/PARA COMPARTIR/Bucket Pollo McCrispy.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Bucket Pollo WlCrispy Para Tres','Cubeta de pollo crujiente ideal para compartir entre tres personas.',179.00,'/Imagenes/PARA COMPARTIR/Bucket Pollo McCrispy ® Para Tres.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Bucket Pollo WlCrispy Snack','Cubeta tipo aperitivo con piezas de pollo crujiente.',110.00,'/Imagenes/PARA COMPARTIR/Bucket Pollo McCrispy ® Snack.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Caja de 24 WlNuggets','Caja para compartir con 24 bocaditos de pollo.',129.00,'/Imagenes/PARA COMPARTIR/caja de 24 wlnuggets.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Caja Grande','Caja familiar con varias opciones principales, papas y bebida.',190.00,'/Imagenes/PARA COMPARTIR/caja grande.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Caja Grande con Postre','Caja familiar con productos principales, papas, bebida y postres.',215.00,'/Imagenes/para compartir/caja grande con postre (1).png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Para compartir','Caja Grande Snack','Caja para compartir con quesoburguesas, papas y bocaditos.',145.00,'/Imagenes/PARA COMPARTIR/caja grande snack.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Almuerzos','Creaciones Gourmet','Clásica Gourmet','Hamburguesa gourmet de res con queso, tomate, lechuga y cebolla.',49.00,'/Imagenes/HAMBURGUESAS/clasica gourmet res.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Creaciones Gourmet','Clásica Gourmet Doble Res Doble','Hamburguesa gourmet doble de res con queso y vegetales frescos.',62.00,'/Imagenes/HAMBURGUESAS/clasica_gourmet_doble_res_doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Cuarto de Libra Bacon con Queso','Carne de res con queso y tocino estilo Cuarto de Libra.',48.00,'/Imagenes/HAMBURGUESAS/cuarto de libra bacon con queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Cuarto de Libra Bacon Doble con Queso','Doble carne de res con queso y tocino estilo Cuarto de Libra.',61.00,'/Imagenes/HAMBURGUESAS/cuarto_de_libra_bacon_doble_queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Cuarto de Libra con Queso','Carne de res con queso, cebolla, pepinillos, ketchup y mostaza.',39.00,'/Imagenes/HAMBURGUESAS/cuarto de libra con queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Doble Big Mac','Gran Wl con cuatro tortas de res, queso, lechuga, pepinillos y salsa especial.',54.00,'/Imagenes/HAMBURGUESAS/doble_bigmac.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Doble Cuarto de Libra con Queso','Doble carne de res con queso estilo Cuarto de Libra.',55.00,'/Imagenes/HAMBURGUESAS/doble_cuarto_libracon_queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Doble Hamburguesa con Queso','Doble hamburguesa de res con queso cheddar y condimentos.',44.00,'/Imagenes/HAMBURGUESAS/doble_hamburguesa_con_queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Hamburguesa','Hamburguesa clásica de res con cebolla, pepinillos, ketchup y mostaza.',20.00,'/Imagenes/HAMBURGUESAS/hamburguesa.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Hamburguesa con Queso','Hamburguesa de res con queso cheddar, pepinillos y condimentos.',32.00,'/Imagenes/HAMBURGUESAS/hamburguesacon_queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Hamburguesa Deluxe','Hamburguesa de res con vegetales frescos y aderezo cremoso.',29.00,'/Imagenes/HAMBURGUESAS/hamburguesa_deluxe.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Creaciones Gourmet','Pico Guacamol Gourmet Doble','Hamburguesa gourmet doble de res con guacamole y pico de gallo.',62.00,'/Imagenes/HAMBURGUESAS/pico_guacamol_gourmetdoble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Creaciones Gourmet','Pico Guacamol Gourmet Res','Hamburguesa gourmet de res con guacamole, pico de gallo y queso.',49.00,'/Imagenes/HAMBURGUESAS/pico_guacamol_gourmet_res.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Pollo','Pollo WlCrispy','Pollo crujiente sazonado, dorado por fuera y jugoso por dentro.',30.00,'/Imagenes/HAMBURGUESAS/pollo_wlcrispy.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Almuerzos','Pollo','Pollo WlCrispy 1 Pieza','Una pieza de pollo crujiente, dorada por fuera y jugosa por dentro.',25.00,'/Imagenes/HAMBURGUESAS/pollo_wlcrispy_1_pieza.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Almuerzos','Pollo','Pollo WlCrispy 10 Piezas','Diez piezas de pollo crujiente para compartir.',175.00,'/Imagenes/HAMBURGUESAS/pollo mc crispy 10 piezas.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Almuerzos','Pollo','Pollo WlCrispy Bacon Ranch','Pollo crujiente acompañado de tocino y salsa campestre.',50.00,'/Imagenes/HAMBURGUESAS/pollo mc crispy bacon ranch.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Pollo','Pollo WlCrispy Dos Piezas','Dos piezas de pollo crujiente, doradas y jugosas.',40.00,'/Imagenes/HAMBURGUESAS/pollowlcrispy _dospiezas.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Almuerzos','Hamburguesas','Quesoburguesa','Hamburguesa de res con queso cheddar, pepinillos y condimentos.',32.00,'/Imagenes/HAMBURGUESAS/quesoburguesa.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Res','Hamburguesa de res con queso y vegetales frescos.',42.00,'/Imagenes/HAMBURGUESAS/hamburguesa.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Pollo','Sándwich WlPollo Doble','Sándwich doble de pollo con lechuga y mayonesa.',39.00,'/Imagenes/HAMBURGUESAS/sandwich_mcpollo_doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Creaciones Gourmet','Smoke Tocino Gourmet de Res','Hamburguesa gourmet de res con tocino, queso y cebolla crujiente.',50.00,'/Imagenes/HAMBURGUESAS/smoke_tocino_gourmetres.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Creaciones Gourmet','Smoke Tocino Gourmet Doble','Hamburguesa gourmet doble de res con tocino, queso y cebolla crujiente.',63.00,'/Imagenes/HAMBURGUESAS/smoke tocino gourmet doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Triple Bacon','Hamburguesa triple de res con queso y tocino.',46.00,'/Imagenes/HAMBURGUESAS/triple_bacon.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','Triple Hamburguesa con Queso','Tres tortas de res acompañadas de queso y condimentos.',46.00,'/Imagenes/HAMBURGUESAS/triplehamburguesa_queso.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Pollo','WlCrispy Chicken Deluxe','Sándwich de pollo crujiente con vegetales frescos y aderezo.',39.00,'/Imagenes/HAMBURGUESAS/WlCrispy_Chicken_Deluxe.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','WlDouble','Doble hamburguesa de res con queso y condimentos.',44.00,'/Imagenes/HAMBURGUESAS/wcdouble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','WlNífica de Res Doble','Hamburguesa doble de res con queso, lechuga, tomate y cebolla.',57.00,'/Imagenes/HAMBURGUESAS/mcnifica de res doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','WlNífica Doble','Hamburguesa doble de res con queso, lechuga, tomate y cebolla.',57.00,'/Imagenes/HAMBURGUESAS/mcnifica_doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Hamburguesas','WlNífica Res Doble','Hamburguesa doble de res con queso, lechuga, tomate y cebolla.',57.00,'/Imagenes/HAMBURGUESAS/mcnifica_res_doble.png','TODO_DIA','RECETA',TRUE,0,0),
('Almuerzos','Otras opciones','WlNuggets','Piezas de pollo empanizado, tiernas por dentro y crujientes por fuera.',39.00,'/Imagenes/HAMBURGUESAS/wlnuggets.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Antojos','Antojos Almuerzo y Cena','Derretido clásico','Pan ligeramente tostado con queso caliente y derretido.',22.00,'/Imagenes/ANTOJOS/derretido clasico.png','TODO_DIA','RECETA',TRUE,0,0),
('Antojos','Antojos Desayuno','Hash Brown','Papa rallada dorada y crujiente, ideal para acompañar el desayuno.',18.00,'/Imagenes/ANTOJOS/hash brown.png','DESAYUNO','DIRECTO',FALSE,100,10),
('Antojos','Antojos Almuerzo y Cena','Papas','Papas doradas y crujientes, servidas como acompañamiento.',28.00,'/Imagenes/ANTOJOS/papas.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Antojos','Antojos Almuerzo y Cena','Salsa cheddar','Salsa cremosa de queso cheddar para acompañar tus favoritos.',15.00,'/Imagenes/ANTOJOS/salsa cheddar.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Antojos','Antojos Almuerzo y Cena','Tostado de loroco','Tostado caliente con queso y un toque de loroco.',25.00,'/Imagenes/ANTOJOS/tostado de loroco.png','TODO_DIA','RECETA',TRUE,0,0),
('Antojos','Antojos Almuerzo y Cena','Tostado queso y frijol','Tostado caliente con queso y frijoles.',25.00,'/Imagenes/ANTOJOS/tostado queso y frijol.png','TODO_DIA','RECETA',TRUE,0,0),
('Antojos','Antojos Almuerzo y Cena','Tostado queso y jamón','Tostado caliente con queso y jamón.',25.00,'/Imagenes/ANTOJOS/tostado queso y jamon.png','TODO_DIA','RECETA',TRUE,0,0),
('Antojos','Antojos Almuerzo y Cena','Tostado queso y tomate','Tostado caliente con queso y tomate.',25.00,'/Imagenes/ANTOJOS/tostado queso y tomate.png','TODO_DIA','RECETA',TRUE,0,0),
('Antojos','Antojos Almuerzo y Cena','WlNuggets 4 pz','Cuatro piezas de pollo empanizado, doradas y crujientes.',22.00,'/Imagenes/ANTOJOS/wcnuggets 4 pz.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Antojos','Antojos Almuerzo y Cena','WlPatatas','Papas doradas y crujientes, servidas como acompañamiento.',28.00,'/Imagenes/ANTOJOS/wcpatatas.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Naturales','Agua','Agua pura refrescante para acompañar cualquier comida.',15.00,'/Imagenes/BEBIDAS/agua.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Calientes','Café','Café caliente de sabor suave y aromático.',18.00,'/Imagenes/BEBIDAS/café.png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Calientes','Café con leche','Café caliente combinado con leche cremosa.',22.00,'/Imagenes/BEBIDAS/cafe_con _leche.png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Calientes','Chocolate','Bebida caliente de chocolate, cremosa y reconfortante.',22.00,'/Imagenes/BEBIDAS/chocolate.png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Sodas','Coca-Cola','Bebida gaseosa Coca-Cola servida fría.',23.00,'/Imagenes/BEBIDAS/coca_cola.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Sodas','Coca-Cola Zero','Bebida gaseosa Coca-Cola sin azúcar, fría y sin azúcar.',23.00,'/Imagenes/BEBIDAS/coca_cola_zero.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Sodas','Fanta','Bebida gaseosa sabor naranja servida fría.',23.00,'/Imagenes/BEBIDAS/fanta.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Naturales','Jamaica','Bebida refrescante de jamaica servida fría.',20.00,'/Imagenes/BEBIDAS/jamaica.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Naturales','Jugo manzana','Jugo de manzana frío y refrescante.',18.00,'/Imagenes/BEBIDAS/jugo_manzana.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Naturales','Jugo naranja','Jugo de naranja frío para acompañar el desayuno o la comida.',18.00,'/Imagenes/BEBIDAS/jugo_naranja.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Sodas','Sprite','Bebida gaseosa lima-limón servida fría.',23.00,'/Imagenes/BEBIDAS/sprite.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Calientes','Té caliente','Té caliente de sabor ligero y aromático.',18.00,'/Imagenes/BEBIDAS/té_caliente.png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Naturales','Té Lipton','Té frío Lipton, refrescante y listo para acompañar tu comida.',20.00,'/Imagenes/BEBIDAS/te_lipton.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Bebidas','Naturales','WlFizz A.M.','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/wlcafe/wlfizz_A.M..png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Naturales','WlFrizz Blue','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/BEBIDAS/wlfrizz_blue.png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Naturales','WlFrizz Manzana Verde','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/BEBIDAS/wlfrizz_manzanaverde.png','TODO_DIA','RECETA',TRUE,0,0),
('Bebidas','Naturales','WlFrizz Pink','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/BEBIDAS/wlfrizz_pink.png','TODO_DIA','RECETA',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de derretido','Menú infantil con derretido, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de derretido.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de derretido almuerzo-cena','Menú infantil con derretido, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de derretido almuerzo-cena.png','ALMUERZO','NINGUNO',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de hamburguesa','Menú infantil con hamburguesa, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de hamburguesa.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de hamburguesa Jr.','Menú infantil con hamburguesa junior, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de hamburguesa jr.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Cajita Feliz','Desayuno','Cajita Feliz de Hot Cakes','Menú infantil de desayuno con panqueques y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de hotcakes.png','DESAYUNO','NINGUNO',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de Pollo WlCrispy','Menú infantil con pollo crujiente, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de pollo wlcrispy.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de quesoburguesa','Menú infantil con quesoburguesa, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de quesoburguesa.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Cajita Feliz','Desayuno','Cajita Feliz de WlMuffin de frijol','Menú infantil de desayuno con panecillo de frijol y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de wlmuffin de frijol.png','DESAYUNO','NINGUNO',TRUE,0,0),
('Cajita Feliz','Desayuno','Cajita Feliz de WlMuffin de Huevo y queso','Menú infantil de desayuno con panecillo de huevo y queso.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de wlmuffin de huevo y queso.png','DESAYUNO','NINGUNO',TRUE,0,0),
('Cajita Feliz','Desayuno','Cajita Feliz de WlMuffin de salchicha','Menú infantil de desayuno con panecillo de salchicha.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de wlmuffin de salchicha.png','DESAYUNO','NINGUNO',TRUE,0,0),
('Cajita Feliz','Almuerzo/Cena','Cajita Feliz de WlNuggets','Menú infantil con bocaditos de pollo, acompañamiento y bebida.',45.00,'/Imagenes/CAJITA FELIZ/cajita feliz de wlnuggets.png','TODO_DIA','NINGUNO',TRUE,0,0),
('Desayunos','Desayunos','Burrito','Burrito de desayuno con huevo, salchicha, vegetales y queso.',16.00,'/Imagenes/DESAYUNOS/burrito.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','Desayunos','Burritos','Porción de burritos de desayuno con huevo, salchicha, vegetales y queso.',27.00,'/Imagenes/DESAYUNOS/burritos.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','Caja Grande','Caja Grande Deluxe','Caja para compartir con una selección de desayunos estilo de lujo.',160.00,'/Imagenes/DESAYUNOS/Caja_grande_deluxe.png','DESAYUNO','NINGUNO',TRUE,0,0),
('Desayunos','Caja Grande','Caja Grande Desayuno','Caja para compartir con una selección de productos de desayuno.',140.00,'/Imagenes/DESAYUNOS/Caja_grande_desayuno.png','DESAYUNO','NINGUNO',TRUE,0,0),
('Desayunos','Desayunos','Desayuno clásico','Huevos revueltos con salchicha y pan panecillo tostado.',30.00,'/Imagenes/DESAYUNOS/desayuno_clàsico.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','Desayunos','Desayuno Deluxe','Huevos revueltos, salchicha, panqueques y pan panecillo tostado.',44.00,'/Imagenes/DESAYUNOS/desayuno_deluxe.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','Desayunos','Desayuno tradicional','Desayuno chapín con huevo, frijoles, plátanos, queso, crema y tortillas.',45.00,'/Imagenes/DESAYUNOS/desayuno_tradicional.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','Desayunos','Hot Cakes','Tres panqueques calientes acompañados de mantequilla y miel.',33.00,'/Imagenes/DESAYUNOS/hotcakes.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlGriddle','Pan WlGriddle','Pan suave tipo WlGriddle con un toque dulce de arce.',18.00,'/Imagenes/DESAYUNOS/pan_wlgriddle.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlGriddle','WlGriddle de Salchicha y Huevo','Pan tipo WlGriddle con salchicha, huevo y queso.',36.00,'/Imagenes/DESAYUNOS/wlgriddle de salchicha y huevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlGriddle','WlGriddle Salchicha','Pan tipo WlGriddle con salchicha y queso.',31.00,'/Imagenes/DESAYUNOS/wlgriddle_salchicha.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlGriddle','WlGriddle Tocino Huevo','Pan tipo WlGriddle con tocino, huevo y queso.',33.00,'/Imagenes/DESAYUNOS/wlgriddle_tocinohuevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Chapín Jamón','Pan panecillo tostado con jamón y acompañamientos estilo chapín.',36.00,'/Imagenes/DESAYUNOS/wlmuffin_chapin_jamon.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Chapín Salchicha','Pan panecillo tostado con salchicha y acompañamientos estilo chapín.',28.00,'/Imagenes/DESAYUNOS/wlmuffin_chapin_salchicha.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Cheddar WlMelt','Pan panecillo tostado con salchicha, huevo y queso cheddar.',42.00,'/Imagenes/DESAYUNOS/wlmuffin_cheddarwlmelt.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin de Doble Huevo','Pan panecillo tostado con jamón, queso y doble huevo.',41.00,'/Imagenes/DESAYUNOS/eggwlmuffin_doble_huevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin de Huevo','Pan panecillo tostado con jamón, huevo y queso.',36.00,'/Imagenes/DESAYUNOS/egg_wlmuffin.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Doble de Huevo','Pan panecillo tostado con jamón, huevo y queso.',37.00,'/Imagenes/DESAYUNOS/egg_wlmuffin_doble.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Huevo y Frijol','Pan panecillo tostado con huevo y frijoles.',24.00,'/Imagenes/DESAYUNOS/wlmuffin_huevofrijol.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Huevo y Queso','Pan panecillo tostado con huevo y queso.',21.00,'/Imagenes/DESAYUNOS/wlmuffin_huevoqueso.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Salchicha','Pan panecillo tostado con salchicha.',28.00,'/Imagenes/DESAYUNOS/wlmuffin_salchicha.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Salchicha Doble y Huevo','Panecillo de desayuno con doble salchicha, huevo y queso.',42.00,'/Imagenes/DESAYUNOS/wlMuffi_Salchich_Doble_y_Huevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Salchicha y doble huevo','Pan panecillo tostado con salchicha y doble huevo.',42.00,'/Imagenes/DESAYUNOS/wlMuffin_Salchicha_y_doble huevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Salchicha y huevo','Pan panecillo tostado con salchicha, huevo y queso.',37.00,'/Imagenes/DESAYUNOS/wlmuffin_salchichay_huevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Súper Chapín con Jamón','Panecillo estilo chapín con jamón y acompañamientos de desayuno.',42.00,'/Imagenes/DESAYUNOS/wlmuffinsuper_chapin_con_jamon.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Súper Chapín Salchicha','Panecillo estilo chapín con salchicha y acompañamientos de desayuno.',35.00,'/Imagenes/DESAYUNOS/wlmuffiinsuper_chapin_salchicha.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Tocino Doble Huevo','Pan panecillo tostado con tocino y doble huevo.',42.00,'/Imagenes/DESAYUNOS/wlmuffin_tocino_doble_huevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Desayunos','WlMuffin','WlMuffin Tocino y Huevo','Pan panecillo tostado con tocino, huevo y queso.',30.00,'/Imagenes/DESAYUNOS/wlmuffin_tocinohuevo.png','DESAYUNO','RECETA',TRUE,0,0),
('Postres','Pasteles','Pastel de manzana','Pastel caliente con relleno dulce de manzana.',17.00,'/Imagenes/POSTRES/pastel de manzana.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Postres','Pasteles','Pastel de queso','Pastel caliente con relleno cremoso de queso.',17.00,'/Imagenes/POSTRES/pastel de queso.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Postres','Helados','Sundae Caramelo','Helado suave de vainilla cubierto con salsa de caramelo.',15.00,'/Imagenes/POSTRES/sundae caramelo.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','Sundae Chocolate','Helado suave de vainilla cubierto con salsa de chocolate.',15.00,'/Imagenes/POSTRES/sundae chocolate.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','Sundae Fresa','Helado suave de vainilla cubierto con salsa de fresa.',15.00,'/Imagenes/POSTRES/sundae fresa.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlCono Vainilla','Cono crujiente con helado suave de vainilla.',10.00,'/Imagenes/POSTRES/mc cono vainilla.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry M&M''s','Helado suave de vainilla mezclado con M&M.',24.00,'/Imagenes/POSTRES/mcflurry m&ms.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry M&M''s Chocolate','Helado suave de vainilla mezclado con M&M y chocolate.',25.00,'/Imagenes/POSTRES/mflurry m&ms chocolate.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry M&M''s Fresa','Helado suave de vainilla mezclado con M&M y fresa.',25.00,'/Imagenes/POSTRES/mcflurry m&ms fresa.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry Oreo','Helado suave de vainilla mezclado con Oreo.',24.00,'/Imagenes/POSTRES/mcflurry oreo.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry Oreo Caramelo','Helado suave de vainilla mezclado con Oreo y caramelo.',25.00,'/Imagenes/POSTRES/mcflurry oreo caramelo.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry Oreo Chocolate','Helado suave de vainilla mezclado con Oreo y chocolate.',25.00,'/Imagenes/POSTRES/mclfurry oreo chocolate.png','TODO_DIA','RECETA',TRUE,0,0),
('Postres','Helados','WlFlurry Oreo Fresa','Helado suave de vainilla mezclado con Oreo y fresa.',25.00,'/Imagenes/POSTRES/mcflurry oreo fresa.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Postres WlCafé','Brownie Bites','Bizcocho de chocolate suave de chocolate con trozos de chocolate.',16.00,'/Imagenes/WLCAFE/brownie_bites.png','TODO_DIA','DIRECTO',FALSE,100,10),
('WlCafé','Café en bolsa','Café Blend Grano','Café mezcla en grano para preparar en casa.',55.00,'/Imagenes/WLCAFE/cafe_blend_grano.png','TODO_DIA','DIRECTO',FALSE,100,10),
('WlCafé','Café en bolsa','Café Blend Molido','Café mezcla molido para preparar en casa.',55.00,'/Imagenes/WLCAFE/cafe_blend_molido.png','TODO_DIA','DIRECTO',FALSE,100,10),
('WlCafé','Bebidas Calientes','Café guatemalteco','Café guatemalteco de aroma intenso y sabor balanceado.',25.00,'/Imagenes/wlcafe/cafe_guatemalteco.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Capuccino','Café expreso con leche vaporizada y espuma cremosa.',29.00,'/Imagenes/WLCAFE/capuccino.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Postres WlCafé','Cheesecake Fresa','Porción de pastel de queso con cobertura de fresa.',34.00,'/Imagenes/WLCAFE/cheescake_fresa.png','TODO_DIA','DIRECTO',FALSE,100,10),
('WlCafé','Postres WlCafé','Cheesecake Mora','Porción de pastel de queso con cobertura de mora.',34.00,'/Imagenes/WLCAFE/cheescake_mora.png','TODO_DIA','DIRECTO',FALSE,100,10),
('WlCafé','Bebidas Calientes','Chocolate caliente','Chocolate caliente, cremoso y reconfortante.',29.00,'/Imagenes/WLCAFE/chocolate_caliente.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Frappé Caramelo','Bebida frappé fría y cremosa sabor caramelo.',39.00,'/Imagenes/wlcafe/frappé caramelo.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Frappé Chocolate','Bebida frappé fría y cremosa sabor chocolate.',39.00,'/Imagenes/WLCAFE/frappé chocolate.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Frappé Oreo','Bebida frappé fría y cremosa con sabor a Oreo.',39.00,'/Imagenes/WLCAFE/Frappé Oreo.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Frappé Original','Bebida frappé fría y cremosa sabor original.',39.00,'/Imagenes/WLCAFE/Frappe original.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Frappé Vainilla','Bebida frappé fría y cremosa sabor vainilla.',39.00,'/Imagenes/wlcafe/Frappé Vainilla.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Frappé Vainilla Light','Bebida frappé fría y cremosa sabor vainilla.',39.00,'/Imagenes/WLCAFE/frappé vainilla light.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Horchata','Bebida fría de horchata con sabor dulce y especiado.',30.00,'/Imagenes/WLCAFE/Horchata.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Iced Coffee Caramelo','Café frío con hielo y sabor a caramelo.',35.00,'/Imagenes/WLCAFE/iced coffe caramelo.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Iced Coffee Chocolate','Café frío con hielo y sabor a chocolate.',35.00,'/Imagenes/WLCAFE/iced coffe chocolate.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Iced Coffee Horchata','Café frío con hielo y sabor a horchata.',35.00,'/Imagenes/WLCAFE/Iced coffe horchata.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Iced Coffee Original','Café frío con hielo de sabor original.',35.00,'/Imagenes/WLCAFE/Iced coffe original.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Iced Coffee Vainilla','Café frío con hielo y sabor a vainilla.',35.00,'/Imagenes/WLCAFE/Iced coffe vainilla.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Iced Coffee Vainilla Light','Café frío con hielo y sabor a vainilla.',35.00,'/Imagenes/WLCAFE/iced coffe vainilla light.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Latte','Café expreso combinado con leche caliente y textura cremosa.',30.00,'/Imagenes/WLCAFE/latte.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Postres WlCafé','Pastel de elote','Porción de pastel de elote suave y dulce.',32.00,'/Imagenes/WLCAFE/pastel_elote.png','TODO_DIA','DIRECTO',FALSE,100,10),
('WlCafé','Bebidas Frías','Smoothie Berries','Batido frío de frutos rojos con hielo triturado.',39.00,'/Imagenes/WLCAFE/smothie_berries.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','Smoothie Mango','Batido frío de mango con hielo triturado.',39.00,'/Imagenes/WLCAFE/smothie_mango.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Chai Original','Bebida de té chai con especias y textura cremosa.',41.00,'/Imagenes/WLCAFE/techai_original.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Chai Té Verde','Bebida de té chai combinada con té verde.',41.00,'/Imagenes/WLCAFE/techai_te_verde.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Chai Vainilla','Bebida de té chai con especias y un toque de vainilla.',41.00,'/Imagenes/WLCAFE/te_chai_vainilla.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Chai Vainilla Light','Té chai ligero con sabor a vainilla.',41.00,'/Imagenes/WLCAFE/techai_vainilla_light.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Guatemalteco Bora Bora','Infusión aromática con mezcla de sabores frutales.',29.00,'/Imagenes/wlcafe/te_guatemalteco_borabora.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Guatemalteco Melocotón Mix','Infusión de té guatemalteco con notas de melocotón.',29.00,'/Imagenes/wlcafe/te_guatealteco_melocoton_mix.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Guatemalteco Menta Fusión','Infusión aromática con sabor refrescante a menta.',29.00,'/Imagenes/wlcafe/te_guatemalteco_menta_fusion.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Calientes','Té Guatemalteco Vainilla Relax','Infusión de té con notas suaves de vainilla.',29.00,'/Imagenes/wlcafe/teguatemalteco_vainilla_relax.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Tostados','Tostado Queso Frijol','Tostado caliente con queso y frijoles.',25.00,'/Imagenes/WLCAFE/tostado_queso_frijol.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Tostados','Tostado Queso Tomate','Tostado caliente con queso y tomate.',25.00,'/Imagenes/WLCAFE/tostado_queso_tomate.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','WlFizz A.M.','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/wlcafe/wlfizz_A.M..png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','WlFrizz Blue','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/WLCAFE/wlfrizz blue.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','WlFrizz Manzana Verde','Bebida fría y burbujeante con sabor a manzana verde.',26.00,'/Imagenes/WLCAFE/wlfrizz_manzanaverde.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Bebidas Frías','WlFrizz Pink','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/WLCAFE/wlfrizz_pink.png','TODO_DIA','RECETA',TRUE,0,0),
('WlCafé','Postres WlCafé','WlPop Chocolate','Bizcocho individual con relleno de chocolate.',16.00,'/Imagenes/WLCAFE/wlpop_chocolate.png','TODO_DIA','DIRECTO',FALSE,100,10),
('Cajita Feliz','Complementos','Papas Kids','Porción infantil de papas.',10.00,'/Imagenes/ANTOJOS/papas.png','TODO_DIA','DIRECTO',FALSE,200,20),
('Cajita Feliz','Complementos','Puré de manzana','Puré de manzana para menú infantil.',9.00,'/Imagenes/BEBIDAS/jugo_manzana.png','TODO_DIA','DIRECTO',FALSE,150,15),
('Cajita Feliz','Complementos','Yogur de fresa','Yogur de fresa para menú infantil.',9.00,NULL,'TODO_DIA','DIRECTO',FALSE,150,15),
('Cajita Feliz','Bebidas','Jugo de manzana Kids','Jugo de manzana en tamaño infantil.',8.00,'/Imagenes/BEBIDAS/jugo_manzana.png','TODO_DIA','DIRECTO',FALSE,150,15),
('Cajita Feliz','Juguetes','Juguete sorpresa','Juguete disponible para la promoción infantil.',12.00,NULL,'TODO_DIA','DIRECTO',FALSE,200,20),
('Bebidas','Sodas','Coca-Cola 1.5 L','Bebida familiar para cajas y combos.',25.00,'/Imagenes/BEBIDAS/coca_cola.png','TODO_DIA','DIRECTO',FALSE,100,10);
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

-- En los menús que incluyen Papas también se permite cambiarlas por
-- WlPatatas. El precio adicional corresponde a una porción.
INSERT INTO opcion_grupo
(id_grupo,nombre,incremento_precio,predeterminada,estado)
SELECT gp.id_grupo,'WlPatatas',@precio_extra_wlpatatas,FALSE,TRUE
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
SELECT gp.id_grupo,'WlPatatas',@precio_extra_wlpatatas,FALSE,TRUE
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
              THEN @precio_extra_wlpatatas * 4
         WHEN principal.nombre='Caja Grande Snack'
              THEN @precio_extra_wlpatatas * 2
         ELSE @precio_extra_wlpatatas * 3
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
