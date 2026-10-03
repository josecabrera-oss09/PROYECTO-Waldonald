package DAO;

import Conexion.Conexion;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Consultas de solo lectura; las ventas se reconocen al pagar el pedido. */
public final class ReporteDAO {
    public record Resumen(BigDecimal ventas, long pedidos, BigDecimal ticket,
            long cancelados, long stockBajo, List<Object[]> recientes, List<Object[]> productos,
            List<Object[]> alertas, List<Object[]> ventasDetalle,
            BigDecimal efectivo, BigDecimal tarjeta, BigDecimal otros) {

        public Resumen(BigDecimal ventas, long pedidos, BigDecimal ticket,
                long cancelados, long stockBajo, List<Object[]> recientes,
                List<Object[]> productos, List<Object[]> alertas) {
            this(ventas, pedidos, ticket, cancelados, stockBajo, recientes,
                    productos, alertas, List.of(), BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    public Resumen cargar(LocalDate fecha) throws SQLException {
        try (Connection c = Conexion.conectar()) {
            if (c == null) throw new SQLException("No se pudo conectar con la base de datos.");
            return cargar(c, fecha);
        }
    }

    Resumen cargar(Connection c, LocalDate fecha) throws SQLException {
            c.setReadOnly(true);
            c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            c.setAutoCommit(false);
            try {
                List<Object[]> indicadores = consultar(c,
                    "SELECT COALESCE(SUM(CASE WHEN estado='PAGADO' THEN total ELSE 0 END),0), "
                    + "COALESCE(SUM(estado='PAGADO'),0), COALESCE(AVG(CASE WHEN estado='PAGADO' THEN total END),0), "
                    + "COALESCE(SUM(estado='CANCELADO'),0) "
                    + "FROM pedido WHERE fecha_hora>=? AND fecha_hora<?", fecha);
                List<Object[]> stock = consultar(c,
                    "SELECT COUNT(*) FROM producto WHERE estado=1 AND tipo_stock='DIRECTO' AND stock_actual<=stock_minimo", null);
                List<Object[]> pedidos = consultar(c,
                    "SELECT p.numero_orden, CONCAT(u.nombre,' ',u.apellido), DATE_FORMAT(p.fecha_hora,'%H:%i'), "
                    + "CASE p.tipo_servicio WHEN 'COMER_AQUI' THEN 'En salón' WHEN 'PARA_LLEVAR' THEN 'Para llevar' ELSE 'A domicilio' END, "
                    + "p.estado, p.total FROM pedido p JOIN usuario u ON u.id_usuario=p.id_usuario "
                    + "WHERE p.fecha_hora>=? AND p.fecha_hora<? ORDER BY p.fecha_hora DESC,p.id_pedido DESC", fecha);
                List<Object[]> ventasDetalle;
                try {
                    ventasDetalle = consultar(c,
                        "SELECT p.numero_orden, DATE_FORMAT(p.fecha_hora,'%d/%m/%Y %H:%i'), "
                        + "CONCAT(u.nombre,' ',u.apellido), "
                        + "CASE p.tipo_servicio WHEN 'COMER_AQUI' THEN 'En el local' "
                        + "WHEN 'PARA_LLEVAR' THEN 'Para llevar' ELSE 'A domicilio' END, "
                        + "p.estado, COALESCE(p.metodo_pago,'-'), "
                        + "COALESCE(p.monto_recibido,0), p.total, "
                        + "COALESCE(SUM(d.cantidad),0) "
                        + "FROM pedido p JOIN usuario u ON u.id_usuario=p.id_usuario "
                        + "LEFT JOIN pedido_detalle d ON d.id_pedido=p.id_pedido "
                        + "WHERE p.fecha_hora>=? AND p.fecha_hora<? "
                        + "GROUP BY p.id_pedido,p.numero_orden,p.fecha_hora,u.nombre,u.apellido,"
                        + "p.tipo_servicio,p.estado,p.metodo_pago,p.monto_recibido,p.total "
                        + "ORDER BY p.fecha_hora DESC,p.id_pedido DESC", fecha);
                } catch (SQLException esquemaAnterior) {
                    // Mantiene compatibilidad con bases antiguas sin datos de pago.
                    ventasDetalle = new ArrayList<>();
                }
                // La presentación es la línea vendida; sus productos internos no duplican la venta.
                List<Object[]> productos = consultar(c,
                    "SELECT pr.nombre, ca.nombre, SUM(d.cantidad), "
                    + "CASE WHEN pr.estado=0 THEN 'Inactivo' WHEN pr.stock_actual<=0 THEN 'Agotado' ELSE 'Disponible' END "
                    + "FROM pedido_detalle d JOIN pedido p ON p.id_pedido=d.id_pedido "
                    + "JOIN presentacion_menu pm ON pm.id_presentacion=d.id_presentacion "
                    + "JOIN producto pr ON pr.id_producto=pm.id_producto_principal JOIN categoria ca ON ca.id_categoria=pr.id_categoria "
                    + "WHERE p.estado='PAGADO' AND p.fecha_hora>=? AND p.fecha_hora<? "
                    + "GROUP BY pr.id_producto,pr.nombre,ca.nombre,pr.estado,pr.stock_actual "
                    + "ORDER BY SUM(d.cantidad) DESC,pr.nombre LIMIT 10", fecha);
                List<Object[]> alertas = consultar(c,
                    "SELECT 'Producto',nombre,stock_actual,stock_minimo,'unidades', "
                    + "CASE WHEN stock_actual<=0 THEN 'Agotado' ELSE 'Stock bajo' END "
                    + "FROM producto WHERE estado=1 AND tipo_stock='DIRECTO' AND stock_actual<=stock_minimo "
                    + "UNION ALL SELECT 'Ingrediente',nombre,stock_actual,stock_minimo,unidad_medida, "
                    + "CASE WHEN stock_actual<=0 THEN 'Agotado' ELSE 'Stock bajo' END "
                    + "FROM ingrediente WHERE estado=1 AND stock_actual<=stock_minimo ORDER BY 6,2", null);
                Object[] k = indicadores.get(0);
                BigDecimal efectivo = totalPorMetodo(ventasDetalle, "EFECTIVO");
                BigDecimal tarjeta = totalPorMetodo(ventasDetalle, "TARJETA");
                BigDecimal ventasPagadas = new BigDecimal(k[0].toString());
                BigDecimal otros = ventasPagadas.subtract(efectivo).subtract(tarjeta);
                if (otros.signum() < 0) otros = BigDecimal.ZERO;
                Resumen resultado = new Resumen(new BigDecimal(k[0].toString()), ((Number) k[1]).longValue(),
                    new BigDecimal(k[2].toString()), ((Number) k[3]).longValue(),
                    ((Number) stock.get(0)[0]).longValue(), pedidos, productos, alertas,
                    ventasDetalle, efectivo, tarjeta, otros);
                c.commit();
                return resultado;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
    }

    private BigDecimal totalPorMetodo(
            List<Object[]> ventas, String metodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] fila : ventas) {
            if ("PAGADO".equals(fila[4]) && metodo.equals(fila[5])) {
                total = total.add(new BigDecimal(fila[7].toString()));
            }
        }
        return total;
    }

    private List<Object[]> consultar(Connection c, String sql, LocalDate fecha) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setQueryTimeout(20);
            if (fecha != null) {
                // Límites inclusivo/exclusivo: incluye todo el día y permite usar índices.
                s.setObject(1, fecha.atStartOfDay());
                s.setObject(2, fecha.plusDays(1).atStartOfDay());
            }
            try (ResultSet r = s.executeQuery()) {
                List<Object[]> filas = new ArrayList<>();
                int columnas = r.getMetaData().getColumnCount();
                while (r.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 0; i < columnas; i++) fila[i] = r.getObject(i + 1);
                    filas.add(fila);
                }
                return filas;
            }
        }
    }
}
