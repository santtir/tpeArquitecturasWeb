package tpe.app;

import tpe.model.dto.ClienteFacturacion;
import tpe.model.dto.ProductoRecaudacion;
import tpe.schema.SchemaMigrator;


import tpe.dao.*;
import tpe.dao.jdbc.*;
import tpe.model.*;

import tpe.services.CsvLoaderService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class App {
    public static void main(String[] args) {
        SchemaMigrator sm = new SchemaMigrator();
        sm.migrate();

        // Opcional: empezar desde cero para evitar choques de PK
        sm.reset();


        //carga de cvs
        CsvLoaderService loader = new CsvLoaderService();
        loader.cargarClientes("cvs/clientes.csv");
        loader.cargarProductos("cvs/productos.csv");
        loader.cargarFacturas("cvs/facturas.csv");
        loader.cargarFacturaProducto("cvs/facturas-productos.csv");

        System.out.println("cvs importados");

        ReportesDAO rep = new ReportesDAOJDBC();
        //inciso 3) productos con mas recaudaciones
        try {
            Optional<ProductoRecaudacion> p = rep.productoQueMasRecaudo();
            if (p.isPresent()) {
                ProductoRecaudacion r = p.get();
                System.out.println("Producto con mas recaudaciones: " + r);
            } else {
                System.out.println("No hay ventas registradas.");
            }
        } catch (SQLException e) {
            System.err.println("Error ejecutando reporte de recaudación:");
            e.printStackTrace();
        }

        //inciso 4) clientes por facturacion
        try {
            List<ClienteFacturacion> ranking = rep.clientesOrdenadosPorFacturacionDesc();
            System.out.println("Ranking de clientes por facturación:");
            for (ClienteFacturacion cf : ranking) {
                System.out.println(cf);
            }
        } catch (SQLException e) {
            System.err.println("Error en ranking de clientes:");
            e.printStackTrace();
        }
    }
}
