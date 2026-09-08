/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI_ADMINISTRADOR;

import CRUD.UsuarioCRUD;
import Componentes.BotonDesplegable;
import Componentes.BotonRedondeado;
import Modelos.PaginaUsuarios;
import Modelos.ResumenUsuarios;
import Modelos.Usuario;
import Utilidades.IconosUsuarios;
import Utilidades.SesionUsuario;
import Utilidades.TemaAdmin;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Humberto Alvarado
 */
public class UsuariosPanel extends javax.swing.JPanel {

    private static final int USUARIOS_POR_PAGINA = 10;
    private static final Color AZUL = new Color(0, 20, 43);
    private static final Color SECUNDARIO = new Color(92, 103, 124);
    private static final Color BORDE = new Color(225, 229, 235);
    private static final Color AMARILLO = new Color(255, 188, 0);
    private static final Color ROJO = new Color(231, 55, 65);
    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TemaAdmin tema = new TemaAdmin();
    private final UsuarioCRUD crud = new UsuarioCRUD();
    private final ModeloTablaUsuarios modeloTabla = new ModeloTablaUsuarios();
    private final Timer temporizadorBusqueda;

    private final List<BotonRedondeado> botonesPagina = new ArrayList<>();

    private String rolSeleccionado;
    private Boolean estadoSeleccionado;
    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int secuenciaCarga;
    private boolean mostrandoErrorConexion;

    /**
     * Creates new form UsuariosPanel
     */
    public UsuariosPanel() {
        initComponents();

        // Agrupa pulsaciones rápidas: la consulta se ejecuta 300 ms después
        // de la última tecla, no una vez por cada carácter.
        temporizadorBusqueda = new Timer(300, evento -> {
            paginaActual = 1;
            cargarPagina();
        });
        temporizadorBusqueda.setRepeats(false);

        configurarElementosExistentes();
        configurarFiltrosVisuales();
        configurarTabla();
        configurarEventos();
        cargarResumen();
        cargarPagina();
    }

    /** Corrige las tarjetas que estaban copiadas del panel de ingredientes. */
    private void configurarElementosExistentes() {
        botonDerretido1.addActionListener(evento -> abrirFormulario(null));
        botonExportar.addActionListener(evento -> exportarCsv());
    }


    /** Configura los controles que ya existen visualmente en el .form. */
    private void configurarFiltrosVisuales() {
        filtroRol.addMenuOpcionListener(evento -> {
            String opcion = evento.getActionCommand();
            rolSeleccionado = "Todos los roles".equals(opcion)
                    ? null : opcion;
            filtroRol.setText(opcion);
            paginaActual = 1;
            cargarPagina();
        });

        filtroEstado.addMenuOpcionListener(evento -> {
            String opcion = evento.getActionCommand();
            estadoSeleccionado = switch (opcion) {
                case "Activo" -> Boolean.TRUE;
                case "Inactivo" -> Boolean.FALSE;
                default -> null;
            };
            filtroEstado.setText(opcion);
            paginaActual = 1;
            cargarPagina();
        });

        botonLimpiarFiltros.addActionListener(
                evento -> limpiarFiltros());
    }

    /** Aplica comportamiento a la JTable dibujada en UsuariosPanel.form. */
    private void configurarTabla() {
        tablaUsuarios.setModel(modeloTabla);

        JTableHeader cabecera = tablaUsuarios.getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setBackground(Color.WHITE);
        cabecera.setForeground(AZUL);
        cabecera.setFont(tema.negrita(12f));
        cabecera.setPreferredSize(new Dimension(0, 42));
        cabecera.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, BORDE));
        cabecera.setDefaultRenderer(new RenderCabecera());

        tablaUsuarios.setDefaultRenderer(
                Object.class, new RenderTextoTabla());
        tablaUsuarios.getColumnModel().getColumn(5)
                .setCellRenderer(new RenderEstado());
        tablaUsuarios.getColumnModel().getColumn(7)
                .setCellRenderer(new RenderAcciones());
        tablaUsuarios.getColumnModel().getColumn(7)
                .setCellEditor(new EditorAcciones());
        configurarAnchosColumnas();

        scrollUsuarios.getViewport().setBackground(Color.WHITE);
        botonAnterior.addActionListener(evento -> cambiarPagina(
                paginaActual - 1));

        botonesPagina.add(botonPagina1);
        botonesPagina.add(botonPagina2);
        botonesPagina.add(botonPagina3);
        for (int indice = 0; indice < botonesPagina.size(); indice++) {
            BotonRedondeado boton = botonesPagina.get(indice);
            final int posicion = indice;
            boton.addActionListener(evento -> {
                Object valor = botonesPagina.get(posicion)
                        .getClientProperty("pagina");
                if (valor instanceof Integer pagina) {
                    cambiarPagina(pagina);
                }
            });
        }

        botonSiguiente.addActionListener(evento -> cambiarPagina(
                paginaActual + 1));
    }

    private void configurarAnchosColumnas() {
        int[] anchos = {78, 180, 135, 145, 285, 105, 175, 118};
        for (int indice = 0; indice < anchos.length; indice++) {
            tablaUsuarios.getColumnModel().getColumn(indice)
                    .setPreferredWidth(anchos[indice]);
        }
    }

    private void configurarEventos() {
        campoBusqueda.getDocument()
                .addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                temporizadorBusqueda.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                temporizadorBusqueda.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                temporizadorBusqueda.restart();
            }
        });
    }

    /** Ejecuta la consulta fuera del hilo gráfico para no congelar Swing. */
    private void cargarPagina() {
        final int solicitud = ++secuenciaCarga;
        final String busqueda = campoBusqueda.getText();
        final String rol = rolSeleccionado;
        final Boolean estado = estadoSeleccionado;
        final int paginaSolicitada = paginaActual;

        tablaUsuarios.setEnabled(false);
        etiquetaRango.setText("Cargando usuarios...");

        new SwingWorker<PaginaUsuarios, Void>() {
            @Override
            protected PaginaUsuarios doInBackground() throws Exception {
                return crud.listarPagina(
                        busqueda, rol, estado,
                        paginaSolicitada, USUARIOS_POR_PAGINA);
            }

            @Override
            protected void done() {
                if (solicitud != secuenciaCarga) {
                    return; // Ignora respuestas anteriores a un filtro nuevo.
                }
                try {
                    PaginaUsuarios resultado = get();
                    totalPaginas = Math.max(1, (int) Math.ceil(
                            resultado.totalRegistros()
                                    / (double) USUARIOS_POR_PAGINA));
                    if (paginaActual > totalPaginas) {
                        paginaActual = totalPaginas;
                        cargarPagina();
                        return;
                    }

                    modeloTabla.setUsuarios(resultado.usuarios());
                    actualizarPaginacion(resultado.totalRegistros());
                    mostrandoErrorConexion = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarErrorDatos(ex.getCause());
                    modeloTabla.setUsuarios(List.of());
                    actualizarPaginacion(0);
                } finally {
                    tablaUsuarios.setEnabled(true);
                }
            }
        }.execute();
    }

    private void cargarResumen() {
        new SwingWorker<ResumenUsuarios, Void>() {
            @Override
            protected ResumenUsuarios doInBackground() throws Exception {
                return crud.obtenerResumen();
            }

            @Override
            protected void done() {
                try {
                    ResumenUsuarios resumen = get();
                    labelTitulo5.setText(String.valueOf(resumen.total()));
                    labelTitulo8.setText(String.valueOf(
                            resumen.administradoresActivos()));
                    labelTitulo14.setText(String.valueOf(
                            resumen.cajerosActivos()));
                    labelTitulo11.setText(String.valueOf(
                            resumen.inactivos()));
                    mostrandoErrorConexion = false;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    mostrarErrorDatos(ex.getCause());
                }
            }
        }.execute();
    }

    private void actualizarPaginacion(int totalRegistros) {
        if (totalRegistros == 0) {
            etiquetaRango.setText("No se encontraron usuarios");
        } else {
            int primero = (paginaActual - 1) * USUARIOS_POR_PAGINA + 1;
            int ultimo = Math.min(
                    primero + USUARIOS_POR_PAGINA - 1, totalRegistros);
            etiquetaRango.setText(String.format(
                    "Mostrando %d–%d de %d usuarios",
                    primero, ultimo, totalRegistros));
        }

        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        int inicio = Math.max(1,
                Math.min(paginaActual - 1, totalPaginas - 2));

        for (int indice = 0; indice < botonesPagina.size(); indice++) {
            int pagina = inicio + indice;
            BotonRedondeado boton = botonesPagina.get(indice);
            boolean visible = pagina <= totalPaginas;
            boton.setVisible(visible);
            if (!visible) {
                continue;
            }
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
        if (pagina < 1 || pagina > totalPaginas || pagina == paginaActual) {
            return;
        }
        paginaActual = pagina;
        cargarPagina();
    }

    private void limpiarFiltros() {
        campoBusqueda.setText("");
        temporizadorBusqueda.stop();
        rolSeleccionado = null;
        estadoSeleccionado = null;
        filtroRol.setText("Todos los roles");
        filtroEstado.setText("Todos los estados");
        paginaActual = 1;
        cargarPagina();
    }

    private void abrirFormulario(Usuario usuario) {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        JRootPane raiz = SwingUtilities.getRootPane(this);
        Component cristalAnterior = raiz != null ? raiz.getGlassPane() : null;
        boolean cristalVisible = cristalAnterior != null
                && cristalAnterior.isVisible();

        if (raiz != null) {
            FondoOscuro fondo = new FondoOscuro();
            raiz.setGlassPane(fondo);
            fondo.setVisible(true);
        }

        boolean modificado;
        try {
            modificado = UsuarioFormDialog.mostrar(
                    ventana, crud, usuario);
        } finally {
            if (raiz != null && cristalAnterior != null) {
                raiz.setGlassPane(cristalAnterior);
                cristalAnterior.setVisible(cristalVisible);
            }
        }

        if (modificado) {
            cargarResumen();
            cargarPagina();
            JOptionPane.showMessageDialog(
                    this,
                    usuario == null
                            ? "Usuario agregado correctamente."
                            : "Usuario actualizado correctamente.",
                    "Gestión de usuarios",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void desactivarUsuario(Usuario usuario) {
        if (!usuario.isActivo()) {
            return;
        }
        if (usuario.getIdUsuario() == SesionUsuario.getIdUsuario()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No puedes desactivar tu propia cuenta.",
                    "Acción no permitida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if ("ADMINISTRADOR".equals(usuario.getRol())
                    && crud.contarAdministradoresActivos() <= 1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe existir al menos un administrador activo.",
                        "Acción no permitida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desactivar a " + usuario.getNombreCompleto()
                            + "?\nYa no podrá iniciar sesión, "
                            + "pero sus pedidos se conservarán.",
                    "Confirmar desactivación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (respuesta == JOptionPane.YES_OPTION) {
                crud.desactivar(usuario.getIdUsuario());
                cargarResumen();
                cargarPagina();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible desactivar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Exporta todos los resultados del filtro, no sólo la página visible. */
    private void exportarCsv() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Exportar usuarios");
        selector.setSelectedFile(new java.io.File("usuarios.csv"));
        selector.setFileFilter(new FileNameExtensionFilter(
                "Archivo CSV (*.csv)", "csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destino = selector.getSelectedFile().toPath();
        if (!destino.getFileName().toString().toLowerCase()
                .endsWith(".csv")) {
            destino = destino.resolveSibling(
                    destino.getFileName().toString() + ".csv");
        }

        final Path archivo = destino;
        final String busqueda = campoBusqueda.getText();
        final String rol = rolSeleccionado;
        final Boolean estado = estadoSeleccionado;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                List<Usuario> usuarios = crud.listarParaExportar(
                        busqueda, rol, estado);
                escribirCsv(archivo, usuarios);
                return usuarios.size();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    int cantidad = get();
                    JOptionPane.showMessageDialog(
                            UsuariosPanel.this,
                            "Se exportaron " + cantidad + " usuarios en:\n"
                                    + archivo,
                            "Exportación completada",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(
                            UsuariosPanel.this,
                            "No fue posible exportar: "
                                    + ex.getCause().getMessage(),
                            "Error de exportación",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void escribirCsv(Path archivo, List<Usuario> usuarios)
            throws Exception {
        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivo, StandardCharsets.UTF_8)) {
            escritor.write('\ufeff'); // BOM: conserva tildes al abrir en Excel.
            escritor.write(
                    "ID,Nombre,Apellido,Usuario,Correo,Rol,Estado,Fecha creación");
            escritor.newLine();
            for (Usuario usuario : usuarios) {
                escritor.write(String.join(",",
                        csv(String.valueOf(usuario.getIdUsuario())),
                        csv(usuario.getNombre()),
                        csv(usuario.getApellido()),
                        csv(usuario.getNombreUsuario()),
                        csv(usuario.getCorreo()),
                        csv(usuario.getRol()),
                        csv(usuario.isActivo() ? "Activo" : "Inactivo"),
                        csv(usuario.getFechaCreacion() == null ? ""
                                : usuario.getFechaCreacion()
                                        .format(FORMATO_FECHA))));
                escritor.newLine();
            }
        }
    }

    private String csv(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    private void mostrarErrorDatos(Throwable causa) {
        if (mostrandoErrorConexion) {
            return;
        }
        mostrandoErrorConexion = true;
        String mensaje = causa == null
                ? "Error desconocido."
                : causa.getMessage();
        JOptionPane.showMessageDialog(
                this,
                "No fue posible cargar los usuarios.\n" + mensaje,
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void removeNotify() {
        temporizadorBusqueda.stop();
        super.removeNotify();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitulo2 = new javax.swing.JLabel();
        labelTitulo3 = new javax.swing.JLabel();
        botonDerretido1 = new Componentes.BotonDerretido();
        panelFlotante1 = new Componentes.PanelFlotante();
        panelCircular2 = new Componentes.PanelCircular();
        labelEscalable2 = new Labels.LabelEscalable();
        labelTitulo4 = new javax.swing.JLabel();
        labelTitulo5 = new javax.swing.JLabel();
        panelFlotante2 = new Componentes.PanelFlotante();
        panelCircular3 = new Componentes.PanelCircular();
        labelEscalable3 = new Labels.LabelEscalable();
        labelTitulo7 = new javax.swing.JLabel();
        labelTitulo8 = new javax.swing.JLabel();
        panelFlotante4 = new Componentes.PanelFlotante();
        panelCircular5 = new Componentes.PanelCircular();
        labelEscalable5 = new Labels.LabelEscalable();
        labelTitulo13 = new javax.swing.JLabel();
        labelTitulo14 = new javax.swing.JLabel();
        panelFlotante3 = new Componentes.PanelFlotante();
        panelCircular4 = new Componentes.PanelCircular();
        labelEscalable4 = new Labels.LabelEscalable();
        labelTitulo10 = new javax.swing.JLabel();
        labelTitulo11 = new javax.swing.JLabel();
        botonExportar = new Componentes.BotonRedondeado();
        panelFiltros = new Componentes.PanelFlotante();
        campoBusqueda = new Componentes.CampoBusquedaAdmin();
        filtroRol = new Componentes.BotonDesplegable();
        filtroEstado = new Componentes.BotonDesplegable();
        botonLimpiarFiltros = new Componentes.BotonRedondeado();
        panelTabla = new Componentes.PanelFlotante();
        scrollUsuarios = new javax.swing.JScrollPane();
        tablaUsuarios = new javax.swing.JTable();
        etiquetaRango = new javax.swing.JLabel();
        panelPaginacion = new javax.swing.JPanel();
        botonAnterior = new Componentes.BotonRedondeado();
        botonPagina1 = new Componentes.BotonRedondeado();
        botonPagina2 = new Componentes.BotonRedondeado();
        botonPagina3 = new Componentes.BotonRedondeado();
        botonSiguiente = new Componentes.BotonRedondeado();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitulo2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        labelTitulo2.setForeground(new java.awt.Color(127, 137, 154));
        labelTitulo2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo2.setText("Administra los usuarios del sistema");
        add(labelTitulo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 320, 76));

        labelTitulo3.setFont(new java.awt.Font("Dialog", 1, 55)); // NOI18N
        labelTitulo3.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo3.setText("Gestión de Usuarios");
        add(labelTitulo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 540, 76));

        botonDerretido1.setForeground(new java.awt.Color(0, 0, 0));
        botonDerretido1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_agregar.png"))); // NOI18N
        botonDerretido1.setText("Agregar usuario");
        botonDerretido1.setIconTextGap(15);
        add(botonDerretido1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 60, 230, 80));

        panelFlotante1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_usuariostotal.png"))); // NOI18N
        panelCircular2.add(labelEscalable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 20, 70, 50));

        panelFlotante1.add(panelCircular2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo4.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo4.setText("Total de usuarios");
        panelFlotante1.add(labelTitulo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelTitulo5.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo5.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo5.setText("48");
        panelFlotante1.add(labelTitulo5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        add(panelFlotante1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, 360, 150));

        panelFlotante2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular3.setColorFondo(new java.awt.Color(252, 233, 233));
        panelCircular3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_admin.png"))); // NOI18N
        panelCircular3.add(labelEscalable3, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 15, 65, 65));

        panelFlotante2.add(panelCircular3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo7.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo7.setText("Administradores");
        panelFlotante2.add(labelTitulo7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelTitulo8.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo8.setForeground(new java.awt.Color(195, 61, 66));
        labelTitulo8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo8.setText("48");
        panelFlotante2.add(labelTitulo8, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        add(panelFlotante2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 150, 360, 150));

        panelFlotante4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_usuario.png"))); // NOI18N
        panelCircular5.add(labelEscalable5, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 9, 75, 75));

        panelFlotante4.add(panelCircular5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 80, 90));

        labelTitulo13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTitulo13.setForeground(new java.awt.Color(92, 103, 124));
        labelTitulo13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo13.setText("Cajeros");
        panelFlotante4.add(labelTitulo13, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 0, 650, 76));

        labelTitulo14.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        labelTitulo14.setForeground(new java.awt.Color(13, 17, 23));
        labelTitulo14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTitulo14.setText("48");
        panelFlotante4.add(labelTitulo14, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 90, 76));

        add(panelFlotante4, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 150, 360, 150));

        panelFlotante3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCircular4.setColorFondo(new java.awt.Color(252, 233, 233));
        panelCircular4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelEscalable4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/icono_usuario_inactivo.png"))); // NOI18N
        panelCircular4.add(labelEscalable4, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 12, 70, 70));

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

        add(panelFlotante3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 150, 360, 150));

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

        campoBusqueda.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        campoBusqueda.setPlaceholder("Buscar usuario, nombre o correo...");
        panelFiltros.add(campoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 22, 455, 56));

        filtroRol.setColorFondo(new java.awt.Color(255, 255, 255));
        filtroRol.setColorHover(new java.awt.Color(248, 249, 251));
        filtroRol.setForeground(new java.awt.Color(0, 20, 43));
        filtroRol.setText("Todos los roles");
        filtroRol.setTextoDesplegable("Todos los roles;ADMINISTRADOR;CAJERO");
        filtroRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroRolActionPerformed(evt);
            }
        });
        panelFiltros.add(filtroRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 24, 330, 52));

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

        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaUsuarios.setRowHeight(40);
        scrollUsuarios.setViewportView(tablaUsuarios);

        panelTabla.add(scrollUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 1442, 458));

        etiquetaRango.setForeground(new java.awt.Color(92, 103, 124));
        etiquetaRango.setText("Mostrando 1–10 de 10 usuarios");
        panelTabla.add(etiquetaRango, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 482, 330, 42));

        panelPaginacion.setOpaque(false);
        panelPaginacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonAnterior.setText("‹");
        botonAnterior.setColorInicio(new java.awt.Color(255, 255, 255));
        botonAnterior.setDegradado(false);
        panelPaginacion.add(botonAnterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 40, 38));

        botonPagina1.setText("1");
        botonPagina1.setColorInicio(new java.awt.Color(255, 188, 0));
        botonPagina1.setDegradado(false);
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

    private void filtroRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroRolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroRolActionPerformed

    /** Modelo no editable salvo por la celda que contiene las acciones. */
    private final class ModeloTablaUsuarios extends AbstractTableModel {

        private final String[] columnas = {
            "ID", "Nombre", "Usuario", "Rol", "Correo",
            "Estado", "Fecha creación", "Acciones"
        };
        private List<Usuario> usuarios = List.of();

        private void setUsuarios(List<Usuario> nuevosUsuarios) {
            usuarios = List.copyOf(nuevosUsuarios);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return usuarios.size();
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
        public Object getValueAt(int fila, int columna) {
            Usuario usuario = usuarios.get(fila);
            return switch (columna) {
                case 0 -> String.format("#%04d", usuario.getIdUsuario());
                case 1 -> usuario.getNombreCompleto();
                case 2 -> usuario.getNombreUsuario();
                case 3 -> usuario.getRol();
                case 4 -> usuario.getCorreo();
                case 5, 7 -> usuario;
                case 6 -> usuario.getFechaCreacion() == null
                        ? "" : usuario.getFechaCreacion().format(FORMATO_FECHA);
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == 5 || columna == 7
                    ? Usuario.class : String.class;
        }

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return columna == 7;
        }
    }

    private final class RenderCabecera extends DefaultTableCellRenderer {

        private RenderCabecera() {
            setOpaque(true);
            setBackground(Color.WHITE);
            setForeground(AZUL);
            setFont(tema.negrita(12f));
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionado,
                boolean foco,
                int fila,
                int columna) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    tabla, valor, seleccionado, foco, fila, columna);
            etiqueta.setHorizontalAlignment(
                    columna == 0 || columna >= 5
                            ? SwingConstants.CENTER : SwingConstants.LEFT);
            etiqueta.setBackground(Color.WHITE);
            etiqueta.setForeground(AZUL);
            etiqueta.setBorder(new EmptyBorder(0, 12, 0, 12));
            return etiqueta;
        }
    }

    private final class RenderTextoTabla extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionado,
                boolean foco,
                int fila,
                int columna) {
            JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    tabla, valor, seleccionado, foco, fila, columna);
            etiqueta.setBorder(new EmptyBorder(0, 12, 0, 12));
            etiqueta.setForeground(AZUL);
            etiqueta.setFont(tema.regular(12f));
            etiqueta.setHorizontalAlignment(
                    columna == 0 || columna == 3 || columna == 6
                            ? SwingConstants.CENTER : SwingConstants.LEFT);
            return etiqueta;
        }
    }

    /** Dibuja Activo/Inactivo como una insignia, no como texto plano. */
    private final class RenderEstado implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionado,
                boolean foco,
                int fila,
                int columna) {
            Usuario usuario = (Usuario) valor;
            JPanel panel = new JPanel(new FlowLayout(
                    FlowLayout.CENTER, 0, 7));
            panel.setBackground(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);

            JLabel estado = new JLabel(
                    usuario.isActivo() ? "Activo" : "Inactivo");
            estado.setHorizontalAlignment(SwingConstants.CENTER);
            estado.setFont(tema.media(11f));
            estado.setOpaque(true);
            estado.setForeground(usuario.isActivo()
                    ? new Color(34, 139, 71) : SECUNDARIO);
            estado.setBackground(usuario.isActivo()
                    ? new Color(230, 245, 234)
                    : new Color(237, 239, 243));
            estado.setBorder(new EmptyBorder(3, 13, 3, 13));
            panel.add(estado);
            return panel;
        }
    }

    private final class RenderAcciones implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionado,
                boolean foco,
                int fila,
                int columna) {
            Usuario usuario = (Usuario) valor;
            JPanel panel = crearPanelAcciones(seleccionado
                    ? tabla.getSelectionBackground() : Color.WHITE);
            BotonRedondeado editar = crearBotonAccion(
                    IconosUsuarios.Tipo.EDITAR,
                    new Color(214, 151, 0));
            BotonRedondeado eliminar = crearBotonAccion(
                    IconosUsuarios.Tipo.ELIMINAR, ROJO);
            eliminar.setEnabled(usuario.isActivo());
            panel.add(editar);
            panel.add(eliminar);
            return panel;
        }
    }

    /**
     * El renderer sólo dibuja. Este editor es el que convierte los iconos en
     * botones reales y llama las operaciones de la fila correspondiente.
     */
    private final class EditorAcciones extends AbstractCellEditor
            implements TableCellEditor {

        private final JPanel panel = crearPanelAcciones(Color.WHITE);
        private final BotonRedondeado editar = crearBotonAccion(
                IconosUsuarios.Tipo.EDITAR, new Color(214, 151, 0));
        private final BotonRedondeado eliminar = crearBotonAccion(
                IconosUsuarios.Tipo.ELIMINAR, ROJO);
        private Usuario usuario;

        private EditorAcciones() {
            editar.addActionListener(this::editar);
            eliminar.addActionListener(this::eliminar);
            panel.add(editar);
            panel.add(eliminar);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable tabla,
                Object valor,
                boolean seleccionado,
                int fila,
                int columna) {
            usuario = (Usuario) valor;
            panel.setBackground(tabla.getSelectionBackground());
            eliminar.setEnabled(usuario.isActivo());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return usuario;
        }

        private void editar(ActionEvent evento) {
            Usuario seleccionado = usuario;
            fireEditingStopped();
            abrirFormulario(seleccionado);
        }

        private void eliminar(ActionEvent evento) {
            Usuario seleccionado = usuario;
            fireEditingStopped();
            desactivarUsuario(seleccionado);
        }
    }

    private JPanel crearPanelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 7, 4));
        panel.setBackground(fondo);
        return panel;
    }

    private BotonRedondeado crearBotonAccion(
            IconosUsuarios.Tipo icono,
            Color color) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText("");
        boton.setIcon(IconosUsuarios.crear(icono, color, 17));
        boton.setDegradado(false);
        boton.setColorInicio(Color.WHITE);
        boton.setColorFinal(Color.WHITE);
        boton.setColorBorde(color);
        boton.setGrosorBorde(1f);
        boton.setRadio(9);
        boton.setPreferredSize(new Dimension(33, 30));
        boton.setToolTipText(icono == IconosUsuarios.Tipo.EDITAR
                ? "Editar usuario" : "Desactivar usuario");
        return boton;
    }

    /** Capa temporal que oscurece el panel mientras el JDialog está abierto. */
    private static final class FondoOscuro extends JPanel {

        private FondoOscuro() {
            setOpaque(false);
            // Consume clics para que el fondo no pueda modificarse.
            addMouseListener(new java.awt.event.MouseAdapter() {
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setColor(new Color(0, 12, 28, 90));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonRedondeado botonAnterior;
    private Componentes.BotonDerretido botonDerretido1;
    private Componentes.BotonRedondeado botonExportar;
    private Componentes.BotonRedondeado botonLimpiarFiltros;
    private Componentes.BotonRedondeado botonPagina1;
    private Componentes.BotonRedondeado botonPagina2;
    private Componentes.BotonRedondeado botonPagina3;
    private Componentes.BotonRedondeado botonSiguiente;
    private Componentes.CampoBusquedaAdmin campoBusqueda;
    private javax.swing.JLabel etiquetaRango;
    private Componentes.BotonDesplegable filtroEstado;
    private Componentes.BotonDesplegable filtroRol;
    private Labels.LabelEscalable labelEscalable2;
    private Labels.LabelEscalable labelEscalable3;
    private Labels.LabelEscalable labelEscalable4;
    private Labels.LabelEscalable labelEscalable5;
    private javax.swing.JLabel labelTitulo10;
    private javax.swing.JLabel labelTitulo11;
    private javax.swing.JLabel labelTitulo13;
    private javax.swing.JLabel labelTitulo14;
    private javax.swing.JLabel labelTitulo2;
    private javax.swing.JLabel labelTitulo3;
    private javax.swing.JLabel labelTitulo4;
    private javax.swing.JLabel labelTitulo5;
    private javax.swing.JLabel labelTitulo7;
    private javax.swing.JLabel labelTitulo8;
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
    private javax.swing.JTable tablaUsuarios;
    // End of variables declaration//GEN-END:variables
}
