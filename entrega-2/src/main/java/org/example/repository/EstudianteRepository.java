package org.example.repository;

import org.example.modelo.Estudiante;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository {
    Estudiante save(Estudiante e);                    // 2a
    Optional<Estudiante> findById(Long id);
    List<Estudiante> findAllOrdenadosPorApellido();  // 2c
    Optional<Estudiante> findByNroLibreta(String lu);// 2d (String!)
    List<Estudiante> findByGenero(String genero);    // 2e
    List<Estudiante> estudiantesDeCarreraPorCiudad(Long carreraId, String ciudad); // 2g
    void deleteById(Long id);
}


