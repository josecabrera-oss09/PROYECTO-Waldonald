package Modelos;

import java.util.List;
import java.util.UUID;

public record ProductoPedido(String idInterno, int idProducto, String nombre,
        int cantidad, String tipoStock, List<ModificacionPedido> modificaciones) {
    public ProductoPedido {
        if (idInterno == null || idInterno.isBlank()) idInterno = UUID.randomUUID().toString();
        modificaciones = List.copyOf(modificaciones == null ? List.of() : modificaciones);
    }
    public ProductoPedido conModificaciones(List<ModificacionPedido> nuevas) {
        return new ProductoPedido(idInterno, idProducto, nombre, cantidad, tipoStock, nuevas);
    }
}
