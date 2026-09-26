package GUI_ADMINISTRADOR;

import CRUD.ProductoCRUD;

import Modelos.Producto;

import Utilidades.GestorImagenProducto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import java.io.File;

import java.math.BigDecimal;

import java.nio.file.Path;

import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.EmptyBorder;

import javax.swing.filechooser.FileNameExtensionFilter;

public class ProductoFormDialog
        extends JDialog {

    // ============================================================
    // COLORES
    // ============================================================

    private static final Color AZUL =
            new Color(
                    0,
                    20,
                    43
            );

    private static final Color AMARILLO =
            new Color(
                    255,
                    188,
                    0
            );

    private static final Color ROJO =
            new Color(
                    231,
                    55,
                    65
            );

    private static final Color GRIS =
            new Color(
                    92,
                    103,
                    124
            );

    private static final Color BORDE =
            new Color(
                    218,
                    223,
                    231
            );

    // ============================================================
    // DATOS
    // ============================================================

    private final ProductoCRUD crud;

    private final Producto productoOriginal;

    private boolean modificado;

    private File archivoImagenSeleccionada;

    private String rutaImagenActual;

    private boolean quitarImagen;

    // ============================================================
    // COMPONENTES
    // ============================================================

    private JLabel labelTitulo;
    private JLabel labelSubtitulo;

    private JTextField campoNombre;
    private JTextField campoPrecio;

    private JTextField campoStockActual;
    private JTextField campoStockMinimo;

    private JTextArea campoDescripcion;

    private JComboBox<CategoriaItem>
            comboCategoria;

    private JComboBox<String>
            comboDisponibilidad;

    private JComboBox<String>
            comboTamano;

    private JComboBox<String>
            comboTipo;

    private JComboBox<String>
            comboEstado;

    private JLabel labelVistaPrevia;
    private JLabel labelNombreImagen;

    private JButton botonSeleccionarImagen;
    private JButton botonQuitarImagen;

    private JButton botonCancelar;
    private JButton botonGuardar;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    private ProductoFormDialog(
            Window propietario,
            ProductoCRUD crud,
            Producto producto) {

        super(
                propietario,
                producto == null
                        ? "Agregar producto"
                        : "Editar producto",
                ModalityType.APPLICATION_MODAL
        );

        this.crud = crud;
        this.productoOriginal =
                producto;

        construirInterfaz();

        configurarEventos();

        cargarCategorias();

        if (productoOriginal != null) {

            cargarProducto();

        } else {

            configurarNuevoProducto();
        }

        setDefaultCloseOperation(
                DISPOSE_ON_CLOSE
        );

        setResizable(false);

        pack();

        setMinimumSize(
                new Dimension(
                        780,
                        760
                )
        );

        setSize(
                new Dimension(
                        780,
                        760
                )
        );

        setLocationRelativeTo(
                propietario
        );
    }

    // ============================================================
    // MOSTRAR
    // ============================================================

    public static boolean mostrar(
            Window propietario,
            ProductoCRUD crud,
            Producto producto) {

        ProductoFormDialog dialogo =
                new ProductoFormDialog(
                        propietario,
                        crud,
                        producto
                );

        dialogo.setVisible(
                true
        );

        return dialogo.modificado;
    }

    // ============================================================
    // INTERFAZ
    // ============================================================

    private void construirInterfaz() {

        JPanel principal =
                new JPanel(
                        new BorderLayout()
                );

        principal.setBackground(
                Color.WHITE
        );

        principal.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        20,
                        30
                )
        );

        // CABECERA
        JPanel cabecera =
                new JPanel();

        cabecera.setOpaque(
                false
        );

        cabecera.setLayout(
                new javax.swing.BoxLayout(
                        cabecera,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        labelTitulo =
                new JLabel(
                        productoOriginal == null
                                ? "Agregar producto"
                                : "Editar producto"
                );

        labelTitulo.setForeground(
                AZUL
        );

        labelTitulo.setFont(
                new Font(
                        "Dialog",
                        Font.BOLD,
                        27
                )
        );

        labelSubtitulo =
                new JLabel(
                        productoOriginal == null
                                ? "Completa los datos del nuevo producto"
                                : "Actualiza la información del producto"
                );

        labelSubtitulo.setForeground(
                GRIS
        );

        labelSubtitulo.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        16
                )
        );

        cabecera.add(
                labelTitulo
        );

        cabecera.add(
                javax.swing.Box.createVerticalStrut(
                        5
                )
        );

        cabecera.add(
                labelSubtitulo
        );

        principal.add(
                cabecera,
                BorderLayout.NORTH
        );

        // FORMULARIO
        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(
                false
        );

        formulario.setBorder(
                new EmptyBorder(
                        22,
                        0,
                        10,
                        0
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.anchor =
                GridBagConstraints.NORTHWEST;

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        15
                );

        gbc.weightx = 1;

        int fila = 0;

        // NOMBRE / CATEGORÍA
        agregarEtiqueta(
                formulario,
                "Nombre",
                0,
                fila
        );

        agregarEtiqueta(
                formulario,
                "Categoría",
                1,
                fila
        );

        fila++;

        campoNombre =
                crearCampoTexto();

        comboCategoria =
                crearCombo();

        agregarComponente(
                formulario,
                campoNombre,
                0,
                fila
        );

        agregarComponente(
                formulario,
                comboCategoria,
                1,
                fila
        );

        fila++;

        // PRECIO / DISPONIBILIDAD
        agregarEtiqueta(
                formulario,
                "Precio base",
                0,
                fila
        );

        agregarEtiqueta(
                formulario,
                "Disponibilidad",
                1,
                fila
        );

        fila++;

        campoPrecio =
                crearCampoTexto();

        comboDisponibilidad =
                new JComboBox<>(
                        new String[]{
                            "TODO_DIA",
                            "DESAYUNO",
                            "ALMUERZO"
                        }
                );

        configurarCombo(
                comboDisponibilidad
        );

        agregarComponente(
                formulario,
                campoPrecio,
                0,
                fila
        );

        agregarComponente(
                formulario,
                comboDisponibilidad,
                1,
                fila
        );

        fila++;

        // STOCK
        agregarEtiqueta(
                formulario,
                "Stock actual",
                0,
                fila
        );

        agregarEtiqueta(
                formulario,
                "Stock mínimo",
                1,
                fila
        );

        fila++;

        campoStockActual =
                crearCampoTexto();

        campoStockMinimo =
                crearCampoTexto();

        agregarComponente(
                formulario,
                campoStockActual,
                0,
                fila
        );

        agregarComponente(
                formulario,
                campoStockMinimo,
                1,
                fila
        );

        fila++;

        // TIPO / TAMAÑO
        agregarEtiqueta(
                formulario,
                "Tipo",
                0,
                fila
        );

        agregarEtiqueta(
                formulario,
                "Tamaño de bebida",
                1,
                fila
        );

        fila++;

        comboTipo =
                new JComboBox<>(
                        new String[]{
                            "PRODUCTO",
                            "COMBO"
                        }
                );

        configurarCombo(
                comboTipo
        );

        comboTamano =
                new JComboBox<>(
                        new String[]{
                            "NO_APLICA",
                            "PEQUENA",
                            "MEDIANA",
                            "GRANDE"
                        }
                );

        configurarCombo(
                comboTamano
        );

        agregarComponente(
                formulario,
                comboTipo,
                0,
                fila
        );

        agregarComponente(
                formulario,
                comboTamano,
                1,
                fila
        );

        fila++;

        // ESTADO
        agregarEtiqueta(
                formulario,
                "Estado",
                0,
                fila
        );

        fila++;

        comboEstado =
                new JComboBox<>(
                        new String[]{
                            "ACTIVO",
                            "INACTIVO"
                        }
                );

        configurarCombo(
                comboEstado
        );

        agregarComponente(
                formulario,
                comboEstado,
                0,
                fila
        );

        fila++;

        // DESCRIPCIÓN
        agregarEtiqueta(
                formulario,
                "Descripción",
                0,
                fila
        );

        fila++;

        campoDescripcion =
                new JTextArea(
                        3,
                        20
                );

        campoDescripcion.setLineWrap(
                true
        );

        campoDescripcion.setWrapStyleWord(
                true
        );

        campoDescripcion.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        14
                )
        );

        campoDescripcion.setBorder(
                new EmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        JScrollPane scrollDescripcion =
                new JScrollPane(
                        campoDescripcion
                );

        scrollDescripcion.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        formulario.add(
                scrollDescripcion,
                gbc
        );

        gbc.gridwidth = 1;

        fila++;

        // IMAGEN
        agregarEtiqueta(
                formulario,
                "Imagen del producto",
                0,
                fila
        );

        fila++;

        JPanel panelImagen =
                crearPanelImagen();

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;

        formulario.add(
                panelImagen,
                gbc
        );

        principal.add(
                formulario,
                BorderLayout.CENTER
        );

        // BOTONES INFERIORES
        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                15,
                                0
                        )
                );

        acciones.setOpaque(
                false
        );

        botonCancelar =
                crearBotonSecundario(
                        "Cancelar"
                );

        botonGuardar =
                crearBotonPrincipal(
                        productoOriginal == null
                                ? "Guardar producto"
                                : "Guardar cambios"
                );

        acciones.add(
                botonCancelar
        );

        acciones.add(
                botonGuardar
        );

        principal.add(
                acciones,
                BorderLayout.SOUTH
        );

        setContentPane(
                principal
        );
    }

    private JPanel crearPanelImagen() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                15,
                                10
                        )
                );

        panel.setOpaque(
                false
        );

        labelVistaPrevia =
                new JLabel(
                        "Sin imagen",
                        SwingConstants.CENTER
                );

        labelVistaPrevia.setPreferredSize(
                new Dimension(
                        220,
                        145
                )
        );

        labelVistaPrevia.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        labelVistaPrevia.setForeground(
                GRIS
        );

        JPanel derecha =
                new JPanel();

        derecha.setOpaque(
                false
        );

        derecha.setLayout(
                new javax.swing.BoxLayout(
                        derecha,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        botonSeleccionarImagen =
                crearBotonPrincipal(
                        "Seleccionar imagen"
                );

        botonQuitarImagen =
                crearBotonSecundario(
                        "Quitar imagen"
                );

        labelNombreImagen =
                new JLabel(
                        "Ninguna imagen seleccionada"
                );

        labelNombreImagen.setForeground(
                GRIS
        );

        labelNombreImagen.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        12
                )
        );

        derecha.add(
                botonSeleccionarImagen
        );

        derecha.add(
                javax.swing.Box.createVerticalStrut(
                        10
                )
        );

        derecha.add(
                botonQuitarImagen
        );

        derecha.add(
                javax.swing.Box.createVerticalStrut(
                        12
                )
        );

        derecha.add(
                labelNombreImagen
        );

        panel.add(
                labelVistaPrevia,
                BorderLayout.WEST
        );

        panel.add(
                derecha,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ============================================================
    // EVENTOS
    // ============================================================

    private void configurarEventos() {

        botonCancelar.addActionListener(
                evento -> dispose()
        );

        botonGuardar.addActionListener(
                evento -> guardar()
        );

        botonSeleccionarImagen
                .addActionListener(
                        evento ->
                                seleccionarImagen()
                );

        botonQuitarImagen
                .addActionListener(
                        evento ->
                                quitarImagen()
                );

        comboTipo.addActionListener(
                evento ->
                        actualizarTipoProducto()
        );
    }

    // ============================================================
    // NUEVO PRODUCTO
    // ============================================================

    private void configurarNuevoProducto() {

        campoStockActual.setText(
                "0"
        );

        campoStockMinimo.setText(
                "0"
        );

        comboDisponibilidad.setSelectedItem(
                "TODO_DIA"
        );

        comboTipo.setSelectedItem(
                "PRODUCTO"
        );

        comboTamano.setSelectedItem(
                "NO_APLICA"
        );

        comboEstado.setSelectedItem(
                "ACTIVO"
        );

        rutaImagenActual =
                null;

        actualizarTipoProducto();
        actualizarVistaPrevia();
    }

    // ============================================================
    // CARGAR PRODUCTO
    // ============================================================

    private void cargarProducto() {

        labelSubtitulo.setText(
                String.format(
                        "Actualiza la información de #%04d",
                        productoOriginal
                                .getIdProducto()
                )
        );

        campoNombre.setText(
                productoOriginal
                        .getNombre()
        );

        campoDescripcion.setText(
                productoOriginal
                        .getDescripcion() == null
                                ? ""
                                : productoOriginal
                                        .getDescripcion()
        );

        campoPrecio.setText(
                productoOriginal
                        .getPrecioBase()
                        .toPlainString()
        );

        campoStockActual.setText(
                String.valueOf(
                        productoOriginal
                                .getStockActual()
                )
        );

        campoStockMinimo.setText(
                String.valueOf(
                        productoOriginal
                                .getStockMinimo()
                )
        );

        comboDisponibilidad
                .setSelectedItem(
                        productoOriginal
                                .getDisponibilidadMenu()
                );

        comboTipo.setSelectedItem(
                productoOriginal
                        .isCombo()
                                ? "COMBO"
                                : "PRODUCTO"
        );

        if (productoOriginal
                .getTamanoBebida()
                == null) {

            comboTamano.setSelectedItem(
                    "NO_APLICA"
            );

        } else {

            comboTamano.setSelectedItem(
                    productoOriginal
                            .getTamanoBebida()
            );
        }

        comboEstado.setSelectedItem(
                productoOriginal
                        .isActivo()
                                ? "ACTIVO"
                                : "INACTIVO"
        );

        seleccionarCategoria(
                productoOriginal
                        .getIdCategoria()
        );

        rutaImagenActual =
                productoOriginal
                        .getImagen();

        archivoImagenSeleccionada =
                null;

        quitarImagen =
                false;

        actualizarTipoProducto();

        actualizarVistaPrevia();
    }

    // ============================================================
    // CATEGORÍAS
    // ============================================================

    private void cargarCategorias() {

        try {

            Integer categoriaActual =
                    productoOriginal == null
                            ? null
                            : productoOriginal
                                    .getIdCategoria();

            Map<Integer, String> categorias =
                    crud.listarCategoriasParaFormulario(
                            categoriaActual
                    );

            comboCategoria.removeAllItems();

            for (Map.Entry<Integer, String> entrada
                    : categorias.entrySet()) {

                comboCategoria.addItem(
                        new CategoriaItem(
                                entrada.getKey(),
                                entrada.getValue()
                        )
                );
            }

        } catch (Exception ex) {

            botonGuardar.setEnabled(
                    false
            );

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible cargar las categorías.\n"
                    + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void seleccionarCategoria(
            int idCategoria) {

        for (int i = 0;
                i < comboCategoria
                        .getItemCount();
                i++) {

            CategoriaItem item =
                    comboCategoria
                            .getItemAt(i);

            if (item.id()
                    == idCategoria) {

                comboCategoria
                        .setSelectedIndex(i);

                return;
            }
        }
    }

    // ============================================================
    // IMÁGENES
    // ============================================================

    private void seleccionarImagen() {

        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
                "Seleccionar imagen del producto"
        );

        selector.setAcceptAllFileFilterUsed(
                false
        );

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Imágenes PNG, JPG o JPEG",
                        "png",
                        "jpg",
                        "jpeg"
                )
        );

        int resultado =
                selector.showOpenDialog(
                        this
                );

        if (resultado
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        File archivo =
                selector.getSelectedFile();

        try {

            GestorImagenProducto
                    .validarImagen(
                            archivo
                    );

            archivoImagenSeleccionada =
                    archivo;

            quitarImagen =
                    false;

            actualizarVistaPrevia();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Imagen no válida",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void quitarImagen() {

        archivoImagenSeleccionada =
                null;

        quitarImagen =
                true;

        actualizarVistaPrevia();
    }

    private void actualizarVistaPrevia() {

        if (archivoImagenSeleccionada
                != null) {

            var icono =
                    GestorImagenProducto
                            .cargarIcono(
                                    archivoImagenSeleccionada,
                                    205,
                                    130
                            );

            labelVistaPrevia
                    .setText("");

            labelVistaPrevia
                    .setIcon(
                            icono
                    );

            labelNombreImagen
                    .setText(
                            archivoImagenSeleccionada
                                    .getName()
                    );

            return;
        }

        if (!quitarImagen
                && rutaImagenActual != null
                && !rutaImagenActual.isBlank()) {

            var icono =
                    GestorImagenProducto
                            .cargarIcono(
                                    rutaImagenActual,
                                    205,
                                    130
                            );

            if (icono != null) {

                labelVistaPrevia
                        .setText("");

                labelVistaPrevia
                        .setIcon(
                                icono
                        );

                try {

                    labelNombreImagen
                            .setText(
                                    Path.of(
                                            rutaImagenActual
                                    )
                                            .getFileName()
                                            .toString()
                            );

                } catch (Exception ex) {

                    labelNombreImagen
                            .setText(
                                    rutaImagenActual
                            );
                }

                return;
            }
        }

        labelVistaPrevia.setIcon(
                null
        );

        labelVistaPrevia.setText(
                "Sin imagen"
        );

        labelNombreImagen.setText(
                "Ninguna imagen seleccionada"
        );
    }

    // ============================================================
    // TIPO
    // ============================================================

    private void actualizarTipoProducto() {

        boolean combo =
                "COMBO".equals(
                        comboTipo
                                .getSelectedItem()
                );

        /*
         * Los combos no utilizan tamaño de bebida
         * en la tabla producto.
         */
        comboTamano.setEnabled(
                !combo
        );

        if (combo) {

            comboTamano.setSelectedItem(
                    "NO_APLICA"
            );
        }
    }

    // ============================================================
    // GUARDAR
    // ============================================================

    private void guardar() {

        if (!validarFormulario()) {
            return;
        }

        String nuevaRutaImagen =
                null;

        String imagenAnterior =
                productoOriginal == null
                        ? null
                        : productoOriginal
                                .getImagen();

        try {

            /*
             * Solo copiamos la nueva imagen
             * cuando el usuario presiona Guardar.
             */
            if (archivoImagenSeleccionada
                    != null) {

                nuevaRutaImagen =
                        GestorImagenProducto
                                .guardarImagen(
                                        archivoImagenSeleccionada
                                );
            }

            String imagenFinal;

            if (archivoImagenSeleccionada
                    != null) {

                imagenFinal =
                        nuevaRutaImagen;

            } else if (quitarImagen) {

                imagenFinal =
                        null;

            } else {

                imagenFinal =
                        rutaImagenActual;
            }

            Producto producto =
                    obtenerProductoFormulario(
                            imagenFinal
                    );

            if (productoOriginal == null) {

                crud.insertar(
                        producto
                );

            } else {

                crud.actualizar(
                        producto
                );
            }

            /*
             * MySQL ya confirmó el UPDATE.
             *
             * Ahora podemos eliminar una imagen antigua
             * creada por el propio programa.
             */
            if (productoOriginal != null
                    && imagenAnterior != null
                    && (archivoImagenSeleccionada != null
                    || quitarImagen)
                    && !imagenAnterior.equals(
                            imagenFinal
                    )) {

                GestorImagenProducto
                        .eliminarImagenGestionada(
                                imagenAnterior
                        );
            }

            modificado =
                    true;

            dispose();

        } catch (Exception ex) {

            /*
             * Si ya habíamos copiado una nueva imagen pero
             * MySQL falló, no dejamos un archivo huérfano.
             */
            if (nuevaRutaImagen != null) {

                GestorImagenProducto
                        .eliminarImagenGestionada(
                                nuevaRutaImagen
                        );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible guardar el producto.\n"
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Producto obtenerProductoFormulario(
            String imagen) {

        CategoriaItem categoria =
                (CategoriaItem)
                comboCategoria
                        .getSelectedItem();

        String tamano =
                (String)
                comboTamano
                        .getSelectedItem();

        if ("NO_APLICA".equals(
                tamano
        )) {

            tamano =
                    null;
        }

        boolean combo =
                "COMBO".equals(
                        comboTipo
                                .getSelectedItem()
                );

        if (combo) {

            tamano =
                    null;
        }

        return new Producto(
                productoOriginal == null
                        ? 0
                        : productoOriginal
                                .getIdProducto(),

                categoria.id(),

                categoria.nombre(),

                campoNombre
                        .getText()
                        .trim(),

                campoDescripcion
                        .getText()
                        .trim(),

                new BigDecimal(
                        campoPrecio
                                .getText()
                                .trim()
                ),

                imagen,

                (String)
                comboDisponibilidad
                        .getSelectedItem(),

                tamano,

                combo,

                Integer.parseInt(
                        campoStockActual
                                .getText()
                                .trim()
                ),

                Integer.parseInt(
                        campoStockMinimo
                                .getText()
                                .trim()
                ),

                "ACTIVO".equals(
                        comboEstado
                                .getSelectedItem()
                )
        );
    }

    // ============================================================
    // VALIDACIONES
    // ============================================================

    private boolean validarFormulario() {

        String nombre =
                campoNombre
                        .getText()
                        .trim();

        if (nombre.isEmpty()) {

            advertencia(
                    "Ingresa el nombre del producto."
            );

            campoNombre.requestFocus();

            return false;
        }

        if (nombre.length()
                > 100) {

            advertencia(
                    "El nombre no puede superar los 100 caracteres."
            );

            return false;
        }

        if (comboCategoria
                .getSelectedItem()
                == null) {

            advertencia(
                    "Selecciona una categoría."
            );

            return false;
        }

        try {

            BigDecimal precio =
                    new BigDecimal(
                            campoPrecio
                                    .getText()
                                    .trim()
                    );

            if (precio.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                advertencia(
                        "El precio no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException ex) {

            advertencia(
                    "Ingresa un precio válido. Ejemplo: 35.00"
            );

            campoPrecio.requestFocus();

            return false;
        }

        try {

            int stock =
                    Integer.parseInt(
                            campoStockActual
                                    .getText()
                                    .trim()
                    );

            if (stock < 0) {

                advertencia(
                        "El stock actual no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException ex) {

            advertencia(
                    "El stock actual debe ser un número entero."
            );

            campoStockActual.requestFocus();

            return false;
        }

        try {

            int minimo =
                    Integer.parseInt(
                            campoStockMinimo
                                    .getText()
                                    .trim()
                    );

            if (minimo < 0) {

                advertencia(
                        "El stock mínimo no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException ex) {

            advertencia(
                    "El stock mínimo debe ser un número entero."
            );

            campoStockMinimo.requestFocus();

            return false;
        }

        if (campoDescripcion
                .getText()
                .trim()
                .length()
                > 255) {

            advertencia(
                    "La descripción no puede superar los 255 caracteres."
            );

            return false;
        }

        /*
         * Exigimos que todos los productos del menú
         * tengan imagen.
         */
        boolean tieneImagen =
                archivoImagenSeleccionada != null
                || (!quitarImagen
                && rutaImagenActual != null
                && !rutaImagenActual.isBlank());

        if (!tieneImagen) {

            advertencia(
                    "Selecciona una imagen para el producto."
            );

            return false;
        }

        return true;
    }

    private void advertencia(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Verifica los datos",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ============================================================
    // COMPONENTES AUXILIARES
    // ============================================================

    private JTextField crearCampoTexto() {

        JTextField campo =
                new JTextField();

        campo.setPreferredSize(
                new Dimension(
                        310,
                        42
                )
        );

        campo.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        14
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory
                                .createLineBorder(
                                        BORDE
                                ),
                        new EmptyBorder(
                                5,
                                10,
                                5,
                                10
                        )
                )
        );

        return campo;
    }

    private <T> JComboBox<T>
            crearCombo() {

        JComboBox<T> combo =
                new JComboBox<>();

        configurarCombo(
                combo
        );

        return combo;
    }

    private void configurarCombo(
            JComboBox<?> combo) {

        combo.setPreferredSize(
                new Dimension(
                        310,
                        42
                )
        );

        combo.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        14
                )
        );

        combo.setBackground(
                Color.WHITE
        );
    }

    private void agregarEtiqueta(
            JPanel panel,
            String texto,
            int x,
            int y) {

        JLabel label =
                new JLabel(
                        texto
                );

        label.setFont(
                new Font(
                        "Dialog",
                        Font.PLAIN,
                        14
                )
        );

        label.setForeground(
                AZUL
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = x;
        gbc.gridy = y;

        gbc.weightx = 1;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.anchor =
                GridBagConstraints.WEST;

        gbc.insets =
                new Insets(
                        5,
                        5,
                        2,
                        15
                );

        panel.add(
                label,
                gbc
        );
    }

    private void agregarComponente(
            JPanel panel,
            java.awt.Component componente,
            int x,
            int y) {

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = x;
        gbc.gridy = y;

        gbc.weightx = 1;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(
                        2,
                        5,
                        10,
                        15
                );

        panel.add(
                componente,
                gbc
        );
    }

    private JButton crearBotonPrincipal(
            String texto) {

        JButton boton =
                new JButton(
                        texto
                );

        boton.setBackground(
                AMARILLO
        );

        boton.setForeground(
                Color.BLACK
        );

        boton.setFont(
                new Font(
                        "Dialog",
                        Font.BOLD,
                        14
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setPreferredSize(
                new Dimension(
                        165,
                        45
                )
        );

        return boton;
    }

    private JButton crearBotonSecundario(
            String texto) {

        JButton boton =
                new JButton(
                        texto
                );

        boton.setBackground(
                Color.WHITE
        );

        boton.setForeground(
                ROJO
        );

        boton.setFont(
                new Font(
                        "Dialog",
                        Font.BOLD,
                        14
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setPreferredSize(
                new Dimension(
                        150,
                        45
                )
        );

        return boton;
    }

    // ============================================================
    // ITEM DE CATEGORÍA
    // ============================================================

    private record CategoriaItem(
            int id,
            String nombre) {

        @Override
        public String toString() {

            return nombre;
        }
    }
}