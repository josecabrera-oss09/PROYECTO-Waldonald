package Modelos;

import java.math.BigDecimal;
import java.util.List;

public record PresentacionMenu(int idPresentacion, int idProductoPrincipal,
        String nombre, String tipo, BigDecimal precio, boolean predeterminada,
        List<GrupoMenu> grupos) {
    public PresentacionMenu {
        precio = precio == null ? BigDecimal.ZERO : precio;
        grupos = List.copyOf(grupos == null ? List.of() : grupos);
    }
    @Override public String toString() { return nombre; }
}
