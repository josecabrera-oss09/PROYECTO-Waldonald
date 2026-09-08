package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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

    // Mouse encima
    private boolean mouseEncima = false;

    // Si el botón está seleccionado
    private boolean seleccionado = false;

    // Colores
    private final Color colorFondoNormal = Color.WHITE;
    private final Color bordeNormal = new Color(235, 235, 235);
    private final Color colorAmarillo = new Color(255, 188, 13);
    private final Color colorTexto = new Color(30, 30, 30);

    // Redondeado
    private int radio = 22;

    public BotonLetras() {

        setText("Categoría");

        setPreferredSize(new Dimension(105, 105));

        setFont(new Font("Arial", Font.BOLD, 12));
        setForeground(colorTexto);

        // Centrar texto
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);

        setIconTextGap(4);

        // Quitar diseño normal del JButton
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        // Cursor
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ==========================================
        // MOUSE
        // ==========================================
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

        // ==========================================
        // CLICK
        // ==========================================
        addActionListener(e -> {

            seleccionarEsteBoton();

        });
    }

    // ==========================================
    // SELECCIONAR ESTE BOTÓN
    // ==========================================
    private void seleccionarEsteBoton() {

        Container contenedor = getParent();

        if (contenedor == null) {
            return;
        }

        // Buscar todos los componentes
        for (Component componente : contenedor.getComponents()) {

            // Si es un BotonLetras
            if (componente instanceof BotonLetras) {

                BotonLetras boton = (BotonLetras) componente;

                // Quitar selección
                boton.setSeleccionado(false);
            }
        }

        // Seleccionar únicamente este
        setSeleccionado(true);
    }

    // ==========================================
    // DIBUJAR BOTÓN
    // ==========================================
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();

        // ==========================================
        // AMARILLO
        // ==========================================
        // Si:
        // - está seleccionado
        // - o el mouse está encima
        if (seleccionado || mouseEncima) {

            g2.setColor(colorAmarillo);

            g2.fillRoundRect(
                    2,
                    2,
                    ancho - 4,
                    alto - 4,
                    radio,
                    radio
            );

            g2.setColor(colorAmarillo);

            g2.setStroke(
                    new BasicStroke(2.5f)
            );

        } else {

            // ==========================================
            // BLANCO
            // ==========================================

            g2.setColor(colorFondoNormal);

            g2.fillRoundRect(
                    2,
                    2,
                    ancho - 4,
                    alto - 4,
                    radio,
                    radio
            );

            g2.setColor(bordeNormal);

            g2.setStroke(
                    new BasicStroke(2f)
            );
        }

        // Borde
        g2.drawRoundRect(
                2,
                2,
                ancho - 5,
                alto - 5,
                radio,
                radio
        );

        g2.dispose();

        // Texto e imagen
        super.paintComponent(g);
    }

    // ==========================================
    // SELECCIONAR / DESELECCIONAR
    // ==========================================
    public void setSeleccionado(boolean seleccionado) {

        this.seleccionado = seleccionado;

        repaint();
    }

    public boolean isSeleccionado() {

        return seleccionado;
    }

    // ==========================================
    // RADIO
    // ==========================================
    public void setRadio(int radio) {

        this.radio = radio;

        repaint();
    }

    public int getRadio() {

        return radio;
    }
}