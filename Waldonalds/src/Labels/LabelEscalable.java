package Labels;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.BeanProperty;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/** JLabel que escala imágenes con alta calidad y caché por tamaño. */
@SuppressWarnings({"serial", "this-escape"})
public class LabelEscalable extends JLabel {

    private BufferedImage imagenOriginal;
    private BufferedImage imagenEscalada;
    private boolean mantenerProporcion;
    private int anchoCache = -1;
    private int altoCache = -1;

    @Override
    public void setIcon(javax.swing.Icon icon) {
        super.setIcon(icon);
        if (icon instanceof ImageIcon) {
            imagenOriginal = convertir((ImageIcon) icon);
        } else {
            imagenOriginal = null;
        }
        invalidarCache();
        repaint();
    }

    @BeanProperty(preferred = true,
            description = "Conserva la proporción original de la imagen.")
    public boolean isMantenerProporcion() {
        return mantenerProporcion;
    }

    public void setMantenerProporcion(boolean mantenerProporcion) {
        boolean anterior = this.mantenerProporcion;
        this.mantenerProporcion = mantenerProporcion;
        invalidarCache();
        firePropertyChange("mantenerProporcion", anterior, mantenerProporcion);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (imagenOriginal == null || getWidth() <= 0 || getHeight() <= 0) {
            super.paintComponent(graphics);
            return;
        }

        int anchoObjetivo = getWidth();
        int altoObjetivo = getHeight();
        if (mantenerProporcion) {
            double escala = Math.min(
                    getWidth() / (double) imagenOriginal.getWidth(),
                    getHeight() / (double) imagenOriginal.getHeight()
            );
            anchoObjetivo = Math.max(
                    1,
                    (int) Math.round(imagenOriginal.getWidth() * escala)
            );
            altoObjetivo = Math.max(
                    1,
                    (int) Math.round(imagenOriginal.getHeight() * escala)
            );
        }

        if (imagenEscalada == null
                || anchoCache != anchoObjetivo
                || altoCache != altoObjetivo) {
            imagenEscalada = reducirProgresivamente(
                    imagenOriginal,
                    anchoObjetivo,
                    altoObjetivo
            );
            anchoCache = anchoObjetivo;
            altoCache = altoObjetivo;
        }

        int x = (getWidth() - imagenEscalada.getWidth()) / 2;
        int y = (getHeight() - imagenEscalada.getHeight()) / 2;
        graphics.drawImage(imagenEscalada, x, y, this);
    }

    private BufferedImage convertir(ImageIcon icono) {
        int ancho = Math.max(1, icono.getIconWidth());
        int alto = Math.max(1, icono.getIconHeight());
        BufferedImage resultado = new BufferedImage(
                ancho,
                alto,
                BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2 = resultado.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.drawImage(icono.getImage(), 0, 0, ancho, alto, null);
        g2.dispose();
        return resultado;
    }

    private BufferedImage reducirProgresivamente(
            BufferedImage origen,
            int anchoObjetivo,
            int altoObjetivo) {

        BufferedImage actual = origen;
        while (actual.getWidth() > anchoObjetivo * 2
                || actual.getHeight() > altoObjetivo * 2) {
            int anchoSiguiente = Math.max(anchoObjetivo, actual.getWidth() / 2);
            int altoSiguiente = Math.max(altoObjetivo, actual.getHeight() / 2);
            actual = escalarEtapa(actual, anchoSiguiente, altoSiguiente);
        }
        if (actual.getWidth() != anchoObjetivo
                || actual.getHeight() != altoObjetivo) {
            actual = escalarEtapa(actual, anchoObjetivo, altoObjetivo);
        }
        return actual;
    }

    private BufferedImage escalarEtapa(Image origen, int ancho, int alto) {
        BufferedImage destino = new BufferedImage(
                ancho,
                alto,
                BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2 = destino.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(origen, 0, 0, ancho, alto, null);
        g2.dispose();
        return destino;
    }

    private void invalidarCache() {
        imagenEscalada = null;
        anchoCache = -1;
        altoCache = -1;
    }
}

