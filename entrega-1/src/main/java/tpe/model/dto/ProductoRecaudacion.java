package tpe.model.dto;

/** Resultado de la consulta: producto que más recaudó. */
public class ProductoRecaudacion {
    private final int idProducto;
    private final String nombre;
    private final float valorUnitario;
    private final long unidadesVendidas;
    private final float recaudacion;

    public ProductoRecaudacion(int idProducto, String nombre, float valorUnitario,
                               long unidadesVendidas, float recaudacion) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.valorUnitario = valorUnitario;
        this.unidadesVendidas = unidadesVendidas;
        this.recaudacion = recaudacion;
    }

    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public float getValorUnitario() { return valorUnitario; }
    public long getUnidadesVendidas() { return unidadesVendidas; }
    public float getRecaudacion() { return recaudacion; }

    @Override
    public String toString() {
        return "ProductoRecaudacion{id=" + idProducto + ", nombre='" + nombre + '\'' +
                ", valor=" + valorUnitario + ", unidades=" + unidadesVendidas +
                ", recaudacion=" + recaudacion + '}';
    }
}