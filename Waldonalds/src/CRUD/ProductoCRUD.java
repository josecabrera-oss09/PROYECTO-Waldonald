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

public class ProductoCRUD {

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
                            AND tipo_stock = 'DIRECTO'
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
                    p.tipo_stock,
                    p.personalizable,
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
                    tipo_stock,
                    personalizable,
                    stock_actual,
                    stock_minimo,
                    estado,
                    subCategoria
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = conectar()) {
            conexion.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = conexion.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
                    completarParametrosProducto(ps, producto);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException(
                                "No se obtuvo el identificador del producto.");
                        id = rs.getInt(1);
                    }
                }
                int presentacion = insertarId(conexion,
                        "INSERT INTO presentacion_menu(id_producto_principal,nombre,tipo,precio,predeterminada,estado) VALUES(?,'Individual','INDIVIDUAL',?,TRUE,TRUE)",
                        id, producto.getPrecioBase());
                int grupo = insertarId(conexion,
                        "INSERT INTO grupo_presentacion(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado) VALUES(?,'__Producto principal',1,1,FALSE,FALSE,TRUE,TRUE)",
                        presentacion);
                int opcion = insertarId(conexion,
                        "INSERT INTO opcion_grupo(id_grupo,nombre,incremento_precio,predeterminada,estado) VALUES(?,?,0,TRUE,TRUE)",
                        grupo, producto.getNombre());
                try (PreparedStatement componente = conexion.prepareStatement(
                        "INSERT INTO opcion_componente(id_opcion,id_producto,cantidad) VALUES(?,?,1)")) {
                    componente.setInt(1, opcion);
                    componente.setInt(2, id);
                    componente.executeUpdate();
                }
                conexion.commit();
                producto.setIdProducto(id);
                return id;
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            }
        }
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
                    tipo_stock = ?,
                    personalizable = ?,
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

            try (PreparedStatement precioIndividual = conexion.prepareStatement(
                    "UPDATE presentacion_menu SET precio=? WHERE id_producto_principal=? AND tipo='INDIVIDUAL' AND nombre='Individual'")) {
                precioIndividual.setBigDecimal(1, producto.getPrecioBase());
                precioIndividual.setInt(2, producto.getIdProducto());
                precioIndividual.executeUpdate();
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
                    p.tipo_stock,
                    p.personalizable,
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

        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setIdCategoria(rs.getInt("id_categoria"));
        producto.setCategoria(rs.getString("categoria"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecioBase(rs.getBigDecimal("precio_base"));
        producto.setImagen(rs.getString("imagen"));
        producto.setDisponibilidadMenu(rs.getString("disponibilidad_menu"));
        producto.setTipoStock(rs.getString("tipo_stock"));
        producto.setPersonalizable(rs.getBoolean("personalizable"));
        producto.setStockActual(rs.getInt("stock_actual"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setActivo(rs.getBoolean("estado"));
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

    private int insertarId(Connection conexion, String sql, Object... valores)
            throws SQLException {
        try (PreparedStatement ps = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < valores.length; i++) ps.setObject(i + 1, valores[i]);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("No se obtuvo el registro creado.");
                return rs.getInt(1);
            }
        }
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

        ps.setString(7, producto.getTipoStock());

        ps.setBoolean(
                8,
                producto.isPersonalizable()
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
}
