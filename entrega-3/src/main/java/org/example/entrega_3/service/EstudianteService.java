package org.example.entrega_3.service;

import org.example.entrega_3.dto.EstudianteDTO;
import java.util.List;

public interface EstudianteService {
    List<EstudianteDTO> findAll();
    EstudianteDTO findById(Long id);
    EstudianteDTO create(EstudianteDTO dto);
    EstudianteDTO update(Long id, EstudianteDTO dto);
    void delete(Long id);

    // ejercicios del practico
    EstudianteDTO matricular(Long idEstudiante, Long idCarrera);
    EstudianteDTO porLibreta(String nroLibreta);
    List<EstudianteDTO> porGenero(String genero);
    List<EstudianteDTO> porCarreraYCiudad(String carrera, String ciudad);
    public List<EstudianteDTO> findAllSorted(String sortBy);

}
