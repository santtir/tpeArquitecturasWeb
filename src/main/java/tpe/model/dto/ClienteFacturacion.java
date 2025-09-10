package tpe.model.dto;

/** Resultado: cliente + total facturado. */
public class ClienteFacturacion {
    private final int idCliente;
    private final String nombre;
    private final String email;
    private final float totalFacturado;

    public ClienteFacturacion(int idCliente, String nombre, String email, float totalFacturado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.totalFacturado = totalFacturado;
    }

    public int getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public float getTotalFacturado() { return totalFacturado; }

    @Override
    public String toString() {
        return "ClienteFacturacion{idCliente=" + idCliente + ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' + ", total=" + totalFacturado + "}";
    }
}
