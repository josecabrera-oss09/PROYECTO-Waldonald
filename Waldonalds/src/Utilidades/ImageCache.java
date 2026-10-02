package Utilidades;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/** Carga y escala imágenes fuera del EDT, reutilizando resultados en memoria. */
public final class ImageCache {

    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
            4, runnable -> {
                Thread hilo = new Thread(runnable, "waldonald-imagenes");
                hilo.setDaemon(true);
                return hilo;
            });

    private ImageCache() { }

    public static void cargarAsync(String ruta, int ancho, int alto,
            Consumer<ImageIcon> callback) {
        if (ruta == null || ruta.isBlank()) {
            SwingUtilities.invokeLater(() -> callback.accept(null));
            return;
        }

        String clave = ruta + "|" + ancho + "x" + alto;
        ImageIcon cacheada = CACHE.get(clave);
        if (cacheada != null) {
            SwingUtilities.invokeLater(() -> callback.accept(cacheada));
            return;
        }

        EXECUTOR.execute(() -> {
            ImageIcon resultado = cargarYEscalar(ruta, ancho, alto);
            if (resultado != null) CACHE.putIfAbsent(clave, resultado);
            SwingUtilities.invokeLater(() -> callback.accept(resultado));
        });
    }

    private static ImageIcon cargarYEscalar(String ruta, int ancho, int alto) {
        try {
            BufferedImage original;
            URL recurso = ImageCache.class.getResource(ruta);
            if (recurso != null) {
                original = ImageIO.read(recurso);
            } else {
                File archivo = new File(ruta);
                if (!archivo.exists()) return null;
                original = ImageIO.read(archivo);
            }
            if (original == null) return null;

            BufferedImage escalada = new BufferedImage(
                    ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = escalada.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            graphics.drawImage(original.getScaledInstance(
                    ancho, alto, Image.SCALE_SMOOTH), 0, 0, null);
            graphics.dispose();
            return new ImageIcon(escalada);
        } catch (Exception ex) {
            return null;
        }
    }
}
