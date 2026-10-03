package Modelos;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private int idCategoria;
    private String categoria;
    private String subCategoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase = BigDecimal.ZERO;
    private String imagen;
    private String disponibilidadMenu = "TODO_DIA";
    private String tipoStock = "DIRECTO";
    private boolean personalizable = true;
    private int stockActual;
    private int stockMinimo;
    private boolean activo = true;

    public Producto() {
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getSubCategoria() { return subCategoria; }
    public void setSubCategoria(String subCategoria) {
        this.subCategoria = subCategoria;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getDisponibilidadMenu() { return disponibilidadMenu; }
    public void setDisponibilidadMenu(String disponibilidadMenu) {
        this.disponibilidadMenu = disponibilidadMenu;
    }

    public String getTipoStock() { return tipoStock; }
    public void setTipoStock(String tipoStock) { this.tipoStock = tipoStock; }

    public boolean isPersonalizable() { return personalizable; }
    public void setPersonalizable(boolean personalizable) {
        this.personalizable = personalizable;
    }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getTipo() {
        return switch (tipoStock == null ? "" : tipoStock) {
            case "RECETA" -> "Preparado por receta";
            case "NINGUNO" -> "Sin control de stock";
            default -> "Stock directo";
        };
    }

    public boolean tieneStockBajo() {
        return activo && !"NINGUNO".equals(tipoStock)
                && stockMinimo > 0 && stockActual <= stockMinimo;
    }

    @Override
    public String toString() { return nombre; }
}
