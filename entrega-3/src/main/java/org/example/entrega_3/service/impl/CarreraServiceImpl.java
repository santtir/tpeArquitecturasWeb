package org.example.entrega_3.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.CarreraDTO;
import org.example.entrega_3.mapper.CarreraMapper;
import org.example.entrega_3.model.Carrera;
import org.example.entrega_3.repository.CarreraRepository;
import org.example.entrega_3.service.CarreraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository repo;

    @Transactional(readOnly = true)
    public List<CarreraDTO> findAll() {
        return repo.findAllByOrderByNombreAsc().stream().map(CarreraMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public CarreraDTO findById(Long id) {
        Carrera c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada"));
        return CarreraMapper.toDTO(c);
    }

    @Transactional
    public CarreraDTO create(CarreraDTO dto) {
        if (repo.existsByNombreIgnoreCase(dto.nombre())) {
            throw new IllegalStateException("Ya existe una carrera con ese nombre");
        }
        return CarreraMapper.toDTO(repo.save(CarreraMapper.toEntity(dto)));
    }

    @Transactional
    public CarreraDTO update(Long id, CarreraDTO dto) {
        Carrera c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada"));
        c.setNombre(dto.nombre());
        return CarreraMapper.toDTO(repo.save(c));
    }

    @Transactional
    public void delete(Long id) { repo.deleteById(id); }
}
