package Modelos;

import java.util.List;

public record PaginaProductos(
        List<Producto> productos,
        int totalRegistros
) {
}
