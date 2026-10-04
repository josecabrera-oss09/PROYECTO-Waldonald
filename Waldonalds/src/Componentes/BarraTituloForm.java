/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Componentes;

/**
 *
 * @author EMILIANISOMASCOS
 */
public class BarraTituloForm extends javax.swing.JPanel {
    private static final String BARRA_INSTALADA = "barraTituloForm";
    private static final java.awt.Color AZUL = new java.awt.Color(1, 20, 36);
    private static final java.awt.Color AMARILLO = new java.awt.Color(255, 188, 13);

    private enum TipoIconoVentana {
        MINIMIZAR, MAXIMIZAR, RESTAURAR, CERRAR
    }

    /** Instala una sola barra adaptable encima del contenido de la ventana. */
    public static BarraTituloForm instalar(javax.swing.JFrame ventana,
            String seccion) {

        Object instalada = ventana.getRootPane().getClientProperty(BARRA_INSTALADA);
        if (instalada instanceof BarraTituloForm barraExistente) {
            barraExistente.setSeccion(seccion);
            return barraExistente;
        }

        java.awt.Container contenidoOriginal = ventana.getContentPane();

        javax.swing.JPanel contenedor = new javax.swing.JPanel(
                new java.awt.BorderLayout());

        contenedor.setBackground(contenidoOriginal.getBackground());

        BarraTituloForm barra = new BarraTituloForm(ventana);
        barra.setSeccion(seccion);

        ventana.setContentPane(contenedor);
        contenedor.add(barra, java.awt.BorderLayout.NORTH);
        contenedor.add(contenidoOriginal, java.awt.BorderLayout.CENTER);
        ventana.getRootPane().putClientProperty(BARRA_INSTALADA, barra);

        contenedor.revalidate();
        contenedor.repaint();
        return barra;
    }

    public static BarraTituloForm instalar(javax.swing.JFrame ventana) {
        return instalar(ventana, "");
    }

    private javax.swing.JFrame ventana;

    private int mouseX;
    private int mouseY;

    private boolean maximizada = false;
    private javax.swing.JLabel etiquetaMarca;
    private javax.swing.JLabel etiquetaSeccion;

    public BarraTituloForm(javax.swing.JFrame ventana) {

        initComponents();

        this.ventana = ventana;

        configurarDisenoAdaptable();
        configurarBotones();
        configurarMovimiento();
    }

    /** Cambia el texto visible que identifica dónde está el usuario. */
    public void setSeccion(String seccion) {
        String texto = seccion == null ? "" : seccion.trim();
        etiquetaSeccion.setText(texto);
        if (ventana != null) {
            ventana.setTitle(texto.isEmpty()
                    ? "Waldonald's"
                    : "Waldonald's - " + texto);
        }
        revalidate();
        repaint();
    }

    private void configurarDisenoAdaptable() {
        removeAll();
        setLayout(new java.awt.BorderLayout());
        setOpaque(true);
        setBackground(AZUL);
        setPreferredSize(new java.awt.Dimension(1, 48));
        setMinimumSize(new java.awt.Dimension(1, 48));
        setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0,
                AMARILLO));

        javax.swing.JPanel izquierda = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        izquierda.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 18, 0, 0));

        labelEscalable1.setPreferredSize(new java.awt.Dimension(48, 42));
        labelEscalable1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEscalable1.setMantenerProporcion(true);
        izquierda.add(labelEscalable1);

        etiquetaMarca = etiqueta("Waldonald's", java.awt.Font.BOLD, 19);
        izquierda.add(etiquetaMarca);
        izquierda.add(separador());

        etiquetaSeccion = etiqueta("", java.awt.Font.PLAIN, 16);
        izquierda.add(etiquetaSeccion);

        javax.swing.JPanel controles = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        controles.setOpaque(false);
        prepararBotonVisual(botonMinimizar, TipoIconoVentana.MINIMIZAR, false);
        prepararBotonVisual(botonMaximizar, TipoIconoVentana.MAXIMIZAR, false);
        prepararBotonVisual(botonCerrar, TipoIconoVentana.CERRAR, true);
        controles.add(botonMinimizar);
        controles.add(botonMaximizar);
        controles.add(botonCerrar);

        add(izquierda, java.awt.BorderLayout.WEST);
        add(controles, java.awt.BorderLayout.EAST);
    }

    private javax.swing.JLabel etiqueta(String texto, int estilo, int tamano) {
        javax.swing.JLabel etiqueta = new javax.swing.JLabel(texto);
        etiqueta.setForeground(java.awt.Color.WHITE);
        etiqueta.setFont(new java.awt.Font("Dialog", estilo, tamano));
        etiqueta.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        etiqueta.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        return etiqueta;
    }

    private javax.swing.JLabel separador() {
        javax.swing.JLabel separador = etiqueta("•", java.awt.Font.BOLD, 17);
        separador.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 16, 0, 8));
        return separador;
    }

    private void prepararBotonVisual(javax.swing.JButton boton,
            TipoIconoVentana tipo, boolean cerrar) {
        boton.setIcon(new IconoVentana(tipo));
        boton.setText("");
        boton.setForeground(java.awt.Color.WHITE);
        boton.setPreferredSize(new java.awt.Dimension(52, 45));
        boton.setMinimumSize(new java.awt.Dimension(52, 45));
        boton.setMaximumSize(new java.awt.Dimension(52, 45));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBackground(AZUL);
        boton.setCursor(java.awt.Cursor.getPredefinedCursor(
                java.awt.Cursor.HAND_CURSOR));
    }

    /** Iconos vectoriales para no depender de fuentes ni de imágenes escaladas. */
    private static final class IconoVentana implements javax.swing.Icon {
        private final TipoIconoVentana tipo;

        IconoVentana(TipoIconoVentana tipo) {
            this.tipo = tipo;
        }

        @Override public int getIconWidth() { return 20; }

        @Override public int getIconHeight() { return 20; }

        @Override
        public void paintIcon(java.awt.Component componente, java.awt.Graphics graphics,
                int x, int y) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics.create();
            g2.setColor(java.awt.Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2.2f,
                    java.awt.BasicStroke.CAP_ROUND,
                    java.awt.BasicStroke.JOIN_ROUND));
            int izquierda = x + 3;
            int derecha = x + 17;
            int arriba = y + 3;
            int abajo = y + 17;
            switch (tipo) {
                case MINIMIZAR -> g2.drawLine(izquierda, y + 14, derecha, y + 14);
                case MAXIMIZAR -> g2.drawRect(izquierda, arriba, 14, 14);
                case RESTAURAR -> {
                    g2.drawRect(x + 6, y + 3, 11, 11);
                    g2.drawRect(x + 3, y + 6, 11, 11);
                }
                case CERRAR -> {
                    g2.drawLine(izquierda, arriba, derecha, abajo);
                    g2.drawLine(derecha, arriba, izquierda, abajo);
                }
            }
            g2.dispose();
        }
    }

    private void configurarBotones() {

        // ==========================================
        // MINIMIZAR
        // ==========================================
        botonMinimizar.addActionListener(e -> {

            ventana.setState(
                    java.awt.Frame.ICONIFIED
            );

        });

        // ==========================================
        // MAXIMIZAR / RESTAURAR
        // ==========================================
        botonMaximizar.addActionListener(e -> {

            boolean estaMaximizada
                    = (ventana.getExtendedState()
                    & java.awt.Frame.MAXIMIZED_BOTH)
                    == java.awt.Frame.MAXIMIZED_BOTH;

            if (estaMaximizada) {

                ventana.setExtendedState(
                        java.awt.Frame.NORMAL
                );

                actualizarIconoMaximizar();

            } else {

                ventana.setExtendedState(
                        java.awt.Frame.MAXIMIZED_BOTH
                );

                actualizarIconoMaximizar();
            }
        });

        // ==========================================
        // CERRAR
        // ==========================================
        botonCerrar.addActionListener(e -> {

            ventana.dispose();

        });

        // ==========================================
        // HOVER
        // ==========================================
        agregarHover(
                botonMinimizar,
                new java.awt.Color(35, 45, 60)
        );

        agregarHover(
                botonMaximizar,
                new java.awt.Color(35, 45, 60)
        );

        agregarHover(
                botonCerrar,
                new java.awt.Color(200, 0, 10)
        );
    }

    private void actualizarIconoMaximizar() {
        boolean estaMaximizada = ventana != null
                && (ventana.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH)
                == java.awt.Frame.MAXIMIZED_BOTH;
        botonMaximizar.setIcon(new IconoVentana(estaMaximizada
                ? TipoIconoVentana.RESTAURAR : TipoIconoVentana.MAXIMIZAR));
    }

    private void agregarHover(
            javax.swing.JButton boton,
            java.awt.Color colorHover) {

        java.awt.Color colorNormal
                = new java.awt.Color(1, 15, 30);

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(
                    java.awt.event.MouseEvent evt) {

                boton.setOpaque(true);

                boton.setBackground(
                        colorHover
                );
            }

            @Override
            public void mouseExited(
                    java.awt.event.MouseEvent evt) {

                boton.setBackground(
                        colorNormal
                );
            }
        });
    }

    private void configurarMovimiento() {

        this.addMouseListener(
                new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(
                    java.awt.event.MouseEvent evt) {

                mouseX = evt.getX();
                mouseY = evt.getY();
            }

            @Override
            public void mouseClicked(
                    java.awt.event.MouseEvent evt) {

                if (evt.getClickCount() == 2) {

                    alternarMaximizado();
                }
            }
        });

        this.addMouseMotionListener(
                new java.awt.event.MouseMotionAdapter() {

            @Override
            public void mouseDragged(
                    java.awt.event.MouseEvent evt) {

                boolean estaMaximizada
                        = (ventana.getExtendedState()
                        & java.awt.Frame.MAXIMIZED_BOTH)
                        == java.awt.Frame.MAXIMIZED_BOTH;

                if (estaMaximizada) {
                    return;
                }

                ventana.setLocation(
                        evt.getXOnScreen() - mouseX,
                        evt.getYOnScreen() - mouseY
                );
            }
        });
    }

    private void alternarMaximizado() {

        boolean estaMaximizada
                = (ventana.getExtendedState()
                & java.awt.Frame.MAXIMIZED_BOTH)
                == java.awt.Frame.MAXIMIZED_BOTH;

        if (estaMaximizada) {

            ventana.setExtendedState(
                    java.awt.Frame.NORMAL
            );

                actualizarIconoMaximizar();

        } else {

            ventana.setExtendedState(
                    java.awt.Frame.MAXIMIZED_BOTH
            );

                actualizarIconoMaximizar();
        }
    }

    /**
     * Creates new form BarraTituloForm
     */
    public BarraTituloForm() {
        initComponents();
        configurarDisenoAdaptable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelEscalable1 = new Labels.LabelEscalable();
        botonCerrar = new javax.swing.JButton();
        botonMinimizar = new javax.swing.JButton();
        botonMaximizar = new javax.swing.JButton();

        setBackground(new java.awt.Color(1, 15, 30));
        setPreferredSize(new java.awt.Dimension(1530, 45));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/LogoW.png"))); // NOI18N
        labelEscalable1.setPreferredSize(new java.awt.Dimension(38, 30));
        add(labelEscalable1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 8, -1, -1));

        botonCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ICONOS_BARRA_TITULO/icono_cerrar.png"))); // NOI18N
        botonCerrar.setBorderPainted(false);
        botonCerrar.setContentAreaFilled(false);
        botonCerrar.setFocusPainted(false);
        botonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarActionPerformed(evt);
            }
        });
        add(botonCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1456, -14, -1, -1));

        botonMinimizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ICONOS_BARRA_TITULO/icono_minimizar.png"))); // NOI18N
        botonMinimizar.setBorderPainted(false);
        botonMinimizar.setContentAreaFilled(false);
        botonMinimizar.setFocusPainted(false);
        botonMinimizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonMinimizarActionPerformed(evt);
            }
        });
        add(botonMinimizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1308, -19, -1, -1));

        botonMaximizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ICONOS_BARRA_TITULO/icono_maximizar.png"))); // NOI18N
        botonMaximizar.setBorderPainted(false);
        botonMaximizar.setContentAreaFilled(false);
        botonMaximizar.setFocusPainted(false);
        botonMaximizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonMaximizarActionPerformed(evt);
            }
        });
        add(botonMaximizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1382, -14, -1, -1));

        getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void botonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonCerrarActionPerformed

    private void botonMinimizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonMinimizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonMinimizarActionPerformed

    private void botonMaximizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonMaximizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonMaximizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonCerrar;
    private javax.swing.JButton botonMaximizar;
    private javax.swing.JButton botonMinimizar;
    private Labels.LabelEscalable labelEscalable1;
    // End of variables declaration//GEN-END:variables
}
