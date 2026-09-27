package Modelos;

import java.math.BigDecimal;

public record LineaPedido(int idProducto, String nombre, BigDecimal precio, int cantidad) {
    public LineaPedido {
        if (idProducto <= 0 || nombre == null || precio == null || precio.signum() < 0
                || precio.scale() > 2 || cantidad < 1 || cantidad > 999) {
            throw new IllegalArgumentException("Producto, precio o cantidad inválidos.");
        }
    }

    public BigDecimal subtotal() { return precio.multiply(BigDecimal.valueOf(cantidad)); }
}
