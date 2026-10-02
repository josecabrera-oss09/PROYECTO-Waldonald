package DAO;

import Conexion.Conexion;
import Modelos.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import Utilidades.HorarioMenu;

/** Lee y administra presentaciones, elecciones, componentes y recetas. */
public class ConfiguracionMenuDAO {

    private Connection conectar() throws SQLException {
        Connection conexion = Conexion.conectar();
        if (conexion == null) throw new SQLException("No fue posible conectar con MySQL.");
        return conexion;
    }

    public ConfiguracionProducto cargarProducto(int idProducto) throws SQLException {
        try (Connection c = conectar()) {
            ConfiguracionProducto resultado = cargarProducto(c, idProducto, true, true);
            if (!HorarioMenu.estaDisponible(resultado.disponibilidadMenu(), LocalTime.now()))
                throw new IllegalArgumentException("Este producto no está disponible en este horario.");
            return resultado;
        }
    }

    public ConfiguracionProducto cargarProductoAdmin(int idProducto) throws SQLException {
        try (Connection c = conectar()) { return cargarProducto(c, idProducto, true, false); }
    }

    public ConfiguracionProducto cargarProducto(Connection c, int idProducto,
            boolean soloActivos) throws SQLException {
        return cargarProducto(c, idProducto, soloActivos, true);
    }

    private ConfiguracionProducto cargarProducto(Connection c, int idProducto,
            boolean soloActivos, boolean validarHorario) throws SQLException {
        String nombre;
        String descripcion;
        String imagen;
        String disponibilidad;
        try (PreparedStatement s = c.prepareStatement("SELECT nombre,descripcion,imagen,disponibilidad_menu,estado FROM producto WHERE id_producto=?")) {
            s.setInt(1, idProducto);
            try (ResultSet r = s.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("El producto ya no existe.");
                if (soloActivos && !r.getBoolean("estado"))
                    throw new IllegalArgumentException("El producto está inactivo.");
                nombre = r.getString("nombre");
                descripcion = r.getString("descripcion");
                imagen = r.getString("imagen");
                disponibilidad = r.getString("disponibilidad_menu");
            }
        }
        List<PresentacionMenu> presentaciones = new ArrayList<>();
        String sql = "SELECT id_presentacion,nombre,tipo,precio,predeterminada FROM presentacion_menu "
                + "WHERE id_producto_principal=?" + (soloActivos ? " AND estado=TRUE" : "")
                + " ORDER BY predeterminada DESC,id_presentacion";
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, idProducto);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    int idPresentacion = r.getInt("id_presentacion");
                    List<GrupoMenu> grupos = cargarGrupos(c, idPresentacion, soloActivos, validarHorario);
                    if (validarHorario && (grupos.isEmpty() || grupos.stream()
                            .anyMatch(g -> g.minimo() > 0 && g.opciones().isEmpty()))) continue;
                    presentaciones.add(new PresentacionMenu(idPresentacion, idProducto,
                            r.getString("nombre"), r.getString("tipo"),
                            r.getBigDecimal("precio"), r.getBoolean("predeterminada"),
                            grupos));
                }
            }
        }
        if (presentaciones.isEmpty())
            throw new IllegalArgumentException("Este producto todavía no tiene una presentación configurada.");
        return new ConfiguracionProducto(idProducto, nombre, descripcion, imagen,
                disponibilidad, presentaciones);
    }

    private List<GrupoMenu> cargarGrupos(Connection c, int idPresentacion,
            boolean soloActivos, boolean validarHorario) throws SQLException {
        List<GrupoMenu> grupos = new ArrayList<>();
        String sql = "SELECT id_grupo,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar "
                + "FROM grupo_presentacion WHERE id_presentacion=?"
                + (soloActivos ? " AND estado=TRUE" : "") + " ORDER BY id_grupo";
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, idPresentacion);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    int idGrupo = r.getInt("id_grupo");
                    grupos.add(new GrupoMenu(idGrupo, r.getString("nombre"),
                            r.getInt("minimo"), r.getInt("maximo"),
                            r.getBoolean("permite_repetir"), r.getBoolean("visible"),
                            r.getBoolean("permite_personalizar"),
                            cargarOpciones(c, idGrupo, soloActivos, validarHorario)));
                }
            }
        }
        return grupos;
    }

    private List<OpcionMenu> cargarOpciones(Connection c, int idGrupo,
            boolean soloActivos, boolean validarHorario) throws SQLException {
        List<OpcionMenu> opciones = new ArrayList<>();
        String sql = "SELECT id_opcion,nombre,incremento_precio,predeterminada FROM opcion_grupo "
                + "WHERE id_grupo=?" + (soloActivos ? " AND estado=TRUE" : "")
                + " ORDER BY predeterminada DESC,id_opcion";
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, idGrupo);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    int idOpcion = r.getInt("id_opcion");
                    List<ComponenteMenu> componentes = cargarComponentes(c, idOpcion, soloActivos, validarHorario);
                    if (soloActivos && componentes.size() != contarComponentes(c, idOpcion)) continue;
                    opciones.add(new OpcionMenu(idOpcion, r.getString("nombre"),
                            r.getBigDecimal("incremento_precio"),
                            r.getBoolean("predeterminada"), componentes));
                }
            }
        }
        return opciones;
    }

    private int contarComponentes(Connection c, int idOpcion) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(
                "SELECT COUNT(*) FROM opcion_componente WHERE id_opcion=?")) {
            s.setInt(1, idOpcion);
            try (ResultSet r = s.executeQuery()) { r.next(); return r.getInt(1); }
        }
    }

    private List<ComponenteMenu> cargarComponentes(Connection c, int idOpcion,
            boolean soloActivos, boolean validarHorario) throws SQLException {
        List<ComponenteMenu> componentes = new ArrayList<>();
        String sql = "SELECT oc.id_producto,oc.cantidad,p.nombre,p.tipo_stock,p.personalizable,p.disponibilidad_menu,p.estado "
                + "FROM opcion_componente oc JOIN producto p ON p.id_producto=oc.id_producto "
                + "WHERE oc.id_opcion=? ORDER BY oc.id_opcion_componente";
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, idOpcion);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    if (soloActivos && (!r.getBoolean("estado")
                            || (validarHorario && !HorarioMenu.estaDisponible(
                                    r.getString("disponibilidad_menu"), LocalTime.now()))))
                        continue;
                    int idProducto = r.getInt("id_producto");
                    String tipoStock = r.getString("tipo_stock");
                    boolean personalizable = r.getBoolean("personalizable");
                    componentes.add(new ComponenteMenu(idProducto, r.getString("nombre"),
                            r.getInt("cantidad"), tipoStock,
                            personalizable, r.getString("disponibilidad_menu"),
                            "RECETA".equals(tipoStock) && personalizable
                                    ? cargarIngredientes(c, idProducto, soloActivos) : List.of()));
                }
            }
        }
        return componentes;
    }

    public List<IngredienteProducto> cargarIngredientes(Connection c, int idProducto,
            boolean soloActivos) throws SQLException {
        List<IngredienteProducto> ingredientes = new ArrayList<>();
        String sql = "SELECT pi.id_producto_ingrediente,pi.id_ingrediente,i.nombre,pi.cantidad_default,"
                + "pi.permite_quitar,pi.permite_extra,pi.cantidad_extra,pi.precio_extra,pi.max_extras "
                + "FROM producto_ingrediente pi JOIN ingrediente i ON i.id_ingrediente=pi.id_ingrediente "
                + "WHERE pi.id_producto=?" + (soloActivos ? " AND pi.estado=TRUE AND i.estado=TRUE" : "")
                + " ORDER BY i.nombre";
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, idProducto);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) ingredientes.add(new IngredienteProducto(
                        r.getInt("id_producto_ingrediente"), r.getInt("id_ingrediente"),
                        r.getString("nombre"), r.getBigDecimal("cantidad_default"),
                        r.getBoolean("permite_quitar"), r.getBoolean("permite_extra"),
                        r.getBigDecimal("cantidad_extra"), r.getBigDecimal("precio_extra"),
                        r.getInt("max_extras")));
            }
        }
        return ingredientes;
    }

    public List<IngredienteProducto> cargarIngredientes(int idProducto) throws SQLException {
        try (Connection c = conectar()) { return cargarIngredientes(c, idProducto, true); }
    }

    public void asegurarPresentacionIndividual(int idProducto) throws SQLException {
        try (Connection c = conectar()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement existe = c.prepareStatement("SELECT id_presentacion FROM presentacion_menu WHERE id_producto_principal=? LIMIT 1")) {
                    existe.setInt(1, idProducto);
                    try (ResultSet r = existe.executeQuery()) { if (r.next()) { c.rollback(); return; } }
                }
                String nombre;
                BigDecimal precio;
                try (PreparedStatement p = c.prepareStatement("SELECT nombre,precio_base FROM producto WHERE id_producto=?")) {
                    p.setInt(1, idProducto);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new SQLException("No se encontró el producto.");
                        nombre = r.getString(1); precio = r.getBigDecimal(2);
                    }
                }
                int presentacion = insertarId(c, "INSERT INTO presentacion_menu(id_producto_principal,nombre,tipo,precio,predeterminada,estado) VALUES(?,?,'INDIVIDUAL',?,TRUE,TRUE)", idProducto, "Individual", precio);
                int grupo = insertarId(c, "INSERT INTO grupo_presentacion(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado) VALUES(?, '__Producto principal',1,1,FALSE,FALSE,TRUE,TRUE)", presentacion);
                int opcion = insertarId(c, "INSERT INTO opcion_grupo(id_grupo,nombre,incremento_precio,predeterminada,estado) VALUES(?,?,0,TRUE,TRUE)", grupo, nombre);
                ejecutar(c, "INSERT INTO opcion_componente(id_opcion,id_producto,cantidad) VALUES(?,?,1)", opcion, idProducto);
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }

    public int guardarPresentacion(Integer id, int idProducto, String nombre,
            String tipo, BigDecimal precio, boolean predeterminada) throws SQLException {
        try (Connection c = conectar()) {
            if (predeterminada) ejecutar(c, "UPDATE presentacion_menu SET predeterminada=FALSE WHERE id_producto_principal=?", idProducto);
            if (id == null) return insertarId(c, "INSERT INTO presentacion_menu(id_producto_principal,nombre,tipo,precio,predeterminada,estado) VALUES(?,?,?,?,?,TRUE)", idProducto,nombre,tipo,precio,predeterminada);
            ejecutar(c, "UPDATE presentacion_menu SET nombre=?,tipo=?,precio=?,predeterminada=?,estado=TRUE WHERE id_presentacion=?", nombre,tipo,precio,predeterminada,id);
            return id;
        }
    }

    public int guardarGrupo(Integer id, int idPresentacion, String nombre, int minimo,
            int maximo, boolean repetir, boolean visible, boolean personalizar) throws SQLException {
        try (Connection c = conectar()) {
            if (id == null) return insertarId(c, "INSERT INTO grupo_presentacion(id_presentacion,nombre,minimo,maximo,permite_repetir,visible,permite_personalizar,estado) VALUES(?,?,?,?,?,?,?,TRUE)", idPresentacion,nombre,minimo,maximo,repetir,visible,personalizar);
            ejecutar(c, "UPDATE grupo_presentacion SET nombre=?,minimo=?,maximo=?,permite_repetir=?,visible=?,permite_personalizar=?,estado=TRUE WHERE id_grupo=?", nombre,minimo,maximo,repetir,visible,personalizar,id);
            return id;
        }
    }

    public int guardarOpcion(Integer id, int idGrupo, String nombre,
            BigDecimal incremento, boolean predeterminada) throws SQLException {
        try (Connection c = conectar()) {
            if (predeterminada) ejecutar(c, "UPDATE opcion_grupo SET predeterminada=FALSE WHERE id_grupo=?", idGrupo);
            if (id == null) return insertarId(c, "INSERT INTO opcion_grupo(id_grupo,nombre,incremento_precio,predeterminada,estado) VALUES(?,?,?,?,TRUE)", idGrupo,nombre,incremento,predeterminada);
            ejecutar(c, "UPDATE opcion_grupo SET nombre=?,incremento_precio=?,predeterminada=?,estado=TRUE WHERE id_opcion=?", nombre,incremento,predeterminada,id);
            return id;
        }
    }

    public void agregarComponente(int idOpcion, int idProducto, int cantidad) throws SQLException {
        try (Connection c = conectar()) { ejecutar(c, "INSERT INTO opcion_componente(id_opcion,id_producto,cantidad) VALUES(?,?,?)", idOpcion,idProducto,cantidad); }
    }

    public void quitarComponente(int idOpcion, int idProducto) throws SQLException {
        try (Connection c = conectar()) { ejecutar(c,
                "DELETE FROM opcion_componente WHERE id_opcion=? AND id_producto=?",
                idOpcion,idProducto); }
    }

    public void desactivar(String tabla, String campoId, int id) throws SQLException {
        if (!Set.of("presentacion_menu","grupo_presentacion","opcion_grupo").contains(tabla))
            throw new IllegalArgumentException("Tabla inválida.");
        try (Connection c = conectar()) { ejecutar(c, "UPDATE " + tabla + " SET estado=FALSE WHERE " + campoId + "=?", id); }
    }

    public Map<Integer,String> listarProductos() throws SQLException {
        return listarNombres("SELECT id_producto,nombre FROM producto WHERE estado=TRUE ORDER BY nombre");
    }

    public Map<Integer,String> listarIngredientes() throws SQLException {
        return listarNombres("SELECT id_ingrediente,nombre FROM ingrediente WHERE estado=TRUE ORDER BY nombre");
    }

    private Map<Integer,String> listarNombres(String sql) throws SQLException {
        Map<Integer,String> resultado = new LinkedHashMap<>();
        try (Connection c = conectar(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            while (r.next()) resultado.put(r.getInt(1), r.getString(2));
        }
        return resultado;
    }

    public void guardarIngredienteProducto(Integer id, int idProducto, int idIngrediente,
            BigDecimal cantidadDefault, boolean quitar, boolean extra,
            BigDecimal cantidadExtra, BigDecimal precioExtra, int maxExtras) throws SQLException {
        try (Connection c = conectar()) {
            if (id == null) ejecutar(c, "INSERT INTO producto_ingrediente(id_producto,id_ingrediente,cantidad_default,permite_quitar,permite_extra,cantidad_extra,precio_extra,max_extras,estado) VALUES(?,?,?,?,?,?,?,?,TRUE)", idProducto,idIngrediente,cantidadDefault,quitar,extra,cantidadExtra,precioExtra,maxExtras);
            else ejecutar(c, "UPDATE producto_ingrediente SET id_ingrediente=?,cantidad_default=?,permite_quitar=?,permite_extra=?,cantidad_extra=?,precio_extra=?,max_extras=?,estado=TRUE WHERE id_producto_ingrediente=?", idIngrediente,cantidadDefault,quitar,extra,cantidadExtra,precioExtra,maxExtras,id);
        }
    }

    public void desactivarIngredienteProducto(int id) throws SQLException {
        try (Connection c = conectar()) { ejecutar(c, "UPDATE producto_ingrediente SET estado=FALSE WHERE id_producto_ingrediente=?", id); }
    }

    private static int insertarId(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(s, valores); s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                if (!r.next()) throw new SQLException("No se obtuvo el identificador creado.");
                return r.getInt(1);
            }
        }
    }

    private static void ejecutar(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement s = c.prepareStatement(sql)) { asignar(s, valores); s.executeUpdate(); }
    }

    private static void asignar(PreparedStatement s, Object... valores) throws SQLException {
        for (int i=0; i<valores.length; i++) s.setObject(i+1, valores[i]);
    }
}
