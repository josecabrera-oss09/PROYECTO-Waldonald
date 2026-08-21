package Vistas;

import Componentes.CargaW;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PantallaCarga extends JFrame {

    private CargaW cargaW;

    private JPanel pnlFondo;
    private JPanel pnlCargaW;
    private JLabel lblCargando;

    public PantallaCarga() {

        configurarVentana();
        crearComponentes();
        configurarCargaW();
    }

    private void configurarVentana() {

        setSize(500, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
    }

    private void crearComponentes() {

        pnlFondo = new JPanel();
        pnlFondo.setLayout(null);

        // ===============================
        // COLOR DE FONDO
        // ===============================
        // Rojo McDonald's:
        pnlFondo.setBackground(new Color(218, 41, 28));

        add(pnlFondo);

        pnlCargaW = new JPanel();
        pnlCargaW.setOpaque(false);
        pnlCargaW.setLayout(new BorderLayout());

        // ===============================
        // ÁREA DEL LOGO
        // ===============================
        // Si haces el logo más grande, este panel ya tiene suficiente espacio
        pnlCargaW.setBounds(150, 55, 200, 140);

        pnlFondo.add(pnlCargaW);

        lblCargando = new JLabel("Cargando...");
        lblCargando.setHorizontalAlignment(SwingConstants.CENTER);
        lblCargando.setFont(new Font("Arial", Font.BOLD, 14));
        lblCargando.setForeground(Color.WHITE);

        lblCargando.setBounds(120, 205, 260, 30);

        pnlFondo.add(lblCargando);
    }

    private void configurarCargaW() {

        cargaW = new CargaW();

        // ===============================
        // TAMAÑO DEL LOGO
        // ===============================
        // Cambia este número si quieres el logo más grande o más pequeño
        cargaW.setTamanoLogo(110);

        // Opcional: ajustar velocidad o ancho de luz
        cargaW.setVelocidad(2.8f);
        cargaW.setAnchoLuz(46);
        cargaW.setOpacidadBase(0.22f);

        pnlCargaW.add(cargaW, BorderLayout.CENTER);
        pnlCargaW.revalidate();
        pnlCargaW.repaint();
    }

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            PantallaCarga pantalla = new PantallaCarga();
            pantalla.setVisible(true);
        });
    }
}