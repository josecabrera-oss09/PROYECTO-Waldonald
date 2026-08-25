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
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TextBox_Login extends JPanel {

    // =====================================================
    // TEMAS DISPONIBLES
    // =====================================================
    public enum Tema {
        AMARILLO,
        ROJO
    }

    private Tema tema = Tema.AMARILLO;

    // =====================================================
    // COMPONENTES
    // =====================================================
    private JTextField txtCampo;
    private JLabel lblIcono;

    // =====================================================
    // CONFIGURACIÓN
    // =====================================================
    private String placeholder = "Ingresa texto";

    private boolean mostrandoPlaceholder = true;

    private int radioCaja = 35;
    private int radioIcono = 28;

    // =====================================================
    // COLORES
    // =====================================================

    // Amarillo WalDonald's
    private Color amarilloPrincipal =
            new Color(255, 183, 0);

    private Color amarilloClaro =
            new Color(255, 239, 190);

    // Rojo WalDonald's
    private Color rojoPrincipal =
            new Color(215, 25, 25);

    private Color rojoClaro =
            new Color(255, 205, 205);

    // Caja donde se escribe
    private Color fondoCampo =
            new Color(248, 248, 248);

    private Color texto =
            new Color(35, 35, 35);

    private Color textoPlaceholder =
            new Color(145, 145, 145);


    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public TextBox_Login() {

        setOpaque(false);

        setLayout(null);

        setPreferredSize(
                new Dimension(600, 90)
        );

        setMinimumSize(
                new Dimension(300, 70)
        );


        // =================================================
        // ICONO
        // =================================================
        lblIcono = new JLabel();

        lblIcono.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setVerticalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setOpaque(false);

        add(lblIcono);


        // =================================================
        // CAMPO DE TEXTO
        // =================================================
        txtCampo = new JTextField();

        txtCampo.setText(placeholder);

        txtCampo.setForeground(
                textoPlaceholder
        );

        txtCampo.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        16
                )
        );

        txtCampo.setBorder(
                BorderFactory.createEmptyBorder()
        );

        txtCampo.setOpaque(false);

        txtCampo.setBackground(
                new Color(0, 0, 0, 0)
        );

        txtCampo.setCaretColor(
                texto
        );

        txtCampo.setCursor(
                new Cursor(
                        Cursor.TEXT_CURSOR
                )
        );

        add(txtCampo);


        // =================================================
        // PLACEHOLDER
        // =================================================
        txtCampo.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(
                            FocusEvent e) {

                        if (mostrandoPlaceholder) {

                            txtCampo.setText("");

                            txtCampo.setForeground(
                                    texto
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
                                txtCampo
                                        .getText()
                                        .trim()
                                        .isEmpty()
                        ) {

                            txtCampo.setText(
                                    placeholder
                            );

                            txtCampo.setForeground(
                                    textoPlaceholder
                            );

                            mostrandoPlaceholder =
                                    true;
                        }

                        repaint();
                    }
                }
        );
    }


    // =====================================================
    // POSICIONAR COMPONENTES
    // =====================================================
    @Override
    public void doLayout() {

        super.doLayout();

        int alto = getHeight();

        // El cuadro izquierdo es casi del alto total
        int tamanoIcono =
                Math.min(
                        alto - 8,
                        90
                );

        int yIcono =
                (alto - tamanoIcono) / 2;


        // =================================================
        // CUADRO DEL ICONO
        // =================================================
        lblIcono.setBounds(
                4,
                yIcono,
                tamanoIcono,
                tamanoIcono
        );


        // =================================================
        // TEXTBOX
        // =================================================
        int inicioTexto =
                tamanoIcono + 30;

        txtCampo.setBounds(
                inicioTexto,
                5,
                getWidth()
                        - inicioTexto
                        - 30,
                alto - 10
        );
    }


    // =====================================================
    // PINTAR DISEÑO
    // =====================================================
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        // =================================================
        // SELECCIONAR COLORES
        // =================================================
        Color colorPrincipal;

        Color colorClaro;

        if (tema == Tema.ROJO) {

            colorPrincipal =
                    rojoPrincipal;

            colorClaro =
                    rojoClaro;

        } else {

            colorPrincipal =
                    amarilloPrincipal;

            colorClaro =
                    amarilloClaro;
        }


        int ancho =
                getWidth();

        int alto =
                getHeight();


        int tamanoIcono =
                Math.min(
                        alto - 8,
                        90
                );


        // =================================================
        // CAJA PRINCIPAL
        // =================================================

        int xCaja =
                tamanoIcono / 2;

        int yCaja = 10;

        int anchoCaja =
                ancho
                - xCaja
                - 3;

        int altoCaja =
                alto - 20;


        // Fondo gris/blanco
        g2.setColor(
                fondoCampo
        );

        g2.fillRoundRect(
                xCaja,
                yCaja,
                anchoCaja,
                altoCaja,
                radioCaja,
                radioCaja
        );


        // =================================================
        // BORDE COLOREADO
        // =================================================
        g2.setColor(
                colorPrincipal
        );

        g2.setStroke(
                new BasicStroke(2.5f)
        );


        RoundRectangle2D borde =
                new RoundRectangle2D.Double(
                        xCaja,
                        yCaja,
                        anchoCaja,
                        altoCaja,
                        radioCaja,
                        radioCaja
                );

        g2.draw(borde);


        // =================================================
        // CUADRO IZQUIERDO
        // =================================================

        int xIcono = 3;

        int yIcono =
                (alto - tamanoIcono)
                / 2;


        // Fondo amarillo claro / rojo claro
        g2.setColor(
                colorClaro
        );

        g2.fillRoundRect(
                xIcono,
                yIcono,
                tamanoIcono,
                tamanoIcono,
                radioIcono,
                radioIcono
        );


        // =================================================
        // BORDE DEL CUADRO
        // =================================================
        g2.setColor(
                colorPrincipal
        );

        g2.setStroke(
                new BasicStroke(3f)
        );

        g2.drawRoundRect(
                xIcono,
                yIcono,
                tamanoIcono,
                tamanoIcono,
                radioIcono,
                radioIcono
        );


        // =================================================
        // LÍNEAS DECORATIVAS
        // =================================================

        int inicioDecoracion =
                tamanoIcono + 15;

        int centroDecoracion =
                (int) (ancho * 0.68);

        int finalDecoracion =
                ancho - 45;


        g2.setStroke(
                new BasicStroke(2f)
        );

        g2.setColor(
                colorPrincipal
        );


        // Línea superior izquierda
        g2.drawLine(
                inicioDecoracion,
                10,
                centroDecoracion - 50,
                10
        );


        // Línea superior derecha
        g2.drawLine(
                centroDecoracion + 55,
                10,
                finalDecoracion,
                10
        );


        // Línea inferior izquierda
        g2.drawLine(
                inicioDecoracion,
                alto - 10,
                centroDecoracion - 50,
                alto - 10
        );


        // Línea inferior derecha
        g2.drawLine(
                centroDecoracion + 55,
                alto - 10,
                finalDecoracion,
                alto - 10
        );


        // =================================================
        // PUNTOS DECORATIVOS
        // =================================================

        int cantidadPuntos = 6;

        int separacion = 12;

        int tamanoPunto = 5;


        int inicioPuntos =
                centroDecoracion
                - (
                (
                cantidadPuntos - 1
                )
                * separacion
                / 2
                );


        for (
                int i = 0;
                i < cantidadPuntos;
                i++
        ) {

            int x =
                    inicioPuntos
                    + i * separacion;


            // Superior
            g2.fillOval(
                    x,
                    8,
                    tamanoPunto,
                    tamanoPunto
            );


            // Inferior
            g2.fillOval(
                    x,
                    alto - 13,
                    tamanoPunto,
                    tamanoPunto
            );
        }


        g2.dispose();
    }


    // =====================================================
    // CAMBIAR TEMA
    // =====================================================
    public void setTema(Tema tema) {

        this.tema = tema;

        repaint();
    }


    public Tema getTema() {

        return tema;
    }


    // =====================================================
    // TEXTO
    // =====================================================
    public String getTexto() {

        if (mostrandoPlaceholder) {
            return "";
        }

        return txtCampo.getText();
    }


    public void setTexto(String textoNuevo) {

        if (
                textoNuevo == null
                || textoNuevo.isEmpty()
        ) {

            txtCampo.setText(
                    placeholder
            );

            txtCampo.setForeground(
                    textoPlaceholder
            );

            mostrandoPlaceholder =
                    true;

        } else {

            txtCampo.setText(
                    textoNuevo
            );

            txtCampo.setForeground(
                    texto
            );

            mostrandoPlaceholder =
                    false;
        }
    }


    // =====================================================
    // PLACEHOLDER
    // =====================================================
    public void setPlaceholder(
            String placeholder) {

        this.placeholder =
                placeholder;

        if (mostrandoPlaceholder) {

            txtCampo.setText(
                    placeholder
            );
        }
    }


    public String getPlaceholder() {

        return placeholder;
    }


    // =====================================================
    // ICONO
    // =====================================================
    public void setIcono(Icon icono) {

        lblIcono.setIcon(
                icono
        );
    }


    public Icon getIcono() {

        return lblIcono.getIcon();
    }


    // =====================================================
    // ACCESO AL JTextField
    // =====================================================
    public JTextField getTextField() {

        return txtCampo;
    }


    // =====================================================
    // CAMBIAR COLORES MANUALMENTE
    // =====================================================
    public void setAmarilloPrincipal(
            Color color) {

        amarilloPrincipal =
                color;

        repaint();
    }


    public void setRojoPrincipal(
            Color color) {

        rojoPrincipal =
                color;

        repaint();
    }


    public void setFondoCampo(
            Color color) {

        fondoCampo =
                color;

        repaint();
    }
}