package Modelos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SolicitudPago(String clave, int usuario, List<LineaPedido> lineas,
        String servicio, String metodo, BigDecimal recibido, String referencia) {
    public SolicitudPago {
        UUID.fromString(clave);
        lineas = List.copyOf(lineas);
        if (usuario <= 0 || lineas.isEmpty()) throw new IllegalArgumentException("Inicie sesión y agregue productos.");
        if (!List.of("COMER_AQUI", "PARA_LLEVAR").contains(servicio)
                || !List.of("EFECTIVO", "TARJETA").contains(metodo)) {
            throw new IllegalArgumentException("Servicio o método inválido.");
        }
        if (lineas.stream().map(LineaPedido::idLinea).distinct().count() != lineas.size()) {
            throw new IllegalArgumentException("Hay líneas repetidas en el pedido.");
        }
        BigDecimal total = lineas.stream().map(LineaPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.signum() <= 0 || total.compareTo(new BigDecimal("99999999.99")) > 0
                || recibido == null || recibido.scale() > 2 || recibido.compareTo(total) < 0
                || recibido.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("Ingrese un monto válido, con hasta dos decimales, que cubra el total.");
        }
        referencia = referencia == null ? "" : referencia.trim();
        if (referencia.length() > 100 || (metodo.equals("TARJETA")
                && (referencia.isEmpty() || recibido.compareTo(total) != 0))) {
            throw new IllegalArgumentException("Confirme el pago externo e ingrese su referencia (máximo 100 caracteres).");
        }
    }

    public BigDecimal total() {
        return lineas.stream().map(LineaPedido::subtotal).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    public BigDecimal cambio() { return recibido.subtract(total()); }
}
