package org.example.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<EstudianteCarrera> inscripciones = new HashSet<>();

    @Builder
    public Carrera(String nombre) { this.nombre = nombre; }
}
