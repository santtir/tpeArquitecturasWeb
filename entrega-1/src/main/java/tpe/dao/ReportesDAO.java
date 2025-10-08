package tpe.dao;

import tpe.model.dto.ClienteFacturacion;
import tpe.model.dto.ProductoRecaudacion;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Consultas de reportes (agregaciones). */
public interface ReportesDAO {

    /** Devuelve el producto que más recaudó. */
    Optional<ProductoRecaudacion> productoQueMasRecaudo() throws SQLException;

    /** Lista clientes ordenados por monto facturado descendente. */
    List<ClienteFacturacion> clientesOrdenadosPorFacturacionDesc() throws SQLException;
}
