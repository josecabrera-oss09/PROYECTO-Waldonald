package Componentes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

public class EscaladorPanel {

    private final Container panel;

    private final int anchoBase;
    private final int altoBase;

    private final Map<Component, Rectangle> posicionesOriginales
            = new HashMap<>();

    private final Map<Component, Float> fuentesOriginales
            = new HashMap<>();


    public EscaladorPanel(
            Container panel,
            int anchoBase,
            int altoBase) {

        this.panel = panel;

        this.anchoBase = anchoBase;
        this.altoBase = altoBase;


        // Esperar a que NetBeans termine de crear
        // todos los componentes
        SwingUtilities.invokeLater(() -> {

            guardarComponentes(panel);

            escalar();

        });


        // Cada vez que el panel cambie de tamaño
        panel.addComponentListener(
                new ComponentAdapter() {

            @Override
            public void componentResized(
                    ComponentEvent e) {

                SwingUtilities.invokeLater(() -> {
                    escalar();
                });
            }
        });
    }


    // =====================================================
    // GUARDAR POSICIONES ORIGINALES
    // =====================================================

    private void guardarComponentes(
            Container contenedor) {

        for (Component componente
                : contenedor.getComponents()) {

            posicionesOriginales.put(
                    componente,
                    new Rectangle(
                            componente.getBounds()
                    )
            );


            if (componente.getFont() != null) {

                fuentesOriginales.put(
                        componente,
                        componente.getFont()
                                .getSize2D()
                );
            }


            if (componente instanceof Container) {

                guardarComponentes(
                        (Container) componente
                );
            }
        }
    }


    // =====================================================
    // ESCALAR
    // =====================================================

    public void escalar() {

        if (posicionesOriginales.isEmpty()) {
            return;
        }


        int anchoActual = panel.getWidth();
        int altoActual = panel.getHeight();


        if (anchoActual <= 0 || altoActual <= 0) {
            return;
        }


        double escalaX =
                (double) anchoActual
                        / anchoBase;

        double escalaY =
                (double) altoActual
                        / altoBase;


        escalarComponentes(
                panel,
                escalaX,
                escalaY
        );


        panel.revalidate();
        panel.repaint();
    }


    // =====================================================
    // COMPONENTES
    // =====================================================

    private void escalarComponentes(
            Container contenedor,
            double escalaX,
            double escalaY) {

        for (Component componente
                : contenedor.getComponents()) {


            Rectangle original =
                    posicionesOriginales.get(
                            componente
                    );


            if (original != null) {

                int nuevoX =
                        (int) Math.round(
                                original.x
                                        * escalaX
                        );

                int nuevoY =
                        (int) Math.round(
                                original.y
                                        * escalaY
                        );


                int nuevoAncho =
                        (int) Math.round(
                                original.width
                                        * escalaX
                        );

                int nuevoAlto =
                        (int) Math.round(
                                original.height
                                        * escalaY
                        );


                componente.setBounds(
                        nuevoX,
                        nuevoY,
                        nuevoAncho,
                        nuevoAlto
                );
            }


            // =================================================
            // FUENTE
            // =================================================

            Float tamañoOriginal =
                    fuentesOriginales.get(
                            componente
                    );


            if (tamañoOriginal != null
                    && componente.getFont() != null) {


                double escalaFuente =
                        Math.min(
                                escalaX,
                                escalaY
                        );


                // No dejar que las fuentes
                // crezcan exageradamente
                escalaFuente =
                        Math.min(
                                escalaFuente,
                                1.20
                        );


                float nuevoTamaño =
                        (float) (
                                tamañoOriginal
                                        * escalaFuente
                        );


                nuevoTamaño =
                        Math.max(
                                9f,
                                nuevoTamaño
                        );


                componente.setFont(
                        componente
                                .getFont()
                                .deriveFont(
                                        nuevoTamaño
                                )
                );
            }


            if (componente instanceof Container) {

                escalarComponentes(
                        (Container) componente,
                        escalaX,
                        escalaY
                );
            }
        }
    }
}

