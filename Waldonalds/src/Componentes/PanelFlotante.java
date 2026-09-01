package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.BeanProperty;

import javax.swing.JPanel;

/**
 * Contenedor reutilizable con esquinas redondeadas, borde, degradado y sombra.
 * Tiene constructor vacío para poder instalarse en la Paleta de NetBeans.
 */
@SuppressWarnings({"serial", "this-escape"})
public class PanelFlotante extends JPanel {

    private Color colorFondo = Color.WHITE;
    private Color colorFondoFinal = Color.WHITE;
    private Color colorBorde = new Color(225, 225, 225);
    private Color colorSombra = new Color(25, 32, 43, 35);
    private int radio = 24;
    private float grosorBorde = 1f;
    private boolean degradado;
    private boolean sombra = true;
    private int tamanoSombra = 10;
    private int desplazamientoSombraY = 5;

    public PanelFlotante() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 220));
    }

    @BeanProperty(preferred = true, description = "Color principal del fondo.")
    public Color getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(Color colorFondo) {
        Color anterior = this.colorFondo;
        this.colorFondo = colorFondo != null ? colorFondo : Color.WHITE;
        firePropertyChange("colorFondo", anterior, this.colorFondo);
        repaint();
    }

    @BeanProperty(description = "Segundo color utilizado cuando el degradado está activo.")
    public Color getColorFondoFinal() {
        return colorFondoFinal;
    }

    public void setColorFondoFinal(Color colorFondoFinal) {
        Color anterior = this.colorFondoFinal;
        this.colorFondoFinal = colorFondoFinal != null ? colorFondoFinal : Color.WHITE;
        firePropertyChange("colorFondoFinal", anterior, this.colorFondoFinal);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color del contorno.")
    public Color getColorBorde() {
        return colorBorde;
    }

    public void setColorBorde(Color colorBorde) {
        Color anterior = this.colorBorde;
        this.colorBorde = colorBorde != null ? colorBorde : new Color(0, 0, 0, 0);
        firePropertyChange("colorBorde", anterior, this.colorBorde);
        repaint();
    }

    @BeanProperty(description = "Color de la sombra, incluido su nivel de transparencia.")
    public Color getColorSombra() {
        return colorSombra;
    }

    public void setColorSombra(Color colorSombra) {
        Color anterior = this.colorSombra;
        this.colorSombra = colorSombra != null ? colorSombra : new Color(0, 0, 0, 0);
        firePropertyChange("colorSombra", anterior, this.colorSombra);
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

    @BeanProperty(description = "Pinta el fondo usando los dos colores configurados.")
    public boolean isDegradado() {
        return degradado;
    }

    public void setDegradado(boolean degradado) {
        boolean anterior = this.degradado;
        this.degradado = degradado;
        firePropertyChange("degradado", anterior, degradado);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Activa la elevación visual del panel.")
    public boolean isSombra() {
        return sombra;
    }

    public void setSombra(boolean sombra) {
        boolean anterior = this.sombra;
        this.sombra = sombra;
        firePropertyChange("sombra", anterior, sombra);
        repaint();
    }

    @BeanProperty(description = "Extensión visual de la sombra.")
    public int getTamanoSombra() {
        return tamanoSombra;
    }

    public void setTamanoSombra(int tamanoSombra) {
        int anterior = this.tamanoSombra;
        this.tamanoSombra = Math.max(0, tamanoSombra);
        firePropertyChange("tamanoSombra", anterior, this.tamanoSombra);
        repaint();
    }

    @BeanProperty(description = "Desplazamiento vertical de la sombra.")
    public int getDesplazamientoSombraY() {
        return desplazamientoSombraY;
    }

    public void setDesplazamientoSombraY(int desplazamientoSombraY) {
        int anterior = this.desplazamientoSombraY;
        this.desplazamientoSombraY = desplazamientoSombraY;
        firePropertyChange("desplazamientoSombraY", anterior, desplazamientoSombraY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);

        int margen = sombra ? Math.max(2, tamanoSombra) : 2;
        int x = margen;
        int y = margen;
        int ancho = Math.max(1, getWidth() - margen * 2);
        int alto = Math.max(1, getHeight() - margen * 2 - Math.max(0, desplazamientoSombraY));

        if (sombra && tamanoSombra > 0) {
            pintarSombra(g2, x, y, ancho, alto);
        }

        if (degradado) {
            g2.setPaint(new GradientPaint(
                    x,
                    y,
                    colorFondo,
                    x + ancho,
                    y + alto,
                    colorFondoFinal
            ));
        } else {
            g2.setColor(colorFondo);
        }
        g2.fillRoundRect(x, y, ancho, alto, radio, radio);

        if (grosorBorde > 0f && colorBorde.getAlpha() > 0) {
            g2.setStroke(new BasicStroke(grosorBorde));
            g2.setColor(colorBorde);
            int ajuste = Math.max(1, Math.round(grosorBorde));
            g2.drawRoundRect(
                    x,
                    y,
                    Math.max(1, ancho - ajuste),
                    Math.max(1, alto - ajuste),
                    radio,
                    radio
            );
        }
        g2.dispose();
    }

    private void pintarSombra(Graphics2D g2, int x, int y, int ancho, int alto) {
        int alphaBase = colorSombra.getAlpha();
        for (int nivel = tamanoSombra; nivel >= 1; nivel--) {
            float proporcion = 1f - (nivel - 1f) / Math.max(1f, tamanoSombra);
            int alpha = Math.max(1, Math.round(alphaBase * proporcion * 0.32f));
            g2.setColor(new Color(
                    colorSombra.getRed(),
                    colorSombra.getGreen(),
                    colorSombra.getBlue(),
                    alpha
            ));
            g2.fillRoundRect(
                    x - nivel / 2,
                    y + desplazamientoSombraY + nivel / 2,
                    ancho + nivel,
                    alto + nivel,
                    radio + nivel,
                    radio + nivel
            );
        }
    }

    private static void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
    }
}

