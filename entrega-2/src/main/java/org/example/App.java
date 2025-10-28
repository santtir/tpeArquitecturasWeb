package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.modelo.Carrera;
import org.example.modelo.Estudiante;
import org.example.repository.impl.CarreraRepositoryImpl;
import org.example.repository.impl.EstudianteCarreraRepositoryImpl;
import org.example.repository.impl.EstudianteRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("tpePU");
        EntityManager em = emf.createEntityManager();

        var estRepo = new EstudianteRepositoryImpl(em);
        var carRepo = new CarreraRepositoryImpl(em);
        var ecRepo  = new EstudianteCarreraRepositoryImpl(em);

        try {
            // Carrera
            var ingSistemas = new Carrera("Ing. en Sistemas");
            em.getTransaction().begin();
            em.persist(ingSistemas);
            em.getTransaction().commit();

            // 2a) Alta estudiante
            var e1 = Estudiante.builder()
                    .nroLibreta("LU-001")
                    .nombres("Ana")
                    .apellido("Pérez")
                    .edad(22)
                    .genero("F")
                    .documento("40111222")
                    .ciudadResidencia("Tandil")
                    .build();
            estRepo.save(e1);

            var e2 = Estudiante.builder()
                    .nroLibreta("LU-002")
                    .nombres("Bruno")
                    .apellido("García")
                    .edad(24)
                    .genero("M")
                    .documento("38123456")
                    .ciudadResidencia("Tandil")
                    .build();
            estRepo.save(e2);

            // 2b) Matricular
            ecRepo.matricular(e1.getId(), ingSistemas.getId(), 2023);
            ecRepo.matricular(e2.getId(), ingSistemas.getId(), 2024);

            // 2c) Listado orden simple
            List<org.example.modelo.Estudiante> ordenados = estRepo.findAllOrdenadosPorApellido();
            System.out.println("2c) Estudiantes ordenados: " + ordenados);

            // 2d) Buscar por LU
            Optional<org.example.modelo.Estudiante> porLU = estRepo.findByNroLibreta("LU-001");
            System.out.println("2d) Por LU-001: " + porLU);

            // 2e) Por género
            var mujeres = estRepo.findByGenero("F");
            System.out.println("2e) Género F: " + mujeres);

            // 2f) Carreras por cantidad de inscriptos (usa tu CarreraCantidadDTO)
            var ranking = carRepo.carrerasOrdenadasPorCantidadDeInscriptos();
            System.out.println("2f) Ranking carreras: " + ranking);

            // 2g) Estudiantes de una carrera por ciudad
            var deTandil = estRepo.estudiantesDeCarreraPorCiudad(ingSistemas.getId(), "Tandil");
            System.out.println("2g) Estudiantes Ing. Sistemas en Tandil: " + deTandil);

            // 3) Reporte anual (usa tu ResumenCarreraAnualDTO)
            var reporte = ecRepo.reporteCarrerasAnual();
            System.out.println("3) Reporte carreras anual: " + reporte);

        } finally {
            em.close();
            emf.close();
        }
    }
}
