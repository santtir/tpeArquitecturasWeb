package org.example.entrega_3.mapper;

import org.example.entrega_3.dto.InscripcionDTO;
import org.example.entrega_3.model.Inscripcion;

public final class InscripcionMapper {
    private InscripcionMapper() {}
    public static InscripcionDTO toDTO(Inscripcion i) {
        return new InscripcionDTO(
                i.getId(), i.getEstudiante().getId(), i.getCarrera().getId(),
                i.getAnioEvento(), i.getAnioGraduacion(), i.isGraduado(), i.getAntiguedadEnAnios()
        );
    }
}
