package Modelos;

/** Valores mostrados en las cuatro tarjetas superiores. */
public record ResumenUsuarios(
        int total,
        int administradoresActivos,
        int cajerosActivos,
        int inactivos) {
}
