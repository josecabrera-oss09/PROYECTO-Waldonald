package Utilidades;

import Modelos.LineaPedido;
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
            p.setProperty(prefijo+"id", ""+l.idProducto()); p.setProperty(prefijo+"nombre", l.nombre());
            p.setProperty(prefijo+"precio", l.precio().toPlainString()); p.setProperty(prefijo+"cantidad", ""+l.cantidad());
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
                lineas.add(new LineaPedido(Integer.parseInt(p.getProperty(prefijo+"id")),p.getProperty(prefijo+"nombre"),
                        new BigDecimal(p.getProperty(prefijo+"precio")),Integer.parseInt(p.getProperty(prefijo+"cantidad"))));
            }
            return new SolicitudPago(p.getProperty("clave"),Integer.parseInt(p.getProperty("usuario")),lineas,
                    p.getProperty("servicio"),p.getProperty("metodo"),new BigDecimal(p.getProperty("recibido")),p.getProperty("referencia"));
        } catch(RuntimeException ex) { throw new IOException("El archivo de cobro pendiente no se puede leer. Verifique la venta antes de continuar.",ex); }
    }
    public void eliminar() throws IOException { Files.deleteIfExists(archivo); }
}
