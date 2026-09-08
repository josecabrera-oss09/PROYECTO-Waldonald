package GUI_CAJERO;

public class MenuCajeroPanel extends javax.swing.JPanel {
    
    private Componentes.EscaladorPanel escalador;

    public MenuCajeroPanel() {

     initComponents();

     CategoriasPanel categoriasPanel =
        new CategoriasPanel(this);

     panelCategoria.setPanelCategorias(
             categoriasPanel
     );
    
    // TAMAÑO BASE DEL DISEÑO
    setSize(1920, 1080);

    doLayout();
    setLayout(null);


    // ==========================================
    // ESCALADOR
    // ==========================================

    escalador = new Componentes.EscaladorPanel(
            this, 
            1920,
            1080
    );
}
    
    public void mostrarPanelCategoria(
        javax.swing.JPanel panel) {

    panelContenido.removeAll();

    panelContenido.setLayout(
            new java.awt.BorderLayout()
    );

    panelContenido.add(
            panel,
            java.awt.BorderLayout.CENTER
    );

    panel.setVisible(true);

    panelContenido.revalidate();
    panelContenido.repaint();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCategoria = new Componentes.Scroll_Categorias();
        jLabel1 = new javax.swing.JLabel();
        panelContenido = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(panelCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, 1810, 300));

        jLabel1.setFont(new java.awt.Font("DM Sans 18pt", 1, 110)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 13, 27));
        jLabel1.setText("Menú");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 440, 100));

        panelContenido.setBackground(new java.awt.Color(255, 255, 255));
        add(panelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 480, 1810, 600));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private Componentes.Scroll_Categorias panelCategoria;
    private javax.swing.JPanel panelContenido;
    // End of variables declaration//GEN-END:variables
}
