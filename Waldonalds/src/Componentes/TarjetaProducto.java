package Componentes;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TarjetaProducto extends JPanel {

    private JLabel lblImagen;
    private JLabel lblNombre;
    private JTextArea txtDescripcion;
    private JLabel lblPrecio;
    private JButton btnAgregar;

    private int idProducto;

    public TarjetaProducto(
            int idProducto,
            String nombre,
            String descripcion,
            double precio,
            String rutaImagen
    ) {

        this.idProducto = idProducto;

        setPreferredSize(new Dimension(300, 350));
        setBackground(Color.WHITE);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(235, 235, 235),
                        1
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        setLayout(new BorderLayout(10, 10));

        // ==========================
        // IMAGEN
        // ==========================

        lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);

        lblImagen.setPreferredSize(
                new Dimension(250, 170)
        );

        cargarImagen(rutaImagen);

        add(lblImagen, BorderLayout.NORTH);

        // ==========================
        // INFORMACIÓN
        // ==========================

        JPanel panelInformacion = new JPanel();
        panelInformacion.setOpaque(false);

        panelInformacion.setLayout(
                new BoxLayout(
                        panelInformacion,
                        BoxLayout.Y_AXIS
                )
        );

        lblNombre = new JLabel(nombre);

        lblNombre.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        lblNombre.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelInformacion.add(lblNombre);

        panelInformacion.add(
                Box.createVerticalStrut(8)
        );

        txtDescripcion = new JTextArea(descripcion);

        txtDescripcion.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        txtDescripcion.setForeground(
                new Color(80, 80, 80)
        );

        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setFocusable(false);
        txtDescripcion.setOpaque(false);

        txtDescripcion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelInformacion.add(txtDescripcion);

        add(
                panelInformacion,
                BorderLayout.CENTER
        );

        // ==========================
        // PRECIO + BOTÓN
        // ==========================

        JPanel panelInferior = new JPanel(
                new BorderLayout()
        );

        panelInferior.setOpaque(false);

        lblPrecio = new JLabel(
                String.format("$%.2f", precio)
        );

        lblPrecio.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        btnAgregar = new JButton("Agregar");

        btnAgregar.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        btnAgregar.setBackground(Color.WHITE);

        btnAgregar.setForeground(
                new Color(20, 20, 20)
        );

        btnAgregar.setFocusPainted(false);

        btnAgregar.setBorder(
                BorderFactory.createLineBorder(
                        new Color(255, 188, 13),
                        2
                )
        );

        btnAgregar.setPreferredSize(
                new Dimension(100, 36)
        );

        panelInferior.add(
                lblPrecio,
                BorderLayout.WEST
        );

        panelInferior.add(
                btnAgregar,
                BorderLayout.EAST
        );

        add(
                panelInferior,
                BorderLayout.SOUTH
        );

        // ==========================
        // EVENTO AGREGAR
        // ==========================

        btnAgregar.addActionListener(e -> {

            System.out.println(
                    "Producto agregado: "
                    + idProducto
            );

        });
    }

    // ==========================
    // CARGAR IMAGEN
    // ==========================

    private void cargarImagen(String rutaImagen) {

        try {

            if (rutaImagen == null
                    || rutaImagen.isBlank()) {

                lblImagen.setText("Sin imagen");
                return;
            }

            URL url = getClass()
                    .getResource(rutaImagen);

            if (url == null) {

                lblImagen.setText(
                        "Imagen no encontrada"
                );

                return;
            }

            ImageIcon iconoOriginal =
                    new ImageIcon(url);

            Image imagenEscalada =
                    iconoOriginal
                            .getImage()
                            .getScaledInstance(
                                    220,
                                    160,
                                    Image.SCALE_SMOOTH
                            );

            lblImagen.setIcon(
                    new ImageIcon(imagenEscalada)
            );

        } catch (Exception e) {

            lblImagen.setText("Sin imagen");

            System.out.println(
                    "Error cargando imagen: "
                    + e.getMessage()
            );
        }
    }

    public int getIdProducto() {
        return idProducto;
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }
}