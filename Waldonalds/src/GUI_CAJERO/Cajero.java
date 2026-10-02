/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI_CAJERO;

/**
 *
 * @author Computacion
 */
public class Cajero extends javax.swing.JFrame {
    // =====================================================
// COLORES
// =====================================================

private final java.awt.Color AZUL_BARRA =
        new java.awt.Color(1, 20, 36);

private final java.awt.Color AMARILLO =
        new java.awt.Color(255, 188, 13);

private final java.awt.Color AMARILLO_HOVER_SUAVE =
        new java.awt.Color(255, 196, 28);

private final java.awt.Color GRIS_TEXTO =
        new java.awt.Color(110, 110, 110);


// =====================================================
// CARD LAYOUT
// =====================================================

private java.awt.CardLayout cardLayout;

private javax.swing.JPanel panelContenido;

private MenuCajeroPanel menuCajeroPanel;

private promocionesPanel promocionesPanel;



// =====================================================
// ZONA CENTRAL Y PEDIDO
// =====================================================

private javax.swing.JPanel zonaCentral;
private javax.swing.JPanel panelPedido;
private boolean configurandoProducto;


// =====================================================
// BOTONES
// =====================================================

private javax.swing.JButton btnMenu;
private javax.swing.JButton btnPromociones;
private javax.swing.JButton btnOrdenar;

private javax.swing.JButton botonActivo;


// =====================================================
// CONSTRUCTOR
// =====================================================

public Cajero() {

    initComponents();

    configurarInterfaz();

    setExtendedState(
            javax.swing.JFrame.MAXIMIZED_BOTH
    );

    setLocationRelativeTo(null);
}


// =====================================================
// ESCALA
// =====================================================

private double obtenerEscala() {

    java.awt.Dimension pantalla =
            java.awt.Toolkit.getDefaultToolkit().getScreenSize();

    double escalaX =
            pantalla.getWidth() / 1360.0;

    double escalaY =
            pantalla.getHeight() / 720.0;

    return Math.min(
            escalaX,
            escalaY
    );
}


private int escalar(int valor) {

    return Math.max(
            1,
            (int) Math.round(
                    valor * obtenerEscala()
            )
    );
}


private int escalarFuente(int valor) {

    double escala = obtenerEscala();

    // Evitar letras exageradamente grandes
    double escalaFuente =
            Math.min(escala, 1.20);

    return Math.max(
            10,
            (int) Math.round(
                    valor * escalaFuente
            )
    );
}


// =====================================================
// CONFIGURAR INTERFAZ PRINCIPAL
// =====================================================

private void configurarInterfaz() {

    // Limpiar JFrame
    getContentPane().removeAll();

    getContentPane().setLayout(
            new java.awt.BorderLayout()
    );

    getContentPane().add(
            jPanel1,
            java.awt.BorderLayout.CENTER
    );


    // Panel principal
    jPanel1.removeAll();

    jPanel1.setLayout(
            new java.awt.BorderLayout()
    );


    // =================================================
    // CARD LAYOUT
    // =================================================

    cardLayout =
            new java.awt.CardLayout();

    panelContenido =
            new javax.swing.JPanel(cardLayout);

    panelContenido.setBackground(
            new java.awt.Color(
                    252,
                    252,
                    253
            )
    );


    // =================================================
    // CREAR LOS JPanel Form
    // =================================================

    menuCajeroPanel =
            new MenuCajeroPanel();
    
    promocionesPanel =
            new promocionesPanel();



    // =================================================
    // AGREGAR AL CARD LAYOUT
    // =================================================

    panelContenido.add(
            menuCajeroPanel,
            "MENU"
    );
    
    panelContenido.add(
            promocionesPanel,
            "PROMOCIONES"
    );

    // =================================================
    // CREAR PEDIDO
    // =================================================

    crearPanelPedido();


    // =================================================
    // ZONA CENTRAL
    // =================================================

    zonaCentral =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );

    zonaCentral.setBackground(
            new java.awt.Color(
                    252,
                    252,
                    253
            )
    );


    // Los JPanel Form van en el centro
    zonaCentral.add(
            panelContenido,
            java.awt.BorderLayout.CENTER
    );


    // El pedido va a la derecha
    zonaCentral.add(
            panelPedido,
            java.awt.BorderLayout.EAST
    );


    // =================================================
    // BARRA SUPERIOR
    // =================================================

    configurarBarra();


    // =================================================
    // AGREGAR AL JFrame
    // =================================================

    jPanel1.add(
            jPanel2,
            java.awt.BorderLayout.NORTH
    );

    jPanel1.add(
            zonaCentral,
            java.awt.BorderLayout.CENTER
    );


    // =================================================
    // MENÚ AL INICIAR
    // =================================================

    mostrarPanel("MENU");

    seleccionarBoton(
            btnMenu
    );


    jPanel1.revalidate();
    jPanel1.repaint();
}


// =====================================================
// MOSTRAR UN PANEL
// =====================================================

private void mostrarPanel(
        String nombrePanel) {

    cardLayout.show(
            panelContenido,
            nombrePanel
    );

    panelContenido.revalidate();
    panelContenido.repaint();
}


// =====================================================
// BARRA SUPERIOR
// =====================================================

private void configurarBarra() {

    jPanel2.removeAll();

    jPanel2.setBackground(
            AZUL_BARRA
    );

    jPanel2.setPreferredSize(
            new java.awt.Dimension(
                    0,
                    escalar(80)
            )
    );

    jPanel2.setLayout(
            new java.awt.BorderLayout()
    );


    // =================================================
    // IZQUIERDA
    // =================================================

    javax.swing.JPanel panelIzquierdo =
            new javax.swing.JPanel(
                    new java.awt.FlowLayout(
                            java.awt.FlowLayout.LEFT,
                            escalar(18),
                            escalar(10)
                    )
            );

    panelIzquierdo.setOpaque(false);


    // =================================================
    // ESPACIO PARA LOGO
    // =================================================

    javax.swing.JPanel espacioLogo =
            new javax.swing.JPanel();

    espacioLogo.setOpaque(false);

    espacioLogo.setPreferredSize(
            new java.awt.Dimension(
                    escalar(95),
                    escalar(60)
            )
    );

    panelIzquierdo.add(
            espacioLogo
    );


    // =================================================
    // BOTONES
    // =================================================

    btnMenu =
            crearBotonBarra(
                    "Menú"
            );

    btnPromociones =
            crearBotonBarra(
                    "Promociones"
            );

    panelIzquierdo.add(
            btnMenu
    );

    panelIzquierdo.add(
            btnPromociones
    );


    // =================================================
    // ORDENAR
    // =================================================

    btnOrdenar = new Componentes.BotonDerretido();
    btnOrdenar.setText("Ordenar");


    btnOrdenar.setPreferredSize(
            new java.awt.Dimension(
                    escalar(180),
                    escalar(60)
            )
    );


    btnOrdenar.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    escalarFuente(16)
            )
    );


    btnOrdenar.setForeground(
            java.awt.Color.BLACK
    );

    btnOrdenar.setOpaque(false);

    btnOrdenar.setContentAreaFilled(false);

    btnOrdenar.setBorderPainted(false);

    btnOrdenar.setFocusPainted(false);

    // Conserva la posición del texto del botón Ordenar original.
    btnOrdenar.setHorizontalAlignment(
            javax.swing.SwingConstants.RIGHT
    );
    btnOrdenar.setVerticalAlignment(
            javax.swing.SwingConstants.CENTER
    );

    btnOrdenar.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(
                    0,
                    escalar(45),
                    escalar(14),
                    escalar(20)
            )
    );


    btnOrdenar.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );


    // =================================================
    // DERECHA
    // =================================================

    javax.swing.JPanel panelDerecho =
            new javax.swing.JPanel(
                    new java.awt.FlowLayout(
                            java.awt.FlowLayout.RIGHT,
                            escalar(25),
                            escalar(15)
                    )
            );

    panelDerecho.setOpaque(false);

    panelDerecho.add(
            btnOrdenar
    );


    // =================================================
    // AGREGAR A BARRA
    // =================================================

    jPanel2.add(
            panelIzquierdo,
            java.awt.BorderLayout.WEST
    );

    jPanel2.add(
            panelDerecho,
            java.awt.BorderLayout.EAST
    );


    // =================================================
    // MENÚ
    // =================================================

    btnMenu.addActionListener(e -> {

        mostrarPanel(
                "MENU"
        );

        seleccionarBoton(
                btnMenu
        );
    });


    // =================================================
    // PROMOCIONES
    // =================================================

    btnPromociones.addActionListener(e -> {

        mostrarPanel(
                "PROMOCIONES"
        );

        seleccionarBoton(
                btnPromociones
        );
    });

    // =================================================
    // ORDENAR
    // =================================================

    btnOrdenar.addActionListener(e -> {

        abrirPedido();

    });


    jPanel2.revalidate();
    jPanel2.repaint();
}


// =====================================================
// CREAR BOTONES DE LA BARRA
// =====================================================

private javax.swing.JButton crearBotonBarra(
        String texto) {

    javax.swing.JButton boton =
            new javax.swing.JButton(
                    texto
            ) {

        @Override
        protected void paintComponent(
                java.awt.Graphics g) {

            super.paintComponent(g);


            // Línea amarilla
            if (this == botonActivo) {

                java.awt.Graphics2D g2 =
                        (java.awt.Graphics2D)
                                g.create();

                g2.setRenderingHint(
                        java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        AMARILLO
                );

                int anchoLinea =
                        escalar(35);

                int altoLinea =
                        Math.max(
                                3,
                                escalar(3)
                        );

                int x =
                        (
                                getWidth()
                                - anchoLinea
                        ) / 2;

                int y =
                        getHeight()
                                - escalar(6);

                g2.fillRoundRect(
                        x,
                        y,
                        anchoLinea,
                        altoLinea,
                        altoLinea,
                        altoLinea
                );

                g2.dispose();
            }
        }
    };


    // =================================================
    // FUENTE
    // =================================================

    java.awt.Font fuente =
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    escalarFuente(14)
            );

    boton.setFont(
            fuente
    );


    // =================================================
    // ANCHO SEGÚN TEXTO
    // =================================================

    java.awt.FontMetrics fm =
            boton.getFontMetrics(
                    fuente
            );

    int anchoTexto =
            fm.stringWidth(
                    texto
            );

    int anchoBoton =
            anchoTexto
                    + escalar(45);


    boton.setPreferredSize(
            new java.awt.Dimension(
                    anchoBoton,
                    escalar(55)
            )
    );


    boton.setMinimumSize(
            new java.awt.Dimension(
                    anchoBoton,
                    escalar(55)
            )
    );


    // =================================================
    // ESTILO
    // =================================================

    boton.setForeground(
            java.awt.Color.WHITE
    );

    boton.setOpaque(false);

    boton.setContentAreaFilled(
            false
    );

    boton.setBorderPainted(
            false
    );

    boton.setFocusPainted(
            false
    );


    boton.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );


    // =================================================
    // EFECTO MOUSE
    // =================================================

    boton.addMouseListener(
            new java.awt.event.MouseAdapter() {

        @Override
        public void mouseEntered(
                java.awt.event.MouseEvent e) {

            if (boton != botonActivo) {

                boton.setForeground(
                        new java.awt.Color(
                                220,
                                220,
                                220
                        )
                );
            }
        }


        @Override
        public void mouseExited(
                java.awt.event.MouseEvent e) {

            boton.setForeground(
                    java.awt.Color.WHITE
            );
        }
    });


    return boton;
}


// =====================================================
// BOTÓN ACTIVO
// =====================================================

private void seleccionarBoton(
        javax.swing.JButton boton) {

    botonActivo =
            boton;


    if (btnMenu != null) {

        btnMenu.setForeground(
                java.awt.Color.WHITE
        );

        btnMenu.repaint();
    }


    if (btnPromociones != null) {

        btnPromociones.setForeground(
                java.awt.Color.WHITE
        );

        btnPromociones.repaint();
    }
}


// =====================================================
// ABRIR PEDIDO
// =====================================================

private void abrirPedido() {

    panelPedido.setVisible(
            true
    );

    zonaCentral.revalidate();
    zonaCentral.repaint();
}


// =====================================================
// CERRAR PEDIDO
// =====================================================

private void cerrarPedido() {

    panelPedido.setVisible(
            false
    );

    zonaCentral.revalidate();
    zonaCentral.repaint();
}


// =====================================================
// CREAR PANEL PEDIDO
// =====================================================

private void crearPanelPedido() {
    panelPedido = new PedidoPanel();
    panelPedido.setPreferredSize(new java.awt.Dimension(Math.max(370, escalar(390)), 0));
    panelPedido.setVisible(false);
}

public void agregarProducto(int idProducto) {
    if (configurandoProducto) return;
    configurandoProducto = true;
    setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
    new javax.swing.SwingWorker<Modelos.ConfiguracionProducto, Void>() {
        @Override protected Modelos.ConfiguracionProducto doInBackground() throws Exception {
            return new DAO.ConfiguracionMenuDAO().cargarProducto(idProducto);
        }
        @Override protected void done() {
            setCursor(java.awt.Cursor.getDefaultCursor());
            try {
                Modelos.ConfiguracionProducto configuracion = get();
                abrirPedido();
                ((PedidoPanel) panelPedido).mostrarConfigurador(
                        configuracion, () -> configurandoProducto = false);
            } catch (Exception ex) {
                Throwable causa = ex instanceof java.util.concurrent.ExecutionException
                        && ex.getCause() != null ? ex.getCause() : ex;
                javax.swing.JOptionPane.showMessageDialog(Cajero.this,
                        causa.getMessage() == null ? "No se pudo configurar el producto." : causa.getMessage(),
                        "Producto no disponible", javax.swing.JOptionPane.WARNING_MESSAGE);
                configurandoProducto = false;
            }
        }
    }.execute();
}
private void configurarHoverSutilCancelar(javax.swing.JButton boton) {
    java.awt.Color bordeNormal = new java.awt.Color(190, 190, 190);
    java.awt.Color bordeHover = new java.awt.Color(170, 170, 170);

    boton.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evento) {
            boton.setForeground(new java.awt.Color(65, 65, 65));
            boton.setBorder(javax.swing.BorderFactory.createLineBorder(
                    bordeHover, Math.max(1, escalar(1))));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evento) {
            boton.setForeground(new java.awt.Color(80, 80, 80));
            boton.setBorder(javax.swing.BorderFactory.createLineBorder(
                    bordeNormal, Math.max(1, escalar(1))));
        }
    });
}

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(252, 252, 253));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(1, 15, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 80));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 720));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cajero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cajero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cajero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cajero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cajero().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
