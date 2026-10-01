package CRUD;

import Conexion.Conexion;
import Modelos.PaginaProductos;
import Modelos.Producto;
import Modelos.ResumenProductos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;

public class ProductoCRUD {

    public record OpcionCombo(int idProducto, String nombre, String grupo,
            BigDecimal precioAdicional, boolean predeterminado) { }

    private Connection conectar() throws SQLException {

        Connection conexion = Conexion.conectar();

        if (conexion == null) {
            throw new SQLException(
                    "No fue posible establecer conexión con MySQL."
            );
        }

        return conexion;
    }

    // ============================================================
    // RESUMEN
    // ============================================================

    public ResumenProductos obtenerResumen()
            throws SQLException {

        String sql = """
                SELECT
                    COUNT(*) AS total,

                    SUM(
                        CASE
                            WHEN estado = TRUE
                            AND stock_minimo > 0
                            AND stock_actual <= stock_minimo
                            THEN 1
                            ELSE 0
                        END
                    ) AS stock_bajo,

                    SUM(
                        CASE
                            WHEN estado = TRUE
                            THEN 1
                            ELSE 0
                        END
                    ) AS activos,

                    SUM(
                        CASE
                            WHEN estado = FALSE
                            THEN 1
                            ELSE 0
                        END
                    ) AS inactivos

                FROM producto
                """;

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(sql);
                ResultSet rs =
                        ps.executeQuery()) {

            if (rs.next()) {

                return new ResumenProductos(
                        rs.getInt("total"),
                        rs.getInt("stock_bajo"),
                        rs.getInt("activos"),
                        rs.getInt("inactivos")
                );
            }
        }

        return new ResumenProductos(
                0,
                0,
                0,
                0
        );
    }

    // ============================================================
    // LISTAR CON FILTROS Y PAGINACIÓN
    // ============================================================

    public PaginaProductos listarPagina(
            String busqueda,
            Integer idCategoria,
            String disponibilidad,
            Boolean estado,
            int pagina,
            int porPagina)
            throws SQLException {

        StringBuilder condiciones =
                new StringBuilder(" WHERE 1 = 1 ");

        List<Object> parametros =
                new ArrayList<>();

        // BÚSQUEDA
        if (busqueda != null
                && !busqueda.isBlank()) {

            condiciones.append("""
                    AND (
                        p.nombre LIKE ?
                        OR p.descripcion LIKE ?
                        OR c.nombre LIKE ?
                        OR p.subCategoria LIKE ?
                    )
                    """);

            String texto =
                    "%" + busqueda.trim() + "%";

            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
            parametros.add(texto);
        }

        // CATEGORÍA
        if (idCategoria != null) {

            condiciones.append(
                    " AND p.id_categoria = ? "
            );

            parametros.add(idCategoria);
        }

        // DISPONIBILIDAD
        if (disponibilidad != null
                && !disponibilidad.isBlank()) {

            condiciones.append(
                    " AND p.disponibilidad_menu = ? "
            );

            parametros.add(disponibilidad);
        }

        // ESTADO
        if (estado != null) {

            condiciones.append(
                    " AND p.estado = ? "
            );

            parametros.add(estado);
        }

        String sqlContar = """
                SELECT COUNT(*) AS total
                FROM producto p
                INNER JOIN categoria c
                    ON c.id_categoria = p.id_categoria
                """
                + condiciones;

        String sqlDatos = """
                SELECT
                    p.id_producto,
                    p.id_categoria,
                    c.nombre AS categoria,
                    p.subCategoria,
                    p.nombre,
                    p.descripcion,
                    p.precio_base,
                    p.imagen,
                    p.disponibilidad_menu,
                    p.tamano_bebida,
                    p.es_combo,
                    p.stock_actual,
                    p.stock_minimo,
                    p.estado

                FROM producto p

                INNER JOIN categoria c
                    ON c.id_categoria = p.id_categoria
                """
                + condiciones
                + """
                
                ORDER BY p.id_producto DESC

                LIMIT ?
                OFFSET ?
                """;

        try (Connection conexion = conectar()) {

            int totalRegistros;

            // CONTAR
            try (PreparedStatement ps =
                    conexion.prepareStatement(
                            sqlContar
                    )) {

                colocarParametros(
                        ps,
                        parametros
                );

                try (ResultSet rs =
                        ps.executeQuery()) {

                    rs.next();

                    totalRegistros =
                            rs.getInt(
                                    "total"
                            );
                }
            }

            // CONSULTAR PRODUCTOS
            List<Producto> productos =
                    new ArrayList<>();

            try (PreparedStatement ps =
                    conexion.prepareStatement(
                            sqlDatos
                    )) {

                int indice =
                        colocarParametros(
                                ps,
                                parametros
                        );

                ps.setInt(
                        indice++,
                        porPagina
                );

                int offset =
                        (pagina - 1)
                        * porPagina;

                ps.setInt(
                        indice,
                        offset
                );

                try (ResultSet rs =
                        ps.executeQuery()) {

                    while (rs.next()) {

                        productos.add(
                                mapearProducto(rs)
                        );
                    }
                }
            }

            return new PaginaProductos(
                    productos,
                    totalRegistros
            );
        }
    }

    // ============================================================
    // INSERTAR
    // ============================================================

    public int insertar(
            Producto producto)
            throws SQLException {

        String sql = """
                INSERT INTO producto (
                    id_categoria,
                    nombre,
                    descripcion,
                    precio_base,
                    imagen,
                    disponibilidad_menu,
                    tamano_bebida,
                    es_combo,
                    stock_actual,
                    stock_minimo,
                    estado,
                    subCategoria
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )) {

            completarParametrosProducto(
                    ps,
                    producto
            );

            ps.executeUpdate();

            try (ResultSet rs =
                    ps.getGeneratedKeys()) {

                if (rs.next()) {

                    int id =
                            rs.getInt(1);

                    producto.setIdProducto(id);

                    return id;
                }
            }
        }

        return 0;
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================

    public void actualizar(
            Producto producto)
            throws SQLException {

        String sql = """
                UPDATE producto
                SET
                    id_categoria = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio_base = ?,
                    imagen = ?,
                    disponibilidad_menu = ?,
                    tamano_bebida = ?,
                    es_combo = ?,
                    stock_actual = ?,
                    stock_minimo = ?,
                    estado = ?,
                    subCategoria = ?
                WHERE id_producto = ?
                """;

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)) {

            completarParametrosProducto(
                    ps,
                    producto
            );

            ps.setInt(
                    13,
                    producto.getIdProducto()
            );

            int filas =
                    ps.executeUpdate();

            if (filas == 0) {

                throw new SQLException(
                        "No se encontró el producto que se desea actualizar."
                );
            }
        }
    }

    // ============================================================
    // ACTIVAR / DESACTIVAR
    // ============================================================

    public void cambiarEstado(
            int idProducto,
            boolean estado)
            throws SQLException {

        String sql = """
                UPDATE producto
                SET estado = ?
                WHERE id_producto = ?
                """;

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)) {

            ps.setBoolean(
                    1,
                    estado
            );

            ps.setInt(
                    2,
                    idProducto
            );

            ps.executeUpdate();
        }
    }

    public void desactivar(
            int idProducto)
            throws SQLException {

        cambiarEstado(
                idProducto,
                false
        );
    }

    public void activar(
            int idProducto)
            throws SQLException {

        cambiarEstado(
                idProducto,
                true
        );
    }

    // ============================================================
    // OBTENER POR ID
    // ============================================================

    public Producto obtenerPorId(
            int idProducto)
            throws SQLException {

        String sql = """
                SELECT
                    p.id_producto,
                    p.id_categoria,
                    c.nombre AS categoria,
                    p.subCategoria,
                    p.nombre,
                    p.descripcion,
                    p.precio_base,
                    p.imagen,
                    p.disponibilidad_menu,
                    p.tamano_bebida,
                    p.es_combo,
                    p.stock_actual,
                    p.stock_minimo,
                    p.estado

                FROM producto p

                INNER JOIN categoria c
                    ON c.id_categoria = p.id_categoria

                WHERE p.id_producto = ?
                """;

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    idProducto
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                if (rs.next()) {

                    return mapearProducto(
                            rs
                    );
                }
            }
        }

        return null;
    }

    // ============================================================
    // CATEGORÍAS ACTIVAS
    // ============================================================

    public Map<Integer, String>
            listarCategoriasActivas()
            throws SQLException {

        String sql = """
                SELECT
                    id_categoria,
                    nombre
                FROM categoria
                WHERE estado = TRUE
                ORDER BY nombre
                """;

        Map<Integer, String> categorias =
                new LinkedHashMap<>();

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(sql);
                ResultSet rs =
                        ps.executeQuery()) {

            while (rs.next()) {

                categorias.put(
                        rs.getInt(
                                "id_categoria"
                        ),
                        rs.getString(
                                "nombre"
                        )
                );
            }
        }

        return categorias;
    }

    /*
     * En edición incluimos también la categoría actual,
     * aunque haya sido desactivada posteriormente.
     */
    public Map<Integer, String>
            listarCategoriasParaFormulario(
                    Integer categoriaActual)
            throws SQLException {

        String sql;

        if (categoriaActual == null) {

            sql = """
                    SELECT id_categoria, nombre
                    FROM categoria
                    WHERE estado = TRUE
                    ORDER BY nombre
                    """;

        } else {

            sql = """
                    SELECT id_categoria, nombre
                    FROM categoria
                    WHERE estado = TRUE
                       OR id_categoria = ?
                    ORDER BY nombre
                    """;
        }

        Map<Integer, String> categorias =
                new LinkedHashMap<>();

        try (
                Connection conexion = conectar();
                PreparedStatement ps =
                        conexion.prepareStatement(sql)) {

            if (categoriaActual != null) {

                ps.setInt(
                        1,
                        categoriaActual
                );
            }

            try (ResultSet rs =
                    ps.executeQuery()) {

                while (rs.next()) {

                    categorias.put(
                            rs.getInt(
                                    "id_categoria"
                            ),
                            rs.getString(
                                    "nombre"
                            )
                    );
                }
            }
        }

        return categorias;
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private Producto mapearProducto(
            ResultSet rs)
            throws SQLException {

        Producto producto = new Producto(
                rs.getInt(
                        "id_producto"
                ),
                rs.getInt(
                        "id_categoria"
                ),
                rs.getString(
                        "categoria"
                ),
                rs.getString(
                        "nombre"
                ),
                rs.getString(
                        "descripcion"
                ),
                rs.getBigDecimal(
                        "precio_base"
                ),
                rs.getString(
                        "imagen"
                ),
                rs.getString(
                        "disponibilidad_menu"
                ),
                rs.getString(
                        "tamano_bebida"
                ),
                rs.getBoolean(
                        "es_combo"
                ),
                rs.getInt(
                        "stock_actual"
                ),
                rs.getInt(
                        "stock_minimo"
                ),
                rs.getBoolean(
                        "estado"
                )
        );
        producto.setSubCategoria(rs.getString("subCategoria"));
        return producto;
    }

    private int colocarParametros(
            PreparedStatement ps,
            List<Object> parametros)
            throws SQLException {

        int indice = 1;

        for (Object parametro
                : parametros) {

            ps.setObject(
                    indice++,
                    parametro
            );
        }

        return indice;
    }

    private void completarParametrosProducto(
            PreparedStatement ps,
            Producto producto)
            throws SQLException {

        ps.setInt(
                1,
                producto.getIdCategoria()
        );

        ps.setString(
                2,
                producto.getNombre()
        );

        ps.setString(
                3,
                producto.getDescripcion()
        );

        ps.setBigDecimal(
                4,
                producto.getPrecioBase()
        );

        if (producto.getImagen() == null
                || producto.getImagen().isBlank()) {

            ps.setNull(
                    5,
                    Types.VARCHAR
            );

        } else {

            ps.setString(
                    5,
                    producto.getImagen()
            );
        }

        ps.setString(
                6,
                producto.getDisponibilidadMenu()
        );

        if (producto.getTamanoBebida() == null
                || producto.getTamanoBebida().isBlank()) {

            ps.setNull(
                    7,
                    Types.VARCHAR
            );

        } else {

            ps.setString(
                    7,
                    producto.getTamanoBebida()
            );
        }

        ps.setBoolean(
                8,
                producto.isCombo()
        );

        ps.setInt(
                9,
                producto.getStockActual()
        );

        ps.setInt(
                10,
                producto.getStockMinimo()
        );

        ps.setBoolean(
                11,
                producto.isActivo()
        );

        String subCategoria = producto.getSubCategoria();
        if (subCategoria == null || subCategoria.isBlank()) {
            ps.setNull(12, Types.VARCHAR);
        } else {
            ps.setString(12, subCategoria.trim());
        }
    }

    /** Suma el precio de los extras configurados para un producto. */
    public BigDecimal calcularPrecioExtras(int idProducto, Set<String> ingredientes)
            throws SQLException {
        if (ingredientes == null || ingredientes.isEmpty()) return BigDecimal.ZERO;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ingredientes.size(); i++) {
            if (i > 0) placeholders.append(',');
            placeholders.append('?');
        }
        String sql = "SELECT COALESCE(SUM(rp.precio_extra),0) "
                + "FROM receta_producto rp JOIN ingrediente i "
                + "ON i.id_ingrediente=rp.id_ingrediente "
                + "WHERE rp.id_producto=? AND i.nombre IN (" + placeholders + ")";
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            int indice = 1;
            ps.setInt(indice++, idProducto);
            for (String ingrediente : ingredientes) ps.setString(indice++, ingrediente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    public List<OpcionCombo> listarOpcionesCombo(int idCombo) throws SQLException {
        String sql = "SELECT co.id_producto_opcion,p.nombre,co.grupo,"
                + "co.precio_adicional,co.es_predeterminado "
                + "FROM combo_opcion co JOIN producto p "
                + "ON p.id_producto=co.id_producto_opcion "
                + "WHERE co.id_combo=? AND co.estado=TRUE AND p.estado=TRUE "
                + "ORDER BY FIELD(co.grupo,'PRINCIPAL','ACOMPANAMIENTO','BEBIDA','POSTRE','OTRO'),p.nombre";
        List<OpcionCombo> opciones = new ArrayList<>();
        try (Connection conexion = conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCombo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) opciones.add(new OpcionCombo(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getBigDecimal(4), rs.getBoolean(5)));
            }
        }
        return opciones;
    }

    public BigDecimal calcularPrecioOpcionesCombo(int idCombo, Set<String> nombres)
            throws SQLException {
        if (nombres == null || nombres.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (OpcionCombo opcion : listarOpcionesCombo(idCombo)) {
            if (nombres.stream().anyMatch(n -> n.equalsIgnoreCase(opcion.nombre()))) {
                total = total.add(opcion.precioAdicional());
            }
        }
        return total;
    }
}
