package Utilidades;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.Icon;

/** Iconos vectoriales del módulo; no dependen de imágenes externas. */
public final class IconosUsuarios {

    public enum Tipo {
        BUSCAR, EDITAR, ELIMINAR, FILTRO, EXPORTAR, AGREGAR,
        ANTERIOR, SIGUIENTE, USUARIOS, ADMINISTRADOR, CAJERO,
        INACTIVO, OJO
    }

    private IconosUsuarios() {
    }

    public static Icon crear(Tipo tipo, Color color, int tamano) {
        return new IconoLinea(tipo, color, Math.max(12, tamano));
    }

    private static final class IconoLinea implements Icon {

        private final Tipo tipo;
        private final Color color;
        private final int tamano;

        private IconoLinea(Tipo tipo, Color color, int tamano) {
            this.tipo = tipo;
            this.color = color;
            this.tamano = tamano;
        }

        @Override
        public int getIconWidth() {
            return tamano;
        }

        @Override
        public int getIconHeight() {
            return tamano;
        }

        @Override
        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.scale(tamano / 24.0, tamano / 24.0);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            switch (tipo) {
                case BUSCAR -> pintarBuscar(g2);
                case EDITAR -> pintarEditar(g2);
                case ELIMINAR -> pintarEliminar(g2);
                case FILTRO -> pintarFiltro(g2);
                case EXPORTAR -> pintarExportar(g2);
                case AGREGAR -> pintarAgregar(g2);
                case ANTERIOR -> pintarFlecha(g2, false);
                case SIGUIENTE -> pintarFlecha(g2, true);
                case USUARIOS -> pintarUsuarios(g2);
                case ADMINISTRADOR -> pintarAdministrador(g2);
                case CAJERO -> pintarCajero(g2);
                case INACTIVO -> pintarInactivo(g2);
                case OJO -> pintarOjo(g2);
            }
            g2.dispose();
        }

        private void pintarBuscar(Graphics2D g2) {
            g2.drawOval(4, 4, 11, 11);
            g2.drawLine(14, 14, 20, 20);
        }

        private void pintarEditar(Graphics2D g2) {
            Path2D lapiz = new Path2D.Double();
            lapiz.moveTo(5, 18);
            lapiz.lineTo(6, 13);
            lapiz.lineTo(16, 3);
            lapiz.lineTo(21, 8);
            lapiz.lineTo(11, 18);
            lapiz.closePath();
            g2.draw(lapiz);
            g2.drawLine(14, 5, 19, 10);
            g2.drawLine(5, 18, 4, 20);
        }

        private void pintarEliminar(Graphics2D g2) {
            g2.drawRoundRect(6, 7, 12, 14, 2, 2);
            g2.drawLine(4, 6, 20, 6);
            g2.drawLine(9, 3, 15, 3);
            g2.drawLine(10, 10, 10, 17);
            g2.drawLine(14, 10, 14, 17);
        }

        private void pintarFiltro(Graphics2D g2) {
            g2.drawLine(4, 6, 20, 6);
            g2.drawLine(7, 12, 20, 12);
            g2.drawLine(10, 18, 20, 18);
            g2.drawOval(5, 10, 4, 4);
            g2.drawOval(11, 4, 4, 4);
            g2.drawOval(14, 16, 4, 4);
        }

        private void pintarExportar(Graphics2D g2) {
            g2.drawRoundRect(4, 10, 16, 10, 2, 2);
            g2.drawLine(12, 16, 12, 3);
            g2.drawLine(8, 7, 12, 3);
            g2.drawLine(16, 7, 12, 3);
        }

        private void pintarAgregar(Graphics2D g2) {
            g2.drawOval(3, 3, 18, 18);
            g2.drawLine(12, 7, 12, 17);
            g2.drawLine(7, 12, 17, 12);
        }

        private void pintarFlecha(Graphics2D g2, boolean derecha) {
            int inicio = derecha ? 8 : 16;
            int finalX = derecha ? 16 : 8;
            g2.drawLine(inicio, 5, finalX, 12);
            g2.drawLine(finalX, 12, inicio, 19);
        }

        private void pintarUsuarios(Graphics2D g2) {
            g2.drawOval(8, 3, 8, 8);
            g2.drawArc(4, 11, 16, 10, 0, 180);
            g2.drawArc(1, 8, 8, 9, 70, 120);
            g2.drawArc(15, 8, 8, 9, -10, 120);
        }

        private void pintarAdministrador(Graphics2D g2) {
            pintarUsuarios(g2);
            Path2D corona = new Path2D.Double();
            corona.moveTo(6, 5);
            corona.lineTo(8, 1);
            corona.lineTo(12, 4);
            corona.lineTo(16, 1);
            corona.lineTo(18, 5);
            g2.draw(corona);
        }

        private void pintarCajero(Graphics2D g2) {
            g2.drawOval(8, 3, 8, 8);
            g2.drawArc(4, 11, 16, 10, 0, 180);
        }

        private void pintarInactivo(Graphics2D g2) {
            pintarCajero(g2);
            g2.drawLine(16, 15, 22, 21);
            g2.drawLine(22, 15, 16, 21);
        }

        private void pintarOjo(Graphics2D g2) {
            Path2D ojo = new Path2D.Double();
            ojo.moveTo(2, 12);
            ojo.curveTo(7, 5, 17, 5, 22, 12);
            ojo.curveTo(17, 19, 7, 19, 2, 12);
            g2.draw(ojo);
            g2.drawOval(9, 9, 6, 6);
        }
    }
}
