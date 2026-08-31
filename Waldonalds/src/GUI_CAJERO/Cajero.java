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

private final java.awt.Color GRIS_TEXTO =
        new java.awt.Color(110, 110, 110);


// =====================================================
// CARD LAYOUT
// =====================================================

private java.awt.CardLayout cardLayout;

private javax.swing.JPanel panelContenido;

private MenuCajeroPanel menuCajeroPanel;

private promocionesPanel promocionesPanel;

private informacionPanel informacionPanel;

private novedadesPanel novedadesPanel;


// =====================================================
// ZONA CENTRAL Y PEDIDO
// =====================================================

private javax.swing.JPanel zonaCentral;
private javax.swing.JPanel panelPedido;


// =====================================================
// BOTONES
// =====================================================

private javax.swing.JButton btnMenu;
private javax.swing.JButton btnPromociones;
private javax.swing.JButton btnNovedades;
private javax.swing.JButton btnInformacion;
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

    novedadesPanel =
            new novedadesPanel();

    informacionPanel =
            new informacionPanel();


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

    panelContenido.add(
            novedadesPanel,
            "NOVEDADES"
    );

    panelContenido.add(
            informacionPanel,
            "INFORMACION"
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

    btnNovedades =
            crearBotonBarra(
                    "Novedades"
            );

    btnInformacion =
            crearBotonBarra(
                    "Información"
            );


    panelIzquierdo.add(
            btnMenu
    );

    panelIzquierdo.add(
            btnPromociones
    );

    panelIzquierdo.add(
            btnNovedades
    );

    panelIzquierdo.add(
            btnInformacion
    );


    // =================================================
    // ORDENAR
    // =================================================

    btnOrdenar =
            new javax.swing.JButton(
                    "Ordenar"
            ) {

        @Override
        protected void paintComponent(
                java.awt.Graphics g) {

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

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    escalar(14),
                    escalar(14)
            );

            g2.dispose();

            super.paintComponent(g);
        }
    };


    btnOrdenar.setPreferredSize(
            new java.awt.Dimension(
                    escalar(145),
                    escalar(48)
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


    // Espacio a la izquierda para icono
    btnOrdenar.setHorizontalAlignment(
            javax.swing.SwingConstants.RIGHT
    );


    btnOrdenar.setBorder(
            javax.swing.BorderFactory
                    .createEmptyBorder(
                            0,
                            escalar(45),
                            0,
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
    // NOVEDADES
    // =================================================

    btnNovedades.addActionListener(e -> {

        mostrarPanel(
                "NOVEDADES"
        );

        seleccionarBoton(
                btnNovedades
        );
    });


    // =================================================
    // INFORMACIÓN
    // =================================================

    btnInformacion.addActionListener(e -> {

        mostrarPanel(
                "INFORMACION"
        );

        seleccionarBoton(
                btnInformacion
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


    if (btnNovedades != null) {

        btnNovedades.setForeground(
                java.awt.Color.WHITE
        );

        btnNovedades.repaint();
    }


    if (btnInformacion != null) {

        btnInformacion.setForeground(
                java.awt.Color.WHITE
        );

        btnInformacion.repaint();
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

    panelPedido =
            new javax.swing.JPanel() {

        @Override
        protected void paintComponent(
                java.awt.Graphics g) {

            java.awt.Graphics2D g2 =
                    (java.awt.Graphics2D)
                            g.create();


            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );


            // Fondo exterior
            g2.setColor(
                    new java.awt.Color(
                            247,
                            247,
                            247
                    )
            );


            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );


            // Tarjeta blanca
            g2.setColor(
                    java.awt.Color.WHITE
            );


            g2.fillRoundRect(
                    escalar(15),
                    escalar(15),

                    getWidth()
                            - escalar(30),

                    getHeight()
                            - escalar(30),

                    escalar(25),
                    escalar(25)
            );


            g2.dispose();
        }
    };


    panelPedido.setOpaque(
            false
    );


    panelPedido.setPreferredSize(
            new java.awt.Dimension(
                    escalar(350),
                    0
            )
    );


    panelPedido.setLayout(
            new java.awt.BorderLayout()
    );


    // =================================================
    // CONTENIDO
    // =================================================

    javax.swing.JPanel contenidoPedido =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );


    contenidoPedido.setOpaque(
            false
    );


    contenidoPedido.setBorder(
            javax.swing.BorderFactory
                    .createEmptyBorder(
                            escalar(35),
                            escalar(35),
                            escalar(35),
                            escalar(35)
                    )
    );


    panelPedido.add(
            contenidoPedido,
            java.awt.BorderLayout.CENTER
    );


    // =================================================
    // SUPERIOR
    // =================================================

    javax.swing.JPanel panelSuperior =
            new javax.swing.JPanel();


    panelSuperior.setOpaque(
            false
    );


    panelSuperior.setLayout(
            new javax.swing.BoxLayout(
                    panelSuperior,
                    javax.swing.BoxLayout.Y_AXIS
            )
    );


    javax.swing.JLabel titulo =
            new javax.swing.JLabel(
                    "Tu pedido"
            );


    titulo.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    escalarFuente(22)
            )
    );


    titulo.setAlignmentX(
            java.awt.Component.LEFT_ALIGNMENT
    );


    javax.swing.JSeparator separador =
            new javax.swing.JSeparator();


    separador.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    escalar(2)
            )
    );


    panelSuperior.add(
            titulo
    );


    panelSuperior.add(
            javax.swing.Box
                    .createVerticalStrut(
                            escalar(20)
                    )
    );


    panelSuperior.add(
            separador
    );


    contenidoPedido.add(
            panelSuperior,
            java.awt.BorderLayout.NORTH
    );


    // =================================================
    // PRODUCTOS
    // =================================================

    javax.swing.JPanel panelProductos =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );


    panelProductos.setOpaque(
            false
    );


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


    lblVacio.setForeground(
            GRIS_TEXTO
    );


    lblVacio.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.PLAIN,
                    escalarFuente(14)
            )
    );


    lblVacio.setBorder(
            javax.swing.BorderFactory
                    .createEmptyBorder(
                            escalar(45),
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


    // =================================================
    // INFERIOR
    // =================================================

    javax.swing.JPanel panelInferior =
            new javax.swing.JPanel();


    panelInferior.setOpaque(
            false
    );


    panelInferior.setLayout(
            new javax.swing.BoxLayout(
                    panelInferior,
                    javax.swing.BoxLayout.Y_AXIS
            )
    );


    // =================================================
    // TOTAL
    // =================================================

    javax.swing.JPanel panelTotal =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );


    panelTotal.setOpaque(
            false
    );


    javax.swing.JLabel lblTotal =
            new javax.swing.JLabel(
                    "Total"
            );


    lblTotal.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    escalarFuente(19)
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
                    escalarFuente(20)
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
                    escalar(40)
            )
    );


    // =================================================
    // CONTINUAR
    // =================================================

    javax.swing.JButton btnContinuar =
            new javax.swing.JButton(
                    "Continuar     →"
            ) {

        @Override
        protected void paintComponent(
                java.awt.Graphics g) {

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


            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    escalar(15),
                    escalar(15)
            );


            g2.dispose();

            super.paintComponent(g);
        }
    };


    btnContinuar.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    escalarFuente(15)
            )
    );


    btnContinuar.setForeground(
            java.awt.Color.BLACK
    );


    btnContinuar.setOpaque(
            false
    );

    btnContinuar.setContentAreaFilled(
            false
    );

    btnContinuar.setBorderPainted(
            false
    );

    btnContinuar.setFocusPainted(
            false
    );


    btnContinuar.setPreferredSize(
            new java.awt.Dimension(
                    escalar(280),
                    escalar(50)
            )
    );


    btnContinuar.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    escalar(50)
            )
    );


    btnContinuar.setAlignmentX(
            java.awt.Component.CENTER_ALIGNMENT
    );


    // =================================================
    // CANCELAR
    // =================================================

    javax.swing.JButton btnCancelar =
            new javax.swing.JButton(
                    "Cancelar pedido"
            );


    btnCancelar.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.PLAIN,
                    escalarFuente(13)
            )
    );


    btnCancelar.setForeground(
            new java.awt.Color(
                    80,
                    80,
                    80
            )
    );


    btnCancelar.setContentAreaFilled(
            false
    );

    btnCancelar.setFocusPainted(
            false
    );

    btnCancelar.setOpaque(
            false
    );


    // BORDE
    btnCancelar.setBorder(
            javax.swing.BorderFactory
                    .createLineBorder(
                            new java.awt.Color(
                                    190,
                                    190,
                                    190
                            ),
                            Math.max(
                                    1,
                                    escalar(1)
                            )
                    )
    );


    btnCancelar.setPreferredSize(
            new java.awt.Dimension(
                    escalar(280),
                    escalar(42)
            )
    );


    btnCancelar.setMaximumSize(
            new java.awt.Dimension(
                    Integer.MAX_VALUE,
                    escalar(42)
            )
    );


    btnCancelar.setAlignmentX(
            java.awt.Component.CENTER_ALIGNMENT
    );


    // CERRAR PEDIDO
    btnCancelar.addActionListener(e -> {

        cerrarPedido();

    });


    // =================================================
    // AGREGAR ABAJO
    // =================================================

    panelInferior.add(
            panelTotal
    );


    panelInferior.add(
            javax.swing.Box
                    .createVerticalStrut(
                            escalar(20)
                    )
    );


    panelInferior.add(
            btnContinuar
    );


    panelInferior.add(
            javax.swing.Box
                    .createVerticalStrut(
                            escalar(10)
                    )
    );


    panelInferior.add(
            btnCancelar
    );


    contenidoPedido.add(
            panelInferior,
            java.awt.BorderLayout.SOUTH
    );


    // Oculto inicialmente
    panelPedido.setVisible(
            false
    );
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
