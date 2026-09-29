    package Componentes;

    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.io.File;
    import java.net.URL;
    import javax.swing.*;
    import javax.swing.border.EmptyBorder;

    public class TarjetaProducto extends JPanel {

        private static final String FUENTE = "Arial";

        private int idProducto;

        private JLabel lblImagen;
        private JLabel lblNombre;
        private JLabel lblDescripcion;
        private JLabel lblPrecio;
        private JButton btnAgregar;
        private JLabel lblEstado;

        private boolean hover = false;
        private boolean disponible;

        public TarjetaProducto(
                int idProducto,
                String nombre,
                String descripcion,
                double precio,
                String imagen
        ) {
            this(idProducto, nombre, descripcion, precio, imagen, true);
        }

        public TarjetaProducto(
                int idProducto,
                String nombre,
                String descripcion,
                double precio,
                String imagen,
                boolean disponible
        ) {
            this.idProducto = idProducto;
            this.disponible = disponible;
            init(nombre, descripcion, precio, imagen);
        }

        private void init(
                String nombre,
                String descripcion,
                double precio,
                String imagen
        ) {

            setOpaque(false);
            Dimension tamano = new Dimension(230, 250);
            setPreferredSize(tamano);
            setMinimumSize(tamano);
            setMaximumSize(tamano);
            setLayout(new BorderLayout(0, 8));
            setBorder(new EmptyBorder(12, 12, 12, 12));
            setCursor(new Cursor(disponible ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));

            // =========================
            // IMAGEN
            // =========================
            lblImagen = new JLabel();
            lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
            lblImagen.setPreferredSize(new Dimension(180, 100));

            cargarImagen(imagen);

            add(lblImagen, BorderLayout.NORTH);

            // =========================
            // CENTRO
            // =========================
            JPanel panelCentro = new JPanel();
            panelCentro.setOpaque(false);
            panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));

            lblNombre = new JLabel(
                    "<html><b>" + nombre + "</b></html>"
            );
            lblNombre.setFont(new Font(FUENTE, Font.BOLD, 17));
            lblNombre.setForeground(new Color(20, 27, 35));
            lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblEstado = new JLabel("No disponible ahora");
            lblEstado.setFont(new Font(FUENTE, Font.BOLD, 11));
            lblEstado.setForeground(new Color(102, 108, 115));
            lblEstado.setOpaque(true);
            lblEstado.setBackground(new Color(232, 235, 238));
            lblEstado.setBorder(new EmptyBorder(4, 7, 4, 7));
            lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblEstado.setVisible(!disponible);

            panelCentro.add(lblNombre);
            panelCentro.add(Box.createVerticalStrut(6));
            panelCentro.add(lblEstado);
            if (!disponible) {
                panelCentro.add(Box.createVerticalStrut(5));
                lblNombre.setForeground(new Color(112, 118, 124));
            }

            if (descripcion == null) {
                descripcion = "";
            }

            lblDescripcion = new JLabel(
                    "<html><div style='width:155px;'>"
                    + descripcion
                    + "</div></html>"
            );
            lblDescripcion.setFont(new Font(FUENTE, Font.PLAIN, 12));
            lblDescripcion.setForeground(disponible
                    ? new Color(95, 95, 95) : new Color(142, 147, 152));
            lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

            panelCentro.add(lblDescripcion);

            add(panelCentro, BorderLayout.CENTER);

            // =========================
            // INFERIOR
            // =========================
            JPanel panelInferior = new JPanel(new BorderLayout());
            panelInferior.setOpaque(false);

            lblPrecio = new JLabel(String.format("Q%.2f", precio));
            lblPrecio.setFont(new Font(FUENTE, Font.BOLD, 18));
            lblPrecio.setForeground(disponible
                    ? new Color(20, 27, 35) : new Color(135, 140, 145));

            btnAgregar = new JButton("Agregar");
            btnAgregar.setFocusPainted(false);
            btnAgregar.setBorderPainted(false);
            btnAgregar.setContentAreaFilled(false);
            btnAgregar.setOpaque(false);
            btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAgregar.setFont(new Font(FUENTE, Font.BOLD, 13));
            btnAgregar.setForeground(new Color(20, 27, 35));
            btnAgregar.setPreferredSize(new Dimension(95, 34));
            btnAgregar.setEnabled(disponible);
            if (!disponible) {
                btnAgregar.setToolTipText("Este producto no está disponible en este horario.");
            }

            panelInferior.add(lblPrecio, BorderLayout.WEST);
            panelInferior.add(btnAgregar, BorderLayout.EAST);

            add(panelInferior, BorderLayout.SOUTH);

            btnAgregar.addActionListener(e -> {
                if (!disponible) {
                    return;
                }
                java.awt.Window ventana = SwingUtilities.getWindowAncestor(this);
                if (ventana instanceof GUI_CAJERO.Cajero cajero) cajero.agregarProducto(idProducto);
            });

            // Hover de tarjeta
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = disponible;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });

            // Hover botón
            btnAgregar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnAgregar.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnAgregar.repaint();
                }
            });
        }

        private void cargarImagen(String ruta) {

            if (ruta == null || ruta.trim().isEmpty()) {
                lblImagen.setText("Sin imagen");
                return;
            }

            try {
                ImageIcon icono = null;

                URL recurso = getClass().getResource(ruta);

                if (recurso != null) {
                    icono = new ImageIcon(recurso);
                } else {
                    File archivo = new File(ruta);
                    if (archivo.exists()) {
                        icono = new ImageIcon(archivo.getAbsolutePath());
                    }
                }

                if (icono == null) {
                    lblImagen.setText("Imagen no encontrada");
                    return;
                }

                Image imagenEscalada = icono.getImage().getScaledInstance(
                        125,
                        95,
                        Image.SCALE_SMOOTH
                );

                lblImagen.setIcon(new ImageIcon(imagenEscalada));

            } catch (Exception e) {
                lblImagen.setText("Sin imagen");
                System.out.println("Error cargando imagen: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // sombra suave
            g2.setColor(new Color(0, 0, 0, 12));
            g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 24, 24);

            // fondo
            if (!disponible) {
                g2.setColor(new Color(242, 243, 244));
            } else if (hover) {
                g2.setColor(new Color(255, 252, 245));
            } else {
                g2.setColor(disponible ? Color.WHITE : new Color(235, 237, 239));
            }
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 24, 24);

            // borde
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 24, 24);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);

            if (btnAgregar != null) {
                Graphics2D g2 = (Graphics2D) g.create();

                Point p = SwingUtilities.convertPoint(
                        btnAgregar.getParent(),
                        btnAgregar.getLocation(),
                        this
                );

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(
                        p.x,
                        p.y,
                        btnAgregar.getWidth(),
                        btnAgregar.getHeight(),
                        22,
                        22
                );

                g2.setColor(disponible
                        ? new Color(255, 188, 13) : new Color(205, 208, 211));
                g2.drawRoundRect(
                        p.x,
                        p.y,
                        btnAgregar.getWidth() - 1,
                        btnAgregar.getHeight() - 1,
                        22,
                        22
                );

                FontMetrics fm = g2.getFontMetrics(btnAgregar.getFont());
                String texto = btnAgregar.getText();
                int tx = p.x + (btnAgregar.getWidth() - fm.stringWidth(texto)) / 2;
                int ty = p.y + ((btnAgregar.getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.setFont(btnAgregar.getFont());
                g2.setColor(disponible
                        ? new Color(20, 27, 35) : new Color(145, 149, 153));
                g2.drawString(texto, tx, ty);

                g2.dispose();
            }
        }

        public int getIdProducto() {
            return idProducto;
        }

        public JButton getBtnAgregar() {
            return btnAgregar;
        }
    }
