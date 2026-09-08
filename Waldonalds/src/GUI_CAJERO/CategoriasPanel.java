package GUI_CAJERO;

public class CategoriasPanel extends javax.swing.JPanel {

    public CategoriasPanel() {

        initComponents();

        setPreferredSize(
                new java.awt.Dimension(1790, 200)
        );
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonCategoria1 = new Componentes.BotonCategoria();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonCategoria1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        botonCategoria1.setHideActionText(true);
        botonCategoria1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        add(botonCategoria1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 150, 150));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonCategoria botonCategoria1;
    // End of variables declaration//GEN-END:variables
}