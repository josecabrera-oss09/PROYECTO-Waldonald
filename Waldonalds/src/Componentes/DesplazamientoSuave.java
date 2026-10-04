package Componentes;

import java.awt.event.MouseWheelEvent;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;

/** Desplaza una barra con una animación corta en lugar de dar saltos bruscos. */
public final class DesplazamientoSuave {

    private static final int DURACION_MILLIS = 180;
    private static final Map<JScrollBar, Integer> objetivos = new WeakHashMap<>();
    private static final Map<JScrollBar, Timer> animaciones = new WeakHashMap<>();

    private DesplazamientoSuave() {
    }

    public static void instalarVertical(JScrollPane scroll) {
        instalar(scroll, scroll.getVerticalScrollBar(), false);
    }

    public static void instalarHorizontal(JScrollPane scroll) {
        instalar(scroll, scroll.getHorizontalScrollBar(), true);
    }

    /** Conecta la rueda al contenido interno, donde normalmente se origina. */
    public static void instalarSobreVista(JComponent vista, JScrollBar barra,
            boolean horizontal) {
        vista.addMouseWheelListener(evento -> {
            desplazar(barra, (int) Math.round(evento.getPreciseWheelRotation()
                    * (horizontal ? 45 : 32)));
            evento.consume();
        });
    }

    private static void instalar(JScrollPane scroll, JScrollBar barra, boolean horizontal) {
        scroll.addMouseWheelListener(evento -> {
            if (horizontal) {
                desplazar(barra, (int) Math.round(evento.getPreciseWheelRotation() * 45));
            } else {
                desplazar(barra, (int) Math.round(evento.getPreciseWheelRotation() * 32));
            }
            evento.consume();
        });
    }

    public static void desplazar(JScrollBar barra, int distancia) {
        if (distancia == 0) {
            return;
        }

        int minimo = barra.getMinimum();
        int maximo = Math.max(minimo, barra.getMaximum() - barra.getVisibleAmount());
        int objetivo = objetivos.getOrDefault(barra, barra.getValue()) + distancia;
        objetivos.put(barra, Math.max(minimo, Math.min(maximo, objetivo)));

        Timer anterior = animaciones.get(barra);
        if (anterior != null && anterior.isRunning()) {
            return;
        }

        Timer animacion = new Timer(15, evento -> {
            int actual = barra.getValue();
            int destino = objetivos.getOrDefault(barra, actual);
            int diferencia = destino - actual;
            if (Math.abs(diferencia) <= 1) {
                barra.setValue(destino);
                ((Timer) evento.getSource()).stop();
                animaciones.remove(barra);
                return;
            }
            barra.setValue(actual + (int) Math.round(diferencia * 0.24));
        });
        animacion.setCoalesce(true);
        animaciones.put(barra, animacion);
        animacion.start();
    }
}
