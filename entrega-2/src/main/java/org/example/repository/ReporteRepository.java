package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.dto.ResumenCarreraAnualDTO;

import java.util.*;

public class ReporteRepository extends BaseRepository {
    public ReporteRepository(EntityManager em) { super(em); }

    // Devuelve: lista ordenada por nombre de carrera (A-Z) y por año (asc)
    public List<ResumenCarreraAnualDTO> reporteCarrerasAnual() {

        // 1) Inscriptos por (carrera, anioIngreso)
        List<Object[]> ins = em.createQuery(
                "SELECT c.nombre, ec.anioIngreso, COUNT(ec) " +
                        "FROM EstudianteCarrera ec " +
                        "JOIN ec.carrera c " +
                        "GROUP BY c.nombre, ec.anioIngreso",
                Object[].class
        ).getResultList();

        // 2) Egresados por (carrera, anioGraduacion)
        List<Object[]> egr = em.createQuery(
                "SELECT c.nombre, ec.anioGraduacion, COUNT(ec) " +
                        "FROM EstudianteCarrera ec " +
                        "JOIN ec.carrera c " +
                        "WHERE ec.graduado = true AND ec.anioGraduacion IS NOT NULL " +
                        "GROUP BY c.nombre, ec.anioGraduacion",
                Object[].class
        ).getResultList();

        // 3) Merge ordenado: carrera A-Z, año asc
        Map<String, Map<Integer, ResumenCarreraAnualDTO>> agg = new TreeMap<>();

        for (Object[] row : ins) {
            String carrera = (String) row[0];
            Integer anio   = (Integer) row[1];
            Long count     = (Long) row[2];
            if (anio == null) continue;
            agg.computeIfAbsent(carrera, k -> new TreeMap<>())
                    .computeIfAbsent(anio, a -> new ResumenCarreraAnualDTO(carrera, anio, 0L, 0L))
                    .setInscriptos( (agg.get(carrera).get(anio).getInscriptos() == null ? 0 : agg.get(carrera).get(anio).getInscriptos()) + count );
        }

        for (Object[] row : egr) {
            String carrera = (String) row[0];
            Integer anio   = (Integer) row[1];
            Long count     = (Long) row[2];
            if (anio == null) continue;
            agg.computeIfAbsent(carrera, k -> new TreeMap<>())
                    .computeIfAbsent(anio, a -> new ResumenCarreraAnualDTO(carrera, anio, 0L, 0L))
                    .setEgresados( (agg.get(carrera).get(anio).getEgresados() == null ? 0 : agg.get(carrera).get(anio).getEgresados()) + count );
        }

        List<ResumenCarreraAnualDTO> out = new ArrayList<>();
        agg.forEach((carrera, porAnio) -> porAnio.values().forEach(out::add));
        return out;
    }
}
