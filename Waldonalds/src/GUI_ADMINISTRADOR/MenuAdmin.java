/*
 * JFrame Form editable desde el disenador visual de NetBeans.
 */
package GUI_ADMINISTRADOR;

import Componentes.BotonMenuLateral;
import Login.Login;
import Utilidades.TemaAdmin;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JPanel;
import GUI_ADMINISTRADOR.InicioAdminForm;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla contenedora del modulo administrativo.
 */
@SuppressWarnings({"serial", "this-escape"})
public class MenuAdmin extends javax.swing.JFrame {

    private static final String DASHBOARD = "dashboard";
    private static final String USUARIOS = "usuarios";
    private static final String GESTION_MENU = "gestionMenu";
    private static final String INGREDIENTES = "ingredientes";
    private static final String REPORTES = "reportes";
    private final Map<Component, Rectangle> posicionesContenidoOriginales
        = new HashMap<>();
    private final TemaAdmin tema;
    private CardLayout navegador;
    private BotonMenuLateral botonActivo;
    // ==========================================
// MENÚ LATERAL DESPLEGABLE
// ==========================================

private boolean menuLateralAbierto = true;

// 1.0 = completamente abierto
// 0.0 = completamente cerrado
private double aperturaMenu = 1.0;

private double aperturaInicial;
private double aperturaObjetivo;

private Timer timerMenu;
private long tiempoInicioAnimacion;

// Duración de la animación
private static final int DURACION_MENU = 280;

// Medidas después de aplicar el Escalador
private int anchoLateralAbierto;
private int anchoLateralCerrado;

// Bounds originales
private Rectangle boundsLogoAbierto;

private Rectangle boundsBotonDashboard;
private Rectangle boundsBotonUsuarios;
private Rectangle boundsBotonGestionMenu;
private Rectangle boundsBotonIngredientes;
private Rectangle boundsBotonReportes;
private Rectangle boundsBotonCerrarSesion;

private Rectangle boundsCabeceraAbierta;
private Rectangle boundsContenidoAbierto;
private Rectangle boundsSeparadorAbierto;

private Rectangle boundsIconoUsuario;
private Rectangle boundsBienvenido;
private Rectangle boundsUsuario;

   public MenuAdmin() {
    tema = new TemaAdmin();

    initComponents();

    aplicarTipografia();
    configurarSecciones();
    mostrarSeccion(DASHBOARD, botonDashboard);

    setLocationRelativeTo(null);

    // Tu escalador permanece exactamente igual
    Utilidades.Escalador.aplicar(this);

    // Configuramos el menú DESPUÉS de escalar
    configurarMenuDesplegable();
}
   private void configurarMenuDesplegable() {

    /*
     * Guardamos las dimensiones actuales.
     *
     * En este punto Escalador.aplicar(this) ya se ejecutó,
     * así que estas dimensiones ya están adaptadas
     * a la resolución de la computadora.
     */

    anchoLateralAbierto = panelLateral.getWidth();

    /*
     * Tu menú original mide 340.
     * Cerrado queremos que sea aproximadamente 90.
     *
     * Usamos una proporción para NO interferir
     * con tu Escalador.
     */
    anchoLateralCerrado =
            (int) Math.round(anchoLateralAbierto * (90.0 / 340.0));

    // Guardamos posiciones originales
    boundsLogoAbierto = new Rectangle(labelLogo.getBounds());

    boundsBotonDashboard =
            new Rectangle(botonDashboard.getBounds());

    boundsBotonUsuarios =
            new Rectangle(botonUsuarios.getBounds());

    boundsBotonGestionMenu =
            new Rectangle(botonGestionMenu.getBounds());

    boundsBotonIngredientes =
            new Rectangle(botonIngredientes.getBounds());

    boundsBotonReportes =
            new Rectangle(botonReportes.getBounds());

    boundsBotonCerrarSesion =
            new Rectangle(botonCerrarSesion.getBounds());

    boundsCabeceraAbierta =
            new Rectangle(panelCabecera.getBounds());

    boundsContenidoAbierto =
            new Rectangle(panelContenido.getBounds());

    boundsSeparadorAbierto =
            new Rectangle(separadorCabecera.getBounds());

    // Elementos que están pegados al lado derecho
    // de la cabecera.
    boundsIconoUsuario =
            new Rectangle(labelIconoUsuario.getBounds());

    boundsBienvenido =
            new Rectangle(labelBienvenido.getBounds());

    boundsUsuario =
            new Rectangle(labelUsuario.getBounds());


    // ==========================================
    // HACER CLICKEABLE EL ICONO ☰
    // ==========================================

    labelHamburguesa.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    );

    labelHamburguesa.setToolTipText(
            "Abrir / cerrar menú"
    );

    labelHamburguesa.addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            alternarMenuLateral();
        }
    });
    guardarPosicionesContenido();
}
   private void guardarPosicionesContenido() {

    posicionesContenidoOriginales.clear();

    for (Component tarjeta : panelContenido.getComponents()) {

        if (tarjeta instanceof JPanel panelSeccion) {

            for (Component componente : panelSeccion.getComponents()) {

                posicionesContenidoOriginales.put(
                        componente,
                        new Rectangle(componente.getBounds())
                );
            }
        }
    }
}
   private void centrarContenido(double apertura) {

    /*
     * Cuando está abierto:
     * apertura = 1
     * desplazamiento = 0
     *
     * Cuando está cerrado:
     * apertura = 0
     * desplazamiento = la mitad del espacio liberado
     */

    int espacioLiberado =
            anchoLateralAbierto - anchoLateralCerrado;

    int desplazamientoMaximo =
            espacioLiberado / 2;

    int desplazamientoActual =
            (int) Math.round(
                    desplazamientoMaximo * (1.0 - apertura)
            );


    for (Component seccion : panelContenido.getComponents()) {

        if (!(seccion instanceof JPanel panelSeccion)) {
            continue;
        }

        for (Component componente : panelSeccion.getComponents()) {

            Rectangle original =
                    posicionesContenidoOriginales.get(componente);

            if (original == null) {
                continue;
            }

            componente.setBounds(
                    original.x + desplazamientoActual,
                    original.y,
                    original.width,
                    original.height
            );
        }

        panelSeccion.revalidate();
        panelSeccion.repaint();
    }
}
   private void alternarMenuLateral() {

    // Cambiamos el estado deseado
    menuLateralAbierto = !menuLateralAbierto;

    // Partimos desde donde se encuentre actualmente.
    // Esto permite incluso pulsar el botón mientras
    // todavía se está animando.
    aperturaInicial = aperturaMenu;

    aperturaObjetivo =
            menuLateralAbierto ? 1.0 : 0.0;

    tiempoInicioAnimacion =
            System.currentTimeMillis();


    // Si ya existe una animación, la detenemos.
    if (timerMenu != null && timerMenu.isRunning()) {
        timerMenu.stop();
    }


    /*
     * Al cerrar quitamos inmediatamente los textos
     * grandes para evitar que se vean recortados.
     */
    if (!menuLateralAbierto) {
        ocultarTextosMenu();
    }


    // Aproximadamente 60 FPS
    timerMenu = new Timer(15, e -> {

        long tiempoActual =
                System.currentTimeMillis();

        double progreso =
                (double) (tiempoActual - tiempoInicioAnimacion)
                / DURACION_MENU;


        if (progreso >= 1.0) {
            progreso = 1.0;
        }


        /*
         * Easing cubic.
         *
         * Hace que la animación salga rápida
         * y termine suavemente, parecido al video.
         */
        double suavizado =
                1.0 - Math.pow(1.0 - progreso, 3);


        aperturaMenu =
                aperturaInicial
                + (aperturaObjetivo - aperturaInicial)
                * suavizado;


        aplicarAperturaMenu(aperturaMenu);


        if (progreso >= 1.0) {

            timerMenu.stop();

            aperturaMenu = aperturaObjetivo;

            aplicarAperturaMenu(aperturaMenu);


            if (menuLateralAbierto) {
                mostrarTextosMenu();
            }
        }
    });


    timerMenu.start();
}
   private void aplicarAperturaMenu(double apertura) {

    /*
     * apertura:
     *
     * 0.0 = cerrado
     * 1.0 = abierto
     */

    int anchoActual = interpolar(
            anchoLateralCerrado,
            anchoLateralAbierto,
            apertura
    );


    // ==========================================
    // PANEL LATERAL
    // ==========================================

    panelLateral.setBounds(
            0,
            0,
            anchoActual,
            panelLateral.getHeight()
    );


    // ==========================================
    // PANEL CABECERA
    // ==========================================

    int anchoCabecera =
            panelRaiz.getWidth() - anchoActual;

    panelCabecera.setBounds(
            anchoActual,
            boundsCabeceraAbierta.y,
            anchoCabecera,
            boundsCabeceraAbierta.height
    );


    // ==========================================
    // PANEL CONTENIDO
    // ==========================================

    int anchoContenido =
            panelRaiz.getWidth() - anchoActual;

    panelContenido.setBounds(
            anchoActual,
            boundsContenidoAbierto.y,
            anchoContenido,
            boundsContenidoAbierto.height
    );


    // ==========================================
    // SEPARADOR SUPERIOR
    // ==========================================

    separadorCabecera.setBounds(
            0,
            boundsSeparadorAbierto.y,
            anchoCabecera,
            boundsSeparadorAbierto.height
    );


    /*
     * Como la cabecera aumenta de ancho hacia
     * la izquierda, movemos el usuario hacia
     * la derecha para que permanezca en el mismo
     * lugar visual.
     */

    int diferenciaCabecera =
            anchoCabecera - boundsCabeceraAbierta.width;


    labelIconoUsuario.setLocation(
            boundsIconoUsuario.x + diferenciaCabecera,
            boundsIconoUsuario.y
    );

    labelBienvenido.setLocation(
            boundsBienvenido.x + diferenciaCabecera,
            boundsBienvenido.y
    );

    labelUsuario.setLocation(
            boundsUsuario.x + diferenciaCabecera,
            boundsUsuario.y
    );


    // ==========================================
    // LOGO
    // ==========================================

    int xLogoCerrado =
            (anchoLateralCerrado
            - boundsLogoAbierto.width) / 2;

    int xLogoActual = interpolar(
            xLogoCerrado,
            boundsLogoAbierto.x,
            apertura
    );

    labelLogo.setLocation(
            xLogoActual,
            boundsLogoAbierto.y
    );


    // ==========================================
    // BOTONES
    // ==========================================

    actualizarBotonCompacto(
            botonDashboard,
            boundsBotonDashboard,
            apertura
    );

    actualizarBotonCompacto(
            botonUsuarios,
            boundsBotonUsuarios,
            apertura
    );

    actualizarBotonCompacto(
            botonGestionMenu,
            boundsBotonGestionMenu,
            apertura
    );

    actualizarBotonCompacto(
            botonIngredientes,
            boundsBotonIngredientes,
            apertura
    );

    actualizarBotonCompacto(
            botonReportes,
            boundsBotonReportes,
            apertura
    );

    actualizarBotonCompacto(
            botonCerrarSesion,
            boundsBotonCerrarSesion,
            apertura
    );

    centrarContenido(apertura);
    panelLateral.revalidate();
    panelLateral.repaint();

    panelCabecera.revalidate();
    panelCabecera.repaint();

    panelContenido.revalidate();
    panelContenido.repaint();

    panelRaiz.revalidate();
    panelRaiz.repaint();
}
   private void actualizarBotonCompacto(
        BotonMenuLateral boton,
        Rectangle abierto,
        double apertura) {

    /*
     * Margen proporcional al tamaño que dejó
     * el Escalador.
     */
    int margenCerrado =
            Math.max(
                    5,
                    (int) Math.round(
                            anchoLateralAbierto
                            * (12.0 / 340.0)
                    )
            );


    int anchoBotonCerrado =
            anchoLateralCerrado
            - (margenCerrado * 2);


    int xActual = interpolar(
            margenCerrado,
            abierto.x,
            apertura
    );

    int anchoActual = interpolar(
            anchoBotonCerrado,
            abierto.width,
            apertura
    );


    boton.setBounds(
            xActual,
            abierto.y,
            anchoActual,
            abierto.height
    );
}
   private int interpolar(
        int cerrado,
        int abierto,
        double apertura) {

    return (int) Math.round(
            cerrado
            + (abierto - cerrado) * apertura
    );
}
   private void ocultarTextosMenu() {

    // Ocultamos nombre y rol
    labelMarca.setVisible(false);
    labelRol.setVisible(false);


    // Dejamos solamente los iconos
    botonDashboard.setText("");
    botonUsuarios.setText("");
    botonGestionMenu.setText("");
    botonIngredientes.setText("");
    botonReportes.setText("");
    botonCerrarSesion.setText("");


    // Tooltip para saber qué es cada botón
    botonDashboard.setToolTipText("Dashboard");

    botonUsuarios.setToolTipText(
            "Gestión de Usuarios"
    );

    botonGestionMenu.setToolTipText(
            "Gestión del Menú"
    );

    botonIngredientes.setToolTipText(
            "Ingredientes"
    );

    botonReportes.setToolTipText(
            "Reportes"
    );

    botonCerrarSesion.setToolTipText(
            "Volver Al Menú"
    );


    /*
     * Si BotonMenuLateral utiliza los iconos
     * normales de JButton, esto los centrará.
     */
    botonDashboard.setHorizontalAlignment(
            SwingConstants.CENTER
    );

    botonUsuarios.setHorizontalAlignment(
            SwingConstants.CENTER
    );

    botonGestionMenu.setHorizontalAlignment(
            SwingConstants.CENTER
    );

    botonIngredientes.setHorizontalAlignment(
            SwingConstants.CENTER
    );

    botonReportes.setHorizontalAlignment(
            SwingConstants.CENTER
    );

    botonCerrarSesion.setHorizontalAlignment(
            SwingConstants.CENTER
    );
}
   private void mostrarTextosMenu() {

    labelMarca.setVisible(true);
    labelRol.setVisible(true);


    botonDashboard.setText(
            "Dashboard"
    );

    botonUsuarios.setText(
            "Gestión de Usuarios"
    );

    botonGestionMenu.setText(
            "Gestión del Menú"
    );

    botonIngredientes.setText(
            "Ingredientes"
    );

    botonReportes.setText(
            "Reportes"
    );

    botonCerrarSesion.setText(
            "Volver Al Menú"
    );


    botonDashboard.setHorizontalAlignment(
            SwingConstants.LEFT
    );

    botonUsuarios.setHorizontalAlignment(
            SwingConstants.LEFT
    );

    botonGestionMenu.setHorizontalAlignment(
            SwingConstants.LEFT
    );

    botonIngredientes.setHorizontalAlignment(
            SwingConstants.LEFT
    );

    botonReportes.setHorizontalAlignment(
            SwingConstants.LEFT
    );

    botonCerrarSesion.setHorizontalAlignment(
            SwingConstants.LEFT
    );
}
   
    private void aplicarTipografia() {
        labelMarca.setFont(tema.negrita(28f));
        labelRol.setFont(tema.media(18f));
        labelTituloCabecera.setFont(tema.negrita(25f));
        labelBienvenido.setFont(tema.regular(16f));
        labelUsuario.setFont(tema.negrita(16f));
        botonDashboard.setFont(tema.negrita(17f));
        botonUsuarios.setFont(tema.media(17f));
        botonGestionMenu.setFont(tema.media(17f));
        botonIngredientes.setFont(tema.media(17f));
        botonReportes.setFont(tema.media(17f));
        botonCerrarSesion.setFont(tema.negrita(17f));
    }

    private void configurarSecciones() {
        navegador = new CardLayout();
        panelContenido.removeAll();
        panelContenido.setLayout(navegador);
        agregarSeccion(new DashboardPanel(), DASHBOARD);
        agregarSeccion(new UsuariosPanel(), USUARIOS);
        agregarSeccion(new GestionMenuPanel(), GESTION_MENU);
        agregarSeccion(new IngredientesPanel(), INGREDIENTES);
        agregarSeccion(new ReportesPanel(), REPORTES);
    }

    private void agregarSeccion(JPanel seccion, String nombre) {
        seccion.setBackground(new Color(248, 249, 251));
        panelContenido.add(seccion, nombre);
    }

    private void mostrarSeccion(String nombre, BotonMenuLateral botonSeleccionado) {
        if (botonActivo != null && botonActivo != botonSeleccionado) {
            botonActivo.setSeleccionado(false);
        }
        botonSeleccionado.setSeleccionado(true);
        botonActivo = botonSeleccionado;
        navegador.show(panelContenido, nombre);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void cerrarSesion() {
        InicioAdminForm inicio = new InicioAdminForm();
        inicio.setLocationRelativeTo(null);
        inicio.setVisible(true);
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRaiz = new javax.swing.JPanel();
        panelLateral = new javax.swing.JPanel();
        labelLogo = new Labels.LabelEscalable();
        labelMarca = new javax.swing.JLabel();
        labelRol = new javax.swing.JLabel();
        botonDashboard = new Componentes.BotonMenuLateral();
        botonUsuarios = new Componentes.BotonMenuLateral();
        botonGestionMenu = new Componentes.BotonMenuLateral();
        botonIngredientes = new Componentes.BotonMenuLateral();
        botonReportes = new Componentes.BotonMenuLateral();
        botonCerrarSesion = new Componentes.BotonMenuLateral();
        panelCabecera = new javax.swing.JPanel();
        labelHamburguesa = new javax.swing.JLabel();
        labelTituloCabecera = new javax.swing.JLabel();
        labelIconoUsuario = new Labels.LabelEscalable();
        labelBienvenido = new javax.swing.JLabel();
        labelUsuario = new javax.swing.JLabel();
        separadorCabecera = new javax.swing.JSeparator();
        panelContenido = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Panel de Administración");
        setMinimumSize(new java.awt.Dimension(1024, 650));
        setSize(new java.awt.Dimension(1920, 1080));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRaiz.setBackground(new java.awt.Color(248, 249, 251));
        panelRaiz.setPreferredSize(new java.awt.Dimension(1920, 1080));
        panelRaiz.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelLateral.setBackground(new java.awt.Color(1, 15, 30));
        panelLateral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/LogoW.png"))); // NOI18N
        labelLogo.setMantenerProporcion(true);
        panelLateral.add(labelLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 25, 68, 68));

        labelMarca.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        labelMarca.setForeground(new java.awt.Color(255, 255, 255));
        labelMarca.setText("Waldonald's");
        panelLateral.add(labelMarca, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 25, 205, 39));

        labelRol.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        labelRol.setForeground(new java.awt.Color(255, 190, 0));
        labelRol.setText("Administrador");
        panelLateral.add(labelRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 62, 190, 30));

        botonDashboard.setText("Dashboard");
        botonDashboard.setSeleccionado(true);
        botonDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDashboardActionPerformed(evt);
            }
        });
        panelLateral.add(botonDashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 145, 296, 64));

        botonUsuarios.setText("Gestión de Usuarios");
        botonUsuarios.setTipoIcono("USUARIOS");
        botonUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonUsuariosActionPerformed(evt);
            }
        });
        panelLateral.add(botonUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 230, 296, 64));

        botonGestionMenu.setText("Gestión del Menú");
        botonGestionMenu.setTipoIcono("MENU");
        botonGestionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGestionMenuActionPerformed(evt);
            }
        });
        panelLateral.add(botonGestionMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 315, 296, 64));

        botonIngredientes.setText("Ingredientes");
        botonIngredientes.setTipoIcono("INGREDIENTES");
        botonIngredientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIngredientesActionPerformed(evt);
            }
        });
        panelLateral.add(botonIngredientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 400, 296, 64));

        botonReportes.setText("Reportes");
        botonReportes.setTipoIcono("REPORTES");
        botonReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonReportesActionPerformed(evt);
            }
        });
        panelLateral.add(botonReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 485, 296, 64));

        botonCerrarSesion.setText("Volver Al Menú");
        botonCerrarSesion.setMostrarBorde(true);
        botonCerrarSesion.setTipoIcono("SALIR");
        botonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarSesionActionPerformed(evt);
            }
        });
        panelLateral.add(botonCerrarSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 985, 296, 62));

        panelRaiz.add(panelLateral, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 1080));

        panelCabecera.setBackground(new java.awt.Color(255, 255, 255));
        panelCabecera.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        panelCabecera.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelHamburguesa.setFont(new java.awt.Font("Dialog", 0, 32)); // NOI18N
        labelHamburguesa.setForeground(new java.awt.Color(0, 20, 43));
        labelHamburguesa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelHamburguesa.setText("☰");
        panelCabecera.add(labelHamburguesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 21, 55, 56));

        labelTituloCabecera.setFont(new java.awt.Font("Dialog", 1, 25)); // NOI18N
        labelTituloCabecera.setForeground(new java.awt.Color(0, 20, 43));
        labelTituloCabecera.setText("PANEL DE ADMINISTRACION");
        panelCabecera.add(labelTituloCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 25, 510, 50));

        labelIconoUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/IconoUsuario.png"))); // NOI18N
        labelIconoUsuario.setMantenerProporcion(true);
        panelCabecera.add(labelIconoUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 22, 56, 56));

        labelBienvenido.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        labelBienvenido.setForeground(new java.awt.Color(18, 37, 63));
        labelBienvenido.setText("Bienvenido,");
        panelCabecera.add(labelBienvenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 32, 105, 38));

        labelUsuario.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        labelUsuario.setForeground(new java.awt.Color(18, 37, 63));
        labelUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelUsuario.setText("Administrador");
        labelUsuario.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        panelCabecera.add(labelUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1440, 15, 123, 50));

        separadorCabecera.setForeground(new java.awt.Color(225, 229, 235));
        panelCabecera.add(separadorCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 99, 1580, 1));

        panelRaiz.add(panelCabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 1580, 100));

        panelContenido.setBackground(new java.awt.Color(248, 249, 251));
        panelContenido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelRaiz.add(panelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 1580, 980));

        getContentPane().add(panelRaiz, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 1080));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDashboardActionPerformed
        mostrarSeccion(DASHBOARD, botonDashboard);
    }//GEN-LAST:event_botonDashboardActionPerformed

    private void botonUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonUsuariosActionPerformed
        mostrarSeccion(USUARIOS, botonUsuarios);
    }//GEN-LAST:event_botonUsuariosActionPerformed

    private void botonGestionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGestionMenuActionPerformed
        mostrarSeccion(GESTION_MENU, botonGestionMenu);
    }//GEN-LAST:event_botonGestionMenuActionPerformed

    private void botonIngredientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIngredientesActionPerformed
        mostrarSeccion(INGREDIENTES, botonIngredientes);
    }//GEN-LAST:event_botonIngredientesActionPerformed

    private void botonReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonReportesActionPerformed
        mostrarSeccion(REPORTES, botonReportes);
    }//GEN-LAST:event_botonReportesActionPerformed

    private void botonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarSesionActionPerformed
        cerrarSesion();
    }//GEN-LAST:event_botonCerrarSesionActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MenuAdmin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Componentes.BotonMenuLateral botonCerrarSesion;
    private Componentes.BotonMenuLateral botonDashboard;
    private Componentes.BotonMenuLateral botonGestionMenu;
    private Componentes.BotonMenuLateral botonIngredientes;
    private Componentes.BotonMenuLateral botonReportes;
    private Componentes.BotonMenuLateral botonUsuarios;
    private javax.swing.JLabel labelBienvenido;
    private javax.swing.JLabel labelHamburguesa;
    private Labels.LabelEscalable labelIconoUsuario;
    private Labels.LabelEscalable labelLogo;
    private javax.swing.JLabel labelMarca;
    private javax.swing.JLabel labelRol;
    private javax.swing.JLabel labelTituloCabecera;
    private javax.swing.JLabel labelUsuario;
    private javax.swing.JPanel panelCabecera;
    private javax.swing.JPanel panelContenido;
    private javax.swing.JPanel panelLateral;
    private javax.swing.JPanel panelRaiz;
    private javax.swing.JSeparator separadorCabecera;
    // End of variables declaration//GEN-END:variables
}
