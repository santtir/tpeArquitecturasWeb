package org.example.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.dto.ResumenCarreraAnualDTO;
import org.example.modelo.Carrera;
import org.example.modelo.Estudiante;
import org.example.modelo.EstudianteCarrera;
import org.example.repository.EstudianteCarreraRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class EstudianteCarreraRepositoryImpl implements EstudianteCarreraRepository {

    private final EntityManager em;

    public EstudianteCarreraRepositoryImpl(EntityManager em) { this.em = em; }

    @Override
    public void matricular(Long estudianteId, Long carreraId, int anioIngreso) {
        inTx(() -> {
            Estudiante e = em.getReference(Estudiante.class, estudianteId);
            Carrera c    = em.getReference(Carrera.class, carreraId);

            EstudianteCarrera ec = EstudianteCarrera.builder()
                    .estudiante(e)
                    .carrera(c)
                    .anioIngreso(anioIngreso)
                    .graduado(false)
                    .build();

            em.persist(ec);
            return null;
        });
    }

    @Override
    public List<ResumenCarreraAnualDTO> reporteCarrerasAnual() {
        var ins = em.createQuery(
                "SELECT c.nombre, ec.anioIngreso, COUNT(ec) " +
                        "FROM EstudianteCarrera ec JOIN ec.carrera c " +
                        "GROUP BY c.nombre, ec.anioIngreso",
                Object[].class
        ).getResultList();

        var egr = em.createQuery(
                "SELECT c.nombre, ec.anioGraduacion, COUNT(ec) " +
                        "FROM EstudianteCarrera ec JOIN ec.carrera c " +
                        "WHERE ec.graduado = true AND ec.anioGraduacion IS NOT NULL " +
                        "GROUP BY c.nombre, ec.anioGraduacion",
                Object[].class
        ).getResultList();

        Map<String, Map<Integer, ResumenCarreraAnualDTO>> agg = new TreeMap<>();

        for (Object[] r : ins) {
            String carrera = (String) r[0];
            Integer anio   = (Integer) r[1];
            Long cant      = (Long) r[2];
            if (anio == null) continue;
            agg.computeIfAbsent(carrera, k -> new TreeMap<>())
                    .computeIfAbsent(anio, a -> new ResumenCarreraAnualDTO(carrera, anio, 0L, 0L))
                    .setInscriptos(agg.get(carrera).get(anio).getInscriptos() + cant);
        }

        for (Object[] r : egr) {
            String carrera = (String) r[0];
            Integer anio   = (Integer) r[1];
            Long cant      = (Long) r[2];
            if (anio == null) continue;
            agg.computeIfAbsent(carrera, k -> new TreeMap<>())
                    .computeIfAbsent(anio, a -> new ResumenCarreraAnualDTO(carrera, anio, 0L, 0L))
                    .setEgresados(agg.get(carrera).get(anio).getEgresados() + cant);
        }

        List<ResumenCarreraAnualDTO> out = new ArrayList<>();
        agg.forEach((c, porAnio) -> porAnio.values().forEach(out::add)); // carrera A-Z, año asc
        return out;
    }

    private <T> T inTx(Callable<T> work) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T res = work.call();
            tx.commit();
            return res;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
