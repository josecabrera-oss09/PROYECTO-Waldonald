package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String HOST = valor("waldonalds.db.host", "WALDONALDS_DB_HOST", "127.0.0.1");
    private static final String PUERTO = valor("waldonalds.db.port", "WALDONALDS_DB_PORT", "3306");
    private static final String BASE = valor("waldonalds.db.name", "WALDONALDS_DB_NAME", "waldonalds");
    private static final String USUARIO = valor("waldonalds.db.user", "WALDONALDS_DB_USER", "root");
    private static final String CONTRASENA = valor("waldonalds.db.password", "WALDONALDS_DB_PASSWORD", "123456789");

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public static Connection conectar() {

        try {

            Connection conexion = DriverManager.getConnection(
                    URL,
                    USUARIO,
                    CONTRASENA
            );

            try (Statement s = conexion.createStatement();
                    ResultSet r = s.executeQuery(
                            "SELECT DATABASE(), @@port, "
                            + "(SELECT COUNT(*) FROM ingrediente)")) {
                if (r.next()) {
                    System.out.println("Conexion exitosa con MySQL: base=" + r.getString(1)
                            + ", puerto=" + r.getInt(2)
                            + ", ingredientes=" + r.getInt(3));
                }
            }
            return conexion;

        } catch (SQLException e) {

            System.out.println("Error al conectar con MySQL:");
            System.out.println(e.getMessage());

            return null;
        }
    }

    private static String valor(String propiedad, String variable, String defecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null || valor.isBlank()) valor = System.getenv(variable);
        return valor == null || valor.isBlank() ? defecto : valor.trim();
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

