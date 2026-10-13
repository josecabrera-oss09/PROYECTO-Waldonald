import Utilidades.DialogosCajero;

public final class DialogosCajeroPrueba {
    private DialogosCajeroPrueba() { }

    public static void main(String[] args) {
        DialogosCajero.confirmar(
                null,
                "Cancelar pedido",
                "¿Cancelar el pedido #5?\nSe devolverán sus existencias al inventario."
        );
    }
}
