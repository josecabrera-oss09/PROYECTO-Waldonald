package Componentes;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class Scroll_Categorias extends JScrollPane {

    private final JPanel panelCategorias;

    public Scroll_Categorias() {

        // ==========================================
        // ORIENTACIÓN IZQUIERDA A DERECHA
        // ==========================================
        setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
        );


        // ==========================================
        // PANEL DE CATEGORÍAS
        // ==========================================
        panelCategorias = new JPanel();

        panelCategorias.setLayout(
                new BoxLayout(
                        panelCategorias,
                        BoxLayout.X_AXIS
                )
        );

        panelCategorias.setBackground(Color.WHITE);
        panelCategorias.setOpaque(true);

        panelCategorias.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
        );


        // ==========================================
        // TAMAÑO DEL CONTENIDO
        // ==========================================
        // Debe ser más ancho que el JScrollPane
        // para que exista desplazamiento horizontal.
        panelCategorias.setPreferredSize(
                new Dimension(2000, 170)
        );


        // Espacio inicial
        panelCategorias.add(
                Box.createHorizontalStrut(10)
        );


        // ==========================================
        // METER PANEL DENTRO DEL SCROLL
        // ==========================================
        setViewportView(panelCategorias);

        getViewport().setBackground(Color.WHITE);

        getViewport().setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
        );


        // ==========================================
        // CONFIGURACIÓN DEL SCROLLPANE
        // ==========================================

        setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );


        // Quitar borde exterior
        setBorder(
                BorderFactory.createEmptyBorder()
        );

        // Quitar borde interno
        setViewportBorder(
                BorderFactory.createEmptyBorder()
        );

        setBackground(Color.WHITE);


        // ==========================================
        // BARRA HORIZONTAL
        // ==========================================
        JScrollBar barra = getHorizontalScrollBar();

        barra.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
        );

        // Quitar borde
        barra.setBorder(
                BorderFactory.createEmptyBorder()
        );

        // Fondo transparente
        barra.setOpaque(false);

        barra.setBackground(Color.WHITE);


        // Altura del espacio donde está la barra
        barra.setPreferredSize(
                new Dimension(0, 12)
        );


        // Velocidad del scroll
        barra.setUnitIncrement(25);

        barra.setBlockIncrement(120);


        // Aplicar diseño
        barra.setUI(
                new BarraNaranja()
        );


        // ==========================================
        // RUEDA DEL MOUSE
        // ==========================================
        addMouseWheelListener(e -> {

            JScrollBar scroll =
                    getHorizontalScrollBar();

            int movimiento =
                    e.getWheelRotation() * 45;

            scroll.setValue(
                    scroll.getValue()
                    + movimiento
            );
        });


        // ==========================================
        // INICIAR EN LA IZQUIERDA
        // ==========================================
        SwingUtilities.invokeLater(() -> {

            getViewport().setViewPosition(
                    new Point(0, 0)
            );

            barra.setValue(
                    barra.getMinimum()
            );
        });
    }


    // ==========================================
    // AGREGAR CATEGORÍAS
    // ==========================================
    public void agregarCategoria(
            JComponent componente) {

        panelCategorias.add(componente);

        // Separación
        panelCategorias.add(
                Box.createHorizontalStrut(15)
        );

        panelCategorias.revalidate();
        panelCategorias.repaint();
    }


    // ==========================================
    // OBTENER PANEL
    // ==========================================
    public JPanel getPanelCategorias() {

        return panelCategorias;
    }


    // ==========================================
    // REGRESAR AL INICIO
    // ==========================================
    public void irAlInicio() {

        JScrollBar barra =
                getHorizontalScrollBar();

        barra.setValue(
                barra.getMinimum()
        );

        getViewport().setViewPosition(
                new Point(0, 0)
        );
    }


    // ==========================================
    // DISEÑO DE LA BARRA
    // ==========================================
    private static class BarraNaranja
            extends BasicScrollBarUI {

        // Color naranja
        private final Color naranja =
                new Color(255, 174, 0);


        // ======================================
        // TAMAÑO REAL DE LA BARRA
        // ======================================
        @Override
        protected Dimension getMinimumThumbSize() {

            return new Dimension(
                    70,
                    6
            );
        }


        @Override
        protected Dimension getMaximumThumbSize() {

            return new Dimension(
                    70,
                    6
            );
        }


        // ======================================
        // QUITAR FLECHA IZQUIERDA
        // ======================================
        @Override
        protected JButton createDecreaseButton(
                int orientation) {

            return crearBotonInvisible();
        }


        // ======================================
        // QUITAR FLECHA DERECHA
        // ======================================
        @Override
        protected JButton createIncreaseButton(
                int orientation) {

            return crearBotonInvisible();
        }


        // ======================================
        // BOTÓN INVISIBLE
        // ======================================
        private JButton crearBotonInvisible() {

            JButton boton = new JButton();

            boton.setPreferredSize(
                    new Dimension(0, 0)
            );

            boton.setMinimumSize(
                    new Dimension(0, 0)
            );

            boton.setMaximumSize(
                    new Dimension(0, 0)
            );

            boton.setBorder(
                    BorderFactory.createEmptyBorder()
            );

            boton.setContentAreaFilled(false);

            boton.setFocusPainted(false);

            boton.setOpaque(false);

            return boton;
        }


        // ======================================
        // DIBUJAR BARRA NARANJA
        // ======================================
        @Override
        protected void paintThumb(
                Graphics g,
                JComponent c,
                Rectangle thumbBounds) {

            if (thumbBounds.isEmpty()) {
                return;
            }

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(naranja);


            // Centrar verticalmente
            int alto = 6;

            int y =
                    thumbBounds.y
                    + (thumbBounds.height - alto) / 2;


            // IMPORTANTE:
            // usamos directamente thumbBounds.x
            // porque esta es la barra REAL de Swing
            g2.fillRoundRect(
                    thumbBounds.x,
                    y,
                    thumbBounds.width,
                    alto,
                    10,
                    10
            );

            g2.dispose();
        }


        // ======================================
        // NO DIBUJAR FONDO DEL TRACK
        // ======================================
        @Override
        protected void paintTrack(
                Graphics g,
                JComponent c,
                Rectangle trackBounds) {

            // Vacío intencionalmente.
            // Así no aparece línea negra.
        }
    }
}