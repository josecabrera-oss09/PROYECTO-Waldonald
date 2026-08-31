package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

/**
 * Boton reutilizable para la navegacion lateral del modulo administrativo.
 * El fondo, el hover, la seleccion y los iconos se pintan como vectores para
 * conservar nitidez con cualquier tamano de ventana.
 */
public class BotonMenuLateral extends JButton {

    private Color colorNormal = new Color(0, 0, 0, 0);
    private Color colorHover = new Color(255, 255, 255, 28);
    private Color colorSeleccionado = new Color(255, 190, 0);
    private Color colorTextoNormal = Color.WHITE;
    private Color colorTextoSeleccionado = new Color(0, 22, 47);
    private Color colorBorde = new Color(255, 255, 255, 170);
    private int radio = 14;
    private boolean seleccionado;
    private boolean mostrarBorde;
    private String tipoIcono = "DASHBOARD";

    public BotonMenuLateral() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(LEFT);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color fondo = seleccionado ? colorSeleccionado
                : getModel().isRollover() ? colorHover : colorNormal;
        if (getModel().isPressed()) {
            fondo = mezclar(fondo, Color.BLACK, 0.10f);
        }

        Shape forma = new RoundRectangle2D.Float(
                1, 1, getWidth() - 2f, getHeight() - 2f, radio * 2f, radio * 2f);
        if (fondo.getAlpha() > 0) {
            g2.setColor(fondo);
            g2.fill(forma);
        }
        if (mostrarBorde) {
            g2.setColor(colorBorde);
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(forma);
        }

        Color colorContenido = seleccionado ? colorTextoSeleccionado : colorTextoNormal;
        if (!isEnabled()) {
            colorContenido = new Color(colorContenido.getRed(), colorContenido.getGreen(),
                    colorContenido.getBlue(), 110);
        }

        int centroY = getHeight() / 2;
        pintarIcono(g2, 31, centroY, colorContenido);

        g2.setColor(colorContenido);
        g2.setFont(getFont());
        FontMetrics metricas = g2.getFontMetrics();
        int textoY = centroY + (metricas.getAscent() - metricas.getDescent()) / 2;
        g2.drawString(getText() == null ? "" : getText(), 66, textoY);
        g2.dispose();
    }

    private void pintarIcono(Graphics2D g2, int centroX, int centroY, Color color) {
        Graphics2D icono = (Graphics2D) g2.create();
        icono.setColor(color);
        icono.setStroke(new BasicStroke(2.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        icono.translate(centroX - 13, centroY - 13);

        switch (tipoIcono == null ? "" : tipoIcono.trim().toUpperCase()) {
            case "USUARIOS":
                icono.draw(new Ellipse2D.Float(8, 2, 8, 8));
                icono.draw(new Arc2D.Float(4, 10, 16, 14, 15, 150, Arc2D.OPEN));
                icono.draw(new Arc2D.Float(14, 5, 8, 8, 260, 220, Arc2D.OPEN));
                icono.draw(new Arc2D.Float(15, 13, 10, 10, 340, 125, Arc2D.OPEN));
                break;
            case "MENU":
                icono.draw(new Arc2D.Float(2, 7, 22, 13, 0, 180, Arc2D.OPEN));
                icono.draw(new Line2D.Float(2, 14, 24, 14));
                icono.draw(new RoundRectangle2D.Float(2, 17, 22, 4, 2, 2));
                icono.draw(new Line2D.Float(12, 4, 14, 4));
                break;
            case "INGREDIENTES":
                Path2D hoja = new Path2D.Float();
                hoja.moveTo(4, 21);
                hoja.curveTo(4, 8, 13, 3, 24, 2);
                hoja.curveTo(23, 14, 17, 23, 7, 22);
                hoja.closePath();
                icono.draw(hoja);
                icono.draw(new Line2D.Float(4, 24, 18, 8));
                icono.draw(new Line2D.Float(10, 17, 10, 11));
                break;
            case "REPORTES":
                icono.draw(new RoundRectangle2D.Float(2, 15, 4, 9, 1, 1));
                icono.draw(new RoundRectangle2D.Float(10, 9, 4, 15, 1, 1));
                icono.draw(new RoundRectangle2D.Float(18, 3, 4, 21, 1, 1));
                break;
            case "SALIR":
                icono.draw(new RoundRectangle2D.Float(3, 2, 12, 22, 2, 2));
                icono.draw(new Line2D.Float(11, 13, 25, 13));
                icono.draw(new Line2D.Float(20, 8, 25, 13));
                icono.draw(new Line2D.Float(20, 18, 25, 13));
                break;
            case "DASHBOARD":
            default:
                Path2D casa = new Path2D.Float();
                casa.moveTo(2, 12);
                casa.lineTo(13, 3);
                casa.lineTo(24, 12);
                casa.moveTo(5, 10);
                casa.lineTo(5, 23);
                casa.lineTo(21, 23);
                casa.lineTo(21, 10);
                casa.moveTo(10, 23);
                casa.lineTo(10, 16);
                casa.lineTo(16, 16);
                casa.lineTo(16, 23);
                icono.draw(casa);
                break;
        }
        icono.dispose();
    }

    private Color mezclar(Color base, Color destino, float proporcion) {
        float inversa = 1f - proporcion;
        return new Color(
                Math.round(base.getRed() * inversa + destino.getRed() * proporcion),
                Math.round(base.getGreen() * inversa + destino.getGreen() * proporcion),
                Math.round(base.getBlue() * inversa + destino.getBlue() * proporcion),
                base.getAlpha());
    }

    public Color getColorNormal() {
        return colorNormal;
    }

    public void setColorNormal(Color colorNormal) {
        this.colorNormal = colorNormal == null ? new Color(0, 0, 0, 0) : colorNormal;
        repaint();
    }

    public Color getColorHover() {
        return colorHover;
    }

    public void setColorHover(Color colorHover) {
        this.colorHover = colorHover == null ? new Color(255, 255, 255, 28) : colorHover;
        repaint();
    }

    public Color getColorSeleccionado() {
        return colorSeleccionado;
    }

    public void setColorSeleccionado(Color colorSeleccionado) {
        this.colorSeleccionado = colorSeleccionado == null ? new Color(255, 190, 0) : colorSeleccionado;
        repaint();
    }

    public Color getColorTextoNormal() {
        return colorTextoNormal;
    }

    public void setColorTextoNormal(Color colorTextoNormal) {
        this.colorTextoNormal = colorTextoNormal == null ? Color.WHITE : colorTextoNormal;
        repaint();
    }

    public Color getColorTextoSeleccionado() {
        return colorTextoSeleccionado;
    }

    public void setColorTextoSeleccionado(Color colorTextoSeleccionado) {
        this.colorTextoSeleccionado = colorTextoSeleccionado == null
                ? new Color(0, 22, 47) : colorTextoSeleccionado;
        repaint();
    }

    public Color getColorBorde() {
        return colorBorde;
    }

    public void setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde == null ? new Color(255, 255, 255, 170) : colorBorde;
        repaint();
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = Math.max(0, radio);
        repaint();
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        repaint();
    }

    public boolean isMostrarBorde() {
        return mostrarBorde;
    }

    public void setMostrarBorde(boolean mostrarBorde) {
        this.mostrarBorde = mostrarBorde;
        repaint();
    }

    public String getTipoIcono() {
        return tipoIcono;
    }

    public void setTipoIcono(String tipoIcono) {
        this.tipoIcono = tipoIcono == null ? "DASHBOARD" : tipoIcono;
        repaint();
    }
}
