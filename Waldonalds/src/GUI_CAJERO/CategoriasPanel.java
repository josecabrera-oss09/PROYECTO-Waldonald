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

        this.menuCajeroPanel
                = menuCajeroPanel;

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

        btnCajita = new Componentes.BotonCategoria();
        btnDesayuno1 = new Componentes.BotonCategoria();
        btnAlmuerzos = new Componentes.BotonCategoria();
        btnPostres = new Componentes.BotonCategoria();
        btnCafe = new Componentes.BotonCategoria();
        btnBebidos = new Componentes.BotonCategoria();
        btnAntojos = new Componentes.BotonCategoria();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCajita.setText("Cajita Feliz");
        btnCajita.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnCajita.setHideActionText(true);
        btnCajita.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCajita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCajitaActionPerformed(evt);
            }
        });
        add(btnCajita, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 10, 180, 170));

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

        btnAlmuerzos.setText("Almuerzos");
        btnAlmuerzos.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnAlmuerzos.setHideActionText(true);
        btnAlmuerzos.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAlmuerzos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlmuerzosActionPerformed(evt);
            }
        });
        add(btnAlmuerzos, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 180, 170));

        btnPostres.setText("Postres");
        btnPostres.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnPostres.setHideActionText(true);
        btnPostres.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnPostres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPostresActionPerformed(evt);
            }
        });
        add(btnPostres, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 180, 170));

        btnCafe.setText("McCafé");
        btnCafe.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnCafe.setHideActionText(true);
        btnCafe.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCafe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCafeActionPerformed(evt);
            }
        });
        add(btnCafe, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 180, 170));

        btnBebidos.setText("Bebidas");
        btnBebidos.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnBebidos.setHideActionText(true);
        btnBebidos.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnBebidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBebidosActionPerformed(evt);
            }
        });
        add(btnBebidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 10, 180, 170));

        btnAntojos.setText("Antojos");
        btnAntojos.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnAntojos.setHideActionText(true);
        btnAntojos.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAntojos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAntojosActionPerformed(evt);
            }
        });
        add(btnAntojos, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 10, 180, 170));
    }// </editor-fold>//GEN-END:initComponents

    private void btnCajitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajitaActionPerformed
        mostrarCategoriaDinamica("Cajita Feliz", java.util.List.of(
                "Hamburguesas", "McNuggets", "Acompañamientos",
                "Bebidas", "Postres", "Juguetes"));
    }//GEN-LAST:event_btnCajitaActionPerformed

    private void btnDesayuno1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesayuno1ActionPerformed
        mostrarCategoriaDinamica("Desayunos", java.util.List.of(
                "Sándwiches", "McMuffin", "Hot Cakes", "Bebidas",
                "Por tiempo limitado"));
    }//GEN-LAST:event_btnDesayuno1ActionPerformed

    private void btnAlmuerzosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlmuerzosActionPerformed
        mostrarCategoriaDinamica("Almuerzos", java.util.List.of(
                "Hamburguesas", "McNuggets", "Combos", "Papas",
                "Ensaladas", "Bebidas"));
    }//GEN-LAST:event_btnAlmuerzosActionPerformed

    private void btnPostresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPostresActionPerformed
        mostrarCategoriaDinamica("Postres", java.util.List.of(
                "McFlurry", "Sundae", "Conos", "Pasteles", "Galletas",
                "Por tiempo limitado"));
    }//GEN-LAST:event_btnPostresActionPerformed

    private void btnCafeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCafeActionPerformed
        mostrarCategoriaDinamica("McCafé", java.util.List.of(
                "Café", "Frappés", "Chocolate", "Té", "Bebidas frías",
                "Repostería"));
    }//GEN-LAST:event_btnCafeActionPerformed

    private void btnBebidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBebidosActionPerformed
        mostrarCategoriaDinamica("Bebidas", java.util.List.of(
                "Gaseosas", "Jugos", "Agua", "Café", "Bebidas frías",
                "Batidos"));
    }//GEN-LAST:event_btnBebidosActionPerformed

    private void btnAntojosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAntojosActionPerformed
        mostrarCategoriaDinamica("Antojos", java.util.List.of(
                "Papas", "McNuggets", "Snacks", "Acompañamientos",
                "Compartir"));
    }//GEN-LAST:event_btnAntojosActionPerformed

    private void mostrarCategoriaDinamica(
            String nombreCategoria,
            java.util.List<String> subcategorias) {
        if (menuCajeroPanel != null) {
            menuCajeroPanel.mostrarPanelCategoria(
                    new SubCategoriasPanel(nombreCategoria, subcategorias)
            );
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonCategoria btnAlmuerzos;
    private Componentes.BotonCategoria btnAntojos;
    private Componentes.BotonCategoria btnBebidos;
    private Componentes.BotonCategoria btnCafe;
    private Componentes.BotonCategoria btnCajita;
    private Componentes.BotonCategoria btnDesayuno1;
    private Componentes.BotonCategoria btnPostres;
    // End of variables declaration//GEN-END:variables
}
