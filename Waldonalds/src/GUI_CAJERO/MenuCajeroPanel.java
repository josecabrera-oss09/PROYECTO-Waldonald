package GUI_CAJERO;

public class MenuCajeroPanel extends javax.swing.JPanel {

    private Componentes.EscaladorPanel escalador;

    public MenuCajeroPanel() {

        initComponents();

        CategoriasPanel categoriasPanel
                = new CategoriasPanel(this);

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
                            producto.getImagen()
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
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 30, 310, 100));

        panelContenido.setBackground(new java.awt.Color(255, 255, 255));
        add(panelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 410, 1810, 660));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private Componentes.Scroll_Categorias panelCategoria;
    private javax.swing.JPanel panelContenido;
    // End of variables declaration//GEN-END:variables
}
