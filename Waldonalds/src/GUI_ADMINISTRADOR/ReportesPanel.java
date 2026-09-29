package GUI_ADMINISTRADOR;

import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
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

        configurarBotonAccion(botonExportar, ROJO);
        botonExportar.setIcon(IconosUsuarios.crear(
                IconosUsuarios.Tipo.EXPORTAR, ROJO, 20));
        botonExportar.setIconTextGap(10);
        botonExportar.addActionListener(e -> exportarReporte());

        configurarBotonAccion(botonImprimir, AZUL);
        botonImprimir.addActionListener(e -> imprimirReporte());

        configurarTarjeta(panelVentas, panelIconoVentas,
                labelTituloVentas, labelVentas, false);
        configurarTarjeta(panelPedidos, panelIconoPedidos,
                labelTituloPedidos, labelPedidos, true);
        configurarTarjeta(panelTicket, panelIconoTicket,
                labelTituloTicket, labelTicket, false);
        configurarTarjeta(panelCancelados, panelIconoCancelados,
                labelTituloCancelados, labelCancelados, true);
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

    private void configurarTarjeta(PanelFlotante panel,
            PanelCircular icono, JLabel titulo, JLabel valor, boolean roja) {
        panel.setColorFondo(Color.WHITE);
        panel.setColorBorde(BORDE);
        panel.setRadio(18);
        panel.setSombra(true);
        panel.setTamanoSombra(9);
        icono.setColorFondo(roja
                ? new Color(252, 233, 233)
                : new Color(255, 244, 211));
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
        setBackground(new Color(248, 249, 251));
        setPreferredSize(new Dimension(1580, 980));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo = etiqueta("Reportes - Ventas del día", 42, true);
        add(labelTitulo, posicion(70, 25, 690, 65));
        labelSubtitulo = etiqueta(
                "Resumen detallado de todas las ventas realizadas en la fecha seleccionada",
                16, false);
        add(labelSubtitulo, posicion(70, 88, 760, 28));

        botonActualizar = new Componentes.BotonDerretido();
        botonActualizar.setText("Actualizar reporte");
        add(botonActualizar, posicion(980, 50, 220, 80));
        botonExportar = boton("Exportar");
        add(botonExportar, posicion(1215, 60, 145, 58));
        botonImprimir = boton("Imprimir");
        add(botonImprimir, posicion(1375, 60, 145, 58));

        panelVentas = tarjeta();
        panelIconoVentas = icono(panelVentas, "/Imagenes/quetzal_icono.png");
        labelTituloVentas = tituloTarjeta(panelVentas, "Ventas del día");
        labelVentas = valorTarjeta(panelVentas);
        add(panelVentas, posicion(60, 150, 360, 150));

        panelPedidos = tarjeta();
        panelIconoPedidos = icono(panelPedidos, "/Imagenes/bolsa_icono.png");
        labelTituloPedidos = tituloTarjeta(panelPedidos, "Pedidos cobrados");
        labelPedidos = valorTarjeta(panelPedidos);
        add(panelPedidos, posicion(430, 150, 360, 150));

        panelTicket = tarjeta();
        panelIconoTicket = icono(panelTicket, "/Imagenes/ticket_icono.png");
        labelTituloTicket = tituloTarjeta(panelTicket, "Ticket promedio");
        labelTicket = valorTarjeta(panelTicket);
        add(panelTicket, posicion(800, 150, 360, 150));

        panelCancelados = tarjeta();
        panelIconoCancelados = icono(panelCancelados, "/Imagenes/x_icono.png");
        labelTituloCancelados = tituloTarjeta(panelCancelados, "Cancelados");
        labelCancelados = valorTarjeta(panelCancelados);
        add(panelCancelados, posicion(1170, 150, 360, 150));

        panelFiltros = panel();
        panelFiltros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        fechaReporte = new JSpinner();
        panelFiltros.add(fechaReporte, posicion(20, 24, 180, 52));
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        campoBusqueda.setPlaceholder("Buscar por número de orden...");
        panelFiltros.add(campoBusqueda, posicion(215, 22, 330, 56));
        filtroCajero = filtro("Todos los cajeros", "Todos los cajeros");
        panelFiltros.add(filtroCajero, posicion(560, 24, 235, 52));
        filtroMetodo = filtro("Todos los métodos",
                "Todos los métodos;Efectivo;Tarjeta;Otros");
        panelFiltros.add(filtroMetodo, posicion(810, 24, 200, 52));
        filtroServicio = filtro("Todos los servicios",
                "Todos los servicios;En el local;Para llevar;A domicilio");
        panelFiltros.add(filtroServicio, posicion(1025, 24, 205, 52));
        botonLimpiarFiltros = boton("Limpiar filtros");
        panelFiltros.add(botonLimpiarFiltros, posicion(1245, 24, 200, 52));
        add(panelFiltros, posicion(60, 320, 1470, 100));

        panelTabla = panel();
        panelTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        scrollPedidos = new javax.swing.JScrollPane();
        scrollPedidos.setBorder(null);
        scrollPedidos.setVerticalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        tablaPedidos = new Componentes.TablaAdministrativa();
        tablaPedidos.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"N° orden", "Fecha/Hora", "Cajero",
                    "Tipo servicio", "Estado", "Método de pago",
                    "Monto recibido", "Total", "Cantidad de ítems"}) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        scrollPedidos.setViewportView(tablaPedidos);
        panelTabla.add(scrollPedidos, posicion(14, 14, 1442, 300));
        etiquetaRango = etiqueta("No se encontraron ventas", 12, false);
        panelTabla.add(etiquetaRango, posicion(28, 328, 420, 42));

        panelPaginacion = new JPanel(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelPaginacion.setOpaque(false);
        botonAnterior = boton("‹");
        panelPaginacion.add(botonAnterior, posicion(0, 2, 40, 38));
        botonPagina1 = boton("1");
        panelPaginacion.add(botonPagina1, posicion(50, 2, 40, 38));
        botonPagina2 = boton("2");
        panelPaginacion.add(botonPagina2, posicion(100, 2, 40, 38));
        botonPagina3 = boton("3");
        panelPaginacion.add(botonPagina3, posicion(150, 2, 40, 38));
        botonSiguiente = boton("›");
        panelPaginacion.add(botonSiguiente, posicion(200, 2, 40, 38));
        panelTabla.add(panelPaginacion, posicion(1165, 328, 270, 42));
        add(panelTabla, posicion(60, 435, 1470, 390));

        panelResumen = panel();
        panelResumen.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        labelResumen = etiqueta("Resumen de caja", 17, true);
        panelResumen.add(labelResumen, posicion(28, 18, 180, 28));
        labelResumenSubtitulo = etiqueta("Totales del día", 13, false);
        panelResumen.add(labelResumenSubtitulo, posicion(28, 50, 180, 24));
        separadorResumen1 = separador(panelResumen, 225);
        efectivoTitulo = tituloResumen(panelResumen, "Efectivo", 250);
        labelEfectivo = valorResumen(panelResumen, 250);
        labelPorcentajeEfectivo = detalleResumen(panelResumen, "0% del total", 250);
        separadorResumen2 = separador(panelResumen, 530);
        tarjetaTitulo = tituloResumen(panelResumen, "Tarjeta", 555);
        labelTarjeta = valorResumen(panelResumen, 555);
        labelPorcentajeTarjeta = detalleResumen(panelResumen, "0% del total", 555);
        separadorResumen3 = separador(panelResumen, 835);
        otrosTitulo = tituloResumen(panelResumen, "Otros métodos", 860);
        labelOtros = valorResumen(panelResumen, 860);
        labelPorcentajeOtros = detalleResumen(panelResumen, "0% del total", 860);
        separadorResumen4 = separador(panelResumen, 1140);
        cancelacionTitulo = tituloResumen(panelResumen, "Cancelaciones", 1165);
        labelCancelacionesCaja = valorResumen(panelResumen, 1165);
        labelCancelacionesCaja.setText("0 pedidos");
        cancelacionDetalle = detalleResumen(
                panelResumen, "Pedidos cancelados", 1165);
        add(panelResumen, posicion(60, 840, 1470, 110));

        estadoCarga = etiqueta(
                "Selecciona una fecha y actualiza el reporte.", 12, false);
        add(estadoCarga, posicion(60, 952, 1450, 24));
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
    private javax.swing.JLabel labelCancelacionesCaja;
    private javax.swing.JLabel labelCancelados;
    private javax.swing.JLabel labelEfectivo;
    private javax.swing.JLabel labelOtros;
    private javax.swing.JLabel labelPedidos;
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
    private javax.swing.JLabel otrosTitulo;
    private Componentes.PanelFlotante panelCancelados;
    private Componentes.PanelFlotante panelFiltros;
    private Componentes.PanelCircular panelIconoCancelados;
    private Componentes.PanelCircular panelIconoPedidos;
    private Componentes.PanelCircular panelIconoTicket;
    private Componentes.PanelCircular panelIconoVentas;
    private Componentes.PanelFlotante panelPedidos;
    private javax.swing.JPanel panelPaginacion;
    private Componentes.PanelFlotante panelResumen;
    private Componentes.PanelFlotante panelTabla;
    private Componentes.PanelFlotante panelTicket;
    private Componentes.PanelFlotante panelVentas;
    private javax.swing.JScrollPane scrollPedidos;
    private javax.swing.JSeparator separadorResumen1;
    private javax.swing.JSeparator separadorResumen2;
    private javax.swing.JSeparator separadorResumen3;
    private javax.swing.JSeparator separadorResumen4;
    private Componentes.TablaAdministrativa tablaPedidos;
    private javax.swing.JLabel tarjetaTitulo;
    // End of variables declaration//GEN-END:variables
}
