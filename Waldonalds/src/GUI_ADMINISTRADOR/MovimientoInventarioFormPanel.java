package GUI_ADMINISTRADOR;

import DAO.InventarioDAO;
import Modelos.ArticuloInventario;
import Utilidades.TemaAdmin;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** Formulario visual para registrar entradas, salidas y ajustes. */
@SuppressWarnings({"serial", "this-escape"})
public class MovimientoInventarioFormPanel extends javax.swing.JPanel {

    public static final String EVENTO_GUARDADO = "movimientoGuardado";
    public static final String EVENTO_CANCELAR = "cancelarMovimiento";

    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(222, 227, 234);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final BigDecimal MAXIMO = new BigDecimal("99999999.99");

    private final TemaAdmin tema = new TemaAdmin();
    private InventarioDAO dao;
    private ArticuloInventario articulo;
    private String tipoSeleccionado = "ENTRADA";

    public MovimientoInventarioFormPanel() {
        this(null, null, null);
    }

    public MovimientoInventarioFormPanel(InventarioDAO dao,
            ArticuloInventario articulo, String tipoFijo) {
        this.dao = dao;
        this.articulo = articulo;
        initComponents();
        configurarAspecto();
        configurarSelector(tipoFijo);
        cargarArticulo();
    }

    private void configurarAspecto() {
        labelTitulo.setFont(tema.negrita(25f));
        labelSubtitulo.setFont(tema.regular(15f));
        labelSubtitulo.setForeground(SECUNDARIO);
        labelError.setFont(tema.media(13f));
        labelError.setForeground(ROJO);
        labelInstruccion.setFont(tema.regular(12f));
        labelInstruccion.setForeground(SECUNDARIO);
        for (javax.swing.JLabel etiqueta : new javax.swing.JLabel[]{
            labelTipo, labelCantidad, labelMotivo, labelStock}) {
            etiqueta.setFont(tema.media(14f));
            etiqueta.setForeground(AZUL);
        }
        for (JTextField campo : new JTextField[]{
            campoCantidad, campoMotivo, campoStock}) {
            campo.setFont(tema.regular(14f));
            campo.setForeground(AZUL);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE, 1, true),
                    new EmptyBorder(0, 13, 0, 13)));
        }
        campoStock.setBackground(new Color(248, 249, 251));
        botonCancelar.setFont(tema.negrita(14f));
        botonGuardar.setFont(tema.negrita(14f));
    }

    private void configurarSelector(String tipoFijo) {
        selectorTipo.setFont(tema.regular(14f));
        selectorTipo.setForeground(AZUL);
        selectorTipo.setColorFondo(Color.WHITE);
        selectorTipo.setColorHover(new Color(248, 249, 251));
        selectorTipo.setColorDesplegado(new Color(255, 247, 222));
        selectorTipo.setColorTextoOpcion(AZUL);
        selectorTipo.setColorBordeMenu(BORDE);
        selectorTipo.setAnchoMenu(315);
        selectorTipo.setAltoOpcion(42);
        selectorTipo.addMenuOpcionListener(evento -> {
            seleccionarTipo(evento.getActionCommand());
        });
        if (tipoFijo != null) {
            seleccionarTipo(etiquetaTipo(tipoFijo));
            selectorTipo.setEnabled(false);
        } else {
            seleccionarTipo("Entrada");
        }
    }

    private void seleccionarTipo(String etiqueta) {
        selectorTipo.setText(etiqueta);
        tipoSeleccionado = switch (etiqueta) {
            case "Salida" -> "SALIDA";
            case "Ajuste" -> "AJUSTE";
            default -> "ENTRADA";
        };
        labelInstruccion.setText("AJUSTE".equals(tipoSeleccionado)
                ? "En Ajuste, escribe el stock final que debe quedar."
                : "Escribe cuánto se "
                    + ("SALIDA".equals(tipoSeleccionado) ? "retira" : "agrega")
                    + " del inventario.");
    }

    private String etiquetaTipo(String tipo) {
        return switch (tipo) {
            case "SALIDA" -> "Salida";
            case "AJUSTE" -> "Ajuste";
            default -> "Entrada";
        };
    }

    private void cargarArticulo() {
        if (articulo == null) {
            labelSubtitulo.setText("Selecciona un artículo desde Existencias");
            campoStock.setText("—");
            botonGuardar.setEnabled(false);
            return;
        }
        labelSubtitulo.setText(articulo.getCodigoVisible() + " · "
                + articulo.getNombre());
        campoStock.setText(articulo.getStockActual().stripTrailingZeros()
                .toPlainString() + " " + articulo.getUnidad());
        labelCantidad.setText("Cantidad (" + articulo.getUnidad() + ")");
    }

    private void guardar() {
        labelError.setText(" ");
        if (dao == null || articulo == null) {
            labelError.setText("No hay un artículo seleccionado.");
            return;
        }
        BigDecimal cantidad;
        try {
            cantidad = new BigDecimal(campoCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            labelError.setText("Ingresa una cantidad válida. Usa punto decimal.");
            return;
        }
        boolean ajuste = "AJUSTE".equals(tipoSeleccionado);
        if (cantidad.scale() > 2 || cantidad.compareTo(MAXIMO) > 0
                || (ajuste ? cantidad.signum() < 0 : cantidad.signum() <= 0)) {
            labelError.setText("Cantidad inválida; usa hasta dos decimales.");
            return;
        }
        String motivo = campoMotivo.getText().trim();
        if (motivo.length() > 150) {
            labelError.setText("El motivo admite hasta 150 caracteres.");
            return;
        }
        botonGuardar.setEnabled(false);
        try {
            dao.registrarMovimiento(articulo, tipoSeleccionado, cantidad, motivo);
            firePropertyChange(EVENTO_GUARDADO, false, true);
        } catch (SQLException | IllegalArgumentException ex) {
            labelError.setText("No fue posible registrar: " + ex.getMessage());
        } finally {
            botonGuardar.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelTarjeta = new Componentes.PanelFlotante();
        panelIcono = new Componentes.PanelCircular();
        labelIcono = new javax.swing.JLabel();
        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        labelTipo = new javax.swing.JLabel();
        selectorTipo = new Componentes.BotonDesplegable();
        labelStock = new javax.swing.JLabel();
        campoStock = new javax.swing.JTextField();
        labelCantidad = new javax.swing.JLabel();
        campoCantidad = new javax.swing.JTextField();
        labelMotivo = new javax.swing.JLabel();
        campoMotivo = new javax.swing.JTextField();
        labelInstruccion = new javax.swing.JLabel();
        labelError = new javax.swing.JLabel();
        botonCancelar = new Componentes.BotonRedondeado();
        botonGuardar = new Componentes.BotonDerretido();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(700, 500));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelTarjeta.setColorBorde(new java.awt.Color(222, 227, 234));
        panelTarjeta.setRadio(26);
        panelTarjeta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelIcono.setColorFondo(new java.awt.Color(255, 244, 211));
        panelIcono.setLayout(new java.awt.BorderLayout());
        labelIcono.setFont(new java.awt.Font("Dialog", 1, 31));
        labelIcono.setForeground(new java.awt.Color(0, 20, 43));
        labelIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelIcono.setText("±");
        panelIcono.add(labelIcono, java.awt.BorderLayout.CENTER);
        panelTarjeta.add(panelIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 26, 70, 70));
        labelTitulo.setForeground(new java.awt.Color(0, 20, 43));
        labelTitulo.setText("Movimiento de inventario");
        panelTarjeta.add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 27, 535, 34));
        labelSubtitulo.setText("Artículo seleccionado");
        panelTarjeta.add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 62, 535, 26));

        labelTipo.setText("Tipo de movimiento");
        panelTarjeta.add(labelTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 120, 315, 24));
        selectorTipo.setText("Entrada");
        selectorTipo.setTextoDesplegable("Entrada;Salida;Ajuste");
        panelTarjeta.add(selectorTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 148, 315, 46));
        labelStock.setText("Stock actual");
        panelTarjeta.add(labelStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 120, 285, 24));
        campoStock.setEditable(false);
        panelTarjeta.add(campoStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 148, 285, 46));
        labelCantidad.setText("Cantidad");
        panelTarjeta.add(labelCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 220, 315, 24));
        panelTarjeta.add(campoCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 248, 315, 46));
        labelMotivo.setText("Motivo (opcional)");
        panelTarjeta.add(labelMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, 285, 24));
        panelTarjeta.add(campoMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 248, 285, 46));
        labelInstruccion.setText("Escribe cuánto se agrega del inventario.");
        panelTarjeta.add(labelInstruccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 315, 630, 28));
        labelError.setText(" ");
        panelTarjeta.add(labelError, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 365, 630, 30));

        botonCancelar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonCancelar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonCancelar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonCancelar.setDegradado(false);
        botonCancelar.setForeground(new java.awt.Color(231, 55, 65));
        botonCancelar.setText("Cancelar");
        botonCancelar.addActionListener(e -> botonCancelarActionPerformed(e));
        panelTarjeta.add(botonCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 425, 130, 46));
        botonGuardar.setText("Guardar movimiento");
        botonGuardar.addActionListener(e -> botonGuardarActionPerformed(e));
        panelTarjeta.add(botonGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 425, 215, 46));
        add(panelTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 700, 500));
    }

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        firePropertyChange(EVENTO_CANCELAR, false, true);
    }

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        guardar();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonRedondeado botonCancelar;
    private Componentes.BotonDerretido botonGuardar;
    private javax.swing.JTextField campoCantidad;
    private javax.swing.JTextField campoMotivo;
    private javax.swing.JTextField campoStock;
    private javax.swing.JLabel labelCantidad;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelIcono;
    private javax.swing.JLabel labelInstruccion;
    private javax.swing.JLabel labelMotivo;
    private javax.swing.JLabel labelStock;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTipo;
    private javax.swing.JLabel labelTitulo;
    private Componentes.PanelCircular panelIcono;
    private Componentes.PanelFlotante panelTarjeta;
    private Componentes.BotonDesplegable selectorTipo;
    // End of variables declaration//GEN-END:variables
}
