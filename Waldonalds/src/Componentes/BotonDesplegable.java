package Componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanProperty;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * Botón desplegable reutilizable y editable desde Properties de NetBeans.
 * Las opciones se escriben separadas por punto y coma.
 */
@SuppressWarnings({"serial", "this-escape"})
public class BotonDesplegable extends JButton {

    private String textoDesplegable = "Mi perfil;Cerrar sesión";
    private Color colorFondo = new Color(0, 0, 0, 0);
    private Color colorHover = new Color(255, 255, 255, 28);
    private Color colorDesplegado = new Color(255, 255, 255, 42);
    private Color colorFondoMenu = Color.WHITE;
    private Color colorTextoOpcion = new Color(24, 31, 42);
    private Color colorHoverOpcion = new Color(255, 244, 210);
    private Color colorTextoHoverOpcion = new Color(13, 17, 23);
    private Color colorBordeMenu = new Color(226, 231, 238);
    private int radio = 18;
    private int anchoMenu = 230;
    private int altoOpcion = 48;
    private int separacionMenu = 6;
    private boolean mouseEncima;
    private boolean desplegado;
    private Color colorActual = colorFondo;
    private String opcionSeleccionada = "";
    private int indiceOpcionSeleccionada = -1;

    private final Timer animacion;
    private final JPopupMenu menuDesplegable;
    private final EventListenerList escuchasOpciones = new EventListenerList();

    public BotonDesplegable() {
        setText("Administrador");
        setForeground(Color.WHITE);
        setBorder(new EmptyBorder(0, 18, 0, 42));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setHorizontalAlignment(LEFT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        menuDesplegable = new JPopupMenu();
        configurarAspectoMenu();
        reconstruirOpciones();
        menuDesplegable.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent evento) {
                setDesplegado(true);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evento) {
                setDesplegado(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent evento) {
                setDesplegado(false);
            }
        });

        animacion = new Timer(15, evento -> actualizarAnimacion());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                mouseEncima = true;
                iniciarAnimacion();
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                mouseEncima = false;
                iniciarAnimacion();
            }
        });
        addActionListener(evento -> alternarMenu());
    }

    @BeanProperty(preferred = true,
            description = "Opciones del menú separadas por punto y coma. Usa - para insertar un separador.")
    public String getTextoDesplegable() {
        return textoDesplegable;
    }

    public void setTextoDesplegable(String textoDesplegable) {
        String anterior = this.textoDesplegable;
        this.textoDesplegable = textoDesplegable != null ? textoDesplegable : "";
        firePropertyChange("textoDesplegable", anterior, this.textoDesplegable);
        reconstruirOpciones();
    }

    @BeanProperty(preferred = true, description = "Color normal del botón.")
    public Color getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(Color colorFondo) {
        Color anterior = this.colorFondo;
        this.colorFondo = colorSeguro(colorFondo);
        if (!mouseEncima && !desplegado) {
            colorActual = this.colorFondo;
        }
        firePropertyChange("colorFondo", anterior, this.colorFondo);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color del botón al pasar el mouse.")
    public Color getColorHover() {
        return colorHover;
    }

    public void setColorHover(Color colorHover) {
        Color anterior = this.colorHover;
        this.colorHover = colorSeguro(colorHover);
        firePropertyChange("colorHover", anterior, this.colorHover);
        iniciarAnimacion();
    }

    @BeanProperty(description = "Color del botón mientras el menú está abierto.")
    public Color getColorDesplegado() {
        return colorDesplegado;
    }

    public void setColorDesplegado(Color colorDesplegado) {
        Color anterior = this.colorDesplegado;
        this.colorDesplegado = colorSeguro(colorDesplegado);
        firePropertyChange("colorDesplegado", anterior, this.colorDesplegado);
        iniciarAnimacion();
    }

    @BeanProperty(preferred = true, description = "Color de fondo del menú desplegable.")
    public Color getColorFondoMenu() {
        return colorFondoMenu;
    }

    public void setColorFondoMenu(Color colorFondoMenu) {
        Color anterior = this.colorFondoMenu;
        this.colorFondoMenu = colorFondoMenu != null ? colorFondoMenu : Color.WHITE;
        firePropertyChange("colorFondoMenu", anterior, this.colorFondoMenu);
        configurarAspectoMenu();
        reconstruirOpciones();
    }

    @BeanProperty(description = "Color normal del texto de las opciones.")
    public Color getColorTextoOpcion() {
        return colorTextoOpcion;
    }

    public void setColorTextoOpcion(Color colorTextoOpcion) {
        Color anterior = this.colorTextoOpcion;
        this.colorTextoOpcion = colorTextoOpcion != null ? colorTextoOpcion : Color.BLACK;
        firePropertyChange("colorTextoOpcion", anterior, this.colorTextoOpcion);
        reconstruirOpciones();
    }

    @BeanProperty(preferred = true, description = "Color de las opciones al pasar el mouse.")
    public Color getColorHoverOpcion() {
        return colorHoverOpcion;
    }

    public void setColorHoverOpcion(Color colorHoverOpcion) {
        Color anterior = this.colorHoverOpcion;
        this.colorHoverOpcion = colorHoverOpcion != null ? colorHoverOpcion : Color.LIGHT_GRAY;
        firePropertyChange("colorHoverOpcion", anterior, this.colorHoverOpcion);
        reconstruirOpciones();
    }

    @BeanProperty(description = "Color del texto de una opción durante el hover.")
    public Color getColorTextoHoverOpcion() {
        return colorTextoHoverOpcion;
    }

    public void setColorTextoHoverOpcion(Color colorTextoHoverOpcion) {
        Color anterior = this.colorTextoHoverOpcion;
        this.colorTextoHoverOpcion = colorTextoHoverOpcion != null
                ? colorTextoHoverOpcion : Color.BLACK;
        firePropertyChange("colorTextoHoverOpcion", anterior, this.colorTextoHoverOpcion);
        reconstruirOpciones();
    }

    @BeanProperty(description = "Color del borde del menú desplegable.")
    public Color getColorBordeMenu() {
        return colorBordeMenu;
    }

    public void setColorBordeMenu(Color colorBordeMenu) {
        Color anterior = this.colorBordeMenu;
        this.colorBordeMenu = colorSeguro(colorBordeMenu);
        firePropertyChange("colorBordeMenu", anterior, this.colorBordeMenu);
        configurarAspectoMenu();
    }

    @BeanProperty(preferred = true, description = "Radio de las esquinas del botón.")
    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        int anterior = this.radio;
        this.radio = Math.max(0, radio);
        firePropertyChange("radio", anterior, this.radio);
        repaint();
    }


    @BeanProperty(description = "Ancho del menú desplegable.")
    public int getAnchoMenu() {
        return anchoMenu;
    }

    public void setAnchoMenu(int anchoMenu) {
        int anterior = this.anchoMenu;
        this.anchoMenu = Math.max(80, anchoMenu);
        firePropertyChange("anchoMenu", anterior, this.anchoMenu);
        reconstruirOpciones();
    }

    @BeanProperty(description = "Altura de cada opción del menú.")
    public int getAltoOpcion() {
        return altoOpcion;
    }

    public void setAltoOpcion(int altoOpcion) {
        int anterior = this.altoOpcion;
        this.altoOpcion = Math.max(24, altoOpcion);
        firePropertyChange("altoOpcion", anterior, this.altoOpcion);
        reconstruirOpciones();
    }

    @BeanProperty(description = "Separación entre el botón y el menú.")
    public int getSeparacionMenu() {
        return separacionMenu;
    }

    public void setSeparacionMenu(int separacionMenu) {
        int anterior = this.separacionMenu;
        this.separacionMenu = Math.max(0, separacionMenu);
        firePropertyChange("separacionMenu", anterior, this.separacionMenu);
    }

    @BeanProperty(hidden = true)
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    @BeanProperty(hidden = true)
    public int getIndiceOpcionSeleccionada() {
        return indiceOpcionSeleccionada;
    }

    @Override
    public void setFont(Font fuente) {
        super.setFont(fuente);
        reconstruirOpciones();
    }

    public void addMenuOpcionListener(ActionListener escucha) {
        escuchasOpciones.add(ActionListener.class, escucha);
    }

    public void removeMenuOpcionListener(ActionListener escucha) {
        escuchasOpciones.remove(ActionListener.class, escucha);
    }

    private void configurarAspectoMenu() {
        if (menuDesplegable == null) {
            return;
        }
        menuDesplegable.setOpaque(true);
        menuDesplegable.setBackground(colorFondoMenu);
        menuDesplegable.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorBordeMenu, 1, true),
                new EmptyBorder(7, 7, 7, 7)
        ));
    }

    private void reconstruirOpciones() {
        if (menuDesplegable == null) {
            return;
        }
        menuDesplegable.removeAll();
        int indice = 0;
        for (String valor : textoDesplegable.split(";")) {
            String textoOpcion = valor.trim();
            if (textoOpcion.isEmpty()) {
                continue;
            }
            if ("-".equals(textoOpcion)) {
                menuDesplegable.addSeparator();
                continue;
            }
            menuDesplegable.add(crearOpcion(textoOpcion, indice));
            indice++;
        }
        menuDesplegable.revalidate();
        menuDesplegable.repaint();
    }

    private JMenuItem crearOpcion(String textoOpcion, int indice) {
        JMenuItem opcion = new JMenuItem(textoOpcion);
        opcion.setFont(getFont());
        opcion.setForeground(colorTextoOpcion);
        opcion.setBackground(colorFondoMenu);
        opcion.setOpaque(true);
        opcion.setBorder(new EmptyBorder(0, 16, 0, 16));
        opcion.setPreferredSize(new Dimension(anchoMenu, altoOpcion));
        opcion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        opcion.setUI(new BasicMenuItemUI() {
            {
                selectionBackground = colorHoverOpcion;
                selectionForeground = colorTextoHoverOpcion;
            }
        });
        opcion.addActionListener(evento -> seleccionarOpcion(textoOpcion, indice));
        return opcion;
    }

    private void seleccionarOpcion(String opcion, int indice) {
        String opcionAnterior = opcionSeleccionada;
        int indiceAnterior = indiceOpcionSeleccionada;
        opcionSeleccionada = opcion;
        indiceOpcionSeleccionada = indice;
        firePropertyChange("opcionSeleccionada", opcionAnterior, opcionSeleccionada);
        firePropertyChange("indiceOpcionSeleccionada", indiceAnterior,
                indiceOpcionSeleccionada);

        ActionEvent evento = new ActionEvent(
                this,
                ActionEvent.ACTION_PERFORMED,
                opcionSeleccionada
        );
        for (ActionListener escucha
                : escuchasOpciones.getListeners(ActionListener.class)) {
            escucha.actionPerformed(evento);
        }
    }

    private void alternarMenu() {
        if (menuDesplegable.isVisible()) {
            menuDesplegable.setVisible(false);
            return;
        }
        if (!isShowing() || menuDesplegable.getComponentCount() == 0) {
            return;
        }
        int posicionX = getWidth() - anchoMenu - 14;
        menuDesplegable.show(this, posicionX, getHeight() + separacionMenu);
    }

    private void setDesplegado(boolean desplegado) {
        this.desplegado = desplegado;
        iniciarAnimacion();
        repaint();
    }

    private void iniciarAnimacion() {
        if (animacion != null && !animacion.isRunning()) {
            animacion.start();
        }
    }

    private void actualizarAnimacion() {
        Color objetivo = desplegado
                ? colorDesplegado
                : mouseEncima ? colorHover : colorFondo;
        colorActual = mezclar(colorActual, objetivo, 0.24f);
        if (coloresCercanos(colorActual, objetivo)) {
            colorActual = objetivo;
            animacion.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);
        if (colorActual.getAlpha() > 0) {
            g2.setColor(colorActual);
            g2.fillRoundRect(0, 2, Math.max(1, getWidth() - 1),
                    Math.max(1, getHeight() - 4), radio, radio);
        }
        g2.dispose();

        super.paintComponent(graphics);
        pintarFlecha(graphics);
    }

    private void pintarFlecha(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        aplicarCalidad(g2);
        g2.setColor(isEnabled() ? getForeground() : getForeground().darker());
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        int centroX = getWidth() - 23;
        int centroY = getHeight() / 2;
        int direccion = desplegado ? -1 : 1;
        g2.drawLine(centroX - 5, centroY - 2 * direccion,
                centroX, centroY + 3 * direccion);
        g2.drawLine(centroX, centroY + 3 * direccion,
                centroX + 5, centroY - 2 * direccion);
        g2.dispose();
    }

    private static void aplicarCalidad(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
    }

    private static Color colorSeguro(Color color) {
        return color != null ? color : new Color(0, 0, 0, 0);
    }

    private static Color mezclar(Color origen, Color destino, float proporcion) {
        float inversa = 1f - proporcion;
        return new Color(
                Math.round(origen.getRed() * inversa + destino.getRed() * proporcion),
                Math.round(origen.getGreen() * inversa + destino.getGreen() * proporcion),
                Math.round(origen.getBlue() * inversa + destino.getBlue() * proporcion),
                Math.round(origen.getAlpha() * inversa + destino.getAlpha() * proporcion)
        );
    }

    private static boolean coloresCercanos(Color primero, Color segundo) {
        return Math.abs(primero.getRed() - segundo.getRed()) <= 2
                && Math.abs(primero.getGreen() - segundo.getGreen()) <= 2
                && Math.abs(primero.getBlue() - segundo.getBlue()) <= 2
                && Math.abs(primero.getAlpha() - segundo.getAlpha()) <= 2;
    }
}
