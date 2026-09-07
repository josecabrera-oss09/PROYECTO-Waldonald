package Modelos;

import java.util.List;

/** Resultado de una página y cantidad total que coincide con los filtros. */
public record PaginaUsuarios(List<Usuario> usuarios, int totalRegistros) {

    public PaginaUsuarios {
        usuarios = List.copyOf(usuarios);
    }
}
