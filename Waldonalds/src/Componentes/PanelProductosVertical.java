package Componentes;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/** Panel de tarjetas que se ajusta al ancho y solo crece verticalmente. */
public class PanelProductosVertical extends JPanel implements Scrollable {

    public PanelProductosVertical() {
        super(new FlowLayout(FlowLayout.LEFT, 15, 15));
        setOpaque(false);
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
