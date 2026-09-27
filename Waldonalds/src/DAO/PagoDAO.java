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
                    try (PreparedStatement s = c.prepareStatement("SELECT nombre,precio_base,stock_actual,estado,disponibilidad_menu FROM producto WHERE id_producto=? FOR UPDATE")) {
                        s.setInt(1, linea.idProducto());
                        try (ResultSet r = s.executeQuery()) {
                            if (!r.next() || !r.getBoolean("estado") || !HorarioMenu.estaDisponible(r.getString("disponibilidad_menu"), LocalTime.now()))
                                throw new IllegalArgumentException(linea.nombre() + ": ya no está disponible.");
                            if (r.getInt("stock_actual") < linea.cantidad()) throw new IllegalArgumentException(linea.nombre() + ": existencias insuficientes.");
                            if (r.getBigDecimal("precio_base").compareTo(linea.precio()) != 0)
                                throw new IllegalArgumentException(linea.nombre() + ": cambió el precio. Quite el producto y agréguelo nuevamente.");
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
                    ejecutar(c, "INSERT INTO pedido_detalle(id_pedido,id_producto,cantidad,precio_unitario) VALUES(?,?,?,?)", id, linea.idProducto(), linea.cantidad(), linea.precio());
                    ejecutar(c, "UPDATE producto SET stock_actual=stock_actual-? WHERE id_producto=?", linea.cantidad(), linea.idProducto());
                    ejecutar(c, "INSERT INTO movimiento_inventario(id_producto,id_pedido,tipo_movimiento,cantidad,motivo) VALUES(?,?,'SALIDA',?,'Venta en caja')", linea.idProducto(), id, linea.cantidad());
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
