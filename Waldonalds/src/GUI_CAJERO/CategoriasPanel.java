package GUI_CAJERO;

public class CategoriasPanel extends javax.swing.JPanel {
    private MenuCajeroPanel menuCajeroPanel;
    
    public CategoriasPanel() {

        initComponents();

        setPreferredSize(
                new java.awt.Dimension(1790, 200)
        );
    }
    
    public CategoriasPanel(
            MenuCajeroPanel menuCajeroPanel) {

        initComponents();

        this.menuCajeroPanel =
                menuCajeroPanel;

        setPreferredSize(
                new java.awt.Dimension(
                        2400,
                        170
                )
        );
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnDesayuno = new Componentes.BotonCategoria();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnDesayuno.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno.setHideActionText(true);
        btnDesayuno.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayunoActionPerformed(evt);
            }
        });
        add(btnDesayuno, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, 150));
    }// </editor-fold>//GEN-END:initComponents

    private void btnDesayunoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayunoActionPerformed
    menuCajeroPanel.mostrarPanelCategoria(
                new SubCatDesayunos()
        );
    }//GEN-LAST:event_btnDesayunoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonCategoria btnDesayuno;
    // End of variables declaration//GEN-END:variables
}