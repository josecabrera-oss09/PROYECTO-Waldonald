package GUI_ADMINISTRADOR;

import CRUD.IngredienteCRUD;
import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
import Modelos.Ingrediente;
import Modelos.PaginaIngredientes;
import Utilidades.IconosUsuarios;
import Utilidades.TemaAdmin;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/** Catálogo de ingredientes integrado al módulo Inventario. */
@SuppressWarnings({"serial", "this-escape"})
public class CatalogoIngredientesPanel extends javax.swing.JPanel {

    private static final int POR_PAGINA = 10;
    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 230, 237);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color ROJO = new Color(231, 55, 65);

    private final IngredienteCRUD crud = new IngredienteCRUD();
    private final TemaAdmin tema = new TemaAdmin();
    private final ModeloTabla modelo = new ModeloTabla();
    private final JButton[] botonesPagina;
    private final Timer temporizadorBusqueda;
    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int secuenciaCarga;
    private Boolean stockBajoActual;
    private Boolean estadoActual;
    private boolean errorMostrado;

    public CatalogoIngredientesPanel() {
        initComponents();
        botonesPagina = new JButton[]{botonPagina1, botonPagina2, botonPagina3};
        temporizadorBusqueda = new Timer(350, e -> {
            paginaActual = 1;
            cargarPagina();
        });
        temporizadorBusqueda.setRepeats(false);
        prepararVista();
        configurarTabla();
        configurarFiltros();
        configurarPaginacion();
        botonAgregar.addActionListener(e -> abrirFormulario(null));
        cargarPagina();
    }

    public void recargarDatos() {
        cargarPagina();
    }

    private void prepararVista() {
        labelTitulo.setFont(tema.negrita(42f));
        labelSubtitulo.setFont(tema.regular(16f));
        labelSubtitulo.setForeground(SECUNDARIO);
        botonAgregar.setFont(tema.negrita(14f));
        campoBusqueda.setFont(tema.regular(14f));
        botonLimpiar.setFont(tema.negrita(14f));
        botonLimpiar.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.FILTRO, ROJO, 19));
        botonLimpiar.setIconTextGap(9);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        scrollTabla.getViewport().setBackground(Color.WHITE);
        etiquetaRango.setFont(tema.regular(12f));
        etiquetaRango.setForeground(SECUNDARIO);
    }

    private void configurarTabla() {
        tablaIngredientes.setModel(modelo);
        tablaIngredientes.aplicarEstilo();
        tablaIngredientes.setRowHeight(53);
        tablaIngredientes.setColumnasCentradas("0,2,3,4,5,6,7");
        tablaIngredientes.getColumnModel().getColumn(5)
                .setCellRenderer(new RenderNivel());
        tablaIngredientes.getColumnModel().getColumn(6)
                .setCellRenderer(new RenderEstado());
        tablaIngredientes.getColumnModel().getColumn(7)
                .setCellRenderer(new RenderAcciones());
        tablaIngredientes.getColumnModel().getColumn(7)
                .setCellEditor(new EditorAcciones());
        int[] anchos = {80, 330, 155, 135, 135, 115, 115, 125};
        for (int i = 0; i < anchos.length; i++) {
            tablaIngredientes.getColumnModel().getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }
    }

    private void configurarFiltros() {
        prepararSelector(filtroStock);
        filtroStock.addMenuOpcionListener(e -> {
            filtroStock.setText(e.getActionCommand());
            stockBajoActual = switch (e.getActionCommand()) {
                case "Stock bajo" -> true;
                case "Stock normal" -> false;
                default -> null;
            };
            paginaActual = 1;
            cargarPagina();
        });
        prepararSelector(filtroEstado);
        filtroEstado.addMenuOpcionListener(e -> {
            filtroEstado.setText(e.getActionCommand());
            estadoActual = switch (e.getActionCommand()) {
                case "Activos" -> true;
                case "Inactivos" -> false;
                default -> null;
            };
            paginaActual = 1;
            cargarPagina();
        });
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { temporizadorBusqueda.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { temporizadorBusqueda.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { temporizadorBusqueda.restart(); }
        });
        botonLimpiar.addActionListener(e -> {
            temporizadorBusqueda.stop();
            campoBusqueda.setText("");
            temporizadorBusqueda.stop();
            stockBajoActual = null;
            estadoActual = null;
            filtroStock.setText("Todos los niveles");
            filtroEstado.setText("Todos los estados");
            paginaActual = 1;
            cargarPagina();
        });
    }

    private void prepararSelector(BotonDesplegable selector) {
        selector.setFont(tema.regular(14f));
        selector.setForeground(AZUL);
        selector.setColorFondo(Color.WHITE);
        selector.setColorHover(new Color(248, 249, 251));
        selector.setColorDesplegado(new Color(255, 247, 222));
        selector.setColorTextoOpcion(AZUL);
        selector.setColorBordeMenu(BORDE);
        selector.setAnchoMenu(280);
        selector.setAltoOpcion(42);
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

    private void prepararBotonPagina(JButton boton, IconosUsuarios.Tipo icono) {
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
        if (icono != null) {
            boton.setText("");
            boton.setIcon(IconosUsuarios.crear(icono, AZUL, 16));
        }
    }

    private void cargarPagina() {
        final int solicitud = ++secuenciaCarga;
        tablaIngredientes.setEnabled(false);
        etiquetaRango.setText("Cargando ingredientes...");
        final String busqueda = campoBusqueda.getText().trim();
        final int pagina = paginaActual;
        final Boolean nivel = stockBajoActual;
        final Boolean estado = estadoActual;
        new SwingWorker<PaginaIngredientes, Void>() {
            @Override protected PaginaIngredientes doInBackground() throws SQLException {
                return crud.listarPagina(busqueda, nivel, estado, pagina, POR_PAGINA);
            }
            @Override protected void done() {
                if (solicitud != secuenciaCarga) return;
                try {
                    PaginaIngredientes datos = get();
                    totalPaginas = Math.max(1,
                            (datos.totalRegistros() + POR_PAGINA - 1) / POR_PAGINA);
                    if (paginaActual > totalPaginas) {
                        paginaActual = totalPaginas;
                        cargarPagina();
                        return;
                    }
                    modelo.setIngredientes(datos.ingredientes());
                    actualizarPaginacion(datos.totalRegistros());
                    errorMostrado = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    modelo.setIngredientes(List.of());
                    actualizarPaginacion(0);
                    mostrarError("cargar los ingredientes", ex.getCause());
                } finally {
                    tablaIngredientes.setEnabled(true);
                }
            }
        }.execute();
    }

    private void actualizarPaginacion(int total) {
        if (total == 0) etiquetaRango.setText("No se encontraron ingredientes");
        else {
            int inicio = (paginaActual - 1) * POR_PAGINA + 1;
            int fin = Math.min(inicio + POR_PAGINA - 1, total);
            etiquetaRango.setText(String.format(
                    "Mostrando %d–%d de %d ingredientes", inicio, fin, total));
        }
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        int inicio = Math.max(1, Math.min(paginaActual - 1, totalPaginas - 2));
        for (int i = 0; i < botonesPagina.length; i++) {
            JButton boton = botonesPagina[i];
            int pagina = inicio + i;
            boton.setVisible(pagina <= totalPaginas);
            if (pagina > totalPaginas) continue;
            boton.putClientProperty("pagina", pagina);
            boton.setText(String.valueOf(pagina));
            boolean seleccionada = pagina == paginaActual;
            boton.setFont(seleccionada ? tema.negrita(13f) : tema.media(13f));
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

    private void abrirFormulario(Ingrediente ingrediente) {
        Window propietario = SwingUtilities.getWindowAncestor(this);
        if (IngredienteFormDialog.mostrar(propietario, crud, ingrediente)) {
            cargarPagina();
        }
    }

    private void editar(Ingrediente ingrediente) {
        try {
            Ingrediente actual = crud.obtenerPorId(ingrediente.getIdIngrediente());
            if (actual == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el ingrediente.");
                cargarPagina();
                return;
            }
            abrirFormulario(actual);
        } catch (SQLException ex) {
            mostrarError("obtener el ingrediente", ex);
        }
    }

    private void desactivar(Ingrediente ingrediente) {
        if (!ingrediente.isActivo()) return;
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desactivar «" + ingrediente.getNombre() + "»?",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) return;
        try {
            crud.cambiarEstado(ingrediente.getIdIngrediente(), false);
            cargarPagina();
        } catch (SQLException ex) {
            mostrarError("desactivar el ingrediente", ex);
        }
    }

    private void mostrarError(String accion, Throwable error) {
        if (errorMostrado) return;
        errorMostrado = true;
        JOptionPane.showMessageDialog(this,
                "No fue posible " + accion + ".\n"
                        + (error == null ? "Error no especificado." : error.getMessage()),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private final class ModeloTabla extends AbstractTableModel {
        private final String[] columnas = {"ID", "Ingrediente", "Unidad",
            "Stock actual", "Stock mínimo", "Nivel", "Estado", "Acciones"};
        private List<Ingrediente> ingredientes = List.of();
        void setIngredientes(List<Ingrediente> nuevos) {
            ingredientes = List.copyOf(nuevos);
            fireTableDataChanged();
        }
        @Override public int getRowCount() { return ingredientes.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int columna) { return columnas[columna]; }
        @Override public boolean isCellEditable(int fila, int columna) { return columna == 7; }
        @Override public Class<?> getColumnClass(int columna) {
            return columna >= 5 ? Ingrediente.class : String.class;
        }
        @Override public Object getValueAt(int fila, int columna) {
            Ingrediente item = ingredientes.get(fila);
            return switch (columna) {
                case 0 -> String.format("#%04d", item.getIdIngrediente());
                case 1 -> item.getNombre();
                case 2 -> item.getUnidadMedida();
                case 3 -> item.getStockActual().stripTrailingZeros().toPlainString();
                case 4 -> item.getStockMinimo().stripTrailingZeros().toPlainString();
                case 5, 6, 7 -> item;
                default -> "";
            };
        }
    }

    private JPanel etiquetaEstado(String texto, Color color, Color fondo,
            JTable tabla, boolean seleccionado) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 13));
        panel.setBackground(seleccionado ? tabla.getSelectionBackground() : Color.WHITE);
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setOpaque(true);
        etiqueta.setFont(tema.media(12f));
        etiqueta.setForeground(color);
        etiqueta.setBackground(fondo);
        etiqueta.setBorder(new EmptyBorder(4, 11, 4, 11));
        panel.add(etiqueta);
        return panel;
    }

    private final class RenderNivel implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionado, boolean foco, int fila, int columna) {
            Ingrediente item = (Ingrediente) valor;
            return etiquetaEstado(item.tieneStockBajo() ? "Bajo" : "Normal",
                    item.tieneStockBajo() ? ROJO : new Color(34, 139, 71),
                    item.tieneStockBajo() ? new Color(252, 233, 233)
                            : new Color(230, 245, 234), tabla, seleccionado);
        }
    }

    private final class RenderEstado implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionado, boolean foco, int fila, int columna) {
            Ingrediente item = (Ingrediente) valor;
            return etiquetaEstado(item.isActivo() ? "Activo" : "Inactivo",
                    item.isActivo() ? new Color(34, 139, 71) : SECUNDARIO,
                    item.isActivo() ? new Color(230, 245, 234)
                            : new Color(237, 239, 243), tabla, seleccionado);
        }
    }

    private JPanel panelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 11));
        panel.setBackground(fondo);
        return panel;
    }

    private BotonRedondeado botonAccion(IconosUsuarios.Tipo icono,
            Color color, String texto) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setIcon(IconosUsuarios.crear(icono, color, 17));
        boton.setText("");
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(color);
        boton.setGrosorBorde(1f);
        boton.setRadio(9);
        boton.setPreferredSize(new Dimension(34, 30));
        boton.setToolTipText(texto);
        return boton;
    }

    private final class RenderAcciones implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabla,
                Object valor, boolean seleccionado, boolean foco, int fila, int columna) {
            Ingrediente item = (Ingrediente) valor;
            JPanel panel = panelAcciones(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            panel.add(botonAccion(IconosUsuarios.Tipo.EDITAR,
                    new Color(214, 151, 0), "Editar ingrediente"));
            BotonRedondeado baja = botonAccion(IconosUsuarios.Tipo.ELIMINAR,
                    ROJO, "Desactivar ingrediente");
            baja.setEnabled(item.isActivo());
            panel.add(baja);
            return panel;
        }
    }

    private final class EditorAcciones extends AbstractCellEditor
            implements TableCellEditor {
        private final JPanel panel = panelAcciones(Color.WHITE);
        private final BotonRedondeado editar = botonAccion(
                IconosUsuarios.Tipo.EDITAR, new Color(214, 151, 0), "Editar ingrediente");
        private final BotonRedondeado baja = botonAccion(
                IconosUsuarios.Tipo.ELIMINAR, ROJO, "Desactivar ingrediente");
        private Ingrediente item;
        EditorAcciones() {
            panel.add(editar);
            panel.add(baja);
            editar.addActionListener(e -> {
                Ingrediente seleccionado = item;
                fireEditingStopped();
                editar(seleccionado);
            });
            baja.addActionListener(e -> {
                Ingrediente seleccionado = item;
                fireEditingStopped();
                desactivar(seleccionado);
            });
        }
        @Override public Component getTableCellEditorComponent(JTable tabla,
                Object valor, boolean seleccionado, int fila, int columna) {
            item = (Ingrediente) valor;
            baja.setEnabled(item.isActivo());
            panel.setBackground(tabla.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return item; }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        botonAgregar = new Componentes.BotonDerretido();
        panelFiltros = new Componentes.PanelFlotante();
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        filtroStock = new Componentes.BotonDesplegable();
        filtroEstado = new Componentes.BotonDesplegable();
        botonLimpiar = new Componentes.BotonRedondeado();
        panelTabla = new Componentes.PanelFlotante();
        scrollTabla = new javax.swing.JScrollPane();
        tablaIngredientes = new Componentes.TablaAdministrativa();
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

        labelTitulo.setForeground(new java.awt.Color(0, 20, 43));
        labelTitulo.setText("Catálogo de Ingredientes");
        add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 16, 650, 64));

        labelSubtitulo.setText("Define los insumos, su unidad y el nivel mínimo de reposición");
        add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 76, 690, 36));

        botonAgregar.setText("Agregar ingrediente");
        add(botonAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1290, 42, 220, 60));

        panelFiltros.setColorBorde(new java.awt.Color(225, 230, 237));
        panelFiltros.setRadio(18);
        panelFiltros.setSombra(false);
        panelFiltros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        campoBusqueda.setPlaceholder("Buscar ingrediente o unidad...");
        panelFiltros.add(campoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 440, 55));

        filtroStock.setText("Todos los niveles");
        filtroStock.setTextoDesplegable("Todos los niveles;Stock bajo;Stock normal");
        panelFiltros.add(filtroStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 22, 280, 52));

        filtroEstado.setText("Todos los estados");
        filtroEstado.setTextoDesplegable("Todos los estados;Activos;Inactivos");
        panelFiltros.add(filtroEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 22, 280, 52));

        botonLimpiar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonLimpiar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonLimpiar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonLimpiar.setDegradado(false);
        botonLimpiar.setForeground(new java.awt.Color(231, 55, 65));
        botonLimpiar.setText("Limpiar filtros");
        panelFiltros.add(botonLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 22, 250, 52));

        add(panelFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 175, 1470, 95));

        panelTabla.setColorBorde(new java.awt.Color(225, 230, 237));
        panelTabla.setRadio(18);
        panelTabla.setSombra(false);
        panelTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scrollTabla.setBorder(null);
        scrollTabla.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollTabla.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTabla.setViewportView(tablaIngredientes);

        panelTabla.add(scrollTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 1442, 555));

        etiquetaRango.setText("Mostrando ingredientes");
        panelTabla.add(etiquetaRango, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 581, 430, 42));

        panelPaginacion.setOpaque(false);
        panelPaginacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelPaginacion.add(botonAnterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 40, 38));

        botonPagina1.setText("1");
        panelPaginacion.add(botonPagina1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 2, 40, 38));

        botonPagina2.setText("2");
        panelPaginacion.add(botonPagina2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 2, 40, 38));

        botonPagina3.setText("3");
        panelPaginacion.add(botonPagina3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 2, 40, 38));
        panelPaginacion.add(botonSiguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 2, 40, 38));

        panelTabla.add(panelPaginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 581, 270, 42));

        add(panelTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 285, 1470, 640));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonAgregar;
    private Componentes.BotonRedondeado botonAnterior;
    private Componentes.BotonRedondeado botonLimpiar;
    private Componentes.BotonRedondeado botonPagina1;
    private Componentes.BotonRedondeado botonPagina2;
    private Componentes.BotonRedondeado botonPagina3;
    private Componentes.BotonRedondeado botonSiguiente;
    private Componentes.CampoBusquedaAdmin campoBusqueda;
    private javax.swing.JLabel etiquetaRango;
    private Componentes.BotonDesplegable filtroEstado;
    private Componentes.BotonDesplegable filtroStock;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTitulo;
    private Componentes.PanelFlotante panelFiltros;
    private javax.swing.JPanel panelPaginacion;
    private Componentes.PanelFlotante panelTabla;
    private javax.swing.JScrollPane scrollTabla;
    private Componentes.TablaAdministrativa tablaIngredientes;
    // End of variables declaration//GEN-END:variables
}
