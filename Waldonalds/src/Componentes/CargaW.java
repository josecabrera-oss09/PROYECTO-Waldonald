package Componentes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Logo animado utilizado en la pantalla de carga. */
public class CargaW extends JPanel {

    private static final Color AMARILLO_W = new Color(255, 199, 44);
    private static final int INTERVALO_ANIMACION_MS = 16;
    private static final int UMBRAL_ALPHA = 88;

    // A velocidad 1.0, el destello tarda dos segundos en recorrer la W.
    private static final double DURACION_VUELTA_SEGUNDOS = 2.0;

    private BufferedImage imagenOriginal;
    private BufferedImage imagenEscalada;

    private int tamanoLogo = 184;
    private int anchoLuz = 70;
    private float opacidadBase = 0.20f;
    private float velocidad = 1.0f;
    private final int margen = 8;

    private int ultimoAncho = -1;
    private int ultimoAlto = -1;
    private long inicioAnimacionNanos;
    private Timer timer;

    public CargaW() {
        setOpaque(false);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(280, 180));
        setMinimumSize(new Dimension(280, 180));
        cargarImagen();
        iniciarAnimacion();
    }

    private void cargarImagen() {
        try {
            java.net.URL ruta = CargaW.class.getResource("/Imagenes/CargaW.png");

            if (ruta == null) {
                System.err.println("No se encontro /Imagenes/CargaW.png");
                return;
            }

            BufferedImage original = ImageIO.read(ruta);
            BufferedImage recortada = recortarTransparencia(original);
            imagenOriginal = recolorearImagen(recortada, AMARILLO_W);
        } catch (IOException e) {
            System.err.println("Error leyendo CargaW.png: " + e.getMessage());
        }
    }

    /** Retira el espacio transparente y el halo amplio del PNG original. */
    private BufferedImage recortarTransparencia(BufferedImage original) {
        int minX = original.getWidth();
        int minY = original.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int alpha = (original.getRGB(x, y) >>> 24) & 0xFF;
                if (alpha > UMBRAL_ALPHA) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return original;
        }

        BufferedImage recortada = new BufferedImage(
                maxX - minX + 1,
                maxY - minY + 1,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = recortada.createGraphics();
        g2.drawImage(original, -minX, -minY, null);
        g2.dispose();
        return recortada;
    }

    private BufferedImage recolorearImagen(BufferedImage original, Color color) {
        BufferedImage resultado = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int alpha = (original.getRGB(x, y) >>> 24) & 0xFF;
                if (alpha <= UMBRAL_ALPHA) {
                    continue;
                }

                int alphaLimpio = Math.min(
                        255,
                        Math.round((alpha - UMBRAL_ALPHA)
                                * 255f / (255 - UMBRAL_ALPHA))
                );
                int nuevoPixel = (alphaLimpio << 24)
                        | (color.getRed() << 16)
                        | (color.getGreen() << 8)
                        | color.getBlue();
                resultado.setRGB(x, y, nuevoPixel);
            }
        }

        return resultado;
    }

    private BufferedImage escalarAltaCalidad(
            BufferedImage original,
            int anchoFinal,
            int altoFinal) {

        BufferedImage resultado = new BufferedImage(
                anchoFinal,
                altoFinal,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = resultado.createGraphics();
        aplicarCalidad(g2);
        g2.setComposite(AlphaComposite.Src);
        g2.drawImage(original, 0, 0, anchoFinal, altoFinal, null);
        g2.dispose();
        return resultado;
    }

    private void iniciarAnimacion() {
        inicioAnimacionNanos = System.nanoTime();
        timer = new Timer(INTERVALO_ANIMACION_MS, e -> repaint());
        timer.setCoalesce(true);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (imagenOriginal == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);

        double proporcion = (double) imagenOriginal.getHeight()
                / imagenOriginal.getWidth();
        int anchoDeseado = Math.min(tamanoLogo, getWidth() - margen * 2);
        int altoDeseado = (int) Math.round(anchoDeseado * proporcion);
        int altoMaximo = getHeight() - margen * 2;

        if (altoDeseado > altoMaximo) {
            altoDeseado = altoMaximo;
            anchoDeseado = (int) Math.round(altoDeseado / proporcion);
        }

        if (anchoDeseado <= 0 || altoDeseado <= 0) {
            g2.dispose();
            return;
        }

        if (imagenEscalada == null
                || anchoDeseado != ultimoAncho
                || altoDeseado != ultimoAlto) {
            imagenEscalada = escalarAltaCalidad(
                    imagenOriginal,
                    anchoDeseado,
                    altoDeseado
            );
            ultimoAncho = anchoDeseado;
            ultimoAlto = altoDeseado;
        }

        int x = (getWidth() - anchoDeseado) / 2;
        int y = (getHeight() - altoDeseado) / 2;

        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                opacidadBase
        ));
        g2.drawImage(imagenEscalada, x, y, null);

        double segundos = (System.nanoTime() - inicioAnimacionNanos)
                / 1_000_000_000.0;
        double duracionVuelta = DURACION_VUELTA_SEGUNDOS
                / Math.max(0.15f, velocidad);
        double progreso = (segundos % duracionVuelta) / duracionVuelta;
        int centroLuz = x + (int) Math.round(progreso * anchoDeseado);

        pintarDestello(g2, centroLuz, x, y, anchoDeseado, altoDeseado);
        pintarDestello(
                g2,
                centroLuz - anchoDeseado,
                x,
                y,
                anchoDeseado,
                altoDeseado
        );
        g2.dispose();
    }

    private void pintarDestello(
            Graphics2D g2,
            int centroLuz,
            int xLogo,
            int yLogo,
            int anchoLogo,
            int altoLogo) {

        Shape clipOriginal = g2.getClip();
        int inicio = centroLuz - anchoLuz / 2;

        for (int i = 0; i < anchoLuz; i++) {
            int xActual = inicio + i;
            if (xActual < xLogo || xActual >= xLogo + anchoLogo) {
                continue;
            }

            double distancia = Math.abs((i + 0.5) - anchoLuz / 2.0)
                    / (anchoLuz / 2.0);
            float intensidad = (float) Math.pow(
                    Math.max(0.0, 1.0 - distancia),
                    1.35
            );
            float alpha = intensidad;

            if (alpha <= 0.01f) {
                continue;
            }

            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    alpha
            ));
            g2.setClip(xActual, yLogo, 1, altoLogo);
            g2.drawImage(imagenEscalada, xLogo, yLogo, null);
        }

        g2.setClip(clipOriginal);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.setRenderingHint(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        );
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE
        );
    }

    public void setTamanoLogo(int tamanoLogo) {
        this.tamanoLogo = Math.max(1, tamanoLogo);
        imagenEscalada = null;
        repaint();
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = Math.max(0.15f, velocidad);
    }

    public void setAnchoLuz(int anchoLuz) {
        this.anchoLuz = Math.max(8, anchoLuz);
    }

    public void setOpacidadBase(float opacidadBase) {
        this.opacidadBase = Math.max(0f, Math.min(1f, opacidadBase));
        repaint();
    }

    public void iniciar() {
        inicioAnimacionNanos = System.nanoTime();
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }

    public void detener() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}
