package Modelos;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private int idCategoria;
    private String categoria;

    private String nombre;
    private String descripcion;

    private BigDecimal precioBase;

    private String imagen;

    private String disponibilidadMenu;
    private String tamanoBebida;

    private boolean combo;

    private int stockActual;
    private int stockMinimo;

    private boolean activo;

    public Producto() {
    }

    public Producto(
            int idProducto,
            int idCategoria,
            String categoria,
            String nombre,
            String descripcion,
            BigDecimal precioBase,
            String imagen,
            String disponibilidadMenu,
            String tamanoBebida,
            boolean combo,
            int stockActual,
            int stockMinimo,
            boolean activo) {

        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.imagen = imagen;
        this.disponibilidadMenu = disponibilidadMenu;
        this.tamanoBebida = tamanoBebida;
        this.combo = combo;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDisponibilidadMenu() {
        return disponibilidadMenu;
    }

    public void setDisponibilidadMenu(String disponibilidadMenu) {
        this.disponibilidadMenu = disponibilidadMenu;
    }

    public String getTamanoBebida() {
        return tamanoBebida;
    }

    public void setTamanoBebida(String tamanoBebida) {
        this.tamanoBebida = tamanoBebida;
    }

    public boolean isCombo() {
        return combo;
    }

    public void setCombo(boolean combo) {
        this.combo = combo;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getTipo() {
        return combo ? "Combo" : "Producto";
    }

    public boolean tieneStockBajo() {

        return activo
                && stockMinimo > 0
                && stockActual <= stockMinimo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
