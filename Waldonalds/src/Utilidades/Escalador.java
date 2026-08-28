package Utilidades;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class Escalador {

    // =========================================================
    // RESOLUCIÓN EN LA QUE DISEÑASTE EL PROYECTO EN NETBEANS
    // =========================================================
    private static final int ANCHO_BASE = 1920;
    private static final int ALTO_BASE = 1080;

    private Escalador() {
        // Evita crear objetos de esta clase.
    }

    /**
     * Escala todos los componentes dentro del contenedor.
     *
     * @param contenedor JPanel principal, contentPane, etc.
     */
    public static void escalar(Container contenedor) {

        // Obtener resolución actual del monitor.
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        double escalaX = pantalla.getWidth() / ANCHO_BASE;
        double escalaY = pantalla.getHeight() / ALTO_BASE;

        escalarContenedor(contenedor, escalaX, escalaY);

        contenedor.revalidate();
        contenedor.repaint();
    }

    /**
     * Escala usando un ancho y alto específicos.
     */
    public static void escalar(
            Container contenedor,
            int anchoActual,
            int altoActual) {

        double escalaX = (double) anchoActual / ANCHO_BASE;
        double escalaY = (double) altoActual / ALTO_BASE;

        escalarContenedor(contenedor, escalaX, escalaY);

        contenedor.revalidate();
        contenedor.repaint();
    }

    /**
     * Recorre todos los componentes del formulario.
     */
    private static void escalarContenedor(
            Container contenedor,
            double escalaX,
            double escalaY) {

        for (Component componente : contenedor.getComponents()) {

            escalarComponente(
                    componente,
                    escalaX,
                    escalaY
            );

            // Si también contiene otros componentes,
            // los escala recursivamente.
            if (componente instanceof Container) {

                Container hijo = (Container) componente;

                /*
                 * JScrollPane tiene una estructura interna especial.
                 * No conviene modificar manualmente todos sus componentes
                 * internos.
                 */
                if (!(componente instanceof JScrollPane)) {

                    escalarContenedor(
                            hijo,
                            escalaX,
                            escalaY
                    );
                }
            }
        }
    }

    /**
     * Escala posición, tamaño, fuente e imágenes.
     */
    private static void escalarComponente(
            Component componente,
            double escalaX,
            double escalaY) {

        // ==========================================
        // GUARDAR BOUNDS ORIGINALES
        // ==========================================
        Rectangle bounds = componente.getBounds();

        int nuevaX = (int) Math.round(bounds.x * escalaX);
        int nuevaY = (int) Math.round(bounds.y * escalaY);

        int nuevoAncho
                = (int) Math.round(bounds.width * escalaX);

        int nuevoAlto
                = (int) Math.round(bounds.height * escalaY);

        componente.setBounds(
                nuevaX,
                nuevaY,
                nuevoAncho,
                nuevoAlto
        );

        // ==========================================
        // ESCALAR FUENTE
        // ==========================================
        escalarFuente(
                componente,
                escalaX,
                escalaY
        );

        // ==========================================
        // ESCALAR IMAGEN DE JLABEL
        // ==========================================
        if (componente instanceof JLabel) {

            JLabel label = (JLabel) componente;

            if (!(label instanceof Labels.LabelEscalable)
                    && label.getIcon() instanceof ImageIcon) {

                escalarIconoLabel(
                        label,
                        nuevoAncho,
                        nuevoAlto
                );
            }
        }

        // ==========================================
        // ESCALAR ICONO DE BOTONES
        // ==========================================
        if (componente instanceof AbstractButton) {

            AbstractButton boton
                    = (AbstractButton) componente;

            // Comprobar si este botón debe conservar
            // el tamaño original de su icono
            boolean noEscalarIcono
                    = Boolean.TRUE.equals(
                            boton.getClientProperty("noEscalarIcono")
                    );

            if (!noEscalarIcono
                    && boton.getIcon() instanceof ImageIcon) {

                escalarIconoBoton(
                        boton,
                        nuevoAncho,
                        nuevoAlto
                );
            }
        }

        // ==========================================
        // AJUSTAR FILAS DE JTABLE
        // ==========================================
        if (componente instanceof JTable) {

            JTable tabla = (JTable) componente;

            int alturaOriginal = tabla.getRowHeight();

            int nuevaAlturaFila
                    = (int) Math.round(
                            alturaOriginal * escalaY
                    );

            tabla.setRowHeight(
                    Math.max(nuevaAlturaFila, 1)
            );
        }
    }

    /**
     * Escala automáticamente las fuentes.
     */
    private static void escalarFuente(
            Component componente,
            double escalaX,
            double escalaY) {

        Font fuente = componente.getFont();

        if (fuente == null) {
            return;
        }

        /*
         * Usamos la escala menor para evitar
         * que las letras crezcan demasiado.
         */
        double escalaFuente
                = Math.min(escalaX, escalaY);

        float nuevoTamano
                = (float) (fuente.getSize2D()
                * escalaFuente);

        // Tamaño mínimo para evitar fuentes invisibles.
        nuevoTamano
                = Math.max(nuevoTamano, 8f);

        componente.setFont(
                fuente.deriveFont(nuevoTamano)
        );
    }

    /**
     * Escala una imagen dentro de un JLabel.
     */
    private static void escalarIconoLabel(
            JLabel label,
            int ancho,
            int alto) {

        ImageIcon iconoOriginal
                = (ImageIcon) label.getIcon();

        if (iconoOriginal == null) {
            return;
        }

        if (ancho <= 0 || alto <= 0) {
            return;
        }

        Image imagen = iconoOriginal.getImage();

        Image imagenEscalada
                = imagen.getScaledInstance(
                        ancho,
                        alto,
                        Image.SCALE_SMOOTH
                );

        label.setIcon(
                new ImageIcon(imagenEscalada)
        );
    }

    /**
     * Escala imágenes colocadas dentro de botones.
     */
    private static void escalarIconoBoton(
            AbstractButton boton,
            int ancho,
            int alto) {

        ImageIcon iconoOriginal
                = (ImageIcon) boton.getIcon();

        if (iconoOriginal == null) {
            return;
        }

        if (ancho <= 0 || alto <= 0) {
            return;
        }

        /*
         * Se deja un pequeño margen para que
         * el icono no toque los bordes.
         */
        int anchoIcono
                = Math.max(ancho - 10, 1);

        int altoIcono
                = Math.max(alto - 10, 1);

        Image imagen = iconoOriginal.getImage();

        Image imagenEscalada
                = imagen.getScaledInstance(
                        anchoIcono,
                        altoIcono,
                        Image.SCALE_SMOOTH
                );

        boton.setIcon(
                new ImageIcon(imagenEscalada)
        );
    }
}
