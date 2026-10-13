package Utilidades;

import java.awt.Component;
import javax.swing.JOptionPane;

/** Diálogos modales compartidos por las pantallas del cajero. */
public final class DialogosCajero {

    private DialogosCajero() { }

    public static void mensaje(Component padre, String titulo, String texto,
            int tipo) {
        JOptionPane.showMessageDialog(padre, texto, titulo, tipo);
    }

    public static boolean confirmar(Component padre, String titulo,
            String texto) {
        Object[] opciones = {"Sí", "No"};
        int respuesta = JOptionPane.showOptionDialog(
                padre,
                texto,
                titulo,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[1]
        );
        return respuesta == JOptionPane.YES_OPTION;
    }
}
