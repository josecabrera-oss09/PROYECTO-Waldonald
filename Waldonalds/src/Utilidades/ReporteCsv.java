package Utilidades;

import DAO.ReporteDAO;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/** Exporta el resultado consultado sin modificar datos. */
public final class ReporteCsv {
    private ReporteCsv() {}

    public static String generar(LocalDate fecha, ReporteDAO.Resumen r) {
        StringBuilder b = new StringBuilder("\uFEFF");
        fila(b, "Reporte de ventas", fecha);
        fila(b, "Ventas pagadas (Q)", r.ventas());
        fila(b, "Pedidos cobrados", r.pedidos());
        fila(b, "Ticket promedio pagado (Q)", r.ticket());
        fila(b, "Pedidos cancelados", r.cancelados());
        fila(b, "Efectivo (Q)", r.efectivo());
        fila(b, "Tarjeta (Q)", r.tarjeta());
        fila(b, "Otros m�todos (Q)", r.otros());
        fila(b, "Productos activos con stock bajo (actual)", r.stockBajo());
        if (!r.ventasDetalle().isEmpty()) {
            seccion(b, "Detalle de ventas", new String[]{"Orden", "Fecha/Hora",
                "Cajero", "Servicio", "Estado", "M�todo de pago",
                "Monto recibido", "Total", "Cantidad de �tems"},
                r.ventasDetalle());
        }
        seccion(b, "Pedidos del día", new String[]{"Orden", "Cajero", "Hora", "Servicio", "Estado", "Total (Q)"}, r.recientes());
        seccion(b, "10 productos más vendidos (líneas principales pagadas)", new String[]{"Producto", "Categoría", "Unidades", "Estado actual"}, r.productos());
        seccion(b, "Alertas de inventario actual", new String[]{"Tipo", "Nombre", "Existencias", "Mínimo", "Unidad", "Estado"}, r.alertas());
        return b.toString();
    }

    private static void seccion(StringBuilder b, String titulo, String[] columnas, List<Object[]> filas) {
        b.append("\r\n");
        fila(b, titulo);
        fila(b, (Object[]) columnas);
        for (Object[] valores : filas) {
            // Algunas consultas incluyen identificadores internos ocultos.
            // El CSV conserva únicamente las columnas visibles declaradas.
            fila(b, Arrays.copyOf(valores,
                    Math.min(valores.length, columnas.length)));
        }
    }

    private static void fila(StringBuilder b, Object... valores) {
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) b.append(',');
            String texto = valores[i] == null ? "" : valores[i].toString();
            String inicio = texto.stripLeading();
            // Nombres que empiezan con operadores no deben ejecutarse como fórmulas.
            if (!(valores[i] instanceof Number) && !inicio.isEmpty()
                    && "=+-@".indexOf(inicio.charAt(0)) >= 0) texto = "'" + texto;
            b.append('"').append(texto.replace("\"", "\"\"")).append('"');
        }
        b.append("\r\n");
    }
}
