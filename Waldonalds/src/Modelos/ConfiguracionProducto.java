package Modelos;

import java.util.List;

public record ConfiguracionProducto(int idProducto, String nombre,
        String descripcion, String imagen, String disponibilidadMenu,
        List<PresentacionMenu> presentaciones) {
    public ConfiguracionProducto {
        presentaciones = List.copyOf(presentaciones == null ? List.of() : presentaciones);
    }
}
