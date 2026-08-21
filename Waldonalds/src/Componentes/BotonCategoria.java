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

public class BotonCategoria extends JButton {

    // Indica si el mouse está encima
    private boolean mouseEncima = false;

    // Indica si esta categoría está seleccionada
    private boolean seleccionado = false;

    // Colores
    private final Color colorNormal = new Color(255, 255, 255);
    private final Color colorHover = new Color(255, 249, 230);
    private final Color amarillo = new Color(255, 188, 13);
    private final Color bordeNormal = new Color(235, 235, 235);

    // Redondeado
    private int radio = 22;

    public BotonCategoria() {

        // Texto inicial
        setText("Categoría");

        // Tamaño recomendado
        setPreferredSize(new Dimension(105, 105));

        // Fuente
        setFont(new Font("Arial", Font.BOLD, 12));

        // Color del texto
        setForeground(new Color(25, 25, 25));

        // Imagen arriba y texto abajo
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);

        // Separación entre imagen y texto
        setIconTextGap(4);

        // Quitar diseño normal del JButton
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        // Cursor de mano
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Detectar mouse
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

        // Mejor calidad
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();

        // ==============================
        // FONDO
        // ==============================

        if (mouseEncima || seleccionado) {
            g2.setColor(colorHover);
        } else {
            g2.setColor(colorNormal);
        }

        g2.fillRoundRect(
                1,
                1,
                ancho - 2,
                alto - 2,
                radio,
                radio
        );

        // ==============================
        // BORDE
        // ==============================

        if (mouseEncima || seleccionado) {

            g2.setColor(amarillo);
            g2.setStroke(new BasicStroke(2f));

        } else {

            g2.setColor(bordeNormal);
            g2.setStroke(new BasicStroke(1f));
        }

        g2.drawRoundRect(
                1,
                1,
                ancho - 3,
                alto - 3,
                radio,
                radio
        );

        g2.dispose();

        // Dibujar texto e imagen del JButton
        super.paintComponent(g);
    }

    // =================================
    // SELECCIONAR CATEGORÍA
    // =================================

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        repaint();
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    // =================================
    // CAMBIAR RADIO DE ESQUINAS
    // =================================

    public void setRadio(int radio) {
        this.radio = radio;
        repaint();
    }

    public int getRadio() {
        return radio;
    }
}