package CRUD;

import Conexion.Conexion;
import Modelos.Ingrediente;
import Modelos.PaginaIngredientes;
import Modelos.ResumenIngredientes;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IngredienteCRUD {
    private static final BigDecimal MAX_STOCK = new BigDecimal("99999999.99");
    private static final String STOCK_BAJO =
            "(stock_minimo > 0 AND stock_actual <= stock_minimo)";

    private Connection conectar() throws SQLException {
        Connection conexion = Conexion.conectar();
        if (conexion == null) throw new SQLException("No fue posible conectar con MySQL.");
        return conexion;
    }

    public ResumenIngredientes obtenerResumen() throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total,
                    COALESCE(SUM(CASE WHEN estado = TRUE AND stock_minimo > 0
                        AND stock_actual <= stock_minimo THEN 1 ELSE 0 END), 0) AS stock_bajo,
                    COALESCE(SUM(CASE WHEN estado = TRUE THEN 1 ELSE 0 END), 0) AS activos,
                    COALESCE(SUM(CASE WHEN estado = FALSE THEN 1 ELSE 0 END), 0) AS inactivos
                FROM ingrediente
                """;
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            return new ResumenIngredientes(rs.getInt("total"),
                    rs.getInt("stock_bajo"), rs.getInt("activos"),
                    rs.getInt("inactivos"));
        }
    }

    public PaginaIngredientes listarPagina(String busqueda, Boolean stockBajo,
            Boolean estado, int pagina, int porPagina) throws SQLException {
        StringBuilder condiciones = new StringBuilder(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (busqueda != null && !busqueda.isBlank()) {
            condiciones.append(" AND (nombre LIKE ? OR unidad_medida LIKE ?)");
            String texto = "%" + busqueda.trim() + "%";
            parametros.add(texto);
            parametros.add(texto);
        }
        if (stockBajo != null) {
            condiciones.append(stockBajo ? " AND " + STOCK_BAJO
                    : " AND NOT " + STOCK_BAJO);
        }
        if (estado != null) {
            condiciones.append(" AND estado = ?");
            parametros.add(estado);
        }
        String contar = "SELECT COUNT(*) AS total FROM ingrediente" + condiciones;
        String listar = """
                SELECT id_ingrediente, nombre, unidad_medida, stock_actual,
                    stock_minimo, estado
                FROM ingrediente
                """ + condiciones + " ORDER BY id_ingrediente DESC LIMIT ? OFFSET ?";

        try (Connection conexion = conectar()) {
            int total;
            try (PreparedStatement ps = conexion.prepareStatement(contar)) {
                colocarParametros(ps, parametros);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getInt("total");
                }
            }
            List<Ingrediente> ingredientes = new ArrayList<>();
            try (PreparedStatement ps = conexion.prepareStatement(listar)) {
                int indice = colocarParametros(ps, parametros);
                ps.setInt(indice++, porPagina);
                ps.setInt(indice, (pagina - 1) * porPagina);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) ingredientes.add(mapear(rs));
                }
            }
            return new PaginaIngredientes(ingredientes, total);
        }
    }

    public Ingrediente obtenerPorId(int id) throws SQLException {
        String sql = """
                SELECT id_ingrediente, nombre, unidad_medida, stock_actual,
                    stock_minimo, estado
                FROM ingrediente WHERE id_ingrediente = ?
                """;
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public int insertar(Ingrediente ingrediente) throws SQLException {
        String sql = """
                INSERT INTO ingrediente
                    (nombre, unidad_medida, stock_actual, stock_minimo, estado)
                VALUES (?, ?, 0.00, ?, ?)
                """;
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ingrediente.getNombre());
            ps.setString(2, ingrediente.getUnidadMedida());
            ps.setBigDecimal(3, ingrediente.getStockMinimo());
            ps.setBoolean(4, ingrediente.isActivo());
            ps.executeUpdate();
            ingrediente.setStockActual(BigDecimal.ZERO);
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ingrediente.setIdIngrediente(rs.getInt(1));
                    return ingrediente.getIdIngrediente();
                }
            }
        }
        return 0;
    }

    /** No modifica stock_actual; las existencias se cambian con movimientos. */
    public void actualizar(Ingrediente ingrediente) throws SQLException {
        String sql = """
                UPDATE ingrediente SET nombre = ?, unidad_medida = ?,
                    stock_minimo = ?, estado = ? WHERE id_ingrediente = ?
                """;
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, ingrediente.getNombre());
            ps.setString(2, ingrediente.getUnidadMedida());
            ps.setBigDecimal(3, ingrediente.getStockMinimo());
            ps.setBoolean(4, ingrediente.isActivo());
            ps.setInt(5, ingrediente.getIdIngrediente());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el ingrediente.");
            }
        }
    }

    public void cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE ingrediente SET estado = ? WHERE id_ingrediente = ?";
        try (Connection conexion = conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el ingrediente.");
            }
        }
    }

    /**
     * ENTRADA y SALIDA reciben una cantidad positiva. AJUSTE recibe el nuevo
     * stock absoluto y registra como cantidad la diferencia con el anterior.
     * La actualización y el movimiento se confirman en una sola transacción.
     */
    public BigDecimal registrarMovimiento(int id, String tipo,
            BigDecimal cantidad, String motivo) throws SQLException {
        if (!("ENTRADA".equals(tipo) || "SALIDA".equals(tipo)
                || "AJUSTE".equals(tipo))) {
            throw new IllegalArgumentException("Tipo de movimiento inválido.");
        }
        if (cantidad == null || cantidad.scale() > 2
                || cantidad.compareTo(MAX_STOCK) > 0
                || ("AJUSTE".equals(tipo) ? cantidad.signum() < 0
                        : cantidad.signum() <= 0)) {
            throw new IllegalArgumentException("Cantidad inválida; usa hasta dos decimales.");
        }
        if (motivo != null && motivo.length() > 150) {
            throw new IllegalArgumentException("El motivo admite hasta 150 caracteres.");
        }
        try (Connection conexion = conectar()) {
            conexion.setAutoCommit(false);
            try {
                BigDecimal anterior;
                try (PreparedStatement ps = conexion.prepareStatement(
                        "SELECT stock_actual FROM ingrediente WHERE id_ingrediente = ? FOR UPDATE")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("No se encontró el ingrediente.");
                        anterior = rs.getBigDecimal("stock_actual");
                    }
                }
                BigDecimal nuevo = switch (tipo) {
                    case "ENTRADA" -> anterior.add(cantidad);
                    case "SALIDA" -> anterior.subtract(cantidad);
                    default -> cantidad;
                };
                if (nuevo.signum() < 0) {
                    throw new SQLException("No hay existencias suficientes para la salida.");
                }
                if (nuevo.compareTo(MAX_STOCK) > 0) {
                    throw new SQLException("El stock supera el máximo permitido.");
                }
                BigDecimal cantidadRegistrada = "AJUSTE".equals(tipo)
                        ? nuevo.subtract(anterior) : cantidad;
                if (cantidadRegistrada.signum() == 0) {
                    throw new SQLException("El ajuste no cambia el stock.");
                }
                try (PreparedStatement ps = conexion.prepareStatement(
                        "UPDATE ingrediente SET stock_actual = ? WHERE id_ingrediente = ?")) {
                    ps.setBigDecimal(1, nuevo);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                String sql = """
                        INSERT INTO movimiento_inventario
                            (id_ingrediente, tipo_movimiento, cantidad, motivo)
                        VALUES (?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setString(2, tipo);
                    ps.setBigDecimal(3, cantidadRegistrada);
                    ps.setString(4, motivo == null || motivo.isBlank() ? null : motivo.trim());
                    ps.executeUpdate();
                }
                conexion.commit();
                return nuevo;
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            }
        }
    }

    private static int colocarParametros(PreparedStatement ps,
            List<Object> parametros) throws SQLException {
        int indice = 1;
        for (Object parametro : parametros) ps.setObject(indice++, parametro);
        return indice;
    }

    private static Ingrediente mapear(ResultSet rs) throws SQLException {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setIdIngrediente(rs.getInt("id_ingrediente"));
        ingrediente.setNombre(rs.getString("nombre"));
        ingrediente.setUnidadMedida(rs.getString("unidad_medida"));
        ingrediente.setStockActual(rs.getBigDecimal("stock_actual"));
        ingrediente.setStockMinimo(rs.getBigDecimal("stock_minimo"));
        ingrediente.setActivo(rs.getBoolean("estado"));
        return ingrediente;
    }
}
