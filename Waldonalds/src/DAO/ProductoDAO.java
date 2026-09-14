package DAO;

import Conexion.Conexion;
import Modelos.Productos;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Productos> obtenerPorCategoria(int idCategoria) {

        List<Productos> productos = new ArrayList<>();

        String sql = """
                SELECT
                    id_producto,
                    id_categoria,
                    nombre,
                    descripcion,
                    precio_base,
                    imagen
                FROM producto
                WHERE id_categoria = ?
                AND estado = TRUE
                """;

        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            return productos;
        }

        try (
                conexion; PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Productos producto = new Productos(
                            rs.getInt("id_producto"),
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio_base"),
                            rs.getString("imagen")
                    );

                    productos.add(producto);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error cargando productos: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Busca el id real de una categoría por su nombre. Así los botones del
     * cajero no dependen de ids fijos de la base de datos.
     */
    public Integer obtenerIdCategoriaPorNombre(String nombreCategoria) {
        String sql = "SELECT id_categoria, nombre FROM categoria WHERE estado = TRUE";

        try (Connection conexion = Conexion.conectar()) {
            if (conexion == null) {
                return null;
            }

            try (PreparedStatement ps = conexion.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (coincideNombreCategoria(
                            rs.getString("nombre"), nombreCategoria)) {
                        return rs.getInt("id_categoria");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error buscando categoría: " + e.getMessage());
        }

        return null;
    }

    /**
     * Obtiene las subcategorías almacenadas en la base de datos. Se admiten
     * los dos esquemas habituales del proyecto: producto.subcategoria o la
     * relación producto.id_subcategoria -> subcategoria.nombre. Si no existe
     * una relación de subcategorías, devuelve una lista vacía y la UI muestra
     * solo "Todos", sin inventar datos.
     */
    public List<String> obtenerSubcategorias(int idCategoria) {
        List<String> subcategorias = new ArrayList<>();

        try (Connection conexion = Conexion.conectar()) {
            if (conexion == null) {
                return subcategorias;
            }

            String sql = sqlSubcategorias(conexion);
            if (sql == null) {
                return subcategorias;
            }

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idCategoria);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String nombre = rs.getString("subcategoria");
                        if (nombre != null && !nombre.isBlank()) {
                            subcategorias.add(nombre.trim());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error cargando subcategorías: " + e.getMessage());
        }

        return subcategorias;
    }

    public List<Productos> obtenerPorCategoriaYSubcategoria(
            int idCategoria,
            String nombreSubcategoria) {

        if (nombreSubcategoria == null || nombreSubcategoria.isBlank()) {
            return obtenerPorCategoria(idCategoria);
        }

        List<Productos> productos = new ArrayList<>();
        try (Connection conexion = Conexion.conectar()) {
            if (conexion == null) {
                return productos;
            }

            String sql = sqlProductosSubcategoria(conexion);
            if (sql == null) {
                return filtrarPorTexto(
                        obtenerPorCategoria(idCategoria), nombreSubcategoria);
            }

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idCategoria);
                ps.setString(2, nombreSubcategoria);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        productos.add(new Productos(
                                rs.getInt("id_producto"),
                                rs.getInt("id_categoria"),
                                rs.getString("nombre"),
                                rs.getString("descripcion"),
                                rs.getDouble("precio_base"),
                                rs.getString("imagen")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error filtrando subcategoría: " + e.getMessage());
        }

        return productos;
    }

    /*
     * Compatibilidad para el esquema actual, que no tiene una columna de
     * subcategoría. Se filtra únicamente dentro de la categoría ya consultada
     * usando nombre y descripción del producto, sin crear ni alterar datos.
     */
    private List<Productos> filtrarPorTexto(
            List<Productos> productos,
            String nombreSubcategoria) {
        List<Productos> filtrados = new ArrayList<>();
        String subcategoria = normalizar(nombreSubcategoria);

        for (Productos producto : productos) {
            String texto = normalizar(
                    producto.getNombre() + " " + producto.getDescripcion());
            if (texto.contains(subcategoria)) {
                filtrados.add(producto);
            }
        }
        return filtrados;
    }

    private String sqlSubcategorias(Connection conexion) throws SQLException {
        if (columnaExiste(conexion, "producto", "subcategoria")) {
            return """
                    SELECT DISTINCT subcategoria
                    FROM producto
                    WHERE id_categoria = ?
                    AND estado = TRUE
                    AND subcategoria IS NOT NULL
                    ORDER BY subcategoria
                    """;
        }

        if (tieneRelacionSubcategoria(conexion)) {
            return """
                    SELECT DISTINCT s.nombre AS subcategoria
                    FROM producto p
                    INNER JOIN subcategoria s ON s.id_subcategoria = p.id_subcategoria
                    WHERE p.id_categoria = ?
                    AND p.estado = TRUE
                    ORDER BY s.nombre
                    """;
        }
        return null;
    }

    private String sqlProductosSubcategoria(Connection conexion) throws SQLException {
        if (columnaExiste(conexion, "producto", "subcategoria")) {
            return """
                    SELECT id_producto, id_categoria, nombre, descripcion, precio_base, imagen
                    FROM producto
                    WHERE id_categoria = ? AND subcategoria = ? AND estado = TRUE
                    """;
        }

        if (tieneRelacionSubcategoria(conexion)) {
            return """
                    SELECT p.id_producto, p.id_categoria, p.nombre, p.descripcion,
                           p.precio_base, p.imagen
                    FROM producto p
                    INNER JOIN subcategoria s ON s.id_subcategoria = p.id_subcategoria
                    WHERE p.id_categoria = ? AND s.nombre = ? AND p.estado = TRUE
                    """;
        }
        return null;
    }

    private boolean tieneRelacionSubcategoria(Connection conexion) throws SQLException {
        return tablaExiste(conexion, "subcategoria")
                && columnaExiste(conexion, "producto", "id_subcategoria")
                && columnaExiste(conexion, "subcategoria", "id_subcategoria")
                && columnaExiste(conexion, "subcategoria", "nombre");
    }

    private boolean tablaExiste(Connection conexion, String tabla) throws SQLException {
        DatabaseMetaData metadata = conexion.getMetaData();
        try (ResultSet rs = metadata.getTables(conexion.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                if (tabla.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean columnaExiste(Connection conexion, String tabla, String columna) throws SQLException {
        DatabaseMetaData metadata = conexion.getMetaData();
        try (ResultSet rs = metadata.getColumns(conexion.getCatalog(), null, tabla, "%")) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return java.text.Normalizer.normalize(
                texto,
                java.text.Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT);
    }

    private boolean coincideNombreCategoria(String nombreBase, String nombreBuscado) {
        String base = normalizar(nombreBase);
        String buscado = normalizar(nombreBuscado);
        return base.equals(buscado)
                || quitarPlural(base).equals(quitarPlural(buscado));
    }

    private String quitarPlural(String texto) {
        return texto.endsWith("s")
                ? texto.substring(0, texto.length() - 1)
                : texto;
    }
}
