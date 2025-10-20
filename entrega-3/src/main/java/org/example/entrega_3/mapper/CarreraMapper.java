package org.example.entrega_3.mapper;

import org.example.entrega_3.dto.CarreraDTO;
import org.example.entrega_3.model.Carrera;

public final class CarreraMapper {
    private CarreraMapper() {}
    public static CarreraDTO toDTO(Carrera c) { return new CarreraDTO(c.getId(), c.getNombre()); }
    public static Carrera toEntity(CarreraDTO dto) {
        return Carrera.builder().id(dto.id()).nombre(dto.nombre()).build();
    }
}
