-- Agrega el seguimiento de preparación a una base ya existente.
-- Compatible con versiones de MySQL que no admiten IF NOT EXISTS en ALTER TABLE.
SET @sql = (SELECT IF(COUNT(*) = 0,
    'ALTER TABLE pedido ADD COLUMN tiempo_preparacion_min INT NOT NULL DEFAULT 5',
    'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido'
      AND COLUMN_NAME = 'tiempo_preparacion_min');
PREPARE sentencia FROM @sql;
EXECUTE sentencia;
DEALLOCATE PREPARE sentencia;

SET @sql = (SELECT IF(COUNT(*) = 0,
    'ALTER TABLE pedido ADD COLUMN estado_preparacion VARCHAR(20) NOT NULL DEFAULT ''PENDIENTE''',
    'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido'
      AND COLUMN_NAME = 'estado_preparacion');
PREPARE sentencia FROM @sql;
EXECUTE sentencia;
DEALLOCATE PREPARE sentencia;
