package GUI_ADMINISTRADOR;

import DAO.InventarioDAO;
import Modelos.MovimientoInventario;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

/** Historial de movimientos integrado al módulo Inventario. */
@SuppressWarnings({"serial", "this-escape"})
public class MovimientosInventarioPanel extends javax.swing.JPanel {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final InventarioDAO dao = new InventarioDAO();
    private final ModeloMovimientos modelo = new ModeloMovimientos();
    private final Timer temporizadorFiltro;
    private List<MovimientoInventario> movimientos = List.of();
    private String tipoActual;

    public MovimientosInventarioPanel() {
        initComponents();
        temporizadorFiltro = new Timer(250, e -> aplicarFiltros());
        temporizadorFiltro.setRepeats(false);
        prepararVista();
        configurarFiltros();
        cargarMovimientos();
    }

    public void recargarDatos() {
        cargarMovimientos();
    }

    private void prepararVista() {
        tablaMovimientos.setModel(modelo);
        tablaMovimientos.aplicarEstilo();
    }

    private void configurarFiltros() {
        filtroTipo.addMenuOpcionListener(e -> {
            filtroTipo.setText(e.getActionCommand());
            tipoActual = switch (e.getActionCommand()) {
                case "Entradas" -> "ENTRADA";
                case "Salidas" -> "SALIDA";
                case "Ajustes" -> "AJUSTE";
                default -> null;
            };
            aplicarFiltros();
        });
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { temporizadorFiltro.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { temporizadorFiltro.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { temporizadorFiltro.restart(); }
        });
        botonActualizar.addActionListener(e -> cargarMovimientos());
    }

    private void cargarMovimientos() {
        botonActualizar.setEnabled(false);
        etiquetaRango.setText("Cargando movimientos...");
        new SwingWorker<List<MovimientoInventario>, Void>() {
            @Override protected List<MovimientoInventario> doInBackground()
                    throws SQLException {
                return dao.listarMovimientos(300);
            }
            @Override protected void done() {
                botonActualizar.setEnabled(true);
                try {
                    movimientos = List.copyOf(get());
                    aplicarFiltros();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    movimientos = List.of();
                    modelo.setMovimientos(movimientos);
                    etiquetaRango.setText("No fue posible cargar el historial");
                    Throwable causa = ex.getCause();
                    JOptionPane.showMessageDialog(MovimientosInventarioPanel.this,
                            causa == null ? "Error no especificado." : causa.getMessage(),
                            "Movimientos de inventario", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void aplicarFiltros() {
        String texto = campoBusqueda.getText().trim().toLowerCase(Locale.ROOT);
        List<MovimientoInventario> filtrados = movimientos.stream()
                .filter(m -> tipoActual == null
                        || tipoActual.equals(m.tipoMovimiento()))
                .filter(m -> texto.isEmpty()
                        || contiene(m.articulo(), texto)
                        || contiene(m.tipoArticulo(), texto)
                        || contiene(m.motivo(), texto)
                        || String.valueOf(m.id()).contains(texto))
                .toList();
        modelo.setMovimientos(filtrados);
        etiquetaRango.setText(filtrados.size() + " movimiento"
                + (filtrados.size() == 1 ? "" : "s") + " mostrado"
                + (filtrados.size() == 1 ? "" : "s"));
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    private String cantidadVisible(MovimientoInventario movimiento) {
        BigDecimal cantidad = movimiento.cantidad();
        if ("SALIDA".equals(movimiento.tipoMovimiento())) {
            cantidad = cantidad.abs().negate();
        }
        return (cantidad.signum() > 0 ? "+" : "")
                + cantidad.stripTrailingZeros().toPlainString();
    }

    private final class ModeloMovimientos extends AbstractTableModel {
        private final String[] columnas = {"ID", "Fecha", "Artículo", "Tipo",
            "Movimiento", "Cantidad", "Motivo", "Pedido"};
        private List<MovimientoInventario> datos = List.of();
        void setMovimientos(List<MovimientoInventario> nuevos) {
            datos = List.copyOf(nuevos);
            fireTableDataChanged();
        }
        @Override public int getRowCount() { return datos.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int columna) { return columnas[columna]; }
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
        @Override public Object getValueAt(int fila, int columna) {
            MovimientoInventario item = datos.get(fila);
            return switch (columna) {
                case 0 -> "#" + item.id();
                case 1 -> item.fecha() == null ? "—" : FECHA.format(item.fecha());
                case 2 -> item.articulo();
                case 3 -> item.tipoArticulo();
                case 4 -> switch (item.tipoMovimiento()) {
                    case "ENTRADA" -> "Entrada";
                    case "SALIDA" -> "Salida";
                    default -> "Ajuste";
                };
                case 5 -> cantidadVisible(item);
                case 6 -> item.motivo() == null || item.motivo().isBlank()
                        ? "—" : item.motivo();
                case 7 -> item.idPedido() == null ? "—" : "#" + item.idPedido();
                default -> "";
            };
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        botonActualizar = new Componentes.BotonDerretido();
        panelFiltros = new Componentes.PanelFlotante();
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        filtroTipo = new Componentes.BotonDesplegable();
        panelTabla = new Componentes.PanelFlotante();
        scrollTabla = new javax.swing.JScrollPane();
        tablaMovimientos = new Componentes.TablaAdministrativa();
        etiquetaRango = new javax.swing.JLabel();

        setBackground(new java.awt.Color(248, 249, 251));
        setPreferredSize(new java.awt.Dimension(1530, 932));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo.setFont(new java.awt.Font("Dialog", 1, 42)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(0, 20, 43));
        labelTitulo.setText("Movimientos de Inventario");
        add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 16, 700, 64));

        labelSubtitulo.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        labelSubtitulo.setForeground(new java.awt.Color(92, 103, 124));
        labelSubtitulo.setText("Consulta entradas, salidas y ajustes registrados");
        add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 76, 650, 36));

        botonActualizar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        botonActualizar.setText("Actualizar historial");
        add(botonActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1290, 42, 220, 60));

        panelFiltros.setColorBorde(new java.awt.Color(225, 230, 237));
        panelFiltros.setRadio(18);
        panelFiltros.setSombra(false);
        panelFiltros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        campoBusqueda.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        campoBusqueda.setPlaceholder("Buscar artículo, motivo o ID...");
        panelFiltros.add(campoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 750, 55));

        filtroTipo.setAltoOpcion(42);
        filtroTipo.setAnchoMenu(280);
        filtroTipo.setColorBordeMenu(new java.awt.Color(225, 230, 237));
        filtroTipo.setColorDesplegado(new java.awt.Color(255, 247, 222));
        filtroTipo.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroTipo.setColorHover(new java.awt.Color(248, 249, 251));
        filtroTipo.setColorTextoOpcion(new java.awt.Color(0, 20, 43));
        filtroTipo.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        filtroTipo.setForeground(new java.awt.Color(0, 20, 43));
        filtroTipo.setText("Todos los movimientos");
        filtroTipo.setTextoDesplegable("Todos los movimientos;Entradas;Salidas;Ajustes");
        panelFiltros.add(filtroTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 22, 280, 52));

        add(panelFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 175, 1470, 95));

        panelTabla.setColorBorde(new java.awt.Color(225, 230, 237));
        panelTabla.setRadio(18);
        panelTabla.setSombra(false);
        panelTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scrollTabla.setBorder(null);
        scrollTabla.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollTabla.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tablaMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Fecha", "Artículo", "Tipo", "Movimiento", "Cantidad", "Motivo", "Pedido"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMovimientos.setRowHeight(49);
        tablaMovimientos.setAnchosColumnas("75,145,275,145,125,105,360,85");
        tablaMovimientos.setColumnasCentradas("0,1,3,4,5,7");
        scrollTabla.setViewportView(tablaMovimientos);

        panelTabla.add(scrollTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 1442, 555));

        etiquetaRango.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        etiquetaRango.setForeground(new java.awt.Color(92, 103, 124));
        etiquetaRango.setText("Cargando movimientos...");
        panelTabla.add(etiquetaRango, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 581, 800, 42));

        add(panelTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 285, 1470, 640));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonActualizar;
    private Componentes.CampoBusquedaAdmin campoBusqueda;
    private javax.swing.JLabel etiquetaRango;
    private Componentes.BotonDesplegable filtroTipo;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTitulo;
    private Componentes.PanelFlotante panelFiltros;
    private Componentes.PanelFlotante panelTabla;
    private javax.swing.JScrollPane scrollTabla;
    private Componentes.TablaAdministrativa tablaMovimientos;
    // End of variables declaration//GEN-END:variables
}
