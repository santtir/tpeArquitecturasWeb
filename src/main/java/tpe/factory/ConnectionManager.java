package tpe.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * ConnectionManager (patrón Singleton).
 *
 * - Mantiene una única Connection reutilizable.
 * - Configura autocommit=false para permitir control explícito de transacciones
 *   mediante commit() y rollback().
 * - Si la conexión se cierra o queda inválida por inactividad, la reabre automáticamente.
 *
 *
 */

public final class ConnectionManager {


    /**
     * Instancia única del singleton.
     * El modificador volatile garantiza visibilidad de memoria con double-checked locking.
     */
    private static volatile ConnectionManager instance;

    /** Conexión compartida y reutilizada. */
    private Connection connection;


    private static final String URL =
            "jdbc:mysql://localhost:3306/bd_pruebas"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "appuser";
    private static final String PASSWORD = "app123";

    /**
     * Constructor privado. Registra el driver y abre la conexión inicial
     * con autocommit=false para manejar transacciones manualmente.
     *
     * @throws RuntimeException si no se encuentra el driver o falla la conexión inicial.
     */
    private ConnectionManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            reconnect();
            System.out.println("conexión MySQL abierta");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("No se pudo abrir la conexión a MySQL", e);
        }
    }
    /**
     * Reabre la conexión y establece autocommit=false.
     * No llamar desde fuera de la clase. Usar getConnection().
     *
     * @throws SQLException si no puede establecer la conexión.
     */
    private void reconnect() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        this.connection.setAutoCommit(false); // listo para transacciones
    }
    /**
     * Devuelve la instancia única del manejador de conexiones.
     * Implementa double-checked locking para minimizar la sincronización.
     *
     * @return instancia única de ConnectionManager.
     */
    public static ConnectionManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) instance = new ConnectionManager();
            }
        }
        return instance;
    }
    /**
     * Obtiene una conexión JDBC válida. Si la conexión actual está cerrada
     * o no es válida, intenta reabrirla.
     *
     * @return conexión lista para usar con autocommit=false.
     * @throws RuntimeException si no puede obtener una conexión válida.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                reconnect();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo la conexión", e);
        }
    }
    /**
     * Confirma (commit) la transacción en curso sobre la conexión compartida.
     *
     * @throws RuntimeException si ocurre un error durante el commit.
     */
    public void commit(){
        try { connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e); }
    }

    /**
     * Revierte (rollback) la transacción en curso sobre la conexión compartida.
     * Los errores durante el rollback se silencian al ser parte de un fallo anterior.
     */
    public void rollback(){
        try { connection.rollback();
        } catch (SQLException ignored) {}
    }
}