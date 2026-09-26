package Modelos;

public class Productos {

    private int idProducto;
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagen;
    private final boolean disponibleHorario;

    public Productos(
            int idProducto,
            int idCategoria,
            String nombre,
            String descripcion,
            double precio,
            String imagen
    ) {
        this(idProducto, idCategoria, nombre, descripcion, precio, imagen, true);
    }

    public Productos(
            int idProducto,
            int idCategoria,
            String nombre,
            String descripcion,
            double precio,
            String imagen,
            boolean disponibleHorario
    ) {

        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.disponibleHorario = disponibleHorario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public boolean isDisponibleHorario() {
        return disponibleHorario;
    }
}
