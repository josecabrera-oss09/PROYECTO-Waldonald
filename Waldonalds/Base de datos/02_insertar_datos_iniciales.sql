USE waldonalds;

-- ============================================================
-- USUARIOS INICIALES PARA PROBAR EL SISTEMA
-- Las contraseñas se guardan con SHA-256. Al iniciar sesión por
-- primera vez, el programa las actualiza automáticamente a PBKDF2.
--
-- Administrador: admin  / Admin123!
-- Cajero:        cajero / Cajero123!
-- ============================================================

INSERT INTO usuario (
    nombre,apellido,usuario,correo,password_hash,rol,
    estado,turno,hora_inicio,hora_fin
) VALUES
('Administrador','Prueba','admin','admin@waldonalds.local',
 SHA2('Admin123!',256),'ADMINISTRADOR',TRUE,'MANANA','06:00:00','14:00:00'),
('Cajero','Prueba','cajero','cajero@waldonalds.local',
 SHA2('Cajero123!',256),'CAJERO',TRUE,'TARDE','14:00:00','22:00:00');

-- Fuente adaptada: productos_terminados.sql
-- Filas de producto encontradas: 186
-- Productos únicos conservados: 164
-- Filas duplicadas descartadas: 22
-- Productos internos agregados para Cajitas y combos: 6
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
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'10 WlNuggets de Pollo','Diez piezas de pollo empanizado, tiernas por dentro y crujientes por fuera.',39.00,'/Imagenes/HAMBURGUESAS/10 mcnuggets de pollo.png','Otras opciones','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Bacon Cheddar WlMelt','Hamburguesa con carne, tocino y queso cheddar derretido.',49.00,'/Imagenes/HAMBURGUESAS/bacon_cheddar_mcmelt.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Big Mac','Hamburguesa con dos tortas de res, queso, lechuga, pepinillos y salsa especial.',41.00,'/Imagenes/HAMBURGUESAS/bigmac.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Big Tasty','Hamburguesa de res con queso, vegetales y salsa estilo Sabrosa.',48.00,'/Imagenes/HAMBURGUESAS/big tasty.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Big Tasty Bacon','Hamburguesa de res con tocino, queso, vegetales y salsa estilo Sabrosa.',54.00,'/Imagenes/HAMBURGUESAS/bigtasty_bacon.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Big Tasty Bacon Doble','Hamburguesa doble de res con tocino, queso, vegetales y salsa estilo Sabrosa.',65.00,'/Imagenes/HAMBURGUESAS/bigtasty_bacon_doble.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Big Tasty de Pollo','Hamburguesa de pollo con queso, vegetales y salsa estilo Sabrosa.',47.00,'/Imagenes/HAMBURGUESAS/big_tasty_pollo.png','Pollo','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Bucket para Todos','Cubeta de pollo crujiente pensado para compartir entre varias personas.',179.00,'/Imagenes/para compartir/bucket para todos.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Bucket Pollo WlCrispy','Cubeta con selección de piezas de pollo crujiente para compartir.',179.00,'/Imagenes/para compartir/Bucket Pollo McCrispy.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Bucket Pollo WlCrispy Para Tres','Cubeta de pollo crujiente ideal para compartir entre tres personas.',179.00,'/Imagenes/para compartir/Bucket Pollo McCrispy ® Para Tres.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Bucket Pollo WlCrispy Snack','Cubeta tipo aperitivo con piezas de pollo crujiente.',110.00,'/Imagenes/para compartir/Bucket Pollo McCrispy ® Snack.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Caja de 24 WlNuggets','Caja para compartir con 24 bocaditos de pollo.',129.00,'/Imagenes/para compartir/caja de 24 wlnuggets.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Caja Grande','Caja familiar con varias opciones principales, papas y bebida.',190.00,'/Imagenes/para compartir/caja grande.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Caja Grande con Postre','Caja familiar con productos principales, papas, bebida y postres.',215.00,'/Imagenes/para compartir/caja grande con postre (1).png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Caja Grande Snack','Caja para compartir con quesoburguesas, papas y bocaditos.',145.00,'/Imagenes/para compartir/caja grande snack.png','Para compartir','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Clásica Gourmet','Hamburguesa gourmet de res con queso, tomate, lechuga y cebolla.',49.00,'/Imagenes/HAMBURGUESAS/clasica_gourmet.png','Creaciones Gourmet','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Clásica Gourmet Doble Res Doble','Hamburguesa gourmet doble de res con queso y vegetales frescos.',62.00,'/Imagenes/HAMBURGUESAS/clasica_gourmet_doble_res_doble.png','Creaciones Gourmet','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Cuarto de Libra Bacon con Queso','Carne de res con queso y tocino estilo Cuarto de Libra.',48.00,'/Imagenes/HAMBURGUESAS/cuarto de libra bacon con queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Cuarto de Libra Bacon Doble con Queso','Doble carne de res con queso y tocino estilo Cuarto de Libra.',61.00,'/Imagenes/HAMBURGUESAS/cuarto_libra_bacon_doble_queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Cuarto de Libra con Queso','Carne de res con queso, cebolla, pepinillos, ketchup y mostaza.',39.00,'/Imagenes/HAMBURGUESAS/cuarto de libra con queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Doble Big Mac','Gran Wl con cuatro tortas de res, queso, lechuga, pepinillos y salsa especial.',54.00,'/Imagenes/HAMBURGUESAS/doble_bigmac.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Doble Cuarto de Libra con Queso','Doble carne de res con queso estilo Cuarto de Libra.',55.00,'/Imagenes/HAMBURGUESAS/doble_cuarto_libracon_queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Doble Hamburguesa con Queso','Doble hamburguesa de res con queso cheddar y condimentos.',44.00,'/Imagenes/HAMBURGUESAS/doble_hamburguesa_con_queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Hamburguesa','Hamburguesa clásica de res con cebolla, pepinillos, ketchup y mostaza.',20.00,'/Imagenes/HAMBURGUESAS/hamburguesa.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Hamburguesa con Queso','Hamburguesa de res con queso cheddar, pepinillos y condimentos.',32.00,'/Imagenes/HAMBURGUESAS/hamburguesacon_queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Hamburguesa Deluxe','Hamburguesa de res con vegetales frescos y aderezo cremoso.',29.00,'/Imagenes/HAMBURGUESAS/hamburguesa_deluxe.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pico Guacamol Gourmet Doble','Hamburguesa gourmet doble de res con guacamole y pico de gallo.',62.00,'/Imagenes/HAMBURGUESAS/pico_guacamol_gourmet_res.png','Creaciones Gourmet','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pico Guacamol Gourmet Res','Hamburguesa gourmet de res con guacamole, pico de gallo y queso.',49.00,'/Imagenes/HAMBURGUESAS/pico_guacamol_gourmet_res.png','Creaciones Gourmet','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pollo WlCrispy','Pollo crujiente sazonado, dorado por fuera y jugoso por dentro.',30.00,'/Imagenes/HAMBURGUESAS/pollo_wlcrispy.png','Pollo','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pollo WlCrispy 1 Pieza','Una pieza de pollo crujiente, dorada por fuera y jugosa por dentro.',25.00,'/Imagenes/HAMBURGUESAS/pollo_wlcrispy_1_pieza.png','Pollo','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pollo WlCrispy 10 Piezas','Diez piezas de pollo crujiente para compartir.',175.00,'/Imagenes/HAMBURGUESAS/pollo mc crispy 10 piezas.png','Pollo','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pollo WlCrispy Bacon Ranch','Pollo crujiente acompañado de tocino y salsa campestre.',50.00,'/Imagenes/HAMBURGUESAS/pollo mc crispy bacon ranch.png','Pollo','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Pollo WlCrispy Dos Piezas','Dos piezas de pollo crujiente, doradas y jugosas.',40.00,'/Imagenes/HAMBURGUESAS/pollowlcrispy _dospiezas.png','Pollo','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Quesoburguesa','Hamburguesa de res con queso cheddar, pepinillos y condimentos.',32.00,'/Imagenes/HAMBURGUESAS/quesoburguesa.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Sándwich WlPollo Doble','Sándwich doble de pollo con lechuga y mayonesa.',39.00,'/Imagenes/HAMBURGUESAS/sandwich_mcpollo_doble.png','Pollo','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Smoke Tocino Gourmet de Res','Hamburguesa gourmet de res con tocino, queso y cebolla crujiente.',50.00,'/Imagenes/HAMBURGUESAS/smoke_tocino_gourmetres.png','Creaciones Gourmet','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Smoke Tocino Gourmet Doble','Hamburguesa gourmet doble de res con tocino, queso y cebolla crujiente.',63.00,'/Imagenes/HAMBURGUESAS/smoke tocino gourmet doble.png','Creaciones Gourmet','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Triple Bacon','Hamburguesa triple de res con queso y tocino.',46.00,'/Imagenes/HAMBURGUESAS/triple_bacon.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'Triple Hamburguesa con Queso','Tres tortas de res acompañadas de queso y condimentos.',46.00,'/Imagenes/HAMBURGUESAS/triplehamburguesa_queso.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'WlCrispy Chicken Deluxe','Sándwich de pollo crujiente con vegetales frescos y aderezo.',39.00,'/Imagenes/HAMBURGUESAS/WlCrispy_Chicken_Deluxe.png','Pollo','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'WlDouble','Doble hamburguesa de res con queso y condimentos.',44.00,'/Imagenes/HAMBURGUESAS/wcdouble.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'WlNífica de Res Doble','Hamburguesa doble de res con queso, lechuga, tomate y cebolla.',57.00,'/Imagenes/HAMBURGUESAS/mcnifica de res doble.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'WlNífica Doble','Hamburguesa doble de res con queso, lechuga, tomate y cebolla.',57.00,'/Imagenes/HAMBURGUESAS/mcnifica_doble.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'WlNífica Res Doble','Hamburguesa doble de res con queso, lechuga, tomate y cebolla.',57.00,'/Imagenes/HAMBURGUESAS/mcnifica_res_doble.png','Hamburguesas','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Almuerzos'),'WlNuggets','Piezas de pollo empanizado, tiernas por dentro y crujientes por fuera.',39.00,'/Imagenes/HAMBURGUESAS/wlnuggets.png','Otras opciones','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Derretido clásico','Pan ligeramente tostado con queso caliente y derretido.',22.00,'/Imagenes/antojos/derretido clasico.png','Antojos Almuerzo y Cena','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Hash Brown','Papa rallada dorada y crujiente, ideal para acompañar el desayuno.',18.00,'/Imagenes/antojos/hash brown.png','Antojos Desayuno','DESAYUNO','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Papas','Papas doradas y crujientes, servidas como acompañamiento.',28.00,'/Imagenes/antojos/papas.png','Antojos Almuerzo y Cena','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Salsa cheddar','Salsa cremosa de queso cheddar para acompañar tus favoritos.',15.00,'/Imagenes/antojos/salsa cheddar.png','Antojos Almuerzo y Cena','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Tostado de loroco','Tostado caliente con queso y un toque de loroco.',25.00,'/Imagenes/antojos/tostado de loroco.png','Antojos Almuerzo y Cena','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Tostado queso y frijol','Tostado caliente con queso y frijoles.',25.00,'/Imagenes/antojos/tostado queso y frijol.png','Antojos Almuerzo y Cena','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Tostado queso y jamón','Tostado caliente con queso y jamón.',25.00,'/Imagenes/antojos/tostado queso y jamon.png','Antojos Almuerzo y Cena','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'Tostado queso y tomate','Tostado caliente con queso y tomate.',25.00,'/Imagenes/antojos/tostado queso y tomate.png','Antojos Almuerzo y Cena','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'WlNuggets 4 pz','Cuatro piezas de pollo empanizado, doradas y crujientes.',22.00,'/Imagenes/antojos/wcnuggets 4 pz.png','Antojos Almuerzo y Cena','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Antojos'),'WlPatatas','Papas doradas y crujientes, servidas como acompañamiento.',28.00,'/Imagenes/antojos/wcpatatas.png','Antojos Almuerzo y Cena','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Agua','Agua pura refrescante para acompañar cualquier comida.',15.00,'/Imagenes/bebidas/agua.png','Naturales','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Café','Café caliente de sabor suave y aromático.',18.00,'/Imagenes/bebidas/café.png','Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Café con leche','Café caliente combinado con leche cremosa.',22.00,'/Imagenes/bebidas/cafe_con _leche.png','Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Chocolate','Bebida caliente de chocolate, cremosa y reconfortante.',22.00,'/Imagenes/bebidas/chocolate.png','Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Coca-Cola','Bebida gaseosa Coca-Cola servida fría.',23.00,'/Imagenes/bebidas/coca_cola.png','Sodas','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Coca-Cola Zero','Bebida gaseosa Coca-Cola sin azúcar, fría y sin azúcar.',23.00,'/Imagenes/bebidas/coca_cola_zero.png','Sodas','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Fanta','Bebida gaseosa sabor naranja servida fría.',23.00,'/Imagenes/bebidas/fanta.png','Sodas','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Jamaica','Bebida refrescante de jamaica servida fría.',20.00,'/Imagenes/bebidas/jamaica.png','Naturales','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Jugo manzana','Jugo de manzana frío y refrescante.',18.00,'/Imagenes/bebidas/jugo_manzana.png','Naturales','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Jugo naranja','Jugo de naranja frío para acompañar el desayuno o la comida.',18.00,'/Imagenes/bebidas/jugo_naranja.png','Naturales','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Sprite','Bebida gaseosa lima-limón servida fría.',23.00,'/Imagenes/bebidas/sprite.png','Sodas','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Té caliente','Té caliente de sabor ligero y aromático.',18.00,'/Imagenes/bebidas/té_caliente.png','Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Té Lipton','Té frío Lipton, refrescante y listo para acompañar tu comida.',20.00,'/Imagenes/bebidas/te_lipton.png','Naturales','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'WlFizz A.M.','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/wlcafe/wlfizz_A.M..png','Naturales','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'WlFrizz Blue','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/bebidas/wlfrizz blue.png','Naturales','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'WlFrizz Manzana Verde','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/bebidas/wlfrizz_manzanaverde.png','Naturales','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'WlFrizz Pink','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/bebidas/wlfrizz_pink.png','Naturales','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de derretido','Menú infantil con derretido, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de derretido.png','Almuerzo/Cena','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de derretido almuerzo-cena','Menú infantil con derretido, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de derretido almuerzo-cena.png','Almuerzo/Cena','ALMUERZO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de hamburguesa','Menú infantil con hamburguesa, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de hamburguesa.png','Almuerzo/Cena','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de hamburguesa Jr.','Menú infantil con hamburguesa junior, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de hamburguesa jr.png','Almuerzo/Cena','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de Hot Cakes','Menú infantil de desayuno con panqueques y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de hotcakes.png','Desayuno','DESAYUNO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de Pollo WlCrispy','Menú infantil con pollo crujiente, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de pollo wlcrispy.png','Almuerzo/Cena','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de quesoburguesa','Menú infantil con quesoburguesa, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de quesoburguesa.png','Almuerzo/Cena','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de WlMuffin de frijol','Menú infantil de desayuno con panecillo de frijol y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de wlmuffin de frijol.png','Desayuno','DESAYUNO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de WlMuffin de Huevo y queso','Menú infantil de desayuno con panecillo de huevo y queso.',45.00,'/Imagenes/cajita feliz/cajita feliz de wlmuffin de huevo y queso.png','Desayuno','DESAYUNO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de WlMuffin de salchicha','Menú infantil de desayuno con panecillo de salchicha.',45.00,'/Imagenes/cajita feliz/cajita feliz de wlmuffin de salchicha.png','Desayuno','DESAYUNO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Cajita Feliz de WlNuggets','Menú infantil con bocaditos de pollo, acompañamiento y bebida.',45.00,'/Imagenes/cajita feliz/cajita feliz de wlnuggets.png','Almuerzo/Cena','TODO_DIA','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Burrito','Burrito de desayuno con huevo, salchicha, vegetales y queso.',16.00,'/Imagenes/Desayunos/burrito.png','Desayunos','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Burritos','Porción de burritos de desayuno con huevo, salchicha, vegetales y queso.',27.00,'/Imagenes/Desayunos/burritos.png','Desayunos','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Caja Grande Deluxe','Caja para compartir con una selección de desayunos estilo de lujo.',160.00,'/Imagenes/Desayunos/Caja_grande_deluxe.png','Caja Grande','DESAYUNO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Caja Grande Desayuno','Caja para compartir con una selección de productos de desayuno.',140.00,'/Imagenes/Desayunos/Caja_grande_desayuno.png','Caja Grande','DESAYUNO','NINGUNO',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Desayuno clásico','Huevos revueltos con salchicha y pan panecillo tostado.',30.00,'/Imagenes/Desayunos/desayuno_clàsico.png','Desayunos','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Desayuno Deluxe','Huevos revueltos, salchicha, panqueques y pan panecillo tostado.',44.00,'/Imagenes/Desayunos/desayuno_deluxe.png','Desayunos','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Desayuno tradicional','Desayuno chapín con huevo, frijoles, plátanos, queso, crema y tortillas.',45.00,'/Imagenes/Desayunos/desayuno_tradicional.png','Desayunos','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Hot Cakes','Tres panqueques calientes acompañados de mantequilla y miel.',33.00,'/Imagenes/Desayunos/hotcakes.png','Desayunos','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'Pan WlGriddle','Pan suave tipo WlGriddle con un toque dulce de arce.',18.00,'/Imagenes/Desayunos/pan_wlgriddle.png','WlGriddle','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlGriddle de Salchicha y Huevo','Pan tipo WlGriddle con salchicha, huevo y queso.',36.00,'/Imagenes/Desayunos/wlgriddle de salchicha y huevo.png','WlGriddle','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlGriddle Salchicha','Pan tipo WlGriddle con salchicha y queso.',31.00,'/Imagenes/Desayunos/wlgriddle_salchicha.png','WlGriddle','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlGriddle Tocino Huevo','Pan tipo WlGriddle con tocino, huevo y queso.',33.00,'/Imagenes/Desayunos/wlgriddle_tocinohuevo.png','WlGriddle','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Chapín Jamón','Pan panecillo tostado con jamón y acompañamientos estilo chapín.',36.00,'/Imagenes/Desayunos/wlmuffin_chapin_jamon.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Chapín Salchicha','Pan panecillo tostado con salchicha y acompañamientos estilo chapín.',28.00,'/Imagenes/Desayunos/wlmuffin_chapin_salchicha.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Cheddar WlMelt','Pan panecillo tostado con salchicha, huevo y queso cheddar.',42.00,'/Imagenes/Desayunos/wlmuffin_cheddar_wlmelt.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin de Doble Huevo','Pan panecillo tostado con jamón, queso y doble huevo.',41.00,'/Imagenes/Desayunos/eggwlmuffin_doble_huevo.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin de Huevo','Pan panecillo tostado con jamón, huevo y queso.',36.00,'/Imagenes/Desayunos/egg_wlmuffin.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Doble de Huevo','Pan panecillo tostado con jamón, huevo y queso.',37.00,'/Imagenes/Desayunos/egg_wlmuffin_doble.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Huevo y Frijol','Pan panecillo tostado con huevo y frijoles.',24.00,'/Imagenes/Desayunos/wlmuffin_huevofrijol.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Huevo y Queso','Pan panecillo tostado con huevo y queso.',21.00,'/Imagenes/Desayunos/wlmuffin_huevoqueso.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Salchicha','Pan panecillo tostado con salchicha.',28.00,'/Imagenes/Desayunos/wlmuffin_salchicha.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Salchicha Doble y Huevo','Panecillo de desayuno con doble salchicha, huevo y queso.',42.00,'/Imagenes/Desayunos/wlMuffi_Salchich_Doble_y_Huevo.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Salchicha y doble huevo','Pan panecillo tostado con salchicha y doble huevo.',42.00,'/Imagenes/Desayunos/wlMuffin_Salchicha_y_doble huevo.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Salchicha y huevo','Pan panecillo tostado con salchicha, huevo y queso.',37.00,'/Imagenes/Desayunos/wlmuffin_salchichay_huevo.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Súper Chapín con Jamón','Panecillo estilo chapín con jamón y acompañamientos de desayuno.',42.00,'/Imagenes/Desayunos/wlmuffinsuper_chapin_con_jamon.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Súper Chapín Salchicha','Panecillo estilo chapín con salchicha y acompañamientos de desayuno.',35.00,'/Imagenes/Desayunos/wlmuffiinsuper_chapin_salchicha.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Tocino Doble Huevo','Pan panecillo tostado con tocino y doble huevo.',42.00,'/Imagenes/Desayunos/wlmuffin_tocino_doble_huevo.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Desayunos'),'WlMuffin Tocino y Huevo','Pan panecillo tostado con tocino, huevo y queso.',30.00,'/Imagenes/Desayunos/wlmuffin_tocinohuevo.png','WlMuffin','DESAYUNO','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'Pastel de manzana','Pastel caliente con relleno dulce de manzana.',17.00,'/Imagenes/postres/pastel de manzana.png','Pasteles','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'Pastel de queso','Pastel caliente con relleno cremoso de queso.',17.00,'/Imagenes/postres/pastel de queso.png','Pasteles','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'Sundae Caramelo','Helado suave de vainilla cubierto con salsa de caramelo.',15.00,'/Imagenes/postres/sundae caramelo.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'Sundae Chocolate','Helado suave de vainilla cubierto con salsa de chocolate.',15.00,'/Imagenes/postres/sundae chocolate.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'Sundae Fresa','Helado suave de vainilla cubierto con salsa de fresa.',15.00,'/Imagenes/postres/sundae fresa.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlCono Vainilla','Cono crujiente con helado suave de vainilla.',10.00,'/Imagenes/postres/mc cono vainilla.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry M&M''s','Helado suave de vainilla mezclado con M&M.',24.00,'/Imagenes/postres/mcflurry m&ms.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry M&M''s Chocolate','Helado suave de vainilla mezclado con M&M y chocolate.',25.00,'/Imagenes/postres/mflurry m&ms chocolate.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry M&M''s Fresa','Helado suave de vainilla mezclado con M&M y fresa.',25.00,'/Imagenes/postres/mcflurry m&ms fresa.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry Oreo','Helado suave de vainilla mezclado con Oreo.',24.00,'/Imagenes/postres/mcflurry oreo.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry Oreo Caramelo','Helado suave de vainilla mezclado con Oreo y caramelo.',25.00,'/Imagenes/postres/mcflurry oreo caramelo.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry Oreo Chocolate','Helado suave de vainilla mezclado con Oreo y chocolate.',25.00,'/Imagenes/postres/mclfurry oreo chocolate.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Postres'),'WlFlurry Oreo Fresa','Helado suave de vainilla mezclado con Oreo y fresa.',25.00,'/Imagenes/postres/mcflurry oreo fresa.png','Helados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Brownie Bites','Bizcocho de chocolate suave de chocolate con trozos de chocolate.',16.00,'/Imagenes/wlcafe/brownie_bites.png','Postres WlCafé','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Café Blend Grano','Café mezcla en grano para preparar en casa.',55.00,'/Imagenes/wlcafe/cafe_blend_grano.png','Café en bolsa','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Café Blend Molido','Café mezcla molido para preparar en casa.',55.00,'/Imagenes/wlcafe/cafe_blend_molido.png','Café en bolsa','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Café guatemalteco','Café guatemalteco de aroma intenso y sabor balanceado.',25.00,'/Imagenes/wlcafe/cafe_guatemalteco.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Capuccino','Café expreso con leche vaporizada y espuma cremosa.',29.00,'/Imagenes/wlcafe/capuccino.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Cheesecake Fresa','Porción de pastel de queso con cobertura de fresa.',34.00,'/Imagenes/wlcafe/cheescake_fresa.png','Postres WlCafé','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Cheesecake Mora','Porción de pastel de queso con cobertura de mora.',34.00,'/Imagenes/wlcafe/cheescake_mora.png','Postres WlCafé','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Chocolate caliente','Chocolate caliente, cremoso y reconfortante.',29.00,'/Imagenes/wlcafe/chocolate_caliente.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Frappé Caramelo','Bebida frappé fría y cremosa sabor caramelo.',39.00,'/Imagenes/wlcafe/frappe_caramelo.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Frappé Chocolate','Bebida frappé fría y cremosa sabor chocolate.',39.00,'/Imagenes/wlcafe/frappé chocolate.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Frappé Oreo','Bebida frappé fría y cremosa con sabor a Oreo.',39.00,'/Imagenes/wlcafe/Frappé Oreo.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Frappé Original','Bebida frappé fría y cremosa sabor original.',39.00,'/Imagenes/wlcafe/Frappe original.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Frappé Vainilla','Bebida frappé fría y cremosa sabor vainilla.',39.00,'/Imagenes/wlcafe/frappe_vainilla.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Frappé Vainilla Light','Bebida frappé fría y cremosa sabor vainilla.',39.00,'/Imagenes/wlcafe/frappé vainilla light.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Horchata','Bebida fría de horchata con sabor dulce y especiado.',30.00,'/Imagenes/wlcafe/Horchata.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Iced Coffee Caramelo','Café frío con hielo y sabor a caramelo.',35.00,'/Imagenes/wlcafe/iced coffe caramelo.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Iced Coffee Chocolate','Café frío con hielo y sabor a chocolate.',35.00,'/Imagenes/wlcafe/iced coffe chocolate.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Iced Coffee Horchata','Café frío con hielo y sabor a horchata.',35.00,'/Imagenes/wlcafe/Iced coffe horchata.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Iced Coffee Original','Café frío con hielo de sabor original.',35.00,'/Imagenes/wlcafe/Iced coffe original.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Iced Coffee Vainilla','Café frío con hielo y sabor a vainilla.',35.00,'/Imagenes/wlcafe/Iced coffe vainilla.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Iced Coffee Vainilla Light','Café frío con hielo y sabor a vainilla.',35.00,'/Imagenes/wlcafe/iced coffe vainilla light.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Latte','Café expreso combinado con leche caliente y textura cremosa.',30.00,'/Imagenes/wlcafe/latte.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Pastel de elote','Porción de pastel de elote suave y dulce.',32.00,'/Imagenes/wlcafe/pastel_elote.png','Postres WlCafé','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Smoothie Berries','Batido frío de frutos rojos con hielo triturado.',39.00,'/Imagenes/wlcafe/smothie_berries.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Smoothie Mango','Batido frío de mango con hielo triturado.',39.00,'/Imagenes/wlcafe/smothie_mango.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Chai Original','Bebida de té chai con especias y textura cremosa.',41.00,'/Imagenes/wlcafe/techai_original.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Chai Té Verde','Bebida de té chai combinada con té verde.',41.00,'/Imagenes/wlcafe/techai_te_verde.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Chai Vainilla','Bebida de té chai con especias y un toque de vainilla.',41.00,'/Imagenes/wlcafe/te_chai_vainilla.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Chai Vainilla Light','Té chai ligero con sabor a vainilla.',41.00,'/Imagenes/wlcafe/techai_vainilla_light.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Guatemalteco Bora Bora','Infusión aromática con mezcla de sabores frutales.',29.00,'/Imagenes/wlcafe/te_guatemalteco_borabora.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Guatemalteco Melocotón Mix','Infusión de té guatemalteco con notas de melocotón.',29.00,'/Imagenes/wlcafe/te_guatealteco_melocoton_mix.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Guatemalteco Menta Fusión','Infusión aromática con sabor refrescante a menta.',29.00,'/Imagenes/wlcafe/te_guatemalteco_menta_fusion.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Té Guatemalteco Vainilla Relax','Infusión de té con notas suaves de vainilla.',29.00,'/Imagenes/wlcafe/teguatemalteco_vainilla_relax.png','Bebidas Calientes','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Tostado Queso Frijol','Tostado caliente con queso y frijoles.',25.00,'/Imagenes/wlcafe/tostado_queso_frijol.png','Tostados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'Tostado Queso Tomate','Tostado caliente con queso y tomate.',25.00,'/Imagenes/wlcafe/tostado_queso_tomate.png','Tostados','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'WlFizz A.M.','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/wlcafe/wlfizz_A.M..png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'WlFrizz Blue','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/bebidas/wlfrizz blue.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'WlFrizz Manzana Verde','Bebida fría y burbujeante con sabor a manzana verde.',26.00,'/Imagenes/wlcafe/wlfrizz_manzanaverde.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'WlFrizz Pink','Bebida fría y burbujeante con sabor frutal.',26.00,'/Imagenes/wlcafe/wlfrizz_pink.png','Bebidas Frías','TODO_DIA','RECETA',TRUE,0,0,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='WlCafé'),'WlPop Chocolate','Bizcocho individual con relleno de chocolate.',16.00,'/Imagenes/wlcafe/wlpop_chocolate.png','Postres WlCafé','TODO_DIA','DIRECTO',FALSE,100,10,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Papas Kids','Porción infantil de papas.',10.00,'/Imagenes/antojos/papas.png','Complementos','TODO_DIA','DIRECTO',FALSE,200,20,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Puré de manzana','Puré de manzana para menú infantil.',9.00,'/Imagenes/bebidas/jugo_manzana.png','Complementos','TODO_DIA','DIRECTO',FALSE,150,15,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Yogur de fresa','Yogur de fresa para menú infantil.',9.00,NULL,'Complementos','TODO_DIA','DIRECTO',FALSE,150,15,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Jugo de manzana Kids','Jugo de manzana en tamaño infantil.',8.00,'/Imagenes/bebidas/jugo_manzana.png','Bebidas','TODO_DIA','DIRECTO',FALSE,150,15,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Cajita Feliz'),'Juguete sorpresa','Juguete sorpresa disponible para niños.',12.00,NULL,'Juguetes','TODO_DIA','DIRECTO',FALSE,200,20,TRUE),
((SELECT id_categoria FROM categoria WHERE nombre='Bebidas'),'Coca-Cola 1.5 L','Bebida familiar para cajas y combos.',25.00,'/Imagenes/bebidas/coca_cola.png','Sodas','TODO_DIA','DIRECTO',FALSE,100,10,TRUE);
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
  AND (p.nombre LIKE '%Tostado%' OR p.nombre LIKE '%Derretido%')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,3.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso americano'
WHERE p.tipo_stock='RECETA' AND c.nombre IN ('Antojos','WlCafé')
  AND (p.nombre LIKE '%Tostado%' OR p.nombre LIKE '%Derretido%')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,40,TRUE,TRUE,20,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Frijol'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Frijol%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Loroco'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%loroco%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jamón'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%jamón%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,1.50,3
FROM producto p JOIN ingrediente i ON i.nombre='Tomate'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tomate%'

-- Hamburguesas y sándwiches de almuerzo.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan hamburguesa'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'

UNION ALL
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
  AND p.nombre NOT LIKE '%Sándwich%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' THEN 2 ELSE 1 END,
       FALSE,TRUE,1,10.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Filete de pollo'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Pollo%' OR p.nombre LIKE '%Crispy%'
       OR p.nombre LIKE '%Sándwich%')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' OR p.nombre LIKE '%Triple%' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,3.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso americano'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT LIKE '%Cheddar%'
  AND (p.descripcion LIKE '%queso%'
       OR p.nombre REGEXP 'Queso|WlMelt|Big Mac|Big Tasty|WlNífica|WlDouble|Triple Bacon|Pico Guacamol|Smoke Tocino|Gourmet')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,3.50,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso cheddar'
WHERE c.nombre IN ('Almuerzos','Desayunos') AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Cheddar%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' OR p.nombre LIKE '%Triple%' THEN 3 ELSE 2 END,
       TRUE,TRUE,1,4.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Tocino'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Bacon%' OR p.nombre LIKE '%Tocino%')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Lechuga'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Big|Tasty|Deluxe|Gourmet|WlNífica|Pollo|Crispy|Res|Sándwich'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,1.50,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Tomate'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Tasty|Deluxe|Gourmet|WlNífica|Pollo|Crispy|Res|Sándwich'
  AND p.nombre NOT LIKE '%Big Mac%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,10,TRUE,TRUE,5,0.75,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Cebolla'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT REGEXP 'Pollo|Crispy|Sándwich'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,1.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pepinillo'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre NOT REGEXP 'Pollo|Crispy|Sándwich|Gourmet|Tasty'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,12,TRUE,TRUE,6,0.75,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Ketchup'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Hamburguesa|Cuarto|Triple|WlDouble|Quesoburguesa'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,8,TRUE,TRUE,4,0.75,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Mostaza'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Hamburguesa|Cuarto|Triple|WlDouble|Quesoburguesa'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,15,TRUE,TRUE,8,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Mayonesa'
WHERE c.nombre='Almuerzos' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Pollo|Crispy|Sándwich|Deluxe'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa Big Mac'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Big Mac%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa Big Tasty'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Big Tasty%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,35,TRUE,TRUE,20,4.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Guacamole'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Guacamol%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,TRUE,TRUE,15,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Pico de gallo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Pico%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa ahumada'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Smoke%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,TRUE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Salsa ranch'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Ranch%'

-- Desayunos: WlMuffin y WlGriddle.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan muffin'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%WlMuffin%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Pan WlGriddle'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%WlGriddle%'

UNION ALL
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
                       'Desayuno Deluxe','Desayuno tradicional'))

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%Doble%' OR p.nombre='Burritos' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,5.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Salchicha desayuno'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Salchicha%' OR p.descripcion LIKE '%salchicha%'
       OR p.nombre LIKE '%Burrito%'
       OR p.nombre IN ('Desayuno clásico','Desayuno Deluxe'))

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,TRUE,TRUE,1,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Jamón'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.nombre LIKE '%Jamón%' OR p.descripcion LIKE '%jamón%')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,TRUE,TRUE,1,4.00,3
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Tocino'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Tocino%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,TRUE,TRUE,1,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Queso americano'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND (p.descripcion LIKE '%queso%' OR p.nombre LIKE '%Queso%'
       OR p.nombre LIKE '%Cheddar%')
  AND p.nombre NOT LIKE '%Cheddar%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,40,TRUE,TRUE,20,2.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Frijol'
WHERE c.nombre='Desayunos' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Chapín%'

-- Burritos.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre='Burritos' THEN 2 ELSE 1 END,FALSE,FALSE,1,0,1
FROM producto p JOIN ingrediente i ON i.nombre='Tortilla de harina'
WHERE p.tipo_stock='RECETA' AND p.nombre IN ('Burrito','Burritos')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre='Burritos' THEN 60 ELSE 30 END,
       TRUE,TRUE,20,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Vegetales mixtos'
WHERE p.tipo_stock='RECETA' AND p.nombre IN ('Burrito','Burritos')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre='Burritos' THEN 2 ELSE 1 END,
       TRUE,TRUE,1,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Queso americano'
WHERE p.tipo_stock='RECETA' AND p.nombre IN ('Burrito','Burritos')

-- Hot Cakes y platos de desayuno.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,3,FALSE,TRUE,1,5.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Masa de hot cake'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Hot Cakes','Desayuno Deluxe')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,10,TRUE,TRUE,5,1.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Mantequilla'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Hot Cakes','Desayuno Deluxe')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,TRUE,TRUE,15,1.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Miel'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Hot Cakes','Desayuno Deluxe')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,2,FALSE,FALSE,1,0,1
FROM producto p JOIN ingrediente i ON i.nombre='Pan tostado'
WHERE p.tipo_stock='RECETA'
  AND p.nombre IN ('Desayuno clásico','Desayuno Deluxe')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,80,TRUE,TRUE,40,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Frijol'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,80,TRUE,TRUE,40,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Plátano'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,TRUE,TRUE,15,1.50,2
FROM producto p JOIN ingrediente i ON i.nombre='Crema'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,3,FALSE,TRUE,1,1.00,3
FROM producto p JOIN ingrediente i ON i.nombre='Tortilla de maíz'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,TRUE,TRUE,1,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Queso americano'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%tradicional%'

-- Bebidas calientes del catálogo general.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,18,FALSE,TRUE,9,2.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Café molido'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Café%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,250,FALSE,FALSE,50,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Agua preparada'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Café|Chocolate|Té caliente'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,180,FALSE,FALSE,50,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Agua preparada'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'WlFizz|WlFrizz'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,160,FALSE,TRUE,60,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Hielo'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'WlFizz|WlFrizz'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,150,FALSE,TRUE,50,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Leche'
WHERE c.nombre='Bebidas' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'con leche|Chocolate'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Chocolate en polvo'
WHERE p.tipo_stock='RECETA' AND p.nombre='Chocolate'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,FALSE,TRUE,1,1.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Bolsa de té'
WHERE p.tipo_stock='RECETA' AND p.nombre='Té caliente'

-- WlCafé: café, frappés, bebidas frías, té y smoothies.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,18,FALSE,TRUE,9,2.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Café molido'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Café|Coffee|Capuccino|Latte|Frappé'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,180,FALSE,FALSE,50,0,1
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Agua preparada'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.subcategoria IN ('Bebidas Calientes','Bebidas Frías')

UNION ALL
SELECT p.id_producto,i.id_ingrediente,180,FALSE,TRUE,50,3.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Leche'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'Capuccino|Chocolate|Frappé|Coffee|Latte|Chai'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,160,FALSE,TRUE,60,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Hielo'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.subcategoria='Bebidas Frías'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Chocolate en polvo'
WHERE p.tipo_stock='RECETA' AND p.nombre='Chocolate caliente'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe caramelo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Caramelo%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe chocolate'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Chocolate%'
  AND p.nombre<>'Chocolate caliente'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,20,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe vainilla'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Vainilla%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Galleta Oreo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Oreo%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,200,FALSE,TRUE,50,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Base horchata'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Horchata%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,120,FALSE,TRUE,40,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Pulpa de mango'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Mango%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,120,FALSE,TRUE,40,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Pulpa de berries'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Berries%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Mezcla chai'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Chai%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,FALSE,TRUE,1,1.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Bolsa de té'
WHERE c.nombre='WlCafé' AND p.tipo_stock='RECETA'
  AND p.nombre LIKE '%Té Guatemalteco%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe frutal'
WHERE p.tipo_stock='RECETA'
  AND p.nombre REGEXP 'WlFizz|WlFrizz|Bora Bora|Melocotón|Menta'

-- Helados y postres preparados.
UNION ALL
SELECT p.id_producto,i.id_ingrediente,
       CASE WHEN p.nombre LIKE '%WlFlurry%' THEN 180 ELSE 120 END,
       FALSE,TRUE,60,5.00,2
FROM producto p JOIN categoria c ON c.id_categoria=p.id_categoria
JOIN ingrediente i ON i.nombre='Helado de vainilla'
WHERE c.nombre='Postres' AND p.tipo_stock='RECETA'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,1,FALSE,FALSE,1,0,1
FROM producto p JOIN ingrediente i ON i.nombre='Cono'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Cono%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Galleta Oreo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%WlFlurry Oreo%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,30,FALSE,TRUE,15,3.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Chocolate M&M'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%M&M%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe caramelo'
WHERE p.tipo_stock='RECETA' AND p.nombre LIKE '%Caramelo%'

UNION ALL
SELECT p.id_producto,i.id_ingrediente,25,FALSE,TRUE,10,2.00,2
FROM producto p JOIN ingrediente i ON i.nombre='Jarabe chocolate'
WHERE p.tipo_stock='RECETA' AND p.nombre REGEXP 'Sundae Chocolate|WlFlurry.*Chocolate'

UNION ALL
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
