package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final Object INVENTARIO_LOCK = new Object();
    private static volatile boolean inventarioInicializado;

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

            inicializarInventario(conexion);

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

    /**
     * Asegura las partes mínimas del inventario dependiente para que la
     * aplicación funcione aunque la migración SQL todavía no se haya ejecutado.
     */
    private static void inicializarInventario(Connection conexion) throws SQLException {
        if (inventarioInicializado) return;
        synchronized (INVENTARIO_LOCK) {
            if (inventarioInicializado) return;
            agregarColumnaSiFalta(conexion, "producto", "tipo_control_stock",
                    "ALTER TABLE producto ADD COLUMN tipo_control_stock "
                    + "ENUM('DIRECTO','RECETA','COMBO') NOT NULL DEFAULT 'DIRECTO' AFTER precio_base");
            agregarColumnaSiFalta(conexion, "movimiento_inventario", "id_detalle",
                    "ALTER TABLE movimiento_inventario ADD COLUMN id_detalle INT NULL AFTER id_pedido");

            try (Statement s = conexion.createStatement()) {
                s.executeUpdate("UPDATE producto SET tipo_control_stock = "
                        + "CASE WHEN es_combo = TRUE THEN 'COMBO' ELSE 'DIRECTO' END");
                s.executeUpdate("UPDATE producto p SET tipo_control_stock = 'RECETA' "
                        + "WHERE p.es_combo = FALSE AND EXISTS (SELECT 1 FROM receta_producto rp "
                        + "WHERE rp.id_producto = p.id_producto)");

                inicializarDatosInventario(conexion);

                s.executeUpdate("CREATE OR REPLACE VIEW vista_stock_producto_base AS "
                        + "SELECT p.id_producto,p.nombre,p.tipo_control_stock,p.stock_minimo,"
                        + "CASE WHEN p.tipo_control_stock='DIRECTO' THEN p.stock_actual "
                        + "WHEN p.tipo_control_stock='RECETA' THEN COALESCE(("
                        + "SELECT FLOOR(MIN(i.stock_actual/rp.cantidad_requerida)) "
                        + "FROM receta_producto rp INNER JOIN ingrediente i "
                        + "ON i.id_ingrediente=rp.id_ingrediente "
                        + "WHERE rp.id_producto=p.id_producto AND i.estado=TRUE),0) "
                        + "ELSE NULL END AS stock_disponible FROM producto p "
                        + "WHERE p.tipo_control_stock IN ('DIRECTO','RECETA')");

                s.executeUpdate("CREATE OR REPLACE VIEW vista_stock_combo AS "
                        + "SELECT c.id_producto,c.nombre,c.tipo_control_stock,c.stock_minimo,"
                        + "COALESCE(MIN(FLOOR(v.stock_disponible/co.cantidad_incluida)),0) "
                        + "AS stock_disponible FROM producto c INNER JOIN combo_opcion co "
                        + "ON co.id_combo=c.id_producto AND co.estado=TRUE INNER JOIN "
                        + "vista_stock_producto_base v ON v.id_producto=co.id_producto_opcion "
                        + "WHERE c.tipo_control_stock='COMBO' GROUP BY c.id_producto,c.nombre,"
                        + "c.tipo_control_stock,c.stock_minimo");

                s.executeUpdate("CREATE OR REPLACE VIEW vista_stock_disponible AS "
                        + "SELECT id_producto,nombre,tipo_control_stock,stock_minimo,stock_disponible "
                        + "FROM vista_stock_producto_base UNION ALL SELECT id_producto,nombre,"
                        + "tipo_control_stock,stock_minimo,stock_disponible FROM vista_stock_combo");
            }
            inventarioInicializado = true;
        }
    }

    private static void agregarColumnaSiFalta(Connection conexion, String tabla,
            String columna, String ddl) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=? AND COLUMN_NAME=?";
        try (PreparedStatement p = conexion.prepareStatement(sql)) {
            p.setString(1, tabla);
            p.setString(2, columna);
            try (ResultSet r = p.executeQuery()) {
                r.next();
                if (r.getInt(1) == 0) {
                    try (Statement s = conexion.createStatement()) {
                        s.executeUpdate(ddl);
                    }
                }
            }
        }
    }

    private static void inicializarDatosInventario(Connection conexion) throws SQLException {
        try (Statement s = conexion.createStatement()) {
            s.executeUpdate("INSERT IGNORE INTO ingrediente "
                    + "(nombre,unidad_medida,stock_actual,stock_minimo,estado) VALUES "
                    + "('Carne de hamburguesa','UNIDAD',100,10,TRUE),"
                    + "('Pan de hamburguesa','UNIDAD',100,10,TRUE),"
                    + "('Queso','UNIDAD',100,10,TRUE),"
                    + "('Lechuga','GRAMOS',10000,1000,TRUE),"
                    + "('Salsa especial','ML',10000,1000,TRUE),"
                    + "('Papas prefritas','GRAMOS',20000,2000,TRUE),"
                    + "('Jarabe Coca-Cola','ML',10000,1000,TRUE),"
                    + "('Agua carbonatada','ML',50000,5000,TRUE),"
                    + "('Hielo','GRAMOS',20000,2000,TRUE),"
                    + "('Vaso mediano','UNIDAD',100,20,TRUE),"
                    + "('Tapa mediana','UNIDAD',100,20,TRUE),"
                    + "('Helado de vainilla','PORCION',100,10,TRUE),"
                    + "('Cono','UNIDAD',100,10,TRUE),"
                    + "('Topping M&M','GRAMOS',5000,500,TRUE),"
                    + "('Vaso de postre','UNIDAD',100,10,TRUE),"
                    + "('Tomate','GRAMOS',10000,1000,TRUE)");

            s.executeUpdate("UPDATE ingrediente SET stock_actual=100 "
                    + "WHERE stock_actual=0 AND nombre IN "
                    + "('Carne de hamburguesa','Pan de hamburguesa','Queso',"
                    + "'Papas prefritas','Jarabe Coca-Cola','Agua carbonatada','Hielo',"
                    + "'Vaso mediano','Tapa mediana','Helado de vainilla','Cono',"
                    + "'Topping M&M','Vaso de postre','Tomate')");
        }

        receta(conexion, "Big Mac", "Pan de hamburguesa", 1);
        receta(conexion, "Big Mac", "Carne de hamburguesa", 2);
        receta(conexion, "Big Mac", "Queso", 1);
        receta(conexion, "Big Mac", "Lechuga", 15);
        receta(conexion, "Big Mac", "Salsa especial", 20);

        receta(conexion, "Papas", "Papas prefritas", 150);

        receta(conexion, "Coca-Cola", "Jarabe Coca-Cola", 50);
        receta(conexion, "Coca-Cola", "Agua carbonatada", 450);
        receta(conexion, "Coca-Cola", "Hielo", 120);
        receta(conexion, "Coca-Cola", "Vaso mediano", 1);
        receta(conexion, "Coca-Cola", "Tapa mediana", 1);

        receta(conexion, "WlCono Vainilla", "Helado de vainilla", 1);
        receta(conexion, "WlCono Vainilla", "Cono", 1);
        receta(conexion, "WlFlurry M&M's", "Helado de vainilla", 1);
        receta(conexion, "WlFlurry M&M's", "Topping M&M", 20);
        receta(conexion, "WlFlurry M&M's", "Vaso de postre", 1);

        configurarHamburguesas(conexion);
        configurarOpcionesCombos(conexion);

        try (Statement s = conexion.createStatement()) {
            s.executeUpdate("UPDATE receta_producto rp JOIN ingrediente i "
                    + "ON i.id_ingrediente=rp.id_ingrediente SET "
                    + "rp.permite_quitar=TRUE, rp.permite_extra=TRUE, "
                    + "rp.precio_extra=CASE i.nombre "
                    + "WHEN 'Queso' THEN 3.00 WHEN 'Tomate' THEN 2.00 "
                    + "WHEN 'Lechuga' THEN 2.00 WHEN 'Salsa especial' THEN 2.00 "
                    + "ELSE 0.00 END "
                    + "WHERE i.nombre IN ('Queso','Tomate','Lechuga','Salsa especial','Hielo')");
            s.executeUpdate("UPDATE producto SET tipo_control_stock='RECETA' "
                    + "WHERE nombre IN ('Big Mac','Papas','Coca-Cola','WlCono Vainilla',"
                    + "'WlFlurry M&M''s') AND es_combo=FALSE");
            s.executeUpdate("UPDATE producto SET stock_actual=100 "
                    + "WHERE es_combo=FALSE AND tipo_control_stock='DIRECTO' "
                    + "AND stock_actual=0");
        }
    }

    private static void receta(Connection conexion, String producto, String ingrediente,
            double cantidad) throws SQLException {
        String sql = "INSERT IGNORE INTO receta_producto "
                + "(id_producto,id_ingrediente,cantidad_requerida) "
                + "SELECT p.id_producto,i.id_ingrediente,? FROM producto p "
                + "JOIN ingrediente i ON i.nombre=? WHERE p.nombre=?";
        try (PreparedStatement p = conexion.prepareStatement(sql)) {
            p.setDouble(1, cantidad);
            p.setString(2, ingrediente);
            p.setString(3, producto);
            p.executeUpdate();
        }
    }

    private static void configurarHamburguesas(Connection conexion) throws SQLException {
        String filtro = "(p.descripcion LIKE '%Hamburguesa%' "
                + "OR p.nombre LIKE '%Big Mac%' OR p.nombre LIKE '%Big Tasty%' "
                + "OR p.nombre LIKE '%Cuarto de Libra%' OR p.nombre LIKE '%WlNífica%' "
                + "OR p.nombre LIKE '%Quesoburguesa%') AND p.es_combo=FALSE";
        try (Statement s = conexion.createStatement()) {
            s.executeUpdate("UPDATE producto p SET tipo_control_stock='RECETA' WHERE " + filtro);
        }
        recetaPorFiltro(conexion, filtro, "Pan de hamburguesa", "1.00");
        recetaPorFiltro(conexion, filtro, "Carne de hamburguesa",
                "CASE WHEN p.nombre LIKE '%Triple%' THEN 3 "
                + "WHEN p.nombre LIKE '%Doble%' OR p.nombre LIKE '%Doble Big Mac%' "
                + "OR p.nombre LIKE '%Big Mac%' THEN 2 ELSE 1 END");
        recetaPorFiltro(conexion, filtro + " AND (p.descripcion LIKE '%queso%' "
                + "OR p.nombre LIKE '%Quesoburguesa%')", "Queso", "1.00");
        recetaPorFiltro(conexion, filtro + " AND (p.descripcion LIKE '%lechuga%' "
                + "OR p.descripcion LIKE '%vegetales%')", "Lechuga", "15.00");
        recetaPorFiltro(conexion, filtro + " AND p.descripcion LIKE '%tomate%'", "Tomate", "20.00");
    }

    /**
     * La base original marcaba productos como combo, pero no cargaba sus
     * filas en combo_opcion. En ese caso el configurador no tiene nada que
     * mostrar. Se agregan opciones iniciales únicamente a combos vacíos; si el
     * administrador ya configuró un combo, sus opciones se respetan.
     */
    private static void configurarOpcionesCombos(Connection conexion) throws SQLException {
        String combos = "SELECT id_producto,nombre FROM producto WHERE es_combo=TRUE "
                + "AND NOT EXISTS (SELECT 1 FROM combo_opcion co "
                + "WHERE co.id_combo=producto.id_producto AND co.estado=TRUE)";
        try (PreparedStatement p = conexion.prepareStatement(combos);
                ResultSet r = p.executeQuery()) {
            while (r.next()) {
                int idCombo = r.getInt(1);
                String nombreCombo = r.getString(2).toLowerCase(java.util.Locale.ROOT);
                String principal = nombreCombo.contains("nugget") ? "WlNuggets"
                        : nombreCombo.contains("pollo") || nombreCombo.contains("crispy")
                        ? "WlCrispy Chicken Deluxe"
                        : nombreCombo.contains("hamburguesa") || nombreCombo.contains("big mac")
                        ? "Big Mac" : "WlNuggets";

                insertarOpcionCombo(conexion, idCombo, principal, "PRINCIPAL", 1, true, 0);
                insertarOpcionCombo(conexion, idCombo, "Papas", "ACOMPANAMIENTO", 1, true, 0);
                insertarOpcionCombo(conexion, idCombo, "WlPatatas", "ACOMPANAMIENTO", 1, false, 7);
                insertarOpcionCombo(conexion, idCombo, "Coca-Cola", "BEBIDA", 1, true, 0);
                insertarOpcionCombo(conexion, idCombo, "Fanta", "BEBIDA", 1, false, 0);
                insertarOpcionCombo(conexion, idCombo, "Agua", "BEBIDA", 1, false, 0);
                if (nombreCombo.contains("postre")) {
                    insertarOpcionCombo(conexion, idCombo, "WlCono Vainilla", "POSTRE", 1, true, 0);
                }
            }
        }
    }

    private static void insertarOpcionCombo(Connection conexion, int idCombo,
            String nombreProducto, String grupo, int cantidad, boolean predeterminado,
            double precioAdicional) throws SQLException {
        String sql = "INSERT IGNORE INTO combo_opcion "
                + "(id_combo,id_producto_opcion,grupo,cantidad_incluida,"
                + "es_predeterminado,precio_adicional,estado) "
                + "SELECT ?,id_producto,?,?,?, ?,TRUE FROM producto WHERE nombre=? AND estado=TRUE";
        try (PreparedStatement p = conexion.prepareStatement(sql)) {
            p.setInt(1, idCombo);
            p.setString(2, grupo);
            p.setInt(3, cantidad);
            p.setBoolean(4, predeterminado);
            p.setDouble(5, precioAdicional);
            p.setString(6, nombreProducto);
            p.executeUpdate();
        }
    }

    private static void recetaPorFiltro(Connection conexion, String filtro,
            String ingrediente, String cantidad) throws SQLException {
        String sql = "INSERT IGNORE INTO receta_producto "
                + "(id_producto,id_ingrediente,cantidad_requerida) "
                + "SELECT p.id_producto,i.id_ingrediente," + cantidad
                + " FROM producto p JOIN ingrediente i ON i.nombre=? WHERE " + filtro;
        try (PreparedStatement p = conexion.prepareStatement(sql)) {
            p.setString(1, ingrediente);
            p.executeUpdate();
        }
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

