package GUI_CAJERO;

public class promoCategoriasPanel extends javax.swing.JPanel {

    public promoCategoriasPanel() {
        initComponents();
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        botonCategoria1 = new Componentes.BotonCategoria();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonCategoria1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        botonCategoria1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel1.add(botonCategoria1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 180, 170));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1790, 190));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonCategoria botonCategoria1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
