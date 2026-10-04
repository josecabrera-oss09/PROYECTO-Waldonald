package Utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Funciones compartidas por el login y el mantenimiento de usuarios. */
public final class SeguridadContrasena {

    public static final int LONGITUD_MINIMA = 8;
    private static final int ITERACIONES = 600_000;
    private static final int TAMANO_SAL = 16;
    private static final int TAMANO_HASH = 32;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SeguridadContrasena() {
    }

    public static String validarPolitica(String contrasena, String usuario) {
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA) {
            return "La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres.";
        }
        if (contrasena.chars().noneMatch(Character::isUpperCase)) {
            return "La contraseña debe incluir al menos una letra mayúscula.";
        }
        if (contrasena.chars().noneMatch(Character::isLowerCase)) {
            return "La contraseña debe incluir al menos una letra minúscula.";
        }
        if (contrasena.chars().noneMatch(Character::isDigit)) {
            return "La contraseña debe incluir al menos un número.";
        }
        if (contrasena.chars().noneMatch(c -> !Character.isLetterOrDigit(c))) {
            return "La contraseña debe incluir al menos un carácter especial.";
        }
        return null;
    }

    public static String hash(String contrasena) {
        byte[] sal = new byte[TAMANO_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] derivado = derivar(contrasena.toCharArray(), sal, ITERACIONES);
        return "PBKDF2-SHA256$" + ITERACIONES + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(sal) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(derivado);
    }

    public static boolean verificar(String contrasena, String almacenada) {
        if (contrasena == null || almacenada == null || almacenada.isBlank()) {
            return false;
        }
        if (!almacenada.startsWith("PBKDF2-SHA256$")) {
            // Compatibilidad con cuentas antiguas; las nuevas nunca usan SHA-256 simple.
            return MessageDigest.isEqual(sha256(contrasena).getBytes(), almacenada.getBytes());
        }
        try {
            String[] partes = almacenada.split("\\$", -1);
            if (partes.length != 4) return false;
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] actual = derivar(contrasena.toCharArray(), sal, iteraciones);
            return MessageDigest.isEqual(actual, esperado);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] derivar(char[] contrasena, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(
                    contrasena, sal, iteraciones, TAMANO_HASH * 8);
            try {
                return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                        .generateSecret(especificacion).getEncoded();
            } finally {
                especificacion.clearPassword();
            }
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("PBKDF2 no está disponible", ex);
        }
    }

    /** Compatibilidad de lectura para hashes antiguos. */
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
