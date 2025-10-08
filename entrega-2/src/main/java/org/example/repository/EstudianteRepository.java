package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.modelo.Estudiante;

import java.util.List;
import java.util.Optional;

public class EstudianteRepository extends BaseRepository {

    public EstudianteRepository(EntityManager em) { super(em); }

    // 2.a) Alta de estudiante
    public void alta(Estudiante e) {
        inTx(()->em.persist(e));
    }

    // 2.d) buscar por nro de libreta
    public Optional<Estudiante> porLibreta(String nroLibreta) {
        try {
            TypedQuery<Estudiante> q = em.createQuery(
                    "SELECT e FROM Estudiante e WHERE e.nroLibreta = :lu", Estudiante.class);
            q.setParameter("lu", nroLibreta);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        } }

    // 2.c) todos los estudiantes con orden simple (apellido y nombre)
    public List<Estudiante> todosOrdenadosPor() {
        return em.createQuery("SELECT e FROM Estudiante e ORDER BY e.apellido, e.nombres ASC", Estudiante.class).getResultList();
    }

    // 2.e) Todos los estudiantes por género
    public List<Estudiante> porGenero(String genero) {
        return em.createQuery(
            "SELECT e FROM Estudiante e WHERE e.genero = :g",
            Estudiante.class
    ).setParameter("g", genero).getResultList();
    }

    // 2.g) Estudiantes de una carrera filtrado por ciudad
    public List<Estudiante> deCarreraPorCiudad(String nombreCarrera, String ciudad) {
        return em.createQuery(
                        "SELECT e FROM EstudianteCarrera ec " +
                                "JOIN ec.estudiante e " +
                                "JOIN ec.carrera c " +
                                "WHERE c.nombre = :name AND e.ciudadResidencia = :city",
                        Estudiante.class
                ).setParameter("name", nombreCarrera)
                .setParameter("city", ciudad)
                .getResultList();
    }

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

}
