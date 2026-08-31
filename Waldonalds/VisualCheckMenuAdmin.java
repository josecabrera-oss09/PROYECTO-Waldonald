import GUI_ADMINISTRADOR.MenuAdmin;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public final class VisualCheckMenuAdmin {
    private VisualCheckMenuAdmin() {
    }

    public static void main(String[] args) throws Exception {
        final MenuAdmin[] ventana = new MenuAdmin[1];
        SwingUtilities.invokeAndWait(() -> ventana[0] = new MenuAdmin());
        SwingUtilities.invokeAndWait(() -> {
            Container contenido = ventana[0].getContentPane();
            contenido.setSize(1920, 1080);
            organizar(contenido);
            BufferedImage imagen = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graficos = imagen.createGraphics();
            contenido.printAll(graficos);
            graficos.dispose();
            try {
                ImageIO.write(imagen, "png", new File(args[0]));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            ventana[0].dispose();
        });
    }

    private static void organizar(Container contenedor) {
        contenedor.doLayout();
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof Container hijo) {
                organizar(hijo);
            }
        }
    }
}
