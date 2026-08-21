package Labels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class TexBox extends JPanel {

    private JTextField txtInput;
    private JLabel lblIcono;

    private int radioEsquinas = 20;

    private String placeholder = "Ingresa correo electrónico";

    // Controla si se muestra o no el placeholder
    private boolean mostrarPlaceholder = true;

    public TexBox() {

        setOpaque(false);
        setLayout(new BorderLayout());

        // Espacio interno
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 12));

        txtInput = new JTextField() {

            @Override
            protected void paintComponent(Graphics g) {

                // Primero dibujamos normalmente el JTextField
                super.paintComponent(g);

                // Dibujar placeholder solamente si corresponde
                if (mostrarPlaceholder && getText().isEmpty()) {

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
        // ESTILO DEL TEXTFIELD
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

        // ==============================
        // QUITAR PLACEHOLDER AL HACER CLIC
        // ==============================

       // Al hacer clic con el mouse, desaparece el placeholder
txtInput.addMouseListener(new java.awt.event.MouseAdapter() {

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        mostrarPlaceholder = false;
        txtInput.repaint();
    }
});


// Si sale del campo y está vacío,
// vuelve a aparecer el placeholder
txtInput.addFocusListener(new FocusAdapter() {

    @Override
    public void focusLost(FocusEvent e) {

        if (txtInput.getText().isEmpty()) {
            mostrarPlaceholder = true;
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
    // FONDO DEL TEXTBOX
    // ==============================

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Fondo #EAEAEA
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

    // Obtener el texto escrito
    public String getText() {
        return txtInput.getText();
    }

    // Cambiar el placeholder desde NetBeans/código
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
        txtInput.repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }
}