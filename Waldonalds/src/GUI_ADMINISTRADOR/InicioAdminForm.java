/*
 * JFrame Form editable desde el diseñador visual de NetBeans.
 */
package GUI_ADMINISTRADOR;

import GUI_CAJERO.Cajero;
import Login.Login;
import Utilidades.TemaAdmin;
import java.awt.EventQueue;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import Login.Login;

/**
 * Pantalla administrativa construida como JFrame Form de NetBeans.
 */
@SuppressWarnings({"serial", "this-escape"})
public class InicioAdminForm extends javax.swing.JFrame {
    private void abrirMenuCerrarSesion() {

    // Crear menú desplegable
    JPopupMenu menu = new JPopupMenu();

    // Fondo y borde del menú
    menu.setBackground(new Color(1, 15, 30));

    menu.setBorder(
        BorderFactory.createLineBorder(
            new Color(254, 195, 6),
            1
        )
    );

    // Crear opción
    JMenuItem cerrarSesion = new JMenuItem("Cerrar sesión");

    // Fuente
    cerrarSesion.setFont(tema.media(16f));

    // Colores
    cerrarSesion.setForeground(Color.WHITE);
    cerrarSesion.setBackground(new Color(1, 15, 30));

    // Necesario para mostrar el color de fondo
    cerrarSesion.setOpaque(true);

    // Tamaño
    cerrarSesion.setPreferredSize(
        new Dimension(215, 50)
    );

    // Espacio interno
    cerrarSesion.setBorder(
        BorderFactory.createEmptyBorder(
            0,
            18,
            0,
            18
        )
    );

    // Cursor
    cerrarSesion.setCursor(
        new java.awt.Cursor(
            java.awt.Cursor.HAND_CURSOR
        )
    );

    // ==========================================
    // EFECTO AL PASAR EL MOUSE
    // ==========================================
    cerrarSesion.addMouseListener(
        new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                cerrarSesion.setBackground(
                    new Color(198, 0, 10)
                );
            }

            @Override
            public void mouseExited(MouseEvent e) {

                cerrarSesion.setBackground(
                    new Color(1, 15, 30)
                );
            }
        }
    );

    // ==========================================
    // ACCIÓN CERRAR SESIÓN
    // ==========================================
    cerrarSesion.addActionListener(
        e -> cerrarSesion()
    );

    // Agregar opción al menú
    menu.add(cerrarSesion);

    // Mostrar debajo del botón Administrador
    menu.show(
        botonUsuario,
        0,
        botonUsuario.getHeight()
    );
}
    
    private final TemaAdmin tema;
    private Runnable accionMenuAdmin;

    public InicioAdminForm() {

        tema = new TemaAdmin();
        accionMenuAdmin = this::mostrarModuloPendiente;
        initComponents();
        aplicarTipografia();
        botonMenu.putClientProperty("noEscalarIcono", true);
        botonAdmin.putClientProperty("noEscalarIcono", true);

        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        javax.swing.SwingUtilities.invokeLater(()
                -> Utilidades.Escalador.escalar(getContentPane()));

    }

    private void aplicarTipografia() {
        labelMarca.setFont(tema.negrita(34f));
        labelRol.setFont(tema.regular(23f));
        botonUsuario.setFont(tema.media(17f));
        labelTitulo.setFont(tema.negrita(52f));
        labelSubtitulo.setFont(tema.regular(27f));
        labelTituloMenu.setFont(tema.negrita(36f));
        labelTituloAdmin.setFont(tema.negrita(36f));
        labelDescripcionMenu1.setFont(tema.regular(22f));
        labelDescripcionMenu2.setFont(tema.regular(22f));
        labelDescripcionAdmin1.setFont(tema.regular(22f));
        labelDescripcionAdmin2.setFont(tema.regular(22f));
        botonMenu.setFont(tema.negrita(25f));
        botonAdmin.setFont(tema.negrita(25f));
    }

    public void setAccionMenuAdmin(Runnable accionMenuAdmin) {
        this.accionMenuAdmin = accionMenuAdmin != null
                ? accionMenuAdmin
                : this::mostrarModuloPendiente;
    }

    private void abrirMenu() {
        Cajero menu = new Cajero();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
        dispose();
    }

    private void mostrarModuloPendiente() {
        JOptionPane.showMessageDialog(
                this,
                "Conecta aquí la pantalla principal de administración.",
                "Menú administrativo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarMenuUsuario() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem cerrar = new JMenuItem("Cerrar sesión");
        cerrar.setFont(tema.media(15f));
        cerrar.addActionListener(evento -> cerrarSesion());
        menu.add(cerrar);
        menu.show(botonUsuario, 0, botonUsuario.getHeight());
    }

    private void cerrarSesion() {
        Login login = new Login();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        dispose();
    }

    /**
     * Este bloque lo administra el diseñador visual de NetBeans.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRaiz = new javax.swing.JPanel();
        panelCabecera = new javax.swing.JPanel();
        labelLogo = new Labels.LabelEscalable();
        labelMarca = new javax.swing.JLabel();
        labelRol = new javax.swing.JLabel();
        labelIconoUsuario = new Labels.LabelEscalable();
        botonUsuario = new javax.swing.JButton();
        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        tarjetaMenu = new Componentes.PanelFlotante();
        circuloMenu = new Componentes.PanelCircular();
        iconoMenu = new GUI_ADMINISTRADOR.IconoMenuAdmin();
        labelTituloMenu = new javax.swing.JLabel();
        labelDescripcionMenu1 = new javax.swing.JLabel();
        labelDescripcionMenu2 = new javax.swing.JLabel();
        botonMenu = new Componentes.BotonDerretido();
        tarjetaAdmin = new Componentes.PanelFlotante();
        circuloAdmin = new Componentes.PanelCircular();
        labelIconoAdmin = new Labels.LabelEscalable();
        labelTituloAdmin = new javax.swing.JLabel();
        labelDescripcionAdmin1 = new javax.swing.JLabel();
        labelDescripcionAdmin2 = new javax.swing.JLabel();
        botonAdmin = new Componentes.BotonDerretido();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inicio de Administrador");
        setMinimumSize(new java.awt.Dimension(1024, 650));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRaiz.setBackground(new java.awt.Color(252, 252, 253));
        panelRaiz.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCabecera.setBackground(new java.awt.Color(1, 15, 30));
        panelCabecera.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/LogoW.png"))); // NOI18N
        labelLogo.setMantenerProporcion(true);
        panelCabecera.add(labelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 25, 92, 82));

        labelMarca.setFont(new java.awt.Font("Dialog", 1, 39)); // NOI18N
        labelMarca.setForeground(new java.awt.Color(255, 255, 255));
        labelMarca.setText("Waldonald’s");
        panelCabecera.add(labelMarca, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 28, 360, 50));

        labelRol.setFont(new java.awt.Font("Dialog", 0, 26)); // NOI18N
        labelRol.setForeground(new java.awt.Color(255, 255, 255));
        labelRol.setText("Administrador");
        panelCabecera.add(labelRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 76, 300, 38));

        labelIconoUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/IconoUsuario.png"))); // NOI18N
        labelIconoUsuario.setMantenerProporcion(true);
        panelCabecera.add(labelIconoUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1618, 34, 54, 54));

        botonUsuario.setFont(new java.awt.Font("Dialog", 1, 19)); // NOI18N
        botonUsuario.setForeground(new java.awt.Color(255, 255, 255));
        botonUsuario.setText("Administrador   ⌄");
        botonUsuario.setBorderPainted(false);
        botonUsuario.setContentAreaFilled(false);
        botonUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botonUsuario.setFocusPainted(false);
        botonUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonUsuarioActionPerformed(evt);
            }
        });
        panelCabecera.add(botonUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1680, 31, 215, 62));

        panelRaiz.add(panelCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 135));

        labelTitulo.setFont(new java.awt.Font("Dialog", 1, 59)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitulo.setText("Inicio de Administrador");
        panelRaiz.add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 1920, 76));

        labelSubtitulo.setFont(new java.awt.Font("Dialog", 0, 31)); // NOI18N
        labelSubtitulo.setForeground(new java.awt.Color(80, 88, 104));
        labelSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSubtitulo.setText("Selecciona una opción para continuar");
        panelRaiz.add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 276, 1920, 44));

        tarjetaMenu.setColorBorde(new java.awt.Color(255, 188, 0));
        tarjetaMenu.setDesplazamientoSombraY(6);
        tarjetaMenu.setGrosorBorde(1.5F);
        tarjetaMenu.setRadio(25);
        tarjetaMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        circuloMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        circuloMenu.add(iconoMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 206, 206));

        tarjetaMenu.add(circuloMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 35, 206, 206));

        labelTituloMenu.setFont(new java.awt.Font("Dialog", 1, 41)); // NOI18N
        labelTituloMenu.setForeground(new java.awt.Color(13, 17, 23));
        labelTituloMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTituloMenu.setText("Ver menú");
        tarjetaMenu.add(labelTituloMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 268, 480, 56));

        labelDescripcionMenu1.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        labelDescripcionMenu1.setForeground(new java.awt.Color(80, 88, 104));
        labelDescripcionMenu1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDescripcionMenu1.setText("Accede al menú de alimentos");
        tarjetaMenu.add(labelDescripcionMenu1, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 340, 491, 35));

        labelDescripcionMenu2.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        labelDescripcionMenu2.setForeground(new java.awt.Color(80, 88, 104));
        labelDescripcionMenu2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDescripcionMenu2.setText("y bebidas disponible.");
        tarjetaMenu.add(labelDescripcionMenu2, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 375, 491, 35));

        botonMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/flecha_boton.png"))); // NOI18N
        botonMenu.setText("Entrar");
        botonMenu.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        botonMenu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        botonMenu.setIconTextGap(30);
        botonMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonMenuActionPerformed(evt);
            }
        });
        tarjetaMenu.add(botonMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 460, 470, 110));

        panelRaiz.add(tarjetaMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 375, 549, 594));

        tarjetaAdmin.setColorBorde(new java.awt.Color(198, 0, 10));
        tarjetaAdmin.setDesplazamientoSombraY(6);
        tarjetaAdmin.setGrosorBorde(1.5F);
        tarjetaAdmin.setRadio(25);
        tarjetaAdmin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        circuloAdmin.setColorFondo(new java.awt.Color(254, 232, 233));
        circuloAdmin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelIconoAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/IconoAdmin.png"))); // NOI18N
        labelIconoAdmin.setMantenerProporcion(true);
        circuloAdmin.add(labelIconoAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 170, 150));

        tarjetaAdmin.add(circuloAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 35, 206, 206));

        labelTituloAdmin.setFont(new java.awt.Font("Dialog", 1, 41)); // NOI18N
        labelTituloAdmin.setForeground(new java.awt.Color(13, 17, 23));
        labelTituloAdmin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTituloAdmin.setText("Menú Admin");
        tarjetaAdmin.add(labelTituloAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 268, 480, 56));

        labelDescripcionAdmin1.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        labelDescripcionAdmin1.setForeground(new java.awt.Color(80, 88, 104));
        labelDescripcionAdmin1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDescripcionAdmin1.setText("Gestiona productos, inventario,");
        tarjetaAdmin.add(labelDescripcionAdmin1, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 340, 491, 35));

        labelDescripcionAdmin2.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        labelDescripcionAdmin2.setForeground(new java.awt.Color(80, 88, 104));
        labelDescripcionAdmin2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDescripcionAdmin2.setText("usuarios y reportes.");
        tarjetaAdmin.add(labelDescripcionAdmin2, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 375, 491, 35));

        botonAdmin.setColorBoton(new java.awt.Color(200, 0, 9));
        botonAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/flecha_boton.png"))); // NOI18N
        botonAdmin.setText("Entrar");
        botonAdmin.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        botonAdmin.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        botonAdmin.setIconTextGap(30);
        botonAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAdminActionPerformed(evt);
            }
        });
        tarjetaAdmin.add(botonAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 460, 470, 110));

        panelRaiz.add(tarjetaAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(991, 375, 549, 594));

        getContentPane().add(panelRaiz, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 1080));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void botonUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonUsuarioActionPerformed
        abrirMenuCerrarSesion();
    }//GEN-LAST:event_botonUsuarioActionPerformed

    private void botonAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAdminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonAdminActionPerformed

    private void botonMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonMenuActionPerformed
        abrirMenu();
    }//GEN-LAST:event_botonMenuActionPerformed

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new InicioAdminForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonAdmin;
    private Componentes.BotonDerretido botonMenu;
    private javax.swing.JButton botonUsuario;
    private Componentes.PanelCircular circuloAdmin;
    private Componentes.PanelCircular circuloMenu;
    private GUI_ADMINISTRADOR.IconoMenuAdmin iconoMenu;
    private javax.swing.JLabel labelDescripcionAdmin1;
    private javax.swing.JLabel labelDescripcionAdmin2;
    private javax.swing.JLabel labelDescripcionMenu1;
    private javax.swing.JLabel labelDescripcionMenu2;
    private Labels.LabelEscalable labelIconoAdmin;
    private Labels.LabelEscalable labelIconoUsuario;
    private Labels.LabelEscalable labelLogo;
    private javax.swing.JLabel labelMarca;
    private javax.swing.JLabel labelRol;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelTituloAdmin;
    private javax.swing.JLabel labelTituloMenu;
    private javax.swing.JPanel panelCabecera;
    private javax.swing.JPanel panelRaiz;
    private Componentes.PanelFlotante tarjetaAdmin;
    private Componentes.PanelFlotante tarjetaMenu;
    // End of variables declaration//GEN-END:variables
}
