// org.example.repository.EstudianteCarreraRepository
package org.example.repository;

import org.example.dto.ResumenCarreraAnualDTO;
import java.util.List;

public interface EstudianteCarreraRepository {
    void matricular(Long estudianteId, Long carreraId, int anioIngreso); // 2b
    List<ResumenCarreraAnualDTO> reporteCarrerasAnual(); // 3
}
