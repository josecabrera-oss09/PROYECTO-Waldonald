package DAO;

import Conexion.Conexion;
import Modelos.Productos;

import java.sql.Connection;
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
}
