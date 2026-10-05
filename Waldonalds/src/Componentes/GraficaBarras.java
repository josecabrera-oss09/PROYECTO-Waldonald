package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.beans.BeanProperty;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Gráfica de barras reutilizable para los paneles administrativos.
 * No depende de librerías externas: recibe una lista de datos y se dibuja
 * con Java2D respetando el tamaño disponible del componente.
 */
@SuppressWarnings("serial")
public class GraficaBarras extends JPanel {

    public enum Orientacion {
        HORIZONTAL,
        VERTICAL
    }

    public record Dato(
            String etiqueta,
            double valor,
            String detalle,
            String extra) {

        public Dato(String etiqueta, double valor, String detalle) {
            this(etiqueta, valor, detalle, "");
        }

        public Dato {
            etiqueta = etiqueta == null ? "" : etiqueta;
            detalle = detalle == null ? "" : detalle;
            extra = extra == null ? "" : extra;
            valor = Math.max(0, valor);
        }
    }

    private static final Color TEXTO_PREDETERMINADO =
            new Color(0, 20, 43);
    private static final Color SECUNDARIO_PREDETERMINADO =
            new Color(92, 103, 124);
    private static final Color CUADRICULA_PREDETERMINADA =
            new Color(229, 233, 239);

    private List<Dato> datos = List.of();
    private Orientacion orientacion = Orientacion.HORIZONTAL;
    private Color colorBarra = new Color(255, 188, 0);
    private Color colorTexto = TEXTO_PREDETERMINADO;
    private Color colorSecundario = SECUNDARIO_PREDETERMINADO;
    private Color colorCuadricula = CUADRICULA_PREDETERMINADA;
    private String textoSinDatos = "Sin ventas para la fecha seleccionada";
    private String tituloCategoria = "Cajero";
    private String tituloValor = "Ventas totales";
    private String tituloExtra = "Pedidos";

    public GraficaBarras() {
        setOpaque(false);
    }

    public void setDatos(List<Dato> nuevosDatos) {
        datos = nuevosDatos == null
                ? List.of() : List.copyOf(new ArrayList<>(nuevosDatos));
        revalidate();
        repaint();
    }

    public List<Dato> getDatos() {
        return datos;
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        Graphics2D g = (Graphics2D) grafico.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (datos.isEmpty()) {
                dibujarSinDatos(g);
            } else if (orientacion == Orientacion.VERTICAL) {
                dibujarVertical(g);
            } else {
                dibujarHorizontal(g);
            }
        } finally {
            g.dispose();
        }
    }

    private void dibujarSinDatos(Graphics2D g) {
        g.setFont(getFont().deriveFont(12f));
        g.setColor(colorSecundario);
        FontMetrics fm = g.getFontMetrics();
        int x = Math.max(8, (getWidth() - fm.stringWidth(textoSinDatos)) / 2);
        int y = Math.max(fm.getAscent() + 8,
                (getHeight() + fm.getAscent()) / 2);
        g.drawString(textoSinDatos, x, y);
    }

    private void dibujarHorizontal(Graphics2D g) {
        int margen = 8;
        int yCabecera = 16;
        int ySeparador = 29;
        int inicioFilas = 34;
        int anchoDisponible = Math.max(240, getWidth() - margen * 2);
        int anchoCajero = Math.max(105,
                (int) Math.round(anchoDisponible * 0.30));
        int anchoPedidos = Math.max(54,
                (int) Math.round(anchoDisponible * 0.14));
        int xVentas = margen + anchoCajero;
        int anchoVentas = anchoDisponible - anchoCajero - anchoPedidos;
        int xPedidos = xVentas + anchoVentas;
        int anchoBarra = Math.max(45,
                (int) Math.round(anchoVentas * 0.48));
        int xImporte = xVentas + anchoBarra + 10;
        int altoFila = Math.min(36, Math.max(28,
                (getHeight() - inicioFilas) / Math.max(1, datos.size())));
        double maximo = maximo();

        g.setFont(getFont().deriveFont(java.awt.Font.BOLD, 11f));
        g.setColor(colorTexto);
        g.drawString(tituloCategoria, margen, yCabecera);
        g.drawString(tituloValor, xVentas, yCabecera);
        g.drawString(tituloExtra, xPedidos, yCabecera);
        g.setColor(colorCuadricula);
        g.drawLine(margen, ySeparador,
                getWidth() - margen, ySeparador);

        for (int i = 0; i < datos.size(); i++) {
            Dato dato = datos.get(i);
            int filaY = inicioFilas + i * altoFila;
            int centroY = filaY + altoFila / 2;
            int altoBarra = Math.min(16, Math.max(9, altoFila - 14));
            int y = centroY - altoBarra / 2;

            g.setFont(getFont().deriveFont(11f));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(colorTexto);
            g.drawString(recortar(
                    dato.etiqueta(), fm, anchoCajero - 10),
                    margen, centroY + fm.getAscent() / 2 - 2);

            g.setColor(colorCuadricula);
            g.fill(new RoundRectangle2D.Double(
                    xVentas, y, anchoBarra, altoBarra,
                    altoBarra, altoBarra));
            int anchoValor = dato.valor() <= 0 ? 0
                    : Math.max(4, (int) Math.round(
                            anchoBarra * dato.valor() / maximo));
            g.setColor(colorBarra);
            g.fill(new RoundRectangle2D.Double(
                    xVentas, y, anchoValor, altoBarra,
                    altoBarra, altoBarra));

            g.setColor(colorTexto);
            g.setFont(getFont().deriveFont(java.awt.Font.BOLD, 11f));
            g.drawString(dato.detalle(), xImporte,
                    centroY + g.getFontMetrics().getAscent() / 2 - 2);
            g.drawString(dato.extra(), xPedidos,
                    centroY + g.getFontMetrics().getAscent() / 2 - 2);

            g.setColor(colorCuadricula);
            g.drawLine(margen, filaY + altoFila - 1,
                    getWidth() - margen, filaY + altoFila - 1);
        }
    }

    private void dibujarVertical(Graphics2D g) {
        int izquierda = 45;
        int derecha = 8;
        int arriba = 24;
        int abajo = 32;
        int ancho = Math.max(20, getWidth() - izquierda - derecha);
        int alto = Math.max(20, getHeight() - arriba - abajo);
        double maximo = maximo();

        g.setFont(getFont().deriveFont(10f));
        FontMetrics fm = g.getFontMetrics();
        g.setStroke(new BasicStroke(1f));
        for (int i = 0; i <= 4; i++) {
            int y = arriba + (alto * i / 4);
            g.setColor(colorCuadricula);
            g.drawLine(izquierda, y, izquierda + ancho, y);
            double valor = maximo * (4 - i) / 4.0;
            String texto = abreviar(valor);
            g.setColor(colorSecundario);
            g.drawString(texto,
                    izquierda - fm.stringWidth(texto) - 6,
                    y + fm.getAscent() / 2 - 2);
        }

        double espacio = ancho / (double) Math.max(1, datos.size());
        int anchoBarra = Math.max(5,
                (int) Math.min(34, espacio * 0.58));
        for (int i = 0; i < datos.size(); i++) {
            Dato dato = datos.get(i);
            int x = izquierda + (int) Math.round(i * espacio
                    + (espacio - anchoBarra) / 2);
            int altoBarra = dato.valor() <= 0 ? 0
                    : Math.max(3, (int) Math.round(
                            alto * dato.valor() / maximo));
            int y = arriba + alto - altoBarra;

            g.setColor(colorBarra);
            g.fill(new RoundRectangle2D.Double(
                    x, y, anchoBarra, altoBarra, 7, 7));

            g.setFont(getFont().deriveFont(9f));
            g.setColor(colorTexto);
            String detalle = dato.detalle();
            int detalleX = x + (anchoBarra
                    - g.getFontMetrics().stringWidth(detalle)) / 2;
            if (altoBarra > 0) {
                g.drawString(detalle, detalleX, Math.max(10, y - 4));
            }

            g.setFont(getFont().deriveFont(10f));
            String etiqueta = dato.etiqueta();
            int etiquetaX = x + (anchoBarra
                    - g.getFontMetrics().stringWidth(etiqueta)) / 2;
            g.setColor(colorSecundario);
            g.drawString(etiqueta, etiquetaX,
                    arriba + alto + fm.getAscent() + 8);
        }
    }

    private double maximo() {
        double maximo = datos.stream().mapToDouble(Dato::valor)
                .max().orElse(1);
        return maximo <= 0 ? 1 : maximo;
    }

    private String abreviar(double valor) {
        if (valor >= 1_000_000) {
            return String.format("Q%.1fM", valor / 1_000_000);
        }
        if (valor >= 1_000) {
            return String.format("Q%.1fk", valor / 1_000);
        }
        return "Q" + Math.round(valor);
    }

    private String recortar(String texto, FontMetrics fm, int ancho) {
        if (fm.stringWidth(texto) <= ancho) return texto;
        String puntos = "…";
        int limite = Math.max(0, ancho - fm.stringWidth(puntos));
        int fin = texto.length();
        while (fin > 0 && fm.stringWidth(texto.substring(0, fin)) > limite) {
            fin--;
        }
        return texto.substring(0, fin) + puntos;
    }

    public Orientacion getOrientacion() {
        return orientacion;
    }

    @BeanProperty(description = "Dirección de las barras")
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion == null
                ? Orientacion.HORIZONTAL : orientacion;
        repaint();
    }

    public Color getColorBarra() {
        return colorBarra;
    }

    @BeanProperty(description = "Color principal de las barras")
    public void setColorBarra(Color colorBarra) {
        this.colorBarra = colorBarra == null
                ? new Color(255, 188, 0) : colorBarra;
        repaint();
    }

    public Color getColorTexto() {
        return colorTexto;
    }

    public void setColorTexto(Color colorTexto) {
        this.colorTexto = colorTexto == null
                ? TEXTO_PREDETERMINADO : colorTexto;
        repaint();
    }

    public Color getColorSecundario() {
        return colorSecundario;
    }

    public void setColorSecundario(Color colorSecundario) {
        this.colorSecundario = colorSecundario == null
                ? SECUNDARIO_PREDETERMINADO : colorSecundario;
        repaint();
    }

    public Color getColorCuadricula() {
        return colorCuadricula;
    }

    public void setColorCuadricula(Color colorCuadricula) {
        this.colorCuadricula = colorCuadricula == null
                ? CUADRICULA_PREDETERMINADA : colorCuadricula;
        repaint();
    }

    public String getTextoSinDatos() {
        return textoSinDatos;
    }

    public void setTextoSinDatos(String textoSinDatos) {
        this.textoSinDatos = textoSinDatos == null
                ? "Sin datos" : textoSinDatos;
        repaint();
    }

    public String getTituloCategoria() {
        return tituloCategoria;
    }

    public void setTituloCategoria(String tituloCategoria) {
        this.tituloCategoria = tituloCategoria == null ? "" : tituloCategoria;
        repaint();
    }

    public String getTituloValor() {
        return tituloValor;
    }

    public void setTituloValor(String tituloValor) {
        this.tituloValor = tituloValor == null ? "" : tituloValor;
        repaint();
    }

    public String getTituloExtra() {
        return tituloExtra;
    }

    public void setTituloExtra(String tituloExtra) {
        this.tituloExtra = tituloExtra == null ? "" : tituloExtra;
        repaint();
    }
}
