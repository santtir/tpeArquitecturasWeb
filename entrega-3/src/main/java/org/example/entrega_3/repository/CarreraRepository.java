package org.example.entrega_3.repository;

import org.example.entrega_3.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    Optional<Carrera> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
    List<Carrera> findAllByOrderByNombreAsc();
}
