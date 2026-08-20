package Main;

import Login.Login;

public class main {

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                Login login = new Login();

                login.setLocationRelativeTo(null);

                login.setVisible(true);
            }
        });
        
    }
}
