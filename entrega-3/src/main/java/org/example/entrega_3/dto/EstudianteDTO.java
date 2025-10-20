package org.example.entrega_3.dto;

public record EstudianteDTO(
        Long id,
        String nombres,
        String apellido,
        Integer edad,
        String genero,
        String nroDocumento,
        String ciudadResidencia,
        String nroLibretaUniversitaria
) {}
