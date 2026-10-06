package GUI_ADMINISTRADOR;

import CRUD.DashboardCRUD;
import CRUD.DashboardCRUD.Alerta;
import CRUD.DashboardCRUD.Pedido;
import CRUD.DashboardCRUD.ProductoVendido;
import CRUD.DashboardCRUD.Resumen;
import Componentes.PanelFlotante;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import Utilidades.TemaAdmin;

/** Tres tarjetas del Dashboard construidas por código; no usa el Designer. */
@SuppressWarnings("serial")
public final class DashboardSecciones extends JPanel {

    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 229, 235);
    private static final Color LINEA_TABLA = new Color(216, 224, 234);
    private static final Color FONDO_CABECERA = new Color(247, 249, 252);
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DashboardCRUD crud = new DashboardCRUD();
    private final TemaAdmin tema = new TemaAdmin();
    private final Consumer<Resumen> recibirResumen;
    private final JTable tablaPedidos;
    private final JTable tablaProductos;
    private final JTable tablaAlertas;
    private final JLabel mensaje = new JLabel(" ");
    private List<Alerta> alertasActuales = List.of();
    private boolean cargando;

    public DashboardSecciones(Consumer<Resumen> recibirResumen) {
        this.recibirResumen = recibirResumen;
        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        setPreferredSize(new Dimension(1470, 650));

        tablaPedidos = crearTablaPedidos();
        tablaProductos = crearTablaProductos();
        tablaAlertas = crearTablaAlertas(() -> alertasActuales);

        add(tarjeta("Pedidos recientes", "/Imagenes/lista_icono.png",
                "Ver todos →", tablaPedidos,
                () -> abrirTodos("Todos los pedidos", () -> crud.obtenerPedidos(0),
                        this::tablaCompletaPedidos)),
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, 300));
        add(tarjeta("Productos más vendidos", "/Imagenes/hamburguesa.png",
                "Ver todos →", tablaProductos,
                () -> abrirTodos("Productos más vendidos",
                        () -> crud.obtenerProductosVendidos(0),
                        this::tablaCompletaProductos)),
                new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, 730, 300));
        add(tarjeta("Alertas de inventario", "/Imagenes/advertencia_icono.png",
                "Ver todas →", tablaAlertas,
                () -> abrirTodos("Todas las alertas", crud::obtenerAlertas,
                        this::tablaCompletaAlertas)),
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 320, 1470, 300));

        mensaje.setFont(tema.regular(12f));
        mensaje.setForeground(ROJO);
        add(mensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 626, 1450, 20));
    }

    private PanelFlotante tarjeta(String titulo, String rutaIcono, String textoBoton,
            JTable tabla, Runnable accion) {
        PanelFlotante panel = new PanelFlotante();
        panel.setColorFondo(Color.WHITE);
        panel.setColorBorde(BORDE);
        panel.setRadio(18);
        panel.setLayout(new BorderLayout());

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(12, 17, 9, 17));
        JPanel tituloConIcono = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        tituloConIcono.setOpaque(false);
        JLabel icono = crearIconoCabecera(rutaIcono);
        if (icono != null) {
            tituloConIcono.add(icono);
        }
        JLabel encabezado = new JLabel(titulo);
        encabezado.setFont(tema.negrita(16f));
        encabezado.setForeground(AZUL);
        tituloConIcono.add(encabezado);
        barra.add(tituloConIcono, BorderLayout.WEST);

        JButton verTodos = new JButton(textoBoton);
        verTodos.setFont(tema.media(12f));
        verTodos.setForeground(ROJO);
        verTodos.setContentAreaFilled(false);
        verTodos.setBorderPainted(false);
        verTodos.setFocusPainted(false);
        verTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        verTodos.addActionListener(e -> accion.run());
        barra.add(verTodos, BorderLayout.EAST);
        panel.add(barra, BorderLayout.NORTH);

        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(LINEA_TABLA));
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.setBorder(new EmptyBorder(0, 12, 14, 12));
        cuerpo.add(desplazamiento, BorderLayout.CENTER);
        panel.add(cuerpo, BorderLayout.CENTER);
        return panel;
    }

    /** Ajusta el dibujo visible dentro de 28x28, sin estirar la imagen. */
    private JLabel crearIconoCabecera(String ruta) {
        java.net.URL recurso = getClass().getResource(ruta);
        if (recurso == null) {
            System.err.println("No se encontró el ícono del Dashboard: " + ruta);
            return null;
        }
        javax.swing.ImageIcon original = new javax.swing.ImageIcon(recurso);
        if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
            System.err.println("No se pudo leer el ícono del Dashboard: " + ruta);
            return null;
        }
        int origenAncho = original.getIconWidth();
        int origenAlto = original.getIconHeight();
        java.awt.image.BufferedImage fuente = new java.awt.image.BufferedImage(
                origenAncho, origenAlto, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D copia = fuente.createGraphics();
        try {
            copia.drawImage(original.getImage(), 0, 0, null);
        } finally {
            copia.dispose();
        }

        // Muchos PNG tienen márgenes transparentes: se mide solo el dibujo.
        int izquierda = origenAncho, arriba = origenAlto, derecha = -1, abajo = -1;
        for (int y = 0; y < origenAlto; y++) {
            for (int x = 0; x < origenAncho; x++) {
                if ((fuente.getRGB(x, y) >>> 24) >= 16) {
                    izquierda = Math.min(izquierda, x);
                    derecha = Math.max(derecha, x);
                    arriba = Math.min(arriba, y);
                    abajo = Math.max(abajo, y);
                }
            }
        }
        if (derecha < izquierda) {
            System.err.println("El ícono del Dashboard está vacío: " + ruta);
            return null;
        }
        int dibujoAncho = derecha - izquierda + 1;
        int dibujoAlto = abajo - arriba + 1;
        double escala = Math.min(22.0 / dibujoAncho, 22.0 / dibujoAlto);
        int ancho = Math.max(1, (int) Math.round(dibujoAncho * escala));
        int alto = Math.max(1, (int) Math.round(dibujoAlto * escala));
        java.awt.image.BufferedImage lienzo = new java.awt.image.BufferedImage(
                28, 28, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D grafico = lienzo.createGraphics();
        try {
            grafico.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            grafico.drawImage(fuente,
                    (28 - ancho) / 2, (28 - alto) / 2,
                    (28 - ancho) / 2 + ancho, (28 - alto) / 2 + alto,
                    izquierda, arriba, derecha + 1, abajo + 1, null);
        } finally {
            grafico.dispose();
        }
        JLabel icono = new JLabel(new javax.swing.ImageIcon(lienzo));
        icono.setPreferredSize(new Dimension(28, 28));
        icono.setMinimumSize(new Dimension(28, 28));
        icono.setMaximumSize(new Dimension(28, 28));
        return icono;
    }

    private JTable tabla(String... encabezados) {
        DefaultTableModel modelo = new DefaultTableModel(encabezados, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setFont(tema.regular(13f));
        tabla.setForeground(AZUL);
        tabla.setBackground(Color.WHITE);
        tabla.setRowHeight(40);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(LINEA_TABLA);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(new Color(255, 246, 219));
        tabla.setSelectionForeground(AZUL);
        tabla.setAutoCreateRowSorter(false);
        DefaultTableCellRenderer textoCelda = new DefaultTableCellRenderer();
        textoCelda.setBorder(new EmptyBorder(0, 12, 0, 8));
        tabla.setDefaultRenderer(Object.class, textoCelda);

        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setPreferredSize(new Dimension(0, 40));
        cabecera.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object valor, boolean seleccionada,
                    boolean foco, int fila, int columna) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(
                        t, valor, seleccionada, foco, fila, columna);
                celda.setFont(tema.negrita(13f));
                celda.setForeground(AZUL);
                celda.setBackground(FONDO_CABECERA);
                celda.setHorizontalAlignment(SwingConstants.LEFT);
                celda.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA_TABLA),
                        new EmptyBorder(0, 12, 0, 8)));
                return celda;
            }
        });
        return tabla;
    }

    private JTable crearTablaPedidos() {
        JTable tabla = tabla("Orden", "Cajero", "Fecha / hora",
                "Tipo de servicio", "Estado", "Total");
        anchos(tabla, 68, 140, 110, 125, 92, 85);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new EstadoCelda());
        return tabla;
    }

    private JTable crearTablaProductos() {
        JTable tabla = tabla("Producto", "Categoría", "Unidades vendidas", "Estado");
        anchos(tabla, 240, 170, 150, 105);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new EstadoCelda());
        return tabla;
    }

    private JTable crearTablaAlertas(Supplier<List<Alerta>> obtenerFilas) {
        JTable tabla = tabla("Tipo", "Descripción", "Detalles",
                "Último movimiento", "Acción");
        anchos(tabla, 135, 230, 625, 170, 160);
        tabla.getColumnModel().getColumn(0).setCellRenderer(new EstadoCelda());
        tabla.getColumnModel().getColumn(4).setCellRenderer(new AccionCelda());
        tabla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                int fila = tabla.rowAtPoint(evento.getPoint());
                int columna = tabla.columnAtPoint(evento.getPoint());
                List<Alerta> filas = obtenerFilas.get();
                if (columna == 4 && fila >= 0 && fila < filas.size()) {
                    mostrarAlerta(filas.get(fila));
                }
            }
        });
        return tabla;
    }

    private static void anchos(JTable tabla, int... valores) {
        for (int i = 0; i < valores.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(valores[i]);
        }
    }

    /** Ejecutar al abrir o volver a mostrar la pantalla. La consulta no bloquea Swing. */
    public void recargar() {
        if (cargando) {
            return;
        }
        cargando = true;
        mensaje.setText("Cargando Dashboard...");
        mensaje.setForeground(SECUNDARIO);
        new SwingWorker<DashboardCRUD.Datos, Void>() {
            @Override
            protected DashboardCRUD.Datos doInBackground() throws Exception {
                return crud.cargar();
            }

            @Override
            protected void done() {
                cargando = false;
                try {
                    DashboardCRUD.Datos datos = get();
                    recibirResumen.accept(datos.resumen());
                    llenarPedidos(tablaPedidos, datos.pedidos());
                    llenarProductos(tablaProductos, datos.productos());
                    alertasActuales = datos.alertas().stream().limit(5).toList();
                    llenarAlertas(tablaAlertas, alertasActuales);
                    mensaje.setText(" ");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    mostrarError(ex);
                } catch (ExecutionException ex) {
                    mostrarError(ex.getCause() == null ? ex : ex.getCause());
                }
            }
        }.execute();
    }

    private void mostrarError(Throwable ex) {
        mensaje.setForeground(ROJO);
        mensaje.setText("No fue posible cargar el Dashboard: " + ex.getMessage());
    }

    private static void limpiar(JTable tabla) {
        ((DefaultTableModel) tabla.getModel()).setRowCount(0);
    }

    private static void llenarPedidos(JTable tabla, List<Pedido> pedidos) {
        limpiar(tabla);
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        for (Pedido p : pedidos) {
            modelo.addRow(new Object[]{String.format("#%04d", p.numeroOrden()),
                p.cajero(), fecha(p.fechaHora()), servicio(p.tipoServicio()),
                estadoPedido(p.estado()), moneda(p.total())});
        }
    }

    private static void llenarProductos(JTable tabla, List<ProductoVendido> productos) {
        limpiar(tabla);
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        for (ProductoVendido p : productos) {
            String estado = !p.activo() ? "Inactivo"
                    : p.disponible() ? "Disponible" : "Agotado";
            modelo.addRow(new Object[]{p.nombre(), p.categoria(),
                p.unidades(), estado});
        }
    }

    private static void llenarAlertas(JTable tabla, List<Alerta> alertas) {
        limpiar(tabla);
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        for (Alerta a : alertas) {
            modelo.addRow(new Object[]{a.nivel(),
                a.esProducto() ? "Producto con inventario bajo"
                    : "Ingrediente con inventario bajo",
                a.detalle(), a.ultimoMovimiento() == null
                    ? "—" : fecha(a.ultimoMovimiento()),
                a.esProducto() ? "Ver producto" : "Ver ingrediente"});
        }
    }

    private JTable tablaCompletaPedidos(List<Pedido> filas) {
        JTable tabla = crearTablaPedidos();
        llenarPedidos(tabla, filas);
        return tabla;
    }

    private JTable tablaCompletaProductos(List<ProductoVendido> filas) {
        JTable tabla = crearTablaProductos();
        llenarProductos(tabla, filas);
        return tabla;
    }

    private JTable tablaCompletaAlertas(List<Alerta> filas) {
        JTable tabla = crearTablaAlertas(() -> filas);
        llenarAlertas(tabla, filas);
        return tabla;
    }

    private <T> void abrirTodos(String titulo, Callable<List<T>> consultar,
            Function<List<T>, JTable> construirTabla) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<List<T>, Void>() {
            @Override
            protected List<T> doInBackground() throws Exception {
                return consultar.call();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    Window propietario = SwingUtilities.getWindowAncestor(
                            DashboardSecciones.this);
                    JDialog dialogo = new JDialog(propietario, titulo,
                            Dialog.ModalityType.APPLICATION_MODAL);
                    JPanel contenido = new JPanel(new BorderLayout());
                    contenido.setBackground(Color.WHITE);
                    contenido.setBorder(new EmptyBorder(15, 15, 15, 15));
                    contenido.add(new JScrollPane(construirTabla.apply(get())),
                            BorderLayout.CENTER);
                    dialogo.setContentPane(contenido);
                    dialogo.setSize(1100, 560);
                    dialogo.setLocationRelativeTo(propietario);
                    dialogo.setVisible(true);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(DashboardSecciones.this,
                            "No fue posible cargar los datos: "
                            + ex.getCause().getMessage(), titulo,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void mostrarAlerta(Alerta alerta) {
        String tipo = alerta.esProducto() ? "Producto" : "Ingrediente";
        JOptionPane.showMessageDialog(this,
                tipo + " #" + String.format("%04d", alerta.id())
                + "\n" + alerta.nombre()
                + "\nStock actual: " + numero(alerta.stockActual()) + " "
                + alerta.unidad()
                + "\nStock mínimo: " + numero(alerta.stockMinimo()) + " "
                + alerta.unidad(), "Alerta de inventario",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String numero(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }

    private static String fecha(LocalDateTime valor) {
        return valor == null ? "—" : valor.format(FECHA);
    }

    public static String moneda(BigDecimal valor) {
        DecimalFormatSymbols simbolos = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat formato = new DecimalFormat("'Q'#,##0.00", simbolos);
        return formato.format(valor == null ? BigDecimal.ZERO : valor);
    }

    private static String servicio(String valor) {
        return switch (valor) {
            case "COMER_AQUI" -> "Comer aquí";
            case "PARA_LLEVAR" -> "Para llevar";
            case "A_DOMICILIO" -> "A domicilio";
            default -> valor;
        };
    }

    private static String estadoPedido(String valor) {
        return switch (valor) {
            case "PAGADO" -> "Pagado";
            case "PENDIENTE" -> "Pendiente";
            case "CANCELADO" -> "Cancelado";
            default -> valor;
        };
    }

    private final class EstadoCelda extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionada, boolean foco,
                int fila, int columna) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    tabla, valor, seleccionada, foco, fila, columna);
            etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
            if (!seleccionada) {
                String texto = String.valueOf(valor);
                if (texto.equals("Pagado") || texto.equals("Disponible")) {
                    etiqueta.setForeground(new Color(20, 130, 60));
                    etiqueta.setBackground(new Color(232, 247, 236));
                } else if (texto.equals("Pendiente")
                        || texto.equals("Stock bajo")) {
                    etiqueta.setForeground(new Color(172, 105, 0));
                    etiqueta.setBackground(new Color(255, 246, 221));
                } else {
                    etiqueta.setForeground(ROJO);
                    etiqueta.setBackground(new Color(255, 236, 237));
                }
            }
            return etiqueta;
        }
    }

    private final class AccionCelda extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionada, boolean foco,
                int fila, int columna) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    tabla, valor, seleccionada, foco, fila, columna);
            etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
            etiqueta.setForeground(ROJO);
            etiqueta.setBackground(Color.WHITE);
            etiqueta.setBorder(BorderFactory.createLineBorder(ROJO));
            return etiqueta;
        }
    }
}
