package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class BotonLetras extends JButton {

    private boolean mouseEncima = false;
    private boolean seleccionado = false;

    // Fondo normal
    private final Color colorFondoNormal = Color.WHITE;

    // Borde naranja cuando está normal
private final Color bordeNormal = new Color(235, 235, 235);

    // Amarillo al pasar el mouse
    private final Color colorAmarillo = new Color(255, 188, 13);

    // Texto
    private final Color colorTexto = new Color(30, 30, 30);

    // Esquinas
    private int radio = 22;

    public BotonLetras() {

        setText("Categoría");

        setPreferredSize(new Dimension(105, 105));

        setFont(new Font("Arial", Font.BOLD, 12));
        setForeground(colorTexto);

        // Imagen arriba y texto abajo
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);

        setIconTextGap(4);

        // Quitar diseño predeterminado
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        // Cursor de mano
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Detectar cuando entra y sale el mouse
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEncima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseEncima = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();


        // CUANDO EL MOUSE ESTÁ ENCIMA
 

        if (mouseEncima || seleccionado) {

            // Fondo amarillo
            g2.setColor(colorAmarillo);

            g2.fillRoundRect(
                    2,
                    2,
                    ancho - 4,
                    alto - 4,
                    radio,
                    radio
            );

            // Borde amarillo
            g2.setColor(colorAmarillo);
            g2.setStroke(new BasicStroke(2.5f));

        } else {

    
            // ESTADO NORMAL
      

            // Fondo blanco
            g2.setColor(colorFondoNormal);

            g2.fillRoundRect(
                    2,
                    2,
                    ancho - 4,
                    alto - 4,
                    radio,
                    radio
            );

            // Borde naranja
            g2.setColor(bordeNormal);
            g2.setStroke(new BasicStroke(2f));
        }

        // Dibujar borde
        g2.drawRoundRect(
                2,
                2,
                ancho - 5,
                alto - 5,
                radio,
                radio
        );

        g2.dispose();

        // Dibuja imagen y texto encima
        super.paintComponent(g);
    }

    // Mantener seleccionado
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        repaint();
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    // Cambiar redondeado
    public void setRadio(int radio) {
        this.radio = radio;
        repaint();
    }

    public int getRadio() {
        return radio;
    }
}

