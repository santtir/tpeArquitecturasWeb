package tpe.dao;


import tpe.model.Producto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Producto.
 */
public interface ProductoDAO {
    Producto create(Producto p) throws SQLException;
    Optional<Producto> findById(int id) throws SQLException;
    List<Producto> findAll() throws SQLException;
    void update(Producto p) throws SQLException;
    void delete(int id) throws SQLException;
}
