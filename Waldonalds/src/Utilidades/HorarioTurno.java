package Utilidades;

import java.time.LocalTime;

/** Horarios asignados a los turnos disponibles para cajeros. */
public final class HorarioTurno {
    public static final String MANANA = "MANANA";
    public static final String TARDE = "TARDE";

    public static final LocalTime INICIO_MANANA = LocalTime.of(6, 0);
    public static final LocalTime FIN_MANANA = LocalTime.of(14, 0);
    public static final LocalTime INICIO_TARDE = LocalTime.of(14, 0);
    public static final LocalTime FIN_TARDE = LocalTime.of(22, 0);

    private HorarioTurno() {
    }

    public static boolean estaEnTurno(
            String turno,
            LocalTime inicio,
            LocalTime fin,
            LocalTime ahora) {
        if (turno == null || inicio == null || fin == null
                || ahora == null || inicio.equals(fin)) {
            return false;
        }
        if (inicio.isBefore(fin)) {
            return !ahora.isBefore(inicio) && ahora.isBefore(fin);
        }
        // También admite turnos que cruzan la medianoche.
        return !ahora.isBefore(inicio) || ahora.isBefore(fin);
    }

    public static String etiqueta(String turno) {
        return switch (turno == null ? "" : turno) {
            case MANANA -> "Mañana (06:00–14:00)";
            case TARDE -> "Tarde (14:00–22:00)";
            default -> "Sin turno";
        };
    }
}
