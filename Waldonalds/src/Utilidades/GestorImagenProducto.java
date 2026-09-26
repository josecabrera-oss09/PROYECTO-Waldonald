package Utilidades;

import java.awt.Image;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.UUID;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;

public final class GestorImagenProducto {

    /*
     * Carpeta que se encuentra junto a /src.
     */
    private static final String CARPETA =
            "imagenes_productos";

    /*
     * Las imágenes agregadas desde el programa
     * comenzarán con este prefijo.
     *
     * Así evitamos eliminar accidentalmente imágenes
     * originales que tú pusiste manualmente.
     */
    private static final String PREFIJO_GESTIONADA =
            "prod_";

    private static final long TAMANO_MAXIMO =
            10L * 1024L * 1024L;

    private GestorImagenProducto() {
    }

    // ============================================================
    // CARPETA
    // ============================================================

    public static Path obtenerCarpeta()
            throws IOException {

        Path carpeta =
                Path.of(
                        System.getProperty(
                                "user.dir"
                        ),
                        CARPETA
                );

        Files.createDirectories(
                carpeta
        );

        return carpeta;
    }

    // ============================================================
    // GUARDAR
    // ============================================================

    public static String guardarImagen(
            File archivoOrigen)
            throws IOException {

        if (archivoOrigen == null) {

            throw new IOException(
                    "No se seleccionó ninguna imagen."
            );
        }

        validarImagen(
                archivoOrigen
        );

        String extension =
                obtenerExtension(
                        archivoOrigen
                                .getName()
                );

        String nuevoNombre =
                PREFIJO_GESTIONADA
                + UUID.randomUUID()
                + "."
                + extension;

        Path destino =
                obtenerCarpeta()
                        .resolve(
                                nuevoNombre
                        );

        Files.copy(
                archivoOrigen.toPath(),
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        /*
         * IMPORTANTE:
         *
         * En MySQL NO se guarda C:\Users\...
         *
         * Se guarda:
         *
         * imagenes_productos/prod_xxx.png
         */
        return CARPETA
                + "/"
                + nuevoNombre;
    }

    // ============================================================
    // CARGAR DESDE RUTA GUARDADA
    // ============================================================

    public static ImageIcon cargarIcono(
            String ruta,
            int ancho,
            int alto) {

        if (ruta == null
                || ruta.isBlank()) {

            return null;
        }

        try {

            Path rutaArchivo =
                    resolverRuta(
                            ruta
                    );

            if (!Files.exists(
                    rutaArchivo
            )) {

                return null;
            }

            ImageIcon original =
                    new ImageIcon(
                            rutaArchivo.toString()
                    );

            if (original.getIconWidth()
                    <= 0) {

                return null;
            }

            return escalar(
                    original,
                    ancho,
                    alto
            );

        } catch (Exception ex) {

            return null;
        }
    }

    // ============================================================
    // CARGAR ARCHIVO SELECCIONADO
    // ============================================================

    public static ImageIcon cargarIcono(
            File archivo,
            int ancho,
            int alto) {

        if (archivo == null
                || !archivo.exists()) {

            return null;
        }

        ImageIcon original =
                new ImageIcon(
                        archivo.getAbsolutePath()
                );

        if (original.getIconWidth()
                <= 0) {

            return null;
        }

        return escalar(
                original,
                ancho,
                alto
        );
    }

    // ============================================================
    // ESCALAR CONSERVANDO PROPORCIÓN
    // ============================================================

    private static ImageIcon escalar(
            ImageIcon original,
            int anchoMaximo,
            int altoMaximo) {

        int anchoOriginal =
                original.getIconWidth();

        int altoOriginal =
                original.getIconHeight();

        double escala =
                Math.min(
                        anchoMaximo
                                / (double) anchoOriginal,
                        altoMaximo
                                / (double) altoOriginal
                );

        escala =
                Math.min(
                        escala,
                        1.0
                );

        int nuevoAncho =
                Math.max(
                        1,
                        (int) Math.round(
                                anchoOriginal
                                * escala
                        )
                );

        int nuevoAlto =
                Math.max(
                        1,
                        (int) Math.round(
                                altoOriginal
                                * escala
                        )
                );

        Image imagen =
                original.getImage()
                        .getScaledInstance(
                                nuevoAncho,
                                nuevoAlto,
                                Image.SCALE_SMOOTH
                        );

        return new ImageIcon(
                imagen
        );
    }

    // ============================================================
    // ELIMINAR IMAGEN CREADA POR EL PROGRAMA
    // ============================================================

    public static void eliminarImagenGestionada(
            String ruta) {

        if (ruta == null
                || ruta.isBlank()) {

            return;
        }

        try {

            Path archivo =
                    resolverRuta(
                            ruta
                    );

            String nombre =
                    archivo
                            .getFileName()
                            .toString();

            /*
             * Solo borramos imágenes creadas
             * mediante guardarImagen().
             *
             * NO eliminamos:
             *
             * imagenes_productos/bigwald.png
             *
             * porque puede ser una imagen inicial
             * que forma parte de tu proyecto.
             */
            if (!nombre.startsWith(
                    PREFIJO_GESTIONADA
            )) {

                return;
            }

            Files.deleteIfExists(
                    archivo
            );

        } catch (Exception ex) {

            System.out.println(
                    "No fue posible eliminar "
                    + "la imagen anterior: "
                    + ex.getMessage()
            );
        }
    }

    // ============================================================
    // SABER SI EXISTE
    // ============================================================

    public static boolean existe(
            String ruta) {

        if (ruta == null
                || ruta.isBlank()) {

            return false;
        }

        try {

            return Files.exists(
                    resolverRuta(
                            ruta
                    )
            );

        } catch (Exception ex) {

            return false;
        }
    }

    // ============================================================
    // VALIDAR
    // ============================================================

    public static void validarImagen(
            File archivo)
            throws IOException {

        if (archivo == null
                || !archivo.exists()) {

            throw new IOException(
                    "El archivo seleccionado no existe."
            );
        }

        if (!archivo.isFile()) {

            throw new IOException(
                    "El archivo seleccionado no es válido."
            );
        }

        if (archivo.length()
                > TAMANO_MAXIMO) {

            throw new IOException(
                    "La imagen no puede superar los 10 MB."
            );
        }

        String extension =
                obtenerExtension(
                        archivo.getName()
                );

        boolean permitida =
                extension.equals("png")
                || extension.equals("jpg")
                || extension.equals("jpeg");

        if (!permitida) {

            throw new IOException(
                    "Solo puedes utilizar imágenes PNG, JPG o JPEG."
            );
        }

        BufferedImage imagen =
                ImageIO.read(
                        archivo
                );

        if (imagen == null) {

            throw new IOException(
                    "El archivo seleccionado no contiene una imagen válida."
            );
        }
    }

    // ============================================================
    // RUTA
    // ============================================================

    public static Path resolverRuta(
            String ruta) {

        Path path =
                Path.of(ruta);

        if (path.isAbsolute()) {

            return path;
        }

        return Path.of(
                System.getProperty(
                        "user.dir"
                )
        ).resolve(
                ruta
        ).normalize();
    }

    // ============================================================
    // EXTENSIÓN
    // ============================================================

    private static String obtenerExtension(
            String nombre) {

        int posicion =
                nombre.lastIndexOf(
                        '.'
                );

        if (posicion < 0
                || posicion
                == nombre.length() - 1) {

            return "";
        }

        return nombre
                .substring(
                        posicion + 1
                )
                .toLowerCase();
    }
}
