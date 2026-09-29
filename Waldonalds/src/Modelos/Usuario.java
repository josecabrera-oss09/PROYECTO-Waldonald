package Modelos;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Representa una fila de la tabla usuario.
 * La contraseña se maneja aparte y nunca vuelve desde la base de datos.
 */
public class Usuario {
    private final int idUsuario;
    private final String nombre;
    private final String apellido;
    private final String nombreUsuario;
    private final String correo;
    private final String rol;
    private final boolean activo;
    private final LocalDateTime fechaCreacion;
    private final String turno;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

    public Usuario(int idUsuario, String nombre, String apellido,
            String nombreUsuario, String correo, String rol, boolean activo,
            LocalDateTime fechaCreacion) {
        this(idUsuario, nombre, apellido, nombreUsuario, correo, rol, activo,
                fechaCreacion, null, null, null);
    }

    public Usuario(int idUsuario, String nombre, String apellido,
            String nombreUsuario, String correo, String rol, boolean activo,
            LocalDateTime fechaCreacion, String turno, LocalTime horaInicio,
            LocalTime horaFin) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.turno = turno;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getTurno() { return turno; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public String getTurnoEtiqueta() {
        if (turno == null || horaInicio == null || horaFin == null) {
            return "Sin turno";
        }
        return (turno.equals("MANANA") ? "Mañana" : "Tarde")
                + " (" + horaInicio + "–" + horaFin + ")";
    }
    public String getNombreCompleto() { return nombre + " " + apellido; }
}
