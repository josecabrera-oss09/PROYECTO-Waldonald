package Modelos;

import java.util.List;

public record ComponenteMenu(int idProducto, String nombre, int cantidad,
        String tipoStock, boolean personalizable,
        String disponibilidadMenu, List<IngredienteProducto> ingredientes) {
    public ComponenteMenu {
        ingredientes = List.copyOf(ingredientes == null ? List.of() : ingredientes);
    }
}
