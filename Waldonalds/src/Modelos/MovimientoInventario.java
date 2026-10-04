package Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventario(
        int id,
        LocalDateTime fecha,
        String tipoArticulo,
        String articulo,
        String tipoMovimiento,
        BigDecimal cantidad,
        String motivo,
        Integer idPedido) {
}
