package tpe.dao.jdbc;

import tpe.dao.FacturaProductoDAO;
import tpe.factory.ConnectionManager;
import tpe.model.FacturaProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de FacturaProductoDAO.
 */
public class FacturaProductoDAOJDBC implements FacturaProductoDAO {
    /** Inserta renglón de factura. */
    @Override
    public void create(FacturaProducto fp) throws SQLException {
        String sql = "INSERT INTO Factura_Producto(idFactura, idProducto, cantidad) VALUES(?, ?, ?)";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fp.getIdFactura());
            ps.setInt(2, fp.getIdProducto());
            ps.setInt(3, fp.getCantidad());
            ps.executeUpdate();
        }
    }

    /** Lista renglones por factura. */
    @Override
    public List<FacturaProducto> findByFactura(int idFactura) throws SQLException {
        String sql = "SELECT idFactura, idProducto, cantidad FROM Factura_Producto WHERE idFactura=? ORDER BY idProducto";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            try (ResultSet rs = ps.executeQuery()) {
                List<FacturaProducto> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    /** Actualiza cantidad de un renglón. */
    @Override
    public void updateCantidad(int idFactura, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE Factura_Producto SET cantidad=? WHERE idFactura=? AND idProducto=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idFactura);
            ps.setInt(3, idProducto);
            ps.executeUpdate();
        }
    }

    /** Elimina un renglón (PK compuesta). */
    @Override
    public void deleteItem(int idFactura, int idProducto) throws SQLException {
        String sql = "DELETE FROM Factura_Producto WHERE idFactura=? AND idProducto=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    /** Elimina todos los renglones de una factura. */
    @Override
    public void deleteByFactura(int idFactura) throws SQLException {
        String sql = "DELETE FROM Factura_Producto WHERE idFactura=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.executeUpdate();
        }
    }

    /** Mapea ResultSet a FacturaProducto. */
    private FacturaProducto map(ResultSet rs) throws SQLException {
        return new FacturaProducto(
                rs.getInt("idFactura"),
                rs.getInt("idProducto"),
                rs.getInt("cantidad")
        );
    }
}
