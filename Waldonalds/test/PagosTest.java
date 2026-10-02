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
        ProductoPedido producto = new ProductoPedido(UUID.randomUUID().toString(),1,
                "Producto de prueba",1,"DIRECTO",List.of());
        OpcionPedido opcion = new OpcionPedido(1,1,1,"__Producto principal",
                "Producto de prueba",BigDecimal.ZERO,List.of(producto));
        LineaPedido linea = new LineaPedido(UUID.randomUUID().toString(),1,1,
                "Producto de prueba","Individual",new BigDecimal("10.25"),
                List.of(opcion),cantidad);
        return new SolicitudPago(UUID.randomUUID().toString(),1,List.of(linea),
                "COMER_AQUI","EFECTIVO",new BigDecimal("100.00"),"");
    }
    static SolicitudPago comboPersonalizado() {
        ModificacionPedido sin = new ModificacionPedido(1,1,"SIN","Pepinillo",
                BigDecimal.ONE,1,BigDecimal.ZERO);
        ModificacionPedido extra = new ModificacionPedido(1,1,"EXTRA","Pepinillo",
                BigDecimal.ONE,1,new BigDecimal("2.00"));
        ProductoPedido primero = new ProductoPedido(UUID.randomUUID().toString(),2,
                "Hamburguesa",1,"RECETA",List.of(sin));
        ProductoPedido segundo = new ProductoPedido(UUID.randomUUID().toString(),2,
                "Hamburguesa",1,"RECETA",List.of(extra));
        OpcionPedido uno = new OpcionPedido(2,2,1,"Elige tus hamburguesas",
                "Hamburguesa",BigDecimal.ZERO,List.of(primero));
        OpcionPedido dos = new OpcionPedido(2,2,2,"Elige tus hamburguesas",
                "Hamburguesa",BigDecimal.ZERO,List.of(segundo));
        LineaPedido linea = new LineaPedido(UUID.randomUUID().toString(),3,2,
                "Combo de dos","Combo",new BigDecimal("20.00"),List.of(uno,dos),1);
        return new SolicitudPago(UUID.randomUUID().toString(),1,List.of(linea),
                "PARA_LLEVAR","EFECTIVO",new BigDecimal("100.00"),"");
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
                for(String tabla: List.of("categoria","usuario","ingrediente","producto",
                        "producto_ingrediente","presentacion_menu","grupo_presentacion",
                        "opcion_grupo","opcion_componente","pedido","pedido_detalle",
                        "pedido_opcion","pedido_producto","pedido_modificacion","movimiento_inventario")) {
                    try(Statement s=original.createStatement();ResultSet r=s.executeQuery("SHOW CREATE TABLE `"+tabla+"`")) { r.next(); sql(r.getString(2)); }
                }
                sql(Files.readString(Path.of("sql/pagos.sql")));
                sql("INSERT INTO categoria(id_categoria,nombre) VALUES(1,'Prueba')");
                sql("INSERT INTO usuario(id_usuario,nombre,apellido,usuario,correo,password_hash,rol) VALUES(1,'Prueba','Caja','prueba','prueba@local.test','sin-acceso','CAJERO')");
                sql("INSERT INTO producto(id_producto,id_categoria,nombre,precio_base,tipo_stock,stock_actual) VALUES(1,1,'Producto de prueba',10.25,'DIRECTO',10)");
                sql("INSERT INTO presentacion_menu(id_presentacion,id_producto_principal,nombre,tipo,precio,predeterminada) VALUES(1,1,'Individual','INDIVIDUAL',10.25,1)");
                sql("INSERT INTO grupo_presentacion(id_grupo,id_presentacion,nombre,minimo,maximo,visible) VALUES(1,1,'__Producto principal',1,1,0)");
                sql("INSERT INTO opcion_grupo(id_opcion,id_grupo,nombre,predeterminada) VALUES(1,1,'Producto de prueba',1)");
                sql("INSERT INTO opcion_componente(id_opcion,id_producto,cantidad) VALUES(1,1,1)");
                sql("INSERT INTO producto(id_producto,id_categoria,nombre,precio_base,tipo_stock,stock_actual) VALUES(2,1,'Hamburguesa',10,'RECETA',0),(3,1,'Combo de dos',20,'NINGUNO',0)");
                sql("INSERT INTO ingrediente(id_ingrediente,nombre,unidad_medida,stock_actual,stock_minimo) VALUES(1,'Pepinillo','unidad',10,1)");
                sql("INSERT INTO producto_ingrediente(id_producto_ingrediente,id_producto,id_ingrediente,cantidad_default,permite_quitar,permite_extra,cantidad_extra,precio_extra,max_extras) VALUES(1,2,1,1,1,1,1,2,2)");
                sql("INSERT INTO presentacion_menu(id_presentacion,id_producto_principal,nombre,tipo,precio,predeterminada) VALUES(2,3,'Combo','COMBO',20,1)");
                sql("INSERT INTO grupo_presentacion(id_grupo,id_presentacion,nombre,minimo,maximo,permite_repetir,visible) VALUES(2,2,'Elige tus hamburguesas',2,2,1,1)");
                sql("INSERT INTO opcion_grupo(id_opcion,id_grupo,nombre,predeterminada) VALUES(2,2,'Hamburguesa',1)");
                sql("INSERT INTO opcion_componente(id_opcion,id_producto,cantidad) VALUES(2,2,1)");
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
                int ventas = numero("SELECT COUNT(*) FROM pedido WHERE estado='PAGADO'");
                int stock = numero("SELECT stock_actual FROM producto WHERE id_producto=1");
                comprobar(ticket.contains("79.50") && ventas==1 && stock==8,
                        "efectivo guarda venta y descuenta stock (ventas="+ventas+", stock="+stock+")");
                comprobar(dao.cobrar(p).equals(ticket) && numero("SELECT COUNT(*) FROM pedido")==1 && numero("SELECT COUNT(*) FROM movimiento_inventario")==1, "reintento no duplica venta ni inventario");
                rechazar(()->dao.cobrar(pago(9)), "rechaza stock insuficiente");
                sql("UPDATE presentacion_menu SET precio=11 WHERE id_presentacion=1");
                rechazar(()->dao.cobrar(pago(1)), "rechaza precio modificado");
                sql("UPDATE presentacion_menu SET precio=10.25 WHERE id_presentacion=1");
                sql("UPDATE producto SET estado=0 WHERE id_producto=1");
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
                SolicitudPago personalizado = comboPersonalizado();
                String ticketCombo = dao.cobrar(personalizado);
                comprobar(personalizado.total().compareTo(new BigDecimal("22.00"))==0
                        && numero("SELECT stock_actual FROM ingrediente WHERE id_ingrediente=1")==8
                        && numero("SELECT COUNT(*) FROM pedido_producto WHERE id_producto=2")==2
                        && numero("SELECT COUNT(*) FROM pedido_modificacion")==2
                        && ticketCombo.contains("SIN Pepinillo") && ticketCombo.contains("EXTRA Pepinillo"),
                        "combo repetido conserva y descuenta cada personalización por separado");
                System.out.println("PRUEBAS COMPLETADAS: "+pruebas);
            } finally {
                if(creada) try(Statement s=original.createStatement()) { s.execute("DROP DATABASE `"+base+"`"); }
            }
        }
    }
}
