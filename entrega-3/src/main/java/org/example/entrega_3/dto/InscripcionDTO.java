package org.example.entrega_3.dto;

public record InscripcionDTO(
        Long id, Long estudianteId, Long carreraId,
        Integer anioEvento, Integer anioGraduacion, boolean graduado, Integer antiguedadEnAnios
) {}
