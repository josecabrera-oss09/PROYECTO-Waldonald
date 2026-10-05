package DAO;

import Conexion.Conexion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

/** Tablas TEMPORARY de esta conexión: no modifica las tablas reales. */
public class ReporteDAOTest {
    static void verificar(boolean ok, String mensaje) { if (!ok) throw new AssertionError(mensaje); }
    public static void main(String[] args) throws Exception {
        try (Connection c = Conexion.conectar()) {
            if (c == null) throw new AssertionError("MySQL no disponible");
            try (var s = c.createStatement()) {
                // MySQL oculta los nombres reales solo dentro de esta conexión.
                s.execute("CREATE TEMPORARY TABLE usuario (id_usuario INT,nombre VARCHAR(50),apellido VARCHAR(50))");
                s.execute("CREATE TEMPORARY TABLE categoria (id_categoria INT,nombre VARCHAR(50))");
                s.execute("CREATE TEMPORARY TABLE producto (id_producto INT,id_categoria INT,nombre VARCHAR(100),tipo_stock VARCHAR(20),estado BOOLEAN,stock_actual INT,stock_minimo INT)");
                s.execute("CREATE TEMPORARY TABLE ingrediente (nombre VARCHAR(100),estado BOOLEAN,stock_actual DECIMAL(10,2),stock_minimo DECIMAL(10,2),unidad_medida VARCHAR(20))");
                s.execute("CREATE TEMPORARY TABLE presentacion_menu (id_presentacion INT,id_producto_principal INT)");
                s.execute("CREATE TEMPORARY TABLE pedido (id_pedido INT,numero_orden INT,id_usuario INT,fecha_hora DATETIME,tipo_servicio VARCHAR(20),estado VARCHAR(20),metodo_pago VARCHAR(20),monto_recibido DECIMAL(10,2),total DECIMAL(10,2))");
                s.execute("CREATE TEMPORARY TABLE pedido_detalle (id_pedido INT,id_presentacion INT,cantidad INT)");
                s.execute("INSERT INTO usuario VALUES (1,'Prueba','Temporal')");
                s.execute("INSERT INTO categoria VALUES (1,'Comida')");
                s.execute("INSERT INTO producto VALUES (1,1,'Combo','DIRECTO',1,5,5),(2,1,'Componente','DIRECTO',1,0,0),(3,1,'Inactivo','DIRECTO',0,0,5),(4,1,'Disponible','DIRECTO',1,10,5)");
                s.execute("INSERT INTO presentacion_menu VALUES (1,1),(2,2),(3,3),(4,4)");
                s.execute("INSERT INTO ingrediente VALUES ('Harina',1,1.25,2.00,'kg'),('Inactivo',0,0,5,'kg')");
                s.execute("INSERT INTO pedido VALUES (1,1,1,'2026-09-25 00:00:00','COMER_AQUI','PAGADO','EFECTIVO',100,100),(2,2,1,'2026-09-25 23:59:59','A_DOMICILIO','PAGADO','TARJETA',50,50),(3,3,1,'2026-09-25 12:00:00','PARA_LLEVAR','PENDIENTE',NULL,0,30),(4,4,1,'2026-09-25 14:00:00','PARA_LLEVAR','CANCELADO',NULL,0,200),(5,5,1,'2026-09-26 00:00:00','COMER_AQUI','PAGADO','EFECTIVO',900,900)");
                s.execute("INSERT INTO pedido_detalle VALUES (1,1,2),(2,4,1),(3,4,9),(4,4,20),(5,4,30)");
            }
            var dao = new ReporteDAO();
            var r = dao.cargar(c, LocalDate.of(2026,9,25));
            verificar(r.ventas().compareTo(new BigDecimal("150"))==0,"Ventas pagadas y límites del día");
            verificar(r.pedidos()==2,"Conteo de pedidos cobrados");
            verificar(r.recientes().size()==4,"Listado de todos los estados");
            verificar(r.cancelados()==1,"Conteo de cancelados");
            verificar(r.ticket().compareTo(new BigDecimal("75"))==0,"Promedio solo de pagados");
            verificar(r.stockBajo()==2 && r.alertas().size()==3,"Umbral inclusivo y solo activos");
            verificar(r.productos().size()==2 && "Combo".equals(r.productos().get(0)[0]),"Top sin componentes ni cancelados/pendientes");
            verificar(new BigDecimal(r.productos().get(0)[2].toString()).intValue()==2,"Cantidad del combo");
            verificar(r.productosMasVendidos().size()==2
                    && "Combo".equals(r.productosMasVendidos().get(0)[0]),
                    "Top 5 para reportes");
            verificar(r.ventasPorCajero().size()==1
                    && new BigDecimal(r.ventasPorCajero().get(0)[1].toString())
                            .compareTo(new BigDecimal("150"))==0,
                    "Ventas agrupadas por cajero");
            verificar(r.ventasPorHora().size()==2,
                    "Ventas agrupadas por hora");
            verificar("A domicilio".equals(r.recientes().get(0)[3]),"Servicio y orden reciente");
            var vacio=dao.cargar(c,LocalDate.of(2026,9,24));
            verificar(vacio.pedidos()==0 && vacio.cancelados()==0 && vacio.ventas().signum()==0 && vacio.ticket().signum()==0,"Día sin ventas");
            verificar(vacio.stockBajo()==2,"Inventario siempre actual");
            System.out.println("OK: límites de día, pagados/pendientes/cancelados, combos, stock inclusivo, inactivos, día vacío.");
        }
    }
}
