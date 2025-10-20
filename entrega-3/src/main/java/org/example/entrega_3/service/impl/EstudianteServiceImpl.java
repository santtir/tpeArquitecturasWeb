package org.example.entrega_3.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.EstudianteDTO;
import org.example.entrega_3.mapper.EstudianteMapper;
import org.example.entrega_3.model.Carrera;
import org.example.entrega_3.model.Estudiante;
import org.example.entrega_3.model.Inscripcion;
import org.example.entrega_3.repository.CarreraRepository;
import org.example.entrega_3.repository.EstudianteRepository;
import org.example.entrega_3.repository.InscripcionRepository;
import org.example.entrega_3.service.EstudianteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;


import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estRepo;
    private final CarreraRepository carRepo;
    private final InscripcionRepository insRepo;

    @Transactional(readOnly = true)
    public List<EstudianteDTO> findAll() { return estRepo.findAll().stream().map(EstudianteMapper::toDTO).toList(); }

    @Transactional(readOnly = true)
    public EstudianteDTO findById(Long id) {
        Estudiante e = estRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
        return EstudianteMapper.toDTO(e);
    }

    @Transactional
    public EstudianteDTO create(EstudianteDTO dto) {
        Estudiante e = EstudianteMapper.toEntity(dto);
        return EstudianteMapper.toDTO(estRepo.save(e));
    }

    @Transactional
    public EstudianteDTO update(Long id, EstudianteDTO dto) {
        Estudiante e = estRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
        e.setNombres(dto.nombres()); e.setApellido(dto.apellido()); e.setEdad(dto.edad());
        e.setGenero(dto.genero()); e.setNroDocumento(dto.nroDocumento());
        e.setCiudadResidencia(dto.ciudadResidencia()); e.setNroLibretaUniversitaria(dto.nroLibretaUniversitaria());
        return EstudianteMapper.toDTO(estRepo.save(e));
    }

    @Transactional
    public void delete(Long id) { estRepo.deleteById(id); }

    // ---- prácticos ----

    @Transactional
    public EstudianteDTO matricular(Long idEst, Long idCar) {
        Estudiante e = estRepo.findById(idEst).orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
        Carrera c = carRepo.findById(idCar).orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada"));
        if (insRepo.existsByEstudianteIdAndCarreraIdAndGraduadoFalse(idEst, idCar))
            throw new IllegalStateException("El estudiante ya está inscripto en esa carrera");

        Inscripcion ins = Inscripcion.builder()
                .estudiante(e).carrera(c)
                .fechaInscripcion(LocalDate.now())
                .anioEvento(LocalDate.now().getYear())
                .graduado(false).antiguedadEnAnios(0)
                .build();
        insRepo.save(ins);
        return EstudianteMapper.toDTO(e);
    }

    @Transactional(readOnly = true)
    public EstudianteDTO porLibreta(String lu) {
        Estudiante e = estRepo.findByNroLibretaUniversitaria(lu).orElseThrow(() -> new IllegalArgumentException("No existe LU"));
        return EstudianteMapper.toDTO(e);
    }

    @Transactional(readOnly = true)
    public List<EstudianteDTO> porGenero(String genero) {
        return estRepo.findByGeneroIgnoreCase(genero).stream().map(EstudianteMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EstudianteDTO> porCarreraYCiudad(String carrera, String ciudad) {
        return estRepo.findByCarreraNombreAndCiudad(carrera, ciudad).stream().map(EstudianteMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteDTO> findAllSorted(String sortBy) {
        // whitelist simple para evitar campos inválidos
        var allowed = java.util.Set.of("nombres","apellido","edad","genero","nroDocumento","ciudadResidencia","nroLibretaUniversitaria");
        if (!allowed.contains(sortBy)) sortBy = "apellido";
        return estRepo.findAll(Sort.by(sortBy)).stream().map(EstudianteMapper::toDTO).toList();
    }
}
