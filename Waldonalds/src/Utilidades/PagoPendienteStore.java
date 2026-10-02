package Utilidades;

import Modelos.LineaPedido;
import Modelos.ModificacionPedido;
import Modelos.OpcionPedido;
import Modelos.ProductoPedido;
import Modelos.SolicitudPago;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;

/** Guarda la solicitud antes del cobro para poder recuperarla tras reiniciar la caja. */
public final class PagoPendienteStore {
    private final Path archivo;
    public PagoPendienteStore(int usuario) { this(Path.of("pagos_pendientes", "caja-" + usuario + ".properties")); }
    public PagoPendienteStore(Path archivo) { this.archivo = archivo; }
    public void guardar(SolicitudPago pago) throws IOException {
        Properties p = new Properties();
        p.setProperty("clave", pago.clave()); p.setProperty("usuario", "" + pago.usuario());
        p.setProperty("servicio", pago.servicio()); p.setProperty("metodo", pago.metodo());
        p.setProperty("recibido", pago.recibido().toPlainString()); p.setProperty("referencia", pago.referencia());
        p.setProperty("cantidad", "" + pago.lineas().size());
        for (int i=0; i<pago.lineas().size(); i++) {
            LineaPedido l=pago.lineas().get(i); String prefijo="linea."+i+".";
            p.setProperty(prefijo+"idLinea", l.idLinea());
            p.setProperty(prefijo+"idProducto", ""+l.idProducto());
            p.setProperty(prefijo+"idPresentacion", ""+l.idPresentacion());
            p.setProperty(prefijo+"nombre", l.nombre());
            p.setProperty(prefijo+"presentacion", l.presentacion());
            p.setProperty(prefijo+"precioBase", l.precioBase().toPlainString());
            p.setProperty(prefijo+"cantidad", ""+l.cantidad());
            p.setProperty(prefijo+"opciones", ""+l.opciones().size());
            for (int j=0; j<l.opciones().size(); j++) {
                OpcionPedido o=l.opciones().get(j); String po=prefijo+"opcion."+j+".";
                p.setProperty(po+"idGrupo", ""+o.idGrupo()); p.setProperty(po+"idOpcion", ""+o.idOpcion());
                p.setProperty(po+"numero", ""+o.numero()); p.setProperty(po+"grupo", o.grupo());
                p.setProperty(po+"opcion", o.opcion()); p.setProperty(po+"precio", o.precioExtra().toPlainString());
                p.setProperty(po+"productos", ""+o.productos().size());
                for (int k=0; k<o.productos().size(); k++) {
                    ProductoPedido pr=o.productos().get(k); String pp=po+"producto."+k+".";
                    p.setProperty(pp+"idInterno", pr.idInterno()); p.setProperty(pp+"id", ""+pr.idProducto());
                    p.setProperty(pp+"nombre", pr.nombre()); p.setProperty(pp+"cantidad", ""+pr.cantidad());
                    p.setProperty(pp+"tipoStock", pr.tipoStock()); p.setProperty(pp+"cambios", ""+pr.modificaciones().size());
                    for (int m=0; m<pr.modificaciones().size(); m++) {
                        ModificacionPedido mo=pr.modificaciones().get(m); String pm=pp+"cambio."+m+".";
                        p.setProperty(pm+"idProductoIngrediente", ""+mo.idProductoIngrediente());
                        p.setProperty(pm+"idIngrediente", ""+mo.idIngrediente()); p.setProperty(pm+"tipo", mo.tipo());
                        p.setProperty(pm+"ingrediente", mo.ingrediente()); p.setProperty(pm+"cantidad", mo.cantidad().toPlainString());
                        p.setProperty(pm+"veces", ""+mo.veces()); p.setProperty(pm+"precio", mo.precioExtra().toPlainString());
                    }
                }
            }
        }
        Files.createDirectories(archivo.toAbsolutePath().getParent());
        Path temporal = Files.createTempFile(archivo.toAbsolutePath().getParent(), "pago-", ".tmp");
        try {
            try (FileOutputStream salida = new FileOutputStream(temporal.toFile())) {
                p.store(salida, "Cobro pendiente; no borrar hasta verificar el resultado"); salida.getFD().sync();
            }
            Files.move(temporal, archivo.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally { Files.deleteIfExists(temporal); }
    }
    public SolicitudPago cargar() throws IOException {
        if (!Files.exists(archivo)) return null;
        Properties p = new Properties();
        try(InputStream entrada=Files.newInputStream(archivo)) { p.load(entrada); }
        try {
            List<LineaPedido> lineas=new ArrayList<>();
            int cantidad=Integer.parseInt(p.getProperty("cantidad"));
            if(cantidad < 1 || cantidad > 10000) throw new IllegalArgumentException();
            for(int i=0;i<cantidad;i++) {
                String prefijo="linea."+i+".";
                List<OpcionPedido> opciones=new ArrayList<>();
                int cantidadOpciones=Integer.parseInt(p.getProperty(prefijo+"opciones"));
                for(int j=0;j<cantidadOpciones;j++) {
                    String po=prefijo+"opcion."+j+"."; List<ProductoPedido> productos=new ArrayList<>();
                    int cantidadProductos=Integer.parseInt(p.getProperty(po+"productos"));
                    for(int k=0;k<cantidadProductos;k++) {
                        String pp=po+"producto."+k+"."; List<ModificacionPedido> cambios=new ArrayList<>();
                        int cantidadCambios=Integer.parseInt(p.getProperty(pp+"cambios"));
                        for(int m=0;m<cantidadCambios;m++) {
                            String pm=pp+"cambio."+m+".";
                            cambios.add(new ModificacionPedido(Integer.parseInt(p.getProperty(pm+"idProductoIngrediente")),
                                    Integer.parseInt(p.getProperty(pm+"idIngrediente")),p.getProperty(pm+"tipo"),
                                    p.getProperty(pm+"ingrediente"),new BigDecimal(p.getProperty(pm+"cantidad")),
                                    Integer.parseInt(p.getProperty(pm+"veces")),new BigDecimal(p.getProperty(pm+"precio"))));
                        }
                        productos.add(new ProductoPedido(p.getProperty(pp+"idInterno"),Integer.parseInt(p.getProperty(pp+"id")),
                                p.getProperty(pp+"nombre"),Integer.parseInt(p.getProperty(pp+"cantidad")),
                                p.getProperty(pp+"tipoStock"),cambios));
                    }
                    opciones.add(new OpcionPedido(Integer.parseInt(p.getProperty(po+"idGrupo")),
                            Integer.parseInt(p.getProperty(po+"idOpcion")),Integer.parseInt(p.getProperty(po+"numero")),
                            p.getProperty(po+"grupo"),p.getProperty(po+"opcion"),new BigDecimal(p.getProperty(po+"precio")),productos));
                }
                lineas.add(new LineaPedido(p.getProperty(prefijo+"idLinea"),Integer.parseInt(p.getProperty(prefijo+"idProducto")),
                        Integer.parseInt(p.getProperty(prefijo+"idPresentacion")),p.getProperty(prefijo+"nombre"),
                        p.getProperty(prefijo+"presentacion"),new BigDecimal(p.getProperty(prefijo+"precioBase")),
                        opciones,Integer.parseInt(p.getProperty(prefijo+"cantidad"))));
            }
            return new SolicitudPago(p.getProperty("clave"),Integer.parseInt(p.getProperty("usuario")),lineas,
                    p.getProperty("servicio"),p.getProperty("metodo"),new BigDecimal(p.getProperty("recibido")),p.getProperty("referencia"));
        } catch(RuntimeException ex) { throw new IOException("El archivo de cobro pendiente no se puede leer. Verifique la venta antes de continuar.",ex); }
    }
    public void eliminar() throws IOException { Files.deleteIfExists(archivo); }
}
