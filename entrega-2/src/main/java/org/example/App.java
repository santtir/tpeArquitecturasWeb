package org.example;

import jakarta.persistence.EntityManager;
import org.example.factory.JPAUtil;
import org.example.modelo.Carrera;
import org.example.modelo.Estudiante;
import org.example.repository.CarreraRepository;
import org.example.repository.EstudianteCarreraRepository;
import org.example.repository.EstudianteRepository;
import org.example.importer.CsvImporter;
import org.example.repository.ReporteRepository;

public class App {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.em();
        EstudianteRepository estRepo = new EstudianteRepository(em);
        CarreraRepository carRepo = new CarreraRepository(em);
        EstudianteCarreraRepository ecRepo = new EstudianteCarreraRepository(em);


       /** Estudiante est = Estudiante.builder()
                .nroLibreta("LU12221").nombres("Margarita").apellido("Diaz")
                .edad(20).genero("F").documento("45.200.200").ciudadResidencia("Tandil")
                .build();
        estRepo.alta(est);

        Carrera car = Carrera.builder().nombre("Ingenieria en Sistemas").build();
        carRepo.alta(car);

// ahora, matricular en 2023
        System.out.println("estId=" + est.getId()); // deben imprimir valores != null
        System.out.println("carId=" + car.getId());
        ecRepo.matricular(est.getId(), car.getId(), 2023);
*/
        CsvImporter importer = new CsvImporter(em, estRepo, carRepo);

// IMPORTAR en este orden:
        importer.importarCarreras("data/carreras.csv");
        importer.importarEstudiantes("data/estudiantes.csv");
        importer.importarEstudianteCarrera("data/estudianteCarrera.csv");

        // Reporte:
        var repRepo = new ReporteRepository(em);
        var resumen = repRepo.reporteCarrerasAnual();


// Mostrar (ordenado alfabético por carrera y cronológico por año)
        System.out.println("Carrera;Año;Inscriptos;Egresados");
        for (var r : resumen) {
            System.out.printf("%s;%d;%d;%d%n",
                    r.getNombreCarrera(), r.getAnio(),
                    r.getInscriptos() == null ? 0 : r.getInscriptos(),
                    r.getEgresados()  == null ? 0 : r.getEgresados()
            );
        }

        em.close();
        JPAUtil.close();
        System.out.println("CSV importados OK");

    }
}
