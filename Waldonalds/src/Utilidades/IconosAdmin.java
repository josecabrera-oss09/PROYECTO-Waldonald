package Utilidades;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/** Dibujo vectorial del icono de menú utilizado por InicioAdminForm. */
public final class IconosAdmin {

    private IconosAdmin() {
    }

    public static void pintarTicket(
            Graphics2D g2,
            int centroX,
            int centroY,
            TemaAdmin tema) {

        int x = centroX - tema.px(40);
        int y = centroY - tema.px(53);
        int ancho = tema.px(80);
        int alto = tema.px(112);
        int doblez = tema.px(18);

        Path2D documento = new Path2D.Double();
        documento.moveTo(x + tema.px(10), y);
        documento.lineTo(x + ancho - doblez, y);
        documento.lineTo(x + ancho, y + doblez);
        documento.lineTo(x + ancho, y + alto - tema.px(8));
        documento.quadTo(x + ancho, y + alto, x + ancho - tema.px(8), y + alto);
        documento.lineTo(x + tema.px(8), y + alto);
        documento.quadTo(x, y + alto, x, y + alto - tema.px(8));
        documento.lineTo(x, y + tema.px(10));
        documento.quadTo(x, y, x + tema.px(10), y);

        g2.setStroke(new BasicStroke(
                Math.max(3f, tema.px(5)),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        g2.setColor(TemaAdmin.AMARILLO);
        g2.draw(documento);
        g2.drawLine(x + ancho - doblez, y, x + ancho - doblez, y + doblez);
        g2.drawLine(x + ancho - doblez, y + doblez, x + ancho, y + doblez);

        int burgerY = y + tema.px(43);
        g2.drawArc(x + tema.px(18), burgerY, tema.px(44), tema.px(27), 5, 170);
        g2.drawLine(x + tema.px(17), burgerY + tema.px(17),
                x + tema.px(63), burgerY + tema.px(17));
        g2.drawArc(x + tema.px(18), burgerY + tema.px(15),
                tema.px(44), tema.px(18), 185, 170);

        g2.setStroke(new BasicStroke(
                Math.max(2f, tema.px(3)),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        g2.drawLine(x + tema.px(29), burgerY + tema.px(10),
                x + tema.px(31), burgerY + tema.px(10));
        g2.drawLine(x + tema.px(42), burgerY + tema.px(7),
                x + tema.px(44), burgerY + tema.px(7));
        g2.drawLine(x + tema.px(51), burgerY + tema.px(11),
                x + tema.px(53), burgerY + tema.px(11));

        g2.setStroke(new BasicStroke(
                Math.max(3f, tema.px(5)),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        g2.drawLine(x + tema.px(18), y + tema.px(91),
                x + tema.px(62), y + tema.px(91));
        g2.drawLine(x + tema.px(18), y + tema.px(103),
                x + tema.px(62), y + tema.px(103));
    }
}
