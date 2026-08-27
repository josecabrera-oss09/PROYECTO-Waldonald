package Labels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class Textbox_Contrasena extends JPanel {

    // ==========================================
    // COMPONENTES
    // ==========================================
    private JPasswordField txtPassword;
    private JLabel lblIcono;


    // ==========================================
    // PLACEHOLDER
    // ==========================================
    private String placeholder = "Ingresa contraseña";

    private boolean mostrandoPlaceholder = true;


    // ==========================================
    // COLORES
    // ==========================================
    private final Color rojo =
            new Color(215, 25, 25);

    private final Color rojoClaro =
            new Color(255, 205, 205);

    private final Color fondoCampo =
            new Color(248, 248, 248);

    private final Color colorTexto =
            new Color(35, 35, 35);

    private final Color colorPlaceholder =
            new Color(145, 145, 145);


    // ==========================================
    // CARÁCTER DE CONTRASEÑA
    // ==========================================
    private char caracterPassword;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public Textbox_Contrasena() {

        setOpaque(false);

        setLayout(null);

        setPreferredSize(
                new Dimension(600, 90)
        );

        setMinimumSize(
                new Dimension(300, 70)
        );


        // ======================================
        // ICONO
        // ======================================
        lblIcono = new JLabel();

        lblIcono.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setVerticalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setOpaque(false);

        add(lblIcono);


        // ======================================
        // CAMPO DE CONTRASEÑA
        // ======================================
        txtPassword =
                new JPasswordField();

        caracterPassword =
                txtPassword.getEchoChar();


        // Placeholder visible
        txtPassword.setEchoChar(
                (char) 0
        );

        txtPassword.setText(
                placeholder
        );

        txtPassword.setForeground(
                colorPlaceholder
        );

        txtPassword.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        16
                )
        );

        txtPassword.setBorder(
                BorderFactory.createEmptyBorder()
        );

        txtPassword.setOpaque(false);

        txtPassword.setCaretColor(
                colorTexto
        );

        txtPassword.setCursor(
                new Cursor(
                        Cursor.TEXT_CURSOR
                )
        );

        add(txtPassword);


        // ======================================
        // PLACEHOLDER
        // ======================================
        txtPassword.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(
                            FocusEvent e) {

                        if (mostrandoPlaceholder) {

                            txtPassword.setText("");

                            txtPassword.setEchoChar(
                                    caracterPassword
                            );

                            txtPassword.setForeground(
                                    colorTexto
                            );

                            mostrandoPlaceholder =
                                    false;
                        }

                        repaint();
                    }


                    @Override
                    public void focusLost(
                            FocusEvent e) {

                        if (
                                txtPassword
                                        .getPassword()
                                        .length == 0
                        ) {

                            txtPassword.setEchoChar(
                                    (char) 0
                            );

                            txtPassword.setText(
                                    placeholder
                            );

                            txtPassword.setForeground(
                                    colorPlaceholder
                            );

                            mostrandoPlaceholder =
                                    true;
                        }

                        repaint();
                    }
                }
        );
    }


    // ==========================================
    // POSICIONAR COMPONENTES
    // ==========================================
    @Override
    public void doLayout() {

        super.doLayout();

        int alto =
                getHeight();

        int tamanoCuadro =
                Math.min(
                        alto - 8,
                        90
                );

        int y =
                (alto - tamanoCuadro)
                / 2;


        // Icono
        lblIcono.setBounds(
                4,
                y,
                tamanoCuadro,
                tamanoCuadro
        );


        // Campo password
        int inicioTexto =
                tamanoCuadro + 25;

        txtPassword.setBounds(
                inicioTexto,
                10,
                getWidth()
                        - inicioTexto
                        - 30,
                alto - 20
        );
    }


    // ==========================================
    // DIBUJAR COMPONENTE
    // ==========================================
    @Override
    protected void paintComponent(
            Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        int ancho =
                getWidth();

        int alto =
                getHeight();

        int tamanoCuadro =
                Math.min(
                        alto - 8,
                        90
                );


        // ======================================
        // CAJA PRINCIPAL
        // ======================================
        int xCaja =
                tamanoCuadro / 2;

        int yCaja =
                10;

        int anchoCaja =
                ancho
                - xCaja
                - 3;

        int altoCaja =
                alto - 20;


        // Fondo gris claro
        g2.setColor(
                fondoCampo
        );

        g2.fillRoundRect(
                xCaja,
                yCaja,
                anchoCaja,
                altoCaja,
                35,
                35
        );


        // Borde rojo
        g2.setColor(
                rojo
        );

        g2.setStroke(
                new BasicStroke(2.5f)
        );

        g2.drawRoundRect(
                xCaja,
                yCaja,
                anchoCaja,
                altoCaja,
                35,
                35
        );


        // ======================================
        // CUADRO IZQUIERDO
        // ======================================
        int xCuadro =
                3;

        int yCuadro =
                (alto - tamanoCuadro)
                / 2;


        // Fondo rojo claro
        g2.setColor(
                rojoClaro
        );

        g2.fillRoundRect(
                xCuadro,
                yCuadro,
                tamanoCuadro,
                tamanoCuadro,
                28,
                28
        );


        // Borde rojo
        g2.setColor(
                rojo
        );

        g2.setStroke(
                new BasicStroke(3f)
        );

        g2.drawRoundRect(
                xCuadro,
                yCuadro,
                tamanoCuadro,
                tamanoCuadro,
                28,
                28
        );


        // ======================================
        // LÍNEAS DECORATIVAS
        // ======================================
        int inicioLinea =
                tamanoCuadro + 15;

        int centro =
                (int) (
                        ancho * 0.70
                );

        int finalLinea =
                ancho - 45;


        g2.setStroke(
                new BasicStroke(2f)
        );

        g2.setColor(
                rojo
        );


        // Línea superior izquierda
        g2.drawLine(
                inicioLinea,
                10,
                centro - 50,
                10
        );


        // Línea superior derecha
        g2.drawLine(
                centro + 55,
                10,
                finalLinea,
                10
        );


        // Línea inferior izquierda
        g2.drawLine(
                inicioLinea,
                alto - 10,
                centro - 50,
                alto - 10
        );


        // Línea inferior derecha
        g2.drawLine(
                centro + 55,
                alto - 10,
                finalLinea,
                alto - 10
        );


        // ======================================
        // PUNTOS DECORATIVOS
        // ======================================
        int cantidad =
                6;

        int separacion =
                11;

        int inicioPuntos =
                centro - 28;


        for (
                int i = 0;
                i < cantidad;
                i++
        ) {

            int x =
                    inicioPuntos
                    + (
                    i * separacion
                    );


            // Puntos superiores
            g2.fillOval(
                    x,
                    7,
                    5,
                    5
            );


            // Puntos inferiores
            g2.fillOval(
                    x,
                    alto - 12,
                    5,
                    5
            );
        }


        g2.dispose();
    }


    // ==========================================
    // OBTENER CONTRASEÑA
    // ==========================================
    public String getTexto() {

        if (mostrandoPlaceholder) {

            return "";
        }

        return new String(
                txtPassword.getPassword()
        );
    }


    // ==========================================
    // CAMBIAR CONTRASEÑA
    // ==========================================
    public void setTexto(
            String texto) {

        if (
                texto == null
                || texto.isEmpty()
        ) {

            txtPassword.setEchoChar(
                    (char) 0
            );

            txtPassword.setText(
                    placeholder
            );

            txtPassword.setForeground(
                    colorPlaceholder
            );

            mostrandoPlaceholder =
                    true;

        } else {

            txtPassword.setEchoChar(
                    caracterPassword
            );

            txtPassword.setText(
                    texto
            );

            txtPassword.setForeground(
                    colorTexto
            );

            mostrandoPlaceholder =
                    false;
        }
    }


    // ==========================================
    // CAMBIAR PLACEHOLDER
    // ==========================================
    public void setPlaceholder(
            String placeholder) {

        this.placeholder =
                placeholder;

        if (mostrandoPlaceholder) {

            txtPassword.setText(
                    placeholder
            );
        }
    }


    // ==========================================
    // OBTENER PLACEHOLDER
    // ==========================================
    public String getPlaceholder() {

        return placeholder;
    }


    // ==========================================
    // COLOCAR ICONO
    // ==========================================
    public void setIcono(
            Icon icono) {

        lblIcono.setIcon(
                icono
        );
    }


    // ==========================================
    // OBTENER ICONO
    // ==========================================
    public Icon getIcono() {

        return lblIcono.getIcon();
    }


    // ==========================================
    // OBTENER JPASSWORDFIELD
    // ==========================================
    public JPasswordField getPasswordField() {

        return txtPassword;
    }


    // ==========================================
    // MOSTRAR / OCULTAR CONTRASEÑA
    // ==========================================
    public void mostrarPassword(
            boolean mostrar) {

        if (mostrandoPlaceholder) {

            return;
        }

        if (mostrar) {

            txtPassword.setEchoChar(
                    (char) 0
            );

        } else {

            txtPassword.setEchoChar(
                    caracterPassword
            );
        }
    }

    public void setText(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getPassword() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}