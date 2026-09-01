package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/waldonalds?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USUARIO = "root";

    private static final String CONTRASENA = "123456789";

    public static Connection conectar() {

        try {

            Connection conexion = DriverManager.getConnection(
                    URL,
                    USUARIO,
                    CONTRASENA
            );

            System.out.println("Conexion exitosa con MySQL");
            return conexion;

        } catch (SQLException e) {

            System.out.println("Error al conectar con MySQL:");
            System.out.println(e.getMessage());

            return null;
        }
    }

    public static void main(String[] args) {

        Connection conexion = conectar();

        if (conexion != null) {
            System.out.println("Conexion con Waldonalds realizada correctamente.");
        } else {
            System.out.println("No se pudo conectar.");
        }
    }
}

