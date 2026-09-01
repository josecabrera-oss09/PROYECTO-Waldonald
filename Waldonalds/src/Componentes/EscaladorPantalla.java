package Componentes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

public class EscaladorPantalla {

    private final int anchoBase;
    private final int altoBase;

    private final Map<Component, Rectangle> posicionesOriginales
            = new HashMap<>();

    private final Map<Component, Float> fuentesOriginales
            = new HashMap<>();

    public EscaladorPantalla(
            Container contenedor,
            int anchoBase,
            int altoBase) {

        this.anchoBase = anchoBase;
        this.altoBase = altoBase;

        guardarComponentes(contenedor);
    }

    private void guardarComponentes(Container contenedor) {

        for (Component componente : contenedor.getComponents()) {

            posicionesOriginales.put(
                    componente,
                    componente.getBounds()
            );

            if (componente.getFont() != null) {

                fuentesOriginales.put(
                        componente,
                        componente.getFont().getSize2D()
                );
            }

            if (componente instanceof Container) {

                guardarComponentes((Container) componente);
            }
        }
    }

    public void escalar(Container contenedor) {

        Dimension pantalla
                = Toolkit.getDefaultToolkit().getScreenSize();

        double escalaX
                = pantalla.getWidth() / anchoBase;

        double escalaY
                = pantalla.getHeight() / altoBase;

        escalarComponentes(
                contenedor,
                escalaX,
                escalaY
        );
    }

    private void escalarComponentes(
            Container contenedor,
            double escalaX,
            double escalaY) {

        for (Component componente : contenedor.getComponents()) {

            Rectangle original
                    = posicionesOriginales.get(componente);

            if (original != null) {

                int x = (int) (original.x * escalaX);
                int y = (int) (original.y * escalaY);

                int ancho
                        = (int) (original.width * escalaX);

                int alto
                        = (int) (original.height * escalaY);

                componente.setBounds(
                        x,
                        y,
                        ancho,
                        alto
                );
            }

            Float fuenteOriginal
                    = fuentesOriginales.get(componente);

            if (fuenteOriginal != null) {

                float nuevaFuente
                        = (float) (
                                fuenteOriginal
                                * Math.min(escalaX, escalaY)
                        );

                componente.setFont(
                        componente.getFont()
                                .deriveFont(nuevaFuente)
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

