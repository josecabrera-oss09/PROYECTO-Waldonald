package Modelos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Una línea del carrito. Dos configuraciones distintas nunca se mezclan. */
public record LineaPedido(String idLinea, int idProducto, int idPresentacion,
        String nombre, String presentacion, BigDecimal precioBase,
        List<OpcionPedido> opciones, int cantidad) {
    public LineaPedido {
        if (idLinea == null || idLinea.isBlank()) idLinea = UUID.randomUUID().toString();
        if (idProducto <= 0 || idPresentacion <= 0 || nombre == null
                || presentacion == null || precioBase == null
                || precioBase.signum() < 0 || precioBase.scale() > 2
                || cantidad < 1 || cantidad > 999) {
            throw new IllegalArgumentException("Producto, presentación, precio o cantidad inválidos.");
        }
        opciones = List.copyOf(opciones == null ? List.of() : opciones);
    }

    public BigDecimal precio() {
        BigDecimal total = precioBase;
        for (OpcionPedido opcion : opciones) {
            total = total.add(opcion.precioExtra());
            for (ProductoPedido producto : opcion.productos()) {
                for (ModificacionPedido modificacion : producto.modificaciones())
                    total = total.add(modificacion.precioExtra());
            }
        }
        return total.setScale(2);
    }

    public BigDecimal subtotal() { return precio().multiply(BigDecimal.valueOf(cantidad)); }

    public LineaPedido conCantidad(int nuevaCantidad) {
        return new LineaPedido(idLinea, idProducto, idPresentacion, nombre,
                presentacion, precioBase, opciones, nuevaCantidad);
    }

    public String resumen() {
        StringBuilder texto = new StringBuilder(nombre).append(" · ").append(presentacion);
        opciones.stream().filter(o -> !o.grupo().startsWith("__"))
                .forEach(o -> texto.append(" | ").append(o.opcion()));
        long cambios = opciones.stream().flatMap(o -> o.productos().stream())
                .mapToLong(p -> p.modificaciones().size()).sum();
        if (cambios > 0) texto.append(" | ").append(cambios)
                .append(cambios == 1 ? " cambio" : " cambios");
        return texto.toString();
    }
}
