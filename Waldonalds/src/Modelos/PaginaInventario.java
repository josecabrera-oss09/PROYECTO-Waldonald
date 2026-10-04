package Modelos;

import java.util.List;

public record PaginaInventario(
        List<ArticuloInventario> articulos,
        int totalRegistros) {
}
