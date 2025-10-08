package tpe.dao;

import tpe.model.FacturaProducto;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO para la tabla Factura_Producto (renglones de factura).
 */

public interface FacturaProductoDAO {
    void create(FacturaProducto fp) throws SQLException;
    List<FacturaProducto> findByFactura(int idFactura) throws SQLException;
    void updateCantidad(int idFactura, int idProducto, int cantidad) throws SQLException;
    void deleteItem(int idFactura, int idProducto) throws SQLException;
    void deleteByFactura(int idFactura) throws SQLException;
}
