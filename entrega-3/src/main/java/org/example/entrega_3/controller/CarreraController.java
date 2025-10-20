package org.example.entrega_3.controller;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.CarreraDTO;
import org.example.entrega_3.service.CarreraService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/carrera") @RequiredArgsConstructor
public class CarreraController {
    private final CarreraService service;

    @GetMapping public List<CarreraDTO> listar() { return service.findAll(); }
    @GetMapping("/{id}") public CarreraDTO porId(@PathVariable Long id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CarreraDTO crear(@RequestBody CarreraDTO dto) { return service.create(dto); }
    @PutMapping("/{id}") public CarreraDTO actualizar(@PathVariable Long id, @RequestBody CarreraDTO dto) {
        return service.update(id, dto);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) { service.delete(id); }
}
