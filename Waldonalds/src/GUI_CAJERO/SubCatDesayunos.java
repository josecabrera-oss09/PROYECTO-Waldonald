package GUI_CAJERO;

public class SubCatDesayunos extends javax.swing.JPanel {

    public SubCatDesayunos() {
        initComponents();
        // 1 = id de la categoría Desayunos
        cargarProductos(1);
    }

    private void
            cargarProductos(int idCategoria) {

        jPanel1.removeAll();

        jPanel1.setLayout(
                new java.awt.FlowLayout(
                        java.awt.FlowLayout.LEFT,
                        10,
                        12
                )
        );

        DAO.ProductoDAO productoDAO
                = new DAO.ProductoDAO();

        java.util.List<Modelos.Productos> productos
                = productoDAO.obtenerPorCategoria(idCategoria);

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

        jPanel1.revalidate();
        jPanel1.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        botonLetras1 = new Componentes.BotonLetras();
        botonLetras2 = new Componentes.BotonLetras();
        botonLetras3 = new Componentes.BotonLetras();
        botonLetras4 = new Componentes.BotonLetras();
        botonLetras5 = new Componentes.BotonLetras();
        botonLetras6 = new Componentes.BotonLetras();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1810, 680));

        botonLetras1.setText("Por tiempo limitado");
        add(botonLetras1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 160, 30));

        botonLetras2.setText("Todos");
        add(botonLetras2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 30));

        botonLetras3.setText("Sándwiches");
        add(botonLetras3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 160, 30));

        botonLetras4.setText("McMuffin");
        add(botonLetras4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 160, 30));

        botonLetras5.setText("Hot Cakes");
        add(botonLetras5, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 160, 30));

        botonLetras6.setText("Bebidas");
        add(botonLetras6, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 160, 30));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonLetras botonLetras1;
    private Componentes.BotonLetras botonLetras2;
    private Componentes.BotonLetras botonLetras3;
    private Componentes.BotonLetras botonLetras4;
    private Componentes.BotonLetras botonLetras5;
    private Componentes.BotonLetras botonLetras6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
