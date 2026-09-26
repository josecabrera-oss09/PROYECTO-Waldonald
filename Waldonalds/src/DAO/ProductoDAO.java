package DAO;

import Conexion.Conexion;
import Modelos.Productos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import Utilidades.HorarioMenu;

public class ProductoDAO {

    public List<Productos> obtenerPorCategoria(int idCategoria) {
        return obtenerPorCategoria(idCategoria, LocalTime.now());
    }

    /**
     * Busca todos los productos activos de la categoría y marca cuáles están
     * disponibles para la hora indicada. Los productos de todo el día siempre
     * aparecen como disponibles.
     * La hora explícita permite comprobar el filtro sin insertar productos.
     */
    public List<Productos> obtenerPorCategoria(int idCategoria, LocalTime hora) {

        List<Productos> productos = new ArrayList<>();

        String sql = """
                SELECT
                    id_producto,
                    id_categoria,
                    nombre,
                    descripcion,
                    precio_base,
                    imagen,
                    disponibilidad_menu
                FROM producto
                WHERE id_categoria = ?
                AND estado = TRUE
                """;

        try (
                Connection conexion = Conexion.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Productos producto = new Productos(
                            rs.getInt("id_producto"),
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio_base"),
                            rs.getString("imagen"),
                            HorarioMenu.estaDisponible(
                                    rs.getString("disponibilidad_menu"), hora)
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
}
