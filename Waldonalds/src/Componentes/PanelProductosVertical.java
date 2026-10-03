package Componentes;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/** Panel de tarjetas que se ajusta al ancho y solo crece verticalmente. */
public class PanelProductosVertical extends JPanel implements Scrollable {

    /** Máximo de tarjetas por fila para conservar un layout predecible. */
    private static final int MAX_COLUMNAS = 6;
    // Evita que entren siete tarjetas en la vista de 1810 px.
    private static final int ESPACIO_HORIZONTAL = 40;
    private static final int ESPACIO_VERTICAL = 15;

    public PanelProductosVertical() {
        super(new FlowLayout(FlowLayout.LEFT, ESPACIO_HORIZONTAL, ESPACIO_VERTICAL));
        setOpaque(false);
    }

    /**
     * FlowLayout calcula por defecto una sola fila cuando el panel todavía
     * no tiene un ancho definitivo. Eso hace que el JScrollPane crea que no
     * existe contenido vertical. Calculamos aquí la altura real del panel
     * según el ancho visible y la cantidad de tarjetas.
     */
    @Override
    public Dimension getPreferredSize() {
        if (!(getLayout() instanceof FlowLayout) || getComponentCount() == 0) {
            return super.getPreferredSize();
        }

        int anchoDisponible = getWidth();
        if (anchoDisponible <= 0 && getParent() != null) {
            anchoDisponible = getParent().getWidth();
        }
        if (anchoDisponible <= 0) {
            anchoDisponible = 1_810;
        }

        int anchoTarjeta = 230;
        if (getComponentCount() > 0
                && getComponent(0).getPreferredSize().width > 0) {
            anchoTarjeta = getComponent(0).getPreferredSize().width;
        }

        int columnas = Math.max(1, Math.min(MAX_COLUMNAS,
                (anchoDisponible + ESPACIO_HORIZONTAL)
                        / (anchoTarjeta + ESPACIO_HORIZONTAL)));
        int filas = (getComponentCount() + columnas - 1) / columnas;
        int altoTarjeta = getComponent(0).getPreferredSize().height;

        return new Dimension(anchoDisponible,
                ESPACIO_VERTICAL + filas * (altoTarjeta + ESPACIO_VERTICAL));
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? 32 : 0;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? Math.max(visibleRect.height - 32, 32) : 0;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() { return true; }

    @Override
    public boolean getScrollableTracksViewportHeight() { return false; }
}
