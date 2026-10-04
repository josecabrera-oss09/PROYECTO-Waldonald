package GUI_ADMINISTRADOR;

import Componentes.BotonRedondeado;
import Utilidades.TemaAdmin;
import java.awt.CardLayout;
import java.awt.Color;

/** Módulo único de inventario con vistas internas. */
@SuppressWarnings({"serial", "this-escape"})
public class InventarioPanel extends javax.swing.JPanel {

    private static final String EXISTENCIAS = "existencias";
    private static final String INGREDIENTES = "ingredientes";
    private static final String MOVIMIENTOS = "movimientos";
    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color BORDE = new Color(225, 230, 237);

    private final TemaAdmin tema = new TemaAdmin();
    private CardLayout navegador;
    private BotonRedondeado botonActivo;
    private final GestionInventarioPanel existencias =
            new GestionInventarioPanel();
    private final CatalogoIngredientesPanel ingredientes =
            new CatalogoIngredientesPanel();
    private final MovimientosInventarioPanel movimientos =
            new MovimientosInventarioPanel();

    public InventarioPanel() {
        initComponents();
        configurarVistas();
        configurarBotones();
        mostrar(EXISTENCIAS, botonExistencias);
    }

    private void configurarVistas() {
        navegador = new CardLayout();
        panelVistas.removeAll();
        panelVistas.setLayout(navegador);
        panelVistas.add(existencias, EXISTENCIAS);
        panelVistas.add(ingredientes, INGREDIENTES);
        panelVistas.add(movimientos, MOVIMIENTOS);
        panelCapas.setLayer(panelVistas, javax.swing.JLayeredPane.DEFAULT_LAYER);
        panelCapas.setLayer(botonExistencias, javax.swing.JLayeredPane.PALETTE_LAYER);
        panelCapas.setLayer(botonIngredientes, javax.swing.JLayeredPane.PALETTE_LAYER);
        panelCapas.setLayer(botonMovimientos, javax.swing.JLayeredPane.PALETTE_LAYER);
        traerPestanasAlFrente();
    }

    private void traerPestanasAlFrente() {
        // Las vistas viven en la capa base y la navegación en una capa
        // superior, por lo que cambiar de CardLayout no puede cubrirla.
        panelCapas.moveToFront(botonExistencias);
        panelCapas.moveToFront(botonIngredientes);
        panelCapas.moveToFront(botonMovimientos);
        botonExistencias.setVisible(true);
        botonIngredientes.setVisible(true);
        botonMovimientos.setVisible(true);
    }

    private void configurarBotones() {
        for (BotonRedondeado boton : new BotonRedondeado[]{
            botonExistencias, botonIngredientes, botonMovimientos}) {
            boton.setFont(tema.negrita(14f));
            boton.setDegradado(false);
            boton.setRadio(12);
            boton.setGrosorBorde(1f);
            estilizar(boton, false);
        }
        botonExistencias.addActionListener(e -> mostrar(EXISTENCIAS, botonExistencias));
        botonIngredientes.addActionListener(e -> mostrar(INGREDIENTES, botonIngredientes));
        botonMovimientos.addActionListener(e -> mostrar(MOVIMIENTOS, botonMovimientos));
    }

    private void mostrar(String vista, BotonRedondeado seleccionado) {
        if (botonActivo != null) estilizar(botonActivo, false);
        estilizar(seleccionado, true);
        botonActivo = seleccionado;
        if (INGREDIENTES.equals(vista)) ingredientes.recargarDatos();
        if (MOVIMIENTOS.equals(vista)) movimientos.recargarDatos();
        navegador.show(panelVistas, vista);
        traerPestanasAlFrente();
        panelCapas.revalidate();
        panelCapas.repaint();
    }

    private void estilizar(BotonRedondeado boton, boolean seleccionado) {
        boton.setColorInicio(seleccionado ? AMARILLO : Color.WHITE);
        boton.setColorFinal(seleccionado ? AMARILLO : Color.WHITE);
        boton.setColorBorde(seleccionado ? AMARILLO : BORDE);
        boton.setForeground(AZUL);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCapas = new javax.swing.JLayeredPane();
        panelVistas = new javax.swing.JPanel();
        botonExistencias = new Componentes.BotonRedondeado();
        botonIngredientes = new Componentes.BotonRedondeado();
        botonMovimientos = new Componentes.BotonRedondeado();

        setBackground(new java.awt.Color(248, 249, 251));
        setPreferredSize(new java.awt.Dimension(1530, 932));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCapas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelVistas.setBackground(new java.awt.Color(248, 249, 251));
        panelVistas.setLayout(new java.awt.CardLayout());
        panelCapas.add(panelVistas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1530, 932));

        botonExistencias.setText("Existencias");
        panelCapas.add(botonExistencias, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 190, 42));

        botonIngredientes.setText("Ingredientes");
        panelCapas.add(botonIngredientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 120, 190, 42));

        botonMovimientos.setText("Movimientos");
        panelCapas.add(botonMovimientos, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, 190, 42));

        add(panelCapas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1530, 932));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonRedondeado botonExistencias;
    private Componentes.BotonRedondeado botonIngredientes;
    private Componentes.BotonRedondeado botonMovimientos;
    private javax.swing.JLayeredPane panelCapas;
    private javax.swing.JPanel panelVistas;
    // End of variables declaration//GEN-END:variables
}
