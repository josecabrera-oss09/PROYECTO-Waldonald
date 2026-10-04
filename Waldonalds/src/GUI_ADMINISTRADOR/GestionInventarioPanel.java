/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI_ADMINISTRADOR;

import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
import Componentes.PanelFlotante;
import DAO.InventarioDAO;
import Modelos.ArticuloInventario;
import Modelos.MovimientoInventario;
import Modelos.PaginaInventario;
import Utilidades.IconosUsuarios;
import Utilidades.TemaAdmin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Humberto Alvarado
 */
public class GestionInventarioPanel extends javax.swing.JPanel {

    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final Color BORDE = new Color(225, 230, 237);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final int POR_PAGINA = 10;
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TemaAdmin tema = new TemaAdmin();
    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final ModeloTabla modeloTabla = new ModeloTabla();
    private final JButton[] botonesPagina = new JButton[3];
    private final Timer temporizadorBusqueda;
    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int secuenciaCarga;
    private String tipoActual;
    private String estadoActual;
    private boolean errorConexionMostrado;

    /**
     * Creates new form GestionInventarioPanel
     */
    public GestionInventarioPanel() {
        initComponents();
        temporizadorBusqueda = new Timer(350, e -> {
            paginaActual = 1;
            cargarPagina();
        });
        temporizadorBusqueda.setRepeats(false);
        botonesPagina[0] = botonPagina1;
        botonesPagina[1] = botonPagina2;
        botonesPagina[2] = botonPagina3;
        prepararVista();
        configurarFiltros();
        configurarTabla();
        configurarPaginacion();
        configurarEventos();
        cargarPagina();
    }

    private void prepararVista() {
        labelTitulo3.setFont(tema.negrita(42f));
        labelTitulo2.setFont(tema.regular(16f));
        botonAgregarProducto.setFont(tema.negrita(14f));
        botonRegistrarSalida.setFont(tema.negrita(14f));
        botonMovimientos.setFont(tema.negrita(14f));
        botonExportar.setFont(tema.negrita(14f));
        botonExportar.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.EXPORTAR, ROJO, 19));
        botonExportar.setIconTextGap(9);
        campoBusqueda.setFont(tema.regular(14f));
        botonLimpiarFiltros.setFont(tema.negrita(14f));
        botonLimpiarFiltros.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.FILTRO, ROJO, 19));
        botonLimpiarFiltros.setIconTextGap(9);
        scrollUsuarios.setBorder(BorderFactory.createEmptyBorder());
        scrollUsuarios.getViewport().setBackground(Color.WHITE);
        etiquetaRango.setFont(tema.regular(12f));
        etiquetaRango.setForeground(SECUNDARIO);
    }

    private BotonRedondeado crearBotonSuperior(String texto, Color color) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText(texto);
        boton.setFont(tema.negrita(14f));
        boton.setForeground(color);
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(color);
        boton.setGrosorBorde(1.3f);
        boton.setRadio(13);
        return boton;
    }

    private void configurarFiltros() {
        filtroCategoria.setText("Todos los tipos");
        filtroCategoria.setTextoDesplegable(
                "Todos los tipos;Ingredientes;Productos directos");
        prepararSelector(filtroCategoria);
        filtroCategoria.addMenuOpcionListener(e -> {
            filtroCategoria.setText(e.getActionCommand());
            tipoActual = switch (e.getActionCommand()) {
                case "Ingredientes" -> "INGREDIENTE";
                case "Productos directos" -> "PRODUCTO";
                default -> null;
            };
            paginaActual = 1;
            cargarPagina();
        });

        filtroEstado.setText("Todos los estados");
        filtroEstado.setTextoDesplegable(
                "Todos los estados;Disponible;Stock bajo;Agotado;Inactivo");
        prepararSelector(filtroEstado);
        filtroEstado.addMenuOpcionListener(e -> {
            filtroEstado.setText(e.getActionCommand());
            estadoActual = switch (e.getActionCommand()) {
                case "Disponible" -> "DISPONIBLE";
                case "Stock bajo" -> "BAJO";
                case "Agotado" -> "AGOTADO";
                case "Inactivo" -> "INACTIVO";
                default -> null;
            };
            paginaActual = 1;
            cargarPagina();
        });

        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
            @Override public void removeUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
            @Override public void changedUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
        });
        botonLimpiarFiltros.addActionListener(e -> {
            temporizadorBusqueda.stop();
            campoBusqueda.setText("");
            temporizadorBusqueda.stop();
            tipoActual = null;
            estadoActual = null;
            filtroCategoria.setText("Todos los tipos");
            filtroEstado.setText("Todos los estados");
            paginaActual = 1;
            cargarPagina();
        });
    }

    private void prepararSelector(BotonDesplegable boton) {
        boton.setFont(tema.regular(14f));
        boton.setForeground(AZUL);
        boton.setColorFondo(Color.WHITE);
        boton.setColorHover(new Color(248, 249, 251));
        boton.setColorDesplegado(new Color(255, 247, 222));
        boton.setColorTextoOpcion(AZUL);
        boton.setColorBordeMenu(BORDE);
        boton.setAnchoMenu(280);
        boton.setAltoOpcion(42);
    }

    private void configurarTabla() {
        tablaProductos.setModel(modeloTabla);
        tablaProductos.aplicarEstilo();
        tablaProductos.setRowHeight(53);
        tablaProductos.setAutoCreateRowSorter(false);
        tablaProductos.setColumnasCentradas("0,2,4,5,6,8,9");
        tablaProductos.getColumnModel().getColumn(8)
                .setCellRenderer(new RenderEstado());
        tablaProductos.getColumnModel().getColumn(9)
                .setCellRenderer(new RenderAccion());
        tablaProductos.getColumnModel().getColumn(9)
                .setCellEditor(new EditorAccion());
        int[] anchos = {78, 220, 128, 125, 82, 95, 95, 160, 105, 125};
        for (int i = 0; i < anchos.length; i++) {
            tablaProductos.getColumnModel().getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }
    }

    private void configurarPaginacion() {
        prepararBotonPagina(botonAnterior, IconosUsuarios.Tipo.ANTERIOR);
        botonAnterior.addActionListener(e -> cambiarPagina(paginaActual - 1));
        for (JButton boton : botonesPagina) {
            prepararBotonPagina(boton, null);
            boton.addActionListener(e -> {
                Object numero = boton.getClientProperty("pagina");
                if (numero instanceof Integer pagina) cambiarPagina(pagina);
            });
        }
        prepararBotonPagina(botonSiguiente, IconosUsuarios.Tipo.SIGUIENTE);
        botonSiguiente.addActionListener(e -> cambiarPagina(paginaActual + 1));
    }

    private void prepararBotonPagina(JButton boton,
            IconosUsuarios.Tipo icono) {
        if (boton instanceof BotonRedondeado redondeado) {
            redondeado.setDegradado(false);
            redondeado.setColorInicio(Color.WHITE);
            redondeado.setColorFinal(Color.WHITE);
            redondeado.setColorBorde(BORDE);
            redondeado.setGrosorBorde(1f);
            redondeado.setRadio(10);
        }
        boton.setFont(tema.media(13f));
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boton.setIconTextGap(0);
        if (icono != null) {
            boton.setText("");
            boton.setIcon(IconosUsuarios.crear(icono, AZUL, 16));
        }
    }

    private void configurarEventos() {
        botonAgregarProducto.addActionListener(e ->
                abrirMovimientoSeleccionado("ENTRADA"));
        botonRegistrarSalida.addActionListener(e ->
                abrirMovimientoSeleccionado("SALIDA"));
        botonMovimientos.addActionListener(e ->
                abrirMovimientoSeleccionado("AJUSTE"));
        botonExportar.addActionListener(e -> exportarInventario());
    }

    private void cargarPagina() {
        final int solicitud = ++secuenciaCarga;
        final int paginaSolicitada = paginaActual;
        final String busqueda = campoBusqueda.getText().trim();
        final String tipo = tipoActual;
        final String estado = estadoActual;
        tablaProductos.setEnabled(false);
        etiquetaRango.setText("Cargando inventario...");
        new SwingWorker<PaginaInventario, Void>() {
            @Override protected PaginaInventario doInBackground()
                    throws SQLException {
                return inventarioDAO.listarPagina(busqueda, tipo, estado,
                        paginaSolicitada, POR_PAGINA);
            }

            @Override protected void done() {
                if (solicitud != secuenciaCarga) return;
                try {
                    PaginaInventario datos = get();
                    totalPaginas = Math.max(1, (datos.totalRegistros()
                            + POR_PAGINA - 1) / POR_PAGINA);
                    if (paginaActual > totalPaginas) {
                        paginaActual = totalPaginas;
                        cargarPagina();
                        return;
                    }
                    modeloTabla.setArticulos(datos.articulos());
                    actualizarPaginacion(datos.totalRegistros());
                    errorConexionMostrado = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    modeloTabla.setArticulos(List.of());
                    actualizarPaginacion(0);
                    mostrarError("cargar el inventario", ex.getCause());
                } finally {
                    tablaProductos.setEnabled(true);
                }
            }
        }.execute();
    }

    private void actualizarPaginacion(int total) {
        if (total == 0) {
            etiquetaRango.setText("No se encontraron artículos");
        } else {
            int inicio = (paginaActual - 1) * POR_PAGINA + 1;
            int fin = Math.min(inicio + POR_PAGINA - 1, total);
            etiquetaRango.setText(String.format(
                    "Mostrando %d–%d de %d artículos", inicio, fin, total));
        }
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        int inicio = Math.max(1, Math.min(paginaActual - 1,
                totalPaginas - 2));
        for (int i = 0; i < botonesPagina.length; i++) {
            JButton boton = botonesPagina[i];
            int pagina = inicio + i;
            boton.setVisible(pagina <= totalPaginas);
            if (pagina > totalPaginas) continue;
            boton.putClientProperty("pagina", pagina);
            boton.setText(String.valueOf(pagina));
            boolean seleccionada = pagina == paginaActual;
            boton.setFont(seleccionada
                    ? tema.negrita(13f) : tema.media(13f));
            if (boton instanceof BotonRedondeado redondeado) {
                redondeado.setColorInicio(seleccionada ? AMARILLO : Color.WHITE);
                redondeado.setColorFinal(seleccionada ? AMARILLO : Color.WHITE);
                redondeado.setColorBorde(seleccionada ? AMARILLO : BORDE);
            }
        }
    }

    private void cambiarPagina(int pagina) {
        if (pagina < 1 || pagina > totalPaginas || pagina == paginaActual) return;
        paginaActual = pagina;
        cargarPagina();
    }

    private ArticuloInventario articuloSeleccionado() {
        int filaVista = tablaProductos.getSelectedRow();
        if (filaVista < 0) return null;
        return modeloTabla.obtener(tablaProductos.convertRowIndexToModel(filaVista));
    }

    private void abrirMovimientoSeleccionado(String tipoMovimiento) {
        ArticuloInventario articulo = articuloSeleccionado();
        if (articulo == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona primero un producto o ingrediente de la tabla.",
                    "Selecciona un artículo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        abrirMovimiento(articulo, tipoMovimiento);
    }

    private void abrirMovimiento(ArticuloInventario articulo,
            String tipoMovimiento) {
        Window propietario = SwingUtilities.getWindowAncestor(this);
        if (MovimientoInventarioFormDialog.mostrar(propietario,
                inventarioDAO, articulo, tipoMovimiento)) {
            cargarPagina();
        }
    }

    private void mostrarHistorial() {
        try {
            List<MovimientoInventario> movimientos =
                    inventarioDAO.listarMovimientos(200);
            Window propietario = SwingUtilities.getWindowAncestor(this);
            JDialog dialogo = new JDialog(propietario,
                    "Movimientos de inventario",
                    java.awt.Dialog.ModalityType.APPLICATION_MODAL);
            dialogo.setUndecorated(true);
            dialogo.setBackground(new Color(0, 0, 0, 0));
            dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            PanelFlotante contenido = new PanelFlotante();
            contenido.setLayout(new BorderLayout(0, 16));
            contenido.setColorFondo(Color.WHITE);
            contenido.setColorBorde(BORDE);
            contenido.setRadio(24);
            contenido.setBorder(new EmptyBorder(24, 28, 22, 28));
            JPanel cabecera = new JPanel(new BorderLayout());
            cabecera.setOpaque(false);
            JLabel titulo = new JLabel("Movimientos de inventario");
            titulo.setFont(tema.negrita(24f));
            titulo.setForeground(AZUL);
            JLabel detalle = new JLabel(
                    "Últimos " + movimientos.size() + " movimientos registrados");
            detalle.setFont(tema.regular(13f));
            detalle.setForeground(SECUNDARIO);
            cabecera.add(titulo, BorderLayout.NORTH);
            cabecera.add(detalle, BorderLayout.SOUTH);
            contenido.add(cabecera, BorderLayout.NORTH);

            DefaultTableModel modelo = new DefaultTableModel(new String[]{
                "ID", "Fecha", "Artículo", "Tipo", "Movimiento",
                "Cantidad", "Motivo", "Pedido"
            }, 0) {
                @Override public boolean isCellEditable(int f, int c) {
                    return false;
                }
            };
            for (MovimientoInventario movimiento : movimientos) {
                modelo.addRow(new Object[]{
                    "#" + movimiento.id(),
                    movimiento.fecha() == null ? "—" : FECHA.format(movimiento.fecha()),
                    movimiento.articulo(), movimiento.tipoArticulo(),
                    movimiento.tipoMovimiento(),
                    cantidadVisible(movimiento),
                    movimiento.motivo() == null ? "—" : movimiento.motivo(),
                    movimiento.idPedido() == null ? "—" : "#" + movimiento.idPedido()
                });
            }
            Componentes.TablaAdministrativa tabla =
                    new Componentes.TablaAdministrativa();
            tabla.setModel(modelo);
            tabla.setRowHeight(43);
            tabla.setColumnasCentradas("0,1,3,4,5,7");
            tabla.aplicarEstilo();
            JScrollPane scroll = new JScrollPane(tabla);
            Componentes.DesplazamientoSuave.ocultarBarras(scroll);
            scroll.setBorder(BorderFactory.createLineBorder(BORDE));
            contenido.add(scroll, BorderLayout.CENTER);

            JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pie.setOpaque(false);
            BotonRedondeado cerrar = crearBotonSuperior("Cerrar", AZUL);
            cerrar.setPreferredSize(new Dimension(130, 42));
            cerrar.addActionListener(e -> dialogo.dispose());
            pie.add(cerrar);
            contenido.add(pie, BorderLayout.SOUTH);
            dialogo.setContentPane(contenido);
            dialogo.setSize(1120, 650);
            dialogo.setLocationRelativeTo(propietario);
            dialogo.setVisible(true);
        } catch (SQLException ex) {
            mostrarError("cargar los movimientos", ex);
        }
    }

    private String cantidadVisible(MovimientoInventario movimiento) {
        BigDecimal cantidad = movimiento.cantidad();
        if ("SALIDA".equals(movimiento.tipoMovimiento())) {
            cantidad = cantidad.abs().negate();
        }
        String prefijo = cantidad.signum() > 0 ? "+" : "";
        return prefijo + cantidad.stripTrailingZeros().toPlainString();
    }

    private void exportarInventario() {
        botonExportar.setEnabled(false);
        final String busqueda = campoBusqueda.getText().trim();
        final String tipo = tipoActual;
        final String estado = estadoActual;
        new SwingWorker<List<ArticuloInventario>, Void>() {
            @Override protected List<ArticuloInventario> doInBackground()
                    throws SQLException {
                return inventarioDAO.listarParaExportar(busqueda, tipo, estado);
            }

            @Override protected void done() {
                botonExportar.setEnabled(true);
                try {
                    guardarCsv(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarError("exportar el inventario", ex.getCause());
                }
            }
        }.execute();
    }

    private void guardarCsv(List<ArticuloInventario> articulos) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar inventario");
        selector.setFileFilter(new FileNameExtensionFilter(
                "Archivo CSV", "csv"));
        selector.setSelectedFile(new File("inventario.csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path archivo = selector.getSelectedFile().toPath();
        if (!archivo.toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            archivo = archivo.resolveSibling(archivo.getFileName() + ".csv");
        }
        StringBuilder csv = new StringBuilder("\uFEFF");
        filaCsv(csv, "ID", "Nombre", "Tipo", "Categoría", "Unidad",
                "Stock actual", "Stock mínimo", "Último movimiento", "Estado");
        for (ArticuloInventario articulo : articulos) {
            filaCsv(csv, articulo.getCodigoVisible(), articulo.getNombre(),
                    articulo.getTipoVisible(), articulo.getCategoria(),
                    articulo.getUnidad(), articulo.getStockActual(),
                    articulo.getStockMinimo(),
                    articulo.getUltimoMovimiento() == null ? ""
                            : FECHA.format(articulo.getUltimoMovimiento()),
                    articulo.getEstadoVisible());
        }
        try {
            Files.writeString(archivo, csv.toString(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this,
                    "Inventario guardado en:\n" + archivo,
                    "Exportación completada", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el archivo.",
                    "Exportación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filaCsv(StringBuilder csv, Object... valores) {
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) csv.append(',');
            String texto = valores[i] == null ? "" : valores[i].toString();
            String inicio = texto.stripLeading();
            if (!(valores[i] instanceof Number) && !inicio.isEmpty()
                    && "=+-@".indexOf(inicio.charAt(0)) >= 0) texto = "'" + texto;
            csv.append('"').append(texto.replace("\"", "\"\"")).append('"');
        }
        csv.append("\r\n");
    }

    private void mostrarError(String operacion, Throwable error) {
        if (errorConexionMostrado) return;
        errorConexionMostrado = true;
        String detalle = error == null || error.getMessage() == null
                ? "Error no especificado." : error.getMessage();
        JOptionPane.showMessageDialog(this,
                "No fue posible " + operacion + ".\n" + detalle,
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private final class ModeloTabla extends AbstractTableModel {
        private final String[] columnas = {"ID", "Producto / ingrediente",
            "Tipo", "Categoría", "Unidad", "Stock actual", "Stock mínimo",
            "Último movimiento", "Estado", "Acción"};
        private List<ArticuloInventario> articulos = List.of();

        void setArticulos(List<ArticuloInventario> nuevos) {
            articulos = List.copyOf(nuevos);
            fireTableDataChanged();
        }

        ArticuloInventario obtener(int fila) { return articulos.get(fila); }
        @Override public int getRowCount() { return articulos.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int columna) {
            return columnas[columna];
        }
        @Override public boolean isCellEditable(int fila, int columna) {
            return columna == 9;
        }
        @Override public Class<?> getColumnClass(int columna) {
            return columna >= 8 ? ArticuloInventario.class : String.class;
        }
        @Override public Object getValueAt(int fila, int columna) {
            ArticuloInventario item = articulos.get(fila);
            return switch (columna) {
                case 0 -> item.getCodigoVisible();
                case 1 -> item.getNombre();
                case 2 -> item.getTipoVisible();
                case 3 -> item.getCategoria();
                case 4 -> item.getUnidad();
                case 5 -> item.getStockActual().stripTrailingZeros().toPlainString();
                case 6 -> item.getStockMinimo().stripTrailingZeros().toPlainString();
                case 7 -> item.getUltimoMovimiento() == null ? "Sin movimientos"
                        : FECHA.format(item.getUltimoMovimiento());
                case 8, 9 -> item;
                default -> "";
            };
        }
    }

    private final class RenderEstado implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionado, boolean foco,
                int fila, int columna) {
            ArticuloInventario item = (ArticuloInventario) valor;
            String estado = item.getEstadoVisible();
            Color texto = switch (estado) {
                case "Disponible" -> new Color(34, 139, 71);
                case "Stock bajo" -> new Color(187, 123, 0);
                case "Agotado" -> ROJO;
                default -> SECUNDARIO;
            };
            Color fondo = switch (estado) {
                case "Disponible" -> new Color(230, 245, 234);
                case "Stock bajo" -> new Color(255, 244, 210);
                case "Agotado" -> new Color(252, 233, 233);
                default -> new Color(237, 239, 243);
            };
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 13));
            panel.setBackground(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            JLabel etiqueta = new JLabel(estado);
            etiqueta.setOpaque(true);
            etiqueta.setFont(tema.media(12f));
            etiqueta.setForeground(texto);
            etiqueta.setBackground(fondo);
            etiqueta.setBorder(new EmptyBorder(4, 10, 4, 10));
            panel.add(etiqueta);
            return panel;
        }
    }

    private final class RenderAccion implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionado, boolean foco,
                int fila, int columna) {
            JPanel panel = panelAccion(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            ArticuloInventario item = (ArticuloInventario) valor;
            BotonRedondeado boton = botonMovimiento();
            boton.setEnabled(item.isActivo());
            panel.add(boton);
            return panel;
        }
    }

    private final class EditorAccion extends AbstractCellEditor
            implements TableCellEditor {
        private final JPanel panel = panelAccion(Color.WHITE);
        private final BotonRedondeado boton = botonMovimiento();
        private ArticuloInventario item;

        EditorAccion() {
            panel.add(boton);
            boton.addActionListener(e -> {
                ArticuloInventario seleccionado = item;
                fireEditingStopped();
                abrirMovimiento(seleccionado, null);
            });
        }
        @Override public Component getTableCellEditorComponent(JTable tabla,
                Object valor, boolean seleccionado, int fila, int columna) {
            item = (ArticuloInventario) valor;
            boton.setEnabled(item.isActivo());
            panel.setBackground(tabla.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return item; }
    }

    private JPanel panelAccion(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setBackground(fondo);
        return panel;
    }

    private BotonRedondeado botonMovimiento() {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText("±  Stock");
        boton.setFont(tema.negrita(12f));
        boton.setForeground(AZUL);
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(AMARILLO);
        boton.setGrosorBorde(1f);
        boton.setRadio(9);
        boton.setPreferredSize(new Dimension(92, 31));
        boton.setToolTipText("Registrar entrada, salida o ajuste");
        return boton;
    }

    private final class DialogoMovimiento extends JDialog {
        private final ArticuloInventario articulo;
        private final JComboBox<String> tipo = new JComboBox<>(new String[]{
            "Entrada", "Salida", "Ajuste"
        });
        private final JTextField cantidad = new JTextField();
        private final JTextField motivo = new JTextField();
        private final JLabel instruccion = new JLabel();
        private final JLabel error = new JLabel(" ");
        private final BotonRedondeado guardar = botonDialogo(
                "Guardar movimiento", true);
        private boolean guardado;

        DialogoMovimiento(Window propietario, ArticuloInventario articulo,
                String tipoFijo) {
            super(propietario, ModalityType.APPLICATION_MODAL);
            this.articulo = articulo;
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setContentPane(crearContenido());
            if ("ENTRADA".equals(tipoFijo)) tipo.setSelectedIndex(0);
            if ("SALIDA".equals(tipoFijo)) tipo.setSelectedIndex(1);
            if (tipoFijo != null) tipo.setEnabled(false);
            tipo.addActionListener(e -> actualizarInstruccion());
            actualizarInstruccion();
            guardar.addActionListener(e -> guardar());
            pack();
            setLocationRelativeTo(propietario);
        }

        private PanelFlotante crearContenido() {
            PanelFlotante panel = new PanelFlotante();
            panel.setColorFondo(Color.WHITE);
            panel.setColorBorde(BORDE);
            panel.setRadio(26);
            panel.setPreferredSize(new Dimension(650, 445));
            panel.setLayout(new BorderLayout());

            JPanel cabecera = new JPanel(new BorderLayout(0, 4));
            cabecera.setOpaque(false);
            cabecera.setBorder(new EmptyBorder(27, 32, 15, 32));
            JLabel titulo = new JLabel("Movimiento de inventario");
            titulo.setFont(tema.negrita(24f));
            titulo.setForeground(AZUL);
            JLabel subtitulo = new JLabel(String.format(
                    "%s · %s · Stock actual: %s %s",
                    articulo.getCodigoVisible(), articulo.getNombre(),
                    articulo.getStockActual().stripTrailingZeros().toPlainString(),
                    articulo.getUnidad()));
            subtitulo.setFont(tema.regular(14f));
            subtitulo.setForeground(SECUNDARIO);
            cabecera.add(titulo, BorderLayout.NORTH);
            cabecera.add(subtitulo, BorderLayout.SOUTH);
            panel.add(cabecera, BorderLayout.NORTH);

            JPanel campos = new JPanel(new GridBagLayout());
            campos.setOpaque(false);
            campos.setBorder(new EmptyBorder(0, 30, 0, 30));
            tipo.setFont(tema.regular(14f));
            tipo.setBackground(Color.WHITE);
            tipo.setForeground(AZUL);
            tipo.setBorder(BorderFactory.createLineBorder(BORDE));
            estilizarCampo(cantidad);
            estilizarCampo(motivo);
            agregarCampo(campos, 0, "Tipo de movimiento", tipo);
            agregarCampo(campos, 1,
                    "Cantidad (" + articulo.getUnidad() + ")", cantidad);
            agregarCampo(campos, 2, "Motivo (opcional)", motivo);
            instruccion.setFont(tema.regular(12f));
            instruccion.setForeground(SECUNDARIO);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 3;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(0, 4, 0, 4);
            campos.add(instruccion, c);
            panel.add(campos, BorderLayout.CENTER);

            JPanel pie = new JPanel(new BorderLayout());
            pie.setOpaque(false);
            pie.setPreferredSize(new Dimension(650, 96));
            pie.setBorder(new EmptyBorder(0, 32, 20, 32));
            error.setFont(tema.media(12f));
            error.setForeground(ROJO);
            pie.add(error, BorderLayout.NORTH);
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
            botones.setOpaque(false);
            BotonRedondeado cancelar = botonDialogo("Cancelar", false);
            cancelar.setPreferredSize(new Dimension(128, 45));
            guardar.setPreferredSize(new Dimension(195, 45));
            cancelar.addActionListener(e -> dispose());
            botones.add(cancelar);
            botones.add(guardar);
            pie.add(botones, BorderLayout.CENTER);
            panel.add(pie, BorderLayout.SOUTH);
            return panel;
        }

        private void agregarCampo(JPanel panel, int fila, String nombre,
                Component control) {
            JPanel filaPanel = new JPanel(new BorderLayout(0, 5));
            filaPanel.setOpaque(false);
            JLabel etiqueta = new JLabel(nombre);
            etiqueta.setFont(tema.media(13f));
            etiqueta.setForeground(AZUL);
            filaPanel.add(etiqueta, BorderLayout.NORTH);
            control.setPreferredSize(new Dimension(565, 40));
            filaPanel.add(control, BorderLayout.CENTER);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = fila;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(0, 4, 10, 4);
            panel.add(filaPanel, c);
        }

        private void actualizarInstruccion() {
            instruccion.setText(tipo.getSelectedIndex() == 2
                    ? "En Ajuste, escribe el stock final que debe quedar."
                    : "En Entrada o Salida, escribe cuánto se agrega o retira.");
        }

        private void guardar() {
            error.setText(" ");
            BigDecimal valor;
            try {
                valor = new BigDecimal(cantidad.getText().trim());
            } catch (NumberFormatException ex) {
                error.setText("Ingresa una cantidad válida con punto decimal.");
                return;
            }
            String codigo = switch (tipo.getSelectedIndex()) {
                case 0 -> "ENTRADA";
                case 1 -> "SALIDA";
                default -> "AJUSTE";
            };
            guardar.setEnabled(false);
            try {
                inventarioDAO.registrarMovimiento(articulo, codigo, valor,
                        motivo.getText().trim());
                guardado = true;
                dispose();
            } catch (SQLException | IllegalArgumentException ex) {
                error.setText("No fue posible registrar: " + ex.getMessage());
            } finally {
                guardar.setEnabled(true);
            }
        }
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(tema.regular(14f));
        campo.setForeground(AZUL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(0, 12, 0, 12)));
    }

    private BotonRedondeado botonDialogo(String texto, boolean principal) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText(texto);
        boton.setFont(tema.negrita(13f));
        boton.setDegradado(false);
        boton.setRadio(12);
        boton.setGrosorBorde(1f);
        boton.setColorInicio(principal ? AMARILLO : Color.WHITE);
        boton.setColorFinal(principal ? AMARILLO : Color.WHITE);
        boton.setColorBorde(principal ? AMARILLO : ROJO);
        boton.setForeground(principal ? AZUL : ROJO);
        return boton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitulo3 = new javax.swing.JLabel();
        labelTitulo2 = new javax.swing.JLabel();
        botonAgregarProducto = new Componentes.BotonDerretido();
        botonRegistrarSalida = new Componentes.BotonRedondeado();
        botonMovimientos = new Componentes.BotonRedondeado();
        panelFlotante1 = new Componentes.PanelFlotante();
        panelCircular2 = new Componentes.PanelCircular();
        labelEscalable2 = new Labels.LabelEscalable();
        labelTitulo4 = new javax.swing.JLabel();
        labelTotalProductos = new javax.swing.JLabel();
        labelTitulo6 = new javax.swing.JLabel();
        panelFlotante2 = new Componentes.PanelFlotante();
        panelCircular3 = new Componentes.PanelCircular();
        labelEscalable3 = new Labels.LabelEscalable();
        labelTitulo7 = new javax.swing.JLabel();
        labelStockBajo = new javax.swing.JLabel();
        labelTitulo9 = new javax.swing.JLabel();
        panelFlotante4 = new Componentes.PanelFlotante();
        panelCircular5 = new Componentes.PanelCircular();
        labelEscalable5 = new Labels.LabelEscalable();
        labelTitulo13 = new javax.swing.JLabel();
        labelActivos = new javax.swing.JLabel();
        labelTitulo15 = new javax.swing.JLabel();
        panelFlotante3 = new Componentes.PanelFlotante();
        panelCircular4 = new Componentes.PanelCircular();
        labelEscalable4 = new Labels.LabelEscalable();
        labelTitulo10 = new javax.swing.JLabel();
        labelInactivos = new javax.swing.JLabel();
        labelTitulo12 = new javax.swing.JLabel();
        botonExportar = new Componentes.BotonRedondeado();
        panelFiltros = new Componentes.PanelFlotante();
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        filtroCategoria = new Componentes.BotonDesplegable();
        filtroEstado = new Componentes.BotonDesplegable();
        botonLimpiarFiltros = new Componentes.BotonRedondeado();
        panelTabla = new Componentes.PanelFlotante();
        scrollUsuarios = new javax.swing.JScrollPane();
        tablaProductos = new Componentes.TablaAdministrativa();
        etiquetaRango = new javax.swing.JLabel();
        panelPaginacion = new javax.swing.JPanel();
        botonAnterior = new Componentes.BotonRedondeado();
        botonPagina1 = new Componentes.BotonRedondeado();
        botonPagina2 = new Componentes.BotonRedondeado();
        botonPagina3 = new Componentes.BotonRedondeado();
        botonSiguiente = new Componentes.BotonRedondeado();

        setBackground(new java.awt.Color(248, 249, 251));
        setPreferredSize(new java.awt.Dimension(1530, 932));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo3.setFont(new java.awt.Font("Dialog", 1, 42)); // NOI18N
        labelTitulo3.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo3.setText("Gestión de Inventario");
        add(labelTitulo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 16, 650, 64));

        labelTitulo2.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        labelTitulo2.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo2.setText("Administra existencias, entradas y salidas");
        add(labelTitulo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 76, 620, 36));

        botonAgregarProducto.setForeground(new java.awt.Color(0, 0, 0));
        botonAgregarProducto.setText("Registrar entrada");
        add(botonAgregarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 42, 175, 60));

        botonRegistrarSalida.setColorInicio(new java.awt.Color(255, 255, 255));
        botonRegistrarSalida.setDegradado(false);
        botonRegistrarSalida.setForeground(new java.awt.Color(0, 20, 43));
        botonRegistrarSalida.setText("Registrar salida");
        botonRegistrarSalida.setColorBorde(new java.awt.Color(0, 20, 43));
        botonRegistrarSalida.setColorFinal(new java.awt.Color(255, 255, 255));
        botonRegistrarSalida.setGrosorBorde(1.3F);
        botonRegistrarSalida.setRadio(13);
        add(botonRegistrarSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(965, 45, 175, 54));

        botonMovimientos.setColorInicio(new java.awt.Color(255, 255, 255));
        botonMovimientos.setDegradado(false);
        botonMovimientos.setForeground(new java.awt.Color(0, 20, 43));
        botonMovimientos.setText("Ajustar stock");
        botonMovimientos.setColorBorde(new java.awt.Color(0, 20, 43));
        botonMovimientos.setColorFinal(new java.awt.Color(255, 255, 255));
        botonMovimientos.setGrosorBorde(1.3F);
        botonMovimientos.setRadio(13);
        add(botonMovimientos, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 45, 175, 54));

        panelFlotante1.setVisible(false);
        panelFlotante1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lista_icono.png"))); // NOI18N
        panelCircular2.add(labelEscalable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 50, 50));

        panelFlotante1.add(panelCircular2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo4.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo4.setText("Productos totales");
        panelFlotante1.add(labelTitulo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelTotalProductos.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTotalProductos.setForeground(new java.awt.Color(13, 17, 23));
        labelTotalProductos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTotalProductos.setText("48");
        panelFlotante1.add(labelTotalProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        labelTitulo6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelTitulo6.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo6.setText("En el inventario");
        panelFlotante1.add(labelTitulo6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 460, 80));

        add(panelFlotante1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 360, 160));

        panelFlotante2.setVisible(false);
        panelFlotante2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular3.setColorFondo(new java.awt.Color(252, 233, 233));
        panelCircular3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/advertencia_icono.png"))); // NOI18N
        panelCircular3.add(labelEscalable3, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 22, 43, 43));

        panelFlotante2.add(panelCircular3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo7.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo7.setText("Stock bajo");
        panelFlotante2.add(labelTitulo7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelStockBajo.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelStockBajo.setForeground(new java.awt.Color(195, 61, 66));
        labelStockBajo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelStockBajo.setText("48");
        panelFlotante2.add(labelStockBajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        labelTitulo9.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelTitulo9.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo9.setText("Requieren reposición");
        panelFlotante2.add(labelTitulo9, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 460, 80));

        add(panelFlotante2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 140, 360, 160));

        panelFlotante4.setVisible(false);
        panelFlotante4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular5.setColorFondo(new java.awt.Color(230, 245, 234));
        panelCircular5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cheque.png"))); // NOI18N
        panelCircular5.add(labelEscalable5, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 23, 45, 45));

        panelFlotante4.add(panelCircular5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo13.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo13.setText("Activos");
        panelFlotante4.add(labelTitulo13, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelActivos.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelActivos.setForeground(new java.awt.Color(13, 17, 23));
        labelActivos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelActivos.setText("48");
        panelFlotante4.add(labelActivos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        labelTitulo15.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelTitulo15.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo15.setText("Disponible para uso");
        panelFlotante4.add(labelTitulo15, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 460, 80));

        add(panelFlotante4, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 140, 360, 160));

        panelFlotante3.setVisible(false);
        panelFlotante3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular4.setColorFondo(new java.awt.Color(237, 239, 243));
        panelCircular4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/x_icono.png"))); // NOI18N
        panelCircular4.add(labelEscalable4, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 27, 35, 35));

        panelFlotante3.add(panelCircular4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo10.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo10.setText("Inactivos");
        panelFlotante3.add(labelTitulo10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelInactivos.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelInactivos.setForeground(new java.awt.Color(13, 17, 23));
        labelInactivos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelInactivos.setText("48");
        panelFlotante3.add(labelInactivos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        labelTitulo12.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelTitulo12.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo12.setText("No disponibles");
        panelFlotante3.add(labelTitulo12, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 460, 80));

        add(panelFlotante3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 140, 360, 160));

        botonExportar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonExportar.setDegradado(false);
        botonExportar.setForeground(new java.awt.Color(231, 55, 65));
        botonExportar.setRadio(20);
        botonExportar.setText("Exportar");
        botonExportar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonExportar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonExportar.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        botonExportar.setGrosorBorde(1.6F);
        botonExportar.setMargin(new java.awt.Insets(5, 14, 3, 14));
        add(botonExportar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1335, 45, 175, 54));

        panelFiltros.setColorFondo(new java.awt.Color(255, 255, 255));
        panelFiltros.setColorBorde(new java.awt.Color(225, 230, 237));
        panelFiltros.setRadio(18);
        panelFiltros.setSombra(false);
        panelFiltros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        campoBusqueda.setPlaceholder("Buscar producto o ingrediente...");
        campoBusqueda.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        campoBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoBusquedaActionPerformed(evt);
            }
        });
        panelFiltros.add(campoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 420, 55));

        filtroCategoria.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroCategoria.setColorHover(new java.awt.Color(248, 249, 251));
        filtroCategoria.setForeground(new java.awt.Color(0, 20, 43));
        filtroCategoria.setText("Todos los tipos");
        filtroCategoria.setTextoDesplegable("Todos los tipos;Ingredientes;Productos directos");
        filtroCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroCategoriaActionPerformed(evt);
            }
        });
        panelFiltros.add(filtroCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 22, 280, 52));

        filtroEstado.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroEstado.setColorHover(new java.awt.Color(248, 249, 251));
        filtroEstado.setForeground(new java.awt.Color(0, 20, 43));
        filtroEstado.setText("Todos los estados");
        filtroEstado.setTextoDesplegable("Todos los estados;Disponible;Stock bajo;Agotado;Inactivo");
        panelFiltros.add(filtroEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 22, 280, 52));

        botonLimpiarFiltros.setColorInicio(new java.awt.Color(255, 255, 255));
        botonLimpiarFiltros.setDegradado(false);
        botonLimpiarFiltros.setForeground(new java.awt.Color(231, 55, 65));
        botonLimpiarFiltros.setText("Limpiar filtros");
        botonLimpiarFiltros.setColorBorde(new java.awt.Color(231, 55, 65));
        botonLimpiarFiltros.setColorFinal(new java.awt.Color(255, 255, 255));
        botonLimpiarFiltros.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        botonLimpiarFiltros.setGrosorBorde(1.6F);
        botonLimpiarFiltros.setMargin(new java.awt.Insets(5, 14, 3, 14));
        panelFiltros.add(botonLimpiarFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 22, 380, 52));

        add(panelFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 175, 1470, 95));

        panelTabla.setColorFondo(new java.awt.Color(255, 255, 255));
        panelTabla.setColorBorde(new java.awt.Color(225, 230, 237));
        panelTabla.setRadio(18);
        panelTabla.setSombra(false);
        panelTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scrollUsuarios.setBorder(null);
        scrollUsuarios.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollUsuarios.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollUsuarios.setAutoscrolls(true);

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "ID", "Producto / ingrediente", "Tipo", "Categoría", "Unidad", "Stock actual", "Stock mínimo", "Último movimiento", "Estado", "Acción"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaProductos.setAutoscrolls(false);
        tablaProductos.setColumnasCentradas("0,2,4,5,6,8,9");
        scrollUsuarios.setViewportView(tablaProductos);

        panelTabla.add(scrollUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 1442, 555));

        etiquetaRango.setForeground(new java.awt.Color(92, 103, 124));
        etiquetaRango.setText("Mostrando inventario");
        panelTabla.add(etiquetaRango, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 581, 420, 42));

        panelPaginacion.setOpaque(false);
        panelPaginacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonAnterior.setColorInicio(new java.awt.Color(255, 255, 255));
        botonAnterior.setDegradado(false);
        botonAnterior.setText("‹");
        panelPaginacion.add(botonAnterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 40, 38));

        botonPagina1.setColorInicio(new java.awt.Color(255, 188, 0));
        botonPagina1.setDegradado(false);
        botonPagina1.setText("1");
        panelPaginacion.add(botonPagina1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 2, 40, 38));

        botonPagina2.setColorInicio(new java.awt.Color(255, 255, 255));
        botonPagina2.setDegradado(false);
        botonPagina2.setText("2");
        panelPaginacion.add(botonPagina2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 2, 40, 38));

        botonPagina3.setColorInicio(new java.awt.Color(255, 255, 255));
        botonPagina3.setDegradado(false);
        botonPagina3.setText("3");
        panelPaginacion.add(botonPagina3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 2, 40, 38));

        botonSiguiente.setColorInicio(new java.awt.Color(255, 255, 255));
        botonSiguiente.setDegradado(false);
        botonSiguiente.setText("›");
        panelPaginacion.add(botonSiguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 2, 40, 38));

        panelTabla.add(panelPaginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 581, 270, 42));

        add(panelTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 285, 1470, 640));
    }// </editor-fold>//GEN-END:initComponents

    private void campoBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoBusquedaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoBusquedaActionPerformed

    private void filtroCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroCategoriaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonAgregarProducto;
    private Componentes.BotonRedondeado botonAnterior;
    private Componentes.BotonRedondeado botonExportar;
    private Componentes.BotonRedondeado botonLimpiarFiltros;
    private Componentes.BotonRedondeado botonMovimientos;
    private Componentes.BotonRedondeado botonPagina1;
    private Componentes.BotonRedondeado botonPagina2;
    private Componentes.BotonRedondeado botonPagina3;
    private Componentes.BotonRedondeado botonRegistrarSalida;
    private Componentes.BotonRedondeado botonSiguiente;
    private Componentes.CampoBusquedaAdmin campoBusqueda;
    private javax.swing.JLabel etiquetaRango;
    private Componentes.BotonDesplegable filtroCategoria;
    private Componentes.BotonDesplegable filtroEstado;
    private javax.swing.JLabel labelActivos;
    private Labels.LabelEscalable labelEscalable2;
    private Labels.LabelEscalable labelEscalable3;
    private Labels.LabelEscalable labelEscalable4;
    private Labels.LabelEscalable labelEscalable5;
    private javax.swing.JLabel labelInactivos;
    private javax.swing.JLabel labelStockBajo;
    private javax.swing.JLabel labelTitulo10;
    private javax.swing.JLabel labelTitulo12;
    private javax.swing.JLabel labelTitulo13;
    private javax.swing.JLabel labelTitulo15;
    private javax.swing.JLabel labelTitulo2;
    private javax.swing.JLabel labelTitulo3;
    private javax.swing.JLabel labelTitulo4;
    private javax.swing.JLabel labelTitulo6;
    private javax.swing.JLabel labelTitulo7;
    private javax.swing.JLabel labelTitulo9;
    private javax.swing.JLabel labelTotalProductos;
    private Componentes.PanelCircular panelCircular2;
    private Componentes.PanelCircular panelCircular3;
    private Componentes.PanelCircular panelCircular4;
    private Componentes.PanelCircular panelCircular5;
    private Componentes.PanelFlotante panelFiltros;
    private Componentes.PanelFlotante panelFlotante1;
    private Componentes.PanelFlotante panelFlotante2;
    private Componentes.PanelFlotante panelFlotante3;
    private Componentes.PanelFlotante panelFlotante4;
    private javax.swing.JPanel panelPaginacion;
    private Componentes.PanelFlotante panelTabla;
    private javax.swing.JScrollPane scrollUsuarios;
    private Componentes.TablaAdministrativa tablaProductos;
    // End of variables declaration//GEN-END:variables
}
