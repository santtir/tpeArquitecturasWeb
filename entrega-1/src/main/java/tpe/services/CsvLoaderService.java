package tpe.services;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import tpe.dao.jdbc.ClienteDAOJDBC;
import tpe.dao.jdbc.FacturaDAOJDBC;
import tpe.dao.jdbc.FacturaProductoDAOJDBC;
import tpe.dao.jdbc.ProductoDAOJDBC;
import tpe.factory.ConnectionManager;
import tpe.dao.*;
import tpe.model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Carga datos desde CSV ubicados en src/main/resources/csv/.
 * Si el CSV trae IDs (idCliente, idProducto, idFactura), inserta con ID explícito.
 * Si no, usa los DAO para insertar con AUTO_INCREMENT.
 */
public class CsvLoaderService {

    private final ClienteDAO clienteDAO = new ClienteDAOJDBC();
    private final ProductoDAO productoDAO = new ProductoDAOJDBC();
    private final FacturaDAO facturaDAO = new FacturaDAOJDBC();
    private final FacturaProductoDAO facturaProductoDAO = new FacturaProductoDAOJDBC();

    /**
     * Carga clientes desde resources/csv/clientes.csv
     */
    public void cargarClientes(String resourcePath) {
        ConnectionManager cm = ConnectionManager.getInstance();
        try {
            try (Reader r = readerFromResource(resourcePath);
                 CSVParser parser = CSVFormat.DEFAULT.builder()
                         .setHeader()
                         .setSkipHeaderRecord(true)
                         .setTrim(true)
                         .setIgnoreSurroundingSpaces(true)
                         .build()
                         .parse(r)) {

                Connection con = cm.getConnection();

                for (CSVRecord rec : parser) {
                    String idStr = safe(rec, "idCliente");
                    String nombre = safe(rec, "nombre");
                    String email = safe(rec, "email");

                    if (!idStr.isEmpty()) {
                        // Insert con ID explícito
                        try (PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO Cliente(idCliente, nombre, email) VALUES(?, ?, ?)")) {
                            ps.setInt(1, Integer.parseInt(idStr));
                            ps.setString(2, nombre);
                            ps.setString(3, email.isEmpty() ? null : email);
                            ps.executeUpdate();
                        }
                    } else {
                        // Auto-increment via DAO
                        clienteDAO.create(new Cliente(nombre, email.isEmpty() ? null : email));
                    }
                }
            }
            cm.commit();
            System.out.println("CSV clientes cargado.");
        } catch (Exception e) {
            cm.rollback();
            throw new RuntimeException("Error cargando clientes: " + resourcePath, e);
        }
    }

    /**
     * Carga productos desde resources/csv/productos.csv
     */
    public void cargarProductos(String resourcePath) {
        ConnectionManager cm = ConnectionManager.getInstance();
        try {
            try (Reader r = readerFromResource(resourcePath);
                 CSVParser parser = CSVFormat.DEFAULT.builder()
                         .setHeader()
                         .setSkipHeaderRecord(true)
                         .setTrim(true)
                         .setIgnoreSurroundingSpaces(true)
                         .build()
                         .parse(r)) {

                Connection con = cm.getConnection();

                for (CSVRecord rec : parser) {
                    String idStr = safe(rec, "idProducto");
                    String nombre = safe(rec, "nombre");
                    String valorStr = safe(rec, "valor");
                    float valor = parseFloat(valorStr);

                    if (!idStr.isEmpty()) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO Producto(idProducto, nombre, valor) VALUES(?, ?, ?)")) {
                            ps.setInt(1, Integer.parseInt(idStr));
                            ps.setString(2, nombre);
                            ps.setFloat(3, valor);
                            ps.executeUpdate();
                        }
                    } else {
                        productoDAO.create(new Producto(nombre, valor));
                    }
                }
            }
            cm.commit();
            System.out.println("CSV productos cargado.");
        } catch (Exception e) {
            cm.rollback();
            throw new RuntimeException("Error cargando productos: " + resourcePath, e);
        }
    }

    /**
     * Carga facturas desde resources/csv/facturas.csv
     */
    public void cargarFacturas(String resourcePath) {
        ConnectionManager cm = ConnectionManager.getInstance();
        try {
            try (Reader r = readerFromResource(resourcePath);
                 CSVParser parser = CSVFormat.DEFAULT.builder()
                         .setHeader()
                         .setSkipHeaderRecord(true)
                         .setTrim(true)
                         .setIgnoreSurroundingSpaces(true)
                         .build()
                         .parse(r)) {

                Connection con = cm.getConnection();

                for (CSVRecord rec : parser) {
                    String idFacturaStr = safe(rec, "idFactura");
                    String idClienteStr = safe(rec, "idCliente");
                    int idCliente = Integer.parseInt(idClienteStr);

                    if (!idFacturaStr.isEmpty()) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO Factura(idFactura, idCliente) VALUES(?, ?)")) {
                            ps.setInt(1, Integer.parseInt(idFacturaStr));
                            ps.setInt(2, idCliente);
                            ps.executeUpdate();
                        }
                    } else {
                        facturaDAO.create(new Factura(idCliente));
                    }
                }
            }
            cm.commit();
            System.out.println("CSV facturas cargado.");
        } catch (Exception e) {
            cm.rollback();
            throw new RuntimeException("Error cargando facturas: " + resourcePath, e);
        }
    }

    /**
     * Carga renglones desde resources/csv/factura_producto.csv
     */
    public void cargarFacturaProducto(String resourcePath) {
        ConnectionManager cm = ConnectionManager.getInstance();
        try {
            try (Reader r = readerFromResource(resourcePath);
                 CSVParser parser = CSVFormat.DEFAULT.builder()
                         .setHeader()
                         .setSkipHeaderRecord(true)
                         .setTrim(true)
                         .setIgnoreSurroundingSpaces(true)
                         .build()
                         .parse(r)) {

                for (CSVRecord rec : parser) {
                    int idFactura = Integer.parseInt(safe(rec, "idFactura"));
                    int idProducto = Integer.parseInt(safe(rec, "idProducto"));
                    int cantidad = Integer.parseInt(safe(rec, "cantidad"));

                    facturaProductoDAO.create(new FacturaProducto(idFactura, idProducto, cantidad));
                }
            }
            ConnectionManager.getInstance().commit();
            System.out.println("CSV factura_producto cargado.");
        } catch (Exception e) {
            ConnectionManager.getInstance().rollback();
            throw new RuntimeException("Error cargando factura_producto: " + resourcePath, e);
        }
    }

    // --- helpers ---

    private Reader readerFromResource(String resourcePath) {
        String path = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) throw new IllegalArgumentException("No se encontró el recurso: " + resourcePath);
        return new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    private static String safe(CSVRecord r, String key) {
        try {
            String v = r.get(key);
            return v == null ? "" : v.trim();
        } catch (IllegalArgumentException e) {
            return "";
        } // header ausente
    }

    private static float parseFloat(String s) {
        if (s == null) return 0f;
        s = s.trim();
        if (s.isEmpty()) return 0f;
        // soporta "12,34" y "12.34"
        return Float.parseFloat(s.replace(',', '.'));
    }
}