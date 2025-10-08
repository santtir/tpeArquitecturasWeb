package tpe.model;
/**
 * Entidad Factura
 *
 * Atributos: idFactura int, idCliente int
 * */
public class Factura {
    private Integer idFactura;  // puede ser null antes de insertar (AUTO_INCREMENT en BD)
    private Integer idCliente;

    /**
     * Crea una factura existente (por ejemplo, recuperada desde la BD).
     *
     * @param idFactura identificador de la factura (no null para entidades existentes)
     * @param idCliente identificador del cliente asociado
     */
    public Factura(Integer idFactura, int idCliente) {
        this.idFactura = idFactura;
        this.idCliente = idCliente;
    }

    /**
     * Crea una factura nueva que aún no tiene id asignado.
     * Se usa antes del INSERT; luego el DAO setea el id generado.
     *
     * @param idCliente identificador del cliente asociado
     */
    public Factura(Integer idCliente) {
        this(null, idCliente);
    }

    // Getters y setters simples
    public Integer getIdFactura() { return idFactura; }
    public int getIdCliente() { return idCliente; }

    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    @Override
    public String toString() {
        return "Factura{idFactura=" + idFactura + ", idCliente=" + idCliente + "}";
    }
}
