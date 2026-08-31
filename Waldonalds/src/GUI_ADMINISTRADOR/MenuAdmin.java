/*
 * JFrame Form editable desde el disenador visual de NetBeans.
 */
package GUI_ADMINISTRADOR;

import Componentes.BotonMenuLateral;
import Login.Login;
import Utilidades.TemaAdmin;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JPanel;

/**
 * Pantalla contenedora del modulo administrativo.
 */
@SuppressWarnings({"serial", "this-escape"})
public class MenuAdmin extends javax.swing.JFrame {

    private static final String DASHBOARD = "dashboard";
    private static final String USUARIOS = "usuarios";
    private static final String GESTION_MENU = "gestionMenu";
    private static final String INGREDIENTES = "ingredientes";
    private static final String REPORTES = "reportes";

    private final TemaAdmin tema;
    private CardLayout navegador;
    private BotonMenuLateral botonActivo;

    public MenuAdmin() {
        tema = new TemaAdmin();

        initComponents();

        aplicarTipografia();
        configurarSecciones();
        mostrarSeccion(DASHBOARD, botonDashboard);

        setLocationRelativeTo(null);

        Utilidades.Escalador.aplicar(this);
    }

    private void aplicarTipografia() {
        labelMarca.setFont(tema.negrita(28f));
        labelRol.setFont(tema.media(18f));
        labelTituloCabecera.setFont(tema.negrita(25f));
        labelBienvenido.setFont(tema.regular(16f));
        labelUsuario.setFont(tema.negrita(16f));
        botonDashboard.setFont(tema.negrita(17f));
        botonUsuarios.setFont(tema.media(17f));
        botonGestionMenu.setFont(tema.media(17f));
        botonIngredientes.setFont(tema.media(17f));
        botonReportes.setFont(tema.media(17f));
        botonCerrarSesion.setFont(tema.negrita(17f));
    }

    private void configurarSecciones() {
        navegador = new CardLayout();
        panelContenido.removeAll();
        panelContenido.setLayout(navegador);
        agregarSeccion(new DashboardPanel(), DASHBOARD);
        agregarSeccion(new UsuariosPanel(), USUARIOS);
        agregarSeccion(new GestionMenuPanel(), GESTION_MENU);
        agregarSeccion(new IngredientesPanel(), INGREDIENTES);
        agregarSeccion(new ReportesPanel(), REPORTES);
    }

    private void agregarSeccion(JPanel seccion, String nombre) {
        seccion.setBackground(new Color(248, 249, 251));
        panelContenido.add(seccion, nombre);
    }

    private void mostrarSeccion(String nombre, BotonMenuLateral botonSeleccionado) {
        if (botonActivo != null && botonActivo != botonSeleccionado) {
            botonActivo.setSeleccionado(false);
        }
        botonSeleccionado.setSeleccionado(true);
        botonActivo = botonSeleccionado;
        navegador.show(panelContenido, nombre);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void cerrarSesion() {
        Login login = new Login();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRaiz = new javax.swing.JPanel();
        panelLateral = new javax.swing.JPanel();
        labelLogo = new Labels.LabelEscalable();
        labelMarca = new javax.swing.JLabel();
        labelRol = new javax.swing.JLabel();
        botonDashboard = new Componentes.BotonMenuLateral();
        botonUsuarios = new Componentes.BotonMenuLateral();
        botonGestionMenu = new Componentes.BotonMenuLateral();
        botonIngredientes = new Componentes.BotonMenuLateral();
        botonReportes = new Componentes.BotonMenuLateral();
        botonCerrarSesion = new Componentes.BotonMenuLateral();
        panelCabecera = new javax.swing.JPanel();
        labelHamburguesa = new javax.swing.JLabel();
        labelTituloCabecera = new javax.swing.JLabel();
        labelIconoUsuario = new Labels.LabelEscalable();
        labelBienvenido = new javax.swing.JLabel();
        labelUsuario = new javax.swing.JLabel();
        separadorCabecera = new javax.swing.JSeparator();
        panelContenido = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Panel de Administración");
        setMinimumSize(new java.awt.Dimension(1024, 650));
        setSize(new java.awt.Dimension(1920, 1080));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRaiz.setBackground(new java.awt.Color(248, 249, 251));
        panelRaiz.setPreferredSize(new java.awt.Dimension(1920, 1080));
        panelRaiz.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelLateral.setBackground(new java.awt.Color(0, 20, 43));
        panelLateral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/LogoW.png"))); // NOI18N
        labelLogo.setMantenerProporcion(true);
        panelLateral.add(labelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 25, 68, 68));

        labelMarca.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        labelMarca.setForeground(new java.awt.Color(255, 255, 255));
        labelMarca.setText("Waldonald's");
        panelLateral.add(labelMarca, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 25, 205, 39));

        labelRol.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        labelRol.setForeground(new java.awt.Color(255, 190, 0));
        labelRol.setText("Administrador");
        panelLateral.add(labelRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 62, 190, 30));

        botonDashboard.setText("Dashboard");
        botonDashboard.setSeleccionado(true);
        botonDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDashboardActionPerformed(evt);
            }
        });
        panelLateral.add(botonDashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 145, 296, 64));

        botonUsuarios.setText("Gestión de Usuarios");
        botonUsuarios.setTipoIcono("USUARIOS");
        botonUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonUsuariosActionPerformed(evt);
            }
        });
        panelLateral.add(botonUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 230, 296, 64));

        botonGestionMenu.setText("Gestión del Menú");
        botonGestionMenu.setTipoIcono("MENU");
        botonGestionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGestionMenuActionPerformed(evt);
            }
        });
        panelLateral.add(botonGestionMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 315, 296, 64));

        botonIngredientes.setText("Ingredientes");
        botonIngredientes.setTipoIcono("INGREDIENTES");
        botonIngredientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIngredientesActionPerformed(evt);
            }
        });
        panelLateral.add(botonIngredientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 400, 296, 64));

        botonReportes.setText("Reportes");
        botonReportes.setTipoIcono("REPORTES");
        botonReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonReportesActionPerformed(evt);
            }
        });
        panelLateral.add(botonReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 485, 296, 64));

        botonCerrarSesion.setText("VolverAlMenu");
        botonCerrarSesion.setMostrarBorde(true);
        botonCerrarSesion.setTipoIcono("SALIR");
        botonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarSesionActionPerformed(evt);
            }
        });
        panelLateral.add(botonCerrarSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 985, 296, 62));

        panelRaiz.add(panelLateral, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 1080));

        panelCabecera.setBackground(new java.awt.Color(255, 255, 255));
        panelCabecera.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelHamburguesa.setFont(new java.awt.Font("Dialog", 0, 32)); // NOI18N
        labelHamburguesa.setForeground(new java.awt.Color(0, 20, 43));
        labelHamburguesa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelHamburguesa.setText("☰");
        panelCabecera.add(labelHamburguesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 21, 55, 56));

        labelTituloCabecera.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        labelTituloCabecera.setForeground(new java.awt.Color(0, 20, 43));
        labelTituloCabecera.setText("PANEL DE ADMINISTRACION");
        panelCabecera.add(labelTituloCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 25, 510, 50));

        labelIconoUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/IconoUsuario.png"))); // NOI18N
        labelIconoUsuario.setMantenerProporcion(true);
        panelCabecera.add(labelIconoUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 22, 56, 56));

        labelBienvenido.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        labelBienvenido.setForeground(new java.awt.Color(18, 37, 63));
        labelBienvenido.setText("Bienvenido,");
        panelCabecera.add(labelBienvenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 32, 105, 38));

        labelUsuario.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        labelUsuario.setForeground(new java.awt.Color(18, 37, 63));
        labelUsuario.setText("Administrador");
        panelCabecera.add(labelUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1440, 32, 125, 38));

        separadorCabecera.setForeground(new java.awt.Color(225, 229, 235));
        panelCabecera.add(separadorCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 99, 1580, 1));

        panelRaiz.add(panelCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 1580, 100));

        panelContenido.setBackground(new java.awt.Color(248, 249, 251));
        panelContenido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelRaiz.add(panelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 1580, 980));

        getContentPane().add(panelRaiz, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 1080));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDashboardActionPerformed
        mostrarSeccion(DASHBOARD, botonDashboard);
    }//GEN-LAST:event_botonDashboardActionPerformed

    private void botonUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonUsuariosActionPerformed
        mostrarSeccion(USUARIOS, botonUsuarios);
    }//GEN-LAST:event_botonUsuariosActionPerformed

    private void botonGestionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGestionMenuActionPerformed
        mostrarSeccion(GESTION_MENU, botonGestionMenu);
    }//GEN-LAST:event_botonGestionMenuActionPerformed

    private void botonIngredientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIngredientesActionPerformed
        mostrarSeccion(INGREDIENTES, botonIngredientes);
    }//GEN-LAST:event_botonIngredientesActionPerformed

    private void botonReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonReportesActionPerformed
        mostrarSeccion(REPORTES, botonReportes);
    }//GEN-LAST:event_botonReportesActionPerformed

    private void botonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarSesionActionPerformed
        cerrarSesion();
    }//GEN-LAST:event_botonCerrarSesionActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MenuAdmin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonMenuLateral botonCerrarSesion;
    private Componentes.BotonMenuLateral botonDashboard;
    private Componentes.BotonMenuLateral botonGestionMenu;
    private Componentes.BotonMenuLateral botonIngredientes;
    private Componentes.BotonMenuLateral botonReportes;
    private Componentes.BotonMenuLateral botonUsuarios;
    private javax.swing.JLabel labelBienvenido;
    private javax.swing.JLabel labelHamburguesa;
    private Labels.LabelEscalable labelIconoUsuario;
    private Labels.LabelEscalable labelLogo;
    private javax.swing.JLabel labelMarca;
    private javax.swing.JLabel labelRol;
    private javax.swing.JLabel labelTituloCabecera;
    private javax.swing.JLabel labelUsuario;
    private javax.swing.JPanel panelCabecera;
    private javax.swing.JPanel panelContenido;
    private javax.swing.JPanel panelLateral;
    private javax.swing.JPanel panelRaiz;
    private javax.swing.JSeparator separadorCabecera;
    // End of variables declaration//GEN-END:variables
}
