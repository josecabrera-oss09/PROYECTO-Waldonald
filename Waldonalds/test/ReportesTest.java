import DAO.ReporteDAO;
import GUI_ADMINISTRADOR.ReportesPanel;
import GUI_ADMINISTRADOR.MenuAdmin;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Pruebas de solo lectura y render de reportes. No inserta ni cambia registros. */
public class ReportesTest {
    static Object campo(Object o, String nombre) throws Exception {
        var f = o.getClass().getDeclaredField(nombre); f.setAccessible(true); return f.get(o);
    }
    static void comprobar(boolean valor, String mensaje) {
        if (!valor) throw new AssertionError(mensaje);
    }
    static void organizar(Container c) {
        c.doLayout();
        for (Component hijo : c.getComponents()) if (hijo instanceof Container h) organizar(h);
    }
    static void imagen(Container c, String destino) throws Exception {
        organizar(c);
        BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics(); c.printAll(g); g.dispose();
        File archivo = new File(destino);
        File carpeta = archivo.getParentFile();
        if (carpeta != null) carpeta.mkdirs();
        ImageIO.write(img, "png", archivo);
    }
    public static void main(String[] args) throws Exception {
        var ejemplo = new ReporteDAO.Resumen(new BigDecimal("125.50"), 2, new BigDecimal("62.75"), 0, 0,
                List.<Object[]>of(new Object[]{1,"=SUM(1,2) \"cajero\"","12:00","En salón","PAGADO",new BigDecimal("125.50")}), List.of(), List.of());
        String csv = Utilidades.ReporteCsv.generar(LocalDate.of(2026,9,27), ejemplo);
        comprobar(csv.startsWith("\uFEFF"), "UTF-8 BOM");
        comprobar(csv.contains("\"'=SUM(1,2) \"\"cajero\"\"\""), "Escape CSV y formula");
        comprobar(csv.contains("125.50"), "Importe decimal sin pérdida");
        final ReportesPanel[] panel = new ReportesPanel[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                panel[0] = new ReportesPanel(); panel[0].setSize(1580,1230);
                comprobar(campo(panel[0],"carga") == null, "Constructor sin consultas");
                comprobar(!((JTable) campo(panel[0],"tablaPedidos")).getModel().isCellEditable(0,0), "Tabla solo lectura");
                var fecha = (JSpinner) campo(panel[0],"fechaReporte");
                ((JSpinner.DateEditor) fecha.getEditor()).getTextField().setText("31/02/2026");
                panel[0].actualizarReporte();
                comprobar(campo(panel[0],"carga") == null, "Fecha inválida rechazada");
                fecha.setValue(new java.util.Date());
            } catch (Exception ex) { throw new RuntimeException(ex); }
        });
        System.out.println("OK: CSV, constructor sin DB, tabla de solo lectura y fecha inválida.");
        if (args.length > 0 && args[0].equals("--db")) {
            LocalDate fecha = LocalDate.now();
            ReporteDAO.Resumen r = new ReporteDAO().cargar(fecha);
            comprobar(r.pedidos() == r.recientes().stream().filter(f -> "PAGADO".equals(f[4])).count(), "Conteo de pedidos cobrados");
            comprobar(r.cancelados() == r.recientes().stream().filter(f -> "CANCELADO".equals(f[4])).count(), "Conteo de cancelados");
            BigDecimal suma = BigDecimal.ZERO; int pagados = 0;
            for (Object[] fila : r.recientes()) if ("PAGADO".equals(fila[4])) { suma = suma.add((BigDecimal) fila[5]); pagados++; }
            comprobar(suma.compareTo(r.ventas()) == 0, "Ventas excluyen no pagados");
            BigDecimal promedio = pagados == 0 ? BigDecimal.ZERO : suma.divide(BigDecimal.valueOf(pagados),2,java.math.RoundingMode.HALF_UP);
            comprobar(promedio.compareTo(r.ticket().setScale(2,java.math.RoundingMode.HALF_UP)) == 0, "Ticket promedio");
            comprobar(r.alertas().stream().filter(f -> "Producto".equals(f[0])).count() == r.stockBajo(), "Stock y alertas coherentes");
            comprobar(r.productos().size() <= 10, "Top 10");
            for (Object[] fila : r.alertas()) comprobar(new BigDecimal(fila[2].toString()).compareTo(new BigDecimal(fila[3].toString())) <= 0, "Umbral stock");
            var vacio = new ReporteDAO().cargar(LocalDate.of(1000,1,1));
            comprobar(vacio.pedidos()==0 && vacio.ventas().signum()==0 && vacio.ticket().signum()==0, "Día vacío sin división por cero");
            SwingUtilities.invokeAndWait(() -> panel[0].actualizarReporte());
            long fin = System.currentTimeMillis()+30000;
            while (System.currentTimeMillis()<fin) {
                final boolean[] pendiente={false}; SwingUtilities.invokeAndWait(() -> { try { pendiente[0]=campo(panel[0],"carga")!=null; } catch(Exception ex) { throw new RuntimeException(ex); } });
                if (!pendiente[0]) break; Thread.sleep(50);
            }
            SwingUtilities.invokeAndWait(() -> {
                try {
                    comprobar(campo(panel[0],"ultimoReporte")!=null,"Carga SwingWorker completada");
                    JFrame marco = new JFrame(); marco.setContentPane(panel[0]); marco.pack();
                    panel[0].setSize(1580,1230);
                    imagen(panel[0],"build/reportes-check/reportes.png");
                    marco.dispose();
                } catch(Exception ex) { throw new RuntimeException(ex); }
            });
            System.out.println("OK: MySQL real (solo lectura), indicadores, alertas, día vacío y carga asíncrona. Pedidos del día: "+r.pedidos());
        }
        System.exit(0);
    }
}
