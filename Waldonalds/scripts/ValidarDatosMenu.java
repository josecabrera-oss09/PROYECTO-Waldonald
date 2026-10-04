import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Ejecuta el esquema y los datos del menu en una base temporal. */
public final class ValidarDatosMenu {
    private static final String URL =
            "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456789";

    private ValidarDatosMenu() {
    }

    public static void main(String[] args) throws Exception {
        String base = "waldonalds_validacion_"
                + UUID.randomUUID().toString().replace("-", "");
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("CREATE DATABASE `" + base + "`");
            try {
                sentencia.execute("USE `" + base + "`");
                ejecutarArchivo(sentencia, Path.of("sql/base_datos_completa.sql"));
                ejecutarArchivo(sentencia, Path.of("sql/datos_menu_nueva_base.sql"));

                comprobar(sentencia, "productos", 171,
                        "SELECT COUNT(*) FROM producto");
                comprobarCero(sentencia, "productos RECETA sin ingredientes",
                        "SELECT COUNT(*) FROM producto p "
                        + "WHERE p.tipo_stock='RECETA' AND NOT EXISTS ("
                        + "SELECT 1 FROM producto_ingrediente pi "
                        + "WHERE pi.id_producto=p.id_producto AND pi.estado=TRUE)");
                comprobarCero(sentencia, "productos activos sin uso comercial",
                        "SELECT COUNT(*) FROM producto p WHERE p.estado=TRUE "
                        + "AND NOT EXISTS (SELECT 1 FROM presentacion_menu pm "
                        + "WHERE pm.id_producto_principal=p.id_producto AND pm.estado=TRUE) "
                        + "AND NOT EXISTS (SELECT 1 FROM opcion_componente oc "
                        + "WHERE oc.id_producto=p.id_producto)");
                comprobar(sentencia, "productos auxiliares ocultos del catalogo", 6,
                        "SELECT COUNT(*) FROM producto p WHERE p.nombre IN ("
                        + "'Papas Kids','Puré de manzana','Yogur de fresa',"
                        + "'Jugo de manzana Kids','Juguete sorpresa','Coca-Cola 1.5 L') "
                        + "AND NOT EXISTS (SELECT 1 FROM presentacion_menu pm "
                        + "WHERE pm.id_producto_principal=p.id_producto AND pm.estado=TRUE) "
                        + "AND EXISTS (SELECT 1 FROM opcion_componente oc "
                        + "WHERE oc.id_producto=p.id_producto)");
                comprobarCero(sentencia, "grupos activos sin opciones",
                        "SELECT COUNT(*) FROM grupo_presentacion gp WHERE gp.estado=TRUE "
                        + "AND NOT EXISTS (SELECT 1 FROM opcion_grupo og "
                        + "WHERE og.id_grupo=gp.id_grupo AND og.estado=TRUE)");
                comprobarCero(sentencia, "opciones activas sin componentes",
                        "SELECT COUNT(*) FROM opcion_grupo og WHERE og.estado=TRUE "
                        + "AND NOT EXISTS (SELECT 1 FROM opcion_componente oc "
                        + "WHERE oc.id_opcion=og.id_opcion)");
                comprobar(sentencia, "presentaciones Big Mac", 2,
                        "SELECT COUNT(*) FROM presentacion_menu pm "
                        + "JOIN producto p ON p.id_producto=pm.id_producto_principal "
                        + "WHERE p.nombre='Big Mac' AND pm.tipo IN ('INDIVIDUAL','MENU')");
                comprobar(sentencia, "presentacion infantil de Cajita", 1,
                        "SELECT COUNT(*) FROM presentacion_menu pm "
                        + "JOIN producto p ON p.id_producto=pm.id_producto_principal "
                        + "WHERE p.nombre='Cajita Feliz de Hamburguesa' AND pm.tipo='INFANTIL'");
                comprobar(sentencia, "grupo de cuatro principales de Caja Grande", 1,
                        "SELECT COUNT(*) FROM grupo_presentacion gp "
                        + "JOIN presentacion_menu pm ON pm.id_presentacion=gp.id_presentacion "
                        + "JOIN producto p ON p.id_producto=pm.id_producto_principal "
                        + "WHERE p.nombre='Caja Grande' AND gp.minimo=4 "
                        + "AND gp.maximo=4 AND gp.permite_repetir=TRUE");

                int[] antes = cantidadesConfiguracion(sentencia);
                ejecutarArchivo(sentencia, Path.of("sql/datos_menu_nueva_base.sql"));
                int[] despues = cantidadesConfiguracion(sentencia);
                if (!java.util.Arrays.equals(antes, despues)) {
                    throw new AssertionError("la segunda carga duplicó datos: antes="
                            + java.util.Arrays.toString(antes) + ", despues="
                            + java.util.Arrays.toString(despues));
                }
                System.out.println("OK: ejecutar el archivo dos veces no duplica datos");

                imprimirResumen(sentencia);
                imprimirRecetasCortas(sentencia);
                System.out.println("VALIDACION COMPLETA: todos los datos son compatibles con el esquema nuevo.");
            } finally {
                sentencia.execute("DROP DATABASE IF EXISTS `" + base + "`");
            }
        }
    }

    private static void ejecutarArchivo(Statement sentencia, Path ruta) throws Exception {
        String contenido = Files.readString(ruta, StandardCharsets.UTF_8);
        int ejecutadas = 0;
        for (String sql : separarSentencias(contenido)) {
            String limpia = sql.trim();
            String mayuscula = limpia.toUpperCase();
            if (limpia.isEmpty() || mayuscula.startsWith("CREATE DATABASE")
                    || mayuscula.startsWith("USE ")) {
                continue;
            }
            sentencia.execute(limpia);
            ejecutadas++;
        }
        System.out.println(ruta + ": " + ejecutadas + " sentencias ejecutadas");
    }

    private static List<String> separarSentencias(String contenido) {
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean comillaSimple = false;
        boolean comillaDoble = false;
        boolean acento = false;
        boolean comentarioLinea = false;
        boolean comentarioBloque = false;

        for (int i = 0; i < contenido.length(); i++) {
            char c = contenido.charAt(i);
            char siguiente = i + 1 < contenido.length() ? contenido.charAt(i + 1) : '\0';

            if (comentarioLinea) {
                if (c == '\n') {
                    comentarioLinea = false;
                    actual.append(c);
                }
                continue;
            }
            if (comentarioBloque) {
                if (c == '*' && siguiente == '/') {
                    comentarioBloque = false;
                    i++;
                }
                continue;
            }
            if (!comillaSimple && !comillaDoble && !acento) {
                if (c == '-' && siguiente == '-') {
                    comentarioLinea = true;
                    i++;
                    continue;
                }
                if (c == '/' && siguiente == '*') {
                    comentarioBloque = true;
                    i++;
                    continue;
                }
            }

            if (c == '\'' && !comillaDoble && !acento) {
                if (comillaSimple && siguiente == '\'') {
                    actual.append(c).append(siguiente);
                    i++;
                    continue;
                }
                if (i == 0 || contenido.charAt(i - 1) != '\\') {
                    comillaSimple = !comillaSimple;
                }
            } else if (c == '"' && !comillaSimple && !acento
                    && (i == 0 || contenido.charAt(i - 1) != '\\')) {
                comillaDoble = !comillaDoble;
            } else if (c == '`' && !comillaSimple && !comillaDoble) {
                acento = !acento;
            }

            if (c == ';' && !comillaSimple && !comillaDoble && !acento) {
                sentencias.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (!actual.toString().isBlank()) {
            sentencias.add(actual.toString());
        }
        return sentencias;
    }

    private static int numero(Statement sentencia, String sql) throws Exception {
        try (ResultSet resultado = sentencia.executeQuery(sql)) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    private static void comprobar(Statement sentencia, String nombre,
            int esperado, String sql) throws Exception {
        int obtenido = numero(sentencia, sql);
        if (obtenido != esperado) {
            throw new AssertionError(nombre + ": esperado=" + esperado + ", obtenido=" + obtenido);
        }
        System.out.println("OK: " + nombre + " = " + obtenido);
    }

    private static void comprobarCero(Statement sentencia, String nombre,
            String sql) throws Exception {
        comprobar(sentencia, nombre, 0, sql);
    }

    private static void imprimirResumen(Statement sentencia) throws Exception {
        String sql = "SELECT tipo_stock, COUNT(*) FROM producto GROUP BY tipo_stock ORDER BY tipo_stock";
        try (ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                System.out.println("RESUMEN: " + resultado.getString(1)
                        + " = " + resultado.getInt(2));
            }
        }
    }

    private static int[] cantidadesConfiguracion(Statement sentencia) throws Exception {
        String[] tablas = {"producto", "ingrediente", "producto_ingrediente",
            "presentacion_menu", "grupo_presentacion", "opcion_grupo",
            "opcion_componente"};
        int[] cantidades = new int[tablas.length];
        for (int i = 0; i < tablas.length; i++) {
            cantidades[i] = numero(sentencia, "SELECT COUNT(*) FROM " + tablas[i]);
        }
        return cantidades;
    }

    private static void imprimirRecetasCortas(Statement sentencia) throws Exception {
        String sql = "SELECT p.nombre, COUNT(pi.id_producto_ingrediente) cantidad "
                + "FROM producto p JOIN producto_ingrediente pi ON pi.id_producto=p.id_producto "
                + "WHERE p.tipo_stock='RECETA' AND pi.estado=TRUE "
                + "GROUP BY p.id_producto,p.nombre HAVING cantidad<=2 ORDER BY p.nombre";
        try (ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                System.out.println("AUDITAR RECETA CORTA: " + resultado.getString(1)
                        + " (" + resultado.getInt(2) + " ingredientes)");
            }
        }
    }
}
