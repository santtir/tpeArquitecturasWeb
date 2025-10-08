package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.modelo.Carrera;
import org.example.modelo.Estudiante;
import org.example.modelo.EstudianteCarrera;

import java.util.Optional;

public class EstudianteCarreraRepository extends BaseRepository {

    public EstudianteCarreraRepository(EntityManager em) {
        super(em);
    }

    // 2(b) matricular Estudiante a Carrera
    public void matricular(Long estudianteId, Long carreraId, int anioIngreso) {
        Estudiante e = em.getReference(Estudiante.class, estudianteId);
        Carrera c    = em.getReference(Carrera.class, carreraId);

        //checkeo repetidos
        if (existeMatricula(estudianteId, carreraId, anioIngreso)) {
            throw new IllegalArgumentException(
                    "El estudiante ya esta inscripto en esa carrera para ese año."
            );
        }

        // creo la matricula y persisto en transaccion
        EstudianteCarrera ec = EstudianteCarrera.builder()
                .estudiante(e)
                .carrera(c)
                .anioIngreso(anioIngreso)
                .graduado(false)
                .build();

        inTx(() -> em.persist(ec));
    }

    //buscar estudiante por dni
    public Optional<Estudiante> porDocumento(String dni) {
        try {
            return Optional.of(
                    em.createQuery("SELECT e FROM Estudiante e WHERE e.documento = :dni", Estudiante.class)
                            .setParameter("dni", dni)
                            .getSingleResult()
            );
        } catch (jakarta.persistence.NoResultException ex) {
            return Optional.empty();
        }
    }


    // check de existencia
    private boolean existeMatricula(Long estId, Long carId, int anioIngreso) {
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT ec.id FROM EstudianteCarrera ec " +
                            "WHERE ec.estudiante.id = :eid AND ec.carrera.id = :cid AND ec.anioIngreso = :anio",
                    Long.class);
            q.setParameter("eid", estId);
            q.setParameter("cid", carId);
            q.setParameter("anio", anioIngreso);
            q.setMaxResults(1);
            q.getSingleResult();
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }
}
