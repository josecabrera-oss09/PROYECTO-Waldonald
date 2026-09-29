package Modelos;

import java.util.List;

public record PaginaIngredientes(List<Ingrediente> ingredientes, int totalRegistros) {
}
