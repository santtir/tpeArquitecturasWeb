package tpe.dao.jdbc;

import tpe.dao.ProductoDAO;
import tpe.factory.ConnectionManager;
import tpe.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC de productoDAO
 * - Usa la Connection provista por ConnectionManager (autocommit=false).
 * - Mapea filas de la tabla Producto a la clase tpe.model.Producto.
 * */

public class ProductoDAOJDBC implements ProductoDAO {


    /**
     * Inserta un nuevo producto.
     *
     * Precondiciones:
     * - p no debe ser null.
     * - p.getIdProducto() debería ser null, ya que la PK es AUTO_INCREMENT y se asigna al insertar.
     *
     * Efectos:
     * - Ejecuta INSERT INTO Producto(nombre, valor).
     * - Setea el id generado dentro del mismo objeto p.
     *
     * Transacciones:
     * - No hace commit ni rollback; los maneja la capa superior.
     *
     * @param p entidad a insertar
     * @return la misma entidad p con el id asignado
     * @throws SQLException si ocurre un error de acceso a datos
     */
    @Override
    public Producto create(Producto p) throws SQLException {
        String sql = "INSERT INTO Producto(nombre, valor) VALUES(?, ?)";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setFloat(2, p.getValor());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdProducto(rs.getInt(1));
            }
            return p;
        }
    }

    /**
     * Busca un producto por su id
     *
     * Devuelve un Optional.empty() si no existe
     *
     * @param id identificador del producto (debe ser mayor a 0)
     * @return Optional con el producto si existe, de lo contrario, Optional.empty()
     * @throws SQLException si ocurre un error de acceso a datos
     * */
    @Override
    public Optional<Producto> findById(int id) throws SQLException {
        String sql = "SELECT idProducto, nombre, valor FROM Producto WHERE idProducto=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        }
    }
    /**
     * Trae todos los productos
     *
     * Lista todos los productos ordenados por id.
     *
     * Puede devolver una lista vacía si no hay registros.
     * @return lista de productos
     * @throws SQLException si ocurre un error de acceso a datos
     * */
    @Override
    public List<Producto> findAll() throws SQLException {
        String sql = "SELECT idProducto, nombre, valor FROM Producto ORDER BY idProducto";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Producto> salida = new ArrayList<>();
            while (rs.next()) salida.add(map(rs));
            return salida;
        }
    }

    /**
     * Actualiza un producto existente.
     *
     * Precondiciones:
     * - p no debe ser null.
     * - p.getIdProducto() no debe ser null; indica qué fila actualizar.
     *
     * Efectos:
     * - Ejecuta UPDATE Producto SET nombre=?, valor=? WHERE idProducto=?.
     * - Si no existe una fila con ese id, el conteo de filas afectadas puede ser 0.
     *
     * Transacciones:
     * - No hace commit ni rollback; los maneja la capa superior.
     *
     * @param p entidad con los nuevos valores y un id existente
     * @throws SQLException si ocurre un error de acceso a datos
     */
    @Override
    public void update(Producto p) throws SQLException {
        String sql = "UPDATE Producto SET nombre=?, valor=? WHERE idProducto=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setFloat(2, p.getValor());
            ps.setInt(3, p.getIdProducto());
            ps.executeUpdate();
        }
    }
    /**
     * Elimina un producto por id.
     *
     * Consideraciones de integridad:
     * - Si el producto está referenciado por Factura_Producto y la FK está con ON DELETE RESTRICT,
     *   la eliminación fallará con una SQLException (SQLState 23000 por violación de integridad).
     *
     *
     * @param id identificador del producto
     * @throws SQLException si ocurre un error de acceso a datos o si hay una violación de integridad referencial
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Producto WHERE idProducto=?";
        Connection con = ConnectionManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Mapea la fila actual de un ResultSet a un objeto Producto.
     *
     * Requisitos:
     * - El ResultSet debe estar posicionado en una fila válida.
     *
     * @param rs result set con las columnas idProducto, nombre y valor
     * @return instancia de Producto con los datos de la fila actual
     * @throws SQLException si falla la lectura del result set
     */
    private Producto map(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("idProducto"),
                rs.getString("nombre"),
                rs.getFloat("valor")
        );
    }
}
