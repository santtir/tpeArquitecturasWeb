package tpe.dao;

import tpe.model.Factura;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de la entidad Factura
 * */
public interface FacturaDAO {
    Factura create(Factura f) throws SQLException;
    List<Factura> findAll() throws SQLException;
    Optional<Factura> findById(int id) throws SQLException;
    List<Factura> findByCliente(int idCliente) throws SQLException;
    void delete(int id) throws SQLException;
}
