package Modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Producto directo o ingrediente que sí posee existencias controlables. */
public class ArticuloInventario {

    private final String tipo;
    private final int id;
    private final String nombre;
    private final String categoria;
    private final String unidad;
    private final BigDecimal stockActual;
    private final BigDecimal stockMinimo;
    private final boolean activo;
    private final LocalDateTime ultimoMovimiento;

    public ArticuloInventario(String tipo, int id, String nombre,
            String categoria, String unidad, BigDecimal stockActual,
            BigDecimal stockMinimo, boolean activo,
            LocalDateTime ultimoMovimiento) {
        this.tipo = tipo;
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidad = unidad;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.ultimoMovimiento = ultimoMovimiento;
    }

    public String getTipo() { return tipo; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUnidad() { return unidad; }
    public BigDecimal getStockActual() { return stockActual; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getUltimoMovimiento() { return ultimoMovimiento; }

    public boolean esProducto() {
        return "PRODUCTO".equals(tipo);
    }

    public String getTipoVisible() {
        return esProducto() ? "Producto directo" : "Ingrediente";
    }

    public String getCodigoVisible() {
        return (esProducto() ? "P-" : "I-") + String.format("%04d", id);
    }

    public String getEstadoVisible() {
        if (!activo) return "Inactivo";
        if (stockActual.signum() <= 0) return "Agotado";
        if (stockMinimo.signum() > 0
                && stockActual.compareTo(stockMinimo) <= 0) {
            return "Stock bajo";
        }
        return "Disponible";
    }

    @Override
    public String toString() {
        return getCodigoVisible() + " · " + nombre;
    }
}
