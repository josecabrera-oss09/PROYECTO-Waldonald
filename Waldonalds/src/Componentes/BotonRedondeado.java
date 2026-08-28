package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.BeanProperty;

import javax.swing.JButton;

/** Botón reutilizable redondeado, sólido o con degradado y flecha opcional. */
@SuppressWarnings({"serial", "this-escape"})
public class BotonRedondeado extends JButton {

    private Color colorInicio = new Color(255, 184, 0);
    private Color colorFinal = new Color(255, 205, 26);
    private Color colorBorde = new Color(0, 0, 0, 0);
    private int radio = 18;
    private float grosorBorde;
    private boolean degradado = true;
    private boolean mostrarFlecha;
    private int separacionFlecha = 28;

    public BotonRedondeado() {
        setText("Botón");
        setForeground(Color.BLACK);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 58));
    }

    @BeanProperty(preferred = true, description = "Color inicial o color sólido del botón.")
    public Color getColorInicio() {
        return colorInicio;
    }

    public void setColorInicio(Color colorInicio) {
        Color anterior = this.colorInicio;
        this.colorInicio = colorInicio != null ? colorInicio : Color.WHITE;
        firePropertyChange("colorInicio", anterior, this.colorInicio);
        repaint();
    }

    @BeanProperty(description = "Color final del degradado.")
    public Color getColorFinal() {
        return colorFinal;
    }

    public void setColorFinal(Color colorFinal) {
        Color anterior = this.colorFinal;
        this.colorFinal = colorFinal != null ? colorFinal : colorInicio;
        firePropertyChange("colorFinal", anterior, this.colorFinal);
        repaint();
    }

    @BeanProperty(description = "Color del contorno.")
    public Color getColorBorde() {
        return colorBorde;
    }

    public void setColorBorde(Color colorBorde) {
        Color anterior = this.colorBorde;
        this.colorBorde = colorBorde != null ? colorBorde : new Color(0, 0, 0, 0);
        firePropertyChange("colorBorde", anterior, this.colorBorde);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Radio de las esquinas en píxeles.")
    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        int anterior = this.radio;
        this.radio = Math.max(0, radio);
        firePropertyChange("radio", anterior, this.radio);
        repaint();
    }

    @BeanProperty(description = "Grosor del contorno en píxeles.")
    public float getGrosorBorde() {
        return grosorBorde;
    }

    public void setGrosorBorde(float grosorBorde) {
        float anterior = this.grosorBorde;
        this.grosorBorde = Math.max(0f, grosorBorde);
        firePropertyChange("grosorBorde", anterior, this.grosorBorde);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Activa el degradado entre ambos colores.")
    public boolean isDegradado() {
        return degradado;
    }

    public void setDegradado(boolean degradado) {
        boolean anterior = this.degradado;
        this.degradado = degradado;
        firePropertyChange("degradado", anterior, degradado);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Dibuja una flecha después del texto.")
    public boolean isMostrarFlecha() {
        return mostrarFlecha;
    }

    public void setMostrarFlecha(boolean mostrarFlecha) {
        boolean anterior = this.mostrarFlecha;
        this.mostrarFlecha = mostrarFlecha;
        firePropertyChange("mostrarFlecha", anterior, mostrarFlecha);
        repaint();
    }

    @BeanProperty(description = "Separación entre el texto y la flecha.")
    public int getSeparacionFlecha() {
        return separacionFlecha;
    }

    public void setSeparacionFlecha(int separacionFlecha) {
        int anterior = this.separacionFlecha;
        this.separacionFlecha = Math.max(0, separacionFlecha);
        firePropertyChange("separacionFlecha", anterior, this.separacionFlecha);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);

        Color inicio = colorInicio;
        Color fin = degradado ? colorFinal : colorInicio;
        if (!isEnabled()) {
            inicio = mezclar(inicio, Color.LIGHT_GRAY, 0.55f);
            fin = mezclar(fin, Color.LIGHT_GRAY, 0.55f);
        } else if (getModel().isPressed()) {
            inicio = ajustar(inicio, -18);
            fin = ajustar(fin, -18);
        } else if (getModel().isRollover()) {
            inicio = ajustar(inicio, 12);
            fin = ajustar(fin, 12);
        }

        g2.setPaint(new GradientPaint(0, 0, inicio, getWidth(), 0, fin));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);

        if (grosorBorde > 0f && colorBorde.getAlpha() > 0) {
            g2.setStroke(new BasicStroke(grosorBorde));
            g2.setColor(colorBorde);
            int ajuste = Math.max(1, Math.round(grosorBorde));
            g2.drawRoundRect(0, 0, getWidth() - ajuste, getHeight() - ajuste,
                    radio, radio);
        }
        g2.dispose();

        super.paintComponent(graphics);

        if (mostrarFlecha && getText() != null && !getText().isEmpty()) {
            pintarFlecha(graphics);
        }
    }

    private void pintarFlecha(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);
        FontMetrics metricas = g2.getFontMetrics(getFont());
        int centroTexto = getWidth() / 2;
        int x = centroTexto + metricas.stringWidth(getText()) / 2 + separacionFlecha;
        int y = getHeight() / 2;
        int largo = Math.max(10, Math.min(22, getHeight() / 3));

        g2.setColor(isEnabled() ? getForeground() : getForeground().darker());
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.drawLine(x - largo, y, x, y);
        g2.drawLine(x, y, x - largo / 2, y - largo / 2);
        g2.drawLine(x, y, x - largo / 2, y + largo / 2);
        g2.dispose();
    }

    private static void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static Color ajustar(Color color, int cantidad) {
        return new Color(
                limitar(color.getRed() + cantidad),
                limitar(color.getGreen() + cantidad),
                limitar(color.getBlue() + cantidad),
                color.getAlpha()
        );
    }

    private static Color mezclar(Color origen, Color destino, float proporcion) {
        float p = Math.max(0f, Math.min(1f, proporcion));
        return new Color(
                Math.round(origen.getRed() * (1f - p) + destino.getRed() * p),
                Math.round(origen.getGreen() * (1f - p) + destino.getGreen() * p),
                Math.round(origen.getBlue() * (1f - p) + destino.getBlue() * p),
                origen.getAlpha()
        );
    }

    private static int limitar(int valor) {
        return Math.max(0, Math.min(255, valor));
    }
}
