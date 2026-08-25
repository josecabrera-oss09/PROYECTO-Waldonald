package Main;

import Vistas.PantallaCarga;
import Login.Login;
import javax.swing.Timer;

public class main {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                PantallaCarga carga = new PantallaCarga();
                carga.setLocationRelativeTo(null);
                carga.setVisible(true);

                // Esperar 3 segundos
                Timer timer = new Timer(8000, e -> {

                    // Cerrar pantalla de carga
                    carga.dispose();

                    // Abrir siguiente interfaz
                    Login login = new Login();
                    login.setLocationRelativeTo(null);
                    login.setVisible(true);
                });

                timer.setRepeats(false);
                timer.start();
            }

        });
    }
}
