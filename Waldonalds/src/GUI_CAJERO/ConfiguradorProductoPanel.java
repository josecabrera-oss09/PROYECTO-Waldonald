package GUI_CAJERO;

import Componentes.BotonRedondeado;
import Modelos.ComponenteMenu;
import Modelos.ConfiguracionProducto;
import Modelos.GrupoMenu;
import Modelos.IngredienteProducto;
import Modelos.LineaPedido;
import Modelos.ModificacionPedido;
import Modelos.OpcionMenu;
import Modelos.OpcionPedido;
import Modelos.PresentacionMenu;
import Modelos.ProductoPedido;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Configurador incrustable para el panel derecho del cajero. Las
 * presentaciones, grupos y opciones siguen viniendo completamente de la base
 * de datos; este componente solo controla su aspecto y construye la línea que
 * se agregará al pedido.
 */
public final class ConfiguradorProductoPanel extends JPanel {

    private static final Color AZUL = new Color(1, 20, 36);
    private static final Color AMARILLO = new Color(255, 188, 13);
    private static final Color AMARILLO_SUAVE = new Color(255, 248, 225);
    private static final Color TEXTO = new Color(27, 30, 34);
    private static final Color TEXTO_SECUNDARIO = new Color(112, 116, 123);
    private static final Color BORDE = new Color(229, 231, 235);
    private static final Color FONDO_CONTROL = new Color(248, 249, 251);
    private static final Color ERROR = new Color(196, 30, 42);
    private static final String FUENTE = "Arial";

    private final ConfiguracionProducto producto;
    private final Consumer<LineaPedido> alAgregar;
    private final Runnable alCancelar;
    private final CardLayout tarjetasPresentacion = new CardLayout();
    private final JPanel contenidoPresentacion = new JPanel(tarjetasPresentacion);
    private final JPanel barraPresentaciones = new JPanel();
    private final Map<Integer, VistaPresentacion> vistas = new LinkedHashMap<>();
    private final Map<Integer, BotonPresentacion> botonesPresentacion = new LinkedHashMap<>();
    private final JLabel cantidadTexto = new JLabel("1", SwingConstants.CENTER);
    private final JLabel error = new JLabel(" ");
    private final BotonRedondeado agregar = new BotonRedondeado();
    private PresentacionMenu presentacionActual;
    private int cantidad = 1;

    public ConfiguradorProductoPanel(ConfiguracionProducto producto,
            Consumer<LineaPedido> alAgregar, Runnable alCancelar) {
        super(new BorderLayout(0, 12));
        if (producto == null || producto.presentaciones().isEmpty()) {
            throw new IllegalArgumentException(
                    "El producto no tiene una presentación activa para venderse.");
        }
        this.producto = producto;
        this.alAgregar = alAgregar;
        this.alCancelar = alCancelar;
        setOpaque(false);
        setBorder(new EmptyBorder(18, 16, 18, 16));
        construirInterfaz();
    }

    private void construirInterfaz() {
        add(crearCabecera(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setOpaque(false);
        centro.add(crearBarraPresentaciones(), BorderLayout.NORTH);
        contenidoPresentacion.setOpaque(false);
        centro.add(contenidoPresentacion, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        add(crearPie(), BorderLayout.SOUTH);
        SwingUtilities.invokeLater(this::actualizarPrecio);
    }

    private JComponent crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(8, 8));
        cabecera.setOpaque(false);

        JButton volver = new JButton("‹  Volver al pedido");
        volver.setFont(new Font(FUENTE, Font.BOLD, 12));
        volver.setForeground(TEXTO_SECUNDARIO);
        volver.setOpaque(false);
        volver.setContentAreaFilled(false);
        volver.setBorderPainted(false);
        volver.setFocusPainted(false);
        volver.setHorizontalAlignment(SwingConstants.LEFT);
        volver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        volver.setMargin(new Insets(0, 0, 0, 0));
        volver.addActionListener(evento -> alCancelar.run());
        cabecera.add(volver, BorderLayout.NORTH);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel(producto.nombre());
        titulo.setForeground(AZUL);
        titulo.setFont(new Font(FUENTE, Font.BOLD, 21));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ayuda = new JLabel("<html>" + escaparHtml(
                producto.descripcion() == null || producto.descripcion().isBlank()
                        ? "Personaliza el producto antes de añadirlo."
                        : producto.descripcion()) + "</html>");
        ayuda.setForeground(TEXTO_SECUNDARIO);
        ayuda.setFont(new Font(FUENTE, Font.PLAIN, 12));
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(ayuda);
        cabecera.add(textos, BorderLayout.CENTER);
        return cabecera;
    }

    private JComponent crearBarraPresentaciones() {
        List<PresentacionMenu> ordenadas = new ArrayList<>(producto.presentaciones());
        ordenadas.sort(Comparator.comparingInt(this::ordenPresentacion)
                .thenComparingInt(PresentacionMenu::idPresentacion));

        barraPresentaciones.setOpaque(false);
        barraPresentaciones.setLayout(new GridLayout(1, ordenadas.size(), 0, 0));
        barraPresentaciones.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        barraPresentaciones.setPreferredSize(new Dimension(0, 50));

        PresentacionMenu predeterminada = ordenadas.stream()
                .filter(PresentacionMenu::predeterminada)
                .findFirst().orElse(ordenadas.get(0));

        for (PresentacionMenu presentacion : ordenadas) {
            VistaPresentacion vista = new VistaPresentacion(presentacion);
            vistas.put(presentacion.idPresentacion(), vista);
            contenidoPresentacion.add(vista, clave(presentacion));

            BotonPresentacion boton = new BotonPresentacion(
                    tituloPresentacion(presentacion), presentacion);
            botonesPresentacion.put(presentacion.idPresentacion(), boton);
            barraPresentaciones.add(boton);
        }
        seleccionarPresentacion(predeterminada);
        return barraPresentaciones;
    }

    private int ordenPresentacion(PresentacionMenu presentacion) {
        return switch (presentacion.tipo().toUpperCase()) {
            case "MENU" -> 0;
            case "INDIVIDUAL" -> 1;
            case "INFANTIL" -> 2;
            case "COMBO" -> 3;
            default -> 4;
        };
    }

    private String tituloPresentacion(PresentacionMenu presentacion) {
        if ("MENU".equalsIgnoreCase(presentacion.tipo())) {
            return "WlMenú";
        }
        if ("INDIVIDUAL".equalsIgnoreCase(presentacion.tipo())) {
            return "Individual";
        }
        return presentacion.nombre();
    }

    private String clave(PresentacionMenu presentacion) {
        return "PRESENTACION_" + presentacion.idPresentacion();
    }

    private void seleccionarPresentacion(PresentacionMenu presentacion) {
        presentacionActual = presentacion;
        tarjetasPresentacion.show(contenidoPresentacion, clave(presentacion));
        for (BotonPresentacion boton : botonesPresentacion.values()) {
            boton.setSeleccionado(boton.presentacion.idPresentacion()
                    == presentacion.idPresentacion());
        }
        error.setText(" ");
        actualizarPrecio();
    }

    private JComponent crearPie() {
        JPanel pie = new JPanel(new BorderLayout(0, 7));
        pie.setOpaque(false);

        error.setForeground(ERROR);
        error.setFont(new Font(FUENTE, Font.BOLD, 11));
        pie.add(error, BorderLayout.NORTH);

        JPanel acciones = new JPanel(new BorderLayout(12, 0));
        acciones.setOpaque(false);

        JPanel selectorCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        selectorCantidad.setOpaque(false);
        JButton menos = botonCantidad("−");
        JButton mas = botonCantidad("+");
        cantidadTexto.setFont(new Font(FUENTE, Font.BOLD, 16));
        cantidadTexto.setForeground(TEXTO);
        cantidadTexto.setPreferredSize(new Dimension(22, 42));
        menos.addActionListener(evento -> cambiarCantidad(-1));
        mas.addActionListener(evento -> cambiarCantidad(1));
        selectorCantidad.add(menos);
        selectorCantidad.add(cantidadTexto);
        selectorCantidad.add(mas);
        acciones.add(selectorCantidad, BorderLayout.WEST);

        agregar.setFont(new Font(FUENTE, Font.BOLD, 14));
        agregar.setForeground(AZUL);
        agregar.setDegradado(false);
        agregar.setColorInicio(AMARILLO);
        agregar.setColorFinal(AMARILLO);
        agregar.setColorBorde(AMARILLO);
        agregar.setGrosorBorde(0f);
        agregar.setRadio(17);
        agregar.setPreferredSize(new Dimension(250, 48));
        agregar.addActionListener(evento -> confirmar());
        acciones.add(agregar, BorderLayout.CENTER);
        pie.add(acciones, BorderLayout.CENTER);
        return pie;
    }

    private JButton botonCantidad(String texto) {
        return new BotonPaso(texto, AMARILLO_SUAVE,
                new Color(244, 223, 157), AZUL, 42, 38, 12, 22);
    }

    private void cambiarCantidad(int cambio) {
        cantidad = Math.max(1, Math.min(99, cantidad + cambio));
        cantidadTexto.setText(String.valueOf(cantidad));
        actualizarPrecio();
    }

    private VistaPresentacion vistaActual() {
        return presentacionActual == null
                ? null : vistas.get(presentacionActual.idPresentacion());
    }

    private void actualizarPrecio() {
        VistaPresentacion vista = vistaActual();
        if (vista == null || agregar == null) {
            return;
        }
        BigDecimal total = vista.precioActual()
                .multiply(BigDecimal.valueOf(cantidad)).setScale(2);
        agregar.setText("Añadir al carrito     Q" + total.toPlainString());
    }

    private void confirmar() {
        try {
            VistaPresentacion vista = vistaActual();
            if (vista == null) {
                throw new IllegalArgumentException("Selecciona una presentación.");
            }
            List<OpcionPedido> opciones = vista.crearOpciones();
            LineaPedido linea = new LineaPedido(UUID.randomUUID().toString(),
                    producto.idProducto(), vista.presentacion.idPresentacion(),
                    producto.nombre(), vista.presentacion.nombre(),
                    vista.presentacion.precio(), opciones, cantidad);
            error.setText(" ");
            alAgregar.accept(linea);
        } catch (IllegalArgumentException excepcion) {
            error.setText("<html>" + escaparHtml(excepcion.getMessage()) + "</html>");
        }
    }

    private static String escaparHtml(String texto) {
        return texto == null ? "" : texto.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;");
    }

    private final class BotonPresentacion extends JButton {
        private final PresentacionMenu presentacion;
        private boolean seleccionado;

        BotonPresentacion(String texto, PresentacionMenu presentacion) {
            super(texto);
            this.presentacion = presentacion;
            setFont(new Font(FUENTE, Font.BOLD, 16));
            setForeground(TEXTO_SECUNDARIO);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addActionListener(evento -> seleccionarPresentacion(presentacion));
        }

        void setSeleccionado(boolean valor) {
            seleccionado = valor;
            setForeground(valor ? TEXTO : new Color(165, 166, 169));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (!seleccionado) {
                return;
            }
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AMARILLO);
            g2.fillRoundRect(0, getHeight() - 4, getWidth(), 4, 4, 4);
            g2.dispose();
        }
    }

    private final class VistaPresentacion extends JPanel {
        private final PresentacionMenu presentacion;
        private final List<SelectorGrupo> selectores = new ArrayList<>();

        VistaPresentacion(PresentacionMenu presentacion) {
            super(new BorderLayout());
            this.presentacion = presentacion;
            setOpaque(false);

            JPanel contenido = new JPanel();
            contenido.setBackground(Color.WHITE);
            contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
            contenido.setBorder(new EmptyBorder(16, 8, 18, 8));

            for (GrupoMenu grupo : presentacion.grupos()) {
                SelectorGrupo selector = new SelectorGrupo(grupo);
                selectores.add(selector);
                if (grupo.visible() || selector.tienePersonalizaciones()) {
                    selector.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contenido.add(selector);
                    contenido.add(Box.createVerticalStrut(18));
                }
            }
            contenido.add(Box.createVerticalGlue());

            JScrollPane scroll = new JScrollPane(contenido);
            Componentes.DesplazamientoSuave.ocultarBarras(scroll);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);
            scroll.getViewport().setBackground(Color.WHITE);
            scroll.getVerticalScrollBar().setUnitIncrement(22);
            add(scroll, BorderLayout.CENTER);
        }

        BigDecimal precioActual() {
            BigDecimal total = presentacion.precio();
            for (SelectorGrupo selector : selectores) {
                total = total.add(selector.precioExtra());
            }
            return total;
        }

        List<OpcionPedido> crearOpciones() {
            List<OpcionPedido> resultado = new ArrayList<>();
            for (SelectorGrupo selector : selectores) {
                resultado.addAll(selector.crearOpciones());
            }
            return resultado;
        }
    }

    private final class SelectorGrupo extends JPanel {
        private final GrupoMenu grupo;
        private final JPanel opciones = new JPanel();
        private final JPanel personalizaciones = new JPanel();
        private final List<AbstractButton> botones = new ArrayList<>();
        private final List<JComboBox<OpcionMenu>> listas = new ArrayList<>();
        private final Map<String, PersonalizacionProducto> panelesProducto
                = new LinkedHashMap<>();

        SelectorGrupo(GrupoMenu grupo) {
            super(new BorderLayout(0, 10));
            this.grupo = grupo;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());

            if (grupo.visible()) {
                add(crearEncabezadoGrupo(), BorderLayout.NORTH);
            }
            opciones.setOpaque(false);
            opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));
            crearControles();

            personalizaciones.setOpaque(false);
            personalizaciones.setLayout(new BoxLayout(personalizaciones, BoxLayout.Y_AXIS));
            JPanel centro = new JPanel();
            centro.setOpaque(false);
            centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
            centro.add(opciones);
            centro.add(personalizaciones);
            add(centro, BorderLayout.CENTER);
            reconstruirPersonalizaciones();
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension preferido = getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, preferido.height);
        }

        private JComponent crearEncabezadoGrupo() {
            JPanel encabezado = new JPanel();
            encabezado.setOpaque(false);
            encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
            JLabel titulo = new JLabel(grupo.nombre().startsWith("__")
                    ? "" : grupo.nombre());
            titulo.setFont(new Font(FUENTE, Font.BOLD, 14));
            titulo.setForeground(TEXTO);
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel detalle = new JLabel(textoRegla(grupo));
            detalle.setForeground(TEXTO_SECUNDARIO);
            detalle.setFont(new Font(FUENTE, Font.PLAIN, 11));
            detalle.setAlignmentX(Component.LEFT_ALIGNMENT);
            encabezado.add(titulo);
            encabezado.add(Box.createVerticalStrut(3));
            encabezado.add(detalle);
            return encabezado;
        }

        private String textoRegla(GrupoMenu grupo) {
            if (grupo.minimo() == grupo.maximo()) {
                return grupo.minimo() + (grupo.minimo() == 1
                        ? " requerido" : " requeridos");
            }
            if (grupo.minimo() == 0) {
                return "Selecciona hasta " + grupo.maximo()
                        + (grupo.maximo() == 1 ? " opción" : " opciones");
            }
            return "Selecciona de " + grupo.minimo() + " a " + grupo.maximo();
        }

        private void crearControles() {
            if (!grupo.visible()) {
                return;
            }
            if (grupo.permiteRepetir() && grupo.maximo() > 1) {
                crearListasRepetibles();
                return;
            }

            ButtonGroup radios = grupo.maximo() == 1 ? new ButtonGroup() : null;
            if (radios != null && grupo.minimo() == 0) {
                JRadioButton ninguna = radio("No agregar");
                ninguna.setSelected(true);
                ninguna.addActionListener(evento -> cambioSeleccion());
                radios.add(ninguna);
                opciones.add(ninguna);
                opciones.add(Box.createVerticalStrut(7));
            }

            for (OpcionMenu opcion : grupo.opciones()) {
                AbstractButton control = radios == null
                        ? casilla(opcion.toString(), false) : radio(opcion.toString());
                control.putClientProperty("opcion", opcion);
                control.setSelected(opcion.predeterminada());
                control.addActionListener(evento -> cambioSeleccion());
                if (radios != null) {
                    radios.add(control);
                }
                botones.add(control);
                opciones.add(control);
                opciones.add(Box.createVerticalStrut(7));
            }

            if (grupo.minimo() > 0
                    && botones.stream().noneMatch(AbstractButton::isSelected)
                    && !botones.isEmpty()) {
                botones.get(0).setSelected(true);
            }
        }

        private void crearListasRepetibles() {
            for (int indice = 0; indice < grupo.maximo(); indice++) {
                JPanel fila = new JPanel(new BorderLayout(10, 0));
                fila.setOpaque(false);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
                JLabel etiqueta = new JLabel("Producto " + (indice + 1));
                etiqueta.setFont(new Font(FUENTE, Font.BOLD, 12));
                etiqueta.setForeground(TEXTO);
                etiqueta.setPreferredSize(new Dimension(82, 38));

                JComboBox<OpcionMenu> lista = new JComboBox<>();
                lista.setFont(new Font(FUENTE, Font.PLAIN, 13));
                lista.setBackground(Color.WHITE);
                lista.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
                if (indice >= grupo.minimo()) {
                    lista.addItem(null);
                }
                for (OpcionMenu opcion : grupo.opciones()) {
                    lista.addItem(opcion);
                }
                if (indice < grupo.minimo()) {
                    seleccionarPredeterminada(lista);
                } else {
                    lista.setSelectedIndex(0);
                }
                lista.addActionListener(evento -> cambioSeleccion());
                listas.add(lista);
                fila.add(etiqueta, BorderLayout.WEST);
                fila.add(lista, BorderLayout.CENTER);
                opciones.add(fila);
                opciones.add(Box.createVerticalStrut(8));
            }
        }

        private void seleccionarPredeterminada(JComboBox<OpcionMenu> lista) {
            for (int indice = 0; indice < lista.getItemCount(); indice++) {
                OpcionMenu opcion = lista.getItemAt(indice);
                if (opcion != null && opcion.predeterminada()) {
                    lista.setSelectedIndex(indice);
                    return;
                }
            }
            if (lista.getItemCount() > 0 && grupo.minimo() > 0) {
                lista.setSelectedIndex(0);
            }
        }

        private void cambioSeleccion() {
            reconstruirPersonalizaciones();
            error.setText(" ");
            actualizarPrecio();
        }

        private List<Seleccion> seleccionadas() {
            List<Seleccion> resultado = new ArrayList<>();
            if (!grupo.visible()) {
                List<OpcionMenu> predeterminadas = grupo.opciones().stream()
                        .filter(OpcionMenu::predeterminada).toList();
                if (predeterminadas.isEmpty() && !grupo.opciones().isEmpty()) {
                    predeterminadas = List.of(grupo.opciones().get(0));
                }
                int numero = 1;
                for (OpcionMenu opcion : predeterminadas) {
                    resultado.add(new Seleccion(numero++, opcion));
                }
            } else if (!listas.isEmpty()) {
                for (int indice = 0; indice < listas.size(); indice++) {
                    OpcionMenu opcion = (OpcionMenu) listas.get(indice).getSelectedItem();
                    if (opcion != null) {
                        resultado.add(new Seleccion(indice + 1, opcion));
                    }
                }
            } else {
                int numero = 1;
                for (AbstractButton boton : botones) {
                    if (boton.isSelected()) {
                        resultado.add(new Seleccion(numero++,
                                (OpcionMenu) boton.getClientProperty("opcion")));
                    }
                }
            }
            return resultado;
        }

        private void reconstruirPersonalizaciones() {
            personalizaciones.removeAll();
            if (grupo.permitePersonalizar()) {
                for (Seleccion seleccion : seleccionadas()) {
                    int indiceComponente = 0;
                    for (ComponenteMenu componente : seleccion.opcion.componentes()) {
                        for (int unidad = 1; unidad <= componente.cantidad(); unidad++) {
                            String llave = llave(seleccion, indiceComponente, unidad);
                            PersonalizacionProducto panel = panelesProducto.get(llave);
                            if (panel == null) {
                                panel = new PersonalizacionProducto(componente, unidad,
                                        componente.cantidad());
                                panelesProducto.put(llave, panel);
                            }
                            if (panel.tieneControles()) {
                                personalizaciones.add(Box.createVerticalStrut(12));
                                personalizaciones.add(panel);
                            }
                        }
                        indiceComponente++;
                    }
                }
            }
            personalizaciones.revalidate();
            personalizaciones.repaint();
        }

        private String llave(Seleccion seleccion, int indiceComponente, int unidad) {
            return seleccion.numero + ":" + seleccion.opcion.idOpcion()
                    + ":" + indiceComponente + ":" + unidad;
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
                    for (int unidad = 1; unidad <= componente.cantidad(); unidad++) {
                        PersonalizacionProducto panel = panelesProducto.get(
                                llave(seleccion, indice, unidad));
                        if (panel != null) {
                            total = total.add(panel.precioExtra());
                        }
                    }
                    indice++;
                }
            }
            return total;
        }

        List<OpcionPedido> crearOpciones() {
            List<Seleccion> elegidas = seleccionadas();
            if (elegidas.size() < grupo.minimo()
                    || elegidas.size() > grupo.maximo()) {
                throw new IllegalArgumentException("En “" + grupo.nombre()
                        + "” debes elegir de " + grupo.minimo() + " a "
                        + grupo.maximo() + " opciones.");
            }
            if (!grupo.permiteRepetir()) {
                long diferentes = elegidas.stream()
                        .map(seleccion -> seleccion.opcion.idOpcion())
                        .distinct().count();
                if (diferentes != elegidas.size()) {
                    throw new IllegalArgumentException(
                            "No puedes repetir una opción en “" + grupo.nombre() + "”.");
                }
            }

            List<OpcionPedido> resultado = new ArrayList<>();
            for (Seleccion seleccion : elegidas) {
                List<ProductoPedido> productos = new ArrayList<>();
                int indice = 0;
                for (ComponenteMenu componente : seleccion.opcion.componentes()) {
                    for (int unidad = 1; unidad <= componente.cantidad(); unidad++) {
                        String llave = llave(seleccion, indice, unidad);
                        PersonalizacionProducto panel = panelesProducto.get(llave);
                        if (panel == null) {
                            panel = new PersonalizacionProducto(componente, unidad,
                                    componente.cantidad());
                            panelesProducto.put(llave, panel);
                        }
                        productos.add(new ProductoPedido(UUID.randomUUID().toString(),
                                componente.idProducto(), componente.nombre(), 1,
                                componente.tipoStock(), panel.modificaciones()));
                    }
                    indice++;
                }
                resultado.add(new OpcionPedido(grupo.idGrupo(),
                        seleccion.opcion.idOpcion(), seleccion.numero,
                        grupo.visible() ? grupo.nombre() : "__Producto principal",
                        seleccion.opcion.nombre(), seleccion.opcion.incrementoPrecio(),
                        productos));
            }
            return resultado;
        }
    }

    private final class PersonalizacionProducto extends JPanel {
        private final ComponenteMenu componente;
        private final Map<IngredienteProducto, JCheckBox> incluidos
                = new LinkedHashMap<>();
        private final Map<IngredienteProducto, SelectorExtra> extras
                = new LinkedHashMap<>();

        PersonalizacionProducto(ComponenteMenu componente, int unidad, int total) {
            this.componente = componente;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            if (total > 1) {
                JLabel productoNumero = new JLabel(componente.nombre() + " " + unidad);
                productoNumero.setFont(new Font(FUENTE, Font.BOLD, 14));
                productoNumero.setForeground(AZUL);
                productoNumero.setAlignmentX(Component.LEFT_ALIGNMENT);
                add(productoNumero);
                add(Box.createVerticalStrut(10));
            }

            List<IngredienteProducto> removibles = componente.ingredientes().stream()
                    .filter(IngredienteProducto::permiteQuitar).toList();
            if (!removibles.isEmpty()) {
                add(tituloSeccion("¿Deseas eliminar algún ingrediente?"));
                add(descripcionSeccion("Desmarca lo que deseas eliminar"));
                add(Box.createVerticalStrut(6));
                for (IngredienteProducto ingrediente : removibles) {
                    JCheckBox incluido = casilla(ingrediente.nombre(), true);
                    incluido.setAlignmentX(Component.LEFT_ALIGNMENT);
                    incluidos.put(ingrediente, incluido);
                    add(incluido);
                    add(Box.createVerticalStrut(5));
                }
            }

            List<IngredienteProducto> agregables = componente.ingredientes().stream()
                    .filter(IngredienteProducto::permiteExtra).toList();
            if (!agregables.isEmpty()) {
                if (!removibles.isEmpty()) {
                    add(Box.createVerticalStrut(13));
                }
                add(tituloSeccion("Elige tu complemento"));
                int maximo = agregables.stream()
                        .mapToInt(IngredienteProducto::maxExtras).max().orElse(1);
                add(descripcionSeccion("Selecciona hasta " + maximo
                        + (maximo == 1 ? " opción" : " opciones por ingrediente")));
                add(Box.createVerticalStrut(6));
                for (IngredienteProducto ingrediente : agregables) {
                    SelectorExtra selector = new SelectorExtra(ingrediente);
                    selector.setAlignmentX(Component.LEFT_ALIGNMENT);
                    extras.put(ingrediente, selector);
                    JCheckBox incluido = incluidos.get(ingrediente);
                    if (incluido != null) {
                        incluido.addActionListener(evento -> {
                            if (!incluido.isSelected()) {
                                selector.reiniciar();
                            }
                            selector.setControlesHabilitados(incluido.isSelected());
                            actualizarPrecio();
                        });
                    }
                    add(selector);
                    add(Box.createVerticalStrut(7));
                }
            }
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension preferido = getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, preferido.height);
        }

        private JLabel tituloSeccion(String texto) {
            JLabel etiqueta = new JLabel(texto);
            etiqueta.setFont(new Font(FUENTE, Font.BOLD, 13));
            etiqueta.setForeground(TEXTO);
            etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
            return etiqueta;
        }

        private JLabel descripcionSeccion(String texto) {
            JLabel etiqueta = new JLabel(texto);
            etiqueta.setFont(new Font(FUENTE, Font.PLAIN, 11));
            etiqueta.setForeground(TEXTO_SECUNDARIO);
            etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
            return etiqueta;
        }

        boolean tieneControles() {
            return !incluidos.isEmpty() || !extras.isEmpty();
        }

        BigDecimal precioExtra() {
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<IngredienteProducto, SelectorExtra> entrada
                    : extras.entrySet()) {
                total = total.add(entrada.getKey().precioExtra()
                        .multiply(BigDecimal.valueOf(entrada.getValue().cantidad)));
            }
            return total;
        }

        List<ModificacionPedido> modificaciones() {
            List<ModificacionPedido> resultado = new ArrayList<>();
            for (Map.Entry<IngredienteProducto, JCheckBox> entrada
                    : incluidos.entrySet()) {
                IngredienteProducto ingrediente = entrada.getKey();
                if (!entrada.getValue().isSelected()) {
                    resultado.add(new ModificacionPedido(
                            ingrediente.idProductoIngrediente(),
                            ingrediente.idIngrediente(), "SIN",
                            ingrediente.nombre(), ingrediente.cantidadDefault(),
                            1, BigDecimal.ZERO));
                }
            }
            for (Map.Entry<IngredienteProducto, SelectorExtra> entrada
                    : extras.entrySet()) {
                IngredienteProducto ingrediente = entrada.getKey();
                int veces = entrada.getValue().cantidad;
                if (veces > 0) {
                    resultado.add(new ModificacionPedido(
                            ingrediente.idProductoIngrediente(),
                            ingrediente.idIngrediente(), "EXTRA",
                            ingrediente.nombre(), ingrediente.cantidadExtra()
                                    .multiply(BigDecimal.valueOf(veces)), veces,
                            ingrediente.precioExtra()
                                    .multiply(BigDecimal.valueOf(veces))));
                }
            }
            return resultado;
        }
    }

    private final class SelectorExtra extends JPanel {
        private final IngredienteProducto ingrediente;
        private final JButton menos;
        private final JButton mas;
        private final JLabel numero = new JLabel("0", SwingConstants.CENTER);
        private int cantidad;

        SelectorExtra(IngredienteProducto ingrediente) {
            super(new BorderLayout(8, 0));
            this.ingrediente = ingrediente;
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

            String precioTexto = ingrediente.precioExtra().signum() > 0
                    ? "   + Q" + ingrediente.precioExtra().setScale(2).toPlainString()
                    : "";
            JLabel nombre = new JLabel("Extra " + ingrediente.nombre() + precioTexto);
            nombre.setFont(new Font(FUENTE, Font.PLAIN, 12));
            nombre.setForeground(TEXTO);
            add(nombre, BorderLayout.CENTER);

            JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            controles.setOpaque(false);
            menos = botonExtra("−");
            mas = botonExtra("+");
            numero.setFont(new Font(FUENTE, Font.BOLD, 13));
            numero.setForeground(TEXTO);
            numero.setPreferredSize(new Dimension(20, 30));
            menos.addActionListener(evento -> cambiar(-1));
            mas.addActionListener(evento -> cambiar(1));
            controles.add(menos);
            controles.add(numero);
            controles.add(mas);
            add(controles, BorderLayout.EAST);
            actualizarEstado();
        }

        private JButton botonExtra(String texto) {
            return new BotonPaso(texto, FONDO_CONTROL,
                    BORDE, AZUL, 36, 30, 10, 18);
        }

        private void cambiar(int valor) {
            cantidad = Math.max(0,
                    Math.min(ingrediente.maxExtras(), cantidad + valor));
            actualizarEstado();
            error.setText(" ");
            actualizarPrecio();
        }

        private void reiniciar() {
            cantidad = 0;
            actualizarEstado();
        }

        private void setControlesHabilitados(boolean habilitado) {
            menos.setEnabled(habilitado && cantidad > 0);
            mas.setEnabled(habilitado && cantidad < ingrediente.maxExtras());
            numero.setEnabled(habilitado);
        }

        private void actualizarEstado() {
            numero.setText(String.valueOf(cantidad));
            menos.setEnabled(cantidad > 0);
            mas.setEnabled(cantidad < ingrediente.maxExtras());
        }
    }

    private JCheckBox casilla(String texto, boolean seleccionada) {
        JCheckBox control = new JCheckBox(texto, seleccionada);
        prepararControl(control);
        control.setIcon(new IconoSeleccion(false, false));
        control.setSelectedIcon(new IconoSeleccion(false, true));
        return control;
    }

    private JRadioButton radio(String texto) {
        JRadioButton control = new JRadioButton(texto);
        prepararControl(control);
        control.setIcon(new IconoSeleccion(true, false));
        control.setSelectedIcon(new IconoSeleccion(true, true));
        return control;
    }

    private void prepararControl(AbstractButton control) {
        control.setOpaque(false);
        control.setFont(new Font(FUENTE, Font.PLAIN, 13));
        control.setForeground(TEXTO);
        control.setFocusPainted(false);
        control.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        control.setIconTextGap(10);
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static final class IconoSeleccion implements Icon {
        private final boolean circular;
        private final boolean seleccionado;

        IconoSeleccion(boolean circular, boolean seleccionado) {
            this.circular = circular;
            this.seleccionado = seleccionado;
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(Component componente, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(seleccionado ? AMARILLO : new Color(145, 149, 155));
            if (circular) {
                if (seleccionado) {
                    g2.fillOval(x + 1, y + 1, 16, 16);
                    g2.setColor(AZUL);
                    g2.fillOval(x + 6, y + 6, 6, 6);
                } else {
                    g2.drawOval(x + 1, y + 1, 15, 15);
                }
            } else {
                if (seleccionado) {
                    g2.fillRoundRect(x + 1, y + 1, 16, 16, 3, 3);
                    g2.setColor(AZUL);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 5, y + 9, x + 8, y + 12);
                    g2.drawLine(x + 8, y + 12, x + 14, y + 6);
                } else {
                    g2.drawRoundRect(x + 1, y + 1, 15, 15, 3, 3);
                }
            }
            g2.dispose();
        }
    }

    /**
     * Botón compacto que pinta el símbolo directamente. De esta forma ningún
     * Look & Feel puede sustituir los signos + y − por puntos suspensivos.
     */
    private static final class BotonPaso extends JButton {
        private final Color fondo;
        private final Color borde;
        private final Color simbolo;
        private final int radio;

        BotonPaso(String texto, Color fondo, Color borde, Color simbolo,
                int ancho, int alto, int radio, int tamanoFuente) {
            super(texto);
            this.fondo = fondo;
            this.borde = borde;
            this.simbolo = simbolo;
            this.radio = radio;
            setFont(new Font(FUENTE, Font.BOLD, tamanoFuente));
            setPreferredSize(new Dimension(ancho, alto));
            setMinimumSize(new Dimension(ancho, alto));
            setMaximumSize(new Dimension(ancho, alto));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0, 0, 0, 0));
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Color colorFondo = fondo;
            if (!isEnabled()) {
                colorFondo = new Color(239, 240, 242);
            } else if (getModel().isPressed()) {
                colorFondo = fondo.darker();
            } else if (getModel().isRollover()) {
                colorFondo = aclarar(fondo, 10);
            }
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, Math.max(1, getWidth() - 1),
                    Math.max(1, getHeight() - 1), radio, radio);
            g2.setColor(isEnabled() ? borde : new Color(220, 222, 226));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, Math.max(1, getWidth() - 1),
                    Math.max(1, getHeight() - 1), radio, radio);

            String texto = getText();
            g2.setFont(getFont());
            FontMetrics medidas = g2.getFontMetrics();
            int x = (getWidth() - medidas.stringWidth(texto)) / 2;
            int y = (getHeight() - medidas.getHeight()) / 2
                    + medidas.getAscent() - 1;
            g2.setColor(isEnabled() ? simbolo : new Color(150, 154, 160));
            g2.drawString(texto, x, y);
            g2.dispose();
        }

        private static Color aclarar(Color color, int cantidad) {
            return new Color(
                    Math.min(255, color.getRed() + cantidad),
                    Math.min(255, color.getGreen() + cantidad),
                    Math.min(255, color.getBlue() + cantidad),
                    color.getAlpha());
        }
    }

    private record Seleccion(int numero, OpcionMenu opcion) {
    }
}
