package Modelos;

public class Productos {

    private int idProducto;
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagen;

    public Productos(
            int idProducto,
            int idCategoria,
            String nombre,
            String descripcion,
            double precio,
            String imagen
    ) {

        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
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
}