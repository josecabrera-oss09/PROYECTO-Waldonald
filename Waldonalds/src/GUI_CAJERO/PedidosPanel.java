package GUI_CAJERO;

import Componentes.PanelFlotante;
import DAO.PedidosDAO;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.EmptyBorder;

/** Panel de seguimiento de pedidos y contador de preparación de cinco minutos. */
public final class PedidosPanel extends JPanel {
    private static final Color AZUL = new Color(1, 20, 36);
    private static final Color AMARILLO = new Color(255, 188, 13);
    private static final Color FONDO = new Color(248, 249, 251);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 229, 235);
    private static final Color LINEA_TABLA = new Color(216, 224, 234);
    private static final Color FONDO_CABECERA = new Color(247, 249, 252);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Pedido", "Hora", "Servicio", "Método", "Estado", "Tiempo", "Total", "Acción"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
        @Override public Class<?> getColumnClass(int columna) { return columna == 7 ? JButton.class : String.class; }
    };
    private final JTable tabla = new JTable(modelo);
    private List<PedidosDAO.Pedido> pedidos = List.of();
    private final Timer reloj = new Timer(1000, e -> actualizarTiempos());

    public PedidosPanel() {
        setLayout(new BorderLayout());
        setBackground(FONDO);
        setBorder(new EmptyBorder(22, 28, 22, 28));

        PanelFlotante tarjeta = new PanelFlotante();
        tarjeta.setColorFondo(Color.WHITE);
        tarjeta.setColorBorde(BORDE);
        tarjeta.setRadio(18);
        tarjeta.setTamanoSombra(8);
        tarjeta.setDesplazamientoSombraY(3);
        tarjeta.setLayout(new BorderLayout());
        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(14, 17, 18, 17));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JLabel titulo = new JLabel("Pedidos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(AZUL);
        JLabel ayuda = new JLabel("Tiempo de preparación por pedido: 5 minutos");
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ayuda.setForeground(SECUNDARIO);
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);
        textos.add(titulo); textos.add(ayuda);
        JButton recargar = new BotonActualizar();
        recargar.addActionListener(e -> cargar());
        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(recargar, BorderLayout.EAST);
        contenido.add(encabezado, BorderLayout.NORTH);

        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setForeground(AZUL);
        tabla.setBackground(Color.WHITE);
        tabla.setRowHeight(44);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(LINEA_TABLA);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(new Color(255, 244, 204));
        tabla.setSelectionForeground(AZUL);
        tabla.setFillsViewportHeight(true);
        tabla.setDefaultRenderer(Object.class, new TextoCelda());
        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setPreferredSize(new Dimension(0, 40));
        cabecera.setDefaultRenderer(new EncabezadoCelda());
        tabla.getColumnModel().getColumn(7).setCellRenderer(new BotonCelda());
        tabla.getColumnModel().getColumn(4).setCellRenderer(new EstadoCelda());
        tabla.getColumnModel().getColumn(5).setCellRenderer(new TiempoCelda());
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int columna = tabla.columnAtPoint(e.getPoint());
                if (fila >= 0 && columna == 7
                        && "CANCELAR".equals(tabla.getValueAt(fila, 7))) {
                    cancelar(tabla.convertRowIndexToModel(fila));
                }
            }
        });
        tabla.getColumnModel().getColumn(0).setPreferredWidth(75);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(85);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(125);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(105);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(135);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(95);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(120);
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(LINEA_TABLA));
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.getVerticalScrollBar().setPreferredSize(new Dimension(11, 0));
        desplazamiento.getVerticalScrollBar().setUI(new BarraPedidosUI());
        desplazamiento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contenido.add(desplazamiento, BorderLayout.CENTER);
        tarjeta.add(contenido, BorderLayout.CENTER);
        add(tarjeta, BorderLayout.CENTER);
        reloj.start();
        cargar();
    }

    public void cargar() {
        new SwingWorker<List<PedidosDAO.Pedido>, Void>() {
            @Override protected List<PedidosDAO.Pedido> doInBackground() throws Exception { return new PedidosDAO().listar(); }
            @Override protected void done() {
                try { pedidos = get(); llenar(); }
                catch (Exception ex) { Utilidades.DialogosCajero.mensaje(PedidosPanel.this,
                        "No se pudieron cargar los pedidos", ex.getMessage(), JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void llenar() {
        modelo.setRowCount(0);
        for (PedidosDAO.Pedido pedido : pedidos) {
            modelo.addRow(new Object[]{"#" + pedido.orden(), hora(pedido.fecha()),
                servicio(pedido.servicio()), metodo(pedido.metodo()), estado(pedido), tiempo(pedido),
                "Q" + pedido.total().toPlainString(), accion(pedido)});
        }
    }

    private void actualizarTiempos() {
        for (int i = 0; i < pedidos.size() && i < modelo.getRowCount(); i++) {
            PedidosDAO.Pedido pedido = pedidos.get(i);
            modelo.setValueAt(estado(pedido), i, 4);
            modelo.setValueAt(tiempo(pedido), i, 5);
            modelo.setValueAt(accion(pedido), i, 7);
        }
    }

    private void cancelar(int fila) {
        if (fila < 0 || fila >= pedidos.size()) return;
        PedidosDAO.Pedido pedido = pedidos.get(fila);
        if (!Utilidades.DialogosCajero.confirmar(this, "Cancelar pedido",
                "¿Cancelar el pedido #" + pedido.orden() + "?\nSe devolverán sus existencias al inventario.")) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception { new PedidosDAO().cancelar(pedido.id()); return null; }
            @Override protected void done() {
                try { get(); cargar(); }
                catch (Exception ex) { Utilidades.DialogosCajero.mensaje(PedidosPanel.this,
                        "No se pudo cancelar", ex.getMessage(), JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private static String tiempo(PedidosDAO.Pedido p) {
        if ("CANCELADO".equals(p.estado())) return "Cancelado";
        if (p.listoDirecto()) return "Listo";
        long restante = Math.max(0, 300 - Math.max(0, Duration.between(p.fecha(), ahora()).getSeconds()));
        if (restante == 0) return "Listo";
        return String.format("%02d:%02d", restante / 60, restante % 60);
    }
    private static String estado(PedidosDAO.Pedido p) {
        if ("CANCELADO".equals(p.estado())) return "Cancelado";
        if (p.listoDirecto()) return "Listo";
        long transcurrido = Math.max(0, Duration.between(p.fecha(), ahora()).getSeconds());
        if (transcurrido < 30) return "En espera";
        return transcurrido < 300 ? "En preparación" : "Listo";
    }
    private static String hora(LocalDateTime valor) { return String.format("%02d:%02d", valor.getHour(), valor.getMinute()); }
    private static LocalDateTime ahora() { return LocalDateTime.now(ZoneId.of("America/Guatemala")); }
    private static String servicio(String valor) {
        if ("COMER_AQUI".equals(valor)) return "Comer aquí";
        if ("PARA_LLEVAR".equals(valor)) return "Para llevar";
        return valor == null ? "Sin servicio" : valor;
    }
    private static String metodo(String valor) {
        if ("TARJETA".equals(valor)) return "Tarjeta";
        if ("EFECTIVO".equals(valor)) return "Efectivo";
        return "Sin método";
    }
    private static String accion(PedidosDAO.Pedido pedido) {
        return "CANCELADO".equals(pedido.estado()) ? "CANCELADO" : "CANCELAR";
    }

    private static final class BotonActualizar extends JButton {
        BotonActualizar() {
            super("Actualizar");
            setFont(new Font("Arial", Font.BOLD, 13));
            setForeground(new Color(1, 20, 36));
            setBackground(new Color(255, 188, 13));
            setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? new Color(255, 201, 45) : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private final class BotonCelda extends JButton implements javax.swing.table.TableCellRenderer {
        BotonCelda() {
            setForeground(new Color(170, 30, 40));
            setBackground(new Color(255, 238, 239));
            setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
            setFocusPainted(false);
            setContentAreaFilled(false);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            setText(String.valueOf(v));
            boolean cancelado = "CANCELADO".equals(String.valueOf(v));
            setEnabled(!cancelado);
            setForeground(cancelado ? new Color(125, 130, 138) : new Color(170, 30, 40));
            setBackground(cancelado ? new Color(235, 237, 240) : new Color(255, 238, 239));
            return this;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(7, 6, getWidth() - 14, getHeight() - 12, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    private static final class TextoCelda extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s,
                boolean f, int r, int c) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            etiqueta.setForeground(AZUL);
            etiqueta.setBorder(new EmptyBorder(0, 12, 0, 8));
            etiqueta.setVerticalAlignment(SwingConstants.CENTER);
            return etiqueta;
        }
    }
    private static final class EncabezadoCelda extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s,
                boolean f, int r, int c) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
            etiqueta.setForeground(AZUL);
            etiqueta.setBackground(FONDO_CABECERA);
            etiqueta.setHorizontalAlignment(SwingConstants.LEFT);
            etiqueta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA_TABLA),
                    new EmptyBorder(0, 12, 0, 8)));
            return etiqueta;
        }
    }
    private static final class EstadoCelda extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
            etiqueta.setFont(new Font("Arial", Font.BOLD, 12));
            String texto = String.valueOf(v);
            etiqueta.setForeground("Listo".equals(texto) ? new Color(31, 116, 65)
                    : "Cancelado".equals(texto) ? new Color(180, 30, 40)
                    : "En espera".equals(texto) ? new Color(175, 110, 0) : new Color(30, 90, 150));
            return etiqueta;
        }
    }
    private static final class TiempoCelda extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
            etiqueta.setFont(new Font("Arial", Font.BOLD, 13));
            etiqueta.setForeground("Listo".equals(String.valueOf(v)) ? new Color(31, 116, 65) : new Color(1, 20, 36));
            return etiqueta;
        }
    }

    private static final class BarraPedidosUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(180, 190, 202);
            trackColor = new Color(242, 244, 247);
        }
        @Override protected JButton createDecreaseButton(int orientation) { return botonVacio(); }
        @Override protected JButton createIncreaseButton(int orientation) { return botonVacio(); }
        private JButton botonVacio() {
            JButton boton = new JButton();
            boton.setPreferredSize(new Dimension(0, 0));
            boton.setMinimumSize(new Dimension(0, 0));
            boton.setMaximumSize(new Dimension(0, 0));
            return boton;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !c.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y + 2, Math.max(1, r.width - 4), Math.max(1, r.height - 4), 8, 8);
            g2.dispose();
        }
    }
}
