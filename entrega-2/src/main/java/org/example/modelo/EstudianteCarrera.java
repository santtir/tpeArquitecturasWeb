package org.example.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id","carrera_id","anioIngreso"})
)

public class EstudianteCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ToString.Include
    @Column(nullable = false)
    private Integer anioIngreso;

    private Boolean graduado = false;
    private Integer anioGraduacion; // nullable

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    @ToString.Exclude
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrera_id", nullable = false)
    @ToString.Exclude
    private Carrera carrera;

    @Builder
    public EstudianteCarrera(Integer anioIngreso, Boolean graduado, Integer anioGraduacion,
                       Estudiante estudiante, Carrera carrera) {
        this.anioIngreso = anioIngreso;
        this.graduado = graduado;
        this.anioGraduacion = anioGraduacion;
        this.estudiante = estudiante;
        this.carrera = carrera;
    }
}
