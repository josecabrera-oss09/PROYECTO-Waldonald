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
    // ==========================================
    // COLORES DE LA INTERFAZ
    // ==========================================

    private final java.awt.Color AZUL_BARRA =
            new java.awt.Color(1, 20, 36);

    private final java.awt.Color AMARILLO =
            new java.awt.Color(255, 188, 13);

    private final java.awt.Color GRIS_TEXTO =
            new java.awt.Color(110, 110, 110);


    // ==========================================
    // BOTÓN ACTUALMENTE SELECCIONADO
    // ==========================================

    private javax.swing.JButton botonActivo;
/**
     * Creates new form Menu
     */
    private java.awt.CardLayout cardLayout;

    private javax.swing.JPanel panelContenido;

    private javax.swing.JPanel panelMenu;
    private javax.swing.JPanel panelPromociones;
    private javax.swing.JPanel panelNovedades;
    private javax.swing.JPanel panelAlergenos;
    private javax.swing.JPanel panelInformacion;

    private javax.swing.JPanel panelPedido;

    private javax.swing.JButton btnMenu;
    private javax.swing.JButton btnPromociones;
    private javax.swing.JButton btnNovedades;
    private javax.swing.JButton btnAlergenos;
    private javax.swing.JButton btnInformacion;
    private javax.swing.JButton btnOrdenar;
    private Componentes.EscaladorPantalla escalador;
    
    public Cajero() {
        
    initComponents();
    
    configurarInterfaz();
    
    // Resolución en la que diseñaste el JFrame
    escalador = new Componentes.EscaladorPantalla(
            getContentPane(),
            1360,
            720
    );
    
    // Maximizar ventana
    setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    
    setLocationRelativeTo(null);

    // Escalar todos los componentes
    escalador.escalar(getContentPane());
}   

    private void configurarInterfaz() {

    // =====================================================
    // HACER QUE EL PANEL PRINCIPAL SE ADAPTE A LA PANTALLA
    // =====================================================

    getContentPane().removeAll();
    getContentPane().setLayout(new java.awt.BorderLayout());

    getContentPane().add(
            jPanel1,
            java.awt.BorderLayout.CENTER
    );


    // =====================================================
    // PANEL PRINCIPAL
    // =====================================================

    jPanel1.removeAll();

    jPanel1.setLayout(
            new java.awt.BorderLayout()
    );


    // =====================================================
    // BARRA AZUL
    // =====================================================

    configurarBarra();


    // =====================================================
    // CREAR CARD LAYOUT
    // =====================================================

    cardLayout = new java.awt.CardLayout();

    panelContenido = new javax.swing.JPanel();

    panelContenido.setLayout(cardLayout);

    panelContenido.setBackground(
            new java.awt.Color(252, 252, 253)
    );


    // =====================================================
    // CREAR LAS DIFERENTES PANTALLAS
    // =====================================================

    panelMenu = crearPantalla("Menú");

    panelPromociones =
            crearPantalla("Promociones");

    panelNovedades =
            crearPantalla("Novedades");

    panelAlergenos =
            crearPantalla("Alérgenos");

    panelInformacion =
            crearPantalla("Información");


    // =====================================================
    // AGREGAR LAS PANTALLAS AL CARDLAYOUT
    // =====================================================

    panelContenido.add(
            panelMenu,
            "MENU"
    );

    panelContenido.add(
            panelPromociones,
            "PROMOCIONES"
    );

    panelContenido.add(
            panelNovedades,
            "NOVEDADES"
    );

    panelContenido.add(
            panelAlergenos,
            "ALERGENOS"
    );

    panelContenido.add(
            panelInformacion,
            "INFORMACION"
    );


    // =====================================================
    // PANEL DE PEDIDO
    // =====================================================

    crearPanelPedido();


    // =====================================================
    // ZONA CENTRAL
    // =====================================================

    javax.swing.JPanel zonaCentral =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );

    zonaCentral.add(
            panelContenido,
            java.awt.BorderLayout.CENTER
    );

    zonaCentral.add(
            panelPedido,
            java.awt.BorderLayout.EAST
    );


    // =====================================================
    // AGREGAR TODO
    // =====================================================

    jPanel1.add(
            jPanel2,
            java.awt.BorderLayout.NORTH
    );

    jPanel1.add(
            zonaCentral,
            java.awt.BorderLayout.CENTER
    );


    // =====================================================
    // MOSTRAR MENÚ AL INICIAR
    // =====================================================

    cardLayout.show(
            panelContenido,
            "MENU"
    );


    jPanel1.revalidate();
    jPanel1.repaint();
}

    private javax.swing.JPanel crearPantalla(String titulo) {

    javax.swing.JPanel panel =
            new javax.swing.JPanel();

    panel.setBackground(
            new java.awt.Color(252, 252, 253)
    );

    panel.setLayout(null);


    // =========================================
    // TÍTULO
    // =========================================

    javax.swing.JLabel lblTitulo =
            new javax.swing.JLabel(titulo);

    lblTitulo.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    60
            )
    );

    lblTitulo.setForeground(
            java.awt.Color.BLACK
    );

    lblTitulo.setBounds(
            50,
            30,
            700,
            100
    );


    panel.add(lblTitulo);

    return panel;
}
private void configurarBarra() {

    jPanel2.removeAll();

    jPanel2.setBackground(AZUL_BARRA);

    jPanel2.setPreferredSize(
            new java.awt.Dimension(0, 80)
    );

    jPanel2.setLayout(
            new java.awt.BorderLayout()
    );


    // ====================================================
    // ZONA IZQUIERDA: LOGO + BOTONES
    // ====================================================

    javax.swing.JPanel panelIzquierdo =
            new javax.swing.JPanel();

    panelIzquierdo.setOpaque(false);

    panelIzquierdo.setLayout(
            new java.awt.FlowLayout(
                    java.awt.FlowLayout.LEFT,
                    25,
                    10
            )
    );


    // ====================================================
    // ESPACIO PARA EL LOGO
    // ====================================================

    javax.swing.JLabel lblLogo =
            new javax.swing.JLabel();

    lblLogo.setPreferredSize(
            new java.awt.Dimension(75, 60)
    );

    lblLogo.setHorizontalAlignment(
            javax.swing.SwingConstants.CENTER
    );

    /*
     * CUANDO TENGAS EL LOGO:
     *
     * lblLogo.setIcon(
     *     new javax.swing.ImageIcon(
     *         getClass().getResource("/Imagenes/logo.png")
     *     )
     * );
     */

    panelIzquierdo.add(lblLogo);


    // ====================================================
    // BOTONES
    // ====================================================

    btnMenu = crearBotonBarra("Menú");
    btnPromociones = crearBotonBarra("Promociones");
    btnNovedades = crearBotonBarra("Novedades");
    btnAlergenos = crearBotonBarra("Alérgenos");
    btnInformacion = crearBotonBarra("Información");


    panelIzquierdo.add(btnMenu);
    panelIzquierdo.add(btnPromociones);
    panelIzquierdo.add(btnNovedades);
    panelIzquierdo.add(btnAlergenos);
    panelIzquierdo.add(btnInformacion);


    // ====================================================
    // BOTÓN ORDENAR
    // ====================================================

    btnOrdenar = new javax.swing.JButton("🛒   Ordenar") {

        @Override
        protected void paintComponent(java.awt.Graphics g) {

            java.awt.Graphics2D g2 =
                    (java.awt.Graphics2D) g.create();

            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(AMARILLO);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    18,
                    18
            );

            g2.dispose();

            super.paintComponent(g);
        }
    };

    btnOrdenar.setPreferredSize(
            new java.awt.Dimension(145, 48)
    );

    btnOrdenar.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    16
            )
    );

    btnOrdenar.setForeground(
            java.awt.Color.BLACK
    );

    btnOrdenar.setOpaque(false);
    btnOrdenar.setContentAreaFilled(false);
    btnOrdenar.setBorderPainted(false);
    btnOrdenar.setFocusPainted(false);

    btnOrdenar.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );


    javax.swing.JPanel panelDerecho =
            new javax.swing.JPanel(
                    new java.awt.FlowLayout(
                            java.awt.FlowLayout.RIGHT,
                            25,
                            15
                    )
            );

    panelDerecho.setOpaque(false);

    panelDerecho.add(btnOrdenar);


    // ====================================================
    // AGREGAR A LA BARRA
    // ====================================================

    jPanel2.add(
            panelIzquierdo,
            java.awt.BorderLayout.WEST
    );

    jPanel2.add(
            panelDerecho,
            java.awt.BorderLayout.EAST
    );


    // ====================================================
    // ACCIONES
    // ====================================================

    btnMenu.addActionListener(e -> {

        cardLayout.show(
                panelContenido,
                "MENU"
        );

        seleccionarBoton(btnMenu);
    });


    btnPromociones.addActionListener(e -> {

        cardLayout.show(
                panelContenido,
                "PROMOCIONES"
        );

        seleccionarBoton(btnPromociones);
    });


    btnNovedades.addActionListener(e -> {

        cardLayout.show(
                panelContenido,
                "NOVEDADES"
        );

        seleccionarBoton(btnNovedades);
    });


    btnAlergenos.addActionListener(e -> {

        cardLayout.show(
                panelContenido,
                "ALERGENOS"
        );

        seleccionarBoton(btnAlergenos);
    });


    btnInformacion.addActionListener(e -> {

        cardLayout.show(
                panelContenido,
                "INFORMACION"
        );

        seleccionarBoton(btnInformacion);
    });


    btnOrdenar.addActionListener(e -> {

        panelPedido.setVisible(
                !panelPedido.isVisible()
        );

        jPanel1.revalidate();
        jPanel1.repaint();
    });


    // Menú seleccionado al iniciar
    seleccionarBoton(btnMenu);


    jPanel2.revalidate();
    jPanel2.repaint();
}

private void seleccionarBoton(
        javax.swing.JButton boton) {

    botonActivo = boton;

    btnMenu.repaint();
    btnPromociones.repaint();
    btnNovedades.repaint();
    btnAlergenos.repaint();
    btnInformacion.repaint();
}

private javax.swing.JButton crearBotonBarra(String texto) {

    javax.swing.JButton boton =
            new javax.swing.JButton(texto);

    boton.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    14
            )
    );

    boton.setForeground(
            java.awt.Color.WHITE
    );

    boton.setContentAreaFilled(false);
    boton.setBorderPainted(false);
    boton.setFocusPainted(false);

    boton.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );

    return boton;
}

private void crearPanelPedido() {

    panelPedido = new javax.swing.JPanel() {

        @Override
        protected void paintComponent(java.awt.Graphics g) {

            java.awt.Graphics2D g2 =
                    (java.awt.Graphics2D) g.create();

            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Fondo exterior gris claro
            g2.setColor(
                    new java.awt.Color(247, 247, 247)
            );

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            // Tarjeta blanca redondeada
            g2.setColor(java.awt.Color.WHITE);

            g2.fillRoundRect(
                    15,
                    15,
                    getWidth() - 30,
                    getHeight() - 30,
                    25,
                    25
            );

            g2.dispose();
        }
    };

    panelPedido.setOpaque(false);

    panelPedido.setPreferredSize(
            new java.awt.Dimension(
                    350,
                    0
            )
    );

    /*
     * IMPORTANTE:
     * Ya no usamos null layout.
     * BorderLayout adapta todo automáticamente.
     */
    panelPedido.setLayout(
            new java.awt.BorderLayout()
    );


    // =========================================================
    // CONTENEDOR INTERIOR
    // =========================================================

    javax.swing.JPanel contenidoPedido =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );

    contenidoPedido.setOpaque(false);

    /*
     * Espacio entre los componentes y
     * los bordes de la tarjeta blanca.
     */
    contenidoPedido.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(
                    35,
                    35,
                    35,
                    35
            )
    );

    panelPedido.add(
            contenidoPedido,
            java.awt.BorderLayout.CENTER
    );


    // =========================================================
    // PARTE SUPERIOR
    // =========================================================

    javax.swing.JPanel panelSuperior =
            new javax.swing.JPanel();

    panelSuperior.setOpaque(false);

    panelSuperior.setLayout(
            new javax.swing.BoxLayout(
                    panelSuperior,
                    javax.swing.BoxLayout.Y_AXIS
            )
    );


    // Título
    javax.swing.JLabel titulo =
            new javax.swing.JLabel(
                    "Tu pedido"
            );

    titulo.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    22
            )
    );

    titulo.setForeground(
            new java.awt.Color(
                    15,
                    15,
                    15
            )
    );

    titulo.setAlignmentX(
            java.awt.Component.LEFT_ALIGNMENT
    );


    // Separador
    javax.swing.JSeparator separador =
            new javax.swing.JSeparator();

    separador.setForeground(
            new java.awt.Color(
                    210,
                    210,
                    210
            )
    );

    separador.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    2
            )
    );

    separador.setAlignmentX(
            java.awt.Component.LEFT_ALIGNMENT
    );


    panelSuperior.add(titulo);

    panelSuperior.add(
            javax.swing.Box.createVerticalStrut(20)
    );

    panelSuperior.add(separador);


    contenidoPedido.add(
            panelSuperior,
            java.awt.BorderLayout.NORTH
    );


    // =========================================================
    // CENTRO - AQUÍ IRÁN LOS PRODUCTOS
    // =========================================================

    javax.swing.JPanel panelProductos =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );

    panelProductos.setOpaque(false);


    javax.swing.JLabel lblVacio =
            new javax.swing.JLabel(
                    "Aún no has agregado productos"
            );

    lblVacio.setHorizontalAlignment(
            javax.swing.SwingConstants.CENTER
    );

    lblVacio.setVerticalAlignment(
            javax.swing.SwingConstants.TOP
    );

    lblVacio.setForeground(GRIS_TEXTO);

    lblVacio.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.PLAIN,
                    14
            )
    );

    lblVacio.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(
                    45,
                    0,
                    0,
                    0
            )
    );


    panelProductos.add(
            lblVacio,
            java.awt.BorderLayout.CENTER
    );


    contenidoPedido.add(
            panelProductos,
            java.awt.BorderLayout.CENTER
    );


    // =========================================================
    // PARTE INFERIOR
    // =========================================================

    javax.swing.JPanel panelInferior =
            new javax.swing.JPanel();

    panelInferior.setOpaque(false);

    panelInferior.setLayout(
            new javax.swing.BoxLayout(
                    panelInferior,
                    javax.swing.BoxLayout.Y_AXIS
            )
    );


    // =========================================================
    // TOTAL
    // =========================================================

    javax.swing.JPanel panelTotal =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );

    panelTotal.setOpaque(false);


    javax.swing.JLabel lblTotal =
            new javax.swing.JLabel(
                    "Total"
            );

    lblTotal.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    19
            )
    );


    javax.swing.JLabel lblPrecio =
            new javax.swing.JLabel(
                    "$0.00"
            );

    lblPrecio.setHorizontalAlignment(
            javax.swing.SwingConstants.RIGHT
    );

    lblPrecio.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    20
            )
    );


    panelTotal.add(
            lblTotal,
            java.awt.BorderLayout.WEST
    );

    panelTotal.add(
            lblPrecio,
            java.awt.BorderLayout.EAST
    );


    panelTotal.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    35
            )
    );


    // =========================================================
    // BOTÓN CONTINUAR
    // =========================================================

    javax.swing.JButton btnContinuar =
            new javax.swing.JButton(
                    "Continuar    →"
            ) {

        @Override
        protected void paintComponent(
                java.awt.Graphics g) {

            java.awt.Graphics2D g2 =
                    (java.awt.Graphics2D) g.create();

            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(AMARILLO);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    15,
                    15
            );

            g2.dispose();

            super.paintComponent(g);
        }
    };


    btnContinuar.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    15
            )
    );

    btnContinuar.setForeground(
            java.awt.Color.BLACK
    );

    btnContinuar.setOpaque(false);
    btnContinuar.setContentAreaFilled(false);
    btnContinuar.setBorderPainted(false);
    btnContinuar.setFocusPainted(false);

    btnContinuar.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );

    btnContinuar.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    50
            )
    );

    btnContinuar.setPreferredSize(
            new java.awt.Dimension(
                    280,
                    50
            )
    );

    btnContinuar.setAlignmentX(
            java.awt.Component.CENTER_ALIGNMENT
    );


    // =========================================================
    // CANCELAR PEDIDO
    // =========================================================

    javax.swing.JButton btnCancelar =
            new javax.swing.JButton(
                    "Cancelar pedido"
            );

    btnCancelar.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.PLAIN,
                    13
            )
    );

    btnCancelar.setForeground(
            new java.awt.Color(
                    80,
                    80,
                    80
            )
    );

    btnCancelar.setBackground(
            java.awt.Color.WHITE
    );

    btnCancelar.setFocusPainted(false);

    btnCancelar.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    42
            )
    );

    btnCancelar.setPreferredSize(
            new java.awt.Dimension(
                    280,
                    42
            )
    );

    btnCancelar.setAlignmentX(
            java.awt.Component.CENTER_ALIGNMENT
    );

    btnCancelar.setBorderPainted(false);

    btnCancelar.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );
    
    btnCancelar.addActionListener(e -> {

        panelPedido.setVisible(false);

        jPanel1.revalidate();
        jPanel1.repaint();
    });


    // =========================================================
    // AGREGAR PARTE INFERIOR
    // =========================================================

    panelInferior.add(panelTotal);

    panelInferior.add(
            javax.swing.Box.createVerticalStrut(20)
    );

    panelInferior.add(btnContinuar);

    panelInferior.add(
            javax.swing.Box.createVerticalStrut(10)
    );

    panelInferior.add(btnCancelar);


    contenidoPedido.add(
            panelInferior,
            java.awt.BorderLayout.SOUTH
    );


    // =========================================================
    // INICIA OCULTO
    // =========================================================

    panelPedido.setVisible(false);
}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        animacionLetras1 = new Componentes.AnimacionLetras();
        boton1 = new Componentes.Boton();
        boton2 = new Componentes.Boton();
        boton3 = new Componentes.Boton();
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
    private Componentes.AnimacionLetras animacionLetras1;
    private Componentes.Boton boton1;
    private Componentes.Boton boton2;
    private Componentes.Boton boton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
