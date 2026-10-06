package GUI_ADMINISTRADOR;

import DAO.VentaDetalleDAO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;

/**
 * Detalle de una venta. Toda la estructura visual y sus medidas están en
 * VentaDetalleFormPanel.form para editarlas desde Design en NetBeans.
 */
@SuppressWarnings("serial")
public class VentaDetalleFormPanel extends javax.swing.JPanel {

    public static final String EVENTO_CERRAR = "cerrarDetalleVenta";
    private final DecimalFormat formatoMoneda = new DecimalFormat(
            "'Q'#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private VentaDetalleDAO.Venta venta;

    /** Constructor vacío requerido por el diseñador de NetBeans. */
    public VentaDetalleFormPanel() {
        this(null);
    }

    public VentaDetalleFormPanel(VentaDetalleDAO.Venta venta) {
        this.venta = venta;
        initComponents();
        cargarVenta();
    }

    private void cargarVenta() {
        DefaultTableModel modelo =
                (DefaultTableModel) tablaDetalle.getModel();
        modelo.setRowCount(0);
        if (venta == null) {
            return;
        }

        labelTitulo.setText(String.format(
                "Detalle de venta - Orden #%04d", venta.numeroOrden()));
        labelTotal.setText("Total cobrado: " + moneda(venta.total()));

        for (VentaDetalleDAO.Linea linea : venta.lineas()) {
            agregarFila(modelo, nombreProducto(linea),
                    linea.cantidad(), moneda(linea.precioUnitario()),
                    moneda(linea.subtotal()));
        }
        tablaDetalle.clearSelection();
    }

    private static void agregarFila(DefaultTableModel modelo, Object producto,
            Object cantidad, Object precio, Object subtotal) {
        modelo.addRow(new Object[]{
            producto, cantidad, precio, subtotal
        });
    }

    private static String nombreProducto(VentaDetalleDAO.Linea linea) {
        String presentacion = linea.presentacion();
        if (presentacion == null || presentacion.isBlank()
                || "INDIVIDUAL".equalsIgnoreCase(presentacion)) {
            return linea.nombre();
        }
        return linea.nombre() + " - " + presentacion;
    }

    private String moneda(BigDecimal valor) {
        return formatoMoneda.format(valor == null ? BigDecimal.ZERO : valor);
    }

    /**
     * Este método es generado por el diseñador de NetBeans.
     * Las medidas se editan en VentaDetalleFormPanel.form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTarjeta = new Componentes.PanelFlotante();
        panelTitulo = new Componentes.PanelFlotante();
        labelTitulo = new javax.swing.JLabel();
        scrollDetalle = new javax.swing.JScrollPane();
        tablaDetalle = new Componentes.TablaAdministrativa();
        labelTotal = new javax.swing.JLabel();
        botonCerrar = new Componentes.BotonRedondeado();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1080, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelTarjeta.setColorBorde(new java.awt.Color(222, 227, 234));
        panelTarjeta.setRadio(22);
        panelTarjeta.setSombra(false);
        panelTarjeta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelTitulo.setColorBorde(new java.awt.Color(1, 15, 30));
        panelTitulo.setColorFondo(new java.awt.Color(1, 15, 30));
        panelTitulo.setRadio(22);
        panelTitulo.setSombra(false);
        panelTitulo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(255, 255, 255));
        labelTitulo.setText("Detalle de venta - Orden #0000");
        panelTitulo.add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 1000, 38));

        panelTarjeta.add(panelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1080, 80));

        scrollDetalle.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 229, 235)));
        scrollDetalle.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDetalle.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tablaDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Cantidad", "Precio", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaDetalle.setColumnasCentradas("1");
        tablaDetalle.setColumnasDerecha("2,3");
        tablaDetalle.setFilasAlternadas(true);
        tablaDetalle.setAnchosColumnas("570,100,160,160");
        scrollDetalle.setViewportView(tablaDetalle);

        panelTarjeta.add(scrollDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 105, 1020, 455));

        labelTotal.setFont(new java.awt.Font("Dialog", 1, 22)); // NOI18N
        labelTotal.setForeground(new java.awt.Color(0, 20, 43));
        labelTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelTotal.setText("Total cobrado: Q0.00");
        panelTarjeta.add(labelTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 580, 350, 48));

        botonCerrar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonCerrar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonCerrar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonCerrar.setDegradado(false);
        botonCerrar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        botonCerrar.setForeground(new java.awt.Color(231, 55, 65));
        botonCerrar.setGrosorBorde(1.0F);
        botonCerrar.setText("Cerrar");
        botonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarActionPerformed(evt);
            }
        });
        panelTarjeta.add(botonCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 580, 200, 48));

        add(panelTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1080, 650));
    }// </editor-fold>//GEN-END:initComponents

    private void botonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarActionPerformed
        firePropertyChange(EVENTO_CERRAR, false, true);
    }//GEN-LAST:event_botonCerrarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonRedondeado botonCerrar;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelTotal;
    private Componentes.PanelFlotante panelTarjeta;
    private Componentes.PanelFlotante panelTitulo;
    private javax.swing.JScrollPane scrollDetalle;
    private Componentes.TablaAdministrativa tablaDetalle;
    // End of variables declaration//GEN-END:variables
}
