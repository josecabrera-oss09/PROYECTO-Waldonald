package Modelos;

import java.util.List;

public record GrupoMenu(int idGrupo, String nombre, int minimo, int maximo,
        boolean permiteRepetir, boolean visible, boolean permitePersonalizar,
        List<OpcionMenu> opciones) {
    public GrupoMenu {
        opciones = List.copyOf(opciones == null ? List.of() : opciones);
    }
    @Override public String toString() { return nombre; }
}
