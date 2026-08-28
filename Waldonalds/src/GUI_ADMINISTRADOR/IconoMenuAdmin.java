package GUI_ADMINISTRADOR;

import Utilidades.IconosAdmin;
import Utilidades.TemaAdmin;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** Icono vectorial del menú utilizado por el JFrame Form administrativo. */
@SuppressWarnings({"serial", "this-escape"})
public final class IconoMenuAdmin extends JComponent {

    private final TemaAdmin tema;

    public IconoMenuAdmin() {
        tema = new TemaAdmin();
        setOpaque(false);
        setPreferredSize(new Dimension(180, 180));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        TemaAdmin.aplicarCalidad(g2);
        IconosAdmin.pintarTicket(g2, getWidth() / 2, getHeight() / 2, tema);
        g2.dispose();
    }
}
