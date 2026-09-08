package CRUD;

import Conexion.Conexion;
import Modelos.PaginaUsuarios;
import Modelos.ResumenUsuarios;
import Modelos.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza todas las consultas del módulo de usuarios.
 *
 * La vista nunca arma SQL: solamente entrega filtros, página y datos. Todos
 * los valores variables se envían con PreparedStatement.
 */
public class UsuarioCRUD {

    /**
     * Obtiene una página de resultados y el total filtrado usando la misma
     * conexión. Así la interfaz puede calcular la cantidad de páginas.
     */
    public PaginaUsuarios listarPagina(
            String busqueda,
            String rol,
            Boolean estado,
            int pagina,
            int tamanoPagina) throws SQLException {

        int paginaSegura = Math.max(1, pagina);
        int tamanoSeguro = Math.max(1, tamanoPagina);
        int desplazamiento = (paginaSegura - 1) * tamanoSeguro;
        FiltroSql filtro = construirFiltro(busqueda, rol, estado);

        try (Connection conexion = abrirConexion()) {
            int total = contarFiltrados(conexion, filtro);
            List<Usuario> usuarios = consultarUsuarios(
                    conexion, filtro, tamanoSeguro, desplazamiento, true);
            return new PaginaUsuarios(usuarios, total);
        }
    }

    /** Devuelve todos los resultados filtrados, sin LIMIT, para exportar. */
    public List<Usuario> listarParaExportar(
            String busqueda,
            String rol,
            Boolean estado) throws SQLException {
        FiltroSql filtro = construirFiltro(busqueda, rol, estado);
        try (Connection conexion = abrirConexion()) {
            return consultarUsuarios(conexion, filtro, 0, 0, false);
        }
    }

    /** Las tarjetas son globales y no cambian al aplicar filtros. */
    public ResumenUsuarios obtenerResumen() throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total,
                       COALESCE(SUM(rol = 'ADMINISTRADOR' AND estado = TRUE), 0)
                           AS administradores,
                       COALESCE(SUM(rol = 'CAJERO' AND estado = TRUE), 0)
                           AS cajeros,
                       COALESCE(SUM(estado = FALSE), 0) AS inactivos
                FROM usuario
                """;

        try (Connection conexion = abrirConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            resultado.next();
            return new ResumenUsuarios(
                    resultado.getInt("total"),
                    resultado.getInt("administradores"),
                    resultado.getInt("cajeros"),
                    resultado.getInt("inactivos"));
        }
    }

    public int insertar(Usuario usuario, String passwordHash)
            throws SQLException {
        String sql = """
                INSERT INTO usuario
                    (nombre, apellido, usuario, correo, password_hash, rol, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = abrirConexion();
                PreparedStatement sentencia = conexion.prepareStatement(
                        sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            colocarDatosComunes(sentencia, usuario);
            sentencia.setString(5, passwordHash);
            sentencia.setString(6, usuario.getRol());
            sentencia.setBoolean(7, usuario.isActivo());
            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        }
    }

    /**
     * Si passwordHash es null, conserva la contraseña actual. Nunca se carga
     * el hash existente en el formulario ni se vuelve a escribir sin motivo.
     */
    public void actualizar(Usuario usuario, String passwordHash)
            throws SQLException {
        boolean cambiaContrasena = passwordHash != null;
        String sql = cambiaContrasena
                ? """
                  UPDATE usuario
                  SET nombre = ?, apellido = ?, usuario = ?, correo = ?,
                      rol = ?, estado = ?, password_hash = ?
                  WHERE id_usuario = ?
                  """
                : """
                  UPDATE usuario
                  SET nombre = ?, apellido = ?, usuario = ?, correo = ?,
                      rol = ?, estado = ?
                  WHERE id_usuario = ?
                  """;

        try (Connection conexion = abrirConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            colocarDatosComunes(sentencia, usuario);
            sentencia.setString(5, usuario.getRol());
            sentencia.setBoolean(6, usuario.isActivo());
            int indiceId;
            if (cambiaContrasena) {
                sentencia.setString(7, passwordHash);
                indiceId = 8;
            } else {
                indiceId = 7;
            }
            sentencia.setInt(indiceId, usuario.getIdUsuario());
            sentencia.executeUpdate();
        }
    }

    /** Eliminación lógica: conserva pedidos y permite una futura reactivación. */
    public void desactivar(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET estado = FALSE WHERE id_usuario = ?";
        try (Connection conexion = abrirConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuario);
            sentencia.executeUpdate();
        }
    }

    public boolean existeNombreUsuario(String nombreUsuario, int idExcluido)
            throws SQLException {
        return existeValor("usuario", nombreUsuario, idExcluido);
    }

    public boolean existeCorreo(String correo, int idExcluido)
            throws SQLException {
        return existeValor("correo", correo, idExcluido);
    }

    public int contarAdministradoresActivos() throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM usuario
                WHERE rol = 'ADMINISTRADOR' AND estado = TRUE
                """;
        try (Connection conexion = abrirConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    private boolean existeValor(String columna, String valor, int idExcluido)
            throws SQLException {
        // "columna" nunca viene del usuario; solamente se llama con las dos
        // constantes anteriores. El valor sí se parametriza.
        String sql = "SELECT COUNT(*) FROM usuario WHERE " + columna + " = ?"
                + (idExcluido > 0 ? " AND id_usuario <> ?" : "");
        try (Connection conexion = abrirConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, valor);
            if (idExcluido > 0) {
                sentencia.setInt(2, idExcluido);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private List<Usuario> consultarUsuarios(
            Connection conexion,
            FiltroSql filtro,
            int limite,
            int desplazamiento,
            boolean paginar) throws SQLException {
        String sql = """
                SELECT id_usuario, nombre, apellido, usuario, correo, rol,
                       estado, fecha_creacion
                FROM usuario
                """ + filtro.where() + " ORDER BY id_usuario ASC"
                + (paginar ? " LIMIT ? OFFSET ?" : "");

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            int indice = colocarParametros(sentencia, filtro.parametros());
            if (paginar) {
                sentencia.setInt(indice++, limite);
                sentencia.setInt(indice, desplazamiento);
            }

            List<Usuario> usuarios = new ArrayList<>();
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    usuarios.add(mapear(resultado));
                }
            }
            return usuarios;
        }
    }

    private int contarFiltrados(Connection conexion, FiltroSql filtro)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario " + filtro.where();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            colocarParametros(sentencia, filtro.parametros());
            try (ResultSet resultado = sentencia.executeQuery()) {
                resultado.next();
                return resultado.getInt(1);
            }
        }
    }

    private FiltroSql construirFiltro(
            String busqueda,
            String rol,
            Boolean estado) {
        List<String> condiciones = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();

        if (busqueda != null && !busqueda.isBlank()) {
            condiciones.add("""
                    LOWER(CONCAT_WS(' ', nombre, apellido, usuario, correo))
                    LIKE LOWER(?)
                    """.trim());
            parametros.add("%" + busqueda.trim() + "%");
        }
        if (rol != null && !rol.isBlank()) {
            condiciones.add("rol = ?");
            parametros.add(rol);
        }
        if (estado != null) {
            condiciones.add("estado = ?");
            parametros.add(estado);
        }

        String where = condiciones.isEmpty()
                ? ""
                : "WHERE " + String.join(" AND ", condiciones);
        return new FiltroSql(where, parametros);
    }

    private int colocarParametros(
            PreparedStatement sentencia,
            List<Object> parametros) throws SQLException {
        int indice = 1;
        for (Object parametro : parametros) {
            if (parametro instanceof Boolean valorBooleano) {
                sentencia.setBoolean(indice++, valorBooleano);
            } else {
                sentencia.setString(indice++, String.valueOf(parametro));
            }
        }
        return indice;
    }

    private void colocarDatosComunes(
            PreparedStatement sentencia,
            Usuario usuario) throws SQLException {
        sentencia.setString(1, usuario.getNombre());
        sentencia.setString(2, usuario.getApellido());
        sentencia.setString(3, usuario.getNombreUsuario());
        sentencia.setString(4, usuario.getCorreo());
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        Timestamp fecha = resultado.getTimestamp("fecha_creacion");
        return new Usuario(
                resultado.getInt("id_usuario"),
                resultado.getString("nombre"),
                resultado.getString("apellido"),
                resultado.getString("usuario"),
                resultado.getString("correo"),
                resultado.getString("rol"),
                resultado.getBoolean("estado"),
                fecha != null ? fecha.toLocalDateTime() : null);
    }

    private Connection abrirConexion() throws SQLException {
        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            throw new SQLException("No fue posible conectarse a Waldonalds.");
        }
        return conexion;
    }

    private record FiltroSql(String where, List<Object> parametros) {
    }
}
