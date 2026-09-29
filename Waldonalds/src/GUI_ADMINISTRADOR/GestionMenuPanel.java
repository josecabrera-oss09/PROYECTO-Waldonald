package GUI_ADMINISTRADOR;

import CRUD.ProductoCRUD;
import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
import Modelos.PaginaProductos;
import Modelos.Producto;
import Modelos.ResumenProductos;
import Utilidades.IconosUsuarios;
import Utilidades.TemaAdmin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/** Gestión de productos con la misma presentación que UsuariosPanel. */
@SuppressWarnings("serial")
public class GestionMenuPanel extends javax.swing.JPanel {

    private static final int PRODUCTOS_POR_PAGINA = 10;
    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 229, 235);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final String CARPETA_IMAGENES = "src/Imagenes/productos/";
    private static final String RECURSO_IMAGENES = "/Imagenes/productos/";

    private final TemaAdmin tema = new TemaAdmin();
    private final ProductoCRUD productoCRUD = new ProductoCRUD();
    private final ModeloTablaProductos modeloTabla = new ModeloTablaProductos();
    private final List<BotonRedondeado> botonesPagina = new ArrayList<>();
    private final Map<Integer, String> categorias = new LinkedHashMap<>();
    private final Map<String, ImageIcon> cacheImagenes = new HashMap<>();
    private final Timer temporizadorBusqueda;

    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int secuenciaCarga;
    private Integer categoriaActual;
    private Boolean estadoActual;
    private boolean mostrandoErrorConexion;

    public GestionMenuPanel() {
        initComponents();
        temporizadorBusqueda = new Timer(300, evento -> {
            paginaActual = 1;
            cargarDatos();
        });
        temporizadorBusqueda.setRepeats(false);
        configurarApariencia();
        configurarFiltros();
        configurarTabla();
        configurarEventos();
        cargarCategorias();
        cargarResumen();
        cargarDatos();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitulo3 = new javax.swing.JLabel();
        labelTitulo2 = new javax.swing.JLabel();
        botonAgregarProducto = new Componentes.BotonDerretido();
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

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo3.setFont(new java.awt.Font("Dialog", 1, 55)); // NOI18N
        labelTitulo3.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo3.setText("Gestión del Menú");
        add(labelTitulo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 460, 76));

        labelTitulo2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        labelTitulo2.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo2.setText("Administra productos y combos");
        add(labelTitulo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 320, 76));

        botonAgregarProducto.setForeground(new java.awt.Color(0, 0, 0));
        botonAgregarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_agregar.png"))); // NOI18N
        botonAgregarProducto.setText("Agregar producto");
        botonAgregarProducto.setIconTextGap(15);
        add(botonAgregarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 60, 230, 80));

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
        add(botonExportar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1290, 60, 230, 60));

        panelFiltros.setRadio(18);
        panelFiltros.setSombra(false);
        panelFiltros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        campoBusqueda.setPlaceholder("Buscar producto o categoría...");
        campoBusqueda.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        campoBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoBusquedaActionPerformed(evt);
            }
        });
        panelFiltros.add(campoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 22, 455, 56));

        filtroCategoria.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroCategoria.setColorHover(new java.awt.Color(248, 249, 251));
        filtroCategoria.setForeground(new java.awt.Color(0, 20, 43));
        filtroCategoria.setText("Todas las categorías");
        filtroCategoria.setTextoDesplegable("Todos los roles;ADMINISTRADOR;CAJERO");
        filtroCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroCategoriaActionPerformed(evt);
            }
        });
        panelFiltros.add(filtroCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 24, 330, 52));

        filtroEstado.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroEstado.setColorHover(new java.awt.Color(248, 249, 251));
        filtroEstado.setForeground(new java.awt.Color(0, 20, 43));
        filtroEstado.setText("Todos los estados");
        filtroEstado.setTextoDesplegable("Todos los estados;Activo;Inactivo");
        panelFiltros.add(filtroEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 24, 330, 52));

        botonLimpiarFiltros.setColorInicio(new java.awt.Color(255, 255, 255));
        botonLimpiarFiltros.setDegradado(false);
        botonLimpiarFiltros.setForeground(new java.awt.Color(231, 55, 65));
        botonLimpiarFiltros.setText("Limpiar filtros");
        botonLimpiarFiltros.setColorBorde(new java.awt.Color(231, 55, 65));
        botonLimpiarFiltros.setColorFinal(new java.awt.Color(255, 255, 255));
        botonLimpiarFiltros.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        botonLimpiarFiltros.setGrosorBorde(1.6F);
        botonLimpiarFiltros.setMargin(new java.awt.Insets(5, 14, 3, 14));
        panelFiltros.add(botonLimpiarFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 24, 230, 52));

        add(panelFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 320, 1470, 100));

        panelTabla.setRadio(18);
        panelTabla.setSombra(false);
        panelTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scrollUsuarios.setBorder(null);
        scrollUsuarios.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollUsuarios.setAutoscrolls(true);

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Usuario", "Rol", "Correo", "Estado", "Fecha creación", "Acciones"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaProductos.setAutoscrolls(false);
        tablaProductos.setColumnasCentradas("5,7");
        scrollUsuarios.setViewportView(tablaProductos);

        panelTabla.add(scrollUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 1442, 458));

        etiquetaRango.setForeground(new java.awt.Color(92, 103, 124));
        etiquetaRango.setText("Mostrando 1–10 de 10 productos");
        panelTabla.add(etiquetaRango, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 482, 330, 42));

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

        panelTabla.add(panelPaginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 482, 270, 42));

        add(panelTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 420, 1470, 540));
    }// </editor-fold>//GEN-END:initComponents

    private void filtroCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroCategoriaActionPerformed

    private void campoBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoBusquedaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoBusquedaActionPerformed


  /** Sólo cambia propiedades fuera del bloque generado por NetBeans. */
    private void configurarApariencia() {
        setBackground(new Color(248, 249, 251));
        labelTitulo3.setFont(tema.negrita(55f));
        labelTitulo3.setForeground(AZUL);
        labelTitulo3.setBounds(70, 30, 620, 76);
        labelTitulo2.setFont(tema.regular(17f));
        labelTitulo2.setForeground(SECUNDARIO);
        botonAgregarProducto.setFont(tema.negrita(16f));
        botonAgregarProducto.setBounds(1050, 60, 230, 80);
        botonAgregarProducto.addActionListener(evento -> mostrarFormularioProducto(null));

        Componentes.PanelFlotante[] tarjetas = {
            panelFlotante1, panelFlotante2, panelFlotante4, panelFlotante3
        };
        for (int i = 0; i < tarjetas.length; i++) {
            tarjetas[i].setColorFondo(Color.WHITE);
            tarjetas[i].setColorBorde(BORDE);
            tarjetas[i].setBounds(60 + i * 370, 150, 360, 150);
        }
        panelCircular2.setColorFondo(new Color(255, 244, 211));
        panelCircular3.setColorFondo(new Color(252, 233, 233));
        panelCircular5.setColorFondo(new Color(255, 244, 211));
        panelCircular4.setColorFondo(new Color(252, 233, 233));
        JLabel[] titulos = {
            labelTitulo4, labelTitulo7, labelTitulo13, labelTitulo10
        };
        JLabel[] cifras = {
            labelTotalProductos, labelStockBajo, labelActivos, labelInactivos
        };
        for (JLabel titulo : titulos) {
            titulo.setFont(tema.media(14f));
            titulo.setForeground(SECUNDARIO);
        }
        for (JLabel cifra : cifras) {
            cifra.setFont(tema.negrita(40f));
            cifra.setForeground(AZUL);
        }
        labelStockBajo.setForeground(ROJO);
        // En UsuariosPanel las tarjetas sólo contienen nombre y cantidad.
        labelTitulo6.setVisible(false);
        labelTitulo9.setVisible(false);
        labelTitulo15.setVisible(false);
        labelTitulo12.setVisible(false);

        botonExportar.setFont(tema.negrita(14f));
        botonExportar.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.EXPORTAR, ROJO, 22));
        botonExportar.setIconTextGap(12);
        botonExportar.addActionListener(evento -> exportarCsv());
    }

    private void configurarFiltros() {
        panelFiltros.setColorFondo(Color.WHITE);
        panelFiltros.setColorBorde(BORDE);
        campoBusqueda.setFont(tema.regular(14f));
        configurarSelector(filtroCategoria, 330);
        configurarSelector(filtroEstado, 330);
        filtroCategoria.addMenuOpcionListener(evento -> {
            String opcion = evento.getActionCommand();
            filtroCategoria.setText(opcion);
            categoriaActual = null;
            if (!"Todas las categorías".equals(opcion)) {
                for (Map.Entry<Integer, String> entrada : categorias.entrySet()) {
                    if (entrada.getValue().equals(opcion)) {
                        categoriaActual = entrada.getKey();
                        break;
                    }
                }
            }
            paginaActual = 1;
            cargarDatos();
        });
        filtroEstado.addMenuOpcionListener(evento -> {
            String opcion = evento.getActionCommand();
            filtroEstado.setText(opcion);
            estadoActual = switch (opcion) {
                case "Activo" -> Boolean.TRUE;
                case "Inactivo" -> Boolean.FALSE;
                default -> null;
            };
            paginaActual = 1;
            cargarDatos();
        });
        botonLimpiarFiltros.setFont(tema.negrita(14f));
        botonLimpiarFiltros.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.FILTRO, ROJO, 21));
        botonLimpiarFiltros.setIconTextGap(10);
        botonLimpiarFiltros.addActionListener(evento -> {
            temporizadorBusqueda.stop();
            campoBusqueda.setText("");
            categoriaActual = null;
            estadoActual = null;
            filtroCategoria.setText("Todas las categorías");
            filtroEstado.setText("Todos los estados");
            paginaActual = 1;
            cargarDatos();
        });
    }

    private void configurarSelector(BotonDesplegable selector, int ancho) {
        selector.setForeground(AZUL);
        selector.setFont(tema.regular(14f));
        selector.setColorFondo(Color.WHITE);
        selector.setColorHover(new Color(248, 249, 251));
        selector.setColorDesplegado(new Color(255, 247, 222));
        selector.setColorTextoOpcion(AZUL);
        selector.setColorBordeMenu(BORDE);
        selector.setAnchoMenu(ancho);
        selector.setAltoOpcion(42);
        selector.setBorderPainted(true);
        selector.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(0, 14, 0, 34)));
    }

    private void configurarTabla() {
        panelTabla.setColorFondo(Color.WHITE);
        panelTabla.setColorBorde(BORDE);
        tablaProductos.setModel(modeloTabla);
        tablaProductos.aplicarEstilo();
        tablaProductos.setRowHeight(60);
        tablaProductos.setAutoCreateRowSorter(false);
        tablaProductos.getColumnModel().getColumn(1)
                .setCellRenderer(new RenderProducto());
        tablaProductos.getColumnModel().getColumn(6)
                .setCellRenderer(new RenderEstado());
        tablaProductos.getColumnModel().getColumn(7)
                .setCellRenderer(new RenderAcciones());
        tablaProductos.getColumnModel().getColumn(7)
                .setCellEditor(new EditorAcciones());
        int[] anchos = {75, 315, 170, 115, 165, 90, 110, 130};
        for (int i = 0; i < anchos.length; i++) {
            tablaProductos.getColumnModel().getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }
        scrollUsuarios.setBorder(BorderFactory.createEmptyBorder());
        scrollUsuarios.getViewport().setBackground(Color.WHITE);
        scrollUsuarios.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        etiquetaRango.setFont(tema.regular(12f));
        etiquetaRango.setForeground(SECUNDARIO);

        configurarBotonPagina(botonAnterior, IconosUsuarios.Tipo.ANTERIOR);
        botonAnterior.addActionListener(evento -> cambiarPagina(paginaActual - 1));
        botonesPagina.add(botonPagina1);
        botonesPagina.add(botonPagina2);
        botonesPagina.add(botonPagina3);
        for (BotonRedondeado boton : botonesPagina) {
            configurarBotonPagina(boton, null);
            boton.addActionListener(evento -> {
                Object pagina = boton.getClientProperty("pagina");
                if (pagina instanceof Integer numero) {
                    cambiarPagina(numero);
                }
            });
        }
        configurarBotonPagina(botonSiguiente, IconosUsuarios.Tipo.SIGUIENTE);
        botonSiguiente.addActionListener(evento -> cambiarPagina(paginaActual + 1));
    }

    private void configurarBotonPagina(BotonRedondeado boton,
            IconosUsuarios.Tipo tipoIcono) {
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(BORDE);
        boton.setGrosorBorde(1f);
        boton.setRadio(12);
        boton.setFont(tema.media(13f));
        if (tipoIcono != null) {
            boton.setText("");
            boton.setIcon(IconosUsuarios.crear(tipoIcono, AZUL, 17));
        }
    }

    private void configurarEventos() {
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { temporizadorBusqueda.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { temporizadorBusqueda.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { temporizadorBusqueda.restart(); }
        });
    }

    private void cargarCategorias() {
        new SwingWorker<Map<Integer, String>, Void>() {
            @Override protected Map<Integer, String> doInBackground()
                    throws SQLException {
                return productoCRUD.listarCategoriasActivas();
            }
            @Override protected void done() {
                try {
                    categorias.clear();
                    categorias.putAll(get());
                    List<String> opciones = new ArrayList<>();
                    opciones.add("Todas las categorías");
                    opciones.addAll(categorias.values());
                    filtroCategoria.setTextoDesplegable(String.join(";", opciones));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarErrorDatos("cargar las categorías", ex.getCause());
                }
            }
        }.execute();
    }

    private void cargarResumen() {
        new SwingWorker<ResumenProductos, Void>() {
            @Override protected ResumenProductos doInBackground()
                    throws SQLException {
                return productoCRUD.obtenerResumen();
            }
            @Override protected void done() {
                try {
                    ResumenProductos resumen = get();
                    labelTotalProductos.setText(String.valueOf(resumen.total()));
                    labelStockBajo.setText(String.valueOf(resumen.stockBajo()));
                    labelActivos.setText(String.valueOf(resumen.activos()));
                    labelInactivos.setText(String.valueOf(resumen.inactivos()));
                    mostrandoErrorConexion = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarErrorDatos("cargar el resumen", ex.getCause());
                }
            }
        }.execute();
    }

    private void cargarDatos() {
        final int solicitud = ++secuenciaCarga;
        final String busqueda = campoBusqueda.getText().trim();
        final Integer categoria = categoriaActual;
        final Boolean estado = estadoActual;
        final int paginaSolicitada = paginaActual;
        tablaProductos.setEnabled(false);
        etiquetaRango.setText("Cargando productos...");
        new SwingWorker<PaginaProductos, Void>() {
            @Override protected PaginaProductos doInBackground()
                    throws SQLException {
                return productoCRUD.listarPagina(busqueda, categoria, null,
                        estado, paginaSolicitada, PRODUCTOS_POR_PAGINA);
            }
            @Override protected void done() {
                if (solicitud != secuenciaCarga) return;
                try {
                    PaginaProductos resultado = get();
                    totalPaginas = Math.max(1, (int) Math.ceil(
                            resultado.totalRegistros() / (double) PRODUCTOS_POR_PAGINA));
                    if (paginaActual > totalPaginas) {
                        paginaActual = totalPaginas;
                        cargarDatos();
                        return;
                    }
                    modeloTabla.setProductos(resultado.productos());
                    actualizarPaginacion(resultado.totalRegistros());
                    mostrandoErrorConexion = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    modeloTabla.setProductos(List.of());
                    actualizarPaginacion(0);
                    mostrarErrorDatos("cargar los productos", ex.getCause());
                } finally {
                    tablaProductos.setEnabled(true);
                }
            }
        }.execute();
    }

    private void actualizarPaginacion(int totalRegistros) {
        if (totalRegistros == 0) {
            etiquetaRango.setText("No se encontraron productos");
        } else {
            int primero = (paginaActual - 1) * PRODUCTOS_POR_PAGINA + 1;
            int ultimo = Math.min(primero + PRODUCTOS_POR_PAGINA - 1, totalRegistros);
            etiquetaRango.setText(String.format("Mostrando %d–%d de %d productos",
                    primero, ultimo, totalRegistros));
        }
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        int inicio = Math.max(1, Math.min(paginaActual - 1, totalPaginas - 2));
        for (int i = 0; i < botonesPagina.size(); i++) {
            BotonRedondeado boton = botonesPagina.get(i);
            int pagina = inicio + i;
            boton.setVisible(pagina <= totalPaginas);
            if (pagina > totalPaginas) continue;
            boton.putClientProperty("pagina", pagina);
            boton.setText(String.valueOf(pagina));
            boolean actual = pagina == paginaActual;
            boton.setColorInicio(actual ? AMARILLO : Color.WHITE);
            boton.setColorFinal(actual ? AMARILLO : Color.WHITE);
            boton.setColorBorde(actual ? AMARILLO : BORDE);
            boton.setFont(actual ? tema.negrita(13f) : tema.media(13f));
        }
    }

    private void cambiarPagina(int pagina) {
        if (pagina < 1 || pagina > totalPaginas || pagina == paginaActual) return;
        paginaActual = pagina;
        cargarDatos();
    }

    private void mostrarErrorDatos(String operacion, Throwable causa) {
        if (mostrandoErrorConexion) return;
        mostrandoErrorConexion = true;
        JOptionPane.showMessageDialog(this,
                "No fue posible " + operacion + ".\n"
                        + (causa == null ? "Error desconocido" : causa.getMessage()),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private final class ModeloTablaProductos extends AbstractTableModel {
        private final String[] columnas = {
            "ID", "Producto", "Categoría", "Precio", "Disponibilidad",
            "Stock", "Estado", "Acciones"
        };
        private List<Producto> productos = List.of();

        private void setProductos(List<Producto> nuevos) {
            productos = List.copyOf(nuevos);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return productos.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int c) { return columnas[c]; }
        @Override public Class<?> getColumnClass(int c) {
            return c == 1 || c == 6 || c == 7 ? Producto.class : String.class;
        }
        @Override public boolean isCellEditable(int r, int c) { return c == 7; }

        @Override public Object getValueAt(int fila, int columna) {
            Producto producto = productos.get(fila);
            return switch (columna) {
                case 0 -> String.format("#%04d", producto.getIdProducto());
                case 1, 6, 7 -> producto;
                case 2 -> producto.getCategoria();
                case 3 -> producto.getPrecioBase() == null ? "Q0.00"
                        : "Q" + producto.getPrecioBase().setScale(2,
                                java.math.RoundingMode.HALF_UP).toPlainString();
                case 4 -> nombreDisponibilidad(producto.getDisponibilidadMenu());
                case 5 -> String.valueOf(producto.getStockActual());
                default -> "";
            };
        }
    }

    private String nombreDisponibilidad(String valor) {
        if (valor == null) return "";
        return switch (valor) {
            case "DESAYUNO" -> "Desayuno";
            case "ALMUERZO" -> "Almuerzo";
            case "TODO_DIA" -> "Todo el día";
            default -> valor;
        };
    }

    private final class RenderProducto implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                javax.swing.JTable tabla, Object valor, boolean seleccionado,
                boolean foco, int fila, int columna) {
            Producto producto = (Producto) valor;
            JPanel panel = new JPanel(new BorderLayout(12, 0));
            panel.setBorder(new EmptyBorder(3, 10, 3, 8));
            panel.setBackground(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            JLabel imagen = new JLabel();
            imagen.setHorizontalAlignment(SwingConstants.CENTER);
            imagen.setPreferredSize(new Dimension(40, 40));
            imagen.setIcon(cargarImagen(producto.getImagen(), 40));
            if (imagen.getIcon() == null) imagen.setText("—");
            JPanel textos = new JPanel(new java.awt.GridLayout(2, 1, 0, 0));
            textos.setOpaque(false);
            JLabel nombre = new JLabel(producto.getNombre());
            nombre.setFont(tema.media(13f));
            nombre.setForeground(AZUL);
            String subtipo = producto.isCombo() ? "Combo" : "Producto";
            if (producto.getSubCategoria() != null
                    && !producto.getSubCategoria().isBlank()) {
                subtipo += " · " + producto.getSubCategoria();
            }
            JLabel tipo = new JLabel(subtipo);
            tipo.setFont(tema.regular(11f));
            tipo.setForeground(SECUNDARIO);
            textos.add(nombre);
            textos.add(tipo);
            panel.add(imagen, BorderLayout.WEST);
            panel.add(textos, BorderLayout.CENTER);
            return panel;
        }
    }

    private final class RenderEstado implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                javax.swing.JTable tabla, Object valor, boolean seleccionado,
                boolean foco, int fila, int columna) {
            Producto producto = (Producto) valor;
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
            panel.setBackground(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            JLabel estado = new JLabel(producto.isActivo() ? "Activo" : "Inactivo");
            estado.setFont(tema.media(11f));
            estado.setOpaque(true);
            estado.setForeground(producto.isActivo()
                    ? new Color(34, 139, 71) : SECUNDARIO);
            estado.setBackground(producto.isActivo()
                    ? new Color(230, 245, 234) : new Color(237, 239, 243));
            estado.setBorder(new EmptyBorder(3, 13, 3, 13));
            panel.add(estado);
            return panel;
        }
    }

    private final class RenderAcciones implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                javax.swing.JTable tabla, Object valor, boolean seleccionado,
                boolean foco, int fila, int columna) {
            Producto producto = (Producto) valor;
            JPanel panel = crearPanelAcciones(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            BotonRedondeado editar = crearBotonAccion(
                    IconosUsuarios.Tipo.EDITAR, new Color(214, 151, 0));
            BotonRedondeado desactivar = crearBotonAccion(
                    IconosUsuarios.Tipo.ELIMINAR, ROJO);
            desactivar.setEnabled(producto.isActivo());
            panel.add(editar);
            panel.add(desactivar);
            return panel;
        }
    }

    private final class EditorAcciones extends AbstractCellEditor
            implements TableCellEditor {
        private final JPanel panel = crearPanelAcciones(Color.WHITE);
        private final BotonRedondeado editar = crearBotonAccion(
                IconosUsuarios.Tipo.EDITAR, new Color(214, 151, 0));
        private final BotonRedondeado desactivar = crearBotonAccion(
                IconosUsuarios.Tipo.ELIMINAR, ROJO);
        private Producto producto;

        private EditorAcciones() {
            editar.addActionListener(evento -> {
                Producto seleccionado = producto;
                fireEditingStopped();
                editarProducto(seleccionado.getIdProducto());
            });
            desactivar.addActionListener(evento -> {
                Producto seleccionado = producto;
                fireEditingStopped();
                desactivarProducto(seleccionado, GestionMenuPanel.this);
            });
            panel.add(editar);
            panel.add(desactivar);
        }

        @Override public Component getTableCellEditorComponent(
                javax.swing.JTable tabla, Object valor, boolean seleccionado,
                int fila, int columna) {
            producto = (Producto) valor;
            desactivar.setEnabled(producto.isActivo());
            panel.setBackground(tabla.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return producto; }
    }

    private JPanel crearPanelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 14));
        panel.setBackground(fondo);
        return panel;
    }

    private BotonRedondeado crearBotonAccion(IconosUsuarios.Tipo icono,
            Color color) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText("");
        boton.setIcon(IconosUsuarios.crear(icono, color, 17));
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(color);
        boton.setGrosorBorde(1f);
        boton.setRadio(9);
        boton.setPreferredSize(new Dimension(33, 30));
        boton.setToolTipText(icono == IconosUsuarios.Tipo.EDITAR
                ? "Editar producto" : "Desactivar producto");
        return boton;
    }

    private void editarProducto(int id) {
        try {
            Producto producto = productoCRUD.obtenerPorId(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el producto.",
                        "Producto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            mostrarFormularioProducto(producto);
        } catch (SQLException ex) {
            mostrarError("No fue posible obtener el producto", ex);
        }
    }

    private boolean desactivarProducto(Producto seleccionado, Component padre) {
        if (!seleccionado.isActivo()) return false;
        int respuesta = JOptionPane.showConfirmDialog(padre,
                "¿Desactivar «" + seleccionado.getNombre() + "»?",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) return false;
        try {
            productoCRUD.desactivar(seleccionado.getIdProducto());
            cargarResumen();
            cargarDatos();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(padre,
                    "No fue posible desactivar el producto.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void exportarCsv() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Exportar productos");
        selector.setSelectedFile(new File("productos.csv"));
        selector.setFileFilter(new FileNameExtensionFilter("Archivo CSV (*.csv)", "csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path destino = selector.getSelectedFile().toPath();
        if (!destino.getFileName().toString().toLowerCase(Locale.ROOT)
                .endsWith(".csv")) {
            destino = destino.resolveSibling(destino.getFileName() + ".csv");
        }
        if (Files.exists(destino) && JOptionPane.showConfirmDialog(this,
                "El archivo ya existe. ¿Deseas reemplazarlo?",
                "Confirmar exportación", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) return;
        final Path archivo = destino;
        final String busqueda = campoBusqueda.getText().trim();
        final Integer categoria = categoriaActual;
        final Boolean estado = estadoActual;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        botonExportar.setEnabled(false);
        new SwingWorker<Integer, Void>() {
            @Override protected Integer doInBackground() throws Exception {
                List<Producto> productos = new ArrayList<>();
                PaginaProductos primera = productoCRUD.listarPagina(
                        busqueda, categoria, null, estado, 1, 100);
                productos.addAll(primera.productos());
                int paginas = (int) Math.ceil(primera.totalRegistros() / 100.0);
                for (int pagina = 2; pagina <= paginas; pagina++) {
                    productos.addAll(productoCRUD.listarPagina(
                            busqueda, categoria, null, estado, pagina, 100).productos());
                }
                escribirCsv(archivo, productos);
                return productos.size();
            }
            @Override protected void done() {
                botonExportar.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
                try {
                    JOptionPane.showMessageDialog(GestionMenuPanel.this,
                            "Se exportaron " + get() + " productos en:\n" + archivo,
                            "Exportación completada", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarError("No fue posible exportar", ex.getCause());
                }
            }
        }.execute();
    }

    private void escribirCsv(Path archivo, List<Producto> productos)
            throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivo, StandardCharsets.UTF_8)) {
            escritor.write('\ufeff');
            escritor.write("ID,Nombre,Categoría,Subcategoría,Descripción,Precio,Disponibilidad,"
                    + "Tamaño bebida,Combo,Stock,Stock mínimo,Estado,Imagen");
            escritor.newLine();
            for (Producto producto : productos) {
                escritor.write(String.join(",",
                        csv(String.valueOf(producto.getIdProducto())),
                        csv(producto.getNombre()), csv(producto.getCategoria()),
                        csv(producto.getSubCategoria()),
                        csv(producto.getDescripcion()),
                        csv(producto.getPrecioBase() == null ? ""
                                : producto.getPrecioBase().toPlainString()),
                        csv(producto.getDisponibilidadMenu()),
                        csv(producto.getTamanoBebida()),
                        csv(producto.isCombo() ? "Sí" : "No"),
                        csv(String.valueOf(producto.getStockActual())),
                        csv(String.valueOf(producto.getStockMinimo())),
                        csv(producto.isActivo() ? "Activo" : "Inactivo"),
                        csv(producto.getImagen())));
                escritor.newLine();
            }
        }
    }

    private String csv(String valor) {
        return "\"" + (valor == null ? "" : valor).replace("\"", "\"\"") + "\"";
    }

    private ImageIcon cargarImagen(String ruta, int tamano) {
        if (ruta == null || ruta.isBlank()) return null;
        String clave = ruta + "#" + tamano;
        if (cacheImagenes.containsKey(clave)) return cacheImagenes.get(clave);
        try {
            File archivo = new File(ruta);
            if (!archivo.isFile()) archivo = new File("src/" + ruta.replaceFirst("^/", ""));
            ImageIcon original = null;
            if (archivo.isFile()) original = new ImageIcon(archivo.getAbsolutePath());
            else {
                java.net.URL recurso = getClass().getResource(
                        ruta.startsWith("/") ? ruta : "/" + ruta);
                if (recurso != null) original = new ImageIcon(recurso);
            }
            if (original != null && original.getIconWidth() > 0) {
                Image escalada = original.getImage().getScaledInstance(
                        tamano, tamano, Image.SCALE_SMOOTH);
                cacheImagenes.put(clave, new ImageIcon(escalada));
            }
        } catch (RuntimeException ex) {
            // Se muestra el producto sin miniatura si el archivo falta.
        }
        return cacheImagenes.get(clave);
    }

    private String copiarImagenProducto(File archivo) throws IOException {
        Path carpeta = Paths.get(CARPETA_IMAGENES);
        Files.createDirectories(carpeta);
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        String extension = punto < 0 ? ""
                : nombre.substring(punto).toLowerCase(Locale.ROOT);
        String base = (punto < 0 ? nombre : nombre.substring(0, punto))
                .replaceAll("[^a-zA-Z0-9_-]", "_");
        String nombreFinal = base + "_" + System.currentTimeMillis() + extension;
        Files.copy(archivo.toPath(), carpeta.resolve(nombreFinal),
                StandardCopyOption.REPLACE_EXISTING);
        return RECURSO_IMAGENES + nombreFinal;
    }

    private void mostrarError(String mensaje, Throwable causa) {
        JOptionPane.showMessageDialog(this,
                mensaje + ".\n" + (causa == null ? "" : causa.getMessage()),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override public void removeNotify() {
        temporizadorBusqueda.stop();
        super.removeNotify();
    }

    private static final class FondoOscuro extends JPanel {
        private FondoOscuro() {
            setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() { });
        }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setColor(new Color(0, 12, 28, 90));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
    
    private void mostrarFormularioProducto(Producto original) {
        Window propietario = SwingUtilities.getWindowAncestor(this);
        JRootPane raiz = SwingUtilities.getRootPane(this);
        Component cristalAnterior = raiz == null ? null : raiz.getGlassPane();
        boolean cristalVisible = cristalAnterior != null && cristalAnterior.isVisible();
        try {
            Map<Integer, String> disponibles = productoCRUD
                    .listarCategoriasParaFormulario(original == null
                            ? null : original.getIdCategoria());
            if (disponibles.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero registra una categoría activa.",
                        "Sin categorías", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JDialog dialogo = new JDialog(propietario,
                    java.awt.Dialog.ModalityType.APPLICATION_MODAL);
            dialogo.setUndecorated(true);
            dialogo.setBackground(new Color(0, 0, 0, 0));
            dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            final boolean[] guardado = {false};
            dialogo.setContentPane(crearFormulario(dialogo, original,
                    disponibles, guardado));
            dialogo.pack();
            dialogo.setLocationRelativeTo(propietario);
            if (raiz != null) {
                FondoOscuro fondo = new FondoOscuro();
                raiz.setGlassPane(fondo);
                fondo.setVisible(true);
            }
            try {
                dialogo.setVisible(true);
            } finally {
                if (raiz != null && cristalAnterior != null) {
                    raiz.setGlassPane(cristalAnterior);
                    cristalAnterior.setVisible(cristalVisible);
                }
            }
            if (guardado[0]) {
                cacheImagenes.clear();
                cargarResumen();
                cargarDatos();
                JOptionPane.showMessageDialog(this, original == null
                        ? "Producto agregado correctamente."
                        : "Producto actualizado correctamente.",
                        "Gestión del menú", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            mostrarError("No fue posible abrir el formulario", ex);
        }
    }

    private JPanel crearFormulario(JDialog dialogo, Producto original,
            Map<Integer, String> categoriasDisponibles, boolean[] guardado) {
        boolean editando = original != null;
        Componentes.PanelFlotante tarjeta = new Componentes.PanelFlotante();
        tarjeta.setColorFondo(Color.WHITE);
        tarjeta.setColorBorde(BORDE);
        tarjeta.setRadio(26);
        tarjeta.setPreferredSize(new Dimension(790, 660));
        tarjeta.setLayout(new BorderLayout());

        JPanel cabecera = new JPanel(null);
        cabecera.setOpaque(false);
        cabecera.setPreferredSize(new Dimension(790, 112));
        Componentes.PanelCircular icono = new Componentes.PanelCircular();
        icono.setColorFondo(new Color(255, 244, 211));
        icono.setLayout(new BorderLayout());
        java.net.URL urlIcono = getClass().getResource("/Imagenes/lista_icono.png");
        if (urlIcono != null) {
            JLabel imagen = new JLabel(new ImageIcon(new ImageIcon(urlIcono)
                    .getImage().getScaledInstance(39, 39, Image.SCALE_SMOOTH)));
            imagen.setHorizontalAlignment(SwingConstants.CENTER);
            icono.add(imagen, BorderLayout.CENTER);
        }
        cabecera.add(icono);
        icono.setBounds(30, 20, 80, 80);
        JLabel titulo = new JLabel(editando ? "Editar producto" : "Agregar producto");
        titulo.setFont(tema.negrita(25f));
        titulo.setForeground(AZUL);
        titulo.setBounds(125, 27, 595, 36);
        cabecera.add(titulo);
        JLabel subtitulo = new JLabel(editando
                ? "Actualiza la información de "
                        + String.format("#%04d", original.getIdProducto())
                : "Completa los datos del nuevo producto");
        subtitulo.setFont(tema.regular(15f));
        subtitulo.setForeground(SECUNDARIO);
        subtitulo.setBounds(125, 62, 600, 28);
        cabecera.add(subtitulo);
        tarjeta.add(cabecera, BorderLayout.NORTH);

        JTextField nombre = new JTextField(editando ? original.getNombre() : "");
        JTextField subCategoria = new JTextField(editando
                && original.getSubCategoria() != null
                        ? original.getSubCategoria() : "");
        JTextField precio = new JTextField(editando && original.getPrecioBase() != null
                ? original.getPrecioBase().toPlainString() : "");
        JTextArea descripcion = new JTextArea(editando
                && original.getDescripcion() != null ? original.getDescripcion() : "");
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setFont(tema.regular(14f));
        descripcion.setForeground(AZUL);
        descripcion.setBorder(new EmptyBorder(8, 12, 8, 12));
        JScrollPane scrollDescripcion = new JScrollPane(descripcion);
        scrollDescripcion.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        scrollDescripcion.setPreferredSize(new Dimension(330, 78));
        scrollDescripcion.setWheelScrollingEnabled(false);
        aplicarEstiloCampo(nombre);
        aplicarEstiloCampo(subCategoria);
        aplicarEstiloCampo(precio);

        BotonDesplegable categoria = nuevoSelector("Seleccionar categoría",
                String.join(";", categoriasDisponibles.values()));
        BotonDesplegable disponibilidad = nuevoSelector("Todo el día",
                "Desayuno;Almuerzo;Todo el día");
        BotonDesplegable tamano = nuevoSelector("Sin tamaño",
                "Sin tamaño;Pequeña;Mediana;Grande");
        BotonDesplegable estado = nuevoSelector("Activo", "Activo;Inactivo");
        if (editando) {
            categoria.setText(original.getCategoria());
            disponibilidad.setText(nombreDisponibilidad(original.getDisponibilidadMenu()));
            tamano.setText(nombreTamano(original.getTamanoBebida()));
            estado.setText(original.isActivo() ? "Activo" : "Inactivo");
        }

        JSpinner stock = new JSpinner(new SpinnerNumberModel(
                editando ? original.getStockActual() : 0, 0, 999999999, 1));
        JSpinner stockMinimo = new JSpinner(new SpinnerNumberModel(
                editando ? original.getStockMinimo() : 0, 0, 999999999, 1));
        estilizarSpinner(stock);
        estilizarSpinner(stockMinimo);
        JCheckBox esCombo = new JCheckBox("Es combo",
                editando && original.isCombo());
        esCombo.setFont(tema.media(14f));
        esCombo.setForeground(AZUL);
        esCombo.setOpaque(false);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(3, 28, 12, 28));
        agregarFila(campos, 0, crearCampo("Nombre", nombre),
                crearCampo("Categoría", categoria));
        agregarFila(campos, 1, crearCampo("Subcategoría (opcional)", subCategoria),
                crearCampo("Precio (Q)", precio));
        agregarFila(campos, 2, crearCampo("Disponibilidad", disponibilidad),
                crearCampo("Tamaño de bebida", tamano));
        agregarFila(campos, 3, crearCampo("Stock actual", stock),
                crearCampo("Stock mínimo", stockMinimo));
        agregarFila(campos, 4, crearCampo("Estado", estado),
                crearCampo("Tipo", esCombo));
        agregarFilaCompleta(campos, 5, crearCampo("Descripción", scrollDescripcion));

        final String[] rutaImagen = {editando ? original.getImagen() : null};
        final File[] imagenNueva = {null};
        JLabel vistaPrevia = new JLabel("Sin imagen", SwingConstants.CENTER);
        vistaPrevia.setPreferredSize(new Dimension(84, 84));
        vistaPrevia.setBorder(BorderFactory.createLineBorder(BORDE));
        mostrarVistaPrevia(vistaPrevia, rutaImagen[0]);
        JLabel nombreImagen = new JLabel(rutaImagen[0] == null
                ? "Sin imagen seleccionada" : new File(rutaImagen[0]).getName());
        nombreImagen.setFont(tema.regular(13f));
        nombreImagen.setForeground(SECUNDARIO);
        BotonRedondeado elegir = botonFormulario("Seleccionar imagen", false);
        BotonRedondeado quitar = botonFormulario("Quitar imagen", false);
        elegir.setPreferredSize(new Dimension(165, 38));
        quitar.setPreferredSize(new Dimension(145, 38));
        JPanel controlesImagen = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlesImagen.setOpaque(false);
        controlesImagen.add(elegir);
        controlesImagen.add(quitar);
        JPanel detallesImagen = new JPanel(new BorderLayout(0, 9));
        detallesImagen.setOpaque(false);
        detallesImagen.add(nombreImagen, BorderLayout.NORTH);
        detallesImagen.add(controlesImagen, BorderLayout.CENTER);
        JPanel panelImagen = new JPanel(new BorderLayout(18, 0));
        panelImagen.setOpaque(false);
        panelImagen.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelImagen.add(vistaPrevia, BorderLayout.WEST);
        panelImagen.add(detallesImagen, BorderLayout.CENTER);
        agregarFilaCompleta(campos, 6, crearCampo("Imagen del producto", panelImagen));

        elegir.addActionListener(evento -> {
            JFileChooser selector = new JFileChooser();
            selector.setFileFilter(new FileNameExtensionFilter(
                    "Imágenes PNG, JPG y JPEG", "png", "jpg", "jpeg"));
            if (selector.showOpenDialog(dialogo) == JFileChooser.APPROVE_OPTION) {
                imagenNueva[0] = selector.getSelectedFile();
                rutaImagen[0] = imagenNueva[0].getAbsolutePath();
                nombreImagen.setText(imagenNueva[0].getName());
                mostrarVistaPrevia(vistaPrevia, rutaImagen[0]);
            }
        });
        quitar.addActionListener(evento -> {
            imagenNueva[0] = null;
            rutaImagen[0] = null;
            nombreImagen.setText("Sin imagen seleccionada");
            mostrarVistaPrevia(vistaPrevia, null);
        });

        JScrollPane desplazamiento = new JScrollPane(campos);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.setOpaque(false);
        desplazamiento.getViewport().setOpaque(false);
        desplazamiento.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // La rueda se dirige aquí desde todo el formulario.
        desplazamiento.setWheelScrollingEnabled(false);
        javax.swing.JScrollBar barraVertical =
                desplazamiento.getVerticalScrollBar();
        barraVertical.setPreferredSize(new Dimension(0, 0));
        barraVertical.setUnitIncrement(36);
        barraVertical.setBlockIncrement(180);
        tarjeta.add(desplazamiento, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(new EmptyBorder(0, 30, 22, 30));
        pie.setPreferredSize(new Dimension(790, 105));
        JLabel error = new JLabel(" ");
        error.setFont(tema.media(13f));
        error.setForeground(ROJO);
        pie.add(error, BorderLayout.NORTH);
        JPanel filaBotones = new JPanel(new BorderLayout());
        filaBotones.setOpaque(false);
        BotonRedondeado desactivar = botonFormulario("Desactivar producto", false);
        desactivar.setPreferredSize(new Dimension(180, 48));
        desactivar.setVisible(editando && original.isActivo());
        filaBotones.add(desactivar, BorderLayout.WEST);
        JPanel botonesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        botonesDerecha.setOpaque(false);
        BotonRedondeado cancelar = botonFormulario("Cancelar", false);
        cancelar.setPreferredSize(new Dimension(130, 48));
        BotonRedondeado guardar = botonFormulario("Guardar producto", true);
        guardar.setPreferredSize(new Dimension(165, 48));
        botonesDerecha.add(cancelar);
        botonesDerecha.add(guardar);
        filaBotones.add(botonesDerecha, BorderLayout.EAST);
        pie.add(filaBotones, BorderLayout.CENTER);
        tarjeta.add(pie, BorderLayout.SOUTH);
        cancelar.addActionListener(evento -> dialogo.dispose());
        desactivar.addActionListener(evento -> {
            if (desactivarProducto(original, dialogo)) {
                dialogo.dispose();
            }
        });
        guardar.addActionListener(evento -> guardarProducto(dialogo, original,
                categoriasDisponibles, nombre, subCategoria, precio, descripcion, categoria,
                disponibilidad, tamano, estado, stock, stockMinimo, esCombo,
                rutaImagen[0], imagenNueva[0], error, guardar, guardado));
        java.awt.event.MouseWheelListener ruedaFormulario = evento -> {
            int avance = (int) Math.round(evento.getPreciseWheelRotation()
                    * 3 * barraVertical.getUnitIncrement());
            if (avance != 0) {
                barraVertical.setValue(barraVertical.getValue() + avance);
                evento.consume();
            }
        };
        escucharRuedaEnFormulario(tarjeta, ruedaFormulario);
        return tarjeta;
    }

    private void escucharRuedaEnFormulario(Component componente,
            java.awt.event.MouseWheelListener oyente) {
        componente.addMouseWheelListener(oyente);
        if (componente instanceof java.awt.Container contenedor) {
            for (Component hijo : contenedor.getComponents()) {
                escucharRuedaEnFormulario(hijo, oyente);
            }
        }
    }

    private JPanel crearCampo(String titulo, javax.swing.JComponent control) {
        JPanel campo = new JPanel(new BorderLayout(0, 5));
        campo.setOpaque(false);
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(tema.media(14f));
        etiqueta.setForeground(AZUL);
        campo.add(etiqueta, BorderLayout.NORTH);
        if (control instanceof JTextField || control instanceof BotonDesplegable
                || control instanceof JSpinner || control instanceof JCheckBox) {
            control.setPreferredSize(new Dimension(320, 46));
        }
        campo.add(control, BorderLayout.CENTER);
        return campo;
    }

    private void agregarFila(JPanel panel, int fila,
            JPanel izquierda, JPanel derecha) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(5, 8, 8, 12);
        panel.add(izquierda, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 12, 8, 8);
        panel.add(derecha, gbc);
    }

    private void agregarFilaCompleta(JPanel panel, int fila, JPanel contenido) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 8, 8);
        panel.add(contenido, gbc);
    }

    private void aplicarEstiloCampo(JTextField campo) {
        campo.setFont(tema.regular(14f));
        campo.setForeground(AZUL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(0, 13, 0, 13)));
    }

    private BotonDesplegable nuevoSelector(String texto, String opciones) {
        BotonDesplegable selector = new BotonDesplegable();
        configurarSelector(selector, 330);
        selector.setTextoDesplegable(opciones);
        selector.setText(texto);
        selector.addMenuOpcionListener(
                evento -> selector.setText(evento.getActionCommand()));
        return selector;
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setFont(tema.regular(14f));
        spinner.setForeground(AZUL);
        spinner.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            editor.getTextField().setFont(tema.regular(14f));
            editor.getTextField().setForeground(AZUL);
            editor.getTextField().setBorder(new EmptyBorder(0, 13, 0, 13));
        }
    }

    private BotonRedondeado botonFormulario(String texto, boolean principal) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText(texto);
        boton.setFont(tema.negrita(14f));
        boton.setDegradado(false);
        boton.setColorInicio(principal ? AMARILLO : Color.WHITE);
        boton.setColorFinal(principal ? AMARILLO : Color.WHITE);
        boton.setColorBorde(principal ? AMARILLO : ROJO);
        boton.setGrosorBorde(1f);
        boton.setForeground(principal ? AZUL : ROJO);
        boton.setRadio(12);
        return boton;
    }

    private void mostrarVistaPrevia(JLabel etiqueta, String ruta) {
        etiqueta.setIcon(null);
        etiqueta.setText("Sin imagen");
        ImageIcon icono = cargarImagen(ruta, 75);
        if (icono != null) {
            etiqueta.setIcon(icono);
            etiqueta.setText("");
        }
    }

    private String nombreTamano(String codigo) {
        if (codigo == null) return "Sin tamaño";
        return switch (codigo) {
            case "PEQUENA" -> "Pequeña";
            case "MEDIANA" -> "Mediana";
            case "GRANDE" -> "Grande";
            default -> "Sin tamaño";
        };
    }

    private void guardarProducto(JDialog dialogo, Producto original,
            Map<Integer, String> categoriasDisponibles,
            JTextField nombre, JTextField subCategoria, JTextField precio,
            JTextArea descripcion,
            BotonDesplegable categoria, BotonDesplegable disponibilidad,
            BotonDesplegable tamano, BotonDesplegable estado,
            JSpinner stock, JSpinner stockMinimo, JCheckBox esCombo,
            String rutaImagen, File imagenNueva, JLabel error,
            BotonRedondeado guardar, boolean[] guardado) {
        error.setText(" ");
        String nombreValor = nombre.getText().trim();
        String subCategoriaValor = subCategoria.getText().trim();
        String descripcionValor = descripcion.getText().trim();
        if (nombreValor.isBlank() || nombreValor.length() > 100) {
            error.setText("Ingresa un nombre de hasta 100 caracteres.");
            return;
        }
        if (descripcionValor.length() > 255) {
            error.setText("La descripción admite hasta 255 caracteres.");
            return;
        }
        if (subCategoriaValor.length() > 50) {
            error.setText("La subcategoría admite hasta 50 caracteres.");
            return;
        }
        Integer idCategoria = null;
        for (Map.Entry<Integer, String> entrada : categoriasDisponibles.entrySet()) {
            if (entrada.getValue().equals(categoria.getText())) {
                idCategoria = entrada.getKey();
                break;
            }
        }
        if (idCategoria == null) {
            error.setText("Selecciona una categoría.");
            return;
        }
        BigDecimal precioValor;
        try {
            precioValor = new BigDecimal(precio.getText().trim());
        } catch (NumberFormatException ex) {
            error.setText("Ingresa un precio válido.");
            return;
        }
        if (precioValor.compareTo(BigDecimal.ZERO) < 0
                || precioValor.compareTo(new BigDecimal("99999999.99")) > 0
                || precioValor.stripTrailingZeros().scale() > 2) {
            error.setText("El precio debe ser positivo y tener hasta dos decimales.");
            return;
        }
        String codigoDisponibilidad = switch (disponibilidad.getText()) {
            case "Desayuno" -> "DESAYUNO";
            case "Almuerzo" -> "ALMUERZO";
            default -> "TODO_DIA";
        };
        String codigoTamano = switch (tamano.getText()) {
            case "Pequeña" -> "PEQUENA";
            case "Mediana" -> "MEDIANA";
            case "Grande" -> "GRANDE";
            default -> null;
        };
        String imagenCopiada = null;
        guardar.setEnabled(false);
        try {
            if (imagenNueva != null) {
                imagenCopiada = copiarImagenProducto(imagenNueva);
                rutaImagen = imagenCopiada;
            }
            Producto producto = original == null ? new Producto() : original;
            producto.setIdCategoria(idCategoria);
            producto.setCategoria(categoria.getText());
            producto.setSubCategoria(subCategoriaValor.isBlank()
                    ? null : subCategoriaValor);
            producto.setNombre(nombreValor);
            producto.setDescripcion(descripcionValor.isBlank()
                    ? null : descripcionValor);
            producto.setPrecioBase(precioValor);
            producto.setDisponibilidadMenu(codigoDisponibilidad);
            producto.setTamanoBebida(codigoTamano);
            producto.setCombo(esCombo.isSelected());
            producto.setStockActual(((Number) stock.getValue()).intValue());
            producto.setStockMinimo(((Number) stockMinimo.getValue()).intValue());
            producto.setActivo("Activo".equals(estado.getText()));
            producto.setImagen(rutaImagen);
            if (original == null) productoCRUD.insertar(producto);
            else productoCRUD.actualizar(producto);
            guardado[0] = true;
            dialogo.dispose();
        } catch (SQLException | IOException ex) {
            if (imagenCopiada != null) {
                try {
                    Files.deleteIfExists(Paths.get("src", imagenCopiada.substring(1)));
                } catch (IOException ignorada) { /* Se conserva el error original. */ }
            }
            error.setText("No fue posible guardar: " + ex.getMessage());
        } finally {
            guardar.setEnabled(true);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonAgregarProducto;
    private Componentes.BotonRedondeado botonAnterior;
    private Componentes.BotonRedondeado botonExportar;
    private Componentes.BotonRedondeado botonLimpiarFiltros;
    private Componentes.BotonRedondeado botonPagina1;
    private Componentes.BotonRedondeado botonPagina2;
    private Componentes.BotonRedondeado botonPagina3;
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
