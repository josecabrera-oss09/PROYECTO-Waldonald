package Utilidades;

/**
 * Mantiene los datos mínimos del usuario autenticado durante la ejecución.
 * Permite impedir que un administrador se desactive a sí mismo.
 */
public final class SesionUsuario {

    private static int idUsuario;
    private static String nombreCompleto = "Administrador";
    private static String rol = "ADMINISTRADOR";

    private SesionUsuario() {
    }

    public static void iniciar(
            int nuevoId,
            String nombre,
            String apellido,
            String nuevoRol) {
        idUsuario = nuevoId;
        nombreCompleto = (nombre + " " + apellido).trim();
        rol = nuevoRol;
    }

    public static void cerrar() {
        idUsuario = 0;
        nombreCompleto = "Administrador";
        rol = "ADMINISTRADOR";
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static String getNombreCompleto() {
        return nombreCompleto;
    }

    public static String getRol() {
        return rol;
    }
}
