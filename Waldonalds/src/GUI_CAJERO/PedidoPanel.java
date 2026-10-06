package GUI_CAJERO;

import Componentes.BotonRedondeado;
import Componentes.TablaAdministrativa;
import DAO.PagoDAO;
import Modelos.*;
import Utilidades.SesionUsuario;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** Carrito de una caja; conserva la operación pendiente ante errores de conexión. */
public class PedidoPanel extends JPanel {
    private static final Color AMARILLO = new Color(255, 188, 13);
    private static final Color ROJO = new Color(196, 30, 42);
    private static final Color ROJO_HEADER = new Color(1, 20, 36);
    private static final Color TINTA = new Color(20, 27, 35);
    private static final Color FONDO = new Color(246, 246, 248);
    private static final Color BORDE_SUAVE = new Color(231, 226, 213);
    private static final String FUENTE = "Arial";
    private final CardLayout vistasPanel = new CardLayout();
    private final JPanel resumenPedido = new JPanel(new BorderLayout(8, 16));
    private ConfiguradorProductoPanel configuradorActual;
    private final Map<String, LineaPedido> lineas = new LinkedHashMap<>();
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Producto", "Cant.", "Importe"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final TablaAdministrativa tabla = new TablaAdministrativa() {
        private int filaHover = -1;

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int fila = 0; fila < getRowCount(); fila++) {
                Rectangle area = getCellRect(fila, 0, true);
                g2.setColor(getSelectionModel().isSelectedIndex(fila)
                        ? new Color(255, 235, 170)
                        : fila == filaHover ? new Color(255, 251, 239) : Color.WHITE);
                g2.fillRoundRect(4, area.y + 3, getWidth() - 8,
                        Math.max(1, area.height - 6), 22, 22);
            }
            g2.dispose();
            super.paintComponent(g);
            if (getRowCount() == 0) {
                Graphics2D emptyG2 = (Graphics2D) g.create();
                emptyG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int y = Math.max(48, getHeight() / 2 - 10);
                emptyG2.setFont(new Font("SansSerif", Font.BOLD, 16));
                emptyG2.setColor(TINTA);
                String titulo = "El pedido está vacío";
                emptyG2.drawString(titulo, (getWidth() - emptyG2.getFontMetrics().stringWidth(titulo)) / 2, y);
                emptyG2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                emptyG2.setColor(new Color(105, 109, 115));
                String ayuda = "Agrega productos desde el menú";
                emptyG2.drawString(ayuda, (getWidth() - emptyG2.getFontMetrics().stringWidth(ayuda)) / 2, y + 24);
                emptyG2.dispose();
            }
        }

        @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                int row, int column) {
            Component componente = super.prepareRenderer(renderer, row, column);
            if (componente instanceof JComponent componenteSwing) {
                componenteSwing.setOpaque(false);
                componenteSwing.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            }
            return componente;
        }

        private void actualizarHover(java.awt.event.MouseEvent evento) {
            int anterior = filaHover;
            filaHover = rowAtPoint(evento.getPoint());
            if (anterior != filaHover) repaint();
        }

        {
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override public void mouseMoved(java.awt.event.MouseEvent evento) {
                    actualizarHover(evento);
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseExited(java.awt.event.MouseEvent evento) {
                    if (filaHover != -1) {
                        filaHover = -1;
                        repaint();
                    }
                }
            });
        }
    };
    private final JLabel total = new JLabel("Total: Q0.00");
    private final JLabel contador = new JLabel("0 productos") {
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(239, 241, 244));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(graphics);
        }
    };
    private final JButton cobrar = boton("Continuar al pago", AMARILLO, TINTA);
    private final JButton mas = boton("+", AMARILLO, TINTA), menos = boton("-", new Color(255, 246, 217), TINTA), quitar = boton("Quitar", new Color(255, 237, 238), ROJO);
    private final JButton cancelar = boton("Cancelar pedido", ROJO, Color.WHITE), ultimo = boton("Último comprobante", new Color(245, 246, 248), TINTA);
    private boolean ocupado;
    private SolicitudPago pendiente;
    private String comprobante;
    private final Utilidades.PagoPendienteStore respaldo = new Utilidades.PagoPendienteStore(SesionUsuario.getIdUsuario());
    private final Utilidades.UltimoComprobanteStore ultimoComprobanteStore =
            new Utilidades.UltimoComprobanteStore(SesionUsuario.getIdUsuario());
    private boolean recuperacionFallida;

    public PedidoPanel() {
        super();
        setLayout(vistasPanel);
        setOpaque(false);
        resumenPedido.setOpaque(false);
        resumenPedido.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));
        JPanel encabezadoPedido = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0)) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ROJO_HEADER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        encabezadoPedido.setOpaque(false);
        JPanel indicador = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMARILLO);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.dispose();
            }
        };
        indicador.setOpaque(false);
        indicador.setPreferredSize(new Dimension(48, 48));
        ImageIcon iconoPedido = new ImageIcon(
                getClass().getResource("/Imagenes/pedido.png")
        );
        Image imagenPedido = iconoPedido.getImage().getScaledInstance(
                34, 34, Image.SCALE_SMOOTH
        );
        JLabel marca = new JLabel(new ImageIcon(imagenPedido), SwingConstants.CENTER);
        indicador.setLayout(new BorderLayout());
        indicador.add(marca, BorderLayout.CENTER);
        JPanel textosHeader = new JPanel();
        textosHeader.setOpaque(false);
        textosHeader.setLayout(new BoxLayout(textosHeader, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("PEDIDO");
        titulo.setFont(new Font(FUENTE, Font.BOLD, 21));
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("RESUMEN DEL PEDIDO");
        subtitulo.setFont(new Font(FUENTE, Font.BOLD, 11));
        subtitulo.setForeground(new Color(255, 235, 192));
        textosHeader.add(titulo);
        textosHeader.add(subtitulo);
        encabezadoPedido.add(indicador);
        encabezadoPedido.add(textosHeader);
        encabezadoPedido.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        resumenPedido.add(encabezadoPedido, BorderLayout.NORTH);

        tabla.setModel(modelo);
        tabla.setOpaque(false);
        tabla.setBorder(BorderFactory.createEmptyBorder());
        tabla.setBackground(new Color(247, 247, 249));
        tabla.setRowHeight(76);
        tabla.setShowHorizontalLines(false);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 4));
        tabla.setFont(new Font(FUENTE, Font.PLAIN, 13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setColorCabecera(AMARILLO);
        tabla.setColorTextoCabecera(TINTA);
        tabla.setFuenteCabecera(new Font(FUENTE, Font.BOLD, 12));
        tabla.setAltoCabecera(42);
        tabla.setColorFilas(Color.WHITE);
        tabla.setFilasAlternadas(true);
        tabla.setColorFilaAlterna(new Color(255, 251, 239));
        tabla.setColorSeleccion(new Color(255, 235, 170));
        tabla.setColorLineas(new Color(241, 236, 220));
        tabla.setPaddingHorizontal(10);
        tabla.setColumnasCentradas("1");
        tabla.setColumnasDerecha("2");
        tabla.getColumnModel().getColumn(0).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean selected, boolean focused, int row, int column) {
                javax.swing.JTextPane area = new javax.swing.JTextPane();
                area.setText(String.valueOf(value).replace(" | ", "\n• "));
                area.setEditable(false);
                area.setFocusable(false);
                area.setOpaque(false); area.setForeground(TINTA);
                area.setFont(new Font(FUENTE, Font.PLAIN, 15));
                area.setMargin(new Insets(0, 0, 0, 0));
                area.setBorder(BorderFactory.createEmptyBorder(7, 8, 5, 8));
                javax.swing.text.SimpleAttributeSet centrado =
                        new javax.swing.text.SimpleAttributeSet();
                javax.swing.text.StyleConstants.setAlignment(
                        centrado,
                        javax.swing.text.StyleConstants.ALIGN_CENTER
                );
                area.getStyledDocument().setParagraphAttributes(
                        0,
                        area.getDocument().getLength(),
                        centrado,
                        false
                );
                return area;
            }
        });
        tabla.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(52);
        tabla.getColumnModel().getColumn(1).setMaxWidth(65);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(2).setMinWidth(95);
        JScrollPane scroll = new JScrollPane(tabla);
        Componentes.DesplazamientoSuave.ocultarBarras(scroll);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setPreferredSize(new Dimension(320, 180));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(247, 247, 249));
        javax.swing.table.JTableHeader encabezado = new javax.swing.table.JTableHeader(tabla.getColumnModel()) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMARILLO);
                g2.fillRoundRect(0, 0, Math.max(1, getWidth() - 1),
                        Math.max(1, getHeight() - 1), 18, 18);
                g2.setColor(TINTA);
                g2.setFont(new Font(FUENTE, Font.BOLD, 12));
                for (int columna = 0; columna < getColumnModel().getColumnCount(); columna++) {
                    Rectangle area = getHeaderRect(columna);
                    String texto = tabla.getColumnName(columna);
                    int x;
                    if (columna == 1) {
                        x = area.x + (area.width - g2.getFontMetrics().stringWidth(texto)) / 2;
                    } else if (columna == 2) {
                        x = area.x + area.width - g2.getFontMetrics().stringWidth(texto) - 10;
                    } else {
                        x = area.x + 10;
                    }
                    int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2
                            + g2.getFontMetrics().getAscent();
                    g2.drawString(texto, x, y);
                }
                g2.dispose();
            }
        };
        encabezado.setOpaque(false);
        encabezado.setBackground(Color.WHITE);
        encabezado.setBorder(BorderFactory.createEmptyBorder());
        encabezado.setPreferredSize(new Dimension(0, 36));
        encabezado.setReorderingAllowed(false);
        encabezado.setResizingAllowed(false);
        encabezado.setFocusable(false);
        encabezado.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent evento) { evento.consume(); }
            @Override public void mouseReleased(java.awt.event.MouseEvent evento) { evento.consume(); }
            @Override public void mouseClicked(java.awt.event.MouseEvent evento) { evento.consume(); }
        });
        encabezado.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(javax.swing.JTable table,
                    Object value, boolean selected, boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                label.setOpaque(false);
                label.setForeground(TINTA);
                label.setFont(new Font(FUENTE, Font.BOLD, 12));
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                label.setHorizontalAlignment(column == 1 ? SwingConstants.CENTER
                        : column == 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return label;
            }
        });
        tabla.setTableHeader(encabezado);
        scroll.setColumnHeaderView(encabezado);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        javax.swing.JScrollBar barraVertical = scroll.getVerticalScrollBar();
        barraVertical.setUI(new BarraPedidoMinimalista());
        barraVertical.setOpaque(false);
        barraVertical.setPreferredSize(new Dimension(10, 0));
        barraVertical.setBackground(new Color(0, 0, 0, 0));
        JPanel esquinaAmarilla = new JPanel();
        esquinaAmarilla.setOpaque(true);
        esquinaAmarilla.setBackground(Color.WHITE);
        scroll.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, esquinaAmarilla);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        JPanel tablaContenedor = new JPanel(new BorderLayout());
        tablaContenedor.setOpaque(false);
        tablaContenedor.setBorder(BorderFactory.createEmptyBorder());
        tablaContenedor.add(scroll);
        JPanel encabezadoTabla = new JPanel(new BorderLayout());
        encabezadoTabla.setOpaque(false);
        JLabel tituloProductos = new JLabel("Productos seleccionados");
        tituloProductos.setFont(new Font(FUENTE, Font.BOLD, 16));
        tituloProductos.setForeground(TINTA);
        contador.setFont(new Font(FUENTE, Font.BOLD, 11));
        contador.setForeground(new Color(31, 41, 55));
        contador.setHorizontalAlignment(SwingConstants.CENTER);
        contador.setBorder(BorderFactory.createEmptyBorder(5, 11, 5, 11));
        contador.setOpaque(false);
        encabezadoTabla.add(tituloProductos, BorderLayout.WEST);
        encabezadoTabla.add(contador, BorderLayout.EAST);
        JPanel contenidoTabla = new JPanel(new BorderLayout(0, 8));
        contenidoTabla.setOpaque(false);
        contenidoTabla.add(encabezadoTabla, BorderLayout.NORTH);
        contenidoTabla.add(tablaContenedor, BorderLayout.CENTER);
        centro.add(contenidoTabla);
        JPanel cantidades = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cantidades.setOpaque(false);
        menos.setPreferredSize(new Dimension(42, 36));
        menos.setMargin(new Insets(0, 0, 0, 0));
        mas.setMargin(new Insets(0, 0, 0, 0));
        mas.setPreferredSize(new Dimension(42, 36));
        quitar.setPreferredSize(new Dimension(92, 36));
        menos.setFont(new Font(FUENTE, Font.BOLD, 20));
        mas.setFont(new Font(FUENTE, Font.BOLD, 20));
        menos.setToolTipText("Reducir cantidad del producto seleccionado");
        mas.setToolTipText("Aumentar cantidad del producto seleccionado");
        quitar.setToolTipText("Quitar el producto seleccionado");
        cantidades.add(menos); cantidades.add(mas); cantidades.add(quitar);
        centro.add(cantidades, BorderLayout.SOUTH);
        resumenPedido.add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new GridLayout(0, 1, 0, 10));
        pie.setOpaque(false);
        pie.setPreferredSize(new Dimension(320, 202));
        total.setFont(new Font(FUENTE, Font.BOLD, 23));
        total.setForeground(TINTA);
        total.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(238, 231, 211)));
        pie.add(total); pie.add(cobrar); pie.add(cancelar); pie.add(ultimo);
        resumenPedido.add(pie, BorderLayout.SOUTH);
        mas.addActionListener(e -> cambiar(1)); menos.addActionListener(e -> cambiar(-1)); quitar.addActionListener(e -> cambiar(-999));
        cancelar.addActionListener(e -> {
            // Un cobro en curso conserva su solicitud; cerrar la vista no lo cancela.
            if (!bloqueado()) {
                if (!lineas.isEmpty() && JOptionPane.showConfirmDialog(this,
                        "¿Cancelar todo el pedido y volver al menú?", "Cancelar pedido",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                lineas.clear();
                actualizar();
            }
            setVisible(false);
            if (getParent() != null) {
                getParent().revalidate();
                getParent().repaint();
            }
        });
        cobrar.addActionListener(e -> abrirPago()); ultimo.addActionListener(e -> mostrarComprobante());
        add(resumenPedido, "RESUMEN");
        vistasPanel.show(this, "RESUMEN");
        try {
            pendiente = respaldo.cargar();
            if (pendiente != null) {
                if (pendiente.usuario() != SesionUsuario.getIdUsuario()) throw new java.io.IOException("El cobro pendiente pertenece a otro usuario.");
                for (LineaPedido l : pendiente.lineas()) lineas.put(l.idLinea(), l);
            }
        } catch (java.io.IOException ex) {
            recuperacionFallida = true;
            SwingUtilities.invokeLater(() -> aviso(ex.getMessage()));
        }
        try {
            comprobante = ultimoComprobanteStore.cargar();
        } catch (java.io.IOException ex) {
            SwingUtilities.invokeLater(() -> aviso(
                    "No se pudo recuperar el último comprobante: " + ex.getMessage()));
        }
        actualizar();
    }

    private static JButton boton(String texto, Color fondo, Color textoColor) {
        BotonRedondeado b = new BotonRedondeado();
        b.setText(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setDegradado(false);
        b.setColorInicio(fondo);
        b.setForeground(textoColor);
        b.setRadio(20);
        b.setFocusPainted(true);
        b.setRolloverEnabled(true);
        b.setMargin(new Insets(8, 12, 8, 12));
        b.setPreferredSize(new Dimension(220, 44));
        return b;
    }

    /** Scroll minimalista: solo muestra el pulgar, sin flechas ni riel gris. */
    private static final class BarraPedidoMinimalista
            extends javax.swing.plaf.basic.BasicScrollBarUI {

        @Override protected JButton createDecreaseButton(int orientation) {
            return botonInvisible();
        }

        @Override protected JButton createIncreaseButton(int orientation) {
            return botonInvisible();
        }

        @Override protected void paintTrack(Graphics graphics, JComponent componente,
                Rectangle limites) {
            // El riel queda transparente para mostrar únicamente la barra.
        }

        @Override protected void paintThumb(Graphics graphics, JComponent componente,
                Rectangle limites) {
            if (limites.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(110, 119, 132, 170));
            int ancho = Math.min(6, Math.max(4, limites.width - 3));
            int x = limites.x + (limites.width - ancho) / 2;
            g2.fillRoundRect(x, limites.y + 2, ancho,
                    Math.max(8, limites.height - 4), ancho, ancho);
            g2.dispose();
        }

        private static JButton botonInvisible() {
            JButton boton = new JButton();
            boton.setPreferredSize(new Dimension(0, 0));
            boton.setMinimumSize(new Dimension(0, 0));
            boton.setMaximumSize(new Dimension(0, 0));
            boton.setBorder(BorderFactory.createEmptyBorder());
            boton.setOpaque(false);
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
            return boton;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 10));
        g2.fillRoundRect(2, 4, Math.max(1, getWidth() - 4), Math.max(1, getHeight() - 6), 30, 30);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), Math.max(1, getHeight() - 2), 30, 30);
        g2.dispose();
        super.paintComponent(graphics);
    }

    public void agregar(LineaPedido linea) {
        if (bloqueado()) {
            aviso("Termine o reintente el cobro pendiente antes de modificar el pedido.");
            return;
        }
        lineas.put(linea.idLinea(), linea);
        actualizar();
    }

    /**
     * Sustituye temporalmente el resumen por la personalización del producto.
     * Al agregar o volver se recupera el mismo carrito, sin perder sus líneas.
     */
    public void mostrarConfigurador(ConfiguracionProducto producto,
            Runnable alFinalizar) {
        if (bloqueado()) {
            aviso("Termine o reintente el cobro pendiente antes de agregar productos.");
            if (alFinalizar != null) alFinalizar.run();
            return;
        }
        if (configuradorActual != null) {
            remove(configuradorActual);
        }
        configuradorActual = new ConfiguradorProductoPanel(producto,
                linea -> {
                    agregar(linea);
                    mostrarResumen();
                    if (alFinalizar != null) alFinalizar.run();
                },
                () -> {
                    mostrarResumen();
                    if (alFinalizar != null) alFinalizar.run();
                });
        add(configuradorActual, "CONFIGURADOR");
        vistasPanel.show(this, "CONFIGURADOR");
        revalidate();
        repaint();
    }

    private void mostrarResumen() {
        vistasPanel.show(this, "RESUMEN");
        if (configuradorActual != null) {
            remove(configuradorActual);
            configuradorActual = null;
        }
        revalidate();
        repaint();
    }

    private void cambiar(int incremento) {
        if (bloqueado() || tabla.getSelectedRow() < 0) return;
        LineaPedido l = new ArrayList<>(lineas.values()).get(tabla.getSelectedRow());
        int n = l.cantidad() + incremento;
        if (n <= 0) lineas.remove(l.idLinea());
        else if (n <= 999) lineas.put(l.idLinea(), l.conCantidad(n));
        actualizar();
    }

    private boolean bloqueado() { return ocupado || pendiente != null || recuperacionFallida; }
    private BigDecimal suma() { return lineas.values().stream().map(LineaPedido::subtotal).reduce(new BigDecimal("0.00"), BigDecimal::add); }
    private void actualizar() {
        int seleccion = tabla.getSelectedRow(); modelo.setRowCount(0);
        for (LineaPedido l : lineas.values()) modelo.addRow(new Object[]{l.resumen(), l.cantidad(), "Q" + l.subtotal().toPlainString()});
        if (seleccion >= 0 && seleccion < modelo.getRowCount()) tabla.setRowSelectionInterval(seleccion, seleccion);
        total.setText("Total: Q" + suma().toPlainString());
        contador.setText(modelo.getRowCount() + (modelo.getRowCount() == 1 ? " producto" : " productos"));
        cobrar.setText(pendiente == null ? "Continuar al pago" : "Reintentar cobro pendiente");
        cobrar.setEnabled(!ocupado && !recuperacionFallida && !lineas.isEmpty());
        for (JButton b : new JButton[]{mas, menos, quitar}) b.setEnabled(!bloqueado() && !lineas.isEmpty());
        cancelar.setEnabled(true);
        ultimo.setEnabled(comprobante != null);
    }

    private void abrirPago() {
        if (ocupado || recuperacionFallida || lineas.isEmpty()) return;
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Cobrar pedido", Dialog.ModalityType.APPLICATION_MODAL);
        // Conserva el encabezado del diseño y elimina la barra de título de Windows.
        dialogo.setUndecorated(true);
        dialogo.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        dialogo.setResizable(false);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(220, 226, 233)));
        FormularioCobro formulario = new FormularioCobro(suma());
        JComboBox<String> servicio = formulario.servicio, metodo = formulario.metodo;
        JTextField recibido = formulario.recibido, referencia = formulario.referencia;
        JCheckBox confirmado = formulario.confirmado;
        JButton guardar = formulario.guardar, volver = formulario.volver;
        Runnable refrescar = () -> {
            boolean tarjeta = metodo.getSelectedIndex() == 1;
            recibido.setEnabled(!tarjeta && pendiente == null); referencia.setEnabled(tarjeta && pendiente == null); confirmado.setEnabled(tarjeta && pendiente == null);
            formulario.mostrarMetodo(tarjeta);
            try { formulario.actualizarCambio(monto(recibido.getText()).subtract(suma())); }
            catch (IllegalArgumentException ex) { formulario.montoInvalido(); }
        };
        recibido.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refrescar.run(); }
            public void removeUpdate(DocumentEvent e) { refrescar.run(); }
            public void changedUpdate(DocumentEvent e) { refrescar.run(); }
        });
        metodo.addActionListener(e -> { if (metodo.getSelectedIndex() == 1) recibido.setText(suma().toPlainString()); refrescar.run(); });
        if (pendiente != null) {
            servicio.setSelectedIndex(pendiente.servicio().equals("COMER_AQUI") ? 0 : 1);
            metodo.setSelectedIndex(pendiente.metodo().equals("EFECTIVO") ? 0 : 1);
            recibido.setText(pendiente.recibido().toPlainString()); referencia.setText(pendiente.referencia()); confirmado.setSelected(true);
            servicio.setEnabled(false); metodo.setEnabled(false); guardar.setText("Reintentar misma operación");
        }
        refrescar.run();
        volver.addActionListener(e -> dialogo.dispose());
        guardar.addActionListener(e -> {
            boolean nueva = pendiente == null;
            try {
                if (pendiente == null) {
                    boolean tarjeta = metodo.getSelectedIndex() == 1;
                    if (tarjeta && !confirmado.isSelected()) throw new IllegalArgumentException("Confirme primero la aprobación en la terminal externa.");
                    pendiente = new SolicitudPago(UUID.randomUUID().toString(), SesionUsuario.getIdUsuario(), new ArrayList<>(lineas.values()),
                            servicio.getSelectedIndex() == 0 ? "COMER_AQUI" : "PARA_LLEVAR", tarjeta ? "TARJETA" : "EFECTIVO",
                            tarjeta ? suma() : monto(recibido.getText()), tarjeta ? referencia.getText() : "");
                }
                if (nueva) respaldo.guardar(pendiente);
            } catch (java.io.IOException ex) {
                pendiente = null;
                aviso("No se pudo guardar el cobro pendiente. No se ha enviado a MySQL: " + ex.getMessage()); return;
            } catch (IllegalArgumentException ex) { aviso(ex.getMessage()); return; }
            ocupado = true; actualizar();
            guardar.setEnabled(false); volver.setEnabled(false); servicio.setEnabled(false); metodo.setEnabled(false); refrescar.run();
            dialogo.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            SolicitudPago solicitud = pendiente;
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() throws Exception { return new PagoDAO().cobrar(solicitud); }
                @Override protected void done() {
                    try {
                        comprobante = get();
                        try {
                            ultimoComprobanteStore.guardar(comprobante);
                        } catch (java.io.IOException ex) {
                            aviso("El pago se confirmó, pero no se pudo guardar el último comprobante: "
                                    + ex.getMessage());
                        }
                        respaldo.eliminar();
                        pendiente = null; lineas.clear(); dialogo.dispose(); mostrarComprobante();
                    } catch (Exception ex) {
                        Throwable causa = ex instanceof java.util.concurrent.ExecutionException ? ex.getCause() : ex;
                        if (causa instanceof IllegalArgumentException) {
                            try { respaldo.eliminar(); pendiente = null; dialogo.dispose(); }
                            catch (java.io.IOException fallo) { aviso("No se pudo retirar el respaldo pendiente: " + fallo.getMessage()); }
                        }
                        JOptionPane.showMessageDialog(dialogo.isDisplayable() ? dialogo : PedidoPanel.this,
                                mensaje(ex) + (pendiente != null ? "\nEl pedido se conserva. Reintente esta operación; no repita el cobro externo." : ""), "No se completó el registro", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        ocupado = false; actualizar(); guardar.setEnabled(true); volver.setEnabled(true);
                        guardar.setText("Reintentar misma operación"); dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    }
                }
            }.execute();
        });
        // El contenido conserva su tamaño natural; en pantallas pequeñas puede desplazarse.
        JScrollPane desplazamiento = new JScrollPane(formulario,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        desplazamiento.getVerticalScrollBar().setUI(new BarraPedidoMinimalista());
        desplazamiento.getVerticalScrollBar().setOpaque(false);
        desplazamiento.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        dialogo.setContentPane(desplazamiento);
        dialogo.getRootPane().setDefaultButton(guardar);
        dialogo.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "volverPedido");
        dialogo.getRootPane().getActionMap().put("volverPedido", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (volver.isEnabled()) dialogo.dispose();
            }
        });
        Runnable ajustarVentana = () -> {
            // Calcula el tamaño con los componentes actuales, incluso si el proyecto los escala.
            // No impone una altura mínima que deje espacio vacío debajo de los botones.
            dialogo.setMinimumSize(new Dimension(0, 0));
            dialogo.pack();
            GraphicsConfiguration pantalla = dialogo.getGraphicsConfiguration();
            Rectangle area = pantalla.getBounds();
            Insets bordesPantalla = Toolkit.getDefaultToolkit().getScreenInsets(pantalla);
            int anchoDisponible = Math.max(1, area.width - bordesPantalla.left - bordesPantalla.right - 32);
            int altoDisponible = Math.max(1, area.height - bordesPantalla.top - bordesPantalla.bottom - 32);
            dialogo.setSize(Math.min(dialogo.getWidth(), anchoDisponible),
                    Math.min(dialogo.getHeight(), altoDisponible));
            dialogo.setLocationRelativeTo(this);
        };
        ajustarVentana.run();
        dialogo.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowOpened(java.awt.event.WindowEvent e) {
                // Se vuelve a ajustar después de cualquier escalado al abrir la ventana.
                SwingUtilities.invokeLater(() -> {
                    if (!dialogo.isDisplayable()) return;
                    ajustarVentana.run();
                    if (pendiente != null) guardar.requestFocusInWindow();
                    else if (metodo.getSelectedIndex() == 1) referencia.requestFocusInWindow();
                    else { recibido.requestFocusInWindow(); recibido.selectAll(); }
                });
            }
        });
        dialogo.setVisible(true);
    }

    /** Vista del cobro; no realiza operaciones ni modifica los datos del pedido. */
    private static final class FormularioCobro extends JPanel implements Scrollable {
        private static final Color TEXTO_SECUNDARIO = new Color(108, 119, 135);
        private static final Color BORDE = new Color(220, 226, 233);
        private final JComboBox<String> servicio = selector(new String[]{"Comer aquí", "Para llevar"});
        private final JComboBox<String> metodo = selector(new String[]{"Efectivo", "Tarjeta"});
        private final JTextField recibido;
        private final JTextField referencia = new JTextField();
        private final JCheckBox confirmado = new JCheckBox("Pago aprobado en la terminal externa");
        private final JLabel cambio = new JLabel("Q0.00", SwingConstants.RIGHT);
        private final JLabel tituloCambio = etiqueta("Cambio a entregar", 12, Font.PLAIN);
        private final JButton guardar = new AccionCobro("Confirmar pago", AMARILLO, ROJO_HEADER);
        private final JButton volver = new AccionCobro("Volver al pedido", new Color(243, 245, 248), ROJO_HEADER);
        private final CardLayout modoPago = new CardLayout();
        private final JPanel detallePago = new JPanel(modoPago);
        private final SuperficieCobro resumenCambio = new SuperficieCobro(new Color(242, 247, 243), null);

        private FormularioCobro(BigDecimal importe) {
            super(new BorderLayout(0, 20));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(24, 24, 22, 24));
            recibido = new JTextField(importe.toPlainString());

            JPanel encabezado = transparente(new BorderLayout(14, 0));
            JLabel icono = new JLabel(new IconoCobro());
            encabezado.add(icono, BorderLayout.WEST);
            JPanel textos = transparente(new GridLayout(2, 1, 0, 4));
            textos.add(etiqueta("Cobrar pedido", 21, Font.BOLD));
            JLabel ayuda = etiqueta("Completa los datos para finalizar el pedido", 12, Font.PLAIN);
            ayuda.setForeground(TEXTO_SECUNDARIO);
            textos.add(ayuda);
            encabezado.add(textos, BorderLayout.CENTER);
            add(encabezado, BorderLayout.NORTH);

            JPanel contenido = transparente(new BorderLayout(0, 18));
            SuperficieCobro resumenTotal = new SuperficieCobro(new Color(255, 248, 226), null);
            resumenTotal.setLayout(new BorderLayout(12, 0));
            resumenTotal.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
            resumenTotal.add(etiqueta("Total a pagar", 13, Font.BOLD), BorderLayout.WEST);
            JLabel importeTotal = etiqueta("Q" + importe.toPlainString(), 30, Font.BOLD);
            importeTotal.setHorizontalAlignment(SwingConstants.RIGHT);
            resumenTotal.add(importeTotal, BorderLayout.CENTER);
            contenido.add(resumenTotal, BorderLayout.NORTH);

            JPanel campos = transparente(new BorderLayout(0, 18));
            JPanel opciones = transparente(new GridLayout(1, 2, 16, 0));
            opciones.add(campo("Tipo de pedido", servicio));
            opciones.add(campo("Método de pago", metodo));
            campos.add(opciones, BorderLayout.NORTH);

            recibido.setFont(new Font(FUENTE, Font.BOLD, 18));
            recibido.setForeground(ROJO_HEADER);
            recibido.setDisabledTextColor(TEXTO_SECUNDARIO);
            recibido.setOpaque(false);
            recibido.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
            SuperficieCobro entradaMonto = new SuperficieCobro(Color.WHITE, BORDE);
            entradaMonto.setLayout(new BorderLayout(0, 0));
            entradaMonto.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            entradaMonto.setPreferredSize(new Dimension(0, 46));
            JLabel moneda = etiqueta("Q", 16, Font.PLAIN);
            moneda.setForeground(TEXTO_SECUNDARIO);
            entradaMonto.add(moneda, BorderLayout.WEST);
            entradaMonto.add(recibido, BorderLayout.CENTER);
            entradaMonto.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mousePressed(java.awt.event.MouseEvent e) {
                    if (recibido.isEnabled()) recibido.requestFocusInWindow();
                }
            });
            foco(recibido, entradaMonto);

            JPanel efectivo = transparente(new BorderLayout(0, 14));
            efectivo.add(campo("Monto recibido", entradaMonto, recibido), BorderLayout.NORTH);
            resumenCambio.setLayout(new BorderLayout(8, 0));
            resumenCambio.setBorder(BorderFactory.createEmptyBorder(15, 16, 15, 16));
            cambio.setFont(new Font(FUENTE, Font.BOLD, 20));
            cambio.setForeground(new Color(39, 112, 69));
            resumenCambio.add(tituloCambio, BorderLayout.WEST);
            resumenCambio.add(cambio, BorderLayout.CENTER);
            efectivo.add(resumenCambio, BorderLayout.SOUTH);

            referencia.setFont(new Font(FUENTE, Font.PLAIN, 14));
            referencia.setForeground(ROJO_HEADER);
            referencia.setDisabledTextColor(TEXTO_SECUNDARIO);
            referencia.setBorder(new BordeCampoCobro());
            referencia.setPreferredSize(new Dimension(0, 44));
            referencia.setOpaque(false);
            referencia.setToolTipText("Solo la referencia del comprobante de la terminal; no ingreses datos de tarjeta.");
            foco(referencia, referencia);
            JPanel tarjeta = transparente(new BorderLayout(0, 14));
            JPanel referenciaTerminal = transparente(new BorderLayout(0, 6));
            referenciaTerminal.add(campo("Referencia de terminal", referencia), BorderLayout.CENTER);
            JLabel avisoReferencia = etiqueta("Sin número de tarjeta, fecha de vencimiento ni CVV.", 11, Font.PLAIN);
            avisoReferencia.setForeground(TEXTO_SECUNDARIO);
            referenciaTerminal.add(avisoReferencia, BorderLayout.SOUTH);
            tarjeta.add(referenciaTerminal, BorderLayout.NORTH);
            confirmado.setFont(new Font(FUENTE, Font.PLAIN, 12));
            confirmado.setForeground(ROJO_HEADER);
            confirmado.setOpaque(false);
            confirmado.setBorder(BorderFactory.createEmptyBorder());
            SuperficieCobro aprobacion = new SuperficieCobro(new Color(246, 247, 250), null);
            aprobacion.setLayout(new BorderLayout());
            aprobacion.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
            aprobacion.add(confirmado, BorderLayout.CENTER);
            tarjeta.add(aprobacion, BorderLayout.SOUTH);

            detallePago.setOpaque(false);
            detallePago.add(efectivo, "EFECTIVO");
            detallePago.add(tarjeta, "TARJETA");
            campos.add(detallePago, BorderLayout.CENTER);
            contenido.add(campos, BorderLayout.CENTER);
            add(contenido, BorderLayout.CENTER);

            JPanel acciones = transparente(new GridLayout(1, 2, 12, 0));
            acciones.add(volver);
            acciones.add(guardar);
            JPanel pie = transparente(new BorderLayout());
            pie.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(236, 239, 243)),
                    BorderFactory.createEmptyBorder(18, 0, 0, 0)));
            pie.add(acciones, BorderLayout.CENTER);
            add(pie, BorderLayout.SOUTH);
            servicio.getAccessibleContext().setAccessibleName("Tipo de pedido");
            metodo.getAccessibleContext().setAccessibleName("Método de pago");
            recibido.getAccessibleContext().setAccessibleName("Monto recibido en quetzales");
            referencia.getAccessibleContext().setAccessibleName("Referencia de terminal");
        }

        private void mostrarMetodo(boolean tarjeta) {
            modoPago.show(detallePago, tarjeta ? "TARJETA" : "EFECTIVO");
        }

        private void actualizarCambio(BigDecimal valor) {
            boolean falta = valor.signum() < 0;
            tituloCambio.setText(falta ? "Monto pendiente" : "Cambio a entregar");
            cambio.setFont(new Font(FUENTE, Font.BOLD, 20));
            cambio.setText("Q" + valor.abs().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            cambio.setForeground(falta ? ROJO : new Color(39, 112, 69));
            resumenCambio.fondo = falta ? new Color(255, 241, 242) : new Color(242, 247, 243);
            resumenCambio.repaint();
        }

        private void montoInvalido() {
            tituloCambio.setText("Monto inválido");
            cambio.setFont(new Font(FUENTE, Font.PLAIN, 12));
            cambio.setText("Usa hasta 2 decimales");
            cambio.setForeground(ROJO);
            resumenCambio.fondo = new Color(255, 241, 242);
            resumenCambio.repaint();
        }

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return Math.max(16, (o == SwingConstants.VERTICAL ? r.height : r.width) - 16);
        }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }

        private static JLabel etiqueta(String texto, int tamano, int estilo) {
            JLabel etiqueta = new JLabel(texto);
            etiqueta.setFont(new Font(FUENTE, estilo, tamano));
            etiqueta.setForeground(ROJO_HEADER);
            return etiqueta;
        }

        private static JPanel transparente(LayoutManager layout) {
            JPanel panel = new JPanel(layout);
            panel.setOpaque(false);
            return panel;
        }

        private static JPanel campo(String nombre, JComponent control) {
            return campo(nombre, control, control);
        }

        private static JPanel campo(String nombre, JComponent control, JComponent foco) {
            JPanel panel = transparente(new BorderLayout(0, 8));
            JLabel nombreCampo = etiqueta(nombre, 12, Font.BOLD);
            nombreCampo.setLabelFor(foco);
            panel.add(nombreCampo, BorderLayout.NORTH);
            panel.add(control, BorderLayout.CENTER);
            return panel;
        }

        private static void foco(JComponent control, JComponent superficie) {
            control.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) { superficie.repaint(); }
                @Override public void focusLost(java.awt.event.FocusEvent e) { superficie.repaint(); }
            });
        }

        private static JComboBox<String> selector(String[] opciones) {
            JComboBox<String> combo = new JComboBox<>(opciones);
            combo.setFont(new Font(FUENTE, Font.PLAIN, 13));
            combo.setForeground(ROJO_HEADER);
            combo.setBackground(Color.WHITE);
            combo.setOpaque(false);
            combo.setBorder(new BordeCampoCobro());
            combo.setPreferredSize(new Dimension(0, 44));
            combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override protected JButton createArrowButton() {
                    JButton flecha = new JButton() {
                        @Override protected void paintComponent(Graphics graphics) {
                            Graphics2D g2 = (Graphics2D) graphics.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(isEnabled() ? ROJO_HEADER : TEXTO_SECUNDARIO);
                            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            int x = getWidth() / 2, y = getHeight() / 2;
                            g2.drawLine(x - 4, y - 2, x, y + 2);
                            g2.drawLine(x, y + 2, x + 4, y - 2);
                            g2.dispose();
                        }
                    };
                    flecha.setPreferredSize(new Dimension(26, 24));
                    flecha.setBorder(BorderFactory.createEmptyBorder());
                    flecha.setOpaque(false);
                    flecha.setContentAreaFilled(false);
                    flecha.setFocusable(false);
                    return flecha;
                }
                @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean foco) {
                    // La superficie blanca y el contorno redondeado pertenecen al campo.
                }
            });
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> lista, Object valor,
                        int indice, boolean seleccionado, boolean enfocado) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(lista, valor, indice, seleccionado, enfocado);
                    label.setFont(new Font(FUENTE, Font.PLAIN, 13));
                    label.setOpaque(indice >= 0);
                    label.setBackground(seleccionado ? new Color(255, 246, 217) : Color.WHITE);
                    label.setForeground(combo.isEnabled() ? ROJO_HEADER : TEXTO_SECUNDARIO);
                    label.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
                    return label;
                }
            });
            foco(combo, combo);
            return combo;
        }

        private static boolean contieneFoco(Component componente) {
            Component foco = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            return foco != null && (foco == componente || SwingUtilities.isDescendingFrom(foco, componente));
        }

        private static final class BordeCampoCobro extends javax.swing.border.AbstractBorder {
            @Override public Insets getBorderInsets(Component c) { return new Insets(10, 12, 10, 8); }
            @Override public Insets getBorderInsets(Component c, Insets insets) {
                insets.set(10, 12, 10, 8);
                return insets;
            }
            @Override public void paintBorder(Component c, Graphics graphics, int x, int y, int ancho, int alto) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean foco = contieneFoco(c);
                g2.setColor(foco ? AMARILLO : BORDE);
                g2.setStroke(new BasicStroke(foco ? 2f : 1f));
                g2.drawRoundRect(x + 1, y + 1, Math.max(0, ancho - 3), Math.max(0, alto - 3), 12, 12);
                g2.dispose();
            }
        }

        private static final class SuperficieCobro extends JPanel {
            private Color fondo;
            private final Color borde;
            private SuperficieCobro(Color fondo, Color borde) {
                this.fondo = fondo;
                this.borde = borde;
                setOpaque(false);
            }
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (borde != null) {
                    boolean foco = contieneFoco(this);
                    g2.setColor(foco ? AMARILLO : borde);
                    g2.setStroke(new BasicStroke(foco ? 2f : 1f));
                    g2.drawRoundRect(1, 1, Math.max(0, getWidth() - 3), Math.max(0, getHeight() - 3), 12, 12);
                }
                g2.dispose();
                super.paintComponent(graphics);
            }
        }

        private static final class AccionCobro extends JButton {
            private final Color fondo;
            private AccionCobro(String texto, Color fondo, Color tinta) {
                super(texto);
                this.fondo = fondo;
                setFont(new Font(FUENTE, Font.BOLD, 13));
                setForeground(tinta);
                setPreferredSize(new Dimension(220, 44));
                setMargin(new Insets(8, 10, 8, 10));
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setFocusPainted(false);
                setRolloverEnabled(true);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = fondo;
                if (!isEnabled()) color = new Color(237, 239, 242);
                else if (getModel().isPressed()) color = fondo.darker();
                else if (getModel().isRollover()) color = fondo.equals(AMARILLO)
                        ? new Color(255, 179, 0) : new Color(233, 237, 243);
                g2.setColor(color);
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                if (hasFocus()) {
                    g2.setColor(ROJO_HEADER);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
                }
                g2.dispose();
                super.paintComponent(graphics);
            }
        }

        private static final class IconoCobro implements Icon {
            @Override public int getIconWidth() { return 48; }
            @Override public int getIconHeight() { return 48; }
            @Override public void paintIcon(Component c, Graphics graphics, int x, int y) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.translate(x, y);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 245, 216));
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(new Color(221, 146, 0));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(16, 13, 16, 23, 3, 3);
                g2.drawLine(20, 19, 28, 19);
                g2.drawLine(20, 24, 28, 24);
                g2.drawLine(20, 29, 25, 29);
                g2.dispose();
            }
        }
    }


    public static BigDecimal monto(String texto) {
        if (texto == null || !texto.trim().matches("[0-9]{1,8}([.,][0-9]{1,2})?")) throw new IllegalArgumentException("Monto inválido.");
        return new BigDecimal(texto.trim().replace(',', '.')).setScale(2);
    }
    private void mostrarComprobante() {
        if (comprobante == null) return;
        JDialog dialogo = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Comprobante",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialogo.setUndecorated(true);
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 233)),
                BorderFactory.createEmptyBorder(24, 24, 22, 24)));

        JPanel encabezado = new JPanel(new BorderLayout(14, 0));
        encabezado.setOpaque(false);
        encabezado.add(new JLabel(new FormularioCobro.IconoCobro()), BorderLayout.WEST);
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);
        textos.add(FormularioCobro.etiqueta("Comprobante de venta", 21, Font.BOLD));
        JLabel ayuda = FormularioCobro.etiqueta("El pedido fue cobrado correctamente", 12, Font.PLAIN);
        ayuda.setForeground(new Color(108, 119, 135));
        textos.add(ayuda);
        encabezado.add(textos, BorderLayout.CENTER);
        contenido.add(encabezado, BorderLayout.NORTH);

        JTextArea texto = new JTextArea(comprobante);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        texto.setForeground(TINTA);
        texto.setBackground(new Color(250, 251, 252));
        texto.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        texto.setMargin(new Insets(0, 0, 0, 0));
        texto.setLineWrap(false);
        JScrollPane scrollComprobante = new JScrollPane(texto);
        scrollComprobante.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 233)));
        scrollComprobante.getViewport().setBackground(texto.getBackground());
        contenido.add(scrollComprobante, BorderLayout.CENTER);

        JButton cerrar = new FormularioCobro.AccionCobro(
                "Cerrar", new Color(243, 245, 248), ROJO_HEADER
        );
        cerrar.addActionListener(e -> dialogo.dispose());
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(236, 239, 243)),
                BorderFactory.createEmptyBorder(18, 0, 0, 0)));
        pie.add(cerrar, BorderLayout.EAST);
        contenido.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenido);
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.getRootPane().setDefaultButton(cerrar);
        dialogo.getRootPane().registerKeyboardAction(
                e -> dialogo.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        dialogo.setSize(560, 590);
        dialogo.setMinimumSize(new Dimension(460, 480));
        dialogo.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialogo.setVisible(true);
    }
    private static String mensaje(Exception ex) {
        Throwable causa = ex instanceof java.util.concurrent.ExecutionException ? ex.getCause() : ex;
        return causa.getMessage() == null ? "No se pudo completar la operación." : causa.getMessage();
    }
    private void aviso(String mensaje) { JOptionPane.showMessageDialog(this, mensaje, "Pedido", JOptionPane.WARNING_MESSAGE); }
}
