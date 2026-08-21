package Componentes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CargaW extends JPanel {

    private BufferedImage imagen;

    // Tamaño del logo W
    private int tamanoLogo = 100;

    // Posición de la franja iluminada
    private float posicionLuz = -60f;

    // Velocidad de la animación
    private float velocidad = 2.8f;

    // Ancho de la franja iluminada
    private int anchoLuz = 44;

    // Opacidad de la W tenue del fondo
    private float opacidadBase = 0.22f;

    // Margen interno para que nunca se corte
    private int margen = 12;

    private Timer timer;

    public CargaW() {

        setOpaque(false);

        // Área del componente
        setPreferredSize(new Dimension(180, 140));
        setMinimumSize(new Dimension(180, 140));

        cargarImagen();
        iniciarAnimacion();
    }

    private void cargarImagen() {

        try {

            java.net.URL ruta =
                    CargaW.class.getResource("/Imagenes/CargaW.png");

            if (ruta == null) {

                System.out.println(
                        "ERROR: No se encontro /Imagenes/CargaW.png"
                );

                return;
            }

            imagen = ImageIO.read(ruta);

        } catch (IOException e) {

            System.out.println(
                    "Error leyendo CargaW.png: "
                    + e.getMessage()
            );
        }
    }

    private void iniciarAnimacion() {

        timer = new Timer(16, e -> {

            posicionLuz += velocidad;

            if (posicionLuz > getWidth() + anchoLuz) {
                posicionLuz = -anchoLuz;
            }

            repaint();
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (imagen == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        aplicarCalidad(g2);

        // =========================================
        // CALCULAR TAMAÑO FINAL DEL LOGO
        // =========================================

        int anchoOriginal = imagen.getWidth();
        int altoOriginal = imagen.getHeight();

        double proporcion = (double) altoOriginal / anchoOriginal;

        // Tamaño deseado
        int anchoDeseado = tamanoLogo;
        int altoDeseado = (int) (anchoDeseado * proporcion);

        // Asegurar que NO se salga del panel
        int anchoMax = getWidth() - (margen * 2);
        int altoMax = getHeight() - (margen * 2);

        if (anchoDeseado > anchoMax) {
            anchoDeseado = anchoMax;
            altoDeseado = (int) (anchoDeseado * proporcion);
        }

        if (altoDeseado > altoMax) {
            altoDeseado = altoMax;
            anchoDeseado = (int) (altoDeseado / proporcion);
        }

        int x = (getWidth() - anchoDeseado) / 2;
        int y = (getHeight() - altoDeseado) / 2;

        // =========================================
        // W TENUE DE FONDO
        // =========================================

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        opacidadBase
                )
        );

        g2.drawImage(
                imagen,
                x,
                y,
                anchoDeseado,
                altoDeseado,
                null
        );

        // =========================================
        // CAPA ILUMINADA CON BORDES SUAVES
        // =========================================

        BufferedImage capaImagen = new BufferedImage(
                getWidth(),
                getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D gi = capaImagen.createGraphics();
        aplicarCalidad(gi);

        gi.drawImage(
                imagen,
                x,
                y,
                anchoDeseado,
                altoDeseado,
                null
        );

        gi.dispose();

        BufferedImage mascara = new BufferedImage(
                getWidth(),
                getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D gm = mascara.createGraphics();
        aplicarCalidad(gm);

        float inicio = posicionLuz;
        float fin = posicionLuz + anchoLuz;

        LinearGradientPaint gradiente = new LinearGradientPaint(
                inicio, 0,
                fin, 0,
                new float[]{0f, 0.20f, 0.50f, 0.80f, 1f},
                new Color[]{
                    new Color(255, 255, 255, 0),
                    new Color(255, 255, 255, 90),
                    new Color(255, 255, 255, 255),
                    new Color(255, 255, 255, 90),
                    new Color(255, 255, 255, 0)
                }
        );

        gm.setPaint(gradiente);
        gm.fillRect(
                Math.round(inicio),
                y,
                anchoLuz,
                altoDeseado
        );

        gm.dispose();

        Graphics2D gc = capaImagen.createGraphics();
        aplicarCalidad(gc);

        gc.setComposite(AlphaComposite.DstIn);
        gc.drawImage(mascara, 0, 0, null);

        gc.dispose();

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        1.0f
                )
        );

        g2.drawImage(capaImagen, 0, 0, null);

        g2.dispose();
    }

    private void aplicarCalidad(Graphics2D g2) {

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
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
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE
        );
    }

    // =========================================
    // MÉTODOS PARA CAMBIAR VALORES FÁCILMENTE
    // =========================================

    public void setTamanoLogo(int tamanoLogo) {
        this.tamanoLogo = tamanoLogo;
        repaint();
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public void setAnchoLuz(int anchoLuz) {
        this.anchoLuz = anchoLuz;
    }

    public void setOpacidadBase(float opacidadBase) {
        this.opacidadBase = opacidadBase;
        repaint();
    }

    public void iniciar() {

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