package GUI_CAJERO;

import CRUD.ProductoCRUD;
import Componentes.BotonRedondeado;
import Componentes.TablaAdministrativa;
import DAO.PagoDAO;
import Modelos.*;
import Utilidades.HorarioMenu;
import Utilidades.SesionUsuario;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** Carrito de una caja; conserva la operación pendiente ante errores de conexión. */
public class PedidoPanel extends JPanel {
    private static final Color AMARILLO = new Color(255, 188, 13);
    private static final Color ROJO = new Color(196, 30, 42);
    private static final Color TINTA = new Color(20, 27, 35);
    private final Map<Integer, LineaPedido> lineas = new LinkedHashMap<>();
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Producto", "Cant.", "Importe"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final TablaAdministrativa tabla = new TablaAdministrativa() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getRowCount() == 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int y = Math.max(48, getHeight() / 2 - 10);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.setColor(TINTA);
                String titulo = "El pedido está vacío";
                g2.drawString(titulo, (getWidth() - g2.getFontMetrics().stringWidth(titulo)) / 2, y);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(new Color(105, 109, 115));
                String ayuda = "Agrega productos desde el menú";
                g2.drawString(ayuda, (getWidth() - g2.getFontMetrics().stringWidth(ayuda)) / 2, y + 24);
                g2.dispose();
            }
        }
    };
    private final JLabel total = new JLabel("Total: Q0.00");
    private final JButton cobrar = boton("Continuar al pago", AMARILLO, TINTA);
    private final JButton mas = boton("+", AMARILLO, TINTA), menos = boton("−", new Color(255, 246, 217), TINTA), quitar = boton("Quitar", new Color(255, 237, 238), ROJO);
    private final JButton cancelar = boton("Cancelar pedido", ROJO, Color.WHITE), ultimo = boton("Último comprobante", new Color(245, 246, 248), TINTA);
    private boolean ocupado;
    private SolicitudPago pendiente;
    private String comprobante;
    private final Utilidades.PagoPendienteStore respaldo = new Utilidades.PagoPendienteStore(SesionUsuario.getIdUsuario());
    private boolean recuperacionFallida;

    public PedidoPanel() {
        super(new BorderLayout(8, 16));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
        JLabel titulo = new JLabel("Pedido");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TINTA);
        titulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, AMARILLO),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)));
        add(titulo, BorderLayout.NORTH);

        tabla.setModel(modelo);
        tabla.setRowHeight(48);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setColorCabecera(AMARILLO);
        tabla.setColorTextoCabecera(TINTA);
        tabla.setFuenteCabecera(new Font("SansSerif", Font.BOLD, 12));
        tabla.setAltoCabecera(42);
        tabla.setColorFilas(Color.WHITE);
        tabla.setFilasAlternadas(true);
        tabla.setColorFilaAlterna(new Color(255, 251, 239));
        tabla.setColorSeleccion(new Color(255, 235, 170));
        tabla.setColorLineas(new Color(241, 236, 220));
        tabla.setPaddingHorizontal(10);
        tabla.setColumnasCentradas("1");
        tabla.setColumnasDerecha("2");
        tabla.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(52);
        tabla.getColumnModel().getColumn(1).setMaxWidth(65);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(2).setMinWidth(95);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setPreferredSize(new Dimension(320, 180));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(238, 231, 211)));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(scroll);
        JPanel cantidades = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cantidades.setOpaque(false);
        menos.setPreferredSize(new Dimension(42, 36));
        menos.setMargin(new Insets(0, 0, 0, 0));
        mas.setMargin(new Insets(0, 0, 0, 0));
        mas.setPreferredSize(new Dimension(42, 36));
        quitar.setPreferredSize(new Dimension(92, 36));
        menos.setFont(new Font("SansSerif", Font.BOLD, 20));
        mas.setFont(new Font("SansSerif", Font.BOLD, 20));
        menos.setToolTipText("Reducir cantidad del producto seleccionado");
        mas.setToolTipText("Aumentar cantidad del producto seleccionado");
        quitar.setToolTipText("Quitar el producto seleccionado");
        cantidades.add(menos); cantidades.add(mas); cantidades.add(quitar);
        centro.add(cantidades, BorderLayout.SOUTH);
        add(centro);

        JPanel pie = new JPanel(new GridLayout(0, 1, 0, 10));
        pie.setOpaque(false);
        pie.setPreferredSize(new Dimension(320, 202));
        total.setFont(new Font("SansSerif", Font.BOLD, 23));
        total.setForeground(TINTA);
        total.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(238, 231, 211)));
        pie.add(total); pie.add(cobrar); pie.add(cancelar); pie.add(ultimo);
        add(pie, BorderLayout.SOUTH);
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
        try {
            pendiente = respaldo.cargar();
            if (pendiente != null) {
                if (pendiente.usuario() != SesionUsuario.getIdUsuario()) throw new java.io.IOException("El cobro pendiente pertenece a otro usuario.");
                for (LineaPedido l : pendiente.lineas()) lineas.put(l.idProducto(), l);
            }
        } catch (java.io.IOException ex) {
            recuperacionFallida = true;
            SwingUtilities.invokeLater(() -> aviso(ex.getMessage()));
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
        b.setRadio(18);
        b.setFocusPainted(true);
        b.setRolloverEnabled(true);
        b.setMargin(new Insets(8, 12, 8, 12));
        b.setPreferredSize(new Dimension(220, 44));
        return b;
    }
    public void agregar(int id) {
        if (bloqueado()) { aviso("Termine o reintente el cobro pendiente antes de modificar el pedido."); return; }
        ocupado = true; actualizar();
        new SwingWorker<Producto, Void>() {
            @Override protected Producto doInBackground() throws Exception { return new ProductoCRUD().obtenerPorId(id); }
            @Override protected void done() {
                try {
                    Producto p = get();
                    if (p == null || !p.isActivo() || !HorarioMenu.estaDisponible(p.getDisponibilidadMenu(), LocalTime.now())) throw new IllegalArgumentException("El producto no está disponible ahora.");
                    LineaPedido anterior = lineas.get(id);
                    int cantidad = anterior == null ? 1 : anterior.cantidad() + 1;
                    if (cantidad > p.getStockActual()) throw new IllegalArgumentException("No hay suficientes existencias de " + p.getNombre() + ".");
                    lineas.put(id, new LineaPedido(id, p.getNombre(), p.getPrecioBase(), cantidad));
                } catch (Exception ex) { aviso(mensaje(ex)); }
                finally { ocupado = false; actualizar(); }
            }
        }.execute();
    }

    private void cambiar(int incremento) {
        if (bloqueado() || tabla.getSelectedRow() < 0) return;
        LineaPedido l = new ArrayList<>(lineas.values()).get(tabla.getSelectedRow());
        if (incremento > 0) { agregar(l.idProducto()); return; }
        int n = l.cantidad() + incremento;
        if (n <= 0) lineas.remove(l.idProducto());
        else lineas.put(l.idProducto(), new LineaPedido(l.idProducto(), l.nombre(), l.precio(), n));
        actualizar();
    }

    private boolean bloqueado() { return ocupado || pendiente != null || recuperacionFallida; }
    private BigDecimal suma() { return lineas.values().stream().map(LineaPedido::subtotal).reduce(new BigDecimal("0.00"), BigDecimal::add); }
    private void actualizar() {
        int seleccion = tabla.getSelectedRow(); modelo.setRowCount(0);
        for (LineaPedido l : lineas.values()) modelo.addRow(new Object[]{l.nombre(), l.cantidad(), "Q" + l.subtotal().toPlainString()});
        if (seleccion >= 0 && seleccion < modelo.getRowCount()) tabla.setRowSelectionInterval(seleccion, seleccion);
        total.setText("Total: Q" + suma().toPlainString());
        cobrar.setText(pendiente == null ? "Continuar al pago" : "Reintentar cobro pendiente");
        cobrar.setEnabled(!ocupado && !recuperacionFallida && !lineas.isEmpty());
        for (JButton b : new JButton[]{mas, menos, quitar}) b.setEnabled(!bloqueado() && !lineas.isEmpty());
        cancelar.setEnabled(true);
        ultimo.setEnabled(comprobante != null);
    }

    private void abrirPago() {
        if (ocupado || recuperacionFallida || lineas.isEmpty()) return;
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Cobrar pedido", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel campos = new JPanel(new GridLayout(0, 1, 5, 8)); campos.setBorder(BorderFactory.createEmptyBorder(20,20,20,20)); campos.setBackground(Color.WHITE);
        JComboBox<String> servicio = new JComboBox<>(new String[]{"Comer aquí", "Para llevar"});
        JComboBox<String> metodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta (terminal externa)"});
        JTextField recibido = new JTextField(suma().toPlainString()), referencia = new JTextField();
        JCheckBox confirmado = new JCheckBox("Pago aprobado en la terminal externa");
        JLabel cambio = new JLabel();
        JButton guardar = boton("Confirmar pago", AMARILLO, TINTA), volver = boton("Volver al pedido", new Color(245, 246, 248), TINTA);
        campos.add(new JLabel("Total a pagar: Q" + suma().toPlainString())); campos.add(servicio); campos.add(metodo);
        campos.add(new JLabel("Monto recibido")); campos.add(recibido); campos.add(cambio);
        campos.add(new JLabel("Referencia de terminal (sin datos de tarjeta)")); campos.add(referencia); campos.add(confirmado);
        campos.add(guardar); campos.add(volver);
        Runnable refrescar = () -> {
            boolean tarjeta = metodo.getSelectedIndex() == 1;
            recibido.setEnabled(!tarjeta && pendiente == null); referencia.setEnabled(tarjeta && pendiente == null); confirmado.setEnabled(tarjeta && pendiente == null);
            try { BigDecimal valor = monto(recibido.getText()).subtract(suma()); cambio.setText(valor.signum() < 0 ? "Faltan: Q" + valor.negate() : "Cambio: Q" + valor); }
            catch (IllegalArgumentException ex) { cambio.setText("Ingrese un monto con hasta dos decimales."); }
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
        dialogo.setContentPane(campos); dialogo.pack(); dialogo.setMinimumSize(new Dimension(440, dialogo.getHeight()));
        dialogo.setLocationRelativeTo(this); dialogo.setVisible(true);
    }

    public static BigDecimal monto(String texto) {
        if (texto == null || !texto.trim().matches("[0-9]{1,8}([.,][0-9]{1,2})?")) throw new IllegalArgumentException("Monto inválido.");
        return new BigDecimal(texto.trim().replace(',', '.')).setScale(2);
    }
    private void mostrarComprobante() {
        if (comprobante == null) return;
        JTextArea texto = new JTextArea(comprobante, 20, 38); texto.setEditable(false); texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        Object[] opciones = {"Cerrar", "Imprimir"};
        if (JOptionPane.showOptionDialog(this, new JScrollPane(texto), "Comprobante", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]) == 1) {
            try { texto.print(); } catch (java.awt.print.PrinterException ex) { aviso("No se pudo imprimir. Puede reabrir el comprobante. " + ex.getMessage()); }
        }
    }
    private static String mensaje(Exception ex) {
        Throwable causa = ex instanceof java.util.concurrent.ExecutionException ? ex.getCause() : ex;
        return causa.getMessage() == null ? "No se pudo completar la operación." : causa.getMessage();
    }
    private void aviso(String mensaje) { JOptionPane.showMessageDialog(this, mensaje, "Pedido", JOptionPane.WARNING_MESSAGE); }
}
