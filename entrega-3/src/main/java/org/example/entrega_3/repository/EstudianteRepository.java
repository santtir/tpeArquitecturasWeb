package org.example.entrega_3.repository;

import org.example.entrega_3.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByNroLibretaUniversitaria(String nroLibretaUniversitaria); // d)
    List<Estudiante> findByGeneroIgnoreCase(String genero);                              // e)
    List<Estudiante> findByInscripciones_Carrera_IdAndCiudadResidenciaIgnoreCase (Long carreraId, String ciudadResidencia);
    Optional<Estudiante> findByNroDocumento(String nroDocumento);

    // (g) estudiantes de una carrera filtrados por ciudad
    @Query("""
       select distinct i.estudiante
       from Inscripcion i
       where lower(i.carrera.nombre) = lower(:nombreCarrera)
         and lower(i.estudiante.ciudadResidencia) = lower(:ciudad)
    """)
    List<Estudiante> findByCarreraNombreAndCiudad(String nombreCarrera, String ciudad);
}
