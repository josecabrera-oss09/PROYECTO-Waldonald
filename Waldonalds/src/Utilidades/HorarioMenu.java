package Utilidades;

import java.time.LocalTime;

/** Reglas de disponibilidad del menú según la hora local del restaurante. */
public final class HorarioMenu {

    /** A las 11:00 termina el horario de desayuno. */
    public static final LocalTime INICIO_ALMUERZO = LocalTime.of(11, 0);

    private HorarioMenu() {
    }

    /**
     * Devuelve el valor que utiliza producto.disponibilidad_menu para la hora
     * indicada. El almuerzo/cena empieza a las 11:00; antes de esa hora es
     * desayuno.
     */
    public static String disponibilidadActual(LocalTime hora) {
        if (hora == null) {
            throw new IllegalArgumentException("La hora no puede ser nula.");
        }
        return hora.isBefore(INICIO_ALMUERZO) ? "DESAYUNO" : "ALMUERZO";
    }

    public static boolean estaDisponible(String disponibilidad, LocalTime hora) {
        if (disponibilidad == null) {
            return false;
        }
        return "TODO_DIA".equalsIgnoreCase(disponibilidad)
                || disponibilidadActual(hora).equalsIgnoreCase(disponibilidad);
    }
}
