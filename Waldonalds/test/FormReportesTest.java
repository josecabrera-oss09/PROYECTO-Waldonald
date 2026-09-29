import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Comprueba que el formulario visual y el código de Reportes sigan sincronizados. */
public class FormReportesTest {

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void main(String[] args) throws Exception {
        File archivoForm = new File(
                "src/GUI_ADMINISTRADOR/ReportesPanel.form");
        Document documento = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(archivoForm);

        Map<String, String> componentes = new HashMap<>();
        registrar(documento.getElementsByTagName("Component"), componentes);
        registrar(documento.getElementsByTagName("Container"), componentes);

        Map<String, String> requeridos = Map.ofEntries(
                Map.entry("botonActualizar", "Componentes.BotonDerretido"),
                Map.entry("campoBusqueda", "Componentes.CampoBusquedaAdmin"),
                Map.entry("filtroCajero", "Componentes.BotonDesplegable"),
                Map.entry("filtroMetodo", "Componentes.BotonDesplegable"),
                Map.entry("filtroServicio", "Componentes.BotonDesplegable"),
                Map.entry("botonLimpiarFiltros", "Componentes.BotonRedondeado"),
                Map.entry("tablaPedidos", "Componentes.TablaAdministrativa"),
                Map.entry("botonAnterior", "Componentes.BotonRedondeado"),
                Map.entry("botonPagina1", "Componentes.BotonRedondeado"),
                Map.entry("botonPagina2", "Componentes.BotonRedondeado"),
                Map.entry("botonPagina3", "Componentes.BotonRedondeado"),
                Map.entry("botonSiguiente", "Componentes.BotonRedondeado"),
                Map.entry("panelResumen", "Componentes.PanelFlotante"));

        String fuente = Files.readString(new File(
                "src/GUI_ADMINISTRADOR/ReportesPanel.java").toPath());
        for (Map.Entry<String, String> requerido : requeridos.entrySet()) {
            comprobar(requerido.getValue().equals(
                    componentes.get(requerido.getKey())),
                    "Componente incorrecto o ausente: " + requerido.getKey());
            comprobar(fuente.contains(requerido.getKey()),
                    "El código no declara: " + requerido.getKey());
        }

        comprobar(documento.getElementsByTagName("Table").getLength() == 1,
                "Reportes debe tener una sola tabla principal.");
        System.out.println(
                "OK: ReportesPanel.form es editable y contiene filtros, tabla y paginación.");
    }

    private static void registrar(
            NodeList nodos, Map<String, String> componentes) {
        for (int indice = 0; indice < nodos.getLength(); indice++) {
            Elemento:
            {
                Element elemento = (Element) nodos.item(indice);
                String nombre = elemento.getAttribute("name");
                if (!nombre.isBlank()) {
                    componentes.put(nombre, elemento.getAttribute("class"));
                }
            }
        }
    }
}
