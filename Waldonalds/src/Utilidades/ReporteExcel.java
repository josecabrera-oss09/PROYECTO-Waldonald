package Utilidades;

import DAO.ReporteDAO;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Genera un libro Excel real sin depender de bibliotecas externas. */
public final class ReporteExcel {

    private ReporteExcel() {
    }

    public static void guardar(
            Path archivo,
            LocalDate fecha,
            ReporteDAO.Resumen reporte) throws IOException {
        if (archivo == null || fecha == null || reporte == null) {
            throw new IllegalArgumentException("El reporte está incompleto.");
        }
        Path padre = archivo.toAbsolutePath().normalize().getParent();
        if (padre != null) Files.createDirectories(padre);

        Hoja hoja = construirHoja(reporte);
        try (OutputStream salida = Files.newOutputStream(archivo);
                ZipOutputStream zip = new ZipOutputStream(salida,
                        StandardCharsets.UTF_8)) {
            entrada(zip, "[Content_Types].xml", tiposContenido());
            entrada(zip, "_rels/.rels", relacionesRaiz());
            entrada(zip, "xl/workbook.xml", libro());
            entrada(zip, "xl/_rels/workbook.xml.rels", relacionesLibro());
            entrada(zip, "xl/styles.xml", estilos());
            entrada(zip, "xl/worksheets/sheet1.xml", hoja.xml());
        }
    }

    private static Hoja construirHoja(ReporteDAO.Resumen r) {
        Hoja h = new Hoja();
        h.filaCabecera("N.º orden", "Fecha / hora", "Cajero",
                "Tipo de servicio", "Estado", "Método de pago",
                "Monto recibido", "Total", "Cantidad de ítems");
        for (Object[] fila : r.ventasDetalle()) {
            h.fila(
                    Celda.texto("#" + fila[0]),
                    Celda.texto(fila[1]),
                    Celda.texto(fila[2]),
                    Celda.texto(fila[3]),
                    Celda.texto(estado(fila[4])),
                    Celda.texto(metodo(fila[5])),
                    Celda.numero(fila[6]),
                    Celda.numero(fila[7]),
                    Celda.numero(fila[8]));
        }
        return h;
    }

    private static String estado(Object valor) {
        return switch (String.valueOf(valor)) {
            case "PAGADO" -> "Completado";
            case "CANCELADO" -> "Cancelado";
            default -> String.valueOf(valor);
        };
    }

    private static String metodo(Object valor) {
        return switch (String.valueOf(valor)) {
            case "EFECTIVO" -> "Efectivo";
            case "TARJETA" -> "Tarjeta";
            case "-", "null" -> "—";
            default -> String.valueOf(valor);
        };
    }

    private static BigDecimal decimal(Object valor) {
        return valor == null ? BigDecimal.ZERO
                : new BigDecimal(valor.toString());
    }

    private static void entrada(
            ZipOutputStream zip, String nombre, String contenido)
            throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String tiposContenido() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                  <Default Extension="xml" ContentType="application/xml"/>
                  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                </Types>
                """;
    }

    private static String relacionesRaiz() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                </Relationships>
                """;
    }

    private static String libro() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                  <sheets><sheet name="Ventas del día" sheetId="1" r:id="rId1"/></sheets>
                </workbook>
                """;
    }

    private static String relacionesLibro() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
                </Relationships>
                """;
    }

    private static String estilos() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <fonts count="1"><font><sz val="11"/><name val="Aptos"/></font></fonts>
                  <fills count="2">
                    <fill><patternFill patternType="none"/></fill>
                    <fill><patternFill patternType="gray125"/></fill>
                  </fills>
                  <borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders>
                  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
                  <cellXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/></cellXfs>
                  <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
                </styleSheet>
                """;
    }

    private record Celda(Object valor, boolean numero) {
        static Celda texto(Object valor) {
            return new Celda(valor == null ? "" : valor.toString(),
                    false);
        }

        static Celda numero(Object valor) {
            return new Celda(valor == null ? BigDecimal.ZERO : valor,
                    true);
        }
    }

    private static final class Hoja {
        private final StringBuilder filas = new StringBuilder();
        private int numeroFila = 1;

        void filaCabecera(String... textos) {
            Celda[] celdas = new Celda[textos.length];
            for (int i = 0; i < textos.length; i++) {
                celdas[i] = Celda.texto(textos[i]);
            }
            fila(celdas);
        }

        void fila(Celda... celdas) {
            int filaActual = numeroFila++;
            filas.append("<row r=\"").append(filaActual).append("\">");
            for (int i = 0; i < celdas.length; i++) {
                Celda celda = celdas[i];
                String referencia = columna(i + 1) + filaActual;
                if (celda.numero()) {
                    filas.append("<c r=\"").append(referencia)
                            .append("\"><v>").append(numero(celda.valor()))
                            .append("</v></c>");
                } else {
                    filas.append("<c r=\"").append(referencia)
                            .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                            .append(xml(celda.valor().toString()))
                            .append("</t></is></c>");
                }
            }
            filas.append("</row>");
        }

        String xml() {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                    + "<sheetData>" + filas + "</sheetData>"
                    + "</worksheet>";
        }

        private static String numero(Object valor) {
            if (valor instanceof BigDecimal decimal) {
                return decimal.toPlainString();
            }
            if (valor instanceof Number numero) {
                return numero.toString();
            }
            return decimal(valor).toPlainString();
        }

        private static String columna(int numero) {
            StringBuilder texto = new StringBuilder();
            int valor = numero;
            while (valor > 0) {
                int residuo = (valor - 1) % 26;
                texto.insert(0, (char) ('A' + residuo));
                valor = (valor - 1) / 26;
            }
            return texto.toString();
        }

        private static String xml(String texto) {
            return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
        }
    }
}
