package CRUD;

import Conexion.Conexion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Consultas de solo lectura para el panel de inicio del administrador. */
public final class DashboardCRUD {

    public record Resumen(BigDecimal ventasHoy, int pedidosHoy,
            BigDecimal ticketHoy, int productosStockBajo,
            BigDecimal ventasAyer, int pedidosAyer, BigDecimal ticketAyer) {
    }

    public record Pedido(int id, int numeroOrden, String cajero,
            LocalDateTime fechaHora, String tipoServicio, String estado,
            BigDecimal total) {
    }

    public record ProductoVendido(int id, String nombre, String categoria,
            long unidades, boolean activo, boolean disponible) {
    }

    public record Alerta(boolean esProducto, int id, String nombre,
            BigDecimal stockActual, BigDecimal stockMinimo, String unidad,
            LocalDateTime ultimoMovimiento) {

        public String nivel() {
            if (stockActual.signum() == 0) {
                return "Agotado";
            }
            if (stockActual.multiply(BigDecimal.valueOf(2))
                    .compareTo(stockMinimo) <= 0) {
                return "Stock crítico";
            }
            return "Stock bajo";
        }

        public String detalle() {
            return nombre + " (quedan " + numero(stockActual) + " " + unidad
                    + "; mínimo " + numero(stockMinimo) + ")";
        }
    }

    public record Datos(Resumen resumen, List<Pedido> pedidos,
            List<ProductoVendido> productos, List<Alerta> alertas) {
    }

    private record Totales(BigDecimal ventas, int pedidos, int pagados) {
        BigDecimal ticket() {
            return pagados == 0 ? BigDecimal.ZERO
                    : ventas.divide(BigDecimal.valueOf(pagados), 2,
                            RoundingMode.HALF_UP);
        }
    }

    private Connection conectar() throws SQLException {
        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            throw new SQLException("No fue posible conectar con MySQL.");
        }
        return conexion;
    }

    /** Carga los datos de las tarjetas y las filas visibles en una conexión. */
    public Datos cargar() throws SQLException {
        try (Connection conexion = conectar()) {
            return new Datos(obtenerResumen(conexion),
                    consultarPedidos(conexion, 5),
                    consultarProductos(conexion, 5),
                    consultarAlertas(conexion));
        }
    }

    /** limite = 0 devuelve todos los pedidos, para el botón «Ver todos». */
    public List<Pedido> obtenerPedidos(int limite) throws SQLException {
        try (Connection conexion = conectar()) {
            return consultarPedidos(conexion, limite);
        }
    }

    /** limite = 0 devuelve todos los productos vendidos. */
    public List<ProductoVendido> obtenerProductosVendidos(int limite)
            throws SQLException {
        try (Connection conexion = conectar()) {
            return consultarProductos(conexion, limite);
        }
    }

    public List<Alerta> obtenerAlertas() throws SQLException {
        try (Connection conexion = conectar()) {
            return consultarAlertas(conexion);
        }
    }

    private Resumen obtenerResumen(Connection conexion) throws SQLException {
        // DATETIME se guarda como hora local de Guatemala en este proyecto.
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Guatemala"));
        Totales datosHoy = consultarDia(conexion, hoy);
        Totales datosAyer = consultarDia(conexion, hoy.minusDays(1));

        String sql = """
                SELECT COUNT(*) AS total
                FROM producto
                WHERE estado = TRUE AND tipo_stock = 'DIRECTO'
                    AND stock_minimo > 0
                    AND stock_actual <= stock_minimo
                """;
        int stockBajo;
        try (PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            stockBajo = rs.getInt("total");
        }
        return new Resumen(datosHoy.ventas(), datosHoy.pedidos(),
                datosHoy.ticket(), stockBajo,
                datosAyer.ventas(), datosAyer.pedidos(), datosAyer.ticket());
    }

    private Totales consultarDia(Connection conexion, LocalDate fecha)
            throws SQLException {
        String sql = """
                SELECT COUNT(*) AS pedidos,
                    COALESCE(SUM(CASE WHEN estado = 'PAGADO'
                        THEN total ELSE 0 END), 0) AS ventas,
                    COALESCE(SUM(CASE WHEN estado = 'PAGADO'
                        THEN 1 ELSE 0 END), 0) AS pagados
                FROM pedido
                WHERE fecha_hora >= ? AND fecha_hora < ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fecha.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(fecha.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Totales(rs.getBigDecimal("ventas"),
                        rs.getInt("pedidos"), rs.getInt("pagados"));
            }
        }
    }

    private List<Pedido> consultarPedidos(Connection conexion, int limite)
            throws SQLException {
        String sql = """
                SELECT p.id_pedido, p.numero_orden,
                    CONCAT(u.nombre, ' ', u.apellido) AS cajero,
                    p.fecha_hora, p.tipo_servicio, p.estado, p.total
                FROM pedido p
                JOIN usuario u ON u.id_usuario = p.id_usuario
                ORDER BY p.fecha_hora DESC, p.id_pedido DESC
                """ + (limite > 0 ? " LIMIT ?" : "");
        List<Pedido> filas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (limite > 0) {
                ps.setInt(1, limite);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Pedido(rs.getInt("id_pedido"),
                            rs.getInt("numero_orden"), rs.getString("cajero"),
                            rs.getTimestamp("fecha_hora").toLocalDateTime(),
                            rs.getString("tipo_servicio"),
                            rs.getString("estado"), rs.getBigDecimal("total")));
                }
            }
        }
        return filas;
    }

    private List<ProductoVendido> consultarProductos(Connection conexion, int limite)
            throws SQLException {
        String sql = """
                SELECT pr.id_producto, pr.nombre, c.nombre AS categoria,
                    SUM(d.cantidad) AS unidades, pr.estado,
                    CASE
                        WHEN pr.estado = FALSE THEN FALSE
                        WHEN pr.tipo_stock = 'DIRECTO'
                            THEN pr.stock_actual > 0
                        WHEN pr.tipo_stock = 'RECETA' THEN
                            EXISTS (
                                SELECT 1
                                FROM producto_ingrediente receta
                                WHERE receta.id_producto = pr.id_producto
                                    AND receta.estado = TRUE
                                    AND receta.cantidad_default > 0
                            )
                            AND NOT EXISTS (
                                SELECT 1
                                FROM producto_ingrediente receta
                                LEFT JOIN ingrediente ingrediente_receta
                                    ON ingrediente_receta.id_ingrediente =
                                        receta.id_ingrediente
                                WHERE receta.id_producto = pr.id_producto
                                    AND receta.estado = TRUE
                                    AND receta.cantidad_default > 0
                                    AND (ingrediente_receta.id_ingrediente IS NULL
                                        OR ingrediente_receta.estado = FALSE
                                        OR ingrediente_receta.stock_actual
                                            < receta.cantidad_default)
                            )
                        ELSE TRUE
                    END AS disponible
                FROM pedido_detalle d
                JOIN pedido p ON p.id_pedido = d.id_pedido
                JOIN presentacion_menu pm
                    ON pm.id_presentacion = d.id_presentacion
                JOIN producto pr
                    ON pr.id_producto = pm.id_producto_principal
                JOIN categoria c ON c.id_categoria = pr.id_categoria
                WHERE p.estado = 'PAGADO'
                GROUP BY pr.id_producto, pr.nombre, c.nombre,
                    pr.estado, pr.tipo_stock, pr.stock_actual
                ORDER BY unidades DESC, pr.nombre ASC
                """ + (limite > 0 ? " LIMIT ?" : "");
        List<ProductoVendido> filas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (limite > 0) {
                ps.setInt(1, limite);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new ProductoVendido(rs.getInt("id_producto"),
                            rs.getString("nombre"), rs.getString("categoria"),
                            rs.getLong("unidades"), rs.getBoolean("estado"),
                            rs.getBoolean("disponible")));
                }
            }
        }
        return filas;
    }

    private List<Alerta> consultarAlertas(Connection conexion) throws SQLException {
        List<Alerta> alertas = new ArrayList<>();
        String productosSql = """
                SELECT p.id_producto, p.nombre, p.stock_actual, p.stock_minimo,
                    (SELECT MAX(m.fecha_hora) FROM movimiento_inventario m
                     WHERE m.id_producto = p.id_producto) AS ultimo
                FROM producto p
                WHERE p.estado = TRUE AND p.tipo_stock = 'DIRECTO'
                    AND p.stock_minimo > 0
                    AND p.stock_actual <= p.stock_minimo
                """;
        try (PreparedStatement ps = conexion.prepareStatement(productosSql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alertas.add(new Alerta(true, rs.getInt("id_producto"),
                        rs.getString("nombre"), rs.getBigDecimal("stock_actual"),
                        rs.getBigDecimal("stock_minimo"), "unidades",
                        fechaOpcional(rs, "ultimo")));
            }
        }

        String ingredientesSql = """
                SELECT i.id_ingrediente, i.nombre, i.stock_actual,
                    i.stock_minimo, i.unidad_medida,
                    (SELECT MAX(m.fecha_hora) FROM movimiento_inventario m
                     WHERE m.id_ingrediente = i.id_ingrediente) AS ultimo
                FROM ingrediente i
                WHERE i.estado = TRUE AND i.stock_minimo > 0
                    AND i.stock_actual <= i.stock_minimo
                """;
        try (PreparedStatement ps = conexion.prepareStatement(ingredientesSql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alertas.add(new Alerta(false, rs.getInt("id_ingrediente"),
                        rs.getString("nombre"), rs.getBigDecimal("stock_actual"),
                        rs.getBigDecimal("stock_minimo"),
                        rs.getString("unidad_medida"),
                        fechaOpcional(rs, "ultimo")));
            }
        }
        alertas.sort(Comparator.comparing(Alerta::ultimoMovimiento,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return alertas;
    }

    private static LocalDateTime fechaOpcional(ResultSet rs, String columna)
            throws SQLException {
        Timestamp fecha = rs.getTimestamp(columna);
        return fecha == null ? null : fecha.toLocalDateTime();
    }

    private static String numero(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}
