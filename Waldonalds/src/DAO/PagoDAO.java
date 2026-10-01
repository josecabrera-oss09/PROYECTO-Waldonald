package DAO;

import Conexion.Conexion;
import Modelos.LineaPedido;
import Modelos.SolicitudPago;
import Utilidades.HorarioMenu;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;

/** Cada cobro confirma pedido, detalle e inventario en una sola transacción. */
public class PagoDAO {
    @FunctionalInterface public interface Conexiones { Connection abrir() throws SQLException; }
    private final Conexiones conexiones;
    public PagoDAO() { this(Conexion::conectar); }
    public PagoDAO(Conexiones conexiones) { this.conexiones = conexiones; }

    public String cobrar(SolicitudPago pago) throws SQLException {
        try (Connection c = conexiones.abrir()) {
            if (c == null) throw new SQLException("No se pudo conectar con MySQL. Reintente cuando esté disponible.");
            c.setAutoCommit(false);
            boolean confirmando = false;
            try {
                // La clave se conserva al reintentar. El INSERT bloquea los intentos simultáneos.
                ejecutar(c, "INSERT INTO pago_operacion(clave,solicitud,referencia) VALUES(?,?,?) "
                        + "ON DUPLICATE KEY UPDATE clave=clave", pago.clave(), pago.toString(), pago.referencia());
                try (PreparedStatement s = c.prepareStatement("SELECT solicitud,comprobante FROM pago_operacion WHERE clave=? FOR UPDATE")) {
                    s.setString(1, pago.clave());
                    try (ResultSet r = s.executeQuery()) {
                        r.next();
                        if (!pago.toString().equals(r.getString(1))) throw new IllegalArgumentException("La operación pertenece a otro pedido.");
                        String previo = r.getString(2);
                        if (previo != null) {
                            c.rollback();
                            // Los comprobantes antiguos conservan sus valores; se actualiza solo el símbolo al mostrarlos.
                            return previo.replace("  $", "  Q")
                                    .replace("\nTotal: $", "\nTotal: Q")
                                    .replace("\nRecibido: $", "\nRecibido: Q")
                                    .replace("\nCambio: $", "\nCambio: Q");
                        }
                    }
                }
                try (PreparedStatement s = c.prepareStatement("SELECT estado,rol FROM usuario WHERE id_usuario=? FOR UPDATE")) {
                    s.setInt(1, pago.usuario());
                    try (ResultSet r = s.executeQuery()) {
                        if (!r.next() || !r.getBoolean(1) || !("CAJERO".equals(r.getString(2)) || "ADMINISTRADOR".equals(r.getString(2))))
                            throw new IllegalArgumentException("La sesión no está autorizada para cobrar.");
                    }
                }
                // Orden estable de bloqueos para evitar sobreventa entre cajas.
                for (LineaPedido linea : pago.lineas().stream().sorted(Comparator.comparingInt(LineaPedido::idProducto)).toList()) {
                    try (PreparedStatement s = c.prepareStatement(
                            "SELECT nombre,precio_base,estado,disponibilidad_menu "
                            + "FROM producto WHERE id_producto=? FOR UPDATE")) {
                        s.setInt(1, linea.idProducto());
                        try (ResultSet r = s.executeQuery()) {
                            if (!r.next() || !r.getBoolean("estado") || !HorarioMenu.estaDisponible(r.getString("disponibilidad_menu"), LocalTime.now()))
                                throw new IllegalArgumentException(linea.nombre() + ": ya no está disponible.");
                            if (r.getBigDecimal("precio_base").compareTo(linea.precio()) != 0)
                                throw new IllegalArgumentException(linea.nombre() + ": cambió el precio. Quite el producto y agréguelo nuevamente.");
                        }
                    }
                    try (PreparedStatement s = c.prepareStatement(
                            "SELECT stock_disponible FROM vista_stock_disponible WHERE id_producto=?")) {
                        s.setInt(1, linea.idProducto());
                        try (ResultSet r = s.executeQuery()) {
                            if (!r.next() || r.getInt(1) < linea.cantidad())
                                throw new IllegalArgumentException(linea.nombre() + ": existencias insuficientes.");
                        }
                    }
                }
                int id;
                try (PreparedStatement s = c.prepareStatement("INSERT INTO pedido(numero_orden,id_usuario,tipo_servicio,estado,metodo_pago,monto_recibido,total) VALUES(0,?,?,'PAGADO',?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    s.setInt(1, pago.usuario()); s.setString(2, pago.servicio()); s.setString(3, pago.metodo());
                    s.setBigDecimal(4, pago.recibido()); s.setBigDecimal(5, pago.total()); s.executeUpdate();
                    try (ResultSet r = s.getGeneratedKeys()) { if (!r.next()) throw new SQLException("No se obtuvo el número de pedido."); id = r.getInt(1); }
                }
                ejecutar(c, "UPDATE pedido SET numero_orden=? WHERE id_pedido=?", id, id);
                for (LineaPedido linea : pago.lineas()) {
                    int detalle = insertarDetalle(c, id, linea);
                    descontarInventario(c, linea.idProducto(), linea.cantidad(), id, detalle);
                }
                String ticket = comprobante(id, pago);
                ejecutar(c, "UPDATE pago_operacion SET id_pedido=?,comprobante=? WHERE clave=?", id, ticket, pago.clave());
                confirmando = true;
                c.commit();
                return ticket;
            } catch (SQLException | RuntimeException error) {
                try { c.rollback(); } catch (SQLException rollback) { error.addSuppressed(rollback); }
                if (confirmando) throw new SQLException("No se pudo verificar la confirmación. Reintente este mismo cobro; no vuelva a cobrar en la terminal.", error);
                throw error;
            }
        }
    }

    private static void ejecutar(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) s.setObject(i + 1, args[i]);
            s.executeUpdate();
        }
    }

    private static int insertarDetalle(Connection c, int idPedido, LineaPedido linea) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(
                "INSERT INTO pedido_detalle(id_pedido,id_producto,cantidad,precio_unitario) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, idPedido);
            s.setInt(2, linea.idProducto());
            s.setInt(3, linea.cantidad());
            s.setBigDecimal(4, linea.precio());
            s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                if (!r.next()) throw new SQLException("No se obtuvo el detalle del pedido.");
                return r.getInt(1);
            }
        }
    }

    /** Descuenta el producto directo o todos los componentes de una receta/combos. */
    private static void descontarInventario(Connection c, int idProducto, int cantidad,
            int idPedido, int idDetalle) throws SQLException {
        String tipo;
        try (PreparedStatement s = c.prepareStatement(
                "SELECT tipo_control_stock FROM producto WHERE id_producto=? FOR UPDATE")) {
            s.setInt(1, idProducto);
            try (ResultSet r = s.executeQuery()) {
                if (!r.next()) throw new SQLException("Producto inexistente: " + idProducto);
                tipo = r.getString(1);
            }
        }

        if ("DIRECTO".equals(tipo)) {
            ejecutar(c, "UPDATE producto SET stock_actual=stock_actual-? "
                    + "WHERE id_producto=? AND stock_actual>=?", cantidad, idProducto, cantidad);
            registrarSalidaProducto(c, idProducto, cantidad, idPedido, idDetalle, "Venta en caja");
            return;
        }

        if ("RECETA".equals(tipo)) {
            try (PreparedStatement s = c.prepareStatement(
                    "SELECT id_ingrediente,cantidad_requerida FROM receta_producto "
                    + "WHERE id_producto=? ORDER BY id_ingrediente FOR UPDATE")) {
                s.setInt(1, idProducto);
                try (ResultSet r = s.executeQuery()) {
                    boolean tieneIngredientes = false;
                    while (r.next()) {
                        tieneIngredientes = true;
                        int ingrediente = r.getInt("id_ingrediente");
                        java.math.BigDecimal consumo = r.getBigDecimal("cantidad_requerida")
                                .multiply(java.math.BigDecimal.valueOf(cantidad));
                        int actual;
                        try (PreparedStatement lock = c.prepareStatement(
                                "SELECT stock_actual FROM ingrediente WHERE id_ingrediente=? FOR UPDATE")) {
                            lock.setInt(1, ingrediente);
                            try (ResultSet stock = lock.executeQuery()) {
                                if (!stock.next()) throw new SQLException("Ingrediente inexistente: " + ingrediente);
                                actual = stock.getBigDecimal(1).compareTo(consumo) >= 0 ? 1 : 0;
                            }
                        }
                        if (actual == 0) throw new IllegalArgumentException("No hay ingredientes suficientes para el producto.");
                        ejecutar(c, "UPDATE ingrediente SET stock_actual=stock_actual-? WHERE id_ingrediente=?",
                                consumo, ingrediente);
                        ejecutar(c, "INSERT INTO movimiento_inventario(id_ingrediente,id_pedido,id_detalle,tipo_movimiento,cantidad,motivo) "
                                + "VALUES(?,?,?,'SALIDA',?,'Venta en caja - receta')",
                                ingrediente, idPedido, idDetalle, consumo);
                    }
                    if (!tieneIngredientes) throw new IllegalArgumentException("El producto no tiene receta configurada.");
                }
            }
            return;
        }

        if ("COMBO".equals(tipo)) {
            try (PreparedStatement s = c.prepareStatement(
                    "SELECT id_producto_opcion,cantidad_incluida FROM combo_opcion "
                    + "WHERE id_combo=? AND estado=TRUE ORDER BY id_producto_opcion FOR UPDATE")) {
                s.setInt(1, idProducto);
                try (ResultSet r = s.executeQuery()) {
                    boolean tieneOpciones = false;
                    while (r.next()) {
                        tieneOpciones = true;
                        descontarInventario(c, r.getInt("id_producto_opcion"),
                                Math.multiplyExact(cantidad, r.getInt("cantidad_incluida")), idPedido, idDetalle);
                    }
                    if (!tieneOpciones) throw new IllegalArgumentException("El combo no tiene productos configurados.");
                }
            }
            return;
        }

        throw new IllegalArgumentException("Tipo de inventario no configurado para el producto.");
    }

    private static void registrarSalidaProducto(Connection c, int idProducto, int cantidad,
            int idPedido, int idDetalle, String motivo) throws SQLException {
        ejecutar(c, "INSERT INTO movimiento_inventario(id_producto,id_pedido,id_detalle,tipo_movimiento,cantidad,motivo) "
                + "VALUES(?,?,?,'SALIDA',?,?)", idProducto, idPedido, idDetalle, cantidad, motivo);
    }

    private static String comprobante(int id, SolicitudPago p) {
        StringBuilder b = new StringBuilder("WALDONALD'S\nComprobante de venta\nPedido #" + id + "\n" + LocalDateTime.now().withNano(0)
                + "\nCajero: " + p.usuario() + "\n" + (p.servicio().equals("COMER_AQUI") ? "Comer aquí" : "Para llevar") + "\n\n");
        for (LineaPedido l : p.lineas()) b.append(l.cantidad()).append(" x ").append(l.nombre()).append("  Q").append(l.subtotal().toPlainString()).append('\n');
        b.append("\nTotal: Q").append(p.total().toPlainString()).append("\nMétodo: ").append(p.metodo())
                .append("\nRecibido: Q").append(p.recibido().toPlainString()).append("\nCambio: Q").append(p.cambio().toPlainString());
        if (!p.referencia().isBlank()) b.append("\nReferencia: ").append(p.referencia());
        return b.append("\n\nGracias por su compra.").toString();
    }
}
