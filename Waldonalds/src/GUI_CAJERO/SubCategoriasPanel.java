package GUI_CAJERO;

import Componentes.BotonLetras;
import Componentes.TarjetaProducto;
import DAO.ProductoDAO;
import Modelos.Productos;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Vista reutilizable para las categorías principales distintas de Desayunos.
 * Construye la barra con los nombres que la base de datos ya tenga definidos.
 */
public class SubCategoriasPanel extends JPanel {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final String nombreCategoria;
    private final List<String> subcategoriasConfiguradas;
    private final JPanel barraSubcategorias = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final JPanel panelProductos = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 12));
    private final JScrollPane scrollProductos = new JScrollPane(panelProductos);

    public SubCategoriasPanel(
            String nombreCategoria,
            List<String> subcategoriasConfiguradas) {
        this.nombreCategoria = nombreCategoria;
        this.subcategoriasConfiguradas = List.copyOf(subcategoriasConfiguradas);
        inicializarVista();
        cargarCategoria();
    }

    private void inicializarVista() {
        setBackground(Color.WHITE);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        barraSubcategorias.setBackground(Color.WHITE);
        add(barraSubcategorias,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1810, 30));

        panelProductos.setBackground(Color.WHITE);
        scrollProductos.setBackground(Color.WHITE);
        scrollProductos.getViewport().setBackground(Color.WHITE);
        add(scrollProductos,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1810, 680));
    }

    private void cargarCategoria() {
        Integer idCategoria = productoDAO.obtenerIdCategoriaPorNombre(nombreCategoria);
        List<String> subcategoriasDatos = idCategoria == null
                ? List.of()
                : productoDAO.obtenerSubcategorias(idCategoria);
        List<String> subcategorias = subcategoriasDatos.isEmpty()
                ? subcategoriasConfiguradas
                : subcategoriasDatos;

        if (idCategoria == null) {
            crearBoton("Todos", null, null, true);
            for (String subcategoria : subcategorias) {
                crearBoton(subcategoria, null, subcategoria, false);
            }
            return;
        }

        crearBoton("Todos", idCategoria, null, true);
        for (String subcategoria : subcategorias) {
            crearBoton(subcategoria, idCategoria, subcategoria, false);
        }

        mostrarProductos(idCategoria, null, Color.WHITE);
    }

    private void crearBoton(
            String texto,
            Integer idCategoria,
            String subcategoria,
            boolean seleccionadoInicialmente) {
        BotonLetras boton = new BotonLetras();
        boton.setText(texto);
        boton.setPreferredSize(new Dimension(160, 30));
        boton.setSeleccionado(seleccionadoInicialmente);
        boton.addActionListener(e -> {
            if (idCategoria != null) {
                mostrarProductos(idCategoria, subcategoria,
                        subcategoria == null
                                ? Color.WHITE
                                : colorDeSubcategoria(subcategoria));
            }
        });
        barraSubcategorias.add(boton);
    }

    private void mostrarProductos(
            int idCategoria,
            String subcategoria,
            Color colorFondo) {
        panelProductos.removeAll();
        panelProductos.setBackground(colorFondo);
        scrollProductos.getViewport().setBackground(colorFondo);
        List<Productos> productos = subcategoria == null
                ? productoDAO.obtenerPorCategoria(idCategoria)
                : productoDAO.obtenerPorCategoriaYSubcategoria(
                        idCategoria, subcategoria);

        for (Productos producto : productos) {
            panelProductos.add(new TarjetaProducto(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getImagen()
            ));
        }

        panelProductos.revalidate();
        panelProductos.repaint();
        scrollProductos.getVerticalScrollBar().setValue(0);
    }

    private Color colorDeSubcategoria(String subcategoria) {
        return switch (subcategoria) {
            case "Sándwiches" -> new Color(255, 244, 230);
            case "McMuffin" -> new Color(235, 246, 255);
            case "Hot Cakes" -> new Color(255, 240, 246);
            case "Bebidas" -> new Color(235, 250, 243);
            case "Por tiempo limitado" -> new Color(248, 241, 255);
            default -> new Color(250, 250, 250);
        };
    }
}
