/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI_ADMINISTRADOR;

import CRUD.ProductoCRUD;
import Modelos.PaginaProductos;
import Modelos.Producto;
import Modelos.ResumenProductos;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.math.BigDecimal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestionMenuPanel extends javax.swing.JPanel {

    private final ProductoCRUD productoCRUD = new ProductoCRUD();

    private static final int PRODUCTOS_POR_PAGINA = 10;

    private int paginaActual = 1;

    private int totalPaginas = 1;

    private String busquedaActual = "";

    private Integer categoriaActual = null;

    private Boolean estadoActual = null;

    private String imagenSeleccionada = null;

    private static final String CARPETA_IMAGENES =
            "src/Imagenes/productos/";

    private static final String RECURSO_IMAGENES =
            "/Imagenes/productos/";

    public GestionMenuPanel() {

        initComponents();

        configurarTabla();

        configurarEventos();

        cargarCategorias();

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


    // ============================================================
// CONFIGURACIÓN DE TABLA
// ============================================================

private void configurarTabla() {

    DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{
                "ID",
                "Imagen",
                "Nombre",
                "Categoría",
                "Precio",
                "Disponibilidad",
                "Tipo",
                "Stock",
                "Estado",
                "Acciones"
            },
            0
    ) {
        @Override
        public boolean isCellEditable(
                int row,
                int column) {

            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {

            if (column == 0) {
                return Integer.class;
            }

            if (column == 1) {
                return ImageIcon.class;
            }

            return String.class;
        }
    };

    tablaProductos.setModel(modelo);

    tablaProductos.setRowHeight(75);

    tablaProductos.setAutoCreateRowSorter(true);

    tablaProductos.getColumnModel()
            .getColumn(0)
            .setPreferredWidth(50);

    tablaProductos.getColumnModel()
            .getColumn(1)
            .setPreferredWidth(90);

    tablaProductos.getColumnModel()
            .getColumn(2)
            .setPreferredWidth(200);

    tablaProductos.getColumnModel()
            .getColumn(3)
            .setPreferredWidth(150);

    tablaProductos.getColumnModel()
            .getColumn(4)
            .setPreferredWidth(90);

    tablaProductos.getColumnModel()
            .getColumn(5)
            .setPreferredWidth(130);

    tablaProductos.getColumnModel()
            .getColumn(6)
            .setPreferredWidth(100);

    tablaProductos.getColumnModel()
            .getColumn(7)
            .setPreferredWidth(80);

    tablaProductos.getColumnModel()
            .getColumn(8)
            .setPreferredWidth(100);

    tablaProductos.getColumnModel()
            .getColumn(9)
            .setPreferredWidth(180);

    // Renderer para imagen
    tablaProductos.getColumnModel()
            .getColumn(1)
            .setCellRenderer(
                    new ImagenRenderer()
            );
}

private static class ImagenRenderer
        extends DefaultTableCellRenderer {

    @Override
    public java.awt.Component getTableCellRendererComponent(
            javax.swing.JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        JLabel label = new JLabel();

        label.setHorizontalAlignment(
                JLabel.CENTER
        );

        label.setVerticalAlignment(
                JLabel.CENTER
        );

        if (value instanceof ImageIcon icon) {

            label.setIcon(icon);

        } else {

            label.setText("Sin imagen");
            label.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            11
                    )
            );
        }

        return label;
    }
}

private void configurarEventos() {

    // AGREGAR PRODUCTO
    botonAgregarProducto.addActionListener(e -> {

        mostrarFormularioProducto(null);

    });

    // BUSCAR
    campoBusqueda.addActionListener(e -> {

        busquedaActual =
                campoBusqueda.getText().trim();

        paginaActual = 1;

        cargarDatos();

    });

    // FILTRO CATEGORÍA
    filtroCategoria.addActionListener(e -> {

        String texto =
                filtroCategoria.getText();

        if (texto == null
                || texto.equals("Todas las categorías")) {

            categoriaActual = null;

        } else {

            categoriaActual =
                    obtenerIdCategoriaPorNombre(texto);
        }

        paginaActual = 1;

        cargarDatos();
    });

    // FILTRO ESTADO
    filtroEstado.addActionListener(e -> {

        String texto =
                filtroEstado.getText();

        if (texto == null
                || texto.equals("Todos los estados")) {

            estadoActual = null;

        } else if (texto.equals("Activo")) {

            estadoActual = true;

        } else if (texto.equals("Inactivo")) {

            estadoActual = false;
        }

        paginaActual = 1;

        cargarDatos();
    });

    // LIMPIAR
    botonLimpiarFiltros.addActionListener(e -> {

        campoBusqueda.setText("");

        busquedaActual = "";

        categoriaActual = null;

        estadoActual = null;

        paginaActual = 1;

        filtroCategoria.setText(
                "Todas las categorías"
        );

        filtroEstado.setText(
                "Todos los estados"
        );

        cargarDatos();
    });

    // PAGINACIÓN
    botonAnterior.addActionListener(e -> {

        if (paginaActual > 1) {

            paginaActual--;

            cargarDatos();
        }
    });

    botonSiguiente.addActionListener(e -> {

        if (paginaActual < totalPaginas) {

            paginaActual++;

            cargarDatos();
        }
    });

    botonPagina1.addActionListener(e -> irPagina(1));

    botonPagina2.addActionListener(e -> irPagina(2));

    botonPagina3.addActionListener(e -> irPagina(3));

    // DOBLE CLICK PARA EDITAR
    tablaProductos.addMouseListener(
            new java.awt.event.MouseAdapter() {

        @Override
        public void mouseClicked(
                java.awt.event.MouseEvent e) {

            if (e.getClickCount() == 2
                    && e.getButton()
                    == java.awt.event.MouseEvent.BUTTON1) {

                int fila =
                        tablaProductos.rowAtPoint(
                                e.getPoint()
                        );

                if (fila >= 0) {

                    int id =
                            (int) tablaProductos
                                    .getValueAt(
                                            fila,
                                            0
                                    );

                    editarProducto(id);
                }
            }
        }
    });
}

private void cargarCategorias() {

    try {

        Map<Integer, String> categorias =
                productoCRUD
                        .listarCategoriasActivas();

        String[] nombres =
                new String[categorias.size() + 1];

        nombres[0] =
                "Todas las categorías";

        int i = 1;

        for (String nombre
                : categorias.values()) {

            nombres[i++] = nombre;
        }

        filtroCategoria.setTextoDesplegable(
                String.join(";", nombres)
        );

    } catch (SQLException e) {

        mostrarError(
                "No fue posible cargar las categorías.",
                e
        );
    }
}

private Integer obtenerIdCategoriaPorNombre(
        String nombre) {

    try {

        Map<Integer, String> categorias =
                productoCRUD
                        .listarCategoriasActivas();

        for (Map.Entry<Integer, String> entrada
                : categorias.entrySet()) {

            if (entrada.getValue()
                    .equalsIgnoreCase(nombre)) {

                return entrada.getKey();
            }
        }

    } catch (SQLException e) {

        mostrarError(
                "No fue posible consultar las categorías.",
                e
        );
    }

    return null;
}

private void cargarDatos() {

    try {

        cargarResumen();

        PaginaProductos pagina =
                productoCRUD.listarPagina(
                        busquedaActual,
                        categoriaActual,
                        null,
                        estadoActual,
                        paginaActual,
                        PRODUCTOS_POR_PAGINA
                );

        mostrarProductos(
                pagina.productos()
        );

        actualizarPaginacion(
                pagina.totalRegistros()
        );

    } catch (SQLException e) {

        mostrarError(
                "No fue posible cargar los productos.",
                e
        );
    }
}

private void cargarResumen() {

    try {

        ResumenProductos resumen =
                productoCRUD.obtenerResumen();

        labelTotalProductos.setText(
                String.valueOf(
                        resumen.total()
                )
        );

        labelStockBajo.setText(
                String.valueOf(
                        resumen.stockBajo()
                )
        );

        labelActivos.setText(
                String.valueOf(
                        resumen.activos()
                )
        );

        labelInactivos.setText(
                String.valueOf(
                        resumen.inactivos()
                )
        );

    } catch (SQLException e) {

        mostrarError(
                "No fue posible cargar el resumen.",
                e
        );
    }
}

private void mostrarProductos(
        List<Producto> productos) {

    DefaultTableModel modelo =
            (DefaultTableModel)
                    tablaProductos.getModel();

    modelo.setRowCount(0);

    for (Producto producto
            : productos) {

        ImageIcon imagen =
                cargarImagen(
                        producto.getImagen()
                );

        String estado =
                producto.isActivo()
                        ? "Activo"
                        : "Inactivo";

        String tipo =
                producto.isCombo()
                        ? "Combo"
                        : "Producto";

        String precio =
                producto.getPrecioBase() == null
                        ? "$0.00"
                        : "$" + producto
                                .getPrecioBase()
                                .toPlainString();

        String disponibilidad =
                convertirDisponibilidad(
                        producto
                                .getDisponibilidadMenu()
                );

        modelo.addRow(
                new Object[]{
                    producto.getIdProducto(),
                    imagen,
                    producto.getNombre(),
                    producto.getCategoria(),
                    precio,
                    disponibilidad,
                    tipo,
                    producto.getStockActual(),
                    estado,
                    "Editar / Cambiar estado"
                }
        );
    }
}

private String convertirDisponibilidad(
        String disponibilidad) {

    if (disponibilidad == null) {
        return "";
    }

    return switch (disponibilidad) {

        case "DESAYUNO" ->
                "Desayuno";

        case "ALMUERZO" ->
                "Almuerzo";

        case "TODO_DIA" ->
                "Todo el día";

        default ->
                disponibilidad;
    };
}

private ImageIcon cargarImagen(
        String ruta) {

    if (ruta == null
            || ruta.isBlank()) {

        return null;
    }

    try {

        File archivo =
                new File(ruta);

        if (!archivo.exists()) {

            archivo =
                    new File(
                            "src/" + ruta
                    );
        }

        if (archivo.exists()) {

            ImageIcon original =
                    new ImageIcon(
                            archivo.getAbsolutePath()
                    );

            Image imagen =
                    original.getImage()
                            .getScaledInstance(
                                    65,
                                    65,
                                    Image.SCALE_SMOOTH
                            );

            return new ImageIcon(imagen);
        }

        java.net.URL recurso =
                getClass()
                        .getResource(
                                ruta.startsWith("/")
                                        ? ruta
                                        : "/" + ruta
                        );

        if (recurso != null) {

            ImageIcon original =
                    new ImageIcon(recurso);

            Image imagen =
                    original.getImage()
                            .getScaledInstance(
                                    65,
                                    65,
                                    Image.SCALE_SMOOTH
                            );

            return new ImageIcon(imagen);
        }

    } catch (Exception e) {

        System.out.println(
                "Error cargando imagen: "
                + e.getMessage()
        );
    }

    return null;
}

private void actualizarPaginacion(
        int totalRegistros) {

    totalPaginas =
            Math.max(
                    1,
                    (int) Math.ceil(
                            (double) totalRegistros
                            / PRODUCTOS_POR_PAGINA
                    )
            );

    int inicio;

    if (totalRegistros == 0) {

        inicio = 0;

    } else {

        inicio =
                ((paginaActual - 1)
                        * PRODUCTOS_POR_PAGINA)
                + 1;
    }

    int fin =
            Math.min(
                    paginaActual
                            * PRODUCTOS_POR_PAGINA,
                    totalRegistros
            );

    etiquetaRango.setText(
            "Mostrando "
            + inicio
            + "–"
            + fin
            + " de "
            + totalRegistros
            + " productos"
    );

    botonAnterior.setEnabled(
            paginaActual > 1
    );

    botonSiguiente.setEnabled(
            paginaActual < totalPaginas
    );

    botonPagina1.setVisible(
            totalPaginas >= 1
    );

    botonPagina2.setVisible(
            totalPaginas >= 2
    );

    botonPagina3.setVisible(
            totalPaginas >= 3
    );

    botonPagina1.setText("1");

    botonPagina2.setText("2");

    botonPagina3.setText("3");

    botonPagina1.setColorInicio(
            paginaActual == 1
                    ? new Color(255, 188, 0)
                    : Color.WHITE
    );

    botonPagina2.setColorInicio(
            paginaActual == 2
                    ? new Color(255, 188, 0)
                    : Color.WHITE
    );

    botonPagina3.setColorInicio(
            paginaActual == 3
                    ? new Color(255, 188, 0)
                    : Color.WHITE
    );
}

private void irPagina(int pagina) {

    if (pagina < 1
            || pagina > totalPaginas) {

        return;
    }

    paginaActual = pagina;

    cargarDatos();
}

private void editarProducto(
        int idProducto) {

    try {

        Producto producto =
                productoCRUD
                        .obtenerPorId(
                                idProducto
                        );

        if (producto == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró el producto.",
                    "Producto",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        mostrarFormularioProducto(
                producto
        );

    } catch (SQLException e) {

        mostrarError(
                "No fue posible obtener el producto.",
                e
        );
    }
}

private void mostrarFormularioProducto(
        Producto productoExistente) {

    boolean editar =
            productoExistente != null;

    JDialog dialogo =
            new JDialog(
                    javax.swing.SwingUtilities
                            .getWindowAncestor(this),
                    editar
                            ? "Editar producto"
                            : "Agregar producto",
                    java.awt.Dialog.ModalityType.APPLICATION_MODAL
            );

    dialogo.setSize(650, 720);

    dialogo.setLocationRelativeTo(this);

    dialogo.setLayout(
            new BorderLayout()
    );

    JPanel formulario =
            new JPanel(
                    new GridBagLayout()
            );

    formulario.setBorder(
            BorderFactory.createEmptyBorder(
                    20,
                    25,
                    20,
                    25
            )
    );

    GridBagConstraints gbc =
            new GridBagConstraints();

    gbc.insets =
            new Insets(
                    6,
                    6,
                    6,
                    6
            );

    gbc.fill =
            GridBagConstraints.HORIZONTAL;

    gbc.weightx = 1;

    JTextField campoNombre =
            new JTextField();

    JTextArea campoDescripcion =
            new JTextArea(4, 20);

    campoDescripcion.setLineWrap(true);

    campoDescripcion.setWrapStyleWord(true);

    JTextField campoPrecio =
            new JTextField();

    JComboBox<String> comboCategoria =
            new JComboBox<>();

    JComboBox<String> comboDisponibilidad =
            new JComboBox<>(
                    new String[]{
                        "DESAYUNO",
                        "ALMUERZO",
                        "TODO_DIA"
                    }
            );

    JComboBox<String> comboTamano =
            new JComboBox<>(
                    new String[]{
                        "",
                        "PEQUENA",
                        "MEDIANA",
                        "GRANDE"
                    }
            );

    JCheckBox checkCombo =
            new JCheckBox(
                    "Es combo"
            );

    JSpinner spinnerStock =
            new JSpinner(
                    new SpinnerNumberModel(
                            0,
                            0,
                            999999,
                            1
                    )
            );

    JSpinner spinnerStockMinimo =
            new JSpinner(
                    new SpinnerNumberModel(
                            0,
                            0,
                            999999,
                            1
                    )
            );

    JCheckBox checkActivo =
            new JCheckBox(
                    "Producto activo"
            );

    JLabel labelImagen =
            new JLabel(
                    "Sin imagen"
            );

    labelImagen.setPreferredSize(
            new Dimension(
                    180,
                    150
            )
    );

    labelImagen.setHorizontalAlignment(
            JLabel.CENTER
    );

    labelImagen.setBorder(
            BorderFactory.createLineBorder(
                    new Color(
                            220,
                            220,
                            220
                    )
            )
    );

    JButton botonSeleccionarImagen =
            new JButton(
                    "Seleccionar imagen"
            );

    JButton botonQuitarImagen =
            new JButton(
                    "Quitar imagen"
            );

    imagenSeleccionada =
            editar
                    ? productoExistente.getImagen()
                    : null;

    // ---------------------------------------------------------
    // CATEGORÍAS
    // ---------------------------------------------------------

    try {

        Map<Integer, String> categorias =
                productoCRUD
                        .listarCategoriasParaFormulario(
                                editar
                                        ? productoExistente
                                                .getIdCategoria()
                                        : null
                        );

        for (String nombre
                : categorias.values()) {

            comboCategoria.addItem(
                    nombre
            );
        }

        if (editar) {

            comboCategoria.setSelectedItem(
                    productoExistente
                            .getCategoria()
            );
        }

    } catch (SQLException e) {

        mostrarError(
                "No fue posible cargar las categorías.",
                e
        );

        return;
    }

    // ---------------------------------------------------------
    // DATOS DE EDICIÓN
    // ---------------------------------------------------------

    if (editar) {

        campoNombre.setText(
                productoExistente
                        .getNombre()
        );

        campoDescripcion.setText(
                productoExistente
                        .getDescripcion() == null
                        ? ""
                        : productoExistente
                                .getDescripcion()
        );

        campoPrecio.setText(
                productoExistente
                        .getPrecioBase()
                        .toPlainString()
        );

        comboDisponibilidad.setSelectedItem(
                productoExistente
                        .getDisponibilidadMenu()
        );

        if (productoExistente
                .getTamanoBebida() != null) {

            comboTamano.setSelectedItem(
                    productoExistente
                            .getTamanoBebida()
            );
        }

        checkCombo.setSelected(
                productoExistente
                        .isCombo()
        );

        spinnerStock.setValue(
                productoExistente
                        .getStockActual()
        );

        spinnerStockMinimo.setValue(
                productoExistente
                        .getStockMinimo()
        );

        checkActivo.setSelected(
                productoExistente
                        .isActivo()
        );

        if (productoExistente
                .getImagen() != null) {

            ImageIcon imagen =
                    cargarImagen(
                            productoExistente
                                    .getImagen()
                    );

            if (imagen != null) {

                labelImagen.setIcon(
                        imagen
                );

                labelImagen.setText(
                        ""
                );
            }
        }

    } else {

        checkActivo.setSelected(true);
    }

    // ---------------------------------------------------------
    // SELECCIONAR IMAGEN
    // ---------------------------------------------------------

    botonSeleccionarImagen.addActionListener(e -> {

        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
                "Seleccionar imagen del producto"
        );

        selector.setFileFilter(
                new javax.swing.filechooser
                        .FileNameExtensionFilter(
                                "Imágenes PNG, JPG y JPEG",
                                "png",
                                "jpg",
                                "jpeg"
                        )
        );

        int resultado =
                selector.showOpenDialog(
                        dialogo
                );

        if (resultado
                == JFileChooser.APPROVE_OPTION) {

            File archivo =
                    selector.getSelectedFile();

            try {

                String ruta =
                        copiarImagenProducto(
                                archivo
                        );

                imagenSeleccionada =
                        ruta;

                ImageIcon imagen =
                        cargarImagen(
                                ruta
                        );

                if (imagen != null) {

                    labelImagen.setIcon(
                            imagen
                    );

                    labelImagen.setText(
                            ""
                    );
                }

            } catch (IOException ex) {

                mostrarError(
                        "No fue posible guardar la imagen.",
                        ex
                );
            }
        }
    });

    // ---------------------------------------------------------
    // QUITAR IMAGEN
    // ---------------------------------------------------------

    botonQuitarImagen.addActionListener(e -> {

        imagenSeleccionada = null;

        labelImagen.setIcon(null);

        labelImagen.setText(
                "Sin imagen"
        );
    });

    // ---------------------------------------------------------
    // CAMPOS
    // ---------------------------------------------------------

    int fila = 0;

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Nombre:",
            campoNombre
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Descripción:",
            new JScrollPane(
                    campoDescripcion
            )
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Precio:",
            campoPrecio
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Categoría:",
            comboCategoria
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Disponibilidad:",
            comboDisponibilidad
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Tamaño bebida:",
            comboTamano
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Stock actual:",
            spinnerStock
    );

    agregarCampo(
            formulario,
            gbc,
            fila++,
            "Stock mínimo:",
            spinnerStockMinimo
    );

    gbc.gridx = 0;
    gbc.gridy = fila++;
    gbc.gridwidth = 2;

    formulario.add(
            checkCombo,
            gbc
    );

    gbc.gridy = fila++;

    formulario.add(
            checkActivo,
            gbc
    );

    gbc.gridy = fila++;

    formulario.add(
            labelImagen,
            gbc
    );

    JPanel panelImagenBotones =
            new JPanel(
                    new FlowLayout(
                            FlowLayout.CENTER
                    )
            );

    panelImagenBotones.add(
            botonSeleccionarImagen
    );

    panelImagenBotones.add(
            botonQuitarImagen
    );

    gbc.gridy = fila++;

    formulario.add(
            panelImagenBotones,
            gbc
    );

    // ---------------------------------------------------------
    // BOTONES
    // ---------------------------------------------------------

    JButton botonGuardar =
            new JButton(
                    editar
                            ? "Guardar cambios"
                            : "Agregar producto"
            );

    JButton botonCancelar =
            new JButton(
                    "Cancelar"
            );

    JPanel panelBotones =
            new JPanel(
                    new FlowLayout(
                            FlowLayout.RIGHT
                    )
            );

    panelBotones.add(
            botonCancelar
    );

    panelBotones.add(
            botonGuardar
    );

    botonCancelar.addActionListener(e ->
            dialogo.dispose()
    );

    // ---------------------------------------------------------
    // GUARDAR
    // ---------------------------------------------------------

    botonGuardar.addActionListener(e -> {

        try {

            String nombre =
                    campoNombre
                            .getText()
                            .trim();

            String descripcion =
                    campoDescripcion
                            .getText()
                            .trim();

            String precioTexto =
                    campoPrecio
                            .getText()
                            .trim();

            if (nombre.isBlank()) {

                JOptionPane.showMessageDialog(
                        dialogo,
                        "Ingrese el nombre del producto.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (comboCategoria
                    .getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        dialogo,
                        "Seleccione una categoría.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (precioTexto.isBlank()) {

                JOptionPane.showMessageDialog(
                        dialogo,
                        "Ingrese el precio.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            BigDecimal precio;

            try {

                precio =
                        new BigDecimal(
                                precioTexto
                        );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialogo,
                        "El precio no es válido.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (precio.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                JOptionPane.showMessageDialog(
                        dialogo,
                        "El precio no puede ser negativo.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String nombreCategoria =
                    comboCategoria
                            .getSelectedItem()
                            .toString();

            Integer idCategoria =
                    obtenerIdCategoriaPorNombre(
                            nombreCategoria
                    );

            if (idCategoria == null) {

                JOptionPane.showMessageDialog(
                        dialogo,
                        "No fue posible determinar la categoría.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String disponibilidad =
                    comboDisponibilidad
                            .getSelectedItem()
                            .toString();

            String tamano =
                    comboTamano
                            .getSelectedItem()
                            .toString();

            if (tamano.isBlank()) {

                tamano = null;
            }

            int stock =
                    (Integer)
                            spinnerStock
                                    .getValue();

            int stockMinimo =
                    (Integer)
                            spinnerStockMinimo
                                    .getValue();

            boolean esCombo =
                    checkCombo.isSelected();

            boolean activo =
                    checkActivo.isSelected();

            Producto producto;

            if (editar) {

                producto =
                        productoExistente;

            } else {

                producto =
                        new Producto();
            }

            producto.setIdCategoria(
                    idCategoria
            );

            producto.setCategoria(
                    nombreCategoria
            );

            producto.setNombre(
                    nombre
            );

            producto.setDescripcion(
                    descripcion.isBlank()
                            ? null
                            : descripcion
            );

            producto.setPrecioBase(
                    precio
            );

            producto.setImagen(
                    imagenSeleccionada
            );

            producto.setDisponibilidadMenu(
                    disponibilidad
            );

            producto.setTamanoBebida(
                    tamano
            );

            producto.setCombo(
                    esCombo
            );

            producto.setStockActual(
                    stock
            );

            producto.setStockMinimo(
                    stockMinimo
            );

            producto.setActivo(
                    activo
            );

            if (editar) {

                productoCRUD.actualizar(
                        producto
                );

                JOptionPane.showMessageDialog(
                        dialogo,
                        "Producto actualizado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                productoCRUD.insertar(
                        producto
                );

                JOptionPane.showMessageDialog(
                        dialogo,
                        "Producto agregado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            dialogo.dispose();

            cargarDatos();

        } catch (SQLException ex) {

            mostrarError(
                    "No fue posible guardar el producto.",
                    ex
            );
        }
    });

    dialogo.add(
            new JScrollPane(
                    formulario
            ),
            BorderLayout.CENTER
    );

    dialogo.add(
            panelBotones,
            BorderLayout.SOUTH
    );

    dialogo.setVisible(true);
}

private void agregarCampo(
        JPanel panel,
        GridBagConstraints gbc,
        int fila,
        String texto,
        java.awt.Component componente) {

    gbc.gridx = 0;
    gbc.gridy = fila;
    gbc.gridwidth = 1;
    gbc.weightx = 0;

    JLabel etiqueta =
            new JLabel(
                    texto
            );

    etiqueta.setFont(
            new Font(
                    "Arial",
                    Font.BOLD,
                    14
            )
    );

    panel.add(
            etiqueta,
            gbc
    );

    gbc.gridx = 1;
    gbc.weightx = 1;

    panel.add(
            componente,
            gbc
    );
}

private String copiarImagenProducto(
        File archivo)
        throws IOException {

    Path carpeta =
            Paths.get(
                    CARPETA_IMAGENES
            );

    if (!Files.exists(carpeta)) {

        Files.createDirectories(
                carpeta
        );
    }

    String nombreOriginal =
            archivo.getName();

    String nombreSinExtension =
            nombreOriginal;

    int punto =
            nombreOriginal
                    .lastIndexOf('.');

    if (punto > 0) {

        nombreSinExtension =
                nombreOriginal
                        .substring(
                                0,
                                punto
                        );
    }

    String extension = "";

    if (punto > 0) {

        extension =
                nombreOriginal
                        .substring(
                                punto
                        )
                        .toLowerCase();
    }

    nombreSinExtension =
            nombreSinExtension
                    .replaceAll(
                            "[^a-zA-Z0-9_-]",
                            "_"
                    );

    String nombreFinal =
            nombreSinExtension
            + "_"
            + System.currentTimeMillis()
            + extension;

    Path destino =
            carpeta.resolve(
                    nombreFinal
            );

    Files.copy(
            archivo.toPath(),
            destino,
            StandardCopyOption.REPLACE_EXISTING
    );

    return RECURSO_IMAGENES
            + nombreFinal;
}

private void mostrarError(
        String mensaje,
        Exception e) {

    e.printStackTrace();

    JOptionPane.showMessageDialog(
            this,
            mensaje
            + "\n\n"
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
    );
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
