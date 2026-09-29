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
                s.execute("CREATE TEMPORARY TABLE producto (id_producto INT,id_categoria INT,nombre VARCHAR(100),estado BOOLEAN,stock_actual INT,stock_minimo INT)");
                s.execute("CREATE TEMPORARY TABLE ingrediente (nombre VARCHAR(100),estado BOOLEAN,stock_actual DECIMAL(10,2),stock_minimo DECIMAL(10,2),unidad_medida VARCHAR(20))");
                s.execute("CREATE TEMPORARY TABLE pedido (id_pedido INT,numero_orden INT,id_usuario INT,fecha_hora DATETIME,tipo_servicio VARCHAR(20),estado VARCHAR(20),total DECIMAL(10,2))");
                s.execute("CREATE TEMPORARY TABLE pedido_detalle (id_pedido INT,id_producto INT,cantidad INT,id_detalle_padre INT)");
                s.execute("INSERT INTO usuario VALUES (1,'Prueba','Temporal')");
                s.execute("INSERT INTO categoria VALUES (1,'Comida')");
                s.execute("INSERT INTO producto VALUES (1,1,'Combo',1,5,5),(2,1,'Componente',1,0,0),(3,1,'Inactivo',0,0,5),(4,1,'Disponible',1,10,5)");
                s.execute("INSERT INTO ingrediente VALUES ('Harina',1,1.25,2.00,'kg'),('Inactivo',0,0,5,'kg')");
                s.execute("INSERT INTO pedido VALUES (1,1,1,'2026-09-25 00:00:00','COMER_AQUI','PAGADO',100),(2,2,1,'2026-09-25 23:59:59','A_DOMICILIO','PAGADO',50),(3,3,1,'2026-09-25 12:00:00','PARA_LLEVAR','PENDIENTE',30),(4,4,1,'2026-09-25 14:00:00','PARA_LLEVAR','CANCELADO',200),(5,5,1,'2026-09-26 00:00:00','COMER_AQUI','PAGADO',900)");
                s.execute("INSERT INTO pedido_detalle VALUES (1,1,2,NULL),(1,2,2,1),(2,4,1,NULL),(3,4,9,NULL),(4,4,20,NULL),(5,4,30,NULL)");
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
            verificar("A domicilio".equals(r.recientes().get(0)[3]),"Servicio y orden reciente");
            var vacio=dao.cargar(c,LocalDate.of(2026,9,24));
            verificar(vacio.pedidos()==0 && vacio.cancelados()==0 && vacio.ventas().signum()==0 && vacio.ticket().signum()==0,"Día sin ventas");
            verificar(vacio.stockBajo()==2,"Inventario siempre actual");
            System.out.println("OK: límites de día, pagados/pendientes/cancelados, combos, stock inclusivo, inactivos, día vacío.");
        }
    }
}
