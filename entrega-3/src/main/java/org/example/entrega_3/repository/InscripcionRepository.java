package org.example.entrega_3.repository;

import org.example.entrega_3.dto.CarreraConCantidadDTO;
import org.example.entrega_3.dto.ReporteCarreraAnualDTO;
import org.example.entrega_3.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@SuppressWarnings("JpaQlInspection")
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // listados básicos
    List<Inscripcion> findByEstudianteId(Long estudianteId);
    List<Inscripcion> findByCarreraId(Long carreraId);

    // “guard rails” para matricular
    boolean existsByEstudianteIdAndCarreraIdAndGraduadoFalse(Long estudianteId, Long carreraId);

    // contadores útiles
    long countByCarreraIdAndGraduadoFalse(Long carreraId);
    long countByCarreraIdAndGraduadoTrue(Long carreraId);

    /** (f) Carreras con inscriptos actuales, ordenadas por cantidad desc */
    @Query("""
      select new org.example.entrega_3.dto.CarreraConCantidadDTO(
        i.carrera.id, i.carrera.nombre, count(distinct i.estudiante.id)
      )
      from Inscripcion i
      where i.graduado = false
      group by i.carrera.id, i.carrera.nombre
      order by count(distinct i.estudiante.id) desc, i.carrera.nombre asc
    """)
    List<CarreraConCantidadDTO> carrerasOrdenadasPorInscriptos();

    /** (h)  */
    @Query("""
      select new org.example.entrega_3.dto.ReporteCarreraAnualDTO(
        i.carrera.id, i.carrera.nombre, i.anioEvento, count(i.id), 0L
      )
      from Inscripcion i
      where i.anioEvento is not null
      group by i.carrera.id, i.carrera.nombre, i.anioEvento
    """)
    List<ReporteCarreraAnualDTO> inscriptosPorCarreraYAnioDTO();

    @Query("""
      select new org.example.entrega_3.dto.ReporteCarreraAnualDTO(
        i.carrera.id, i.carrera.nombre, i.anioGraduacion, 0L, count(i.id)
      )
      from Inscripcion i
      where i.graduado = true and i.anioGraduacion is not null
      group by i.carrera.id, i.carrera.nombre, i.anioGraduacion
    """)
    List<ReporteCarreraAnualDTO> egresadosPorCarreraYAnioDTO();
}
