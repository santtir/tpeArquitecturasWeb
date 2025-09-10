package tpe.dao.jdbc;

import tpe.dao.FacturaDAO;
import tpe.factory.ConnectionManager;
import tpe.model.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de FacturaDAO. No hace commit/rollback. */
public class FacturaDAOJDBC implements FacturaDAO {

    /** Inserta una factura para un cliente y asigna id generado. */
    @Override
    public Factura create(Factura f) throws SQLException {
        String sql = "INSERT INTO Factura(idCliente) VALUES(?)";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getIdCliente());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setIdFactura(rs.getInt(1));
            }
            return f;
        }
    }

    /**
     * Lista todas las facturas ordenadas por id.
     *
     * @return lista de facturas (puede ser vacía)
     * @throws SQLException si ocurre un error de acceso a datos
     */
    @Override
    public List<Factura> findAll() throws SQLException {
        String sql="SELECT * FROM Factura ORDER BY idFactura DESC";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<Factura> salida = new ArrayList<>();
            while (rs.next()) {
                salida.add(new Factura(rs.getInt("idFactura"), rs.getInt("idCliente")));
            }
            return salida;
        }

    }

    /** Busca factura por id. */
    @Override
    public Optional<Factura> findById(int id) throws SQLException {
        String sql = "SELECT idFactura, idCliente FROM Factura WHERE idFactura=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        }
    }

    /** Lista facturas de un cliente dado. */
    @Override
    public List<Factura> findByCliente(int idCliente) throws SQLException {
        String sql = "SELECT idFactura, idCliente FROM Factura WHERE idCliente=? ORDER BY idFactura";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Factura> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    /** Elimina una factura por id. Cascada borra sus renglones en Factura_Producto. */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Factura WHERE idFactura=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Mapeo ResultSet -> Factura. */
    private Factura map(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getInt("idFactura"),
                rs.getInt("idCliente")
        );
    }
}
