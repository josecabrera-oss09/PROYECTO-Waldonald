/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Login;

import Conexion.Conexion;

import GUI_ADMINISTRADOR.Menu_Administrador;
import GUI_CAJERO.Cajero;

import java.awt.Font;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

import javax.swing.JOptionPane;

/**
 *
 * @author Computacion
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();

        jLabel1.setFont(cargarFuente("DMSans-Bold.ttf", 28f));

        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        javax.swing.SwingUtilities.invokeLater(() -> {

            Utilidades.Escalador.escalar(
                    getContentPane()
            );

        });
    }

    private Font cargarFuente(String archivo, float tamaño) {
        try {
            InputStream fuente = getClass().getResourceAsStream(
                    "/Font/DMSans/" + archivo
            );

            if (fuente == null) {
                System.out.println("No se encontró la fuente: " + archivo);
                return new Font("Arial", Font.PLAIN, (int) tamaño);
            }

            return Font.createFont(Font.TRUETYPE_FONT, fuente)
                    .deriveFont(tamaño);

        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) tamaño);
        }
    }

    private String sha256(String contraseña) {

        try {

            MessageDigest digest
                    = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    contraseña.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder resultado = new StringBuilder();

            for (byte b : hash) {

                resultado.append(
                        String.format("%02x", b)
                );
            }

            return resultado.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    private void iniciarSesion() {

        // Obtener datos de los campos
        String usuario = texBox1.getText().trim();
        String contraseña = new String(texBoxPassword1.getPassword());

        // Verificar campos vacíos
        if (usuario.isEmpty() || contraseña.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Por favor ingrese su usuario y contraseña.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Conectar a MySQL
        Connection conexion = Conexion.conectar();

        if (conexion == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar con la base de datos.",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Consulta
        String sql = """
                 SELECT id_usuario,
                        nombre,
                        apellido,
                        rol
                 FROM usuario
                 WHERE usuario = ?
                   AND password_hash = ?
                   AND estado = TRUE
                 """;

        try (
                Connection con = conexion; PreparedStatement ps = con.prepareStatement(sql)) {

            // Mandar usuario y contraseña
            ps.setString(1, usuario);
            ps.setString(2, sha256(contraseña));

            try (ResultSet rs = ps.executeQuery()) {

                // Si encontró el usuario
                if (rs.next()) {

                    int idUsuario = rs.getInt("id_usuario");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String rol = rs.getString("rol");

                    System.out.println("Sesión iniciada");
                    System.out.println("ID: " + idUsuario);
                    System.out.println("Nombre: " + nombre + " " + apellido);
                    System.out.println("Rol: " + rol);

                    // ADMINISTRADOR
                    if (rol.equalsIgnoreCase("ADMINISTRADOR")) {

                        Menu_Administrador ventanaAdministrador
                                = new Menu_Administrador();

                        ventanaAdministrador.setLocationRelativeTo(null);
                        ventanaAdministrador.setVisible(true);

                        dispose();
                    } // CAJERO
                    else if (rol.equalsIgnoreCase("CAJERO")) {

                        Cajero ventanaCajero
                                = new Cajero();

                        ventanaCajero.setLocationRelativeTo(null);
                        ventanaCajero.setVisible(true);

                        dispose();
                    }

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Usuario o contraseña incorrectos.",
                            "Inicio de sesión",
                            JOptionPane.ERROR_MESSAGE
                    );

                    texBoxPassword1.setText("");
                    texBoxPassword1.requestFocus();
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ocurrió un error al iniciar sesión.\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
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
        jLabel1 = new javax.swing.JLabel();
        botonDerretido1 = new Componentes.BotonDerretido();
        labelEscalable3 = new Labels.LabelEscalable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        labelEscalable2 = new Labels.LabelEscalable();
        labelEscalable1 = new Labels.LabelEscalable();
        textBox_Login2 = new Labels.TextBox_Login();
        textbox_Contrasena1 = new Labels.Textbox_Contrasena();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Contraseña");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 520, 240, 40));

        botonDerretido1.setForeground(new java.awt.Color(0, 0, 0));
        botonDerretido1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        botonDerretido1.setLabel("Iniciar Sesion");
        botonDerretido1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDerretido1ActionPerformed(evt);
            }
        });
        jPanel1.add(botonDerretido1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 660, 440, 80));

        labelEscalable3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Panel2.png"))); // NOI18N
        labelEscalable3.setText("labelEscalable3");
        jPanel1.add(labelEscalable3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 670, 1080));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Usuario");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 372, 190, 40));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("¡Bienvenido a Waldonald's!");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 270, 730, 60));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/visualizar.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        jButton2.setMaximumSize(new java.awt.Dimension(40, 40));
        jButton2.setMinimumSize(new java.awt.Dimension(40, 40));
        jButton2.setPreferredSize(new java.awt.Dimension(40, 40));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1510, 570, 60, 50));

        labelEscalable2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/MiniLogo 2.png"))); // NOI18N
        labelEscalable2.setText("labelEscalable2");
        jPanel1.add(labelEscalable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 80, 140, 180));

        labelEscalable1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Usuarios.png"))); // NOI18N
        labelEscalable1.setText("labelEscalable1");
        jPanel1.add(labelEscalable1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 420, 50, 50));

        textBox_Login2.setPlaceholder("Ingresa tu usuario");
        jPanel1.add(textBox_Login2, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 400, -1, -1));
        jPanel1.add(textbox_Contrasena1, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 550, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-20, 0, 1940, 1090));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void botonDerretido1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDerretido1ActionPerformed
        iniciarSesion();
    }//GEN-LAST:event_botonDerretido1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonDerretido1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private Labels.LabelEscalable labelEscalable1;
    private Labels.LabelEscalable labelEscalable2;
    private Labels.LabelEscalable labelEscalable3;
    private Labels.TextBox_Login textBox_Login2;
    private Labels.Textbox_Contrasena textbox_Contrasena1;
    // End of variables declaration//GEN-END:variables
}
