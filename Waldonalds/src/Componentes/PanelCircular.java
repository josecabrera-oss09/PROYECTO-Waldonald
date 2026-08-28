package Componentes;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.BeanProperty;
import javax.swing.JPanel;

/** Contenedor circular para iconos, imágenes, avatares o indicadores. */
@SuppressWarnings({"serial", "this-escape"})
public class PanelCircular extends JPanel {

    private Color colorFondo = new Color(255, 246, 216);
    private Color colorBorde = new Color(0, 0, 0, 0);
    private float grosorBorde;

    /** Constructor vacío requerido por la Paleta de NetBeans. */
    public PanelCircular() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(140, 140));
    }

    @BeanProperty(preferred = true, description = "Color interior del círculo.")
    public Color getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(Color colorFondo) {
        Color anterior = this.colorFondo;
        this.colorFondo = colorFondo != null
                ? colorFondo
                : new Color(0, 0, 0, 0);
        firePropertyChange("colorFondo", anterior, this.colorFondo);
        repaint();
    }

    @BeanProperty(description = "Color del contorno del círculo.")
    public Color getColorBorde() {
        return colorBorde;
    }

    public void setColorBorde(Color colorBorde) {
        Color anterior = this.colorBorde;
        this.colorBorde = colorBorde != null
                ? colorBorde
                : new Color(0, 0, 0, 0);
        firePropertyChange("colorBorde", anterior, this.colorBorde);
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

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);

        int margen = Math.max(1, (int) Math.ceil(grosorBorde));
        int diametro = Math.max(
                1,
                Math.min(getWidth(), getHeight()) - margen * 2
        );
        int x = (getWidth() - diametro) / 2;
        int y = (getHeight() - diametro) / 2;

        g2.setColor(colorFondo);
        g2.fillOval(x, y, diametro, diametro);

        if (grosorBorde > 0f && colorBorde.getAlpha() > 0) {
            g2.setStroke(new BasicStroke(grosorBorde));
            g2.setColor(colorBorde);
            int ajuste = Math.max(1, Math.round(grosorBorde));
            g2.drawOval(
                    x,
                    y,
                    Math.max(1, diametro - ajuste),
                    Math.max(1, diametro - ajuste)
            );
        }
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        double radioX = getWidth() / 2.0;
        double radioY = getHeight() / 2.0;
        if (radioX <= 0 || radioY <= 0) {
            return false;
        }
        double normalizadoX = (x - radioX) / radioX;
        double normalizadoY = (y - radioY) / radioY;
        return normalizadoX * normalizadoX
                + normalizadoY * normalizadoY <= 1.0;
    }

    private static void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE
        );
    }
}
