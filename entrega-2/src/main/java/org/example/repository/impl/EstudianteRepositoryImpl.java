package org.example.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.example.modelo.Estudiante;
import org.example.repository.EstudianteRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class EstudianteRepositoryImpl implements EstudianteRepository {

    private final EntityManager em;

    public EstudianteRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Estudiante save(Estudiante e) {
        return inTx(() -> {
            if (e.getId() == null) {
                em.persist(e);
                return e;
            } else {
                return em.merge(e);
            }
        });
    }

    @Override
    public Optional<Estudiante> findById(Long id) {
        return Optional.ofNullable(em.find(Estudiante.class, id));
    }

    @Override
    public List<Estudiante> findAllOrdenadosPorApellido() {
        return em.createQuery(
                "SELECT e FROM Estudiante e ORDER BY e.apellido ASC, e.nombres ASC",
                Estudiante.class
        ).getResultList();
    }

    @Override
    public Optional<Estudiante> findByNroLibreta(String nroLibreta) {
        try {
            Estudiante e = em.createQuery(
                    "SELECT e FROM Estudiante e WHERE e.nroLibreta = :lu",
                    Estudiante.class
            ).setParameter("lu", nroLibreta).getSingleResult();
            return Optional.of(e);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Estudiante> findByGenero(String genero) {
        return em.createQuery(
                "SELECT e FROM Estudiante e WHERE e.genero = :g " +
                        "ORDER BY e.apellido ASC, e.nombres ASC",
                Estudiante.class
        ).setParameter("g", genero).getResultList();
    }

    @Override
    public List<Estudiante> estudiantesDeCarreraPorCiudad(Long carreraId, String ciudad) {
        return em.createQuery(
                        "SELECT DISTINCT e FROM EstudianteCarrera ec " +
                                "JOIN ec.estudiante e " +
                                "WHERE ec.carrera.id = :cid AND e.ciudadResidencia = :city " +
                                "ORDER BY e.apellido ASC, e.nombres ASC",
                        Estudiante.class
                ).setParameter("cid", carreraId)
                .setParameter("city", ciudad)
                .getResultList();
    }

    @Override
    public void deleteById(Long id) {
        inTx(() -> {
            Estudiante e = em.find(Estudiante.class, id);
            if (e != null) em.remove(e);
            return null;
        });
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
