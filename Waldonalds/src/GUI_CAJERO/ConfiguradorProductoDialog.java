package GUI_CAJERO;

import Componentes.BotonRedondeado;
import DAO.ConfiguracionMenuDAO;
import Modelos.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Configura presentaciones, elecciones y cada producto interno del combo. */
public final class ConfiguradorProductoDialog extends JDialog {
    private static final Color AZUL = new Color(1, 20, 36);
    private static final Color AMARILLO = new Color(255, 188, 13);
    private static final Color BORDE = new Color(226, 229, 234);
    private static final Color TEXTO_SECUNDARIO = new Color(102, 108, 118);
    private final ConfiguracionProducto producto;
    private final JTabbedPane presentaciones = new JTabbedPane();
    private final Map<Integer, VistaPresentacion> vistas = new LinkedHashMap<>();
    private final JLabel precio = new JLabel();
    private LineaPedido resultado;

    public static LineaPedido configurar(Window propietario, int idProducto) throws Exception {
        ConfiguracionProducto producto = new ConfiguracionMenuDAO().cargarProducto(idProducto);
        return configurar(propietario, producto);
    }

    public static LineaPedido configurar(Window propietario,
            ConfiguracionProducto producto) {
        ConfiguradorProductoDialog dialogo = new ConfiguradorProductoDialog(propietario, producto);
        dialogo.setVisible(true);
        return dialogo.resultado;
    }

    private ConfiguradorProductoDialog(Window propietario, ConfiguracionProducto producto) {
        super(propietario, "Configurar " + producto.nombre(), ModalityType.APPLICATION_MODAL);
        this.producto = producto;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 690));
        setPreferredSize(new Dimension(880, 780));
        setContentPane(crearContenido());
        pack();
        setLocationRelativeTo(propietario);
    }

    private JComponent crearContenido() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(new Color(247, 248, 250));
        raiz.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cabecera = new JPanel(new BorderLayout(12, 4));
        cabecera.setBackground(AZUL);
        cabecera.setBorder(new EmptyBorder(18, 22, 18, 22));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel(producto.nombre());
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel ayuda = new JLabel(producto.descripcion() == null || producto.descripcion().isBlank()
                ? "Elige cómo preparar este producto" : producto.descripcion());
        ayuda.setForeground(new Color(220, 226, 233));
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textos.add(titulo); textos.add(Box.createVerticalStrut(4)); textos.add(ayuda);
        precio.setForeground(AMARILLO);
        precio.setFont(new Font("SansSerif", Font.BOLD, 24));
        cabecera.add(textos, BorderLayout.CENTER);
        cabecera.add(precio, BorderLayout.EAST);
        raiz.add(cabecera, BorderLayout.NORTH);

        presentaciones.setFont(new Font("SansSerif", Font.BOLD, 14));
        presentaciones.setBackground(Color.WHITE);
        for (PresentacionMenu presentacion : producto.presentaciones()) {
            VistaPresentacion vista = new VistaPresentacion(presentacion);
            vistas.put(presentacion.idPresentacion(), vista);
            presentaciones.addTab(presentacion.nombre(), vista);
            if (presentacion.predeterminada()) presentaciones.setSelectedComponent(vista);
        }
        presentaciones.addChangeListener(e -> actualizarPrecio());
        raiz.add(presentaciones, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);
        pie.setBorder(new EmptyBorder(14, 0, 0, 0));
        BotonRedondeado cancelar = boton("Cancelar", Color.WHITE, new Color(196, 30, 42));
        BotonRedondeado agregar = boton("Agregar al pedido", AMARILLO, AZUL);
        cancelar.addActionListener(e -> dispose());
        agregar.addActionListener(e -> confirmar());
        pie.add(cancelar, BorderLayout.WEST);
        pie.add(agregar, BorderLayout.EAST);
        raiz.add(pie, BorderLayout.SOUTH);
        SwingUtilities.invokeLater(this::actualizarPrecio);
        return raiz;
    }

    private BotonRedondeado boton(String texto, Color fondo, Color frente) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setForeground(frente);
        boton.setDegradado(false);
        boton.setColorInicio(fondo);
        boton.setColorFinal(fondo);
        boton.setColorBorde(fondo.equals(Color.WHITE) ? new Color(196,30,42) : fondo);
        boton.setGrosorBorde(1f);
        boton.setRadio(18);
        boton.setPreferredSize(new Dimension(210, 48));
        return boton;
    }

    private VistaPresentacion vistaActual() {
        return (VistaPresentacion) presentaciones.getSelectedComponent();
    }

    private void actualizarPrecio() {
        VistaPresentacion vista = vistaActual();
        if (vista == null) return;
        precio.setText("Q" + vista.precioActual().setScale(2).toPlainString());
    }

    private void confirmar() {
        try {
            VistaPresentacion vista = vistaActual();
            List<OpcionPedido> opciones = vista.crearOpciones();
            resultado = new LineaPedido(UUID.randomUUID().toString(), producto.idProducto(),
                    vista.presentacion.idPresentacion(), producto.nombre(),
                    vista.presentacion.nombre(), vista.presentacion.precio(), opciones, 1);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Completa el producto", JOptionPane.WARNING_MESSAGE);
        }
    }

    private final class VistaPresentacion extends JPanel {
        private final PresentacionMenu presentacion;
        private final List<SelectorGrupo> selectores = new ArrayList<>();

        VistaPresentacion(PresentacionMenu presentacion) {
            super(new BorderLayout());
            this.presentacion = presentacion;
            setBackground(Color.WHITE);
            JPanel contenido = new JPanel();
            contenido.setBackground(Color.WHITE);
            contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
            contenido.setBorder(new EmptyBorder(18, 22, 18, 22));
            for (GrupoMenu grupo : presentacion.grupos()) {
                SelectorGrupo selector = new SelectorGrupo(grupo);
                selectores.add(selector);
                if (grupo.visible() || selector.tienePersonalizaciones()) {
                    contenido.add(selector);
                    contenido.add(Box.createVerticalStrut(14));
                }
            }
            JScrollPane scroll = new JScrollPane(contenido);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getVerticalScrollBar().setUnitIncrement(22);
            scroll.getViewport().setBackground(Color.WHITE);
            add(scroll);
        }

        BigDecimal precioActual() {
            BigDecimal total = presentacion.precio();
            for (SelectorGrupo selector : selectores) total = total.add(selector.precioExtra());
            return total;
        }

        List<OpcionPedido> crearOpciones() {
            List<OpcionPedido> resultado = new ArrayList<>();
            for (SelectorGrupo selector : selectores) resultado.addAll(selector.crearOpciones());
            return resultado;
        }
    }

    private final class SelectorGrupo extends JPanel {
        private final GrupoMenu grupo;
        private final JPanel opciones = new JPanel();
        private final JPanel personalizaciones = new JPanel();
        private final List<AbstractButton> botones = new ArrayList<>();
        private final List<JComboBox<OpcionMenu>> listas = new ArrayList<>();
        private final Map<String, PersonalizacionProducto> panelesProducto = new LinkedHashMap<>();

        SelectorGrupo(GrupoMenu grupo) {
            super(new BorderLayout(0, 9));
            this.grupo = grupo;
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE, 1, true),
                    new EmptyBorder(14, 16, 14, 16)));
            JLabel titulo = new JLabel(grupo.nombre().startsWith("__") ? "" : grupo.nombre());
            titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
            titulo.setForeground(AZUL);
            String regla = grupo.minimo() == grupo.maximo()
                    ? grupo.minimo() + (grupo.minimo() == 1 ? " requerido" : " requeridos")
                    : "Elige de " + grupo.minimo() + " a " + grupo.maximo();
            JLabel detalle = new JLabel(regla);
            detalle.setForeground(TEXTO_SECUNDARIO);
            detalle.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JPanel encabezado = new JPanel();
            encabezado.setOpaque(false);
            encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
            encabezado.add(titulo); encabezado.add(detalle);
            if (grupo.visible()) add(encabezado, BorderLayout.NORTH);

            opciones.setOpaque(false);
            opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));
            crearControles();
            personalizaciones.setOpaque(false);
            personalizaciones.setLayout(new BoxLayout(personalizaciones, BoxLayout.Y_AXIS));
            JPanel centro = new JPanel();
            centro.setOpaque(false);
            centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
            centro.add(opciones); centro.add(personalizaciones);
            add(centro, BorderLayout.CENTER);
            reconstruirPersonalizaciones();
        }

        private void crearControles() {
            if (!grupo.visible()) return;
            if (grupo.permiteRepetir() && grupo.maximo() > 1) {
                for (int i=0; i<grupo.maximo(); i++) {
                    JPanel fila = new JPanel(new BorderLayout(10,0));
                    fila.setOpaque(false);
                    JLabel etiqueta = new JLabel("Producto " + (i+1));
                    etiqueta.setPreferredSize(new Dimension(95, 34));
                    JComboBox<OpcionMenu> lista = new JComboBox<>();
                    if (i >= grupo.minimo()) lista.addItem(null);
                    for (OpcionMenu opcion : grupo.opciones()) lista.addItem(opcion);
                    if (i < grupo.minimo()) seleccionarPredeterminada(lista);
                    else lista.setSelectedIndex(0);
                    lista.addActionListener(e -> cambioSeleccion());
                    listas.add(lista);
                    fila.add(etiqueta, BorderLayout.WEST); fila.add(lista, BorderLayout.CENTER);
                    opciones.add(fila); opciones.add(Box.createVerticalStrut(7));
                }
            } else {
                ButtonGroup radios = grupo.maximo() == 1 ? new ButtonGroup() : null;
                if (radios != null && grupo.minimo() == 0) {
                    JRadioButton ninguna = new JRadioButton("No agregar");
                    ninguna.setOpaque(false);
                    ninguna.setSelected(true);
                    ninguna.addActionListener(e -> cambioSeleccion());
                    radios.add(ninguna);
                    opciones.add(ninguna);
                }
                for (OpcionMenu opcion : grupo.opciones()) {
                    AbstractButton control = radios == null
                            ? new JCheckBox(opcion.toString()) : new JRadioButton(opcion.toString());
                    control.putClientProperty("opcion", opcion);
                    control.setOpaque(false);
                    control.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    control.setForeground(AZUL);
                    control.setSelected(opcion.predeterminada());
                    control.addActionListener(e -> cambioSeleccion());
                    if (radios != null) radios.add(control);
                    botones.add(control); opciones.add(control);
                }
                if (grupo.minimo() > 0 && botones.stream().noneMatch(AbstractButton::isSelected)
                        && !botones.isEmpty()) botones.get(0).setSelected(true);
            }
        }

        private void seleccionarPredeterminada(JComboBox<OpcionMenu> lista) {
            for (int i=0; i<lista.getItemCount(); i++) {
                OpcionMenu opcion = lista.getItemAt(i);
                if (opcion != null && opcion.predeterminada()) { lista.setSelectedIndex(i); return; }
            }
            if (lista.getItemCount() > 0 && grupo.minimo() > 0) lista.setSelectedIndex(0);
        }

        private void cambioSeleccion() {
            reconstruirPersonalizaciones();
            actualizarPrecio();
        }

        private List<Seleccion> seleccionadas() {
            List<Seleccion> seleccionadas = new ArrayList<>();
            if (!grupo.visible()) {
                int numero = 1;
                List<OpcionMenu> defaults = grupo.opciones().stream().filter(OpcionMenu::predeterminada).toList();
                if (defaults.isEmpty() && !grupo.opciones().isEmpty()) defaults = List.of(grupo.opciones().get(0));
                for (OpcionMenu opcion : defaults) seleccionadas.add(new Seleccion(numero++, opcion));
            } else if (!listas.isEmpty()) {
                for (int i=0; i<listas.size(); i++) {
                    OpcionMenu opcion = (OpcionMenu) listas.get(i).getSelectedItem();
                    if (opcion != null) seleccionadas.add(new Seleccion(i+1, opcion));
                }
            } else {
                int numero = 1;
                for (AbstractButton boton : botones) if (boton.isSelected())
                    seleccionadas.add(new Seleccion(numero++, (OpcionMenu) boton.getClientProperty("opcion")));
            }
            return seleccionadas;
        }

        private void reconstruirPersonalizaciones() {
            personalizaciones.removeAll();
            if (grupo.permitePersonalizar()) {
                for (Seleccion seleccion : seleccionadas()) {
                    int indiceComponente = 0;
                    for (ComponenteMenu componente : seleccion.opcion.componentes()) {
                        for (int unidad=1; unidad<=componente.cantidad(); unidad++) {
                            String clave = seleccion.numero + ":" + seleccion.opcion.idOpcion()
                                    + ":" + indiceComponente + ":" + unidad;
                            PersonalizacionProducto panel = panelesProducto.get(clave);
                            if (panel == null) {
                                panel = new PersonalizacionProducto(componente, unidad, componente.cantidad());
                                panelesProducto.put(clave, panel);
                            }
                            if (panel.tieneControles()) {
                                personalizaciones.add(Box.createVerticalStrut(8));
                                personalizaciones.add(panel);
                            }
                        }
                        indiceComponente++;
                    }
                }
            }
            personalizaciones.revalidate(); personalizaciones.repaint();
        }

        private boolean tienePersonalizaciones() {
            return personalizaciones.getComponentCount() > 0;
        }

        BigDecimal precioExtra() {
            BigDecimal total = BigDecimal.ZERO;
            for (Seleccion seleccion : seleccionadas()) {
                total = total.add(seleccion.opcion.incrementoPrecio());
                int indice = 0;
                for (ComponenteMenu componente : seleccion.opcion.componentes()) {
                    for (int unidad=1; unidad<=componente.cantidad(); unidad++) {
                        PersonalizacionProducto panel = panelesProducto.get(seleccion.numero + ":"
                                + seleccion.opcion.idOpcion() + ":" + indice + ":" + unidad);
                        if (panel != null) total = total.add(panel.precioExtra());
                    }
                    indice++;
                }
            }
            return total;
        }

        List<OpcionPedido> crearOpciones() {
            List<Seleccion> elegidas = seleccionadas();
            if (elegidas.size() < grupo.minimo() || elegidas.size() > grupo.maximo())
                throw new IllegalArgumentException("En “" + grupo.nombre() + "” debes elegir de "
                        + grupo.minimo() + " a " + grupo.maximo() + " opciones.");
            if (!grupo.permiteRepetir()) {
                long diferentes = elegidas.stream().map(s -> s.opcion.idOpcion()).distinct().count();
                if (diferentes != elegidas.size())
                    throw new IllegalArgumentException("No puedes repetir una opción en “" + grupo.nombre() + "”.");
            }
            List<OpcionPedido> resultado = new ArrayList<>();
            for (Seleccion seleccion : elegidas) {
                List<ProductoPedido> productos = new ArrayList<>();
                int indice = 0;
                for (ComponenteMenu componente : seleccion.opcion.componentes()) {
                    for (int unidad=1; unidad<=componente.cantidad(); unidad++) {
                        String clave = seleccion.numero + ":" + seleccion.opcion.idOpcion()
                                + ":" + indice + ":" + unidad;
                        PersonalizacionProducto panel = panelesProducto.get(clave);
                        if (panel == null) {
                            panel = new PersonalizacionProducto(componente, unidad, componente.cantidad());
                            panelesProducto.put(clave, panel);
                        }
                        productos.add(new ProductoPedido(UUID.randomUUID().toString(),
                                componente.idProducto(), componente.nombre(), 1,
                                componente.tipoStock(), panel.modificaciones()));
                    }
                    indice++;
                }
                BigDecimal precioModificaciones = productos.stream()
                        .flatMap(p -> p.modificaciones().stream())
                        .map(ModificacionPedido::precioExtra)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                resultado.add(new OpcionPedido(grupo.idGrupo(), seleccion.opcion.idOpcion(),
                        seleccion.numero, grupo.visible() ? grupo.nombre() : "__Producto principal",
                        seleccion.opcion.nombre(), seleccion.opcion.incrementoPrecio(), productos));
            }
            return resultado;
        }
    }

    private final class PersonalizacionProducto extends JPanel {
        private final ComponenteMenu componente;
        private final Map<IngredienteProducto,JCheckBox> quitar = new LinkedHashMap<>();
        private final Map<IngredienteProducto,JSpinner> extras = new LinkedHashMap<>();

        PersonalizacionProducto(ComponenteMenu componente, int unidad, int total) {
            this.componente = componente;
            setOpaque(true);
            setBackground(new Color(249, 250, 252));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(235,237,241),1,true),
                    new EmptyBorder(10,12,10,12)));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JLabel titulo = new JLabel(componente.nombre()
                    + (total > 1 ? " " + unidad : "") + " — personalización");
            titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
            titulo.setForeground(AZUL);
            add(titulo);
            for (IngredienteProducto ingrediente : componente.ingredientes()) {
                if (!ingrediente.permiteQuitar() && !ingrediente.permiteExtra()) continue;
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
                fila.setOpaque(false);
                JCheckBox sinControl = null;
                JSpinner extraControl = null;
                if (ingrediente.permiteQuitar()) {
                    JCheckBox sin = new JCheckBox("Sin " + ingrediente.nombre());
                    sin.setOpaque(false); quitar.put(ingrediente, sin); fila.add(sin);
                    sinControl = sin;
                }
                if (ingrediente.permiteExtra()) {
                    fila.add(new JLabel("Extra " + ingrediente.nombre()));
                    JSpinner veces = new JSpinner(new SpinnerNumberModel(0,0,ingrediente.maxExtras(),1));
                    veces.setPreferredSize(new Dimension(62,28));
                    veces.addChangeListener(e -> actualizarPrecio());
                    extras.put(ingrediente, veces); fila.add(veces);
                    extraControl = veces;
                    if (ingrediente.precioExtra().signum() > 0)
                        fila.add(new JLabel("+ Q" + ingrediente.precioExtra().setScale(2).toPlainString() + " c/u"));
                }
                if (sinControl != null && extraControl != null) {
                    JCheckBox sin = sinControl;
                    JSpinner veces = extraControl;
                    sin.addActionListener(e -> {
                        if (sin.isSelected()) veces.setValue(0);
                        veces.setEnabled(!sin.isSelected());
                    });
                }
                add(fila);
            }
        }

        boolean tieneControles() { return !quitar.isEmpty() || !extras.isEmpty(); }

        BigDecimal precioExtra() {
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<IngredienteProducto,JSpinner> entrada : extras.entrySet())
                total = total.add(entrada.getKey().precioExtra()
                        .multiply(BigDecimal.valueOf(((Number) entrada.getValue().getValue()).intValue())));
            return total;
        }

        List<ModificacionPedido> modificaciones() {
            List<ModificacionPedido> resultado = new ArrayList<>();
            for (Map.Entry<IngredienteProducto,JCheckBox> entrada : quitar.entrySet()) {
                IngredienteProducto i = entrada.getKey();
                if (entrada.getValue().isSelected()) resultado.add(new ModificacionPedido(
                        i.idProductoIngrediente(), i.idIngrediente(), "SIN", i.nombre(),
                        i.cantidadDefault(), 1, BigDecimal.ZERO));
            }
            for (Map.Entry<IngredienteProducto,JSpinner> entrada : extras.entrySet()) {
                IngredienteProducto i = entrada.getKey();
                int veces = ((Number) entrada.getValue().getValue()).intValue();
                if (veces > 0) resultado.add(new ModificacionPedido(
                        i.idProductoIngrediente(), i.idIngrediente(), "EXTRA", i.nombre(),
                        i.cantidadExtra().multiply(BigDecimal.valueOf(veces)), veces,
                        i.precioExtra().multiply(BigDecimal.valueOf(veces))));
            }
            return resultado;
        }
    }

    private record Seleccion(int numero, OpcionMenu opcion) {}
}
