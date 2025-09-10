package tpe.dao.jdbc;

import tpe.dao.ReportesDAO;
import tpe.factory.ConnectionManager;
import tpe.model.dto.ClienteFacturacion;
import tpe.model.dto.ProductoRecaudacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de reportes. */
public class ReportesDAOJDBC implements ReportesDAO {

    /** Producto que más recaudó (SUM(cantidad) * valor). */
    @Override
    public Optional<ProductoRecaudacion> productoQueMasRecaudo() throws SQLException {
        String sql =
                "SELECT p.idProducto, p.nombre, p.valor, " +
                        "SUM(fp.cantidad) AS unidades, " +
                        "SUM(fp.cantidad) * p.valor  AS recaudacion " +
                        "FROM Producto p " +
                        "JOIN Factura_Producto fp ON fp.idProducto = p.idProducto " +
                        "GROUP BY p.idProducto, p.nombre, p.valor " +
                        "ORDER BY recaudacion DESC " +
                        "LIMIT 1";

        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) return Optional.empty();

            ProductoRecaudacion pr = new ProductoRecaudacion(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getFloat("valor"),
                    rs.getLong("unidades"),
                    rs.getFloat("recaudacion")
            );
            return Optional.of(pr);
        }
    }

    /** Lista clientes por facturación total (desc). Incluye clientes sin ventas con total = 0. */
    @Override
    public List<ClienteFacturacion> clientesOrdenadosPorFacturacionDesc() throws SQLException {
        String sql =
                "SELECT c.idCliente, c.nombre, c.email, " +
                        "COALESCE(SUM(fp.cantidad * p.valor), 0) AS total " +
                        "FROM Cliente c " +
                        "LEFT JOIN Factura f          ON f.idCliente = c.idCliente " +
                        "LEFT JOIN Factura_Producto fp ON fp.idFactura = f.idFactura " +
                        "LEFT JOIN Producto p          ON p.idProducto = fp.idProducto " +
                        "GROUP BY c.idCliente, c.nombre, c.email " +
                        "ORDER BY total DESC, c.nombre ASC";

        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ClienteFacturacion> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new ClienteFacturacion(
                        rs.getInt("idCliente"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getFloat("total")
                ));
            }
            return out;
        }
    }
}
