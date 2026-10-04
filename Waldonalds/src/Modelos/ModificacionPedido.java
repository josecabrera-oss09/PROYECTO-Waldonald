package Modelos;

import java.math.BigDecimal;

public record ModificacionPedido(int idProductoIngrediente, int idIngrediente,
        String tipo, String ingrediente, BigDecimal cantidad,
        int veces, BigDecimal precioExtra) {
    public ModificacionPedido {
        if (!"SIN".equals(tipo) && !"EXTRA".equals(tipo))
            throw new IllegalArgumentException("Modificación inválida.");
        cantidad = cantidad == null ? BigDecimal.ZERO : cantidad;
        precioExtra = precioExtra == null ? BigDecimal.ZERO : precioExtra;
        if (veces < 1) veces = 1;
    }
}
