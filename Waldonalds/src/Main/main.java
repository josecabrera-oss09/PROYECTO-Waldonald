package Main;

import Login.Login;
import Vistas.PantallaCarga;
import javax.swing.Timer;

public class main {

    // Cambia este valor para aumentar o reducir la duración de la carga.
    private static final int TIEMPO_CARGA_MS = 3000;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            PantallaCarga pantallaCarga = new PantallaCarga();
            pantallaCarga.setVisible(true);

            Timer temporizadorInicio = new Timer(TIEMPO_CARGA_MS, e -> {
                pantallaCarga.cerrar();

                Login login = new Login();
                login.setLocationRelativeTo(null);
                login.setVisible(true);
            });
            temporizadorInicio.setRepeats(false);
            temporizadorInicio.start();
        });
    }
}
