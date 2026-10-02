package GUI_CAJERO;

import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JPanel;

/** Selector de promociones con el mismo patrón visual del menú principal. */
public class promoCategoriasPanel extends JPanel {

    private final Consumer<JPanel> panelDestino;
    private final JPanel panelCategorias = new JPanel();

    public promoCategoriasPanel() { this(null); }

    public promoCategoriasPanel(Consumer<JPanel> panelDestino) {
        this.panelDestino = panelDestino;
        inicializarVista();
    }

    private void inicializarVista() {
        setBackground(java.awt.Color.WHITE);
        setPreferredSize(new Dimension(1900, 170));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelCategorias.setBackground(java.awt.Color.WHITE);
        panelCategorias.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(panelCategorias, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1900, 170));

        agregarCategoria("Combos", 10, "Almuerzos", List.of("Hamburguesas", "McNuggets", "Combos", "Papas", "Bebidas"));
        agregarCategoria("Familia", 210, "Cajita Feliz", List.of("Hamburguesas", "McNuggets", "Acompañamientos", "Bebidas", "Postres"));
        agregarCategoria("Descuentos", 410, "Antojos", List.of("Papas", "McNuggets", "Snacks", "Acompañamientos", "Compartir"));
        agregarCategoria("2x1", 610, "Bebidas", List.of("Gaseosas", "Jugos", "Agua", "Café", "Bebidas frías"));
        agregarCategoria("Temporada", 810, "Postres", List.of("McFlurry", "Sundae", "Conos", "Pasteles", "Galletas"));
        agregarCategoria("Cajita Feliz", 1010, "Cajita Feliz", List.of("Hamburguesas", "McNuggets", "Acompañamientos", "Bebidas", "Postres", "Juguetes"));
        agregarCategoria("McCafé", 1210, "WlCafé", List.of("Café", "Frappés", "Chocolate", "Té", "Bebidas frías", "Repostería"));
    }

    private void agregarCategoria(String nombre, int x, String categoriaProductos, List<String> subcategorias) {
        Componentes.BotonCategoria boton = new Componentes.BotonCategoria();
        boton.setText(nombre);
        boton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        boton.setHideActionText(true);
        boton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        boton.addActionListener(e -> mostrarCategoria(categoriaProductos, subcategorias));
        panelCategorias.add(boton, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, 10, 180, 170));
    }

    private void mostrarCategoria(String nombre, List<String> subcategorias) {
        if (panelDestino != null) panelDestino.accept(new SubCategoriasPanel(nombre, subcategorias));
    }
}
