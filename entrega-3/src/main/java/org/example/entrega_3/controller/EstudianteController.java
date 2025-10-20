package org.example.entrega_3.controller;

import lombok.RequiredArgsConstructor;
import org.example.entrega_3.dto.EstudianteDTO;
import org.example.entrega_3.service.EstudianteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/estudiante") @RequiredArgsConstructor
public class EstudianteController {
    private final EstudianteService service;

    @GetMapping public List<EstudianteDTO> listar() { return service.findAll(); }
    @GetMapping("/{id}") public EstudianteDTO porId(@PathVariable Long id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public EstudianteDTO crear(@RequestBody EstudianteDTO dto) { return service.create(dto); }
    @PutMapping("/{id}") public EstudianteDTO actualizar(@PathVariable Long id, @RequestBody EstudianteDTO dto) {
        return service.update(id, dto);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) { service.delete(id); }

    // prácticos
    @PostMapping("/{idEst}/matricular/{idCar}") public EstudianteDTO matricular(@PathVariable Long idEst, @PathVariable Long idCar) {
        return service.matricular(idEst, idCar);
    }
    @GetMapping("/libreta/{lu}") public EstudianteDTO porLibreta(@PathVariable String lu) { return service.porLibreta(lu); }
    @GetMapping("/genero/{g}") public List<EstudianteDTO> porGenero(@PathVariable String g) { return service.porGenero(g); }
    @GetMapping("/carrera/{carrera}/ciudad/{ciudad}")
    public List<EstudianteDTO> porCarreraYCiudad(@PathVariable String carrera, @PathVariable String ciudad) {
        return service.porCarreraYCiudad(carrera, ciudad);
    }

    @GetMapping
    public List<EstudianteDTO> listar(@RequestParam(required = false, name="sort") String sort) {
        return (sort == null || sort.isBlank())
                ? service.findAll()
                : service.findAllSorted(sort);
    }
}
