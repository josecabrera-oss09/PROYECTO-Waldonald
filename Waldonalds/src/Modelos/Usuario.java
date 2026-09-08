package Modelos;

import java.time.LocalDateTime;

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

    public Usuario(int idUsuario, String nombre, String apellido,
            String nombreUsuario, String correo, String rol, boolean activo,
            LocalDateTime fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getNombreCompleto() { return nombre + " " + apellido; }
}
