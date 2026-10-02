package Modelos;

import java.math.BigDecimal;
import java.util.List;

public record OpcionPedido(int idGrupo, int idOpcion, int numero,
        String grupo, String opcion, BigDecimal precioExtra,
        List<ProductoPedido> productos) {
    public OpcionPedido {
        precioExtra = precioExtra == null ? BigDecimal.ZERO : precioExtra;
        productos = List.copyOf(productos == null ? List.of() : productos);
    }
    public OpcionPedido conProductos(List<ProductoPedido> nuevos) {
        return new OpcionPedido(idGrupo, idOpcion, numero, grupo, opcion, precioExtra, nuevos);
    }
}
