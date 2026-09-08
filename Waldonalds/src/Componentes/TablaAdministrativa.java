package Componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.beans.BeanProperty;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * JTable reutilizable para los módulos administrativos.
 *
 * Sus propiedades de apariencia aparecen en Properties de NetBeans. Las
 * columnas especiales se habilitan por código porque dependen del tipo de
 * datos que entrega cada TableModel.
 */
@SuppressWarnings({"serial", "this-escape"})
public class TablaAdministrativa extends JTable {

    private Color colorCabecera = Color.WHITE;
    private Color colorTextoCabecera = new Color(0, 20, 43);
    private Color colorFilas = Color.WHITE;
    private Color colorFilaAlterna = new Color(250, 251, 252);
    private Color colorTexto = new Color(0, 20, 43);
    private Color colorLineas = new Color(235, 238, 242);
    private Color colorSeleccion = new Color(255, 247, 222);
    private Color colorTextoSeleccion = new Color(0, 20, 43);
    private Font fuenteCabecera = new Font("Dialog", Font.BOLD, 12);
    private int altoCabecera = 42;
    private int paddingHorizontal = 12;
    private boolean filasAlternadas;
    private String columnasCentradas = "";
    private String columnasDerecha = "";
    private Set<Integer> indicesCentrados = Set.of();
    private Set<Integer> indicesDerecha = Set.of();

    public TablaAdministrativa() {
        setFont(new Font("Dialog", Font.PLAIN, 13));
        setRowHeight(40);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 1));
        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setDefaultRenderer(Object.class, new RenderTexto());
        setDefaultRenderer(String.class, new RenderTexto());
        aplicarEstilo();
    }

    /** Reaplica al JTable los valores elegidos desde Properties. */
    public final void aplicarEstilo() {
        setBackground(colorFilas);
        setForeground(colorTexto);
        setGridColor(colorLineas);
        setSelectionBackground(colorSeleccion);
        setSelectionForeground(colorTextoSeleccion);
        if (getTableHeader() != null) {
            getTableHeader().setBackground(colorCabecera);
            getTableHeader().setForeground(colorTextoCabecera);
            getTableHeader().setFont(fuenteCabecera);
            getTableHeader().setPreferredSize(
                    new Dimension(0, altoCabecera));
            getTableHeader().setReorderingAllowed(false);
            getTableHeader().setDefaultRenderer(new RenderCabecera());
        }
        repaint();
    }

    /**
     * Configura una columna cuyo valor sea CeldaImagenTexto.
     * La ruta almacenada en la celda se resuelve dentro de carpetaBase.
     */
    public void configurarColumnaImagenTexto(
            int columna,
            Path carpetaBase,
            int anchoImagen,
            int altoImagen) {
        validarColumna(columna);
        getColumnModel().getColumn(columna).setCellRenderer(
                new RenderImagenTexto(carpetaBase, anchoImagen, altoImagen));
        setRowHeight(Math.max(getRowHeight(), altoImagen + 12));
    }

    /** Configura una columna cuyo valor sea CeldaEstrellas. */
    public void configurarColumnaEstrellas(int columna, Color colorEstrella) {
        validarColumna(columna);
        getColumnModel().getColumn(columna).setCellRenderer(
                new RenderEstrellas(colorEstrella));
    }

    /** Configura una columna cuyo valor sea CeldaPorcentaje. */
    public void configurarColumnaPorcentaje(int columna, Color colorBarra) {
        validarColumna(columna);
        getColumnModel().getColumn(columna).setCellRenderer(
                new RenderPorcentaje(colorBarra));
    }

    private void validarColumna(int columna) {
        if (columna < 0 || columna >= getColumnCount()) {
            throw new IllegalArgumentException(
                    "No existe la columna " + columna + ".");
        }
    }

    private Color fondoCelda(boolean seleccionada, int fila) {
        if (seleccionada) {
            return colorSeleccion;
        }
        if (filasAlternadas && fila % 2 != 0) {
            return colorFilaAlterna;
        }
        return colorFilas;
    }

    private int alineacion(int columna) {
        if (indicesCentrados.contains(columna)) {
            return SwingConstants.CENTER;
        }
        if (indicesDerecha.contains(columna)) {
            return SwingConstants.RIGHT;
        }
        return SwingConstants.LEFT;
    }

    private Set<Integer> convertirIndices(String texto) {
        Set<Integer> resultado = new HashSet<>();
        if (texto == null || texto.isBlank()) {
            return resultado;
        }
        for (String parte : texto.split("[,; ]+")) {
            try {
                resultado.add(Integer.valueOf(parte.trim()));
            } catch (NumberFormatException ignorada) {
                // Properties puede quedar temporalmente incompleto al editar.
            }
        }
        return resultado;
    }

    private final class RenderCabecera extends DefaultTableCellRenderer {

        private RenderCabecera() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    tabla, valor, seleccionada, foco, fila, columna);
            etiqueta.setBackground(colorCabecera);
            etiqueta.setForeground(colorTextoCabecera);
            etiqueta.setFont(fuenteCabecera);
            etiqueta.setHorizontalAlignment(alineacion(columna));
            etiqueta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 1, 0, colorLineas),
                    new EmptyBorder(0, paddingHorizontal, 0,
                            paddingHorizontal)));
            return etiqueta;
        }
    }

    private final class RenderTexto extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    tabla, valor, seleccionada, foco, fila, columna);
            etiqueta.setBackground(fondoCelda(seleccionada, fila));
            etiqueta.setForeground(seleccionada
                    ? colorTextoSeleccion : colorTexto);
            etiqueta.setFont(tabla.getFont());
            etiqueta.setHorizontalAlignment(alineacion(columna));
            etiqueta.setBorder(new EmptyBorder(
                    0, paddingHorizontal, 0, paddingHorizontal));
            return etiqueta;
        }
    }

    private final class RenderImagenTexto extends JPanel
            implements TableCellRenderer {

        private final JLabel imagen = new JLabel();
        private final JLabel titulo = new JLabel();
        private final JLabel detalle = new JLabel();
        private final Path carpetaBase;
        private final int anchoImagen;
        private final int altoImagen;
        private final Map<String, Icon> cacheImagenes = new HashMap<>();

        private RenderImagenTexto(
                Path carpetaBase,
                int anchoImagen,
                int altoImagen) {
            this.carpetaBase = carpetaBase.toAbsolutePath().normalize();
            this.anchoImagen = Math.max(1, anchoImagen);
            this.altoImagen = Math.max(1, altoImagen);

            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(5, paddingHorizontal, 5,
                    paddingHorizontal));
            imagen.setHorizontalAlignment(SwingConstants.CENTER);
            imagen.setPreferredSize(new Dimension(
                    this.anchoImagen, this.altoImagen));

            JPanel textos = new JPanel(new GridLayout(2, 1, 0, 1));
            textos.setOpaque(false);
            titulo.setFont(getFont().deriveFont(Font.BOLD));
            detalle.setFont(getFont().deriveFont(
                    Math.max(10f, getFont().getSize2D() - 2f)));
            detalle.setForeground(new Color(92, 103, 124));
            textos.add(titulo);
            textos.add(detalle);
            add(imagen, BorderLayout.WEST);
            add(textos, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {
            CeldaImagenTexto celda = valor instanceof CeldaImagenTexto dato
                    ? dato : new CeldaImagenTexto("", "", null);
            titulo.setText(celda.titulo());
            detalle.setText(celda.detalle());
            imagen.setIcon(cargarImagen(celda.rutaImagen()));
            setBackground(fondoCelda(seleccionada, fila));
            titulo.setForeground(seleccionada
                    ? colorTextoSeleccion : colorTexto);
            return this;
        }

        private Icon cargarImagen(String rutaGuardada) {
            if (rutaGuardada == null || rutaGuardada.isBlank()) {
                return null;
            }
            return cacheImagenes.computeIfAbsent(
                    rutaGuardada, this::leerImagen);
        }

        private Icon leerImagen(String rutaGuardada) {
            Path archivo = carpetaBase.resolve(rutaGuardada).normalize();
            if (!archivo.startsWith(carpetaBase)
                    || !Files.isRegularFile(archivo)) {
                return null;
            }
            try {
                Image original = ImageIO.read(archivo.toFile());
                if (original == null) {
                    return null;
                }
                Image escalada = original.getScaledInstance(
                        anchoImagen, altoImagen, Image.SCALE_SMOOTH);
                return new ImageIcon(escalada);
            } catch (IOException ex) {
                return null;
            }
        }
    }

    private final class RenderEstrellas extends JLabel
            implements TableCellRenderer {

        private final String colorHtml;

        private RenderEstrellas(Color color) {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            colorHtml = String.format("#%02x%02x%02x",
                    color.getRed(), color.getGreen(), color.getBlue());
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {
            CeldaEstrellas celda = valor instanceof CeldaEstrellas dato
                    ? dato : new CeldaEstrellas(0, 5);
            int maximo = Math.max(1, celda.maximo());
            int llenas = Math.max(0, Math.min(
                    maximo, (int) Math.round(celda.valor())));
            String texto = "★".repeat(llenas);
            String vacias = "☆".repeat(maximo - llenas);
            setText("<html><span style=''color:" + colorHtml + "''>"
                    + texto + "</span><span style=''color:#c7ccd4''>"
                    + vacias + "</span></html>");
            setToolTipText(String.format("%.1f de %d",
                    celda.valor(), maximo));
            setBackground(fondoCelda(seleccionada, fila));
            return this;
        }
    }

    private final class RenderPorcentaje extends JPanel
            implements TableCellRenderer {

        private final JProgressBar barra = new JProgressBar(0, 100);

        private RenderPorcentaje(Color color) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 9));
            barra.setPreferredSize(new Dimension(105, 18));
            barra.setForeground(color);
            barra.setStringPainted(true);
            add(barra);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {
            double valorNumerico = valor instanceof CeldaPorcentaje dato
                    ? dato.valor() : 0;
            int porcentaje = (int) Math.round(Math.max(
                    0, Math.min(100, valorNumerico)));
            barra.setValue(porcentaje);
            barra.setString(porcentaje + "%");
            setBackground(fondoCelda(seleccionada, fila));
            return this;
        }
    }

    public record CeldaImagenTexto(
            String titulo,
            String detalle,
            String rutaImagen) {
    }

    public record CeldaEstrellas(double valor, int maximo) {
    }

    public record CeldaPorcentaje(double valor) {
    }

    public Color getColorCabecera() {
        return colorCabecera;
    }

    @BeanProperty(description = "Color de fondo de la cabecera")
    public void setColorCabecera(Color colorCabecera) {
        this.colorCabecera = colorCabecera;
        aplicarEstilo();
    }

    public Color getColorTextoCabecera() {
        return colorTextoCabecera;
    }

    @BeanProperty(description = "Color del texto de la cabecera")
    public void setColorTextoCabecera(Color colorTextoCabecera) {
        this.colorTextoCabecera = colorTextoCabecera;
        aplicarEstilo();
    }

    public Color getColorFilas() {
        return colorFilas;
    }

    @BeanProperty(description = "Color principal de las filas")
    public void setColorFilas(Color colorFilas) {
        this.colorFilas = colorFilas;
        aplicarEstilo();
    }

    public Color getColorFilaAlterna() {
        return colorFilaAlterna;
    }

    @BeanProperty(description = "Color de las filas alternas")
    public void setColorFilaAlterna(Color colorFilaAlterna) {
        this.colorFilaAlterna = colorFilaAlterna;
        repaint();
    }

    public Color getColorTexto() {
        return colorTexto;
    }

    @BeanProperty(description = "Color del texto de las filas")
    public void setColorTexto(Color colorTexto) {
        this.colorTexto = colorTexto;
        aplicarEstilo();
    }

    public Color getColorLineas() {
        return colorLineas;
    }

    @BeanProperty(description = "Color de las líneas divisorias")
    public void setColorLineas(Color colorLineas) {
        this.colorLineas = colorLineas;
        aplicarEstilo();
    }

    public Color getColorSeleccion() {
        return colorSeleccion;
    }

    @BeanProperty(description = "Color de fondo de una fila seleccionada")
    public void setColorSeleccion(Color colorSeleccion) {
        this.colorSeleccion = colorSeleccion;
        aplicarEstilo();
    }

    public Color getColorTextoSeleccion() {
        return colorTextoSeleccion;
    }

    @BeanProperty(description = "Color del texto seleccionado")
    public void setColorTextoSeleccion(Color colorTextoSeleccion) {
        this.colorTextoSeleccion = colorTextoSeleccion;
        aplicarEstilo();
    }

    public Font getFuenteCabecera() {
        return fuenteCabecera;
    }

    @BeanProperty(description = "Fuente de los títulos de columna")
    public void setFuenteCabecera(Font fuenteCabecera) {
        this.fuenteCabecera = fuenteCabecera;
        aplicarEstilo();
    }

    public int getAltoCabecera() {
        return altoCabecera;
    }

    @BeanProperty(description = "Altura de la cabecera en píxeles")
    public void setAltoCabecera(int altoCabecera) {
        this.altoCabecera = Math.max(1, altoCabecera);
        aplicarEstilo();
    }

    public int getPaddingHorizontal() {
        return paddingHorizontal;
    }

    @BeanProperty(description = "Espacio izquierdo y derecho del texto")
    public void setPaddingHorizontal(int paddingHorizontal) {
        this.paddingHorizontal = Math.max(0, paddingHorizontal);
        repaint();
    }

    public boolean isFilasAlternadas() {
        return filasAlternadas;
    }

    @BeanProperty(description = "Alterna el color de las filas")
    public void setFilasAlternadas(boolean filasAlternadas) {
        this.filasAlternadas = filasAlternadas;
        repaint();
    }

    public String getColumnasCentradas() {
        return columnasCentradas;
    }

    @BeanProperty(description = "Índices centrados separados por coma: 0,5,7")
    public void setColumnasCentradas(String columnasCentradas) {
        this.columnasCentradas = columnasCentradas == null
                ? "" : columnasCentradas;
        indicesCentrados = convertirIndices(this.columnasCentradas);
        repaint();
    }

    public String getColumnasDerecha() {
        return columnasDerecha;
    }

    @BeanProperty(description = "Índices a la derecha separados por coma")
    public void setColumnasDerecha(String columnasDerecha) {
        this.columnasDerecha = columnasDerecha == null
                ? "" : columnasDerecha;
        indicesDerecha = convertirIndices(this.columnasDerecha);
        repaint();
    }
}
