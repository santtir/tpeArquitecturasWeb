package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.dto.CarreraCantidadDTO;
import org.example.modelo.Carrera;

import java.util.List;
import java.util.Optional;

public class CarreraRepository extends BaseRepository {

    public CarreraRepository(EntityManager em) { super(em); }

    public void alta(Carrera c) {
        inTx(()->em.persist(c));
    }

    // 2.f) Carreras con cantidad de inscriptos, ordenadas por cantidad (DESC)
    public List<CarreraCantidadDTO> carrerasConCantidadInscriptosDTO() {
        return em.createQuery(
                "SELECT new org.example.dto.CarreraCantidadDTO(c.nombre, COUNT(ec)) " +
                        "FROM EstudianteCarrera ec " +
                        "JOIN ec.carrera c " +
                        "GROUP BY c.nombre " +
                        "ORDER BY COUNT(ec) DESC",
                CarreraCantidadDTO.class
        ).getResultList();
    }

    public Optional<Carrera> porNombre(String nombre) {
        try {
            return Optional.of(
                    em.createQuery("SELECT c FROM Carrera c WHERE c.nombre = :n", Carrera.class)
                            .setParameter("n", nombre)
                            .getSingleResult()
            );
        } catch (jakarta.persistence.NoResultException ex) {
            return Optional.empty();
        }
    }


}
