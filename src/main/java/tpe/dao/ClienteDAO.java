package tpe.dao;

import tpe.model.Cliente;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Cliente.
 */
public interface ClienteDAO {
    Cliente create (Cliente c) throws SQLException;
    Optional<Cliente> findById(int id) throws SQLException;
    List<Cliente> findAll() throws SQLException;
    void update (Cliente c) throws SQLException;
    void delete (int id) throws SQLException;

}
