package org.example.entrega_3.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inscripcion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Estudiante estudiante;
    @ManyToOne(optional = false) private Carrera carrera;

    private LocalDate fechaInscripcion;
    private Integer antiguedadEnAnios;
    private boolean graduado;
    private Integer anioEvento;
    @Column
    private Integer anioGraduacion;
}
