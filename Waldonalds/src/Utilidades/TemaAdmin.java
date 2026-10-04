package Utilidades;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

/** Tipografía y calidad gráfica utilizadas por las pantallas administrativas. */
public final class TemaAdmin {

    public static final Color AMARILLO = new Color(255, 188, 0);

    private final Font fuenteRegular;
    private final Font fuenteMedia;
    private final Font fuenteNegrita;

    public TemaAdmin() {
        fuenteRegular = cargarFuente("DMSans-Regular.ttf", Font.PLAIN);
        fuenteMedia = cargarFuente("DMSans-Medium.ttf", Font.PLAIN);
        fuenteNegrita = cargarFuente("DMSans-Bold.ttf", Font.BOLD);
    }

    public int px(double valor) {
        return Math.max(1, (int) Math.round(valor));
    }

    public Font regular(float tamano) {
        return fuenteRegular.deriveFont(tamano);
    }

    public Font media(float tamano) {
        return fuenteMedia.deriveFont(tamano);
    }

    public Font negrita(float tamano) {
        return fuenteNegrita.deriveFont(tamano);
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
