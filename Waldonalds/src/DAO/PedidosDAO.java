package DAO;

import Conexion.Conexion;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Utilidades.SesionUsuario;

/** Consulta y cancelación segura de pedidos en preparación. */
public final class PedidosDAO {
    public record Pedido(int id, int orden, LocalDateTime fecha, String servicio,
            String metodo, String estado, BigDecimal total, boolean listoDirecto,
            long segundosTranscurridos) { }

    public List<Pedido> listar() throws SQLException {
        return listar(SesionUsuario.getIdUsuario());
    }

    public List<Pedido> listar(int idUsuario) throws SQLException {
        if (idUsuario <= 0) return List.of();
        boolean administrador = "ADMINISTRADOR".equalsIgnoreCase(SesionUsuario.getRol());
        try (Connection c = Conexion.conectar()) {
            if (c == null) throw new SQLException("No se pudo conectar con MySQL.");
            asegurarColumnas(c);
            String sql = "SELECT p.id_pedido,p.numero_orden,p.fecha_hora,p.tipo_servicio,p.metodo_pago,p.estado,p.total, "
                    + "CASE WHEN EXISTS (SELECT 1 FROM pedido_detalle d "
                    + "JOIN presentacion_menu pm ON pm.id_presentacion=d.id_presentacion "
                    + "JOIN producto pr ON pr.id_producto=pm.id_producto_principal "
                    + "WHERE d.id_pedido=p.id_pedido AND LOWER(REPLACE(pr.nombre,'é','e')) LIKE '%cafe blend%') "
                    + "AND NOT EXISTS (SELECT 1 FROM pedido_detalle d "
                    + "JOIN presentacion_menu pm ON pm.id_presentacion=d.id_presentacion "
                    + "JOIN producto pr ON pr.id_producto=pm.id_producto_principal "
                    + "WHERE d.id_pedido=p.id_pedido AND LOWER(REPLACE(pr.nombre,'é','e')) NOT LIKE '%cafe blend%') "
                    + "THEN 1 ELSE 0 END AS listo_directo, "
                    + "TIMESTAMPDIFF(SECOND,p.fecha_hora,NOW()) "
                    + "FROM pedido p "
                    + (administrador ? "" : "WHERE p.id_usuario=? ")
                    + "ORDER BY p.id_pedido DESC LIMIT 100";
            try (PreparedStatement s = c.prepareStatement(sql)) {
                if (!administrador) s.setInt(1, idUsuario);
                try (ResultSet r = s.executeQuery()) {
                List<Pedido> pedidos = new ArrayList<>();
                while (r.next()) {
                    // La instalación actual guarda fecha_hora seis horas atrasada respecto a Guatemala.
                    LocalDateTime fechaGuatemala = r.getTimestamp(3).toLocalDateTime().plusHours(6);
                    pedidos.add(new Pedido(r.getInt(1), r.getInt(2),
                            fechaGuatemala, r.getString(4), r.getString(5), r.getString(6),
                            r.getBigDecimal(7), r.getBoolean(8), r.getLong(9)));
                }
                return pedidos;
                }
            }
        }
    }

    public void cancelar(int idPedido) throws SQLException {
        cancelar(idPedido, SesionUsuario.getIdUsuario());
    }

    public void cancelar(int idPedido, int idUsuario) throws SQLException {
        try (Connection c = Conexion.conectar()) {
            if (c == null) throw new SQLException("No se pudo conectar con MySQL.");
            asegurarColumnas(c);
            c.setAutoCommit(false);
            try {
                String estado;
                int propietario;
                try (PreparedStatement s = c.prepareStatement(
                        "SELECT estado,id_usuario FROM pedido WHERE id_pedido=? FOR UPDATE")) {
                    s.setInt(1, idPedido);
                    try (ResultSet r = s.executeQuery()) {
                        if (!r.next()) throw new IllegalArgumentException("El pedido ya no existe.");
                        estado = r.getString(1);
                        propietario = r.getInt(2);
                    }
                }
                boolean administrador = "ADMINISTRADOR".equalsIgnoreCase(SesionUsuario.getRol());
                if (idUsuario <= 0 || (!administrador && propietario != idUsuario))
                    throw new IllegalArgumentException("El pedido no pertenece a la sesión actual.");
                if (!"PAGADO".equals(estado))
                    throw new IllegalArgumentException("El pedido ya está cancelado.");

                try (PreparedStatement s = c.prepareStatement(
                        "SELECT id_producto,id_ingrediente,cantidad FROM movimiento_inventario "
                        + "WHERE id_pedido=? AND tipo_movimiento='SALIDA' FOR UPDATE")) {
                    s.setInt(1, idPedido);
                    try (ResultSet r = s.executeQuery()) {
                        while (r.next()) {
                            Integer producto = (Integer) r.getObject(1);
                            Integer ingrediente = (Integer) r.getObject(2);
                            BigDecimal cantidad = r.getBigDecimal(3);
                            if (producto != null) {
                                ejecutar(c, "UPDATE producto SET stock_actual=stock_actual+? WHERE id_producto=?", cantidad, producto);
                                ejecutar(c, "INSERT INTO movimiento_inventario(id_producto,id_pedido,tipo_movimiento,cantidad,motivo) VALUES(?,?, 'ENTRADA',?,'Cancelación de pedido')", producto, idPedido, cantidad);
                            } else if (ingrediente != null) {
                                ejecutar(c, "UPDATE ingrediente SET stock_actual=stock_actual+? WHERE id_ingrediente=?", cantidad, ingrediente);
                                ejecutar(c, "INSERT INTO movimiento_inventario(id_ingrediente,id_pedido,tipo_movimiento,cantidad,motivo) VALUES(?,?, 'ENTRADA',?,'Cancelación de pedido')", ingrediente, idPedido, cantidad);
                            }
                        }
                    }
                }
                ejecutar(c, "UPDATE pedido SET estado='CANCELADO',estado_preparacion='CANCELADO' WHERE id_pedido=?", idPedido);
                c.commit();
            } catch (SQLException | RuntimeException ex) {
                try { c.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }

    private static void asegurarColumnas(Connection c) throws SQLException {
        agregarColumnaSiFalta(c, "tiempo_preparacion_min", "INT NOT NULL DEFAULT 5");
        agregarColumnaSiFalta(c, "estado_preparacion", "VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'");
    }

    private static void agregarColumnaSiFalta(Connection c, String columna, String definicion)
            throws SQLException {
        boolean existe;
        try (PreparedStatement s = c.prepareStatement(
                "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='pedido' AND COLUMN_NAME=?")) {
            s.setString(1, columna);
            try (ResultSet r = s.executeQuery()) {
                r.next();
                existe = r.getInt(1) > 0;
            }
        }
        if (!existe) {
            try (Statement s = c.createStatement()) {
                s.executeUpdate("ALTER TABLE pedido ADD COLUMN " + columna + " " + definicion);
            }
        }
    }

    private static void ejecutar(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) s.setObject(i + 1, args[i]);
            s.executeUpdate();
        }
    }
}
