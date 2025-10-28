package org.example.repository;

import org.example.dto.CarreraConInscriptosDTO;
import org.example.modelo.Carrera;
import java.util.List;
import java.util.Optional;

public interface CarreraRepository {
    Carrera save(Carrera c);
    Optional<Carrera> findById(Long id);
    Optional<Carrera> findByNombre(String nombre);
    List<Carrera> findAll();
    void deleteById(Long id);

    // 2f: carreras con inscriptos, ordenadas por cantidad (DESC)
    List<CarreraCantidadDTO> carrerasOrdenadasPorCantidadDeInscriptos();
}
