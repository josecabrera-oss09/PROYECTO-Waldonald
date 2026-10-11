package DAO;

import Conexion.Conexion;
import java.sql.*;
import java.util.*;

/** Catálogo mínimo de NITs para facturación rápida; nunca almacena datos de tarjeta. */
public final class NitDAO {
    @FunctionalInterface public interface Conexiones { Connection abrir() throws SQLException; }
    private final Conexiones conexiones;
    public NitDAO() { this(Conexion::conectar); }
    public NitDAO(Conexiones conexiones) { this.conexiones = conexiones; }

    public List<String> sugerencias(String texto) throws SQLException {
        String prefijo = texto == null ? "" : texto.trim().toUpperCase();
        if (prefijo.isEmpty()) return List.of();
        try (Connection c = conexiones.abrir();
             PreparedStatement s = c.prepareStatement(
                     "SELECT nit FROM nit_cliente WHERE nit LIKE ? ORDER BY ultima_utilizacion DESC LIMIT 8")) {
            s.setString(1, prefijo + "%");
            try (ResultSet r = s.executeQuery()) {
                List<String> resultado = new ArrayList<>();
                while (r.next()) resultado.add(r.getString(1));
                return resultado;
            }
        }
    }
}
