package tpe.model;


/**
 * Entidad Producto del dominio.
 * Campos según el enunciado: id, nombre (VARCHAR 45), valor (FLOAT).
 */

public class Producto {
    private Integer idProducto;
    private String nombre;
    private float valor;

    /**
     * Constructores:
     *
     * Son 2 debido a que el id tiene dos estados posibles en el ciclo de vida del objeto:
     * Antes de guardar en la DB: todavia no existe el id entonces se le setea cuando el DAO hace el INSERT.
     * El otro estado del objeto es cuando ya proviene de la DB, entonces ya tiene id lo usa el DAO al mapear un ResultSet, o cuando se quiere actualizar un registro existente
     *
     * @param idProducto ,
     * @param nombre ,
     * @param valor */

    public Producto(Integer idProducto, String nombre, float valor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.valor = valor;
    }

    public Producto(String nombre, float valor) {
        this(null, nombre, valor);
    }

    public Integer getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public float getValor() { return valor; }

    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setValor(float valor) { this.valor = valor; }

    @Override
    public String toString() {
        return "Producto{id=" + idProducto + ", nombre='" + nombre + "', valor=" + valor + "}";
    }
}
