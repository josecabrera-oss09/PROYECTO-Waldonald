
        cambiarContrasena.setText("Cambiar contraseña");
        cambiarContrasena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarContrasenaActionPerformed(evt);
            }
        });
        panelTarjeta.add(cambiarContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 490, 250, 30));

        panelContrasenas.setOpaque(false);
        panelContrasenas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelContrasena.setText("Contraseña");
        panelContrasenas.add(labelContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 22));

        botonVerContrasena.setContentAreaFilled(false);
        botonVerContrasena.setFocusPainted(false);
        botonVerContrasena.setBorderPainted(false);
        botonVerContrasena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/visualizar.png"))); // NOI18N
        botonVerContrasena.setToolTipText("Mostrar contraseña");
        botonVerContrasena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVerContrasenaActionPerformed(evt);
            }
        });
        panelContrasenas.add(botonVerContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 28, 40, 42));
        panelContrasenas.add(campoContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 26, 330, 46));

        labelConfirmar.setText("Confirmar contraseña");
        panelContrasenas.add(labelConfirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 330, 22));

        botonVerConfirmacion.setContentAreaFilled(false);
        botonVerConfirmacion.setFocusPainted(false);
        botonVerConfirmacion.setBorderPainted(false);
        botonVerConfirmacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/visualizar.png"))); // NOI18N
        botonVerConfirmacion.setToolTipText("Mostrar contraseña");
        botonVerConfirmacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVerConfirmacionActionPerformed(evt);
            }
        });
        panelContrasenas.add(botonVerConfirmacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(665, 28, 40, 42));
        panelContrasenas.add(campoConfirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 26, 330, 46));

        panelTarjeta.add(panelContrasenas, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 528, 710, 76));

        labelError.setForeground(new java.awt.Color(231, 55, 65));
        labelError.setText(" ");
        panelTarjeta.add(labelError, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 612, 710, 26));

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
        panelTarjeta.add(botonDesactivar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 650, 180, 48));

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
        panelTarjeta.add(botonCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 650, 130, 48));

        botonGuardar.setDegradado(false);
        botonGuardar.setText("Guardar usuario");
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        panelTarjeta.add(botonGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 650, 165, 48));

        add(panelTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 790, 730));

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
    private javax.swing.JLabel labelTurno;
    private javax.swing.JLabel labelUsuario;
    private javax.swing.JPanel panelContrasenas;
    private Componentes.PanelCircular panelIcono;
    private Componentes.PanelFlotante panelTarjeta;
    private Componentes.BotonDesplegable selectorEstado;
    private Componentes.BotonDesplegable selectorRol;
    private Componentes.BotonDesplegable selectorTurno;
    // End of variables declaration//GEN-END:variables
}