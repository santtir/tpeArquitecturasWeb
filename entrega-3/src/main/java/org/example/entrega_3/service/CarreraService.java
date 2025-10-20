package org.example.entrega_3.service;

import org.example.entrega_3.dto.CarreraDTO;
import java.util.List;

public interface CarreraService {
    List<CarreraDTO> findAll();
    CarreraDTO findById(Long id);
    CarreraDTO create(CarreraDTO dto);
    CarreraDTO update(Long id, CarreraDTO dto);
    void delete(Long id);
}
