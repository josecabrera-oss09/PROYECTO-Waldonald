package GUI_ADMINISTRADOR;

import CRUD.IngredienteCRUD;
import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
import Componentes.PanelCircular;
import Componentes.PanelFlotante;
import Modelos.Ingrediente;
import Modelos.PaginaIngredientes;
import Modelos.ResumenIngredientes;
import Utilidades.IconosUsuarios;
import Utilidades.TemaAdmin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class IngredientesPanel extends javax.swing.JPanel {

    private static final int POR_PAGINA = 10;
    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 229, 235);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color ROJO = new Color(231, 55, 65);

    private final IngredienteCRUD ingredienteCRUD = new IngredienteCRUD();
    private final TemaAdmin tema = new TemaAdmin();
    private final ModeloTabla modeloTabla = new ModeloTabla();
    private final Timer temporizadorBusqueda;
    private final JButton[] botonesPagina;
    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int secuenciaCarga;
    private Boolean stockBajoActual;
    private Boolean estadoActual;
    private boolean errorConexionMostrado;

// Sustituye SOLO el constructor actual; deja initComponents() intacto:
    public IngredientesPanel() {
        initComponents();
        botonesPagina = new JButton[]{botonPagina1, botonPagina2, botonPagina3};
        temporizadorBusqueda = new Timer(300, e -> {
            paginaActual = 1;
            cargarPagina();
        });
        temporizadorBusqueda.setRepeats(false);
        configurarApariencia();
        configurarTabla();
        configurarFiltros();
        configurarPaginacion();
        botonAgregarIngrediente.addActionListener(e -> abrirFormulario(null));
        cargarTodo();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonAgregarIngrediente = new Componentes.BotonDerretido();
        panelFlotante1 = new Componentes.PanelFlotante();
        panelCircular2 = new Componentes.PanelCircular();
        labelEscalable2 = new Labels.LabelEscalable();
        labelTitulo3 = new javax.swing.JLabel();
        labelTitulo4 = new javax.swing.JLabel();
        labelTitulo2 = new javax.swing.JLabel();
        labelTitulo5 = new javax.swing.JLabel();
        labelTitulo6 = new javax.swing.JLabel();
        panelFlotante2 = new Componentes.PanelFlotante();
        panelCircular3 = new Componentes.PanelCircular();
        labelEscalable3 = new Labels.LabelEscalable();
        labelTitulo7 = new javax.swing.JLabel();
        labelTitulo8 = new javax.swing.JLabel();
        labelTitulo9 = new javax.swing.JLabel();
        panelFlotante4 = new Componentes.PanelFlotante();
        panelCircular5 = new Componentes.PanelCircular();
        labelEscalable5 = new Labels.LabelEscalable();
        labelTitulo13 = new javax.swing.JLabel();
        labelTitulo14 = new javax.swing.JLabel();
        labelTitulo15 = new javax.swing.JLabel();
        panelFlotante3 = new Componentes.PanelFlotante();
        panelCircular4 = new Componentes.PanelCircular();
        labelEscalable4 = new Labels.LabelEscalable();
        labelTitulo10 = new javax.swing.JLabel();
        labelTitulo11 = new javax.swing.JLabel();
        labelTitulo12 = new javax.swing.JLabel();
        panelFiltros = new Componentes.PanelFlotante();
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        filtroStock = new Componentes.BotonDesplegable();
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

        botonAgregarIngrediente.setForeground(new java.awt.Color(0, 0, 0));
        botonAgregarIngrediente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_agregar.png"))); // NOI18N
        botonAgregarIngrediente.setText("Agregar ingrediente");
        botonAgregarIngrediente.setIconTextGap(15);
        add(botonAgregarIngrediente, new org.netbeans.lib.awtextra.AbsoluteConstraints(1290, 50, 230, 80));

        panelFlotante1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lista_icono.png"))); // NOI18N
        panelCircular2.add(labelEscalable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 50, 50));

        panelFlotante1.add(panelCircular2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo3.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo3.setText("Ingredientes totales");
        panelFlotante1.add(labelTitulo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelTitulo4.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo4.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo4.setText("48");
        panelFlotante1.add(labelTitulo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        labelTitulo2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelTitulo2.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo2.setText("En el inventario");
        panelFlotante1.add(labelTitulo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 460, 80));

        add(panelFlotante1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 360, 160));

        labelTitulo5.setFont(new java.awt.Font("Dialog", 1, 55)); // NOI18N
        labelTitulo5.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo5.setText("Gestión de Ingredientes");
        add(labelTitulo5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 650, 76));

        labelTitulo6.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        labelTitulo6.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo6.setText("Administra el inventario de ingredientes");
        add(labelTitulo6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 460, 80));

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

        labelTitulo8.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo8.setForeground(new java.awt.Color(195, 61, 66));
        labelTitulo8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo8.setText("48");
        panelFlotante2.add(labelTitulo8, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

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

        labelTitulo14.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo14.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo14.setText("48");
        panelFlotante4.add(labelTitulo14, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

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

        labelTitulo11.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo11.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo11.setText("48");
        panelFlotante3.add(labelTitulo11, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        labelTitulo12.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        labelTitulo12.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo12.setText("No disponibles");
        panelFlotante3.add(labelTitulo12, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 460, 80));

        add(panelFlotante3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 140, 360, 160));

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

        filtroStock.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroStock.setColorHover(new java.awt.Color(248, 249, 251));
        filtroStock.setForeground(new java.awt.Color(0, 20, 43));
        filtroStock.setText("Stock");
        filtroStock.setTextoDesplegable("");
        filtroStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroStockActionPerformed(evt);
            }
        });
        panelFiltros.add(filtroStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 24, 330, 52));

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
        scrollUsuarios.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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

    // Pega estos métodos dentro de IngredientesPanel, después de initComponents()
// y antes de «Variables declaration - do not modify».
    private void configurarApariencia() {
        setBackground(new Color(248, 249, 251));
        campoBusqueda.setPlaceholder("Buscar ingrediente o unidad...");
        panelFiltros.setColorFondo(Color.WHITE);
        panelFiltros.setColorBorde(BORDE);
        panelTabla.setColorFondo(Color.WHITE);
        panelTabla.setColorBorde(BORDE);
        scrollUsuarios.setBorder(BorderFactory.createEmptyBorder());
        scrollUsuarios.getViewport().setBackground(Color.WHITE);
        campoBusqueda.setFont(tema.regular(14f));
        botonAgregarIngrediente.setText("Agregar ingrediente");
        botonLimpiarFiltros.setFont(tema.negrita(14f));
        botonLimpiarFiltros.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.FILTRO, ROJO, 20));
    }

    private void configurarFiltros() {
        filtroStock.setText("Todos los niveles");
        filtroStock.setTextoDesplegable("Todos los niveles;Stock bajo;Stock normal");
        prepararSelector(filtroStock);
        filtroStock.addMenuOpcionListener(e -> {
            String opcion = e.getActionCommand();
            filtroStock.setText(opcion);
            stockBajoActual = switch (opcion) {
                case "Stock bajo" ->
                    Boolean.TRUE;
                case "Stock normal" ->
                    Boolean.FALSE;
                default ->
                    null;
            };
            paginaActual = 1;
            cargarPagina();
        });
        filtroEstado.setText("Todos los estados");
        filtroEstado.setTextoDesplegable("Todos los estados;Activo;Inactivo");
        prepararSelector(filtroEstado);
        filtroEstado.addMenuOpcionListener(e -> {
            String opcion = e.getActionCommand();
            filtroEstado.setText(opcion);
            estadoActual = switch (opcion) {
                case "Activo" ->
                    Boolean.TRUE;
                case "Inactivo" ->
                    Boolean.FALSE;
                default ->
                    null;
            };
            paginaActual = 1;
            cargarPagina();
        });
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
        });
        botonLimpiarFiltros.addActionListener(e -> {
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

    private void prepararSelector(BotonDesplegable boton) {
        boton.setFont(tema.regular(14f));
        boton.setForeground(AZUL);
        boton.setColorFondo(Color.WHITE);
        boton.setColorHover(new Color(248, 249, 251));
        boton.setColorDesplegado(new Color(255, 247, 222));
        boton.setColorTextoOpcion(AZUL);
        boton.setColorBordeMenu(BORDE);
        boton.setAnchoMenu(275);
        boton.setAltoOpcion(42);
    }

    private void configurarTabla() {
        tablaProductos.setModel(modeloTabla);
        tablaProductos.aplicarEstilo();
        tablaProductos.setRowHeight(53);
        tablaProductos.setAutoCreateRowSorter(false);
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new RenderNivel());
        tablaProductos.getColumnModel().getColumn(6).setCellRenderer(new RenderEstado());
        tablaProductos.getColumnModel().getColumn(7).setCellRenderer(new RenderAcciones());
        tablaProductos.getColumnModel().getColumn(7).setCellEditor(new EditorAcciones());
        int[] anchos = {80, 270, 130, 130, 130, 115, 105, 150};
        for (int i = 0; i < anchos.length; i++) {
            tablaProductos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        etiquetaRango.setFont(tema.regular(12f));
        etiquetaRango.setForeground(SECUNDARIO);
    }

    private void configurarPaginacion() {
        prepararBotonPagina(botonAnterior, IconosUsuarios.Tipo.ANTERIOR);
        botonAnterior.addActionListener(e -> cambiarPagina(paginaActual - 1));
        for (JButton boton : botonesPagina) {
            prepararBotonPagina(boton, null);
            boton.addActionListener(e -> {
                Object numero = boton.getClientProperty("pagina");
                if (numero instanceof Integer pagina) {
                    cambiarPagina(pagina);
                }
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
        if (icono != null) {
            boton.setText("");
            boton.setIcon(IconosUsuarios.crear(icono, AZUL, 16));
        }
    }

    private void cargarTodo() {
        cargarResumen();
        cargarPagina();
    }

    private void cargarResumen() {
        new SwingWorker<ResumenIngredientes, Void>() {
            @Override
            protected ResumenIngredientes doInBackground() throws SQLException {
                return ingredienteCRUD.obtenerResumen();
            }

            @Override
            protected void done() {
                try {
                    ResumenIngredientes datos = get();
                    labelTitulo4.setText(String.valueOf(datos.total()));
                    labelTitulo8.setText(String.valueOf(datos.stockBajo()));
                    labelTitulo14.setText(String.valueOf(datos.activos()));
                    labelTitulo11.setText(String.valueOf(datos.inactivos()));
                    errorConexionMostrado = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarErrorDatos("cargar el resumen", ex.getCause());
                }
            }
        }.execute();
    }

    private void cargarPagina() {
        final int solicitud = ++secuenciaCarga;
        final int paginaSolicitada = paginaActual;
        final String busqueda = campoBusqueda.getText().trim();
        final Boolean stock = stockBajoActual;
        final Boolean estado = estadoActual;
        tablaProductos.setEnabled(false);
        etiquetaRango.setText("Cargando ingredientes...");
        new SwingWorker<PaginaIngredientes, Void>() {
            @Override
            protected PaginaIngredientes doInBackground() throws SQLException {
                return ingredienteCRUD.listarPagina(busqueda, stock, estado,
                        paginaSolicitada, POR_PAGINA);
            }

            @Override
            protected void done() {
                if (solicitud != secuenciaCarga) {
                    return;
                }
                try {
                    PaginaIngredientes datos = get();
                    totalPaginas = Math.max(1, (datos.totalRegistros()
                            + POR_PAGINA - 1) / POR_PAGINA);
                    if (paginaActual > totalPaginas) {
                        paginaActual = totalPaginas;
                        cargarPagina();
                        return;
                    }
                    modeloTabla.setIngredientes(datos.ingredientes());
                    actualizarPaginacion(datos.totalRegistros());
                    errorConexionMostrado = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    modeloTabla.setIngredientes(List.of());
                    totalPaginas = 1;
                    actualizarPaginacion(0);
                    mostrarErrorDatos("cargar ingredientes", ex.getCause());
                } finally {
                    tablaProductos.setEnabled(true);
                }
            }
        }.execute();
    }

    private void actualizarPaginacion(int total) {
        if (total == 0) {
            etiquetaRango.setText("No se encontraron ingredientes");
        } else {
            int inicio = (paginaActual - 1) * POR_PAGINA + 1;
            int fin = Math.min(inicio + POR_PAGINA - 1, total);
            etiquetaRango.setText(String.format("Mostrando %d–%d de %d ingredientes",
                    inicio, fin, total));
        }
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        int inicio = Math.max(1, Math.min(paginaActual - 1, totalPaginas - 2));
        for (int i = 0; i < botonesPagina.length; i++) {
            JButton boton = botonesPagina[i];
            int pagina = inicio + i;
            boton.setVisible(pagina <= totalPaginas);
            if (pagina > totalPaginas) {
                continue;
            }
            boton.putClientProperty("pagina", pagina);
            boton.setText(String.valueOf(pagina));
            boolean seleccionada = pagina == paginaActual;
            boton.setFont(seleccionada ? tema.negrita(13f) : tema.media(13f));
            if (boton instanceof BotonRedondeado redondeado) {
                redondeado.setColorInicio(seleccionada ? AMARILLO : Color.WHITE);
                redondeado.setColorFinal(seleccionada ? AMARILLO : Color.WHITE);
                redondeado.setColorBorde(seleccionada ? AMARILLO : BORDE);
            } else {
                boton.setBackground(seleccionada ? AMARILLO : Color.WHITE);
            }
        }
    }

    private void cambiarPagina(int numero) {
        if (numero < 1 || numero > totalPaginas || numero == paginaActual) {
            return;
        }
        paginaActual = numero;
        cargarPagina();
    }

    private void abrirFormulario(Ingrediente ingrediente) {
        if (mostrarDialogoIngrediente(ingrediente)) {
            cargarTodo();
        }
    }

    private void editar(int id) {
        try {
            Ingrediente ingrediente = ingredienteCRUD.obtenerPorId(id);
            if (ingrediente == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el ingrediente.");
                cargarTodo();
                return;
            }
            abrirFormulario(ingrediente);
        } catch (SQLException ex) {
            mostrarErrorDatos("obtener el ingrediente", ex);
        }
    }

    private void movimiento(int id) {
        try {
            Ingrediente ingrediente = ingredienteCRUD.obtenerPorId(id);
            if (ingrediente == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el ingrediente.");
                cargarTodo();
                return;
            }
            if (mostrarDialogoMovimiento(ingrediente)) {
                cargarTodo();
            }
        } catch (SQLException ex) {
            mostrarErrorDatos("obtener el ingrediente", ex);
        }
    }

    private void desactivar(Ingrediente ingrediente) {
        if (!ingrediente.isActivo()) {
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desactivar «" + ingrediente.getNombre() + "»?",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ingredienteCRUD.cambiarEstado(ingrediente.getIdIngrediente(), false);
            cargarTodo();
        } catch (SQLException ex) {
            mostrarErrorDatos("desactivar el ingrediente", ex);
        }
    }

    private void mostrarErrorDatos(String operacion, Throwable ex) {
        if (errorConexionMostrado) {
            return;
        }
        errorConexionMostrado = true;
        JOptionPane.showMessageDialog(this,
                "No fue posible " + operacion + ".\n" + ex.getMessage(),
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

        @Override
        public int getRowCount() {
            return ingredientes.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int columna) {
            return columnas[columna];
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            return columna >= 5 ? Ingrediente.class : String.class;
        }

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return columna == 7;
        }

        @Override
        public Object getValueAt(int fila, int columna) {
            Ingrediente item = ingredientes.get(fila);
            return switch (columna) {
                case 0 ->
                    String.format("#%04d", item.getIdIngrediente());
                case 1 ->
                    item.getNombre();
                case 2 ->
                    item.getUnidadMedida();
                case 3 ->
                    item.getStockActual().toPlainString();
                case 4 ->
                    item.getStockMinimo().toPlainString();
                case 5, 6, 7 ->
                    item;
                default ->
                    "";
            };
        }
    }

    private final class RenderNivel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t,
                Object valor, boolean seleccionado, boolean foco,
                int fila, int columna) {
            Ingrediente item = (Ingrediente) valor;
            return etiquetaEstado(item.tieneStockBajo() ? "Bajo" : "Normal",
                    item.tieneStockBajo() ? ROJO : new Color(34, 139, 71),
                    item.tieneStockBajo() ? new Color(252, 233, 233)
                    : new Color(230, 245, 234), t, seleccionado);
        }
    }

    private final class RenderEstado implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t,
                Object valor, boolean seleccionado, boolean foco,
                int fila, int columna) {
            Ingrediente item = (Ingrediente) valor;
            return etiquetaEstado(item.isActivo() ? "Activo" : "Inactivo",
                    item.isActivo() ? new Color(34, 139, 71) : SECUNDARIO,
                    item.isActivo() ? new Color(230, 245, 234)
                    : new Color(237, 239, 243), t, seleccionado);
        }
    }

    private JPanel etiquetaEstado(String texto, Color primerPlano,
            Color fondoEtiqueta, javax.swing.JTable tablaActual, boolean seleccionado) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 13));
        panel.setBackground(seleccionado
                ? tablaActual.getSelectionBackground() : Color.WHITE);
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(tema.media(12f));
        etiqueta.setOpaque(true);
        etiqueta.setForeground(primerPlano);
        etiqueta.setBackground(fondoEtiqueta);
        etiqueta.setBorder(new EmptyBorder(4, 11, 4, 11));
        panel.add(etiqueta);
        return panel;
    }

    private final class RenderAcciones implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable t,
                Object valor, boolean seleccionado, boolean foco,
                int fila, int columna) {
            Ingrediente item = (Ingrediente) valor;
            JPanel panel = panelAcciones(seleccionado
                    ? t.getSelectionBackground() : Color.WHITE);
            panel.add(botonAccion(IconosUsuarios.Tipo.EDITAR,
                    new Color(214, 151, 0), "Editar ingrediente"));
            BotonRedondeado mover = botonAccion(null, AZUL, "Registrar movimiento");
            mover.setEnabled(item.isActivo());
            panel.add(mover);
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
        private final BotonRedondeado editar = botonAccion(IconosUsuarios.Tipo.EDITAR,
                new Color(214, 151, 0), "Editar ingrediente");
        private final BotonRedondeado mover = botonAccion(null, AZUL,
                "Registrar movimiento");
        private final BotonRedondeado baja = botonAccion(IconosUsuarios.Tipo.ELIMINAR,
                ROJO, "Desactivar ingrediente");
        private Ingrediente item;

        EditorAcciones() {
            panel.add(editar);
            panel.add(mover);
            panel.add(baja);
            editar.addActionListener(e -> {
                int id = item.getIdIngrediente();
                fireEditingStopped();
                editar(id);
            });
            mover.addActionListener(e -> {
                int id = item.getIdIngrediente();
                fireEditingStopped();
                movimiento(id);
            });
            baja.addActionListener(e -> {
                Ingrediente seleccionado = item;
                fireEditingStopped();
                desactivar(seleccionado);
            });
        }

        @Override
        public Component getTableCellEditorComponent(javax.swing.JTable t,
                Object valor, boolean seleccionado, int fila, int columna) {
            item = (Ingrediente) valor;
            mover.setEnabled(item.isActivo());
            baja.setEnabled(item.isActivo());
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return item;
        }
    }

    private JPanel panelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 11));
        panel.setBackground(fondo);
        return panel;
    }

    private BotonRedondeado botonAccion(IconosUsuarios.Tipo icono,
            Color color, String descripcion) {
        BotonRedondeado boton = new BotonRedondeado();
        if (icono == null) {
            boton.setText("±");
            boton.setFont(tema.negrita(16f));
            boton.setForeground(color);
        } else {
            boton.setIcon(IconosUsuarios.crear(icono, color, 17));
            boton.setText("");
        }
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(color);
        boton.setGrosorBorde(1f);
        boton.setRadio(9);
        boton.setPreferredSize(new Dimension(32, 30));
        boton.setToolTipText(descripcion);
        return boton;
    }

    // Pega estos métodos y la clase interna dentro de IngredientesPanel,
// fuera de initComponents() y fuera del bloque generado.
    private boolean mostrarDialogoIngrediente(Ingrediente ingrediente) {
        Window propietario = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(propietario,
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setBackground(new Color(0, 0, 0, 0));
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        boolean[] guardado = {false};
        PanelFormularioIngrediente formulario = new PanelFormularioIngrediente(ingrediente);
        formulario.addPropertyChangeListener(evento -> {
            if (PanelFormularioIngrediente.EVENTO_GUARDADO.equals(
                    evento.getPropertyName())) {
                guardado[0] = true;
                dialogo.dispose();
            } else if (PanelFormularioIngrediente.EVENTO_CANCELAR.equals(
                    evento.getPropertyName())) {
                dialogo.dispose();
            }
        });
        dialogo.setContentPane(formulario);
        dialogo.pack();
        dialogo.setLocationRelativeTo(propietario);
        mostrarDialogoConFondo(dialogo);
        return guardado[0];
    }

    private void mostrarDialogoConFondo(JDialog dialogo) {
        JRootPane raiz = SwingUtilities.getRootPane(this);
        Component anterior = raiz == null ? null : raiz.getGlassPane();
        boolean eraVisible = anterior != null && anterior.isVisible();
        if (raiz != null) {
            JPanel fondo = new JPanel() {
                @Override
                protected void paintComponent(Graphics grafico) {
                    Graphics2D g = (Graphics2D) grafico.create();
                    g.setColor(new Color(0, 12, 28, 90));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.dispose();
                }
            };
            fondo.setOpaque(false);
            fondo.addMouseListener(new java.awt.event.MouseAdapter() {
            });
            raiz.setGlassPane(fondo);
            fondo.setVisible(true);
        }
        try {
            dialogo.setVisible(true);
        } finally {
            if (raiz != null && anterior != null) {
                raiz.setGlassPane(anterior);
                anterior.setVisible(eraVisible);
            }
        }
    }

    private final class PanelFormularioIngrediente extends PanelFlotante {

        public static final String EVENTO_GUARDADO = "ingredienteGuardado";
        public static final String EVENTO_CANCELAR = "ingredienteCancelado";
        private final Ingrediente original;
        private final JTextField campoNombre = new JTextField();
        private final JTextField campoUnidadMedida = new JTextField();
        private final JTextField campoStockMinimo = new JTextField();
        private final BotonDesplegable selectorEstado = new BotonDesplegable();
        private final JLabel labelError = new JLabel(" ");
        private final BotonRedondeado botonGuardar = boton("Guardar ingrediente", true);

        private PanelFormularioIngrediente(Ingrediente original) {
            this.original = original;
            setColorFondo(Color.WHITE);
            setColorBorde(BORDE);
            setRadio(26);
            setPreferredSize(new Dimension(760, 480));
            setLayout(new BorderLayout());
            construirCabecera();
            construirCampos();
            construirPie();
            if (original != null) {
                campoNombre.setText(original.getNombre());
                campoUnidadMedida.setText(original.getUnidadMedida());
                campoStockMinimo.setText(original.getStockMinimo().toPlainString());
                selectorEstado.setText(original.isActivo() ? "Activo" : "Inactivo");
            }
        }

        private void construirCabecera() {
            JPanel cabecera = new JPanel(null);
            cabecera.setOpaque(false);
            cabecera.setPreferredSize(new Dimension(760, 112));
            PanelCircular circulo = new PanelCircular();
            circulo.setColorFondo(new Color(255, 244, 211));
            circulo.setLayout(new BorderLayout());
            circulo.setBounds(30, 19, 80, 80);
            java.net.URL urlIcono = getClass().getResource("/Imagenes/lista_icono.png");
            if (urlIcono != null) {
                JLabel imagen = new JLabel(new javax.swing.ImageIcon(
                        new javax.swing.ImageIcon(urlIcono).getImage()
                                .getScaledInstance(39, 39, java.awt.Image.SCALE_SMOOTH)));
                imagen.setHorizontalAlignment(SwingConstants.CENTER);
                circulo.add(imagen, BorderLayout.CENTER);
            }
            cabecera.add(circulo);
            JLabel titulo = new JLabel(original == null
                    ? "Agregar ingrediente" : "Editar ingrediente");
            titulo.setFont(tema.negrita(25f));
            titulo.setForeground(AZUL);
            titulo.setBounds(125, 27, 595, 36);
            cabecera.add(titulo);
            JLabel subtitulo = new JLabel(original == null
                    ? "Completa los datos del nuevo ingrediente"
                    : "Actualiza la información de "
                    + String.format("#%04d", original.getIdIngrediente()));
            subtitulo.setFont(tema.regular(15f));
            subtitulo.setForeground(new Color(92, 103, 124));
            subtitulo.setBounds(125, 62, 595, 27);
            cabecera.add(subtitulo);
            add(cabecera, BorderLayout.NORTH);
        }

        private void construirCampos() {
            estilizar(campoNombre);
            estilizar(campoUnidadMedida);
            estilizar(campoStockMinimo);
            selectorEstado.setText("Activo");
            selectorEstado.setTextoDesplegable("Activo;Inactivo");
            selectorEstado.addMenuOpcionListener(
                    evento -> selectorEstado.setText(evento.getActionCommand()));
            selectorEstado.setFont(tema.regular(14f));
            selectorEstado.setForeground(AZUL);
            selectorEstado.setColorFondo(Color.WHITE);
            selectorEstado.setColorBordeMenu(BORDE);
            JLabel stock = new JLabel(original == null ? "0.00"
                    : original.getStockActual().toPlainString());
            stock.setFont(tema.media(15f));
            stock.setForeground(AZUL);
            stock.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE), new EmptyBorder(0, 14, 0, 14)));

            JPanel campos = new JPanel(new GridBagLayout());
            campos.setOpaque(false);
            campos.setBorder(new EmptyBorder(4, 28, 4, 28));
            fila(campos, 0, campo("Nombre", campoNombre),
                    campo("Unidad de medida", campoUnidadMedida));
            fila(campos, 1, campo("Stock actual (solo lectura)", stock),
                    campo("Stock mínimo", campoStockMinimo));
            fila(campos, 2, campo("Estado", selectorEstado),
                    campo("Ejemplos de unidad: gramos, mililitros, unidades",
                            new JLabel("El stock se cambia con un movimiento.")));
            add(campos, BorderLayout.CENTER);
        }

        private JPanel campo(String texto, JComponent control) {
            JPanel panel = new JPanel(new BorderLayout(0, 6));
            panel.setOpaque(false);
            JLabel etiqueta = new JLabel(texto);
            etiqueta.setFont(tema.media(13f));
            etiqueta.setForeground(AZUL);
            panel.add(etiqueta, BorderLayout.NORTH);
            control.setPreferredSize(new Dimension(310, 46));
            panel.add(control, BorderLayout.CENTER);
            return panel;
        }

        private void fila(JPanel panel, int numero, JPanel izquierda, JPanel derecha) {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = numero;
            c.gridx = 0;
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTH;
            c.insets = new Insets(6, 10, 12, 13);
            panel.add(izquierda, c);
            c.gridx = 1;
            c.insets = new Insets(6, 13, 12, 10);
            panel.add(derecha, c);
        }

        private void construirPie() {
            JPanel pie = new JPanel(new BorderLayout());
            pie.setOpaque(false);
            pie.setPreferredSize(new Dimension(760, 104));
            pie.setBorder(new EmptyBorder(0, 30, 20, 30));
            labelError.setForeground(ROJO);
            labelError.setFont(tema.media(13f));
            pie.add(labelError, BorderLayout.NORTH);
            JPanel botones = new JPanel(new BorderLayout());
            botones.setOpaque(false);
            if (original != null && original.isActivo()) {
                BotonRedondeado desactivar = boton("Desactivar ingrediente", false);
                desactivar.setPreferredSize(new Dimension(230, 48));
                desactivar.addActionListener(evento -> desactivar());

                JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                izquierda.setOpaque(false);
                izquierda.add(desactivar);
                botones.add(izquierda, BorderLayout.WEST);
            }
            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
            derecha.setOpaque(false);
            BotonRedondeado cancelar = boton("Cancelar", false);
            cancelar.setPreferredSize(new Dimension(130, 48));
            botonGuardar.setPreferredSize(new Dimension(220, 48));
            cancelar.addActionListener(evento -> firePropertyChange(EVENTO_CANCELAR, false, true));
            botonGuardar.addActionListener(evento -> guardar());
            derecha.add(cancelar);
            derecha.add(botonGuardar);
            botones.add(derecha, BorderLayout.EAST);
            pie.add(botones, BorderLayout.CENTER);
            add(pie, BorderLayout.SOUTH);
        }

        private void guardar() {
            labelError.setText(" ");
            String nombre = campoNombre.getText().trim();
            String unidad = campoUnidadMedida.getText().trim();
            if (nombre.isEmpty() || nombre.length() > 100) {
                labelError.setText("Ingresa un nombre de hasta 100 caracteres.");
                return;
            }
            if (unidad.isEmpty() || unidad.length() > 20) {
                labelError.setText("Ingresa una unidad de hasta 20 caracteres.");
                return;
            }
            BigDecimal minimo;
            try {
                minimo = new BigDecimal(campoStockMinimo.getText().trim());
            } catch (NumberFormatException ex) {
                labelError.setText("Ingresa un stock mínimo válido. Usa punto decimal.");
                return;
            }
            if (minimo.signum() < 0 || minimo.scale() > 2
                    || minimo.compareTo(new BigDecimal("99999999.99")) > 0) {
                labelError.setText("El mínimo debe ser positivo y tener hasta dos decimales.");
                return;
            }
            botonGuardar.setEnabled(false);
            try {
                Ingrediente ingrediente = original == null ? new Ingrediente() : original;
                ingrediente.setNombre(nombre);
                ingrediente.setUnidadMedida(unidad);
                ingrediente.setStockMinimo(minimo);
                ingrediente.setActivo("Activo".equals(selectorEstado.getText()));
                if (original == null) {
                    ingredienteCRUD.insertar(ingrediente);
                } else {
                    ingredienteCRUD.actualizar(ingrediente);
                }
                firePropertyChange(EVENTO_GUARDADO, false, true);
            } catch (SQLException ex) {
                labelError.setText("No fue posible guardar: " + ex.getMessage());
            } finally {
                botonGuardar.setEnabled(true);
            }
        }

        private void desactivar() {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desactivar «" + original.getNombre() + "»?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                ingredienteCRUD.cambiarEstado(original.getIdIngrediente(), false);
                firePropertyChange(EVENTO_GUARDADO, false, true);
            } catch (SQLException ex) {
                labelError.setText("No fue posible desactivar: " + ex.getMessage());
            }
        }

        private void estilizar(JTextField campo) {
            campo.setFont(tema.regular(14f));
            campo.setForeground(AZUL);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE), new EmptyBorder(0, 13, 0, 13)));
        }

        private BotonRedondeado boton(String texto, boolean principal) {
            BotonRedondeado boton = new BotonRedondeado();
            boton.setText(texto);
            boton.setFont(tema.negrita(14f));
            boton.setDegradado(false);
            boton.setRadio(12);
            boton.setGrosorBorde(1f);
            boton.setColorInicio(principal ? AMARILLO : Color.WHITE);
            boton.setColorFinal(principal ? AMARILLO : Color.WHITE);
            boton.setColorBorde(principal ? AMARILLO : ROJO);
            boton.setForeground(principal ? AZUL : ROJO);
            return boton;
        }
    }

    // Formulario para registrar movimientos de inventario.
    // fuera de initComponents() y fuera del bloque generado.
    private boolean mostrarDialogoMovimiento(Ingrediente ingrediente) {
        Window propietario = SwingUtilities.getWindowAncestor(this);
        DialogoMovimientoIngrediente dialogo
                = new DialogoMovimientoIngrediente(propietario, ingrediente);
        mostrarDialogoConFondo(dialogo);
        return dialogo.guardado;
    }

    private final class DialogoMovimientoIngrediente extends JDialog {

        private final Ingrediente ingrediente;
        private final JComboBox<String> tipo = new JComboBox<>(new String[]{
            "Entrada", "Salida", "Ajuste"
        });
        private final JTextField cantidad = new JTextField();
        private final JTextField motivo = new JTextField();
        private final JLabel instruccion = new JLabel();
        private final JLabel error = new JLabel(" ");
        private final BotonRedondeado guardar = crearBoton("Guardar movimiento", true);
        private boolean guardado;

        private DialogoMovimientoIngrediente(Window propietario,
                Ingrediente ingrediente) {
            super(propietario, ModalityType.APPLICATION_MODAL);
            this.ingrediente = ingrediente;
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setContentPane(crearContenido());
            tipo.addActionListener(e -> actualizarInstruccion());
            actualizarInstruccion();
            guardar.addActionListener(e -> guardarMovimiento());
            pack();
            setLocationRelativeTo(propietario);
        }

        private PanelFlotante crearContenido() {
            PanelFlotante panel = new PanelFlotante();
            panel.setColorFondo(Color.WHITE);
            panel.setColorBorde(BORDE);
            panel.setRadio(26);
            panel.setPreferredSize(new Dimension(650, 435));
            panel.setLayout(new BorderLayout());

            JPanel cabecera = new JPanel(new BorderLayout(0, 4));
            cabecera.setOpaque(false);
            cabecera.setBorder(new EmptyBorder(27, 32, 15, 32));
            JLabel titulo = new JLabel("Movimiento de inventario");
            titulo.setFont(tema.negrita(24f));
            titulo.setForeground(AZUL);
            JLabel subtitulo = new JLabel(String.format("#%04d · %s · Stock actual: %s %s",
                    ingrediente.getIdIngrediente(), ingrediente.getNombre(),
                    ingrediente.getStockActual().toPlainString(),
                    ingrediente.getUnidadMedida()));
            subtitulo.setFont(tema.regular(14f));
            subtitulo.setForeground(new Color(92, 103, 124));
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
            estilizar(cantidad);
            estilizar(motivo);
            agregarCampo(campos, 0, "Tipo de movimiento", tipo);
            agregarCampo(campos, 1, "Cantidad (" + ingrediente.getUnidadMedida() + ")", cantidad);
            agregarCampo(campos, 2, "Motivo (opcional)", motivo);
            instruccion.setFont(tema.regular(12f));
            instruccion.setForeground(new Color(92, 103, 124));
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
            pie.setPreferredSize(new Dimension(650, 95));
            pie.setBorder(new EmptyBorder(0, 32, 20, 32));
            error.setFont(tema.media(12f));
            error.setForeground(ROJO);
            pie.add(error, BorderLayout.NORTH);
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
            botones.setOpaque(false);
            BotonRedondeado cancelar = crearBoton("Cancelar", false);
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
                java.awt.Component control) {
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

        private void guardarMovimiento() {
            error.setText(" ");
            BigDecimal valor;
            try {
                valor = new BigDecimal(cantidad.getText().trim());
            } catch (NumberFormatException ex) {
                error.setText("Ingresa una cantidad válida con punto decimal.");
                return;
            }
            boolean ajuste = tipo.getSelectedIndex() == 2;
            if (valor.scale() > 2 || valor.compareTo(new BigDecimal("99999999.99")) > 0
                    || (ajuste ? valor.signum() < 0 : valor.signum() <= 0)) {
                error.setText("Cantidad inválida; usa hasta dos decimales.");
                return;
            }
            String textoMotivo = motivo.getText().trim();
            if (textoMotivo.length() > 150) {
                error.setText("El motivo admite hasta 150 caracteres.");
                return;
            }
            String codigo = switch (tipo.getSelectedIndex()) {
                case 0 ->
                    "ENTRADA";
                case 1 ->
                    "SALIDA";
                default ->
                    "AJUSTE";
            };
            guardar.setEnabled(false);
            try {
                ingredienteCRUD.registrarMovimiento(ingrediente.getIdIngrediente(), codigo,
                        valor, textoMotivo);
                guardado = true;
                dispose();
            } catch (SQLException | IllegalArgumentException ex) {
                error.setText("No fue posible registrar el movimiento: " + ex.getMessage());
            } finally {
                guardar.setEnabled(true);
            }
        }

        private void estilizar(JTextField campo) {
            campo.setFont(tema.regular(14f));
            campo.setForeground(AZUL);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE), new EmptyBorder(0, 12, 0, 12)));
        }

        private BotonRedondeado crearBoton(String texto, boolean principal) {
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
    }


    private void campoBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoBusquedaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoBusquedaActionPerformed

    private void filtroStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroStockActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonAgregarIngrediente;
    private Componentes.BotonRedondeado botonAnterior;
    private Componentes.BotonRedondeado botonLimpiarFiltros;
    private Componentes.BotonRedondeado botonPagina1;
    private Componentes.BotonRedondeado botonPagina2;
    private Componentes.BotonRedondeado botonPagina3;
    private Componentes.BotonRedondeado botonSiguiente;
    private Componentes.CampoBusquedaAdmin campoBusqueda;
    private javax.swing.JLabel etiquetaRango;
    private Componentes.BotonDesplegable filtroEstado;
    private Componentes.BotonDesplegable filtroStock;
    private Labels.LabelEscalable labelEscalable2;
    private Labels.LabelEscalable labelEscalable3;
    private Labels.LabelEscalable labelEscalable4;
    private Labels.LabelEscalable labelEscalable5;
    private javax.swing.JLabel labelTitulo10;
    private javax.swing.JLabel labelTitulo11;
    private javax.swing.JLabel labelTitulo12;
    private javax.swing.JLabel labelTitulo13;
    private javax.swing.JLabel labelTitulo14;
    private javax.swing.JLabel labelTitulo15;
    private javax.swing.JLabel labelTitulo2;
    private javax.swing.JLabel labelTitulo3;
    private javax.swing.JLabel labelTitulo4;
    private javax.swing.JLabel labelTitulo5;
    private javax.swing.JLabel labelTitulo6;
    private javax.swing.JLabel labelTitulo7;
    private javax.swing.JLabel labelTitulo8;
    private javax.swing.JLabel labelTitulo9;
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
