package DAO;

import Conexion.Conexion;
import Modelos.ArticuloInventario;
import Modelos.MovimientoInventario;
import Modelos.PaginaInventario;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Consultas y movimientos del inventario unificado. */
public class InventarioDAO {

    private static final BigDecimal MAX_STOCK = new BigDecimal("99999999.99");
    private static final String INVENTARIO_BASE = """
            SELECT tipo, id, nombre, categoria, unidad, stock_actual,
                   stock_minimo, activo, ultimo_movimiento
            FROM (
                SELECT 'INGREDIENTE' AS tipo,
                       i.id_ingrediente AS id,
                       i.nombre,
                       'Ingredientes' AS categoria,
                       i.unidad_medida AS unidad,
                       i.stock_actual,
                       i.stock_minimo,
                       i.estado AS activo,
                       (SELECT MAX(mi.fecha_hora)
                          FROM movimiento_inventario mi
                         WHERE mi.id_ingrediente = i.id_ingrediente)
                           AS ultimo_movimiento
                  FROM ingrediente i
                UNION ALL
                SELECT 'PRODUCTO' AS tipo,
                       p.id_producto AS id,
                       p.nombre,
                       c.nombre AS categoria,
                       'unidad' AS unidad,
                       CAST(p.stock_actual AS DECIMAL(12,2)) AS stock_actual,
                       CAST(p.stock_minimo AS DECIMAL(12,2)) AS stock_minimo,
                       p.estado AS activo,
                       (SELECT MAX(mi.fecha_hora)
                          FROM movimiento_inventario mi
                         WHERE mi.id_producto = p.id_producto)
                           AS ultimo_movimiento
                  FROM producto p
                  JOIN categoria c ON c.id_categoria = p.id_categoria
                 WHERE p.tipo_stock = 'DIRECTO'
            ) inventario
            """;

    private Connection conectar() throws SQLException {
        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            throw new SQLException("No fue posible conectar con MySQL.");
        }
        return conexion;
    }

    public PaginaInventario listarPagina(String busqueda, String tipo,
            String estado, int pagina, int porPagina) throws SQLException {
        StringBuilder condiciones = new StringBuilder(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        agregarFiltros(condiciones, parametros, busqueda, tipo, estado);

        String contar = "SELECT COUNT(*) AS total FROM ("
                + INVENTARIO_BASE + ") lista" + condiciones;
        String listar = INVENTARIO_BASE + condiciones + """

                ORDER BY
                    CASE
                        WHEN activo = FALSE THEN 4
                        WHEN stock_actual <= 0 THEN 1
                        WHEN stock_minimo > 0 AND stock_actual <= stock_minimo THEN 2
                        ELSE 3
                    END,
                    nombre
                LIMIT ? OFFSET ?
                """;

        try (Connection conexion = conectar()) {
            int total;
            try (PreparedStatement ps = conexion.prepareStatement(contar)) {
                colocarParametros(ps, parametros);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt("total");
                }
            }
            List<ArticuloInventario> articulos = new ArrayList<>();
            try (PreparedStatement ps = conexion.prepareStatement(listar)) {
                int indice = colocarParametros(ps, parametros);
                ps.setInt(indice++, porPagina);
                ps.setInt(indice, (pagina - 1) * porPagina);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) articulos.add(mapearArticulo(rs));
                }
            }
            return new PaginaInventario(articulos, total);
        }
    }

    public List<ArticuloInventario> listarParaExportar(String busqueda,
            String tipo, String estado) throws SQLException {
        StringBuilder condiciones = new StringBuilder(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        agregarFiltros(condiciones, parametros, busqueda, tipo, estado);
        String sql = INVENTARIO_BASE + condiciones + " ORDER BY tipo, nombre";
        List<ArticuloInventario> articulos = new ArrayList<>();
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            colocarParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) articulos.add(mapearArticulo(rs));
            }
        }
        return articulos;
    }

    public List<MovimientoInventario> listarMovimientos(int limite)
            throws SQLException {
        String sql = """
                SELECT mi.id_movimiento, mi.fecha_hora,
                       CASE WHEN mi.id_ingrediente IS NOT NULL
                            THEN 'Ingrediente' ELSE 'Producto directo' END tipo_articulo,
                       COALESCE(i.nombre, p.nombre, 'Artículo no disponible') articulo,
                       mi.tipo_movimiento, mi.cantidad, mi.motivo, mi.id_pedido
                  FROM movimiento_inventario mi
             LEFT JOIN ingrediente i ON i.id_ingrediente = mi.id_ingrediente
             LEFT JOIN producto p ON p.id_producto = mi.id_producto
                 WHERE mi.id_ingrediente IS NOT NULL
                    OR (mi.id_producto IS NOT NULL AND p.tipo_stock = 'DIRECTO')
              ORDER BY mi.fecha_hora DESC, mi.id_movimiento DESC
                 LIMIT ?
                """;
        List<MovimientoInventario> movimientos = new ArrayList<>();
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limite));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp fecha = rs.getTimestamp("fecha_hora");
                    int pedido = rs.getInt("id_pedido");
                    boolean pedidoNulo = rs.wasNull();
                    movimientos.add(new MovimientoInventario(
                            rs.getInt("id_movimiento"),
                            fecha == null ? null : fecha.toLocalDateTime(),
                            rs.getString("tipo_articulo"),
                            rs.getString("articulo"),
                            rs.getString("tipo_movimiento"),
                            rs.getBigDecimal("cantidad"),
                            rs.getString("motivo"),
                            pedidoNulo ? null : pedido));
                }
            }
        }
        return movimientos;
    }

    /**
     * Entrada y salida reciben cuánto se mueve. Ajuste recibe el stock final.
     * La actualización y el historial se guardan dentro de una transacción.
     */
    public BigDecimal registrarMovimiento(ArticuloInventario articulo,
            String tipoMovimiento, BigDecimal cantidad, String motivo)
            throws SQLException {
        validarMovimiento(articulo, tipoMovimiento, cantidad, motivo);
        boolean producto = articulo.esProducto();
        if (producto && cantidad.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException(
                    "Los productos directos se manejan en unidades completas.");
        }

        String tabla = producto ? "producto" : "ingrediente";
        String columnaId = producto ? "id_producto" : "id_ingrediente";
        String seleccionar = "SELECT stock_actual, estado"
                + (producto ? ", tipo_stock" : "")
                + " FROM " + tabla + " WHERE " + columnaId + " = ? FOR UPDATE";

        try (Connection conexion = conectar()) {
            conexion.setAutoCommit(false);
            try {
                BigDecimal anterior;
                try (PreparedStatement ps = conexion.prepareStatement(seleccionar)) {
                    ps.setInt(1, articulo.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("No se encontró el artículo.");
                        if (!rs.getBoolean("estado")) {
                            throw new SQLException("El artículo está inactivo.");
                        }
                        if (producto && !"DIRECTO".equals(rs.getString("tipo_stock"))) {
                            throw new SQLException("El producto ya no usa stock directo.");
                        }
                        anterior = rs.getBigDecimal("stock_actual");
                    }
                }

                BigDecimal nuevo = switch (tipoMovimiento) {
                    case "ENTRADA" -> anterior.add(cantidad);
                    case "SALIDA" -> anterior.subtract(cantidad);
                    default -> cantidad;
                };
                if (nuevo.signum() < 0) {
                    throw new SQLException("No hay existencias suficientes para la salida.");
                }
                if (nuevo.compareTo(MAX_STOCK) > 0) {
                    throw new SQLException("El stock supera el máximo permitido.");
                }
                if (producto && nuevo.stripTrailingZeros().scale() > 0) {
                    throw new IllegalArgumentException(
                            "El stock final del producto debe ser un número entero.");
                }

                BigDecimal cantidadGuardada = "AJUSTE".equals(tipoMovimiento)
                        ? nuevo.subtract(anterior) : cantidad;
                if (cantidadGuardada.signum() == 0) {
                    throw new SQLException("El ajuste no cambia el stock.");
                }

                try (PreparedStatement ps = conexion.prepareStatement(
                        "UPDATE " + tabla + " SET stock_actual = ? WHERE "
                                + columnaId + " = ?")) {
                    ps.setBigDecimal(1, nuevo);
                    ps.setInt(2, articulo.getId());
                    ps.executeUpdate();
                }

                String insertar = producto
                        ? "INSERT INTO movimiento_inventario "
                            + "(id_producto, tipo_movimiento, cantidad, motivo) VALUES (?, ?, ?, ?)"
                        : "INSERT INTO movimiento_inventario "
                            + "(id_ingrediente, tipo_movimiento, cantidad, motivo) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conexion.prepareStatement(insertar)) {
                    ps.setInt(1, articulo.getId());
                    ps.setString(2, tipoMovimiento);
                    ps.setBigDecimal(3, cantidadGuardada);
                    ps.setString(4, motivo == null || motivo.isBlank()
                            ? null : motivo.trim());
                    ps.executeUpdate();
                }
                conexion.commit();
                return nuevo;
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private void agregarFiltros(StringBuilder condiciones,
            List<Object> parametros, String busqueda, String tipo,
            String estado) {
        if (busqueda != null && !busqueda.isBlank()) {
            condiciones.append(" AND (nombre LIKE ? OR categoria LIKE ? OR unidad LIKE ?)");
            String texto = "%" + busqueda.trim() + "%";
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
        }
        if (tipo != null && !tipo.isBlank()) {
            condiciones.append(" AND tipo = ?");
            parametros.add(tipo);
        }
        if (estado != null && !estado.isBlank()) {
            switch (estado) {
                case "DISPONIBLE" -> condiciones.append(
                        " AND activo = TRUE AND stock_actual > 0"
                        + " AND (stock_minimo = 0 OR stock_actual > stock_minimo)");
                case "BAJO" -> condiciones.append(
                        " AND activo = TRUE AND stock_actual > 0"
                        + " AND stock_minimo > 0 AND stock_actual <= stock_minimo");
                case "AGOTADO" -> condiciones.append(
                        " AND activo = TRUE AND stock_actual <= 0");
                case "INACTIVO" -> condiciones.append(" AND activo = FALSE");
                default -> { }
            }
        }
    }

    private void validarMovimiento(ArticuloInventario articulo,
            String tipoMovimiento, BigDecimal cantidad, String motivo) {
        if (articulo == null) {
            throw new IllegalArgumentException("Selecciona un artículo.");
        }
        if (!("ENTRADA".equals(tipoMovimiento)
                || "SALIDA".equals(tipoMovimiento)
                || "AJUSTE".equals(tipoMovimiento))) {
            throw new IllegalArgumentException("Tipo de movimiento inválido.");
        }
        boolean ajuste = "AJUSTE".equals(tipoMovimiento);
        if (cantidad == null || cantidad.scale() > 2
                || cantidad.compareTo(MAX_STOCK) > 0
                || (ajuste ? cantidad.signum() < 0 : cantidad.signum() <= 0)) {
            throw new IllegalArgumentException(
                    "Cantidad inválida; usa hasta dos decimales.");
        }
        if (motivo != null && motivo.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "El motivo admite hasta 150 caracteres.");
        }
    }

    private int colocarParametros(PreparedStatement ps, List<Object> parametros)
            throws SQLException {
        int indice = 1;
        for (Object valor : parametros) ps.setObject(indice++, valor);
        return indice;
    }

    private ArticuloInventario mapearArticulo(ResultSet rs)
            throws SQLException {
        Timestamp fecha = rs.getTimestamp("ultimo_movimiento");
        return new ArticuloInventario(
                rs.getString("tipo"), rs.getInt("id"),
                rs.getString("nombre"), rs.getString("categoria"),
                rs.getString("unidad"), rs.getBigDecimal("stock_actual"),
                rs.getBigDecimal("stock_minimo"), rs.getBoolean("activo"),
                fecha == null ? null : fecha.toLocalDateTime());
    }
}
