package Componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class BotonDerretido extends JButton {

    // Color amarillo del botón
    private Color colorBoton = new Color(255, 188, 13);

    // Cuánto se ha derretido
    private float derretido = 0f;

    // Indica si el mouse está encima
    private boolean mouseEncima = false;

    private Timer timer;

    public BotonDerretido() {

        setText("BOTÓN");

        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Espacio extra abajo para que puedan aparecer las gotas
        setBorder(new EmptyBorder(5, 15, 18, 15));

        setPreferredSize(new Dimension(180, 60));

        // Animación
        timer = new Timer(15, e -> {

            if (mouseEncima) {

                // Derretir
                derretido += 0.06f;

                if (derretido >= 1f) {
                    derretido = 1f;
                }

            } else {

                // Regresar a la normalidad
                derretido -= 0.08f;

                if (derretido <= 0f) {
                    derretido = 0f;
                }
            }

            repaint();

            // Si terminó la animación se detiene
            if (!mouseEncima && derretido <= 0) {
                timer.stop();
            }
        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEncima = true;

                if (!timer.isRunning()) {
                    timer.start();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseEncima = false;

                if (!timer.isRunning()) {
                    timer.start();
                }
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

        // Altura principal del botón
        int baseY = alto - 18;

        // Cuánto bajan las gotas
        float gota1 = 14 * derretido;
        float gota2 = 8 * derretido;
        float gota3 = 17 * derretido;
        float gota4 = 10 * derretido;

        Path2D.Float forma = new Path2D.Float();

        // Empezamos arriba a la izquierda
        forma.moveTo(15, 0);

        // Parte superior
        forma.quadTo(0, 0, 0, 15);

        // Lado izquierdo
        forma.lineTo(0, baseY - 10);

        forma.quadTo(0, baseY, 10, baseY);

        /*
         * ===============================
         * PARTE DERRETIDA
         * ===============================
         */

        // Primera curva
        forma.curveTo(
                ancho * 0.10,
                baseY,
                ancho * 0.13,
                baseY + gota1,
                ancho * 0.18,
                baseY + gota1
        );

        forma.curveTo(
                ancho * 0.23,
                baseY + gota1,
                ancho * 0.23,
                baseY,
                ancho * 0.30,
                baseY
        );

        // Segunda gota
        forma.curveTo(
                ancho * 0.36,
                baseY,
                ancho * 0.37,
                baseY + gota2,
                ancho * 0.42,
                baseY + gota2
        );

        forma.curveTo(
                ancho * 0.47,
                baseY + gota2,
                ancho * 0.48,
                baseY,
                ancho * 0.54,
                baseY
        );

        // Tercera gota, más larga
        forma.curveTo(
                ancho * 0.60,
                baseY,
                ancho * 0.61,
                baseY + gota3,
                ancho * 0.66,
                baseY + gota3
        );

        forma.curveTo(
                ancho * 0.71,
                baseY + gota3,
                ancho * 0.72,
                baseY,
                ancho * 0.78,
                baseY
        );

        // Cuarta gota
        forma.curveTo(
                ancho * 0.84,
                baseY,
                ancho * 0.85,
                baseY + gota4,
                ancho * 0.90,
                baseY + gota4
        );

        forma.curveTo(
                ancho * 0.95,
                baseY + gota4,
                ancho * 0.95,
                baseY,
                ancho - 10,
                baseY
        );

        // Esquina derecha
        forma.quadTo(
                ancho,
                baseY,
                ancho,
                baseY - 10
        );

        forma.lineTo(ancho, 15);

        forma.quadTo(
                ancho,
                0,
                ancho - 15,
                0
        );

        forma.closePath();

        // Pintar botón
        g2.setColor(colorBoton);
        g2.fill(forma);

        g2.dispose();

        // Pintar texto
        super.paintComponent(g);
    }
}