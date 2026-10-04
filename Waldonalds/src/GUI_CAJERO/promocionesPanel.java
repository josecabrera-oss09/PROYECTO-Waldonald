package GUI_CAJERO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import javax.swing.SwingUtilities;

public class promocionesPanel extends javax.swing.JPanel {

    private Componentes.Scroll_Categorias
            scrollCategorias;

    public promocionesPanel() {

        initComponents();

        // NO usar setLayout(null)
        // NetBeans ya utiliza GroupLayout

        // ==========================================
        // CREAR PANEL DE CATEGORÍAS
        // ==========================================

        promoCategoriasPanel categorias =
                new promoCategoriasPanel(this::mostrarPanelPromocion);

        categorias.setPreferredSize(
                new Dimension(1900, 170)
        );

        categorias.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
        );

        // ==========================================
        // CREAR SCROLL
        // ==========================================

        scrollCategorias =
                new Componentes.Scroll_Categorias();

        scrollCategorias.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
        );

        scrollCategorias.setPanelCategorias(
                categorias
        );

        // ==========================================
        // AGREGAR SCROLL AL PANEL
        // ==========================================

        panelCategoriaPromo.removeAll();

        panelCategoriaPromo.setLayout(
                new BorderLayout()
        );

        panelCategoriaPromo.setBackground(
                Color.WHITE
        );

        panelCategoriaPromo.add(
                scrollCategorias,
                BorderLayout.CENTER
        );

        panelCategoriaPromo.revalidate();
        panelCategoriaPromo.repaint();

        // ==========================================
        // INICIAR DESDE LA IZQUIERDA
        // ==========================================

        SwingUtilities.invokeLater(() -> {

            scrollCategorias.irAlInicio();

            scrollCategorias.revalidate();
            scrollCategorias.repaint();
        });

    }

    private void mostrarPanelPromocion(javax.swing.JPanel panel) {
        panelContenidoPromo.removeAll();
        panelContenidoPromo.setLayout(new BorderLayout());
        panelContenidoPromo.add(panel, BorderLayout.CENTER);
        panelContenidoPromo.revalidate();
        panelContenidoPromo.repaint();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        panelCategoriaPromo = new javax.swing.JPanel();
        panelContenidoPromo = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("DM Sans 18pt", 1, 100)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 13, 27));
        jLabel1.setText("Promociones");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 760, 100));

        panelCategoriaPromo.setBackground(new java.awt.Color(255, 255, 255));
        add(panelCategoriaPromo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 1790, 190));

        panelContenidoPromo.setBackground(new java.awt.Color(255, 255, 255));
        add(panelContenidoPromo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 410, 1810, 660));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panelCategoriaPromo;
    private javax.swing.JPanel panelContenidoPromo;
    // End of variables declaration//GEN-END:variables
}
