package GUI_ADMINISTRADOR;

import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
import Componentes.GraficaBarras;
import Componentes.PanelCircular;
import Componentes.PanelFlotante;
import DAO.ReporteDAO;
import Utilidades.IconosUsuarios;
import Utilidades.ReporteCsv;
import Utilidades.TemaAdmin;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** Panel administrativo de ventas diarias. */
public class ReportesPanel extends javax.swing.JPanel {

    private static final int VENTAS_POR_PAGINA = 8;
    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 229, 235);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final Color VERDE = new Color(34, 139, 71);
    private static final Color NARANJA = new Color(235, 79, 35);

    private final TemaAdmin tema = new TemaAdmin();
    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final ModeloTablaVentas modeloTabla = new ModeloTablaVentas();
    private final ModeloProductosVendidos modeloProductos =
            new ModeloProductosVendidos();
    private final DecimalFormat moneda = new DecimalFormat(
            "'Q'#,##0.00",
            DecimalFormatSymbols.getInstance(Locale.US));
    private final DateTimeFormatter fechaVisible =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final List<BotonRedondeado> botonesPagina = new ArrayList<>();
    private final Timer temporizadorBusqueda;

    private SwingWorker<ReporteDAO.Resumen, Void> carga;
    private ReporteDAO.Resumen ultimoReporte;
    private LocalDate fechaCargada;
    private List<Object[]> ventasFiltradas = List.of();
    private String cajeroSeleccionado;
    private String metodoSeleccionado;
    private String servicioSeleccionado;
    private int paginaActual = 1;

    public ReportesPanel() {
        initComponents();
        temporizadorBusqueda = new Timer(250, evento -> {
            paginaActual = 1;
            aplicarFiltros();
        });
        temporizadorBusqueda.setRepeats(false);
        configurarVista();
        configurarFiltros();
        configurarAnalisis();
        configurarTabla();
        configurarEventos();
        limpiarResultados();
    }

    private void configurarVista() {
        setBackground(new Color(248, 249, 251));
        labelTitulo.setFont(tema.negrita(36f));
        labelSubtitulo.setFont(tema.regular(16f));
        labelSubtitulo.setForeground(SECUNDARIO);

        botonActualizar.setFont(tema.negrita(15f));
        botonActualizar.setForeground(AZUL);
        botonActualizar.addActionListener(e -> actualizarReporte());
         configurarTarjeta(panelVentas, 
                labelTituloVentas, labelVentas, false);
        configurarTarjeta(panelPedidos, 
                labelTituloPedidos, labelPedidos, true);
        configurarTarjeta(panelTicket, 
                labelTituloTicket, labelTicket, false);
        configurarTarjeta(panelCancelados, 
                labelTituloCancelados, labelCancelados, true);
        labelCancelados.setForeground(ROJO);
        configurarBotonAccion(botonExportar, ROJO);
        botonExportar.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.EXPORTAR, ROJO, 20));
        botonExportar.setIconTextGap(10);
        botonExportar.addActionListener(e -> exportarReporte());

        configurarBotonAccion(botonImprimir, AZUL);
        botonImprimir.addActionListener(e -> imprimirReporte());
        labelCancelados.setForeground(ROJO);

        panelResumen.setColorFondo(Color.WHITE);
        panelResumen.setColorBorde(BORDE);
        for (javax.swing.JSeparator separador : new javax.swing.JSeparator[]{
            separadorResumen1, separadorResumen2,
            separadorResumen3, separadorResumen4
        }) {
            separador.setForeground(BORDE);
            separador.setBackground(BORDE);
        }
        labelResumen.setFont(tema.negrita(17f));
        labelResumenSubtitulo.setFont(tema.regular(13f));
        labelResumenSubtitulo.setForeground(SECUNDARIO);
        configurarResumen(efectivoTitulo, labelEfectivo,
                labelPorcentajeEfectivo);
        configurarResumen(tarjetaTitulo, labelTarjeta,
                labelPorcentajeTarjeta);
        configurarResumen(otrosTitulo, labelOtros,
                labelPorcentajeOtros);
        configurarResumen(cancelacionTitulo, labelCancelacionesCaja,
                cancelacionDetalle);
        labelCancelacionesCaja.setForeground(ROJO);
        estadoCarga.setFont(tema.regular(12f));
        estadoCarga.setForeground(SECUNDARIO);
    }

    private void configurarAnalisis() {
        for (PanelFlotante panel : new PanelFlotante[]{
            panelProductosVendidos, panelVentasCajero, panelVentasHora
        }) {
            panel.setColorFondo(Color.WHITE);
            panel.setColorBorde(BORDE);
            panel.setSombra(false);
            panel.setRadio(18);
        }

        for (JLabel titulo : new JLabel[]{
            labelProductosVendidos, labelVentasCajero, labelVentasHora,
            labelDetalleVentas
        }) {
            titulo.setFont(tema.negrita(17f));
            titulo.setForeground(AZUL);
        }
        for (JLabel subtitulo : new JLabel[]{
            labelProductosSubtitulo, labelCajeroSubtitulo, labelHoraSubtitulo
        }) {
            subtitulo.setFont(tema.regular(12f));
            subtitulo.setForeground(SECUNDARIO);
        }

        tablaProductos.setModel(modeloProductos);
        tablaProductos.aplicarEstilo();
        tablaProductos.setRowHeight(30);
        tablaProductos.setFilasAlternadas(true);
        tablaProductos.setAltoCabecera(34);
        tablaProductos.setColumnasCentradas("0,2,3");
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(34);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(215);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(75);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(75);
        scrollProductos.setBorder(BorderFactory.createEmptyBorder());
        scrollProductos.getViewport().setBackground(Color.WHITE);

        graficaCajeros.setOrientacion(
                GraficaBarras.Orientacion.HORIZONTAL);
        graficaCajeros.setColorBarra(AMARILLO);
        graficaHoras.setOrientacion(
                GraficaBarras.Orientacion.VERTICAL);
        graficaHoras.setColorBarra(AMARILLO);
    }

    private void configurarTarjeta(PanelFlotante panel,
             JLabel titulo, JLabel valor, boolean roja) {
        panel.setColorFondo(Color.WHITE);
        panel.setColorBorde(BORDE);
        panel.setRadio(18);
        panel.setSombra(true);
        panel.setTamanoSombra(9);
        titulo.setFont(tema.media(14f));
        titulo.setForeground(SECUNDARIO);
        valor.setFont(tema.negrita(28f));
    }

    private void configurarResumen(
            JLabel titulo, JLabel valor, JLabel detalle) {
        titulo.setFont(tema.regular(13f));
        titulo.setForeground(SECUNDARIO);
        valor.setFont(tema.negrita(21f));
        valor.setForeground(AZUL);
        detalle.setFont(tema.regular(12f));
        detalle.setForeground(SECUNDARIO);
    }

    private void configurarBotonAccion(
            BotonRedondeado boton, Color color) {
        boton.setFont(tema.negrita(14f));
        boton.setForeground(color);
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(color);
        boton.setGrosorBorde(1.5f);
        boton.setRadio(18);
        boton.setFocusPainted(false);
    }

    private void configurarFiltros() {
        panelFiltros.setColorFondo(Color.WHITE);
        panelFiltros.setColorBorde(BORDE);
        panelFiltros.setSombra(false);
        configurarSelectorFecha();

        campoBusqueda.setFont(tema.regular(14f));
        campoBusqueda.setPlaceholder("Buscar por número de orden...");
        configurarFiltro(filtroCajero, 225);
        configurarFiltro(filtroMetodo, 190);
        configurarFiltro(filtroServicio, 195);

        filtroCajero.addMenuOpcionListener(e -> {
            String opcion = e.getActionCommand();
            cajeroSeleccionado = opcion.startsWith("Todos") ? null : opcion;
            filtroCajero.setText(opcion);
            paginaActual = 1;
            aplicarFiltros();
        });
        filtroMetodo.addMenuOpcionListener(e -> {
            String opcion = e.getActionCommand();
            metodoSeleccionado = "Todos los métodos".equals(opcion)
                    ? null : opcion;
            filtroMetodo.setText(opcion);
            paginaActual = 1;
            aplicarFiltros();
        });
        filtroServicio.addMenuOpcionListener(e -> {
            String opcion = e.getActionCommand();
            servicioSeleccionado = "Todos los servicios".equals(opcion)
                    ? null : opcion;
            filtroServicio.setText(opcion);
            paginaActual = 1;
            aplicarFiltros();
        });

        configurarBotonAccion(botonLimpiarFiltros, ROJO);
        botonLimpiarFiltros.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.FILTRO, ROJO, 20));
        botonLimpiarFiltros.setIconTextGap(9);
        botonLimpiarFiltros.addActionListener(e -> limpiarFiltros());
    }

    private void configurarSelectorFecha() {
        fechaReporte.setModel(new javax.swing.SpinnerDateModel(
                new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor =
                new JSpinner.DateEditor(fechaReporte, "dd/MM/yyyy");
        editor.getFormat().setLenient(false);
        editor.getTextField().setFont(tema.regular(14f));
        editor.getTextField().setForeground(AZUL);
        editor.getTextField().setBackground(Color.WHITE);
        editor.getTextField().setBorder(new EmptyBorder(0, 14, 0, 8));
        fechaReporte.setEditor(editor);
        fechaReporte.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        fechaReporte.setToolTipText("Fecha del reporte");
    }

    private void configurarFiltro(BotonDesplegable boton, int ancho) {
        boton.setForeground(AZUL);
        boton.setFont(tema.regular(14f));
        boton.setColorFondo(Color.WHITE);
        boton.setColorHover(new Color(248, 249, 251));
        boton.setColorDesplegado(new Color(255, 247, 222));
        boton.setColorTextoOpcion(AZUL);
        boton.setColorBordeMenu(BORDE);
        boton.setAnchoMenu(ancho);
        boton.setAltoOpcion(42);
        boton.setBorderPainted(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(0, 14, 0, 34)));
    }

    private void configurarTabla() {
        panelTabla.setColorFondo(Color.WHITE);
        panelTabla.setColorBorde(BORDE);
        panelTabla.setSombra(false);
        tablaPedidos.setModel(modeloTabla);
        tablaPedidos.aplicarEstilo();
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.setRowHeight(32);
        tablaPedidos.setFilasAlternadas(true);
        tablaPedidos.setAltoCabecera(42);
        tablaPedidos.setColumnasCentradas("4,8");
        tablaPedidos.setColumnasDerecha("6,7");
        tablaPedidos.getColumnModel().getColumn(4)
                .setCellRenderer(new RenderEstado());
        tablaPedidos.getColumnModel().getColumn(6)
                .setCellRenderer(new RenderMoneda(false));
        tablaPedidos.getColumnModel().getColumn(7)
                .setCellRenderer(new RenderMoneda(true));
        configurarAnchosColumnas();

        scrollPedidos.setBorder(BorderFactory.createEmptyBorder());
        scrollPedidos.getViewport().setBackground(Color.WHITE);
        etiquetaRango.setFont(tema.regular(12f));
        etiquetaRango.setForeground(SECUNDARIO);

        configurarBotonPagina(botonAnterior, IconosUsuarios.Tipo.ANTERIOR);
        botonAnterior.addActionListener(e -> cambiarPagina(paginaActual - 1));

        botonesPagina.add(botonPagina1);
        botonesPagina.add(botonPagina2);
        botonesPagina.add(botonPagina3);
        for (BotonRedondeado boton : botonesPagina) {
            configurarBotonPagina(boton, null);
            boton.addActionListener(e -> {
                Object valor = boton.getClientProperty("pagina");
                if (valor instanceof Integer pagina) cambiarPagina(pagina);
            });
        }

        configurarBotonPagina(botonSiguiente, IconosUsuarios.Tipo.SIGUIENTE);
        botonSiguiente.addActionListener(e -> cambiarPagina(paginaActual + 1));
    }

    private void configurarAnchosColumnas() {
        int[] anchos = {78, 145, 165, 135, 115, 145, 130, 120, 105};
        for (int i = 0; i < anchos.length; i++) {
            tablaPedidos.getColumnModel().getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }
    }

    private void configurarBotonPagina(
            BotonRedondeado boton, IconosUsuarios.Tipo tipoIcono) {
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(BORDE);
        boton.setGrosorBorde(1f);
        boton.setRadio(12);
        boton.setPreferredSize(new Dimension(40, 38));
        boton.setFont(tema.media(13f));
        boton.setFocusPainted(false);
        if (tipoIcono != null) {
            boton.setText("");
            boton.setIcon(IconosUsuarios.crear(tipoIcono, AZUL, 17));
        }
    }

    private void configurarEventos() {
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
            @Override public void removeUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
            @Override public void changedUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }
        });

        fechaReporte.addChangeListener(e -> {
            botonExportar.setEnabled(false);
            botonImprimir.setEnabled(false);
            estadoCarga.setText(
                    "Fecha modificada. Pulsa Actualizar reporte.");
        });

        addHierarchyListener(e -> {
            boolean cambioVisibilidad = (e.getChangeFlags()
                    & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0;
            if (cambioVisibilidad && isShowing() && ultimoReporte == null
                    && carga == null && !java.beans.Beans.isDesignTime()) {
                actualizarReporte();
            }
        });
    }

    public void actualizarReporte() {
        if (carga != null) return;
        final LocalDate fecha;
        try {
            fechaReporte.commitEdit();
            fecha = ((Date) fechaReporte.getValue()).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.getYear() < 1000 || fecha.getYear() > 9998) {
                throw new IllegalArgumentException();
            }
        } catch (java.text.ParseException | IllegalArgumentException ex) {
            estadoCarga.setText("Fecha inválida. Usa dd/MM/aaaa.");
            return;
        }

        habilitarConsulta(false);
        limpiarResultados();
        estadoCarga.setText("Consultando "
                + fechaVisible.format(fecha) + "...");

        carga = new SwingWorker<>() {
            @Override protected ReporteDAO.Resumen doInBackground()
                    throws Exception {
                return reporteDAO.cargar(fecha);
            }

            @Override protected void done() {
                if (isCancelled()) return;
                try {
                    ReporteDAO.Resumen resultado = get();
                    ultimoReporte = resultado;
                    fechaCargada = fecha;
                    mostrarResumen(resultado);
                    cargarOpciones(resultado.ventasDetalle());
                    aplicarFiltros();
                    estadoCarga.setText(fechaVisible.format(fecha)
                            + " · Reporte actualizado");
                    botonExportar.setEnabled(true);
                    botonImprimir.setEnabled(
                            !resultado.ventasDetalle().isEmpty());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    estadoCarga.setText(
                            "No se pudo cargar el reporte. Comprueba MySQL.");
                    java.util.logging.Logger.getLogger(
                            ReportesPanel.class.getName()).log(
                            java.util.logging.Level.WARNING,
                            "Error al consultar reportes", ex.getCause());
                } finally {
                    carga = null;
                    habilitarConsulta(true);
                }
            }
        };
        carga.execute();
    }

    private void mostrarResumen(ReporteDAO.Resumen resultado) {
        labelVentas.setText(moneda.format(resultado.ventas()));
        labelPedidos.setText(Long.toString(resultado.pedidos()));
        labelTicket.setText(moneda.format(resultado.ticket()));
        labelCancelados.setText(Long.toString(resultado.cancelados()));
        labelEfectivo.setText(moneda.format(resultado.efectivo()));
        labelTarjeta.setText(moneda.format(resultado.tarjeta()));
        labelOtros.setText(moneda.format(resultado.otros()));
        labelCancelacionesCaja.setText(resultado.cancelados() + " pedidos");
        labelPorcentajeEfectivo.setText(
                porcentaje(resultado.efectivo(), resultado.ventas()));
        labelPorcentajeTarjeta.setText(
                porcentaje(resultado.tarjeta(), resultado.ventas()));
        labelPorcentajeOtros.setText(
                porcentaje(resultado.otros(), resultado.ventas()));
        mostrarAnalisis(resultado);
    }

    private void mostrarAnalisis(ReporteDAO.Resumen resultado) {
        modeloProductos.setFilas(resultado.productosMasVendidos());

        List<GraficaBarras.Dato> cajeros = new ArrayList<>();
        for (Object[] fila : resultado.ventasPorCajero()) {
            BigDecimal total = new BigDecimal(fila[1].toString());
            long pedidos = ((Number) fila[2]).longValue();
            cajeros.add(new GraficaBarras.Dato(
                    String.valueOf(fila[0]), total.doubleValue(),
                    moneda.format(total), Long.toString(pedidos)));
        }
        graficaCajeros.setDatos(cajeros);

        List<GraficaBarras.Dato> horas = new ArrayList<>();
        for (Object[] fila : resultado.ventasPorHora()) {
            int hora = ((Number) fila[0]).intValue();
            BigDecimal total = new BigDecimal(fila[1].toString());
            horas.add(new GraficaBarras.Dato(
                    String.format("%02d", hora), total.doubleValue(),
                    "Q" + total.setScale(0, RoundingMode.HALF_UP)));
        }
        graficaHoras.setDatos(horas);
    }

    private String porcentaje(BigDecimal parte, BigDecimal total) {
        if (total == null || total.signum() == 0) return "0% del total";
        return parte.multiply(BigDecimal.valueOf(100))
                .divide(total, 1, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString() + "% del total";
    }

    private void cargarOpciones(List<Object[]> ventas) {
        Set<String> cajeros = new TreeSet<>();
        for (Object[] fila : ventas) cajeros.add(String.valueOf(fila[2]));
        StringBuilder opciones = new StringBuilder("Todos los cajeros");
        for (String cajero : cajeros) {
            opciones.append(';').append(cajero.replace(';', ' '));
        }
        filtroCajero.setTextoDesplegable(opciones.toString());
        filtroCajero.setText("Todos los cajeros");
        filtroMetodo.setTextoDesplegable(
                "Todos los métodos;Efectivo;Tarjeta;Otros");
        filtroMetodo.setText("Todos los métodos");
        filtroServicio.setTextoDesplegable(
                "Todos los servicios;En el local;Para llevar;A domicilio");
        filtroServicio.setText("Todos los servicios");
        cajeroSeleccionado = null;
        metodoSeleccionado = null;
        servicioSeleccionado = null;
        campoBusqueda.setText("");
    }

    private void aplicarFiltros() {
        if (ultimoReporte == null) {
            ventasFiltradas = List.of();
            mostrarPagina();
            return;
        }
        String busqueda = campoBusqueda.getText().trim()
                .replace("#", "").toLowerCase(Locale.ROOT);
        List<Object[]> resultado = new ArrayList<>();
        for (Object[] fila : ultimoReporte.ventasDetalle()) {
            String metodo = String.valueOf(fila[5]);
            boolean coincideCajero = cajeroSeleccionado == null
                    || cajeroSeleccionado.equals(String.valueOf(fila[2]));
            boolean coincideMetodo = metodoSeleccionado == null
                    || ("Otros".equals(metodoSeleccionado)
                    ? !"EFECTIVO".equalsIgnoreCase(metodo)
                    && !"TARJETA".equalsIgnoreCase(metodo)
                    : metodoSeleccionado.equalsIgnoreCase(metodo));
            boolean coincideServicio = servicioSeleccionado == null
                    || servicioSeleccionado.equals(String.valueOf(fila[3]));
            boolean coincideBusqueda = busqueda.isEmpty()
                    || String.valueOf(fila[0]).toLowerCase(Locale.ROOT)
                            .contains(busqueda);
            if (coincideCajero && coincideMetodo
                    && coincideServicio && coincideBusqueda) {
                resultado.add(fila);
            }
        }
        ventasFiltradas = List.copyOf(resultado);
        paginaActual = 1;
        mostrarPagina();
    }

    private void limpiarFiltros() {
        temporizadorBusqueda.stop();
        campoBusqueda.setText("");
        cajeroSeleccionado = null;
        metodoSeleccionado = null;
        servicioSeleccionado = null;
        filtroCajero.setText("Todos los cajeros");
        filtroMetodo.setText("Todos los métodos");
        filtroServicio.setText("Todos los servicios");
        paginaActual = 1;
        aplicarFiltros();
    }

    private int totalPaginas() {
        return Math.max(1, (int) Math.ceil(
                ventasFiltradas.size() / (double) VENTAS_POR_PAGINA));
    }

    private void mostrarPagina() {
        int totalPaginas = totalPaginas();
        paginaActual = Math.max(1, Math.min(paginaActual, totalPaginas));
        int desde = Math.min((paginaActual - 1) * VENTAS_POR_PAGINA,
                ventasFiltradas.size());
        int hasta = Math.min(desde + VENTAS_POR_PAGINA,
                ventasFiltradas.size());
        modeloTabla.setFilas(ventasFiltradas.subList(desde, hasta));
        actualizarPaginacion(desde, hasta, totalPaginas);
    }

    private void actualizarPaginacion(
            int desde, int hasta, int totalPaginas) {
        etiquetaRango.setText(ventasFiltradas.isEmpty()
                ? "No se encontraron ventas"
                : String.format("Mostrando %d–%d de %d ventas",
                        desde + 1, hasta, ventasFiltradas.size()));
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        int inicio = Math.max(1,
                Math.min(paginaActual - 1, totalPaginas - 2));

        for (int i = 0; i < botonesPagina.size(); i++) {
            int pagina = inicio + i;
            BotonRedondeado boton = botonesPagina.get(i);
            boolean visible = pagina <= totalPaginas;
            boton.setVisible(visible);
            if (!visible) continue;
            boton.putClientProperty("pagina", pagina);
            boton.setText(String.valueOf(pagina));
            boolean seleccionada = pagina == paginaActual;
            boton.setColorInicio(seleccionada ? AMARILLO : Color.WHITE);
            boton.setColorFinal(seleccionada ? AMARILLO : Color.WHITE);
            boton.setColorBorde(seleccionada ? AMARILLO : BORDE);
            boton.setFont(seleccionada
                    ? tema.negrita(13f) : tema.media(13f));
        }
    }

    private void cambiarPagina(int pagina) {
        if (pagina < 1 || pagina > totalPaginas()
                || pagina == paginaActual) return;
        paginaActual = pagina;
        mostrarPagina();
    }

    private void habilitarConsulta(boolean habilitada) {
        botonActualizar.setEnabled(habilitada);
        fechaReporte.setEnabled(habilitada);
    }

    private void limpiarResultados() {
        ultimoReporte = null;
        ventasFiltradas = List.of();
        for (JLabel etiqueta : new JLabel[]{
            labelVentas, labelPedidos, labelTicket, labelCancelados,
            labelEfectivo, labelTarjeta, labelOtros
        }) etiqueta.setText("—");
        labelCancelacionesCaja.setText("0 pedidos");
        labelPorcentajeEfectivo.setText("0% del total");
        labelPorcentajeTarjeta.setText("0% del total");
        labelPorcentajeOtros.setText("0% del total");
        modeloProductos.setFilas(List.of());
        graficaCajeros.setDatos(List.of());
        graficaHoras.setDatos(List.of());
        paginaActual = 1;
        mostrarPagina();
        botonExportar.setEnabled(false);
        botonImprimir.setEnabled(false);
    }

    private void exportarReporte() {
        if (ultimoReporte == null || fechaCargada == null) return;
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte de ventas");
        selector.setFileFilter(new FileNameExtensionFilter(
                "Archivo CSV", "csv"));
        selector.setSelectedFile(new File(
                "reporte-ventas-" + fechaCargada + ".csv"));
        if (selector.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) return;

        Path archivo = selector.getSelectedFile().toPath();
        if (!archivo.toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            archivo = archivo.resolveSibling(
                    archivo.getFileName() + ".csv");
        }
        try {
            Files.writeString(archivo,
                    ReporteCsv.generar(fechaCargada, ultimoReporte),
                    StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this,
                    "Reporte guardado en:\n" + archivo,
                    "Exportación completada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el archivo.",
                    "Exportación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirReporte() {
        if (ventasFiltradas.isEmpty() || fechaCargada == null) return;
        modeloTabla.setFilas(ventasFiltradas);
        try {
            tablaPedidos.print(JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat("Reporte de ventas · "
                            + fechaVisible.format(fechaCargada)),
                    new MessageFormat("Página {0}"));
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo imprimir el reporte.",
                    "Impresión", JOptionPane.ERROR_MESSAGE);
        } finally {
            mostrarPagina();
        }
    }

    @Override
    public void removeNotify() {
        temporizadorBusqueda.stop();
        if (carga != null) {
            carga.cancel(true);
            carga = null;
        }
        super.removeNotify();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitulo = new javax.swing.JLabel();
        labelSubtitulo = new javax.swing.JLabel();
        botonActualizar = new Componentes.BotonDerretido();
        botonExportar = new Componentes.BotonRedondeado();
        botonImprimir = new Componentes.BotonRedondeado();
        panelVentas = new Componentes.PanelFlotante();
        labelTituloVentas = new javax.swing.JLabel();
        labelVentas = new javax.swing.JLabel();
        panelCircular2 = new Componentes.PanelCircular();
        labelEscalable2 = new Labels.LabelEscalable();
        panelPedidos = new Componentes.PanelFlotante();
        labelTituloPedidos = new javax.swing.JLabel();
        labelPedidos = new javax.swing.JLabel();
        panelCircular3 = new Componentes.PanelCircular();
        labelEscalable3 = new Labels.LabelEscalable();
        panelTicket = new Componentes.PanelFlotante();
        labelTituloTicket = new javax.swing.JLabel();
        labelTicket = new javax.swing.JLabel();
        panelCircular5 = new Componentes.PanelCircular();
        labelEscalable5 = new Labels.LabelEscalable();
        panelCancelados = new Componentes.PanelFlotante();
        labelTituloCancelados = new javax.swing.JLabel();
        labelCancelados = new javax.swing.JLabel();
        panelCircular4 = new Componentes.PanelCircular();
        labelEscalable4 = new Labels.LabelEscalable();
        panelFiltros = new Componentes.PanelFlotante();
        fechaReporte = new javax.swing.JSpinner();
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        filtroCajero = new Componentes.BotonDesplegable();
        filtroMetodo = new Componentes.BotonDesplegable();
        filtroServicio = new Componentes.BotonDesplegable();
        botonLimpiarFiltros = new Componentes.BotonRedondeado();
        panelProductosVendidos = new Componentes.PanelFlotante();
        labelProductosVendidos = new javax.swing.JLabel();
        labelProductosSubtitulo = new javax.swing.JLabel();
        scrollProductos = new javax.swing.JScrollPane();
        tablaProductos = new Componentes.TablaAdministrativa();
        panelVentasCajero = new Componentes.PanelFlotante();
        labelVentasCajero = new javax.swing.JLabel();
        labelCajeroSubtitulo = new javax.swing.JLabel();
        graficaCajeros = new Componentes.GraficaBarras();
        panelVentasHora = new Componentes.PanelFlotante();
        labelVentasHora = new javax.swing.JLabel();
        labelHoraSubtitulo = new javax.swing.JLabel();
        graficaHoras = new Componentes.GraficaBarras();
        panelTabla = new Componentes.PanelFlotante();
        labelDetalleVentas = new javax.swing.JLabel();
        scrollPedidos = new javax.swing.JScrollPane();
        tablaPedidos = new Componentes.TablaAdministrativa();
        etiquetaRango = new javax.swing.JLabel();
        panelPaginacion = new javax.swing.JPanel();
        botonAnterior = new Componentes.BotonRedondeado();
        botonPagina1 = new Componentes.BotonRedondeado();
        botonPagina2 = new Componentes.BotonRedondeado();
        botonPagina3 = new Componentes.BotonRedondeado();
        botonSiguiente = new Componentes.BotonRedondeado();
        panelResumen = new Componentes.PanelFlotante();
        labelResumen = new javax.swing.JLabel();
        labelResumenSubtitulo = new javax.swing.JLabel();
        separadorResumen1 = new javax.swing.JSeparator();
        efectivoTitulo = new javax.swing.JLabel();
        labelEfectivo = new javax.swing.JLabel();
        labelPorcentajeEfectivo = new javax.swing.JLabel();
        separadorResumen2 = new javax.swing.JSeparator();
        tarjetaTitulo = new javax.swing.JLabel();
        labelTarjeta = new javax.swing.JLabel();
        labelPorcentajeTarjeta = new javax.swing.JLabel();
        separadorResumen3 = new javax.swing.JSeparator();
        otrosTitulo = new javax.swing.JLabel();
        labelOtros = new javax.swing.JLabel();
        labelPorcentajeOtros = new javax.swing.JLabel();
        separadorResumen4 = new javax.swing.JSeparator();
        cancelacionTitulo = new javax.swing.JLabel();
        labelCancelacionesCaja = new javax.swing.JLabel();
        cancelacionDetalle = new javax.swing.JLabel();
        estadoCarga = new javax.swing.JLabel();

        setBackground(new java.awt.Color(248, 249, 251));
        setPreferredSize(new java.awt.Dimension(1580, 1230));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo.setFont(new java.awt.Font("Dialog", 1, 42)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo.setText("Reportes - Ventas del día");
        add(labelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 25, 690, 65));

        labelSubtitulo.setForeground(new java.awt.Color(92, 103, 124));
        labelSubtitulo.setText("Resumen detallado de todas las ventas realizadas en la fecha seleccionada");
        add(labelSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 88, 760, 28));

        botonActualizar.setForeground(new java.awt.Color(0, 20, 43));
        botonActualizar.setText("Actualizar reporte");
        add(botonActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 60, 220, 80));

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
        botonExportar.setMaximumSize(new java.awt.Dimension(96, 29));
        botonExportar.setMinimumSize(new java.awt.Dimension(96, 29));
        add(botonExportar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1185, 60, 180, 58));

        botonImprimir.setForeground(new java.awt.Color(0, 20, 43));
        botonImprimir.setText("Imprimir");
        botonImprimir.setDegradado(false);
        add(botonImprimir, new org.netbeans.lib.awtextra.AbsoluteConstraints(1375, 60, 145, 58));

        panelVentas.setRadio(18);
        panelVentas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTituloVentas.setText("Ventas del día");
        panelVentas.add(labelTituloVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 195, 30));

        labelVentas.setText("—");
        panelVentas.add(labelVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 52, 195, 48));

        panelCircular2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/quetzal_icono.png"))); // NOI18N
        panelCircular2.add(labelEscalable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 13, 65, 65));

        panelVentas.add(panelCircular2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        add(panelVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, 360, 150));

        panelPedidos.setRadio(18);
        panelPedidos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTituloPedidos.setText("Pedidos cobrados");
        panelPedidos.add(labelTituloPedidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 195, 30));

        labelPedidos.setText("—");
        panelPedidos.add(labelPedidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 52, 195, 48));

        panelCircular3.setColorFondo(new java.awt.Color(252, 233, 233));
        panelCircular3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/bolsa_icono.png"))); // NOI18N
        panelCircular3.add(labelEscalable3, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 13, 58, 60));

        panelPedidos.add(panelCircular3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        add(panelPedidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 150, 360, 150));

        panelTicket.setRadio(18);
        panelTicket.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTituloTicket.setText("Ticket promedio");
        panelTicket.add(labelTituloTicket, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 195, 30));

        labelTicket.setText("—");
        panelTicket.add(labelTicket, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 52, 195, 48));

        panelCircular5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ticket_icono.png"))); // NOI18N
        panelCircular5.add(labelEscalable5, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 15, 60, 60));

        panelTicket.add(panelCircular5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        add(panelTicket, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 150, 360, 150));

        panelCancelados.setRadio(18);
        panelCancelados.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTituloCancelados.setText("Cancelados");
        panelCancelados.add(labelTituloCancelados, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 195, 30));

        labelCancelados.setText("—");
        panelCancelados.add(labelCancelados, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 52, 195, 48));

        panelCircular4.setColorFondo(new java.awt.Color(237, 239, 243));
        panelCircular4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/x_icono.png"))); // NOI18N
        panelCircular4.add(labelEscalable4, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 27, 35, 35));

        panelCancelados.add(panelCircular4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        add(panelCancelados, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 150, 360, 150));

        panelFiltros.setRadio(18);
        panelFiltros.setSombra(false);
        panelFiltros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelFiltros.add(fechaReporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 24, 180, 52));

        campoBusqueda.setPlaceholder("Buscar por número de orden...");
        panelFiltros.add(campoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 22, 330, 56));

        filtroCajero.setForeground(new java.awt.Color(0, 20, 43));
        filtroCajero.setText("Todos los cajeros");
        filtroCajero.setTextoDesplegable("Todos los cajeros");
        panelFiltros.add(filtroCajero, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 24, 235, 52));

        filtroMetodo.setForeground(new java.awt.Color(0, 20, 43));
        filtroMetodo.setText("Todos los métodos");
        filtroMetodo.setTextoDesplegable("Todos los métodos;Efectivo;Tarjeta;Otros");
        panelFiltros.add(filtroMetodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 24, 200, 52));

        filtroServicio.setForeground(new java.awt.Color(0, 20, 43));
        filtroServicio.setText("Todos los servicios");
        filtroServicio.setTextoDesplegable("Todos los servicios;En el local;Para llevar;A domicilio");
        panelFiltros.add(filtroServicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(1025, 24, 205, 52));

        botonLimpiarFiltros.setForeground(new java.awt.Color(231, 55, 65));
        botonLimpiarFiltros.setText("Limpiar filtros");
        botonLimpiarFiltros.setDegradado(false);
        panelFiltros.add(botonLimpiarFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(1245, 24, 200, 52));

        add(panelFiltros, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 320, 1470, 100));

        panelProductosVendidos.setRadio(18);
        panelProductosVendidos.setSombra(false);
        panelProductosVendidos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelProductosVendidos.setText("Productos más vendidos");
        panelProductosVendidos.add(labelProductosVendidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 14, 300, 28));

        labelProductosSubtitulo.setText("Top 5 de la fecha seleccionada");
        panelProductosVendidos.add(labelProductosSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 41, 300, 22));

        scrollProductos.setBorder(null);
        scrollProductos.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollProductos.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Producto", "Cantidad", "% ventas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollProductos.setViewportView(tablaProductos);

        panelProductosVendidos.add(scrollProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 70, 419, 180));

        add(panelProductosVendidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 435, 455, 270));

        panelVentasCajero.setRadio(18);
        panelVentasCajero.setSombra(false);
        panelVentasCajero.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelVentasCajero.setText("Ventas por cajero");
        panelVentasCajero.add(labelVentasCajero, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 14, 300, 28));

        labelCajeroSubtitulo.setText("Total vendido y número de pedidos");
        panelVentasCajero.add(labelCajeroSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 41, 350, 22));

        panelVentasCajero.add(graficaCajeros, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 65, 449, 190));

        add(panelVentasCajero, new org.netbeans.lib.awtextra.AbsoluteConstraints(525, 435, 485, 270));

        panelVentasHora.setRadio(18);
        panelVentasHora.setSombra(false);
        panelVentasHora.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelVentasHora.setText("Ventas por hora");
        panelVentasHora.add(labelVentasHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 14, 300, 28));

        labelHoraSubtitulo.setText("Total vendido por hora");
        panelVentasHora.add(labelHoraSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 41, 300, 22));

        panelVentasHora.add(graficaHoras, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 65, 474, 190));

        add(panelVentasHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 435, 510, 270));

        panelTabla.setRadio(18);
        panelTabla.setSombra(false);
        panelTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelDetalleVentas.setText("Detalle de ventas del día");
        panelTabla.add(labelDetalleVentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 12, 350, 30));

        scrollPedidos.setBorder(null);
        scrollPedidos.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPedidos.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tablaPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "N° orden", "Fecha/Hora", "Cajero", "Tipo servicio", "Estado", "Método de pago", "Monto recibido", "Total", "Cantidad de ítems"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPedidos.setViewportView(tablaPedidos);

        panelTabla.add(scrollPedidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 48, 1442, 216));

        etiquetaRango.setForeground(new java.awt.Color(92, 103, 124));
        etiquetaRango.setText("No se encontraron ventas");
        panelTabla.add(etiquetaRango, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 278, 420, 42));

        panelPaginacion.setOpaque(false);
        panelPaginacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonAnterior.setText("‹");
        panelPaginacion.add(botonAnterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 40, 38));

        botonPagina1.setText("1");
        panelPaginacion.add(botonPagina1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 2, 40, 38));

        botonPagina2.setText("2");
        panelPaginacion.add(botonPagina2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 2, 40, 38));

        botonPagina3.setText("3");
        panelPaginacion.add(botonPagina3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 2, 40, 38));

        botonSiguiente.setText("›");
        panelPaginacion.add(botonSiguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 2, 40, 38));

        panelTabla.add(panelPaginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 278, 270, 42));

        add(panelTabla, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 720, 1470, 340));

        panelResumen.setRadio(18);
        panelResumen.setSombra(false);
        panelResumen.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelResumen.setText("Resumen de caja");
        panelResumen.add(labelResumen, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 18, 180, 28));

        labelResumenSubtitulo.setText("Totales del día");
        panelResumen.add(labelResumenSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 50, 180, 24));

        separadorResumen1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        panelResumen.add(separadorResumen1, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 16, 1, 78));

        efectivoTitulo.setText("Efectivo");
        panelResumen.add(efectivoTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 14, 250, 22));

        labelEfectivo.setText("—");
        panelResumen.add(labelEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 38, 250, 30));

        labelPorcentajeEfectivo.setText("0% del total");
        panelResumen.add(labelPorcentajeEfectivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 73, 250, 22));

        separadorResumen2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        panelResumen.add(separadorResumen2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 16, 1, 78));

        tarjetaTitulo.setText("Tarjeta");
        panelResumen.add(tarjetaTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 14, 250, 22));

        labelTarjeta.setText("—");
        panelResumen.add(labelTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 38, 250, 30));

        labelPorcentajeTarjeta.setText("0% del total");
        panelResumen.add(labelPorcentajeTarjeta, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 73, 250, 22));

        separadorResumen3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        panelResumen.add(separadorResumen3, new org.netbeans.lib.awtextra.AbsoluteConstraints(835, 16, 1, 78));

        otrosTitulo.setText("Otros métodos");
        panelResumen.add(otrosTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 14, 250, 22));

        labelOtros.setText("—");
        panelResumen.add(labelOtros, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 38, 250, 30));

        labelPorcentajeOtros.setText("0% del total");
        panelResumen.add(labelPorcentajeOtros, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 73, 250, 22));

        separadorResumen4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        panelResumen.add(separadorResumen4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 16, 1, 78));

        cancelacionTitulo.setText("Cancelaciones");
        panelResumen.add(cancelacionTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 14, 250, 22));

        labelCancelacionesCaja.setText("0 pedidos");
        panelResumen.add(labelCancelacionesCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 38, 250, 30));

        cancelacionDetalle.setText("Pedidos cancelados");
        panelResumen.add(cancelacionDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 73, 250, 22));

        add(panelResumen, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 1075, 1470, 110));

        estadoCarga.setForeground(new java.awt.Color(92, 103, 124));
        estadoCarga.setText("Selecciona una fecha y actualiza el reporte.");
        add(estadoCarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 1192, 1450, 24));
    }// </editor-fold>//GEN-END:initComponents

    private org.netbeans.lib.awtextra.AbsoluteConstraints posicion(
            int x, int y, int ancho, int alto) {
        return new org.netbeans.lib.awtextra.AbsoluteConstraints(
                x, y, ancho, alto);
    }

    private JLabel etiqueta(String texto, int tamano, boolean negrita) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new java.awt.Font(
                "Dialog", negrita ? 1 : 0, tamano));
        etiqueta.setForeground(negrita ? AZUL : SECUNDARIO);
        return etiqueta;
    }

    private BotonRedondeado boton(String texto) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText(texto);
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        return boton;
    }

    private PanelFlotante panel() {
        PanelFlotante panel = new PanelFlotante();
        panel.setRadio(18);
        panel.setSombra(false);
        panel.setColorFondo(Color.WHITE);
        panel.setColorBorde(BORDE);
        return panel;
    }

    private PanelFlotante tarjeta() {
        PanelFlotante panel = panel();
        panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        return panel;
    }

    private PanelCircular icono(PanelFlotante tarjeta, String ruta) {
        PanelCircular panelIcono = new PanelCircular();
        panelIcono.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        Labels.LabelEscalable imagen = new Labels.LabelEscalable();
        imagen.setIcon(new javax.swing.ImageIcon(getClass().getResource(ruta)));
        panelIcono.add(imagen, posicion(14, 18, 52, 52));
        tarjeta.add(panelIcono, posicion(40, 30, 80, 90));
        return panelIcono;
    }

    private JLabel tituloTarjeta(PanelFlotante panel, String texto) {
        JLabel etiqueta = etiqueta(texto, 14, false);
        panel.add(etiqueta, posicion(140, 20, 195, 30));
        return etiqueta;
    }

    private JLabel valorTarjeta(PanelFlotante panel) {
        JLabel etiqueta = etiqueta("—", 28, true);
        panel.add(etiqueta, posicion(140, 52, 195, 48));
        return etiqueta;
    }

    private BotonDesplegable filtro(String texto, String opciones) {
        BotonDesplegable filtro = new BotonDesplegable();
        filtro.setText(texto);
        filtro.setTextoDesplegable(opciones);
        filtro.setForeground(AZUL);
        return filtro;
    }

    private javax.swing.JSeparator separador(PanelFlotante panel, int x) {
        javax.swing.JSeparator separador =
                new javax.swing.JSeparator(SwingConstants.VERTICAL);
        panel.add(separador, posicion(x, 16, 1, 78));
        return separador;
    }

    private JLabel tituloResumen(
            PanelFlotante panel, String texto, int x) {
        JLabel etiqueta = etiqueta(texto, 13, false);
        panel.add(etiqueta, posicion(x, 14, 250, 22));
        return etiqueta;
    }

    private JLabel valorResumen(PanelFlotante panel, int x) {
        JLabel etiqueta = etiqueta("—", 21, true);
        panel.add(etiqueta, posicion(x, 38, 250, 30));
        return etiqueta;
    }

    private JLabel detalleResumen(
            PanelFlotante panel, String texto, int x) {
        JLabel etiqueta = etiqueta(texto, 12, false);
        panel.add(etiqueta, posicion(x, 73, 250, 22));
        return etiqueta;
    }

    private static final class ModeloProductosVendidos
            extends AbstractTableModel {

        private final String[] columnas = {
            "#", "Producto", "Cantidad", "% ventas"
        };
        private List<Object[]> filas = List.of();

        private void setFilas(List<Object[]> nuevasFilas) {
            filas = nuevasFilas == null
                    ? List.of() : List.copyOf(nuevasFilas);
            fireTableDataChanged();
        }

        @Override public int getRowCount() {
            return filas.size();
        }

        @Override public int getColumnCount() {
            return columnas.length;
        }

        @Override public String getColumnName(int columna) {
            return columnas[columna];
        }

        @Override public Object getValueAt(int fila, int columna) {
            Object[] dato = filas.get(fila);
            return switch (columna) {
                case 0 -> fila + 1;
                case 1 -> dato[0];
                case 2 -> dato[1];
                case 3 -> dato[2] + "%";
                default -> "";
            };
        }

        @Override public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    }

    private final class ModeloTablaVentas extends AbstractTableModel {
        private final String[] columnas = {
            "N° orden", "Fecha/Hora", "Cajero", "Tipo servicio",
            "Estado", "Método de pago", "Monto recibido", "Total",
            "Cantidad de ítems"
        };
        private List<Object[]> filas = List.of();

        private void setFilas(List<Object[]> nuevasFilas) {
            filas = List.copyOf(nuevasFilas);
            fireTableDataChanged();
        }
        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int columna) {
            return columnas[columna];
        }
        @Override public Object getValueAt(int fila, int columna) {
            Object valor = filas.get(fila)[columna];
            return switch (columna) {
                case 0 -> "#" + valor;
                case 5 -> metodoVisible(valor);
                default -> valor;
            };
        }
        @Override public Class<?> getColumnClass(int columna) {
            return switch (columna) {
                case 6, 7 -> BigDecimal.class;
                default -> String.class;
            };
        }
        @Override public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    }

    private String metodoVisible(Object valor) {
        String metodo = valor == null ? "" : valor.toString();
        return switch (metodo.toUpperCase(Locale.ROOT)) {
            case "EFECTIVO" -> "Efectivo";
            case "TARJETA" -> "Tarjeta";
            case "", "-" -> "—";
            default -> metodo;
        };
    }

    private final class RenderEstado implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable tabla, Object valor, boolean seleccionado,
                boolean foco, int fila, int columna) {
            String visible;
            Color texto;
            Color fondo;
            switch (String.valueOf(valor)) {
                case "PAGADO" -> {
                    visible = "Completado";
                    texto = VERDE;
                    fondo = new Color(230, 245, 234);
                }
                case "CANCELADO" -> {
                    visible = "Cancelado";
                    texto = ROJO;
                    fondo = new Color(252, 233, 233);
                }
                default -> {
                    visible = "Pendiente";
                    texto = new Color(176, 117, 0);
                    fondo = new Color(255, 244, 211);
                }
            }
            JPanel panel = new JPanel(new FlowLayout(
                    FlowLayout.CENTER, 0, 5));
            panel.setBackground(fondoFila(tabla, seleccionado, fila));
            JLabel etiqueta = new JLabel(visible);
            etiqueta.setFont(tema.media(11f));
            etiqueta.setForeground(texto);
            etiqueta.setBackground(fondo);
            etiqueta.setOpaque(true);
            etiqueta.setBorder(new EmptyBorder(3, 11, 3, 11));
            panel.add(etiqueta);
            return panel;
        }
    }

    private final class RenderMoneda extends DefaultTableCellRenderer {
        private final boolean resaltar;
        private RenderMoneda(boolean resaltar) {
            this.resaltar = resaltar;
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable tabla, Object valor, boolean seleccionado,
                boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(
                    tabla, valor, seleccionado, foco, fila, columna);
            setText(valor instanceof Number ? moneda.format(valor) : "Q0.00");
            setBackground(fondoFila(tabla, seleccionado, fila));
            setForeground(seleccionado
                    ? tabla.getSelectionForeground()
                    : resaltar ? NARANJA : AZUL);
            setFont(resaltar ? tema.negrita(12f) : tema.regular(12f));
            setBorder(new EmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    private Color fondoFila(
            JTable tabla, boolean seleccionado, int fila) {
        if (seleccionado) return tabla.getSelectionBackground();
        return fila % 2 == 0 ? Color.WHITE : new Color(250, 251, 252);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonDerretido botonActualizar;
    private Componentes.BotonRedondeado botonAnterior;
    private Componentes.BotonRedondeado botonExportar;
    private Componentes.BotonRedondeado botonImprimir;
    private Componentes.BotonRedondeado botonLimpiarFiltros;
    private Componentes.BotonRedondeado botonPagina1;
    private Componentes.BotonRedondeado botonPagina2;
    private Componentes.BotonRedondeado botonPagina3;
    private Componentes.BotonRedondeado botonSiguiente;
    private Componentes.CampoBusquedaAdmin campoBusqueda;
    private javax.swing.JLabel cancelacionDetalle;
    private javax.swing.JLabel cancelacionTitulo;
    private javax.swing.JLabel efectivoTitulo;
    private javax.swing.JLabel estadoCarga;
    private javax.swing.JLabel etiquetaRango;
    private javax.swing.JSpinner fechaReporte;
    private Componentes.BotonDesplegable filtroCajero;
    private Componentes.BotonDesplegable filtroMetodo;
    private Componentes.BotonDesplegable filtroServicio;
    private Componentes.GraficaBarras graficaCajeros;
    private Componentes.GraficaBarras graficaHoras;
    private javax.swing.JLabel labelCajeroSubtitulo;
    private javax.swing.JLabel labelCancelacionesCaja;
    private javax.swing.JLabel labelCancelados;
    private javax.swing.JLabel labelDetalleVentas;
    private javax.swing.JLabel labelEfectivo;
    private Labels.LabelEscalable labelEscalable2;
    private Labels.LabelEscalable labelEscalable3;
    private Labels.LabelEscalable labelEscalable4;
    private Labels.LabelEscalable labelEscalable5;
    private javax.swing.JLabel labelOtros;
    private javax.swing.JLabel labelPedidos;
    private javax.swing.JLabel labelProductosSubtitulo;
    private javax.swing.JLabel labelProductosVendidos;
    private javax.swing.JLabel labelPorcentajeEfectivo;
    private javax.swing.JLabel labelPorcentajeOtros;
    private javax.swing.JLabel labelPorcentajeTarjeta;
    private javax.swing.JLabel labelResumen;
    private javax.swing.JLabel labelResumenSubtitulo;
    private javax.swing.JLabel labelSubtitulo;
    private javax.swing.JLabel labelTarjeta;
    private javax.swing.JLabel labelTicket;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelTituloCancelados;
    private javax.swing.JLabel labelTituloPedidos;
    private javax.swing.JLabel labelTituloTicket;
    private javax.swing.JLabel labelTituloVentas;
    private javax.swing.JLabel labelVentas;
    private javax.swing.JLabel labelVentasCajero;
    private javax.swing.JLabel labelVentasHora;
    private javax.swing.JLabel labelHoraSubtitulo;
    private javax.swing.JLabel otrosTitulo;
    private Componentes.PanelFlotante panelCancelados;
    private Componentes.PanelCircular panelCircular2;
    private Componentes.PanelCircular panelCircular3;
    private Componentes.PanelCircular panelCircular4;
    private Componentes.PanelCircular panelCircular5;
    private Componentes.PanelFlotante panelFiltros;
    private javax.swing.JPanel panelPaginacion;
    private Componentes.PanelFlotante panelPedidos;
    private Componentes.PanelFlotante panelProductosVendidos;
    private Componentes.PanelFlotante panelResumen;
    private Componentes.PanelFlotante panelTabla;
    private Componentes.PanelFlotante panelTicket;
    private Componentes.PanelFlotante panelVentas;
    private Componentes.PanelFlotante panelVentasCajero;
    private Componentes.PanelFlotante panelVentasHora;
    private javax.swing.JScrollPane scrollPedidos;
    private javax.swing.JScrollPane scrollProductos;
    private javax.swing.JSeparator separadorResumen1;
    private javax.swing.JSeparator separadorResumen2;
    private javax.swing.JSeparator separadorResumen3;
    private javax.swing.JSeparator separadorResumen4;
    private Componentes.TablaAdministrativa tablaPedidos;
    private Componentes.TablaAdministrativa tablaProductos;
    private javax.swing.JLabel tarjetaTitulo;
    // End of variables declaration//GEN-END:variables
}
