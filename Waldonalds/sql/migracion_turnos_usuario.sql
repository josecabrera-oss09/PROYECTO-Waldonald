USE waldonalds;

-- Ejecutar una sola vez sobre una base existente.
-- Estas columnas son administradas desde Gestión de Usuarios.
ALTER TABLE usuario
    ADD COLUMN turno ENUM('MANANA', 'TARDE') NULL,
    ADD COLUMN hora_inicio TIME NULL,
    ADD COLUMN hora_fin TIME NULL;
