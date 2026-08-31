package GUI_CAJERO;

public class MenuCajeroPanel extends javax.swing.JPanel {
    
    private Componentes.EscaladorPanel escalador;

    public MenuCajeroPanel() {

    initComponents();

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll_Categorias1 = new Componentes.Scroll_Categorias();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(scroll_Categorias1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, 1790, -1));

        jLabel1.setFont(new java.awt.Font("DM Sans 18pt", 1, 110)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 13, 27));
        jLabel1.setText("Menú");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 390, 100));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private Componentes.Scroll_Categorias scroll_Categorias1;
    // End of variables declaration//GEN-END:variables
}
