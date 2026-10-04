package Componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class Scroll_Vertical extends JScrollPane {

    private JPanel panelContenido;

    public Scroll_Vertical() {

        // Fondo blanco
        getViewport().setBackground(Color.WHITE);
        setBackground(Color.WHITE);

        // ==========================================
        // CONFIGURACIÓN DEL SCROLL
        // ==========================================

        // No queremos scroll horizontal
        setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // El contenido conserva el desplazamiento, pero sin mostrar la barra.
        setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );

        // Quitar bordes
        setBorder(
                BorderFactory.createEmptyBorder()
        );

        setViewportBorder(
                BorderFactory.createEmptyBorder()
        );

        // ==========================================
        // BARRA VERTICAL
        // ==========================================

        JScrollBar barra = getVerticalScrollBar();

        barra.setBorder(
                BorderFactory.createEmptyBorder()
        );

        barra.setOpaque(false);
        barra.setBackground(Color.WHITE);

        // Ancho de la barra
        barra.setPreferredSize(
                new Dimension(12, 0)
        );

        // Velocidad del scroll
        barra.setUnitIncrement(25);
        barra.setBlockIncrement(120);

        // Diseño personalizado
        barra.setUI(
                new BarraNaranjaVertical()
        );

        DesplazamientoSuave.instalarVertical(this);
    }

    // ==========================================
    // COLOCAR PANEL DENTRO DEL SCROLL
    // ==========================================

    public void setPanelContenido(JPanel panel) {

        this.panelContenido = panel;

        setViewportView(panelContenido);

        DesplazamientoSuave.instalarSobreVista(
                panelContenido,
                getVerticalScrollBar(),
                false
        );

        panelContenido.revalidate();
        panelContenido.repaint();

        revalidate();
        repaint();

        // Empezar arriba
        SwingUtilities.invokeLater(() -> {

            getViewport().setViewPosition(
                    new Point(0, 0)
            );

            JScrollBar barra =
                    getVerticalScrollBar();

            barra.setValue(
                    barra.getMinimum()
            );
        });
    }

    // ==========================================
    // AGREGAR COMPONENTES
    // ==========================================

    public void agregarComponente(
            JComponent componente) {

        if (panelContenido == null) {
            return;
        }

        panelContenido.add(componente);

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // ==========================================
    // OBTENER PANEL
    // ==========================================

    public JPanel getPanelContenido() {
        return panelContenido;
    }

    // ==========================================
    // REGRESAR ARRIBA
    // ==========================================

    public void irArriba() {

        JScrollBar barra =
                getVerticalScrollBar();

        barra.setValue(
                barra.getMinimum()
        );

        getViewport().setViewPosition(
                new Point(0, 0)
        );
    }

    // ==========================================
    // DISEÑO DE BARRA VERTICAL
    // ==========================================

    private static class BarraNaranjaVertical
            extends BasicScrollBarUI {

        private final Color naranja =
                new Color(255, 174, 0);

        // Tamaño mínimo del indicador
        @Override
        protected Dimension getMinimumThumbSize() {

            return new Dimension(
                    6,
                    70
            );
        }

        // Tamaño máximo del indicador
        @Override
        protected Dimension getMaximumThumbSize() {

            return new Dimension(
                    6,
                    70
            );
        }

        // Quitar flecha superior
        @Override
        protected JButton createDecreaseButton(
                int orientation) {

            return crearBotonInvisible();
        }

        // Quitar flecha inferior
        @Override
        protected JButton createIncreaseButton(
                int orientation) {

            return crearBotonInvisible();
        }

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

        // ==========================================
        // DIBUJAR INDICADOR NARANJA
        // ==========================================

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

            int ancho = 6;

            int x =
                    thumbBounds.x
                    + (thumbBounds.width - ancho) / 2;

            g2.fillRoundRect(
                    x,
                    thumbBounds.y,
                    ancho,
                    thumbBounds.height,
                    10,
                    10
            );

            g2.dispose();
        }

        // No dibujar el fondo de la barra
        @Override
        protected void paintTrack(
                Graphics g,
                JComponent c,
                Rectangle trackBounds) {

            // Vacío intencionalmente
        }
    }
}
