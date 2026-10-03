package DAO;

import Conexion.Conexion;
import Modelos.*;
import Utilidades.HorarioMenu;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.*;

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
                Consumo consumo = new Consumo();
                for (LineaPedido linea : pago.lineas()) validarLinea(c, linea, consumo);
                bloquearInventario(c, consumo);
                int id;
                try (PreparedStatement s = c.prepareStatement("INSERT INTO pedido(numero_orden,id_usuario,tipo_servicio,estado,metodo_pago,monto_recibido,total) VALUES(0,?,?,'PAGADO',?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    s.setInt(1, pago.usuario()); s.setString(2, pago.servicio()); s.setString(3, pago.metodo());
                    s.setBigDecimal(4, pago.recibido()); s.setBigDecimal(5, pago.total()); s.executeUpdate();
                    try (ResultSet r = s.getGeneratedKeys()) { if (!r.next()) throw new SQLException("No se obtuvo el número de pedido."); id = r.getInt(1); }
                }
                ejecutar(c, "UPDATE pedido SET numero_orden=? WHERE id_pedido=?", id, id);
                for (LineaPedido linea : pago.lineas()) guardarLinea(c, id, linea);
                descontarInventario(c, id, consumo);
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

    private void validarLinea(Connection c, LineaPedido linea, Consumo consumo)
            throws SQLException {
        BigDecimal precioCatalogo;
        try (PreparedStatement s = c.prepareStatement("SELECT pm.id_producto_principal,pm.nombre,pm.precio,pm.estado AS presentacion_activa,p.nombre,p.estado AS producto_activo,p.disponibilidad_menu "
                + "FROM presentacion_menu pm JOIN producto p ON p.id_producto=pm.id_producto_principal "
                + "WHERE pm.id_presentacion=? FOR UPDATE")) {
            s.setInt(1, linea.idPresentacion());
            try (ResultSet r = s.executeQuery()) {
                if (!r.next() || r.getInt("id_producto_principal") != linea.idProducto()
                        || !r.getBoolean("presentacion_activa") || !r.getBoolean("producto_activo")
                        || !HorarioMenu.estaDisponible(r.getString("disponibilidad_menu"), LocalTime.now()))
                    throw new IllegalArgumentException(linea.nombre() + ": la presentación ya no está disponible.");
                precioCatalogo = r.getBigDecimal("precio");
                if (precioCatalogo.compareTo(linea.precioBase()) != 0)
                    throw new IllegalArgumentException(linea.nombre() + ": cambió el precio. Configúralo nuevamente.");
            }
        }

        Map<Integer, ReglaGrupo> reglas = new LinkedHashMap<>();
        try (PreparedStatement s = c.prepareStatement("SELECT id_grupo,minimo,maximo,permite_repetir FROM grupo_presentacion WHERE id_presentacion=? AND estado=TRUE")) {
            s.setInt(1, linea.idPresentacion());
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) reglas.put(r.getInt(1), new ReglaGrupo(r.getInt(2),r.getInt(3),r.getBoolean(4)));
            }
        }
        Map<Integer,Integer> cantidadesGrupo = new HashMap<>();
        Map<Integer,Set<Integer>> opcionesGrupo = new HashMap<>();
        for (OpcionPedido opcion : linea.opciones()) {
            ReglaGrupo regla = reglas.get(opcion.idGrupo());
            if (regla == null) throw new IllegalArgumentException("La configuración de " + linea.nombre() + " cambió.");
            BigDecimal incremento;
            try (PreparedStatement s = c.prepareStatement("SELECT incremento_precio,estado FROM opcion_grupo WHERE id_opcion=? AND id_grupo=?")) {
                s.setInt(1, opcion.idOpcion()); s.setInt(2, opcion.idGrupo());
                try (ResultSet r = s.executeQuery()) {
                    if (!r.next() || !r.getBoolean("estado"))
                        throw new IllegalArgumentException(opcion.opcion() + ": ya no está disponible.");
                    incremento = r.getBigDecimal("incremento_precio");
                }
            }
            if (incremento.compareTo(opcion.precioExtra()) != 0)
                throw new IllegalArgumentException(opcion.opcion() + ": cambió el precio.");
            precioCatalogo = precioCatalogo.add(incremento);
            cantidadesGrupo.merge(opcion.idGrupo(), 1, Integer::sum);
            opcionesGrupo.computeIfAbsent(opcion.idGrupo(), k -> new HashSet<>()).add(opcion.idOpcion());
            validarProductosOpcion(c, linea, opcion, consumo);
            for (ProductoPedido producto : opcion.productos())
                for (ModificacionPedido cambio : producto.modificaciones())
                    precioCatalogo = precioCatalogo.add(cambio.precioExtra());
        }
        for (Map.Entry<Integer,ReglaGrupo> entrada : reglas.entrySet()) {
            int cantidad = cantidadesGrupo.getOrDefault(entrada.getKey(), 0);
            ReglaGrupo regla = entrada.getValue();
            if (cantidad < regla.minimo || cantidad > regla.maximo)
                throw new IllegalArgumentException(linea.nombre() + ": faltan elecciones obligatorias.");
            if (!regla.repetir && opcionesGrupo.getOrDefault(entrada.getKey(), Set.of()).size() != cantidad)
                throw new IllegalArgumentException(linea.nombre() + ": una elección no permite repetirse.");
        }
        if (precioCatalogo.setScale(2).compareTo(linea.precio()) != 0)
            throw new IllegalArgumentException(linea.nombre() + ": el total cambió. Configúralo nuevamente.");
    }

    private void validarProductosOpcion(Connection c, LineaPedido linea,
            OpcionPedido opcion, Consumo consumo) throws SQLException {
        Map<Integer,Integer> esperados = new TreeMap<>();
        try (PreparedStatement s = c.prepareStatement("SELECT id_producto,SUM(cantidad) cantidad FROM opcion_componente WHERE id_opcion=? GROUP BY id_producto")) {
            s.setInt(1, opcion.idOpcion());
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) esperados.put(r.getInt(1), r.getInt(2));
            }
        }
        Map<Integer,Integer> recibidos = new TreeMap<>();
        for (ProductoPedido producto : opcion.productos())
            recibidos.merge(producto.idProducto(), producto.cantidad(), Integer::sum);
        if (!esperados.equals(recibidos))
            throw new IllegalArgumentException(opcion.opcion() + ": sus productos internos cambiaron.");

        for (ProductoPedido producto : opcion.productos()) {
            String tipoStock;
            String nombre;
            try (PreparedStatement s = c.prepareStatement("SELECT nombre,tipo_stock,estado,disponibilidad_menu FROM producto WHERE id_producto=?")) {
                s.setInt(1, producto.idProducto());
                try (ResultSet r = s.executeQuery()) {
                    if (!r.next() || !r.getBoolean("estado")
                            || !HorarioMenu.estaDisponible(r.getString("disponibilidad_menu"), LocalTime.now()))
                        throw new IllegalArgumentException(producto.nombre() + ": ya no está disponible.");
                    nombre = r.getString("nombre"); tipoStock = r.getString("tipo_stock");
                }
            }
            BigDecimal multiplicador = BigDecimal.valueOf((long) linea.cantidad() * producto.cantidad());
            if ("DIRECTO".equals(tipoStock)) {
                consumo.productos.merge(producto.idProducto(), multiplicador, BigDecimal::add);
                if (!producto.modificaciones().isEmpty())
                    throw new IllegalArgumentException(nombre + " no admite cambios por ingredientes.");
            } else if ("RECETA".equals(tipoStock)) {
                validarReceta(c, producto, multiplicador, consumo);
            } else if (!producto.modificaciones().isEmpty()) {
                throw new IllegalArgumentException(nombre + " no admite cambios por ingredientes.");
            }
        }
    }

    private void validarReceta(Connection c, ProductoPedido producto,
            BigDecimal multiplicador, Consumo consumo) throws SQLException {
        Map<Integer, ReglaIngrediente> receta = new LinkedHashMap<>();
        try (PreparedStatement s = c.prepareStatement("SELECT pi.id_producto_ingrediente,pi.id_ingrediente,i.nombre,pi.cantidad_default,pi.permite_quitar,pi.permite_extra,pi.cantidad_extra,pi.precio_extra,pi.max_extras "
                + "FROM producto_ingrediente pi JOIN ingrediente i ON i.id_ingrediente=pi.id_ingrediente "
                + "WHERE pi.id_producto=? AND pi.estado=TRUE AND i.estado=TRUE")) {
            s.setInt(1, producto.idProducto());
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) receta.put(r.getInt("id_producto_ingrediente"), new ReglaIngrediente(
                        r.getInt("id_ingrediente"),r.getString("nombre"),r.getBigDecimal("cantidad_default"),
                        r.getBoolean("permite_quitar"),r.getBoolean("permite_extra"),r.getBigDecimal("cantidad_extra"),
                        r.getBigDecimal("precio_extra"),r.getInt("max_extras")));
            }
        }
        Set<Integer> quitados = new HashSet<>();
        Set<Integer> conExtra = new HashSet<>();
        Set<String> cambiosUnicos = new HashSet<>();
        for (ModificacionPedido cambio : producto.modificaciones()) {
            ReglaIngrediente regla = receta.get(cambio.idProductoIngrediente());
            if (regla == null || regla.idIngrediente != cambio.idIngrediente()
                    || !cambiosUnicos.add(cambio.tipo() + ":" + cambio.idProductoIngrediente()))
                throw new IllegalArgumentException(producto.nombre() + ": una modificación ya no es válida.");
            if ("SIN".equals(cambio.tipo())) {
                if (conExtra.contains(cambio.idProductoIngrediente()))
                    throw new IllegalArgumentException("No puedes quitar y agregar extra del mismo ingrediente.");
                if (!regla.quitar || cambio.cantidad().compareTo(regla.cantidadDefault) != 0
                        || cambio.precioExtra().signum() != 0)
                    throw new IllegalArgumentException("Ya no se permite quitar " + regla.nombre + ".");
                quitados.add(cambio.idProductoIngrediente());
            } else {
                if (quitados.contains(cambio.idProductoIngrediente()))
                    throw new IllegalArgumentException("No puedes quitar y agregar extra del mismo ingrediente.");
                BigDecimal cantidad = regla.cantidadExtra.multiply(BigDecimal.valueOf(cambio.veces()));
                BigDecimal precio = regla.precioExtra.multiply(BigDecimal.valueOf(cambio.veces()));
                if (!regla.extra || cambio.veces() > regla.maxExtras || cambio.veces() < 1
                        || cambio.cantidad().compareTo(cantidad) != 0 || cambio.precioExtra().compareTo(precio) != 0)
                    throw new IllegalArgumentException("Cambió la regla del extra " + regla.nombre + ".");
                consumo.ingredientes.merge(regla.idIngrediente,
                        cambio.cantidad().multiply(multiplicador), BigDecimal::add);
                conExtra.add(cambio.idProductoIngrediente());
            }
        }
        for (Map.Entry<Integer,ReglaIngrediente> entrada : receta.entrySet()) {
            if (!quitados.contains(entrada.getKey())) consumo.ingredientes.merge(
                    entrada.getValue().idIngrediente,
                    entrada.getValue().cantidadDefault.multiply(multiplicador), BigDecimal::add);
        }
    }

    private void bloquearInventario(Connection c, Consumo consumo) throws SQLException {
        for (Map.Entry<Integer,BigDecimal> entrada : consumo.productos.entrySet()) {
            try (PreparedStatement s = c.prepareStatement("SELECT nombre,stock_actual,estado FROM producto WHERE id_producto=? FOR UPDATE")) {
                s.setInt(1, entrada.getKey());
                try (ResultSet r = s.executeQuery()) {
                    if (!r.next() || !r.getBoolean("estado")
                            || BigDecimal.valueOf(r.getInt("stock_actual")).compareTo(entrada.getValue()) < 0)
                        throw new IllegalArgumentException("No hay existencias suficientes de un producto del pedido.");
                }
            }
        }
        for (Map.Entry<Integer,BigDecimal> entrada : consumo.ingredientes.entrySet()) {
            try (PreparedStatement s = c.prepareStatement("SELECT nombre,stock_actual,estado FROM ingrediente WHERE id_ingrediente=? FOR UPDATE")) {
                s.setInt(1, entrada.getKey());
                try (ResultSet r = s.executeQuery()) {
                    if (!r.next() || !r.getBoolean("estado")
                            || r.getBigDecimal("stock_actual").compareTo(entrada.getValue()) < 0)
                        throw new IllegalArgumentException("No hay ingredientes suficientes para preparar el pedido.");
                }
            }
        }
    }

    private void guardarLinea(Connection c, int idPedido, LineaPedido linea) throws SQLException {
        int idDetalle = insertarId(c, "INSERT INTO pedido_detalle(id_pedido,id_presentacion,nombre,presentacion,cantidad,precio_unitario,subtotal) VALUES(?,?,?,?,?,?,?)",
                idPedido,linea.idPresentacion(),linea.nombre(),linea.presentacion(),linea.cantidad(),linea.precio(),linea.subtotal());
        for (OpcionPedido opcion : linea.opciones()) {
            int idPedidoOpcion = insertarId(c, "INSERT INTO pedido_opcion(id_detalle,id_grupo,id_opcion,numero,grupo,opcion,precio_extra) VALUES(?,?,?,?,?,?,?)",
                    idDetalle,opcion.idGrupo(),opcion.idOpcion(),opcion.numero(),opcion.grupo(),opcion.opcion(),opcion.precioExtra());
            for (ProductoPedido producto : opcion.productos()) {
                int idPedidoProducto = insertarId(c, "INSERT INTO pedido_producto(id_pedido_opcion,id_producto,nombre,cantidad) VALUES(?,?,?,?)",
                        idPedidoOpcion,producto.idProducto(),producto.nombre(),producto.cantidad());
                for (ModificacionPedido cambio : producto.modificaciones()) ejecutar(c,
                        "INSERT INTO pedido_modificacion(id_pedido_producto,id_producto_ingrediente,tipo,ingrediente,cantidad,precio_extra) VALUES(?,?,?,?,?,?)",
                        idPedidoProducto,cambio.idProductoIngrediente(),cambio.tipo(),cambio.ingrediente(),cambio.cantidad(),cambio.precioExtra());
            }
        }
    }

    private void descontarInventario(Connection c, int idPedido, Consumo consumo) throws SQLException {
        for (Map.Entry<Integer,BigDecimal> entrada : consumo.productos.entrySet()) {
            ejecutar(c,"UPDATE producto SET stock_actual=stock_actual-? WHERE id_producto=?",entrada.getValue().intValueExact(),entrada.getKey());
            ejecutar(c,"INSERT INTO movimiento_inventario(id_producto,id_pedido,tipo_movimiento,cantidad,motivo) VALUES(?,?,'SALIDA',?,'Venta en caja')",entrada.getKey(),idPedido,entrada.getValue());
        }
        for (Map.Entry<Integer,BigDecimal> entrada : consumo.ingredientes.entrySet()) {
            ejecutar(c,"UPDATE ingrediente SET stock_actual=stock_actual-? WHERE id_ingrediente=?",entrada.getValue(),entrada.getKey());
            ejecutar(c,"INSERT INTO movimiento_inventario(id_ingrediente,id_pedido,tipo_movimiento,cantidad,motivo) VALUES(?,?,'SALIDA',?,'Preparación de pedido')",entrada.getKey(),idPedido,entrada.getValue());
        }
    }

    private static int insertarId(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i=0;i<args.length;i++) s.setObject(i+1,args[i]);
            s.executeUpdate();
            try (ResultSet r=s.getGeneratedKeys()) {
                if (!r.next()) throw new SQLException("No se obtuvo el registro creado.");
                return r.getInt(1);
            }
        }
    }

    private static final class Consumo {
        final Map<Integer,BigDecimal> productos = new TreeMap<>();
        final Map<Integer,BigDecimal> ingredientes = new TreeMap<>();
    }
    private record ReglaGrupo(int minimo,int maximo,boolean repetir) {}
    private record ReglaIngrediente(int idIngrediente,String nombre,
            BigDecimal cantidadDefault,boolean quitar,boolean extra,
            BigDecimal cantidadExtra,BigDecimal precioExtra,int maxExtras) {}

    private static void ejecutar(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) s.setObject(i + 1, args[i]);
            s.executeUpdate();
        }
    }

    private static String comprobante(int id, SolicitudPago p) {
        StringBuilder b = new StringBuilder("WALDONALD'S\nComprobante de venta\nPedido #" + id + "\n" + LocalDateTime.now().withNano(0)
                + "\nCajero: " + p.usuario() + "\n" + (p.servicio().equals("COMER_AQUI") ? "Comer aquí" : "Para llevar") + "\n\n");
        for (LineaPedido l : p.lineas()) {
            b.append(l.cantidad()).append(" x ").append(l.nombre()).append(" (")
                    .append(l.presentacion()).append(")  Q").append(l.subtotal().toPlainString()).append('\n');
            for (OpcionPedido o : l.opciones()) {
                if (!o.grupo().startsWith("__")) b.append("  - ").append(o.grupo()).append(": ").append(o.opcion()).append('\n');
                for (ProductoPedido pr : o.productos()) {
                    b.append("      ").append(pr.nombre()).append('\n');
                    for (ModificacionPedido m : pr.modificaciones())
                        b.append("        ").append(m.tipo().equals("SIN") ? "SIN " : "EXTRA ")
                                .append(m.ingrediente()).append(m.veces()>1 ? " x"+m.veces() : "").append('\n');
                }
            }
        }
        b.append("\nTotal: Q").append(p.total().toPlainString()).append("\nMétodo: ").append(p.metodo())
                .append("\nRecibido: Q").append(p.recibido().toPlainString()).append("\nCambio: Q").append(p.cambio().toPlainString());
        if (!p.referencia().isBlank()) b.append("\nReferencia: ").append(p.referencia());
        return b.append("\n\nGracias por su compra.").toString();
    }
}
