package tpe.schema;

import tpe.factory.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea o valida el esquema del ejercicio en la base actual.
 *
 * Tablas:
 *  - Cliente(idCliente PK, nombre, email)
 *  - Producto(idProducto PK, nombre, valor)
 *  - Factura(idFactura PK, idCliente FK -> Cliente)
 *  - Factura_Producto(idFactura FK, idProducto FK, cantidad, PK compuesta)
 *
 * Notas:
 *  - Se usa InnoDB y autocommit=false para ejecutar todo en una transacción.
 *  - CHECK(cantidad > 0) requiere MySQL 8+.
 *  - Las claves primarias y foráneas crean los índices necesarios en InnoDB (motor de almacenamiento de MySQL).
 */

public class SchemaMigrator {

    /**
     * Ejecuta la migración: crea tablas si no existen.
     * Llama a commit si todo salió bien o rollback si hubo error.
     */

    public void migrate(){
        ConnectionManager cm= ConnectionManager.getInstance();
        Connection cn=cm.getConnection();

        /**
         * Sentencias SQL para crear las tablas: Cliente, Producto, Factura, Factura_Producto.
         * Utilizo ENGINE=InnoDB para dejar claro el motor y tambien porque usamos autocommit=false y luego commit()/rollback() → esto solo funciona con InnoDB de la forma esperada. */
        try(Statement st = cn.createStatement()){
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Cliente (" +
                            "  idCliente INT PRIMARY KEY AUTO_INCREMENT," +
                            "  nombre    VARCHAR(500) NOT NULL," +
                            "  email     VARCHAR(150)" +
                            ") ENGINE=InnoDB"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Producto (" +
                            "  idProducto INT PRIMARY KEY AUTO_INCREMENT," +
                            "  nombre     VARCHAR(45)  NOT NULL," +
                            "  valor      FLOAT        NOT NULL" +
                            ") ENGINE=InnoDB"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Factura (" +
                            "  idFactura INT PRIMARY KEY AUTO_INCREMENT," +
                            "  idCliente INT NOT NULL," +
                            "  CONSTRAINT fk_factura_cliente " +
                            "    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) " +
                            "    ON UPDATE CASCADE ON DELETE RESTRICT" +
                            ") ENGINE=InnoDB"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Factura_Producto (" +
                            "  idFactura  INT NOT NULL," +
                            "  idProducto INT NOT NULL," +
                            "  cantidad   INT NOT NULL," +
                            "  CHECK (cantidad > 0)," +
                            "  PRIMARY KEY (idFactura, idProducto)," +
                            "  CONSTRAINT fk_fp_factura  FOREIGN KEY (idFactura)  REFERENCES Factura(idFactura) " +
                            "    ON UPDATE CASCADE ON DELETE CASCADE," +
                            "  CONSTRAINT fk_fp_producto FOREIGN KEY (idProducto) REFERENCES Producto(idProducto) " +
                            "    ON UPDATE CASCADE ON DELETE RESTRICT" +
                            ") ENGINE=InnoDB"
            );

            cm.commit();
            System.out.println("Esquema creado/validado correctamente.");

        }catch(SQLException e){
            cm.rollback();
            throw new RuntimeException("Error creando el esquema", e);
        }

    }

    /**
     * Vacía todas las tablas usando TRUNCATE en el orden correcto. Es un metodo auxiliar, lo uso para vaciar la DB para pruebas.
     *
     * Efectos:
     * - Deshabilita FOREIGN_KEY_CHECKS, trunca Factura_Producto, Factura, Producto y Cliente,
     *   y vuelve a habilitar FOREIGN_KEY_CHECKS.
     * - TRUNCATE en MySQL hace commit implícito; no puede revertirse con rollback().
     * - TRUNCATE reinicia los AUTO_INCREMENT de forma automática.
     *
     * Uso:
     * - Llamar antes de importar CSV cuando se desea empezar desde una BD vacía.
     *
     * Errores:
     * - Lanza RuntimeException si ocurre un error de acceso a la base de datos.
     *
     * @throws RuntimeException si falla alguna sentencia TRUNCATE o la comunicación con la BD
     */
    public void reset() {
        Connection cn = ConnectionManager.getInstance().getConnection();
        try (Statement st = cn.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            st.executeUpdate("TRUNCATE TABLE Factura_Producto");
            st.executeUpdate("TRUNCATE TABLE Factura");
            st.executeUpdate("TRUNCATE TABLE Producto");
            st.executeUpdate("TRUNCATE TABLE Cliente");
            st.execute("SET FOREIGN_KEY_CHECKS=1");
            System.out.println("Esquema vaciado (TRUNCATE).");
        } catch (SQLException e) {
            throw new RuntimeException("Error reseteando esquema (TRUNCATE)", e);
        }
    }

}
