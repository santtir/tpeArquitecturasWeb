package tpe.dao.jdbc;

import tpe.dao.ClienteDAO;
import tpe.factory.ConnectionManager;
import tpe.model.Cliente;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOJDBC implements ClienteDAO {
    /** Inserta un nuevo cliente y asigna el id generado al objeto. */
    @Override
    public Cliente create(Cliente c) throws SQLException {
        String sql = "INSERT INTO Cliente(nombre, email) VALUES(?, ?)";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCliente(rs.getInt(1));
            }
            return c;
        }
    }

    /** Busca por id. Devuelve Optional.empty() si no existe. */
    @Override
    public Optional<Cliente> findById(int id) throws SQLException {
        String sql = "SELECT idCliente, nombre, email FROM Cliente WHERE idCliente=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        }
    }

    /** Lista todos los clientes. Puede devolver lista vacía. */
    @Override
    public List<Cliente> findAll() throws SQLException {
        String sql = "SELECT idCliente, nombre, email FROM Cliente ORDER BY idCliente";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    /** Actualiza nombre y email del cliente indicado por id. */
    @Override
    public void update(Cliente c) throws SQLException {
        String sql = "UPDATE Cliente SET nombre=?, email=? WHERE idCliente=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getEmail());
            ps.setInt(3, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    /** Elimina por id. Falla si hay facturas referenciando al cliente. */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Cliente WHERE idCliente=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Mapeo ResultSet -> Cliente. */
    private Cliente map(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("idCliente"),
                rs.getString("nombre"),
                rs.getString("email")
        );
    }

}
