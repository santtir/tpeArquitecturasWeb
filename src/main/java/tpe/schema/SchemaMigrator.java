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
}
