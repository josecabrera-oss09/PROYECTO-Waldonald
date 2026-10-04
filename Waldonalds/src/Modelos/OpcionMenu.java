package Modelos;

import java.math.BigDecimal;
import java.util.List;

public record OpcionMenu(int idOpcion, String nombre, BigDecimal incrementoPrecio,
        boolean predeterminada, List<ComponenteMenu> componentes) {
    public OpcionMenu {
        incrementoPrecio = incrementoPrecio == null ? BigDecimal.ZERO : incrementoPrecio;
        componentes = List.copyOf(componentes == null ? List.of() : componentes);
    }
    @Override public String toString() {
        return nombre + (incrementoPrecio.signum() > 0
                ? "  + Q" + incrementoPrecio.setScale(2).toPlainString() : "");
    }
}
