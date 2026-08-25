package Labels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TexBoxPassword extends JPanel {

    private JPasswordField txtInput;
    private JLabel lblIcono;

    private int radioEsquinas = 20;
    private String placeholder = "Ingresa tu contraseña";

    // Controla si se muestra el placeholder
    private boolean mostrarPlaceholder = true;

    public TexBoxPassword() {

        setOpaque(false);
        setLayout(new BorderLayout());

        // Espacio interno
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 12));

        // ==============================
        // CAMPO DE CONTRASEÑA
        // ==============================

        txtInput = new JPasswordField() {

            @Override
            protected void paintComponent(Graphics g) {

                // Dibujar normalmente el campo
                super.paintComponent(g);

                // Mostrar placeholder si está vacío
                if (mostrarPlaceholder && getPassword().length == 0) {

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setColor(new Color(140, 140, 145));
                    g2.setFont(getFont());

                    FontMetrics fm = g2.getFontMetrics();

                    int y = (getHeight() - fm.getHeight()) / 2
                            + fm.getAscent();

                    g2.drawString(placeholder, 0, y);

                    g2.dispose();
                }
            }
        };

        // ==============================
        // ESTILO
        // ==============================

        txtInput.setOpaque(false);

        txtInput.setBackground(
                new Color(0, 0, 0, 0)
        );

        txtInput.setBorder(null);

        txtInput.setFont(
                new Font("Segoe UI", Font.PLAIN, 15)
        );

        txtInput.setForeground(
                new Color(50, 50, 50)
        );

        txtInput.setCaretColor(
                new Color(50, 50, 50)
        );

        // Caracter que aparecerá al escribir contraseña
        txtInput.setEchoChar('•');

        // Evita que aparezca seleccionado al abrir la ventana
        txtInput.setFocusable(false);


        // ==============================
        // AL HACER CLIC
        // ==============================

        txtInput.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // Ahora puede recibir el foco
                txtInput.setFocusable(true);

                // Ocultar placeholder
                mostrarPlaceholder = false;

                // Colocar cursor dentro
                SwingUtilities.invokeLater(() -> {
                    txtInput.requestFocusInWindow();
                });

                txtInput.repaint();
            }
        });


        // ==============================
        // CUANDO SALE DEL CAMPO
        // ==============================

        txtInput.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {

                // Si no escribió contraseña
                if (txtInput.getPassword().length == 0) {

                    mostrarPlaceholder = true;

                    // Evita que tome foco automáticamente
                    txtInput.setFocusable(false);

                    txtInput.repaint();
                }
            }
        });


        // ==============================
        // ICONO
        // ==============================

        lblIcono = new JLabel();

        lblIcono.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );


        // Agregar componentes
        add(txtInput, BorderLayout.CENTER);
        add(lblIcono, BorderLayout.EAST);
    }


    // ==============================
    // FONDO REDONDEADO
    // ==============================

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Fondo gris #EAEAEA
        g2.setColor(
                new Color(234, 234, 234)
        );

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                radioEsquinas,
                radioEsquinas
        );

        g2.dispose();

        super.paintComponent(g);
    }


    // ==============================
    // OBTENER CONTRASEÑA
    // ==============================

    public String getPassword() {
        return new String(txtInput.getPassword());
    }


    // ==============================
    // PLACEHOLDER
    // ==============================

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        txtInput.repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setText(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
