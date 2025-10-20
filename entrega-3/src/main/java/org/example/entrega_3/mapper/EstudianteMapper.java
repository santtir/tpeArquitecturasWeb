package org.example.entrega_3.mapper;

import org.example.entrega_3.dto.EstudianteDTO;
import org.example.entrega_3.model.Estudiante;

public final class EstudianteMapper {
    private EstudianteMapper() {}
    public static EstudianteDTO toDTO(Estudiante e) {
        return new EstudianteDTO(
                e.getId(), e.getNombres(), e.getApellido(), e.getEdad(), e.getGenero(),
                e.getNroDocumento(), e.getCiudadResidencia(), e.getNroLibretaUniversitaria()
        );
    }
    public static Estudiante toEntity(EstudianteDTO dto) {
        return Estudiante.builder()
                .id(dto.id()).nombres(dto.nombres()).apellido(dto.apellido()).edad(dto.edad())
                .genero(dto.genero()).nroDocumento(dto.nroDocumento()).ciudadResidencia(dto.ciudadResidencia())
                .nroLibretaUniversitaria(dto.nroLibretaUniversitaria())
                .build();
    }
}
