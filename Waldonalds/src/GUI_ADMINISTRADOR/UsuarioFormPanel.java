package GUI_ADMINISTRADOR;

import CRUD.UsuarioCRUD;
import Modelos.Usuario;
import Utilidades.SeguridadContrasena;
import Utilidades.SesionUsuario;
import java.awt.Color;
import java.awt.Cursor;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Contenido visual editable desde Design para agregar y editar usuarios.
 * UsuarioFormDialog sólo lo coloca dentro de una ventana modal.
 */
@SuppressWarnings({"serial", "this-escape"})
public class UsuarioFormPanel extends javax.swing.JPanel {

    public static final String EVENTO_GUARDADO = "usuarioGuardado";
    public static final String EVENTO_CANCELAR = "cancelarFormulario";

    private static final Color BORDE = new Color(222, 227, 234);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final Pattern CORREO_VALIDO = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    private UsuarioCRUD crud;
    private Usuario original;
    private String rolSeleccionado;
    private boolean estadoSeleccionado = true;
    private boolean contrasenaVisible;
    private boolean confirmacionVisible;

    /** Constructor vacío requerido por el diseñador de NetBeans. */
    public UsuarioFormPanel() {
        this(null, null);
    }

    public UsuarioFormPanel(UsuarioCRUD crud, Usuario original) {
        this.crud = crud;
        this.original = original;
        initComponents();
        configurarSelectores();
        cargarModo();
    }

    private void configurarSelectores() {
        selectorRol.addMenuOpcionListener(evento -> {
            rolSeleccionado = evento.getActionCommand();
            selectorRol.setText(rolSeleccionado);
        });
        selectorEstado.addMenuOpcionListener(evento -> {
            estadoSeleccionado = "Activo".equals(evento.getActionCommand());
            selectorEstado.setText(evento.getActionCommand());
        });
    }

    private void cargarModo() {
        boolean editando = original != null;
        // Los iconos provienen del archivo .form. Aquí solamente elegimos
        // cuál de los dos JLabel diseñados en NetBeans debe mostrarse.
        labelIconoAgregar.setVisible(!editando);
        labelIconoEditar.setVisible(editando);
        labelTitulo.setText(editando ? "Editar usuario" : "Agregar usuario");
        labelSubtitulo.setText(editando
                ? "Actualiza la información de "
                        + String.format("#%04d", original.getIdUsuario())
                : "Completa los datos del nuevo usuario");

        cambiarContrasena.setVisible(editando);
        panelContrasenas.setVisible(!editando);
        botonDesactivar.setVisible(editando && original.isActivo());

        if (!editando) {
            return;
        }
        campoNombre.setText(original.getNombre());
        campoApellido.setText(original.getApellido());
        campoUsuario.setText(original.getNombreUsuario());
        campoCorreo.setText(original.getCorreo());
        rolSeleccionado = original.getRol();
        estadoSeleccionado = original.isActivo();
        selectorRol.setText(rolSeleccionado);
        selectorEstado.setText(estadoSeleccionado ? "Activo" : "Inactivo");
    }

    private void guardar() {
        limpiarErrores();
        if (crud == null) {
            mostrarError("El formulario no tiene conexión CRUD configurada.");
            return;
        }

        String nombre = campoNombre.getText().trim();
        String apellido = campoApellido.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String correo = campoCorreo.getText().trim().toLowerCase();

        if (nombre.isBlank() || apellido.isBlank() || usuario.isBlank()
                || correo.isBlank() || rolSeleccionado == null) {
            mostrarError("Completa todos los campos obligatorios.");
            return;
        }
        if (nombre.length() > 50 || apellido.length() > 50
                || usuario.length() > 50 || correo.length() > 120) {
            mostrarError("Uno de los campos supera la longitud permitida.");
            return;
        }
        if (!usuario.matches("[A-Za-z0-9._-]{4,50}")) {
            marcarError(campoUsuario);
            mostrarError("El usuario debe tener 4 caracteres o más y no llevar espacios.");
            return;
        }
        if (!CORREO_VALIDO.matcher(correo).matches()) {
            marcarError(campoCorreo);
            mostrarError("Ingresa un correo electrónico válido.");
            return;
        }

        boolean requiereContrasena = original == null
                || cambiarContrasena.isSelected();
        String contrasena = new String(campoContrasena.getPassword());
        String confirmacion = new String(campoConfirmar.getPassword());
        if (requiereContrasena && contrasena.length() < 8) {
            marcarError(campoContrasena);
            mostrarError("La contraseña debe tener al menos 8 caracteres.");
            return;
        }
        if (requiereContrasena && !contrasena.equals(confirmacion)) {
            marcarError(campoConfirmar);
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        int id = original == null ? 0 : original.getIdUsuario();
        botonGuardar.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (crud.existeNombreUsuario(usuario, id)) {
                marcarError(campoUsuario);
                mostrarError("Ese nombre de usuario ya está registrado.");
                return;
            }
            if (crud.existeCorreo(correo, id)) {
                marcarError(campoCorreo);
                mostrarError("Ese correo electrónico ya está registrado.");
                return;
            }
            if (dejaAlSistemaSinAdministrador()) {
                mostrarError("Debe existir al menos un administrador activo.");
                return;
            }
            // Esta protección sólo aplica al editar una cuenta existente.
            // Durante un alta, tanto el ID temporal como una sesión no
            // iniciada pueden valer 0 y no representan a la misma persona.
            if (original != null
                    && id == SesionUsuario.getIdUsuario()
                    && !estadoSeleccionado) {
                mostrarError("No puedes desactivar tu propia cuenta.");
                return;
            }

            Usuario datos = new Usuario(
                    id, nombre, apellido, usuario, correo,
                    rolSeleccionado, estadoSeleccionado,
                    original == null ? LocalDateTime.now()
                            : original.getFechaCreacion());
            String hash = requiereContrasena
                    ? SeguridadContrasena.sha256(contrasena) : null;

            if (original == null) {
                crud.insertar(datos, hash);
            } else {
                crud.actualizar(datos, hash);
            }
            firePropertyChange(EVENTO_GUARDADO, false, true);
        } catch (SQLException ex) {
            mostrarError("No fue posible guardar: " + ex.getMessage());
        } finally {
            botonGuardar.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private boolean dejaAlSistemaSinAdministrador() throws SQLException {
        if (original == null || !original.isActivo()
                || !"ADMINISTRADOR".equals(original.getRol())) {
            return false;
        }
        boolean dejaDeSerAdministrador = !estadoSeleccionado
                || !"ADMINISTRADOR".equals(rolSeleccionado);
        return dejaDeSerAdministrador
                && crud.contarAdministradoresActivos() <= 1;
    }

    private void desactivar() {
        if (original == null || crud == null) {
            return;
        }
        if (original.getIdUsuario() == SesionUsuario.getIdUsuario()) {
            mostrarError("No puedes desactivar tu propia cuenta.");
            return;
        }
        try {
            if ("ADMINISTRADOR".equals(original.getRol())
                    && crud.contarAdministradoresActivos() <= 1) {
                mostrarError("No puedes desactivar al último administrador.");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desactivar a " + original.getNombreCompleto()
                            + "?\nSus pedidos se conservarán.",
                    "Confirmar desactivación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (respuesta == JOptionPane.YES_OPTION) {
                crud.desactivar(original.getIdUsuario());
                firePropertyChange(EVENTO_GUARDADO, false, true);
            }
        } catch (SQLException ex) {
            mostrarError("No fue posible desactivar: " + ex.getMessage());
        }
    }

    private void limpiarErrores() {
        JTextField[] campos = {
            campoNombre, campoApellido, campoUsuario, campoCorreo,
            campoContrasena, campoConfirmar
        };
        for (JTextField campo : campos) {
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE, 1, true),
                    new EmptyBorder(0, 13, 0, 13)));
        }
        labelError.setText(" ");
    }

    private void marcarError(JTextField campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROJO, 1, true),
                new EmptyBorder(0, 13, 0, 13)));
    }

    private void mostrarError(String mensaje) {
        labelError.setText(mensaje);
    }

    private void alternarContrasena(JPasswordField campo, boolean confirmacion) {
        if (confirmacion) {
            confirmacionVisible = !confirmacionVisible;
            campo.setEchoChar(confirmacionVisible ? (char) 0 : '•');
        } else {
            contrasenaVisible = !contrasenaVisible;
            campo.setEchoChar(contrasenaVisible ? (char) 0 : '•');
        }
    }

    private void cambiarContrasenaActionPerformed(
            java.awt.event.ActionEvent evt) {
        panelContrasenas.setVisible(cambiarContrasena.isSelected());
        revalidate();
        repaint();
    }

    private void botonVerContrasenaActionPerformed(
            java.awt.event.ActionEvent evt) {
        alternarContrasena(campoContrasena, false);
    }

    private void botonVerConfirmacionActionPerformed(
            java.awt.event.ActionEvent evt) {
        alternarContrasena(campoConfirmar, true);
    }

    private void botonDesactivarActionPerformed(
            java.awt.event.ActionEvent evt) {
        desactivar();
    }

    private void botonCancelarActionPerformed(
            java.awt.event.ActionEvent evt) {
        firePropertyChange(EVENTO_CANCELAR, false, true);
    }

    private void botonGuardarActionPerformed(
            java.awt.event.ActionEvent evt) {
        guardar();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTarjeta = new Componentes.PanelFlotante();
        panelIcono = new Componentes.PanelCircular();
        labelIconoAgregar = new Labels.LabelEscalable();
        labelIconoEditar = new Labels.LabelEscalable();
        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        labelNombre = new javax.swing.JLabel();
        campoNombre = new javax.swing.JTextField();
        labelApellido = new javax.swing.JLabel();
        campoApellido = new javax.swing.JTextField();
        labelUsuario = new javax.swing.JLabel();
        campoUsuario = new javax.swing.JTextField();
        labelCorreo = new javax.swing.JLabel();
        campoCorreo = new javax.swing.JTextField();
        labelRol = new javax.swing.JLabel();
        selectorRol = new Componentes.BotonDesplegable();
        labelEstado = new javax.swing.JLabel();
        selectorEstado = new Componentes.BotonDesplegable();
        cambiarContrasena = new javax.swing.JCheckBox();
        panelContrasenas = new javax.swing.JPanel();
        labelContrasena = new javax.swing.JLabel();
        campoContrasena = new javax.swing.JPasswordField();
        botonVerContrasena = new javax.swing.JButton();
        labelConfirmar = new javax.swing.JLabel();
        campoConfirmar = new javax.swing.JPasswordField();
        botonVerConfirmacion = new javax.swing.JButton();
        labelError = new javax.swing.JLabel();
        botonDesactivar = new Componentes.BotonRedondeado();
        botonCancelar = new Componentes.BotonRedondeado();
        botonGuardar = new Componentes.BotonRedondeado();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(790, 660));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelTarjeta.setColorBorde(new java.awt.Color(222, 227, 234));
        panelTarjeta.setRadio(26);
        panelTarjeta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelIcono.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelIconoAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_agregarusuario.png"))); // NOI18N
        panelIcono.add(labelIconoAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 80));

        labelIconoEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_editarUsuario.png"))); // NOI18N
        labelIconoEditar.setVisible(false);
        panelIcono.add(labelIconoEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 80));

        panelTarjeta.add(panelIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 35, 80, 80));

        labelTitulo.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(0, 20, 43));
        labelTitulo.setText("Agregar usuario");
        panelTarjeta.add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 35, 560, 35));

        labelSubtitulo.setText("Completa los datos del nuevo usuario");
        panelTarjeta.add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 68, 560, 30));

        labelNombre.setText("Nombre");
        panelTarjeta.add(labelNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 330, 24));
        panelTarjeta.add(campoNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 158, 330, 46));

        labelApellido.setText("Apellido");
        panelTarjeta.add(labelApellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 130, 330, 24));
        panelTarjeta.add(campoApellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 158, 330, 46));

        labelUsuario.setText("Usuario");
        panelTarjeta.add(labelUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 330, 24));
        panelTarjeta.add(campoUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 248, 330, 46));

        labelCorreo.setText("Correo electrónico");
        panelTarjeta.add(labelCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 220, 330, 24));
        panelTarjeta.add(campoCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 248, 330, 46));

        labelRol.setText("Rol");
        panelTarjeta.add(labelRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 330, 24));

        selectorRol.setForeground(new java.awt.Color(0, 20, 43));
        selectorRol.setText("Seleccionar rol");
        selectorRol.setTextoDesplegable("ADMINISTRADOR;CAJERO");
        panelTarjeta.add(selectorRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 338, 330, 46));

        labelEstado.setText("Estado");
        panelTarjeta.add(labelEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 310, 330, 24));

        selectorEstado.setForeground(new java.awt.Color(0, 20, 43));
        selectorEstado.setText("Activo");
        selectorEstado.setTextoDesplegable("Activo;Inactivo");
        panelTarjeta.add(selectorEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 338, 330, 46));

        cambiarContrasena.setText("Cambiar contraseña");
        cambiarContrasena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarContrasenaActionPerformed(evt);
            }
        });
        panelTarjeta.add(cambiarContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 400, 250, 30));

        panelContrasenas.setOpaque(false);
        panelContrasenas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelContrasena.setText("Contraseña");
        panelContrasenas.add(labelContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 22));
        panelContrasenas.add(campoContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 26, 330, 46));

        botonVerContrasena.setContentAreaFilled(false);
        botonVerContrasena.setFocusPainted(false);
        botonVerContrasena.setBorderPainted(false);
        botonVerContrasena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/visualizar.png"))); // NOI18N
        botonVerContrasena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVerContrasenaActionPerformed(evt);
            }
        });
        panelContrasenas.add(botonVerContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 28, 40, 42));

        labelConfirmar.setText("Confirmar contraseña");
        panelContrasenas.add(labelConfirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 330, 22));
        panelContrasenas.add(campoConfirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 26, 330, 46));

        botonVerConfirmacion.setContentAreaFilled(false);
        botonVerConfirmacion.setFocusPainted(false);
        botonVerConfirmacion.setBorderPainted(false);
        botonVerConfirmacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/visualizar.png"))); // NOI18N
        botonVerConfirmacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVerConfirmacionActionPerformed(evt);
            }
        });
        panelContrasenas.add(botonVerConfirmacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(665, 28, 40, 42));

        panelTarjeta.add(panelContrasenas, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 438, 710, 76));

        labelError.setForeground(new java.awt.Color(231, 55, 65));
        labelError.setText(" ");
        panelTarjeta.add(labelError, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 528, 710, 26));

        botonDesactivar.setForeground(new java.awt.Color(231, 55, 65));
        botonDesactivar.setText("Desactivar usuario");
        botonDesactivar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonDesactivar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonDesactivar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonDesactivar.setGrosorBorde(1.0F);
        botonDesactivar.setDegradado(false);
        botonDesactivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDesactivarActionPerformed(evt);
            }
        });
        panelTarjeta.add(botonDesactivar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 575, 180, 48));

        botonCancelar.setForeground(new java.awt.Color(231, 55, 65));
        botonCancelar.setText("Cancelar");
        botonCancelar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonCancelar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonCancelar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonCancelar.setGrosorBorde(1.0F);
        botonCancelar.setDegradado(false);
        botonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCancelarActionPerformed(evt);
            }
        });
        panelTarjeta.add(botonCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 575, 130, 48));

        botonGuardar.setText("Guardar usuario");
        botonGuardar.setDegradado(false);
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        panelTarjeta.add(botonGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 575, 165, 48));

        add(panelTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 660));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonRedondeado botonCancelar;
    private Componentes.BotonRedondeado botonDesactivar;
    private Componentes.BotonRedondeado botonGuardar;
    private javax.swing.JButton botonVerConfirmacion;
    private javax.swing.JButton botonVerContrasena;
    private javax.swing.JCheckBox cambiarContrasena;
    private javax.swing.JTextField campoApellido;
    private javax.swing.JPasswordField campoConfirmar;
    private javax.swing.JPasswordField campoContrasena;
    private javax.swing.JTextField campoCorreo;
    private javax.swing.JTextField campoNombre;
    private javax.swing.JTextField campoUsuario;
    private javax.swing.JLabel labelApellido;
    private javax.swing.JLabel labelConfirmar;
    private javax.swing.JLabel labelContrasena;
    private javax.swing.JLabel labelCorreo;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelEstado;
    private Labels.LabelEscalable labelIconoAgregar;
    private Labels.LabelEscalable labelIconoEditar;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JLabel labelRol;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelUsuario;
    private javax.swing.JPanel panelContrasenas;
    private Componentes.PanelCircular panelIcono;
    private Componentes.PanelFlotante panelTarjeta;
    private Componentes.BotonDesplegable selectorEstado;
    private Componentes.BotonDesplegable selectorRol;
    // End of variables declaration//GEN-END:variables
}
