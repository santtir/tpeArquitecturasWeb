package tpe.model;

/**
 * Renglón de una factura (tabla puente Factura_Producto).
 * Clave primaria compuesta: (idFactura, idProducto).
 */
public class FacturaProducto {
    private Integer idFactura;
    private Integer idProducto;
    private int cantidad;

    public FacturaProducto(Integer idFactura, Integer idProducto, int cantidad) {
        this.idFactura = idFactura;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }
    /**getters y setters simples:*/
    public int getIdFactura() { return idFactura; }
    public int getIdProducto() { return idProducto; }
    public int getCantidad() { return cantidad; }

    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return "FacturaProducto{idFactura=" + idFactura +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad + "}";
    }
}
