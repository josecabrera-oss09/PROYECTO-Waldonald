package Utilidades;

import Componentes.BotonRedondeado;
import java.awt.*;
import javax.swing.*;

/** Alertas modales con el mismo lenguaje visual del diálogo de cierre de sesión. */
public final class DialogosCajero {
    private static final Color AZUL = new Color(1, 20, 36);
    private static final Color AMARILLO = new Color(255, 188, 13);
    private DialogosCajero() { }

    public static void mensaje(Component padre, String titulo, String texto, int tipo) {
        mostrar(padre, titulo, texto, tipo, false);
    }

    public static boolean confirmar(Component padre, String titulo, String texto) {
        return mostrar(padre, titulo, texto, JOptionPane.QUESTION_MESSAGE, true);
    }

    private static boolean mostrar(Component padre, String titulo, String texto, int tipo, boolean pregunta) {
        Window propietario = padre == null ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(propietario, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        JPanel contenido = new JPanel(new BorderLayout(0, 18)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 233)),
                BorderFactory.createEmptyBorder(22, 24, 20, 24)));

        JPanel encabezado = new JPanel(new BorderLayout(14, 0));
        encabezado.setOpaque(false);
        JPanel icono = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tipo == JOptionPane.ERROR_MESSAGE ? new Color(255, 224, 226) : new Color(255, 244, 211));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icono.setOpaque(false);
        icono.setPreferredSize(new Dimension(52, 52));
        String recurso = tipo == JOptionPane.ERROR_MESSAGE ? "/Imagenes/x_icono.png" : "/Imagenes/pregunta.png";
        JLabel imagen = new JLabel(new ImageIcon(new ImageIcon(DialogosCajero.class.getResource(recurso))
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        icono.add(imagen);
        encabezado.add(icono, BorderLayout.WEST);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tituloLabel.setForeground(AZUL);
        JLabel subtitulo = new JLabel(pregunta ? "Confirma la acción para continuar" : "Información del sistema");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(105, 115, 128));
        textos.add(tituloLabel); textos.add(Box.createVerticalStrut(5)); textos.add(subtitulo);
        encabezado.add(textos, BorderLayout.CENTER);
        contenido.add(encabezado, BorderLayout.NORTH);

        JTextArea mensaje = new JTextArea(texto);
        mensaje.setWrapStyleWord(true); mensaje.setLineWrap(true);
        mensaje.setEditable(false); mensaje.setOpaque(false);
        mensaje.setFocusable(false);
        mensaje.setCaret(null);
        mensaje.setHighlighter(null);
        mensaje.setCursor(Cursor.getDefaultCursor());
        mensaje.setFont(new Font("Arial", Font.PLAIN, 14));
        mensaje.setForeground(new Color(45, 53, 63));
        contenido.add(mensaje, BorderLayout.CENTER);

        boolean[] aceptar = {false};
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        if (pregunta) {
            BotonRedondeado cancelar = boton("No", Color.WHITE, AZUL);
            cancelar.setColorBorde(new Color(190, 198, 208));
            cancelar.setGrosorBorde(1.2f);
            cancelar.addActionListener(e -> dialogo.dispose());
            acciones.add(cancelar);
        }
        BotonRedondeado aceptarBoton = boton(pregunta ? "Sí" : "Entendido", AMARILLO, AZUL);
        aceptarBoton.addActionListener(e -> { aceptar[0] = true; dialogo.dispose(); });
        acciones.add(aceptarBoton);
        contenido.add(acciones, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.getRootPane().setDefaultButton(aceptarBoton);
        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogo.pack();
        dialogo.setSize(Math.max(430, Math.min(560, dialogo.getWidth())), pregunta ? 260 : 240);
        dialogo.setLocationRelativeTo(propietario);
        dialogo.setVisible(true);
        return aceptar[0];
    }

    private static BotonRedondeado boton(String texto, Color fondo, Color tinta) {
        BotonRedondeado boton = new BotonRedondeado();
        boton.setText(texto); boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setForeground(tinta); boton.setColorInicio(fondo); boton.setDegradado(false);
        boton.setRadio(12); boton.setPreferredSize(new Dimension(138, 42));
        return boton;
    }
}
