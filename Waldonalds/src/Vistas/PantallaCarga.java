package Vistas;

import Componentes.CargaW;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PantallaCarga extends JFrame {

    private static final int ANCHO_VENTANA = 560;
    private static final int ALTO_VENTANA = 360;
    private static final Color ROJO_SUPERIOR = new Color(207, 31, 25);
    private static final Color ROJO_INFERIOR = new Color(166, 19, 18);

    private CargaW cargaW;
    private FondoCarga pnlFondo;
    private JLabel lblCargando;
    private JLabel lblDerechos;

    private Timer timerCargando;
    private int frameTexto;

    public PantallaCarga() {
        configurarVentana();
        crearComponentes();
        iniciarAnimacionTexto();
    }

    private void configurarVentana() {
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
    }

    private void crearComponentes() {
        pnlFondo = new FondoCarga();
        pnlFondo.setLayout(null);
        setContentPane(pnlFondo);

        cargaW = new CargaW();
        cargaW.setTamanoLogo(198);
        cargaW.setVelocidad(1.0f);
        cargaW.setAnchoLuz(70);
        cargaW.setOpacidadBase(0.20f);
        cargaW.setBounds(130, 25, 300, 205);
        pnlFondo.add(cargaW);

        lblCargando = new JLabel("Preparando tu experiencia", SwingConstants.CENTER);
        lblCargando.setFont(cargarFuente("DMSans-Bold.ttf", 15f, Font.BOLD));
        lblCargando.setForeground(Color.WHITE);
        lblCargando.setBounds(100, 238, 360, 28);
        pnlFondo.add(lblCargando);

        lblDerechos = new JLabel(
                "\u00A9 2026. Todos los derechos reservados.",
                SwingConstants.CENTER
        );
        lblDerechos.setFont(cargarFuente("DMSans-Regular.ttf", 11f, Font.PLAIN));
        lblDerechos.setForeground(new Color(255, 218, 214));
        lblDerechos.setBounds(60, 306, 440, 22);
        pnlFondo.add(lblDerechos);
    }

    private Font cargarFuente(String archivo, float tamano, int estiloAlternativo) {
        try (InputStream fuente = getClass().getResourceAsStream(
                "/Font/DMSans/" + archivo)) {
            if (fuente != null) {
                return Font.createFont(Font.TRUETYPE_FONT, fuente).deriveFont(tamano);
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("No se pudo cargar la fuente " + archivo + ": "
                    + e.getMessage());
        }
        return new Font("SansSerif", estiloAlternativo, Math.round(tamano));
    }

    private void iniciarAnimacionTexto() {
        timerCargando = new Timer(360, e -> {
            frameTexto = (frameTexto + 1) % 4;
            lblCargando.setText(
                    "Preparando tu experiencia" + ".".repeat(frameTexto)
            );
        });
        timerCargando.setCoalesce(true);
        timerCargando.start();
    }

    /** Detiene los timers y libera la ventana al pasar al login. */
    public void cerrar() {
        if (timerCargando != null) {
            timerCargando.stop();
        }
        if (cargaW != null) {
            cargaW.detener();
        }
        setVisible(false);
        dispose();
    }

    private static class FondoCarga extends JPanel {

        FondoCarga() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setPaint(new GradientPaint(
                    0,
                    0,
                    ROJO_SUPERIOR,
                    0,
                    getHeight(),
                    ROJO_INFERIOR
            ));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillOval(-92, -126, 270, 270);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(255, 199, 44, 26));
            g2.drawOval(getWidth() - 146, getHeight() - 138, 210, 210);

            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillRoundRect(85, 288, getWidth() - 170, 1, 1, 1);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            PantallaCarga pantalla = new PantallaCarga();
            pantalla.setVisible(true);
        });
    }
}
