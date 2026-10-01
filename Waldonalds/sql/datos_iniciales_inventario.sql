-- Datos iniciales de inventario para pruebas de Waldonalds.
-- Ejecutar después de tener creada la tabla ingrediente.
USE waldonalds;

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

SELECT id_ingrediente, nombre, unidad_medida, stock_actual, stock_minimo, estado
FROM ingrediente
ORDER BY id_ingrediente;
