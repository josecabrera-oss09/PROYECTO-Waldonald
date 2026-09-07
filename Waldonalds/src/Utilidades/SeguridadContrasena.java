package Utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Funciones compartidas por el login y el mantenimiento de usuarios. */
public final class SeguridadContrasena {

    private SeguridadContrasena() {
    }

    /**
     * Conserva el SHA-256 que ya utilizaba el proyecto para que las cuentas
     * creadas desde el panel puedan iniciar sesión con el login actual.
     */
    public static String sha256(String contrasena) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    contrasena.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder(hash.length * 2);
            for (byte valor : hash) {
                resultado.append(String.format("%02x", valor));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException ex) {
            // SHA-256 forma parte obligatoria de Java; esta excepción indica
            // una instalación de Java dañada o no compatible.
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}
