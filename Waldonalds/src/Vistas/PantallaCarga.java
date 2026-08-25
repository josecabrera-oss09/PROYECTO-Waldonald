package Vistas;

import Componentes.CargaW;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PantallaCarga extends JFrame {

    private CargaW cargaW;

    private JPanel pnlFondo;
    private JPanel pnlCargaW;
    private JLabel lblDerechos;
    private JLabel lblCargando;

    // ===============================
    // ANIMACIÓN DEL TEXTO
    // ===============================
    private Timer timerCargando;

    private int frameTexto = 0;

    private final int cargandoX = 100;
    private final int cargandoY = 230;

    public PantallaCarga() {
        

        configurarVentana();

        crearComponentes();

        configurarCargaW();

        iniciarAnimacionCargando();
    }

    // =================================================
    // VENTANA
    // =================================================
    private void configurarVentana() {

        setSize(
                500,
                320
        );

        setResizable(false);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setUndecorated(true);

        setLocationRelativeTo(null);
    }

    // =================================================
    // COMPONENTES
    // =================================================
    private void crearComponentes() {

        // ==========================================
        // FONDO
        // ==========================================
        pnlFondo
                = new JPanel();

        pnlFondo.setLayout(null);

        /*
         * ROJO
         *
         * RGB(218, 41, 28)
         * #DA291C
         */
        pnlFondo.setBackground(
                new Color(
                        218,
                        41,
                        28
                )
        );

        add(
                pnlFondo
        );

        // ==========================================
        // ÁREA DE LA W
        // ==========================================
        pnlCargaW
                = new JPanel();

        pnlCargaW.setOpaque(false);

        pnlCargaW.setLayout(
                new BorderLayout()
        );

        /*
         * Panel bastante más grande.
         *
         * Antes:
         * 200 × 140
         *
         * Ahora:
         * 320 × 200
         */
        pnlCargaW.setBounds(
                90,
                25,
                320,
                200
        );

        pnlFondo.add(
                pnlCargaW
        );

        // ==========================================
        // CARGANDO
        // ==========================================
        lblCargando
                = new JLabel(
                        "Cargando"
                );

        lblCargando.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblCargando.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        lblCargando.setForeground(
                Color.WHITE
        );

        lblCargando.setBounds(
                cargandoX,
                cargandoY,
                300,
                30
        );

        pnlFondo.add(
                lblCargando
        );

        lblDerechos = new JLabel(
                "©2026 WalDonald’s. Todos los derechos reservados.");
        lblDerechos.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblDerechos.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        lblDerechos.setForeground(
                new Color(255, 230, 230)
        );

        lblDerechos.setBounds(
                50,
                260,
                400,
                25
        );

        pnlFondo.add(
                lblDerechos
        );
    }

    // =================================================
    // CONFIGURAR W
    // =================================================
    private void configurarCargaW() {

        cargaW
                = new CargaW();

        /*
         * ==========================================
         * TAMAÑO DEL LOGO
         * ==========================================
         *
         * Antes estabas usando 110.
         *
         * Ahora usamos 180.
         */
        cargaW.setTamanoLogo(
                180
        );

        /*
         * Movimiento suave.
         */
        cargaW.setVelocidad(
                3.0f
        );

        /*
         * Franja más amplia para que
         * se note mejor.
         */
        cargaW.setAnchoLuz(
                65
        );

        /*
         * W de fondo tenue.
         */
        cargaW.setOpacidadBase(
                0.12f
        );

        pnlCargaW.add(
                cargaW,
                BorderLayout.CENTER
        );

        pnlCargaW.revalidate();

        pnlCargaW.repaint();
    }

    // =================================================
    // ANIMACIÓN DE "CARGANDO"
    // =================================================
    private void iniciarAnimacionCargando() {

        timerCargando = new Timer(350, e -> {

            frameTexto++;

            int fasePuntos = frameTexto % 4;

            switch (fasePuntos) {

                case 0:
                    lblCargando.setText("Cargando");
                    break;

                case 1:
                    lblCargando.setText("Cargando.");
                    break;

                case 2:
                    lblCargando.setText("Cargando..");
                    break;

                default:
                    lblCargando.setText("Cargando...");
                    break;
            }
        });

        timerCargando.setCoalesce(true);
        timerCargando.start();
    }

    // =================================================
    // MAIN
    // =================================================
    public static void main(
            String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            PantallaCarga pantalla
                    = new PantallaCarga();

            pantalla.setVisible(true);
        });
    }
}
