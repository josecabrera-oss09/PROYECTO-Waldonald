package Utilidades;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

/** Tipografía, escala y calidad gráfica utilizadas por InicioAdminForm. */
public final class TemaAdmin {

    public static final int ANCHO_REFERENCIA = 1680;
    public static final int ALTO_REFERENCIA = 945;

    public static final Color AMARILLO = new Color(255, 188, 0);

    private final double escala;
    private final Font fuenteRegular;
    private final Font fuenteMedia;
    private final Font fuenteNegrita;

    public TemaAdmin() {
        escala = calcularEscala();
        fuenteRegular = cargarFuente("DMSans-Regular.ttf", Font.PLAIN);
        fuenteMedia = cargarFuente("DMSans-Medium.ttf", Font.PLAIN);
        fuenteNegrita = cargarFuente("DMSans-Bold.ttf", Font.BOLD);
    }

    public int px(double valor) {
        return Math.max(1, (int) Math.round(valor * escala));
    }

    public Font regular(float tamano) {
        return fuenteRegular.deriveFont((float) (tamano * escala));
    }

    public Font media(float tamano) {
        return fuenteMedia.deriveFont((float) (tamano * escala));
    }

    public Font negrita(float tamano) {
        return fuenteNegrita.deriveFont((float) (tamano * escala));
    }

    private double calcularEscala() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        double escalaX = pantalla.getWidth() / ANCHO_REFERENCIA;
        double escalaY = pantalla.getHeight() / ALTO_REFERENCIA;
        return Math.max(0.72, Math.min(1.15, Math.min(escalaX, escalaY)));
    }

    private Font cargarFuente(String archivo, int estiloAlternativo) {
        try (InputStream entrada = TemaAdmin.class.getResourceAsStream(
                "/Font/DMSans/" + archivo)) {
            if (entrada != null) {
                Font fuente = Font.createFont(Font.TRUETYPE_FONT, entrada);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
                return fuente;
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("No se pudo cargar " + archivo + ": " + e.getMessage());
        }
        return new Font("SansSerif", estiloAlternativo, 14);
    }

    public static void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
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
