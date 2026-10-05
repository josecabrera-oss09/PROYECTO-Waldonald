package GUI_CAJERO;

public class MenuCajeroPanel extends javax.swing.JPanel {

    public MenuCajeroPanel() {

        initComponents();
        configurarDisenoAdaptable();

        CategoriasPanel categoriasPanel
                = new CategoriasPanel(this);

        panelCategoria.setPanelCategorias(
                categoriasPanel
        );

    }

    /**
     * Mantiene fija la cabecera visual y entrega al catálogo exactamente la
     * altura restante. Así, la barra de título no recorta un JScrollPane que
     * todavía crea disponer de 660 px.
     */
    private void configurarDisenoAdaptable() {
        removeAll();
        setLayout(new java.awt.BorderLayout());

        javax.swing.JPanel cabecera = new javax.swing.JPanel(
                new java.awt.BorderLayout(0, 10));
        cabecera.setBackground(java.awt.Color.WHITE);
        cabecera.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                20, 60, 15, 50));
        cabecera.setPreferredSize(new java.awt.Dimension(0, 350));

        jLabel1.setPreferredSize(new java.awt.Dimension(0, 105));
        panelCategoria.setPreferredSize(new java.awt.Dimension(0, 200));
        cabecera.add(jLabel1, java.awt.BorderLayout.NORTH);
        cabecera.add(panelCategoria, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel areaProductos = new javax.swing.JPanel(
                new java.awt.BorderLayout());
        areaProductos.setBackground(java.awt.Color.WHITE);
        areaProductos.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                0, 60, 0, 50));
        areaProductos.add(panelContenido, java.awt.BorderLayout.CENTER);

        add(cabecera, java.awt.BorderLayout.NORTH);
        add(areaProductos, java.awt.BorderLayout.CENTER);
    }

    public void mostrarPanelCategoria(javax.swing.JPanel panel) {
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

    private javax.swing.JPanel jPanel1;

    private void cargarProductos(int idCategoria) {

        // Eliminar productos anteriores
        jPanel1.removeAll();

        // 4 productos por cada fila
        jPanel1.setLayout(
             new java.awt.FlowLayout(
                     java.awt.FlowLayout.LEFT,
                     15,
                     15
             )
     );

        // Consultar base de datos
        DAO.ProductoDAO productoDAO
                = new DAO.ProductoDAO();

        java.util.List<Modelos.Productos> productos
                = productoDAO.obtenerPorCategoria(
                        idCategoria
                );

        // Crear una tarjeta por cada producto
        for (Modelos.Productos producto : productos) {

            Componentes.TarjetaProducto tarjeta
                    = new Componentes.TarjetaProducto(
                            producto.getIdProducto(),
                            producto.getNombre(),
                            producto.getDescripcion(),
                            producto.getPrecio(),
                            producto.getImagen(),
                            producto.isDisponibleHorario()
                    );

            jPanel1.add(tarjeta);
        }

        // Actualizar el panel
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCategoria = new Componentes.Scroll_Categorias();
        jLabel1 = new javax.swing.JLabel();
        panelContenido = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(panelCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 1810, 250));

        jLabel1.setFont(new java.awt.Font("DM Sans 18pt", 1, 110)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 13, 27));
        jLabel1.setText("Menú");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 310, 100));

        panelContenido.setBackground(new java.awt.Color(255, 255, 255));
        add(panelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 410, 1810, 660));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private Componentes.Scroll_Categorias panelCategoria;
    private javax.swing.JPanel panelContenido;
    // End of variables declaration//GEN-END:variables
}
