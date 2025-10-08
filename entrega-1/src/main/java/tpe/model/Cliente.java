package tpe.model;

/**
 * Representa un cliente. idCliente puede ser null antes de insertar.
 */
public class Cliente {
    private Integer idCliente;
    private String nombre;
    private String email;

    // Constructor “existente” (puede venir de la BD).
    public Cliente(Integer idCliente, String nombre, String email) {
        this.idCliente = idCliente;   // puede ser null sin problemas
        this.nombre = nombre;
        this.email = email;
    }

    // Constructor “nuevo” (sin id aún)
    public Cliente(String nombre, String email) {
        this(null, nombre, email);
    }

    public Integer getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }

    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Cliente{idCliente=" + idCliente + ", nombre='" + nombre + "', email='" + email + "'}";
    }
}