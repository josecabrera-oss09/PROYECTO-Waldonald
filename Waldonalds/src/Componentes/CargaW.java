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

public class CargaW extends JPanel {

    // Imagen original
    private BufferedImage imagenOriginal;

    // Imagen ya reducida con alta calidad
    private BufferedImage imagenEscalada;

    // ===============================
    // COLORES
    // ===============================

    // Amarillo clásico
    private final Color AMARILLO_W =
            new Color(255, 199, 44);

    // ===============================
    // CONFIGURACIÓN
    // ===============================

    // Tamaño del logo
    private int tamanoLogo = 180;

    // Posición de la luz
    private float posicionLuz = -80f;

    // Velocidad
    private float velocidad = 3.2f;

    // Ancho de la iluminación
    private int anchoLuz = 62;

    // Opacidad de la W de fondo
    private float opacidadBase = 0.13f;

    // Margen para evitar cortes
    private final int margen = 14;

    // Último tamaño procesado
    private int ultimoAncho = -1;
    private int ultimoAlto = -1;

    private Timer timer;

    public CargaW() {

        setOpaque(false);

        setDoubleBuffered(true);

        setPreferredSize(
                new Dimension(300, 200)
        );

        setMinimumSize(
                new Dimension(300, 200)
        );

        cargarImagen();

        iniciarAnimacion();
    }

    // =================================================
    // CARGAR IMAGEN
    // =================================================

    private void cargarImagen() {

        try {

            java.net.URL ruta =
                    CargaW.class.getResource(
                            "/Imagenes/CargaW.png"
                    );

            if (ruta == null) {

                System.out.println(
                        "ERROR: No se encontro /Imagenes/CargaW.png"
                );

                return;
            }

            BufferedImage original =
                    ImageIO.read(ruta);

            /*
             * Convertimos la imagen al amarillo exacto:
             *
             * RGB(255, 199, 44)
             * #FFC72C
             */
            imagenOriginal =
                    recolorearImagen(
                            original,
                            AMARILLO_W
                    );

        } catch (IOException e) {

            System.out.println(
                    "Error leyendo CargaW.png: "
                    + e.getMessage()
            );
        }
    }

    // =================================================
    // RECOLOREAR
    // =================================================

    private BufferedImage recolorearImagen(
            BufferedImage original,
            Color color) {

        BufferedImage resultado =
                new BufferedImage(
                        original.getWidth(),
                        original.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

        for (int y = 0;
                y < original.getHeight();
                y++) {

            for (int x = 0;
                    x < original.getWidth();
                    x++) {

                int pixel =
                        original.getRGB(x, y);

                /*
                 * Conservamos la transparencia
                 * y el antialiasing original
                 * del PNG.
                 */
                int alpha =
                        (pixel >>> 24) & 0xFF;

                if (alpha > 0) {

                    int nuevoPixel =
                            (alpha << 24)
                            | (color.getRed() << 16)
                            | (color.getGreen() << 8)
                            | color.getBlue();

                    resultado.setRGB(
                            x,
                            y,
                            nuevoPixel
                    );
                }
            }
        }

        return resultado;
    }

    // =================================================
    // ESCALADO DE ALTA CALIDAD
    // =================================================

    private BufferedImage escalarAltaCalidad(
            BufferedImage original,
            int anchoFinal,
            int altoFinal) {

        /*
         * Tu imagen es grande, aproximadamente
         * 845 × 669.
         *
         * No la reducimos directamente.
         *
         * Ejemplo:
         *
         * 845
         * ↓
         * 422
         * ↓
         * 211
         * ↓
         * 180
         *
         * Esto mantiene los bordes mucho
         * más suaves.
         */

        BufferedImage actual =
                original;

        int anchoActual =
                original.getWidth();

        int altoActual =
                original.getHeight();

        while (anchoActual / 2 >= anchoFinal
                && altoActual / 2 >= altoFinal) {

            int nuevoAncho =
                    anchoActual / 2;

            int nuevoAlto =
                    altoActual / 2;

            BufferedImage temporal =
                    new BufferedImage(
                            nuevoAncho,
                            nuevoAlto,
                            BufferedImage.TYPE_INT_ARGB
                    );

            Graphics2D g2 =
                    temporal.createGraphics();

            aplicarCalidad(g2);

            g2.drawImage(
                    actual,
                    0,
                    0,
                    nuevoAncho,
                    nuevoAlto,
                    null
            );

            g2.dispose();

            actual =
                    temporal;

            anchoActual =
                    nuevoAncho;

            altoActual =
                    nuevoAlto;
        }

        /*
         * Último escalado hasta
         * el tamaño exacto.
         */
        BufferedImage resultado =
                new BufferedImage(
                        anchoFinal,
                        altoFinal,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                resultado.createGraphics();

        aplicarCalidad(g2);

        g2.drawImage(
                actual,
                0,
                0,
                anchoFinal,
                altoFinal,
                null
        );

        g2.dispose();

        return resultado;
    }

    // =================================================
    // TIMER
    // =================================================

    private void iniciarAnimacion() {

        /*
         * 16 ms ≈ 60 FPS
         */
        timer =
                new Timer(16, e -> {

            posicionLuz +=
                    velocidad;

            if (posicionLuz
                    > getWidth()
                    + anchoLuz) {

                posicionLuz =
                        -anchoLuz;
            }

            repaint();
        });

        timer.setCoalesce(true);

        timer.start();
    }

    // =================================================
    // PINTAR
    // =================================================

    @Override
    protected void paintComponent(
            Graphics graphics) {

        super.paintComponent(graphics);

        if (imagenOriginal == null) {
            return;
        }

        Graphics2D g2 =
                (Graphics2D)
                graphics.create();

        aplicarCalidad(g2);

        // ============================================
        // CALCULAR TAMAÑO
        // ============================================

        double proporcion =
                (double)
                imagenOriginal.getHeight()
                / imagenOriginal.getWidth();

        int anchoDeseado =
                tamanoLogo;

        int altoDeseado =
                (int)
                Math.round(
                        anchoDeseado
                        * proporcion
                );

        int anchoMax =
                getWidth()
                - margen * 2;

        int altoMax =
                getHeight()
                - margen * 2;

        /*
         * Evitar cortes.
         */
        if (anchoDeseado > anchoMax) {

            anchoDeseado =
                    anchoMax;

            altoDeseado =
                    (int)
                    Math.round(
                            anchoDeseado
                            * proporcion
                    );
        }

        if (altoDeseado > altoMax) {

            altoDeseado =
                    altoMax;

            anchoDeseado =
                    (int)
                    Math.round(
                            altoDeseado
                            / proporcion
                    );
        }

        // ============================================
        // ESCALAR UNA SOLA VEZ
        // ============================================

        if (imagenEscalada == null
                || anchoDeseado != ultimoAncho
                || altoDeseado != ultimoAlto) {

            imagenEscalada =
                    escalarAltaCalidad(
                            imagenOriginal,
                            anchoDeseado,
                            altoDeseado
                    );

            ultimoAncho =
                    anchoDeseado;

            ultimoAlto =
                    altoDeseado;
        }

        // Centrar
        int x =
                (getWidth()
                - anchoDeseado) / 2;

        int y =
                (getHeight()
                - altoDeseado) / 2;

        // ============================================
        // W TENUE
        // ============================================

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        opacidadBase
                )
        );

        /*
         * IMPORTANTE:
         *
         * Ya NO escalamos aquí.
         *
         * La imagen ya tiene exactamente
         * el tamaño necesario.
         */
        g2.drawImage(
                imagenEscalada,
                x,
                y,
                null
        );

        // ============================================
        // W ILUMINADA
        // ============================================

        g2.setComposite(
                AlphaComposite.SrcOver
        );

        /*
         * Guardamos el clip original.
         */
        Shape clipOriginal =
                g2.getClip();

        /*
         * Creamos la franja de iluminación.
         *
         * Tiene:
         *
         * 10% entrada suave
         * 80% amarillo sólido
         * 10% salida suave
         */

        int borde =
                Math.max(
                        4,
                        anchoLuz / 10
                );

        int centro =
                anchoLuz
                - borde * 2;

        int posicion =
                Math.round(
                        posicionLuz
                );

        // -------------------------------
        // BORDE IZQUIERDO
        // -------------------------------

        for (int i = 0;
                i < borde;
                i++) {

            float alpha =
                    (i + 1f)
                    / borde;

            /*
             * Smoothstep:
             * evita sensación brusca.
             */
            alpha =
                    alpha
                    * alpha
                    * (3f - 2f * alpha);

            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            alpha
                    )
            );

            g2.setClip(
                    posicion + i,
                    y,
                    1,
                    altoDeseado
            );

            g2.drawImage(
                    imagenEscalada,
                    x,
                    y,
                    null
            );
        }

        // -------------------------------
        // CENTRO COMPLETAMENTE SÓLIDO
        // -------------------------------

        if (centro > 0) {

            g2.setComposite(
                    AlphaComposite.SrcOver
            );

            g2.setClip(
                    posicion + borde,
                    y,
                    centro,
                    altoDeseado
            );

            g2.drawImage(
                    imagenEscalada,
                    x,
                    y,
                    null
            );
        }

        // -------------------------------
        // BORDE DERECHO
        // -------------------------------

        for (int i = 0;
                i < borde;
                i++) {

            float alpha =
                    1f
                    - (
                        (i + 1f)
                        / borde
                    );

            alpha =
                    Math.max(
                            0f,
                            alpha
                    );

            alpha =
                    alpha
                    * alpha
                    * (3f - 2f * alpha);

            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            alpha
                    )
            );

            g2.setClip(
                    posicion
                    + borde
                    + centro
                    + i,
                    y,
                    1,
                    altoDeseado
            );

            g2.drawImage(
                    imagenEscalada,
                    x,
                    y,
                    null
            );
        }

        /*
         * Restauramos clip.
         */
        g2.setClip(
                clipOriginal
        );

        g2.setComposite(
                AlphaComposite.SrcOver
        );

        g2.dispose();
    }

    // =================================================
    // CALIDAD
    // =================================================

    private void aplicarCalidad(
            Graphics2D g2) {

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

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
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY
        );

        g2.setRenderingHint(
                RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE
        );

        g2.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE
        );
    }

    // =================================================
    // CONFIGURACIÓN PÚBLICA
    // =================================================

    public void setTamanoLogo(
            int tamanoLogo) {

        this.tamanoLogo =
                tamanoLogo;

        /*
         * Obligar a recalcular
         * la imagen escalada.
         */
        imagenEscalada = null;

        repaint();
    }

    public void setVelocidad(
            float velocidad) {

        this.velocidad =
                velocidad;
    }

    public void setAnchoLuz(
            int anchoLuz) {

        this.anchoLuz =
                anchoLuz;
    }

    public void setOpacidadBase(
            float opacidadBase) {

        this.opacidadBase =
                Math.max(
                        0f,
                        Math.min(
                                1f,
                                opacidadBase
                        )
                );

        repaint();
    }

    public void iniciar() {

        if (timer != null
                && !timer.isRunning()) {

            timer.start();
        }
    }

    public void detener() {

        if (timer != null
                && timer.isRunning()) {

            timer.stop();
        }
    }
}