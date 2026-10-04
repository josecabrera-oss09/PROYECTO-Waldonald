package GUI_ADMINISTRADOR;

import CRUD.IngredienteCRUD;
import Modelos.Ingrediente;
import Utilidades.TemaAdmin;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** Formulario visual editable desde Design para crear o editar ingredientes. */
@SuppressWarnings({"serial", "this-escape"})
public class IngredienteFormPanel extends javax.swing.JPanel {

    public static final String EVENTO_GUARDADO = "ingredienteGuardado";
    public static final String EVENTO_CANCELAR = "cancelarIngrediente";

    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(222, 227, 234);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final BigDecimal MAXIMO = new BigDecimal("99999999.99");

    private final TemaAdmin tema = new TemaAdmin();
    private IngredienteCRUD crud;
    private Ingrediente original;
    private boolean activo = true;

    public IngredienteFormPanel() {
        this(null, null);
    }

    public IngredienteFormPanel(IngredienteCRUD crud, Ingrediente original) {
        this.crud = crud;
        this.original = original;
        initComponents();
        configurarAspecto();
        configurarSelector();
        cargarModo();
    }

    private void configurarAspecto() {
        labelTitulo.setFont(tema.negrita(25f));
        labelSubtitulo.setFont(tema.regular(15f));
        labelSubtitulo.setForeground(SECUNDARIO);
        labelError.setFont(tema.media(13f));
        labelError.setForeground(ROJO);
        labelNota.setFont(tema.regular(12f));
        labelNota.setForeground(SECUNDARIO);

        for (javax.swing.JLabel etiqueta : new javax.swing.JLabel[]{
            labelNombre, labelUnidad, labelStockActual, labelStockMinimo,
            labelEstado}) {
            etiqueta.setFont(tema.media(14f));
            etiqueta.setForeground(AZUL);
        }
        for (JTextField campo : new JTextField[]{
            campoNombre, campoUnidad, campoStockActual, campoStockMinimo}) {
            campo.setFont(tema.regular(14f));
            campo.setForeground(AZUL);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE, 1, true),
                    new EmptyBorder(0, 13, 0, 13)));
        }
        campoStockActual.setBackground(new Color(248, 249, 251));
        botonGuardar.setFont(tema.negrita(14f));
        botonCancelar.setFont(tema.negrita(14f));
        botonDesactivar.setFont(tema.negrita(14f));
    }

    private void configurarSelector() {
        selectorEstado.setFont(tema.regular(14f));
        selectorEstado.setForeground(AZUL);
        selectorEstado.setColorFondo(Color.WHITE);
        selectorEstado.setColorHover(new Color(248, 249, 251));
        selectorEstado.setColorDesplegado(new Color(255, 247, 222));
        selectorEstado.setColorTextoOpcion(AZUL);
        selectorEstado.setColorBordeMenu(BORDE);
        selectorEstado.setAnchoMenu(325);
        selectorEstado.setAltoOpcion(42);
        selectorEstado.addMenuOpcionListener(evento -> {
            activo = "Activo".equals(evento.getActionCommand());
            selectorEstado.setText(evento.getActionCommand());
        });
    }

    private void cargarModo() {
        boolean editando = original != null;
        labelTitulo.setText(editando ? "Editar ingrediente" : "Agregar ingrediente");
        labelSubtitulo.setText(editando
                ? "Actualiza la información de "
                    + String.format("#%04d", original.getIdIngrediente())
                : "Completa los datos del nuevo ingrediente");
        botonDesactivar.setVisible(editando && original.isActivo());
        if (!editando) {
            campoStockActual.setText("0.00");
            campoStockMinimo.setText("0.00");
            selectorEstado.setText("Activo");
            return;
        }
        campoNombre.setText(original.getNombre());
        campoUnidad.setText(original.getUnidadMedida());
        campoStockActual.setText(original.getStockActual().toPlainString());
        campoStockMinimo.setText(original.getStockMinimo().toPlainString());
        activo = original.isActivo();
        selectorEstado.setText(activo ? "Activo" : "Inactivo");
    }

    private void guardar() {
        labelError.setText(" ");
        if (crud == null) {
            labelError.setText("El formulario no tiene conexión CRUD configurada.");
            return;
        }
        String nombre = campoNombre.getText().trim();
        String unidad = campoUnidad.getText().trim();
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
                || minimo.compareTo(MAXIMO) > 0) {
            labelError.setText("El mínimo debe ser positivo y tener hasta dos decimales.");
            return;
        }
        botonGuardar.setEnabled(false);
        try {
            Ingrediente ingrediente = original == null
                    ? new Ingrediente() : original;
            ingrediente.setNombre(nombre);
            ingrediente.setUnidadMedida(unidad);
            ingrediente.setStockMinimo(minimo);
            ingrediente.setActivo(activo);
            if (original == null) crud.insertar(ingrediente);
            else crud.actualizar(ingrediente);
            firePropertyChange(EVENTO_GUARDADO, false, true);
        } catch (SQLException ex) {
            labelError.setText("No fue posible guardar: " + ex.getMessage());
        } finally {
            botonGuardar.setEnabled(true);
        }
    }

    private void desactivar() {
        if (original == null) return;
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desactivar «" + original.getNombre() + "»?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        try {
            crud.cambiarEstado(original.getIdIngrediente(), false);
            firePropertyChange(EVENTO_GUARDADO, false, true);
        } catch (SQLException ex) {
            labelError.setText("No fue posible desactivar: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelTarjeta = new Componentes.PanelFlotante();
        panelIcono = new Componentes.PanelCircular();
        labelIcono = new Labels.LabelEscalable();
        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        labelNombre = new javax.swing.JLabel();
        campoNombre = new javax.swing.JTextField();
        labelUnidad = new javax.swing.JLabel();
        campoUnidad = new javax.swing.JTextField();
        labelStockActual = new javax.swing.JLabel();
        campoStockActual = new javax.swing.JTextField();
        labelStockMinimo = new javax.swing.JLabel();
        campoStockMinimo = new javax.swing.JTextField();
        labelEstado = new javax.swing.JLabel();
        selectorEstado = new Componentes.BotonDesplegable();
        labelNota = new javax.swing.JLabel();
        labelError = new javax.swing.JLabel();
        botonDesactivar = new Componentes.BotonRedondeado();
        botonCancelar = new Componentes.BotonRedondeado();
        botonGuardar = new Componentes.BotonDerretido();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(760, 515));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelTarjeta.setColorBorde(new java.awt.Color(222, 227, 234));
        panelTarjeta.setRadio(26);
        panelTarjeta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelIcono.setColorFondo(new java.awt.Color(255, 244, 211));
        panelIcono.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        labelIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lista_icono.png")));
        labelIcono.setMantenerProporcion(true);
        panelIcono.add(labelIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 40, 40));
        panelTarjeta.add(panelIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 26, 70, 70));
        labelTitulo.setForeground(new java.awt.Color(0, 20, 43));
        labelTitulo.setText("Agregar ingrediente");
        panelTarjeta.add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 27, 590, 34));
        labelSubtitulo.setText("Completa los datos del nuevo ingrediente");
        panelTarjeta.add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 62, 590, 26));

        labelNombre.setText("Nombre");
        panelTarjeta.add(labelNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 325, 24));
        panelTarjeta.add(campoNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 148, 325, 46));
        labelUnidad.setText("Unidad de medida");
        panelTarjeta.add(labelUnidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 120, 325, 24));
        panelTarjeta.add(campoUnidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 148, 325, 46));
        labelStockActual.setText("Stock actual (solo lectura)");
        panelTarjeta.add(labelStockActual, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 325, 24));
        campoStockActual.setEditable(false);
        panelTarjeta.add(campoStockActual, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 248, 325, 46));
        labelStockMinimo.setText("Stock mínimo");
        panelTarjeta.add(labelStockMinimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 220, 325, 24));
        panelTarjeta.add(campoStockMinimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 248, 325, 46));
        labelEstado.setText("Estado");
        panelTarjeta.add(labelEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 325, 24));
        selectorEstado.setText("Activo");
        selectorEstado.setTextoDesplegable("Activo;Inactivo");
        panelTarjeta.add(selectorEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 348, 325, 46));
        labelNota.setText("<html>El stock se modifica desde <b>Existencias</b>, "
                + "mediante una entrada, salida o ajuste.</html>");
        panelTarjeta.add(labelNota, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 345, 325, 52));
        labelError.setText(" ");
        panelTarjeta.add(labelError, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 410, 680, 24));

        botonDesactivar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonDesactivar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonDesactivar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonDesactivar.setDegradado(false);
        botonDesactivar.setForeground(new java.awt.Color(231, 55, 65));
        botonDesactivar.setText("Desactivar ingrediente");
        botonDesactivar.addActionListener(e -> botonDesactivarActionPerformed(e));
        panelTarjeta.add(botonDesactivar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 449, 230, 46));
        botonCancelar.setColorInicio(new java.awt.Color(255, 255, 255));
        botonCancelar.setColorFinal(new java.awt.Color(255, 255, 255));
        botonCancelar.setColorBorde(new java.awt.Color(231, 55, 65));
        botonCancelar.setDegradado(false);
        botonCancelar.setForeground(new java.awt.Color(231, 55, 65));
        botonCancelar.setText("Cancelar");
        botonCancelar.addActionListener(e -> botonCancelarActionPerformed(e));
        panelTarjeta.add(botonCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 449, 140, 46));
        botonGuardar.setText("Guardar ingrediente");
        botonGuardar.addActionListener(e -> botonGuardarActionPerformed(e));
        panelTarjeta.add(botonGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 449, 200, 46));
        add(panelTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 760, 515));
    }

    private void botonDesactivarActionPerformed(java.awt.event.ActionEvent evt) {
        desactivar();
    }

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        firePropertyChange(EVENTO_CANCELAR, false, true);
    }

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        guardar();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonRedondeado botonCancelar;
    private Componentes.BotonRedondeado botonDesactivar;
    private Componentes.BotonDerretido botonGuardar;
    private javax.swing.JTextField campoNombre;
    private javax.swing.JTextField campoStockActual;
    private javax.swing.JTextField campoStockMinimo;
    private javax.swing.JTextField campoUnidad;
    private javax.swing.JLabel labelError;
    private Labels.LabelEscalable labelIcono;
    private javax.swing.JLabel labelEstado;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JLabel labelNota;
    private javax.swing.JLabel labelStockActual;
    private javax.swing.JLabel labelStockMinimo;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelUnidad;
    private Componentes.PanelCircular panelIcono;
    private Componentes.PanelFlotante panelTarjeta;
    private Componentes.BotonDesplegable selectorEstado;
    // End of variables declaration//GEN-END:variables
}
