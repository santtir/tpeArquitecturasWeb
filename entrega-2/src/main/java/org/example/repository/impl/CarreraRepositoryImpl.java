package org.example.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.example.dto.CarreraCantidadDTO;
import org.example.modelo.Carrera;
import org.example.repository.CarreraRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class CarreraRepositoryImpl implements CarreraRepository {

    private final EntityManager em;

    public CarreraRepositoryImpl(EntityManager em) { this.em = em; }

    @Override
    public Carrera save(Carrera c) {
        return inTx(() -> {
            if (c.getId() == null) { em.persist(c); return c; }
            return em.merge(c);
        });
    }

    @Override
    public Optional<Carrera> findById(Long id) {
        return Optional.ofNullable(em.find(Carrera.class, id));
    }

    @Override
    public Optional<Carrera> findByNombre(String nombre) {
        try {
            Carrera c = em.createQuery(
                    "SELECT c FROM Carrera c WHERE c.nombre = :n",
                    Carrera.class
            ).setParameter("n", nombre).getSingleResult();
            return Optional.of(c);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Carrera> findAll() {
        return em.createQuery(
                "SELECT c FROM Carrera c ORDER BY c.nombre",
                Carrera.class
        ).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        inTx(() -> {
            Carrera c = em.find(Carrera.class, id);
            if (c != null) em.remove(c);
            return null;
        });
    }

    @Override
    public List<CarreraCantidadDTO> carrerasOrdenadasPorCantidadDeInscriptos() {
        return em.createQuery(
                "SELECT new org.example.dto.CarreraCantidadDTO(c.nombre, COUNT(ec)) " +
                        "FROM EstudianteCarrera ec JOIN ec.carrera c " +
                        "GROUP BY c.nombre " +
                        "ORDER BY COUNT(ec) DESC, c.nombre ASC",
                CarreraCantidadDTO.class
        ).getResultList();
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
