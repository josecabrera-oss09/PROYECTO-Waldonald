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
                        1900,
                        170
                )
        );
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnDesayuno = new Componentes.BotonCategoria();
        btnDesayuno1 = new Componentes.BotonCategoria();
        btnDesayuno2 = new Componentes.BotonCategoria();
        btnDesayuno3 = new Componentes.BotonCategoria();
        btnDesayuno4 = new Componentes.BotonCategoria();
        btnDesayuno5 = new Componentes.BotonCategoria();
        btnDesayuno6 = new Componentes.BotonCategoria();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnDesayuno.setText("Cajita Feliz");
        btnDesayuno.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno.setHideActionText(true);
        btnDesayuno.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayunoActionPerformed(evt);
            }
        });
        add(btnDesayuno, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 10, 180, 170));

        btnDesayuno1.setText("Desayuno");
        btnDesayuno1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno1.setHideActionText(true);
        btnDesayuno1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayuno1ActionPerformed(evt);
            }
        });
        add(btnDesayuno1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 180, 170));

        btnDesayuno2.setText("Almuerzos");
        btnDesayuno2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno2.setHideActionText(true);
        btnDesayuno2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayuno2ActionPerformed(evt);
            }
        });
        add(btnDesayuno2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 180, 170));

        btnDesayuno3.setText("Postres");
        btnDesayuno3.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno3.setHideActionText(true);
        btnDesayuno3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayuno3ActionPerformed(evt);
            }
        });
        add(btnDesayuno3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 180, 170));

        btnDesayuno4.setText("McCafé");
        btnDesayuno4.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno4.setHideActionText(true);
        btnDesayuno4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayuno4ActionPerformed(evt);
            }
        });
        add(btnDesayuno4, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 180, 170));

        btnDesayuno5.setText("Bebidas");
        btnDesayuno5.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno5.setHideActionText(true);
        btnDesayuno5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayuno5ActionPerformed(evt);
            }
        });
        add(btnDesayuno5, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 10, 180, 170));

        btnDesayuno6.setText("Antojos");
        btnDesayuno6.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDesayuno6.setHideActionText(true);
        btnDesayuno6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDesayuno6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesayuno6ActionPerformed(evt);
            }
        });
        add(btnDesayuno6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 10, 180, 170));
    }// </editor-fold>//GEN-END:initComponents

    private void btnDesayunoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayunoActionPerformed

    }//GEN-LAST:event_btnDesayunoActionPerformed

    private void btnDesayuno1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno1ActionPerformed
        menuCajeroPanel.mostrarPanelCategoria(
                new SubCatDesayunos()
        );
    }//GEN-LAST:event_btnDesayuno1ActionPerformed

    private void btnDesayuno2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDesayuno2ActionPerformed

    private void btnDesayuno3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDesayuno3ActionPerformed

    private void btnDesayuno4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDesayuno4ActionPerformed

    private void btnDesayuno5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDesayuno5ActionPerformed

    private void btnDesayuno6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDesayuno6ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonCategoria btnDesayuno;
    private Componentes.BotonCategoria btnDesayuno1;
    private Componentes.BotonCategoria btnDesayuno2;
    private Componentes.BotonCategoria btnDesayuno3;
    private Componentes.BotonCategoria btnDesayuno4;
    private Componentes.BotonCategoria btnDesayuno5;
    private Componentes.BotonCategoria btnDesayuno6;
    // End of variables declaration//GEN-END:variables
}