package Utilidades;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class Escalador {

    // =========================================================
    // RESOLUCIÓN ORIGINAL DEL DISEÑO EN NETBEANS
    // =========================================================
    private static final int ANCHO_BASE = 1920;
    private static final int ALTO_BASE = 1080;

    // =========================================================
    // GUARDAR INFORMACIÓN ORIGINAL DE CADA VENTANA
    // =========================================================
    private static final Map<JFrame, DatosVentana> ventanas
            = new IdentityHashMap<>();

    private Escalador() {
        // Evita crear objetos de esta clase
    }

    // =========================================================
    // MÉTODO PRINCIPAL
    // ESTE ES EL QUE UTILIZARÁS EN TODOS LOS FORMS
    // =========================================================
    public static void aplicar(JFrame ventana) {

    // Evitar aplicar el escalador dos veces
    if (ventanas.containsKey(ventana)) {
        return;
    }

    /*
     * Primero obligamos a NetBeans a colocar
     * todos los componentes en sus posiciones originales.
     */
    ventana.pack();

    /*
     * IMPORTANTE:
     * AbsoluteLayout volvería a colocar los componentes
     * después de modificarlos con setBounds().
     *
     * Por eso lo desactivamos después de que NetBeans
     * haya colocado todo por primera vez.
     */
    desactivarAbsoluteLayout(
            ventana.getContentPane()
    );

    // Crear almacenamiento de valores originales
    DatosVentana datos = new DatosVentana();

    // Guardar posiciones, tamaños, fuentes, etc.
    guardarOriginales(
            ventana.getContentPane(),
            datos
    );

    ventanas.put(
            ventana,
            datos
    );

    // Maximizar la ventana
    ventana.setExtendedState(
            JFrame.MAXIMIZED_BOTH
    );

    // Detectar cambios de tamaño
    ventana.addComponentListener(
            new ComponentAdapter() {

        @Override
        public void componentResized(
                ComponentEvent e) {

            escalarVentana(
                    ventana,
                    datos
            );
        }
    });

    /*
     * Esperamos hasta que JFrame haya terminado
     * de maximizarse.
     */
    SwingUtilities.invokeLater(() -> {

        escalarVentana(
                ventana,
                datos
        );

    });
}
    
    
    private static void desactivarAbsoluteLayout(
        Container contenedor) {

    /*
     * Primero permitimos que el layout coloque
     * los componentes en su posición original.
     */
    contenedor.doLayout();

    /*
     * Verificar si el contenedor utiliza
     * AbsoluteLayout de NetBeans.
     */
    if (contenedor.getLayout() != null
            && contenedor.getLayout()
                    .getClass()
                    .getName()
                    .equals(
                        "org.netbeans.lib.awtextra.AbsoluteLayout"
                    )) {

        /*
         * Al poner null conservamos las posiciones
         * actuales pero evitamos que AbsoluteLayout
         * las vuelva a modificar.
         */
        contenedor.setLayout(null);
    }

    // Revisar componentes internos
    for (Component componente
            : contenedor.getComponents()) {

        if (componente instanceof Container
                && !(componente
                instanceof JScrollPane)) {

            desactivarAbsoluteLayout(
                    (Container) componente
            );
        }
    }
}

    // =========================================================
    // ESCALAR VENTANA
    // =========================================================
    private static void escalarVentana(
            JFrame ventana,
            DatosVentana datos) {

        int anchoActual
                = ventana.getContentPane().getWidth();

        int altoActual
                = ventana.getContentPane().getHeight();

        if (anchoActual <= 0 || altoActual <= 0) {
            return;
        }

        double escalaX
                = (double) anchoActual / ANCHO_BASE;

        double escalaY
                = (double) altoActual / ALTO_BASE;

        escalarContenedor(
                ventana.getContentPane(),
                datos,
                escalaX,
                escalaY
        );

        ventana.revalidate();
        ventana.repaint();
    }

    // =========================================================
    // GUARDAR VALORES ORIGINALES
    // =========================================================
    private static void guardarOriginales(
            Container contenedor,
            DatosVentana datos) {

        for (Component componente
                : contenedor.getComponents()) {

            // Guardar posición y tamaño
            datos.bounds.put(
                    componente,
                    new Rectangle(
                            componente.getBounds()
                    )
            );

            // Guardar fuente
            if (componente.getFont() != null) {

                datos.fuentes.put(
                        componente,
                        componente.getFont()
                );
            }

            // Guardar altura original de las filas
            if (componente instanceof JTable) {

                JTable tabla = (JTable) componente;

                datos.alturaFilas.put(
                        tabla,
                        tabla.getRowHeight()
                );
            }

            // Guardar icono ORIGINAL de JLabel
            if (componente instanceof JLabel) {

                JLabel label = (JLabel) componente;

                if (!(label
                        instanceof Labels.LabelEscalable)
                        && label.getIcon()
                        instanceof ImageIcon) {

                    datos.iconos.put(
                            componente,
                            (ImageIcon) label.getIcon()
                    );
                }
            }

            // Guardar icono ORIGINAL de botones
            if (componente instanceof AbstractButton) {

                AbstractButton boton
                        = (AbstractButton) componente;

                if (boton.getIcon()
                        instanceof ImageIcon) {

                    datos.iconos.put(
                            componente,
                            (ImageIcon) boton.getIcon()
                    );
                }
            }

            // Recorrer componentes hijos
            if (componente instanceof Container
                    && !(componente
                    instanceof JScrollPane)) {

                guardarOriginales(
                        (Container) componente,
                        datos
                );
            }
        }
    }

    // =========================================================
    // ESCALAR CONTENEDOR
    // =========================================================
    private static void escalarContenedor(
            Container contenedor,
            DatosVentana datos,
            double escalaX,
            double escalaY) {

        for (Component componente
                : contenedor.getComponents()) {

            escalarComponente(
                    componente,
                    datos,
                    escalaX,
                    escalaY
            );

            if (componente instanceof Container
                    && !(componente
                    instanceof JScrollPane)) {

                escalarContenedor(
                        (Container) componente,
                        datos,
                        escalaX,
                        escalaY
                );
            }
        }
    }

    // =========================================================
    // ESCALAR COMPONENTE
    // =========================================================
    private static void escalarComponente(
            Component componente,
            DatosVentana datos,
            double escalaX,
            double escalaY) {

        Rectangle original
                = datos.bounds.get(componente);

        if (original == null) {
            return;
        }

        // ==========================================
        // POSICIÓN
        // ==========================================

        int nuevaX
                = (int) Math.round(
                        original.x * escalaX
                );

        int nuevaY
                = (int) Math.round(
                        original.y * escalaY
                );

        // ==========================================
        // TAMAÑO
        // ==========================================

        int nuevoAncho
                = (int) Math.round(
                        original.width * escalaX
                );

        int nuevoAlto
                = (int) Math.round(
                        original.height * escalaY
                );

        componente.setBounds(
                nuevaX,
                nuevaY,
                nuevoAncho,
                nuevoAlto
        );

        // ==========================================
        // FUENTE
        // ==========================================

        Font fuenteOriginal
                = datos.fuentes.get(componente);

        if (fuenteOriginal != null) {

            double escalaFuente
                    = Math.min(
                            escalaX,
                            escalaY
                    );

            float nuevoTamano
                    = (float) (
                            fuenteOriginal.getSize2D()
                            * escalaFuente
                    );

            nuevoTamano
                    = Math.max(
                            nuevoTamano,
                            8f
                    );

            componente.setFont(
                    fuenteOriginal.deriveFont(
                            nuevoTamano
                    )
            );
        }

        // ==========================================
        // JTABLE
        // ==========================================

        if (componente instanceof JTable) {

            JTable tabla
                    = (JTable) componente;

            Integer alturaOriginal
                    = datos.alturaFilas.get(tabla);

            if (alturaOriginal != null) {

                int nuevaAltura
                        = (int) Math.round(
                                alturaOriginal
                                * escalaY
                        );

                tabla.setRowHeight(
                        Math.max(
                                nuevaAltura,
                                1
                        )
                );
            }
        }

        // ==========================================
        // IMÁGENES DE JLABEL
        // ==========================================

        if (componente instanceof JLabel
                && !(componente
                instanceof Labels.LabelEscalable)) {

            JLabel label
                    = (JLabel) componente;

            ImageIcon originalIcono
                    = datos.iconos.get(componente);

            if (originalIcono != null
                    && nuevoAncho > 0
                    && nuevoAlto > 0) {

                label.setIcon(
                        new ImageIcon(
                                originalIcono
                                        .getImage()
                                        .getScaledInstance(
                                                nuevoAncho,
                                                nuevoAlto,
                                                java.awt.Image.SCALE_SMOOTH
                                        )
                        )
                );
            }
        }

        // ==========================================
        // ICONOS DE BOTONES
        // ==========================================

        if (componente instanceof AbstractButton) {

            AbstractButton boton
                    = (AbstractButton) componente;

            boolean noEscalarIcono
                    = Boolean.TRUE.equals(
                            boton.getClientProperty(
                                    "noEscalarIcono"
                            )
                    );

            ImageIcon iconoOriginal
                    = datos.iconos.get(componente);

            if (!noEscalarIcono
                    && iconoOriginal != null
                    && nuevoAncho > 0
                    && nuevoAlto > 0) {

                int anchoIcono
                        = Math.max(
                                nuevoAncho - 10,
                                1
                        );

                int altoIcono
                        = Math.max(
                                nuevoAlto - 10,
                                1
                        );

                boton.setIcon(
                        new ImageIcon(
                                iconoOriginal
                                        .getImage()
                                        .getScaledInstance(
                                                anchoIcono,
                                                altoIcono,
                                                java.awt.Image.SCALE_SMOOTH
                                        )
                        )
                );
            }
        }
    }

    // =========================================================
    // CLASE INTERNA PARA GUARDAR DATOS ORIGINALES
    // =========================================================
    private static class DatosVentana {

        private final Map<Component, Rectangle> bounds
                = new IdentityHashMap<>();

        private final Map<Component, Font> fuentes
                = new IdentityHashMap<>();

        private final Map<Component, ImageIcon> iconos
                = new IdentityHashMap<>();

        private final Map<JTable, Integer> alturaFilas
                = new IdentityHashMap<>();
    }
}
