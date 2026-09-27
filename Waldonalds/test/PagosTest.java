import DAO.PagoDAO;
import Modelos.*;
import GUI_CAJERO.PedidoPanel;
import java.math.BigDecimal;
import java.sql.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.*;

/** Integra contra MySQL real, exclusivamente en una base temporal nueva. */
public class PagosTest {
    static String base;
    static int pruebas;
    static Connection abrir() throws SQLException {
        Connection c = Conexion.Conexion.conectar();
        if (c == null) throw new SQLException("MySQL no disponible");
        c.setCatalog(base); return c;
    }
    static void sql(String texto) throws SQLException {
        try(Connection c=abrir(); Statement s=c.createStatement()) { s.execute(texto); }
    }
    static int numero(String texto) throws SQLException {
        try(Connection c=abrir(); Statement s=c.createStatement(); ResultSet r=s.executeQuery(texto)) { r.next(); return r.getInt(1); }
    }
    static void comprobar(boolean ok, String nombre) {
        if(!ok) throw new AssertionError(nombre);
        System.out.println("OK: " + nombre); pruebas++;
    }
    static SolicitudPago pago(int cantidad) {
        return new SolicitudPago(UUID.randomUUID().toString(),1,List.of(new LineaPedido(1,"Producto de prueba",new BigDecimal("10.25"),cantidad)),"COMER_AQUI","EFECTIVO",new BigDecimal("100.00"),"");
    }
    static void rechazar(Callable<?> accion, String nombre) throws Exception {
        try { accion.call(); throw new AssertionError(nombre); }
        catch (IllegalArgumentException esperado) { comprobar(true,nombre); }
    }
    public static void main(String[] args) throws Exception {
        base = "waldonalds_test_pagos_" + UUID.randomUUID().toString().replace("-", "");
        boolean creada=false;
        try(Connection original=Conexion.Conexion.conectar()) {
            if(original==null) throw new SQLException("MySQL no disponible");
            try {
                try(Statement s=original.createStatement()) { s.execute("CREATE DATABASE `"+base+"`"); creada=true; }
                for(String tabla: List.of("categoria","usuario","producto","pedido","pedido_detalle","ingrediente","movimiento_inventario")) {
                    try(Statement s=original.createStatement();ResultSet r=s.executeQuery("SHOW CREATE TABLE `"+tabla+"`")) { r.next(); sql(r.getString(2)); }
                }
                sql(Files.readString(Path.of("sql/pagos.sql")));
                sql("INSERT INTO categoria(id_categoria,nombre) VALUES(1,'Prueba')");
                sql("INSERT INTO usuario(id_usuario,nombre,apellido,usuario,password_hash,rol) VALUES(1,'Prueba','Caja','prueba','sin-acceso','CAJERO')");
                sql("INSERT INTO producto(id_producto,id_categoria,nombre,precio_base,stock_actual) VALUES(1,1,'Producto de prueba',10.25,10)");
                PagoDAO dao=new PagoDAO(PagosTest::abrir);
                comprobar(PedidoPanel.monto("10,25").equals(new BigDecimal("10.25")), "monto con coma decimal");
                rechazar(()->PedidoPanel.monto("1.001"), "rechaza más de dos decimales");
                rechazar(()->PedidoPanel.monto("-5"), "rechaza montos negativos");
                SolicitudPago p=pago(2);
                                Path respaldoArchivo=Files.createTempDirectory("waldonalds-pago-test").resolve("pendiente.properties");
                Utilidades.PagoPendienteStore respaldo=new Utilidades.PagoPendienteStore(respaldoArchivo);
                try {
                    respaldo.guardar(p);
                    comprobar(p.equals(respaldo.cargar()),"recupera la misma clave y montos tras reiniciar");
                    respaldo.eliminar();
                    comprobar(respaldo.cargar()==null,"retira el respaldo al completar");
                } finally { Files.deleteIfExists(respaldoArchivo); Files.deleteIfExists(respaldoArchivo.getParent()); }
                comprobar(p.total().equals(new BigDecimal("20.50")) && p.cambio().equals(new BigDecimal("79.50")), "total y cambio exactos");
                rechazar(()->new SolicitudPago(UUID.randomUUID().toString(),1,List.of(),"COMER_AQUI","EFECTIVO",BigDecimal.TEN,""), "rechaza pedido vacío");
                rechazar(()->new SolicitudPago(UUID.randomUUID().toString(),1,p.lineas(),"COMER_AQUI","EFECTIVO",BigDecimal.ONE,""), "rechaza efectivo insuficiente");
                rechazar(()->new SolicitudPago(UUID.randomUUID().toString(),1,p.lineas(),"COMER_AQUI","TARJETA",p.total(),""), "tarjeta requiere referencia");
                String ticket=dao.cobrar(p);
                comprobar(ticket.contains("79.50") && numero("SELECT COUNT(*) FROM pedido WHERE estado='PAGADO'")==1 && numero("SELECT stock_actual FROM producto WHERE id_producto=1")==8, "efectivo guarda venta y descuenta stock");
                comprobar(dao.cobrar(p).equals(ticket) && numero("SELECT COUNT(*) FROM pedido")==1 && numero("SELECT COUNT(*) FROM movimiento_inventario")==1, "reintento no duplica venta ni inventario");
                rechazar(()->dao.cobrar(pago(9)), "rechaza stock insuficiente");
                sql("UPDATE producto SET precio_base=11 WHERE id_producto=1");
                rechazar(()->dao.cobrar(pago(1)), "rechaza precio modificado");
                sql("UPDATE producto SET precio_base=10.25,estado=0 WHERE id_producto=1");
                rechazar(()->dao.cobrar(pago(1)), "rechaza producto inactivo");
                sql("UPDATE producto SET estado=1 WHERE id_producto=1");
                sql("UPDATE usuario SET estado=0 WHERE id_usuario=1");
                rechazar(()->dao.cobrar(pago(1)), "rechaza usuario desactivado");
                sql("UPDATE usuario SET estado=1 WHERE id_usuario=1");
                sql("CREATE TRIGGER fallo_prueba BEFORE INSERT ON movimiento_inventario FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fallo controlado'");
                try { dao.cobrar(pago(1)); throw new AssertionError("Debe fallar el movimiento"); } catch(SQLException esperado) { }
                comprobar(numero("SELECT COUNT(*) FROM pedido")==1 && numero("SELECT stock_actual FROM producto WHERE id_producto=1")==8 && numero("SELECT COUNT(*) FROM pago_operacion")==1, "rollback completo tras fallo al registrar inventario");
                sql("DROP TRIGGER fallo_prueba");
                SolicitudPago tarjeta=new SolicitudPago(UUID.randomUUID().toString(),1,pago(1).lineas(),"PARA_LLEVAR","TARJETA",new BigDecimal("10.25"),"TERMINAL-PRUEBA");
                comprobar(dao.cobrar(tarjeta).contains("TERMINAL-PRUEBA") && numero("SELECT COUNT(*) FROM pedido WHERE metodo_pago='TARJETA' AND monto_recibido=total")==1,"registra tarjeta externa y referencia");
                SolicitudPago incierto=pago(1);
                PagoDAO ambiguo=new PagoDAO(()->{
                    Connection real=abrir();
                    return (Connection)Proxy.newProxyInstance(PagosTest.class.getClassLoader(),new Class[]{Connection.class},(obj,method,params)->{
                        try { Object resultado=method.invoke(real,params); if(method.getName().equals("commit")) throw new SQLException("Respuesta de commit perdida"); return resultado; }
                        catch(InvocationTargetException e) { throw e.getCause(); }
                    });
                });
                try { ambiguo.cobrar(incierto); throw new AssertionError("Simular respuesta perdida"); } catch(SQLException esperado) { }
                int antes=numero("SELECT COUNT(*) FROM pedido");
                dao.cobrar(incierto);
                comprobar(numero("SELECT COUNT(*) FROM pedido")==antes,"respuesta de commit perdida se recupera sin duplicar");
                                String noDisponible=Utilidades.HorarioMenu.disponibilidadActual(java.time.LocalTime.now()).equals("DESAYUNO") ? "ALMUERZO" : "DESAYUNO";
                sql("UPDATE producto SET disponibilidad_menu='"+noDisponible+"' WHERE id_producto=1");
                rechazar(()->dao.cobrar(pago(1)),"rechaza productos fuera de horario");
                sql("UPDATE producto SET disponibilidad_menu='TODO_DIA',stock_actual=1 WHERE id_producto=1");
                ExecutorService ejecutor=Executors.newFixedThreadPool(2);
                try {
                    CountDownLatch inicio=new CountDownLatch(1);
                    Callable<Boolean> intento=()->{ inicio.await(); try { dao.cobrar(pago(1)); return true; } catch(IllegalArgumentException ex) {return false;} };
                    Future<Boolean> a=ejecutor.submit(intento), b=ejecutor.submit(intento); inicio.countDown();
                    comprobar(a.get()!=b.get() && numero("SELECT stock_actual FROM producto WHERE id_producto=1")==0,"dos cajas compiten por última existencia sin sobreventa");
                } finally { ejecutor.shutdownNow(); }
                System.out.println("PRUEBAS COMPLETADAS: "+pruebas);
            } finally {
                if(creada) try(Statement s=original.createStatement()) { s.execute("DROP DATABASE `"+base+"`"); }
            }
        }
    }
}
