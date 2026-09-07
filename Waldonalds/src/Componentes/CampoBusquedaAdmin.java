package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.BeanProperty;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Buscador blanco y neutro para las pantallas administrativas.
 *
 * Es un JavaBean con constructor vacío: puede agregarse a la paleta y editarse
 * directamente en el diseñador de NetBeans. El icono se dibuja en gris, sin
 * el círculo amarillo utilizado por los campos del login.
 */
@SuppressWarnings({"serial", "this-escape"})
public class CampoBusquedaAdmin extends JTextField {

    private String placeholder = "Buscar...";
    private Color colorBorde = new Color(222, 227, 234);
    private Color colorIcono = new Color(92, 103, 124);
    private Color colorPlaceholder = new Color(127, 137, 154);
    private int radio = 14;

    public CampoBusquedaAdmin() {
        setOpaque(false);
        setForeground(new Color(0, 20, 43));
        setCaretColor(new Color(0, 20, 43));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(0, 48, 0, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evento) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent evento) {
                repaint();
            }
        });
    }

    @BeanProperty(preferred = true, description = "Texto mostrado cuando el campo está vacío.")
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color del contorno.")
    public Color getColorBorde() {
        return colorBorde;
    }

    public void setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde != null ? colorBorde : Color.LIGHT_GRAY;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color de la lupa.")
    public Color getColorIcono() {
        return colorIcono;
    }

    public void setColorIcono(Color colorIcono) {
        this.colorIcono = colorIcono != null ? colorIcono : Color.GRAY;
        repaint();
    }

    @BeanProperty(description = "Color del placeholder.")
    public Color getColorPlaceholder() {
        return colorPlaceholder;
    }

    public void setColorPlaceholder(Color colorPlaceholder) {
        this.colorPlaceholder = colorPlaceholder != null
                ? colorPlaceholder : Color.GRAY;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Radio de las esquinas.")
    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = Math.max(0, radio);
        repaint();
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 48, 0, 16);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3,
                radio, radio);
        g2.setColor(hasFocus() ? new Color(179, 188, 201) : colorBorde);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3,
                radio, radio);
        g2.dispose();

        super.paintComponent(graphics);

        g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);
        pintarLupa(g2);
        if (getText().isEmpty() && !hasFocus() && !placeholder.isEmpty()) {
            g2.setFont(getFont());
            g2.setColor(colorPlaceholder);
            int base = (getHeight() + g2.getFontMetrics().getAscent()
                    - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(placeholder, 48, base);
        }
        g2.dispose();
    }

    private void pintarLupa(Graphics2D g2) {
        g2.setColor(colorIcono);
        g2.setStroke(new BasicStroke(
                1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int centroY = getHeight() / 2 - 2;
        g2.drawOval(19, centroY - 7, 13, 13);
        g2.drawLine(30, centroY + 5, 36, centroY + 11);
    }

    private static void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
