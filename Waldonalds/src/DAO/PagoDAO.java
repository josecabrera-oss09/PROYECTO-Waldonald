package DAO;

import Conexion.Conexion;
import Modelos.LineaPedido;
import Modelos.SolicitudPago;
import Utilidades.HorarioMenu;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                            BigDecimal precioEsperado = r.getBigDecimal("precio_base")
                                    .add(calcularPrecioExtras(c, linea));
                            if (precioEsperado.compareTo(linea.precio()) != 0)
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
                    descontarInventario(c, linea, id, detalle);
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
    private static void descontarInventario(Connection c, LineaPedido linea,
            int idPedido, int idDetalle) throws SQLException {
        int idProducto = linea.idProducto();
        int cantidad = linea.cantidad();
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
                        String nombreIngrediente;
                        try (PreparedStatement nombre = c.prepareStatement(
                                "SELECT nombre FROM ingrediente WHERE id_ingrediente=?")) {
                            nombre.setInt(1, ingrediente);
                            try (ResultSet dato = nombre.executeQuery()) {
                                if (!dato.next()) throw new SQLException("Ingrediente inexistente: " + ingrediente);
                                nombreIngrediente = dato.getString(1);
                            }
                        }
                        boolean quitar = contiene(linea.quitar(), nombreIngrediente);
                        boolean extra = !quitar && contiene(linea.agregar(), nombreIngrediente);
                        java.math.BigDecimal porUnidad = quitar
                                ? java.math.BigDecimal.ZERO
                                : r.getBigDecimal("cantidad_requerida").multiply(
                                        java.math.BigDecimal.valueOf(extra ? 2 : 1));
                        java.math.BigDecimal consumo = porUnidad.multiply(
                                java.math.BigDecimal.valueOf(cantidad));
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
                        if (consumo.signum() > 0) {
                            ejecutar(c, "UPDATE ingrediente SET stock_actual=stock_actual-? WHERE id_ingrediente=?",
                                    consumo, ingrediente);
                            ejecutar(c, "INSERT INTO movimiento_inventario(id_ingrediente,id_pedido,id_detalle,tipo_movimiento,cantidad,motivo) "
                                    + "VALUES(?,?,?,'SALIDA',?,?)",
                                    ingrediente, idPedido, idDetalle, consumo,
                                    extra ? "Venta en caja - ingrediente extra" : "Venta en caja - receta");
                        }
                        registrarModificacion(c, linea, idProducto, ingrediente, nombreIngrediente,
                                cantidad, idDetalle, quitar, extra);
                    }
                    if (!tieneIngredientes) throw new IllegalArgumentException("El producto no tiene receta configurada.");
                }
            }
            return;
        }

        if ("COMBO".equals(tipo)) {
            try (PreparedStatement s = c.prepareStatement(
                    "SELECT co.id_producto_opcion,p.nombre,co.grupo,co.cantidad_incluida,co.es_predeterminado "
                    + "FROM combo_opcion co JOIN producto p ON p.id_producto=co.id_producto_opcion "
                    + "WHERE co.id_combo=? AND co.estado=TRUE ORDER BY co.id_producto_opcion FOR UPDATE")) {
                s.setInt(1, idProducto);
                try (ResultSet r = s.executeQuery()) {
                    boolean tieneOpciones = false;
                    List<OpcionComboVenta> opciones = new ArrayList<>();
                    while (r.next()) {
                        tieneOpciones = true;
                        opciones.add(new OpcionComboVenta(
                                r.getInt("id_producto_opcion"), r.getString("nombre"),
                                r.getString("grupo"), r.getInt("cantidad_incluida"),
                                r.getBoolean("es_predeterminado")));
                    }
                    if (!tieneOpciones) throw new IllegalArgumentException("El combo no tiene productos configurados.");

                    for (OpcionComboVenta opcion : opcionesElegidas(opciones, linea.agregar())) {
                        LineaPedido componente = new LineaPedido(
                                opcion.idProducto(), opcion.nombre(), BigDecimal.ZERO,
                                Math.multiplyExact(cantidad, opcion.cantidadIncluida()));
                        descontarInventario(c, componente, idPedido, idDetalle);
                    }
                }
            }
            return;
        }

        throw new IllegalArgumentException("Tipo de inventario no configurado para el producto.");
    }

    private static void registrarModificacion(Connection c, LineaPedido linea, int idProducto,
            int idIngrediente, String nombreIngrediente, int cantidad, int idDetalle,
            boolean quitar, boolean extra) throws SQLException {
        if (!quitar && !extra) return;
        String tipo = quitar ? "QUITAR" : "EXTRA";
        BigDecimal cantidadModificada = BigDecimal.ONE;
        BigDecimal precio = BigDecimal.ZERO;
        if (extra) {
            try (PreparedStatement s = c.prepareStatement(
                    "SELECT precio_extra FROM receta_producto WHERE id_producto=? AND id_ingrediente=?")) {
                s.setInt(1, idProducto);
                s.setInt(2, idIngrediente);
                try (ResultSet r = s.executeQuery()) {
                    if (r.next()) precio = r.getBigDecimal(1);
                }
            }
        }
        ejecutar(c, "INSERT INTO detalle_ingrediente_mod "
                + "(id_detalle,id_ingrediente,tipo_modificacion,cantidad,precio_adicional) "
                + "VALUES(?,?,?,?,?)", idDetalle, idIngrediente, tipo, cantidadModificada, precio);
    }

    private static boolean contiene(java.util.Set<String> valores, String objetivo) {
        String esperado = normalizar(objetivo);
        return valores.stream().anyMatch(valor -> normalizar(valor).equals(esperado));
    }

    private static String normalizar(String valor) {
        return java.text.Normalizer.normalize(valor == null ? "" : valor,
                java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT)
                .trim();
    }

    private static void registrarSalidaProducto(Connection c, int idProducto, int cantidad,
            int idPedido, int idDetalle, String motivo) throws SQLException {
        ejecutar(c, "INSERT INTO movimiento_inventario(id_producto,id_pedido,id_detalle,tipo_movimiento,cantidad,motivo) "
                + "VALUES(?,?,?,'SALIDA',?,?)", idProducto, idPedido, idDetalle, cantidad, motivo);
    }

    private static BigDecimal calcularPrecioExtras(Connection c, LineaPedido linea) throws SQLException {
        if (linea.agregar().isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT COALESCE(SUM(rp.precio_extra),0) "
                + "FROM receta_producto rp JOIN ingrediente i "
                + "ON i.id_ingrediente=rp.id_ingrediente "
                + "WHERE rp.id_producto=? AND i.nombre=?";
        try (PreparedStatement s = c.prepareStatement(sql)) {
            for (String extra : linea.agregar()) {
                s.setInt(1, linea.idProducto());
                s.setString(2, extra);
                try (ResultSet r = s.executeQuery()) {
                    if (r.next()) total = total.add(r.getBigDecimal(1));
                }
            }
        }
        // En un combo, las selecciones de la pantalla son productos opcionables,
        // no ingredientes. Su recargo también forma parte del precio esperado.
        String comboSql = "SELECT COALESCE(SUM(co.precio_adicional),0) "
                + "FROM combo_opcion co JOIN producto p ON p.id_producto=co.id_producto_opcion "
                + "WHERE co.id_combo=? AND co.estado=TRUE AND p.nombre=?";
        try (PreparedStatement s = c.prepareStatement(comboSql)) {
            for (String opcion : linea.agregar()) {
                s.setInt(1, linea.idProducto());
                s.setString(2, opcion);
                try (ResultSet r = s.executeQuery()) {
                    if (r.next()) total = total.add(r.getBigDecimal(1));
                }
            }
        }
        return total;
    }

    private record OpcionComboVenta(int idProducto, String nombre, String grupo,
            int cantidadIncluida, boolean predeterminado) {}

    /**
     * Devuelve una opción por grupo: las seleccionadas por el cliente; si no
     * hay selección para ese grupo, conserva la opción predeterminada.
     */
    private static List<OpcionComboVenta> opcionesElegidas(List<OpcionComboVenta> opciones,
            Set<String> seleccionadas) {
        Map<String, List<OpcionComboVenta>> porGrupo = new HashMap<>();
        for (OpcionComboVenta opcion : opciones) {
            porGrupo.computeIfAbsent(opcion.grupo(), clave -> new ArrayList<>()).add(opcion);
        }
        Set<String> gruposConSeleccion = new HashSet<>();
        for (OpcionComboVenta opcion : opciones) {
            if (contiene(seleccionadas, opcion.nombre())) gruposConSeleccion.add(opcion.grupo());
        }

        List<OpcionComboVenta> resultado = new ArrayList<>();
        for (Map.Entry<String, List<OpcionComboVenta>> entrada : porGrupo.entrySet()) {
            boolean haySeleccion = gruposConSeleccion.contains(entrada.getKey());
            boolean agrego = false;
            for (OpcionComboVenta opcion : entrada.getValue()) {
                if ((haySeleccion && contiene(seleccionadas, opcion.nombre()))
                        || (!haySeleccion && opcion.predeterminado())) {
                    resultado.add(opcion);
                    agrego = true;
                }
            }
            // Si el grupo viejo no tenía predeterminado, no se rompe la venta:
            // se toma la primera opción configurada como compatibilidad.
            if (!haySeleccion && !agrego && !entrada.getValue().isEmpty()) {
                resultado.add(entrada.getValue().get(0));
            }
        }
        return resultado;
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
