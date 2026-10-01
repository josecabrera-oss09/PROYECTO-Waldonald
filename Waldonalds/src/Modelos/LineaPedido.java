package Modelos;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

public record LineaPedido(int idProducto, String nombre, BigDecimal precio, int cantidad,
        Set<String> quitar, Set<String> agregar, String tamano) {
    public LineaPedido(int idProducto, String nombre, BigDecimal precio, int cantidad) {
        this(idProducto, nombre, precio, cantidad, Set.of(), Set.of(), "");
    }

    public LineaPedido {
        if (idProducto <= 0 || nombre == null || precio == null || precio.signum() < 0
                || precio.scale() > 2 || cantidad < 1 || cantidad > 999) {
            throw new IllegalArgumentException("Producto, precio o cantidad inválidos.");
        }
        quitar = Set.copyOf(new LinkedHashSet<>(quitar == null ? Set.of() : quitar));
        agregar = Set.copyOf(new LinkedHashSet<>(agregar == null ? Set.of() : agregar));
        tamano = tamano == null ? "" : tamano.trim();
    }

    public BigDecimal subtotal() { return precio.multiply(BigDecimal.valueOf(cantidad)); }
}
