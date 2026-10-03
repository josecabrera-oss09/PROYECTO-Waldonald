package Modelos;

import java.math.BigDecimal;

public record IngredienteProducto(int idProductoIngrediente, int idIngrediente,
        String nombre, BigDecimal cantidadDefault, boolean permiteQuitar,
        boolean permiteExtra, BigDecimal cantidadExtra, BigDecimal precioExtra,
        int maxExtras) {
    public IngredienteProducto {
        cantidadDefault = cantidadDefault == null ? BigDecimal.ZERO : cantidadDefault;
        cantidadExtra = cantidadExtra == null ? BigDecimal.ONE : cantidadExtra;
        precioExtra = precioExtra == null ? BigDecimal.ZERO : precioExtra;
    }
}
