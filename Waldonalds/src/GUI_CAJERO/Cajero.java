/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI_CAJERO;

import Componentes.BarraTituloForm;
import Login.Login;
import Utilidades.SesionUsuario;

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
private javax.swing.JButton btnOrdenar;
private Componentes.BotonMenuLateral btnCerrarSesion;

private javax.swing.JButton botonActivo;
private BarraTituloForm barraTitulo;


// =====================================================
// CONSTRUCTOR
// =====================================================

public Cajero() {

     setUndecorated(true);

    initComponents();

    configurarInterfaz();
    barraTitulo = BarraTituloForm.instalar(this, "Menú");
    setExtendedState(
            javax.swing.JFrame.MAXIMIZED_BOTH
    );

    setLocationRelativeTo(null);
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
    

    // =================================================
    // AGREGAR AL CARD LAYOUT
    // =================================================

    panelContenido.add(
            menuCajeroPanel,
            "MENU"
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

    if (barraTitulo != null) {
        barraTitulo.setSeccion("Menú");
    }
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
                    80
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
                            18,
                            10
                    )
            );

    panelIzquierdo.setOpaque(false);


    // =================================================
    // ESPACIO PARA LOGO
    // =================================================

    javax.swing.JPanel espacioLogo =
            new javax.swing.JPanel();

    espacioLogo.setOpaque(false);
    espacioLogo.setLayout(new java.awt.BorderLayout());

    javax.swing.ImageIcon iconoLogo = new javax.swing.ImageIcon(
            getClass().getResource("/Imagenes/LogoW.png")
    );
    java.awt.Image imagenLogo = iconoLogo.getImage().getScaledInstance(
            72,
            52,
            java.awt.Image.SCALE_SMOOTH
    );
    javax.swing.JLabel logo = new javax.swing.JLabel(
            new javax.swing.ImageIcon(imagenLogo),
            javax.swing.SwingConstants.CENTER
    );
    espacioLogo.add(logo, java.awt.BorderLayout.CENTER);

    espacioLogo.setPreferredSize(
            new java.awt.Dimension(
                    95,
                    60
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

    panelIzquierdo.add(
            btnMenu
    );


    // =================================================
    // ORDENAR
    // =================================================

    btnOrdenar = new Componentes.BotonDerretido();
    btnOrdenar.setText("Ordenar");
    javax.swing.ImageIcon iconoPedidos = new javax.swing.ImageIcon(
            getClass().getResource("/Imagenes/pedidos.png")
    );
    java.awt.Image imagenPedidos = iconoPedidos.getImage().getScaledInstance(
            36,
            36,
            java.awt.Image.SCALE_SMOOTH
    );
    btnOrdenar.setIcon(new javax.swing.ImageIcon(imagenPedidos));
    btnOrdenar.setIconTextGap(8);


    btnOrdenar.setPreferredSize(
            new java.awt.Dimension(
                    180,
                    60
            )
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

    btnOrdenar.setHorizontalTextPosition(
            javax.swing.SwingConstants.RIGHT
    );
    btnOrdenar.setVerticalTextPosition(
            javax.swing.SwingConstants.CENTER
    );
    btnOrdenar.setHorizontalAlignment(
            javax.swing.SwingConstants.CENTER
    );
    btnOrdenar.setVerticalAlignment(
            javax.swing.SwingConstants.CENTER
    );

    btnOrdenar.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(
                    0,
                    0,
                    14,
                    10
            )
    );


    btnOrdenar.setCursor(
            new java.awt.Cursor(
                    java.awt.Cursor.HAND_CURSOR
            )
    );

    btnCerrarSesion = new Componentes.BotonMenuLateral();
    btnCerrarSesion.setText("Cerrar sesión");
    btnCerrarSesion.setTipoIcono("SALIR");
    btnCerrarSesion.setMostrarBorde(true);
    btnCerrarSesion.setFont(
            new java.awt.Font("Arial", java.awt.Font.BOLD, 14)
    );
    btnCerrarSesion.setPreferredSize(
            new java.awt.Dimension(180, 60)
    );


    // =================================================
    // DERECHA
    // =================================================

    javax.swing.JPanel panelDerecho =
            new javax.swing.JPanel(
                    new java.awt.FlowLayout(
                            java.awt.FlowLayout.RIGHT,
                            25,
                            15
                    )
            );

    panelDerecho.setOpaque(false);

    panelDerecho.add(
            btnCerrarSesion
    );

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
    // ORDENAR
    // =================================================

    btnOrdenar.addActionListener(e -> {

        abrirPedido();

    });

    btnCerrarSesion.addActionListener(e -> cerrarSesion());


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
                        35;

                int altoLinea =
                        Math.max(
                                3,
                                3
                        );

                int x =
                        (
                                getWidth()
                                - anchoLinea
                        ) / 2;

                int y =
                        getHeight()
                                - 6;

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
                    14
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
                    + 45;


    boton.setPreferredSize(
            new java.awt.Dimension(
                    anchoBoton,
                    55
            )
    );


    boton.setMinimumSize(
            new java.awt.Dimension(
                    anchoBoton,
                    55
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

}

private void cerrarSesion() {
    if (!confirmarCierreSesion()) {
        return;
    }

    SesionUsuario.cerrar();
    Login login = new Login();
    login.setLocationRelativeTo(null);
    login.setVisible(true);
    dispose();
}

private boolean confirmarCierreSesion() {
    javax.swing.JDialog dialogo = new javax.swing.JDialog(
            this,
            "Cerrar sesión",
            true
    );
    dialogo.setUndecorated(true);

    javax.swing.JPanel contenido = new javax.swing.JPanel(
            new java.awt.BorderLayout(0, 18)
    ) {
        @Override protected void paintComponent(java.awt.Graphics graphics) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics.create();
            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.dispose();
            super.paintComponent(graphics);
        }
    };
    contenido.setOpaque(false);
    contenido.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(
                    new java.awt.Color(220, 226, 233)
            ),
            javax.swing.BorderFactory.createEmptyBorder(22, 24, 20, 24)
    ));

    javax.swing.JPanel encabezado = new javax.swing.JPanel(
            new java.awt.BorderLayout(14, 0)
    );
    encabezado.setOpaque(false);

    javax.swing.JPanel icono = new javax.swing.JPanel(
            new java.awt.GridBagLayout()
    ) {
        @Override protected void paintComponent(java.awt.Graphics graphics) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics.create();
            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(AMARILLO);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(graphics);
        }
    };
    icono.setOpaque(false);
    icono.setPreferredSize(new java.awt.Dimension(52, 52));
    javax.swing.ImageIcon iconoPregunta = new javax.swing.ImageIcon(
            getClass().getResource("/Imagenes/pregunta.png")
    );
    java.awt.Image imagenPregunta = iconoPregunta.getImage().getScaledInstance(
            30,
            30,
            java.awt.Image.SCALE_SMOOTH
    );
    javax.swing.JLabel iconoSalida = new javax.swing.JLabel(
            new javax.swing.ImageIcon(imagenPregunta)
    );
    icono.add(iconoSalida);
    encabezado.add(icono, java.awt.BorderLayout.WEST);

    javax.swing.JPanel textos = new javax.swing.JPanel();
    textos.setOpaque(false);
    textos.setLayout(new javax.swing.BoxLayout(
            textos,
            javax.swing.BoxLayout.Y_AXIS
    ));
    javax.swing.JLabel titulo = new javax.swing.JLabel("Cerrar sesión");
    titulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
    titulo.setForeground(AZUL_BARRA);
    javax.swing.JLabel subtitulo = new javax.swing.JLabel(
            "La sesión actual se cerrará"
    );
    subtitulo.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
    subtitulo.setForeground(new java.awt.Color(105, 115, 128));
    textos.add(titulo);
    textos.add(javax.swing.Box.createVerticalStrut(5));
    textos.add(subtitulo);
    encabezado.add(textos, java.awt.BorderLayout.CENTER);
    contenido.add(encabezado, java.awt.BorderLayout.NORTH);

    javax.swing.JLabel mensaje = new javax.swing.JLabel(
            "¿Deseas cerrar la sesión actual?",
            javax.swing.SwingConstants.CENTER
    );
    mensaje.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 15));
    mensaje.setForeground(new java.awt.Color(45, 53, 63));
    contenido.add(mensaje, java.awt.BorderLayout.CENTER);

    boolean[] confirmar = {false};
    Componentes.BotonRedondeado cancelar = botonDialogo(
            "Cancelar",
            java.awt.Color.WHITE,
            AZUL_BARRA
    );
    cancelar.setColorBorde(new java.awt.Color(190, 198, 208));
    cancelar.setGrosorBorde(1.2f);
    Componentes.BotonRedondeado aceptar = botonDialogo(
            "Cerrar sesión",
            AMARILLO,
            AZUL_BARRA
    );
    aceptar.addActionListener(e -> {
        confirmar[0] = true;
        dialogo.dispose();
    });
    cancelar.addActionListener(e -> dialogo.dispose());

    javax.swing.JPanel acciones = new javax.swing.JPanel(
            new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0)
    );
    acciones.setOpaque(false);
    acciones.add(cancelar);
    acciones.add(aceptar);
    contenido.add(acciones, java.awt.BorderLayout.SOUTH);

    dialogo.setContentPane(contenido);
    dialogo.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    dialogo.getRootPane().setDefaultButton(cancelar);
    dialogo.getRootPane().registerKeyboardAction(
            e -> dialogo.dispose(),
            javax.swing.KeyStroke.getKeyStroke("ESCAPE"),
            javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    dialogo.setSize(430, 235);
    dialogo.setLocationRelativeTo(this);
    dialogo.setVisible(true);
    return confirmar[0];
}

private Componentes.BotonRedondeado botonDialogo(
        String texto,
        java.awt.Color fondo,
        java.awt.Color tinta) {
    Componentes.BotonRedondeado boton = new Componentes.BotonRedondeado();
    boton.setText(texto);
    boton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
    boton.setForeground(tinta);
    boton.setColorInicio(fondo);
    boton.setDegradado(false);
    boton.setRadio(12);
    boton.setPreferredSize(new java.awt.Dimension(138, 42));
    return boton;
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
    panelPedido.setPreferredSize(new java.awt.Dimension(390, 0));
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
                    bordeHover, 1));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evento) {
            boton.setForeground(new java.awt.Color(80, 80, 80));
            boton.setBorder(javax.swing.BorderFactory.createLineBorder(
                    bordeNormal, 1));
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
