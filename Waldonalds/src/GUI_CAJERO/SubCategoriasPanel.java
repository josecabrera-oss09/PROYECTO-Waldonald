package GUI_CAJERO;

import Componentes.BotonLetras;
import Componentes.TarjetaProducto;
import DAO.ProductoDAO;
import Modelos.Productos;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/** Vista de subcategorías con consulta asíncrona y renderizado por lotes. */
public class SubCategoriasPanel extends JPanel {

    private static final int PRODUCTOS_POR_LOTE = 8;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final String nombreCategoria;
    private final List<String> subcategoriasConfiguradas;
    private final JPanel barraSubcategorias = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final JPanel panelProductos = new Componentes.PanelProductosVertical();
    private final JScrollPane scrollProductos = new JScrollPane(panelProductos);
    private SwingWorker<CategoriaDatos, Void> cargaCategoria;
    private SwingWorker<List<Productos>, Void> cargaProductos;
    private Timer temporizadorLotes;
    private int versionVista;
    private List<Productos> productosPendientes = List.of();
    private int indiceLote;

    private record CategoriaDatos(Integer id, List<String> subcategorias) { }

    public SubCategoriasPanel(String nombreCategoria, List<String> subcategoriasConfiguradas) {
        this.nombreCategoria = nombreCategoria;
        this.subcategoriasConfiguradas = List.copyOf(subcategoriasConfiguradas);
        inicializarVista();
        cargarCategoria();
    }

    private void inicializarVista() {
        setBackground(Color.WHITE);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        barraSubcategorias.setBackground(Color.WHITE);
        add(barraSubcategorias, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1810, 30));
        panelProductos.setBackground(Color.WHITE);
        scrollProductos.setBackground(Color.WHITE);
        scrollProductos.getViewport().setBackground(Color.WHITE);
        scrollProductos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollProductos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollProductos.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scrollProductos.getVerticalScrollBar().setUnitIncrement(32);
        scrollProductos.getVerticalScrollBar().setBlockIncrement(180);
        configurarRuedaVertical();
        // El área visible queda limitada; las filas restantes continúan abajo.
        add(scrollProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1810, 610));
        mostrarEstado("Cargando productos...");
    }

    private void configurarRuedaVertical() {
        Componentes.DesplazamientoSuave.instalarVertical(scrollProductos);
        Componentes.DesplazamientoSuave.instalarSobreVista(
                panelProductos,
                scrollProductos.getVerticalScrollBar(),
                false
        );
    }

    private void cargarCategoria() {
        if (cargaCategoria != null && !cargaCategoria.isDone()) cargaCategoria.cancel(true);
        final int version = ++versionVista;
        cargaCategoria = new SwingWorker<>() {
            @Override protected CategoriaDatos doInBackground() {
                Integer id = productoDAO.obtenerIdCategoriaPorNombre(nombreCategoria);
                List<String> subcategorias = id == null
                        ? subcategoriasConfiguradas
                        : productoDAO.obtenerSubcategorias(id);
                return new CategoriaDatos(id,
                        subcategorias.isEmpty() ? subcategoriasConfiguradas : subcategorias);
            }

            @Override protected void done() {
                if (isCancelled() || version != versionVista) return;
                try {
                    CategoriaDatos datos = get();
                    barraSubcategorias.removeAll();
                    crearBoton("Todos", datos.id(), null, true);
                    for (String subcategoria : datos.subcategorias()) {
                        crearBoton(subcategoria, datos.id(), subcategoria, false);
                    }
                    barraSubcategorias.revalidate();
                    barraSubcategorias.repaint();
                    if (datos.id() != null) mostrarProductos(datos.id(), null, Color.WHITE);
                    else mostrarEstado("No hay productos disponibles en esta categoría.");
                } catch (Exception ex) {
                    mostrarEstado("No se pudieron cargar las categorías.");
                }
            }
        };
        cargaCategoria.execute();
    }

    private void crearBoton(String texto, Integer idCategoria, String subcategoria,
            boolean seleccionadoInicialmente) {
        BotonLetras boton = new BotonLetras();
        boton.setText(texto);
        boton.setPreferredSize(new Dimension(160, 30));
        boton.setSeleccionado(seleccionadoInicialmente);
        boton.addActionListener(e -> {
            if (idCategoria != null) {
                mostrarProductos(idCategoria, subcategoria,
                        subcategoria == null ? Color.WHITE : colorDeSubcategoria(subcategoria));
            }
        });
        barraSubcategorias.add(boton);
    }

    private void mostrarProductos(int idCategoria, String subcategoria, Color colorFondo) {
        if (cargaProductos != null && !cargaProductos.isDone()) cargaProductos.cancel(true);
        if (temporizadorLotes != null) temporizadorLotes.stop();
        final int version = ++versionVista;
        panelProductos.setBackground(colorFondo);
        scrollProductos.getViewport().setBackground(colorFondo);
        mostrarEstado("Cargando productos...");
        cargaProductos = new SwingWorker<>() {
            @Override protected List<Productos> doInBackground() {
                return subcategoria == null
                        ? productoDAO.obtenerPorCategoria(idCategoria)
                        : productoDAO.obtenerPorCategoriaYSubcategoria(idCategoria, subcategoria);
            }

            @Override protected void done() {
                if (isCancelled() || version != versionVista) return;
                try {
                    productosPendientes = get();
                    indiceLote = 0;
                    panelProductos.removeAll();
                    panelProductos.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 15));
                    if (productosPendientes.isEmpty()) {
                        mostrarEstado("No hay productos disponibles.");
                    } else {
                        cargarSiguienteLote(version);
                    }
                } catch (Exception ex) {
                    mostrarEstado("No se pudieron cargar los productos.");
                }
            }
        };
        cargaProductos.execute();
    }

    private void cargarSiguienteLote(int version) {
        if (version != versionVista) return;
        int fin = Math.min(indiceLote + PRODUCTOS_POR_LOTE, productosPendientes.size());
        for (int i = indiceLote; i < fin; i++) {
            Productos producto = productosPendientes.get(i);
            panelProductos.add(new TarjetaProducto(producto.getIdProducto(), producto.getNombre(),
                    producto.getDescripcion(), producto.getPrecio(), producto.getImagen(),
                    producto.isDisponibleHorario()));
        }
        indiceLote = fin;
        panelProductos.revalidate();
        panelProductos.repaint();
        scrollProductos.revalidate();
        scrollProductos.repaint();
        if (indiceLote < productosPendientes.size()) {
            temporizadorLotes = new Timer(20, e -> {
                ((Timer) e.getSource()).stop();
                cargarSiguienteLote(version);
            });
            temporizadorLotes.setRepeats(false);
            temporizadorLotes.start();
        }
    }

    private void mostrarEstado(String texto) {
        panelProductos.removeAll();
        JLabel estado = new JLabel(texto, SwingConstants.CENTER);
        estado.setForeground(new Color(95, 95, 95));
        panelProductos.setLayout(new java.awt.BorderLayout());
        panelProductos.add(estado, java.awt.BorderLayout.CENTER);
        panelProductos.revalidate();
        panelProductos.repaint();
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
