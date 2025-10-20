package org.example.entrega_3.controller;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.InscripcionDTO;
import org.example.entrega_3.mapper.InscripcionMapper;
import org.example.entrega_3.repository.InscripcionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/inscripcion") @RequiredArgsConstructor
public class InscripcionController {
    private final org.example.entrega_3.repository.InscripcionRepository repo;

    @GetMapping public List<InscripcionDTO> listar() {
        return repo.findAll().stream().map(InscripcionMapper::toDTO).toList();
    }
    @GetMapping("/estudiante/{id}") public List<InscripcionDTO> porEst(@PathVariable Long id) {
        return repo.findByEstudianteId(id).stream().map(InscripcionMapper::toDTO).toList();
    }
    @GetMapping("/carrera/{id}") public List<InscripcionDTO> porCar(@PathVariable Long id) {
        return repo.findByCarreraId(id).stream().map(InscripcionMapper::toDTO).toList();
    }
}
