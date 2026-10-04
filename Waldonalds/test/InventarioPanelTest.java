import GUI_ADMINISTRADOR.CatalogoIngredientesPanel;
import GUI_ADMINISTRADOR.GestionInventarioPanel;
import GUI_ADMINISTRADOR.InventarioPanel;
import GUI_ADMINISTRADOR.IngredienteFormPanel;
import GUI_ADMINISTRADOR.MenuAdmin;
import GUI_ADMINISTRADOR.MovimientoInventarioFormPanel;
import GUI_ADMINISTRADOR.MovimientosInventarioPanel;
import Modelos.ArticuloInventario;
import Modelos.Ingrediente;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/** Comprobación visual y de datos del módulo de inventario. */
public class InventarioPanelTest {

    private static MenuAdmin menu;

    private static Object campo(Object objeto, String nombre) throws Exception {
        var campo = objeto.getClass().getDeclaredField(nombre);
        campo.setAccessible(true);
        return campo.get(objeto);
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static <T extends Component> T buscarComponente(
            Container contenedor, Class<T> tipo) {
        for (Component componente : contenedor.getComponents()) {
            if (tipo.isInstance(componente) && componente.isVisible()) {
                return tipo.cast(componente);
            }
            if (componente instanceof Container hijo) {
                T encontrado = buscarComponente(hijo, tipo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static void capturar(Component componente, String nombre)
            throws Exception {
        componente.setSize(componente.getPreferredSize());
        acomodar(componente);
        BufferedImage imagen = new BufferedImage(componente.getWidth(),
                componente.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graficos = imagen.createGraphics();
        componente.printAll(graficos);
        graficos.dispose();
        File salida = new File("build/inventory-check/" + nombre);
        salida.getParentFile().mkdirs();
        ImageIO.write(imagen, "png", salida);
    }

    private static void acomodar(Component componente) {
        if (!(componente instanceof Container contenedor)) return;
        contenedor.doLayout();
        for (Component hijo : contenedor.getComponents()) acomodar(hijo);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            menu = new MenuAdmin();
            menu.setExtendedState(JFrame.NORMAL);
            menu.setSize(1920, 1080);
            menu.setVisible(true);
            try {
                ((JButton) campo(menu, "botonInventario")).doClick();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        Thread.sleep(1500);
        SwingUtilities.invokeAndWait(() -> {
            try {
                Container contenido = (Container) campo(menu, "panelContenido");
                InventarioPanel modulo = buscarComponente(
                        contenido, InventarioPanel.class);
                verificar(modulo != null, "No se abrió la tarjeta Inventario");
                GestionInventarioPanel inventario =
                        (GestionInventarioPanel) campo(modulo, "existencias");
                JTable tabla = (JTable) campo(inventario, "tablaProductos");
                verificar(tabla.getRowCount() > 0,
                        "La tabla de inventario no cargó filas");
                verificar(tabla.getValueAt(0, 1) instanceof String,
                        "El nombre debe mostrarse como texto, sin imagen");
                verificar(campo(modulo, "botonIngredientes") instanceof JButton,
                        "Falta la vista interna de Ingredientes");
                verificar(campo(modulo, "botonMovimientos") instanceof JButton,
                        "Falta la vista interna de Movimientos");

                JButton botonIngredientes =
                        (JButton) campo(modulo, "botonIngredientes");
                JButton botonExistencias =
                        (JButton) campo(modulo, "botonExistencias");
                botonIngredientes.doClick();
                verificar(botonIngredientes.isShowing(),
                        "La pestaña Ingredientes desapareció al seleccionarla");
                CatalogoIngredientesPanel catalogo =
                        (CatalogoIngredientesPanel) campo(modulo, "ingredientes");
                verificar(catalogo.isVisible(),
                        "No se abrió la vista interna de Ingredientes");
                JTable tablaIngredientes =
                        (JTable) campo(catalogo, "tablaIngredientes");
                verificar(tablaIngredientes.getRowCount() > 0,
                        "El catálogo de ingredientes no cargó filas");
                capturar(modulo, "inventario-ingredientes.png");

                JButton botonMovimientos =
                        (JButton) campo(modulo, "botonMovimientos");
                botonMovimientos.doClick();
                verificar(botonIngredientes.isShowing()
                                && botonMovimientos.isShowing()
                                && botonExistencias.isShowing(),
                        "Las pestañas desaparecieron al abrir Movimientos");
                verificar("Existencias".equals(botonExistencias.getText()),
                        "La pestaña Existencias perdió su texto");
                JLayeredPane capas = (JLayeredPane) campo(modulo, "panelCapas");
                Component vistas = (Component) campo(modulo, "panelVistas");
                verificar(capas.getLayer(botonExistencias)
                                > capas.getLayer(vistas),
                        "Las pestañas no están en una capa superior");
                verificar(capas.getComponentZOrder(botonExistencias)
                                < capas.getComponentZOrder(vistas),
                        "Existencias quedó detrás del panel de vistas");
                MovimientosInventarioPanel historial =
                        (MovimientosInventarioPanel) campo(modulo, "movimientos");
                verificar(historial.isVisible(),
                        "No se abrió la vista interna de Movimientos");
                JTable tablaMovimientos =
                        (JTable) campo(historial, "tablaMovimientos");
                verificar(tablaMovimientos.getColumnCount() == 8,
                        "La tabla de movimientos no tiene la estructura esperada");
                capturar(modulo, "inventario-movimientos.png");

                ((JButton) campo(modulo, "botonExistencias")).doClick();
                verificar(botonIngredientes.isShowing()
                                && botonMovimientos.isShowing(),
                        "Las pestañas desaparecieron al volver a Existencias");

                Container raiz = menu.getContentPane();
                BufferedImage imagen = new BufferedImage(
                        raiz.getWidth(), raiz.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graficos = imagen.createGraphics();
                raiz.printAll(graficos);
                graficos.dispose();
                File salida = new File(
                        "build/inventory-check/inventario-panel.png");
                salida.getParentFile().mkdirs();
                ImageIO.write(imagen, "png", salida);

                Ingrediente ejemplo = new Ingrediente();
                ejemplo.setIdIngrediente(7);
                ejemplo.setNombre("Carne de res");
                ejemplo.setUnidadMedida("unidad");
                ejemplo.setStockActual(new BigDecimal("1500"));
                ejemplo.setStockMinimo(new BigDecimal("150"));
                ejemplo.setActivo(true);
                capturar(new IngredienteFormPanel(null, ejemplo),
                        "ingrediente-form.png");

                ArticuloInventario articulo = new ArticuloInventario(
                        "INGREDIENTE", 7, "Carne de res", "Ingredientes",
                        "unidad", new BigDecimal("1500"),
                        new BigDecimal("150"), true, null);
                capturar(new MovimientoInventarioFormPanel(
                        null, articulo, "ENTRADA"),
                        "movimiento-form.png");
                menu.dispose();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        System.out.println("OK: navegación, datos y nombres sin imágenes.");
        System.exit(0);
    }
}
