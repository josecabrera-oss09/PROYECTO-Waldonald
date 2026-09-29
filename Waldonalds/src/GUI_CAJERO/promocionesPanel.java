package GUI_CAJERO;

public class promocionesPanel extends javax.swing.JPanel {
    private Componentes.EscaladorPanel escalador;

    public promocionesPanel() {

    initComponents();

    // TAMAÑO BASE DEL DISEÑO
    setSize(1920, 1080);

    doLayout();
    setLayout(null);

    // ==========================================
    // CARGAR CATEGORÍAS DE PROMOCIONES
    // ==========================================

    promoCategoriasPanel categorias = new promoCategoriasPanel();

    // Crear el Scroll_Categorias
    Componentes.Scroll_Categorias scrollCategorias
            = new Componentes.Scroll_Categorias();

    // Meter promoCategoriasPanel dentro del scroll
    scrollCategorias.setPanelCategorias(
            categorias
    );

    // Limpiar el panel contenedor
    panelCategoriaPromo.removeAll();

    // Usar BorderLayout
    panelCategoriaPromo.setLayout(
            new java.awt.BorderLayout()
    );

    // Meter el scroll dentro de panelCategoriaPromo
    panelCategoriaPromo.add(
            scrollCategorias,
            java.awt.BorderLayout.CENTER
    );

    // Actualizar
    panelCategoriaPromo.revalidate();
    panelCategoriaPromo.repaint();
    
    panelCategoriaPromo.add(
        scrollCategorias,
        java.awt.BorderLayout.CENTER
);

panelCategoriaPromo.revalidate();
panelCategoriaPromo.repaint();

    // ==========================================
    // FORZAR SCROLL AL INICIO (IZQUIERDA)
    // ==========================================

    javax.swing.SwingUtilities.invokeLater(() -> {

        scrollCategorias.getHorizontalScrollBar().setValue(
                scrollCategorias.getHorizontalScrollBar().getMinimum()
        );

        scrollCategorias.getViewport().setViewPosition(
                new java.awt.Point(0, 0)
        );
    });
    // ==========================================
    // ESCALADOR
    // ==========================================

    escalador = new Componentes.EscaladorPanel(
            this,
            1920,
            1080
    );
}
    
public void mostrarContenidoPromo(javax.swing.JPanel panel) {

    panelContenidoPromo.removeAll();
    panelContenidoPromo.setLayout(new java.awt.BorderLayout());

    panelContenidoPromo.add(
            panel,
            java.awt.BorderLayout.CENTER
    );

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
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 30, 760, 100));

        panelCategoriaPromo.setBackground(new java.awt.Color(255, 255, 255));
        add(panelCategoriaPromo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 1810, 250));

        panelContenidoPromo.setBackground(new java.awt.Color(255, 255, 255));
        add(panelContenidoPromo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 410, 1810, 660));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panelCategoriaPromo;
    private javax.swing.JPanel panelContenidoPromo;
    // End of variables declaration//GEN-END:variables
}
