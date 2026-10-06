package DAO;

import Conexion.Conexion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Consulta de solo lectura para reconstruir una venta completa. */
public final class VentaDetalleDAO {

    public record Modificacion(String tipo, String ingrediente,
            BigDecimal cantidad, BigDecimal precioExtra) {
    }

    public record Producto(String nombre, int cantidad,
            List<Modificacion> modificaciones) {
    }

    public record Opcion(int numero, String grupo, String opcion,
            BigDecimal precioExtra, List<Producto> productos) {
    }

    public record Linea(String nombre, String presentacion, int cantidad,
            BigDecimal precioUnitario, BigDecimal subtotal,
            List<Opcion> opciones) {
    }

    public record Venta(int idPedido, int numeroOrden, String cajero,
            LocalDateTime fechaHora, String tipoServicio, String estado,
            String metodoPago, BigDecimal montoRecibido, BigDecimal total,
            List<Linea> lineas) {
    }

    public Venta cargar(int idPedido) throws SQLException {
        try (Connection conexion = Conexion.conectar()) {
            if (conexion == null) {
                throw new SQLException("No fue posible conectar con MySQL.");
            }
            conexion.setReadOnly(true);
            Venta cabecera = consultarCabecera(conexion, idPedido);
            Map<Integer, List<Modificacion>> modificaciones =
                    consultarModificaciones(conexion, idPedido);
            Map<Integer, List<Producto>> productos =
                    consultarProductos(conexion, idPedido, modificaciones);
            Map<Integer, List<Opcion>> opciones =
                    consultarOpciones(conexion, idPedido, productos);
            List<Linea> lineas = consultarLineas(
                    conexion, idPedido, opciones);
            return new Venta(cabecera.idPedido(), cabecera.numeroOrden(),
                    cabecera.cajero(), cabecera.fechaHora(),
                    cabecera.tipoServicio(), cabecera.estado(),
                    cabecera.metodoPago(), cabecera.montoRecibido(),
                    cabecera.total(), List.copyOf(lineas));
        }
    }

    private Venta consultarCabecera(Connection conexion, int idPedido)
            throws SQLException {
        String sql = """
                SELECT p.id_pedido, p.numero_orden,
                    CONCAT(u.nombre, ' ', u.apellido) AS cajero,
                    p.fecha_hora, p.tipo_servicio, p.estado,
                    p.metodo_pago, p.monto_recibido, p.total
                FROM pedido p
                JOIN usuario u ON u.id_usuario = p.id_usuario
                WHERE p.id_pedido = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setQueryTimeout(20);
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La venta seleccionada ya no existe.");
                }
                return new Venta(rs.getInt("id_pedido"),
                        rs.getInt("numero_orden"), rs.getString("cajero"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime(),
                        rs.getString("tipo_servicio"), rs.getString("estado"),
                        rs.getString("metodo_pago"),
                        rs.getBigDecimal("monto_recibido"),
                        rs.getBigDecimal("total"), List.of());
            }
        }
    }

    private Map<Integer, List<Modificacion>> consultarModificaciones(
            Connection conexion, int idPedido) throws SQLException {
        String sql = """
                SELECT pm.id_pedido_producto, pm.tipo, pm.ingrediente,
                    pm.cantidad, pm.precio_extra
                FROM pedido_modificacion pm
                JOIN pedido_producto pp
                    ON pp.id_pedido_producto = pm.id_pedido_producto
                JOIN pedido_opcion po
                    ON po.id_pedido_opcion = pp.id_pedido_opcion
                JOIN pedido_detalle d ON d.id_detalle = po.id_detalle
                WHERE d.id_pedido = ?
                ORDER BY pm.id_modificacion
                """;
        Map<Integer, List<Modificacion>> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setQueryTimeout(20);
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.computeIfAbsent(
                            rs.getInt("id_pedido_producto"),
                            clave -> new ArrayList<>()).add(new Modificacion(
                                    rs.getString("tipo"),
                                    rs.getString("ingrediente"),
                                    rs.getBigDecimal("cantidad"),
                                    rs.getBigDecimal("precio_extra")));
                }
            }
        }
        return resultado;
    }

    private Map<Integer, List<Producto>> consultarProductos(
            Connection conexion,
            int idPedido,
            Map<Integer, List<Modificacion>> modificaciones)
            throws SQLException {
        String sql = """
                SELECT pp.id_pedido_producto, pp.id_pedido_opcion,
                    pp.nombre, pp.cantidad
                FROM pedido_producto pp
                JOIN pedido_opcion po
                    ON po.id_pedido_opcion = pp.id_pedido_opcion
                JOIN pedido_detalle d ON d.id_detalle = po.id_detalle
                WHERE d.id_pedido = ?
                ORDER BY pp.id_pedido_producto
                """;
        Map<Integer, List<Producto>> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setQueryTimeout(20);
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idProductoPedido = rs.getInt("id_pedido_producto");
                    resultado.computeIfAbsent(rs.getInt("id_pedido_opcion"),
                            clave -> new ArrayList<>()).add(new Producto(
                                    rs.getString("nombre"),
                                    rs.getInt("cantidad"),
                                    List.copyOf(modificaciones.getOrDefault(
                                            idProductoPedido, List.of()))));
                }
            }
        }
        return resultado;
    }

    private Map<Integer, List<Opcion>> consultarOpciones(
            Connection conexion,
            int idPedido,
            Map<Integer, List<Producto>> productos) throws SQLException {
        String sql = """
                SELECT po.id_pedido_opcion, po.id_detalle, po.numero,
                    po.grupo, po.opcion, po.precio_extra
                FROM pedido_opcion po
                JOIN pedido_detalle d ON d.id_detalle = po.id_detalle
                WHERE d.id_pedido = ?
                ORDER BY po.id_pedido_opcion
                """;
        Map<Integer, List<Opcion>> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setQueryTimeout(20);
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idOpcion = rs.getInt("id_pedido_opcion");
                    resultado.computeIfAbsent(rs.getInt("id_detalle"),
                            clave -> new ArrayList<>()).add(new Opcion(
                                    rs.getInt("numero"),
                                    rs.getString("grupo"),
                                    rs.getString("opcion"),
                                    rs.getBigDecimal("precio_extra"),
                                    List.copyOf(productos.getOrDefault(
                                            idOpcion, List.of()))));
                }
            }
        }
        return resultado;
    }

    private List<Linea> consultarLineas(
            Connection conexion,
            int idPedido,
            Map<Integer, List<Opcion>> opciones) throws SQLException {
        String sql = """
                SELECT id_detalle, nombre, presentacion, cantidad,
                    precio_unitario, subtotal
                FROM pedido_detalle
                WHERE id_pedido = ?
                ORDER BY id_detalle
                """;
        List<Linea> resultado = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setQueryTimeout(20);
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idDetalle = rs.getInt("id_detalle");
                    resultado.add(new Linea(rs.getString("nombre"),
                            rs.getString("presentacion"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getBigDecimal("subtotal"),
                            List.copyOf(opciones.getOrDefault(
                                    idDetalle, List.of()))));
                }
            }
        }
        return resultado;
    }
}
